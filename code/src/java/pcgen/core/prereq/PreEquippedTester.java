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
package pcgen.core.prereq;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.WieldCategory;
import pcgen.system.LanguageBundle;

public abstract class PreEquippedTester extends AbstractPrerequisiteTest
{

    /**
     * Process the tokens and return the number that is not passed.
     *
     * @param prereq       the prereq
     * @param character    The pc to use.
     * @param equippedType The equipped type to look for (e.g. Equipment.EQUIPPED_TWO_HANDS)
     * @return the number that did not pass.
     * @throws PrerequisiteException the prerequisite exception
     */
    public int passesPreEquipHandleTokens(final Prerequisite prereq, final PlayerCharacter character,
            final EquipmentLocation equippedType) throws PrerequisiteException
    {
        // TODO refactor this code with PreEquipTester
        boolean isEquipped = false;

        if (character.hasEquipment())
        {
            String aString = prereq.getKey();
            for (Equipment eq : character.getDisplay().getEquippedEquipmentSet())
            {
                //
                // Only check equipment of the type we are interested in
                //
                if (eq.getLocation() != equippedType)
                {
                    continue;
                }

                if (aString.startsWith("WIELDCATEGORY=") || aString.startsWith("WIELDCATEGORY."))
                {
                    final WieldCategory wCat = eq.getEffectiveWieldCategory(character);
                    if ((wCat != null) && wCat.getKeyName().equalsIgnoreCase(aString.substring(14)))
                    {
                        isEquipped = true;
                        break;
                    }
                } else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    isEquipped = eq.isType(aString);
                    break;
                } else //not a TYPE string
                {
                    String eqName;
                    if (aString.startsWith("BASEITEM=")) //$NON-NLS-1$
                    {
                        eqName = eq.getBaseItemName();
                        aString = aString.substring(aString.indexOf(Constants.EQUALS) + 1);
                    } else
                    {
                        eqName = eq.getName();
                    }
                    if (aString.indexOf('%') >= 0)
                    {
                        //handle wildcards (always assume they
                        // end the line)
                        final int percentPos = aString.indexOf('%');
                        if (eqName.regionMatches(true, 0, aString, 0, percentPos))
                        {
                            isEquipped = true;
                            break;
                        }
                    } else if (eqName.equalsIgnoreCase(aString))
                    {
                        //just a straight String compare
                        isEquipped = true;
                        break;
                    }
                }
            }
        }

        final PrerequisiteOperator operator = prereq.getOperator();

        int runningTotal;
        if (operator.equals(PrerequisiteOperator.EQ) || operator.equals(PrerequisiteOperator.GTEQ))
        {
            runningTotal = isEquipped ? 1 : 0;
        } else if (operator.equals(PrerequisiteOperator.NEQ) || operator.equals(PrerequisiteOperator.LT))
        {
            runningTotal = isEquipped ? 0 : 1;
        } else
        {
            throw new PrerequisiteException(
                    LanguageBundle.getFormattedString(
                            "PreEquipped.error.invalid_comparison", prereq.toString())); //$NON-NLS-1$
        }

        return countedTotal(prereq, runningTotal);

    }

}
