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

import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.Quality;
import pcgen.core.Equipment;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
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
		context.getObjectContext().addToList(eq, ListKey.QUALITY,
				new Quality(key, val));
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Changes<Quality> changes = context.getObjectContext().getListChanges(
				eq, ListKey.QUALITY);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (Quality q : changes.getAdded())
		{
			set.add(new StringBuilder().append(q.getName()).append(
					Constants.PIPE).append(q.getValue()).toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
