package pcgen.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.core.utils.LastGroupSeparator.GroupingMismatchException;

import org.junit.jupiter.api.Test;

class LastGroupSeparatorTest
{

	@Test
	void testNullConstructor()
	{
		try
		{
			new LastGroupSeparator(null);
			fail();
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			// OK
		}
	}
	
	@Test
	void testCantDoThatYet()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test");
		try
		{
			cs.getRoot();
			fail();
		}
		catch (IllegalStateException e)
		{
			//OK
		}
	}

	@Test
	void testSimple()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test");
		assertNull(cs.process());
		assertEquals("Test", cs.getRoot());
	}

	@Test
	void testParenMismatch()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test(Open");
		try
		{
			cs.process();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	void testSecondParenMismatch()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Foo(Test(Open)");
		try
		{
			cs.process();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	void testSecondMismatchParenClose()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test)Open");
		try
		{
			cs.process();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	void testParenCloseBeforeOpen()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test)Open(");
		try
		{
			cs.process();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	void testNormalParen()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(Bar),Test(Goo,Free)");
		assertEquals("Goo,Free", cs.process());
		assertEquals("Foo(Bar),Test", cs.getRoot());
	}

	@Test
	void testComplexMismatchParenOne()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(BarWhee)),Test(Goo,Free)");
		try
		{
			cs.process();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	void testComplexMismatchParenTwo()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(Bar(Whee),Test(Goo,Free)");
		try
		{
			cs.process();
			fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	void testComplexOne()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(Bar(Wheel),Har),Test(Goo,Free)");
		assertEquals("Goo,Free", cs.process());
		assertEquals("Foo(Bar(Wheel),Har),Test", cs.getRoot());
	}

	@Test
	void testEmptyParenSimple()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test()");
		assertEquals("", cs.process());
		assertEquals("Test", cs.getRoot());
	}

	@Test
	void testEmptyParenComplex()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(Bar(Wheel),Har),Test()");
		assertEquals("", cs.process());
		assertEquals("Foo(Bar(Wheel),Har),Test", cs.getRoot());
	}

	@Test
	void testComplexTwo()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Test(Goo,Free) (Bar(Wheel,Deal))");
		assertEquals("Bar(Wheel,Deal)", cs.process());
		assertEquals("Test(Goo,Free) ", cs.getRoot());
	}

	@Test
	void testNotEndParen()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Test(Goo,Free) (Bar(Wheel,Deal)) Greatness");
		assertNull(cs.process());
		assertEquals("Test(Goo,Free) (Bar(Wheel,Deal)) Greatness", cs.getRoot());
	}
}
