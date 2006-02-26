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
 * $Id: WeaponTokenTest.java,v 1.14 2005/11/05 13:37:57 binkley Exp $
 *
 */
package pcgen.io.exporttoken;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
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
import pcgen.core.character.EquipSet;
import pcgen.core.character.WieldCategory;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;

/**
 * <code>WeaponTokenTest</code> contains tests to verify that the
 * WEAPON token is working correctly.
 *
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/11/05 13:37:57 $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.14 $
 */

public class WeaponTokenTest extends AbstractCharacterTestCase
{
	private Equipment dblWpn = null;
	private Equipment bastardSword = null;
	private Equipment largeSword = null;
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
	public WeaponTokenTest(String name) {
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();

		//Stats
		setPCStat(character, "STR", 15);
		setPCStat(character, "DEX", 16);
		setPCStat(character, "INT", 17);
		PCStat stat = ((PCStat) character.getStatList().getStats().get(0));
		stat.setStatMod("floor(SCORE/2)-5");
		stat.addBonusList("COMBAT|TOHIT.Melee|STR|TYPE=Ability");
		stat.addBonusList("DAMAGE|TYPE.Melee,TYPE.Thrown|STR");
		stat.addBonusList("COMBAT|DAMAGEMULT:0|0.5*(STR>=0)");
		stat.addBonusList("COMBAT|DAMAGEMULT:1|1");
		stat.addBonusList("COMBAT|DAMAGEMULT:2|1.5*(STR>=0)");
		stat.addVariable(0, "OFFHANDLIGHTBONUS", "2");


		stat = ((PCStat) character.getStatList().getStats().get(3));
		stat.addBonusList("MODSKILLPOINTS|NUMBER|INT");


		// Race
		Race testRace = new Race();
		testRace.setName("TestRace");
		character.setRace(testRace);

		// Class
		PCClass myClass = new PCClass();
		myClass.setName("My Class");
	        myClass.setAbbrev("Myc");
		myClass.setSkillPointFormula("3");
	        final BonusObj babClassBonus = Bonus.newBonus("1|COMBAT|BAB|CL+15");
	        myClass.addBonusList(babClassBonus);
	        Globals.getClassList().add(myClass);
		character.incrementClassLevel(1, myClass, true);


		character.calcActiveBonuses();

		dblWpn = new Equipment();
		dblWpn.setName("DoubleWpn");
		dblWpn.setTypeInfo("Weapon.Melee.Martial.Double.Standard.Bludgeoning.Flail");
		dblWpn.setDamage("1d10");
		dblWpn.setAltDamage("1d6");
		dblWpn.setCritMult("x2");
		dblWpn.setAltCritMult("x2");
		dblWpn.setCritRange("1");
		dblWpn.setAltCritRange("1");
		dblWpn.setSlots(2);
		dblWpn.setWield("TwoHanded");
		dblWpn.setSize("M", true);
		character.addEquipment(dblWpn);
		EquipSet def = new EquipSet("0.1", "Default");
		character.addEquipSet(def);
		EquipSet es = new EquipSet("0.1.1", "Double Weapon", dblWpn.getName(), dblWpn);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		bastardSword = new Equipment();
		bastardSword.setName("Sword (Bastard)");
		bastardSword.setOutputName("Sword, Bastard");
		bastardSword.setProfName("Sword (Bastard/[Hands])");
		bastardSword.setTypeInfo("Weapon.Melee.Martial.Exotic.Standard.Slashing.Sword");
		bastardSword.setDamage("1d10");
		bastardSword.setCritMult("x2");
		bastardSword.setCritRange("2");
		bastardSword.setWield("TwoHanded");
		bastardSword.setSize("M", true);

		WeaponProf wp = new WeaponProf();
		wp.setName("DoubleWpn");
		wp.setHands("1IFLARGERTHANWEAPON");
		Globals.addWeaponProf(wp);

		wp = new WeaponProf();
		wp.setName("Sword (Bastard/Martial)");
		wp.setTypeInfo("MARTIAL");
		wp.setHands("2");
		Globals.addWeaponProf(wp);
		character.addWeaponProf(wp.getName());

		wp = new WeaponProf();
		wp.setName("Sword (Bastard/Exotic)");
		wp.setTypeInfo("EXOTIC");
		Globals.addWeaponProf(wp);

		largeSword = new Equipment();
		largeSword.setName("Longsword (Large)");
		largeSword.setOutputName("Longsword (Large)");
		largeSword.setProfName("Longsword");
		largeSword.setTypeInfo("Weapon.Melee.Martial.Standard.Slashing.Sword");
		largeSword.setDamage("1d10");
		largeSword.setCritMult("x2");
		largeSword.setCritRange("2");
		largeSword.setWield("OneHanded");
		largeSword.setSize("L", true);

		wp = new WeaponProf();
		wp.setName("Longsword");
		wp.setTypeInfo("MARTIAL");
		Globals.addWeaponProf(wp);
		character.addWeaponProf(wp.getName());

		GameMode gm = SettingsHandler.getGame();
		RuleCheck rc = new RuleCheck();
		rc.setName("SIZECAT");
		gm.addRule(rc);
		SettingsHandler.setRuleCheck("SIZECAT", true);
		gm.setWCStepsFormula("EQUIP.SIZE.INT-PC.SIZE.INT");

		bite = new Equipment();
		bite.setName("Silly Bite");
		bite.setKeyName("SillyBite");
		bite.setOutputName("Silly Bite (For Test)");
		bite.setTypeInfo("Weapon.Natural.Melee.Finesseable.Bludgeoning.Piercing.Slashing");
		bite.setWeight("0");
		bite.setSize("M", true);
		bite.addBonusList("WEAPON|ATTACKS|" + 7);
		bite.setOnlyNaturalWeapon(false);
		bite.setSlots(0);
		bite.setQty(new Float(1));
		bite.setNumberCarried(new Float(1));
		bite.setAttacksProgress(false);
		bite.setDamage("1d10");
		bite.setCritMult("x2");
		bite.setCritRange("2");
		bite.setWield("OneHanded");
		bite.setProfName("SillyBite");

		wp = new WeaponProf();
		wp.setName("SillyBite");
		//wp.setKeyName("SillyBite");
		//wp.setTypeInfo("Weapon.Natural.Melee.Finesseable.Bludgeoning.Piercing.Slashing");
		wp.setTypeInfo("Natural");
		Globals.addWeaponProf(wp);
		character.addWeaponProf(wp.getName());

		// Weild categories
		WieldCategory wCat1h = new WieldCategory("OneHanded");
		wCat1h.setHands(1);
		wCat1h.setSizeDiff(0);
		gm.addWieldCategory(wCat1h);
		WieldCategory wCat2h = new WieldCategory("TwoHanded");
		wCat2h.setHands(2);
		wCat2h.setSizeDiff(1);
		gm.addWieldCategory(wCat2h);
		wCat1h.addSwitchMap("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1", "TwoHanded");

		// Equip mods
		EquipmentModifier eqMod = new EquipmentModifier();
		eqMod.setName("Plus 1 Enhancement");
		eqMod.setKeyName("PLUS1W");
		eqMod.setTypeInfo("Ammunition.Weapon");
		eqMod.setPlus("1");
		eqMod.setItemType("Enhancement.Magic.Plus1");
		eqMod.addBonusList("WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement");
		EquipmentList.getModifierList().add(eqMod);
		eqMod = new EquipmentModifier();
		eqMod.setName("Plus 2 Enhancement");
		eqMod.setKeyName("PLUS2W");
		eqMod.setTypeInfo("Ammunition.Weapon");
		eqMod.setPlus("2");
		eqMod.setItemType("Enhancement.Magic.Plus2");
		eqMod.addBonusList("WEAPON|DAMAGE,TOHIT|2|TYPE=Enhancement");
		EquipmentList.getModifierList().add(eqMod);
		eqMod = new EquipmentModifier();
		eqMod.setName("Masterwork");
		eqMod.setKeyName("MWORKW");
		eqMod.setTypeInfo("Ammunition.Weapon");
		eqMod.setItemType("Masterwork");
		eqMod.addBonusList("WEAPON|TOHIT|1|TYPE=Enhancement");
		EquipmentList.getModifierList().add(eqMod);
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
		assertEquals("Name-H1", "*DoubleWpn (Head 1 only)", token.getToken("WEAPON.1.NAME",
			character, null));
		assertEquals("Name-H2", "*DoubleWpn (Head 2 only)", token.getToken("WEAPON.2.NAME",
			character, null));

		assertEquals("Hand", "Two-Weapons", token.getToken("WEAPON.0.HAND",
			character, null));
		assertEquals("Hand-H1", "Two-Weapons", token.getToken("WEAPON.1.HAND",
			character, null));
		assertEquals("Hand-H2", "Two-Weapons", token.getToken("WEAPON.2.HAND",
			character, null));

		//	1H-P
		assertEquals("1H-P - BASEHIT", "+14/+9/+4/-1", token.getToken("WEAPON.0.BASEHIT",
			character, null));
		assertEquals("1H-P - BASEHIT-H1", null, token.getToken("WEAPON.1.BASEHIT",
			character, null));
		assertEquals("1H-P - BASEHIT-H2", null, token.getToken("WEAPON.2.BASEHIT",
			character, null));

		//	1H-O
		assertEquals("1H-O - OHHIT", "+10/+5/+0/-5", token.getToken("WEAPON.0.OHHIT",
			character, null));
		assertEquals("1H-O - OHHIT-H1", null, token.getToken("WEAPON.1.OHHIT",
			character, null));
		assertEquals("1H-O - OHHIT-H2", null, token.getToken("WEAPON.2.OHHIT",
			character, null));

		//	2H
		assertEquals("2H - THHIT", "+14/+9/+4/-1", token.getToken("WEAPON.0.THHIT",
			character, null));
		assertEquals("2H - THHIT-H1", "+14/+9/+4/-1", token.getToken("WEAPON.1.THHIT",
			character, null));
		assertEquals("2H - THHIT-H2", "+14/+9/+4/-1", token.getToken("WEAPON.2.THHIT",
			character, null));

		//	2W-P-(OH)
		assertEquals("2W-P-(OH) - TWPHITH", "+8/+3/-2/-7", token.getToken("WEAPON.0.TWPHITH",
			character, null));
		assertEquals("2W-P-(OH) - TWPHITH-H1", null, token.getToken("WEAPON.1.TWPHITH",
			character, null));
		assertEquals("2W-P-(OH) - TWPHITH-H2", null, token.getToken("WEAPON.2.TWPHITH",
			character, null));

		//	2W-P-(OL)
		assertEquals("2W-P-(OL) - TWPHITL", "+10/+5/+0/-5", token.getToken("WEAPON.0.TWPHITL",
			character, null));
		assertEquals("2W-P-(OL) - TWPHITL-H1", "+10/+5/+0/-5", token.getToken("WEAPON.1.TWPHITL",
			character, null));
		assertEquals("2W-P-(OL) - TWPHITL-H2", "+10/+5/+0/-5", token.getToken("WEAPON.2.TWPHITL",
			character, null));

		//	2W-OH
		assertEquals("2W-OH - TWOHIT", "+6/+1/-4/-9;+6", token.getToken("WEAPON.0.TWOHIT",
			character, null));
		assertEquals("2W-OH - TWOHIT-H1", "+6", token.getToken("WEAPON.1.TWOHIT",
			character, null));
		assertEquals("2W-OH - TWOHIT-H2", "+6", token.getToken("WEAPON.2.TWOHIT",
			character, null));


		//	1H-P / 2W-P-(OH) / 2W-P-(OL)
		assertEquals("1H-P - BASICDAMAGE", "1d10+2", token.getToken("WEAPON.0.BASICDAMAGE",
			character, null));
		assertEquals("1H-P - BASICDAMAGE-H1", "1d10+2", token.getToken("WEAPON.1.BASICDAMAGE",
			character, null));
		assertEquals("1H-P - BASICDAMAGE-H2", "1d6+2", token.getToken("WEAPON.2.BASICDAMAGE",
			character, null));

		//	1H-O / 2W-OH
		assertEquals("1H-O - OHDAMAGE", "1d10+1", token.getToken("WEAPON.0.OHDAMAGE",
			character, null));
		assertEquals("1H-O - OHDAMAGE-H1", "1d10+1", token.getToken("WEAPON.1.OHDAMAGE",
			character, null));
		assertEquals("1H-O - OHDAMAGE-H2", "1d6+1", token.getToken("WEAPON.2.OHDAMAGE",
			character, null));

		//	2H
		assertEquals("2H - THDAMAGE", "1d10+3", token.getToken("WEAPON.0.THDAMAGE",
			character, null));
		assertEquals("2H - THDAMAGE-H1", "1d10+3", token.getToken("WEAPON.1.THDAMAGE",
			character, null));
		assertEquals("2H - THDAMAGE-H2", "1d6+3", token.getToken("WEAPON.2.THDAMAGE",
			character, null));

		//	Double
		assertEquals("2H - TOTALHIT", "+6/+1/-4/-9;+6", token.getToken("WEAPON.0.TOTALHIT",
			character, null));
		assertEquals("2H - TOTALHIT.0", "+6", token.getToken("WEAPON.0.TOTALHIT.0",
			character, null));
		assertEquals("2H - TOTALHIT.1", "+1", token.getToken("WEAPON.0.TOTALHIT.1",
			character, null));
		assertEquals("1H-P - TOTALHIT.0", "+14", token.getToken("WEAPON.1.TOTALHIT.0",
			character, null));
		assertEquals("1H-P - TOTALHIT.1", "+9", token.getToken("WEAPON.1.TOTALHIT.1",
			character, null));
		assertEquals("2H - THDAMAGE", "1d10+3", token.getToken("WEAPON.0.THDAMAGE",
			character, null));


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
		assertEquals("2H - THHIT-H1 [+1]", "+15/+10/+5/+0", token.getToken("WEAPON.1.THHIT",
			character, null));
		assertEquals("2H - THHIT-H2 [+2]", "+16/+11/+6/+1", token.getToken("WEAPON.2.THHIT",
			character, null));
		assertEquals("2H - THDAMAGE-H1 [+1]", "1d10+4", token.getToken("WEAPON.1.THDAMAGE",
			character, null));
		assertEquals("2H - THDAMAGE-H2 [+2]", "1d6+5", token.getToken("WEAPON.2.THDAMAGE",
			character, null));
	}

	/**
	 * Test the processing of a bastard sword on a medium creature without the
	 * exotic weapon proficiency. It should be able to be wielded one handed,
	 * but with a proficiency penalty.
	 */
	public void testBastardSword()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("1-handed Prof should be exotic",
			"Sword (Bastard/Exotic)", bastardSword.profName(1, character));
		assertEquals("2-handed prof should be martial",
			"Sword (Bastard/Martial)", bastardSword.profName(2, character));

		EquipSet es = new EquipSet("0.1.2", "Sword (Bastard)", bastardSword
			.getName(), bastardSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Now test the output to ensure it is correct
		WeaponToken token = new WeaponToken();
		assertEquals("Name", "Sword, Bastard", token.getToken("WEAPON.3.NAME",
			character, null));
		assertEquals("Name", "+14/+9/+4/-1", token
			.getToken("WEAPON.3.BASEHIT", character, null));
		assertEquals("Name", "+18/+13/+8/+3", token.getToken("WEAPON.3.THHIT", character, null));
	}

	/**
	 * Test the processing of a large sword on a medium creature. It
	 * should be forced to be wielded two handed. Note: Size penalties are not
	 * included in the data prepared, so are not included in the calculations.
	 */
	public void testLargeLongSword()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("1-handed Prof should be longsword", "Longsword", largeSword
			.profName(1, character));
		assertEquals("2-handed prof should be longsword", "Longsword", largeSword
			.profName(2, character));

		EquipSet es = new EquipSet("0.1.3", "Longsword (Large)", largeSword
			.getName(), largeSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Now test the output to ensure it is correct
		WeaponToken token = new WeaponToken();
		assertEquals("Large sword - name", "Longsword (Large)", token.getToken(
			"WEAPON.3.NAME", character, null));
		assertEquals("Large sword - Two handed should be fine", "+18/+13/+8/+3", token
			.getToken("WEAPON.3.THHIT", character, null));
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
		assertEquals("1-handed Prof should be longsword", "Longsword", largeSword
			.profName(1, character));
		assertEquals("2-handed prof should be longsword", "Longsword", largeSword
			.profName(2, character));

		PCTemplate longswordTemplate = new PCTemplate();
		longswordTemplate.setName("LS Bonus");
		longswordTemplate.addBonusList("WEAPONPROF=Longsword|PCSIZE|1");
		character.addTemplate(longswordTemplate);

		character.addEquipment(largeSword);
		EquipSet es = new EquipSet("0.1.3", "Longsword (Large)", largeSword
			.getName(), largeSword);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Test weapon profs effects on large weapons
		WeaponToken token = new WeaponToken();
		assertEquals("Large sword - can't be wielded one handed", "+14/+9/+4/-1", token
			.getToken("WEAPON.3.BASEHIT", character, null));
		character.removeTemplate(longswordTemplate);
		assertEquals("Large sword - can't be wielded one handed", null, token
			.getToken("WEAPON.3.BASEHIT", character, null));

		PCTemplate martialTemplate = new PCTemplate();
		martialTemplate.setName("Martial Bonus");
		martialTemplate.addBonusList("WEAPONPROF=TYPE.Martial|PCSIZE|1");
		character.addTemplate(martialTemplate);
		assertEquals("Large sword - can't be wielded one handed", "+14/+9/+4/-1", token
			.getToken("WEAPON.3.BASEHIT", character, null));

	}


	/**
	 * Test natural weapons
	 */
	public void testNaturalWeapon()
	{
		PlayerCharacter character = getCharacter();
		assertEquals("1-handed Prof should be SillyBite", "SillyBite", bite
			.profName(1, character));
		assertEquals("2-handed prof should be SillyBite", "SillyBite", bite
			.profName(2, character));

		EquipSet es = new EquipSet("0.1.3", "Bite Attack", bite.getName(), bite);
		character.addEquipSet(es);
		character.setCalcEquipmentList();

		// Now test the output to ensure it is correct
		WeaponToken token = new WeaponToken();
		assertEquals("Silly Bite - Basic To Hit", "+18/+18/+18/+18/+18/+18/+18/+18", token
			.getToken("WEAPON.3.BASEHIT", character, null));
		assertEquals("Silly Bite - Total To Hit", "+18/+18/+18/+18/+18/+18/+18/+18", token
			.getToken("WEAPON.3.TOTALHIT", character, null));
		assertEquals("Silly Bite - Total To Hit first attack", "+18", token
			.getToken("WEAPON.3.TOTALHIT.0", character, null));
	}

}
