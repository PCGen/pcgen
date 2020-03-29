/*
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
 */
package pcgen.core;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;

import pcgen.AbstractCharacterTestCase;
import pcgen.base.format.OrderedPairManager;
import pcgen.base.format.StringManager;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.core.analysis.BonusAddition;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbilityCategoryLoader;
import pcgen.persistence.lst.AbilityLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.Test;

/**
 * Test the PObject class.
 */
@SuppressWarnings("nls")
public class PObjectTest extends AbstractCharacterTestCase
{
	/**
	 * Test DR.
	 */
	@Test
	public void testDR()
	{
		Race race = new Race();
		LoadContext context = Globals.getContext();

		race.setName("Race");
		PCTemplate template = new PCTemplate();
		race.setName("Template");

		//		race.setDR("5/Good");
		race.addToListFor(ListKey.DAMAGE_REDUCTION, new DamageReduction(FormulaFactory.getFormulaFor(5), "Good"));
		assertEquals("Basic DR set.", "5/Good", race.getListFor(ListKey.DAMAGE_REDUCTION).get(0)
			.toString());

		race.removeListFor(ListKey.DAMAGE_REDUCTION);
		//		race.setDR("0/-");
		race.addToListFor(ListKey.DAMAGE_REDUCTION, new DamageReduction(FormulaFactory.getFormulaFor(0), "-"));
		assertEquals("Basic DR set.", "0/-", race.getListFor(ListKey.DAMAGE_REDUCTION).get(0).toString());

		//		template.setDR("0/-");
		template.addToListFor(ListKey.DAMAGE_REDUCTION, new DamageReduction(FormulaFactory.getFormulaFor(0), "-"));
		final BonusObj aBonus = Bonus.newBonus(context, "DR|-|1");
		
		if (aBonus != null)
		{
			template.addToListFor(ListKey.BONUS, aBonus);
		}
		PlayerCharacter pc = getCharacter();
		pc.setRace(race);
		pc.addTemplate(template);
		pc.calcActiveBonuses();
		assertEquals("Basic DR set.", "1/-", pc.getDisplay().calcDR());
	}

	/**
	 * Test the processing of getPCCText to ensure that it correctly produces
	 * an LST representation of an object and that the LST can then be reloaded
	 * to recreate the object.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testGetPCCText() throws PersistenceLayerException
	{
		OrderedPairManager opManager = new OrderedPairManager();
		LoadContext context = Globals.getContext();
		context.getVariableContext().assertLegalVariableID(
			CControl.FACE.getDefaultValue(), context.getActiveScope(), opManager);
		Race race = new Race();
		race.setName("TestRace");
		race.put(ObjectKey.CHALLENGE_RATING, new ChallengeRating(FormulaFactory.getFormulaFor(5)));
		String racePCCText = race.getPCCText();
		assertNotNull("PCC Text for race should not be null", racePCCText);

		GenericLoader<Race> raceLoader = new GenericLoader<>(Race.class);
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
		raceLoader.parseLine(context, null, racePCCText, source);
		Race reconstRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "TestRace");
		assertEquals(
			"getPCCText should be the same after being encoded and reloaded",
			racePCCText, reconstRace.getPCCText());
		assertEquals("Racial CR was not restored after saving and reloading.",
				race.get(ObjectKey.CHALLENGE_RATING), reconstRace
						.get(ObjectKey.CHALLENGE_RATING));

		FactKey.getConstant("Abb", new StringManager());
		PCClass aClass = new PCClass();
		aClass.setName("TestClass");
		String classPCCText = aClass.getPCCText();
		assertNotNull("PCC Text for race should not be null", racePCCText);

		PCClassLoader classLoader = new PCClassLoader();
		PCClass reconstClass =
				classLoader.parseLine(context, null, classPCCText,
					source);
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
	@Test
	public void testAssociatedBonus()
	{
		Ability pObj = new Ability();
		pObj.setCDOMCategory(BuildUtilities.getFeatCat());
		pObj.setName("My PObject");
		pObj.setCDOMCategory(BuildUtilities.getFeatCat());
		Globals.getContext().unconditionallyProcess(pObj, "CHOOSE", "LANG|ALL");
		Globals.getContext().unconditionallyProcess(pObj, "MULT", "YES");
		Globals.getContext().unconditionallyProcess(pObj, "STACK", "YES");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "TestPsion 1");

		PlayerCharacter aPC = getCharacter();
		CNAbility cna = AbstractCharacterTestCase.applyAbility(aPC,
			BuildUtilities.getFeatCat(), pObj, "TestPsion 1");
		pObj = cna.getAbility();
		BonusAddition.applyBonus("SPELLKNOWN|CLASS=TestPsion;LEVEL=1|1", "TestPsion 1",
			aPC, pObj);
		aPC.calcActiveBonuses();
		assertEquals("Should get 1 bonus known spells", 1, (int) aPC
			.getTotalBonusTo("SPELLKNOWN", "CLASS.TestPsion;LEVEL.1"));
		AbstractCharacterTestCase.applyAbility(aPC, BuildUtilities.getFeatCat(), pObj, "TestPsion 1");
		BonusAddition.applyBonus("SPELLKNOWN|CLASS=TestPsion;LEVEL=1|1", "TestPsion 1",
			aPC, pObj);
		aPC.calcActiveBonuses();
		assertEquals("Should get 4 bonus known spells", (2 * 2), (int) aPC
			.getTotalBonusTo("SPELLKNOWN", "CLASS.TestPsion;LEVEL.1"));
		AbstractCharacterTestCase.applyAbility(aPC, BuildUtilities.getFeatCat(), pObj, "TestPsion 1");
		BonusAddition.applyBonus("SPELLKNOWN|CLASS=TestPsion;LEVEL=1|1", "TestPsion 1",
			aPC, pObj);
		aPC.calcActiveBonuses();
		assertEquals("Should get 9 bonus known spells", (3 * 3), (int) aPC
			.getTotalBonusTo("SPELLKNOWN", "CLASS.TestPsion;LEVEL.1"));
	}

	/**
	 * Test the function of adding an ability multiple times which has  
	 * no choices and adds a static bonus.
	 * @throws Exception 
	 */
	@Test
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
		loader
			.parseLine(
				Globals.getContext(),
				null,
				"Toughness	CATEGORY:FEAT	TYPE:General	STACK:YES	"
				+ "MULT:YES	CHOOSE:NOCHOICE	BONUS:HP|CURRENTMAX|3", source);

		Ability pObj = Globals.getContext().getReferenceContext()
			.getManufacturerId(BuildUtilities.getFeatCat()).getActiveObject("Toughness");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Foo");
		PlayerCharacter aPC = getCharacter();
		int baseHP = aPC.hitPoints();
		AbstractCharacterTestCase.applyAbility(aPC, BuildUtilities.getFeatCat(), pObj, "");
		aPC.calcActiveBonuses();
		assertEquals("Should have added 3 HPs", baseHP + 3, aPC.hitPoints());

		AbstractCharacterTestCase.applyAbility(aPC, BuildUtilities.getFeatCat(), pObj, "");
		aPC.calcActiveBonuses();
		assertEquals("2 instances should have added 6 HPs", baseHP + 6, aPC
			.hitPoints());

	}

	/**
	 * Test the function of adding an ability multiple times which has  
	 * a single choice and adds a static bonus.
	 * @throws Exception 
	 */
	@Test
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
		loader
			.parseLine(
				Globals.getContext(),
				null,
				"Toughness	CATEGORY:FEAT	TYPE:General	STACK:YES	"
				+ "MULT:YES	CHOOSE:NOCHOICE	BONUS:HP|CURRENTMAX|3", source);
		Ability pObj = Globals.getContext().getReferenceContext()
			.getManufacturerId(BuildUtilities.getFeatCat()).getActiveObject("Toughness");
		PlayerCharacter aPC = getCharacter();
		int baseHP = aPC.hitPoints();
		AbstractCharacterTestCase.applyAbility(aPC, BuildUtilities.getFeatCat(), pObj, "");
		aPC.calcActiveBonuses();
		assertEquals("Should have added 3 HPs", baseHP + 3, aPC.hitPoints());

		AbstractCharacterTestCase.applyAbility(aPC, BuildUtilities.getFeatCat(), pObj, "");
		aPC.calcActiveBonuses();
		assertEquals("2 instances should have added 6 HPs", baseHP + 6, aPC
			.hitPoints());

	}

	/**
	 * Tests description handling
	 */
	@Test
	public void testDescription()
	{
		final Description desc1 = new Description("Description 1.");
		final Race pobj = new Race();
		pobj.addToListFor(ListKey.DESCRIPTION, desc1);

		PlayerCharacter pc = getCharacter();
		assertEquals("Description should match", "Description 1.", pc
			.getDescription(pobj));

		final Description desc2 = new Description("Description 2.");
		pobj.addToListFor(ListKey.DESCRIPTION, desc2);

		assertEquals("Description should match", "Description 1. Description 2.",
			pc.getDescription(pobj));

		final Description desc3 = new Description("Description %1.");
		desc3.addVariable("\"3\"");
		pobj.addToListFor(ListKey.DESCRIPTION, desc3);

		assertEquals("Description should match",
			"Description 1. Description 2. Description 3.", pc
				.getDescription(pobj));

		pobj.removeFromListFor(ListKey.DESCRIPTION, desc2);
		assertEquals("Description should match", "Description 1. Description 3.",
			pc.getDescription(pobj));
	}

	/**
	 * Test the definition and application of abilities. 
	 * @throws PersistenceLayerException 
	 */
	@Test
	public void testAddAbility() throws PersistenceLayerException
	{
		// Create some abilities to be added
		LoadContext context = Globals.getContext();
		AbilityCategory cat = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "TestCat");
		new AbilityCategoryLoader().parseLine(context, "TestCat\tCATEGORY:TestCat", null);
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCDOMCategory(SettingsHandler.getGameAsProperty().get().getAbilityCategory("TestCat"));
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCDOMCategory(SettingsHandler.getGameAsProperty().get().getAbilityCategory("TestCat"));
		context.getReferenceContext().importObject(ab1);
		context.getReferenceContext().importObject(ab2);

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
		GenericLoader<Race> loader = new GenericLoader<>(Race.class);
		loader
			.parseLine(
				context,
				race,
				"Race1	ABILITY:TestCat|AUTOMATIC|Ability1	ABILITY:TestCat|AUTOMATIC|Ability2", source);
		context.getReferenceContext().importObject(ab1);
		context.getReferenceContext().importObject(ab2);
		CDOMSingleRef<AbilityCategory> acRef =
				context.getReferenceContext().getCDOMReference(
					AbilityCategory.class, "TestCat");
		assertTrue(context.getReferenceContext().resolveReferences(null));
		CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(acRef, Nature.AUTOMATIC);
		Collection<CDOMReference<Ability>> listMods = race.getListMods(autoList);
		assertEquals(2, listMods.size());
		Iterator<CDOMReference<Ability>> iterator = listMods.iterator();
		CDOMReference<Ability> ref1 = iterator.next();
		CDOMReference<Ability> ref2 = iterator.next();
		Collection<Ability> contained1 = ref1.getContainedObjects();
		Collection<Ability> contained2 = ref2.getContainedObjects();
		assertEquals(1, contained1.size());
		assertEquals(1, contained2.size());
		assertTrue(contained1.contains(ab1) || contained2.contains(ab1));
		assertTrue(contained1.contains(ab2) || contained2.contains(ab2));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.setRace(race);
		assertTrue("Character should have ability1.", hasAbility(pc, cat,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", hasAbility(pc, cat,
			Nature.AUTOMATIC, ab2));
	}

}
