/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.equipmentmodifier;

import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with ITYPE token
 */
public class ItypeToken extends AbstractTokenWithSeparator<EquipmentModifier>
        implements CDOMPrimaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "ITYPE";
    }

    @Override
    protected char separator()
    {
        return '.';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, EquipmentModifier mod, String value)
    {
        context.getObjectContext().removeList(mod, ListKey.ITEM_TYPES);

        StringTokenizer tok = new StringTokenizer(value, Constants.DOT);
        while (tok.hasMoreTokens())
        {
            final String typeName = tok.nextToken();
            if ("double".equalsIgnoreCase(typeName))
            {
                return new ParseResult.Fail(
                        "IType must not be double. Ignoring occurrence in " + getTokenName() + Constants.COLON + value);
            } else
            {
                context.getObjectContext().addToList(mod, ListKey.ITEM_TYPES, Type.getConstant(typeName));
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, EquipmentModifier mod)
    {
        Changes<Type> changes = context.getObjectContext().getListChanges(mod, ListKey.ITEM_TYPES);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        return new String[]{StringUtil.join(changes.getAdded(), Constants.DOT)};
    }

    @Override
    public Class<EquipmentModifier> getTokenClass()
    {
        return EquipmentModifier.class;
    }
}
