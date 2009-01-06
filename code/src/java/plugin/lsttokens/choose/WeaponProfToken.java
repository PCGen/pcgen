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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class WeaponProfToken implements CDOMSecondaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "WEAPONPROF";
	}

	public String getParentToken()
	{
		return "CHOOSE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		if (value == null)
		{
			Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
					+ " requires additional arguments");
			return false;
		}
		if (value.indexOf(',') != -1)
		{
			Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
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
				Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
						+ " arguments does not contain matching brackets: "
						+ value);
				return false;
			}
			String bracketString = value.substring(bracketLoc + 1, closeLoc);
			if ("WEAPONPROF".equals(bracketString))
			{
				// This is okay.
				suffix = "[WEAPONPROF]" + suffix;
			}
			else if (bracketString.startsWith("FEAT="))
			{
				// This is okay.
				suffix = "[" + bracketString + "]" + suffix;
			}
			else
			{
				Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
						+ " arguments may not contain [" + bracketString
						+ "] : " + value);
				return false;
			}
			value = value.substring(0, bracketLoc);
		}
		if (value.charAt(0) == '|')
		{
			Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
					+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
					+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
					+ " arguments uses double separator || : " + value);
			return false;
		}
		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging
					.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
							+ " must have two or more | delimited arguments : "
							+ value);
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
			Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
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
				Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
						+ " arguments must have value after = : " + tokString);
				Logging.log(Logging.LST_ERROR, "  entire token was: " + value);
				return false;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName()).append('|').append(firstarg).append('|')
				.append(profs).append(suffix);
		Formula f = FormulaFactory.getFormulaFor(firstarg);
		context.obj.put(obj, FormulaKey.NUMCHOICES, f);
		context.obj.put(obj, FormulaKey.EMBEDDED_SELECT, f);
		context.obj.put(obj, StringKey.CHOICE_STRING, sb.toString());
		/*
		 * TODO Error catching here for SELECT/CHOOSE?
		 */
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		String chooseString = context.getObjectContext().getString(cdo,
				StringKey.CHOICE_STRING);
		if (chooseString == null
				|| chooseString.indexOf(getTokenName() + '|') == -1)
		{
			return null;
		}
		return new String[] { chooseString
				.substring(getTokenName().length() + 1) };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	// TODO Deferred?
	// if (prefix.indexOf("NUMCHOICES=") != -1)
	// {
	// Logging.log(Logging.LST_ERROR, "Cannot use NUMCHOICES= with
	// CHOOSE:WEAPONPROF, "
	// + "as it has an integrated choice count");
	// return false;
	// }

}
