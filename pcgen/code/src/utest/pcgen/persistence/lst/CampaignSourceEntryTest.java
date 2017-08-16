/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.persistence.lst;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * CampaignSourceEntryTest checks processing functions of the 
 * CampaignSourceEntry class. 
 * 
 * 
 */
public class CampaignSourceEntryTest
{

	private URI sourceUri;
	
	@Before
	public void setUp() throws Exception
	{
		sourceUri = new URI("file://CampaignSourceEntryTest");
	}
	
	/**
	 * Test method for {@link pcgen.persistence.lst.CampaignSourceEntry#parseSuffix(java.lang.String)}.
	 */
	@Test
	public void testParseSuffix()
	{
		String value = "Full tag contents goes here";

		List<String> result =
				CampaignSourceEntry.parseSuffix("", sourceUri, value);
		assertTrue("Empty string should give empty list", result.isEmpty());

		String firstPrereq = "PREx:1,foo";
		result = CampaignSourceEntry.parseSuffix(firstPrereq, sourceUri, value);
		assertEquals("Single value should match", firstPrereq, result.get(0));
		assertEquals("Incorrect number of tags", 1, result.size());

		String secondPrereq = "PREy:1,bar";
		result =
				CampaignSourceEntry.parseSuffix(firstPrereq + "|"
					+ secondPrereq, sourceUri, value);
		assertEquals("First value should match", firstPrereq, result.get(0));
		assertEquals("Second value should match", secondPrereq, result.get(1));
		assertEquals("Incorrect number of tags", 2, result.size());

		String includeNoBar =
				"(INCLUDE:CATEGORY=Class Ability,Monkey See,Monkey Do)";
		result =
				CampaignSourceEntry.parseSuffix(includeNoBar, sourceUri, value);
		assertEquals("Single value should match", includeNoBar, result.get(0));
		assertEquals("Incorrect number of tags", 1, result.size());

		result =
				CampaignSourceEntry.parseSuffix(includeNoBar + "|"
					+ secondPrereq, sourceUri, value);
		assertEquals("First value should match", includeNoBar, result.get(0));
		assertEquals("Second value should match", secondPrereq, result.get(1));
		assertEquals("Incorrect number of tags", 2, result.size());

		String includeWithBar = "(INCLUDE:Happy Elf|Dower Dwarf)";
		result =
				CampaignSourceEntry.parseSuffix(includeWithBar + "|"
					+ secondPrereq, sourceUri, value);
		assertEquals("First value should match", includeWithBar, result.get(0));
		assertEquals("Second value should match", secondPrereq, result.get(1));
		assertEquals("Incorrect number of tags", 2, result.size());

		result =
				CampaignSourceEntry.parseSuffix(firstPrereq + "|"
					+ includeWithBar, sourceUri, value);
		assertEquals("First value should match", firstPrereq, result.get(0));
		assertEquals("Second value should match", includeWithBar, result.get(1));
		assertEquals("Incorrect number of tags", 2, result.size());

		result =
				CampaignSourceEntry.parseSuffix(firstPrereq + "|"
					+ includeWithBar + "|" + secondPrereq, sourceUri, value);
		assertEquals("First value should match", firstPrereq, result.get(0));
		assertEquals("Second value should match", includeWithBar, result.get(1));
		assertEquals("Third value should match", secondPrereq, result.get(2));
		assertEquals("Incorrect number of tags", 3, result.size());
	}

	/**
	 * Test method for {@link pcgen.persistence.lst.CampaignSourceEntry#parseSuffix(java.lang.String)}.
	 */
	@Test
	public void testParseSuffixInvalid()
	{
		String value = "Full tag contents goes here";

		List<String> result =
				CampaignSourceEntry.parseSuffix("", sourceUri, value);
		assertTrue("Empty string should give empty list", result.isEmpty());

		String includeNoBar =
				"(INCLUDE:CATEGORY=Class Ability,Monkey See,Monkey Do";
		result =
				CampaignSourceEntry.parseSuffix(includeNoBar, sourceUri, value);
		assertNull("Bad data should give null result", result);

		String firstPrereq = "PREx:1,foo";
		result =
				CampaignSourceEntry.parseSuffix(includeNoBar + "|"
					+ firstPrereq, sourceUri, value);
		assertNull("Bad data should give null result", result);

		result =
				CampaignSourceEntry.parseSuffix(firstPrereq + "|"
					+ includeNoBar, sourceUri, value);
		assertNull("Bad data should give null result", result);
	}
	
	/**
	 * Test method for {@link pcgen.persistence.lst.CampaignSourceEntry#parseSuffix(java.lang.String)}.
	 */
	@Test
	public void testParseSuffixInlineBracket()
	{
		String value = "Full tag contents goes here";

		String includeWithBracket = "(INCLUDE:Bluff(Lie))";
		List<String> result =
				CampaignSourceEntry.parseSuffix(includeWithBracket, sourceUri,
					value);
		assertEquals("First value should match", includeWithBracket,
			result.get(0));
		assertEquals("Incorrect number of tags", 1, result.size());

		String includeWithBar = "(INCLUDE:Happy Elf|Dower Dwarf)";
		result =
				CampaignSourceEntry.parseSuffix(includeWithBracket + "|"
					+ includeWithBar, sourceUri, value);
		assertEquals("First value should match", includeWithBracket,
			result.get(0));
		assertEquals("Second value should match", includeWithBar, result.get(1));
		assertEquals("Incorrect number of tags", 2, result.size());

		String includeWithBarBracket = "(INCLUDE:Bluff(Lie)|Perception)";
		result =
				CampaignSourceEntry.parseSuffix(includeWithBarBracket + "|"
					+ includeWithBar, sourceUri, value);
		assertEquals("First value should match", includeWithBarBracket,
			result.get(0));
		assertEquals("Second value should match", includeWithBar, result.get(1));
		assertEquals("Incorrect number of tags", 2, result.size());
	}
}
