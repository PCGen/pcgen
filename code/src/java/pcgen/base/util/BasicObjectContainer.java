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

import java.util.Collection;
import java.util.Collections;

import pcgen.rules.types.FormatManager;

/**
 * A BasicObjectContainer is a simple container for storing one object that is
 * accessed indirectly from an ObjectContainer (meaning the
 * getContainedObjects() method of the BasicObjectContainer will be called at
 * Runtime).
 * 
 * This is generally used for wrapping "known" objects so that all objects are
 * dealt with indirectly. (In some cases, items cannot be known at load time,
 * and thus must be referred to indirectly)
 * 
 * @param <T>
 *            The type of object that the BasicObjectContainer contains
 */
public class BasicObjectContainer<T> implements ObjectContainer<T>
{

	/**
	 * Contains the FormatManager that can handle the object contained in this
	 * BasicObjectContainer
	 */
	private final FormatManager<T> manager;

	/**
	 * Contains the single object contained in this BasicObjectContainer
	 */
	private final T object;

	/**
	 * Constructs a new BasicObjectContainer containing the single object
	 * provided.
	 * 
	 * @param mgr
	 *            The FormatManager usable to manage the given object
	 * @param obj
	 *            The single object that this BasicObjectContainer will contain
	 * @throws IllegalArgumentException
	 *             if either argument is null or if the given object is not
	 *             compatible with the given FormatManager
	 */
	public BasicObjectContainer(FormatManager<T> mgr, T obj)
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
	 * @see pcgen.base.util.ObjectContainer#getLSTformat(boolean)
	 */
	@Override
	public String getLSTformat(boolean useAny)
	{
		return manager.unconvert(object);
	}

	/**
	 * @see pcgen.base.util.ObjectContainer#getReferenceClass()
	 */
	@Override
	public Class<T> getReferenceClass()
	{
		return manager.getType();
	}

	/**
	 * Returns a singleton Collection containing the one item in this
	 * BasicObjectContainer.
	 * 
	 * @see pcgen.base.util.ObjectContainer#getContainedObjects()
	 */
	@Override
	public Collection<? extends T> getContainedObjects()
	{
		return Collections.singleton(object);
	}

	/**
	 * @see pcgen.base.util.ObjectContainer#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T obj)
	{
		return (obj != null) && obj.equals(object);
	}
}
