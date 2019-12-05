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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.EquipmentModifier;
import pcgen.core.SpecialProperty;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with SPROP token
 */
public class SpropToken extends AbstractTokenWithSeparator<EquipmentModifier>
        implements CDOMPrimaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "SPROP";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, EquipmentModifier mod, String value)
    {
        if (Constants.LST_DOT_CLEAR.equals(value))
        {
            context.getObjectContext().removeList(mod, ListKey.SPECIAL_PROPERTIES);
            return ParseResult.SUCCESS;
        }

        SpecialProperty sa = SpecialProperty.createFromLst(value);
        if (sa == null)
        {
            return ParseResult.INTERNAL_ERROR;
        }
        context.getObjectContext().addToList(mod, ListKey.SPECIAL_PROPERTIES, sa);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, EquipmentModifier mod)
    {
        Changes<SpecialProperty> changes = context.getObjectContext().getListChanges(mod, ListKey.SPECIAL_PROPERTIES);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        List<String> list = new ArrayList<>();
        Collection<SpecialProperty> added = changes.getAdded();
        boolean globalClear = changes.includesGlobalClear();
        if (globalClear)
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        if (added != null && !added.isEmpty())
        {
            for (SpecialProperty sp : added)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(sp.getDisplayName());
                if (sp.hasPrerequisites())
                {
                    sb.append(Constants.PIPE);
                    sb.append(getPrerequisiteString(context, sp.getPrerequisiteList()));
                }
                list.add(sb.toString());
            }
        }
        if (list.isEmpty())
        {
            context.addWriteMessage(
                    getTokenName() + " was expecting non-empty changes to include " + "added items or global clear");
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<EquipmentModifier> getTokenClass()
    {
        return EquipmentModifier.class;
    }
}
