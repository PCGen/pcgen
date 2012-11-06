/*
 * Copyright 2012 Vincent Lhote
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package translation.util;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * JUnit Tests for {@link translation.util.Tips}.
 *
 * @author Vincent Lhote
 */
@SuppressWarnings("nls")
public class TipsTest
{

	String comment = "# This is a comment in the tips file";
	String emptyLine = "";
	String tip = "For each method, write a test method";
	String tip2 = "Another tip for you";

	String FILE1 = comment+"\n\n"+tip;
	String FILE2 = tip+"\n"+tip2+"\n";

	/**
	 * Test method for {@link translation.util.Tips#addTip(java.util.Set, java.lang.String)}.
	 */
	@Test
	public void testAddTip()
	{
		Set<String> t = new HashSet<String>();
		assertEquals(0, t.size());
		Tips.addTip(t, tip);
		assertTrue(t.contains(tip));
		assertEquals(1, t.size());
		Tips.addTip(t, tip);
		assertTrue(t.contains(tip));
		assertEquals(1, t.size());
	}

	@Test
	public void isTip() throws Exception
	{
		assertFalse(Tips.isTip(emptyLine));
		assertFalse(Tips.isTip(comment));
		assertTrue(Tips.isTip(tip));
		assertTrue(Tips.isTip(tip2));
	}
	
	@Test
	public void removeEscapeTest()
	{
		assertEquals("", Tips.removeEscaped(""));
		assertEquals("a", Tips.removeEscaped("a"));
		assertEquals("l'eau", Tips.removeEscaped("l\\'eau"));
		assertEquals("\"quoted\"", Tips.removeEscaped("\\\"quoted\\\""));
		assertEquals("\\", Tips.removeEscaped("\\\\"));
	}
	
	@Test
	public void escapeTest()
	{
		assertEquals("", Tips.escape(""));
		assertEquals("a", Tips.escape("a"));
		assertEquals("l\\'eau", Tips.escape("l'eau"));
		assertEquals("\\\"quoted\\\"", Tips.escape("\"quoted\""));
		assertEquals("\\\\", Tips.escape("\\"));
	}
}
