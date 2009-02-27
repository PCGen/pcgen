/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.equipment;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.MapKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ACCHECK token
 */
public class QualityToken extends AbstractToken implements
		CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "QUALITY";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.log(Logging.LST_ERROR, getTokenName() + " expecting '|', format is: "
					+ "QualityType|Quality value was: " + value);
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.log(Logging.LST_ERROR, getTokenName() + " expecting only one '|', "
					+ "format is: QualityType|Quality value was: " + value);
			return false;
		}
		String key = value.substring(0, pipeLoc);
		if (key.length() == 0)
		{
			Logging.log(Logging.LST_ERROR, getTokenName() + " expecting non-empty type, "
					+ "format is: QualityType|Quality value was: " + value);
			return false;
		}
		String val = value.substring(pipeLoc + 1);
		if (val.length() == 0)
		{
			Logging.log(Logging.LST_ERROR, getTokenName() + " expecting non-empty value, "
					+ "format is: QualityType|Quality value was: " + value);
			return false;
		}
		context.getObjectContext().put(eq, MapKey.QUALITY, key, val);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		MapChanges<String, String> changes = context.getObjectContext()
				.getMapChanges(eq, MapKey.QUALITY);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		Map<String, String> added = changes.getAdded();
		for (Map.Entry<String, String> me : added.entrySet())
		{
			set.add(new StringBuilder().append(me.getKey()).append(
					Constants.PIPE).append(me.getValue()).toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
