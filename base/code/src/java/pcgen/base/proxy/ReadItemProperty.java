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
 * A ReadItemProperty is a ReadableHandler that handles Item-based Properties.
 */
public class ReadItemProperty implements ReadableHandler
{
	/**
	 * The set method name for the Property to be processed by this ReadItemProperty.
	 */
	private final String setMethodName;

	/**
	 * The resulting value of the Property after this ReadItemProperty is appropriately
	 * invoked.
	 */
	private Object value;

	/**
	 * Constructs a new ReadItemProperty for the given Property name.
	 * 
	 * @param propertyName
	 *            The Property name that this ReadItemProperty will process
	 */
	public ReadItemProperty(String propertyName)
	{
		this.setMethodName = "set" + Objects.requireNonNull(propertyName);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if (!void.class.equals(method.getReturnType()))
		{
			throw new IllegalArgumentException("Expected invoked set method ("
					+ setMethodName + ") to have a void return type");
		}
		if (method.getName().equalsIgnoreCase(setMethodName))
		{
			value = args[0];
		}
		return null;
	}

	@Override
	public Object getResult()
	{
		return value;
	}
}
