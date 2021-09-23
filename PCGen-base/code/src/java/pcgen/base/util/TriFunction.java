/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
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

/**
 * A TriFuncion is a functional interface for a three-argument function.
 *
 * @param <T>
 *            Format of the first argument
 * @param <U>
 *            Format of the second argument
 * @param <V>
 *            Format of the third argument
 * @param <R>
 *            Format of the return value
 */
public interface TriFunction<T, U, V, R>
{
	/**
	 * Applies this TriFunction to the three given arguments
	 * 
	 * @param t
	 *            The first argument
	 * @param u
	 *            The second argument
	 * @param v
	 *            The third argument
	 * @return The return value
	 */
	public R apply(T t, U u, V v);
}
