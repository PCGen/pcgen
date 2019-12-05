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
import pcgen.cdom.helper.ProfProvider;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

/**
 * {@code PreArmorProfTester} does the testing of armor proficiency
 * prerequisites.
 */
public class PreArmorProfTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
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
                    LanguageBundle.getFormattedString(
                            "Prereq.error", "PREARMOR", prereq.toString()), exceptn); //$NON-NLS-1$ //$NON-NLS-2$
        }

        final String aString = prereq.getKey();
        Equipment keyEquip =
                Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Equipment.class, aString);
        final boolean isType = aString.startsWith("TYPE") && aString.length() > 5;
        final boolean isArmorType = aString.startsWith("ARMORTYPE") && aString.length() > 11;
        String typeString = null;
        if (isType)
        {
            typeString = "ARMOR." + aString.substring(5);
        } else if (isArmorType)
        {
            typeString = "ARMOR." + aString.substring(10);
        }
        for (ProfProvider<ArmorProf> spp : display.getArmorProfList())
        {
            if (keyEquip != null && spp.providesProficiency(keyEquip.getArmorProf()))
            {
                runningTotal++;
            } else if (keyEquip != null && spp.providesEquipmentType(keyEquip.getType()))
            {
                runningTotal++;
            } else if (isType && spp.providesEquipmentType(typeString))
            {
                runningTotal++;
            } else if (isArmorType && spp.providesEquipmentType(typeString))
            {
                runningTotal++;
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
        return "profwitharmor"; //$NON-NLS-1$
    }

}
