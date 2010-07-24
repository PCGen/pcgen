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
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.table;

import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.core.kit.KitGear;
import pcgen.core.kit.KitTable;
import pcgen.core.kit.KitTable.TableEntry;
import pcgen.core.utils.ParsingSeparator;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * VALUES token for KitTable
 */
public class ValuesToken extends AbstractNonEmptyToken<KitTable> implements
		CDOMSecondaryToken<KitTable>
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

	public Class<KitTable> getTokenClass()
	{
		return KitTable.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		KitTable kitTable, String value)
	{
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		while (sep.hasNext())
		{
			String thing = sep.next();
			if (thing.length() == 0)
			{
				return new ParseResult.Fail(getTokenName()
						+ " arguments has invalid pipe separator: " + value);
			}
			KitGear optionInfo = new KitGear();
			for (String s : thing.split("[\\[\\]]"))
			{
				if (s.length() == 0)
				{
					continue;
				}
				int colonLoc = s.indexOf(':');
				if (colonLoc == -1)
				{
					return new ParseResult.Fail("Expected colon in Value item: " + s
							+ " within: " + value);
				}
				String key = s.substring(0, colonLoc);
				String thingValue = s.substring(colonLoc + 1);
				ParseResult pr = context.processSubToken(optionInfo,
						getParentToken(), key, thingValue);
				if (!pr.passed())
				{
					return pr;
				}
			}
			if (!sep.hasNext())
			{
				return new ParseResult.Fail("Odd token count in Value: " + value);
			}
			String range = sep.next();
			if (!processRange(kitTable, optionInfo, range))
			{
				return new ParseResult.Fail("Invalid Range in Value: " + range
						+ " within " + value);
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
		Formula max = FormulaFactory.getFormulaFor(maxString);
		kitTable.addGear(optionInfo, min, max);
		return true;
	}

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
			String[] unparse = context.unparseSubtoken(rl.gear, getParentToken());
			if (unparse.length == 1)
			{
				sb.append(unparse[0]);
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
