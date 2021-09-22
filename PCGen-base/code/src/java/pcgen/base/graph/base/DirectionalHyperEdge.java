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
 * Created on Aug 26, 2004
 */
package pcgen.base.graph.base;

import java.util.Collection;

/**
 * A DirectionalHyperEdge is a Directional Edge which may have more than one
 * connection. This means it is possible to have multiple sources and multiple
 * sinks for this edge.
 * 
 * @param <N>
 *            The type of Node stored in this Edge
 */
public interface DirectionalHyperEdge<N> extends DirectionalEdge<N>
{

	/**
	 * Creates a replacement DirectionalHyperEdge for this DirectionalHyperEdge,
	 * with the replacement connected to the Nodes in the given Collections. The
	 * first Collection represents the source nodes of this
	 * DirectionalHyperEdge, the second Collection represents the sink nodes of
	 * this DirectionalHyperEdge.
	 * 
	 * At least one of the two given Collections must not be empty or null.
	 * 
	 * @param sourceNodes
	 *            The Collection indicating the source Nodes of the replacement
	 *            DirectionalHyperEdge
	 * @param sinkNodes
	 *            The Collection indicating the sink Nodes of the replacement
	 *            DirectionalHyperEdge
	 * @return A Replacement DirectionalHyperEdge connected to the given Nodes
	 */
	public DirectionalHyperEdge<N> createReplacementEdge(
		Collection<N> sourceNodes, Collection<N> sinkNodes);

	/*
	 * Note to users of DirectionalHyperEdge: It is assumed that when the
	 * createReplacementEdge method of HyperEdge is called that the connecting
	 * nodes should have the same interface type as with the existing
	 * DirectionalHyperEdge. There may always be exceptions (always check the
	 * actual object you are using for its operation), but that is the "default"
	 * behavior that should be matched if a specific behavior is not appropriate
	 * for a specialized object.
	 */

}
