package pcgen.core.utils;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import pcgen.core.utils.LastGroupSeparator.GroupingMismatchException;

public class LastGroupSeparatorTest extends TestCase
{

	@Test
	public void testNullConstructor()
	{
		try
		{
			new LastGroupSeparator(null);
			Assert.fail();
		}
		catch (IllegalArgumentException iae)
		{
			// OK
		}
	}
	
	@Test
	public void testCantDoThatYet()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test");
		try
		{
			cs.getRoot();
			Assert.fail();
		}
		catch (IllegalStateException e)
		{
			//OK
		}
	}

	@Test
	public void testSimple()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test");
		Assert.assertNull(cs.process());
		Assert.assertEquals("Test", cs.getRoot());
	}

	@Test
	public void testParenMismatch()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test(Open");
		try
		{
			cs.process();
			Assert.fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	public void testSecondParenMismatch()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Foo(Test(Open)");
		try
		{
			cs.process();
			Assert.fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	public void testSecondMismatchParenClose()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test)Open");
		try
		{
			cs.process();
			Assert.fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	public void testParenCloseBeforeOpen()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test)Open(");
		try
		{
			cs.process();
			Assert.fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	public void testNormalParen()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(Bar),Test(Goo,Free)");
		Assert.assertEquals("Goo,Free", cs.process());
		Assert.assertEquals("Foo(Bar),Test", cs.getRoot());
	}

	@Test
	public void testComplexMismatchParenOne()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(BarWhee)),Test(Goo,Free)");
		try
		{
			cs.process();
			Assert.fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	public void testComplexMismatchParenTwo()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(Bar(Whee),Test(Goo,Free)");
		try
		{
			cs.process();
			Assert.fail();
		}
		catch (GroupingMismatchException iae)
		{
			// OK
		}
		//Root undefined, don't test
	}

	@Test
	public void testComplexOne()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(Bar(Wheel),Har),Test(Goo,Free)");
		Assert.assertEquals("Goo,Free", cs.process());
		Assert.assertEquals("Foo(Bar(Wheel),Har),Test", cs.getRoot());
	}

	@Test
	public void testEmptyParenSimple()
	{
		LastGroupSeparator cs = new LastGroupSeparator("Test()");
		Assert.assertEquals("", cs.process());
		Assert.assertEquals("Test", cs.getRoot());
	}

	@Test
	public void testEmptyParenComplex()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Foo(Bar(Wheel),Har),Test()");
		Assert.assertEquals("", cs.process());
		Assert.assertEquals("Foo(Bar(Wheel),Har),Test", cs.getRoot());
	}

	@Test
	public void testComplexTwo()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Test(Goo,Free) (Bar(Wheel,Deal))");
		Assert.assertEquals("Bar(Wheel,Deal)", cs.process());
		Assert.assertEquals("Test(Goo,Free) ", cs.getRoot());
	}

	@Test
	public void testNotEndParen()
	{
		LastGroupSeparator cs = new LastGroupSeparator(
				"Test(Goo,Free) (Bar(Wheel,Deal)) Greatness");
		Assert.assertNull(cs.process());
		Assert.assertEquals("Test(Goo,Free) (Bar(Wheel,Deal)) Greatness", cs.getRoot());
	}
}
