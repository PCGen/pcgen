/*
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.io.exporttoken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Locale;

import pcgen.AbstractCharacterTestCase;
import pcgen.EnUsLocaleDependentTestCase;
import pcgen.LocaleDependentTestCase;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.helper.Capacity;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.RuleCheck;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.WeaponProf;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.EquipSet;
import pcgen.core.character.WieldCategory;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code WeaponTokenTest} contains tests to verify that the
 * WEAPON token is working correctly.
 */

public class WeaponTokenTest extends AbstractCharacterTestCase
{
	private Equipment dblWpn = null;
	private Equipment bastardSword = null;
	private Equipment largeSword = null;
	private Equipment fineSword = null;
	private Equipment longSpear = null;
	private Equipment bite = null;
	private Equipment longbow = null;
	private Equipment arrow;
	private Ability wpnBonusAbility;
	private PCTemplate wpnBonusPct;

	@BeforeEach
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();
		LoadContext context = Globals.getContext();

		//Stats
		setPCStat(character, str, 15);
		setPCStat(character, dex, 16);
		setPCStat(character, intel, 17);
		BonusObj aBonus = Bonus.newBonus(context, "COMBAT|TOHIT.Melee|STR|TYPE=Ability");
		
		if (aBonus != null)
		{
			str.addToListFor(ListKey.BONUS, aBonus);
		}
		aBonus = Bonus.newBonus(context, "COMBAT|DAMAGE.Melee,DAMAGE.Thrown|STR");
		
		if (aBonus != null)
		{
			str.addToListFor(ListKey.BONUS, aBonus);
		}
		aBonus = Bonus.newBonus(context, "COMBAT|DAMAGEMULT:0|0.5*(STR>=0)");
		
		if (aBonus != null)
		{
			str.addToListFor(ListKey.BONUS, aBonus);
		}
		aBonus = Bonus.newBonus(context, "COMBAT|DAMAGEMULT:1|1");
		
		if (aBonus != null)
		{
			str.addToListFor(ListKey.BONUS, aBonus);
		}
		aBonus = Bonus.newBonus(context, "COMBAT|DAMAGEMULT:2|1.5*(STR>=0)");
		
		if (aBonus != null)
		{
			str.addToListFor(ListKey.BONUS, aBonus);
		}

		aBonus = Bonus.newBonus(context, "MODSKILLPOINTS|NUMBER|INT");
		
		if (aBonus != null)
		{
			intel.addToListFor(ListKey.BONUS, aBonus);
		}

		// Race
		Race testRace = new Race();
		testRace.setName("TestRace");
		testRace.put(StringKey.KEY_NAME, "KEY_TEST_RACE");
		CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(medium);
		CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
		testRace.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));
		character.setRace(testRace);

		// Class
		PCClass myClass = new PCClass();
		myClass.setName("My Class");
		myClass.put(StringKey.KEY_NAME, "KEY_MY_CLASS");
		myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
		final BonusObj babClassBonus = Bonus.newBonus(context, "COMBAT|BASEAB|CL+15");
		myClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, babClassBonus);
		context.getReferenceContext().importObject(myClass);
		character.incrementClassLevel(1, myClass, true);

		character.calcActiveBonuses();

		dblWpn = new Equipment();
		dblWpn.setName("DoubleWpn");
		dblWpn.put(StringKey.KEY_NAME, "KEY_DOUBLE_WPN");
		TestHelper.addType(dblWpn, "Weapon.Melee.Martial.Double.Standard.Bludgeoning.Flail");
		dblWpn.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		dblWpn.getEquipmentHead(2).put(StringKey.DAMAGE, "1d6");
		dblWpn.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		dblWpn.getEquipmentHead(2).put(IntegerKey.CRIT_MULT, 2);
		dblWpn.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 1);
		dblWpn.getEquipmentHead(2).put(IntegerKey.CRIT_RANGE, 1);
		dblWpn.put(IntegerKey.SLOTS, 2);
		dblWpn.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
				WieldCategory.class, "TwoHanded"));
		dblWpn.put(ObjectKey.SIZE, mediumRef);
		dblWpn.put(ObjectKey.BASESIZE, mediumRef);
		character.addEquipment(dblWpn);
		EquipSet def = new EquipSet("0.1", "Default");
		character.addEquipSet(def);
		EquipSet es =
				new EquipSet("0.1.1", "Double Weapon", dblWpn.getKeyName(),
					dblWpn);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		WeaponProf wp = new WeaponProf();
		wp.setName("DoubleWpn");
		wp.put(IntegerKey.HANDS, Constants.HANDS_SIZE_DEPENDENT);
		context.getReferenceContext().importObject(wp);

		wp = new WeaponProf();
		wp.setName("Sword (Bastard)");
		wp.put(StringKey.KEY_NAME, "KEY_Sword (Bastard)");
		TestHelper.addType(wp, "MARTIAL.EXOTIC");
		context.getReferenceContext().importObject(wp);

		bastardSword = new Equipment();
		bastardSword.setName("Sword, Bastard");
		bastardSword.put(StringKey.KEY_NAME, "BASTARD_SWORD");
		bastardSword.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<>(wp));
		TestHelper.addType(bastardSword, "Weapon.Melee.Martial.Exotic.Standard.Slashing.Sword");
		bastardSword.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		bastardSword.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		bastardSword.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 2);
		bastardSword.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
				WieldCategory.class, "TwoHanded"));
		bastardSword.put(ObjectKey.SIZE, mediumRef);
		bastardSword.put(ObjectKey.BASESIZE, mediumRef);

		wp = new WeaponProf();
		wp.setName("Longsword");
		wp.put(StringKey.KEY_NAME, "KEY_LONGSWORD");
		wp.addToListFor(ListKey.TYPE, Type.getConstant("MARTIAL"));
		context.getReferenceContext().importObject(wp);

		largeSword = new Equipment();
		largeSword.setName("Longsword (Large)");
		largeSword.put(StringKey.KEY_NAME, "KEY_LONGSWORD_LARGE");
		largeSword.put(StringKey.OUTPUT_NAME, "Longsword (Large)");
		largeSword.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<>(wp));
		TestHelper.addType(largeSword, "Weapon.Melee.Martial.Standard.Slashing.Sword");
		largeSword.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		largeSword.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		largeSword.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 2);
		largeSword.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
				WieldCategory.class, "OneHanded"));
		largeSword.put(ObjectKey.SIZE, largeRef);
		largeSword.put(ObjectKey.BASESIZE, largeRef);

		fineSword = new Equipment();
		fineSword.setName("Longsword (Fine)");
		fineSword.put(StringKey.KEY_NAME, "KEY_LONGSWORD_FINE");
		fineSword.put(StringKey.OUTPUT_NAME, "Longsword (Fine)");
		fineSword.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<>(wp));
		TestHelper.addType(fineSword, "Weapon.Melee.Martial.Standard.Slashing.Sword.Finesseable");
		fineSword.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		fineSword.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		fineSword.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 2);
		fineSword.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
				WieldCategory.class, "OneHanded"));
		fineSword.put(ObjectKey.SIZE, mediumRef);
		fineSword.put(ObjectKey.BASESIZE, mediumRef);

		WeaponProf spearwp = new WeaponProf();
		spearwp.setName("Spear");
		spearwp.put(StringKey.KEY_NAME, "KEY_SPEAR");
		spearwp.addToListFor(ListKey.TYPE, Type.getConstant("MARTIAL"));
		context.getReferenceContext().importObject(spearwp);

		longSpear = new Equipment();
		longSpear.setName("Longspear");
		longSpear.put(StringKey.KEY_NAME, "KEY_LONGSPEAR");
		longSpear.put(StringKey.OUTPUT_NAME, "Longspear");
		longSpear.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<>(spearwp));
		TestHelper.addType(longSpear, "Weapon.Melee.Martial.Standard.Piercing.Spear");
		longSpear.getEquipmentHead(1).put(StringKey.DAMAGE, "1d6");
		longSpear.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		longSpear.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 1);
		longSpear.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
				WieldCategory.class, "TwoHanded"));
		longSpear.put(ObjectKey.SIZE, mediumRef);
		longSpear.put(ObjectKey.BASESIZE, mediumRef);
		longSpear.put(IntegerKey.REACH, 10);

		GameMode gm = SettingsHandler.getGameAsProperty().get();
		RuleCheck rc = new RuleCheck();
		rc.setName(RuleConstants.SIZECAT);
		gm.getModeContext().getReferenceContext().importObject(rc);
		SettingsHandler.setRuleCheck(RuleConstants.SIZECAT, true);
		gm.setWeaponReachFormula("(RACEREACH+(max(0,REACH-5)))*REACHMULT");

		wp = new WeaponProf();
		wp.setName("Silly Bite");
		wp.put(StringKey.KEY_NAME, "SillyBite");
		//wp.setTypeInfo("Weapon.Natural.Melee.Finesseable.Bludgeoning.Piercing.Slashing");
		wp.addToListFor(ListKey.TYPE, Type.NATURAL);
		context.getReferenceContext().importObject(wp);

		bite = new Equipment();
		bite.setName("Silly Bite");
		bite.put(StringKey.KEY_NAME, "SillyBite");
		bite.put(StringKey.OUTPUT_NAME, "Silly Bite (For Test)");
		TestHelper.addType(bite, "Weapon.Natural.Melee.Finesseable.Bludgeoning.Piercing.Slashing");
		bite.put(ObjectKey.WEIGHT, BigDecimal.ZERO);
		bite.put(ObjectKey.SIZE, mediumRef);
		bite.put(ObjectKey.BASESIZE, mediumRef);
		aBonus = Bonus.newBonus(context, "WEAPON|ATTACKS|" + 7);
		
		if (aBonus != null)
		{
			bite.addToListFor(ListKey.BONUS, aBonus);
		}
		bite.put(IntegerKey.SLOTS, 0);
		bite.setQty(Float.valueOf(1));
		bite.setNumberCarried(1.0f);
		bite.put(ObjectKey.ATTACKS_PROGRESS, false);
		bite.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		bite.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		bite.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 2);
		bite.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
				WieldCategory.class, "OneHanded"));
		bite.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<>(wp));

		longbow = new Equipment();
		longbow.setName("Longbow");
		TestHelper.addType(longbow, "Weapon.Martial.Ranged.Standard.Piercing.Container.Projectile.Bow.Longbow");
		longbow.put(ObjectKey.TOTAL_CAPACITY, Capacity.ANY);
		longbow.put(ObjectKey.CONTAINER_WEIGHT_CAPACITY, BigDecimal.ONE);
		longbow.addToListFor(ListKey.CAPACITY,
			new Capacity("Arrow", BigDecimal.ONE));
		longbow.setQty(Float.valueOf(1));
		longbow.setNumberCarried(1.0f);
		
		arrow = new Equipment();
		arrow.setName("Arrow");
		TestHelper.addType(arrow, "Ammunition.Standard.Arrow.Individual");
		
		
		// Wield categories
		WieldCategory twoHanded = context.getReferenceContext().silentlyGetConstructedCDOMObject(
				WieldCategory.class, "TwoHanded");
		twoHanded.setSizeDifference(1);

		// Equip mods
		EquipmentModifier eqMod = new EquipmentModifier();
		eqMod.setName("Plus 1 Enhancement");
		eqMod.put(StringKey.KEY_NAME, "PLUS1W");
		TestHelper.addType(eqMod, "Ammunition.Weapon");
		eqMod.put(IntegerKey.PLUS, 1);
		eqMod.addToListFor(ListKey.ITEM_TYPES, Type.getConstant("Enhancement"));
		eqMod.addToListFor(ListKey.ITEM_TYPES, Type.MAGIC);
		eqMod.addToListFor(ListKey.ITEM_TYPES, Type.getConstant("Plus1"));
		aBonus = Bonus.newBonus(context, "WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement");
		
		if (aBonus != null)
		{
			eqMod.addToListFor(ListKey.BONUS, aBonus);
		}
		context.getReferenceContext().importObject(eqMod);
		eqMod = new EquipmentModifier();
		eqMod.setName("Plus 2 Enhancement");
		eqMod.put(StringKey.KEY_NAME, "PLUS2W");
		TestHelper.addType(eqMod, "Ammunition.Weapon");
		eqMod.put(IntegerKey.PLUS, 2);
		eqMod.addToListFor(ListKey.ITEM_TYPES, Type.getConstant("Enhancement"));
		eqMod.addToListFor(ListKey.ITEM_TYPES, Type.MAGIC);
		eqMod.addToListFor(ListKey.ITEM_TYPES, Type.getConstant("Plus2"));
		aBonus = Bonus.newBonus(context, "WEAPON|DAMAGE,TOHIT|2|TYPE=Enhancement");
		
		if (aBonus != null)
		{
			eqMod.addToListFor(ListKey.BONUS, aBonus);
		}
		context.getReferenceContext().importObject(eqMod);
		eqMod = new EquipmentModifier();
		eqMod.setName("Masterwork");
		eqMod.put(StringKey.KEY_NAME, "MWORKW");
		TestHelper.addType(eqMod, "Ammunition.Weapon");
		eqMod.addToListFor(ListKey.ITEM_TYPES, Type.MASTERWORK);
		aBonus = Bonus.newBonus(context, "WEAPON|TOHIT|1|TYPE=Enhancement");
		
		if (aBonus != null)
		{
			eqMod.addToListFor(ListKey.BONUS, aBonus);
		}
		context.getReferenceContext().importObject(eqMod);
		
		PCTemplate pct = new PCTemplate();
		context.unconditionallyProcess(pct, "AUTO",
				"WEAPONPROF|KEY_Sword (Bastard)|KEY_LONGSWORD|SillyBite");
		character.addTemplate(pct);
		
		wpnBonusPct = new PCTemplate();
		context.unconditionallyProcess(wpnBonusPct, "BONUS",
				"WEAPONPROF=DoubleWpn|DAMAGE|3");
		context.unconditionallyProcess(wpnBonusPct, "BONUS",
				"WEAPONPROF=DoubleWpn|TOHIT|4");
		
		wpnBonusAbility = TestHelper.makeAbility("FEAT_BONUS", BuildUtilities.getFeatCat(), "General");
		context.unconditionallyProcess(wpnBonusAbility, "BONUS",
				"WEAPONPROF=DoubleWpn|DAMAGE|1");
		context.unconditionallyProcess(wpnBonusAbility, "BONUS",
				"WEAPONPROF=DoubleWpn|TOHIT|2");

		assertTrue(context.getReferenceContext().resolveReferences(null));
	}

	@AfterEach
	@Override
	protected void tearDown() throws Exception
	{
		dblWpn = null;
		str.removeListFor(ListKey.BONUS);
		intel.removeListFor(ListKey.BONUS);

		super.tearDown();
	}

	/**
	 * Test the processing of double weapons on a medium creature. All output
	 * tags are checked.
	 */
	@Test
	public void testDoubleWeapon()
	{
		PlayerCharacter character = getCharacter();

		WeaponToken token = new WeaponToken();

		// First test each sub token
		assertEquals("*DoubleWpn", token.getToken("WEAPON.0.NAME",
			character, null), "Name");
		assertEquals("*DoubleWpn (Head 1 only)", token.getToken(
			"WEAPON.1.NAME", character, null), "Name-H1");
		assertEquals("*DoubleWpn (Head 2 only)", token.getToken(
			"WEAPON.2.NAME", character, null), "Name-H2");

		LocaleDependentTestCase.before(Locale.US);
		assertEquals("Two-Weapons", token.getToken("WEAPON.0.HAND",
			character, null), "Hand");
		assertEquals("Two-Weapons", token.getToken("WEAPON.1.HAND",
			character, null), "Hand-H1");
		assertEquals("Two-Weapons", token.getToken("WEAPON.2.HAND",
			character, null), "Hand-H2");
		EnUsLocaleDependentTestCase.after();

		//	1H-P
		assertEquals("+14/+9/+4/-1", token.getToken(
			"WEAPON.0.BASEHIT", character, null), "1H-P - BASEHIT");
		assertNull(token.getToken(
				"WEAPON.1.BASEHIT", character, null), "1H-P - BASEHIT-H1");
		assertNull(token.getToken(
				"WEAPON.2.BASEHIT", character, null), "1H-P - BASEHIT-H2");

		//	1H-O
		assertEquals("+10/+5/+0/-5", token.getToken(
			"WEAPON.0.OHHIT", character, null), "1H-O - OHHIT");
		assertNull(token.getToken("WEAPON.1.OHHIT",
				character, null
		), "1H-O - OHHIT-H1");
		assertNull(token.getToken("WEAPON.2.OHHIT",
				character, null
		), "1H-O - OHHIT-H2");

		//	2H
		assertEquals("+14/+9/+4/-1", token.getToken(
			"WEAPON.0.THHIT", character, null), "2H - THHIT");
		assertEquals("+14/+9/+4/-1", token.getToken(
			"WEAPON.1.THHIT", character, null), "2H - THHIT-H1");
		assertEquals("+14/+9/+4/-1", token.getToken(
			"WEAPON.2.THHIT", character, null), "2H - THHIT-H2");

		//	2W-P-(OH)
		assertEquals("+8/+3/-2/-7", token.getToken(
			"WEAPON.0.TWPHITH", character, null), "2W-P-(OH) - TWPHITH");
		assertNull(token.getToken(
				"WEAPON.1.TWPHITH", character, null), "2W-P-(OH) - TWPHITH-H1");
		assertNull(token.getToken(
				"WEAPON.2.TWPHITH", character, null), "2W-P-(OH) - TWPHITH-H2");

		//	2W-P-(OL)
		assertEquals("+10/+5/+0/-5", token.getToken(
			"WEAPON.0.TWPHITL", character, null), "2W-P-(OL) - TWPHITL");
		assertEquals("+10/+5/+0/-5", token.getToken(
			"WEAPON.1.TWPHITL", character, null), "2W-P-(OL) - TWPHITL-H1");
		assertEquals("+10/+5/+0/-5", token.getToken(
			"WEAPON.2.TWPHITL", character, null), "2W-P-(OL) - TWPHITL-H2");

		//	2W-OH
		assertEquals("+6/+1/-4/-9;+6", token.getToken(
			"WEAPON.0.TWOHIT", character, null), "2W-OH - TWOHIT");
		assertEquals("+6", token.getToken(
			"WEAPON.1.TWOHIT", character, null), "2W-OH - TWOHIT-H1");
		assertEquals("+6", token.getToken(
			"WEAPON.2.TWOHIT", character, null), "2W-OH - TWOHIT-H2");

		//	1H-P / 2W-P-(OH) / 2W-P-(OL)
		assertEquals("1d10+2", token.getToken(
			"WEAPON.0.BASICDAMAGE", character, null), "1H-P - BASICDAMAGE");
		assertEquals("1d10+2", token.getToken(
			"WEAPON.1.BASICDAMAGE", character, null), "1H-P - BASICDAMAGE-H1");
		assertEquals("1d6+2", token.getToken(
			"WEAPON.2.BASICDAMAGE", character, null), "1H-P - BASICDAMAGE-H2");

		//	1H-O / 2W-OH
		assertEquals("1d10+1", token.getToken(
			"WEAPON.0.OHDAMAGE", character, null), "1H-O - OHDAMAGE");
		assertEquals("1d10+1", token.getToken(
			"WEAPON.1.OHDAMAGE", character, null), "1H-O - OHDAMAGE-H1");
		assertEquals("1d6+1", token.getToken(
			"WEAPON.2.OHDAMAGE", character, null), "1H-O - OHDAMAGE-H2");

		//	2H
		assertEquals("1d10+3", token.getToken(
			"WEAPON.0.THDAMAGE", character, null), "2H - THDAMAGE");
		assertEquals("1d10+3", token.getToken(
			"WEAPON.1.THDAMAGE", character, null), "2H - THDAMAGE-H1");
		assertEquals("1d6+3", token.getToken(
			"WEAPON.2.THDAMAGE", character, null), "2H - THDAMAGE-H2");

		//	Double
		assertEquals("+6/+1/-4/-9;+6", token.getToken(
			"WEAPON.0.TOTALHIT", character, null), "2H - TOTALHIT");
		assertEquals("+6", token.getToken(
			"WEAPON.0.TOTALHIT.0", character, null), "2H - TOTALHIT.0");
		assertEquals("+1", token.getToken(
			"WEAPON.0.TOTALHIT.1", character, null), "2H - TOTALHIT.1");
		assertEquals("+14", token.getToken(
			"WEAPON.1.TOTALHIT.0", character, null), "1H-P - TOTALHIT.0");
		assertEquals("+9", token.getToken(
			"WEAPON.1.TOTALHIT.1", character, null), "1H-P - TOTALHIT.1");
		assertEquals("1d10+3", token.getToken(
			"WEAPON.0.THDAMAGE", character, null), "2H - THDAMAGE");

	}

	/**
	 * Test the processing of double weapons with enhancements on a medium
	 * creature.
	 */
	@Test
	public void testEnhancedDoubleWeapon()
	{
		PlayerCharacter character = getCharacter();

		WeaponToken token = new WeaponToken();
		// Test magical enhancements to the double weapon H1:+1, H2:+2
		dblWpn.addEqModifiers("MWORKW.PLUS1W", true);
		dblWpn.addEqModifiers("MWORKW.PLUS2W", false);
		assertEquals("+15/+10/+5/+0", token.getToken(
			"WEAPON.1.THHIT", character, null), "2H - THHIT-H1 [+1]");
		assertEquals("+16/+11/+6/+1", token.getToken(
			"WEAPON.2.THHIT", character, null), "2H - THHIT-H2 [+2]");
		assertEquals("1d10+4", token.getToken(
			"WEAPON.1.THDAMAGE", character, null), "2H - THDAMAGE-H1 [+1]");
		assertEquals("1d6+5", token.getToken(
			"WEAPON.2.THDAMAGE", character, null), "2H - THDAMAGE-H2 [+2]");
	}

	/**
	 * Test the processing of a bastard sword on a medium creature without the
	 * exotic weapon proficiency. It should not be able to be wielded one handed.<br/>
	 * This is based on the text from the DnD FAQ v20060621 on p32 which states 
	 * "Treat ... these weapons as two-handed weapons when determining who can 
	 * use them and how." when talking about bastard swords for weilders without 
	 * the exotic weapon proficiency.
	 */
	@Test
	public void testBastardSword()
	{
		PlayerCharacter character = getCharacter();
		assertEquals(
				"KEY_Sword (Bastard)", bastardSword.get(ObjectKey.WEAPON_PROF).get().getKeyName(),
				"2-handed prof should be martial");

		EquipSet es =
				new EquipSet("0.1.2", "Sword (Bastard)",
					bastardSword.getName(), bastardSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Now test the output to ensure it is correct
		WeaponToken token = new WeaponToken();
		assertEquals("Sword, Bastard", token.getToken("WEAPON.3.NAME",
			character, null), "Name");
		assertNull(
				token.getToken("WEAPON.3.BASEHIT", character, null),
				"Not possible to weild the bastard sword one handed."
		);
		assertEquals(
				"+18/+13/+8/+3", token.getToken("WEAPON.3.THHIT", character, null),
				"No penalty to weild the bastard sword two handed.");
	}

	/**
	 * Test the processing of a large sword on a medium creature. It
	 * should be forced to be wielded two handed. Note: Size penalties are not
	 * included in the data prepared, so are not included in the calculations.
	 */
	@Test
	public void testLargeLongSword()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("KEY_LONGSWORD", largeSword
				.get(ObjectKey.WEAPON_PROF).get().getKeyName(), "Prof should be longsword");

		EquipSet es =
				new EquipSet("0.1.3", "Longsword (Large)",
					largeSword.getName(), largeSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Now test the output to ensure it is correct
		WeaponToken token = new WeaponToken();
		assertEquals("Longsword (Large)", token.getToken(
			"WEAPON.3.NAME", character, null), "Large sword - name");
		assertEquals(
				"+18/+13/+8/+3", token.getToken("WEAPON.3.THHIT", character, null),
				"Large sword - Two handed should be fine");
		assertNull(token
				.getToken("WEAPON.3.BASEHIT", character, null), "Large sword - can't be wielded one handed");
	}

	/**
	 * Test the processing of a large sword on a medium creature. It
	 * should be forced to be wielded two handed. Note: Size penalties are not
	 * included in the data prepared, so are not included in the calculations.
	 */
	@Test
	public void testLargeWpnBonus()
	{
		PlayerCharacter character = getCharacter();
		LoadContext context = Globals.getContext();
		assertEquals("KEY_LONGSWORD", largeSword
				.get(ObjectKey.WEAPON_PROF).get().getKeyName(), "Prof should be longsword");

		assertTrue(character
			.isProficientWith(largeSword), "Character should be proficient with longsword");

		PCTemplate longswordTemplate = new PCTemplate();
		longswordTemplate.setName("LS Bonus");
		BonusObj aBonus = Bonus.newBonus(context, "WEAPONPROF=KEY_LONGSWORD|PCSIZE|1");
		
		if (aBonus != null)
		{
			longswordTemplate.addToListFor(ListKey.BONUS, aBonus);
		}
		character.addTemplate(longswordTemplate);

		character.addEquipment(largeSword);
		EquipSet es =
				new EquipSet("0.1.3", "Longsword (Large)",
					largeSword.getName(), largeSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Test weapon profs effects on large weapons
		WeaponToken token = new WeaponToken();
		assertEquals(
				"+18/+13/+8/+3", token
				.getToken("WEAPON.3.BASEHIT", character, null),
				"Large sword - can be wielded one handed with template weapon size bonus");
		character.removeTemplate(longswordTemplate);
		character.calcActiveBonuses();
		assertNull(token
				.getToken("WEAPON.3.BASEHIT", character, null), "Large sword - can't be wielded one handed");

		PCTemplate martialTemplate = new PCTemplate();
		martialTemplate.setName("Martial Bonus");
		aBonus = Bonus.newBonus(context, "WEAPONPROF=TYPE.Martial|PCSIZE|1");
		
		if (aBonus != null)
		{
			martialTemplate.addToListFor(ListKey.BONUS, aBonus);
		}
		character.addTemplate(martialTemplate);
		assertEquals(
				"+18/+13/+8/+3", token
				.getToken("WEAPON.3.BASEHIT", character, null),
				"Large sword - can be wielded one handed with template weapon type size bonus");

	}

	/**
	 * Test natural weapons
	 */
	@Test
	public void testNaturalWeapon()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("SillyBite", bite
				.get(ObjectKey.WEAPON_PROF).get().getKeyName(), "Prof should be SillyBite");

		EquipSet es =
				new EquipSet("0.1.3", "Bite Attack", bite.getName(), bite);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Now test the output to ensure it is correct
		WeaponToken token = new WeaponToken();
		assertEquals(
				"+18/+18/+18/+18/+18/+18/+18/+18", token.getToken(
				"WEAPON.3.BASEHIT", character, null), "Silly Bite - Basic To Hit");
		assertEquals(
				"+18/+18/+18/+18/+18/+18/+18/+18", token.getToken(
				"WEAPON.3.TOTALHIT", character, null), "Silly Bite - Total To Hit");
		assertEquals("+18", token
			.getToken("WEAPON.3.TOTALHIT.0", character, null), "Silly Bite - Total To Hit first attack");
	}

	/**
	 * Test the processing of a finesseable weapon both with and without weapon finesse
	 * and temporary bonuses.
	 */
	@Test
	public void testWpnFinesse()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("KEY_LONGSWORD", fineSword
				.get(ObjectKey.WEAPON_PROF).get().getKeyName(), "Prof should be longsword");
		LoadContext context = Globals.getContext();

		character.addEquipment(fineSword);
		EquipSet es =
				new EquipSet("0.1.3", "Longsword (Fine)", fineSword.getName(),
					fineSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Test weapon profs effects on large weapons
		WeaponToken token = new WeaponToken();
		assertEquals("+18/+13/+8/+3", token.getToken(
			"WEAPON.3.BASEHIT", character, null), "Fine sword");

		// Now apply weapon finess and check dex is used rather than str
		Ability wpnFinesse = new Ability();
		wpnFinesse.setName("Weapon Finesse");
		wpnFinesse.setCDOMCategory(BuildUtilities.getFeatCat());
		wpnFinesse.put(StringKey.KEY_NAME, "Weapon Finesse");
		final BonusObj wfBonus =
				Bonus
					.newBonus(context, "COMBAT|TOHIT.Finesseable|((max(STR,DEX)-STR)+SHIELDACCHECK)|TYPE=NotRanged");
		wpnFinesse.addToListFor(ListKey.BONUS, wfBonus);
		addAbility(BuildUtilities.getFeatCat(), wpnFinesse);
		assertEquals("+19/+14/+9/+4", token.getToken(
			"WEAPON.3.BASEHIT", character, null), "Fine sword");

		// Add a temp penalty to dex and check that it is applied
		character.setUseTempMods(true);
		Spell spell2 = new Spell();
		spell2.setName("Concrete Boots");
		final BonusObj aBonus = Bonus.newBonus(context, "STAT|DEX|-4");
		
		if (aBonus != null)
		{
			spell2.addToListFor(ListKey.BONUS, aBonus);
		}
		BonusObj penalty = spell2.getRawBonusList(character).get(0);
		character.addTempBonus(penalty, spell2, character);
		character.calcActiveBonuses();
		assertEquals("+18/+13/+8/+3", token.getToken(
			"WEAPON.3.BASEHIT", character, null), "Fine sword");
	}

	@Test
	public void testWpnReach()
	{
		LoadContext context = Globals.getContext();
		PlayerCharacter character = getCharacter();
		character.addEquipment(largeSword);
		EquipSet es =
				new EquipSet("0.1.3", "Large Sword", largeSword.getName(),
					largeSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		WeaponToken token = new WeaponToken();
		assertEquals(
				"5", token.getToken("WEAPON.3.REACH", character, null),
				"Reach for a non-reach weapon on a character with normal reach");

		character.addEquipment(longSpear);
		es = new EquipSet("0.1.4", "Longspear", longSpear.getName(), longSpear);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// note: longspear ends up inserted before the large sword above, hence we use weapon.3
		assertEquals(
				"10", token.getToken("WEAPON.3.REACH", character, null),
				"Reach for a reach weapon (10') on a character with normal reach");

		// set reach multiplier on the large sword to 2 and retest
		largeSword.put(IntegerKey.REACH_MULT, 2);
		assertEquals(
				"10", token.getToken("WEAPON.4.REACH", character, null),
				"Reach for a reach multiple weapon on a character with normal reach");
		
		// Check we can bonus the reach
		PCTemplate lsReachTemplate = new PCTemplate();
		lsReachTemplate.setName("LongSpear Long Arm");
		BonusObj aBonus = Bonus.newBonus(context, "WEAPONPROF=KEY_SPEAR|REACH|5");
		if (aBonus != null)
		{
			lsReachTemplate.addToListFor(ListKey.BONUS, aBonus);
		}
		character.addTemplate(lsReachTemplate);
		assertEquals(
				"15", token.getToken("WEAPON.3.REACH", character, null),
				"Reach for a reach weapon (10') on a character with bonus for the proficiency");
		
	}
	
	/**
	 * test the AMUNITION count sub token
	 */
	@Test
	public void testAmmunition()
	{
		PlayerCharacter character = getCharacter();
		character.addEquipment(largeSword);
		EquipSet es =
				new EquipSet("0.1.3", "Large Sword", largeSword.getName(),
					largeSword);
		character.addEquipSet(es);
		character.addEquipment(longbow);
		es =
			new EquipSet("0.1.4", "Longbow", longbow.getName(),
				longbow);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		WeaponToken token = new WeaponToken();
		assertEquals(largeSword.getName(),
			token.getToken("WEAPON.4.NAME", character, null), "non-ammo weapon"
		);
		assertEquals(longbow.getName(),
			token.getToken("WEAPON.3.NAME", character, null), "Ammo weapon"
		);

		assertEquals("0",
			token.getToken("WEAPON.4.AMMUNITION", character, null), "Ammo count for a non-ammo weapon"
		);
		String result = token.getToken("WEAPON.3.AMMUNITION", character, null);
		assertEquals("0", result, "Ammo count for an empty ammo weapon");
		
		character.addEquipment(arrow);
		es =
			new EquipSet("0.1.4.1", "Arrow", arrow.getName(),
				arrow);
		character.addEquipSet(es);
		character.setCalcEquipmentList();
		result = token.getToken("WEAPON.3.AMMUNITION", character, null);
		assertEquals(
				"1", result, "Ammo count for longbow with one arrow");
		
	}
	
	/**
	 * Test a two handed weapon that is equipped.
	 */
	@Test
	public void testTwohandedEquipped()
	{
		PlayerCharacter character = getCharacter();
		character.addEquipment(longSpear);
		EquipSet es =
				new EquipSet("0.1.3", "Longspear", longSpear.getName(),
					longSpear);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		WeaponToken token = new WeaponToken();
		assertEquals(longSpear.getName(),
			token.getToken("WEAPON.3.NAME", character, null), "weapon name"
		);
		assertEquals("+14/+9/+4/-1",
			token.getToken("WEAPON.3.THHIT", character, null), "weapon name"
		);
		assertEquals("+14/+9/+4/-1",
			token.getToken("WEAPON.3.TOTALHIT", character, null), "weapon name"
		);
	}	
	
	/**
	 * Test a two handed weapon that is not equipped.
	 */
	@Test
	public void testTwohandedNotequipped()
	{
		PlayerCharacter character = getCharacter();
		character.addEquipment(fineSword);
		LocaleDependentTestCase.before(Locale.US);
		EquipSet es =
				new EquipSet("0.1.3", EquipmentLocation.EQUIPPED_PRIMARY.toString(), fineSword.getName(),
					fineSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		character.addEquipment(longSpear);
		es =
				new EquipSet("0.1.4", EquipmentLocation.NOT_CARRIED.toString(), longSpear.getName(),
					longSpear);
		character.addEquipSet(es);
		es.equipItem(character);
		character.setCalcEquipmentList();
		character.preparePCForOutput();

		WeaponToken token = new WeaponToken();
		assertEquals(longSpear.getName(),
			token.getToken("WEAPON.3.NAME", character, null), "weapon name"
		);
		assertEquals("+14/+9/+4/-1",
			token.getToken("WEAPON.3.THHIT", character, null), "weapon name"
		);
		assertEquals("+14/+9/+4/-1",
			token.getToken("WEAPON.3.TOTALHIT", character, null), "weapon name"
		);
		LocaleDependentTestCase.after();
		
	}	
	
	/**
	 * Check the FEATHIT and FEATDAMAGE weapon subtokens.
	 */
	@Test
	public void testFeatBonus()
	{
		PlayerCharacter character = getCharacter();
		WeaponToken token = new WeaponToken();
		assertEquals(dblWpn.getName(),
			token.getToken("WEAPON.0.NAME.NOSTAR", character, null), "weapon name"
		);
		assertEquals("+0",
			token.getToken("WEAPON.0.FEATHIT", character, null), "feat tohit bonus, before adding"
		);
		assertEquals("+0",
			token.getToken("WEAPON.0.FEATDAMAGE", character, null), "feat damage bonus, before adding"
		);
		
		addAbility(BuildUtilities.getFeatCat(), wpnBonusAbility);
		character.calcActiveBonuses();
		assertEquals("+2",
			token.getToken("WEAPON.0.FEATHIT", character, null), "feat tohit bonus, after adding"
		);
		assertEquals("+1",
			token.getToken("WEAPON.0.FEATDAMAGE", character, null), "feat damage bonus, after adding"
		);
	}
	
	/**
	 * Check the TEMPLATEHIT and TEMPLATEDAMAGE weapon subtokens.
	 */
	@Test
	public void testTemplateBonus()
	{
		PlayerCharacter character = getCharacter();
		WeaponToken token = new WeaponToken();
		assertEquals(dblWpn.getName(),
			token.getToken("WEAPON.0.NAME.NOSTAR", character, null), "weapon name"
		);
		assertEquals("+0",
			token.getToken("WEAPON.0.TEMPLATEHIT", character, null), "feat tohit bonus, before adding"
		);
		assertEquals("+0",
			token.getToken("WEAPON.0.TEMPLATEDAMAGE", character, null), "feat damage bonus, before adding"
		);
		
		character.addTemplate(wpnBonusPct);
		character.calcActiveBonuses();
		assertEquals("+4",
			token.getToken("WEAPON.0.TEMPLATEHIT", character, null), "feat tohit bonus, after adding"
		);
		assertEquals("+3",
			token.getToken("WEAPON.0.TEMPLATEDAMAGE", character, null), "feat damage bonus, after adding"
		);
	}
}
