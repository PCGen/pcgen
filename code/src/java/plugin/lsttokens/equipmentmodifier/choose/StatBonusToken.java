/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.equipmentmodifier.choose;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class StatBonusToken implements CDOMSecondaryToken<EquipmentModifier>
{

	@Override
	public String getTokenName()
	{
		return "STATBONUS";
	}

	@Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, EquipmentModifier obj, String value)
	{
		if (value == null)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " requires additional arguments");
		}
		if (value.indexOf('[') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not contain [] : " + value);
		}
		if (value.charAt(0) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not start with | : " + value);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not end with | : " + value);
		}
		if (value.contains("||"))
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments uses double separator || : " + value);
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail(
				"CHOOSE:" + getTokenName() + " must have two or more | delimited arguments : " + value);
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		Collection<PCStat> list = context.getReferenceContext().getConstructedCDOMObjects(PCStat.class);
		Integer min = null;
		Integer max = null;
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if (tokString.startsWith("MIN="))
			{
				min = Integer.valueOf(tokString.substring(4));
				// OK
			}
			else if (tokString.startsWith("MAX="))
			{
				max = Integer.valueOf(tokString.substring(4));
				// OK
			}
			else if (tokString.startsWith("TITLE="))
			{
				Logging.debugPrint("Do not process TITLE=");
			}
			else if (tokString.startsWith("INCREMENT="))
			{
				// OK
				Integer.parseInt(tokString.substring(4));
			}
			else
			{
				// Ensure this is a primitive STAT
				boolean found = false;
				for (PCStat stat : list)
				{
					if (tokString.equals(stat.getKeyName()))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					return new ParseResult.Fail(
						"Did not find STAT: " + tokString + " used in CHOOSE:STATBONUS " + value);
				}
			}
		}
		if (max == null)
		{
			if (min != null)
			{
				return new ParseResult.Fail("Cannot have MIN=n without MAX=m in CHOOSE:STATBONUS: " + value);
			}
		}
		else
		{
			if (min == null)
			{
				return new ParseResult.Fail("Cannot have MAX=n without MIN=m in CHOOSE:STATBONUS: " + value);
			}
			if (max < min)
			{
				return new ParseResult.Fail("Cannot have MAX= less than MIN= in CHOOSE:STATBONUS: " + value);
			}
		}
		context.getObjectContext().put(obj, StringKey.CHOICE_STRING, getTokenName() + '|' + value);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, EquipmentModifier eqMod)
	{
		String chooseString = context.getObjectContext().getString(eqMod, StringKey.CHOICE_STRING);
		if (chooseString == null || !chooseString.contains(getTokenName() + '|'))
		{
			return null;
		}
		return new String[]{chooseString.substring(getTokenName().length() + 1)};
	}

	@Override
	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
