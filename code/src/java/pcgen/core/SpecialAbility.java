/*
 * Copyright 2004 (C) Devon Jones
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package pcgen.core;

/**
 * {@code SpecialAbility}.
 */
public final class SpecialAbility extends TextProperty
{
    private String propDesc = "";

    /**
     * Default constructor
     */
    public SpecialAbility()
    {
        super();
    }

    /**
     * Constructor - with name
     *
     * @param name
     */
    public SpecialAbility(final String name)
    {
        super(name);
    }

    /**
     * Constructor
     *
     * @param name     The name of the Special Ability
     * @param propDesc NEEDDOC
     */
    public SpecialAbility(final String name, final String propDesc)
    {
        super(name);
        this.propDesc = propDesc;
    }

    /**
     * Set the description of the Special Ability
     *
     * @param saDesc
     */
    public void setSADesc(final String saDesc)
    {
        setPropDesc(saDesc);
    }

    /**
     * Get the description of the Special Ability
     *
     * @return the description of the Special Ability
     */
    public String getSADesc()
    {
        return getPropDesc();
    }

    @Override
    public int compareTo(final Object obj)
    {
        return getKeyName().compareToIgnoreCase(obj.toString());
    }

    @Override
    public String toString()
    {
        return getDisplayName();
    }

    /**
     * Set the property description
     *
     * @param propDesc
     */
    public void setPropDesc(final String propDesc)
    {
        this.propDesc = propDesc;
    }

    String getPropDesc()
    {
        return propDesc;
    }

    @Override
    public String getText()
    {
        final String text;
        if ((getPropDesc() == null) || "".equals(getPropDesc()))
        {
            text = super.getText();
        } else
        {
            text = super.getText() + " (" + getPropDesc() + ")";
        }
        return text;
    }
}
