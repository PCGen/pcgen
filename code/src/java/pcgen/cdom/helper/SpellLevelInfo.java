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
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.converter.AddFilterConverter;
import pcgen.cdom.converter.DereferencingConverter;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

public class SpellLevelInfo implements PrimitiveFilter<PCClass>
{

	private final PrimitiveCollection<PCClass> filter;
	private final Formula minimumLevel;
	private final Formula maximumLevel;

	public SpellLevelInfo(PrimitiveCollection<PCClass> classFilter, Formula minLevel, Formula maxLevel)
	{
		filter = classFilter;
		minimumLevel = minLevel;
		maximumLevel = maxLevel;
	}

	@Override
	public String toString()
	{
        return filter.getLSTformat(false)
                + Constants.PIPE
                + minimumLevel
                + Constants.PIPE
                + maximumLevel;
	}

	public Collection<SpellLevel> getLevels(PlayerCharacter pc)
	{
		List<SpellLevel> list = new ArrayList<>();
		Converter<PCClass, PCClass> conv = new AddFilterConverter<>(new DereferencingConverter<>(pc), this);
		for (PCClass cl : filter.getCollection(pc, conv))
		{
			int min = minimumLevel.resolve(pc, cl.getQualifiedKey()).intValue();
			int max = maximumLevel.resolve(pc, cl.getQualifiedKey()).intValue();
			if (min > max)
			{
				Logging.errorPrint("Resolved Minimum: " + min + " (from " + minimumLevel
					+ ") was greater than resolved Maximum: " + max + " (from " + maximumLevel + ")");
			}
			for (int i = min; i <= max; ++i)
			{
				list.add(new SpellLevel(cl, i));
			}
		}
		return list;
	}

	@Override
	public boolean allow(PlayerCharacter pc, PCClass cl)
	{
		return pc.getClassKeyed(cl.getKeyName()) != null;
	}

}
