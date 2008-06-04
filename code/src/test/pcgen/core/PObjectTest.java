/*
 * PObjectTest.java
 * Copyright 2005 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Apr 9, 2005
 *
 * $Id$
 *
 */
package pcgen.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.PCGenTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability.Nature;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbilityLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.RaceLoader;
import pcgen.util.TestHelper;
import pcgen.util.chooser.ChooserFactory;

/**
 * Test the PObject class.
 * @author jdempsey
 */
@SuppressWarnings("nls")
public class PObjectTest extends AbstractCharacterTestCase
{

	/**
	 * Constructs a new <code>PObjectTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public PObjectTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>PObjectTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public PObjectTest(final String name)
	{
		super(name);
	}

	/**
	 * Run the tests
	 * @param args
	 */
	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(PObjectTest.class);
	}

	/**
	 * Returns all the test methods in this class.
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		// quick method, adds all methods beginning with "test"
		return new TestSuite(PObjectTest.class);
	}

	/**
	 * Test DR
	 * @throws Exception
	 */
	public void testDR() throws Exception
	{
		Race race = new Race();
		race.setName("Race");
		PCTemplate template = new PCTemplate();
		race.setName("Template");

		//		race.setDR("5/Good");
		race.addDR(new DamageReduction("5", "Good"));
		assertEquals("Basic DR set.", "5/Good", race.getDRList().get(0)
			.toString());

		race.clearDR();
		//		race.setDR("0/-");
		race.addDR(new DamageReduction("0", "-"));
		assertEquals("Basic DR set.", "0/-", race.getDRList().get(0).toString());

		//		template.setDR("0/-");
		template.addDR(new DamageReduction("0", "-"));
		template.addBonusList("DR|-|1");
		PlayerCharacter pc = getCharacter();
		pc.setRace(race);
		pc.addTemplate(template);
		pc.calcActiveBonuses();
		assertEquals("Basic DR set.", "1/-", pc.calcDR());
	}

	/**
	 * Test the processing of getPCCText to ensure that it correctly produces
	 * an LST representation of an object and that the LST can then be reloaded
	 * to recrete the object.
	 *
	 * @throws PersistenceLayerException
	 */
	public void testGetPCCText() throws PersistenceLayerException
	{
		Race race = new Race();
		race.setName("TestRace");
		race.put(ObjectKey.CHALLENGE_RATING, new ChallengeRating("5"));
		String racePCCText = race.getPCCText();
		assertNotNull("PCC Text for race should not be null", racePCCText);

		RaceLoader raceLoader = new RaceLoader();
		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		Race reconstRace = new Race();
		raceLoader.parseLine(Globals.getContext(), reconstRace, racePCCText, source);
		assertEquals(
			"getPCCText should be the same after being encoded and reloaded",
			racePCCText, reconstRace.getPCCText());
		assertEquals("Racial CR was not restored after saving and reloading.",
				race.get(ObjectKey.CHALLENGE_RATING), reconstRace
						.get(ObjectKey.CHALLENGE_RATING));

		PCClass aClass = new PCClass();
		aClass.setName("TestClass");
		String classPCCText = aClass.getPCCText();
		assertNotNull("PCC Text for race should not be null", racePCCText);

		PCClassLoader classLoader = new PCClassLoader();
		PCClass reconstClass = new PCClass();
		reconstClass =
				classLoader.parseLine(Globals.getContext(), reconstClass, classPCCText, source);
		assertEquals(
			"getPCCText should be the same after being encoded and reloaded",
			classPCCText, reconstClass.getPCCText());
		assertEquals(
			"Class abbrev was not restored after saving and reloading.", aClass
				.getAbbrev(), reconstClass.getAbbrev());

	}

	/**
	 * Test the function of adding a bonus each time an associated value is chosen.
	 */
	public void testAssociatedBonus()
	{
		Ability pObj = new Ability();
		pObj.setName("My PObject");

		PlayerCharacter aPC = getCharacter();
		aPC.addFeat(pObj, null);

		pObj.addAssociated("TestPsion 1");
		pObj.applyBonus("SPELLKNOWN|CLASS=TestPsion;LEVEL=1|1", "TestPsion 1",
			aPC, false);
		aPC.calcActiveBonuses();
		assertEquals("Should get 1 bonus known spells", 1, (int) aPC
			.getTotalBonusTo("SPELLKNOWN", "CLASS.TestPsion;LEVEL.1"));
		pObj.addAssociated("TestPsion 1");
		pObj.applyBonus("SPELLKNOWN|CLASS=TestPsion;LEVEL=1|1", "TestPsion 1",
			aPC, true);
		aPC.calcActiveBonuses();
		assertEquals("Should get 3 bonus known spells", (2 * 1) + 1, (int) aPC
			.getTotalBonusTo("SPELLKNOWN", "CLASS.TestPsion;LEVEL.1"));
		pObj.addAssociated("TestPsion 1");
		pObj.applyBonus("SPELLKNOWN|CLASS=TestPsion;LEVEL=1|1", "TestPsion 1",
			aPC, false);
		aPC.calcActiveBonuses();
		assertEquals("Should get 7 bonus known spells", (3 * 2) + 1, (int) aPC
			.getTotalBonusTo("SPELLKNOWN", "CLASS.TestPsion;LEVEL.1"));
		for (BonusObj bonus : pObj.getBonusList())
		{
			bonus.setAddOnceOnly(true);
		}
		aPC.calcActiveBonuses();
		assertEquals("Should get 3 bonus known spells", 3, (int) aPC
			.getTotalBonusTo("SPELLKNOWN", "CLASS.TestPsion;LEVEL.1"));
	}

	/**
	 * Test the function of adding an ability multiple times which has  
	 * no choices and adds a static bonus.
	 * @throws Exception 
	 */
	public void testNoChoiceBonus() throws Exception
	{
		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		AbilityLoader loader = new AbilityLoader();
		Ability pObj = new Ability();
		loader
			.parseLine(
				Globals.getContext(),
				pObj,
				"Toughness	TYPE:General	STACK:YES	MULT:YES	CHOOSE:NOCHOICE	BONUS:HP|CURRENTMAX|3", source);

		PlayerCharacter aPC = getCharacter();
		int baseHP = aPC.hitPoints();
		pObj.addAssociated("");
		aPC.addFeat(pObj, null);
		aPC.calcActiveBonuses();
		assertEquals("Should have added 3 HPs", baseHP + 3, aPC.hitPoints());

		pObj.addAssociated("");
		aPC.calcActiveBonuses();
		assertEquals("2 instances should have added 6 HPs", baseHP + 6, aPC
			.hitPoints());

	}

	/**
	 * Test the function of adding an ability multiple times which has  
	 * a single choice and adds a static bonus.
	 * @throws Exception 
	 */
	public void testNoSubsChoiceBonus() throws Exception
	{
		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		AbilityLoader loader = new AbilityLoader();
		Ability pObj = new Ability();
		loader
			.parseLine(
				Globals.getContext(),
				pObj,
				"Toughness	TYPE:General	STACK:YES	MULT:YES	CHOOSE:HP|+3 HP	BONUS:HP|CURRENTMAX|3", source);

		PlayerCharacter aPC = getCharacter();
		int baseHP = aPC.hitPoints();
		pObj.addAssociated("+3 HP");
		aPC.addFeat(pObj, null);
		aPC.calcActiveBonuses();
		assertEquals("Should have added 3 HPs", baseHP + 3, aPC.hitPoints());

		pObj.addAssociated("+3 HP");
		aPC.calcActiveBonuses();
		assertEquals("2 instances should have added 6 HPs", baseHP + 6, aPC
			.hitPoints());

	}

	/**
	 * Tests description handling
	 */
	public void testDescription()
	{
		final Description desc1 = new Description("Description 1");
		final PObject pobj = new PObject();
		pobj.addDescription(desc1);

		assertEquals("Description should match", pobj
			.getDescription(getCharacter()), "Description 1");

		final Description desc2 = new Description("Description 2");
		pobj.addDescription(desc2);

		assertEquals("Description should match", "Description 1, Description 2",
			pobj.getDescription(getCharacter()));

		final Description desc3 = new Description("Description %1");
		desc3.addVariable("\"3\"");
		pobj.addDescription(desc3);

		assertEquals("Description should match",
			"Description 1, Description 2, Description 3", pobj
				.getDescription(getCharacter()));

		pobj.removeDescription("Description 2");
		assertEquals("Description should match", "Description 1, Description 3",
			pobj.getDescription(getCharacter()));

		pobj.removeDescription("Description %");
		assertEquals("Description should match", "Description 1", pobj
			.getDescription(getCharacter()));

		pobj.removeDescription("Description %\\w+");
		assertEquals("Description should match", "Description 1", pobj
			.getDescription(getCharacter()));
	}

	/**
	 * Test the definition and application of abilities. 
	 * @throws PersistenceLayerException 
	 */
	public void testAddAbility() throws PersistenceLayerException
	{
		// Create some abilities to be added
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCategory("TestCat");
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCategory("TestCat");
		AbilityCategory cat = new AbilityCategory("TestCat");
		SettingsHandler.getGame().addAbilityCategory(cat);
		Globals.addAbility(ab1);
		Globals.addAbility(ab2);

		// Link them to a template
		Race race = new Race();
		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		RaceLoader loader = new RaceLoader();
		loader
			.parseLine(
				Globals.getContext(),
				race,
				"Race1	ABILITY:TestCat|AUTOMATIC|Ability1	ABILITY:TestCat|AUTOMATIC|Ability2", source);
		List<String> keys = race.getAbilityKeys(null, cat, Nature.AUTOMATIC);
		assertEquals(2, keys.size());
		assertEquals(ab1.getKeyName(), keys.get(0));
		assertEquals(ab2.getKeyName(), keys.get(1));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.setRace(race);
		// Need to do this to populate the ability list
		pc.getAutomaticAbilityList(cat);
		assertTrue("Character should have ability1.", pc.hasAbility(null,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", pc.hasAbility(cat,
			Nature.AUTOMATIC, ab2));
	}
	
	/**
	 * Test the REMOVE:FEAT functions  
	 */
	public void testRemoveFeat()
	{
		ChooserFactory.setInterfaceClassname("pcgen.util.chooser.RandomChooser");
		Ability alertness = TestHelper.makeAbility("Alertness", AbilityCategory.FEAT.getAbilityCategory(), "General");
		Race arRace = TestHelper.makeRace("AddRemove");
		arRace.addAddList(1, "FEAT(KEY_Alertness)");
		PCClass pcClass = TestHelper.makeClass("Remove Feat Test");
		pcClass.setRemoveString("2|FEAT(KEY_Alertness)1");
		PlayerCharacter pc = getCharacter();
		pc.setRace(arRace);
		assertEquals("Initial number of feats", 0.0, pc.getFeats(), 0.1);
		
		pc.incrementClassLevel(1, pcClass);
		assertEquals("Number of feats at 1st level", 0.0, pc.getFeats(), 0.1);
		assertNotNull("Has feat", pc.getFeatKeyed(alertness.getKeyName()));
		
		pc.incrementClassLevel(2, pcClass);
		assertEquals("Number of feats at 2nd level", 1.0, pc.getFeats(), 0.1);
		assertNull("No longer has feat", pc.getFeatKeyed(alertness.getKeyName()));
	}
	
	/**
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * @see pcgen.AbstractCharacterTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
}
