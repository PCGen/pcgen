/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.core.prereq;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.system.LanguageBundle;

/**
 * This is a transition class in order to get PrerequisiteTest objects off of
 * using PlayerCharacter and getting them to use CharacterDisplay.
 */
public abstract class AbstractDisplayPrereqTest extends AbstractPrerequisiteTest
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
    public final int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
            throws PrerequisiteException
    {
        return passes(prereq, (character == null) ? null : character.getDisplay(), source);
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
    public final int passes(final Prerequisite prereq, final Equipment equipment, PlayerCharacter character)
            throws PrerequisiteException
    {
        return passes(prereq, equipment, (character == null) ? null : character.getDisplay());
    }

    /**
     * Does nothing other than throw an exception since the subclass does not
     * implement a passes method with this signature.
     *
     * @param prereq  The prerequisite to test
     * @param display The PC to test the prerequisite against.
     * @return nothing. This always throws an exception.
     * @throws PrerequisiteException Oops, haven't implemented passes with this signature in the
     *                               subclass
     */
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
            throws PrerequisiteException
    {
        String name = this.getClass().getName();
        String eString = LanguageBundle.getFormattedString("prereq.error.does_not_support_characters", name);
        throw new PrerequisiteException(eString);
    }

    /**
     * Is only called if a subclass fails to define a passes method for
     * equipment. IF character is null, then we do not have a PC available, so
     * we cannot try the PC version of passes. This is used by parts of the GUI
     * that do not know or care which PC is current. If character is defined
     * then try to call the PC version of passes.
     *
     * @param prereq    The prerequisite to test
     * @param equipment The equipment to test the prerequisite against.
     * @param display   The PC to use as a last resort.
     * @return 1 if it passes, 0 otherwise
     * @throws PrerequisiteException Oops, haven't implemented passes with this signature in the
     *                               subclass
     */
    public int passes(final Prerequisite prereq, final Equipment equipment, CharacterDisplay display)
            throws PrerequisiteException
    {
        if (display == null)
        {
            String name = this.getClass().getName();
            String eString = LanguageBundle.getFormattedString("prereq.error.does_not_support_equipment", name);
            throw new PrerequisiteException(eString);
        }

        return passes(prereq, display, equipment);
    }
}
