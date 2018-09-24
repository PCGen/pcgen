/*
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
 */
package pcgen.core.utils;

import junit.framework.TestCase;
import pcgen.base.lang.StringUtil;
import pcgen.system.PCGenPropBundle;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code CoreUtilityTest}.
 *
 * Tests the CoreUtility class.
 *
 * @see pcgen.core.utils.CoreUtility
 */
@SuppressWarnings("nls")
public class CoreUtilityTest extends TestCase
{
	/**
	 * Constructs a new {@code CoreUtilityTest}.
	 *
	 * @see pcgen.PCGenTestCase#PCGenTestCase()
	 */
	public CoreUtilityTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new {@code CoreUtilityTest} with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see pcgen.PCGenTestCase#PCGenTestCase(String)
	 */
	public CoreUtilityTest(final String name)
	{
		super(name);
	}

	public void testisNetURL() throws MalformedURLException
	{
		URL https = new URL("https://127.0.0.1");
		URL http = new URL("http://127.0.0.1");
		URL ftp = new URL("ftp://127.0.0.1");
		assertTrue(CoreUtility.isNetURL(https));
		assertTrue(CoreUtility.isNetURL(http));
		assertTrue(CoreUtility.isNetURL(ftp));
	}

	/**
	 * Test unsplit string (join method).
	 */
	public void testJoin()
	{
		final String sep = "|";
		final List<String> list = constructList();
		final String result = StringUtil.join(list, sep);
		final String trueResult = "one|two|three|four";
		assertTrue("join returned bad String: got '" + result
			+ "' should be '" + trueResult + "'", trueResult.equals(result));
	}

	public void testCompareVersions()
	{
		int[] firstVer = {5, 13, 6};
		int[] secondVer = {5, 13, 6};
		
		assertEquals("Check for equal values", 0, CoreUtility.compareVersions(firstVer, secondVer));
		secondVer[2] = 4;
		assertEquals("Check for first later", 1, CoreUtility.compareVersions(firstVer, secondVer));
		secondVer[2] = 7;
		assertEquals("Check for first earlier", -1, CoreUtility.compareVersions(firstVer, secondVer));
		secondVer[2] = 6;
		secondVer[1] = 12;
		assertEquals("Check for first later", 1, CoreUtility.compareVersions(firstVer, secondVer));
		secondVer[1] = 14;
		assertEquals("Check for first earlier", -1, CoreUtility.compareVersions(firstVer, secondVer));
		secondVer[1] = 13;
		secondVer[0] = 4;
		assertEquals("Check for first later", 1, CoreUtility.compareVersions(firstVer, secondVer));
		secondVer[0] = 6;
		assertEquals("Check for first earlier", -1, CoreUtility.compareVersions(firstVer, secondVer));
	}

	public void testCompareVersionsString()
	{
		String firstVer = "5.13.6";
		
		assertEquals("Check for equal values", 0, CoreUtility.compareVersions(firstVer, firstVer));
		assertEquals("Check for first later", 1, CoreUtility.compareVersions(firstVer, "5.13.4"));
	}
	
	public void testConvertVersionToNumber()
	{
		int[] result = CoreUtility.convertVersionToNumber("5.13.6");
		assertEquals("Number of fields", 3, result.length);
		assertEquals("Major verison ", 5, result[0]);
		assertEquals("Minor verison ", 13, result[1]);
		assertEquals("Release number", 6, result[2]);
		result = CoreUtility.convertVersionToNumber("5.13.6 RC1");
		assertEquals("Number of fields", 3, result.length);
		assertEquals("Major verison ", 5, result[0]);
		assertEquals("Minor verison ", 13, result[1]);
		assertEquals("Release number", 6, result[2]);
	}

	public void testIsCurrMinorVer()
	{
		String currVerStr = PCGenPropBundle.getVersionNumber();
		assertEquals("Check for same verison", true, CoreUtility
			.isCurrMinorVer(currVerStr));
		int[] currVer = CoreUtility.convertVersionToNumber(currVerStr);
		currVer[2] = 99;
		String verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for differing release", true, CoreUtility
			.isCurrMinorVer(verStr));
		int oldMinor = currVer[1];
		currVer[1] = 99;
		verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for differing minor", false, CoreUtility
			.isCurrMinorVer(verStr));
		currVer[1] = oldMinor;
		verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for returned minor", true, CoreUtility
			.isCurrMinorVer(verStr));
		currVer[0] = 2;
		verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for differing major", false, CoreUtility
			.isCurrMinorVer(verStr));
	}

	public void testIsPriorToCurrent()
	{
		String currVerStr = PCGenPropBundle.getVersionNumber();
		assertEquals("Check for same verison", true, CoreUtility
			.isPriorToCurrent(currVerStr));
		int[] currVer = CoreUtility.convertVersionToNumber(currVerStr);
		currVer[2] = 99;
		String verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for differing release", false, CoreUtility
			.isPriorToCurrent(verStr));
		currVer[2] = 0;
		verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for earlier release", true, CoreUtility
			.isPriorToCurrent(verStr));
		currVer[1] = 99;
		verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for differing minor", false, CoreUtility
			.isPriorToCurrent(verStr));
		currVer[1] = 0;
		verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for earlier minor", true, CoreUtility
			.isPriorToCurrent(verStr));
		currVer[0] = 99;
		verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for differing major", false, CoreUtility
			.isPriorToCurrent(verStr));
		currVer[0] = 0;
		verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
		assertEquals("Check for earlier major", true, CoreUtility
			.isPriorToCurrent(verStr));
	}
	
	private List<String> constructList()
	{
		final List<String> list = new ArrayList<>();
		list.add("one");
		list.add("two");
		list.add("three");
		list.add("four");

		return list;
	}
}
