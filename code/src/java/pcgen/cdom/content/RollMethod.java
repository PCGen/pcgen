/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import java.net.URI;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.SortKeyRequired;

/**
 * A RollMethod is a method used to generate dice rolls for Character creation
 */
public class RollMethod implements Loadable, SortKeyRequired
{

    /**
     * The source URI of this RollMethod.
     */
    private URI sourceURI;

    /**
     * The name of this RollMethod.
     */
    private String methodName;

    /**
     * The implementation of this RollMethod. This is a string representation of a
     * formula.
     */
    private String rollMethod;

    /**
     * The sort key of this RollMethod, to indicate which items should appear first.
     */
    private String sortKey;

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
    public void setName(String name)
    {
        methodName = name;
    }

    @Override
    public String getDisplayName()
    {
        return methodName;
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

    public void setMethodRoll(String method)
    {
        rollMethod = method;
    }

    public String getMethodRoll()
    {
        return rollMethod;
    }

    public void setSortKey(String sortKey)
    {
        this.sortKey = sortKey;
    }

    @Override
    public String getSortKey()
    {
        return sortKey;
    }

}
