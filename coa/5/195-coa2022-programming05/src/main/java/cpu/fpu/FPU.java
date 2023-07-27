package cpu.fpu;

import cpu.alu.ALU;
import util.DataType;
import util.IEEE754Float;
import util.Transformer;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用3位保护位进行计算
 */
public class FPU {

    private final String[][] addCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN}
    };

    private final String[][] subCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN}
    };

    /**
     * compute the float add of (dest + src)
     */
    public DataType add(DataType src, DataType dest) {
        StringBuilder a = new StringBuilder(src.toString());
        StringBuilder b = new StringBuilder(dest.toString());
        String ret;

        //处理边界、NaN
        String IsCorner = cornerCheck(addCorner, a.toString(), b.toString());
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
        char fuhaoA = a.charAt(0);
        char fuhaoB= b.charAt(0);
        char fuhaoRet = 0;
        StringBuilder jiemaA = new StringBuilder(a.substring(1, 9));
        StringBuilder jiemaB = new StringBuilder(b.substring(1, 9));
        StringBuilder jiemaRet;
        StringBuilder weishuRet = null;
        StringBuilder weishuA;
        StringBuilder weishuB;
        int jiemaIntA;
        int jiemaIntB;
        int jiemaIntRet;
        //无穷大
        if (jiemaA.toString().equals("11111111")) {
            return new DataType(a.toString());
        }
        if (jiemaB.toString().equals("11111111")) {
            return new DataType(b.toString());
        }


        //阶码全为0
        if (jiemaA.toString().equals("00000000")) {
            //非规格化
            jiemaIntA = -126;
            weishuA = new StringBuilder("0");
            if (a.toString().equals("00000000000000000000000000000000")) {
                return dest;
            }
            jiemaA = new StringBuilder("00000001");
        } else {
            //规格化
            jiemaIntA = -127;
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
            jiemaIntB = -126;
            weishuB = new StringBuilder("0");
            if (b.toString().equals("00000000000000000000000000000000")) {
                return src;
            }
            jiemaB = new StringBuilder("00000001");
        } else {
            //规格化
            jiemaIntB = -127;
            for (int i = 0; i < 8; i++) {
                if (jiemaB.charAt(i) == '1') {
                    jiemaIntB += Math.pow(2, 7 - i);
                }
            }
            weishuB = new StringBuilder("1");
        }
        weishuB.append(new StringBuilder(b.substring(9)));
        weishuB.append(new StringBuilder("000"));

        //对阶
        if (jiemaIntA > jiemaIntB) {
            //A > B
            int chazhi = jiemaIntA - jiemaIntB;
            jiemaIntRet = jiemaIntA;
            jiemaRet = jiemaA;
            weishuB = new StringBuilder(rightShift(weishuB.toString(), chazhi));

        } else if (jiemaIntA < jiemaIntB){
            //A < B
            int chazhi = jiemaIntB - jiemaIntA;
            jiemaIntRet = jiemaIntB;
            jiemaRet = jiemaB;
            weishuA = new StringBuilder(rightShift(weishuA.toString(), chazhi));
        } else {
            // A = B
            jiemaIntRet = jiemaIntA;
            jiemaRet = jiemaA;
        }

        ALU alu = new ALU();

        //calculate
        weishuA.insert(0, "00000");
        weishuB.insert(0, "00000");
        if (fuhaoA == '0' && fuhaoB == '1') {
            //pos + neg =
            String weishuRettmp =
                    (alu.sub(new DataType(weishuB.toString()),
                            new DataType(weishuA.toString())).toString());
            if (weishuRettmp.charAt(0) == '1') {
                fuhaoRet = '1';
                 weishuRet = new StringBuilder
                        (alu.sub(new DataType(weishuA.toString()),
                                new DataType(weishuB.toString())).toString().substring(5));
            } else {
                fuhaoRet = '0';
                weishuRet = new StringBuilder
                        (alu.sub(new DataType(weishuB.toString()),
                                new DataType(weishuA.toString())).toString().substring(5));
            }
        } else if (fuhaoA == '1' && fuhaoB == '0') {
            //neg + pos =
            String weishuRettmp =
                    (alu.sub(new DataType(weishuA.toString()),
                            new DataType(weishuB.toString())).toString());
            if (weishuRettmp.charAt(0) == '1') {
                fuhaoRet = '1';
                weishuRet = new StringBuilder
                        (alu.sub(new DataType(weishuB.toString()),
                                new DataType(weishuA.toString())).toString().substring(5));
            } else {
                fuhaoRet = '0';
                weishuRet = new StringBuilder
                        (alu.sub(new DataType(weishuA.toString()),
                                new DataType(weishuB.toString())).toString().substring(5));
            }
        } else if (fuhaoA == '1' && fuhaoB == '1'){
            //neg + neg
            fuhaoRet = '1';
            String weishuRettmp =
                    (alu.add(new DataType(weishuA.toString()),
                            new DataType(weishuB.toString())).toString());
            if (weishuRettmp.charAt(4) == '1') {
                // 产生了进位 阶码溢出
                jiemaIntRet += 1;
                if (jiemaIntRet == 128) {
                    return new DataType(IEEE754Float.N_INF);
                }
                weishuRet = new StringBuilder(rightShift(weishuRettmp, 1).substring(5));
            } else {
                //未产生进位
                weishuRet = new StringBuilder(weishuRettmp.substring(5));
            }
        } else if (fuhaoA == '0' && fuhaoB == '0') {
            //pos + pos
            fuhaoRet = '0';
            String weishuRettmp =
                    (alu.add(new DataType(weishuA.toString()),
                            new DataType(weishuB.toString())).toString());

            if (weishuRettmp.charAt(4) == '1') {
                // 产生了进位 阶码溢出
                jiemaIntRet += 1;
                if (jiemaIntRet == 128) {
                    return new DataType(IEEE754Float.P_INF);
                }
                weishuRet = new StringBuilder(rightShift(weishuRettmp, 1).substring(5));
            } else {
                //未产生进位
                weishuRet = new StringBuilder(weishuRettmp.substring(5));
            }
        }
        //从27位尾数开头开始有几位0
        int count = 0;
        for (int i = 0; i < 27; i++) {
            if (weishuRet.charAt(i) == '1') {
                break;
            } else {
                count++;
            }
        }
        //左移

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 26; j++) {
                weishuRet.setCharAt(j, weishuRet.charAt(j + 1));
            }
            weishuRet.setCharAt(26, '0');
            jiemaIntRet--;
            if (jiemaIntRet <= -126) {
                jiemaRet = new StringBuilder("00000000");
                weishuRet = new StringBuilder(rightShift(weishuRet.toString(), 1));
                break;
            }
        }
        if (jiemaIntRet != -126 || !jiemaRet.toString().equals("00000000")) {
            if (weishuRet.toString().equals("000000000000000000000000000")) {
                jiemaIntRet = -127;
            }
            jiemaRet = new StringBuilder(Transformer.intToBinary(String.valueOf(jiemaIntRet + 127)).substring(24));
        }
        ret = round(fuhaoRet, jiemaRet.toString(), weishuRet.toString());
        return new DataType(ret);
    }

    /**
     * compute the float add of (dest - src)
     */
    public DataType sub(DataType src, DataType dest) {
        StringBuilder a = new StringBuilder(src.toString());
        StringBuilder b = new StringBuilder(dest.toString());
        StringBuilder ret = new StringBuilder();

        //处理边界、NaN
        String IsCorner = cornerCheck(subCorner, a.toString(), b.toString());
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
        if (a.charAt(0) == '0') {
            a.setCharAt(0, '1');
        } else {
            a.setCharAt(0, '0');
        }

        //dest(b) - src(a)
        return add(new DataType(a.toString()), new DataType(b.toString()));
    }


    private String cornerCheck(String[][] cornerMatrix, String oprA, String oprB) {
        for (String[] matrix : cornerMatrix) {
            if (oprA.equals(matrix[0]) && oprB.equals(matrix[1])) {
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
        if (grs > 4) {
            sig = oneAdder(sig);
        } else if (grs == 4 && sig.endsWith("1")) {
            sig = oneAdder(sig);
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
        StringBuilder temp = new StringBuilder(operand);
        temp.reverse();
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
