package memory.cache.cacheReplacementStrategy;

import memory.cache.Cache;

/**
 * TODO 最近不经常使用算法
 */
public class LFUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache cache = Cache.getCache();
        cache.addVisited(rowNO);
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        int minVisited = 0;
        int hanghao = start;
        Cache cache = Cache.getCache();
        //先找有没有空位
        for (int i = start; i < end; i++) {
            if (!cache.getValid(i)) {
                return i;
            }
        }
        //没有空位再替换
        minVisited = cache.getVisited(start);
        hanghao = start;
        for (int i = start; i < end; i++) {
            int visitedtimei = cache.getVisited(i);
            if (visitedtimei < minVisited) {
                hanghao = i;
                minVisited = visitedtimei;
            }
        }
        return hanghao;
    }

}
