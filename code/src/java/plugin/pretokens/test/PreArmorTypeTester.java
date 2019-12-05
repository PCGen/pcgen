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

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.Equipment;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * Prerequisite test the type of a piece of armour.
 */
public class PreArmorTypeTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

    // TODO All the equipment related PRE tag code should be refactored into a
    // common base class.
    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
    {
        int runningTotal = 0;

        if (display.hasEquipment())
        {
            final String desiredType = prereq.getKey();
            for (Equipment eq : display.getEquippedEquipmentSet())
            {
                if (!eq.isArmor())
                {
                    continue;
                }

                // Match against a TYPE of armour
                if (desiredType.startsWith(Constants.LST_TYPE_EQUAL) || desiredType.startsWith(Constants.LST_TYPE_DOT))
                {

                    String stripped = desiredType.substring(Constants.SUBSTRING_LENGTH_FIVE);
                    StringTokenizer tok = new StringTokenizer(stripped.toUpperCase(), ".");

                    boolean match = false;
                    if (tok.hasMoreTokens())
                    {
                        match = true;
                    }
                    //
                    // Must match all listed types to qualify
                    //
                    while (tok.hasMoreTokens())
                    {
                        final String type = tok.nextToken();
                        if (!eq.isType(type))
                        {
                            match = false;
                            break;
                        }
                    }
                    if (match)
                    {
                        runningTotal++;
                        break;
                    }
                } else
                { //not a TYPE string
                    final String eqName = eq.getName().toUpperCase();
                    final int percentPos = desiredType.indexOf('%');
                    if (percentPos >= 0)
                    {
                        //handle wildcards (always assume they
                        // end the line)
                        final String substring = desiredType.substring(0, percentPos).toUpperCase();
                        if (eqName.startsWith(substring))
                        {
                            runningTotal++;
                            break;
                        }
                    } else if (desiredType.contains("LIST")) //$NON-NLS-1$
                    {
                        if (display.isProficientWithArmor(eq))
                        {
                            runningTotal++;
                            break;
                        }
                    } else if (eqName.equals(desiredType)) //just a straight String compare
                    {
                        runningTotal++;
                        break;
                    }
                }
            }
        }
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
        return "ARMORTYPE"; //$NON-NLS-1$
    }

}
