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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * {@code VisibleToken} handles the processing of the VISIBLE tag in the
 * definition of an Equipment Modifier.
 *
 * (Sat, 24 May 2008) $
 */
public class VisibleToken extends AbstractNonEmptyToken<EquipmentModifier>
		implements CDOMPrimaryToken<EquipmentModifier>
{

	@Override
	public String getTokenName()
	{
		return "VISIBLE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, EquipmentModifier eqm, String value)
	{
		Visibility vis;
        switch (value)
        {
            case "QUALIFY":
                vis = Visibility.QUALIFY;
                break;
            case "NO":
                vis = Visibility.HIDDEN;
                break;
            case "YES":
                vis = Visibility.DEFAULT;
                break;
            default:
                return new ParseResult.Fail("Can't understand Visibility: " + value);
        }
		context.getObjectContext().put(eqm, ObjectKey.VISIBILITY, vis);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, EquipmentModifier eqm)
	{
		Visibility vis = context.getObjectContext().getObject(eqm, ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		switch (vis)
		{
			case DEFAULT -> visString = "YES";
			case QUALIFY -> visString = "QUALIFY";
			case HIDDEN -> visString = "NO";
			default -> {
				context.addWriteMessage("Visibility " + vis + " is not a valid Visibility for a EqMod");
				return null;
			}
		}
		return new String[]{visString};
	}

	@Override
	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
