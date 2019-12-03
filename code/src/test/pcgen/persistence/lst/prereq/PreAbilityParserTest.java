/*
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
 */
package pcgen.persistence.lst.prereq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreAbilityParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreAbilityParserTest} tests the function of the
 * PREABILITY parser.
 */
@SuppressWarnings("nls")
public class PreAbilityParserTest extends EnUsLocaleDependentTestCase
{

	@Test
	public void testCategoryInterpretation() throws Exception
	{

		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq = parser.parse("ability", "1,CATEGORY.Mutation,Sneak Attack", false, false);
		assertEquals(
				"<prereq operator=\"GTEQ\" operand=\"1\" >\n"
				+ "<prereq kind=\"ability\" count-multiples=\"true\" category=\"Mutation\" "
					+ "key=\"Sneak Attack\" operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString(), "Category specified for single key");

		prereq = parser.parse("ability", "2,CATEGORY=Mutation,Foo,Bar", false, false);
		assertEquals(
				"<prereq operator=\"GTEQ\" operand=\"2\" >\n"
				+ "<prereq kind=\"ability\" count-multiples=\"true\" category=\"Mutation\" key=\"Foo\" "
					+ "operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"ability\" count-multiples=\"true\" category=\"Mutation\" key=\"Bar\" "
					+ "operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString(), "Category specified for multiple keys");

		prereq = parser.parse("ability", "1,CATEGORY.ANY,Sneak Attack", false, false);
		assertEquals(
				"<prereq operator=\"GTEQ\" operand=\"1\" >\n"
		+ "<prereq kind=\"ability\" count-multiples=\"true\" key=\"Sneak Attack\" operator=\"GTEQ\" operand=\"1\" >\n"
		+ "</prereq>\n" + "</prereq>\n", prereq.toString(), "Category of ANY specified for single key");

		prereq = parser.parse("ability", "1,CATEGORY.ANY,Foo,Bar", false, false);
		assertEquals(
				"<prereq operator=\"GTEQ\" operand=\"1\" >\n"
			+ "<prereq kind=\"ability\" count-multiples=\"true\" key=\"Foo\" operator=\"GTEQ\" operand=\"1\" >\n"
			+ "</prereq>\n" 
			+ "<prereq kind=\"ability\" count-multiples=\"true\" key=\"Bar\" operator=\"GTEQ\" operand=\"1\" >\n"
			+ "</prereq>\n" 
			+ "</prereq>\n", prereq.toString(), "Category specified for multiple key");
	}

	
	@Test
	public void testSingleEntry() throws Exception
	{

		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq = parser.parse("ability", "1,Sneak Attack", false, false);
		assertEquals(
				"<prereq kind=\"ability\" key=\"Sneak Attack\" operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n", prereq.toString(), "Category not specified for single key");
	}

	
	@Test
	public void testNegates() throws Exception
	{
		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq = parser.parse("ability", "1,Sneak Attack,[Alertness]", false, false);
		assertEquals(
				"<prereq operator=\"GTEQ\" operand=\"2\" >\n"
		+ "<prereq kind=\"ability\" count-multiples=\"true\" key=\"Sneak Attack\" operator=\"GTEQ\" operand=\"1\" >\n"
		+ "</prereq>\n"
		+ "<prereq kind=\"ability\" count-multiples=\"true\" key=\"Alertness\" operator=\"LT\" operand=\"1\" >\n"
		+ "</prereq>\n" + "</prereq>\n", prereq.toString(), "Negated entry should be parsed");
	}

	
	@Test
	public void testNoKey() throws Exception
	{

		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq = parser.parse("ability", "1,CATEGORY.Mutation", false, false);
		assertEquals(
				"<prereq kind=\"ability\" category=\"Mutation\" key=\"ANY\" operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n", prereq.toString(), "Category specified for no key");
	}
	
	/**
	 * Test that an error is produced if two categories are specified.
	 */
	@Test
	public void testTwoCategories()
	{
		PreAbilityParser parser = new PreAbilityParser();
		assertThrows(PersistenceLayerException.class,
				() -> parser.parse("ability", "1,CATEGORY.Mutation,KEY_a,CATEGORY.Foo", false,
				false));
	}
	
	/**
	 * Test that an error is produced if separators are incorrect.
	 */
	@Test
	public void testInvalidSeparators()
	{
		PreAbilityParser parser = new PreAbilityParser();
		assertThrows(PersistenceLayerException.class,
				() -> parser.parse("ability", "1,CATEGORY.Mutation,,KEY_a", false, false));
	}
	
	/**
	 * Test that an error is produced if separators are incorrect.
	 */
	@Test
	public void testInvalidCharacter()
	{
		PreAbilityParser parser = new PreAbilityParser();
		assertThrows(PersistenceLayerException.class,
				() -> parser.parse("ability", "1,CATEGORY.Mutation,KEY_a|Key_b", false, false));
	}
	
	@BeforeEach
	public void setUp() {
		Globals.setUseGUI(false);
		Globals.emptyLists();
		SettingsHandler.setGame("3.5");
	}

}
