package pcgen.base.formula;

import junit.framework.TestCase;

public class DividingFormulaTest extends TestCase
{

	public void testToString()
	{
		assertEquals("/1", new DividingFormula(1).toString());
		assertEquals("/3", new DividingFormula(3).toString());
		assertEquals("/-3", new DividingFormula(-3).toString());
	}
	
	public void testIdentity()
	{
		DividingFormula f = new DividingFormula(1);
		assertEquals(2, f.resolve(Integer.valueOf(2)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(2.5)).intValue());
		testBrokenCalls(f);
	}

	public void testEquality()
	{
		DividingFormula f1 = new DividingFormula(1);
		DividingFormula f2 = new DividingFormula(1);
		DividingFormula f3 = new DividingFormula(2);
		DividingFormula f4 = new DividingFormula(-1);
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
		DividingFormula f = new DividingFormula(3);
		assertEquals(1, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(2, f.resolve(Integer.valueOf(6)).intValue());
		assertEquals(2, f.resolve(Integer.valueOf(7)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(6.5)).intValue());
		testBrokenCalls(f);
	}

	public void testZero()
	{
		try
		{
			new DividingFormula(0);
			fail("DividingFormula should not allow build with zero (will always fail)");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	public void testNegative()
	{
		DividingFormula f = new DividingFormula(-2);
		assertEquals(-2, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(3, f.resolve(Double.valueOf(-6.7)).intValue());
		testBrokenCalls(f);
	}

	private void testBrokenCalls(DividingFormula f)
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
