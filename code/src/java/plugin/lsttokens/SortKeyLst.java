/**
 * SortKeyLst.java Copyright 2010 (C) James Dempsey This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * Created on 28/05/2010 10:02:14 PM $Id$
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.SortKeyRequired;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractStringToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.PostValidationToken;
import pcgen.util.Logging;

/**
 * The Class <code>SortKeyLst</code> implements the global SORTKEY tag, which
 * allows items to be sorted in a custom manner. <br/>
 * Last Editor: $Author$ Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
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

	@Override
	public boolean process(LoadContext context,
		Collection<? extends CDOMObject> c)
	{
		Map<String, CDOMObject> map = new TreeMap<String, CDOMObject>();
		CDOMObject sample = c.iterator().next();

		Class<? extends CDOMObject> cl = sample.getClass();
		//This Interface tag is placed on classes where SORTKEY is required
		boolean sortKeyRequired = sample instanceof SortKeyRequired;
		for (CDOMObject obj : c)
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
						+ "in the next version of PCGen.  "
						+ "Use without a SORTKEY is deprecated", context);
				}
			}
			else
			{
				CDOMObject prev = map.put(sortkey, obj);
				/*
				 * This is a universal check, not just SortKeyRequired - if
				 * we're going to use a SortKey it really should work
				 */
				if (prev != null)
				{
					Logging.log(Logging.LST_WARNING, obj.getClass()
						.getSimpleName()
						+ " "
						+ obj.getKeyName()
						+ " and "
						+ prev.getKeyName()
						+ " should not have the same SORTKEY: " + sortkey);
				}
			}
		}
		if (!sortKeyRequired)
		{
			//Break out now if these aren't SortKeyRequired objects
			return true;
		}

		/*
		 * Per the transition rules, the sort key must match the existing order
		 * in the files
		 */
		List<CDOMObject> sortKeySort = new ArrayList<CDOMObject>(map.values());
		List<? extends CDOMObject> baseSort =
				context.getReferenceContext().getOrderSortedCDOMObjects(cl);
		if (!baseSort.equals(sortKeySort))
		{
			Logging.log(
				Logging.LST_ERROR,
				"For " + sample.getClass().getSimpleName()
					+ ", the file order was: "
					+ StringUtil.join(baseSort, ", ")
					+ " while the order based on SORTKEY was: "
					+ StringUtil.join(sortKeySort, ", ")
					+ ".  These lists must match.");
			return false;
		}
		return true;
	}

	@Override
	public Class<CDOMObject> getValidationTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public int getPriority()
	{
		return 11;
	}
}
