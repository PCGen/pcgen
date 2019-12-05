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
package pcgen.core;

import java.net.URI;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Loadable;

public final class PointBuyCost extends ConcretePrereqObject implements Loadable
{
    private URI sourceURI;
    private int statValue = 0;
    private int buyCost = 0;

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

    @Override
    public String getDisplayName()
    {
        return Integer.toString(statValue);
    }

    @Override
    public void setName(String name)
    {
        try
        {
            statValue = Integer.parseInt(name);
        } catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Name for a PointBuyCost must be in integer, found: " + name, e);
        }

    }

    @Override
    public String getKeyName()
    {
        return getDisplayName();
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

    public int getStatValue()
    {
        return statValue;
    }

    public void setBuyCost(int cost)
    {
        buyCost = cost;
    }

    public int getBuyCost()
    {
        return buyCost;
    }

}
