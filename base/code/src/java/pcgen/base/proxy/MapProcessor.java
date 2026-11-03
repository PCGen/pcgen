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
import java.util.List;

/**
 * A MapProcessor is a PropertyProcessor that handles Map-based properties. These are
 * properties that use "put" or "putX" to set items in the map and "get" or "getX" to
 * retrieve items.
 * 
 * Note that it is expected that the put* methods take two parameters (key and value) and
 * get* methods take a single parameter, which is the key to the map.
 */
public class MapProcessor implements PropertyProcessor
{

	@Override
	public boolean isProcessedWriteMethod(Method method)
	{
		return method.getName().regionMatches(true, 0, "put", 0, 3)
			&& (method.getParameterTypes().length == 2)
			&& (method.getReturnType() == void.class);

	}

	@Override
	public String getPropertyNameFromWrite(String methodName)
	{
		return "put".equalsIgnoreCase(methodName) ? "" : methodName.substring(3);
	}

	@Override
	public String getPropertyNameFromRead(String methodName)
	{
		return "get".equalsIgnoreCase(methodName) ? "" : methodName.substring(3);
	}

	@Override
	public Method claimMethod(Method putMethod, List<Method> possibleReadMethods)
	{
		String name = putMethod.getName();
		String propertyName = getPropertyNameFromWrite(name);
		Method getMethod = PropertyProcessor.retrieveMethod("get" + propertyName,
			possibleReadMethods);
		Class<?>[] getParams = getMethod.getParameterTypes();
		if (getParams.length != 1)
		{
			throw new IllegalArgumentException(
				"Expected " + getMethod.getName() + " to have one argument");
		}
		Class<?>[] setParams = putMethod.getParameterTypes();
		Class<?> getParameter = getParams[0];
		if (!getParameter.equals(setParams[0]))
		{
			throw new IllegalArgumentException(
				putMethod.getName() + " set lookup format: "
					+ setParams[0].getCanonicalName() + " but " + getMethod.getName()
					+ " fetched via a " + getParameter.getCanonicalName());
		}
		if (!setParams[1].equals(getMethod.getReturnType()))
		{
			throw new IllegalArgumentException(putMethod.getName() + " added format: "
				+ setParams[1].getCanonicalName() + " but " + getMethod.getName()
				+ " returned a " + getMethod.getReturnType().getCanonicalName());
		}
		return getMethod;
	}

	@Override
	public ReadableHandler getInvocationHandler(String readMethodName, Object[] args,
		Class<?> propertyClass)
	{
		return new ReadMapProperty(getPropertyNameFromRead(readMethodName), args[0]);
	}

}
