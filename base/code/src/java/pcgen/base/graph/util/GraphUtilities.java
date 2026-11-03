/*
 * Copyright (c) Thomas Parker, 2004-18.
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

import java.util.ArrayList;
import java.util.List;

import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.Graph;

/**
 * GraphUtilities is a utility class designed to provide utility methods when working with
 * pcgen.base.graph.base.Graph Objects.
 */
public final class GraphUtilities
{

	private GraphUtilities()
	{
		//Don't construct a utility class
	}

	/**
	 * Checks for equality between two Graph objects.
	 * 
	 * @param graphA
	 *            The first graph to be checked for equality
	 * @param graphB
	 *            The second graph to be checked for equality
	 * @return true if the two Graphs have matching nodes and edges; false otherwise
	 */
	public static boolean equals(Graph<?, ?> graphA, Graph<?, ?> graphB)
	{
		List<?> nodeListA = graphA.getNodeList();
		List<?> nodeListB = graphB.getNodeList();
		int thisNodeSize = nodeListA.size();
		if (thisNodeSize != nodeListB.size())
		{
			return false;
		}
		// (potentially wasteful, but defensive copy so we know it is editable)
		nodeListB = new ArrayList<>(nodeListB);
		if (nodeListB.retainAll(nodeListA))
		{
			// GraphB contains nodes not in GraphA
			return false;
		}
		// Here, the node lists are identical...
		List<?> edgeListA = graphA.getEdgeList();
		List<?> edgeListB = graphB.getEdgeList();
		int thisEdgeSize = edgeListA.size();
		if (thisEdgeSize != edgeListB.size())
		{
			return false;
		}
		// (potentially wasteful, but defensive copy so we know it is editable)
		edgeListB = new ArrayList<>(edgeListB);
		// possible that GraphB contains edges not in GraphA
		return !edgeListB.retainAll(edgeListA);
	}

	/**
	 * Returns an unmodifiable view of the given Graph.
	 * 
	 * @param <N>
	 *            The format of the nodes in the given Graph
	 * @param <E>
	 *            The format of the edges in the given Graph
	 * @param graph
	 *            The Graph for which an unmodifiable view should be returned
	 * @return An unmodifiable view of the given Graph
	 */
	public static <N, E extends Edge<N>> Graph<N, E> unmodifiableGraph(Graph<N, E> graph)
	{
		return new UnmodifiableGraph<>(graph);
	}
}
