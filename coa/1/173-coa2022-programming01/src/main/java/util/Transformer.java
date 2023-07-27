package util;

import java.text.DecimalFormat;
import java.util.Map;

import static java.lang.Math.*;

public class Transformer {

    public static String intToBinary(String numStr) {
        StringBuilder str = new StringBuilder(new String());
        char[] tmp1 = new char[32];
        for (int i = 0; i < 32; i++) {
            tmp1[i] = '0';
        }
        int yushu = 0;
        int chushu = Integer.parseInt(numStr);
        if (chushu >= 0) { // +
            int i = 31;
            while (chushu != 0) {
                yushu = chushu % 2;
                chushu = chushu / 2;
                if (yushu == 0) {
                    tmp1[i] = '0';
                } else { // yushu == 1
                    tmp1[i] = '1';
                }
                i--;
            }
            for (int j = 0; j < 32; j++) {
                str.append(tmp1[j]);
            }
        }  else if (chushu < 0 && chushu != -2147483648){// -
            chushu = chushu * (-1);
            // 当作正数处理
            int i = 31;
            while (chushu != 0) {
                yushu = chushu % 2;
                chushu = chushu / 2;
                if (yushu == 0) {
                    tmp1[i] = '0';
                } else { // yushu == 1
                    tmp1[i] = '1';
                }
                i--;
            }

            // 取反
            /**
             * int lang;
             * String str2 = Long.toBinaryString(~lang) 取反的方式 被禁止了
             */
            char[] tmp2 = new char[32];
            for (int j = 0; j < 32; j++) {
                if (tmp1[j] == '0') {
                    tmp2[j] = '1';
                } else if (tmp1[j] == '1'){
                    tmp2[j] = '0';
                }
            }
            //加一
            int plus = 0;
            if (tmp2[31] == '0') {
                plus = 0;
            } else if (tmp2[31] == '1'){
                plus = 1;
            }
            for (int j = 31; j >= 1; j--) {
                if (plus == 1) {
                    if (tmp2[j] == '0') {
                        tmp2[j] = '1';
                        break;
                    } else if (tmp2[j] == '1'){
                        tmp2[j] = '0';
                    }
                } else { //plus = 0
                    if (tmp2[j] == '0') {
                        tmp2[j] = '1';
                        break;
                    } else if (tmp2[j] == '1'){
                        tmp2[j] = '0';
                        plus = 1;
                    }
                }
            }

            for (int j = 0; j < 32; j++) {
                str.append(tmp2[j]);
            }
        } else if (chushu == -2147483648) {
            str.append("10000000000000000000000000000000");
        }

        return str.toString();
    }

    public static String binaryToInt(String binStr) {
        String str = new String();
        int sum = 0;

        /**
         * 正负需要分别处理
         */

        if (binStr.charAt(0) == '0') {
            for (int i = 31; i >= 1; i--) {
                sum += Integer.parseInt(String.valueOf(binStr.charAt(i))) * pow(2, 31- i);
            }
            str = String.valueOf(sum);
        } else if (binStr.charAt(0) == '1'){ // first bit is 1
            if (binStr.equals("10000000000000000000000000000000")) {
                str = "-2147483648";
            } else {
                // 取反
                char[] tmp1 = new char[32];
                for (int i = 0; i < 32; i++) {
                    if (binStr.charAt(i) == '0') {
                        tmp1[i] = '1';
                    } else if (binStr.charAt(i) == '1'){
                        tmp1[i] = '0';
                    }
                }
                //加一
                int plus = 0;
                if (tmp1[31] == '0') {
                    plus = 0;
                } else if (tmp1[31] == '1'){
                    plus = 1;
                }
                for (int i = 31; i >= 1; i--) {
                    if (plus == 1) {
                        if (tmp1[i] == '0') {
                            tmp1[i] = '1';
                            break;
                        } else if (tmp1[i] == '1'){
                            tmp1[i] = '0';
                        }
                    } else { //plus = 0
                        if (tmp1[i] == '0') {
                            tmp1[i] = '1';
                            break;
                        } else if (tmp1[i] == '1'){
                            tmp1[i] = '0';
                            plus = 1;
                        }
                    }
                }
                //normal handle
                for (int i = 31; i >= 1; i--) {
                    sum -= Integer.parseInt(String.valueOf(tmp1[i])) * pow(2, 31- i);
                }
                str = String.valueOf(sum);
            }

        }

        return str;
    }

    public static String decimalToNBCD(String decimalStr) {
        StringBuilder str = new StringBuilder();
        int decimal = Integer.parseInt(decimalStr);
        int[] arr = new int[7];

        int len = decimalStr.length();
        if (decimal >= 0) {
            str.append("1100");
            for (int i = 0; i < len; i++) {
                arr[6 - i] = Integer.parseInt(String.valueOf(decimalStr.charAt(len - 1 - i)));
            }
        } else {// < 0
            str.append("1101");
            for (int i = 0; i < len - 1; i++) {
                arr[6 - i] = Integer.parseInt(String.valueOf(decimalStr.charAt(len - 1 - i)));
            }
        }
        for (int j = 0; j < 7; j++) {
            switch (arr[j]) {
                case 9:
                    str.append("1001");
                    break;
                case 8:
                    str.append("1000");
                    break;
                case 7:
                    str.append("0111");
                    break;
                case 6:
                    str.append("0110");
                    break;
                case 5:
                    str.append("0101");
                    break;
                case 4:
                    str.append("0100");
                    break;
                case 3:
                    str.append("0011");
                    break;
                case 2:
                    str.append("0010");
                    break;
                case 1:
                    str.append("0001");
                    break;
                case 0:
                    str.append("0000");
                    break;
            }

        }
        return str.toString();
    }

    public static String NBCDToDecimal(String NBCDStr) {
        StringBuilder str = new StringBuilder();
        int sum = 0;
        int flag = 0;
        if (NBCDStr.charAt(3) == '0') { // 1100

        } else { // 1101
            if (!("11010000000000000000000000000000".equals(NBCDStr))) {
                str.append('-');
            }
        }
        for (int i = 1; i < 8; i++) {
            sum = 0;
            for (int j = 0; j < 4; j++) {
                sum += Integer.parseInt(String.valueOf(NBCDStr.charAt(4 * i + j))) * pow(2, 3 - j);
            }
            if (sum != 0) {
                flag = 1;
            }
            if (flag != 0) {
                str.append(sum);
            }
        }
        if ("11010000000000000000000000000000".equals(NBCDStr) || "11000000000000000000000000000000".equals(NBCDStr)) {
            str.append(0);
        }
        return str.toString();
    }

    public static String floatToBinary(String floatStr) {
        StringBuilder str = new StringBuilder();
        double doublenum = Double.parseDouble(floatStr);
        // == 0
        if (floatStr.equals("-0")) {
            return "10000000000000000000000000000000";
        }
        if (doublenum > Float.MAX_VALUE) {
            return "+Inf";
        }
        if (doublenum < -Float.MAX_VALUE) {
            return "-Inf";
        }
        //address
        float floatnum = Float.parseFloat(floatStr);
        if (floatnum >= 0) { // > 0
            str.append('0');
        } else { // < 0
            str.append('1');
            floatnum = -floatnum;
        }

        //非规格化
        if (floatnum < Float.MIN_NORMAL && floatnum > -Float.MIN_NORMAL) {
            str.append("00000000");
            float tmp = floatnum * (float) pow(2, 126);
            while (tmp != 0) {
                tmp *= 2;
                if (tmp >= 1) {
                    tmp -= 1;
                    str.append(1);
                } else {
                    str.append(0);
                }
            }
            int lens = 32 - str.length();
            for (int i = 0; i < lens; i++) {
                str.append(0);
            }
            return str.toString();
        }

        if ((-Float.MAX_VALUE <= floatnum && floatnum <= -Float.MIN_NORMAL) ||
                (Float.MIN_NORMAL <= floatnum && floatnum <= Float.MAX_VALUE) ){
            int yima = 0;
            StringBuilder sb = new StringBuilder();
            //规格化
            if (floatnum >= 1) {
                while (floatnum >= 2) {
                    floatnum /= 2;
                    yima++;
                }
            }
            if (floatnum < 1) {
                while (floatnum < 1) {
                    floatnum *= 2;
                    yima--;
                }
            }

            yima += 127;

            for (int i = 0; i < 8; i++) {
                sb.append(yima % 2);
                yima /= 2;
            }
            for (int i = 7; i >= 0; i--) {
                str.append(sb.charAt(i));
            }

            double tmp = (floatnum - 1) * pow(2, 23);
            StringBuilder sa = new StringBuilder();
            int k = (int)tmp;
            for (int i = 0; i < 23; i++) {
                sa.append(k % 2);
                k /= 2;
            }
            for (int i = 22; i >= 0; i--) {
                str.append(sa.charAt(i));
            }
            return str.toString();
        }
        return null;
    }

    public static String binaryToFloat(String binStr) {
        StringBuilder str = new StringBuilder();
        int yima = 0;
        float sum = 1;
        int[] arr = new int[24];
        arr[0] = 1;
        if (binStr.charAt(0) == '0') { // > 0

            for (int i = 1; i < 9; i++) {
                yima += (float)Math.pow(2, 8 - i) * Integer.parseInt(String.valueOf(binStr.charAt(i)));
            }
            if (yima == 255) {
                return "+Inf";
            } else {
                yima = yima - 127;
                for (int i = 1; i < 24; i++) {
                    arr[i] = Integer.parseInt(String.valueOf(binStr.charAt(8 + i)));
                }
                if (yima == -127) { //非规格化
                    sum = 0;
                    for (int i = 1; i < 24; i++) {
                        sum += (float) Math.pow(0.5, i) * arr[i];
                    }
                    sum = sum * (float) Math.pow(2, -126);
                } else {//规格化
                    for (int i = 1; i < 24; i++) {
                        sum += (float) Math.pow(0.5, i) * arr[i];
                    }
                    sum = sum * (float) Math.pow(2, yima);
                }
                return String.valueOf(sum);

            }


        } else { // < 0

            str.append('-');
            for (int i = 1; i < 9; i++) {
                yima += (float)Math.pow(2, 8 - i) * Integer.parseInt(String.valueOf(binStr.charAt(i)));
            }
            if (yima == 255) {
                str.append("Inf");
            } else {
                yima = yima - 127;
                for (int i = 1; i < 24; i++) {
                    arr[i] = Integer.parseInt(String.valueOf(binStr.charAt(8 + i)));
                }
                if (yima == -127) { //非规格化
                    sum = 0;
                    for (int i = 1; i < 24; i++) {
                        sum += (float) Math.pow(0.5, i) * arr[i];
                    }
                    sum = sum * (float) Math.pow(2, -126);
                } else {//规格化
                    for (int i = 1; i < 24; i++) {
                        sum += (float) Math.pow(0.5, i) * arr[i];
                    }
                    sum = sum * (float) Math.pow(2, yima);
                }
                String tmp = String.valueOf(sum);
                str.append(tmp);
            }

            return str.toString();

        }
    }

}
