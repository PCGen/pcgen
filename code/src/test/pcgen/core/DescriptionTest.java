/*
 * DescriptionTest.java
 *
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 *
 * Last Editor: $Author: $
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.Constants;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * This class tests the handling of DESC fields in PCGen
 */
@SuppressWarnings("nls")
public class DescriptionTest extends AbstractCharacterTestCase
{
	/**
	 * Constructs a new <code>DescriptionTest</code>.
	 */
	public DescriptionTest()
	{
		super();
	}

	/**
	 * Tests outputting an empty description.
	 *
	 */
	public void testEmptyDesc()
	{
		final Description desc = new Description(Constants.EMPTY_STRING);
		assertTrue(desc.getDescription(this.getCharacter()).equals(""));
	}

	/**
	 * Tests outputting a simple description.
	 *
	 */
	public void testSimpleDesc()
	{
		final String simpleDesc = "This is a test";
		final Description desc = new Description(simpleDesc);
		assertTrue(desc.getDescription(getCharacter()).equals(simpleDesc));
	}

	/**
	 * Test PREREQs for Desc
	 * @throws Exception
	 */
	public void testPreReqs() throws Exception
	{
		final String simpleDesc = "This is a test";
		final Description desc = new Description(simpleDesc);

		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereqNE =
				factory.parse("PRETEMPLATE:KEY_Natural Lycanthrope");
		desc.addPreReq(prereqNE);
		is(desc.getDescription(getCharacter()), strEq(""));

		PCTemplate template = new PCTemplate();
		template.setName("Natural Lycanthrope");
		template.setKeyName("KEY_Natural Lycanthrope");
		getCharacter().addTemplate(template);
		is(desc.getDescription(getCharacter()), strEq(simpleDesc));
	}

	/**
	 * Tests a simple string replacement.
	 */
	public void testSimpleReplacement()
	{
		final Description desc = new Description("%1");
		desc.addVariable("\"Variable\"");
		assertTrue(desc.getDescription(getCharacter()).equals("Variable"));
	}

	/**
	 * Test name replacement
	 */
	public void testSimpleNameReplacement()
	{
		final PObject pobj = new PObject();
		pobj.setName("PObject");

		final Description desc = new Description("%1");
		desc.addVariable("%NAME");
		desc.setOwner(pobj);
		assertTrue(desc.getDescription(getCharacter()).equals("PObject"));
	}

	/**
	 * Tests simple variable replacement
	 */
	public void testSimpleVariableReplacement()
	{
		final Race dummy = new Race();
		dummy.addVariable(-9, "TestVar", "2");

		final Description desc = new Description("%1");
		desc.addVariable("TestVar");
		desc.setOwner(dummy);
		assertTrue(desc.getDescription(getCharacter()).equals("0"));

		getCharacter().setRace(dummy);
		assertTrue(desc.getDescription(getCharacter()).equals("2"));
	}

	/**
	 * Tests simple replacement of %CHOICE
	 */
	public void testSimpleChoiceReplacement()
	{
		final PObject pobj = new PObject();

		final Description desc = new Description("%1");
		desc.addVariable("%CHOICE");
		desc.setOwner(pobj);
		assertTrue(desc.getDescription(getCharacter()).equals(""));

		pobj.addAssociated("Foo");
		assertTrue(desc.getDescription(getCharacter()).equals("Foo"));
	}

	/**
	 * Tests simple %LIST replacement.
	 */
	public void testSimpleListReplacement()
	{
		final PObject pobj = new PObject();

		final Description desc = new Description("%1");
		desc.addVariable("%LIST");
		desc.setOwner(pobj);
		assertTrue(desc.getDescription(getCharacter()).equals(""));

		pobj.addAssociated("Foo");
		assertTrue(desc.getDescription(getCharacter()).equals("Foo"));
	}

	/**
	 * Test a replacement with missing variables.
	 */
	public void testEmptyReplacement()
	{
		final PObject pobj = new PObject();

		final Description desc = new Description("%1");
		desc.setOwner(pobj);
		assertTrue(desc.getDescription(getCharacter()).equals(""));
	}

	/**
	 * Test having extra variables present
	 */
	public void testExtraVariables()
	{
		final PObject pobj = new PObject();

		final Description desc = new Description("Testing");
		desc.addVariable("%LIST");
		desc.setOwner(pobj);
		assertTrue(desc.getDescription(getCharacter()).equals("Testing"));

		pobj.addAssociated("Foo");
		assertTrue(desc.getDescription(getCharacter()).equals("Testing"));
	}

	/**
	 * Test complex replacements.
	 */
	public void testComplexVariableReplacement()
	{
		final Race dummy = new Race();
		dummy.addVariable(-9, "TestVar", "2");
		dummy.addAssociated("Associated 1");
		dummy.addAssociated("Associated 2");

		final Description desc = new Description("%1 test %3 %2");
		desc.addVariable("TestVar");
		desc.setOwner(dummy);
		assertEquals("0 test  ", desc.getDescription(getCharacter()));

		getCharacter().setRace(dummy);
		assertEquals("2 test  ", desc.getDescription(getCharacter()));

		desc.addVariable("%CHOICE");
		assertEquals("2 test  Associated 1", desc
			.getDescription(getCharacter()));

		desc.addVariable("%LIST");
		assertEquals("Replacement of %LIST failed",
			"2 test Associated 1 and Associated 2 Associated 1", desc
				.getDescription(getCharacter()));
	}
}
