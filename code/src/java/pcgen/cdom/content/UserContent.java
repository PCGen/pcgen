/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.Objects;

import pcgen.cdom.base.Loadable;

/**
 * A UserContent manages common functions for user defined content within PCGen.
 */
public abstract class UserContent implements Loadable
{

    /**
     * The unique name of this UserContent.
     */
    private String name;

    /**
     * The source URI where this UserContent was originally defined
     */
    private URI sourceURI;

    /**
     * A String representing an explanation for the content described by this
     * UserContent.
     */
    private String explanation;

    @Override
    public void setName(String name)
    {
        Objects.requireNonNull(name, "Name cannot be null");
        this.name = name;
    }

    @Override
    public String getKeyName()
    {
        return name;
    }

    @Override
    public void setSourceURI(URI source)
    {
        sourceURI = source;
    }

    @Override
    public URI getSourceURI()
    {
        return sourceURI;
    }

    /**
     * Sets the Explanation for this UserContent. This is intended to be a user
     * understood String; it is not processed by PCGen.
     *
     * @param value The Explanation for this UserContent
     */
    public void setExplanation(String value)
    {
        Objects.requireNonNull(value, "Explanation may not be null");
        explanation = value;
    }

    /**
     * Returns the non-null Explanation for this UserContent.
     *
     * @return The non-null Explanation for this UserContent
     */
    public String getExplanation()
    {
        return explanation;
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public boolean isInternal()
    {
        return false;
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public boolean isType(String type)
    {
        return false;
    }
}
