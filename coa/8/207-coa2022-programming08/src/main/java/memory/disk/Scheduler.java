package memory.disk;

import javax.sound.midi.Track;
import java.util.Arrays;

import static memory.disk.Disk.TRACK_NUM;

public class Scheduler {

    /**
     * 先来先服务算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double FCFS(int start, int[] request) {
        // TODO
        int lens = request.length;
        double ret = 0;
        int last = start;
        for (int i = 0; i < lens; i++) {
            ret += (Math.abs(request[i] - last));
            last = request[i];
        }
        return ret / lens;
    }

    /**
     * 最短寻道时间优先算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double SSTF(int start, int[] request) {
        int lens = request.length;
        int[] beifen = new int[lens];
        for (int i = 0; i < lens; i++) {
            beifen[i] = request[i];
        }
        int last = start;
        int minFlag = 0;
        int min;
        double ret = 0;

        for (int i = 0 ;i < lens; i++) {
            min = Math.abs(beifen[0] - last);
            minFlag = 0;
            for (int j = 0; j < lens; j++) {
                if (Math.abs(beifen[j] - last) < min) {

                    min = Math.abs(beifen[j] - last);
                    minFlag = j;

                }
            }
            ret += min;
            last = beifen[minFlag];
            beifen[minFlag] = 2147483647;

        }

        return ret / lens;
    }

    /**
     * 扫描算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double SCAN(int start, int[] request, boolean direction) {
        // TODO
        int last = start;
        double ret = 0;
        int lens = request.length;

        int[] beifen = new int[lens];
        for (int i = 0; i < lens; i++) {
            beifen[i] = request[i];
        }

        int min, minflag;
        for (int i = 0; i < lens; i++) {
             min = beifen[i];
             minflag = i;
            for (int j = i; j < lens; j++) {
                if (beifen[j] < min) {
                    min = beifen[j];
                    minflag = j;
                }
            }
            int tmp = beifen[i];
            beifen[i] = beifen[minflag];
            beifen[minflag] = tmp;
        }



        if (direction) {
            if (beifen[0] < start) {
                ret = ((double) (TRACK_NUM  - 1 - start) + (TRACK_NUM - 1 - beifen[0])) / lens;
            } else {
                ret = ((double) (beifen[lens - 1] - start)) / lens;
            }
        } else {
            if (beifen[lens - 1] > start) {
                ret = ((double) start + request[lens - 1]) / lens;
            } else {
                ret = ((double) start - request[0]) / lens;
            }
        }
        return ret;
    }

}
