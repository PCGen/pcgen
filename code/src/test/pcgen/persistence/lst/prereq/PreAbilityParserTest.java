/*
 * PreAbilityParserTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 * Created on January 23, 2006
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreAbilityParser;

/**
 * <code>PreAbilityParserTest</code> tests the function of the 
 * PREABILITY parser.
 *
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1777 $
 */
public class PreAbilityParserTest extends TestCase
{
	public static void main(String args[])
	{
		TestRunner.run(PreAbilityParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreAbilityParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testCategoryInterpretation() throws Exception
	{

		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq = parser.parse("ability", "1,CATEGORY.Mutation,Sneak Attack", false, false);
		assertEquals("Category specified for single key",
			"<prereq operator=\"gteq\" operand=\"1\" >\n"
				+ "<prereq kind=\"ability\" count-multiples=\"true\" category=\"Mutation\" key=\"Sneak Attack\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n" + "", prereq.toString());

		prereq = parser.parse("ability", "2,CATEGORY=Mutation,Foo,Bar", false, false);
		assertEquals("Category specified for multiple keys",
			"<prereq operator=\"gteq\" operand=\"2\" >\n"
				+ "<prereq kind=\"ability\" count-multiples=\"true\" category=\"Mutation\" key=\"Foo\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"ability\" count-multiples=\"true\" category=\"Mutation\" key=\"Bar\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n" + "", prereq.toString());

		prereq = parser.parse("ability", "1,CATEGORY.ANY,Sneak Attack", false, false);
		assertEquals("Category of ANY specified for single key",
			"<prereq operator=\"gteq\" operand=\"1\" >\n"
			+ "<prereq kind=\"ability\" count-multiples=\"true\" key=\"Sneak Attack\" operator=\"gteq\" operand=\"1\" >\n"
			+ "</prereq>\n" + "</prereq>\n" + "", prereq.toString());

		prereq = parser.parse("ability", "1,CATEGORY.ANY,Foo,Bar", false, false);
		assertEquals("Category specified for multiple key",
			"<prereq operator=\"gteq\" operand=\"1\" >\n"
			+ "<prereq kind=\"ability\" count-multiples=\"true\" key=\"Foo\" operator=\"gteq\" operand=\"1\" >\n"
			+ "</prereq>\n" 
			+ "<prereq kind=\"ability\" count-multiples=\"true\" key=\"Bar\" operator=\"gteq\" operand=\"1\" >\n"
			+ "</prereq>\n" 
			+ "</prereq>\n" + "", prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testSingleEntry() throws Exception
	{

		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq = parser.parse("ability", "1,Sneak Attack", false, false);
		assertEquals("Category not specified for single key",
			"<prereq kind=\"ability\" key=\"Sneak Attack\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n" + "", prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testNoKey() throws Exception
	{

		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq = parser.parse("ability", "1,CATEGORY.Mutation", false, false);
		assertEquals("Category specified for no key",
				"<prereq kind=\"ability\" category=\"Mutation\" key=\"ANY\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n" + "", prereq.toString());
	}
	
	/**
	 * Test that an error is produced if two categories are specified.
	 * @throws Exception
	 */
	public void testTwoCategories() throws Exception
	{
		try
		{
			PreAbilityParser parser = new PreAbilityParser();
			Prerequisite prereq =
					parser.parse("ability",
						"1,CATEGORY.Mutation,KEY_a,CATEGORY.Foo", false, false);
			fail("Should have thrown a PersistenceLayerException.");
		}
		catch (PersistenceLayerException e)
		{
			// Ignore, this is the expected result.
		}
	}
	
	protected void setUp() throws Exception
	{
		Globals.setUseGUI(false);
		Globals.emptyLists();
		SettingsHandler.setGame("3.5");
	}
}