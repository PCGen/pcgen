/**
 * SortKeyLst.java
 * Copyright 2010 (C) James Dempsey
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
 *
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.SortKeyRequired;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractStringToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.PostValidationToken;
import pcgen.util.Logging;

/**
 * The Class {@code SortKeyLst} implements the global SORTKEY tag, which
 * allows items to be sorted in a custom manner.
 *
 * <br>
 * 
 */
public class SortKeyLst extends AbstractStringToken<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>, PostValidationToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "SORTKEY";
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	protected StringKey stringKey()
	{
		return StringKey.SORT_KEY;
	}

	/**
	 * Enforces that SORTKEY exists on any object which carries the
	 * SortKeyRequired interface.
	 * 
	 * All such objects must have a SORTKEY and in PCGen 6.5/6.6, the file order
	 * must match the SORTKEY order.
	 * 
	 * @see pcgen.rules.persistence.token.PostValidationToken#process(pcgen.rules.context.LoadContext,
	 *      java.util.Collection)
	 */
	@Override
	public boolean process(LoadContext context,
		Collection<? extends CDOMObject> allObjects)
	{
		if (allObjects.isEmpty())
		{
			return true;
		}

		CDOMObject sample = allObjects.iterator().next();
		Class<? extends CDOMObject> cl = sample.getClass();
		//This Interface tag is placed on classes where SORTKEY is required
		boolean sortKeyRequired = sample instanceof SortKeyRequired;

		for (CDOMObject obj : allObjects)
		{
			String sortkey = obj.get(stringKey());
			if (sortkey == null)
			{
				/*
				 * Do not join IFs, we want sortkey == null and not required to
				 * not process the map
				 */
				if (sortKeyRequired)
				{
					//This becomes an error in PCGen 6.7
					Logging.deprecationPrint("Objects of type "
						+ obj.getClass().getName() + " will require a SORTKEY "
						+ "in the next version of PCGen (6.7).  "
						+ "Use without a SORTKEY is deprecated", context);
				}
			}
		}
		/*
		 * This is likely permanent, as certain objects (e.g. Alignment/Stat)
		 * will "always" need a sort unique from the order in the file, and this
		 * is a good nudge to indicate to data writers that the items are sort
		 * order sensitive.
		 */
		if (!sortKeyRequired)
		{
			//Break out now if these aren't SortKeyRequired objects
			return true;
		}

		/*
		 * Per the transition rules, the sort key must match the existing order
		 * in the files (PCGen 6.5/6.6)
		 */
		AbstractReferenceContext refContext = context.getReferenceContext();
		List<? extends CDOMObject> sortKeySort =
				new ArrayList<>(refContext.getSortOrderedList(cl));
		List<? extends CDOMObject> orderSort =
				refContext.getOrderSortedCDOMObjects(cl);
		//This IF is order sensitive ... want to have ArrayList first to use its .equals()
		if (!sortKeySort.equals(orderSort))
		{
			Logging.log(
				Logging.LST_ERROR,
				"For " + sample.getClass().getSimpleName()
					+ ", the file order was: "
					+ StringUtil.join(new ArrayList<CDOMObject>(orderSort), ", ")
					+ " while the order based on SORTKEY was: "
					+ StringUtil.join(sortKeySort, ", ")
					+ ".  These lists must match.");
			return false;
		}
		return true;
	}

	/**
	 * @see pcgen.rules.persistence.token.PostValidationToken#getValidationTokenClass()
	 */
	@Override
	public Class<CDOMObject> getValidationTokenClass()
	{
		return CDOMObject.class;
	}

	/**
	 * @see pcgen.rules.persistence.token.PostValidationToken#getPriority()
	 */
	@Override
	public int getPriority()
	{
		return 11;
	}
}
