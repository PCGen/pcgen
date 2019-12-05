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

package plugin.lsttokens.kit.gear;

import pcgen.cdom.base.CDOMReference;
import pcgen.core.Equipment;
import pcgen.core.kit.KitGear;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * GEAR Token for KitGear
 */
public class GearToken extends AbstractNonEmptyToken<KitGear> implements CDOMPrimaryToken<KitGear>
{
    private static final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;

    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "GEAR";
    }

    @Override
    public Class<KitGear> getTokenClass()
    {
        return KitGear.class;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, KitGear kitGear, String value)
    {
        CDOMReference<Equipment> ref = TokenUtilities.getTypeOrPrimitive(context, EQUIPMENT_CLASS, value);
        kitGear.setEquipment(ref);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitGear kitGear)
    {
        CDOMReference<Equipment> ref = kitGear.getEquipment();
        if (ref == null)
        {
            return null;
        }
        return new String[]{ref.getLSTformat(false)};
    }

}
