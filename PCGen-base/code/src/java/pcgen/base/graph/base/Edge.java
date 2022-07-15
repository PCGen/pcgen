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

import java.util.List;

/**
 * An Edge is an object in a Graph which connects to one or more Nodes.
 * 
 * @param <N>
 *            The type of Node stored in this Edge
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Edge<N>
{

	/**
	 * Returns the Node at the given index in the Edge. An Edge in general makes
	 * no guarantee about the order in which a Node will appear in the Edge.
	 * 
	 * @param index
	 *            The index of the Node to be returned
	 * @return The Node at the given index in this Edge
	 */
	public N getNodeAt(int index);

	/**
	 * Returns a List of the Nodes which are adjacent (connected) to the Edge.
	 * 
	 * Ownership of the returned List must be transferred to the calling Object. No
	 * reference to the List Object may be maintained by the Edge. However, the Nodes
	 * contained in the List are returned BY REFERENCE, and modification of the returned
	 * Nodes will modify the Nodes contained within the Edge.
	 * 
	 * @return The List of Nodes which are adjacent to the Edge
	 */
	public List<N> getAdjacentNodes();

	/**
	 * Returns the number of adjacent nodes to this Edge.
	 * 
	 * @return The number of adjacent nodes to this Edge.
	 */
	public int getAdjacentNodeCount();

	/**
	 * Returns true if the given Node is adjacent to this Edge. Returns false if
	 * the given Node is not adjacent to this Edge or if the given Node is null.
	 * 
	 * @param node
	 *            The Node to be tested
	 * @return true if the given Node is adjacent to this Edge; false otherwise.
	 */
	public boolean isAdjacentNode(N node);
}
