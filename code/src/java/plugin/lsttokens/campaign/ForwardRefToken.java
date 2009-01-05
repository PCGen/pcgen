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
package plugin.lsttokens.campaign;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.Qualifier;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Campaign;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

public class ForwardRefToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "FORWARDREF";
	}

	public boolean parse(LoadContext context, Campaign obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		if (value.indexOf("|") == -1)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " requires at least two arguments, "
					+ "ReferenceType and Key: " + value);
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		String firstToken = st.nextToken();
		ReferenceManufacturer<? extends CDOMObject, ?> rm = context
				.getManufacturer(firstToken);
		if (rm == null)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " unable to generate manufacturer for type: " + value);
			return false;
		}

		while (st.hasMoreTokens())
		{
			CDOMSingleRef<? extends CDOMObject> ref = rm.getReference(st
					.nextToken());
			context.obj.addToList(obj, ListKey.FORWARDREF, new Qualifier(rm
					.getReferenceClass(), ref));
		}

		return true;
	}

	public String[] unparse(LoadContext context, Campaign obj)
	{
		Changes<Qualifier> changes = context.getObjectContext().getListChanges(
				obj, ListKey.FORWARDREF);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Collection<Qualifier> quals = changes.getAdded();
		HashMapToList<String, CDOMSingleRef<?>> map = new HashMapToList<String, CDOMSingleRef<?>>();
		for (Qualifier qual : quals)
		{
			Class<? extends CDOMObject> cl = qual.getQualifiedClass();
			String s = StringPClassUtil.getStringFor(cl);
			CDOMSingleRef<?> ref = qual.getQualifiedReference();
			String key = s;
			if (ref instanceof CategorizedCDOMReference)
			{
				Category<?> cat = ((CategorizedCDOMReference<?>) ref)
						.getCDOMCategory();
				key += '=' + cat.toString();
			}
			map.addToListFor(key, ref);
		}
		Set<CDOMSingleRef<?>> set = new TreeSet<CDOMSingleRef<?>>(
				ReferenceUtilities.REFERENCE_SORTER);
		Set<String> returnSet = new TreeSet<String>();
		for (String key : map.getKeySet())
		{
			set.clear();
			set.addAll(map.getListFor(key));
			StringBuilder sb = new StringBuilder();
			sb.append(key).append(Constants.PIPE).append(
					ReferenceUtilities.joinLstFormat(set, Constants.PIPE));
			returnSet.add(sb.toString());
		}
		return returnSet.toArray(new String[returnSet.size()]);
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}
