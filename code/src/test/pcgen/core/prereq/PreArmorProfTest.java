/*
 * PreArmorProfTest.java
 * Copyright 2008 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 22/03/2008
 *
 * $Id$
 */
package pcgen.core.prereq;


import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.ArmorProf;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;

/**
 * <code>PreArmorProfTest</code> tests that the PREARMORPROF tag is
 * working correctly.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class PreArmorProfTest extends AbstractCharacterTestCase
{
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreArmorProfTest.class);
	}

	/**
	 * Suite.
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreArmorProfTest.class);
	}
	
	/**
	 * Test with a simple armor proficiency.
	 * 
	 * @throws Exception the exception
	 */
	public void testOneOption() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREARMORPROF:1,Chainmail");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addArmorProf("Chainmail");
		character.addArmorProf("Full Plate");

		assertTrue("Character has the Chainmail proficiency.", 
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREARMORPROF:1,Leather");
		
		assertFalse("Character does not have the Leather proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREARMORPROF:1,Full Plate");
		
		assertTrue("Character has the Full Plate proficiency.", 
				PrereqHandler.passes(prereq, character, null));
	}


	/**
	 * Tests to see if a character has a certain number of weaponprofs from a list.
	 * 
	 * @throws Exception the exception
	 */
	public void testMultiple() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREARMORPROF:1,Chainmail,Full Plate");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addArmorProf("Chainmail");
		character.addArmorProf("Full Plate");

		assertTrue("Character has one of Chainmail or Full Plate proficiency", 
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREARMORPROF:2,Chainmail,Full Plate");

		assertTrue("Character has both Chainmail and Full Plate proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREARMORPROF:3,Chainmail,Full Plate,Leather");

		assertFalse("Character has both Chainmail and Full Plate proficiency but not Leather", 
				PrereqHandler.passes(prereq, character, null));
		
	}
	
	/**
	 * Test a preweaponprof that checks for a number of profs of a certain type.
	 * 
	 * @throws Exception the exception
	 */
	public void testType() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREARMORPROF:1,TYPE.Medium");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));
		
		character.addArmorProf("ARMORTYPE=Medium");
		
		assertTrue("Character has Medium Armor Proficiency", 
				PrereqHandler.passes(prereq, character, null));
	}
	
	/**
	 * Test with negation.
	 * 
	 * @throws Exception the exception
	 */
	public void testInverse() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("!PREARMORPROF:1,Breastplate");

		assertTrue("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addArmorProf("Breastplate");
		character.addArmorProf("Chainmail");

		assertFalse("Character has the Breastplate proficiency.", 
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("!PREARMORPROF:1,Leather");
		
		assertTrue("Character does not have the Leather proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("!PREARMORPROF:1,Chainmail");
		
		assertFalse("Character has the Chainmail proficiency.", 
				PrereqHandler.passes(prereq, character, null));
		
	}
	
	/**
	 * Test the prearmorprof with armorprofs added by a AUTO:ARMORPROF tag
	 * This is probably more an integration test than a unit test
	 * 
	 * @throws Exception the exception
	 */
	public void testArmorProfAddedWithAutoArmorProf() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREARMORPROF:1,Breastplate");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));
		
		final Ability martialProf = 
			TestHelper.makeAbility("Armor Proficiency (Single)", "FEAT", "General");
		martialProf.addAutoArray("ARMORPROF", "ARMORTYPE.Medium");
		
		AbilityUtilities.modFeat(
				character, null, "KEY_Armor Proficiency (Single)", true, false);

		assertTrue("Character has the Breastplate proficiency.", 
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREARMORPROF:1,Chainmail");
		assertTrue("Character has the Chainmail proficiency.",
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREARMORPROF:1,Leather");
		assertFalse("Character does not have the Leather proficiency.",
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREARMORPROF:1,TYPE.Medium");
		assertTrue("Character has martial weaponprofs.",
					PrereqHandler.passes(prereq, character, null));
		
	}
	
	/**
	 * Test Preweaponprof with a feat that has a bonus tag
	 * This test was written to help find the source of bug 1699779.
	 * 
	 * @throws Exception the exception
	 */
	public void testWithFeatThatGrantsBonus() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		
		final FeatLoader featLoader = new FeatLoader();
		
		CampaignSourceEntry cse;
		try
		{
			cse = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		
		int baseHp = character.hitPoints();
		
		Ability bar = new Ability();
		final String barStr =
			"Bar	TYPE:General	DESC:See Text	BONUS:HP|CURRENTMAX|50";
		featLoader.parseLine(Globals.getContext(), bar, barStr, cse);
		character.addFeat(bar, null);
		
		assertEquals("Character should have 50 bonus hp added.",
					baseHp+50,
					character.hitPoints()
					);
		
		character.addArmorProf("Full Plate");
		
		Ability foo = new Ability();
		final String fooStr =
			"Foo	TYPE:General	DESC:See Text	BONUS:HP|CURRENTMAX|50|PREARMORPROF:1,Full Plate";
		featLoader.parseLine(Globals.getContext(), foo, fooStr, cse);
		character.addFeat(foo, null);
		
		assertEquals("Character has the Full Plate proficiency so the bonus should be added",
					baseHp+50+50,
					character.hitPoints()
					);
	
	}
	
	/* (non-Javadoc)
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		ArmorProf leather = new ArmorProf();
		leather.setName("Leather");
		leather.setTypeInfo("Light");
		Globals.addArmorProf(leather);

		ArmorProf chainmail = new ArmorProf();
		chainmail.setName("Chainmail");
		chainmail.setTypeInfo("Medium");
		Globals.addArmorProf(chainmail);

		ArmorProf breastplate = new ArmorProf();
		breastplate.setName("Breastplate");
		breastplate.setTypeInfo("Medium");
		Globals.addArmorProf(breastplate);
	}
}
