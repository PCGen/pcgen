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

import java.util.function.Supplier;

/**
 * A ReadySupplier is a Supplier that can indicate if it is Ready to supply an item.
 * Results of calling get() on the ReadySupplier if isReady() returns false are
 * implementation dependent (though a NoSuchElementException wouldn't be surprising, it is
 * not guaranteed).
 * 
 * @param <T>
 *            The type of object returned by this ReadySupplier
 */
public interface ReadySupplier<T> extends Supplier<T>
{
	/**
	 * Indicates if this ReadySupplier is ready to have get() called.
	 * 
	 * @return true if get() can be safely called on this ReadySupplier; false otherwise
	 */
	public boolean isReady();
}
