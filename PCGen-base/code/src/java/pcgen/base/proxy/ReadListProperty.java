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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A ReadItemProperty is a ReadableHandler that handles List-based Properties.
 */
public class ReadListProperty implements ReadableHandler
{
	/**
	 * The add method name for the Property to be processed by this ReadListProperty.
	 */
	private final String addMethodName;

	/**
	 * The resulting value of the Property after this ReadListProperty is appropriately
	 * invoked.
	 */
	private final List<Object> value = new ArrayList<>();

	private final Class<?> arrayClass;

	/**
	 * Constructs a new ReadListProperty for the given Property name.
	 * 
	 * @param propertyName
	 *            The Property name that this ReadListProperty will process
	 * @param arrayClass
	 *            The Class of objects this ReadListProperty will process
	 */
	public ReadListProperty(String propertyName, Class<?> arrayClass)
	{
		this.addMethodName = "add" + Objects.requireNonNull(propertyName);
		this.arrayClass = Objects.requireNonNull(arrayClass);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if (!void.class.equals(method.getReturnType()))
		{
			throw new IllegalArgumentException("Expected invoked set method ("
					+ addMethodName + ") to have a void return type");
		}
		if (method.getName().equalsIgnoreCase(addMethodName))
		{
			value.add(args[0]);
		}
		return null;
	}

	@Override
	public Object getResult()
	{
		Object[] array =
				(Object[]) Array.newInstance(arrayClass.getComponentType(), value.size());
		return value.toArray(array);
	}
}
