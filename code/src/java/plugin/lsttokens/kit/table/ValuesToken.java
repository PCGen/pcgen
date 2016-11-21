/*
 * ValuesToken.java
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
 *
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
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
import pcgen.util.Logging;

/**
 * VALUES token for KitTable
 */
public class ValuesToken extends AbstractNonEmptyToken<KitTable> implements
		CDOMPrimaryToken<KitTable>
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
	protected ParseResult parseNonEmptyToken(LoadContext context,
		KitTable kitTable, String value)
	{
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		while (sep.hasNext())
		{
			String thing = sep.next();
			if (thing.isEmpty())
			{
				return new ParseResult.Fail(getTokenName()
						+ " arguments has invalid pipe separator: " + value, context);
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
					return new ParseResult.Fail("Expected colon in Value item: " + s
							+ " within: " + value, context);
				}
				String key = s.substring(0, colonLoc);
				String thingValue = s.substring(colonLoc + 1);
				try
				{
					boolean passed = context.processToken(optionInfo, key,
							thingValue);
					if (!passed)
					{
						return new ParseResult.Fail("Failure in token: " + key, context);
					}
				}
				catch (PersistenceLayerException e)
				{
					return new ParseResult.Fail("Failure in token: " + key
							+ " " + e.getMessage(), context);
				}
			}
			if (!sep.hasNext())
			{
				return new ParseResult.Fail("Odd token count in Value: " + value, context);
			}
			String range = sep.next();
			if (!processRange(kitTable, optionInfo, range))
			{
				return new ParseResult.Fail("Invalid Range in Value: " + range
						+ " within " + value, context);
			}
		}

		return ParseResult.SUCCESS;
	}

	private boolean processRange(KitTable kitTable, KitGear optionInfo,
			String range)
	{
		if (isEmpty(range) || hasIllegalSeparator(',', range))
		{
			return false;
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
			return false;
		}
		Formula min = FormulaFactory.getFormulaFor(minString);
		if (!min.isValid())
		{
			Logging.errorPrint("Min Formula in " + getTokenName()
					+ " was not valid: " + min.toString());
			return false;
		}
		Formula max = FormulaFactory.getFormulaFor(maxString);
		if (!max.isValid())
		{
			Logging.errorPrint("Max Formula in " + getTokenName()
					+ " was not valid: " + max.toString());
			return false;
		}
		kitTable.addGear(optionInfo, min, max);
		return true;
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
		return new String[] { sb.toString() };
	}
}
