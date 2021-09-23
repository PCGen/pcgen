/*
 * Copyright (c) Thomas Parker, 2017.
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
 * 
 * Created on Aug 26, 2004
 */
package pcgen.base.graph.util;

import java.util.function.Function;

import pcgen.base.graph.base.Edge;

/**
 * EdgeUtilities is a utility class designed to provide utility methods when working with
 * pcgen.base.graph.base.Edge Objects.
 */
public final class EdgeUtilities
{

	private EdgeUtilities()
	{
		//Don't construct a utility class
	}

	/**
	 * Returns the Node at the given location in the Edge provided to the returned
	 * Function.
	 * 
	 * @param i
	 *            The location of the Node to be returned from the Edge provided to the
	 *            returned Function
	 * @param <N>
	 *            The type of Node in the Edge provided to the Function
	 * @return A Function that will return the Node at the given location in the Edge
	 *         provided to said Function
	 */
	public static <N> Function<Edge<N>, N> getNode(int i)
	{
		return edge -> edge.getNodeAt(i);
	}

}
