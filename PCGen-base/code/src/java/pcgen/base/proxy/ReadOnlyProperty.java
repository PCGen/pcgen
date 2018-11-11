/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Implements a read only property on a proxied object.
 */
public class ReadOnlyProperty implements ReadableHandler
{
	/**
	 * The value of the Property to be processed by this ReadOnlyProperty.
	 */
	private final Object value;

	/**
	 * Constructs a new ReadOnlyProperty.
	 * 
	 * Note: The method to read the read only is actually called in this implementation.
	 * 
	 * @param underlying
	 *            The underlying object on which the read only property exists
	 * @param methodName
	 *            The method name used to retrieve the read only property
	 */
	public ReadOnlyProperty(Object underlying, String methodName)
	{
		try
		{
			Method method = underlying.getClass().getMethod(methodName);
			value = method.invoke(underlying);
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		return null;
	}

	@Override
	public Object getResult()
	{
		return value;
	}

}
