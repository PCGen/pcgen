/*
 * Copyright 2009-17 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * An object used to wrap an object to ensure checks are done with identity (==) not
 * equality (.equals()).
 * 
 * @param <T>
 *            The type of object underlying this Identity
 */
public final class Identity<T>
{

	/**
	 * The underlying item for this Identity.
	 */
	private final T underlying;

	/**
	 * Constructs a new Identity with the given underlying item.
	 * 
	 * @param item
	 *            The underlying item for this Identity
	 */
	public Identity(T item)
	{
		underlying = item;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof Identity)
			&& (((Identity<?>) obj).underlying == underlying);
	}

	@Override
	public int hashCode()
	{
		return underlying.hashCode();
	}

	/**
	 * Returns the object underlying this Identity.
	 * 
	 * @return The object underlying this Identity
	 */
	public T getUnderlying()
	{
		return underlying;
	}

	/**
	 * Convert an object to the Identity wrapper for that object that does identity
	 * comparison.
	 * 
	 * @param <T>
	 *            The type of object for which the identity is being returned
	 * @param value
	 *            The value for which the identity is being returned
	 * @return The Identity object for the given parameter
	 */
	public static <T> Identity<T> valueOf(T value)
	{
		return new Identity<>(value);
	}

}
