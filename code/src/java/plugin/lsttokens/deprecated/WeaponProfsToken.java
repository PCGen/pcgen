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
package plugin.lsttokens.deprecated;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class WeaponProfsToken implements CDOMSecondaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "WEAPONPROFS";
	}

	public String getParentToken()
	{
		return "CHOOSE";
	}

	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
	{
		if (value == null)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " requires additional arguments", context);
		}
		if (value.indexOf(',') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value, context);
		}
		if (value.indexOf('[') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value, context);
		}
		if (value.charAt(0) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value, context);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value, context);
		}
		if (value.indexOf("||") != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value, context);
		}
		String newValue = processMagicalWords(context, value);
		Logging.deprecationPrint("CHOOSE:WEAPONPROFS"
			+ " has been deprecated, "
			+ "please use CHOOSE:WEAPONPROFICIENCY|", context);
		return context.processSubToken(obj, "CHOOSE", "WEAPONPROFICIENCY",
			newValue);
	}

	private String processMagicalWords(LoadContext context, String value)
	{
		StringTokenizer st = new StringTokenizer(value, "|", true);
		StringBuilder sb = new StringBuilder();
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			// DEITYWEAPON unchanged
			if ("LIST".equalsIgnoreCase(tok))
			{
				tok = "PC";
			}
			if ("SIZE.".regionMatches(true, 0, tok, 0, 5))
			{
				final String profKey = tok.substring(7);
				Logging.deprecationPrint("CHOOSE:WEAPONPROFS|SIZE "
					+ "has been changed to PC.", context);
				tok = "PC[" + profKey + "]";
			}
			if ("ADD.".regionMatches(true, 0, tok, 0, 4))
			{
				tok = tok.substring(4);
			}
			if ("WSIZE.".regionMatches(true, 0, tok, 0, 6))
			{
				StringTokenizer bTok =
						new StringTokenizer(tok.substring(6), ".");
				StringBuilder wsize = new StringBuilder();
				wsize.append("PC,EQUIPMENT[");
				String wield = bTok.nextToken();

				boolean needsPipe = false;
				while (bTok.hasMoreTokens()) // any additional constraints
				{
					if (needsPipe)
					{
						wsize.append('|');
					}
					wsize.append("WIELD=");
					wsize.append(wield);
					wsize.append(",TYPE=");
					wsize.append(bTok.nextToken());
					needsPipe = true;
				}
				if (!needsPipe)
				{
					wsize.append("WIELD=");
					wsize.append(wield);
				}

				wsize.append(']');
				tok = wsize.toString();
			}
			if ("TYPE.".regionMatches(true, 0, tok, 0, 5)
				|| "TYPE=".regionMatches(true, 0, tok, 0, 5))
			{
				String prefix = "EQUIPMENT[TYPE=";
				String type = tok.substring(5);
				if ("Not.".regionMatches(true, 0, type, 0, 4))
				{
					type = type.substring(4);
					prefix = "EQUIPMENT[!TYPE=";
				}
				tok = prefix + type + "]";
			}
			if ("SPELLCASTER.".regionMatches(true, 0, tok, 0, 12))
			{
				tok = "SPELLCASTER[" + tok.substring(12) + "]";
			}
			sb.append(tok);
		}
		return sb.toString();
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		return null;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
