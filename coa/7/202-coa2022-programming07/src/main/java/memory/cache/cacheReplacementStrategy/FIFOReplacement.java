package memory.cache.cacheReplacementStrategy;

import memory.Memory;
import memory.cache.Cache;

/**
 * TODO 先进先出算法
 */
public class FIFOReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {

    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        long minStamp;
        int hanghao = start;
        Cache cache = Cache.getCache();

        //先找有没有空位
        for (int i = start; i < end; i++) {
            if (!cache.getValid(i)) {
                hanghao = i;
                return i;
            }
        }


        //没有空位再替换
        minStamp = cache.getTimeStamp(start);
        for (int i = start; i < end; i++) {
            if (cache.getTimeStamp(i) < minStamp) {
                hanghao = i;
                minStamp = cache.getTimeStamp(i);
            }
        }

        return hanghao;
    }

}
