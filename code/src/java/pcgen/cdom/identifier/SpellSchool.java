/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.identifier;

import java.net.URI;

import pcgen.cdom.base.Loadable;

public class SpellSchool implements Loadable, Comparable<SpellSchool>
{

    private URI sourceURI;
    private String name;

    @Override
    public String getDisplayName()
    {
        return name;
    }

    @Override
    public String getKeyName()
    {
        return name;
    }

    @Override
    public boolean isInternal()
    {
        return false;
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }

    @Override
    public void setName(String newName)
    {
        name = newName;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals(Object other)
    {
        return other instanceof SpellSchool && name.equals(((SpellSchool) other).name);
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public int compareTo(SpellSchool other)
    {
        return name.compareTo(other.name);
    }

    @Override
    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI source)
    {
        sourceURI = source;
    }

}
