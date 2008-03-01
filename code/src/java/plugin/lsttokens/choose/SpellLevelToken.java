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

import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class SpellLevelToken implements ChooseLstToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (prefix.indexOf("NUMCHOICES=") != -1)
		{
			Logging.errorPrint("Cannot use NUMCHOICES= with CHOOSE:SPELLLEVEL, "
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
			if (bracketString.startsWith("BONUS:"))
			{
				//This is okay.
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
		int choiceCount;
		try
		{
			choiceCount = Integer.parseInt(start);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " first argument must be an Integer : " + value);
			return false;
		}
		StringTokenizer tok =
				new StringTokenizer(value.substring(pipeLoc + 1),
					Constants.PIPE);
		if (tok.countTokens() % 3 != 0)
		{
			Logging.errorPrint("COUNT:" + getTokenName()
				+ " requires a multiple of three arguments: " + value);
			return false;
		}
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			int equalsLoc = tokString.indexOf("=");
			if (equalsLoc == tokString.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments must have value after = : " + tokString);
				Logging.errorPrint("  entire token was: " + value);
				return false;
			}
			if (!tokString.startsWith("CLASS=")
				&& !tokString.startsWith("TYPE="))
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " argument must start with CLASS= or TYPE= : "
					+ tokString);
				Logging.errorPrint("  Entire Token was: " + value);
				return false;
			}
			String second = tok.nextToken();
			try
			{
				Integer.parseInt(second);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " second argument must be an Integer : " + value);
				return false;
			}
			String lastTok = tok.nextToken();
			if (lastTok.indexOf(".A[") != -1 || lastTok.endsWith(".A"))
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " use of .A in third argument is deprecated, "
						+ "please contact the PCGen team for alternatives:"
						+ value);
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("NUMCHOICES=").append(choiceCount).append('|');
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value).append(suffix);
		po.setChoiceString(sb.toString());
		return true;
	}

	public String getTokenName()
	{
		return "SPELLLEVEL";
	}
}
