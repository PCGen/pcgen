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
import java.util.Objects;

/**
 * An ReadOnlyProcessor is a PropertyProcessor that handles Read Only properties. These
 * are properties that use @ReadOnly with only a "getX" to retrieve the object.
 * 
 * Note: These Read Only properties cannot depend on things that are written through other
 * properties.
 */
public class ReadOnlyProcessor implements PropertyProcessor
{

	/**
	 * The object underlying this ReadOnlyProcessor, from which the read only information
	 * will be retrieved.
	 */
	private final Object underlying;

	/**
	 * Constructs a new ReadOnlyProcess with the underlying object.
	 * 
	 * @param underlying
	 *            object underlying this ReadOnlyProcessor, from which the read only
	 *            information will be retrieved
	 */
	public ReadOnlyProcessor(Object underlying)
	{
		this.underlying = Objects.requireNonNull(underlying);
	}

	@Override
	public boolean isProcessedWriteMethod(Method method)
	{
		return false;
	}

	@Override
	public String getPropertyNameFromWrite(String methodName)
	{
		throw new UnsupportedOperationException(
			"ReadOnlyProcessor has no write awareness");
	}

	@Override
	public String getPropertyNameFromRead(String methodName)
	{
		return methodName.substring(3);
	}

	@Override
	public Method claimMethod(Method setMethod, List<Method> possibleReadMethods)
	{
		throw new UnsupportedOperationException(
			"ReadOnlyProcessor has no write awareness to evaluate any method");
	}

	@Override
	public ReadableHandler getInvocationHandler(String methodName, Object[] args,
		Class<?> propertyClass)
	{
		return new ReadOnlyProperty(underlying, methodName);
	}

}
