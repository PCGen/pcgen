/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import java.util.Set;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.PCStat;
import pcgen.core.utils.ParsingSeparator;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * @author djones4
 * 
 */
public class DefineLst implements CDOMPrimaryToken<CDOMObject>
{

	public static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	@Override
	public String getTokenName()
	{
		return "DEFINE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj,
			String value)
	{
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + " may not be empty", context);
		}
		String firstItem = sep.next();

		if (firstItem.startsWith("UNLOCK."))
		{
			if (sep.hasNext())
			{
				return new ParseResult.Fail(
						getTokenName()
								+ " found UNLOCK. with additional pipe separated item.  "
								+ "Must be of Format: varName|varFormula or "
								+ "LOCK.<stat>|value or UNLOCK.<stat>", context);
			}
			PCStat stat = context.ref.getAbbreviatedObject(PCSTAT_CLASS, value
					.substring(7));
			context.obj.addToList(obj, ListKey.UNLOCKED_STATS, stat);
			return ParseResult.SUCCESS;
		}
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + " varName|varFormula"
					+ "or LOCK.<stat>|value syntax requires an argument", context);
		}
		String var = firstItem;
		if (var.length() == 0)
		{
			return new ParseResult.Fail("Empty Variable Name found in "
					+ getTokenName() + ": " + value, context);
		}
		try
		{
			Formula f = FormulaFactory.getFormulaFor(sep.next());
			if (!f.isValid())
			{
				return new ParseResult.Fail("Formula in " + getTokenName()
						+ " was not valid: " + f.toString(), context);
			}
			if (sep.hasNext())
			{
				return new ParseResult.Fail(getTokenName() + " " + firstItem
						+ " syntax requires only one argument: " + value, context);
			}
			if (value.startsWith("LOCK."))
			{
				PCStat stat = context.ref.getAbbreviatedObject(PCSTAT_CLASS,
						firstItem.substring(5));
				context.getObjectContext().addToList(obj, ListKey.STAT_LOCKS,
						new StatLock(stat, f));
			}
			else
			{
				context.getObjectContext().put(obj,
						VariableKey.getConstant(var), f);
			}
			return ParseResult.SUCCESS;
		}
		catch (IllegalArgumentException e)
		{
			return new ParseResult.Fail("Illegal Formula found in "
					+ getTokenName() + ": " + value + " "
					+ e.getLocalizedMessage(), context);
		}
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<StatLock> changes = context.getObjectContext().getListChanges(
				obj, ListKey.STAT_LOCKS);
		Changes<PCStat> ulchanges = context.getObjectContext().getListChanges(
				obj, ListKey.UNLOCKED_STATS);
		Set<VariableKey> keys = context.getObjectContext().getVariableKeys(obj);
		TreeSet<String> set = new TreeSet<String>();
		if (keys != null && !keys.isEmpty())
		{
			for (VariableKey key : keys)
			{
				set.add(key.toString() + Constants.PIPE
						+ context.getObjectContext().getVariable(obj, key));
			}
		}
		if (changes != null && !changes.isEmpty())
		{
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage("DEFINE:LOCK does not support .CLEAR");
				return null;
			}
			if (changes.hasAddedItems())
			{
				for (StatLock sl : changes.getAdded())
				{
					set.add("LOCK." + sl.getLockedStat().getLSTformat() + "|"
							+ sl.getLockValue());
				}
			}
		}
		if (ulchanges != null && !ulchanges.isEmpty())
		{
			if (ulchanges.includesGlobalClear())
			{
				context.addWriteMessage("DEFINE:UNLOCK "
						+ "does not support .CLEAR");
				return null;
			}
			if (ulchanges.hasAddedItems())
			{
				for (PCStat st : ulchanges.getAdded())
				{
					set.add("UNLOCK." + st.getLSTformat());
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
