/*
 * PreWeaponProfTest.java
 *
 * Copyright 2007 (C) Koen Van Daele <kador@foeffighters.be>
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
 */
package pcgen.core.prereq;


import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreWeaponProfTest</code> tests that the PREWEAPONPROF tag is
 * working correctly.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Koen Van Daele <kador@foeffighters.be>
 * @version $Revision$
 */
public class PreWeaponProfTest extends AbstractCharacterTestCase
{
	
	public static void main(final String[] args)
	{
		TestRunner.run(PreWeaponProfTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreWeaponProfTest.class);
	}
	
	/**
	 * Test with a simple weapon proficiency
	 * @throws Exception
	 */
	public void testOneOption() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREWEAPONPROF:1,Longsword");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addWeaponProf("Longsword");
		character.addWeaponProf("Dagger");

		assertTrue("Character has the Longsword proficiency.", 
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREWEAPONPROF:1,Longbow");
		
		assertFalse("Character does not have the Longbow proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREWEAPONPROF:1,Dagger");
		
		assertTrue("Character has the Dagger proficiency.", 
				PrereqHandler.passes(prereq, character, null));
	}


	/**
	 * Tests to see if a character has a certain number of weaponprofs from a list
	 * @throws Exception
	 */
	public void testMultiple() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREWEAPONPROF:1,Longsword,Dagger");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addWeaponProf("Longsword");
		character.addWeaponProf("Dagger");

		assertTrue("Character has one of Longsword or Dagger proficiency", 
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREWEAPONPROF:2,Longsword,Dagger");

		assertTrue("Character has both Longsword and Dagger proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREWEAPONPROF:3,Longsword,Dagger,Longbow");

		assertFalse("Character has both Longsword and Dagger proficiency but not Longbow", 
				PrereqHandler.passes(prereq, character, null));
		
	}
	
	
	protected void setUp() throws Exception
	{
		super.setUp();

		WeaponProf Longsword = new WeaponProf();
		Longsword.setName("Longsword");
		Globals.addWeaponProf(Longsword);

		WeaponProf Longbow = new WeaponProf();
		Longbow.setName("Longbow");
		Globals.addWeaponProf(Longbow);

		WeaponProf Dagger = new WeaponProf();
		Dagger.setName("Dagger");
		Globals.addWeaponProf(Dagger);

	}
}
