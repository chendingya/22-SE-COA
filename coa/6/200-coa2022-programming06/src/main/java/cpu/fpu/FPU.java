package cpu.fpu;

import cpu.alu.ALU;
import util.DataType;
import util.IEEE754Float;
import util.Transformer;

import java.util.Arrays;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用3位保护位进行计算
 */
public class FPU {

    private final String[][] mulCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN}
    };

    private final String[][] divCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
    };

    /**
     * compute the float mul of dest * src
     */
    public DataType mul(DataType src, DataType dest) {

        //处理边界、NaN
        String IsCorner = cornerCheck(mulCorner, src.toString(), dest.toString());
        if (IsCorner == null) {
            String aNaN = dest.toString();
            String bNaN = src.toString();
            if (aNaN.matches(IEEE754Float.NaN_Regular) || bNaN.matches(IEEE754Float.NaN_Regular)) {
                return new DataType(IEEE754Float.NaN);
            }
        } else {
            return new DataType(IsCorner);
        }

        //非边界和NaN
        StringBuilder a = new StringBuilder(src.toString());
        StringBuilder b = new StringBuilder(dest.toString());
        String ret = null;
        char fuhaoA = a.charAt(0);
        char fuhaoB= b.charAt(0);
        char fuhaoRet = 0;
        StringBuilder jiemaA = new StringBuilder(a.substring(1, 9));
        StringBuilder jiemaB = new StringBuilder(b.substring(1, 9));
        StringBuilder jiemaRet;
        StringBuilder weishuRet = new StringBuilder("");
        StringBuilder weishuA;
        StringBuilder weishuB;
        int jiemaIntA;
        int jiemaIntB;
        int jiemaIntRet;
        //无穷大
        if (jiemaA.toString().equals("11111111")) {
            if (fuhaoA == '0' && fuhaoB == '0') {
                return new DataType(IEEE754Float.P_INF);
            }
            if (fuhaoA == '0' && fuhaoB == '1') {
                return new DataType(IEEE754Float.N_INF);
            }
            if (fuhaoA == '1' && fuhaoB == '0') {
                return new DataType(IEEE754Float.N_INF);
            }
            if (fuhaoA == '1' && fuhaoB == '1') {
                return new DataType(IEEE754Float.P_INF);
            }
        }
        if (jiemaB.toString().equals("11111111")) {
            if (fuhaoA == '0' && fuhaoB == '0') {
                return new DataType(IEEE754Float.P_INF);
            }
            if (fuhaoA == '0' && fuhaoB == '1') {
                return new DataType(IEEE754Float.N_INF);
            }
            if (fuhaoA == '1' && fuhaoB == '0') {
                return new DataType(IEEE754Float.N_INF);
            }
            if (fuhaoA == '1' && fuhaoB == '1') {
                return new DataType(IEEE754Float.P_INF);
            }
        }

        //符号位计算
        if (fuhaoA == '0' && fuhaoB == '0') {
            fuhaoRet = '0';
        }
        if (fuhaoA == '0' && fuhaoB == '1') {
            fuhaoRet = '1';
        }
        if (fuhaoA == '1' && fuhaoB == '0') {
            fuhaoRet = '1';
        }
        if (fuhaoA == '1' && fuhaoB == '1') {
            fuhaoRet = '0';
        }
        //阶码全为0
        if (jiemaA.toString().equals("00000000")) {
            //非规格化
            jiemaIntA = 1;
            weishuA = new StringBuilder("0");
            if (a.toString().equals("00000000000000000000000000000000")) {
                if (fuhaoRet == '0') {
                    return (new DataType("00000000000000000000000000000000"));
                } else {
                    return (new DataType("10000000000000000000000000000000"));
                }
            }
            jiemaA = new StringBuilder("00000001");
        } else {
            //规格化
            jiemaIntA = 0;
            for (int i = 0; i < 8; i++) {
                if (jiemaA.charAt(i) == '1') {
                    jiemaIntA += Math.pow(2, 7 - i);
                }
            }
            weishuA = new StringBuilder("1");
        }
        weishuA.append(new StringBuilder(a.substring(9)));
        weishuA.append(new StringBuilder("000"));

        if (jiemaB.toString().equals("00000000")) {
            //非规格化
            jiemaIntB = 1;
            weishuB = new StringBuilder("0");
            if (b.toString().equals("00000000000000000000000000000000")) {
                if (fuhaoRet == '0') {
                    return (new DataType("00000000000000000000000000000000"));
                } else {
                    return (new DataType("10000000000000000000000000000000"));
                }
            }
            jiemaB = new StringBuilder("00000001");
        } else {
            //规格化
            jiemaIntB = 0;
            for (int i = 0; i < 8; i++) {
                if (jiemaB.charAt(i) == '1') {
                    jiemaIntB += Math.pow(2, 7 - i);
                }
            }
            weishuB = new StringBuilder("1");
        }
        weishuB.append(new StringBuilder(b.substring(9)));
        weishuB.append(new StringBuilder("000"));

        //阶码
        jiemaIntRet = jiemaIntA + jiemaIntB - 127;

        //尾数
        String mul1 = weishuA.toString();
        String mul2 = weishuB.toString();
        //计算控制数组
        int[] ac = new int[27];

        for (int i = 26; i >= 0; i--) {
            if (mul2.charAt(i) == '0') {
                ac[i] = 0;
            } else {
                ac[i] = 1;
            }
        }
        char[] ans = new char[54];
        for (int i = 0; i < 54; i++) {
            ans[i] = '0';
        }
        ALU alu = new ALU();

        for (int i = 26; i >= 0; i--) {
            int jinwei = 0;
            if (ac[i] == 1) {
                StringBuilder tmp2 = new StringBuilder("");
                StringBuilder tmp1 = new StringBuilder(mul1);
                for (int j = 0; j < 27; j++) {
                    tmp2.append(ans[j]);
                }
                tmp2.append("00000");
                tmp1.append("00000");

                //加法
                int[] srcInt = new int[27];
                int[] destInt = new int[27];
                int[] s = new int[27];
                for (int j = 0; j < 27; j++) {
                    if (tmp1.charAt(j) == '0') {
                        srcInt[j] = 0;
                    } else {
                        srcInt[j] = 1;
                    }
                    if (tmp2.charAt(j) == '0') {
                        destInt[j] = 0;
                    } else {
                        destInt[j] = 1;
                    }
                }

                int[] c = new int[27];
                for (int j = 0; j < 27; j++) {
                    c[j] = 0;
                }
                for (int j = 26; j >= 0; j--) {
                    if (j != 0) {
                        s[j] = srcInt[j] ^ destInt[j] ^ c[j];
                        c[j - 1] = (srcInt[j] & destInt[j]) | (srcInt[j] & c[j]) | (destInt[j] & c[j]);
                    } else { //j == 0
                        s[j] = srcInt[j] ^ destInt[j] ^ c[j];
                        jinwei = (srcInt[j] & destInt[j]) | (srcInt[j] & c[j]) | (destInt[j] & c[j]);
                    }
                }

                for (int j = 0; j < 27; j++) {
                    if (s[j] == 0) {
                        ans[j] = '0';
                    } else {
                        ans[j] = '1';
                    }
                }
            }

            // 右移
            for (int j = 26 + 26 - i; j >= 0; j--) {
                ans[j + 1] = ans[j];
            }
            if (jinwei == 0) {
                ans[0] = '0';
            } else {
                ans[0] = '1';
            }
        }
        jiemaIntRet += 1;

        //规格化并舍入后返回
        //尾数规格化
        if (ans[0] == '0' && jiemaIntRet > 0) {
            while (jiemaIntRet > 0 && ans[0] == '0') {
                jiemaIntRet--;
                for (int i = 0; i < 53; i++) {
                    ans[i] = ans[i + 1];
                }
                ans[53] = '0';
            }
        }

        //阶码<0 && 尾数前27位不全为0
        boolean iszero = true;
        for (int i = 0; i < 27; i++) {
            if (ans[i] != 0) {
                iszero = false;
                break;
            }
        }
        if (jiemaIntRet < 0 && !iszero) {
            while (jiemaIntRet < 0 && !iszero) {
                jiemaIntRet++;
                for (int i = 52; i >= 0; i--) {
                    ans[i + 1] = ans[i];
                }
                ans[0] = '0';

                iszero = true;
                for (int i = 0; i < 27; i++) {
                    if (ans[i] != 0) {
                        iszero = false;
                        break;
                    }
                }
            }
        }


        //阶码为"11111111"，发生阶码上溢
        if (jiemaIntRet >= 255) {
            if (fuhaoRet == '0') {
                return new DataType(IEEE754Float.P_INF);
            }
            if (fuhaoRet == '1') {
                return new DataType(IEEE754Float.N_INF);
            }

        } else if (jiemaIntRet == 0) {
            //阶码 = 0 尾数右移一次化为非规格化数
            for (int i = 52; i >= 0; i--) {
                ans[i + 1] = ans[i];
            }
            ans[0] = '0';

        } else if (jiemaIntRet < 0){
            // 阶码仍小于0，发生阶码下溢，将结果置为0
            if (fuhaoRet == '0') {
                ret = "00000000000000000000000000000000";
            } else {
                ret = "10000000000000000000000000000000";
            }

        } else {
            //此时阶码正常，无需任何操作
        }

        for (int i = 0; i < 54; i++) {
            weishuRet.append(ans[i]);
        }


        jiemaRet = new StringBuilder
                (Transformer.intToBinary
                        (String.valueOf
                                (jiemaIntRet)).substring(24));

        ret = round(fuhaoRet, jiemaRet.toString(), weishuRet.toString());

        return new DataType(ret);
    }


    /**
     * compute the float mul of dest / src
     */
    public DataType div(DataType src, DataType dest) {

//
//        System.out.println(" src is " + src.toString());
 //       System.out.println("dest is " + dest.toString());
//
        //处理边界、NaN
        String IsCorner = cornerCheck(divCorner, src.toString(), dest.toString());
        if (IsCorner == null) {
            String aNaN = dest.toString();
            String bNaN = src.toString();
            if (aNaN.matches(IEEE754Float.NaN_Regular) || bNaN.matches(IEEE754Float.NaN_Regular)) {
                return new DataType(IEEE754Float.NaN);
            }
        } else {
            return new DataType(IsCorner);
        }

        //非边界和NaN
        StringBuilder a = new StringBuilder(src.toString());
        StringBuilder b = new StringBuilder(dest.toString());
        String ret = null;
        char fuhaoA = a.charAt(0);
        char fuhaoB= b.charAt(0);
        char fuhaoRet = 0;
        StringBuilder jiemaA = new StringBuilder(a.substring(1, 9));
        StringBuilder jiemaB = new StringBuilder(b.substring(1, 9));
        StringBuilder jiemaRet;
        StringBuilder weishuRet = new StringBuilder("");
        StringBuilder weishuA;
        StringBuilder weishuB;
        int jiemaIntA;
        int jiemaIntB;
        int jiemaIntRet;
        //无穷大
        //b / inf = 0
        if (jiemaA.toString().equals("11111111")) {
            if (fuhaoA == '0' && fuhaoB == '0') {
                return new DataType("00000000000000000000000000000000");
            }
            if (fuhaoA == '0' && fuhaoB == '1') {
                return new DataType("10000000000000000000000000000000");
            }
            if (fuhaoA == '1' && fuhaoB == '0') {
                return new DataType("10000000000000000000000000000000");
            }
            if (fuhaoA == '1' && fuhaoB == '1') {
                return new DataType("00000000000000000000000000000000");
            }
        }
        // inf / a = inf
        if (jiemaB.toString().equals("11111111")) {
            if (fuhaoA == '0' && fuhaoB == '0') {
                return new DataType(IEEE754Float.P_INF);
            }
            if (fuhaoA == '0' && fuhaoB == '1') {
                return new DataType(IEEE754Float.N_INF);
            }
            if (fuhaoA == '1' && fuhaoB == '0') {
                return new DataType(IEEE754Float.N_INF);
            }
            if (fuhaoA == '1' && fuhaoB == '1') {
                return new DataType(IEEE754Float.P_INF);
            }
        }

        //符号位计算
        if (fuhaoA == '0' && fuhaoB == '0') {
            fuhaoRet = '0';
        }
        if (fuhaoA == '0' && fuhaoB == '1') {
            fuhaoRet = '1';
        }
        if (fuhaoA == '1' && fuhaoB == '0') {
            fuhaoRet = '1';
        }
        if (fuhaoA == '1' && fuhaoB == '1') {
            fuhaoRet = '0';
        }

        //阶码全为0
        if (jiemaA.toString().equals("00000000")) {
            //非规格化
            jiemaIntA = 1;
            weishuA = new StringBuilder("0");
            if (a.toString().equals("00000000000000000000000000000000")) {
                // b / 0:
                if (!(b.toString().equals("00000000000000000000000000000000"))) {
                    throw new ArithmeticException();
                }
            }
            jiemaA = new StringBuilder("00000001");
        } else {
            //规格化
            jiemaIntA = 0;
            for (int i = 0; i < 8; i++) {
                if (jiemaA.charAt(i) == '1') {
                    jiemaIntA += Math.pow(2, 7 - i);
                }
            }
            weishuA = new StringBuilder("1");
        }
        weishuA.append(new StringBuilder(a.substring(9)));
        weishuA.append(new StringBuilder("000"));

        if (jiemaB.toString().equals("00000000")) {
            //非规格化
            jiemaIntB = 1;
            weishuB = new StringBuilder("0");
            if (b.toString().equals("00000000000000000000000000000000")) {
                //  0 / a
                if (!(a.toString().equals("00000000000000000000000000000000"))) {
                    return (new DataType("00000000000000000000000000000000"));
                }
            }
            jiemaB = new StringBuilder("00000001");
        } else {
            //规格化
            jiemaIntB = 0;
            for (int i = 0; i < 8; i++) {
                if (jiemaB.charAt(i) == '1') {
                    jiemaIntB += Math.pow(2, 7 - i);
                }
            }
            weishuB = new StringBuilder("1");
        }
        weishuB.append(new StringBuilder(b.substring(9)));
        weishuB.append(new StringBuilder("000"));

        //阶码
        jiemaIntRet = jiemaIntB - jiemaIntA + 127;

//
//        System.out.println("jiemab is " + jiemaIntB);
 //       System.out.println("jiemaa is " + jiemaIntA);
 //       System.out.println("jiemaret is " + jiemaIntRet);
//
        //尾数
        String mul1 = weishuA.toString();
        String mul2 = weishuB.toString();

        //mul2 / mul1
        char[] ans = new char[54];
        for (int i = 0; i < 27; i++) {
            if (mul2.charAt(i) == '1') {
                ans[i] = '1';
            } else {
                ans[i] = '0';
            }
        }
        for (int i = 27; i < 54; i++) {
            ans[i] = '0';
        }
        ALU alu = new ALU();
        StringBuilder srctmp = new StringBuilder("00000");
        //

        for (int i = 0; i < 27; i++) {
            srctmp.append(mul1.charAt(i));
        }

        //- 27轮 生成54位 ans
        char shouwei = '0';
        for (int i = 0; i < 27; i++) {
            char jinwei = '0';
            //判断够不够-
            StringBuilder desttmp = new StringBuilder("0000");
            desttmp.append(shouwei);
            for (int j = 0; j < 27; j++) {
                desttmp.append(ans[j]);
            }
//
 //           System.out.println("destmp is " + desttmp);
//
 //           System.out.println("srctmp is " + srctmp);
//
//
            String str = String.valueOf(alu.sub(new DataType(srctmp.toString()), new DataType(desttmp.toString())));


            if (str.charAt(0) == '0') {
                //够减 取5-32位作为ans
                jinwei = '1';
                for (int j = 0; j < 27; j++) {
                    ans[j] = str.charAt(j + 5);
                }
            } else {
                jinwei = '0';
            }

//
//            System.out.println("substr is " + str);
 //           System.out.println("sub jinwei is " + jinwei);
//

            /**
             * 减 并左移的算法有一个bug
             * 写给复习的我：
             * 一旦遇到减之前首位为1， 即ANS[0] == 1
             * 但是不够减的情况，就会触发
             * 原因在于左移舍去了首位1， 使得余数发生了改变，实际变小了
             * 这时候的补救方案是：
             * 将左移前的首位保存下来，放在shouwei中。
             * 补齐32位时： 先补4位0 ，再补shouwei，再补ans中前27位
             * 这样desttmp - srctmp的时候就有最高位可以借位，
             * 不会发生实际够减，算法不够减的情况
             */

            if ((jinwei == '0')) {
                if (desttmp.charAt(4) == '1' || desttmp.charAt(5) == '1') {
                    shouwei = '1';
                }
            } else {
                shouwei = '0';
            }



            // 左移
            for (int j = 0; j < 53; j++) {
                ans[j] = ans[j + 1];
            }
            ans[53] = jinwei;

//
//            System.out.print("sub ans is ");
 //           for (int j = 0; j < 54; j++) {
 //               System.out.print(ans[j]);
 //           }
  //          System.out.println();
  //          System.out.println("shouwei = " + shouwei);
//
        }
//
 //       System.out.print("sub hou ans is ");
  //      for (int j = 0; j < 54; j++) {
 //           System.out.print(ans[j]);
 //       }
 //       System.out.println();
//

        //规格化并舍入后返回
        //尾数规格化
        if (ans[27] == '0' && jiemaIntRet > 0) {
            while (jiemaIntRet > 0 && ans[27] == '0') {
                jiemaIntRet--;
                for (int i = 0; i < 53; i++) {
                    ans[i] = ans[i + 1];
                }
                ans[53] = '0';
            }
        }

//
 //       System.out.print("ans[0] == '0' && jiemaIntRet > 0 is ");
 //       for (int j = 0; j < 54; j++) {
 //           System.out.print(ans[j]);
 //       }
  //      System.out.println();
  //      System.out.println("jiemaRet is " + jiemaIntRet);
//
//

        //阶码<0 && 尾数后27位不全为0
        boolean iszero = true;
        for (int i = 0; i < 27; i++) {
            if (ans[i + 27] != 0) {
                iszero = false;
                break;
            }
        }
        if (jiemaIntRet < 0 && !iszero) {
            while (jiemaIntRet < 0 && !iszero) {
                jiemaIntRet++;
                for (int i = 52; i >= 0; i--) {
                    ans[i + 1] = ans[i];
                }
                ans[0] = '0';

                iszero = true;
                for (int i = 0; i < 27; i++) {
                    if (ans[i] != 0) {
                        iszero = false;
                        break;
                    }
                }
            }
        }
//
 //       System.out.print("jiemaIntRet < 0 && !iszero");
 //       for (int j = 0; j < 54; j++) {
 //           System.out.print(ans[j]);
 //       }
 //       System.out.println();
 //       System.out.println("jiemaRet is " + jiemaIntRet);
//
//


        //阶码为"11111111"，发生阶码上溢
        if (jiemaIntRet >= 255) {
            if (fuhaoRet == '0') {
                return new DataType(IEEE754Float.P_INF);
            }
            if (fuhaoRet == '1') {
                return new DataType(IEEE754Float.N_INF);
            }

        } else if (jiemaIntRet == 0) {
            //阶码 = 0 尾数右移一次化为非规格化数
            for (int i = 52; i >= 0; i--) {
                ans[i + 1] = ans[i];
            }
            ans[0] = '0';

        } else if (jiemaIntRet < 0){
            // 阶码仍小于0，发生阶码下溢，将结果置为0
            if (fuhaoRet == '0') {
                ret = "00000000000000000000000000000000";
            } else {
                ret = "10000000000000000000000000000000";
            }

        } else {
            //此时阶码正常，无需任何操作
        }

        for (int i = 0; i < 27; i++) {
            weishuRet.append(ans[i + 27]);
        }

//
 //       System.out.print("weishu ret is ");
 //       System.out.println(weishuRet);
//

        jiemaRet = new StringBuilder
                (Transformer.intToBinary
                        (String.valueOf
                                (jiemaIntRet)).substring(24));

        ret = round(fuhaoRet, jiemaRet.toString(), weishuRet.toString());

        return new DataType(ret);
    }


    private String cornerCheck(String[][] cornerMatrix, String oprA, String oprB) {
        for (String[] matrix : cornerMatrix) {
            if (oprA.equals(matrix[0]) &&
                    oprB.equals(matrix[1])) {
                return matrix[2];
            }
        }
        return null;
    }

    /**
     * right shift a num without considering its sign using its string format
     *
     * @param operand to be moved
     * @param n       moving nums of bits
     * @return after moving
     */
    private String rightShift(String operand, int n) {
        StringBuilder result = new StringBuilder(operand);  //保证位数不变
        boolean sticky = false;
        for (int i = 0; i < n; i++) {
            sticky = sticky || result.toString().endsWith("1");
            result.insert(0, "0");
            result.deleteCharAt(result.length() - 1);
        }
        if (sticky) {
            result.replace(operand.length() - 1, operand.length(), "1");
        }
        return result.substring(0, operand.length());
    }

    /**
     * 对GRS保护位进行舍入
     *
     * @param sign    符号位
     * @param exp     阶码
     * @param sig_grs 带隐藏位和保护位的尾数
     * @return 舍入后的结果
     */
    private String round(char sign, String exp, String sig_grs) {
        int grs = Integer.parseInt(sig_grs.substring(24, 27), 2);
        if ((sig_grs.substring(27).contains("1")) && (grs % 2 == 0)) {
            grs++;
        }
        String sig = sig_grs.substring(0, 24); // 隐藏位+23位
        if (grs > 4 || (grs == 4 && sig.endsWith("1"))) {
            sig = oneAdder(sig);
            if (sig.charAt(0) == '1') {
                exp = oneAdder(exp).substring(1);
                sig = sig.substring(1);
            }
        }

        if (Integer.parseInt(sig.substring(0, sig.length() - 23), 2) > 1) {
            sig = rightShift(sig, 1);
            exp = oneAdder(exp).substring(1);
        }
        if (exp.equals("11111111")) {
            return sign == '0' ? IEEE754Float.P_INF : IEEE754Float.N_INF;
        }

        return sign + exp + sig.substring(sig.length() - 23);
    }

    /**
     * add one to the operand
     *
     * @param operand the operand
     * @return result after adding, the first position means overflow (not equal to the carray to the next) and the remains means the result
     */
    private String oneAdder(String operand) {
        int len = operand.length();
        StringBuffer temp = new StringBuffer(operand);
        temp = temp.reverse();
        int[] num = new int[len];
        for (int i = 0; i < len; i++) num[i] = temp.charAt(i) - '0';  //先转化为反转后对应的int数组
        int bit = 0x0;
        int carry = 0x1;
        char[] res = new char[len];
        for (int i = 0; i < len; i++) {
            bit = num[i] ^ carry;
            carry = num[i] & carry;
            res[i] = (char) ('0' + bit);  //显示转化为char
        }
        String result = new StringBuffer(new String(res)).reverse().toString();
        return "" + (result.charAt(0) == operand.charAt(0) ? '0' : '1') + result;  //注意有进位不等于溢出，溢出要另外判断
    }

}
