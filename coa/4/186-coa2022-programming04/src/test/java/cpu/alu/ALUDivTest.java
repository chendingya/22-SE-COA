package cpu.alu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import static org.junit.Assert.assertEquals;

public class ALUDivTest {

    private final ALU alu = new ALU();
    private DataType src;
    private DataType dest;
    private DataType result;

    /**
     * 10 / 10 = 1 (0)
     */
    @Test
    public void DivTest1() {
        src = new DataType("00000000000000000000000000001010");
        dest = new DataType("00000000000000000000000000001010");
        result = alu.div(src, dest);
        String quotient = "00000000000000000000000000000001";
        String remainder = "00000000000000000000000000000000";
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }

    /**
     * -8 / 2 = -4 (0)
     * 除法算法固有的bug
     */
    @Test
    public void DivSpecialTest() {
        src = new DataType(Transformer.intToBinary("2"));
        dest = new DataType(Transformer.intToBinary("-8"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("-4");
        String remainder = Transformer.intToBinary("0");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }

    /**
     * 0 / 0  除0异常
     */
    @Test(expected = ArithmeticException.class)
    public void DivExceptionTest1() {
        src = new DataType("00000000000000000000000000000000");
        dest = new DataType("00000000000000000000000000000000");
        result = alu.div(src, dest);
    }


    @Test
    public void DivSpecialTest2() {
        src = new DataType(Transformer.intToBinary("3"));
        dest = new DataType(Transformer.intToBinary("-7"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("-2");
        String remainder = Transformer.intToBinary("-1");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
    @Test
    public void DivSpecialTest7() {
        src = new DataType(Transformer.intToBinary("-3"));
        dest = new DataType(Transformer.intToBinary("7"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("-2");
        String remainder = Transformer.intToBinary("1");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
    @Test
    public void DivSpecialTest8() {
        src = new DataType(Transformer.intToBinary("-3"));
        dest = new DataType(Transformer.intToBinary("-7"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("2");
        String remainder = Transformer.intToBinary("-1");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
    @Test
    public void DivSpecialTest9() {
        src = new DataType(Transformer.intToBinary("3"));
        dest = new DataType(Transformer.intToBinary("7"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("2");
        String remainder = Transformer.intToBinary("1");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
    @Test
    public void DivSpecialTest3() {
        src = new DataType(Transformer.intToBinary("-2"));
        dest = new DataType(Transformer.intToBinary("-12"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("6");
        String remainder = Transformer.intToBinary("0");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
    @Test
    public void DivSpecialTest10() {
        src = new DataType(Transformer.intToBinary("2"));
        dest = new DataType(Transformer.intToBinary("12"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("6");
        String remainder = Transformer.intToBinary("0");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
    @Test
    public void DivSpecialTest6() {
        src = new DataType(Transformer.intToBinary("2"));
        dest = new DataType(Transformer.intToBinary("-12"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("-6");
        String remainder = Transformer.intToBinary("0");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
    @Test
    public void DivSpecialTest11() {
        src = new DataType(Transformer.intToBinary("-2"));
        dest = new DataType(Transformer.intToBinary("12"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("-6");
        String remainder = Transformer.intToBinary("0");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
    @Test
    public void DivSpecialTest4() {
        src = new DataType(Transformer.intToBinary("7"));
        dest = new DataType(Transformer.intToBinary("9"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("1");
        String remainder = Transformer.intToBinary("2");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
    @Test
    public void DivSpecialTest5() {
        src = new DataType(Transformer.intToBinary("3"));
        dest = new DataType(Transformer.intToBinary("9"));
        result = alu.div(src, dest);
        String quotient = Transformer.intToBinary("3");
        String remainder = Transformer.intToBinary("0");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }
}
