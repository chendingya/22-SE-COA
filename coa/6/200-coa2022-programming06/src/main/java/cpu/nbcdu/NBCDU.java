package cpu.nbcdu;

import cpu.alu.ALU;
import util.DataType;

import java.util.Objects;

public class NBCDU {

    /**
     * @param src  A 32-bits NBCD String
     * @param dest A 32-bits NBCD String
     * @return dest + src
     */
    DataType add(DataType src, DataType dest) {
        /**
         * add1存放src
         * add2存放dest
         * ret存放计算后返回字符串
         * retu存放最终函数返回字符串
         * 进位记录每一次按位加法的进位，i对应 i- 1 位进上来的 1
         */
        StringBuilder[] add1 = new StringBuilder[8];
        StringBuilder[] add2 = new StringBuilder[8];
        String[] ret = new String[8];
        StringBuilder retu = new StringBuilder();
        int[] jinwei = new int[8];

        //分别存入数组 从高到低 0为高位
        for (int i = 0; i < 8; i++) {
            add1[i] = new StringBuilder(src.toString().substring(4 * i, 4 * i + 4));
            add2[i] = new StringBuilder(dest.toString().substring(4 * i, 4 * i + 4));
            jinwei[i] = 0;
        }
        int fuhaoA = 0;
        int fuhaoB = 0;

        //判断符号位
        if ("1101".equals(add1[0].toString())) {
            fuhaoA = 1;
        }
        if ("1101".equals(add2[0].toString())) {
            fuhaoB = 1;
        }
        /**
         * 如果src/dest == 0 可以直接返回另一个
         * 0 + (-0) 调用sub 0 - 0 直接返回 + 0
         */
        if ("0000000000000000000000000000".equals(src.toString().substring(4))) {
            return dest;
        }
        if ("0000000000000000000000000000".equals(dest.toString().substring(4))) {
            return src;
        }

        /**
         * 分情况：
         * 两者符号相同， 则可以直接进行计算
         * 两者符号不同， 则调用sub 使其符号相同
         */

        if ((fuhaoA == 0 && fuhaoB == 0) || (fuhaoA == 1 && fuhaoB == 1)) {
            //同号 ：从低位数组开始add
            if (fuhaoA == 1) {
                ret[0] = "1101";
            } else {
                ret[0] = "1100";
            }
            for (int i = 7; i > 0; i--) {
                StringBuilder tmp1 = new StringBuilder("0000000000000000000000000000");
                tmp1.append(add1[i]);
                StringBuilder tmp2 = new StringBuilder("0000000000000000000000000000");
                tmp2.append(add2[i]);

                ALU alu = new ALU();
                //tmp1 + tmp2
                String tmp3 = String.valueOf(alu.add(new DataType(tmp1.toString()), new DataType(tmp2.toString())));

                //有来自上一位的进位
                if (jinwei[i] == 1) {
                    tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000001")));
                }

                /**
                 *
                 * 该位如果有5位表示，则有一种情况需要修正：
                 * 本位 > 10             +0110
                 *
                 * 该位如果有4位表示，则两种情况需要修正：
                 * 本位 > 10 || alu返回的32位字符串中第27位为1   +0110
                 *
                 * 计算这一位是否需要进位 ：
                 * alu返回的32位字符串中第27位为1
                 */
                int sum = 0;
                for (int j = 0; j < 4; j++) {
                    if (tmp3.charAt(28 + j) == '1') {
                        sum += Math.pow(2, 3 - j);
                    }
                }

                //有进位
                if (tmp3.charAt(27) == '1' || sum >= 10) {
                    tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000110")));
                }
                if (tmp3.charAt(27) == '1') {
                    jinwei[i - 1] = 1;
                }

                ret[i] = (tmp3.substring(28));
            }

            //将ret填入retu
            for (int i= 0; i < 8; i++) {
                retu.append(ret[i]);
            }
            return new DataType(retu.toString());
        } else {
            //不同号：
            if (fuhaoA == 1 && (fuhaoB == 0)) {
                //B - (-A)
                NBCDU nbcdu = new NBCDU();
                StringBuilder srctmp = new StringBuilder("1100");
                StringBuilder desttmp = new StringBuilder("1100");
                for (int i = 1; i < 8; i++) {
                    desttmp.append(add2[i]); // b = dest
                    srctmp.append(add1[i]); //a = src
                }
                return nbcdu.sub(new DataType(srctmp.toString()), new DataType(desttmp.toString()));
            }
            if ((fuhaoB == 1) && (fuhaoA == 0)) {
                // A -( -B)
                NBCDU nbcdu = new NBCDU();
                StringBuilder srctmp = new StringBuilder("1100");
                StringBuilder desttmp = new StringBuilder("1100");
                for (int i = 1; i < 8; i++) {
                    desttmp.append(add1[i]);
                    srctmp.append(add2[i]);
                }
                return nbcdu.sub(new DataType(srctmp.toString()), new DataType(desttmp.toString()));
            }
        }
        return null;
    }

    /***
     *
     * @param src A 32-bits NBCD String
     * @param dest A 32-bits NBCD String
     * @return dest - src
     */
    DataType sub(DataType src, DataType dest) {
        StringBuilder[] add1 = new StringBuilder[8];
        StringBuilder[] add2 = new StringBuilder[8];
        String[] ret = new String[8];
        StringBuilder retu = new StringBuilder();
        int[] jinwei = new int[8];

        //分别存入数组 从高到低 0为高位
        for (int i = 0; i < 8; i++) {
            add1[i] = new StringBuilder(src.toString().substring(4 * i, 4 * i + 4));
            add2[i] = new StringBuilder(dest.toString().substring(4 * i, 4 * i + 4));
            jinwei[i] = 0;
        }
        int fuhaoA = 0;
        int fuhaoB = 0;

        //判断符号位
        if ("1101".equals(add1[0].toString())) {
            fuhaoA = 1;
        }
        if ("1101".equals(add2[0].toString())) {
            fuhaoB = 1;
        }

        /**
         * 特判 被减数 或者 减数 == 0 的情况
         * 如果减数 == 0 则 应当返回 被减数
         * 如果被减数 == 0 则应当返回 减数的相反数
         */
        if ("0000000000000000000000000000".equals(src.toString().substring(4))) {
            return dest;
        }
        if ("0000000000000000000000000000".equals(dest.toString().substring(4))) {
            StringBuilder rett;
            if (fuhaoA == 0) {
                rett = new StringBuilder("1101");
            } else {
                rett = new StringBuilder("1100");
            }
            rett.append(src.toString().substring(4));
            return new DataType(rett.toString());
        }

        // b - a
        /**
         * 首先应当判断两个数的原型的最高位， 以在最后的判断”是否存在进位，需要反转并加一“的过程中作为最高位使用
         */
        if (fuhaoA == fuhaoB) {
            int maxlena = 0;
            int maxlenb = 0;
            int maxlen = 0;

            for (int i = 1; i < 8; i++) {
                if (!add1[i].toString().equals("0000")) {
                    maxlena = i;
                    break;
                }
            }
            for (int i = 1; i < 8; i++) {
                if (!add2[i].toString().equals("0000")) {
                    maxlenb = i;
                    break;
                }
            }
            if (maxlena <= maxlenb) {
                maxlen = maxlena - 1;
            } else {
                maxlen = maxlenb - 1;
            }

            // a == b == +
            if (fuhaoA == 0) {
                /**
                 * 首先将add1 即被减数反转 + 1
                 */
                for (int i = 1; i < 8; i++) {
                    add1[i] = new StringBuilder(Reverse(add1[i]));
                }
                for (int i = 7; i > 0; i--) {
                    StringBuilder tmp1;
                    if (i == 7) {
                        tmp1 = new StringBuilder("00000000000000000000000000000001");
                    } else {
                        tmp1 = new StringBuilder("00000000000000000000000000000000");
                    }
                    StringBuilder tmp2 = new StringBuilder("0000000000000000000000000000");
                    tmp2.append(add1[i]);
                    ALU alu = new ALU();
                    //tmp1 + tmp2
                    String tmp3 = String.valueOf(alu.add(new DataType(tmp1.toString()), new DataType(tmp2.toString())));

                    if (jinwei[i] == 1) {
                        tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000001")));
                    }
                    int sum = 0;
                    for (int j = 0; j < 4; j++) {
                        if (tmp3.charAt(28 + j) == '1') {
                            sum += Math.pow(2, 3 - j);
                        }
                    }
                    //有进位
                    if (tmp3.charAt(27) == '1' || sum >= 10) {
                        tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000110")));
                    }
                    if (tmp3.charAt(27) == '1') {
                        jinwei[i - 1] = 1;
                    }
                    add1[i] = new StringBuilder((tmp3.substring(28)));
                }

                //清空进位
                for (int i = 0; i < 8; i++) {
                    jinwei[i] = 0;
                }

                /**
                 * 开始计算 反转后的减数与被减数的和
                 */
                for (int i = 7; i > 0; i--) {
                    StringBuilder tmp1 = new StringBuilder("0000000000000000000000000000");
                    tmp1.append(add1[i]);
                    StringBuilder tmp2 = new StringBuilder("0000000000000000000000000000");
                    tmp2.append(add2[i]);

                    ALU alu = new ALU();
                    //tmp1 + tmp2
                    String tmp3 = String.valueOf(alu.add(new DataType(tmp1.toString()), new DataType(tmp2.toString())));

                    if (jinwei[i] == 1) {
                        tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000001")));
                    }
                    int sum = 0;
                    for (int j = 0; j < 4; j++) {
                        if (tmp3.charAt(28 + j) == '1') {
                            sum += Math.pow(2, 3 - j);
                        }
                    }
                   //有进位
                    if (tmp3.charAt(27) == '1' || sum >= 10) {
                        tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000110")));
                    }
                    if (tmp3.charAt(27) == '1') {
                        jinwei[i - 1] = 1;
                    }
                    ret[i] = (tmp3.substring(28));
                }

                /**
                 * 判断最高位（的上一位）有没有出现进位
                 * 如果有，则不用更改结果
                 * 如果没有， 则需要修正：
                 * 对结果按位反转后加1，并将结果符号设为与两个数相反的符号
                 * 注意jinwei[] 在过程中被多次使用， 需要在不用的时候清空，但不能在还需要判断有无进位的时候清空
                 */

                if (jinwei[maxlen] == 0) {
                    //清空进位
                    for (int i = 0; i < 8; i++) {
                        jinwei[i] = 0;
                    }

                    //按位反转 + 1
                    for (int i = 1; i < 8; i++) {
                        ret[i] = Reverse(new StringBuilder(ret[i]));
                    }
                    for (int i = 7; i > 0; i--) {
                        StringBuilder tmp1;
                        if (i == 7) {
                            tmp1 = new StringBuilder("00000000000000000000000000000001");
                        } else {
                            tmp1 = new StringBuilder("00000000000000000000000000000000");
                        }
                        StringBuilder tmp2 = new StringBuilder("0000000000000000000000000000");
                        tmp2.append(ret[i]);

                        ALU alu = new ALU();
                        //tmp1 + tmp2
                        String tmp3 = String.valueOf(alu.add(new DataType(tmp1.toString()), new DataType(tmp2.toString())));

                        if (jinwei[i] == 1) {
                            tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000001")));
                        }
                        int sum = 0;
                        for (int j = 0; j < 4; j++) {
                            if (tmp3.charAt(28 + j) == '1') {
                                sum += Math.pow(2, 3 - j);
                            }
                        }

                        //有进位
                        if (tmp3.charAt(27) == '1' || sum >= 10) {
                            tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000110")));
                        }
                        if (tmp3.charAt(27) == '1') {
                            jinwei[i - 1] = 1;
                        }

                        ret[i] = (tmp3.substring(28));
                    }

                    /**
                     * 如果此时结果已经为0
                     * 则应该直接返回 +0
                     */
                    int flag = 0;
                    for (int i = 1; i < 8; i++) {
                        if (!"0000".equals(ret[i].toString())) {
                            flag = 1;
                        }
                    }
                    if (flag == 1) {
                        retu.append("1101");
                    } else {
                        retu.append("1100");
                    }
                } else {
                    retu.append("1100");
                }
                for (int i = 1; i < 8; i++) {
                    retu.append(ret[i]);
                }
                return new DataType(retu.toString());
            } else {
                // a == b == -
                for (int i = 1; i < 8; i++) {
                    add1[i] = new StringBuilder(Reverse(add1[i]));
                }
                for (int i = 7; i > 0; i--) {
                    StringBuilder tmp1;
                    if (i == 7) {
                        tmp1 = new StringBuilder("00000000000000000000000000000001");
                    } else {
                        tmp1 = new StringBuilder("00000000000000000000000000000000");
                    }
                    StringBuilder tmp2 = new StringBuilder("0000000000000000000000000000");
                    tmp2.append(add1[i]);
                    ALU alu = new ALU();
                    //tmp1 + tmp2
                    String tmp3 = String.valueOf(alu.add(new DataType(tmp1.toString()), new DataType(tmp2.toString())));

                    if (jinwei[i] == 1) {
                        tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000001")));
                    }
                    int sum = 0;
                    for (int j = 0; j < 4; j++) {
                        if (tmp3.charAt(28 + j) == '1') {
                            sum += Math.pow(2, 3 - j);
                        }
                    }
                    //有进位
                    if (tmp3.charAt(27) == '1' || sum >= 10) {
                        tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000110")));
                    }
                    if (tmp3.charAt(27) == '1') {
                        jinwei[i - 1] = 1;
                    }
                    add1[i] = new StringBuilder((tmp3.substring(28)));
                }

                //清空进位
                for (int i = 0; i < 8; i++) {
                    jinwei[i] = 0;
                }

                for (int i = 7; i > 0; i--) {
                    StringBuilder tmp1 = new StringBuilder("0000000000000000000000000000");
                    tmp1.append(add1[i]);
                    StringBuilder tmp2 = new StringBuilder("0000000000000000000000000000");
                    tmp2.append(add2[i]);

                    ALU alu = new ALU();
                    //tmp1 + tmp2
                    String tmp3 = String.valueOf(alu.add(new DataType(tmp1.toString()), new DataType(tmp2.toString())));


                    if (jinwei[i] == 1) {
                        tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000001")));

                    }
                    int sum = 0;
                    for (int j = 0; j < 4; j++) {
                        if (tmp3.charAt(28 + j) == '1') {
                            sum += Math.pow(2, 3 - j);
                        }
                    }


                    //有进位
                    if (tmp3.charAt(27) == '1' || sum >= 10) {
                        tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000110")));
                    }
                    if (tmp3.charAt(27) == '1') {
                        jinwei[i - 1] = 1;
                    }

                    ret[i] = (tmp3.substring(28));
                }


                if (jinwei[maxlen] == 0) {
                    //清空进位
                    for (int i = 0; i < 8; i++) {
                        jinwei[i] = 0;
                    }
                    retu.append("1100");
                    //按位反转 + 1
                    for (int i = 1; i < 8; i++) {
                        ret[i] = Reverse(new StringBuilder(ret[i]));
                    }
                    for (int i = 7; i > 0; i--) {
                        StringBuilder tmp1;
                        if (i == 7) {
                            tmp1 = new StringBuilder("00000000000000000000000000000001");
                        } else {
                            tmp1 = new StringBuilder("00000000000000000000000000000000");
                        }
                        StringBuilder tmp2 = new StringBuilder("0000000000000000000000000000");
                        tmp2.append(ret[i]);

                        ALU alu = new ALU();
                        //tmp1 + tmp2
                        String tmp3 = String.valueOf(alu.add(new DataType(tmp1.toString()), new DataType(tmp2.toString())));

                        if (jinwei[i] == 1) {
                            tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000001")));
                        }
                        int sum = 0;
                        for (int j = 0; j < 4; j++) {
                            if (tmp3.charAt(28 + j) == '1') {
                                sum += Math.pow(2, 3 - j);
                            }
                        }


                        //有进位
                        if (tmp3.charAt(27) == '1' || sum >= 10) {
                            tmp3 = String.valueOf(alu.add(new DataType(tmp3), new DataType("00000000000000000000000000000110")));
                        }
                        if (tmp3.charAt(27) == '1') {
                            jinwei[i - 1] = 1;
                        }
//
 //                       System.out.println("jinweihou tmp3 is " + tmp3);
//
                        ret[i] = (tmp3.substring(28));
                    }

                } else {
                    int flag = 0;
                    for (int i = 1; i < 8; i++) {
                        if (!"0000".equals(ret[i].toString())) {
                            flag = 1;
                        }
                    }
                    if (flag == 1) {
                        retu.append("1101");
                    } else {
                        retu.append("1100");
                    }
                }
                for (int i = 1; i < 8; i++) {
                    retu.append(ret[i]);
                }
                return new DataType(retu.toString());
            }

        } else {
            if (fuhaoA == 1 && fuhaoB == 0) {
                // a == - b == +  a 反转 b + a  0 + 0
                NBCDU nbcdu = new NBCDU();

                StringBuilder srctmp = new StringBuilder("1100");
                StringBuilder desttmp = new StringBuilder("1100");
                for (int i = 1; i < 8; i++) {
                    desttmp.append(add1[i]);
                    srctmp.append(add2[i]);
                }
                return nbcdu.add(new DataType(srctmp.toString()), new DataType(desttmp.toString()));
            }

            if (fuhaoA == 0 && fuhaoB == 1) {
                // a == + b == -  a反转 b + a
                // 1 + 1
                NBCDU nbcdu = new NBCDU();

                StringBuilder srctmp = new StringBuilder("1101");
                StringBuilder desttmp = new StringBuilder("1101");
                for (int i = 1; i < 8; i++) {
                    desttmp.append(add1[i]);
                    srctmp.append(add2[i]);
                }
                return nbcdu.add(new DataType(srctmp.toString()), new DataType(desttmp.toString()));
            }
        }
        return null;
    }

    public String Reverse(StringBuilder add1) {
        if(Objects.equals(add1.toString(), "0000"))
        {
            return "1001";
        }

        if(Objects.equals(add1.toString(),"0001"))
        {
            return "1000";
        }

        if(Objects.equals(add1.toString(),"0010"))
        {
            return "0111";
        }

        if(Objects.equals(add1.toString(),"0011"))
        {
            return "0110";
        }

        if(Objects.equals(add1.toString(),"0100"))
        {
            return "0101";
        }

        if(Objects.equals(add1.toString(),"0101"))
        {
            return "0100";
        }

        if(Objects.equals(add1.toString(),"0110"))
        {
            return "0011";
        }

        if(Objects.equals(add1.toString(),"0111"))
        {
            return "0010";
        }

        if(Objects.equals(add1.toString(),"1000"))
        {
            return "0001";
        }

        return "0000";
    }
}
