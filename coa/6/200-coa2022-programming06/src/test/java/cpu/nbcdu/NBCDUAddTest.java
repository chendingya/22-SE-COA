package cpu.nbcdu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class NBCDUAddTest {

	private final NBCDU nbcdu = new NBCDU();
	private DataType src;
	private DataType dest;
	private DataType result;

	@Test
	public void AddTest1() {
		src = new DataType("11000000000000000000000010011000");
		dest = new DataType("11000000000000000000000001111001");
		result = nbcdu.add(src, dest);
		assertEquals("11000000000000000000000101110111", result.toString());
	}

	@Test
	public void allTest1() {
		for (int i = - 100; i <= 100; i++)
			for (int j = - 1000; j <= 1000; j++) {
				src = new DataType(Transformer.decimalToNBCD(String.valueOf(i)));
				dest = new DataType(Transformer.decimalToNBCD(String.valueOf(j)));
				result = nbcdu.sub(src,dest);
				if (! Objects.equals(Transformer.decimalToNBCD(String.valueOf(j - i)), nbcdu.sub(src, dest).toString()))
					System.out.println(i + " - " + j);
				assertEquals(Transformer.decimalToNBCD(String.valueOf(j-i)), nbcdu.sub(src, dest).toString());
			}
	}


	@Test
	public void allTest2() {
		for (int i = - 1000; i <= 100; i++)
			for (int j = - 100; j <= 1000; j++) {
				src = new DataType(Transformer.decimalToNBCD(String.valueOf(i)));
				dest = new DataType(Transformer.decimalToNBCD(String.valueOf(j)));
				//
				System.out.println("src " + src.toString());
				System.out.println("dest " + dest.toString());
//
				if (! Objects.equals(Transformer.decimalToNBCD(String.valueOf(i + j)), nbcdu.add(src, dest).toString()))
					System.out.println(i + " + " + j);
				assertEquals(Transformer.decimalToNBCD(String.valueOf(i + j)), nbcdu.add(src, dest).toString());
			}
	}
	@Test

	public void allTestra() {
		int i = 0;
		int j = -0;

		src = new DataType(Transformer.decimalToNBCD(String.valueOf(i)));
		dest = new DataType(Transformer.decimalToNBCD(String.valueOf(j)));

		result = nbcdu.add(src,dest);
		if (! Objects.equals(Transformer.decimalToNBCD(String.valueOf(i+ j)), nbcdu.add(src, dest).toString()))
			System.out.println(i + " + " + j);
		assertEquals(Transformer.decimalToNBCD(String.valueOf(i+j)), nbcdu.add(src, dest).toString());

		result = nbcdu.sub(src,dest);
		if (! Objects.equals(Transformer.decimalToNBCD(String.valueOf(j - i)), nbcdu.sub(src, dest).toString()))
			System.out.println(j + " - " + i);
		assertEquals(Transformer.decimalToNBCD(String.valueOf(j-i)), nbcdu.sub(src, dest).toString());

	}

}
