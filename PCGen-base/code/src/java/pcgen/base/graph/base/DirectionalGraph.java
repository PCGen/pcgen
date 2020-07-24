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
package pcgen.base.graph.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A DirectionalGraph is a Graph which contains directional edges. Directional
 * edges are edges which have orientation: possess source(s) and sink(s).
 * 
 * FUTURE Should these be Sets to be consistent with getAdjacentEdges in Graph?
 * However, that can't be trivially done, as MapPainter uses the first item in
 * inward edge list in order to determine which parent to draw the creator on...
 * 
 * @param <N>
 *            The type of Node stored in this Graph
 * @param <ET>
 *            The type of Edge stored in this Graph
 */
public interface DirectionalGraph<N, ET extends DirectionalEdge<N>> extends
		Graph<N, ET>
{
	/**
	 * Returns a List of the Edges for which the given Node is a sink Node in
	 * this DirectionalGraph.
	 * 
	 * @param node
	 *            The Node for which to return the inward Edges.
	 * @return The List of Edges for which the given Node is a sink Node.
	 */
	public default List<ET> getInwardEdgeList(N node)
	{
		Collection<ET> adjacentEdgeList = getAdjacentEdges(node);
		if (adjacentEdgeList == null)
		{
			return null;
		}
		return adjacentEdgeList.stream()
							   .filter(edge -> edge.isSink(node))
							   .collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Returns a List of the Edges for which the given Node is a source Node in
	 * this DirectionalGraph.
	 * 
	 * @param node
	 *            The Node for which to return the outward Edges.
	 * @return The List of Edges for which the given Node is a source Node.
	 */
	public default List<ET> getOutwardEdgeList(N node)
	{
		Collection<ET> adjacentEdgeList = getAdjacentEdges(node);
		if (adjacentEdgeList == null)
		{
			return null;
		}
		return adjacentEdgeList.stream()
							   .filter(edge -> edge.isSource(node))
							   .collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Returns true if the given Node is connected to any Edge in this
	 * DirectionalGraph as a sink Node. Returns false if the given Node is not
	 * in this DirectionalGraph.
	 * 
	 * @param node
	 *            The Node for which to check for inward Edges.
	 * @return true if the given Node is connected to any Edge in this
	 *         DirectionalGraph as a sink Node; false otherwise
	 */
	public default boolean hasInwardEdge(N node)
	{
		Collection<ET> adjacentEdgeList = getAdjacentEdges(node);
		if (adjacentEdgeList == null)
		{
			return false;
		}
		for (ET edge : adjacentEdgeList)
		{
			if (edge.isSink(node))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given Node is connected to any Edge in this
	 * DirectionalGraph as a source Node. Returns false if the given Node is not
	 * in this DirectionalGraph.
	 * 
	 * @param node
	 *            The Node for which to check for outward Edges.
	 * @return true if the given Node is connected to any Edge in this
	 *         DirectionalGraph as a source Node; false otherwise
	 */
	public default boolean hasOutwardEdge(N node)
	{
		Collection<ET> adjacentEdgeList = getAdjacentEdges(node);
		if (adjacentEdgeList == null)
		{
			return false;
		}
		for (ET edge : adjacentEdgeList)
		{
			if (edge.isSource(node))
			{
				return true;
			}
		}
		return false;
	}

}
