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

import pcgen.system.LanguageBundle;

/**
 * Represents the Genders available in PCGen.
 * <p>
 * It is designed to hold Genders in a type-safe fashion, so that they can be
 * quickly compared and use less memory when identical Genders exist in two
 * CDOMObjects.
 */
public enum Gender
{
    Male
            {
                @Override
                public String toString()
                {
                    return LanguageBundle.getString("in_genderMale");
                }
            },

    Female
            {
                @Override
                public String toString()
                {
                    return LanguageBundle.getString("in_genderFemale");
                }
            },

    Neuter
            {
                @Override
                public String toString()
                {
                    return LanguageBundle.getString("in_genderNeuter");
                }
            },

    Host
            {
                @Override
                public String toString()
                {
                    return LanguageBundle.getString("in_genderHost");
                }
            },
    Unknown
            {
                @Override
                public String toString()
                {
                    return LanguageBundle.getString("in_genderUnknown");
                }
            };

    public static Gender getDefaultValue()
    {
        return Male;
    }

    /**
     * Retrieve a Gender object to match the name ({@link #name()}) or localized
     * name (output by {@link #toString()}). The localized lookup is kept for
     * legacy purpose when the localized name was saved in the character files
     * (instead of the {@link #name()}).
     *
     * @param name The localized display name of the Gender.
     * @return The matching Gender.
     */
    public static Gender getGenderByName(String name)
    {
        for (Gender gender : values())
        {
            if (gender.toString().equals(name))
            {
                return gender;
            }
        }

        return valueOf(name);
    }
}
