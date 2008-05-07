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
package plugin.lsttokens.choose;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.PObject;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class WeaponProfToken implements ChooseLstToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (prefix.indexOf("NUMCHOICES=") != -1)
		{
			Logging.errorPrint("Cannot use NUMCHOICES= with CHOOSE:WEAPONPROF, "
				+ "as it has an integrated choice count");
			return false;
		}
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " requires additional arguments");
			return false;
		}
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return false;
		}
		String suffix = "";
		int bracketLoc;
		while ((bracketLoc = value.lastIndexOf('[')) != -1)
		{
			int closeLoc = value.indexOf("]", bracketLoc);
			if (closeLoc != value.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments does not contain matching brackets: "
					+ value);
				return false;
			}
			String bracketString = value.substring(bracketLoc + 1, closeLoc);
			if ("WEAPONPROF".equals(bracketString))
			{
				//This is okay.
				suffix = "[WEAPONPROF]" + suffix;
			}
			else if (bracketString.startsWith("FEAT="))
			{
				// This is okay.
				suffix = "[" + bracketString + "]" + suffix;
			}
			else
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain [" + bracketString + "] : "
					+ value);
				return false;
			}
			value = value.substring(0, bracketLoc);
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
		String start = value.substring(0, pipeLoc);
		int firstarg;
		try
		{
			firstarg = Integer.parseInt(start);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " first argument must be an Integer : " + value);
			return false;
		}
		String profs = value.substring(pipeLoc + 1);
		StringTokenizer st = new StringTokenizer(profs, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			int equalsLoc = tokString.indexOf("=");
			if (equalsLoc == tokString.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments must have value after = : " + tokString);
				Logging.errorPrint("  entire token was: " + value);
				return false;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("NUMCHOICES=").append(firstarg).append('|');
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(profs).append(suffix);
		po.setChoiceString(sb.toString());
		po.setSelect(start);
		/*
		 * TODO Error catching here for SELECT/CHOOSE?
		 */
		return true;
	}

	public String getTokenName()
	{
		return "WEAPONPROF";
	}
}
