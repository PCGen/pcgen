/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.display;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillsOutputOrder;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.SkillComparator;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

/**
 * Utility class for skill UI tasks
 */
public final class SkillDisplay
{

    private SkillDisplay()
    {
    }

    /**
     * Retrieves a list of the character's skills in output order. This is in
     * ascending order of the skill's outputIndex field. If skills have the same
     * outputIndex they will be ordered by name. Note hidden skills (outputIndex =
     * -1) are not included in this list.
     *
     * @param skills A list of skills which will be sorted, filtered and returned
     * @return An ArrayList of the skill objects in output order.
     */
    public static List<Skill> getSkillListInOutputOrder(final PlayerCharacter pc, final List<Skill> skills)
    {
        skills.sort(new Comparator<>()
        {
            /**
             * Comparator will be specific to Skill objects
             */
            @Override
            public int compare(final Skill skill1, final Skill skill2)
            {
                Integer obj1Index = pc.getSkillOrder(skill1);
                Integer obj2Index = pc.getSkillOrder(skill2);

                // Force unset items (index of 0) to appear at the end
                if (obj1Index == null || obj1Index == 0)
                {
                    obj1Index = Constants.ARBITRARY_END_SKILL_INDEX;
                }

                if (obj2Index == null || obj2Index == 0)
                {
                    obj2Index = Constants.ARBITRARY_END_SKILL_INDEX;
                }

                if (obj1Index > obj2Index)
                {
                    return 1;
                } else if (obj1Index < obj2Index)
                {
                    return -1;
                } else
                {
                    return skill1.getOutputName().compareToIgnoreCase(skill2.getOutputName());
                }
            }
        });

        // Remove the hidden skills from the list
        for (Iterator<Skill> i = skills.iterator();i.hasNext();)
        {
            final Skill bSkill = i.next();

            Visibility skVis = bSkill.getSafe(ObjectKey.VISIBILITY);
            Integer outputIndex = pc.getSkillOrder(bSkill);
            if ((outputIndex != null && outputIndex == -1) || skVis.isVisibleTo(View.HIDDEN_EXPORT)
                    || !bSkill.qualifies(pc, null))
            {
                i.remove();
            }
        }

        return skills;
    }

    /**
     * Retrieves a list of the character's skills in output order. This is in
     * ascending order of the skill's outputIndex field. If skills have the same
     * outputIndex they will be ordered by name. Note hidden skills (outputIndex =
     * -1) are not included in this list.
     *
     * @return An ArrayList of the skill objects in output order.
     */
    public static List<Skill> getSkillListInOutputOrder(PlayerCharacter pc)
    {
        return getSkillListInOutputOrder(pc, new ArrayList<>(pc.getSkillSet()));
    }

    public static void updateSkillsOutputOrder(PlayerCharacter pc, Skill aSkill)
    {
        // in order to get the selected table to sort properly
        // we need to sort the PC's skill list now that the
        // new skill has been added, this won't get called
        // when adding a rank to an existing skill
        //		Collections.sort(theCharacter.getSkillList(),
        //			new StringIgnoreCaseComparator());

        // Now re calc the output order
        if (pc.getSkillsOutputOrder() == SkillsOutputOrder.MANUAL)
        {
            Integer outputIndex = pc.getSkillOrder(aSkill);
            if (outputIndex == null || outputIndex == 0)
            {
                pc.setSkillOrder(aSkill, getHighestOutputIndex(pc) + 1);
            }
        } else
        {
            resortSelected(pc, pc.getSkillsOutputOrder());
        }
    }

    public static void resortSelected(PlayerCharacter pc, SkillsOutputOrder sortSelection)
    {
        resortSelected(pc, sortSelection.getComparator(pc));
    }

    private static void resortSelected(PlayerCharacter pc, SkillComparator comparator)
    {
        if ((pc == null) || (comparator == null))
        {
            return;
        }
        List<Skill> skillList = new ArrayList<>(pc.getSkillSet());
        skillList.sort(comparator);

        int nextOutputIndex = 1;
        for (final Skill aSkill : skillList)
        {
            Integer outputIndex = pc.getSkillOrder(aSkill);
            if ((outputIndex == null) || (outputIndex >= 0))
            {
                pc.setSkillOrder(aSkill, nextOutputIndex++);
            }
        }
    }

    /**
     * Retrieve the highest output index used in any of the
     * character's skills.
     *
     * @return highest output index
     */
    private static int getHighestOutputIndex(PlayerCharacter pc)
    {
        int maxOutputIndex = 0;
        final Iterable<Skill> skillList = new ArrayList<>(pc.getSkillSet());
        for (Skill bSkill : skillList)
        {
            Integer outputIndex = pc.getSkillOrder(bSkill);
            if (outputIndex != null && outputIndex > maxOutputIndex)
            {
                maxOutputIndex = outputIndex;
            }
        }

        return maxOutputIndex;
    }
}
