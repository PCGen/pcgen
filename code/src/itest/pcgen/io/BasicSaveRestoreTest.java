/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.io;

import java.util.Arrays;

import org.junit.Test;

import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.Deity;
import pcgen.core.GameMode;
import pcgen.core.NoteItem;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.io.testsupport.AbstractSaveRestoreTest;
import plugin.lsttokens.CcskillLst;
import plugin.lsttokens.CskillLst;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.choose.SkillToken;
import plugin.lsttokens.race.MoncskillToken;
import plugin.lsttokens.skill.ExclusiveToken;

public class BasicSaveRestoreTest extends AbstractSaveRestoreTest
{

	@Test
	public void testBoilerplateOnly()
	{
		finishLoad();
		runRoundRobin();
	}

	@Test
	public void testDeity()
	{
		Deity deity = create(Deity.class, "MyDeity");
		finishLoad();
		pc.setDeity(deity);
		runRoundRobin();
	}

	@Test
	public void testDeityAddRemove()
	{
		Deity deity = create(Deity.class, "MyDeity");
		finishLoad();
		pc.setDeity(deity);
		//TODO No way to remove a deity once set :(
		//pc.removeDeity();
		runRoundRobin();
	}

	@Test
	public void testAlignment()
	{
		finishLoad();
		pc.setAlignment(le);
		runRoundRobin();
	}

	@Test
	public void testAlignmentAddRemove()
	{
		finishLoad();
		pc.setAlignment(le);
		//TODO No way to remove a alignment once set :(
		//pc.removeAlignment();
		runRoundRobin();
	}

	@Test
	public void testTemplate()
	{
		PCTemplate template = create(PCTemplate.class, "MyTemplate");
		finishLoad();
		pc.addTemplate(template);
		runRoundRobin();
	}

	@Test
	public void testTemplateAddRemove()
	{
		PCTemplate template = create(PCTemplate.class, "MyTemplate");
		finishLoad();
		pc.addTemplate(template);
		pc.removeTemplate(template);
		runRoundRobin();
	}

	@Test
	public void testSpellBook()
	{
		finishLoad();
		pc.addSpellBook("MyBook");
		runRoundRobin();
	}

	@Test
	public void testSpellBookAddRemove()
	{
		finishLoad();
		pc.addSpellBook("MyBook");
		pc.delSpellBook("MyBook");
		runRoundRobin();
	}

	@Test
	public void testCharacterType()
	{
		GameMode mode = SettingsHandler.getGame();
		mode.setCharacterTypeList(Arrays.asList(new String[]{"Default",
			"MyType"}));
		finishLoad();
		pc.setCharacterType("MyType");
		runRoundRobin();
	}

	@Test
	public void testNotes()
	{
		finishLoad();
		pc.addNotesItem(new NoteItem(1, -1, "NoteName", "NoteValue"));
		runRoundRobin();
	}

	@Test
	public void testNotesAddRemove()
	{
		finishLoad();
		NoteItem item = new NoteItem(1, -1, "NoteName", "NoteValue");
		pc.addNotesItem(item);
		pc.removeNote(item);
		runRoundRobin();
	}

	@Test
	public void testSkill()
	{
		create(Skill.class, "MySkill");
		finishLoad();
		runRoundRobin();
	}

	//TODO need to test different types of skills:
	/*
	 * Ranked CLASS CROSS_CLASS EXCLUSIVE (had and not) QUALIFIED (PRE) NOT
	 * QUALIFIED (!PRE)
	 */

	@Test
	public void testStatRank()
	{
		finishLoad();
		pc.setStat(str, 14);
		pc.setStat(intel, 15);
		pc.setStat(wis, 16);
		pc.setStat(dex, 17);
		pc.setStat(con, 18);
		pc.setStat(cha, 19);
		runRoundRobin();
	}

	@Test
	public void testRaceMonCSkill()
	{
		PCClass monclass = create(PCClass.class, "MonClass");
		new TypeLst().parseToken(context, monclass, "Monster");
		Skill monskill = create(Skill.class, "MonSkill");
		new ExclusiveToken().parseToken(context, monskill, "Yes");
		Race monster = create(Race.class, "Monster");
		Skill skill = create(Skill.class, "MySkill");
		new ExclusiveToken().parseToken(context, skill, "Yes");
		new MoncskillToken().parseToken(context, monster, "LIST");
		new SkillToken().parseToken(context, monster, "MonSkill|MySkill");
		finishLoad();
		pc.setRace(monster);
		runRoundRobin();
		assertEquals(SkillCost.CLASS,
			reloadedPC.getSkillCostForClass(monskill, monclass));
	}

	@Test
	public void testGlobalCSkill()
	{
		PCClass monclass = create(PCClass.class, "MonClass");
		new TypeLst().parseToken(context, monclass, "Monster");
		Skill monskill = create(Skill.class, "MonSkill");
		new ExclusiveToken().parseToken(context, monskill, "Yes");
		Race monster = create(Race.class, "Monster");
		Skill skill = create(Skill.class, "MySkill");
		new ExclusiveToken().parseToken(context, skill, "Yes");
		new CskillLst().parseToken(context, monster, "LIST");
		new SkillToken().parseToken(context, monster, "MonSkill|MySkill");
		finishLoad();
		pc.setRace(monster);
		runRoundRobin();
		assertEquals(SkillCost.CLASS,
			reloadedPC.getSkillCostForClass(monskill, monclass));
	}

	@Test
	public void testGlobalCCSkill()
	{
		PCClass myclass = create(PCClass.class, "MyClass");
		Skill monskill = create(Skill.class, "MonSkill");
		new ExclusiveToken().parseToken(context, monskill, "Yes");
		Race monster = create(Race.class, "Monster");
		create(Skill.class, "MySkill");
		new CcskillLst().parseToken(context, monster, "LIST");
		new SkillToken().parseToken(context, monster, "MonSkill|MySkill");
		finishLoad();
		pc.setRace(monster);
		runRoundRobin();
		assertEquals(SkillCost.CROSS_CLASS,
			reloadedPC.getSkillCostForClass(monskill, myclass));
	}
}
