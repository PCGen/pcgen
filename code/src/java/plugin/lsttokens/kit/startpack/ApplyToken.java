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

import pcgen.cdom.enumeration.KitApply;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Kit;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with APPLY lst token within KitStartpack
 */
public class ApplyToken extends AbstractNonEmptyToken<Kit> implements CDOMPrimaryToken<Kit>
{
    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "APPLY";
    }

    @Override
    public Class<Kit> getTokenClass()
    {
        return Kit.class;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Kit kit, String value)
    {
        try
        {
            KitApply ka = KitApply.valueOf(value);
            kit.put(ObjectKey.APPLY_MODE, ka);
            return ParseResult.SUCCESS;
        } catch (IllegalArgumentException e)
        {
            return new ParseResult.Fail(getTokenName() + " encountered unexpected application type: " + value);
        }
    }

    @Override
    public String[] unparse(LoadContext context, Kit kit)
    {
        KitApply bd = kit.get(ObjectKey.APPLY_MODE);
        if (bd == null)
        {
            return null;
        }
        return new String[]{bd.toString()};
    }
}
