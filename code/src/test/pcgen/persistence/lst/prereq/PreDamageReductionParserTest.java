/*
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
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
import plugin.pretokens.parser.PreDamageReductionParser;


@SuppressWarnings("nls")
public class PreDamageReductionParserTest extends EnUsLocaleDependentTestCase
{
	
	@Test
	public void testMultipleFails()
	{
		PreDamageReductionParser parser = new PreDamageReductionParser();

		try
		{
			parser.parse("DR", "Evil=5,Magic.10", false, false);
			fail("should have thrown a PersistenceLayerException!");
		}
		catch (PersistenceLayerException ple)
		{
			assertEquals(
				"Badly formed passesPreDR/number of DRs attribute: Evil=5", ple
					.getMessage());
		}
	}

	
	/**
	 * Test multiple passes.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testMultiplePasses() throws PersistenceLayerException
	{
		PreDamageReductionParser parser = new PreDamageReductionParser();

		Prerequisite prereq =
				parser.parse("DR", "1,Evil=5,Magic.10", false, false);

		assertEquals(
			"<prereq operator=\"GTEQ\" operand=\"1\" >\n<prereq kind=\"dr\" key=\"Evil\" "
			+ "operator=\"GTEQ\" operand=\"5\" >\n</prereq>\n<prereq kind=\"dr\" key=\"Magic\" "
			+ "operator=\"GTEQ\" operand=\"10\" >\n</prereq>\n</prereq>\n",
			prereq.toString());
	}

	
	/**
	 * Test no value.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testNoValue() throws PersistenceLayerException
	{
		PreDamageReductionParser parser = new PreDamageReductionParser();

		Prerequisite prereq =
				parser.parse("DR", "1,Evil=5,Magic", false, false);

		assertEquals(
			"<prereq operator=\"GTEQ\" operand=\"1\" >\n<prereq kind=\"dr\" key=\"Evil\" "
					+ "operator=\"GTEQ\" operand=\"5\" >\n</prereq>\n<prereq kind=\"dr\" key=\"Magic\" "
					+ "operator=\"GTEQ\" operand=\"0\" >\n</prereq>\n</prereq>\n",
			prereq.toString());
	}
}
