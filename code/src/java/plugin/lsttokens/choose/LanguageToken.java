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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class LanguageToken implements CDOMSecondaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "LANGUAGE";
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
		if (value.indexOf('[') != -1)
		{
			Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
					+ " arguments may not contain [] : " + value);
			return false;
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
		StringBuilder sb = new StringBuilder();
		sb.append("Language").append('(');
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		boolean first = true;
		while (st.hasMoreTokens())
		{
			if (!first)
			{
				sb.append(',');
			}
			first = false;
			String tokString = st.nextToken();
			if (tokString.indexOf('.') != tokString.lastIndexOf('.'))
			{
				Logging.log(Logging.LST_ERROR, "CHOOSE:" + getTokenName()
						+ " arguments cannot have two . : " + tokString);
				Logging.log(Logging.LST_ERROR,
						"  format for argument must be X or X.Y");
				Logging.log(Logging.LST_ERROR, "  entire token was: " + value);
				return false;
			}
			sb.append(tokString);
		}
		sb.append(')');
		context.obj.put(obj, StringKey.CHOICE_STRING, sb.toString());
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
}
