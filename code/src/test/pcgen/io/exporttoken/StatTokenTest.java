/*
 * StatTokenTest.java
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
package pcgen.io.exporttoken;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.EquipSet;
import pcgen.core.spell.Spell;

/**
 * <code>StatTokenTest</code> tests the functioning of the STAT token. 
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class StatTokenTest extends AbstractCharacterTestCase
{

	Equipment boots;
	Spell spell;
	PCTemplate template1;
	PCTemplate template2;

	/**
	 * 
	 */
	public StatTokenTest()
	{
		super();
	}

	/**
	 * @param name
	 */
	public StatTokenTest(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		boots = new Equipment();
		boots.setName("Boots of Dex");
		boots.addBonusList("STAT|DEX|2|TYPE=Enhancement");

		spell = new Spell();
		spell.setName("Weasel's Slipperiness");
		spell.addBonusList("STAT|DEX|4|TYPE=Enhancement");

		template1 = new PCTemplate();
		template1.setName("Munchkiny Goodness I");
		template1.addBonusList("STAT|STR,CON,DEX|1|TYPE=Luck");

		template2 = new PCTemplate();
		template2.setName("Munchkiny Goodness II");
		template2.addBonusList("STAT|STR,CON,DEX|1|TYPE=Enhancement");
	}

	/**
	 * Test out the stacking of bonuses particularly when NOEUIP or NOTEMP
	 * are used.  
	 */
	public void testBonusStacking()
	{
		PlayerCharacter pc = getCharacter();
		StatToken statTok = new StatToken();

		setPCStat(pc, "DEX", 10);
		assertEquals("Stat Name.", "DEX", statTok.getToken("STAT.1.NAME", pc,
			null));
		assertEquals("Total stat.", "10", statTok.getToken("STAT.1", pc, null));
		assertEquals("Temp stat.", "10", statTok.getToken("STAT.1.NOEQUIP", pc,
			null));
		assertEquals("Equip stat.", "10", statTok.getToken("STAT.1.NOTEMP", pc,
			null));
		assertEquals("No equip/temp stat.", "10", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "10", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null));

		pc.addEquipment(boots);
		EquipSet es = new EquipSet("0.1", "Equipped");
		pc.addEquipSet(es);
		es = new EquipSet("0.1.2", "Feet", boots.getName(), boots);
		pc.addEquipSet(es);
		pc.setCalcEquipmentList();
		pc.calcActiveBonuses();

		assertEquals("Total stat.", "12", statTok.getToken("STAT.1", pc, null));
		assertEquals("Temp stat.", "10", statTok.getToken("STAT.1.NOEQUIP", pc,
			null));
		assertEquals("Equip stat.", "12", statTok.getToken("STAT.1.NOTEMP", pc,
			null));
		assertEquals("No equip/temp stat.", "10", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "10", statTok.getToken("STAT.1.BASE", pc,
			null));

		pc.setUseTempMods(true);
		BonusObj bonus = spell.getBonusList().get(0);
		pc.addTempBonus(bonus);
		bonus.setTargetObject(pc);
		pc.calcActiveBonuses();
		assertEquals("Total stat.", "14", statTok.getToken("STAT.1", pc, null));
		assertEquals("Temp stat.", "14", statTok.getToken("STAT.1.NOEQUIP", pc,
			null));
		assertEquals("Equip stat.", "12", statTok.getToken("STAT.1.NOTEMP", pc,
			null));
		assertEquals("No equip/temp stat.", "10", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "10", statTok.getToken("STAT.1.BASE", pc,
			null));

		pc.addTemplate(template1);
		pc.calcActiveBonuses();
		assertEquals("Total stat.", "15", statTok.getToken("STAT.1", pc, null));
		assertEquals("Temp stat.", "15", statTok.getToken("STAT.1.NOEQUIP", pc,
			null));
		assertEquals("Equip stat.", "13", statTok.getToken("STAT.1.NOTEMP", pc,
			null));
		assertEquals("No equip/temp stat.", "11", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "10", statTok.getToken("STAT.1.BASE", pc,
			null));

		pc.addTemplate(template2);
		pc.calcActiveBonuses();
		assertEquals("Total stat.", "15", statTok.getToken("STAT.1", pc, null));
		assertEquals("Temp stat.", "15", statTok.getToken("STAT.1.NOEQUIP", pc,
			null));
		assertEquals("Equip stat.", "13", statTok.getToken("STAT.1.NOTEMP", pc,
			null));
		assertEquals("No equip/temp stat.", "12", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "10", statTok.getToken("STAT.1.BASE", pc,
			null));
	}

	/**
	 * Test out the processing of stat mods, including stacking issues.  
	 */
	public void testStatMods()
	{
		PlayerCharacter pc = getCharacter();
		StatToken statTok = new StatToken();

		setPCStat(pc, "DEX", 10);
		assertEquals("Stat Name.", "DEX", statTok.getToken("STAT.1.NAME", pc,
			null));
		assertEquals("Total stat.", "+0", statTok.getToken("STAT.1.MOD", pc,
			null));
		assertEquals("Temp stat.", "+0", statTok.getToken("STAT.1.MOD.NOEQUIP",
			pc, null));
		assertEquals("Equip stat.", "+0", statTok.getToken("STAT.1.MOD.NOTEMP",
			pc, null));
		assertEquals("No equip/temp stat.", "+0", statTok.getToken(
			"STAT.1.MOD.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "+0", statTok.getToken(
			"STAT.1.MOD.NOEQUIP.NOTEMP", pc, null));

		pc.addEquipment(boots);
		EquipSet es = new EquipSet("0.1", "Equipped");
		pc.addEquipSet(es);
		es = new EquipSet("0.1.2", "Feet", boots.getName(), boots);
		pc.addEquipSet(es);
		pc.setCalcEquipmentList();
		pc.calcActiveBonuses();

		assertEquals("Total stat.", "+1", statTok.getToken("STAT.1.MOD", pc,
			null));
		assertEquals("Temp stat.", "+0", statTok.getToken("STAT.1.MOD.NOEQUIP",
			pc, null));
		assertEquals("Equip stat.", "+1", statTok.getToken("STAT.1.MOD.NOTEMP",
			pc, null));
		assertEquals("No equip/temp stat.", "+0", statTok.getToken(
			"STAT.1.MOD.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "+0", statTok.getToken("STAT.1.BASEMOD", pc,
			null));

		pc.setUseTempMods(true);
		BonusObj bonus = spell.getBonusList().get(0);
		pc.addTempBonus(bonus);
		bonus.setTargetObject(pc);
		pc.calcActiveBonuses();
		assertEquals("Total stat.", "+2", statTok.getToken("STAT.1.MOD", pc,
			null));
		assertEquals("Temp stat.", "+2", statTok.getToken("STAT.1.MOD.NOEQUIP",
			pc, null));
		assertEquals("Equip stat.", "+1", statTok.getToken("STAT.1.MOD.NOTEMP",
			pc, null));
		assertEquals("No equip/temp stat.", "+0", statTok.getToken(
			"STAT.1.MOD.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "+0", statTok.getToken("STAT.1.BASEMOD", pc,
			null));

		pc.addTemplate(template1);
		pc.calcActiveBonuses();
		assertEquals("Total stat.", "+2", statTok.getToken("STAT.1.MOD", pc,
			null));
		assertEquals("Temp stat.", "+2", statTok.getToken("STAT.1.MOD.NOEQUIP",
			pc, null));
		assertEquals("Equip stat.", "+1", statTok.getToken("STAT.1.MOD.NOTEMP",
			pc, null));
		assertEquals("No equip/temp stat.", "+0", statTok.getToken(
			"STAT.1.MOD.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "+0", statTok.getToken("STAT.1.BASEMOD", pc,
			null));

		pc.addTemplate(template2);
		pc.calcActiveBonuses();
		assertEquals("Total stat.", "+2", statTok.getToken("STAT.1.MOD", pc,
			null));
		assertEquals("Temp stat.", "+2", statTok.getToken("STAT.1.MOD.NOEQUIP",
			pc, null));
		assertEquals("Equip stat.", "+1", statTok.getToken("STAT.1.MOD.NOTEMP",
			pc, null));
		assertEquals("No equip/temp stat.", "+1", statTok.getToken(
			"STAT.1.MOD.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "+0", statTok.getToken("STAT.1.BASEMOD", pc,
			null));
	}

	/**
	 * Test out the output of stats as at a particular level, including 
	 * stacking issues.  
	 */
	public void testLevelStat()
	{
		StatToken statTok = new StatToken();
		PlayerCharacter pc = getCharacter();
		setPCStat(pc, "DEX", 10);

		PCClass myClass = new PCClass();
		myClass.setName("My Class");
		myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
		pc.incrementClassLevel(1, myClass, true);

		pc.incrementClassLevel(1, myClass, true);
		pc.saveStatIncrease("DEX", 1, true);
		pc.saveStatIncrease("DEX", 1, false);
		setPCStat(pc, "DEX", 12);

		pc.incrementClassLevel(1, myClass, true);
		pc.saveStatIncrease("DEX", 1, true);
		pc.saveStatIncrease("DEX", 1, false);
		setPCStat(pc, "DEX", 14);

		assertEquals("Total stat.", "14", statTok.getToken("STAT.1", pc, null));
		assertEquals("Level 1 stat.", "10", statTok.getToken("STAT.1.LEVEL.1",
			pc, null));
		assertEquals("Level 2 stat.", "11", statTok.getToken(
			"STAT.1.LEVEL.2.NOPOST", pc, null));

		pc.addEquipment(boots);
		EquipSet es = new EquipSet("0.1", "Equipped");
		pc.addEquipSet(es);
		es = new EquipSet("0.1.2", "Feet", boots.getName(), boots);
		pc.addEquipSet(es);
		pc.setCalcEquipmentList();
		pc.calcActiveBonuses();

		pc.setUseTempMods(true);
		BonusObj bonus = spell.getBonusList().get(0);
		pc.addTempBonus(bonus);
		bonus.setTargetObject(pc);
		pc.calcActiveBonuses();

		assertEquals("Total stat.", "18", statTok.getToken("STAT.1", pc, null));
		assertEquals("Level 1 stat.", "14", statTok.getToken("STAT.1.LEVEL.1",
			pc, null));
		assertEquals("Level 1 stat.", "14", statTok.getToken(
			"STAT.1.LEVEL.1.NOEQUIP", pc, null));
		assertEquals("Level 1 stat.", "12", statTok.getToken(
			"STAT.1.LEVEL.1.NOTEMP", pc, null));
		assertEquals("Level 1 stat.", "10", statTok.getToken(
			"STAT.1.LEVEL.1.NOEQUIP.NOTEMP", pc, null));
	}
}
