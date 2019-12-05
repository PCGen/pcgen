/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

/**
 * An GlobalModifiers is a CDOMObject designed to serve as a cache of
 * information for global defaults. It is therefore somewhat simple (non-typed).
 */
public class GlobalModifiers extends CDOMObject
{

    /**
     * Returns false, as GlobalModifiers never has a type.
     */
    @Override
    public boolean isType(String type)
    {
        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof GlobalModifiers)
        {
            return isCDOMEqual((CDOMObject) o);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return 53281;
    }
}
