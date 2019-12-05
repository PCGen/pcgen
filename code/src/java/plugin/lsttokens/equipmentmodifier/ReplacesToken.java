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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with REPLACES token
 */
public class ReplacesToken extends AbstractTokenWithSeparator<EquipmentModifier>
        implements CDOMPrimaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "REPLACES";
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, EquipmentModifier mod, String value)
    {
        context.getObjectContext().removeList(mod, ListKey.REPLACED_KEYS);

        StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
        while (tok.hasMoreTokens())
        {
            CDOMSingleRef<EquipmentModifier> ref =
                    context.getReferenceContext().getCDOMReference(EquipmentModifier.class, tok.nextToken());
            context.getObjectContext().addToList(mod, ListKey.REPLACED_KEYS, ref);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, EquipmentModifier mod)
    {
        Changes<CDOMSingleRef<EquipmentModifier>> changes =
                context.getObjectContext().getListChanges(mod, ListKey.REPLACED_KEYS);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(changes.getAdded(), Constants.COMMA)};
    }

    @Override
    public Class<EquipmentModifier> getTokenClass()
    {
        return EquipmentModifier.class;
    }
}
