/*
 *
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 * 
 * 
 */
package pcgen.persistence.lst.prereq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreClassParser;


@SuppressWarnings("nls")
public class PreClassTest extends EnUsLocaleDependentTestCase
{

	@Test
	public void testNoClassLevels() throws Exception
	{
		PreClassParser parser = new PreClassParser();
		Prerequisite prereq = parser.parse("class", "1,Monk=1", true, false);

		assertEquals(
			"<prereq kind=\"class\" key=\"Monk\" operator=\"LT\" operand=\"1\" >\n"
				+ "</prereq>\n", prereq.toString());

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
			PreClassParser parser = new PreClassParser();
			parser.parse("class", "1,,Monk=1", false, false);
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
			PreClassParser parser = new PreClassParser();
			parser.parse("class", "1,Monk=1|Cleric=1", false, false);
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
	public void testInvalidNegate() throws Exception
	{
		try
		{
			PreClassParser parser = new PreClassParser();
			parser.parse("class", "1,Monk=1[Cleric=1]", false, false);
			fail("Should have thrown a PersistenceLayerException.");
		}
		catch (PersistenceLayerException e)
		{
			// Ignore, this is the expected result.
		}
	}
	
}
