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
package pcgen.cdom.helper;

import pcgen.cdom.base.Identified;
import pcgen.core.PCClass;

public class ClassSource implements Identified
{
    private final PCClass pcclass;
    private final int level;

    public ClassSource(PCClass pcc)
    {
        this(pcc, -1);
    }

    public ClassSource(PCClass pcc, int lvl)
    {
        pcclass = pcc;
        level = lvl;
    }

    public int getLevel()
    {
        return level;
    }

    public PCClass getPcclass()
    {
        return pcclass;
    }

    @Override
    public int hashCode()
    {
        return pcclass.hashCode() - level;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (o instanceof ClassSource)
        {
            ClassSource other = (ClassSource) o;
            return (level == other.level) && (pcclass.equals(other.pcclass));
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "ClassSource: " + getDisplayName();
    }

    @Override
    public String getKeyName()
    {
        return pcclass.getFullKey() + " " + level;
    }

    @Override
    public String getDisplayName()
    {
        return pcclass.getDisplayName() + " " + level;
    }
}
