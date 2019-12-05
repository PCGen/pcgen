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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.Assert.assertTrue;

import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.choose.ClassToken;
import plugin.lsttokens.choose.SkillToken;
import plugin.lsttokens.pcclass.HdToken;
import plugin.lsttokens.pcclass.IsmonsterToken;
import plugin.lsttokens.race.FavclassToken;
import plugin.lsttokens.race.MoncskillToken;
import plugin.lsttokens.skill.ExclusiveToken;

import org.junit.jupiter.api.Test;

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
        new HdToken().parseToken(context, monclass, "8");
        new IsmonsterToken().parseToken(context, monclass, "YES");
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
        pc.incrementClassLevel(1, monclass);
        pc.setHP(pc.getActiveClassLevel(monclass, 0), 3);
        final Runnable cleanup = getPreEqualityCleanup();
        Runnable fullcleanup = () -> {
            if (cleanup != null)
            {
                cleanup.run();
            }
            //TODO need this to create the spell support :/
            PCClass cl =
                    context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                            "MonClass");
            reloadedPC.getSpellSupport(cl);
        };
        runRoundRobin(fullcleanup);
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
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.getDisplay().getFavoredClasses().contains(monclass));
        assertTrue(reloadedPC.getDisplay().getFavoredClasses()
                .contains(monclass));
        reloadedPC.setRace(other);
        reloadedPC.setDirty(true);
        assertFalse(reloadedPC.getDisplay().getFavoredClasses()
                .contains(monclass));
    }
}
