/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.graph.search;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import pcgen.base.graph.base.DirectionalEdge;
import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.Graph;

/**
 * This is an implementation of a Depth First Search of a Graph.
 * 
 * See http://en.wikipedia.org/wiki/Depth-first_search for a full definition.
 * 
 * Note: This class uses the simple "remember which nodes (and edges) I've already seen"
 * method in order to avoid confusion from non-termination. This may limit the size of the
 * graph that can be traversed using this class.
 * 
 * @param <N>
 *            The format of the nodes in the Graph this DepthFirstTraverseAlgorithm will
 *            analyze
 * @param <E>
 *            The format of the edges in the Graph this DepthFirstTraverseAlgorithm will
 *            analyze
 */
public class DepthFirstTraverseAlgorithm<N, E extends Edge<N>>
{
	/**
	 * Indicates the Graph on which this search algorithm is operating.
	 */
	private final Graph<N, E> graph;

	/**
	 * A Set of the nodes which have already been visited by this search algorithm.
	 * Used for performance improvement (avoid visiting multiple times), as well as to
	 * avoid an infinite loop in the case of a cycle in a Graph.
	 */
	private final Set<N> visitedNodes = new HashSet<N>();

	/**
	 * A Set of the Edges which have already been visited by this search algorithm.
	 * Used for performance improvement (avoid visiting multiple times), as well as to
	 * avoid an infinite loop in the case of a cycle in a Graph.
	 */
	private final Set<E> visitedEdges = new HashSet<>();

	/**
	 * Constructs a new DepthFirstTraverseAlgorithm that will operate on the given Graph.
	 * 
	 * @param graph
	 *            The Graph this DepthFirstTraverseAlgorithm will analyze
	 */
	public DepthFirstTraverseAlgorithm(Graph<N, E> graph)
	{
		this.graph = Objects.requireNonNull(graph);
	}

	/**
	 * Traverses the Graph from the given node.
	 * 
	 * @param node
	 *            The node to start the Depth-First search from
	 */
	public void traverseFromNode(N node)
	{
		if (!visitedNodes.isEmpty() || !visitedEdges.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (node == null)
		{
			throw new IllegalArgumentException(
				"Node to traverse from cannot be null");
		}
		uncheckedTraverseFrom(node);
	}

	private void uncheckedTraverseFrom(N node)
	{
		visitedNodes.add(node);
		for (E edge : graph.getAdjacentEdges(node))
		{
			// Don't have visited the edge already...
			if (!visitedEdges.contains(edge)
				&& canTraverseEdge(edge, node, DirectionalEdge.SOURCE))
			{
				uncheckedTraverseFrom(edge);
			}
		}
	}

	/**
	 * Indicates if an Edge should be traversed
	 * 
	 * @param edge
	 *            The Edge to be checked
	 * @param node
	 *            The Node that may be traversed FROM
	 * @param type
	 *            The type of link implied by a naive search
	 * @return true if the Edge should be traversed; false otherwise
	 */
	protected boolean canTraverseEdge(E edge, N node, int type)
	{
		return true;
	}

	/**
	 * Traverses the Graph from the given edge.
	 * 
	 * @param edge
	 *            The edge to start the Depth-First search from
	 */
	public void traverseFromEdge(E edge)
	{
		if (!visitedNodes.isEmpty() || !visitedEdges.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (edge == null)
		{
			throw new IllegalArgumentException(
				"Edge to traverse from cannot be null");
		}
		uncheckedTraverseFrom(edge);
	}

	private void uncheckedTraverseFrom(E he)
	{
		visitedEdges.add(he);
		List<N> graphNodes = he.getAdjacentNodes();
		for (N node : graphNodes)
		{
			// If node was not visited already
			if (!visitedNodes.contains(node)
				&& canTraverseEdge(he, node, DirectionalEdge.SINK))
			{
				uncheckedTraverseFrom(node);
			}
		}
	}

	/**
	 * Returns the set of nodes visited by this DepthFirstTraverseAlgorithm.
	 * 
	 * @return The set of nodes visited by this DepthFirstTraverseAlgorithm
	 */
	public Set<N> getVisitedNodes()
	{
		return new HashSet<N>(visitedNodes);
	}

	/**
	 * Returns the set of edges visited by this DepthFirstTraverseAlgorithm.
	 * 
	 * @return The set of edges visited by this DepthFirstTraverseAlgorithm
	 */
	public Set<E> getVisitedEdges()
	{
		return new HashSet<E>(visitedEdges);
	}

	/**
	 * Clears the list of nodes and edges traversed by this DepthFirstTraverseAlgorithm.
	 */
	public void clear()
	{
		visitedNodes.clear();
		visitedEdges.clear();
	}
}
