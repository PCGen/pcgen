/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 */
package plugin.pretokens.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

public class PreSkillTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    @SuppressWarnings("unused")
    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
    {
        CharacterDisplay display = character.getDisplay();
        final int requiredRanks = Integer.parseInt(prereq.getOperand());
        // Compute the skill name from the Prerequisite
        String requiredSkillKey = prereq.getKey().toUpperCase();
        if (prereq.getSubKey() != null)
        {
            requiredSkillKey += " (" + prereq.getSubKey().toUpperCase() + ')'; //$NON-NLS-1$
        }
        final boolean isType =
                (requiredSkillKey.startsWith("TYPE.")
                        || requiredSkillKey.startsWith("TYPE=")); //$NON-NLS-1$ //$NON-NLS-2$
        if (isType)
        {
            requiredSkillKey = requiredSkillKey.substring(5);
        }
        final String skillKey = requiredSkillKey;
        // Now locate all instances of this skillname and test them
        final int percentageSignPosition = skillKey.lastIndexOf('%');
        HashMap<Skill, Set<Skill>> serveAsSkills = new HashMap<>();
        Set<Skill> imitators = new HashSet<>();
        this.getImitators(serveAsSkills, imitators, display);
        int runningTotal = 0;
        boolean foundMatch = false;
        boolean foundSkill = false;
        for (Skill aSkill : display.getSkillSet())
        {
            final String aSkillKey = aSkill.getKeyName().toUpperCase();
            if (isType)
            {
                if (percentageSignPosition >= 0)
                {
                    foundMatch = matchesTypeWildCard(skillKey, percentageSignPosition, foundSkill, aSkill);
                    foundSkill = foundMatch;
                    runningTotal = getRunningTotal(aSkill, character, prereq, foundMatch, runningTotal, requiredRanks);
                } else if (aSkill.isType(skillKey))
                {
                    foundMatch = true;
                    foundSkill = true;
                    runningTotal = getRunningTotal(aSkill, character, prereq, foundMatch, runningTotal, requiredRanks);
                }
                // If there wasn't a match, then check other skills of the type
                if (runningTotal == 0)
                {
                    foundMatch = false;
                }
            } else if (aSkillKey.equals(skillKey) || ((percentageSignPosition >= 0)
                    && aSkillKey.startsWith(skillKey.substring(0, percentageSignPosition))))
            {
                foundMatch = true;
                foundSkill = true;
                runningTotal = getRunningTotal(aSkill, character, prereq, foundMatch, runningTotal, requiredRanks);
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
        if (!isType && !foundSkill)
        {
            for (final Map.Entry<Skill, Set<Skill>> entry : serveAsSkills.entrySet())
            {
                Skill mock = entry.getKey();
                Set<Skill> targets = entry.getValue();
                for (Skill target : targets)
                {
                    if (foundSkill)
                    {
                        break;
                    }
                    final String aSkillKey = target.getKeyName().toUpperCase();
                    if (target.getDisplayName().equalsIgnoreCase(skillKey))
                    {
                        foundSkill = true;
                        foundMatch = true;
                        int theTotal =
                                getRunningTotal(mock, character, prereq, foundMatch, runningTotal, requiredRanks);
                        runningTotal += theTotal;
                    } else if (aSkillKey.equals(skillKey) || ((percentageSignPosition >= 0)
                            && aSkillKey.startsWith(skillKey.substring(0, percentageSignPosition))))
                    {
                        foundSkill = true;
                        foundMatch = true;
                        int theTotal =
                                getRunningTotal(mock, character, prereq, foundMatch, runningTotal, requiredRanks);
                        runningTotal += theTotal;
                    }
                }
            }
        } else if (isType && !foundSkill)
        {
            for (final Map.Entry<Skill, Set<Skill>> entry : serveAsSkills.entrySet())
            {
                Skill mock = entry.getKey();
                Set<Skill> targets = entry.getValue();
                for (Skill target : targets)
                {
                    if (foundSkill)
                    {
                        break;
                    }
                    if (target.isType(skillKey))
                    {
                        foundSkill = true;
                        foundMatch = true;
                        int theTotal =
                                getRunningTotal(mock, character, prereq, foundMatch, runningTotal, requiredRanks);
                        runningTotal += theTotal;
                    } else if ((percentageSignPosition >= 0))
                    {
                        List<Type> mockTypes = target.getTrueTypeList(true);
                        for (Type mockType : mockTypes)
                        {
                            foundMatch = matchesTypeWildCard(skillKey, percentageSignPosition, foundSkill, target);
                            foundSkill = foundMatch;
                            runningTotal =
                                    getRunningTotal(mock, character, prereq, foundMatch, runningTotal, requiredRanks);
                            if (foundSkill)
                            {
                                break;
                            }
                        }
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

    private void getImitators(HashMap<Skill, Set<Skill>> serveAsSkills, Set<Skill> imitators, CharacterDisplay display)
    {
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
                imitators.add(aSkill);
                serveAsSkills.put(aSkill, servesAs);
            }
        }
    }

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "SKILL"; //$NON-NLS-1$
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

    /**
     * Check if the skill's types match the supplied pattern.
     * <p>
     * Mar 6, 2008 - Joe.Frazier
     *
     * @param skillKey               Upper case key to be matched.
     * @param percentageSignPosition Spot in the key which has the percentage sign
     * @param found                  Has a match already been found?
     * @param aSkill                 The skill to be checked.
     * @return boolean.
     */
    private boolean matchesTypeWildCard(final String skillKey, final int percentageSignPosition, boolean found,
            Skill aSkill)
    {
        for (Type type : aSkill.getTrueTypeList(false))
        {
            if (type.toString().toUpperCase().startsWith(skillKey.substring(0, percentageSignPosition)))
            {
                found = true;
                break;
            }
        }
        return found;
    }

    private int getRunningTotal(Skill aSkill, PlayerCharacter character, Prerequisite prereq, boolean foundMatch,
            int runningTotal, int requiredRanks)
    {
        if (foundMatch)
        {
            if (prereq.isTotalValues())
            {
                runningTotal += SkillRankControl.getTotalRank(character, aSkill).intValue();
            } else
            {
                if (prereq.getOperator().compare(SkillRankControl.getTotalRank(character, aSkill).intValue(),
                        requiredRanks) > 0)
                {
                    runningTotal++;
                }
            }
        }
        return runningTotal;
    }
}
