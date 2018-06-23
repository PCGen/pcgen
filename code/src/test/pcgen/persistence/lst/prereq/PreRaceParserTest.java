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
package pcgen.persistence.lst.prereq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreRaceParser;

/**
 * PreRaceParserTest checks that the PreRaceParser class is operating 
 * correctly.
 * 
 * 
 */
public class PreRaceParserTest extends EnUsLocaleDependentTestCase
{

	/**
	 * Test that exclusions are parsed properly.
	 * @throws Exception
	 */
	@Test
	public void testExclusions() throws Exception
	{
		PreRaceParser parser = new PreRaceParser();

		Prerequisite prereq =
				parser.parse("race", "1,Elf%,[Elf (aquatic)]",
					false, false);

		assertEquals("PRERACE with an excluded race",
			"<prereq operator=\"GTEQ\" operand=\"2\" >\n"
			+ "<prereq kind=\"race\" count-multiples=\"true\" key=\"Elf%\" operator=\"GTEQ\" operand=\"1\" >\n"
			+ "</prereq>\n"
			+ "<prereq kind=\"race\" count-multiples=\"true\" key=\"Elf (aquatic)\" operator=\"LT\" operand=\"1\" >\n"
			+ "</prereq>\n</prereq>\n", prereq.toString());
	}
	
	/**
	 * Test that an error is produced if separators are incorrect
	 * @throws Exception
	 */
	@Test
	public void testInvalidSeparators() throws Exception
	{
		try
		{
			PreRaceParser parser = new PreRaceParser();
			parser.parse("race", "1,,KEY_a", false, false);
			fail("Should have thrown a PersistenceLayerException.");
		}
		catch (PersistenceLayerException e)
		{
			// Ignore, this is the expected result.
		}
	}
	
	/**
	 * Test that an error is produced if separators are incorrect
	 * @throws Exception
	 */
	@Test
	public void testInvalidCharacter() throws Exception
	{
		try
		{
			PreRaceParser parser = new PreRaceParser();
			parser.parse("race", "1,KEY_a|Key_b", false, false);
			fail("Should have thrown a PersistenceLayerException.");
		}
		catch (PersistenceLayerException e)
		{
			// Ignore, this is the expected result.
		}
	}

}
