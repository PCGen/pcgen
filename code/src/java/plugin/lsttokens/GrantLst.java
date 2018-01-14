/*
 * Copyright 2016 (C) Thomas Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.Dynamic;
import pcgen.cdom.inst.DynamicCategory;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

import org.apache.commons.lang3.StringUtils;

public class GrantLst extends AbstractTokenWithSeparator<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>
{

	private static final Class<Dynamic> DYNAMIC_CLASS = Dynamic.class;
	private static final Class<DynamicCategory> DYNAMIC_CATEGORY_CLASS =
			DynamicCategory.class;

	@Override
	public String getTokenName()
	{
		return "GRANT";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail("Cannot use " + getTokenName()
				+ " on an Ungranted object type: "
				+ obj.getClass().getSimpleName(), context);
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		String scope = tok.nextToken();
		if (!tok.hasMoreTokens())
		{
			return new ParseResult.Fail(getTokenName()
				+ " must have identifier(s), "
				+ "Format is: DYNAMICSCOPE|DynamicName: " + value, context);
		}

		ReferenceManufacturer<Dynamic> rm =
				context.getReferenceContext().getManufacturer(DYNAMIC_CLASS,
					DYNAMIC_CATEGORY_CLASS, scope);
		if (rm == null)
		{
			return new ParseResult.Fail(
				"Could not get Reference Manufacturer for Dynamic Scope: "
					+ scope, context);
		}

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<Dynamic> dynamic = rm.getReference(token);
			if (dynamic == null)
			{
				return ParseResult.INTERNAL_ERROR;
			}
			context.getObjectContext().addToList(obj, ListKey.GRANTED, dynamic);
		}

		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<CDOMReference<Dynamic>> changes =
				context.getObjectContext().getListChanges(obj, ListKey.GRANTED);
		HashMapToList<String, String> map = new HashMapToList<>();
		Collection<CDOMReference<Dynamic>> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			for (CDOMReference<Dynamic> ref : added)
			{
				CategorizedCDOMReference<Dynamic> catRef =
						(CategorizedCDOMReference<Dynamic>) ref;
				map.addToListFor(catRef.getLSTCategory(),
					ref.getLSTformat(false));
			}
		}
		if (map.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<>();
		for (String scope : map.getKeySet())
		{
			List<String> scopeList = map.getListFor(scope);
			set.add(scope + Constants.PIPE
				+ StringUtils.join(scopeList, Constants.PIPE));
		}
		return set.toArray(new String[set.size()]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
