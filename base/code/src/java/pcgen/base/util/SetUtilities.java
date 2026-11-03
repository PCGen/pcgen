/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Set;
import java.util.function.Consumer;

/**
 * ListUtilities is a utility class designed to provide utility methods when working with
 * java.util.Set Objects.
 */
public final class SetUtilities
{

	private SetUtilities()
	{
		//Do not construct utility class
	}

	/**
	 * Provides a Consumer that removes the given object from the Set provided to the
	 * Consumer. Intended to be used in a Stream.
	 * 
	 * @param object
	 *            The object to be removed from the Set provided to the Consumer
	 * @return A Consumer that removes the given object from the Set provided to the
	 *         Consumer
	 * @param <T>
	 *            The type of the object contained in the Set managed by the Consumer
	 */
	public static <T> Consumer<? super Set<T>> removeFromSet(T object)
	{
		return x -> x.remove(object);
	}

}
