package pcgen.base.formula;

import junit.framework.TestCase;


public class SubtractingFormulaTest extends TestCase
{

	public void testToString()
	{
		assertEquals("-1", new SubtractingFormula(1).toString());
		assertEquals("-3", new SubtractingFormula(3).toString());
		assertEquals("+3", new SubtractingFormula(-3).toString());
		assertEquals("-0", new SubtractingFormula(0).toString());
	}
	
	public void testIdentity()
	{
		SubtractingFormula f = new SubtractingFormula(1);
		assertEquals(-1, f.resolve(Integer.valueOf(0)).intValue());
		assertEquals(1, f.resolve(Integer.valueOf(2)).intValue());
		assertEquals(1, f.resolve(Double.valueOf(2.5)).intValue());
		testBrokenCalls(f);
	}

	public void testEquality()
	{
		SubtractingFormula f1 = new SubtractingFormula(1);
		SubtractingFormula f2 = new SubtractingFormula(1);
		SubtractingFormula f3 = new SubtractingFormula(2);
		SubtractingFormula f4 = new SubtractingFormula(-1);
		assertTrue(f1 != f2);
		assertEquals(f1.hashCode(), f2.hashCode());
		assertEquals(f1, f2);
		assertFalse(f1.equals(null));
		assertFalse(f1.hashCode() == f3.hashCode());
		assertFalse(f1.equals(f3));
		assertFalse(f1.hashCode() == f4.hashCode());
		assertFalse(f1.equals(f4));
	}

	public void testPositive()
	{
		SubtractingFormula f = new SubtractingFormula(3);
		assertEquals(2, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(5.5)).intValue());
		testBrokenCalls(f);
	}

	public void testZero()
	{
		SubtractingFormula f = new SubtractingFormula(0);
		assertEquals(5, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(2.3)).intValue());
		testBrokenCalls(f);
	}

	public void testNegative()
	{
		SubtractingFormula f = new SubtractingFormula(-2);
		assertEquals(7, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(-4, f.resolve(Double.valueOf(-6.7)).intValue());
		testBrokenCalls(f);
	}

	private void testBrokenCalls(SubtractingFormula f)
	{
		try
		{
			f.resolve((Number[]) null);
			fail("null should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			f.resolve(new Number[]{});
			fail("empty array should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			f.resolve(new Number[]{Integer.valueOf(4), Double.valueOf(2.5)});
			fail("two arguments in array should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			f.resolve(Integer.valueOf(4), Double.valueOf(2.5));
			fail("two arguments should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

}
