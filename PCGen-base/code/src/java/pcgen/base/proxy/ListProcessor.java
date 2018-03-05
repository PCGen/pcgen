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

/**
 * A ListProcessor is a PropertyProcessor that handles List-based properties. These are
 * properties that use "addX" to add items to the list and "getXArray" to retrieve the
 * list.
 */
public class ListProcessor implements PropertyProcessor
{

	@Override
	public boolean isProcessedMethod(Method method)
	{
		return method.getName().regionMatches(true, 0, "add", 0, 3)
			&& (method.getParameterTypes().length == 1)
			&& (method.getReturnType() == void.class);
	}

	@Override
	public String getPropertyName(String methodName)
	{
		return methodName.substring(3);
	}

	@Override
	public Method claimMethod(Method addMethod, Method[] possibleReadMethods)
	{
		String propertyName = getPropertyName(addMethod.getName());
		Method getMethod = PropertyProcessor
			.retrieveMethod("get" + propertyName + "Array", possibleReadMethods);
		if (getMethod.getParameterTypes().length != 0)
		{
			throw new IllegalArgumentException("Did not expect GET Method: "
				+ getMethod.getName() + " to have arguments");
		}
		Class<?>[] setParams = addMethod.getParameterTypes();
		Class<?> returnType = getMethod.getReturnType();
		if (!returnType.isArray())
		{
			throw new IllegalArgumentException("ADD Method: " + addMethod.getName()
				+ " set format: " + setParams[0].getCanonicalName() + " but GET Method: "
				+ getMethod.getName() + " did not return an array: "
				+ getMethod.getReturnType().getCanonicalName());
		}
		if (!setParams[0].equals(returnType.getComponentType()))
		{
			throw new IllegalArgumentException("ADD Method: " + addMethod.getName()
				+ " set format: " + setParams[0].getCanonicalName() + " but GET Method: "
				+ getMethod.getName() + " returned a "
				+ getMethod.getReturnType().getCanonicalName());
		}
		return getMethod;
	}

	@Override
	public ReadableHandler getInvocationHandler(String methodName, Object[] args)
	{
		return new ReadListProperty(methodName);
	}

}
