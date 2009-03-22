/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.filter;

import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.core.PlayerCharacter;

public class NegatingFilter<T> implements PrimitiveChoiceFilter<T>
{

	private final PrimitiveChoiceFilter<T> filter;

	public NegatingFilter(PrimitiveChoiceFilter<T> f)
	{
		if (f == null)
		{
			throw new IllegalArgumentException();
		}
		filter = f;
	}

	public boolean allow(PlayerCharacter pc, T obj)
	{
		return !filter.allow(pc, obj);
	}

	public Class<T> getReferenceClass()
	{
		return filter.getReferenceClass();
	}

	public String getLSTformat()
	{
		return "!" + filter.getLSTformat();
	}

}
