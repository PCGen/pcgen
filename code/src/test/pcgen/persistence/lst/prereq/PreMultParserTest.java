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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import plugin.pretokens.parser.PreMultParser;

/*** Test
	 * [PREARMORPROF:1,TYPE.Medium],[PREFEAT:1,Armor Proficiency (Medium)]
 */
@SuppressWarnings("nls")
class PreMultParserTest extends EnUsLocaleDependentTestCase
{

	@BeforeEach
	void setUp()
	{
		TestHelper.loadPlugins();
	}

	@Test
	public void testFeat1() throws Exception
	{
		PreMultParser parser = new PreMultParser();

		Prerequisite prereq =
				parser
					.parse(
						"mult",
						"1,[PREPROFWITHARMOR:1,TYPE.Medium],[PREFEAT:1,Armor Proficiency (Medium)]",
						false, false);

		assertEquals(
			"<prereq operator=\"GTEQ\" operand=\"1\" >\n"
				+ "<prereq kind=\"profwitharmor\" key=\"TYPE.Medium\" operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"ability\" category=\"FEAT\" key=\"Armor Proficiency\" sub-key=\"Medium\" "
				+ "operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

	@Test
	public void testUnbalancedBracket()
	{
		PreMultParser parser = new PreMultParser();

		try
		{
			parser.parse("mult",
				"1,[PREPROFWITHARMOR:1,TYPE.Medium],[PREFEAT:1,Armor Proficiency (Medium)",
				false, false);
			fail("Expected unbalanced bracket to be detected.");
		}
		catch (PersistenceLayerException e)
		{
			assertEquals(
				"Unbalanced [] in PREMULT '[PREPROFWITHARMOR:1,TYPE.Medium],[PREFEAT:1,Armor Proficiency (Medium)'.",
				e.getMessage());
		}
	}
}
