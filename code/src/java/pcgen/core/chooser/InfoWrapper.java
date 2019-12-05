/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.core.chooser;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.facade.core.InfoFacade;
import pcgen.util.SortKeyAware;

/**
 * InfoWrapper is a general purpose container for data in choosers. It wraps
 * data of any type in an InfoFacade compliant wrapper.
 */
public class InfoWrapper implements InfoFacade, SortKeyAware
{
    private static final NumberFormat SORTABLE_NUMBER_FORMAT = new DecimalFormat("0000000000.00000");

    private final Object obj;

    public InfoWrapper(Object cdomObj)
    {
        this.obj = cdomObj;

    }

    @Override
    public String toString()
    {
        return String.valueOf(obj);
    }

    @Override
    public String getSource()
    {
        return "";
    }

    @Override
    public String getSourceForNodeDisplay()
    {
        return "";
    }

    @Override
    public String getKeyName()
    {
        return obj.toString();
    }

    @Override
    public boolean isNamePI()
    {
        return false;
    }

    /**
     * @return the obj
     */
    public Object getObj()
    {
        return obj;
    }

    @Override
    public String getType()
    {
        if (obj instanceof CDOMObject)
        {
            final List<Type> types = ((CDOMObject) obj).getSafeListFor(ListKey.TYPE);
            return StringUtil.join(types, ".");
        }
        return "";
    }

    @Override
    public String getSortKey()
    {
        if (obj instanceof Number)
        {
            return SORTABLE_NUMBER_FORMAT.format(100000.0d + ((Number) obj).doubleValue());
        }
        return toString();
    }
}
