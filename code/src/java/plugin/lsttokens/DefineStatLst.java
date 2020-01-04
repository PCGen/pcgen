/*
 * Copyright 2013 (C) James Dempsey
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

import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCStat;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code DefineStatLst} parses the DEFINESTAT tag. Valid sub tags are:
 * <p>
 * DEFINESTAT:LOCK|stat|value <br>
 * DEFINESTAT:UNLOCK|stat <br>
 * DEFINESTAT:NONSTAT|stat <br>
 * DEFINESTAT:STAT|stat <br>
 * DEFINESTAT:MINVALUE|stat|value
 *
 * 
 */
public class DefineStatLst implements CDOMPrimaryToken<CDOMObject>
{

	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	public enum DefineStatSubToken
	{
		LOCK, UNLOCK, NONSTAT, STAT, MINVALUE, MAXVALUE
	}

	@Override
	public String getTokenName()
	{
		return "DEFINESTAT";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
		}
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + " may not be empty");
		}
		String firstItem = sep.next();

		DefineStatSubToken subToken;
		try
		{
			subToken = DefineStatSubToken.valueOf(firstItem);
		}
		catch (IllegalArgumentException e1)
		{
			return new ParseResult.Fail(
				"Found unexpected sub tag " + firstItem + " in " + getTokenName() + Constants.COLON + value
					+ ". Must be one of " + StringUtils.join(DefineStatSubToken.values(), ", ") + Constants.DOT);
		}

		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + Constants.COLON + subToken + "| must be followed by a stat.");
		}
		String statKey = sep.next();
		CDOMSingleRef<PCStat> stat = context.getReferenceContext().getCDOMReference(PCSTAT_CLASS, statKey);

		Formula f = null;
		if (subToken == DefineStatSubToken.LOCK || subToken == DefineStatSubToken.MINVALUE
			|| subToken == DefineStatSubToken.MAXVALUE)
		{
			if (!sep.hasNext())
			{
				return new ParseResult.Fail(
					getTokenName() + Constants.COLON + subToken + "| must be followed by both a stat and a value.");
			}
			String formula = sep.next();
			f = FormulaFactory.getFormulaFor(formula);
			if (!f.isValid())
			{
				return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + f.toString());
			}
		}

		if (sep.hasNext())
		{
			return new ParseResult.Fail(
				getTokenName() + Constants.COLON + value + " has too many pipe separated item.");
		}

		switch (subToken)
		{
			case LOCK:
				context.getObjectContext().addToList(obj, ListKey.STAT_LOCKS, new StatLock(stat, f));
				break;

			case UNLOCK:
				context.getObjectContext().addToList(obj, ListKey.UNLOCKED_STATS, stat);
				break;

			case NONSTAT:
				context.getObjectContext().addToList(obj, ListKey.NONSTAT_STATS, stat);
				break;

			case STAT:
				context.getObjectContext().addToList(obj, ListKey.NONSTAT_TO_STAT_STATS, stat);
				break;

			case MINVALUE:
				context.getObjectContext().addToList(obj, ListKey.STAT_MINVALUE, new StatLock(stat, f));
				break;

			case MAXVALUE:
				context.getObjectContext().addToList(obj, ListKey.STAT_MAXVALUE, new StatLock(stat, f));
				break;
			default:
				//Case not caught, should this cause an error?
				break;
		}

		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<StatLock> lockChanges = context.getObjectContext().getListChanges(obj, ListKey.STAT_LOCKS);
		Changes<CDOMSingleRef<PCStat>> ulchanges =
				context.getObjectContext().getListChanges(obj, ListKey.UNLOCKED_STATS);
		Changes<CDOMSingleRef<PCStat>> nonStatChanges =
				context.getObjectContext().getListChanges(obj, ListKey.NONSTAT_STATS);
		Changes<CDOMSingleRef<PCStat>> nonStatToStatChanges =
				context.getObjectContext().getListChanges(obj, ListKey.NONSTAT_TO_STAT_STATS);
		Changes<StatLock> minValueChanges = context.getObjectContext().getListChanges(obj, ListKey.STAT_MINVALUE);
		Changes<StatLock> maxValueChanges = context.getObjectContext().getListChanges(obj, ListKey.STAT_MAXVALUE);
		TreeSet<String> set = new TreeSet<>();
		if (lockChanges != null && !lockChanges.isEmpty())
		{
			if (lockChanges.includesGlobalClear())
			{
				context.addWriteMessage("DEFINE:LOCK does not support .CLEAR");
				return null;
			}
			if (lockChanges.hasAddedItems())
			{
				for (StatLock sl : lockChanges.getAdded())
				{
					set.add("LOCK|" + sl.getLSTformat() + Constants.PIPE + sl.getLockValue());
				}
			}
		}
		if (ulchanges != null && !ulchanges.isEmpty())
		{
			if (ulchanges.includesGlobalClear())
			{
				context.addWriteMessage("DEFINE:UNLOCK " + "does not support .CLEAR");
				return null;
			}
			if (ulchanges.hasAddedItems())
			{
				for (CDOMSingleRef<PCStat> st : ulchanges.getAdded())
				{
					set.add("UNLOCK|" + st.getLSTformat(false));
				}
			}
		}
		if (nonStatChanges != null && !nonStatChanges.isEmpty())
		{
			if (nonStatChanges.hasAddedItems())
			{
				for (CDOMSingleRef<PCStat> st : nonStatChanges.getAdded())
				{
					set.add("NONSTAT|" + st.getLSTformat(false));
				}
			}
		}
		if (nonStatToStatChanges != null && !nonStatToStatChanges.isEmpty())
		{
			if (nonStatToStatChanges.hasAddedItems())
			{
				for (CDOMSingleRef<PCStat> st : nonStatToStatChanges.getAdded())
				{
					set.add("STAT|" + st.getLSTformat(false));
				}
			}
		}
		if (minValueChanges != null && !minValueChanges.isEmpty())
		{
			if (minValueChanges.hasAddedItems())
			{
				for (StatLock sl : minValueChanges.getAdded())
				{
					set.add("MINVALUE|" + sl.getLSTformat() + Constants.PIPE + sl.getLockValue());
				}
			}
		}
		if (maxValueChanges != null && !maxValueChanges.isEmpty())
		{
			if (maxValueChanges.hasAddedItems())
			{
				for (StatLock sl : maxValueChanges.getAdded())
				{
					set.add("MAXVALUE|" + sl.getLSTformat() + Constants.PIPE + sl.getLockValue());
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[0]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
