/*
 * Copyright (c) Thomas Parker, 2004-2007
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.EdgeChangeEvent;
import pcgen.base.graph.base.Graph;
import pcgen.base.graph.base.GraphChangeListener;
import pcgen.base.graph.base.NodeChangeEvent;
import pcgen.base.graph.util.GraphUtilities;

/**
 * This class is a simple Graph which stores a List of the nodes and edges in
 * the Graph. While this may not be the most efficient storage mechanism from a
 * speed perspective, it is relatively efficient in terms of memory use.
 * 
 * This Graph uses normal equality (.equals()) to determine equality for
 * purposes of checking whether nodes and edges are already part of the Graph.
 * 
 * Note: It is NOT possible for an edge to connect to a node which is not in the
 * graph. There are (at least) two side effects to this limit: (1) If an edge is
 * added when the nodes to which it is not connected are not in the Graph, those
 * nodes will be implicitly added to the graph. (2) If a node is removed from
 * the Graph, all of the edges connected to that node will also be removed from
 * the graph.
 * 
 * WARNING: This GraphStorageStrategy has SIDE EFFECTS. When any GraphNode is
 * deleted from the graph, ANY and ALL HyperEdges connected to that GraphNode
 * are implicitly deleted from the graph. You CANNOT rely on the
 * GraphNodeRemoved event, as it will occur AFTER all of the attached edges have
 * been removed. You must check for and clean up adjacent edges BEFORE removing
 * any GraphNode if you wish for those edges to remain (in a modified form) in
 * the graph.
 * 
 * @param <N>
 *            The type of Node stored in this Graph
 * @param <ET>
 *            The type of Edge stored in this Graph
 */
@SuppressWarnings("PMD.TooManyMethods")
public class SimpleListGraph<N, ET extends Edge<N>> implements Graph<N, ET>
{

	/**
	 * The List of nodes contained in this Graph.
	 */
	private final List<N> nodeList;

	/**
	 * The List of edges contained in this Graph. An edge must be connected to a
	 * node which is already in the nodeList (this makes no statement about
	 * whether this addition is done implicitly by addEdge [it is in
	 * SimpleListGraph] or whether it is explicit).
	 */
	private final List<ET> edgeList;

	/**
	 * The GraphChangeSupport object which provides management of
	 * GraphChangeListeners and fires events to the listeners.
	 */
	private final GraphChangeSupport<N, ET> gcs;

	/**
	 * Create a new, empty SimpleListMapGraph.
	 */
	public SimpleListGraph()
	{
		super();
		edgeList = new ArrayList<>();
		nodeList = new ArrayList<>();
		gcs = new GraphChangeSupport<>(this);
	}

	@Override
	public boolean addNode(N v)
	{
		if ((v == null) || (nodeList.contains(v)))
		{
			return false;
		}
		nodeList.add(v);
		gcs.fireGraphNodeChangeEvent(v, NodeChangeEvent.NODE_ADDED);
		return true;
	}

	@Override
	public boolean addEdge(ET e)
	{
		if ((e == null) || (edgeList.contains(e)))
		{
			return false;
		}
		e.getAdjacentNodes().forEach(node -> addNode(node));
		edgeList.add(e);
		gcs.fireGraphEdgeChangeEvent(e, EdgeChangeEvent.EDGE_ADDED);
		return true;
	}

	@Override
	public boolean containsNode(Object v)
	{
		return nodeList.contains(v);
	}

	@Override
	public boolean containsEdge(Edge<?> e)
	{
		return edgeList.contains(e);
	}

	/**
	 * Returns a List of the Nodes contained within this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by SimpleListGraph. However,
	 * the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the nodes contained within
	 * the SimpleListGraph.
	 */
	@Override
	public List<N> getNodeList()
	{
		return new ArrayList<>(nodeList);
	}

	/**
	 * Returns a List of the Edges contained in this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by SimpleListGraph. However,
	 * the Edges contained in the List are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the SimpleListGraph.
	 */
	@Override
	public List<ET> getEdgeList()
	{
		return new ArrayList<>(edgeList);
	}

	@Override
	public boolean removeNode(N gn)
	{
		if ((gn == null) || (!containsNode(gn)))
		{
			return false;
		}
		boolean successful = true;
		for (ET edge : getAdjacentEdges(gn))
		{
			successful &= removeEdge(edge);
		}
		if (successful)
		{
			successful &= nodeList.remove(gn);
		}
		if (successful)
		{
			gcs.fireGraphNodeChangeEvent(gn, NodeChangeEvent.NODE_REMOVED);
		}
		return successful;
	}

	@Override
	public boolean removeEdge(ET ge)
	{
		if (ge == null)
		{
			return false;
		}
		if (edgeList.remove(ge))
		{
			gcs.fireGraphEdgeChangeEvent(ge, EdgeChangeEvent.EDGE_REMOVED);
			return true;
		}
		return false;
	}

	@Override
	public boolean hasAdjacentEdge(N gn)
	{
		return containsNode(gn) && edgeList.stream().anyMatch(e -> e.isAdjacentNode(gn));
	}

	@Override
	public Set<ET> getAdjacentEdges(N gn)
	{
		if (!containsNode(gn))
		{
			return null;
		}
		return edgeList.stream()
					   .filter(e -> e.isAdjacentNode(gn))
					   .collect(Collectors.toSet());
	}

	@Override
	public void addGraphChangeListener(GraphChangeListener<N, ET> arg0)
	{
		gcs.addGraphChangeListener(arg0);
	}

	/**
	 * Returns an array of the GraphChangeListeners to this Graph.
	 * 
	 * Ownership of the returned Array is transferred to the calling Object. No
	 * reference to the Array is maintained by SimpleListGraph. However, the
	 * GraphChangeListeners contained in the Array are (obviously!) returned BY
	 * REFERENCE, and care should be taken with modifying those
	 * GraphChangeListeners.
	 */
	@Override
	public GraphChangeListener<N, ET>[] getGraphChangeListeners()
	{
		return gcs.getGraphChangeListeners();
	}

	@Override
	public void removeGraphChangeListener(GraphChangeListener<N, ET> arg0)
	{
		gcs.removeGraphChangeListener(arg0);
	}

	/**
	 * Tests to see if this Graph is equal to the provided Object. This will return true
	 * if the given Object is also a Graph (not just a SimpleListGraph), and that Graph
	 * contains equal Nodes and Edges.
	 * 
	 * @param other
	 *            The Object to be tested for equality with this Graph
	 * @return true if the given Object is a Graph that contains equal Nodes and Edges to
	 *         this Graph; false otherwise
	 */
	@Override
	public boolean equals(Object other)
	{
		return (other instanceof Graph)
			&& GraphUtilities.equals(this, (Graph<?, ?>) other);
	}

	/**
	 * Returns the hashCode for this Graph. In order to keep this able to
	 * compare to any other Graph (not just a SimpleListGraph), this is based on
	 * common characteristics between graphs.
	 * 
	 * @return the hashCode for this Graph.
	 */
	@Override
	public int hashCode()
	{
		// This is really simple, but it works... and prevents a deep hash
		return nodeList.size() + (edgeList.size() * 23);
	}

	@Override
	public boolean isEmpty()
	{
		//Only need to check nodes, since addEdge adds the adjacent nodes
		return nodeList.isEmpty();
	}

	@Override
	public int getNodeCount()
	{
		return nodeList.size();
	}

	@Override
	public void clear()
	{
		/*
		 * TODO This doesn't actually notify GraphChangeListeners, is that a
		 * problem? - probably is ... thpr, 6/27/07
		 */
		nodeList.clear();
		edgeList.clear();
	}
}
