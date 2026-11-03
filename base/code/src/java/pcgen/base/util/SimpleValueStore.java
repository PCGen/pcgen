/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

/**
 * A simple implementation of the ValueStore interface.
 */
public class SimpleValueStore implements ValueStore
{
	/**
	 * The underlying Map for this SimpleValueStore that stores the default values by
	 * their identifier.
	 */
	private final CaseInsensitiveMap<Object> map = new CaseInsensitiveMap<>();

	/**
	 * Adds a new value to this ValueStore for the given Identifier.
	 * 
	 * @param identifier
	 *            The Identifier for which the given value should be added
	 * @param value
	 *            The value for the given Identifier
	 * @return The previous value for the given identifier, if any
	 */
	public Object addValueFor(String identifier, Object value)
	{
		return map.put(identifier, value);
	}

	@Override
	public Object getValueFor(String identifier)
	{
		Object value = map.get(identifier);
		if (value == null)
		{
			throw new IllegalArgumentException(
				"InitializeFrom Method did not have a value for " + identifier);
		}
		return value;
	}
}
