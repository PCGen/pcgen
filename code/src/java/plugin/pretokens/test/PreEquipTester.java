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
import pcgen.core.PlayerCharacter;
import pcgen.core.character.WieldCategory;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

public class PreEquipTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
            throws PrerequisiteException
    {
        int runningTotal = 0;

        final int number;
        try
        {
            number = Integer.parseInt(prereq.getOperand());
        } catch (NumberFormatException exceptn)
        {
            throw new PrerequisiteException(
                    LanguageBundle.getFormattedString("PreFeat.error", prereq.toString()), exceptn); //$NON-NLS-1$
        }

        CharacterDisplay display = character.getDisplay();
        if (display.hasEquipment())
        {

            String targetEquip = prereq.getKey();
            for (Equipment eq : display.getEquippedEquipmentSet())
            {
                if (targetEquip.startsWith("WIELDCATEGORY=") || targetEquip.startsWith("WIELDCATEGORY."))
                {
                    final WieldCategory wCat = eq.getEffectiveWieldCategory(character);
                    if ((wCat != null) && wCat.getKeyName().equalsIgnoreCase(targetEquip.substring(14)))
                    {
                        ++runningTotal;
                        break;
                    }
                } else if (targetEquip.startsWith("TYPE=") || targetEquip.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    StringTokenizer tok = new StringTokenizer(targetEquip.substring(5).toUpperCase(), ".");
                    boolean match = false;
                    if (tok.hasMoreTokens())
                    {
                        match = true;
                    }
                    //
                    // Must match all listed types in order to qualify
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
                        ++runningTotal;
                        break;
                    }
                } else
                //not a TYPE string
                {
                    String eqName;
                    if (targetEquip.startsWith("BASEITEM=")) //$NON-NLS-1$
                    {
                        eqName = eq.getBaseItemName().toUpperCase();
                        targetEquip = targetEquip.substring(targetEquip.indexOf(Constants.EQUALS) + 1);
                    } else
                    {
                        eqName = eq.getName().toUpperCase();
                    }

                    if (targetEquip.indexOf('%') >= 0)
                    {
                        //handle wildcards (always assume
                        // they end the line)
                        final int percentPos = targetEquip.indexOf('%');
                        final String substring = targetEquip.substring(0, percentPos).toUpperCase();
                        if ((eqName.startsWith(substring)))
                        {
                            ++runningTotal;
                            break;
                        }
                    } else if (eqName.equalsIgnoreCase(targetEquip))
                    {
                        //just a straight String compare
                        ++runningTotal;
                        break;
                    }
                }
            }
        }

        runningTotal = prereq.getOperator().compare(runningTotal, number);
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
        return "EQUIP"; //$NON-NLS-1$
    }
}
