/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.rules.types.FormatManager;

/**
 * A BasicIndirect is a simple container for storing an object that is accessed
 * indirectly (meaning the resolvesTo() method of the BasicIndirect will be
 * called at Runtime).
 * 
 * This is generally used for wrapping "known" objects so that all objects are
 * dealt with indirectly. (In some cases, items cannot be known at load time,
 * and thus must be referred to indirectly)
 * 
 * @param <T>
 *            The type of object that the BasicIndirect contains
 */
public class BasicIndirect<T> implements Indirect<T>
{

	/**
	 * Contains the FormatManager that can handle the object contained in this
	 * BasicIndirect
	 */
	private final FormatManager<T> manager;

	/**
	 * Contains the single object contained in this BasicIndirect
	 */
	private final T object;

	/**
	 * Constructs a new BasicIndirect containing the single object provided.
	 * 
	 * @param mgr
	 *            The FormatManager usable to manage the given object
	 * @param obj
	 *            The single object that this BasicIndirect will contain
	 * @throws IllegalArgumentException
	 *             if either argument is null or if the given object is not
	 *             compatible with the given FormatManager
	 */
	public BasicIndirect(FormatManager<T> mgr, T obj)
	{
		if (mgr == null)
		{
			throw new IllegalArgumentException("Manager may not be null");
		}
		if (obj == null)
		{
			throw new IllegalArgumentException("Object may not be null");
		}
		//Validate the generics ;)
		if (!mgr.getType().isAssignableFrom(obj.getClass()))
		{
			throw new IllegalArgumentException("Object of class "
				+ obj.getClass()
				+ " is not compatible with provided Format Manager: "
				+ mgr.getType());
		}
		object = obj;
		manager = mgr;
	}

	/**
	 * @see pcgen.base.util.Indirect#resolvesTo()
	 */
	@Override
	public T resolvesTo()
	{
		return object;
	}

	/**
	 * @see pcgen.base.util.Indirect#getUnconverted()
	 */
	@Override
	public String getUnconverted()
	{
		return manager.unconvert(object);
	}

	@Override
	public String toString()
	{
		return String.valueOf(object);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof BasicIndirect)
		{
			BasicIndirect other = (BasicIndirect) obj;
			if (object == null)
			{
				return other.object == null;
			}
			return object.equals(other.object);
		}
		return false;
	}
}
