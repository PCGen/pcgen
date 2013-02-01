/*
 * Copyright (c) Thomas Parker, 2004-2013.
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
package pcgen.base.graph.core;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * Represents a default implementation of GraphEdge. A GraphEdge is an edge of a
 * Graph which is connected to two (and only two) GraphNodes.
 * 
 * A DefaultGraphEdge is not a DirectionalEdge.
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
		if (node1 == null)
		{
			throw new IllegalArgumentException(
				"(First) GraphNode of DefaultGraphEdge cannot be null");
		}
		if (node2 == null)
		{
			throw new IllegalArgumentException(
				"(Second) GraphNode of DefaultGraphEdge cannot be null");
		}
		firstNode = node1;
		secondNode = node2;
	}

	/**
	 * Returns the node at the given index.
	 * 
	 * @see pcgen.base.graph.core.Edge#getNodeAt(int)
	 */
	@Override
	public N getNodeAt(int i)
	{
		if (i == 0)
		{
			return firstNode;
		}
		else if (i == 1)
		{
			return secondNode;
		}
		else
		{
			throw new IndexOutOfBoundsException(
				"GraphEdge does not contain a Node at " + i);
		}
	}

	/**
	 * Returns the Node attached to this DefaultGraphEdge opposite of the given
	 * Node. Returns null if the given Node is not adjacent (connected) to this
	 * DefaultGraphEdge.
	 * 
	 * @see pcgen.base.graph.core.GraphEdge#getOppositeNode(java.lang.Object)
	 */
	@Override
	public N getOppositeNode(N gn)
	{
		if (firstNode.equals(gn))
		{
			return secondNode;
		}
		else if (secondNode.equals(gn))
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
	 * 
	 * @see pcgen.base.graph.core.GraphEdge#createReplacementEdge(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public DefaultGraphEdge<N> createReplacementEdge(N gn1, N gn2)
	{
		return new DefaultGraphEdge<N>(gn1, gn2);
	}

	/**
	 * Returns the List of Adjacent (connected) Nodes to this DefaultGraphEdge.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by DefaultGraphEdge. However,
	 * the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the nodes contained within
	 * the DefaultGraphEdge.
	 * 
	 * @see pcgen.base.graph.core.Edge#getAdjacentNodes()
	 */
	@Override
	public List<N> getAdjacentNodes()
	{
		List<N> l = new LinkedList<N>();
		l.add(firstNode);
		l.add(secondNode);
		return l;
	}

	/**
	 * Returns true if the given Node is adjacent (connected) to this
	 * DefaultGraphEdge; false otherwise.
	 * 
	 * @see pcgen.base.graph.core.Edge#isAdjacentNode(java.lang.Object)
	 */
	@Override
	public boolean isAdjacentNode(N gn)
	{
		return firstNode.equals(gn) || secondNode.equals(gn);
	}

	/**
	 * Returns 2: the number of Nodes to which this DefaultGraphEdge is
	 * connected.
	 * 
	 * @see pcgen.base.graph.core.Edge#getAdjacentNodeCount()
	 */
	@Override
	public int getAdjacentNodeCount()
	{
		return 2;
	}
}