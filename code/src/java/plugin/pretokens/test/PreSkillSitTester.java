/*
 * Copyright 2014 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.pretokens.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

public class PreSkillSitTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
    {
        CharacterDisplay display = character.getDisplay();
        final int requiredRanks = Integer.parseInt(prereq.getOperand());

        // Compute the skill name from the Prerequisite
        String requiredSkill = prereq.getCategoryName();
        String situation = prereq.getKey();

        Map<Skill, Set<Skill>> serveAsSkills = getImitators(display);

        int runningTotal = 0;

        boolean foundMatch = false;
        boolean foundSkill = false;

        for (Skill aSkill : display.getSkillSet())
        {
            final String aSkillKey = aSkill.getKeyName().toUpperCase();
            if (aSkillKey.equals(requiredSkill))
            {
                foundMatch = true;
                foundSkill = true;
                runningTotal =
                        getRunningTotal(aSkill, character, prereq, foundMatch, runningTotal, requiredRanks, situation);
            }

            if (prereq.isCountMultiples() || prereq.isTotalValues())
            {
                // For counted totals we want to count all occurances, not just the first
                foundMatch = false;
            }
            if (foundMatch)
            {
                break;
            }
        }
        if (!foundSkill)
        {
            for (final Map.Entry<Skill, Set<Skill>> entry : serveAsSkills.entrySet())
            {
                Set<Skill> targets = entry.getValue();
                for (Skill target : targets)
                {
                    if (foundSkill)
                    {
                        break;
                    }
                    final String aSkillKey = target.getKeyName().toUpperCase();
                    if (aSkillKey.equals(requiredSkill))
                    {
                        foundSkill = true;
                        foundMatch = true;
                        int theTotal = getRunningTotal(
                                entry.getKey(), character, prereq, foundMatch, runningTotal, requiredRanks,
                                situation);
                        runningTotal += theTotal;

                    }
                }
            }
        }

        // If we are looking for a negative test i.e. !PRESKILL and the PC
        // doesn't have the skill we have to return a match
        if (!foundSkill)
        {
            if (prereq.getOperator() == PrerequisiteOperator.LT)
            {
                runningTotal++;
            }
        }
        return countedTotal(prereq, runningTotal);
    }

    private Map<Skill, Set<Skill>> getImitators(CharacterDisplay display)
    {
        HashMap<Skill, Set<Skill>> serveAsSkills = new HashMap<>();
        Set<Skill> skillSet = new HashSet<>(display.getSkillSet());
        for (Skill aSkill : skillSet)
        {
            Set<Skill> servesAs = new HashSet<>();
            for (CDOMReference<Skill> ref : aSkill.getSafeListFor(ListKey.SERVES_AS_SKILL))
            {
                servesAs.addAll(ref.getContainedObjects());
            }

            if (!servesAs.isEmpty())
            {
                serveAsSkills.put(aSkill, servesAs);
            }
        }
        return serveAsSkills;
    }

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "SKILLSIT"; //$NON-NLS-1$
    }

    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        String skillName = prereq.getKey();
        if (prereq.getSubKey() != null && !prereq.getSubKey().equals("")) //$NON-NLS-1$
        {
            skillName += " (" + prereq.getSubKey() + ')'; //$NON-NLS-1$

        }

        final String foo = LanguageBundle.getFormattedString("PreSkill.toHtml", //$NON-NLS-1$
                prereq.getOperator().toDisplayString(), prereq.getOperand(), skillName);
        return foo;
    }

    private int getRunningTotal(Skill aSkill, PlayerCharacter character, Prerequisite prereq, boolean foundMatch,
            int runningTotal, int requiredRanks, String situation)
    {
        if (foundMatch)
        {
            int rank = SkillRankControl.getTotalRank(character, aSkill).intValue();
            rank += character.getTotalBonusTo("SITUATION", aSkill.getKeyName() + '=' + situation);
            if (prereq.isTotalValues())
            {
                runningTotal += rank;
            } else
            {
                if (prereq.getOperator().compare(rank, requiredRanks) > 0)
                {
                    runningTotal++;
                }
            }
        }
        return runningTotal;
    }
}
