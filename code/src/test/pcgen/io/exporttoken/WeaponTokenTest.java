/*
 * WeaponTokenTest.java
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
 *
 * Created on Dec 11, 2004
 *
 * $Id$
 *
 */
package pcgen.io.exporttoken;

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.EquipmentModifier;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.RuleCheck;
import pcgen.core.SettingsHandler;
import pcgen.core.WeaponProf;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.EquipSet;
import pcgen.core.character.WieldCategory;
import pcgen.core.spell.Spell;

/**
 * <code>WeaponTokenTest</code> contains tests to verify that the
 * WEAPON token is working correctly.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class WeaponTokenTest extends AbstractCharacterTestCase
{
	private Equipment dblWpn = null;
	private Equipment bastardSword = null;
	private Equipment largeSword = null;
	private Equipment fineSword = null;
	private Equipment longSpear = null;
	private Equipment bite = null;

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(WeaponTokenTest.class);
	}

	/**
	 * Basic constructor, name only.
	 * @param name The name of the test class.
	 */
	public WeaponTokenTest(String name)
	{
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		GameMode gamemode = SettingsHandler.getGame();
		PlayerCharacter character = getCharacter();

		//Stats
		setPCStat(character, "STR", 15);
		setPCStat(character, "DEX", 16);
		setPCStat(character, "INT", 17);
		PCStat stat = character.getStatList().getStatAt(0);
		stat.setStatMod("floor(SCORE/2)-5");
		stat.addBonusList("COMBAT|TOHIT.Melee|STR|TYPE=Ability");
		stat.addBonusList("DAMAGE|TYPE.Melee,TYPE.Thrown|STR");
		stat.addBonusList("COMBAT|DAMAGEMULT:0|0.5*(STR>=0)");
		stat.addBonusList("COMBAT|DAMAGEMULT:1|1");
		stat.addBonusList("COMBAT|DAMAGEMULT:2|1.5*(STR>=0)");
		stat.addVariable(0, "OFFHANDLIGHTBONUS", "2");

		stat = character.getStatList().getStatAt(3);
		stat.addBonusList("MODSKILLPOINTS|NUMBER|INT");

		// Race
		Race testRace = new Race();
		testRace.setName("TestRace");
		testRace.setKeyName("KEY_TEST_RACE");
		testRace.put(FormulaKey.SIZE, new FixedSizeFormula(gamemode
				.getSizeAdjustmentNamed("Medium")));
		character.setRace(testRace);

		// Class
		PCClass myClass = new PCClass();
		myClass.setName("My Class");
		myClass.setKeyName("KEY_MY_CLASS");
		myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
		final BonusObj babClassBonus = Bonus.newBonus("1|COMBAT|BAB|CL+15");
		myClass.addBonusList(babClassBonus);
		Globals.getClassList().add(myClass);
		character.incrementClassLevel(1, myClass, true);

		character.calcActiveBonuses();

		dblWpn = new Equipment();
		dblWpn.setName("DoubleWpn");
		dblWpn.setKeyName("KEY_DOUBLE_WPN");
		dblWpn
			.setTypeInfo("Weapon.Melee.Martial.Double.Standard.Bludgeoning.Flail");
		dblWpn.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		dblWpn.getEquipmentHead(2).put(StringKey.DAMAGE, "1d6");
		dblWpn.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		dblWpn.getEquipmentHead(2).put(IntegerKey.CRIT_MULT, 2);
		dblWpn.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 1);
		dblWpn.getEquipmentHead(2).put(IntegerKey.CRIT_RANGE, 1);
		dblWpn.put(IntegerKey.SLOTS, 2);
		dblWpn.put(ObjectKey.WIELD, WieldCategory.findByName("TwoHanded"));
		dblWpn.setSize("M", true);
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
		wp.put(IntegerKey.HANDS, Constants.HANDS_SIZEDEPENDENT);
		Globals.addWeaponProf(wp);

		wp = new WeaponProf();
		wp.setName("Sword (Bastard)");
		wp.setKeyName("KEY_Sword (Bastard)");
		wp.setTypeInfo("MARTIAL.EXOTIC");
		Globals.addWeaponProf(wp);
		character.addWeaponProf(wp.getKeyName());

		bastardSword = new Equipment();
		bastardSword.setName("Sword, Bastard");
		bastardSword.setKeyName("BASTARD_SWORD");
		bastardSword.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<WeaponProf>(wp));
		bastardSword
			.setTypeInfo("Weapon.Melee.Martial.Exotic.Standard.Slashing.Sword");
		bastardSword.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		bastardSword.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		bastardSword.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 2);
		bastardSword.put(ObjectKey.WIELD, WieldCategory.findByName("TwoHanded"));
		bastardSword.setSize("M", true);

		wp = new WeaponProf();
		wp.setName("Longsword");
		wp.setKeyName("KEY_LONGSWORD");
		wp.setTypeInfo("MARTIAL");
		Globals.addWeaponProf(wp);
		character.addWeaponProf(wp.getKeyName());

		largeSword = new Equipment();
		largeSword.setName("Longsword (Large)");
		largeSword.setKeyName("KEY_LONGSWORD_LARGE");
		largeSword.setOutputName("Longsword (Large)");
		largeSword.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<WeaponProf>(wp));
		largeSword.setTypeInfo("Weapon.Melee.Martial.Standard.Slashing.Sword");
		largeSword.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		largeSword.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		largeSword.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 2);
		largeSword.put(ObjectKey.WIELD, WieldCategory.findByName("OneHanded"));
		largeSword.setSize("L", true);

		fineSword = new Equipment();
		fineSword.setName("Longsword (Fine)");
		fineSword.setKeyName("KEY_LONGSWORD_FINE");
		fineSword.setOutputName("Longsword (Fine)");
		fineSword.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<WeaponProf>(wp));
		fineSword
			.setTypeInfo("Weapon.Melee.Martial.Standard.Slashing.Sword.Finesseable");
		fineSword.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		fineSword.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		fineSword.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 2);
		fineSword.put(ObjectKey.WIELD, WieldCategory.findByName("OneHanded"));
		fineSword.setSize("M", true);

		longSpear = new Equipment();
		longSpear.setName("Longspear");
		longSpear.setKeyName("KEY_LONGSPEAR");
		longSpear.setOutputName("Longspear");
		longSpear.setTypeInfo("Weapon.Melee.Martial.Standard.Piercing.Spear");
		longSpear.getEquipmentHead(1).put(StringKey.DAMAGE, "1d6");
		longSpear.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		longSpear.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 1);
		longSpear.put(ObjectKey.WIELD, WieldCategory.findByName("TwoHanded"));
		longSpear.setSize("M", true);
		longSpear.put(IntegerKey.REACH, 10);

		GameMode gm = SettingsHandler.getGame();
		RuleCheck rc = new RuleCheck();
		rc.setName("SIZECAT");
		gm.addRule(rc);
		SettingsHandler.setRuleCheck("SIZECAT", true);
		gm.setWCStepsFormula("EQUIP.SIZE.INT-PC.SIZE.INT");
		gm.setWeaponReachFormula("(RACEREACH+(max(0,REACH-5)))*REACHMULT");

		wp = new WeaponProf();
		wp.setName("Silly Bite");
		wp.setKeyName("SillyBite");
		//wp.setTypeInfo("Weapon.Natural.Melee.Finesseable.Bludgeoning.Piercing.Slashing");
		wp.setTypeInfo("Natural");
		Globals.addWeaponProf(wp);
		character.addWeaponProf(wp.getKeyName());

		bite = new Equipment();
		bite.setName("Silly Bite");
		bite.setKeyName("SillyBite");
		bite.setOutputName("Silly Bite (For Test)");
		bite
			.setTypeInfo("Weapon.Natural.Melee.Finesseable.Bludgeoning.Piercing.Slashing");
		bite.put(ObjectKey.WEIGHT, BigDecimal.ZERO);
		bite.setSize("M", true);
		bite.addBonusList("WEAPON|ATTACKS|" + 7);
		bite.setOnlyNaturalWeapon(false);
		bite.put(IntegerKey.SLOTS, 0);
		bite.setQty(Float.valueOf(1));
		bite.setNumberCarried(Float.valueOf(1));
		bite.put(ObjectKey.ATTACKS_PROGRESS, false);
		bite.getEquipmentHead(1).put(StringKey.DAMAGE, "1d10");
		bite.getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
		bite.getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 2);
		bite.put(ObjectKey.WIELD, WieldCategory.findByName("OneHanded"));
		bite.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<WeaponProf>(wp));

		// Weild categories
		WieldCategory wCat1h = new WieldCategory("OneHanded");
		wCat1h.setHands(1);
		wCat1h.setSizeDiff(0);
		gm.addWieldCategory(wCat1h);
		WieldCategory wCat2h = new WieldCategory("TwoHanded");
		wCat2h.setHands(2);
		wCat2h.setSizeDiff(1);
		gm.addWieldCategory(wCat2h);
		wCat1h.addSwitchMap("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1",
			"TwoHanded");

		// Equip mods
		EquipmentModifier eqMod = new EquipmentModifier();
		eqMod.setName("Plus 1 Enhancement");
		eqMod.setKeyName("PLUS1W");
		eqMod.setTypeInfo("Ammunition.Weapon");
		eqMod.put(IntegerKey.PLUS, 1);
		eqMod.addToListFor(ListKey.ITEM_TYPES, "Enhancement");
		eqMod.addToListFor(ListKey.ITEM_TYPES, "Magic");
		eqMod.addToListFor(ListKey.ITEM_TYPES, "Plus1");
		eqMod.addBonusList("WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement");
		EquipmentList.addEquipmentModifier(eqMod);
		eqMod = new EquipmentModifier();
		eqMod.setName("Plus 2 Enhancement");
		eqMod.setKeyName("PLUS2W");
		eqMod.setTypeInfo("Ammunition.Weapon");
		eqMod.put(IntegerKey.PLUS, 2);
		eqMod.addToListFor(ListKey.ITEM_TYPES, "Enhancement");
		eqMod.addToListFor(ListKey.ITEM_TYPES, "Magic");
		eqMod.addToListFor(ListKey.ITEM_TYPES, "Plus2");
		eqMod.addBonusList("WEAPON|DAMAGE,TOHIT|2|TYPE=Enhancement");
		EquipmentList.addEquipmentModifier(eqMod);
		eqMod = new EquipmentModifier();
		eqMod.setName("Masterwork");
		eqMod.setKeyName("MWORKW");
		eqMod.setTypeInfo("Ammunition.Weapon");
		eqMod.addToListFor(ListKey.ITEM_TYPES, "Masterwork");
		eqMod.addBonusList("WEAPON|TOHIT|1|TYPE=Enhancement");
		EquipmentList.addEquipmentModifier(eqMod);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		dblWpn = null;

		super.tearDown();
	}

	/**
	 * Test the processing of double weapons on a medium creature. All output
	 * tags are checked.
	 */
	public void testDoubleWeapon()
	{
		PlayerCharacter character = getCharacter();

		WeaponToken token = new WeaponToken();

		// First test each sub token
		assertEquals("Name", "*DoubleWpn", token.getToken("WEAPON.0.NAME",
			character, null));
		assertEquals("Name-H1", "*DoubleWpn (Head 1 only)", token.getToken(
			"WEAPON.1.NAME", character, null));
		assertEquals("Name-H2", "*DoubleWpn (Head 2 only)", token.getToken(
			"WEAPON.2.NAME", character, null));

		assertEquals("Hand", "Two-Weapons", token.getToken("WEAPON.0.HAND",
			character, null));
		assertEquals("Hand-H1", "Two-Weapons", token.getToken("WEAPON.1.HAND",
			character, null));
		assertEquals("Hand-H2", "Two-Weapons", token.getToken("WEAPON.2.HAND",
			character, null));

		//	1H-P
		assertEquals("1H-P - BASEHIT", "+14/+9/+4/-1", token.getToken(
			"WEAPON.0.BASEHIT", character, null));
		assertEquals("1H-P - BASEHIT-H1", null, token.getToken(
			"WEAPON.1.BASEHIT", character, null));
		assertEquals("1H-P - BASEHIT-H2", null, token.getToken(
			"WEAPON.2.BASEHIT", character, null));

		//	1H-O
		assertEquals("1H-O - OHHIT", "+10/+5/+0/-5", token.getToken(
			"WEAPON.0.OHHIT", character, null));
		assertEquals("1H-O - OHHIT-H1", null, token.getToken("WEAPON.1.OHHIT",
			character, null));
		assertEquals("1H-O - OHHIT-H2", null, token.getToken("WEAPON.2.OHHIT",
			character, null));

		//	2H
		assertEquals("2H - THHIT", "+14/+9/+4/-1", token.getToken(
			"WEAPON.0.THHIT", character, null));
		assertEquals("2H - THHIT-H1", "+14/+9/+4/-1", token.getToken(
			"WEAPON.1.THHIT", character, null));
		assertEquals("2H - THHIT-H2", "+14/+9/+4/-1", token.getToken(
			"WEAPON.2.THHIT", character, null));

		//	2W-P-(OH)
		assertEquals("2W-P-(OH) - TWPHITH", "+8/+3/-2/-7", token.getToken(
			"WEAPON.0.TWPHITH", character, null));
		assertEquals("2W-P-(OH) - TWPHITH-H1", null, token.getToken(
			"WEAPON.1.TWPHITH", character, null));
		assertEquals("2W-P-(OH) - TWPHITH-H2", null, token.getToken(
			"WEAPON.2.TWPHITH", character, null));

		//	2W-P-(OL)
		assertEquals("2W-P-(OL) - TWPHITL", "+10/+5/+0/-5", token.getToken(
			"WEAPON.0.TWPHITL", character, null));
		assertEquals("2W-P-(OL) - TWPHITL-H1", "+10/+5/+0/-5", token.getToken(
			"WEAPON.1.TWPHITL", character, null));
		assertEquals("2W-P-(OL) - TWPHITL-H2", "+10/+5/+0/-5", token.getToken(
			"WEAPON.2.TWPHITL", character, null));

		//	2W-OH
		assertEquals("2W-OH - TWOHIT", "+6/+1/-4/-9;+6", token.getToken(
			"WEAPON.0.TWOHIT", character, null));
		assertEquals("2W-OH - TWOHIT-H1", "+6", token.getToken(
			"WEAPON.1.TWOHIT", character, null));
		assertEquals("2W-OH - TWOHIT-H2", "+6", token.getToken(
			"WEAPON.2.TWOHIT", character, null));

		//	1H-P / 2W-P-(OH) / 2W-P-(OL)
		assertEquals("1H-P - BASICDAMAGE", "1d10+2", token.getToken(
			"WEAPON.0.BASICDAMAGE", character, null));
		assertEquals("1H-P - BASICDAMAGE-H1", "1d10+2", token.getToken(
			"WEAPON.1.BASICDAMAGE", character, null));
		assertEquals("1H-P - BASICDAMAGE-H2", "1d6+2", token.getToken(
			"WEAPON.2.BASICDAMAGE", character, null));

		//	1H-O / 2W-OH
		assertEquals("1H-O - OHDAMAGE", "1d10+1", token.getToken(
			"WEAPON.0.OHDAMAGE", character, null));
		assertEquals("1H-O - OHDAMAGE-H1", "1d10+1", token.getToken(
			"WEAPON.1.OHDAMAGE", character, null));
		assertEquals("1H-O - OHDAMAGE-H2", "1d6+1", token.getToken(
			"WEAPON.2.OHDAMAGE", character, null));

		//	2H
		assertEquals("2H - THDAMAGE", "1d10+3", token.getToken(
			"WEAPON.0.THDAMAGE", character, null));
		assertEquals("2H - THDAMAGE-H1", "1d10+3", token.getToken(
			"WEAPON.1.THDAMAGE", character, null));
		assertEquals("2H - THDAMAGE-H2", "1d6+3", token.getToken(
			"WEAPON.2.THDAMAGE", character, null));

		//	Double
		assertEquals("2H - TOTALHIT", "+6/+1/-4/-9;+6", token.getToken(
			"WEAPON.0.TOTALHIT", character, null));
		assertEquals("2H - TOTALHIT.0", "+6", token.getToken(
			"WEAPON.0.TOTALHIT.0", character, null));
		assertEquals("2H - TOTALHIT.1", "+1", token.getToken(
			"WEAPON.0.TOTALHIT.1", character, null));
		assertEquals("1H-P - TOTALHIT.0", "+14", token.getToken(
			"WEAPON.1.TOTALHIT.0", character, null));
		assertEquals("1H-P - TOTALHIT.1", "+9", token.getToken(
			"WEAPON.1.TOTALHIT.1", character, null));
		assertEquals("2H - THDAMAGE", "1d10+3", token.getToken(
			"WEAPON.0.THDAMAGE", character, null));

	}

	/**
	 * Test the processing of double weapons with enhancements on a medium
	 * creature.
	 */
	public void testEnhancedDoubleWeapon()
	{
		PlayerCharacter character = getCharacter();

		WeaponToken token = new WeaponToken();
		// Test magical enhancements to the double weapon H1:+1, H2:+2
		dblWpn.addEqModifiers("MWORKW.PLUS1W", true);
		dblWpn.addEqModifiers("MWORKW.PLUS2W", false);
		assertEquals("2H - THHIT-H1 [+1]", "+15/+10/+5/+0", token.getToken(
			"WEAPON.1.THHIT", character, null));
		assertEquals("2H - THHIT-H2 [+2]", "+16/+11/+6/+1", token.getToken(
			"WEAPON.2.THHIT", character, null));
		assertEquals("2H - THDAMAGE-H1 [+1]", "1d10+4", token.getToken(
			"WEAPON.1.THDAMAGE", character, null));
		assertEquals("2H - THDAMAGE-H2 [+2]", "1d6+5", token.getToken(
			"WEAPON.2.THDAMAGE", character, null));
	}

	/**
	 * Test the processing of a bastard sword on a medium creature without the
	 * exotic weapon proficiency. It should not be able to be wielded one handed.<br/>
	 * This is based on the text from the DnD FAQ v20060621 on p32 which states 
	 * "Treat ... these weapons as two-handed weapons when determining who can 
	 * use them and how." when talking about bastard swords for weilders without 
	 * the exotic weapon proficiency.
	 */
	public void testBastardSword()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("2-handed prof should be martial",
			"KEY_Sword (Bastard)", bastardSword.get(ObjectKey.WEAPON_PROF).resolvesTo().getKeyName());

		EquipSet es =
				new EquipSet("0.1.2", "Sword (Bastard)",
					bastardSword.getName(), bastardSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Now test the output to ensure it is correct
		WeaponToken token = new WeaponToken();
		assertEquals("Name", "Sword, Bastard", token.getToken("WEAPON.3.NAME",
			character, null));
		assertEquals("Not possible to weild the bastard sword one handed.",
			null, token.getToken("WEAPON.3.BASEHIT", character, null));
		assertEquals("No penalty to weild the bastard sword two handed.",
			"+18/+13/+8/+3", token.getToken("WEAPON.3.THHIT", character, null));
	}

	/**
	 * Test the processing of a large sword on a medium creature. It
	 * should be forced to be wielded two handed. Note: Size penalties are not
	 * included in the data prepared, so are not included in the calculations.
	 */
	public void testLargeLongSword()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("Prof should be longsword", "KEY_LONGSWORD", largeSword
				.get(ObjectKey.WEAPON_PROF).resolvesTo().getKeyName());

		EquipSet es =
				new EquipSet("0.1.3", "Longsword (Large)",
					largeSword.getName(), largeSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Now test the output to ensure it is correct
		WeaponToken token = new WeaponToken();
		assertEquals("Large sword - name", "Longsword (Large)", token.getToken(
			"WEAPON.3.NAME", character, null));
		assertEquals("Large sword - Two handed should be fine",
			"+18/+13/+8/+3", token.getToken("WEAPON.3.THHIT", character, null));
		assertEquals("Large sword - can't be wielded one handed", null, token
			.getToken("WEAPON.3.BASEHIT", character, null));
	}

	/**
	 * Test the processing of a large sword on a medium creature. It
	 * should be forced to be wielded two handed. Note: Size penalties are not
	 * included in the data prepared, so are not included in the calculations.
	 */
	public void testLargeWpnBonus()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("Prof should be longsword", "KEY_LONGSWORD", largeSword
				.get(ObjectKey.WEAPON_PROF).resolvesTo().getKeyName());

		assertTrue("Character should be proficient with longsword", character
			.isProficientWith(largeSword));

		PCTemplate longswordTemplate = new PCTemplate();
		longswordTemplate.setName("LS Bonus");
		longswordTemplate.addBonusList("WEAPONPROF=KEY_LONGSWORD|PCSIZE|1");
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
			"Large sword - can be wielded one handed with template weapon size bonus",
			"+18/+13/+8/+3", token
				.getToken("WEAPON.3.BASEHIT", character, null));
		character.removeTemplate(longswordTemplate);
		assertEquals("Large sword - can't be wielded one handed", null, token
			.getToken("WEAPON.3.BASEHIT", character, null));

		PCTemplate martialTemplate = new PCTemplate();
		martialTemplate.setName("Martial Bonus");
		martialTemplate.addBonusList("WEAPONPROF=TYPE.Martial|PCSIZE|1");
		character.addTemplate(martialTemplate);
		assertEquals(
			"Large sword - can be wielded one handed with template weapon type size bonus",
			"+18/+13/+8/+3", token
				.getToken("WEAPON.3.BASEHIT", character, null));

	}

	/**
	 * Test natural weapons
	 */
	public void testNaturalWeapon()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("Prof should be SillyBite", "SillyBite", bite
				.get(ObjectKey.WEAPON_PROF).resolvesTo().getKeyName());

		EquipSet es =
				new EquipSet("0.1.3", "Bite Attack", bite.getName(), bite);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Now test the output to ensure it is correct
		WeaponToken token = new WeaponToken();
		assertEquals("Silly Bite - Basic To Hit",
			"+18/+18/+18/+18/+18/+18/+18/+18", token.getToken(
				"WEAPON.3.BASEHIT", character, null));
		assertEquals("Silly Bite - Total To Hit",
			"+18/+18/+18/+18/+18/+18/+18/+18", token.getToken(
				"WEAPON.3.TOTALHIT", character, null));
		assertEquals("Silly Bite - Total To Hit first attack", "+18", token
			.getToken("WEAPON.3.TOTALHIT.0", character, null));
	}

	/**
	 * Test the processing of a finesseable weapon both with and without weapon finesse
	 * and temporary bonuses.
	 */
	public void testWpnFinesse()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("Prof should be longsword", "KEY_LONGSWORD", fineSword
				.get(ObjectKey.WEAPON_PROF).resolvesTo().getKeyName());

		character.addEquipment(fineSword);
		EquipSet es =
				new EquipSet("0.1.3", "Longsword (Fine)", fineSword.getName(),
					fineSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Test weapon profs effects on large weapons
		WeaponToken token = new WeaponToken();
		assertEquals("Fine sword", "+18/+13/+8/+3", token.getToken(
			"WEAPON.3.BASEHIT", character, null));

		// Now apply weapon finess and check dex is used rather than str
		Ability wpnFinesse = new Ability();
		wpnFinesse.setName("Weapon Finesse");
		wpnFinesse.setKeyName("Weapon Finesse");
		final BonusObj wfBonus =
				Bonus
					.newBonus("COMBAT|TOHIT.Finesseable|((max(STR,DEX)-STR)+SHIELDACCHECK)|TYPE=NotRanged");
		wfBonus.setCreatorObject(wpnFinesse);
		wpnFinesse.addBonusList(wfBonus);
		character.addFeat(wpnFinesse, null);
		assertEquals("Fine sword", "+19/+14/+9/+4", token.getToken(
			"WEAPON.3.BASEHIT", character, null));

		// Add a temp penalty to dex and check that it is applied
		character.setUseTempMods(true);
		Spell spell2 = new Spell();
		spell2.setName("Concrete Boots");
		spell2.addBonusList("STAT|DEX|-4");
		BonusObj penalty = spell2.getBonusList().get(0);
		character.addTempBonus(penalty);
		penalty.setTargetObject(character);
		character.calcActiveBonuses();
		assertEquals("Fine sword", "+18/+13/+8/+3", token.getToken(
			"WEAPON.3.BASEHIT", character, null));
	}

	public void testWpnReach()
	{
		PlayerCharacter character = getCharacter();
		character.addEquipment(largeSword);
		EquipSet es =
				new EquipSet("0.1.3", "Large Sword", largeSword.getName(),
					largeSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		WeaponToken token = new WeaponToken();
		assertEquals(
			"Reach for a non-reach weapon on a character with normal reach",
			"5", token.getToken("WEAPON.3.REACH", character, null));

		character.addEquipment(longSpear);
		es = new EquipSet("0.1.4", "Longspear", longSpear.getName(), longSpear);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// note: longspear ends up inserted before the large sword above, hence we use weapon.3
		assertEquals(
			"Reach for a reach weapon (10') on a character with normal reach",
			"10", token.getToken("WEAPON.3.REACH", character, null));

		// set reach multiplier on the large sword to 2 and retest
		largeSword.put(IntegerKey.REACH_MULT, 2);
		assertEquals(
			"Reach for a reach multiple weapon on a character with normal reach",
			"10", token.getToken("WEAPON.4.REACH", character, null));
	}
}