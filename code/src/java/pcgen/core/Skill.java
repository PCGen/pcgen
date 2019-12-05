/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import pcgen.base.formula.Formula;
import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.bonus.BonusObj;

/**
 * {@code Skill}.
 */
public final class Skill extends PObject implements ChooseDriver, VarScoped
{
    public String getKeyStatAbb()
    {
        CDOMSingleRef<PCStat> keyStat = get(ObjectKey.KEY_STAT);
        return keyStat == null ? "" : keyStat.get().getKeyName();
    }

    @Override
    public boolean equals(final Object obj)
    {
        return obj instanceof Skill && getKeyName().equalsIgnoreCase(((Skill) obj).getKeyName());
    }

    @Override
    public int hashCode()
    {
        return getKeyName().hashCode();
    }

    @Override
    public List<BonusObj> getRawBonusList(PlayerCharacter pc)
    {
        List<BonusObj> list = new ArrayList<>(super.getRawBonusList(pc));
        list.sort(new SkillBonusComparator(this));
        return list;
    }

    /**
     * A comparator for sorting bonuses which puts the bonuses in the order
     * bonuses to this skill, bonuses without prereqs, bonuses with prereqs.
     */
    public static final class SkillBonusComparator implements Comparator<BonusObj>
    {

        private final Skill skill;

        private SkillBonusComparator(Skill skill)
        {
            this.skill = skill;

        }

        @Override
        public int compare(BonusObj arg0, BonusObj arg1)
        {
            boolean arg0BonusThisSkill = bonusToThisSkill(arg0);
            boolean arg1BonusThisSkill = bonusToThisSkill(arg1);
            if (arg0BonusThisSkill != arg1BonusThisSkill)
            {
                if (arg0BonusThisSkill)
                {
                    return -1;
                }
                return 1;
            }
            if (arg0.hasPrerequisites() != arg1.hasPrerequisites())
            {
                if (arg1.hasPrerequisites())
                {
                    return -1;
                }
                return 1;
            }

            return arg0.toString().compareTo(arg1.toString());
        }

        private boolean bonusToThisSkill(BonusObj bonus)
        {
            if (!"SKILL".equals(bonus.getBonusName()))
            {
                return false;
            }
            for (Object target : bonus.getBonusInfoList())
            {
                if (String.valueOf(target).equalsIgnoreCase(skill.getKeyName()))
                {
                    return true;
                }
            }
            return false;
        }

    }

    @Override
    public ChooseInformation<?> getChooseInfo()
    {
        return get(ObjectKey.CHOOSE_INFO);
    }

    @Override
    public Formula getSelectFormula()
    {
        return getSafe(FormulaKey.SELECT);
    }

    @Override
    public List<ChooseSelectionActor<?>> getActors()
    {
        return getListFor(ListKey.NEW_CHOOSE_ACTOR);
    }

    @Override
    public String getFormulaSource()
    {
        return getKeyName();
    }

    @Override
    public Formula getNumChoices()
    {
        return getSafe(FormulaKey.NUMCHOICES);
    }

    @Override
    public Optional<String> getLocalScopeName()
    {
        return Optional.of("PC.SKILL");
    }
}
