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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Interface to provide feedback on complex operations that either return a value, or
 * deserve a message that describes whey they failed (without throwing an exception).
 * 
 * @param <T>
 *            The format of the object returned by this ComplexResult
 */
public interface ComplexResult<T> extends Supplier<T>
{
	/**
	 * Returns any messages contained by this ComplexResult.
	 * 
	 * Note: If the ComplexResult returns a non-null value to get() then the behavior of
	 * this method is not controlled by this interface.
	 * 
	 * @return A non-null list of messages contained by this ComplexResult.
	 */
	public Collection<String> getMessages();

	/**
	 * Returns true if this ComplexResult is successful; false otherwise.
	 * 
	 * @return true if this ComplexResult is successful; false otherwise
	 */
	public boolean isSuccessful();

	/**
	 * Provides an in-line processor for the messages in this ComplexResult. This should
	 * unconditionally return the originating ComplexResult ("this").
	 * 
	 * If the ComplexResult is not successful, provides an unmodifiable view of the
	 * messages in this ComplexResult to the given Consumer. If this ComplexResult is
	 * successful, then the Consumer is not called.
	 *
	 * @param consumer
	 *            The consumer to be called if this ComplexResult contains any failure
	 *            messages
	 * @return Itself. i.e. The originating ComplexResult ("this")
	 */
	public default ComplexResult<T> whileMessaging(
		Consumer<Collection<String>> consumer)
	{
		if (!isSuccessful())
		{
			consumer.accept(Collections.unmodifiableCollection(getMessages()));
		}
		return this;
	}

	/**
	 * If the ComplexResult is successful, returns the value, otherwise returns the given
	 * parameter.
	 *
	 * Note: There is no protection against the return value being null if the given
	 * parameter is null.
	 *
	 * @param object
	 *            the value to be returned, if the ComplexResult is not successful
	 * @return The value, if the ComplexResult is successful, otherwise the given
	 *         parameter
	 */
	public default T orElse(T object)
	{
		return isSuccessful() ? get() : object;
	}

	/**
	 * If the ComplexResult is successful, returns the value, otherwise retrieves the
	 * value from the supplier and returns that retrieved value.
	 * 
	 * Note: There is no protection against Supplier or the return value of the Supplier
	 * being null.
	 *
	 * @param supplier
	 *            The supplier of the value to be returned, if the ComplexResult is not
	 *            successful
	 * @return The value, if the ComplexResult is successful, otherwise the value from
	 *         the supplier
	 */
	public default T orElseGet(Supplier<T> supplier)
	{
		return isSuccessful() ? get() : supplier.get();
	}

	/**
	 * If the ComplexResult is successful, returns the value, otherwise throws the
	 * exception provided by the given Function. The input to the function is an
	 * unmodifiable view of the Collection of error messages in this ComplexResult.
	 * 
	 * @param <TH>
	 *            The class of exception to be thrown if this ComplexResult is not
	 *            successful
	 * @param exceptionSupplier
	 *            The supplier of the exception to be thrown, if the ComplexResult is not
	 *            successful
	 * @return The value, if the ComplexResult is successful, otherwise throws the
	 *         exception provided by the given Function
	 * @throws TH if this ComplexResult is not successful
	 */
	public default <TH extends Throwable> T orElseThrow(
		Function<Collection<String>, ? extends TH> exceptionSupplier) throws TH
	{
		if (isSuccessful())
		{
			return get();
		}
		throw exceptionSupplier
			.apply(Collections.unmodifiableCollection(getMessages()));
	}

	/**
	 * If the ComplexResult is not successful, returns the ComplexResult.
	 * 
	 * If the ComplexResult is successful, provides the value to the given Function and
	 * returns the result of that function.
	 * 
	 * Note: There is no protection against Supplier or the return value of the Supplier
	 * being null.
	 *
	 * @param function
	 *            The function to be run if the ComplexResult is successful
	 * @return The result of the given Function (which is provided the value of this
	 *         ComplexResult) if this ComplexResult is successful, otherwise this
	 *         ComplexResult
	 */
	public default ComplexResult<T> ifSuccessful(
		Function<T, ComplexResult<T>> function)
	{
		return isSuccessful() ? function.apply(get()) : this;
	}

	/**
	 * If the ComplexResult is successful, performs the given action with the return
	 * value, otherwise does nothing.
	 *
	 * @param action
	 *            The action to be performed, if the ComplexResult is successful
	 */
	public default void ifPresent(Consumer<? super T> action)
	{
		if (isSuccessful())
		{
			action.accept(get());
		}
	}

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
