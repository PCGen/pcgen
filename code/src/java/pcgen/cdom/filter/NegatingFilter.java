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

/**
 * A NegatingFilter is a PrimitiveChoiceFilter that negates the result of
 * another PrimitiveChoiceFilter.
 * 
 * @param <T>
 *            The type of object filtered by this NegatingFilter
 */
public class NegatingFilter<T> implements PrimitiveChoiceFilter<T>
{

	/**
	 * The underlying PrimitiveChoiceFilter that this NegatingFilter will use to
	 * determine if objects are allowed (results from this underlying
	 * PrimitiveChoiceFilter are negated)
	 */
	private final PrimitiveChoiceFilter<T> filter;

	/**
	 * Constructs a new NegatingFilter with the given underlying
	 * PrimitiveChoiceFilter. This NegatingFilter will negate the results (from
	 * the allow method) of the given PrimitiveChoiceFilter.
	 * 
	 * @param f
	 *            The underlying PrimitiveChoiceFilter that this NegatingFilter
	 *            uses
	 */
	public NegatingFilter(PrimitiveChoiceFilter<T> f)
	{
		if (f == null)
		{
			throw new IllegalArgumentException(
					"PrimitiveChoiceFilter for NegatingFilter cannot be null");
		}
		filter = f;
	}

	/**
	 * Return true if the given PlayerCharacter is allowed to select the given
	 * object
	 * 
	 * @param pc
	 *            The PlayerCharacter to be tested to determine if the given
	 *            object is allowed to be selected by this PlayerCharacter
	 * @param obj
	 *            The object to be tested to determine if the given
	 *            PlayerCharacter is allowed to select this object
	 * @return true if the given PlayerCharacter is allowed to select the given
	 *         object; false otherwise
	 */
	public boolean allow(PlayerCharacter pc, T obj)
	{
		return !filter.allow(pc, obj);
	}

	/**
	 * Returns the Class object representing the Class that this NegatingFilter
	 * evaluates.
	 * 
	 * @return Class object representing the Class that this NegatingFilter
	 *         evaluates
	 */
	public Class<T> getReferenceClass()
	{
		return filter.getReferenceClass();
	}

	/**
	 * Returns a representation of this NegatingFilter, suitable for storing in
	 * an LST file.
	 * 
	 * @return A representation of this NegatingFilter, suitable for storing in
	 *         an LST file.
	 */
	public String getLSTformat()
	{
		return "!" + filter.getLSTformat();
	}

	/**
	 * Returns the consistent-with-equals hashCode for this NegatingFilter
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return -filter.hashCode();
	}

	/**
	 * Returns true if this NegatingFilter is equal to the given Object.
	 * Equality is defined as being another NegatingFilter object with equal
	 * underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof NegatingFilter)
		{
			NegatingFilter<?> other = (NegatingFilter<?>) obj;
			return filter.equals(other.filter);
		}
		return false;
	}
}
