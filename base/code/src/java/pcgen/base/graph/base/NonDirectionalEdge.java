/*
 * Copyright (c) Thomas Parker, 2005-2007.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * Created on Jan 8, 2005
 */
package pcgen.base.graph.base;

import java.util.Collection;

/**
 * A NonDirectionalEdge is an edge whcih does not Maintain a specific direction
 * in a Graph. In contract, a DirectionalEdge has source and sink nodes. A
 * NonDirectionalEdge places no contract on the number of Nodes to which it can
 * be connected, other than it must be greater than zero.
 * 
 * @param <N>
 *            The type of Node which this Edge contains
 */
public interface NonDirectionalEdge<N> extends Edge<N>
{
	/**
	 * Creates a 'replacement' Edge for this NonDirectionalEdge. The replacement
	 * Edge should share all characteristics with the original
	 * NonDirectionalEdge except that the Nodes connected to the replacement
	 * NonDirectionalEdge are the Nodes in the given Collection.
	 * 
	 * The given collection must be non-null and non-empty.
	 * 
	 * @param nodes
	 *            The collection of Nodes to which the replacement Edge will be
	 *            connected
	 * @return A 'replacement' Edge for this NonDirectionalEdge which is
	 *         connected to the given Nodes.
	 */
	public NonDirectionalEdge<N> createReplacementEdge(Collection<N> nodes);
}
