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
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.core.Language;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.StringPClassUtil;

public class AllowDupesToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "ALLOWDUPES";
	}

	public boolean parse(LoadContext context, Campaign obj, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		else if ("SPELL".equals(value))
		{
			context.getObjectContext().addToList(obj, ListKey.DUPES_ALLOWED,
					Spell.class);
			return true;
		}
		else if ("LANGUAGE".equals(value))
		{
			context.getObjectContext().addToList(obj, ListKey.DUPES_ALLOWED,
					Language.class);
			return true;
		}
		else
		{
			return false;
		}
	}

	public String[] unparse(LoadContext context, Campaign obj)
	{
		Changes<Class<?>> changes = context.getObjectContext().getListChanges(
				obj, ListKey.DUPES_ALLOWED);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Collection<Class<?>> added = changes.getAdded();
		Set<String> returnSet = new TreeSet<String>();
		for (Class<?> cl : added)
		{
			returnSet.add(StringPClassUtil.getStringFor(cl));
		}
		return returnSet.toArray(new String[returnSet.size()]);
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}
