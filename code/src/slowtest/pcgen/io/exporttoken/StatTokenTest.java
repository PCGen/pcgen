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

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code StatTokenTest} tests the functioning of the STAT token.
 */

public class StatTokenTest extends AbstractCharacterTestCase
{

	private Equipment boots;
	private Spell spell;
	private PCTemplate template1;
	private PCTemplate template2;
	private PCTemplate template3;
	private PCTemplate template4;

	@BeforeEach
    @Override
	public void setUp() throws Exception
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
	@Test
	void testBonusStacking()
	{
		PlayerCharacter pc = getCharacter();
		StatToken statTok = new StatToken();

		setPCStat(pc, dex, 10);
		assertEquals("DEX", statTok.getToken("STAT.1.NAME", pc, null),
			"Stat Name.");
		assertEquals("10", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("10", statTok.getToken("STAT.1.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("10", statTok.getToken("STAT.1.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("10", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("10", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null),
			"Base stat.");

		pc.addEquipment(boots);
		EquipSet es = new EquipSet("0.1", "Equipped");
		pc.addEquipSet(es);
		es = new EquipSet("0.1.2", "Feet", boots.getName(), boots);
		pc.addEquipSet(es);
		pc.setCalcEquipmentList();
		pc.calcActiveBonuses();

		assertEquals("12", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("10", statTok.getToken("STAT.1.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("12", statTok.getToken("STAT.1.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("10", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("10", statTok.getToken("STAT.1.BASE", pc, null),
			"Base stat.");

		pc.setUseTempMods(true);
		BonusObj bonus = spell.getRawBonusList(pc).get(0);
		pc.addTempBonus(bonus, spell, pc);
		pc.calcActiveBonuses();
		assertEquals("14", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("14", statTok.getToken("STAT.1.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("12", statTok.getToken("STAT.1.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("10", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("10", statTok.getToken("STAT.1.BASE", pc, null),
			"Base stat.");

		pc.addTemplate(template1);
		pc.calcActiveBonuses();
		assertEquals("15", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("15", statTok.getToken("STAT.1.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("13", statTok.getToken("STAT.1.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("11", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("10", statTok.getToken("STAT.1.BASE", pc, null),
			"Base stat.");

		pc.addTemplate(template2);
		pc.calcActiveBonuses();
		assertEquals("15", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("15", statTok.getToken("STAT.1.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("13", statTok.getToken("STAT.1.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("12", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("10", statTok.getToken("STAT.1.BASE", pc, null),
			"Base stat.");
	}

	/**
	 * Test out the processing of stat mods, including stacking issues.  
	 */
	@Test
	void testStatMods()
	{
		PlayerCharacter pc = getCharacter();
		StatToken statTok = new StatToken();

		setPCStat(pc, dex, 10);
		assertEquals("DEX", statTok.getToken("STAT.1.NAME", pc, null),
			"Stat Name.");
		assertEquals("+0", statTok.getToken("STAT.1.MOD", pc, null),
			"Total stat.");
		assertEquals("+0", statTok.getToken("STAT.1.MOD.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("+0", statTok.getToken("STAT.1.MOD.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("+0",
			statTok.getToken("STAT.1.MOD.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("+0",
			statTok.getToken("STAT.1.MOD.NOEQUIP.NOTEMP", pc, null),
			"Base stat.");

		pc.addEquipment(boots);
		EquipSet es = new EquipSet("0.1", "Equipped");
		pc.addEquipSet(es);
		es = new EquipSet("0.1.2", "Feet", boots.getName(), boots);
		pc.addEquipSet(es);
		pc.setCalcEquipmentList();
		pc.calcActiveBonuses();

		assertEquals("+1", statTok.getToken("STAT.1.MOD", pc, null),
			"Total stat.");
		assertEquals("+0", statTok.getToken("STAT.1.MOD.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("+1", statTok.getToken("STAT.1.MOD.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("+0",
			statTok.getToken("STAT.1.MOD.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("+0", statTok.getToken("STAT.1.BASEMOD", pc, null),
			"Base stat.");

		pc.setUseTempMods(true);
		BonusObj bonus = spell.getRawBonusList(pc).get(0);
		pc.addTempBonus(bonus, spell, pc);
		pc.calcActiveBonuses();
		assertEquals("+2", statTok.getToken("STAT.1.MOD", pc, null),
			"Total stat.");
		assertEquals("+2", statTok.getToken("STAT.1.MOD.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("+1", statTok.getToken("STAT.1.MOD.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("+0",
			statTok.getToken("STAT.1.MOD.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("+0", statTok.getToken("STAT.1.BASEMOD", pc, null),
			"Base stat.");

		pc.addTemplate(template1);
		pc.calcActiveBonuses();
		assertEquals("+2", statTok.getToken("STAT.1.MOD", pc, null),
			"Total stat.");
		assertEquals("+2", statTok.getToken("STAT.1.MOD.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("+1", statTok.getToken("STAT.1.MOD.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("+0",
			statTok.getToken("STAT.1.MOD.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("+0", statTok.getToken("STAT.1.BASEMOD", pc, null),
			"Base stat.");

		pc.addTemplate(template2);
		pc.calcActiveBonuses();
		assertEquals("+2", statTok.getToken("STAT.1.MOD", pc, null),
			"Total stat.");
		assertEquals("+2", statTok.getToken("STAT.1.MOD.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("+1", statTok.getToken("STAT.1.MOD.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("+1",
			statTok.getToken("STAT.1.MOD.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
		assertEquals("+0", statTok.getToken("STAT.1.BASEMOD", pc, null),
			"Base stat.");
	}

	/**
	 * Test out the output of stats as at a particular level, including 
	 * stacking issues.  
	 */
	@Test
	void testLevelStat()
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

		assertEquals("14", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("10", statTok.getToken("STAT.1.LEVEL.1", pc, null),
			"Level 1 stat.");
		assertEquals("11", statTok.getToken("STAT.1.LEVEL.2.NOPOST", pc, null),
			"Level 2 stat.");

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

		assertEquals("18", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("14", statTok.getToken("STAT.1.LEVEL.1", pc, null),
			"Level 1 stat.");
		assertEquals("14", statTok.getToken("STAT.1.LEVEL.1.NOEQUIP", pc, null),
			"Level 1 stat.");
		assertEquals("12", statTok.getToken("STAT.1.LEVEL.1.NOTEMP", pc, null),
			"Level 1 stat.");
		assertEquals("10",
			statTok.getToken("STAT.1.LEVEL.1.NOEQUIP.NOTEMP", pc, null),
			"Level 1 stat.");
	}

	/**
	 * Test out the way in which locked stats are reported.
	 */
	@Test
	void testLockedStats()
	{
		PlayerCharacter pc = getCharacter();
		StatToken statTok = new StatToken();

		setPCStat(pc, wis, 10);
		assertEquals("WIS", statTok.getToken("STAT.4.NAME", pc, null),
			"Stat Name.");
		assertEquals("10", statTok.getToken("STAT.4", pc, null), "Total stat.");
		assertEquals("10", statTok.getToken("STAT.4.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("10", statTok.getToken("STAT.4.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("10", statTok.getToken("STAT.4.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");

		pc.addTemplate(template3);
		pc.calcActiveBonuses();
		assertEquals("WIS", statTok.getToken("STAT.4.NAME", pc, null),
			"Stat Name.");
		assertEquals("12", statTok.getToken("STAT.4", pc, null), "Total stat.");
		assertEquals("12", statTok.getToken("STAT.4.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("12", statTok.getToken("STAT.4.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("12", statTok.getToken("STAT.4.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");

		pc.addTemplate(template4);
		pc.calcActiveBonuses();
		assertEquals("WIS", statTok.getToken("STAT.4.NAME", pc, null),
			"Stat Name.");
		assertEquals("12", statTok.getToken("STAT.4", pc, null), "Total stat.");
		assertEquals("12", statTok.getToken("STAT.4.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("12", statTok.getToken("STAT.4.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("12", statTok.getToken("STAT.4.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");

		pc.removeTemplate(template3);
		pc.calcActiveBonuses();
		assertEquals("WIS", statTok.getToken("STAT.4.NAME", pc, null),
			"Stat Name.");
		assertEquals("2", statTok.getToken("STAT.4", pc, null), "Total stat.");
		assertEquals("2", statTok.getToken("STAT.4.NOEQUIP", pc, null),
			"Temp stat.");
		assertEquals("2", statTok.getToken("STAT.4.NOTEMP", pc, null),
			"Equip stat.");
		assertEquals("2", statTok.getToken("STAT.4.NOEQUIP.NOTEMP", pc, null),
			"No equip/temp stat.");
	}
}
