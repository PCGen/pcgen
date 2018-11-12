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
 * A PropertyProcessor is capable of evaluating read and write methods and determining if
 * they meet a specific behavior that would have those methods recognized as a property of
 * an object that implements one or both of those interfaces.
 * 
 * These objects effectively perform analysis similar to that which might be performed in
 * evaluating if an object is a JavaBean, although PropertyProcessor objects may allow
 * significantly more flexibility (and an object need not have both a getter and setter
 * for a PropertyProcessor to evaluate or work with that object).
 * 
 * Note: This interface assumes that a write method (e.g. setX) will only claim one read
 * method. This may or may not be true, and in general, introspection here is challenging,
 * in that this can't be very intelligent about what it claims. On the other hand, this
 * current design is stateless, which is a help in reducing object count.
 */
public interface PropertyProcessor
{
	/**
	 * Returns true if the given Method is a method processed by this PropertyProcessor.
	 * This method should be on a WRITEABLE interface, meaning it is a "set", "put",
	 * "add", etc. that is intended to write to the object.
	 * 
	 * Note: This method must perform sufficient actions such that claimMethod will not
	 * reject the "writeable" method provided here.
	 * 
	 * @param writeMethod
	 *            The Method to be evaluated to determine if it is processed by this
	 *            PropertyProcessor.
	 * @return true if the given Method is a method processed by this PropertyProcessor;
	 *         false otherwise
	 */
	public boolean isProcessedWriteMethod(Method writeMethod);

	/**
	 * Returns the property name given a write method name.
	 * 
	 * @param writeMethodName
	 *            The write method name to be converted to the property name
	 * @return The property name as derived from the given write method name
	 */
	public String getPropertyNameFromWrite(String writeMethodName);

	/**
	 * Returns the property name given a read method name.
	 * 
	 * @param readMethodName
	 *            The read method name to be converted to the property name
	 * @return The property name as derived from the given read method name
	 */
	public String getPropertyNameFromRead(String readMethodName);

	/**
	 * Returns the Method "claimed" as being the "read" Method associated with the given
	 * "write" Method. This method will be from the given array of possible "read"
	 * Methods.
	 * 
	 * In effect, this determines the matching pair of "read"/"write" Methods for a given
	 * property by using the given "write" Method to establish which property is being
	 * evaluated.
	 * 
	 * Note: This method may reserve the right to throw exceptions if some behavior does
	 * not properly match between the "read"/"write" Methods for a given property.
	 * Specifically, if the read and write return different Classes, then the
	 * PropertyProcessor can refuse to proceed.
	 * 
	 * @param writeMethod
	 *            The "write" Method used to establish the property being evaluated (and
	 *            to validate the "read" method is compatible)
	 * @param possibleReadMethods
	 *            The possible "read" Methods to be checked to determine which one is the
	 *            Method associated with the given "write" Method
	 * @return A Method from the array of possible "read" Methods that is "claimed" as
	 *         being the "read" Method associated with the given "write" Method
	 */
	public Method claimMethod(Method writeMethod, List<Method> possibleReadMethods);

	/**
	 * Returns a ReadableHandler for the given method name and arguments.
	 * 
	 * @param methodName
	 *            The Method name for which a ReadableHandler should be returned
	 * @param args
	 *            The arguments that may be needed to construct the appropriate
	 *            ReadableHandler
	 * @param propertyClass
	 *            The Class of objects the property will contain
	 * @return A ReadableHandler for the given method name and arguments
	 */
	public ReadableHandler getInvocationHandler(String methodName, Object[] args,
		Class<?> propertyClass);

	/**
	 * Returns a Method from the given Array of Method objects which has the given Method
	 * name. An IllegalArgumentException is returned if none of the Methods in the given
	 * Array has the given name.
	 * 
	 * @param methodName
	 *            The Method name to be checked against the given possible Methods
	 * @param possibleMethods
	 *            The Array of possible Methods to be checked
	 * @return A Method from the given Array of Method objects which has the given Method
	 *         name
	 * @throws IllegalArgumentException
	 *             if none of the Methods in the given Array has the given name
	 */
	public static Method retrieveMethod(String methodName, List<Method> possibleMethods)
	{
		for (Method m : possibleMethods)
		{
			if (m.getName().equalsIgnoreCase(methodName))
			{
				return m;
			}
		}
		throw new IllegalArgumentException(
			"Expected Method: " + methodName + " to exist");
	}

}
