/*
 * Copyright (c) 2017 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * Interface to provide feedback on complex operations that either return a value, or
 * deserve a message that describes whey they failed (without throwing an exception).
 * 
 * @param <T> The format of the object returned by this ComplexResult
 */
public interface ComplexResult<T> extends Supplier<T> 
{
	/**
	 * Returns any messages contained by this ComplexResult.
	 * 
	 * Note: If the ComplexResult returns a non-null value to get() then the behavior
	 * of this method is not controlled by this interface.
	 * 
	 * @return A non-null list of messages contained by this ComplexResult.
	 */
	public Collection<String> getMessages();

	/**
	 * Returns true if this ComplexResult was successful; false otherwise.
	 * 
	 * @return true if this ComplexResult was successful; false otherwise
	 */
	public boolean isSuccessful();
	
	/**
	 * Returns a ComplexResult representing successful processing of an operation with the
	 * given value as the result.
	 * 
	 * @param <T>
	 *            The format of the object contained by the returned ComplexResult
	 * @param result
	 *            The result value contained by this ComplexResult
	 * @return A ComplexResult indicating successful processing and containing the given
	 *         value
	 */
	public static <T> ComplexResult<T> ofSuccess(T result)
	{
		return new ComplexResult<>()
		{
			@Override
			public T get()
			{
				return result;
			}

			@Override
			public Collection<String> getMessages()
			{
				//No messages because we passed
				return Collections.emptyList();
			}

			@Override
			public boolean isSuccessful()
			{
				return true;
			}
		};
	}
}
