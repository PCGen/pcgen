/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit.table;

import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.core.kit.KitGear;
import pcgen.core.kit.KitTable;
import pcgen.core.kit.KitTable.TableEntry;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * VALUES token for KitTable
 */
public class ValuesToken extends AbstractNonEmptyToken<KitTable> implements CDOMPrimaryToken<KitTable>
{
	/**
	 * Gets the name of the tag this class will parse.
	 *
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "VALUES";
	}

	@Override
	public Class<KitTable> getTokenClass()
	{
		return KitTable.class;
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, KitTable kitTable, String value)
	{
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		while (sep.hasNext())
		{
			String thing = sep.next();
			if (thing.isEmpty())
			{
				return new ParseResult.Fail(getTokenName() + " arguments has invalid pipe separator: " + value);
			}
			KitGear optionInfo = new KitGear();
			for (String s : thing.split("[\\[\\]]"))
			{
				if (s.isEmpty())
				{
					continue;
				}
				int colonLoc = s.indexOf(':');
				if (colonLoc == -1)
				{
					return new ParseResult.Fail("Expected colon in Value item: " + s + " within: " + value);
				}
				String key = s.substring(0, colonLoc);
				String thingValue = s.substring(colonLoc + 1);

					boolean passed = context.processToken(optionInfo, key, thingValue);
					if (!passed)
					{
						return new ParseResult.Fail("Failure in token: " + key);
					}


			}
			if (!sep.hasNext())
			{
				return new ParseResult.Fail("Odd token count in Value: " + value);
			}
			String range = sep.next();
			ParseResult pr = processRange(kitTable, optionInfo, range);
			if (!pr.passed())
			{
				return new ParseResult.Fail(
					"Invalid Range in Value: " + range + " within " + value + " report was: " + pr);
			}
		}

		return ParseResult.SUCCESS;
	}

	private ParseResult processRange(KitTable kitTable, KitGear optionInfo, String range)
	{
		ParseResult pr = checkSeparatorsAndNonEmpty(',', range);
		if (!pr.passed())
		{
			return pr;
		}

		ParsingSeparator sep = new ParsingSeparator(range, ',');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');
		String minString = sep.next();
		String maxString;
		if (sep.hasNext())
		{
			maxString = sep.next();
		}
		else
		{
			maxString = range;
		}
		if (sep.hasNext())
		{
			return new ParseResult.Fail("Expected more than one value in a range, found: " + range);
		}
		Formula min = FormulaFactory.getFormulaFor(minString);
		if (!min.isValid())
		{
			return new ParseResult.Fail("Min Formula in " + getTokenName() + " was not valid: " + min.toString());
		}
		Formula max = FormulaFactory.getFormulaFor(maxString);
		if (!max.isValid())
		{
			return new ParseResult.Fail("Max Formula in " + getTokenName() + " was not valid: " + max.toString());
		}
		kitTable.addGear(optionInfo, min, max);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, KitTable kitTable)
	{
		StringBuilder sb = new StringBuilder();
		List<TableEntry> list = kitTable.getList();
		if (list.isEmpty())
		{
			return null;
		}
		boolean first = true;
		for (TableEntry rl : list)
		{
			if (!first)
			{
				sb.append(Constants.PIPE);
			}
			Collection<String> unparse = context.unparse(rl.gear);
			if (unparse.size() == 1)
			{
				sb.append(unparse.iterator().next());
			}
			else
			{
				for (String s : unparse)
				{
					sb.append('[');
					sb.append(s);
					sb.append(']');
				}
			}
			sb.append(Constants.PIPE);
			sb.append(rl.lowRange.toString());
			if (!rl.lowRange.equals(rl.highRange))
			{
				sb.append(',');
				sb.append(rl.highRange.toString());
			}
			first = false;
		}
		return new String[]{sb.toString()};
	}
}
