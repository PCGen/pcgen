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
package pcgen.cdom.inst;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;

/**
 * A PCClassLevel is a CDOMObject that represents items gained in a specific
 * level of a PCClass.
 */
public final class PCClassLevel extends CDOMObject implements Cloneable
{

    @Override
    public int hashCode()
    {
        String name = this.getDisplayName();
        return name == null ? 0 : name.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof PCClassLevel && ((PCClassLevel) obj).isCDOMEqual(this);
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }

    @Override
    public PCClassLevel clone() throws CloneNotSupportedException
    {
        return (PCClassLevel) super.clone();
    }

    @Override
    public String getQualifiedKey()
    {
        return get(StringKey.QUALIFIED_KEY);
    }

    @Override
    public String toString()
    {
        return getDisplayName();
    }
}
