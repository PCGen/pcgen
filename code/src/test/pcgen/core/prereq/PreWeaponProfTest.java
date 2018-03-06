/*
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
 */
package pcgen.core.prereq;


import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * <code>PreWeaponProfTest</code> tests that the PREWEAPONPROF tag is
 * working correctly.
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

		PCTemplate pct = new PCTemplate();
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(pct, "AUTO", "WEAPONPROF|Longsword|Dagger");
		assertTrue(context.getReferenceContext().resolveReferences(null));

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREWEAPONPROF:1,Longsword");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addTemplate(pct);

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
		PCTemplate pct = new PCTemplate();
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(pct, "AUTO", "WEAPONPROF|Longsword|Dagger");
		assertTrue(context.getReferenceContext().resolveReferences(null));

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREWEAPONPROF:1,Longsword,Dagger");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addTemplate(pct);

		assertTrue("Character has one of Longsword or Dagger proficiency", 
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREWEAPONPROF:2,Longsword,Dagger");

		assertTrue("Character has both Longsword and Dagger proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREWEAPONPROF:3,Longsword,Dagger,Longbow");

		assertFalse("Character has both Longsword and Dagger proficiency but not Longbow", 
				PrereqHandler.passes(prereq, character, null));
		
	}
	
	/**
	 * Test a preweaponprof that checks for a number of profs of a certain type
	 * @throws Exception
	 */
	public void testType() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		PCTemplate pctls = new PCTemplate();
		PCTemplate pctlb = new PCTemplate();
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(pctls, "AUTO", "WEAPONPROF|Longsword");
		context.unconditionallyProcess(pctlb, "AUTO", "WEAPONPROF|Longbow");
		assertTrue(context.getReferenceContext().resolveReferences(null));

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREWEAPONPROF:1,TYPE.Martial");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));
		
		character.addTemplate(pctls);
		
		assertTrue("Character has one Martial Weapon Proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREWEAPONPROF:2,TYPE.Martial");

		assertFalse("Character only has one proficiency", PrereqHandler.passes(
			prereq, character, null));
		
		character.addTemplate(pctlb);
		
		assertTrue("Character has two Martial Weapon Proficiencies", 
				PrereqHandler.passes(prereq, character, null));
	
	}
	
	/**
	 * Test with negation
	 * @throws Exception
	 */
	public void testInverse() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		PCTemplate pct = new PCTemplate();
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(pct, "AUTO", "WEAPONPROF|Longsword|Dagger");
		assertTrue(context.getReferenceContext().resolveReferences(null));

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("!PREWEAPONPROF:1,Longsword");

		assertTrue("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));

		character.addTemplate(pct);

		assertFalse("Character has the Longsword proficiency.", 
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("!PREWEAPONPROF:1,Longbow");
		
		assertTrue("Character does not have the Longbow proficiency", 
				PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("!PREWEAPONPROF:1,Dagger");
		
		assertFalse("Character has the Dagger proficiency.", 
				PrereqHandler.passes(prereq, character, null));
		
	}
	
	/**
	 * Test the preweaponprof with weaponprofs added by a AUTO:WEAPONPROF tag
	 * This is probably more an integration test than a unit test
	 * This test was written to help find the source of bug 1699779
	 * @throws Exception
	 */
	public void testWeaponProfAddedWithAutoWeaponProf() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREWEAPONPROF:1,Longsword");

		assertFalse("Character has no proficiencies", PrereqHandler.passes(
			prereq, character, null));
		
		final Ability martialProf = 
			TestHelper.makeAbility("Weapon Proficiency (Martial)", BuildUtilities.getFeatCat(), "General");
		Globals.getContext().unconditionallyProcess(martialProf, "AUTO",
				"WEAPONPROF|TYPE.Martial");
		assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));
		
		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

		assertTrue("Character has the Longsword proficiency.", 
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREWEAPONPROF:1,Longbow");
		assertTrue("Character has the Longbow proficiency.",
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREWEAPONPROF:1,Dagger");
		assertFalse("Character does not have the Dagger proficiency.",
					PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PREWEAPONPROF:1,TYPE.Martial");
		assertTrue("Character has martial weaponprofs.",
					PrereqHandler.passes(prereq, character, null));
		
	}
	
	/**
	 * Test Preweaponprof with a feat that has a bonus tag
	 * This test was written to help find the source of bug 1699779
	 * @throws Exception
	 */
	public void testWithFeatThatGrantsBonus() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		PCTemplate pctls = new PCTemplate();
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(pctls, "AUTO", "WEAPONPROF|Longsword");
		assertTrue(context.getReferenceContext().resolveReferences(null));
		
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
		addAbility(BuildUtilities.getFeatCat(), bar);
		
		assertEquals("Character should have 50 bonus hp added.",
					baseHp+50,
					character.hitPoints()
					);
		
		character.addTemplate(pctls);
		
		Ability foo = new Ability();
		final String fooStr =
			"Foo	TYPE:General	DESC:See Text	BONUS:HP|CURRENTMAX|50|PREWEAPONPROF:1,Longsword";
		featLoader.parseLine(Globals.getContext(), foo, fooStr, cse);
		addAbility(BuildUtilities.getFeatCat(), foo);
		
		assertEquals("Character has the longsword proficiency so the bonus should be added",
					baseHp+50+50,
					character.hitPoints()
					);
	
	}
	
	/**
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		WeaponProf Longsword = new WeaponProf();
		Longsword.setName("Longsword");
		Longsword.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		Globals.getContext().getReferenceContext().importObject(Longsword);

		WeaponProf Longbow = new WeaponProf();
		Longbow.setName("Longbow");
		Longbow.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		Globals.getContext().getReferenceContext().importObject(Longbow);

		WeaponProf Dagger = new WeaponProf();
		Dagger.setName("Dagger");
		Dagger.addToListFor(ListKey.TYPE, Type.SIMPLE);
		Globals.getContext().getReferenceContext().importObject(Dagger);

	}
}
