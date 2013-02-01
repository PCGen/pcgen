/*
 * Copyright (c) Thomas Parker, 2004-2013
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
 * Created on Oct 12, 2004
 */
package pcgen.base.graph.visitor;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.Graph;
import pcgen.base.util.WrappedMapSet;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This is an implementation of a Depth First Search of a Graph.
 * 
 * See http://en.wikipedia.org/wiki/Depth-first_search for a full definition.
 * 
 * Note: This class uses the simple "remember which nodes (and edges) I've
 * already seen" method in order to avoid confusion from non-termination. This
 * may limit the size of the graph that can be traversed using this class.
 */
public class DepthFirstTraverseAlgorithm<N, ET extends Edge<N>> implements
		NodeTourist<N>, EdgeTourist<ET>
{

	/**
	 * Indicates the Graph on which this search algorithm is operating.
	 */
	private final Graph<N, ET> graph;

	/**
	 * A Set of the GraphNodes which have already been visited by this search
	 * algorithm. Used for performance improvement (avoid visiting multiple
	 * times), as well as to avoid an infinite loop in the case of a cycle in a
	 * Graph.
	 */
	private final Set<N> visitedNodes = new WrappedMapSet<N>(IdentityHashMap.class);

	/**
	 * A List of the GraphNodes leading from the initial search object, that have
	 * been visited by this search algorithm. Used to detect a cycle in a Graph.
	 */
	private final Set<N> currentNodes = new WrappedMapSet<N>(IdentityHashMap.class);
	private final Stack<N> errorNodes = new Stack<N>();

	/**
	 * A Set of the HyperEdges which have already been visited by this search
	 * algorithm. Used for performance improvement (avoid visiting multiple
	 * times), as well as to avoid an infinite loop in the case of a cycle in a
	 * Graph.
	 */
	private final Set<ET> visitedEdges = new WrappedMapSet<ET>(IdentityHashMap.class);

	/**
	 * Creates a new DepthFirstTraverseAlgorithm to traverse the given Graph.
	 * 
	 * @param g
	 *            The Graph this DepthFirstTraverseAlgorithm will traverse.
	 */
	public DepthFirstTraverseAlgorithm(Graph<N, ET> g)
	{
		super();
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		graph = g;
		/*
		 * FUTURE Should this detect a concurrent modification on a graph? Only
		 * trigger/detect after this is running... so after a traverseFrom
		 * method is called. The question is when to 'call it off' - does this
		 * still trigger any time any method is called once the Graph is
		 * modified, or does this only worry about when it is actively
		 * traversing?
		 */
	}

	/**
	 * Traverses the graph to connected Nodes and Edges in the Graph from the
	 * given source Node.
	 * 
	 * Results of this traversal are available from the getVisited* methods.
	 * 
	 * Calling traverseFrom* methods a second time without calling clear() will
	 * result in an UnsupportedOperationException being thrown.
	 * 
	 * @param gn
	 *            The source Node to be used for Graph traversal
	 */
	public void traverseFromNode(N gn)
	{
		if (!visitedNodes.isEmpty() || !visitedEdges.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (gn == null)
		{
			throw new IllegalArgumentException(
				"Node to traverse from cannot be null");
		}
		uncheckedTraverseFrom(gn);
	}

	/**
	 * Actually performs a traverse of the Graph from the given Node.
	 * 
	 * The lists of visitedNodes and visitedEdges are required because this is a
	 * Graph: It is possible to have a node or edge that is reachable in more
	 * than one way, and it is also possible to have a cycle in the graph (a
	 * cycle would cause an infinite loop if a tag and sweep system was not used
	 * to identify nodes and edges that have already been visited.
	 */
	private void uncheckedTraverseFrom(N gn)
	{
		if (currentNodes.contains(gn))
		{
			takeCycleAction(gn);
		}
		if (visitedNodes.contains(gn))
		{
			return;
		}
		currentNodes.add(gn);
		errorNodes.push(gn);
		visitedNodes.add(gn);
		for (ET edge : graph.getAdjacentEdges(gn))
		{

			// Don't traverse an edge a second time...
			if (!visitedEdges.contains(edge)
				&& canTraverseEdge(edge, gn, DirectionalEdge.SOURCE))
			{
				uncheckedTraverseFrom(edge);
			}
		}
		currentNodes.remove(gn);
		errorNodes.pop();
	}

	protected void takeCycleAction(N node)
	{
		throw new GraphCycleDetected(node, errorNodes);
	}

	/**
	 * Indicates if this DepthFirstTraverseAlgorithm should traverse the given
	 * Edge. This is done with respect to the given node and node interface
	 * type. Returns true if the edge should be traversed.
	 * 
	 * @return true if the given edge should be traversed; false otherwise
	 */
	protected boolean canTraverseEdge(ET edge, N gn, int type)
	{
		return true;
	}

	/**
	 * Traverses the graph to connected Nodes and Edges in the Graph from the
	 * given source Edge.
	 * 
	 * Results of this traversal are available from the getVisited* methods.
	 * 
	 * Calling traverseFrom* methods a second time without calling clear() will
	 * result in an UnsupportedOperationException being thrown.
	 * 
	 * @param he
	 *            The source Edge to be used for Graph traversal
	 */
	public void traverseFromEdge(ET he)
	{
		if (!visitedNodes.isEmpty() || !visitedEdges.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (he == null)
		{
			throw new IllegalArgumentException(
				"Edge to traverse from cannot be null");
		}
		uncheckedTraverseFrom(he);
	}

	/**
	 * Actually performs a traverse of the Graph from the given Edge.
	 * 
	 * The lists of visitedNodes and visitedEdges are required because this is a
	 * Graph: It is possible to have a node or edge that is reachable in more
	 * than one way, and it is also possible to have a cycle in the graph (a
	 * cycle would cause an infinite loop if a tag and sweep system was not used
	 * to identify nodes and edges that have already been visited.
	 */
	private void uncheckedTraverseFrom(ET he)
	{
		visitedEdges.add(he);
		List<N> graphNodes = he.getAdjacentNodes();
		for (N node : graphNodes)
		{
			// If node was not visited already and traverse legal
			if (canTraverseEdge(he, node, DirectionalEdge.SINK))
			{
				uncheckedTraverseFrom(node);
			}
		}
	}

	/**
	 * Returns the Set of Nodes visited by the last traversal by this
	 * DepthFirstTraverseAlgorithm
	 * 
	 * @return Set of Nodes visited by this DepthFirstTraverseAlgorithm
	 */
	@Override
	public Set<N> getVisitedNodes()
	{
		Set<N> set = new WrappedMapSet<N>(IdentityHashMap.class);
		set.addAll(visitedNodes);
		return set;
	}

	/**
	 * Returns the Set of Edges visited by the last traversal by this
	 * DepthFirstTraverseAlgorithm
	 * 
	 * @return Set of Edges visited by this DepthFirstTraverseAlgorithm
	 */
	@Override
	public Set<ET> getVisitedEdges()
	{
		Set<ET> set = new WrappedMapSet<ET>(IdentityHashMap.class);
		set.addAll(visitedEdges);
		return set;
	}

	/**
	 * Clears the distance results so that this DepthFirstTraverseAlgorithm may
	 * have a traverseFrom method called.
	 */
	public void clear()
	{
		visitedNodes.clear();
		visitedEdges.clear();
	}
}