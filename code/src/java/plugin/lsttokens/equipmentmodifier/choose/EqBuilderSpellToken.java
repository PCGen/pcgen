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

public class EqBuilderSpellToken implements
		CDOMSecondaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "EQBUILDER.SPELL";
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
			context.obj.put(obj, StringKey.CHOICE_STRING, getTokenName());
			return true;
		}
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain , : " + value);
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
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() != 3)
		{
			Logging.errorPrint("COUNT:" + getTokenName()
					+ " requires three arguments: " + value);
			return false;
		}
		tok.nextToken();
		if (tok.hasMoreTokens())
		{
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
		}
		if (tok.hasMoreTokens())
		{
			String third = tok.nextToken();
			if (!third.equals("MAXLEVEL"))
			{
				try
				{
					Integer.parseInt(third);
				}
				catch (NumberFormatException nfe)
				{
					Logging
							.errorPrint("CHOOSE:"
									+ getTokenName()
									+ " third argument must be an Integer or 'MAXLEVEL': "
									+ value);
					return false;
				}
			}
		}
		if (tok.hasMoreTokens())
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " must have 1 to 3 | delimited arguments: " + value);
			return false;
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
		if (chooseString == null)
		{
			return null;
		}
		String returnString;
		if (getTokenName().equals(chooseString))
		{
			returnString = "";
		}
		else
		{
			if (chooseString.indexOf(getTokenName() + '|') == -1)
			{
				return null;
			}
			returnString = chooseString.substring(getTokenName().length() + 1);
		}
		return new String[] { returnString };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
