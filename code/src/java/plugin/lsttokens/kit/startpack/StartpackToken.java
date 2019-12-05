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

package plugin.lsttokens.kit.startpack;

import pcgen.core.Kit;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * STARTPACK token for Kit Startpack
 */
public class StartpackToken extends AbstractNonEmptyToken<Kit> implements CDOMPrimaryToken<Kit>
{
    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "STARTPACK";
    }

    @Override
    public Class<Kit> getTokenClass()
    {
        return Kit.class;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Kit obj, String value)
    {
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Kit obj)
    {
        return new String[]{obj.getDisplayName()};
    }

}
