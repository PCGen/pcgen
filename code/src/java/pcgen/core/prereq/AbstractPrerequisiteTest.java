/*
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.prereq;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.system.LanguageBundle;

/**
 * This is the base class for Prerequisites, if a given prerequisite does not
 * implement a method it falls through to this and is dealt with in some kind of
 * sensible fashion.
 */
public abstract class AbstractPrerequisiteTest implements PrerequisiteTest
{
    /**
     * Does nothing other than throw an exception since the subclass does not
     * implement a passes method with this signature.
     *
     * @param prereq    The prerequisite to test
     * @param character The PC to test the prerequisite against.
     * @return nothing. This always throws an exception.
     * @throws PrerequisiteException Oops, haven't implemented passes with
     *                               this signature in the subclass
     */
    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
            throws PrerequisiteException
    {
        String name = this.getClass().getName();
        String eString = LanguageBundle.getFormattedString("prereq.error.does_not_support_characters", name);
        throw new PrerequisiteException(eString);
    }

    /**
     * Is only called if a subclass fails to define a passes method for
     * equipment.  IF character is null, then we do not have a PC available, so
     * we cannot try the PC version of passes.  This is used by parts of the GUI
     * that do not know or care which PC is current.  If character is defined
     * then try to call the PC version of passes.
     *
     * @param prereq    The prerequisite to test
     * @param equipment The equipment to test the prerequisite against.
     * @param character The PC to use as a last resort.
     * @return 1 if it passes, 0 otherwise
     * @throws PrerequisiteException Oops, haven't implemented passes with
     *                               this signature in the subclass
     */
    @Override
    public int passes(final Prerequisite prereq, final Equipment equipment, PlayerCharacter character)
            throws PrerequisiteException
    {
        if (character == null)
        {
            String name = this.getClass().getName();
            String eString = LanguageBundle.getFormattedString("prereq.error.does_not_support_equipment", name);
            throw new PrerequisiteException(eString);
        }

        return passes(prereq, character, equipment);
    }

    protected int countedTotal(final Prerequisite prereq, final int runningTotal)
    {
        if (prereq.isCountMultiples() || prereq.isTotalValues())
        {
            return runningTotal;
        }

        return (runningTotal > 0) ? 1 : 0;
    }

    /**
     * Convert PreReq to an HTML string
     *
     * @param prereq
     * @return html String representation of the PreReq
     */
    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        return LanguageBundle.getFormattedString("AbstractPrerequisiteTest.toHtml", prereq.getKind(), prereq.getKey(),
                prereq.getOperator().toDisplayString(), prereq.getOperand()); // $NON-NLS-1$
    }
}
