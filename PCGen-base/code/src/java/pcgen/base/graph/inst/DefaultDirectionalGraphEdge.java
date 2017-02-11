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

import java.util.Collections;
import java.util.List;

import pcgen.base.graph.base.DirectionalEdge;

/**
 * Represents a default implementation of a Directional GraphEdge. A GraphEdge
 * is an edge of a Graph which is connected to two (and only two) GraphNodes.
 * 
 * @param <N>
 *            The type of Node stored in this Edge
 */
public class DefaultDirectionalGraphEdge<N> extends DefaultGraphEdge<N>
		implements DirectionalEdge<N>
{

	/**
	 * Creates a new DefaultDirectionalGraphEdge which is connected to the given
	 * Nodes.
	 * 
	 * @param node1
	 *            The first Node (the source) to which this
	 *            DefaultDirectionalGraphEdge is connected
	 * @param node2
	 *            The second Node (the sink) to which this
	 *            DefaultDirectionalGraphEdge is connected
	 */
	public DefaultDirectionalGraphEdge(N node1, N node2)
	{
		super(node1, node2);
	}

	/**
	 * Creates a replacement DefaultDirectionalGraphEdge for this
	 * DefaultDirectionalGraphEdge, with the replacement connected to the two
	 * given Nodes.
	 */
	@Override
	public DefaultDirectionalGraphEdge<N> createReplacementEdge(N gn1, N gn2)
	{
		return new DefaultDirectionalGraphEdge<>(gn1, gn2);
	}

	/**
	 * Returns a bitmask indicating the interface type of the given Node with
	 * respect to this DefaultDirectionalGraphEdge.
	 */
	@Override
	public int getNodeInterfaceType(N node)
	{
		int interfaceType = DirectionalEdge.UNCONNECTED;
		if (getNodeAt(0).equals(node))
		{
			interfaceType |= DirectionalEdge.SOURCE;
		}
		if (getNodeAt(1).equals(node))
		{
			interfaceType |= DirectionalEdge.SINK;
		}
		return interfaceType;
	}

	/**
	 * Returns a List of the source Nodes of this DefaultDirectionalGraphEdge.
	 * Will always return a List of length one containing only the second Node.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by
	 * DefaultDirectionalGraphEdge. However, the Nodes contained in the List are
	 * returned BY REFERENCE, and modification of the returned Nodes will modify
	 * the Nodes contained within the DefaultDirectionalGraphEdge.
	 */
	@Override
	public List<N> getSinkNodes()
	{
		return Collections.singletonList(getNodeAt(1));
	}

	/**
	 * Returns a List of the source Nodes of this DefaultDirectionalGraphEdge.
	 * Will always return a List of length one containing only the first Node.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by
	 * DefaultDirectionalGraphEdge. However, the Nodes contained in the List are
	 * returned BY REFERENCE, and modification of the returned Nodes will modify
	 * the Nodes contained within the DefaultDirectionalGraphEdge.
	 */
	@Override
	public List<N> getSourceNodes()
	{
		return Collections.singletonList(getNodeAt(0));
	}

	@Override
	public String toString()
	{
		return "Edge: " + getNodeAt(0) + " -> " + getNodeAt(1);
	}
}
