/*
 * UtilityTest.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 * @author Pat Ludwig <havoc@boldo.com>
 * Created on May 20th, 2002
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;

import pcgen.PCGenTestCase;
import pcgen.core.utils.CoreUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>UtilityTest</code>.
 *
 * Tests the CoreUtility class.
 *
 * @author Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision$
 * @see pcgen.core.utils.CoreUtility
 */
public class UtilityTest extends PCGenTestCase
{
	/**
	 * Constructs a new <code>UtilityTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public UtilityTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>UtilityTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public UtilityTest(final String name)
	{
		super(name);
	}

	/**
	 * Test commaDelimit method.
	 */
	public void testCommaDelimit()
	{
		final List<String> list = constructList();
		final String result = CoreUtility.commaDelimit(list);
		final String trueResult = "one, two, three, four";
		assertTrue("commaDelimit returned bad String: got '" + result + "' should be '" + trueResult + "'",
			trueResult.equals(result));
	}

	/**
	 * Test replaceFirst method.
	 *
	 * @throws Exception
	 */
	public void testReplaceFirst() throws Exception
	{
		assertEquals(CoreUtility.replaceFirst("abcdefghijklmnopqrstuvwz", "def", "01234"), "abc01234ghijklmnopqrstuvwz");
		assertEquals(CoreUtility.replaceFirst("abcdefghijklmnopqrstuvwz", "def", "01"), "abc01ghijklmnopqrstuvwz");
	}

	/**
	 * Test unsplit char (join method).
	 */
	public void testUnSplitChar()
	{
		final char sep = ',';
		final List<String> list = constructList();
		final String result = CoreUtility.join(list, sep);
		final String trueResult = "one, two, three, four";
		assertTrue("unSplit returned bad String: got '" + result + "' should be '" + trueResult + "'",
			trueResult.equals(result));
	}

	/**
	 * Test unsplit string (join method).
	 */
	public void testUnSplitString()
	{
		final String sep = "|";
		final List<String> list = constructList();
		final String result = CoreUtility.join(list, sep);
		final String trueResult = "one|two|three|four";
		assertTrue("unSplit returned bad String: got '" + result + "' should be '" + trueResult + "'",
			trueResult.equals(result));
	}

	private List<String> constructList()
	{
		final List<String> list = new ArrayList<String>();
		list.add("one");
		list.add("two");
		list.add("three");
		list.add("four");

		return list;
	}
}
