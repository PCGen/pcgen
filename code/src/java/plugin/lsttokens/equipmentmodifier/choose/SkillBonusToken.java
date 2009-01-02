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

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class SkillBonusToken implements CDOMSecondaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "SKILLBONUS";
	}

	public String getParentToken()
	{
		return "CHOOSE";
	}

	public boolean parse(LoadContext context, EquipmentModifier obj,
			String value) throws PersistenceLayerException
	{
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " requires additional arguments");
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain [] : " + value);
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments uses double separator || : " + value);
			return false;
		}
		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging
					.errorPrint("CHOOSE:" + getTokenName()
							+ " must have two or more | delimited arguments : "
							+ value);
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
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
				// OK
			}
			else if (tokString.startsWith("INCREMENT="))
			{
				// OK
				Integer.parseInt(tokString.substring(4));
			}
			else
			{
				// Assume it's a primitive skill??
			}
		}
		if (max == null)
		{
			if (min != null)
			{
				Logging
						.errorPrint("Cannot have MIN=n without MAX=m in CHOOSE:STATBONUS: "
								+ value);
				return false;
			}
		}
		else
		{
			if (min == null)
			{
				Logging
						.errorPrint("Cannot have MAX=n without MIN=m in CHOOSE:STATBONUS: "
								+ value);
				return false;
			}
			if (max < min)
			{
				Logging
						.errorPrint("Cannot have MAX= less than MIN= in CHOOSE:STATBONUS: "
								+ value);
				return false;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName()).append('|').append(value);
		context.obj.put(obj, StringKey.CHOICE_STRING, sb.toString());
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier eqMod)
	{
		String chooseString = context.getObjectContext().getString(eqMod,
				StringKey.CHOICE_STRING);
		if (chooseString == null
				|| chooseString.indexOf(getTokenName() + '|') == -1)
		{
			return null;
		}
		return new String[] { chooseString
				.substring(getTokenName().length() + 1) };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
