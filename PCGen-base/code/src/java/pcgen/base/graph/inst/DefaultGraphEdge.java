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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import pcgen.base.graph.base.GraphEdge;

/**
 * Represents a default implementation of GraphEdge. A GraphEdge is an edge of a
 * Graph which is connected to two (and only two) GraphNodes.
 * 
 * A DefaultGraphEdge is not a DirectionalEdge.
 * 
 * @param <N>
 *            The type of Node stored in this Edge
 */
public class DefaultGraphEdge<N> implements GraphEdge<N>
{

	/**
	 * One GraphNode to which this DefaultGraphEdge is connected. This GraphNode
	 * is not referred to as either the source or sink, as a DefaultGraphEdge is
	 * not a DirectionalHyperEdge.
	 */
	private final N firstNode;

	/**
	 * The second GraphNode to which this DefaultGraphEdge is connected. This
	 * GraphNode is not referred to as either the source or sink, as a
	 * DefaultGraphEdge is not a DirectionalHyperEdge.
	 */
	private final N secondNode;

	/**
	 * Creates a new DefaultGraphEdge which is connected to the given Nodes.
	 * 
	 * @param node1
	 *            The first Node to which this DefaultGraphEdge is connected
	 * @param node2
	 *            The second Node to which this DefaultGraphEdge is connected
	 */
	public DefaultGraphEdge(N node1, N node2)
	{
		super();
		firstNode = Objects.requireNonNull(node1);
		secondNode = Objects.requireNonNull(node2);
	}

	/**
	 * Returns the node at the given index.
	 */
	@Override
	public N getNodeAt(int index)
	{
		if (index == 0)
		{
			return firstNode;
		}
		else if (index == 1)
		{
			return secondNode;
		}
		else
		{
			throw new IndexOutOfBoundsException(
				"GraphEdge does not contain a Node at " + index);
		}
	}

	/**
	 * Returns the Node attached to this DefaultGraphEdge opposite of the given
	 * Node. Returns null if the given Node is not adjacent (connected) to this
	 * DefaultGraphEdge.
	 */
	@Override
	public N getOppositeNode(N node)
	{
		if (firstNode.equals(node))
		{
			return secondNode;
		}
		else if (secondNode.equals(node))
		{
			return firstNode;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Creates a replacement DefaultGraphEdge for this DefaultGraphEdge, with
	 * the replacement connected to the two given Nodes.
	 */
	@Override
	public DefaultGraphEdge<N> createReplacementEdge(N node1, N node2)
	{
		return new DefaultGraphEdge<>(node1, node2);
	}

	/**
	 * Returns the List of Adjacent (connected) Nodes to this DefaultGraphEdge.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by DefaultGraphEdge. However,
	 * the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the nodes contained within
	 * the DefaultGraphEdge.
	 */
	@Override
	public List<N> getAdjacentNodes()
	{
		List<N> l = new LinkedList<>();
		l.add(firstNode);
		l.add(secondNode);
		return l;
	}

	/**
	 * Returns true if the given Node is adjacent (connected) to this
	 * DefaultGraphEdge; false otherwise.
	 */
	@Override
	public boolean isAdjacentNode(N node)
	{
		return firstNode.equals(node) || secondNode.equals(node);
	}

	/**
	 * Returns 2: the number of Nodes to which this DefaultGraphEdge is
	 * connected.
	 */
	@Override
	public int getAdjacentNodeCount()
	{
		return 2;
	}
}
