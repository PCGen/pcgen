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
package pcgen.base.proxy;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A ReadMapProperty is a ReadableHandler that handles Map-based Properties.
 */
public class ReadMapProperty implements ReadableHandler
{
	/**
	 * The set method name for the Property to be processed by this ReadMapProperty.
	 */
	private final String putMethodName;

	/**
	 * The key for the portion of the Property to be processed by this ReadMapProperty.
	 */
	private final Object key;

	/**
	 * The resulting value of the Property after this ReadMapProperty is appropriately
	 * invoked.
	 */
	private Object value;

	/**
	 * Constructs a new ReadMapProperty for the given Property name and map key.
	 * 
	 * @param propertyName
	 *            The Property name that this ReadMapProperty will process
	 * @param key
	 *            The key for the portion of the Property to be processed by this
	 *            ReadMapProperty
	 */
	public ReadMapProperty(String propertyName, Object key)
	{
		this.putMethodName = "put" + Objects.requireNonNull(propertyName);
		this.key = Objects.requireNonNull(key);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if (!void.class.equals(method.getReturnType()))
		{
			throw new IllegalArgumentException("Expected invoked set method ("
					+ putMethodName + ") to have a void return type");
		}
		if (method.getName().equalsIgnoreCase(putMethodName) && key.equals(args[0]))
		{
			value = args[1];
		}
		return null;
	}

	@Override
	public Object getResult()
	{
		return value;
	}
}
