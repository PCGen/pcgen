/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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

import java.net.URI;
import java.util.Objects;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.SortKeyRequired;
import pcgen.system.LanguageBundle;

import org.apache.commons.lang3.StringUtils;

/**
 * The Paper information for output sheets
 */
public final class PaperInfo implements Loadable, SortKeyRequired
{
    /**
     * The source URI of this PaperInfo.
     */
    private URI sourceURI;

    /**
     * The name of this PaperInfo
     */
    private String infoName;

    /**
     * The sort key of this PaperInfo, to indicate which items should appear first.
     */
    private String sortKey;

    /**
     * Array of 6 paper information variables to keep hold of
     */
    private final String[] paperInfo = new String[7];

    public static final int NAME = 0;
    public static final int HEIGHT = 1;
    public static final int WIDTH = 2;
    public static final int TOPMARGIN = 3;
    public static final int BOTTOMMARGIN = 4;
    public static final int LEFTMARGIN = 5;
    public static final int RIGHTMARGIN = 6;

    /**
     * Set a paper info item
     *
     * @param infoType The type (key)
     * @param info     The value
     */
    public void setPaperInfo(final int infoType, final String info)
    {
        if (!validIndex(infoType))
        {
            throw new IndexOutOfBoundsException("invalid index: " + infoType);
        }

        if (StringUtils.isNotBlank(info) && info.startsWith("in_"))
        {
            paperInfo[infoType] = LanguageBundle.getString(info);
        } else
        {
            paperInfo[infoType] = info;
        }
    }

    String getName()
    {
        return getPaperInfo(PaperInfo.NAME);
    }

    public String getPaperInfo(final int infoType)
    {
        if (!validIndex(infoType))
        {
            return null;
        }

        return paperInfo[infoType];
    }

    private static boolean validIndex(final int index)
    {
        switch (index)
        {
            case PaperInfo.NAME:
            case PaperInfo.HEIGHT:
            case PaperInfo.WIDTH:
            case PaperInfo.TOPMARGIN:
            case PaperInfo.BOTTOMMARGIN:
            case PaperInfo.LEFTMARGIN:
            case PaperInfo.RIGHTMARGIN:
                break;

            default:
                return false;
        }

        return true;
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

    @Override
    public void setName(String name)
    {
        infoName = name;
        paperInfo[0] = name;
    }

    @Override
    public String getDisplayName()
    {
        return infoName;
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

    public void setSortKey(String value)
    {
        Objects.requireNonNull(value, "SortKey cannot be null");
        sortKey = value;
    }

    @Override
    public String getSortKey()
    {
        return sortKey;
    }

}
