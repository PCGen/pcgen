/*
 * Created on 22-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.analysis.SkillModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.SourceEntry;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import org.junit.Assert;
import plugin.pretokens.parser.PreVariableParser;

/**
 * Tests PREVAR token
 */
public class PreVarTest extends AbstractCharacterTestCase
{

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreVarTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreVarTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testVarPass() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		PreVariableParser parser = new PreVariableParser();

		Prerequisite prereq = parser.parse("vareq",
				"1,count(\"ABILITIES\",\"CATEGORY=BARDIC\",\"NAME=Dancer\")",
				false, false);

		Assert.assertFalse("Test matches with no abilities.", PrereqHandler.passes(
				prereq, character, null));

		Ability ab2 = TestHelper.makeAbility("Dancer", "BARDIC",
				"General.Bardic");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		addAbility(TestHelper.getAbilityCategory(ab2), ab2);

		Assert.assertTrue("Test fails with ability present.", PrereqHandler.passes(
				prereq, character, null));
	}

	public void testMutiplePositive() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		setPCStat(character, str, 10);
		setPCStat(character, dex, 14);
		character.calcActiveBonuses();

		PreVariableParser parser = new PreVariableParser();
		Prerequisite prereq = parser.parse("VAR", "abs(STR),1,abs(DEX),3",
				false, false);
		Assert.assertFalse("Test matches with no stats passing", PrereqHandler.passes(
				prereq, character, null));

		setPCStat(character, str, 12);
		character.calcActiveBonuses();
		Assert.assertFalse("Test matches with no stats passing", PrereqHandler.passes(
				prereq, character, null));

		setPCStat(character, dex, 16);
		character.calcActiveBonuses();
		Assert.assertTrue("Test should match now both stats pass", PrereqHandler
				.passes(prereq, character, null));

	}

	public void testMutipleNegative() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		setPCStat(character, str, 10);
		setPCStat(character, dex, 14);
		character.calcActiveBonuses();

		PreVariableParser parser = new PreVariableParser();
		Prerequisite prereq = parser.parse("VAR", "abs(STR),1,abs(DEX),3",
				true, false);
		Assert.assertTrue("Test matches with no stats passing", PrereqHandler.passes(
				prereq, character, null));

		setPCStat(character, str, 12);
		character.calcActiveBonuses();
		Assert.assertTrue("Test matches with no stats passing", PrereqHandler.passes(
				prereq, character, null));

		setPCStat(character, dex, 16);
		character.calcActiveBonuses();
		Assert.assertFalse("Test should match now both stats pass", PrereqHandler
				.passes(prereq, character, null));

	}

	public void test2857849a()
	{
		final PCClass warrior = new PCClass();
		warrior.setName("Warrior");
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(warrior, "DEFINE", "MyVar|0");
		context.unconditionallyProcess(warrior, "BONUS", "VAR|MyVar|2");
		PCClassLoader loader = new PCClassLoader();
		try
		{
			SourceEntry se = new CampaignSourceEntry(new Campaign(), new URI(
					"file://test"));
			final PCClass notawarrior = loader.parseLine(context, null,
					"CLASS:NotAWarrior\tPREVARGTEQ:MyVar,1", se);
			loader.completeObject(context, se, warrior);
			loader.completeObject(context, se, notawarrior);
			PlayerCharacter character = this.getCharacter();
			Assert.assertFalse(notawarrior.qualifies(character, notawarrior));
			character.incrementClassLevel(1, warrior);
			Assert.assertTrue(notawarrior.qualifies(character, notawarrior));
		}
		catch (URISyntaxException | PersistenceLayerException e)
		{
			Assert.fail(e.getMessage());
		}
	}

	public void test2857849and2862276()
	{
		LoadContext context = Globals.getContext();
		final PCClass spellcaster = new PCClass();
		spellcaster.setName("Spellcaster");
		context.getReferenceContext().importObject(spellcaster);
		context.unconditionallyProcess(spellcaster, "SPELLTYPE", "Arcane");
		context.unconditionallyProcess(spellcaster, "SPELLSTAT", "INT");
		context.unconditionallyProcess(spellcaster, "PREVARGTEQ",
				"BASESPELLSTAT,2");
		PCClassLoader loader = new PCClassLoader();
		try
		{
			SourceEntry se = new CampaignSourceEntry(new Campaign(), new URI(
					"file://test"));
			loader.completeObject(context, se, spellcaster);
			context.getReferenceContext().resolveReferences(null);
			PlayerCharacter character = this.getCharacter();
			setPCStat(character, intel, 16);
			Assert.assertTrue(spellcaster.qualifies(character, spellcaster));
		}
		catch (URISyntaxException | PersistenceLayerException e)
		{
			Assert.fail(e.getMessage());
		}
	}

	public void test2857848a()
	{
		LoadContext context = Globals.getContext();
		final PCClass spellcaster = new PCClass();
		spellcaster.setName("Spellcaster");
		context.getReferenceContext().importObject(spellcaster);
		Skill concentration = context.getReferenceContext().constructCDOMObject(Skill.class,
				"Concentration");
		context.unconditionallyProcess(spellcaster, "SPELLTYPE", "Arcane");
		context.unconditionallyProcess(spellcaster, "SPELLSTAT", "INT");
		context.unconditionallyProcess(spellcaster, "CSKILL", "Concentration");
		context.unconditionallyProcess(spellcaster, "BONUS",
				"SKILL|Concentration|5|PREVARGT:BASESPELLSTAT,2");
		Assert.assertTrue(context.getReferenceContext().resolveReferences(null));
		PlayerCharacter character = this.getCharacter();
		setPCStat(character, intel, 16);
		PCClassLoader loader = new PCClassLoader();
		try
		{
			SourceEntry se = new CampaignSourceEntry(new Campaign(), new URI(
					"file://test"));
			loader.completeObject(context, se, spellcaster);
			Assert.assertEquals(0, SkillModifier.modifier(concentration, character)
                                                .intValue());
			character.incrementClassLevel(1, spellcaster);
			Assert.assertEquals(5, SkillModifier.modifier(concentration, character)
                                                .intValue());
		}
		catch (URISyntaxException | PersistenceLayerException e)
		{
			Assert.fail(e.getMessage());
		}
	}

	public void test2857848b()
	{
		final PCClass warrior = new PCClass();
		warrior.setName("Warrior");
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(warrior, "DEFINE", "MyVar|0");
		context.unconditionallyProcess(warrior, "BONUS", "VAR|MyVar|2");
		final PCClass notawarrior = new PCClass();
		notawarrior.setName("NotAWarrior");
		Skill concentration = context.getReferenceContext().constructCDOMObject(Skill.class,
				"Concentration");
		context.unconditionallyProcess(notawarrior, "CSKILL", "Concentration");
		context.unconditionallyProcess(notawarrior, "BONUS",
				"SKILL|Concentration|5|PREVARGT:MyVar,1");
		Assert.assertTrue(context.getReferenceContext().resolveReferences(null));
		PCClassLoader loader = new PCClassLoader();
		try
		{
			SourceEntry se = new CampaignSourceEntry(new Campaign(), new URI(
					"file://test"));
			loader.completeObject(context, se, warrior);
			loader.completeObject(context, se, notawarrior);
			PlayerCharacter character = this.getCharacter();
			character.incrementClassLevel(1, notawarrior);
			Assert.assertEquals(0, SkillModifier.modifier(concentration, character)
                                                .intValue());
			character.incrementClassLevel(1, warrior);
			Assert.assertEquals(5, SkillModifier.modifier(concentration, character)
                                                .intValue());
		}
		catch (URISyntaxException | PersistenceLayerException e)
		{
			Assert.fail(e.getMessage());
		}
	}

	public void test2857848c()
	{
		final PCClass warrior = new PCClass();
		warrior.setName("Warrior");
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(warrior, "DEFINE", "MyVar|0");
		context.unconditionallyProcess(warrior, "BONUS", "VAR|MyVar|2");
		final PCClass notawarrior = new PCClass();
		notawarrior.setName("NotAWarrior");
		context.unconditionallyProcess(notawarrior, "PREVARGTEQ", "MyVar,1");
		Skill concentration = context.getReferenceContext().constructCDOMObject(Skill.class,
				"Concentration");
		context.unconditionallyProcess(notawarrior, "CSKILL", "Concentration");
		context.unconditionallyProcess(notawarrior, "BONUS",
				"SKILL|Concentration|5");
		Assert.assertTrue(context.getReferenceContext().resolveReferences(null));
		PCClassLoader loader = new PCClassLoader();
		try
		{
			SourceEntry se = new CampaignSourceEntry(new Campaign(), new URI(
					"file://test"));
			loader.completeObject(context, se, warrior);
			loader.completeObject(context, se, notawarrior);
			PlayerCharacter character = this.getCharacter();
			Assert.assertFalse(notawarrior.qualifies(character, notawarrior));
			character.incrementClassLevel(1, notawarrior); //Fails
			Assert.assertEquals(0, SkillModifier.modifier(concentration, character)
                                                .intValue());
			character.incrementClassLevel(1, warrior);
			Assert.assertEquals(0, SkillModifier.modifier(concentration, character)
                                                .intValue());
			Assert.assertTrue(notawarrior.qualifies(character, notawarrior));
			character.incrementClassLevel(1, notawarrior);
			Assert.assertEquals(5, SkillModifier.modifier(concentration, character)
                                                .intValue());
		}
		catch (URISyntaxException | PersistenceLayerException e)
		{
			Assert.fail(e.getMessage());
		}
	}

	public void test2856622()
	{
		LoadContext context = Globals.getContext();
		final PCClass warrior = new PCClass();
		warrior.setName("Warrior");
		PCClassLevel level1 = warrior.getOriginalClassLevel(1);
		context.unconditionallyProcess(level1, "SAB",
				"Test Works|PREVARGTEQ:CL,3");
		Assert.assertTrue(context.getReferenceContext().resolveReferences(null));
		PlayerCharacter character = this.getCharacter();
		character.incrementClassLevel(1, warrior);
		PCClassLoader loader = new PCClassLoader();
		try
		{
			SourceEntry se = new CampaignSourceEntry(new Campaign(), new URI(
					"file://test"));
			loader.completeObject(context, se, warrior);
			List<SpecialAbility> sabList = level1.getListFor(ListKey.SAB);
			Assert.assertNotNull(sabList);
			Assert.assertEquals(1, sabList.size());
			SpecialAbility sab = sabList.get(0);
			Assert.assertFalse(sab.qualifies(character, warrior));
			character.incrementClassLevel(1, warrior);
			Assert.assertFalse(sab.qualifies(character, warrior));
			character.incrementClassLevel(1, warrior);
			Assert.assertTrue(sab.qualifies(character, warrior));
		}
		catch (URISyntaxException | PersistenceLayerException e)
		{
			Assert.fail(e.getMessage());
		}
	}

	public void test2856626()
	{
		LoadContext context = Globals.getContext();
		final PCClass warrior = new PCClass();
		warrior.setName("Warrior");
		context.getReferenceContext().importObject(warrior);
		context.unconditionallyProcess(warrior, "SAB",
				"Test Works|PREVARGTEQ:CL,2");
		Assert.assertTrue(context.getReferenceContext().resolveReferences(null));
		PlayerCharacter character = this.getCharacter();
		character.incrementClassLevel(1, warrior);
		PCClassLoader loader = new PCClassLoader();
		try
		{
			CampaignSourceEntry se = new CampaignSourceEntry(new Campaign(), new URI(
					"file://test"));
			loader.completeObject(context, se, warrior);
			PCClass notawarrior = loader.getCopy(context, "Warrior", "NotAWarrior", se);
			List<SpecialAbility> sabList = notawarrior.getListFor(ListKey.SAB);
			Assert.assertNotNull(sabList);
			Assert.assertEquals(1, sabList.size());
			SpecialAbility sab = sabList.get(0);
			Assert.assertFalse(sab.qualifies(character, notawarrior));
			character.incrementClassLevel(1, notawarrior);
			Assert.assertFalse(sab.qualifies(character, notawarrior));
			character.incrementClassLevel(1, notawarrior);
			Assert.assertTrue(sab.qualifies(character, notawarrior));
		}
		catch (URISyntaxException | PersistenceLayerException e)
		{
			Assert.fail(e.getMessage());
		}
	}
}
