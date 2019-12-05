/**
 * SortKeyLst.java
 * Copyright 2010 (C) James Dempsey
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.SortKeyRequired;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractStringToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.PostValidationToken;
import pcgen.util.Logging;

/**
 * The Class {@code SortKeyLst} implements the global SORTKEY tag, which
 * allows items to be sorted in a custom manner.
 */
public class SortKeyLst extends AbstractStringToken<CDOMObject>
        implements CDOMPrimaryToken<CDOMObject>, PostValidationToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "SORTKEY";
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    protected StringKey stringKey()
    {
        return StringKey.SORT_KEY;
    }

    /**
     * Enforces that SORTKEY exists on any object which carries the
     * SortKeyRequired interface.
     * <p>
     * All such objects must have a SORTKEY and in PCGen 6.5/6.6, the file order
     * must match the SORTKEY order.
     */
    @Override
    public boolean process(LoadContext context, Collection<? extends CDOMObject> allObjects)
    {
        if (allObjects.isEmpty())
        {
            return true;
        }

        boolean returnValue = true;
        for (CDOMObject obj : allObjects)
        {
            String sortkey = obj.get(stringKey());
            if ((sortkey == null) && (obj instanceof SortKeyRequired))
            {
                Logging.errorPrint("Objects of type " + obj.getClass().getName() + " requires a SORTKEY", context);
                returnValue = false;
            }
        }
        return returnValue;
    }

    @Override
    public Class<CDOMObject> getValidationTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public int getPriority()
    {
        return 11;
    }
}
