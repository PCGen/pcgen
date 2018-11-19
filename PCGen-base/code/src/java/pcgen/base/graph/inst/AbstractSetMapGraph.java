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
package pcgen.base.graph.inst;

import static pcgen.base.util.SetUtilities.removeFromSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.EdgeChangeEvent;
import pcgen.base.graph.base.Graph;
import pcgen.base.graph.base.GraphChangeListener;
import pcgen.base.graph.base.NodeChangeEvent;
import pcgen.base.graph.util.GraphUtilities;

/**
 * This Graph uses redundant storage to improve query speed for certain methods.
 * In addition to simple lists of the nodes and edges present in a Graph, a Map
 * from each node to the adjacent edges is maintained.
 * 
 * This Graph uses normal equality (.equals()) to determine equality for
 * purposes of checking whether nodes and edges are already part of the Graph.
 * 
 * This Graph implementation provides a more balanced query speed for querying
 * adjacent graph elements. Specifically, an edge knows to which nodes it is
 * connected. To determine which edges a node is connected to requires a query
 * to the Graph. The Map maintained by this class prevents an iteration over the
 * entire List of edges whenever getAdjacentEdgeList(GraphNode n) is called.
 * 
 * This Graph implementation also provides much faster .containsNode() or
 * .containsEdge() processing speed than AbstractListMapGraph. However, if those
 * methods are not called frequently or the number of Nodes and Edges in the
 * Graph is small, then the use of AbstractListMapGraph would conserve memory
 * relative to AbstractSetMapGraph.
 * 
 * WARNING: This AbstractSetMapGraph contains a CACHE which uses the Nodes as a
 * KEY. Due to the functioning of a Map (it uses the .hashCode() method), if a
 * Node is modified IN PLACE in the Graph (without being removed and readded),
 * it WILL cause the caching to FAIL, because the cache will have indexed the
 * Node by the old hash code. It is therefore HIGHLY advised that this Graph
 * implementation ONLY be used where the Nodes are either Immutable or do not
 * override Object.equals().
 * 
 * Note: It is NOT possible for an edge to connect to a node which is not in the
 * graph. There are (at least) two side effects to this limit: (1) If an edge is
 * added when the nodes to which it is not connected are not in the Graph, those
 * nodes will be implicitly added to the graph. (2) If a node is removed from
 * the Graph, all of the edges connected to that node will also be removed from
 * the graph.
 * 
 * WARNING: This Graph has SIDE EFFECTS. When any GraphNode is deleted from the
 * graph, ANY and ALL Edges connected to that GraphNode are implicitly deleted
 * from the graph. You CANNOT rely on the GraphNodeRemoved event, as it will
 * occur AFTER all of the attached edges have been removed. You must check for
 * and clean up adjacent edges BEFORE removing any GraphNode if you wish for
 * those edges (in a modified form) to remain in the graph.
 * 
 * @param <N>
 *            The type of Node stored in this Graph
 * @param <ET>
 *            The type of Edge stored in this Graph
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class AbstractSetMapGraph<N, ET extends Edge<N>> implements
		Graph<N, ET>
{

	/**
	 * The Set of nodes contained in this Graph.
	 */
	private final Map<N, N> nodeMap;

	/**
	 * The Set of edges contained in this Graph. An edge must be connected to a
	 * node which is already in the nodeMap (this makes no statement about
	 * whether this addition is done implicitly by addEdge [it is in
	 * AbstractSetMapGraph] or whether it is explicit).
	 */
	private final Set<ET> edgeSet;

	/**
	 * A Map indicating which nodes are connected to which edges. This is
	 * redundant information to what is actually contained in the edges
	 * themselves, but is present in AbstractSetMapGraph in order to speed calls
	 * to getAdjacentEdges
	 */
	private final Map<N, Set<ET>> nodeEdgeMap;

	/**
	 * The GraphChangeSupport object which provides management of
	 * GraphChangeListeners and fires events to the listeners.
	 */
	private final GraphChangeSupport<N, ET> gcs;

	/**
	 * Creates a new, empty AbstractSetMapGraph.
	 */
	public AbstractSetMapGraph()
	{
		super();
		edgeSet = new HashSet<>();
		nodeMap = new HashMap<>();
		gcs = new GraphChangeSupport<>(this);
		nodeEdgeMap = new HashMap<>();
	}

	/**
	 * Adds the given Node to the Graph. Returns true if the given Node was
	 * successfully added. Because the Nodes in this Graph are a Set, this
	 * method will return false if a Node is already present in the Graph.
	 */
	@Override
	public boolean addNode(N node)
	{
		if (node == null)
		{
			return false;
		}
		if (nodeMap.containsKey(node))
		{
			// Node already in this Graph
			return false;
		}
		nodeMap.put(node, node);
		nodeEdgeMap.put(node, new HashSet<>());
		gcs.fireGraphNodeChangeEvent(node, NodeChangeEvent.NODE_ADDED);
		return true;
	}

	/**
	 * Returns the node actually stored in the graph that is equal to the given
	 * node. This is used to avoid memory leaks in the case of matching Nodes
	 * (to avoid storing a Node that is .equal but not == in an edge that will
	 * be placed into the Graph).
	 * 
	 * @param node
	 *            The Node to be internalized.
	 * @return The internalized version of the Node, relative to this Graph.
	 */
	public N getInternalizedNode(N node)
	{
		if (node == null)
		{
			return null;
		}
		// TODO Consider whether to return null or v... if not in the Graph?
		return nodeMap.get(node);
	}

	/**
	 * Adds the given Edge to the Graph. Returns true if the given Edge was
	 * successfully added. Implicitly adds any Nodes connected to the given Edge
	 * to the Graph. Because the Edges in this Graph are a Set, this method will
	 * return false if an Edges is already present in the Graph.
	 */
	@Override
	public boolean addEdge(ET edge)
	{
		if (edge == null)
		{
			return false;
		}
		boolean added = edgeSet.add(edge);
		if (!added)
		{
			return false;
		}
		for (N node : edge.getAdjacentNodes())
		{
			addNode(node);
			nodeEdgeMap.get(node).add(edge);
		}
		gcs.fireGraphEdgeChangeEvent(edge, EdgeChangeEvent.EDGE_ADDED);
		return true;
	}

	/**
	 * Returns true if this Graph contains the given Node.
	 */
	@Override
	public boolean containsNode(Object node)
	{
		// This is presumably faster than searching through nodeList
		return nodeEdgeMap.containsKey(node);
	}

	/**
	 * Returns true if this Graph contains the given Edge.
	 */
	@Override
	public boolean containsEdge(Edge<?> edge)
	{
		return edgeSet.contains(edge);
	}

	/**
	 * Returns a List of Nodes in this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by AbstractSetMapGraph.
	 * 
	 * However, the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the nodes contained within
	 * the AbstractSetMapGraph.
	 * 
	 * *WARNING*: Modification of the Nodes in place may result in failure of
	 * the AbstractSetMapGraph to return appropriate values from various methods
	 * of AbstractSetMapGraph. If a Node is modified in place, the modifications
	 * must not alter the hash code (as returned by the Node's .hashCode()
	 * method) for AbstractSetMapGraph to maintain proper operation.
	 */
	@Override
	public List<N> getNodeList()
	{
		return new ArrayList<>(nodeMap.keySet());
	}

	/**
	 * Returns a List of Edges in this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by AbstractSetMapGraph.
	 * However, the Edges contained in the List are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the AbstractSetMapGraph.
	 */
	@Override
	public List<ET> getEdgeList()
	{
		return new ArrayList<>(edgeSet);
	}

	/**
	 * Removes the given Node from the AbstractSetMapGraph. As a byproduct of
	 * this removal, all Edges connected to the Node will also be removed from
	 * the Graph.
	 */
	@Override
	public boolean removeNode(N node)
	{
		if (node == null)
		{
			return false;
		}
		if (!containsNode(node))
		{
			return false;
		}
		/*
		 * Note: This method is command sequence sensitive.
		 * 
		 * First, the remove (from nodeEdgeMap) below is "guaranteed" to work,
		 * since the graph must contain the node (test above) and it is assumed
		 * that the addNode method initialized nodeEdgeMap.
		 * 
		 * Second, the use of remove is significant, in that it removes the set
		 * of connected edges from the Map. This is important, since removeEdge
		 * is called from within the stream, and removeEdge will alter sets within
		 * nodeEdgeMap. Therefore, the use of get in place of remove for
		 * creation of this Iterator would result in a
		 * ConcurrentModificationException (since the set for GraphNode gn would
		 * be modified by removeEdge while inside this Iterator).
		 */
		// FUTURE Consider Check of return values of removeEdge here to ensure success??
		nodeEdgeMap.remove(node).forEach(edge -> removeEdge(edge));
		/*
		 * containsNode test means we don't need to check return value of remove
		 * we 'know' it is present (barring an internal error!). This remove
		 * must happen after removeEdge above, as removeEdge may trigger side
		 * effects that will expect this Node to still be present in the Graph.
		 */
		nodeMap.remove(node);
		gcs.fireGraphNodeChangeEvent(node, NodeChangeEvent.NODE_REMOVED);
		return true;
	}

	/**
	 * Removes the given Edge from the AbstractSetMapGraph.
	 */
	@Override
	public boolean removeEdge(ET edge)
	{
		if (edge == null)
		{
			return false;
		}
		boolean removed = edgeSet.remove(edge);
		if (!removed)
		{
			return false;
		}
		//Edge must have been present in the Graph if we made it to this point
		for (N node : edge.getAdjacentNodes())
		{
			//null protection required to protect against side effects
			Optional.ofNullable(nodeEdgeMap.get(node)).ifPresent(removeFromSet(edge));
		}
		gcs.fireGraphEdgeChangeEvent(edge, EdgeChangeEvent.EDGE_REMOVED);
		return true;
	}

	@Override
	public boolean hasAdjacentEdge(N node)
	{
		// implicitly returns null if gn is not in the nodeEdgeMap
		Set<ET> adjacentEdges = nodeEdgeMap.get(node);
		return (adjacentEdges != null) && !adjacentEdges.isEmpty();
	}

	/**
	 * Returns a Set of the Edges which are Adjacent (connected) to the given
	 * Node. Returns null if the given Node is not in the Graph.
	 * 
	 * Ownership of the returned Set is transferred to the calling Object. No
	 * reference to the Set Object is maintained by AbstractSetMapGraph.
	 * However, the Edges contained in the Set are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the AbstractSetMapGraph.
	 */
	@Override
	public Set<ET> getAdjacentEdges(N node)
	{
		// implicitly returns null if gn is not in the nodeEdgeMap
		Set<ET> adjacentEdges = nodeEdgeMap.get(node);
		return (adjacentEdges == null) ? null : new HashSet<>(adjacentEdges);
	}

	/**
	 * Adds the given GraphChangeListener as a GraphChangeListener of this
	 * Graph.
	 */
	@Override
	public void addGraphChangeListener(GraphChangeListener<N, ET> listener)
	{
		gcs.addGraphChangeListener(listener);
	}

	/**
	 * Returns an array of the GraphChangeListeners to this Graph.
	 * 
	 * Ownership of the returned Array is transferred to the calling Object. No
	 * reference to the Array is maintained by AbstractSetMapGraph. However, the
	 * GraphChangeListeners contained in the Array are (obviously!) returned BY
	 * REFERENCE, and care should be taken with modifying those
	 * GraphChangeListeners.
	 */
	@Override
	public GraphChangeListener<N, ET>[] getGraphChangeListeners()
	{
		return gcs.getGraphChangeListeners();
	}

	/**
	 * Removes the given GraphChangeListener as a GraphChangeListener of this
	 * Graph.
	 */
	@Override
	public void removeGraphChangeListener(GraphChangeListener<N, ET> listener)
	{
		gcs.removeGraphChangeListener(listener);
	}

	/**
	 * Tests to see if this Graph is equal to the provided Object. This will
	 * return true if the given Object is also a Graph, and that Graph contains
	 * equal Nodes and Edges.
	 * 
	 * @param other
	 *            The Object to be tested for equality with this Graph
	 * @return true if the given Object is a Graph that contains equal Nodes and
	 *         Edges to this Graph; false otherwise
	 */
	@Override
	public boolean equals(Object other)
	{
		return (other instanceof Graph)
				&& GraphUtilities.equals(this, (Graph<?, ?>) other);
	}

	/**
	 * Returns the hashCode for this Graph.
	 * 
	 * @return the hashCode for this Graph.
	 */
	@Override
	public int hashCode()
	{
		// This is really simple, but it works... and prevents a deep hash
		return nodeMap.size() + (edgeSet.size() * 23);
	}

	/**
	 * Returns true if this Graph is empty (has no Nodes and no Edges); false
	 * otherwise.
	 * 
	 * @return true if this Graph is empty; false otherwise
	 */
	@Override
	public boolean isEmpty()
	{
		return nodeMap.isEmpty() && edgeSet.isEmpty();
	}

	/**
	 * Returns the number of nodes in this Graph.
	 * 
	 * @return The number of nodes in the Graph, as an integer
	 */
	@Override
	public int getNodeCount()
	{
		return nodeMap.size();
	}

	/**
	 * Clears this Graph, removing all Nodes and Edges from the Graph.
	 */
	@Override
	public void clear()
	{
		/*
		 * TODO This doesn't actually notify GraphChangeListeners, is that a
		 * problem? - probably is ... thpr, 6/27/07
		 */
		nodeEdgeMap.clear();
		nodeMap.clear();
		edgeSet.clear();
	}
}
