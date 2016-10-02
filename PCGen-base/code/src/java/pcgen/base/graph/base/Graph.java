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
 * Created on Aug 27, 2004
 */
package pcgen.base.graph.base;

import java.util.Collection;
import java.util.List;

/**
 * A Graph is a collection of nodes and edges (which connect the nodes and
 * implement the Edge interface). This is a structure related to graph theory
 * (where the nodes are typically called vertices - that term is avoided here
 * because a vertex has meaning in geometry which can result in confusion
 * [especially to the original programmer ;) ]).
 * 
 * In a typical (graph theory) graph, an edge is limited to connecting to only
 * two nodes. The Graph interface makes no such limitation. HyperEdges may be
 * connected to more than two nodes.
 * 
 * Note that it is an expectation of implementing this interface that the Graph
 * properly supports the .equals(Object o) method to compare this Graph to
 * another Graph. (Note this may fail user expectations if objects contained
 * within the Graph do not implement the .equals method beyond the instance
 * identity provided by Object - this behavior is no different from how Sets
 * work, so that is reasonable). It is expected that a Graph will also maintain
 * the hashCode method to be consistent with equals.
 * 
 * @param <N>
 *            The type of Node stored in this Graph
 * @param <ET>
 *            The type of Edge stored in this Graph
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface Graph<N, ET extends Edge<N>>
{

	/**
	 * Adds the given Node to the Graph.
	 * 
	 * Note that the rules for addition may differ between Graph
	 * implementations. In no case can a Graph have two equal Nodes present in
	 * the Graph (The nodes form a set). However, the definition of equality can
	 * be determined by the Graph implementation. In some cases, equality may be
	 * determined by .equals(), while in others it may be identity (==).
	 * 
	 * Returns true if the given Node was added to the Graph. Returns false if
	 * the Node was not added to the Graph or if the given parameter was null.
	 * 
	 * @param node
	 *            The Node to add to the Graph
	 * @return true if the given Node was added to the Graph; false otherwise
	 */
	public boolean addNode(N node);

	/**
	 * Adds the given Edge to the Graph.
	 * 
	 * Note that the rules for addition may differ between Graph
	 * implementations. In no case can a Graph have two equal Edges present in
	 * the Graph (The nodes form a set). However, the definition of equality can
	 * be determined by the Graph implementation. In some cases, equality may be
	 * determined by .equals(), while in others it may be identity (==).
	 * 
	 * Note that some implementations of Graph may implicitly add any Nodes
	 * connected to the Edge, while other implementations may prohibit adding an
	 * Edge until all Nodes connected to the Edge are already present in the
	 * Graph.
	 * 
	 * Returns true if the given Edge was added to the Graph. Returns false if
	 * the Edge was not added to the Graph or if the given parameter was null.
	 * 
	 * @param edge
	 *            The Edge to add to the Graph
	 * @return true if the given Edge was added to the Graph; false otherwise
	 */
	public boolean addEdge(ET edge);

	/**
	 * Returns true if this Graph contains the given Object as a Node in the
	 * Graph.
	 * 
	 * @param obj
	 *            The Object to be tested to determine if it is a Node in the
	 *            Graph.
	 * @return true if this Graph contains the given Object as a Node in the
	 *         Graph; false otherwise.
	 */
	public boolean containsNode(Object obj);

	/**
	 * Returns true if this Graph contains the given Edge in the Graph.
	 * 
	 * @param edge
	 *            The Edge to be tested to determine if it is a Edge in the
	 *            Graph.
	 * @return true if this Graph contains the given Edge in the Graph; false
	 *         otherwise.
	 */
	public boolean containsEdge(Edge<?> edge);

	/**
	 * Returns a Count of the Nodes in this Graph.
	 * 
	 * @return A Count of the Nodes in this Graph
	 */
	public int getNodeCount();

	/**
	 * Returns a List of the Nodes in this Graph. Will return an Empty List (not
	 * null) if there are no Nodes in the Graph.
	 * 
	 * @return A List of the Nodes in this Graph
	 */
	public List<N> getNodeList();

	/**
	 * Returns a List of the Edges in this Graph. Will return an Empty List (not
	 * null) if there are no Edges in the Graph.
	 * 
	 * @return A List of the Edges in this Graph
	 */
	public List<ET> getEdgeList();

	/**
	 * Removes the given Node from the Graph.
	 * 
	 * Returns true if the given Node was removed from the Graph. Returns false
	 * if the Node was not present in the Graph to be removed, or if the given
	 * parameter was null.
	 * 
	 * Note that some implementations of Graph may implicitly remove any Edges
	 * connected to the given Node, while other implementations may prohibit
	 * removing a Node until there are no Edges connected to the Node in the
	 * Graph.
	 * 
	 * @param node
	 *            The Node to remove from the Graph
	 * @return true if the given Node removed from to the Graph; false otherwise
	 */
	public boolean removeNode(N node);

	/**
	 * Removes the given Edge from the Graph.
	 * 
	 * Returns true if the given Edge was removed from the Graph. Returns false
	 * if the Edge was not present in the Graph to be removed, or if the given
	 * parameter was null.
	 * 
	 * @param edge
	 *            The Edge to remove from the Graph
	 * @return true if the given Edge removed from to the Graph; false otherwise
	 */
	public boolean removeEdge(ET edge);

	/**
	 * Returns true if there are one or more Edges that are adjacent (connected) to the
	 * given Node. Returns false if there are no adjacent edges or the given Node is not
	 * in this Graph.
	 * 
	 * @param node
	 *            The Node for which adjacent Edges should be checked
	 * @return true if there are one or more Edges that are adjacent (connected) to the
	 *         given Node; false otherwise
	 */
	public boolean hasAdjacentEdge(N node);

	/**
	 * Returns a Set of the Edges that are adjacent (connected) to the given
	 * Node. Returns null if the given Node is not present in the Graph.
	 * 
	 * @param node
	 *            The Node for which the adjacent Edges should be returned.
	 * @return A Set of the Edges that are adjacent (connected) to the given
	 *         Node.
	 */
	public Collection<ET> getAdjacentEdges(N node);

	/**
	 * Adds a new GraphChangeListener to receive GraphChangeEvents
	 * (EdgeChangeEvent and NodeChangeEvent) from this Graph.
	 * 
	 * @param listener
	 *            The GraphChangeListener to receive GraphChangeEvents
	 */
	public void addGraphChangeListener(GraphChangeListener<N, ET> listener);

	/**
	 * Returns an Array of GraphChangeListeners receiving Graph Change Events
	 * from this Graph.
	 * 
	 * @return An Array of GraphChangeListeners receiving Graph Change Events
	 *         from this Graph
	 */
	public GraphChangeListener<N, ET>[] getGraphChangeListeners();

	/**
	 * Removes a GraphChangeListener so that it will no longer receive Graph
	 * Change Events from this Graph.
	 * 
	 * @param listener
	 *            The GraphChangeListener to be removed
	 */
	public void removeGraphChangeListener(GraphChangeListener<N, ET> listener);

	/**
	 * Returns the hashCode for this Graph.
	 * 
	 * @return the hashCode for this Graph.
	 */
	@Override
	public int hashCode();

	/**
	 * Tests to see if this Graph is equal to the provided Object. This will
	 * return true if the given Object is also a Graph, and that Graph contains
	 * equal Nodes and Edges.
	 * 
	 * @param o
	 *            The Object to be tested for equality with this Graph
	 * @return true if the given Object is a Graph that contains equal Nodes and
	 *         Edges to this Graph; false otherwise
	 */
	@Override
	public boolean equals(Object o);

	/**
	 * Returns true if this Graph is empty (has no Nodes and no Edges); false
	 * otherwise.
	 * 
	 * @return true if this Graph is empty; false otherwise
	 */
	public boolean isEmpty();

	/**
	 * Clears this Graph, removing all Nodes and Edges from the Graph.
	 */
	public void clear();
}
