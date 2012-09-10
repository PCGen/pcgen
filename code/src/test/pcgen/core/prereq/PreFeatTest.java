/*
 * Created on 23-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.EnUsLocaleDependentTestCase;
import pcgen.LocaleDependentTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.WeaponProf;
import pcgen.gui.utils.SwingChooser;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;
import pcgen.util.chooser.ChooserFactory;
import plugin.pretokens.parser.PreFeatParser;

/**
 * @author Valued Customer
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreFeatTest extends AbstractCharacterTestCase
{
	public static void main(final String[] args)
	{
		TestRunner.run(PreFeatTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreFeatTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void test2Feats() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Ability powerAttack = new Ability();
		powerAttack.setName("Power Attack");
		character.addAbilityNeedCheck(AbilityCategory.FEAT, powerAttack);
		
		final Ability cleave = new Ability();
		cleave.setName("Cleave");
		character.addAbilityNeedCheck(AbilityCategory.FEAT, cleave);
		
		final Prerequisite prePA = new Prerequisite();
		prePA.setKind("FEAT");
		prePA.setKey("Power Attack");
		prePA.setOperand("1");
		prePA.setOperator(PrerequisiteOperator.EQ);

		boolean passes = PrereqHandler.passes(prePA, character, null);
		is(passes, eq(true), "Has Power Attack Feat");

		final Prerequisite preCleave = new Prerequisite();
		preCleave.setKind("FEAT");
		preCleave.setKey("Cleave");
		preCleave.setOperand("1");
		preCleave.setOperator(PrerequisiteOperator.EQ);

		passes = PrereqHandler.passes(preCleave, character, null);
		is(passes, eq(true), "Has Cleave Feat");

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind(null);
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("2");
		prereq.addPrerequisite(prePA);
		prereq.addPrerequisite(preCleave);

		passes = PrereqHandler.passes(prereq, character, null);
		is(passes, eq(true), "Has both Power Attack and Cleave Feats");
	}

	/**
	 * @throws Exception
	 */
	public void testWeaponFocus() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		
		final WeaponProf wp = new WeaponProf();
		wp.setName("Rapier");
		Globals.getContext().ref.importObject(wp);

		final Equipment e = new Equipment();
		e.setName("Rapier");
		TestHelper.addType(e, "Weapon.Melee.Martial.Finesseable.Standard.Piercing.Sword");
		Globals.getContext().ref.importObject(e);

		final Ability focusFeat = new Ability();
		focusFeat.setName("Weapon Focus");
		focusFeat.setCDOMCategory(AbilityCategory.FEAT);
		focusFeat.put(StringKey.CHOICE_STRING, "WEAPONPROFS|LIST");
		Ability pcFocusFeat = character.addAbilityNeedCheck(AbilityCategory.FEAT, focusFeat);
		character.addAssociation(pcFocusFeat, "Rapier");

		final Prerequisite preFeat = new Prerequisite();
		preFeat.setKind("FEAT");
		preFeat.setKey("Weapon Focus");
		preFeat.setSubKey("Rapier");
		preFeat.setOperand("1");
		preFeat.setOperator(PrerequisiteOperator.EQ);

		boolean passes = PrereqHandler.passes(preFeat, character, null);
		assertTrue(passes);

		new Prerequisite();
		preFeat.setKind("FEAT");
		preFeat.setKey("Weapon Focus");
		preFeat.setSubKey("%");
		preFeat.setOperand("1");
		preFeat.setOperator(PrerequisiteOperator.EQ);
		passes = PrereqHandler.passes(preFeat, character, null);
		assertTrue(passes);

//		new Prerequisite();
//		preFeat.setKind("FEAT");
//		preFeat.setKey("Weapon Focus");
//		preFeat.setSubKey("TYPE.Finesseable.Piercing");
//		preFeat.setOperand("1");
//		preFeat.setOperator(PrerequisiteOperator.EQ);
//		passes = PrereqHandler.passes(preFeat, character, null);
//		assertTrue(passes);
}

	/**
	 * @throws Exception
	 */
	public void test966023a() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Ability armourProf = new Ability();
		armourProf.setName("Armor Proficiency (Light)");
		character.addAbilityNeedCheck(AbilityCategory.FEAT, armourProf);

		final Prerequisite preArmour = new Prerequisite();
		preArmour.setKind("FEAT");
		preArmour.setKey("Armor Proficiency");
		preArmour.setSubKey("Light");
		preArmour.setOperand("1");
		preArmour.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(preArmour, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void test966023b() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Ability spellFocus = new Ability();
		spellFocus.setName("Spell Focus");
		Ability pcSpellFocus = character.addAbilityNeedCheck(AbilityCategory.FEAT, spellFocus);
		character.addAssociation(pcSpellFocus, "Conjuration");

		final Prerequisite preSpellFocus = new Prerequisite();
		preSpellFocus.setKind("FEAT");
		preSpellFocus.setKey("Spell Focus");
		preSpellFocus.setSubKey("Conjuration");
		preSpellFocus.setOperand("1");
		preSpellFocus.setOperator(PrerequisiteOperator.EQ);

		final boolean passes =
				PrereqHandler.passes(preSpellFocus, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void test966023c() throws Exception
	{
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());

		SettingsHandler.getGame().addToSchoolList("Conjuration");
		SettingsHandler.getGame().addToSchoolList("Evocation");
		SettingsHandler.getGame().addToSchoolList("Illusion");
		SettingsHandler.getGame().addToSchoolList("Necromany");
		final PlayerCharacter character = getCharacter();
		final Ability spellFocus = new Ability();

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

		final String spellFocusStr =
				"Spell Focus	TYPE:General	DESC:See Text	STACK:NO	MULT:YES	CHOOSE:SCHOOLS|ALL	BONUS:DC|SCHOOL.%LIST|1	SOURCEPAGE:Feats.rtf";
		final FeatLoader featLoader = new FeatLoader();
		featLoader.parseLine(Globals.getContext(), spellFocus, spellFocusStr, cse);
		character.addAbilityNeedCheck(AbilityCategory.FEAT, spellFocus);
		character.addAssociation(spellFocus, "Evocation");

		final Prerequisite preSpellFocus = new Prerequisite();
		preSpellFocus.setKind("FEAT");
		preSpellFocus.setKey("Spell Focus");
		preSpellFocus.setSubKey("Conjuration");
		preSpellFocus.setOperand("1");
		preSpellFocus.setOperator(PrerequisiteOperator.EQ);

		final boolean passes =
				PrereqHandler.passes(preSpellFocus, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testExclusion() throws Exception
	{
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());

		SettingsHandler.getGame().addToSchoolList("Conjuration");
		SettingsHandler.getGame().addToSchoolList("Evocation");
		SettingsHandler.getGame().addToSchoolList("Illusion");
		SettingsHandler.getGame().addToSchoolList("Necromany");
		final PlayerCharacter character = getCharacter();
		final Ability spellFocus = new Ability();

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

		final String spellFocusStr =
				"Spell Focus	TYPE:FeatTest	DESC:See Text	STACK:NO	MULT:YES	CHOOSE:SCHOOLS|ALL	BONUS:DC|SCHOOL.%LIST|1	SOURCEPAGE:Feats.rtf";
		final FeatLoader featLoader = new FeatLoader();
		featLoader.parseLine(Globals.getContext(), spellFocus, spellFocusStr, cse);
		character.addAbilityNeedCheck(AbilityCategory.FEAT, spellFocus);
		character.addAssociation(spellFocus, "Evocation");

		final Ability armourProf = new Ability();
		armourProf.setName("Armor Proficiency (Light)");
		armourProf.addToListFor(ListKey.TYPE, Type.getConstant("WPNPROF"));
		character.addAbilityNeedCheck(AbilityCategory.FEAT, armourProf);

		PreFeatParser parser = new PreFeatParser();

		Prerequisite prereq =
				parser.parse("feat", "1,TYPE.FeatTest,[Spell Focus]", false,
					false);

		final Prerequisite preFeatType1 = new Prerequisite();
		preFeatType1.setKind("FEAT");
		preFeatType1.setKey("TYPE.FeatTest");
		preFeatType1.setOperand("1");
		preFeatType1.setOperator(PrerequisiteOperator.EQ);

		final Prerequisite preFeatType2 = new Prerequisite();
		preFeatType2.setKind("FEAT");
		preFeatType2.setKey("TYPE.FeatTest");
		preFeatType2.setOperand("2");
		preFeatType2.setOperator(PrerequisiteOperator.EQ);

		boolean passes = PrereqHandler.passes(preFeatType1, character, null);
		assertTrue("Should pass single test feat test", passes);
		passes = PrereqHandler.passes(preFeatType2, character, null);
		assertFalse("Should not pass double test feat test", passes);
		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse("Should not pass combined test", passes);
	}

	public void testInverted() throws PersistenceLayerException
	{
		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereq = factory.parse("!PREFEAT:1,Uncanny Dodge");
		// Prerequisite.toString() is localized, so force other locale here.
		LocaleDependentTestCase.before(Locale.US);
		assertEquals(
			"Inverted feat test parsing",
			"<prereq kind=\"feat\" key=\"Uncanny Dodge\" operator=\"LT\" operand=\"1\" >\n</prereq>\n",
			prereq.toString());
		EnUsLocaleDependentTestCase.after();
	}

	/**
	 * Test that wildcards in feat prereqs work.
	 * @throws Exception
	 */
	public void testWildcard() throws Exception
	{
		final Ability skillFocusKnow = new Ability();
		skillFocusKnow.setName("Skill Focus");
		skillFocusKnow.put(StringKey.KEY_NAME, "Skill Focus");

		final PlayerCharacter character = getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		final Prerequisite prereq =
				factory.parse("PREFEAT:1,Skill Focus (Knowledge%)");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse("Should not pass without skill focus", passes);

		Ability pcFeat = character.addAbilityNeedCheck(AbilityCategory.FEAT, skillFocusKnow);
		character.addAssociation(pcFeat, "Knowledge (Arcana)");
		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue("Should pass with skill focus", passes);

		factory.parse("PREFEAT:1,Skill Focus (%)");

		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue("Should pass with skill focus", passes);

	}

	public void testCheckMult() throws Exception
	{
		final Ability spellFocus = new Ability();
		spellFocus.setName("Spell Focus");
		spellFocus.put(StringKey.KEY_NAME, "Spell Focus");
		spellFocus.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);

		final PlayerCharacter character = getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PREFEAT:2,CHECKMULT,Spell Focus");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse("Should not pass without spell focus", passes);

		Ability pcFeat = character.addAbilityNeedCheck(AbilityCategory.FEAT, spellFocus);
		character.addAssociation(pcFeat, "Evocation");

		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse("Should not pass with only one spell focus", passes);

		character.addAssociation(pcFeat, "Enchantment");

		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue("Should pass with spell focus", passes);

		prereq =
				factory
					.parse("PREFEAT:2,CHECKMULT,Spell Focus,[Spell Focus (Enchantment)]");

		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse("Should not pass has Spell Focus (Enchantment)", passes);
	}
}
