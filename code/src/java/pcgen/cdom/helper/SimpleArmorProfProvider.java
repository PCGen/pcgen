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

import pcgen.core.ArmorProf;
import pcgen.core.Equipment;

/**
 * A SimpleArmorProfProvider is an object that provides proficiency based on a
 * single ArmorProf
 */
public class SimpleArmorProfProvider extends AbstractSimpleProfProvider<ArmorProf>
{

    /**
     * Constructs a new SimpleArmorProfProvider that provides proficiency based
     * on a the given ArmorProf
     *
     * @param proficiency The ArmorProf that this SimpleArmorProfProvider provides
     */
    public SimpleArmorProfProvider(ArmorProf proficiency)
    {
        super(proficiency);
    }

    /**
     * Returns true if this SimpleArmorProfProvider provides proficiency for the
     * given Equipment; false otherwise.
     *
     * @param equipment The Equipment to be tested to see if this
     *                  SimpleArmorProfProvider provides proficiency for the Equipment
     * @return true if this SimpleArmorProfProvider provides proficiency for the
     * given Equipment; false otherwise.
     */
    @Override
    public boolean providesProficiencyFor(Equipment equipment)
    {
        return providesProficiency(equipment.getArmorProf());
    }

    @Override
    public int hashCode()
    {
        return getLstFormat().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return (o == this) || ((o instanceof SimpleArmorProfProvider) && hasSameProf((SimpleArmorProfProvider) o));
    }
}
