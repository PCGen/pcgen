/*
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 */
package pcgen.core;

import java.util.Optional;

import pcgen.cdom.enumeration.PCStringKey;
import pcgen.io.FileAccess;
import pcgen.util.Logging;

/**
 * {@code NoteItem}.
 */
public final class NoteItem implements Cloneable
{
    private String name = "";
    private String value = "";
    private int id_parent = -1;
    private int id_value = -1;
    private final Optional<PCStringKey> key;

    public NoteItem(Optional<PCStringKey> key, final int my_id, final int my_parent, final String aName, final String aValue)
    {
        this.key = key;
        id_value = my_id;
        id_parent = my_parent;
        name = aName;
        value = aValue;
    }

    public NoteItem(PCStringKey key, final int my_id, final int my_parent, final String aName, final String aValue)
    {
        this(Optional.of(key), my_id, my_parent, aName, aValue);
    }

    public NoteItem(final int my_id, final int my_parent, final String aName, final String aValue)
    {
        this(Optional.empty(), my_id, my_parent, aName, aValue);
    }

    /**
     * This is used to export to character sheets
     * e.g. getExportString("<b>","</b>,"<br>,"")
     * would return the name in bold and the value on the next line in html format.
     *
     * @param beforeName  The markup to be included before the name.
     * @param afterName   The markup to be included after the name.
     * @param beforeValue The markup to be included before the value.
     * @param afterValue  The markup to be included after the value.
     * @return The export string including markup, the name of the note and the note contents.
     */
    public String getExportString(final String beforeName, final String afterName, final String beforeValue,
            final String afterValue)
    {
        return beforeName + FileAccess.filterString(name) + afterName + beforeValue + FileAccess.filterString(value)
                + afterValue;
    }

    public int getId()
    {
        return id_value;
    }

    public void setIdValue(final int x)
    {
        id_value = x;
    }

    public void setName(final String x)
    {
        name = x;
    }

    public String getName()
    {
        return name;
    }

    public void setParentId(final int x)
    {
        id_parent = x;
    }

    public int getParentId()
    {
        return id_parent;
    }

    public void setValue(final String x)
    {
        value = x;
    }

    public String getValue()
    {
        return value;
    }

    public Optional<PCStringKey> getPCStringKey()
    {
        return key;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public NoteItem clone()
    {
        try
        {
            return (NoteItem) super.clone();
        } catch (CloneNotSupportedException e)
        {
            Logging.errorPrint("NoteItem.clone failed", e);
            return null;
        }
    }

    @Override
    public int hashCode()
    {
        return 17 * id_value ^ 23 * id_parent;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof NoteItem)
        {
            NoteItem other = (NoteItem) o;
            return (id_parent == other.id_parent) && (id_value == other.id_value) && (name.equals(other.name))
                    && (value.equals(other.value)) && (key.equals(other.key));
        }
        return false;
    }
}
