/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.enumeration;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

public enum EqWield
{
    Unarmed
            {
                @Override
                public boolean checkWield(PlayerCharacter pc, Equipment equipment)
                {
                    //TODO What is appropriate here?
                    return false;
                }
            },
    Light
            {
                @Override
                public boolean checkWield(PlayerCharacter pc, Equipment equipment)
                {
                    return equipment.isWeaponLightForPC(pc);
                }
            },
    OneHanded
            {
                @Override
                public String toString()
                {
                    return "1 Handed";
                }

                @Override
                public boolean checkWield(PlayerCharacter pc, Equipment equipment)
                {
                    return equipment.isWeaponOneHanded(pc);
                }
            },
    TwoHanded
            {
                @Override
                public String toString()
                {
                    return "2 Handed";
                }

                @Override
                public boolean checkWield(PlayerCharacter pc, Equipment equipment)
                {
                    return equipment.isWeaponTwoHanded(pc);
                }
            };

    public abstract boolean checkWield(PlayerCharacter pc, Equipment equipment);
}
