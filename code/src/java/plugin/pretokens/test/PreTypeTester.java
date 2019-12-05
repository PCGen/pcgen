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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * Prerequisite tester, tests for the presence of a type.
 */
public class PreTypeTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "TYPE"; //$NON-NLS-1$
    }

    @Override
    public int passes(final Prerequisite prereq, final Equipment equipment, CharacterDisplay display)
            throws PrerequisiteException
    {

        final String requiredType = prereq.getKey();
        int runningTotal = 0;

        if (prereq.getOperator().equals(PrerequisiteOperator.EQ))
        {
            if (equipment.isPreType(requiredType))
            {
                runningTotal++;
            }
        } else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ))
        {
            if (!equipment.isPreType(requiredType))
            {
                runningTotal++;
            }
        } else
        {
            throw new PrerequisiteException(
                    LanguageBundle.getFormattedString("PreType.error.invalidComparison", //$NON-NLS-1$
                            prereq.getOperator().toString(), prereq.toString()));
        }

        runningTotal = countedTotal(prereq, runningTotal);
        return runningTotal;
    }

    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
    {
        Logging.errorPrint("PRETYPE has been deprecated for non-Equipment Prerequisites."
                + "\n  Please use PRERACE as an alternative");
        if (display == null)
        {
            return 0;
        }

        final String requiredType = prereq.getKey();

        Logging.errorPrint("  PRETYPE value was: " + requiredType + '\n');

        final int numRequired = Integer.parseInt(prereq.getOperand());
        int runningTotal = 0;

        for (String element : getTypes(display))
        {
            if (element.equalsIgnoreCase(requiredType))
            {
                runningTotal++;
            }
        }

        runningTotal = prereq.getOperator().compare(runningTotal, numRequired);
        return countedTotal(prereq, runningTotal);
    }

    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        return LanguageBundle.getFormattedString("PreType.toHtml", prereq.getOperator().toDisplayString(), //$NON-NLS-1$
                prereq.getKey());
    }

    /**
     * Get a list of types that apply to this character.
     *
     * @return a List of Strings where each String is a type that the character
     * has. The list returned will never be null
     * @deprecated Use getRaceType() and getRacialSubTypes() instead
     */
    @Deprecated
    private static List<String> getTypes(CharacterDisplay display)
    {
        final List<String> list = new ArrayList<>();

        Race race = display.getRace();
        if (race != null)
        {
            list.add(race.getType());
        } else
        {
            list.add("Humanoid");
        }

        for (PCTemplate t : display.getTemplateSet())
        {
            list.add(t.getType());
        }

        return list;

    }

}
