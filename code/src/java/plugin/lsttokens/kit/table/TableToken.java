/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit.table;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Kit;
import pcgen.core.kit.BaseKit;
import pcgen.core.kit.KitTable;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * TABLE token for KitTable
 */
public class TableToken extends AbstractNonEmptyToken<KitTable>
        implements CDOMPrimaryToken<KitTable>, DeferredToken<Kit>
{
    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "TABLE";
    }

    @Override
    public Class<KitTable> getTokenClass()
    {
        return KitTable.class;
    }

    @Override

    protected ParseResult parseNonEmptyToken(LoadContext context, KitTable kitTable, String value)
    {
        kitTable.setTableName(value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitTable kitTable)
    {
        String bd = kitTable.getTableName();
        if (bd == null)
        {
            return null;
        }
        return new String[]{bd};
    }

    @Override
    public boolean process(LoadContext context, Kit obj)
    {
        for (BaseKit bk : obj.getSafeListFor(ListKey.KIT_TASKS))
        {
            if (bk instanceof KitTable)
            {
                obj.removeFromListFor(ListKey.KIT_TASKS, bk);
                KitTable kt = obj.addTable((KitTable) bk);
                if (kt != null)
                {
                    Logging.errorPrint("Kit Table: " + kt.getTableName() + " in Kit " + obj.getKeyName()
                            + " was a duplicate, " + "Kit had more than one table with that name.");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Class<Kit> getDeferredTokenClass()
    {
        return Kit.class;
    }
}
