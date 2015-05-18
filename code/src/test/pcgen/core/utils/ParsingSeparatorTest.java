package pcgen.core.utils;

import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.core.utils.ParsingSeparator.GroupingMismatchException;

public class ParsingSeparatorTest extends TestCase
{

	@Test
	public void testNullConstructor()
	{
		try
		{
			new ParsingSeparator(null, '|');
			fail();
		}
		catch (IllegalArgumentException iae)
		{
			// OK
		}
	}

	@Test
	public void testSimple()
	{
		ParsingSeparator cs = new ParsingSeparator("Test", '|');
		assertTrue(cs.hasNext());
		assertEquals("Test", cs.next());
		assertFalse(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (NoSuchElementException iae)
		{
			// OK
		}
	}

	@Test
	public void testBracketMismatch()
	{
		ParsingSeparator cs = new ParsingSeparator("Test[Open", '|');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testParenMismatch()
	{
		ParsingSeparator cs = new ParsingSeparator("Test(Open", '|');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testSecondBracketMismatch()
	{
		ParsingSeparator cs = new ParsingSeparator("Foo|Test[Open", '|');
		assertTrue(cs.hasNext());
		assertEquals("Foo", cs.next());
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testSecondParenMismatch()
	{
		ParsingSeparator cs = new ParsingSeparator("Foo|Test(Open", '|');
		assertTrue(cs.hasNext());
		assertEquals("Foo", cs.next());
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testSecondMismatchParenClose()
	{
		ParsingSeparator cs = new ParsingSeparator("Foo|Test)Open", '|');
		assertTrue(cs.hasNext());
		assertEquals("Foo", cs.next());
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testSecondMismatchBracketClose()
	{
		ParsingSeparator cs = new ParsingSeparator("Foo|Test]Open", '|');
		assertTrue(cs.hasNext());
		assertEquals("Foo", cs.next());
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testParenCloseBeforeOpen()
	{
		ParsingSeparator cs = new ParsingSeparator("Test)Open(", '|');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testBracketCloseBeforeOpen()
	{
		ParsingSeparator cs = new ParsingSeparator("Test]Open[", '|');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testNormalParen()
	{
		ParsingSeparator cs = new ParsingSeparator("Foo(Bar),Test(Goo,Free)",
				',');
		assertTrue(cs.hasNext());
		assertEquals("Foo(Bar)", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Test(Goo,Free)", cs.next());
		assertFalse(cs.hasNext());
	}

	@Test
	public void testNormalBracket()
	{
		ParsingSeparator cs = new ParsingSeparator("Foo[Bar],Test[Goo,Free]",
				',');
		assertTrue(cs.hasNext());
		assertEquals("Foo[Bar]", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Test[Goo,Free]", cs.next());
		assertFalse(cs.hasNext());
	}

	@Test
	public void testEndsWith()
	{
		ParsingSeparator cs = new ParsingSeparator("Foo[Bar],Test[Goo,Free],",
				',');
		assertTrue(cs.hasNext());
		assertEquals("Foo[Bar]", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Test[Goo,Free]", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("", cs.next());
		assertFalse(cs.hasNext());
	}

	@Test
	public void testStartsWith()
	{
		ParsingSeparator cs = new ParsingSeparator(",Foo[Bar],Test[Goo,Free]",
				',');
		assertTrue(cs.hasNext());
		assertEquals("", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Foo[Bar]", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Test[Goo,Free]", cs.next());
		assertFalse(cs.hasNext());
	}

	@Test
	public void testDoubleComma()
	{
		ParsingSeparator cs = new ParsingSeparator("Foo[Bar],,Test[Goo,Free]",
				',');
		assertTrue(cs.hasNext());
		assertEquals("Foo[Bar]", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Test[Goo,Free]", cs.next());
		assertFalse(cs.hasNext());
	}

	@Test
	public void testComplexMismatchParenOne()
	{
		ParsingSeparator cs = new ParsingSeparator(
				"Foo(BarWhee)),Test(Goo,Free)", ',');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testComplexMismatchBracketOne()
	{
		ParsingSeparator cs = new ParsingSeparator(
				"Foo[BarWhee]],Test[Goo,Free]", ',');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testComplexMismatchParenTwo()
	{
		ParsingSeparator cs = new ParsingSeparator(
				"Foo(Bar(Whee),Test(Goo,Free)", ',');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testComplexMismatchBracketTwo()
	{
		ParsingSeparator cs = new ParsingSeparator(
				"Foo[Bar[Whee],Test[Goo,Free]", ',');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testComplexEmbeddedCloseParen()
	{
		ParsingSeparator cs = new ParsingSeparator(
				"Foo[BarWheel,Deal)],Test[Goo,Free]", ',');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testComplexEmbeddedCloseBracket()
	{
		ParsingSeparator cs = new ParsingSeparator(
				"Foo(BarWheel,Deal]),Test[Goo,Free]", ',');
		assertTrue(cs.hasNext());
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testComplexMismatchOrder()
	{
		ParsingSeparator cs = new ParsingSeparator(
				"Foo[Bar(Wheel,Deal]),Test[Goo,Free]", ',');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testComplexOne()
	{
		ParsingSeparator cs = new ParsingSeparator(
				"Foo[Bar(Wheel),Har],Test[Goo,Free]", ',');
		assertTrue(cs.hasNext());
		assertEquals("Foo[Bar(Wheel),Har]", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Test[Goo,Free]", cs.next());
		assertFalse(cs.hasNext());
	}

	@Test
	public void testComplexTwo()
	{
		ParsingSeparator cs = new ParsingSeparator(
				"Foo[Bar(Wheel,Deal)],Test[Goo,Free]", ',');
		assertTrue(cs.hasNext());
		assertEquals("Foo[Bar(Wheel,Deal)]", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Test[Goo,Free]", cs.next());
		assertFalse(cs.hasNext());
	}
}
