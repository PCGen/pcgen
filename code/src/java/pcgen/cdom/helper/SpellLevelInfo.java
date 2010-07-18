/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

public class SpellLevelInfo
{

	private final PrimitiveChoiceFilter<PCClass> filter;
	private final int minimumLevel;
	private final Formula maximumLevel;

	public SpellLevelInfo(PrimitiveChoiceFilter<PCClass> classFilter,
			int minLevel, Formula maxLevel)
	{
		filter = classFilter;
		minimumLevel = minLevel;
		maximumLevel = maxLevel;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(filter.getLSTformat());
		sb.append(Constants.PIPE);
		sb.append(minimumLevel);
		sb.append(Constants.PIPE);
		sb.append(maximumLevel);
		return sb.toString();
	}

	public Collection<SpellLevel> getLevels(PlayerCharacter pc)
	{
		List<SpellLevel> list = new ArrayList<SpellLevel>();
		for (PCClass cl : pc.getClassList())
		{
			if (filter.allow(pc, cl))
			{
				int max = maximumLevel.resolve(pc, cl.getQualifiedKey())
						.intValue();
				for (int i = minimumLevel; i <= max; ++i)
				{
					list.add(new SpellLevel(cl, i));
				}
			}
		}
		return list;
	}

}
