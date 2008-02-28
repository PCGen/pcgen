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

public class SpellsToken implements ChooseLstToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " requires additional arguments");
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
		while (tok.hasMoreTokens())
		{
			String item = tok.nextToken();
			StringTokenizer st = new StringTokenizer(item, ",");
			while (st.hasMoreTokens())
			{
				String token = st.nextToken();
				if (token.startsWith("DOMAIN=") || token.startsWith("DOMAIN."))
				{
					Logging.deprecationPrint("DOMAIN is deprecated in "
							+ "CHOOSE:SPELLS, please use DOMAINLIST=x");
				}
				else if (token.startsWith("CLASS=")
						|| token.startsWith("CLASS."))
				{
					Logging.deprecationPrint("CLASS is deprecated in "
							+ "CHOOSE:SPELLS, please use CLASSLIST=x");
				}
				else if (token.startsWith("DOMAINLIST="))
				{
					int bracketLoc = token.indexOf('[');
					if (bracketLoc == -1)
					{
						if (token.length() < 12)
						{
							Logging.errorPrint("Invalid DOMAINLIST= entry for "
									+ "CHOOSE:SPELLS: requires a domain name");
							return false;
						}
					}
					else
					{
						if (!token.endsWith("]"))
						{
							Logging.errorPrint("Invalid entry in "
									+ "CHOOSE:SPELLS: " + token
									+ " did not have matching brackets");
							return false;
						}
						String domainName = token.substring(11, bracketLoc);
						if (domainName == null || domainName.length() == 0)
						{
							Logging.errorPrint("Invalid DOMAINLIST= entry for "
									+ "CHOOSE:SPELLS: requires a domain name");
							return false;
						}
						validateRestriction(token.substring(bracketLoc + 1,
								token.length() - 1));
					}
				}
				else if (token.startsWith("CLASSLIST="))
				{
					int bracketLoc = token.indexOf('[');
					if (bracketLoc == -1)
					{
						if (token.length() < 10)
						{
							Logging.errorPrint("Invalid CLASSLIST= entry for "
									+ "CHOOSE:SPELLS: requires a class name");
							return false;
						}
					}
					else
					{
						if (!token.endsWith("]"))
						{
							Logging.errorPrint("Invalid entry in "
									+ "CHOOSE:SPELLS: " + token
									+ " did not have matching brackets");
							return false;
						}
						String className = token.substring(10, bracketLoc);
						if (className == null || className.length() == 0)
						{
							Logging.errorPrint("Invalid CLASSLIST= entry for "
									+ "CHOOSE:SPELLS: requires a class name");
							return false;
						}
						validateRestriction(token.substring(bracketLoc + 1,
								token.length() - 1));
					}
				}
				else if (token.startsWith("SCHOOL="))
				{
					if (token.length() < 8)
					{
						Logging.errorPrint("Invalid SCHOOL= entry for "
								+ "CHOOSE:SPELLS: requires a school name");
						return false;
					}
				}
				else if (token.startsWith("SUBSCHOOL="))
				{
					if (token.length() < 11)
					{
						Logging.errorPrint("Invalid SUBSCHOOL= entry for "
								+ "CHOOSE:SPELLS: requires a subschool name");
						return false;
					}
				}
				else if (token.startsWith("DESCRIPTOR="))
				{
					if (token.length() < 12)
					{
						Logging.errorPrint("Invalid DESCRIPTOR= entry for "
								+ "CHOOSE:SPELLS: requires a descriptor name");
						return false;
					}
				}
				else if (token.startsWith("SPELLBOOK="))
				{
					if (token.length() < 11)
					{
						Logging.errorPrint("Invalid SPELLBOOK= entry for "
								+ "CHOOSE:SPELLS: requires a spellbook name");
						return false;
					}
				}
				else if (token.startsWith("PROHIBITED="))
				{
					String prohibited = token.substring(11);
					if (!"YES".equals(prohibited) && !"NO".equals(prohibited))
					{
						Logging.errorPrint("Invalid PROHIBITED= entry for "
								+ "CHOOSE:SPELLS: must be YES or NO");
						return false;
					}
				}
				else if (token.startsWith("TYPE=") || token.startsWith("TYPE."))
				{
					if (token.length() < 6)
					{
						Logging.errorPrint("Invalid TYPE= entry for "
								+ "CHOOSE:SPELLS: requires a type name");
						return false;
					}
				}
				else
				{
					// Just a spell name
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		po.setChoiceString(sb.toString());
		return true;
	}

	private void validateRestriction(String restrString)
	{
		StringTokenizer restr = new StringTokenizer(restrString, ";");
		while (restr.hasMoreTokens())
		{
			String tok = restr.nextToken();
			if (tok.startsWith("LEVELMAX="))
			{
			}
			else if (tok.startsWith("LEVELMIN="))
			{
			}
			else if ("KNOWN=YES".equals(tok))
			{
			}
			else if ("KNOWN=NO".equals(tok))
			{
			}
			else
			{
				Logging.errorPrint("Unknown restriction: " + tok
						+ " in CHOOSE:SPELLS");
				continue;
			}
		}
	}

	public String getTokenName()
	{
		return "SPELLS";
	}
}
