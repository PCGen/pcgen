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

import pcgen.base.graph.base.Edge;

/**
 * This Graph uses redundant storage to improve query speed for certain methods.
 * In addition to simple lists of the nodes and edges present in a Graph, a Map
 * from each node to the adjacent edges is maintained.
 * 
 * This Graph uses normal equality (.equals()) to determine equality for
 * purposes of checking whether nodes and edges are already part of the Graph.
 * 
 * This class provides a more balanced query speed for querying adjacent graph
 * elements. Specifically, an edge knows to which nodes it is connected. To
 * determine which edges a node is connected to requires a query to the Graph.
 * The Map maintained by this class prevents an iteration over the entire List
 * of edges whenever getAdjacentEdgeList(GraphNode n) is called.
 * 
 * WARNING: This SimpleListMapGraph contains a CACHE which uses the Nodes as a
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
 * graph, ANY and ALL HyperEdges connected to that GraphNode are implicitly
 * deleted from the graph. You CANNOT rely on the GraphNodeRemoved event, as it
 * will occur AFTER all of the attached edges have been removed. You must check
 * for and clean up adjacent edges BEFORE removing any GraphNode if you wish for
 * those edges to remain (in a modified form) in the graph.
 * 
 * @param <N>
 *            The type of Node stored in this Graph
 * @param <ET>
 *            The type of Edge stored in this Graph
 */
public class SimpleListMapGraph<N, ET extends Edge<N>> extends
		AbstractListMapGraph<N, ET>
{
	/*
	 * Note to programmers: While it may be tempting to assimilate this
	 * functionality into AbstractListMapGraph (and make that NOT abstract),
	 * that is a deceptive integration that would cause other subclasses to
	 * violate the interface defined by SimpleListMapGraph. Given that it is
	 * possible to have a Graph which is Directional yet still uses
	 * *ListMapGraph, such a definition (that all *ListMapGraphs were
	 * SimpleListMapGraphs) would cause a violation of the contract of a
	 * SimpleListMapGraph similar to the circle/ellipse dilemma. (for more
	 * information, see http://ootips.org/ellipse-circle.html )
	 */
}
