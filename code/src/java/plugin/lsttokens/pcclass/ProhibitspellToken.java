/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * Class deals with PROHIBITSPELL Token
 */
public class ProhibitspellToken extends AbstractTokenWithSeparator<PCClass>
		implements CDOMPrimaryToken<PCClass>
{

	@Override
	public String getTokenName()
	{
		return "PROHIBITSPELL";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		PCClass pcc, String value)
	{
		SpellProhibitor sp = subParse(value);
		if (sp == null)
		{
			return ParseResult.INTERNAL_ERROR;
		}
		context.getObjectContext().addToList(pcc, ListKey.SPELL_PROHIBITOR, sp);
		return ParseResult.SUCCESS;
	}

	public SpellProhibitor subParse(String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String token = tok.nextToken();

		int dotLoc = token.indexOf(Constants.DOT);
		if (dotLoc == -1)
		{
			Logging.errorPrint(getTokenName()
					+ " has no . separator for arguments: " + value);
			return null;
		}
		String pstString = token.substring(0, dotLoc);
		ProhibitedSpellType type;

		try
		{
			type = ProhibitedSpellType.valueOf(pstString);
		}
		catch (IllegalArgumentException e)
		{
			Logging
					.errorPrint(getTokenName()
							+ " encountered an invalid Prohibited Spell Type: "
							+ value);
			Logging.errorPrint("  Legal values are: "
					+ StringUtil.join(Arrays.asList(ProhibitedSpellType
							.values()), ", "));
			return null;
		}

		SpellProhibitor spellProb =
				typeSafeParse(type, token.substring(dotLoc + 1));
		if (spellProb == null)
		{
			Logging.errorPrint("  entire token value was: " + value);
			return null;
		}
		if (!tok.hasMoreTokens())
		{
			// No prereqs, so we're done
			return spellProb;
		}
		token = tok.nextToken();

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging
						.errorPrint("   (Did you put more than one limit, or items after the "
								+ "PRExxx tags in " + getTokenName() + ":?)");
				return null;
			}
			spellProb.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}
		return spellProb;
	}

	private SpellProhibitor typeSafeParse(ProhibitedSpellType type, String args)
	{
		SpellProhibitor spellProb = new SpellProhibitor();
		spellProb.setType(type);
		if (args.isEmpty())
		{
			Logging.errorPrint(getTokenName() + " " + type
					+ " has no arguments");
			return null;
		}

		String joinChar = getJoinChar(type, new LinkedList<>());
		if (args.indexOf(joinChar) == 0)
		{
			Logging.errorPrint(getTokenName()
					+ " arguments may not start with " + joinChar);
			return null;
		}
		if (args.lastIndexOf(joinChar) == args.length() - 1)
		{
			Logging.errorPrint(getTokenName() + " arguments may not end with "
					+ joinChar);
			return null;
		}
		if (args.indexOf(joinChar + joinChar) != -1)
		{
			Logging
					.errorPrint(getTokenName()
							+ " arguments uses double separator " + joinChar
							+ joinChar);
			return null;
		}

		StringTokenizer elements = new StringTokenizer(args, joinChar);
		while (elements.hasMoreTokens())
		{
			String aValue = elements.nextToken();
			if (type.equals(ProhibitedSpellType.ALIGNMENT)
					&& (!aValue.equalsIgnoreCase("GOOD"))
					&& (!aValue.equalsIgnoreCase("EVIL"))
					&& (!aValue.equalsIgnoreCase("LAWFUL"))
					&& (!aValue.equalsIgnoreCase("CHAOTIC")))
			{
				Logging.errorPrint("Illegal PROHIBITSPELL:ALIGNMENT subtag '"
						+ aValue + "'");
				return null;
			}
			else
			{
				spellProb.addValue(aValue);
			}
		}
		return spellProb;
	}

	@Override
	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Changes<SpellProhibitor> changes = context.getObjectContext()
				.getListChanges(pcc, ListKey.SPELL_PROHIBITOR);
		Collection<SpellProhibitor> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		List<String> list = new ArrayList<>();
		for (SpellProhibitor sp : added)
		{
			StringBuilder sb = new StringBuilder();
			ProhibitedSpellType pst = sp.getType();
			sb.append(pst.toString().toUpperCase());
			sb.append('.');
			Collection<String> valueSet = sp.getValueList();
			String joinChar = getJoinChar(pst, valueSet);
			sb.append(StringUtil.join(new TreeSet<>(valueSet), joinChar));

			if (sp.hasPrerequisites())
			{
				sb.append(Constants.PIPE);
				sb.append(getPrerequisiteString(context, sp
						.getPrerequisiteList()));
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	private <T> String getJoinChar(ProhibitedSpellType pst,
			Collection<String> spValues)
	{
		return pst.getRequiredCount(spValues) == 1 ? Constants.COMMA
				: Constants.DOT;
	}

	@Override
	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
