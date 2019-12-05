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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.LimitedVarHolder;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.bonus.BonusObj;
import pcgen.util.enumeration.View;

/**
 * The CDOMObject for Templates.
 */
public final class PCTemplate extends PObject implements ChooseDriver, LimitedVarHolder
{
    /**
     * Get the total adjustment to Challenge rating of a character at a given
     * level (Class and Hit Dice). This will include the absolute adjustment
     * made with {@literal CR:, LEVEL:<num>:CR and HD:<num>:CR tags}
     *
     * @param level   The level to calculate the adjustment for
     * @param hitdice The Hit dice to calculate the adjustment for
     * @return a Challenge Rating adjustment
     */
    public Integer getCR(final int level, final int hitdice)
    {
        return getSafe(ObjectKey.CR_MODIFIER).intValue()
                + getConditionalTemplates(level, hitdice).stream()
                .mapToInt(pct -> pct.getSafe(ObjectKey.CR_MODIFIER).intValue())
                .sum();
    }

    /**
     * Query whether this Template is removable. Factors in the visibility of
     * the Template
     *
     * @return whether this Template is removable
     */
    public boolean isRemovable()
    {
        boolean result = false;

        if (getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_DISPLAY))
        {
            result = getSafe(ObjectKey.REMOVABLE);
        }

        return result;
    }

    public List<PCTemplate> getConditionalTemplates(int totalLevels, int totalHitDice)
    {
        List<PCTemplate> returnList = new ArrayList<>();

        for (PCTemplate rlt : getSafeListFor(ListKey.REPEATLEVEL_TEMPLATES))
        {
            for (PCTemplate lt : rlt.getSafeListFor(ListKey.LEVEL_TEMPLATES))
            {
                if (lt.get(IntegerKey.LEVEL) <= totalLevels)
                {
                    returnList.add(lt);
                }
            }
        }

        for (PCTemplate lt : getSafeListFor(ListKey.LEVEL_TEMPLATES))
        {
            if (lt.get(IntegerKey.LEVEL) <= totalLevels)
            {
                returnList.add(lt);
            }
        }

        for (PCTemplate lt : getSafeListFor(ListKey.HD_TEMPLATES))
        {
            if (lt.get(IntegerKey.HD_MAX) >= totalHitDice && lt.get(IntegerKey.HD_MIN) <= totalHitDice)
            {
                returnList.add(lt);
            }
        }
        return returnList;
    }

    @Override
    public List<BonusObj> getRawBonusList(PlayerCharacter pc)
    {
        List<BonusObj> list = new ArrayList<>(super.getRawBonusList(pc));
        /*
         * TODO Does this require a test of getTotalLevels() totalHitDice() on
         * the PC?
         *
         * for (PCTemplate pct : getConditionalTemplates(pc.getTotalLevels(),
         * pc.totalHitDice())) { list.addAll(pct.getRawBonusList(pc); }
         */
        for (PCTemplate rlt : getSafeListFor(ListKey.REPEATLEVEL_TEMPLATES))
        {
            for (PCTemplate lt : rlt.getSafeListFor(ListKey.LEVEL_TEMPLATES))
            {
                list.addAll(lt.getRawBonusList(pc));
            }
        }

        for (PCTemplate lt : getSafeListFor(ListKey.LEVEL_TEMPLATES))
        {
            list.addAll(lt.getRawBonusList(pc));
        }

        for (PCTemplate lt : getSafeListFor(ListKey.HD_TEMPLATES))
        {
            list.addAll(lt.getRawBonusList(pc));
        }
        // end potential TO-DO change
        return list;
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
    public String getIdentifier()
    {
        return "TEMPLATE";
    }
}
