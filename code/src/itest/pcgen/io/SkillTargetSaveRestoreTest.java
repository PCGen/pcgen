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
import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;
import plugin.lsttokens.pcclass.HdToken;

public class SkillTargetSaveRestoreTest extends
        AbstractGlobalTargetedSaveRestoreTest<Skill>
{

    @Override
    public Class<Skill> getObjectClass()
    {
        return Skill.class;
    }

    @Override
    protected void applyObject(Skill obj)
    {
        PCClass cl =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                        "MyClass");
        pc.addClass(cl);
        pc.incrementClassLevel(1, cl);
        pc.setHP(pc.getActiveClassLevel(cl, 0), 4);
        SkillRankControl.modRanks(1.0, cl, true, pc, obj);
        SkillRankControl.getSkillRankBonusTo(pc, obj);
    }

    @Override
    protected Object prepare(Skill obj)
    {
        obj.put(ObjectKey.EXCLUSIVE, true);
        PCClass cl = create(PCClass.class, "MyClass");
        new HdToken().parseToken(context, cl, "6");
        return obj;
    }

    @Override
    protected void remove(Object o)
    {
        PCClass cl =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                        "MyClass");
        Skill sk = (Skill) o;
        SkillRankControl.modRanks(-1.0, cl, true, reloadedPC, sk);
        assertEquals(0.0f, reloadedPC.getRank(sk), 0.0);
        SkillRankControl.getSkillRankBonusTo(reloadedPC, sk);
        assertFalse(reloadedPC.hasSkill(sk));
    }

    @Override
    protected Runnable getPreEqualityCleanup()
    {
        final Runnable sup = super.getPreEqualityCleanup();
        return () -> {
            if (sup != null)
            {
                sup.run();
            }
            //TODO need this to create the spell support :/
            PCClass cl =
                    context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                            "MyClass");
            reloadedPC.getSpellSupport(cl);
        };
    }

    //CODE-2015 needs to ensure this gets removed...
    @Override
    protected boolean isSymmetric()
    {
        return false;
    }

}
