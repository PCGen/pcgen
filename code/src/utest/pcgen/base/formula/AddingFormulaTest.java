package pcgen.base.formula;

import junit.framework.TestCase;

public class AddingFormulaTest extends TestCase
{

	public void testToString()
	{
		assertEquals("+1", new AddingFormula(1).toString());
		assertEquals("+0", new AddingFormula(0).toString());
		assertEquals("+3", new AddingFormula(3).toString());
		assertEquals("-3", new AddingFormula(-3).toString());
	}
	

	public void testIdentity()
	{
		AddingFormula f = new AddingFormula(1);
		assertEquals(1, f.resolve(Integer.valueOf(0)).intValue());
		assertEquals(3, f.resolve(Double.valueOf(2.5)).intValue());
		testBrokenCalls(f);
	}

	public void testEquality()
	{
		AddingFormula f1 = new AddingFormula(1);
		AddingFormula f2 = new AddingFormula(1);
		AddingFormula f3 = new AddingFormula(2);
		AddingFormula f4 = new AddingFormula(-1);
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
		AddingFormula f = new AddingFormula(3);
		assertEquals(8, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(10, f.resolve(Double.valueOf(7.5)).intValue());
		testBrokenCalls(f);
	}

	public void testZero()
	{
		AddingFormula f = new AddingFormula(0);
		assertEquals(5, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(2.3)).intValue());
		testBrokenCalls(f);
	}

	public void testNegative()
	{
		AddingFormula f = new AddingFormula(-2);
		assertEquals(3, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(-8, f.resolve(Double.valueOf(-6.7)).intValue());
		testBrokenCalls(f);
	}

	private void testBrokenCalls(AddingFormula f)
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
