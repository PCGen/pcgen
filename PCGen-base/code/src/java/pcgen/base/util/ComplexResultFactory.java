/*
 * Copyright (c) 2020 Tom Parker <thpr@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * A Factory for ComplexResult objects, in order to capture multiple messages.
 */
public class ComplexResultFactory
{
	/**
	 * The messages for the resulting ComplexResult
	 */
	private final List<String> messages = new ArrayList<>();

	/**
	 * Adds a new message for the ComplexResult this ComplexResultFactory will return
	 * 
	 * @param message
	 *            The new message
	 */
	public void addMessage(String message)
	{
		messages.add(message);
	}

	/**
	 * Returns a ComplexResult with the messages in this ComplexResultFactory if there are
	 * any messages. Otherwise returns the ComplexResult provided by the given Supplier.
	 * 
	 * @param <T>
	 *            The format (class) of object returned by the ComplexResult (in case of
	 *            success)
	 * @param supplier
	 *            The Supplier that will provide the ComplexResult to be returned if this
	 *            ComplexResultFactory has no messages
	 * @return A ComplexResult with the messages in this ComplexResultFactory if there are
	 *         any messages; otherwise the ComplexResult provided by the given Supplier
	 */
	public <T> ComplexResult<T> ofComplex(Supplier<ComplexResult<T>> supplier)
	{
		if (messages.isEmpty())
		{
			return supplier.get();
		}
		return fail();
	}

	/**
	 * Returns a ComplexResult with the messages in this ComplexResultFactory if there are
	 * any messages. Otherwise returns a successful ComplexResult containing the value
	 * provided by the given Supplier.
	 * 
	 * @param <T>
	 *            The format (class) of object returned by the ComplexResult (in case of
	 *            success)
	 * @param supplier
	 *            The Supplier that will provide the value to be returned if this
	 *            ComplexResultFactory has no messages
	 * @return A ComplexResult with the messages in this ComplexResultFactory if there are
	 *         any messages; otherwise a successful ComplexResult containing the value
	 *         provided by the given Supplier
	 */
	public <T> ComplexResult<T> of(Supplier<T> supplier)
	{
		if (messages.isEmpty())
		{
			return ComplexResult.ofSuccess(supplier.get());
		}
		return fail();
	}

	/**
	 * Returns a ComplexResult with the messages in this ComplexResultFactory if there are
	 * any messages. Otherwise returns a successful ComplexResult containing the given
	 * value.
	 * 
	 * @param <T>
	 *            The format (class) of object returned by the ComplexResult (in case of
	 *            success)
	 * @param object
	 *            The value to be returned if this ComplexResultFactory has no messages
	 * @return A ComplexResult with the messages in this ComplexResultFactory if there are
	 *         any messages; otherwise a successful ComplexResult containing the given
	 *         value
	 */
	public <T> ComplexResult<T> of(T object)
	{
		if (messages.isEmpty())
		{
			return ComplexResult.ofSuccess(object);
		}
		return fail();
	}

	/**
	 * Produces a new ComplexResult indicating failure, and containing the messages
	 * provided to this ComplexResultFactory.
	 * 
	 * @param <T>
	 *            The format (class) of object this ComplexResult would have provided
	 * @return A new ComplexResult indicating failure, and containing the messages
	 *         provided to this ComplexResultFactory
	 */
	private <T> ComplexResult<T> fail()
	{
		return new ComplexResult<>()
		{

			@Override
			public T get()
			{
				throw new NoSuchElementException(
					"Can't perform get on failed result");
			}

			@Override
			public Collection<String> getMessages()
			{
				return Collections.unmodifiableCollection(messages);
			}

			@Override
			public boolean isSuccessful()
			{
				return false;
			}

		};
	}
}
