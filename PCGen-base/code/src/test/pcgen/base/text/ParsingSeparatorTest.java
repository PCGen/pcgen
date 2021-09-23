/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * Test the ParsingSeparator class
 */
public class ParsingSeparatorTest
{
	@Test
	public void testConstructor()
	{
		assertThrows(NullPointerException.class, () -> new ParsingSeparator(null, ','));
	}

	@Test
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
			//expected
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
			//expected
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
	}

	@Test
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
			//expected
		}
		try
		{
			separator.addGroupingPair('[', '(');
			fail("expected illegal state");
		}
		catch (IllegalStateException e)
		{
			//expected
		}
		try
		{
			separator.addGroupingPair('{', ')');
			fail("expected illegal state");
		}
		catch (IllegalStateException e)
		{
			//expected
		}
		try
		{
			separator.addGroupingPair(')', '}');
			fail("expected illegal state");
		}
		catch (IllegalStateException e)
		{
			//expected
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
			//expected
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
			//expected
		}
	}
	
	@Test
	public void testRemove()
	{
		ParsingSeparator separator = new ParsingSeparator("a,b\"c,d\"", ',');
		separator.addGroupingPair('"', '"');
		assertEquals("a", separator.next());
		try
		{
			separator.remove();
			fail("Expect remove to be unsupported");
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
	}
}
