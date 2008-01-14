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

import pcgen.core.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EqModChooseLstToken;
import pcgen.util.Logging;

public class NumberToken implements EqModChooseLstToken
{

	public String getTokenName()
	{
		return "NUMBER";
	}

	public boolean parse(EquipmentModifier mod, String prefix, String value)
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
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " must have two or more | delimited arguments : " + value);
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
				//OK
			}
			else if (tokString.startsWith("MAX="))
			{
				max = Integer.valueOf(tokString.substring(4));
				//OK
			}
			else if (tokString.startsWith("TITLE="))
			{
				//OK
			}
			else if (tokString.startsWith("INCREMENT="))
			{
				//OK
				Integer.parseInt(tokString.substring(4));
			}
			else if (tokString.startsWith("NOSIGN"))
			{
				//OK
			}
			else if (tokString.startsWith("MULTIPLE"))
			{
				//OK
			}
			else
			{
				Integer.parseInt(tokString);
			}
		}
		if (max == null)
		{
			if (min != null)
			{
				Logging
						.errorPrint("Cannot have MIN=n without MAX=m in CHOOSE:NUMBER: "
								+ value);
				return false;
			}
		}
		else
		{
			if (min == null)
			{
				Logging
						.errorPrint("Cannot have MAX=n without MIN=m in CHOOSE:NUMBER: "
								+ value);
				return false;
			}
			if (max < min)
			{
				Logging
						.errorPrint("Cannot have MAX= less than MIN= in CHOOSE:NUMBER: "
								+ value);
				return false;
			}
		}
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		mod.setChoiceString(sb.toString());
		return true;
	}
}
