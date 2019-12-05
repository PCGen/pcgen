/*
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
package pcgen.gui2.util.treeview;

import pcgen.system.LanguageBundle;

public class DefaultDataViewColumn implements DataViewColumn
{

    private String name;
    private Class<?> dataclass;
    private Visibility visibility;
    private boolean editable;

    public DefaultDataViewColumn(String name, Class<?> dataclass)
    {
        this(name, dataclass, false, false);
    }

    public DefaultDataViewColumn(String name, Class<?> dataclass, boolean visible)
    {
        this(name, dataclass, visible, false);
    }

    public DefaultDataViewColumn(String name, Class<?> dataclass, boolean visible, boolean editable)
    {
        this(name, dataclass, visible, editable, true);
    }

    public DefaultDataViewColumn(String name, Class<?> dataclass, boolean visible, boolean editable, boolean cache)
    {
        if (name.startsWith("in_"))
        {
            this.name = LanguageBundle.getString(name);
        } else
        {
            this.name = name;
        }
        this.dataclass = dataclass;
        this.editable = editable;
        if (visible)
        {
            this.visibility = Visibility.INITIALLY_VISIBLE;
        } else
        {
            this.visibility = Visibility.INITIALLY_INVISIBLE;
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Class<?> getDataClass()
    {
        return dataclass;
    }

    @Override
    public Visibility getVisibility()
    {
        return visibility;
    }

    @Override
    public boolean isEditable()
    {
        return editable;
    }

}
