/*
 * PreShieldProfTest.java
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
import pcgen.core.ShieldProf;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;

/**
 * <code>PreShieldProfTest</code> tests that the PRESHIELDPROF tag is
 * working correctly.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class PreShieldProfTest extends AbstractCharacterTestCase
{
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreShieldProfTest.class);
	}

	/**
	 * Suite.
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreShieldProfTest.class);
	}
	
	/**
	 * Test with a simple shield proficiency.
	 * 
	 * @throws Exception the exception
	 */
	public void testOneOption() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRESHIELDPROF:1,Heavy Wooden Shield");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addShieldProf("Heavy Wooden Shield");
		character.addShieldProf("Heavy Steel Sheild");

		assertTrue("Character has the Heavy Wooden Shield proficiency.", 
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESHIELDPROF:1,Light Wooden Shield");
		
		assertFalse("Character does not have the Light Wooden Shield proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESHIELDPROF:1,Heavy Steel Sheild");
		
		assertTrue("Character has the Heavy Steel Sheild proficiency.", 
				PrereqHandler.passes(prereq, character, null));
	}


	/**
	 * Tests to see if a character has a certain number of shieldprofs from a list.
	 * 
	 * @throws Exception the exception
	 */
	public void testMultiple() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRESHIELDPROF:1,Heavy Wooden Shield,Full Plate");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addShieldProf("Heavy Wooden Shield");
		character.addShieldProf("Full Plate");

		assertTrue("Character has one of Heavy Wooden Shield or Full Plate proficiency", 
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PRESHIELDPROF:2,Heavy Wooden Shield,Full Plate");

		assertTrue("Character has both Heavy Wooden Shield and Full Plate proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESHIELDPROF:3,Heavy Wooden Shield,Full Plate,Light Wooden Shield");

		assertFalse("Character has both Heavy Wooden Shield and Full Plate proficiency but not Light Wooden Shield", 
				PrereqHandler.passes(prereq, character, null));
		
	}
	
	/**
	 * Test a preshieldprof that checks for a number of profs of a certain type.
	 * 
	 * @throws Exception the exception
	 */
	public void testType() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRESHIELDPROF:1,TYPE.Medium");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));
		
		character.addShieldProf("SHIELDTYPE=Medium");
		
		assertTrue("Character has Medium Shield Proficiency", 
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
		prereq = factory.parse("!PRESHIELDPROF:1,Heavy Steel Shield");

		assertTrue("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addShieldProf("Heavy Steel Shield");
		character.addShieldProf("Heavy Wooden Shield");

		assertFalse("Character has the Heavy Steel Shield proficiency.", 
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("!PRESHIELDPROF:1,Light Wooden Shield");
		
		assertTrue("Character does not have the Light Wooden Shield proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("!PRESHIELDPROF:1,Heavy Wooden Shield");
		
		assertFalse("Character has the Heavy Wooden Shield proficiency.", 
				PrereqHandler.passes(prereq, character, null));
		
	}
	
	/**
	 * Test the preshieldprof with shieldprofs added by a AUTO:SHIELDPROF tag
	 * This is probably more an integration test than a unit test
	 * 
	 * @throws Exception the exception
	 */
	public void testShieldProfAddedWithAutoShieldProf() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRESHIELDPROF:1,Heavy Steel Shield");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));
		
		final Ability martialProf = 
			TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");
		martialProf.addAutoArray("SHIELDPROF", "SHIELDTYPE.Heavy");
		
		AbilityUtilities.modFeat(
				character, null, "KEY_Shield Proficiency (Single)", true, false);

		assertTrue("Character has the Heavy Steel Shield proficiency.", 
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESHIELDPROF:1,Heavy Wooden Shield");
		assertTrue("Character has the Heavy Wooden Shield proficiency.",
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESHIELDPROF:1,Light Wooden Shield");
		assertFalse("Character does not have the Light Wooden Shield proficiency.",
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESHIELDPROF:1,TYPE.Heavy");
		assertTrue("Character has heavy shield prof.",
					PrereqHandler.passes(prereq, character, null));
		
	}
	
	/**
	 * Test PreShieldProf with a feat that has a bonus tag
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
		featLoader.parseLine(bar, barStr, cse);
		character.addFeat(bar, null);
		
		assertEquals("Character should have 50 bonus hp added.",
					baseHp+50,
					character.hitPoints()
					);
		
		character.addShieldProf("Full Plate");
		
		Ability foo = new Ability();
		final String fooStr =
			"Foo	TYPE:General	DESC:See Text	BONUS:HP|CURRENTMAX|50|PRESHIELDPROF:1,Full Plate";
		featLoader.parseLine(foo, fooStr, cse);
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

		ShieldProf lightWood = new ShieldProf();
		lightWood.setName("Light Wooden Shield");
		lightWood.setTypeInfo("Light");
		Globals.addShieldProf(lightWood);

		ShieldProf heavyWood = new ShieldProf();
		heavyWood.setName("Heavy Wooden Shield");
		heavyWood.setTypeInfo("Heavy");
		Globals.addShieldProf(heavyWood);

		ShieldProf heavySteel = new ShieldProf();
		heavySteel.setName("Heavy Steel Shield");
		heavySteel.setTypeInfo("Heavy");
		Globals.addShieldProf(heavySteel);
	}
}
