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
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryParserToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ErrorParsingWrapper;
import pcgen.rules.persistence.token.ParseResult;

public class SpellsToken extends ErrorParsingWrapper<CDOMObject> implements CDOMSecondaryParserToken<CDOMObject>
{

	public String getTokenName()
	{
		return "SPELLS";
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
					+ " requires additional arguments");
		}
		if (value.charAt(0) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not start with | : " + value);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not end with | : " + value);
		}
		if (value.indexOf("||") != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments uses double separator || : " + value);
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		ComplexParseResult cpr = new ComplexParseResult();
		while (tok.hasMoreTokens())
		{
			String item = tok.nextToken();
			StringTokenizer st = new StringTokenizer(item, ",");
			while (st.hasMoreTokens())
			{
				String token = st.nextToken();
				if (token.startsWith("DOMAIN=") || token.startsWith("DOMAIN."))
				{
					cpr.addWarningMessage("DOMAIN is deprecated in "
							+ "CHOOSE:SPELLS, please use DOMAINLIST=x");
				}
				else if (token.startsWith("CLASS=")
						|| token.startsWith("CLASS."))
				{
					cpr.addWarningMessage("CLASS is deprecated in "
							+ "CHOOSE:SPELLS, please use CLASSLIST=x");
				}
				else if (token.startsWith("DOMAINLIST="))
				{
					int bracketLoc = token.indexOf('[');
					if (bracketLoc == -1)
					{
						if (token.length() < 12)
						{
							return new ParseResult.Fail("Invalid DOMAINLIST= entry for "
													+ "CHOOSE:SPELLS: requires a domain name");
						}
					}
					else
					{
						if (!token.endsWith("]"))
						{
							return new ParseResult.Fail("Invalid entry in "
									+ "CHOOSE:SPELLS: " + token
									+ " did not have matching brackets");
						}
						String domainName = token.substring(11, bracketLoc);
						if (domainName == null || domainName.length() == 0)
						{
							return new ParseResult.Fail("Invalid DOMAINLIST= entry for "
													+ "CHOOSE:SPELLS: requires a domain name");
						}
						cpr = validateRestriction(token.substring(bracketLoc + 1,
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
							return new ParseResult.Fail("Invalid CLASSLIST= entry for "
													+ "CHOOSE:SPELLS: requires a class name");
						}
					}
					else
					{
						if (!token.endsWith("]"))
						{
							return new ParseResult.Fail("Invalid entry in "
									+ "CHOOSE:SPELLS: " + token
									+ " did not have matching brackets");
						}
						String className = token.substring(10, bracketLoc);
						if (className == null || className.length() == 0)
						{
							return new ParseResult.Fail("Invalid CLASSLIST= entry for "
													+ "CHOOSE:SPELLS: requires a class name");
						}
						cpr = validateRestriction(token.substring(bracketLoc + 1,
								token.length() - 1));
					}
				}
				else if (token.startsWith("SPELLTYPE="))
				{
					int bracketLoc = token.indexOf('[');
					if (bracketLoc == -1)
					{
						if (token.length() < 10)
						{
							return new ParseResult.Fail("Invalid SPELLTYPE= entry for "
													+ "CHOOSE:SPELLS: requires a spell type");
						}
					}
					else
					{
						if (!token.endsWith("]"))
						{
							return new ParseResult.Fail("Invalid entry in "
									+ "CHOOSE:SPELLS: " + token
									+ " did not have matching brackets");
						}
						String className = token.substring(10, bracketLoc);
						if (className == null || className.length() == 0)
						{
							return new ParseResult.Fail("Invalid SPELLTYPE= entry for "
													+ "CHOOSE:SPELLS: requires a spell type");
						}
						cpr = validateRestriction(token.substring(bracketLoc + 1,
								token.length() - 1));
					}
				}
				else if (token.startsWith("ANY"))
				{
					int bracketLoc = token.indexOf('[');
					if (bracketLoc > -1 && bracketLoc != 3)
					{
						return new ParseResult.Fail("Invalid ANY entry for "
												+ "CHOOSE:SPELLS, bracket must immediately follow 'ANY'");
					}
					else
					{
						if (!token.endsWith("]"))
						{
							return new ParseResult.Fail("Invalid entry in "
									+ "CHOOSE:SPELLS: " + token
									+ " did not have matching brackets");
						}
						cpr = validateRestriction(token.substring(bracketLoc + 1,
								token.length() - 1));
					}
				}
				else if (token.startsWith("SCHOOL="))
				{
					if (token.length() < 8)
					{
						return new ParseResult.Fail("Invalid SCHOOL= entry for "
												+ "CHOOSE:SPELLS: requires a school name");
					}
				}
				else if (token.startsWith("SUBSCHOOL="))
				{
					if (token.length() < 11)
					{
						return new ParseResult.Fail("Invalid SUBSCHOOL= entry for "
												+ "CHOOSE:SPELLS: requires a subschool name");
					}
				}
				else if (token.startsWith("DESCRIPTOR="))
				{
					if (token.length() < 12)
					{
						return new ParseResult.Fail("Invalid DESCRIPTOR= entry for "
												+ "CHOOSE:SPELLS: requires a descriptor name");
					}
				}
				else if (token.startsWith("SPELLBOOK="))
				{
					if (token.length() < 11)
					{
						return new ParseResult.Fail("Invalid SPELLBOOK= entry for "
												+ "CHOOSE:SPELLS: requires a spellbook name");
					}
				}
				else if (token.startsWith("PROHIBITED="))
				{
					String prohibited = token.substring(11);
					if (!"YES".equals(prohibited) && !"NO".equals(prohibited))
					{
						return new ParseResult.Fail(
								"Invalid PROHIBITED= entry for "
										+ "CHOOSE:SPELLS: must be YES or NO");
					}
				}
				else if (token.startsWith("TYPE=") || token.startsWith("TYPE."))
				{
					if (token.length() < 6)
					{
						return new ParseResult.Fail("Invalid TYPE= entry for "
												+ "CHOOSE:SPELLS: requires a type name");
					}
				}
				else
				{
					if (token.indexOf('[') != -1 || token.indexOf('=') != -1)
					{
						return new ParseResult.Fail(
								"Invalid (unknown) entry: " + token + " for "
										+ "CHOOSE:SPELLS:");
					}
					// Just a spell name
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName()).append('|').append(value);
		context.obj.put(obj, StringKey.CHOICE_STRING, sb.toString());
		return cpr;
	}

	private ComplexParseResult validateRestriction(String restrString)
	{
		ComplexParseResult cpr = new ComplexParseResult();
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
				cpr.addWarningMessage("Unknown restriction: " + tok
						+ " in CHOOSE:SPELLS");
				continue;
			}
		}
		return cpr;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		String chooseString = context.getObjectContext().getString(cdo,
				StringKey.CHOICE_STRING);
		if (chooseString == null
				|| chooseString.indexOf(getTokenName() + '|') != 0)
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
