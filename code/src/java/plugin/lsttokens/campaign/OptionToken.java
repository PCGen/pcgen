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

import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.MapKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with OPTION Token
 */
public class OptionToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "OPTION";
	}

	public boolean parse(LoadContext context, Campaign pcc, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		final int equalsPos = value.indexOf(Constants.EQUALS);

		if (equalsPos < 0)
		{
			Logging.log(Logging.LST_ERROR, "Invalid option line in campaign "
					+ pcc.getKeyName() + " : " + value);
			return false;
		}
		String optName = value.substring(0, equalsPos);

		if (optName.regionMatches(true, 0, "pcgen.options.", 0, 14))
		{
			optName = optName.substring(14);
		}

		final String optValue = value.substring(equalsPos + 1);
		context.obj.put(pcc, MapKey.PROPERTY, optName, optValue);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign pcc)
	{
		MapChanges<String, String> changes = context.getObjectContext()
				.getMapChanges(pcc, MapKey.PROPERTY);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (String property : changes.getAdded().keySet())
		{
			String value = changes.getAdded().get(property);
			set.add(new StringBuilder().append(property).append(
					Constants.EQUALS).append(value).toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}
