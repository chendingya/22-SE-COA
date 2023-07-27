package cpu.alu;

import util.DataType;
import util.Transformer;

import java.io.DataInput;
import java.util.Objects;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {

    DataType remainderReg;

    /**
     * 返回两个二进制整数的除法结果
     * dest ÷ src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType div(DataType src, DataType dest) {
        StringBuilder shang = new StringBuilder(dest.toString());
        StringBuilder chushu = new StringBuilder(src.toString());
        StringBuilder yushu = null;
        DataType rem;
        DataType ret = null;
        if (Objects.equals(src.toString(), "00000000000000000000000000000000")) {
            throw new ArithmeticException();
        } else {
            //符号拓展32位
            if (shang.charAt(0) == '0') {
                yushu = new StringBuilder(new String("00000000000000000000000000000000"));
            } else if (shang.charAt(0) == '1') {
                yushu = new StringBuilder(new String("11111111111111111111111111111111"));
            }

            //除数与被除数不同号，则将余数取为相反数
            /**
             * flag
             * 除数与被除数不同号 flag == true
             */
            boolean flag = false;
            if (shang.charAt(0) != chushu.charAt(0)) {
                flag = true;
            }
            /**
             * weifu
             * 被除数为负 weifu == true
             */
            boolean weifu = false;
            if (shang.charAt(0) == '1') {
                weifu = true;
            }

            //开始计算
            for (int k = 0; k < 32; k++) {
                //左移余数和商
                for (int i = 0; i < 31; i++) {
                    yushu.setCharAt(i, yushu.charAt(i + 1));
                }
                yushu.setCharAt(31, shang.charAt(0));
                for (int i = 0; i < 31; i++) {
                    shang.setCharAt(i, shang.charAt(i + 1));
                }

                //判断是否够
                //判断余数、除数是否同号
                if (yushu.charAt(0) == chushu.charAt(0)) {
                    //同号 : -
                    StringBuilder tmp = new StringBuilder(sub(new DataType(chushu.toString()) ,new DataType(yushu.toString())).toString());
                    if (tmp.charAt(0) != yushu.charAt(0)) {
                        //不够
                        shang.setCharAt(31, '0');
                    } else {
                        //够
                        shang.setCharAt(31, '1');
                        for (int i = 0; i < 32; i++) {
                            yushu.setCharAt(i, tmp.charAt(i));
                        }
                    }
                } else {
                    //不同号 : +
                    StringBuilder tmp = new StringBuilder(add(new DataType(yushu.toString()), new DataType(chushu.toString())).toString());
                    if (tmp.charAt(0) != yushu.charAt(0)) {
                        //不够
                        shang.setCharAt(31, '0');
                    } else {
                        //够
                        shang.setCharAt(31, '1');
                        for (int i = 0; i < 32; i++) {
                            yushu.setCharAt(i, tmp.charAt(i));
                        }
                    }
                }

            }


            boolean xiangdeng = true;
            boolean xiangfan = true;
            boolean zhengchu = false;
            if (weifu) {
                /**
                 * 如果被除数为负，本质上算出来的是商的绝对值
                 * 所以 shang 取反加一
                 * 被除数为负 算出来的yushu 为负
                 * 如果 yushu 和 chushu 相等 则xiangdeng == true
                 * 如果 yushu 和 chushu 相反 则xiangfan == true
                 * a 是 yushu 取反加一 是相反数
                 */
                StringBuilder a = new StringBuilder("");
                for (int i = 0; i < 32; i++) {
                    if (yushu.charAt(i) == '0') {
                        a.append('1');
                    } else {
                        a.append('0');
                    }
                }
                a = new StringBuilder(add(new DataType(a.toString()), new DataType(Transformer.intToBinary("1"))).toString());
                for (int i = 0; i < 32; i++) {
                    if (yushu.charAt(i) != chushu.charAt(i)) {
                        xiangdeng = false;
                        break;
                    }
                }
                for (int i = 0; i < 32; i++) {
                    if (a.charAt(i) != chushu.charAt(i)) {
                        xiangfan = false;
                        break;
                    }
                }
                // 整除即是相等或者相反
                zhengchu = xiangdeng || xiangfan;
            }
            /**
             * rem 是返回的余数
             */
            rem = new DataType(yushu.toString());
            if (!flag) {
                //除数 和 被除数 不同号
                if (weifu && zhengchu) {
                    /**
                     * 被除数为负 并且整除
                     * 余数直接返回 0
                     * 商应该返回 原商加一
                     */
                    yushu = new StringBuilder("00000000000000000000000000000000");
                    rem = new DataType(yushu.toString());
                    shang = new StringBuilder(add(new DataType(shang.toString()), new DataType(Transformer.intToBinary("1"))).toString());
                    ret = new DataType(shang.toString());
                } else {
                    ret = new DataType(shang.toString());
                }
            } else { // flag
                if (!weifu) { // ~
                    for (int i = 0; i < 32; i++) {
                        if (shang.charAt(i) == '0') {
                            shang.setCharAt(i, '1');
                        } else {
                            shang.setCharAt(i, '0');
                        }
                    }
                    ret = add(new DataType(shang.toString()), new DataType(Transformer.intToBinary("1")));
                } else {
                    if (zhengchu) {
                        yushu = new StringBuilder("00000000000000000000000000000000");
                        rem = new DataType(yushu.toString());
                        shang = new StringBuilder(add(new DataType(shang.toString()), new DataType(Transformer.intToBinary("1"))).toString());
                        for (int i = 0; i < 32; i++) {
                            if (shang.charAt(i) == '0') {
                                shang.setCharAt(i, '1');
                            } else {
                                shang.setCharAt(i, '0');
                            }
                        }
                        ret = add(new DataType(shang.toString()), new DataType(Transformer.intToBinary("1")));
                    } else {
                        for (int i = 0; i < 32; i++) {
                            if (shang.charAt(i) == '0') {
                                shang.setCharAt(i, '1');
                            } else {
                                shang.setCharAt(i, '0');
                            }
                        }
                        ret = add(new DataType(shang.toString()), new DataType(Transformer.intToBinary("1")));
                    }
                }
            }
        }
        this.remainderReg = rem;
        return ret;
    }
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
