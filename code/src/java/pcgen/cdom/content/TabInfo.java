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

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.Loadable;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

public class TabInfo implements Loadable
{

    private URI sourceURI;
    private String tabName = "";
    private Tab tabID;
    private boolean isVisible = true;
    private File helpFile;
    private Set<Integer> hiddenColumns;
    private String helpContext;

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

    public void setTab(Tab tab)
    {
        tabID = tab;
    }

    @Override
    public void setName(String name)
    {
        if (!Tab.exists(name))
        {
            throw new IllegalArgumentException(name + " is not a valid Tab name");
        }
        tabID = Tab.getTab(name);
    }

    @Override
    public String getDisplayName()
    {
        return tabID.toString();
    }

    @Override
    public String getKeyName()
    {
        return getDisplayName();
    }

    public void setTabName(String name)
    {
        tabName = name;
    }

    public String getTabName()
    {
        return tabName;
    }

    public String getResolvedName()
    {
        String temp = tabName;

        if (temp.startsWith("in_"))
        {
            temp = LanguageBundle.getString(temp);
        }

        return temp;
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

    public void setVisible(boolean visible)
    {
        isVisible = visible;
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public Tab getTab()
    {
        return tabID;
    }

    public void clearHiddenColumns()
    {
        if (hiddenColumns != null)
        {
            hiddenColumns.clear();
        }
    }

    public void hideColumn(int column)
    {
        if (hiddenColumns == null)
        {
            hiddenColumns = new HashSet<>();
        }
        hiddenColumns.add(column);
    }

    public boolean isColumnVisible(int column)
    {
        return (hiddenColumns == null) || !hiddenColumns.contains(column);
    }

    public Collection<Integer> getHiddenColumns()
    {
        if (hiddenColumns == null)
        {
            return Collections.emptyList();
        }
        return Collections.unmodifiableSet(hiddenColumns);
    }

    public void setHelpContext(File context)
    {
        helpFile = context;
    }

    public File getHelpContext()
    {
        return helpFile;
    }

    public String getRawHelpContext()
    {
        return helpContext;
    }

    public void setRawHelpContext(String value)
    {
        helpContext = value;
    }

}
