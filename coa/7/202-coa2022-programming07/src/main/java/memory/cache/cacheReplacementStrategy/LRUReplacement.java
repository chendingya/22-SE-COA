package memory.cache.cacheReplacementStrategy;

import memory.cache.Cache;

/**
 * TODO 最近最少用算法
 */
public class LRUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache cache = Cache.getCache();
        cache.setTimeStamp(rowNO, System.currentTimeMillis());
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        long minStamp = 0l;
        int hanghao = start;
        Cache cache = Cache.getCache();
        //先找有没有空位
        for (int i = start; i < end; i++) {
            if (!cache.getValid(i)) {
                return i;
            }
        }
        //没有空位再替换
        minStamp = cache.getTimeStamp(start);
        hanghao = start;
        for (int i = start; i < end; i++) {
            if (cache.getTimeStamp(i) < minStamp) {
                hanghao = i;
                minStamp = cache.getTimeStamp(i);
            }
        }
        return hanghao;
    }

}





























