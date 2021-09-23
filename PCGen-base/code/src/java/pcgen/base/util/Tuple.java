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

/**
 * A Tuple is an ordered sequence of two objects
 * 
 * @param <F>
 *            The format of the first Object contained in the Tuple
 * @param <S>
 *            The format of the second Object contained in the Tuple
 */
public class Tuple<F, S>
{
	/**
	 * The first Object contained in the Tuple.
	 */
	private final F first;

	/**
	 * The second Object contained in the Tuple.
	 */
	private final S second;

	/**
	 * Constructs a new Tuple from the two given Objects.
	 * 
	 * @param first
	 *            The first Object contained in the Tuple
	 * @param second
	 *            The second Object contained in the Tuple
	 */
	public Tuple(F first, S second)
	{
		this.first = first;
		this.second = second;
	}

	/**
	 * Returns the first Object contained in the Tuple.
	 * 
	 * @return The first Object contained in the Tuple
	 */
	public F getFirst()
	{
		return first;
	}

	/**
	 * Returns the second Object contained in the Tuple.
	 * 
	 * @return The second Object contained in the Tuple
	 */
	public S getSecond()
	{
		return second;
	}
	
	@Override
	public String toString()
	{
		return "<(" + first + "," + second + ")>";
	}
}
