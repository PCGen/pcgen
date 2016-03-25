/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

/**
 * A TypedKey is a generic object designed to be used as a key to a type
 * key-value pair.
 *
 * Note that the Generic type has no local effects, it is used solely as a
 * constraint when dealing with objects aware of generics.
 *
 * @param <T>
 *            The type of object managed by another object when the given
 *            TypedKey is provided.
 */
public final class TypedKey<T>
{

	/**
	 * Holds the "Default Value" to be used when there is no value for the given
	 * TypedKey.
	 */
	private final T defaultValue;

	/**
	 * Constructs a new TypedKey with no default value. The null value will be
	 * considered the default.
	 */
	public TypedKey()
	{
		this(null);
	}

	/**
	 * Constructs a new TypedKey with the given value as the "default value" for
	 * this TypedKey.
	 * 
	 * @param value
	 *            The "Default Value" to be used when there is no value for the
	 *            given TypedKey
	 */
	public TypedKey(T value)
	{
		defaultValue = value;
	}

	/**
	 * Cast the given object to the type contained by this TypedKey.
	 * 
	 * @param object
	 *            The object to be cast to the type managed by this TypedKey
	 * @return The given object, cast to the type managed by this TypedKey
	 */
	@SuppressWarnings("unchecked")
	public T cast(Object object)
	{
		return (T) object;
	}

	/**
	 * Returns the "Default Value" to be used when there is no value for the
	 * given TypedKey.
	 * 
	 * @return The "Default Value" to be used when there is no value for the
	 *         given TypedKey
	 */
	public T getDefaultValue()
	{
		return defaultValue;
	}
}
