/*
 * Formatcat.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.EqModFormatCat;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with FORMATCAT token, which indicates where the name of the equipment
 * modifier should be added in the name of any equipment item the eqmod is added
 * to.
 */
public class FormatcatToken extends AbstractNonEmptyToken<EquipmentModifier>
        implements CDOMPrimaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "FORMATCAT";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, EquipmentModifier mod, String value)
    {
        try
        {
            context.getObjectContext().put(mod, ObjectKey.FORMAT, EqModFormatCat.valueOf(value));
        } catch (IllegalArgumentException iae)
        {
            return new ParseResult.Fail("Invalid Format provided in " + getTokenName() + ": " + value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, EquipmentModifier mod)
    {
        EqModFormatCat fc = context.getObjectContext().getObject(mod, ObjectKey.FORMAT);
        if (fc == null)
        {
            return null;
        }
        return new String[]{fc.toString()};
    }

    @Override
    public Class<EquipmentModifier> getTokenClass()
    {
        return EquipmentModifier.class;
    }
}
