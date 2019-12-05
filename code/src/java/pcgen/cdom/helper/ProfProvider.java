/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.core.Equipment;

/**
 * An ProfProvider is an object that contains the ability to contain
 * Proficiencies, either by TYPE of Equipment or direct references.
 *
 * @param <T> The type of Proficiency (CDOMObject) that this ProfProvider
 *            provides
 */
public interface ProfProvider<T extends CDOMObject> extends QualifyingObject
{
    /**
     * Returns true if this ProfProvider provides proficiency for the given
     * Equipment; false otherwise.
     *
     * @param equipment The Equipment to be tested to see if this ProfProvider
     *                  provides proficiency for the Equipment
     * @return true if this ProfProvider provides proficiency for the given
     * Equipment; false otherwise.
     */
    boolean providesProficiencyFor(Equipment equipment);

    /**
     * Returns true if this ProfProvider provides the given proficiency.
     * <p>
     * This may only test a limited set of granting by the ProfProvider, by
     * testing only primitive proficiency objects. For a full query to test
     * proficiency, one should use the providesProficiencyFor method.
     *
     * @param prof The proficiency to be tested to see if this ProfProvider
     *             provides the given proficiency
     * @return true if this ProfProvider provides the given proficiency; false
     * otherwise.
     */
    boolean providesProficiency(T prof);

    /**
     * Returns true if this ProfProvider provides proficiency with the given
     * Equipment TYPE. This only tests against the Equipment TYPE reference list
     * provided during construction of the ProfProvider.
     * <p>
     * This may only test a limited set of granting by the ProfProvider, by
     * testing only Equipment TYPE based proficiency grants. For a full query to
     * test proficiency, one should use the providesProficiencyFor method.
     *
     * @param typeString The TYPE of Equipment to be tested to see if this ProfProvider
     *                   provides proficiency with the given Equipment TYPE
     * @return true if this ProfProvider provides proficiency with the given
     * Equipment TYPE.
     */
    boolean providesEquipmentType(String typeString);

    /**
     * Returns the LST format for this ProfProvider. Provided primarily to allow
     * the Token/Loader system to properly unparse the ProfProvider.
     *
     * @return The LST format of this ProfProvider
     */
    String getLstFormat();
}
