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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.persistence.token.AbstractIntToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with PLUS token
 */
public class PlusToken extends AbstractIntToken<EquipmentModifier> implements CDOMPrimaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "PLUS";
    }

    @Override
    protected IntegerKey integerKey()
    {
        return IntegerKey.PLUS;
    }

    @Override
    protected ParseResult checkValue(Integer plus)
    {
        if (plus == 0)
        {
            return new ParseResult.Fail(getTokenName() + " must be an integer not equal to 0");
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public Class<EquipmentModifier> getTokenClass()
    {
        return EquipmentModifier.class;
    }
}
