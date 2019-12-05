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

import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.core.Equipment;
import pcgen.core.ShieldProf;

/**
 * An ShieldProfProvider is an object that contains the ability to contain
 * Shield Proficiencies, either by TYPE of Equipment or direct ShieldProf
 * references. Explicit Storage of TYPE vs. primitive is necessary due to the
 * ability of the TYPE being a resolved against Equipment.
 * <p>
 * This is typically used for an AUTO:ShieldProf token to store the granted
 * proficiencies prior to resolution
 */
public class ShieldProfProvider extends AbstractProfProvider<ShieldProf>
{

    /**
     * Constructs a new ShieldProfProvider with the given List of ShieldProf
     * references and Equipment TYPE references.
     * <p>
     * No reference is maintained to the internal structure of the given Lists,
     * so modifications to this ShieldProfProvider are not reflected in the
     * given Lists (and vice versa).
     *
     * @param profs      The List of ShieldProf references indicating the primitive
     *                   ShieldProf objects this ShieldProfProvider will contain.
     * @param equipTypes The List of Equipment references indicating the TYPEs of
     *                   Equipment objects this ShieldProfProvider will contain.
     */
    public ShieldProfProvider(List<CDOMReference<ShieldProf>> profs, List<CDOMReference<Equipment>> equipTypes)
    {
        super(profs, equipTypes);
    }

    /**
     * Returns true if this ShieldProfProvider provides proficiency for the
     * given Equipment; false otherwise.
     *
     * @param equipment The Equipment to be tested to see if this ShieldProfProvider
     *                  provides proficiency for the Equipment
     * @return true if this ShieldProfProvider provides proficiency for the
     * given Equipment; false otherwise.
     */
    @Override
    public boolean providesProficiencyFor(Equipment equipment)
    {
        /*
         * CONSIDER using providesEquipmentType might be optimized if references
         * can contain late-created objects, dependent upon full resolution of
         * Tracker 2001287 - thpr Oct 15, 2008
         */
        return providesProficiency(equipment.getShieldProf()) || providesEquipmentType(equipment.getType());
    }

    /**
     * Returns the String "SHIELD", indicating the type of proficiency granted
     * by this ShieldProfProvider.
     *
     * @return The String "SHIELD", indicating the type of proficiency granted
     * by this ShieldProfProvider.
     */
    @Override
    protected String getSubType()
    {
        return "SHIELD";
    }
}
