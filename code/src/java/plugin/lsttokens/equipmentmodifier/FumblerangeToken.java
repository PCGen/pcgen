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

import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractStringToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with FUMBLERANGE token
 */
public class FumblerangeToken extends AbstractStringToken<EquipmentModifier>
        implements CDOMPrimaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "FUMBLERANGE";
    }

    @Override
    public Class<EquipmentModifier> getTokenClass()
    {
        return EquipmentModifier.class;
    }

    @Override
    protected StringKey stringKey()
    {
        return StringKey.FUMBLE_RANGE;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, EquipmentModifier obj, String value)
    {
        if (ControlUtilities.hasControlToken(context, CControl.FUMBLERANGE))
        {
            return new ParseResult.Fail(getTokenName() + " is disabled when FUMBLERANGE control is used: " + value);
        }
        return super.parseNonEmptyToken(context, obj, value);
    }

}
