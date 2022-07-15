/*
 * Copyright (c) Thomas Parker, 2004-2007.
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
 * Created on Aug 31, 2004
 */
package pcgen.base.graph.base;

/**
 * A GraphEdge represents an edge in a Graph which connects to two, and only
 * two, GraphNodes. (A GraphEdge is a special case of a Edge where the number of
 * connected nodes is equal to two).
 * 
 * @param <N>
 *            The type of Node contained in this Edge
 */
public interface GraphEdge<N> extends Edge<N>
{

	/**
	 * Returns the node connected to the GraphEdge that is opposite of the given
	 * Node. Returns null if the given Node is not connected to the GraphEdge.
	 * 
	 * @param node
	 *            The Node for which the opposite Node should be returned.
	 * 
	 * @return The node connected to the GraphEdge that is opposite of the given
	 *         Node. null if the given Node is not connected to the GraphEdge.
	 */
	public N getOppositeNode(N node);

	/**
	 * Creates a replacement edge for the current GraphEdge given two Nodes.
	 * This interface makes no guarantee as to the legality of null as a
	 * parameter to this method. It may be restricted (or not) by the
	 * implementing class.
	 * 
	 * The replacement GraphEdge should be of the same class as the original
	 * GraphEdge on which this method is called.
	 * 
	 * @param node1
	 *            The first Node to be connected to the replacement GraphEdge.
	 * @param node2
	 *            The second Node to be connected to the replacement GraphEdge.
	 * @return The replacement GraphEdge.
	 */
	public GraphEdge<N> createReplacementEdge(N node1, N node2);
}
