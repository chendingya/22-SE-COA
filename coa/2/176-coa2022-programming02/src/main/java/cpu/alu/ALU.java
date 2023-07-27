package cpu.alu;

import util.DataType;

import java.util.Arrays;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {

    /**
     * 返回两个二进制整数的和
     * dest + src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType add(DataType src, DataType dest) {
        int[] srcInt = new int[32];
        int[] destInt = new int[32];
        int[] s = new int[32];
        String srcString = src.toString();
        String destString = dest.toString();
        for (int i = 0; i < 32; i++) {
            srcInt[i] = Integer.parseInt(String.valueOf(srcString.charAt(i)));
            destInt[i] = Integer.parseInt(String.valueOf(destString.charAt(i)));
        }
        int[] c = new int[32];
        for (int i = 0; i < 32; i++) {
            c[i] = 0;
        }
        for (int i = 31; i >= 0; i--) {
            if (i != 0) {
                s[i] = srcInt[i] ^ destInt[i] ^ c[i];
                c[i - 1] = (srcInt[i] & destInt[i]) | (srcInt[i] & c[i]) | (destInt[i] & c[i]);
            } else { //i == 0
                s[i] = srcInt[i] ^ destInt[i] ^ c[i];
            }
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            str.append(s[i]);
        }
        DataType ret = new DataType(str.toString());
        return ret;
    }

    /**
     * 返回两个二进制整数的差
     * dest - src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType sub(DataType src, DataType dest) {
        int[] srcInt = new int[32];
        int[] destInt = new int[32];
        int[] s = new int[32];
        String srcString = src.toString();
        String destString = dest.toString();
        for (int i = 0; i < 32; i++) {
            srcInt[i] = Integer.parseInt(String.valueOf(srcString.charAt(i)));
            destInt[i] = Integer.parseInt(String.valueOf(destString.charAt(i)));
        }

        for (int i = 0; i < 32; i++) {
            srcInt[i] = srcInt[i] ^ 1;
        }
        int jinwei = 1;
        for (int i = 31; i >= 0; i--) {
            if (jinwei + srcInt[i] == 2) {
                srcInt[i] = 0;
                jinwei = 1;
            } else {
                srcInt[i] += jinwei;
                jinwei = 0;
            }
        }
        int[] c = new int[32];
        for (int i = 0; i < 32; i++) {
            c[i] = 0;
        }
        for (int i = 31; i >= 0; i--) {
            if (i != 0) {
                s[i] = srcInt[i] ^ destInt[i] ^ c[i];
                c[i - 1] = (srcInt[i] & destInt[i]) | (srcInt[i] & c[i]) | (destInt[i] & c[i]);
            } else { //i == 0
                s[i] = srcInt[i] ^ destInt[i] ^ c[i];
            }
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            str.append(s[i]);
        }
        DataType ret = new DataType(str.toString());
        return ret;
    }

}
