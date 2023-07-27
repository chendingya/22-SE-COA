package memory.cache;

import memory.Memory;
import memory.cache.cacheReplacementStrategy.ReplacementStrategy;
import util.Transformer;

import java.util.Arrays;

/**
 * 高速缓存抽象类
 */
public class Cache {

    public static final boolean isAvailable = true; // 默认启用Cache

    public static final int CACHE_SIZE_B = 32 * 1024; // 32 KB 总大小

    public static final int LINE_SIZE_B = 64; // 64 B 行大小

    private final CacheLine[] cache = new CacheLine[CACHE_SIZE_B / LINE_SIZE_B];

    private int SETS;   // 组数

    public int getSETS() {
        return SETS;
    }

    private int setSize;    // 每组行数

    // 单例模式
    private static final Cache cacheInstance = new Cache();

    private Cache() {
        for (int i = 0; i < cache.length; i++) {
            cache[i] = new CacheLine();
        }
    }

    public static Cache getCache() {
        return cacheInstance;
    }

    private ReplacementStrategy replacementStrategy;    // 替换策略

    public static boolean isWriteBack;   // 写策略

    /**
     * 读取[pAddr, pAddr + len)范围内的连续数据，可能包含多个数据块的内容
     *
     * @param pAddr 数据起始点(32位物理地址 = 26位块号 + 6位块内地址)
     * @param len   待读数据的字节数
     * @return 读取出的数据，以char数组的形式返回
     */
    public byte[] read(String pAddr, int len) {
        //data用于存储cache中读回的数据
        byte[] data = new byte[len];
        int addr = Integer.parseInt(Transformer.binaryToInt("0" + pAddr));
        int upperBound = addr + len;
        int index = 0;
        while (addr < upperBound) {
            //这一次应该读本行中还剩下的nextSegLen长的数据
            int nextSegLen = LINE_SIZE_B - (addr % LINE_SIZE_B);

            //最后一行不读完
            if (addr + nextSegLen >= upperBound) {
                nextSegLen = upperBound - addr;
            }
            //rowNO 是cache中的物理实现的第几行 addr是主存中块号+块内地址
            int rowNO = fetch(Transformer.intToBinary(String.valueOf(addr)));

            //cache_data 从cache中一次性读回64B
            byte[] cache_data = cache[rowNO].getData();
            //将cache_data中的数据 挨个装入char[] 的data的正确位置index
            int i = 0;
            while (i < nextSegLen) {
                data[index] = cache_data[addr % LINE_SIZE_B + i];
                index++;
                i++;
            }
            addr += nextSegLen;
        }
        return data;
    }

    /**
     * 向cache中写入[pAddr, pAddr + len)范围内的连续数据，可能包含多个数据块的内容
     *
     * @param pAddr 数据起始点(32位物理地址 = 26位块号 + 6位块内地址)
     * @param len   待写数据的字节数
     * @param data  待写数据
     */
    public void write(String pAddr, int len, byte[] data) {
        int addr = Integer.parseInt(Transformer.binaryToInt("0" + pAddr));
        int upperBound = addr + len;
        int index = 0;
        while (addr < upperBound) {
            int nextSegLen = LINE_SIZE_B - (addr % LINE_SIZE_B);
            if (addr + nextSegLen >= upperBound) {
                nextSegLen = upperBound - addr;
            }
            //如果cache中有，就写cache
            //如果cache中没有，fetch会为其分配cache一行：rowNO 将更新的主存块装入cache行中
            int rowNO = fetch(Transformer.intToBinary(String.valueOf(addr)));
            //如果是写回，就需要将rowNO的dirty设为true
            if (isWriteBack) {
                cache[rowNO].dirty = true;
            }
            //cache_data是cache的一个引用
            byte[] cache_data = cache[rowNO].getData();
            int i = 0;
            //将写的内容装入cache
            while (i < nextSegLen) {
                cache_data[addr % LINE_SIZE_B + i] = data[index];
                index++;
                i++;
            }

            // TODO
            if (!isWriteBack) {
                //write through 写直达
                //将主存修改
                Memory memory = Memory.getMemory();
                String add = Transformer.intToBinary(String.valueOf(addr / 64 * 64));
                if (add.length() < 32) {
                    add = '0' + add;
                }
                memory.write(add, 64, cache[rowNO].getData());
            }


            addr += nextSegLen;
        }
    }


    /**
     * 查询{@link Cache#cache}表以确认包含pAddr的数据块是否在cache内
     * 如果目标数据块不在Cache内，则将其从内存加载到Cache
     *
     * @param pAddr 数据起始点(32位物理地址 = 26位块号 + 6位块内地址)
     * @return 数据块在Cache中的对应行号
     */
    private int fetch(String pAddr) {
        // TODO
        int kuaihao = getBlockNO(pAddr);

        int hanghao = map(kuaihao);

        if (hanghao == -1) {
            //加载
            if (SETS == 512) {
                //直接映射
                Memory memory = Memory.getMemory();
                int qishi = kuaihao * 64;
                hanghao = kuaihao % 64;

                byte[] data = new byte[64];
                data = memory.read(Transformer.intToBinary(String.valueOf(qishi)), 64);

                char[] addrTag = new char[17];
                for (int i = 0; i < 17; i++) {
                    addrTag[i] = pAddr.charAt(i);
                }
                // 调用replace
                char[] tager = new char[26];
                for (int i = 0; i < 9; i++) {
                    tager[i] = '0';
                }
                for (int i = 0; i < 17; i++) {
                    tager[i + 9] = pAddr.charAt(i);
                }
                update(hanghao, tager, data);

            } else if (SETS == 1 && setSize == 512) {
                // 全关联映射
                Memory memory = Memory.getMemory();
                int qishi = kuaihao * 64;
                hanghao = 0;
                byte[] data = new byte[64];
                data = memory.read(Transformer.intToBinary(String.valueOf(qishi)), 64);

                char[] addrTag = new char[26];
                for (int i = 0; i < 26; i++) {
                    addrTag[i] = pAddr.charAt(i);
                }
                hanghao = replacementStrategy.replace(0, 512, addrTag, data);

                update(hanghao, addrTag, data);

            } else {
                // 2^n路组关联映射

                Memory memory = Memory.getMemory();
                int qishi = kuaihao * 64;
                hanghao = 0;
                byte[] data = new byte[64];
                data = memory.read(Transformer.intToBinary(String.valueOf(qishi)), 64);
                //存入cache中 (kuaihao % SETS) 组 存入行数根据替换策略自定
                //cache中对应(kuaihao % SETS) * setSize ~ (kuaihao % SETS) * setSize + setSize - 1 行
                char[] addrTag = new char[26];
                int tag = kuaihao / SETS; //前26 - 9 - n位
                String tagString = (Transformer.intToBinary(String.valueOf(tag)));

                for (int i = 0; i < 26; i++) {
                    addrTag[i] = tagString.charAt(i + 6);
                }

                hanghao = replacementStrategy.replace((kuaihao % SETS) * setSize, (kuaihao % SETS) * setSize + setSize, addrTag, data);

                update(hanghao, addrTag, data);

                return hanghao;
            }
            return hanghao;
        } else {
            replacementStrategy.hit(hanghao);
            return hanghao;
        }
    }


    /**
     * 根据目标数据内存地址前26位的int表示，进行映射
     *
     * @param blockNO 数据在内存中的块号
     * @return 返回cache中所对应的行，-1表示未命中
     */
    private int map(int blockNO) {
        // TODO
        // blockNO 块号
        int hanghao = 0;
        // 直接映射
        if (SETS == 512) {
            hanghao = blockNO % 64;
            if (!cache[hanghao].validBit) {
                return -1;
            } else {
                return hanghao;
            }
        } else if (SETS == 1 && setSize == 512) {
            // 全关联映射
            for (int i = 0; i < cache.length; i++) {
                if (cache[i].validBit) {
                    StringBuilder tmp = new StringBuilder();
                    for (int j = 0; j < 26; j++) {
                        tmp.append(cache[i].tag[j]);
                    }
                    int kuaihao = Integer.parseInt(Transformer.binaryToInt("0" + tmp));
                    if (kuaihao == blockNO) {
                        return i;
                    }
                }
            }
            return -1;
        } else {
            //set
            int tag = blockNO / SETS;  // blockNO % SETS cache 组号  blockNO / SETS tag cache块号的高26-9+n位
            for (int i = 0; i < setSize; i++) {
                if (cache[blockNO % SETS * setSize + i].validBit) {
                    StringBuilder strtmp = new StringBuilder();
                    for (int j = 0; j < cache[blockNO % SETS * setSize + i].tag.length; j++) {
                        strtmp.append(cache[blockNO % SETS * setSize + i].tag[j]);
                    }
                    int tmpint = Integer.parseInt(Transformer.binaryToInt("0" + strtmp));
                    if (tag == tmpint) {
                        return (blockNO % SETS * setSize + i);
                    }
                }
            }
            return -1;
        }
    }

    /**
     * 用于返回address
     *
     * @param rowNO 行号
     * @return string类型地址
     */
    private String Getaddr(int rowNO) {

        StringBuilder oldtag = new StringBuilder();
        for (int i = 0; i < cache[rowNO].tag.length; i++) {
            oldtag.append(cache[rowNO].tag[i]);
        }

        //直接映射为0， 全映射为 9，组关联为1-8,
        //9 - n 表示缺失的位数
        int n = (int) (Math.log(setSize) / Math.log(2));
        //取9 - n位以后 26-9+n
        String newtag = oldtag.substring(9 - n);


        if (SETS > 1) {
            //group 对应512 / 2^n 9-n
            int group = rowNO / setSize;
            String houmian = Transformer.intToBinary(String.valueOf(group));
            // 26 - 9 + n  + (9 - n)
            newtag = newtag + houmian.substring(32 - 9 + n);
        }

        //补六位结尾的块内地址
        return newtag + "000000";
    }

    /**
     * 更新cache
     *
     * @param rowNO 需要更新的cache行号
     * @param tag   待更新数据的Tag
     * @param input 待更新的数据
     */
    public void update(int rowNO, char[] tag, byte[] input) {
        // TODO

        //isWriteBack 写回并且主存块已经被修改，正在被替换
        if (isWriteBack && cache[rowNO].dirty) {
            cache[rowNO].dirty = false;
            Memory m = Memory.getMemory();
            String addr = Getaddr(rowNO);


            m.write(addr, 64, cache[rowNO].getData());
            //注意write会将ValidBit置false
            cache[rowNO].validBit = true;

        }
        int dataLength = input.length;
        int tagLength = tag.length;
        cache[rowNO].data = Arrays.copyOf(input, dataLength);
        cache[rowNO].tag = Arrays.copyOf(tag, tagLength);
        cache[rowNO].validBit = true;
        cache[rowNO].timeStamp = System.currentTimeMillis();
        cache[rowNO].visited = 1;
        cache[rowNO].dirty = false;
        /**
         * dirty  未修改
         */

    }


    /**
     * 从32位物理地址(26位块号 + 6位块内地址)获取目标数据在内存中对应的块号
     *
     * @param pAddr 32位物理地址
     * @return 数据在内存中的块号
     */
    private int getBlockNO(String pAddr) {
        return Integer.parseInt(Transformer.binaryToInt("0" + pAddr.substring(0, 26)));
    }


    /**
     * 该方法会被用于测试，请勿修改
     * 使用策略模式，设置cache的替换策略
     *
     * @param replacementStrategy 替换策略
     */
    public void setReplacementStrategy(ReplacementStrategy replacementStrategy) {
        this.replacementStrategy = replacementStrategy;
    }

    /**
     * 该方法会被用于测试，请勿修改
     *
     * @param SETS 组数
     */
    public void setSETS(int SETS) {
        this.SETS = SETS;
    }

    /**
     * 该方法会被用于测试，请勿修改
     *
     * @param setSize 每组行数
     */
    public void setSetSize(int setSize) {
        this.setSize = setSize;
    }

    /**
     * 告知Cache某个连续地址范围内的数据发生了修改，缓存失效
     * 该方法仅在memory类中使用，请勿修改
     *
     * @param pAddr 发生变化的数据段的起始地址
     * @param len   数据段长度
     */
    public void invalid(String pAddr, int len) {
        int from = getBlockNO(pAddr);
        int to = getBlockNO(Transformer.intToBinary(String.valueOf(Integer.parseInt(Transformer.binaryToInt("0" + pAddr)) + len - 1)));

        for (int blockNO = from; blockNO <= to; blockNO++) {
            int rowNO = map(blockNO);
            if (rowNO != -1) {
                cache[rowNO].validBit = false;
            }
        }
    }

    /**
     * 清除Cache全部缓存
     * 该方法会被用于测试，请勿修改
     */
    public void clear() {
        for (CacheLine line : cache) {
            if (line != null) {
                line.validBit = false;
                line.dirty = false;
            }
        }
    }

    /**
     * 输入行号和对应的预期值，判断Cache当前状态是否符合预期
     * 这个方法仅用于测试，请勿修改
     *
     * @param lineNOs     行号
     * @param validations 有效值
     * @param tags        tag
     * @return 判断结果
     */
    public boolean checkStatus(int[] lineNOs, boolean[] validations, char[][] tags) {
        if (lineNOs.length != validations.length || validations.length != tags.length) {
            return false;
        }
        for (int i = 0; i < lineNOs.length; i++) {
            CacheLine line = cache[lineNOs[i]];
            if (line.validBit != validations[i]) {
                return false;
            }
            if (!Arrays.equals(line.getTag(), tags[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cache行，每行长度为(1+22+{@link Cache#LINE_SIZE_B})
     */
    private static class CacheLine {

        // 有效位，标记该条数据是否有效
        boolean validBit = false;

        // 脏位，标记该条数据是否被修改
        boolean dirty = false;

        // 用于LFU算法，记录该条cache使用次数
        int visited = 0;

        // 用于LRU和FIFO算法，记录该条数据时间戳
        Long timeStamp = 0L;

        // 标记，占位长度为26位，有效长度取决于映射策略：
        // 直接映射: 17 位
        // 全关联映射: 26 位
        // (2^n)-路组关联映射: 26-(9-n) 位
        // 注意，tag在物理地址中用高位表示，如：直接映射(32位)=tag(17位)+行号(9位)+块内地址(6位)，
        // 那么对于值为0b1111的tag应该表示为00000000000000000000001111，其中低12位为有效长度
        char[] tag = new char[26];

        // 数据
        byte[] data = new byte[LINE_SIZE_B];

        byte[] getData() {
            return this.data;
        }

        char[] getTag() {
            return this.tag;
        }

    }

    // 获取有效位
    public boolean getValid(int rowNO) {
        return cache[rowNO].validBit;
    }

    // 获取脏位
    public boolean isDirty(int rowNO) {
        return cache[rowNO].dirty;
    }

    // LFU算法增加访问次数
    public void addVisited(int rowNO) {
        cache[rowNO].visited++;
    }

    // 获取访问次数
    public int getVisited(int rowNO) {
        return cache[rowNO].visited;
    }

    // 用于LRU算法，重置时间戳
    public void setTimeStamp(int rowNO, long num) {
        cache[rowNO].timeStamp = num;
    }

    // 获取时间戳
    public long getTimeStamp(int rowNO) {
        return cache[rowNO].timeStamp;
    }

    // 获取该行数据
    public char[] getData(int rowNO) {
        return new char[1];
    }
    // 获取有效位

}
