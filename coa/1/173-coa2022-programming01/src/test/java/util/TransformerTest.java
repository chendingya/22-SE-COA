package util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransformerTest {

    @Test
    public void intToBinaryTest1() {
        assertEquals("00000000000000000000000000000010", Transformer.intToBinary("2"));
    }

    @Test
    public void binaryToIntTest1() {
        assertEquals("2", Transformer.binaryToInt("00000000000000000000000000000010"));
    }

    @Test
    public void decimalToNBCDTest1() {
        assertEquals("11010000011001010100001100100001", Transformer.decimalToNBCD("-654321"));
    }

    @Test
    public void NBCDToDecimalTest1() {
        assertEquals("10", Transformer.NBCDToDecimal("11000000000000000000000000010000"));
    }

    @Test
    public void floatToBinaryTest1() {
        assertEquals("00000000010000000000000000000000", Transformer.floatToBinary(String.valueOf(Math.pow(2, -127))));
    }
    @Test
    public void floatToBinaryTest11() {
        assertEquals("00000000011000000000000000000000", Transformer.floatToBinary(String.valueOf(0.75 * Math.pow(2, -126))));
    }
    @Test
    public void floatToBinaryTest0() {
        assertEquals("00000000000000000000000000000000", Transformer.floatToBinary(String.valueOf(0)));
    }
    @Test
    public void floatToBinaryTest01() {
        assertEquals("10000000000000000000000000000000", Transformer.floatToBinary(String.valueOf("-0")));
    }
    @Test
    public void floatToBinaryTest21() {
        assertEquals("10000000011000000000000000000000", Transformer.floatToBinary(String.valueOf(-0.75 * Math.pow(2, -126))));
    }

    @Test
    public void floatToBinaryTest2() {
        assertEquals("+Inf", Transformer.floatToBinary("" + Double.MAX_VALUE)); // 对于float来说溢出
    }

    @Test
    public void binaryToFloatTest1() {
        assertEquals(String.valueOf((float) Math.pow(2, -127)), Transformer.binaryToFloat("00000000010000000000000000000000"));
    }
    @Test
    public void binaryToFloatTest2() {
        assertEquals("+Inf", Transformer.binaryToFloat("011111111000000000000000000000000"));
    }

}
