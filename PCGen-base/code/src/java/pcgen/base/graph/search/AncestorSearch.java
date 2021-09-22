/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.graph.search;

import pcgen.base.graph.base.DirectionalEdge;
import pcgen.base.graph.base.Graph;

/**
 * Performs a search for ancestors of a node or edge in a Graph
 * 
 * @param <N>
 *            The format of node in the graph being searched
 * @param <E>
 *            The format of edge in the graph being searched
 */
public class AncestorSearch<N, E extends DirectionalEdge<N>>
		extends DepthFirstTraverseAlgorithm<N, E>
{
	/**
	 * Identifies the bits to be reversed (since we will search the hierarchy backwards of
	 * the assumption in DepthFirstTraverseAlgorithm).
	 */
	private static final int REVERSE = DirectionalEdge.SINK | DirectionalEdge.SOURCE;

	/**
	 * Constructs a new AncestorSearch that will operate on the given graph
	 * @param graph The graph this AncestorSearch will operate on
	 */
	public AncestorSearch(Graph<N, E> graph)
	{
		super(graph);
	}

	/*
	 * Indicates if an edge can be traversed - will effectively allow an edge to be
	 * traversed if the node is opposite of the implied type (meaning it will traverse
	 * directional edges backwards).
	 */
	@Override
	protected boolean canTraverseEdge(E edge, N node, int type)
	{
		return (edge.getNodeInterfaceType(node) & (type ^ REVERSE)) != 0;
	}
}
