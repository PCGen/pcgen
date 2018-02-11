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

/**
 * A Staging is an object which has certain staged information which can be applied to an
 * object of the Class (by design, an interface) processed by this Staging.
 * 
 * @param <T>
 *            The Class of the (write) interface that this Staging will call on the target
 *            object when applyTo is called.
 */
public interface Staging<T>
{
	/**
	 * Applies the contents of this Staging to the given object (which must implement the
	 * reference interface of this Staging). The contents represents the method calls that
	 * were captured by this Staging.
	 * 
	 * @param target
	 *            The target object (which must implement the write interface of this
	 *            Staging), on which the method calls captured by this Staging will be
	 *            repeated
	 */
	public void applyTo(T target);

	/**
	 * Returns the Class of the interface of this Staging.
	 * 
	 * @return The Class of the interface of this Staging
	 */
	public Class<T> getInterface();
}
