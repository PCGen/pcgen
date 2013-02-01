/*
 * Copyright (c) Thomas Parker, 2005-2013
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
 * Created on May 15, 2005
 */
package pcgen.base.graph.visitor;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * Performs a Depth First Search of a Graph, treating all edges in the Graph as
 * directed.
 * 
 * Note that use of this class over a simple DepthFirstTraverseAlgorithm is not
 * required in a DirectedGraph, as it is possible in a DirectedGraph for a user
 * to desire only absolute distance or to know directional distance, depending
 * on the circumstances of the graph search.
 * 
 * The only edges which can be traversed are DirectionalEdges (unless Java
 * Generics are ignored and canTraverseEdge is overridden by a subclass).
 * 
 * @see DepthFirstTraverseAlgorithm
 */
public class DirectedDepthFirstTraverseAlgorithm<N, ET extends DirectionalEdge<N>>
		extends DepthFirstTraverseAlgorithm<N, ET>
{

	/**
	 * Creates a new DirectedDepthFirstTraverseAlgorithm to traverse the given
	 * Graph.
	 * 
	 * @param g
	 *            The Graph this DirectedDepthFirstTraverseAlgorithm will
	 *            traverse.
	 */
	public DirectedDepthFirstTraverseAlgorithm(DirectionalGraph<N, ET> g)
	{
		super(g);
	}

	/**
	 * Indicates if this DirectedDepthFirstTraverseAlgorithm should traverse the
	 * given Edge. This is done with respect to the given node and node
	 * interface type. Returns true if the edge should be traversed.
	 * 
	 * This method enforces the directional nature of the
	 * DirectedDepthFirstTraverseAlgorithm.
	 * 
	 * @return true if the given edge should be traversed; false otherwise
	 */
	@Override
	protected boolean canTraverseEdge(ET edge, N gn, int type)
	{
		return (edge.getNodeInterfaceType(gn) & type) != 0;
	}
}
