/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import pcgen.system.LanguageBundle;

public enum EquipmentLocation
{

    /**
     * The item is held in neither hand
     */
    EQUIPPED_NEITHER
            {
                @Override
                public String getString()
                {
                    return LanguageBundle.getString("EquipLocation.Neither");
                }

                @Override
                public boolean isEquipped()
                {
                    return true;
                }
            },

    /**
     * The item is held in the primary hand
     */
    EQUIPPED_PRIMARY
            {
                @Override
                public String getString()
                {
                    return LanguageBundle.getString("EquipLocation.Primary");
                }

                @Override
                public boolean isEquipped()
                {
                    return true;
                }
            },

    /**
     * The item is held in the secondary hand
     */
    EQUIPPED_SECONDARY
            {
                @Override
                public String getString()
                {
                    return LanguageBundle.getString("EquipLocation.Secondary");
                }

                @Override
                public boolean isEquipped()
                {
                    return true;
                }
            },

    /**
     * The item is held in both hands
     */
    EQUIPPED_BOTH
            {
                @Override
                public String getString()
                {
                    return LanguageBundle.getString("EquipLocation.Both");
                }

                @Override
                public boolean isEquipped()
                {
                    return true;
                }
            },

    /**
     * The item is either a double weapon or one of a pair of weapons
     */
    EQUIPPED_TWO_HANDS
            {
                @Override
                public String getString()
                {
                    return LanguageBundle.getString("EquipLocation.TwoHands");
                }

                @Override
                public boolean isEquipped()
                {
                    return true;
                }
            },

    /**
     * The item is held in neither hand and equipped for a temporary bonus
     */
    EQUIPPED_TEMPBONUS
            {
                @Override
                public String getString()
                {
                    return LanguageBundle.getString("EquipLocation.TempBonus");
                }

                @Override
                public boolean isEquipped()
                {
                    /*
                     * TODO The code actually seems to imply this is false.
                     */
                    return true;
                }
            },

    /**
     * The item is carried but not equipped
     */
    CARRIED_NEITHER
            {
                @Override
                public String getString()
                {
                    return LanguageBundle.getString("EquipLocation.Carried");
                }

                @Override
                public boolean isEquipped()
                {
                    return false;
                }
            },

    /**
     * The item is contained by another item
     */
    CONTAINED
            {
                @Override
                public String getString()
                {
                    return LanguageBundle.getString("EquipLocation.Contained");
                }

                @Override
                public boolean isEquipped()
                {
                    return false;
                }
            },

    /**
     * The item is not carried
     */
    NOT_CARRIED
            {
                @Override
                public String getString()
                {
                    return LanguageBundle.getString("EquipLocation.NotCarried");
                }

                @Override
                public boolean isEquipped()
                {
                    return false;
                }
            };

    public abstract String getString();

    public abstract boolean isEquipped();
}
