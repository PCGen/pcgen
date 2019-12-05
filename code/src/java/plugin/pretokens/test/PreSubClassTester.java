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
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCClass;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

/**
 * Prerequisite tester, tests for the presence of a subclass.
 */
public class PreSubClassTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
            throws PrerequisiteException
    {
        int runningTotal = 0;
        int num;
        try
        {
            num = Integer.parseInt(prereq.getOperand()); // number we must match
        } catch (NumberFormatException nfe)
        {
            throw new PrerequisiteException(
                    LanguageBundle.getFormattedString("PreSubClass.error.badly_formed", prereq.toString()), nfe); //$NON
            // -NLS-1$
        }

        final String thisClass = prereq.getKey();
        for (PCClass aClass : display.getClassSet())
        {
            final String subClassName = display.getSubClassName(aClass);
            if (subClassName != null && !subClassName.isEmpty())
            {
                if (thisClass.equalsIgnoreCase(subClassName))
                {
                    runningTotal++;
                    break;
                }
            }
        }

        runningTotal = prereq.getOperator().compare(runningTotal, num);
        return countedTotal(prereq, runningTotal);
    }

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "SUBCLASS"; //$NON-NLS-1$
    }

}
