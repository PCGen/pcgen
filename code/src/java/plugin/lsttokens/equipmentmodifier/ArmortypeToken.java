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

import java.util.Collection;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.processor.ChangeArmorType;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with ARMORTYPE token
 */
public class ArmortypeToken extends AbstractTokenWithSeparator<EquipmentModifier>
        implements CDOMPrimaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "ARMORTYPE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, EquipmentModifier mod, String value)
    {
        int pipeLoc = value.indexOf(Constants.PIPE);
        ChangeArmorType cat;
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(getTokenName() + " has no PIPE character: Must be of the form old|new");
        } else if (pipeLoc != value.lastIndexOf(Constants.PIPE))
        {
            return new ParseResult.Fail(
                    getTokenName() + " has too many PIPE characters: " + "Must be of the form old|new");
        } else
        {
            /*
             * TODO Are the ArmorTypes really a subset of Encumbrence?
             */
            String oldType = value.substring(0, pipeLoc);
            String newType = value.substring(pipeLoc + 1);
            /*
             * TODO Need some check if the Armor Types in value are not valid...
             */
            cat = new ChangeArmorType(oldType, newType);
        }
        context.getObjectContext().addToList(mod, ListKey.ARMORTYPE, cat);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, EquipmentModifier mod)
    {
        Changes<ChangeArmorType> changes = context.getObjectContext().getListChanges(mod, ListKey.ARMORTYPE);
        Collection<ChangeArmorType> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            // Zero indicates no Token
            return null;
        }
        TreeSet<String> set = new TreeSet<>();
        for (ChangeArmorType cat : added)
        {
            set.add(cat.getLSTformat());
        }
        return set.toArray(new String[0]);
    }

    @Override
    public Class<EquipmentModifier> getTokenClass()
    {
        return EquipmentModifier.class;
    }
}
