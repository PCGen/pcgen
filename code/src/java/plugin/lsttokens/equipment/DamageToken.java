/*
 *
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.core.RollInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

import org.apache.commons.lang3.StringUtils;

/**
 * Deals with DAMAGE token
 */
public class DamageToken extends AbstractNonEmptyToken<Equipment> implements CDOMPrimaryToken<Equipment>
{

    @Override
    public String getTokenName()
    {
        return "DAMAGE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
    {
        if (!"-".equals(value) && !"special".equalsIgnoreCase(value))
        {
            String errorMessage = RollInfo.validateRollString(value);
            if (!StringUtils.isBlank(errorMessage))
            {
                return new ParseResult.Fail(getTokenName() + " is invalid: " + errorMessage);
            }
        }
        context.getObjectContext().put(eq.getEquipmentHead(1), StringKey.DAMAGE, value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Equipment eq)
    {
        EquipmentHead head = eq.getEquipmentHeadReference(1);
        if (head == null)
        {
            return null;
        }
        String damage = context.getObjectContext().getString(head, StringKey.DAMAGE);
        if (damage == null)
        {
            return null;
        }
        return new String[]{damage};
    }

    @Override
    public Class<Equipment> getTokenClass()
    {
        return Equipment.class;
    }
}
