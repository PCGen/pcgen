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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pcgen.base.graph.base.DirectionalEdge;
import pcgen.base.graph.base.Graph;

/**
 * This is an implementation of a Loop Detector using a Depth First Search of a Graph.
 * 
 * See http://en.wikipedia.org/wiki/Depth-first_search for a full definition.
 * 
 * This traverse is a directional traverse.
 * 
 * @param <N>
 *            The format of the nodes in the Graph this LoopDetectionAlgorithm will
 *            analyze
 */
public class LoopDetectionAlgorithm<N>
{

	/**
	 * Indicates the Graph on which this search algorithm is operating.
	 */
	private final Graph<N, ? extends DirectionalEdge<N>> graph;

	/**
	 * A Set of the ancestor nodes which have already been visited by this search
	 * algorithm.
	 */
	private final List<N> visitedNodes = new ArrayList<N>();

	/**
	 * Constructs a new LoopDetectionAlgorithm that will operate on the given Graph
	 * 
	 * @param graph
	 *            The Graph this LoopDetectionAlgorithm will analyze
	 */
	public LoopDetectionAlgorithm(Graph<N, ? extends DirectionalEdge<N>> graph)
	{
		this.graph = Objects.requireNonNull(graph);
	}

	/**
	 * Returns true if the Graph this LoopDetectionAlgorithm analyzes has a loop when
	 * starting from the given node
	 * 
	 * @param node
	 *            The node to start analysis from
	 * @return true if the Graph this LoopDetectionAlgorithm analyzes has a loop when
	 *         starting from the given node; false otherwise
	 */
	public boolean hasLoopFromNode(N node)
	{
		if (visitedNodes.contains(Objects.requireNonNull(node)))
		{
			return true;
		}
		visitedNodes.add(node);
		for (DirectionalEdge<N> edge : graph.getAdjacentEdges(node))
		{
			if (edge.isSource(node) && traverseFromEdge(edge))
			{
				return true;
			}
		}
		visitedNodes.remove(node);
		return false;
	}

	/**
	 * Traverses from the given Edge.
	 * 
	 * @param edge
	 *            The edge to be traversed from
	 * @return true if a loop was detected by a "child" node
	 */
	private boolean traverseFromEdge(DirectionalEdge<N> edge)
	{
		List<N> graphNodes = edge.getAdjacentNodes();
		for (N node : graphNodes)
		{
			if (edge.isSink(node) && hasLoopFromNode(node))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a list of the looping nodes if a loop was detected by this
	 * LoopDetectionAlgorithm.
	 * 
	 * @return A list of the looping nodes if a loop was detected by this
	 *         LoopDetectionAlgorithm
	 */
	public List<N> getLoopingNodes()
	{
		return new ArrayList<N>(visitedNodes);
	}

	/**
	 * Clears the visited node list so the LoopDetectionAlgorithm can be reused.
	 */
	public void clear()
	{
		visitedNodes.clear();
	}
}
