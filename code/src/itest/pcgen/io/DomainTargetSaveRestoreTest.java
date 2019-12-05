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

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;
import plugin.lsttokens.choose.SkillToken;
import plugin.lsttokens.domain.CcskillToken;
import plugin.lsttokens.domain.CskillToken;
import plugin.lsttokens.pcclass.HdToken;
import plugin.lsttokens.skill.ExclusiveToken;

import org.junit.jupiter.api.Test;

public class DomainTargetSaveRestoreTest extends
        AbstractGlobalTargetedSaveRestoreTest<Domain>
{

    @Override
    public Class<Domain> getObjectClass()
    {
        return Domain.class;
    }

    @Override
    protected void applyObject(Domain obj)
    {
        PCClass cl =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                        "MyClass");
        pc.addClass(cl);
        pc.incrementClassLevel(1, cl);
        pc.setHP(pc.getActiveClassLevel(cl, 0), 4);
        pc.addDomain(obj, new ClassSource(cl, 1));
    }

    @Override
    protected Object prepare(Domain obj)
    {
        PCClass cl = create(PCClass.class, "MyClass");
        new HdToken().parseToken(context, cl, "6");
        return obj;
    }

    @Override
    protected void remove(Object o)
    {
        reloadedPC.removeDomain((Domain) o);
    }

    @Test
    public void testDomainCSkill()
    {
        Skill granted = create(Skill.class, "Granted");
        new ExclusiveToken().parseToken(context, granted, "Yes");
        Domain target = create(getObjectClass(), "Target");
        create(Skill.class, "MySkill");
        new CskillToken().parseToken(context, target, "LIST");
        new SkillToken().parseToken(context, target, "Granted|MySkill");
        Object o = prepare(target);
        finishLoad();
        applyObject(target);
        PCClass myclass = pc.getClassKeyed("MyClass");
        assertEquals(SkillCost.CLASS,
                pc.getSkillCostForClass(granted, myclass));
        runRoundRobin(getPreEqualityCleanup());
        assertEquals(SkillCost.CLASS,
                pc.getSkillCostForClass(granted, myclass));
        myclass = reloadedPC.getClassKeyed("MyClass");
        assertEquals(SkillCost.CLASS,
                reloadedPC.getSkillCostForClass(granted, myclass));
        remove(o);
        reloadedPC.setDirty(true);
        assertEquals(SkillCost.EXCLUSIVE,
                reloadedPC.getSkillCostForClass(granted, myclass));
    }

    @Test
    public void testDomainCCSkill()
    {
        Skill granted = create(Skill.class, "Granted");
        new ExclusiveToken().parseToken(context, granted, "Yes");
        Domain target = create(getObjectClass(), "Target");
        create(Skill.class, "MySkill");
        new CcskillToken().parseToken(context, target, "LIST");
        new SkillToken().parseToken(context, target, "Granted|MySkill");
        Object o = prepare(target);
        finishLoad();
        applyObject(target);
        PCClass myclass = pc.getClassKeyed("MyClass");
        assertEquals(SkillCost.CROSS_CLASS,
                pc.getSkillCostForClass(granted, myclass));
        runRoundRobin(getPreEqualityCleanup());
        assertEquals(SkillCost.CROSS_CLASS,
                pc.getSkillCostForClass(granted, myclass));
        myclass = reloadedPC.getClassKeyed("MyClass");
        assertEquals(SkillCost.CROSS_CLASS,
                reloadedPC.getSkillCostForClass(granted, myclass));
        remove(o);
        reloadedPC.setDirty(true);
        assertEquals(SkillCost.EXCLUSIVE,
                reloadedPC.getSkillCostForClass(granted, myclass));
    }
}
