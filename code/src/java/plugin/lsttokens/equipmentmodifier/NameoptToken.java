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

import pcgen.cdom.enumeration.EqModNameOpt;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with NAMEOPT token
 */
public class NameoptToken extends AbstractNonEmptyToken<EquipmentModifier>
		implements CDOMPrimaryToken<EquipmentModifier>
{

	@Override
	public String getTokenName()
	{
		return "NAMEOPT";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, EquipmentModifier mod, String value)
	{
		String optString = value;
		if (optString.startsWith("TEXT"))
		{
			if (optString.length() < 6 || optString.charAt(4) != '=')
			{
				return new ParseResult.Fail(getTokenName() + " has invalid TEXT argument: " + value);
			}
			optString = "TEXT";
			context.getObjectContext().put(mod, StringKey.NAME_TEXT, value.substring(5));
		}
		try
		{
			context.getObjectContext().put(mod, ObjectKey.NAME_OPT, EqModNameOpt.valueOfIgnoreCase(optString));
			return ParseResult.SUCCESS;
		}
		catch (IllegalArgumentException iae)
		{
			return new ParseResult.Fail("Invalid Naming Option provided in " + getTokenName() + ": " + value);
		}
	}

	@Override
	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		EqModNameOpt opt = context.getObjectContext().getObject(mod, ObjectKey.NAME_OPT);
		String text = context.getObjectContext().getString(mod, StringKey.NAME_TEXT);
		if (opt == null)
		{
            if (text != null) {
                context.addWriteMessage("Cannot have both NAME_TEXT without " + "NAME_OPT in EquipmentModifier");
            }
            return null;
        }
		String retString;
		if (opt.equals(EqModNameOpt.TEXT))
		{
			if (text == null)
			{
				context.addWriteMessage("Must have NAME_TEXT with " + "NAME_OPT TEXT in EquipmentModifier");
				return null;
			}
			else
			{
				retString = "TEXT=" + text;
			}
		}
		else
		{
			/*
			 * Don't test text == null here because .MODS will leave TEXT around -
			 * that's "okay"
			 */
			retString = opt.toString();
		}
		return new String[]{retString};
	}

	@Override
	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
