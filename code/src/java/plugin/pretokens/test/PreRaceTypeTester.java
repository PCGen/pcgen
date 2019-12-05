/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
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
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;

public class PreRaceTypeTester extends AbstractDisplayPrereqTest
{

    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
    {
        final int reqnumber = Integer.parseInt(prereq.getOperand());
        final String requiredRaceType = prereq.getKey();
        int runningTotal = 0;

        try
        {
            if (requiredRaceType.equalsIgnoreCase(display.getRaceType()))
            {
                runningTotal++;
            }
        } catch (IllegalArgumentException e)
        {
            //Can't match
        }
        if (getCritterTypes(display).contains(requiredRaceType))
        {
            runningTotal++;
        }
        runningTotal = prereq.getOperator().compare(runningTotal, reqnumber);
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
        return "RACETYPE"; //$NON-NLS-1$
    }

    /**
     * Get ArrayList populated with creature types for this PC (defaults to humanoid).
     *
     * @return the list of types
     */
    @Deprecated
    public static List<String> getCritterTypes(CharacterDisplay display)
    {
        ArrayList<String> critterTypes = new ArrayList<>();

        // Not too sure about this if, but that's what the previous code
        // implied...
        Race race = display.getRace();
        if (race != null)
        {
            critterTypes.add(race.getType());
        } else
        {
            critterTypes.add("Humanoid");
        }

        for (PCTemplate t : display.getTemplateSet())
        {
            final String aType = t.getType();

            if (aType != null && !aType.isEmpty())
            {
                critterTypes.add(aType);
            }
        }

        return critterTypes;
    }

}
