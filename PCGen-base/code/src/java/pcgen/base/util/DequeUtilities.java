/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.Deque;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * DequeUtilities are utility methods related to the Deque interface.
 */
public final class DequeUtilities
{
	private DequeUtilities()
	{
		//Do not construct utility class
	}

	/**
	 * Empties a Deque full of ReadySupplier objects, providing them to the given
	 * Consumer.
	 * 
	 * Note that this will detect infinite loops where no ReadySupplier is ready, and an
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param <T>
	 *            The type of object supplied by the given ReadySupplier objects
	 * @param deque
	 *            The Deque containing the ReadySupplier objects
	 * @param consumer
	 *            The Consumer that will consume the objects provided by the given
	 *            ReadySupplier objects
	 * @throws IllegalArgumentException
	 *             if the Deque can't be emptied because all ReadySupplier objects are not
	 *             ready
	 */
	public static <T> void emptySupplyingDeque(Deque<ReadySupplier<T>> deque,
		Consumer<T> consumer)
	{
		Optional<ReadySupplier<T>> lastFailed = Optional.empty();
		while (!deque.isEmpty())
		{
			ReadySupplier<T> supplier = deque.pop();
			if (supplier.isReady())
			{
				lastFailed = Optional.empty();
				consumer.accept(supplier.get());
			}
			else
			{
				if (lastFailed.isEmpty())
				{
					lastFailed = Optional.of(supplier);
				}
				else if (lastFailed.get().equals(supplier))
				{
					//Infinite Loop
					throw new IllegalArgumentException(
						"Unable to resolve queue: " + deque);
				}
				deque.add(supplier);
			}
		}
	}

}
