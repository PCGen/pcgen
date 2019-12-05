/*
 * from Ability.java Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.enumeration;

/**
 * An enum for the various types of ability options.
 */
public enum Nature
{
    /**
     * Ability is Normal
     */
    NORMAL,
    /**
     * Ability is Automatic
     */
    AUTOMATIC,
    /**
     * Ability is Virtual
     */
    VIRTUAL,
    /**
     * Ability of any type
     */
    ANY;

    public static Nature getBestNature(Nature nature1, Nature nature2)
    {
        if (nature1 == null)
        {
            return nature2;
        }
        //n1 has a valid value
        if (nature2 == null || nature2 == AUTOMATIC)
        {
            return nature1;
        }
        //n2 is VIRTUAL or NORMAL, can use VIRTUAL unless n1 == NORMAL
        if (nature1 == NORMAL)
        {
            return nature1;
        }
        return nature2;
    }
}
