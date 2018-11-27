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
package pcgen.io.exporttoken;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.EquipSet;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;

/**
 * {@code StatTokenTest} tests the functioning of the STAT token.
 */

public class StatTokenTest extends AbstractCharacterTestCase
{

	Equipment boots;
	Spell spell;
	PCTemplate template1;
	PCTemplate template2;
	PCTemplate template3;
	PCTemplate template4;

	/**
	 * @param name
	 */
	public StatTokenTest(String name)
	{
		super(name);
	}

    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		LoadContext context = Globals.getContext();

		boots = new Equipment();
		boots.setName("Boots of Dex");
		BonusObj aBonus = Bonus.newBonus(context, "STAT|DEX|2|TYPE=Enhancement");
		
		if (aBonus != null)
		{
			boots.addToListFor(ListKey.BONUS, aBonus);
		}

		spell = new Spell();
		spell.setName("Weasel's Slipperiness");
		aBonus = Bonus.newBonus(context, "STAT|DEX|4|TYPE=Enhancement");
		
		if (aBonus != null)
		{
			spell.addToListFor(ListKey.BONUS, aBonus);
		}

		template1 = new PCTemplate();
		template1.setName("Munchkiny Goodness I");
		aBonus = Bonus.newBonus(context, "STAT|STR,CON,DEX|1|TYPE=Luck");
		
		if (aBonus != null)
		{
			template1.addToListFor(ListKey.BONUS, aBonus);
		}

		template2 = new PCTemplate();
		template2.setName("Munchkiny Goodness II");
		aBonus = Bonus.newBonus(context, "STAT|STR,CON,DEX|1|TYPE=Enhancement");
		
		if (aBonus != null)
		{
			template2.addToListFor(ListKey.BONUS, aBonus);
		}

		template3 = new PCTemplate();
		template3.setName("Lock the stat");
		template3.addToListFor(ListKey.STAT_MINVALUE,
			new StatLock(context.getReferenceContext().getCDOMReference(
				PCStat.class, "WIS"), FormulaFactory.getFormulaFor("12")));


		template4 = new PCTemplate();
		template4.setName("Unwise");
		aBonus = Bonus.newBonus(context, "STAT|WIS|-8|TYPE=Enhancement");
		if (aBonus != null)
		{
			template4.addToListFor(ListKey.BONUS, aBonus);
		}
	}

	/**
	 * Test out the stacking of bonuses particularly when NOEUIP or NOTEMP
	 * are used.  
	 */
	public void testBonusStacking()
	{
		PlayerCharacter pc = getCharacter();
		StatToken statTok = new StatToken();

		setPCStat(pc, dex, 10);
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
		BonusObj bonus = spell.getRawBonusList(pc).get(0);
		pc.addTempBonus(bonus, spell, pc);
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

		setPCStat(pc, dex, 10);
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
		BonusObj bonus = spell.getRawBonusList(pc).get(0);
		pc.addTempBonus(bonus, spell, pc);
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
		setPCStat(pc, dex, 10);

		PCClass myClass = new PCClass();
		myClass.setName("My Class");
		myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
		pc.incrementClassLevel(1, myClass, true);

		pc.incrementClassLevel(1, myClass, true);
		pc.saveStatIncrease(dex, 1, true);
		pc.saveStatIncrease(dex, 1, false);
		setPCStat(pc, dex, 12);

		pc.incrementClassLevel(1, myClass, true);
		pc.saveStatIncrease(dex, 1, true);
		pc.saveStatIncrease(dex, 1, false);
		setPCStat(pc, dex, 14);

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
		BonusObj bonus = spell.getRawBonusList(pc).get(0);
		pc.addTempBonus(bonus, spell, pc);
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

	/**
	 * Test out the way in which locked stats are reported.
	 */
	public void testLockedStats()
	{
		PlayerCharacter pc = getCharacter();
		StatToken statTok = new StatToken();

		setPCStat(pc, wis, 10);
		assertEquals("Stat Name.", "WIS", statTok.getToken("STAT.4.NAME", pc,
			null));
		assertEquals("Total stat.", "10", statTok.getToken("STAT.4", pc, null));
		assertEquals("Temp stat.", "10", statTok.getToken("STAT.4.NOEQUIP", pc,
			null));
		assertEquals("Equip stat.", "10", statTok.getToken("STAT.4.NOTEMP", pc,
			null));
		assertEquals("No equip/temp stat.", "10", statTok.getToken(
			"STAT.4.NOEQUIP.NOTEMP", pc, null));

		pc.addTemplate(template3);
		pc.calcActiveBonuses();
		assertEquals("Stat Name.", "WIS", statTok.getToken("STAT.4.NAME", pc,
			null));
		assertEquals("Total stat.", "12", statTok.getToken("STAT.4", pc, null));
		assertEquals("Temp stat.", "12", statTok.getToken("STAT.4.NOEQUIP", pc,
			null));
		assertEquals("Equip stat.", "12", statTok.getToken("STAT.4.NOTEMP", pc,
			null));
		assertEquals("No equip/temp stat.", "12", statTok.getToken(
			"STAT.4.NOEQUIP.NOTEMP", pc, null));

		pc.addTemplate(template4);
		pc.calcActiveBonuses();
		assertEquals("Stat Name.", "WIS", statTok.getToken("STAT.4.NAME", pc,
			null));
		assertEquals("Total stat.", "12", statTok.getToken("STAT.4", pc, null));
		assertEquals("Temp stat.", "12", statTok.getToken("STAT.4.NOEQUIP", pc,
			null));
		assertEquals("Equip stat.", "12", statTok.getToken("STAT.4.NOTEMP", pc,
			null));
		assertEquals("No equip/temp stat.", "12", statTok.getToken(
			"STAT.4.NOEQUIP.NOTEMP", pc, null));

		pc.removeTemplate(template3);
		pc.calcActiveBonuses();
		assertEquals("Stat Name.", "WIS", statTok.getToken("STAT.4.NAME", pc,
			null));
		assertEquals("Total stat.", "2", statTok.getToken("STAT.4", pc, null));
		assertEquals("Temp stat.", "2", statTok.getToken("STAT.4.NOEQUIP", pc,
			null));
		assertEquals("Equip stat.", "2", statTok.getToken("STAT.4.NOTEMP", pc,
			null));
		assertEquals("No equip/temp stat.", "2", statTok.getToken(
			"STAT.4.NOEQUIP.NOTEMP", pc, null));
	}
}
