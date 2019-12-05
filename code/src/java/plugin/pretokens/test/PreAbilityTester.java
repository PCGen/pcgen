/*
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.system.LanguageBundle;

/**
 * {@code PreAbilityParser} tests whether a character passes ability
 * prereqs.
 */
public class PreAbilityTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final Equipment equipment, final PlayerCharacter aPC)
            throws PrerequisiteException
    {
        if (aPC == null)
        {
            return 0;
        }
        return passes(prereq, aPC, equipment);
    }

    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
            throws PrerequisiteException
    {
        final int number;
        try
        {
            number = Integer.parseInt(prereq.getOperand());
        } catch (NumberFormatException exception)
        {
            throw new PrerequisiteException(
                    LanguageBundle.getFormattedString("PreAbility.error", prereq.toString()), exception); //$NON-NLS-1$
        }

        String categoryName = prereq.getCategoryName();

        int runningTotal = PrerequisiteUtilities.passesAbilityTest(prereq, character, number, categoryName);

        return countedTotal(prereq, runningTotal);
    }

    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        String aString = prereq.getKey();
        if ((prereq.getSubKey() != null) && !prereq.getSubKey().equals(""))
        {
            aString = aString + " ( " + prereq.getSubKey() + " )";
        }

        if (aString.startsWith("TYPE=")) //$NON-NLS-1$
        {
            if (!prereq.getCategoryName().isEmpty())
            {
                // {0} {1} {2}(s) of type {3}
                return LanguageBundle.getFormattedString("PreAbility.type.toHtml", //$NON-NLS-1$
                        prereq.getOperator().toDisplayString(), prereq.getOperand(), prereq.getCategoryName(),
                        aString.substring(5));
            } else
            {
                // {0} {1} ability(s) of type {2}
                return LanguageBundle.getFormattedString("PreAbility.type.noCat.toHtml", //$NON-NLS-1$
                        prereq.getOperator().toDisplayString(), prereq.getOperand(), aString.substring(5));
            }

        }
        // {2} {3} {1} {0}
        return LanguageBundle.getFormattedString("PreAbility.toHtml", //$NON-NLS-1$
                prereq.getCategoryName(), aString, prereq.getOperator().toDisplayString(), prereq.getOperand());
    }

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "ABILITY"; //$NON-NLS-1$
    }

}
