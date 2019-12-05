/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

/**
 * Prerequisite tester, tests for the presence of a kit.
 */
public class PreKitTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

    private static final Class<Kit> KIT_CLASS = Kit.class;

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
                    LanguageBundle.getFormattedString("PreKit.error", prereq.toString()), exceptn); //$NON-NLS-1$
        }

        String kitKey = prereq.getKey().toUpperCase();
        final int wildCard = kitKey.indexOf('%');
        //handle wildcards (always assume they end the line)
        if (wildCard >= 0)
        {
            kitKey = kitKey.substring(0, wildCard);
            for (Kit kit : display.getKitInfo())
            {
                if (kit.getKeyName().toUpperCase().startsWith(kitKey))
                {
                    runningTotal++;
                }
            }
        } else
        {
            Kit kit = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(KIT_CLASS, kitKey);
            if (display.hasKit(kit))
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
        return "KIT"; //$NON-NLS-1$
    }

}
