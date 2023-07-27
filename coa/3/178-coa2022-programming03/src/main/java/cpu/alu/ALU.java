package cpu.alu;


import util.DataType;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {

    /**
     * 返回两个二进制整数的乘积(结果低位截取后32位)
     * dest * src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType mul(DataType src, DataType dest) {
        String mul1 = src.toString();
        String mul2 = dest.toString();
        //计算控制数组
        int[] ac = new int[32];
        ac[31] = -Integer.parseInt(String.valueOf(mul2.charAt(31)));

        for (int i = 31; i > 0; i--) {
            ac[i - 1] = (Integer.parseInt(String.valueOf(mul2.charAt(i))) - Integer.parseInt(String.valueOf(mul2.charAt(i - 1))));
        }
        char[] ans = new char[64];
        for (int i = 0; i < 32; i++) {
            ans[i] = '0';
        }

        for (int i = 31; i >= 0; i--) {
            if (ac[i] == 1) {
                StringBuilder tmp = new StringBuilder(32);
                for (int j = 0; j < 32; j++) {
                    tmp.append(ans[j]);
                }
                String num = add(new DataType(String.valueOf(tmp)), src).toString();
                for (int j = 0; j < 32; j++) {
                    ans[j] = num.charAt(j);
                }

            }
            if (ac[i] == -1) {
                StringBuilder tmp = new StringBuilder(32);
                for (int j = 0; j < 32; j++) {
                    tmp.append(ans[j]);
                }
                String num = sub(src, new DataType(String.valueOf(tmp))).toString();
                for (int j = 0; j < 32; j++) {
                    ans[j] = num.charAt(j);
                }

            }
            // 右移
            for (int j = 31 + 31 - i; j >= 0; j--) {
                ans[j + 1] = ans[j];
            }
            ans[0] = ans[1];
        }
        StringBuffer ret = new StringBuffer(32);
        for (int i = 32; i < 64; i++) {
            ret.append(ans[i]);
        }

        return new DataType(String.valueOf(ret));
    }
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
