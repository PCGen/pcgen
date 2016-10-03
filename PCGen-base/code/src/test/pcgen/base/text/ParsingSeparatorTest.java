package pcgen.base.text;

import java.util.NoSuchElementException;

import junit.framework.TestCase;

public class ParsingSeparatorTest extends TestCase
{
	public void testConstructor()
	{
		try
		{
			new ParsingSeparator(null, ',');
			fail("Expected ParsingSeparator to reject null base String");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testEmpty()
	{
		ParsingSeparator separator = new ParsingSeparator("", ',');
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
		separator = new ParsingSeparator(",", ',');
		//before the comma
		assertTrue(separator.hasNext());
		assertEquals("", separator.next());
		//after the comma
		assertTrue(separator.hasNext());
		assertEquals("", separator.next());
		//done
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
		//We are NOT String.split like to consume blanks...
		separator = new ParsingSeparator(",,", ',');
		//before the comma
		assertTrue(separator.hasNext());
		assertEquals("", separator.next());
		//middle
		assertTrue(separator.hasNext());
		assertEquals("", separator.next());
		//after the comma
		assertTrue(separator.hasNext());
		assertEquals("", separator.next());
		//done
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
	}

	public void testMismatchedOpen()
	{
		ParsingSeparator separator = new ParsingSeparator("a,b(c,d", ',');
		separator.addGroupingPair('(', ')');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: mismatched");
		}
		catch (ParsingSeparator.GroupingMismatchException e)
		{
			//ok
		}
	}

	public void testMismatchedClosed()
	{
		ParsingSeparator separator = new ParsingSeparator("a,b)c,d", ',');
		separator.addGroupingPair('(', ')');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: mismatched");
		}
		catch (ParsingSeparator.GroupingMismatchException e)
		{
			//ok
		}
	}

	public void testMismatchedOffset()
	{
		ParsingSeparator separator = new ParsingSeparator("a,b(c,[d)]", ',');
		separator.addGroupingPair('(', ')');
		separator.addGroupingPair('[', ']');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: mismatched");
		}
		catch (ParsingSeparator.GroupingMismatchException e)
		{
			//ok
		}
	}

	public void testSimple()
	{
		ParsingSeparator separator = new ParsingSeparator("a,b(c,d)", ',');
		separator.addGroupingPair('(', ')');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("b(c,d)", separator.next());
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
	}

	public void testQuotes()
	{
		ParsingSeparator separator = new ParsingSeparator("a,b\"c,d\"", ',');
		separator.addGroupingPair('"', '"');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("b\"c,d\"", separator.next());
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
	}

	public void testQuotesComplex()
	{
		ParsingSeparator separator =
				new ParsingSeparator("a,b\"c,d\",e,f\"g\"", ',');
		separator.addGroupingPair('"', '"');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("b\"c,d\"", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("e", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("f\"g\"", separator.next());
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
	}

	public void testQuotesEmbedded()
	{
		ParsingSeparator separator =
				new ParsingSeparator("a,b\"c,(d\",e,f\")g\",h", ',');
		separator.addGroupingPair('(', ')');
		separator.addGroupingPair('"', '"');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("b\"c,(d\",e,f\")g\"", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("h", separator.next());
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
	}

	public void testParenEmbedded()
	{
		ParsingSeparator separator =
				new ParsingSeparator("a,b(c,[d,(e),f]g)h,ijk", ',');
		separator.addGroupingPair('(', ')');
		separator.addGroupingPair('[', ']');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("b(c,[d,(e),f]g)h", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("ijk", separator.next());
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
	}

	public void testParenStart()
	{
		ParsingSeparator separator =
				new ParsingSeparator("(a),b(c,[d,(e),f]g)h,ijk", ',');
		separator.addGroupingPair('(', ')');
		separator.addGroupingPair('[', ']');
		assertTrue(separator.hasNext());
		assertEquals("(a)", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("b(c,[d,(e),f]g)h", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("ijk", separator.next());
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
	}

	public void testParenEnd()
	{
		ParsingSeparator separator =
				new ParsingSeparator("a,b(c,[d,(e),f]g)(h,ijk)", ',');
		separator.addGroupingPair('(', ')');
		separator.addGroupingPair('[', ']');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("b(c,[d,(e),f]g)(h,ijk)", separator.next());
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
	}

	public void testBlankEnd()
	{
		ParsingSeparator separator =
				new ParsingSeparator("a,b(c,[d,(e),f]g)(h,ijk),", ',');
		separator.addGroupingPair('(', ')');
		separator.addGroupingPair('[', ']');
		assertTrue(separator.hasNext());
		assertEquals("a", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("b(c,[d,(e),f]g)(h,ijk)", separator.next());
		assertTrue(separator.hasNext());
		assertEquals("", separator.next());
		assertFalse(separator.hasNext());
		try
		{
			separator.next();
			fail("Expected ParsingSeparator to fail: should be done");
		}
		catch (NoSuchElementException e)
		{
			//ok
		}
	}

	public void testAddGroupingPair()
	{
		ParsingSeparator separator =
				new ParsingSeparator("a,b\"c,d\",e,f\"g\"", ',');
		separator.addGroupingPair('(', ')');
		try
		{
			separator.addGroupingPair('(', ']');
			fail("expected illegal state");
		}
		catch (IllegalStateException e)
		{
			//ok
		}
		try
		{
			separator.addGroupingPair('[', '(');
			fail("expected illegal state");
		}
		catch (IllegalStateException e)
		{
			//ok
		}
		try
		{
			separator.addGroupingPair('{', ')');
			fail("expected illegal state");
		}
		catch (IllegalStateException e)
		{
			//ok
		}
		try
		{
			separator.addGroupingPair(')', '}');
			fail("expected illegal state");
		}
		catch (IllegalStateException e)
		{
			//ok
		}
		//but reuse of same is okay
		separator.addGroupingPair('(', ')');
		separator = new ParsingSeparator("a,b\"c,d\",e,f\"g\"", ',');
		separator.hasNext();
		//No longer valid since hasNext was called
		try
		{
			separator.addGroupingPair('{', '}');
			fail("expected illegal state");
		}
		catch (IllegalStateException e)
		{
			//ok
		}
		separator = new ParsingSeparator("a,b\"c,d\",e,f\"g\"", ',');
		separator.next();
		//No longer valid since next was called
		try
		{
			separator.addGroupingPair('{', '}');
			fail("expected illegal state");
		}
		catch (IllegalStateException e)
		{
			//ok
		}
	}
}
