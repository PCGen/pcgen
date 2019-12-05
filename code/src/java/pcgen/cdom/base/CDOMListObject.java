/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import java.net.URI;

/**
 * This is an abstract object intended to be used as a basis for "concrete"
 * CDOMList objects.
 * <p>
 * CDOMListObject provides basic equality, ensuring matching Class, matching
 * underlying class (the Class of objects in the CDOMList) and matching List
 * name. It does not fully test the underlying CDOMObject contents.
 *
 * @param <T> The type of object contained in the CDOMList
 */
public abstract class CDOMListObject<T extends CDOMObject> extends ConcretePrereqObject implements CDOMList<T>, Loadable
{
    private String name = null;
    private String keyName = null;
    private URI sourceURI = null;

    @Override
    public String getKeyName()
    {
        return (keyName == null) ? name : keyName;
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

    public void setKeyName(String key)
    {
        keyName = key;
    }

    @Override
    public String getDisplayName()
    {
        return name;
    }

    @Override
    public String getLSTformat()
    {
        return getKeyName();
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public boolean isInternal()
    {
        return false;
    }

    @Override
    public void setName(String n)
    {
        name = n;
    }

    @Override
    public String toString()
    {
        return getKeyName();
    }
}
