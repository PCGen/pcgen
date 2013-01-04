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

import org.junit.Test;

import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.choose.ClassToken;
import plugin.lsttokens.choose.SkillToken;
import plugin.lsttokens.race.FavclassToken;
import plugin.lsttokens.race.MoncskillToken;
import plugin.lsttokens.skill.ExclusiveToken;

public class RaceTargetSaveRestoreTest extends
		AbstractGlobalTargetedSaveRestoreTest<Race>
{

	@Override
	public Class<Race> getObjectClass()
	{
		return Race.class;
	}

	@Override
	protected void applyObject(Race obj)
	{
		pc.setRace(obj);
	}

	@Override
	protected Object prepare(Race obj)
	{
		return create(Race.class, "Other");
	}

	@Override
	protected void remove(Object o)
	{
		reloadedPC.setRace((Race) o);
	}

	@Test
	public void testRaceMonCSkill()
	{
		PCClass monclass = create(PCClass.class, "MonClass");
		new TypeLst().parseToken(context, monclass, "Monster");
		Skill monskill = create(Skill.class, "MonSkill");
		new ExclusiveToken().parseToken(context, monskill, "Yes");
		Race monster = create(Race.class, "Monster");
		Race other = create(Race.class, "Other");
		Skill skill = create(Skill.class, "MySkill");
		new ExclusiveToken().parseToken(context, skill, "Yes");
		new MoncskillToken().parseToken(context, monster, "LIST");
		new SkillToken().parseToken(context, monster, "MonSkill|MySkill");
		finishLoad();
		pc.setRace(monster);
		runRoundRobin();
		assertEquals(SkillCost.CLASS,
			pc.getSkillCostForClass(monskill, monclass));
		assertEquals(SkillCost.CLASS,
			reloadedPC.getSkillCostForClass(monskill, monclass));
		reloadedPC.setRace(other);
		reloadedPC.setDirty(true);
		assertEquals(SkillCost.EXCLUSIVE,
			reloadedPC.getSkillCostForClass(monskill, monclass));
	}

	@Test
	public void testRaceFavClass()
	{
		PCClass monclass = create(PCClass.class, "MonClass");
		new TypeLst().parseToken(context, monclass, "Monster");
		Race monster = create(Race.class, "Monster");
		Race other = create(Race.class, "Other");
		create(PCClass.class, "MyClass");
		new FavclassToken().parseToken(context, monster, "%LIST");
		new ClassToken().parseToken(context, monster, "MonClass|MyClass");
		finishLoad();
		pc.setRace(monster);
		runRoundRobin();
		assertTrue(pc.getDisplay().getFavoredClasses().contains(monclass));
		assertTrue(reloadedPC.getDisplay().getFavoredClasses()
			.contains(monclass));
		reloadedPC.setRace(other);
		reloadedPC.setDirty(true);
		assertFalse(reloadedPC.getDisplay().getFavoredClasses()
			.contains(monclass));
	}
}
