/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;

public class SpellLevel implements Comparable<SpellLevel>
{

    private final PCClass pcclass;
    private final int level;

    public SpellLevel(PCClass pcc, int lvl)
    {
        pcclass = pcc;
        level = lvl;
    }

    @Override
    public String toString()
    {
        String sb = pcclass
                + " "
                + level;
        return sb;
    }

    /**
     * Provide a machine readable encoding of this SpellLevel for use in storing choices.
     *
     * @return The encoded spell level.
     */
    public String encodeChoice()
    {
        String sb = "CLASS."
                + pcclass.getKeyName()
                + ";LEVEL."
                + level;
        return sb;
    }

    public static SpellLevel decodeChoice(LoadContext context, String persistentFormat)
    {
        int loc = persistentFormat.indexOf(";LEVEL.");
        String classString;
        String levelString;
        if (loc == -1)
        {
            /*
             * Handle old persistence
             */
            int spaceLoc = persistentFormat.indexOf(' ');
            classString = persistentFormat.substring(0, spaceLoc);
            levelString = persistentFormat.substring(spaceLoc + 1);
        } else
        {
            String classText = persistentFormat.substring(0, 6);
            if (!"CLASS.".equals(classText))
            {
                return null;
            }
            classString = persistentFormat.substring(6, loc);
            levelString = persistentFormat.substring(loc + 7);
        }
        PCClass pcc = context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class, classString);
        try
        {
            int level = Integer.parseInt(levelString);
            return new SpellLevel(pcc, level);
        } catch (NumberFormatException e)
        {
            return null;
        }
    }

    @Override
    public int hashCode()
    {
        return level ^ pcclass.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof SpellLevel)
        {
            SpellLevel other = (SpellLevel) obj;
            return level == other.level && pcclass.equals(other.pcclass);
        }
        return false;
    }

    @Override
    public int compareTo(SpellLevel other)
    {
        int compareResult = pcclass.compareTo(other.pcclass);
        if (compareResult == 0)
        {
            if (level < other.level)
            {
                return -1;
            } else if (level > other.level)
            {
                return 1;
            }
        }
        return compareResult;
    }
}
