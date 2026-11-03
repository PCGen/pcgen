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

import pcgen.base.graph.base.DirectionalGraphEdge;

/**
 * Represents a default implementation of a Directional GraphEdge. A GraphEdge
 * is an edge of a Graph which is connected to two (and only two) GraphNodes.
 * 
 * @param <N>
 *            The type of Node stored in this Edge
 */
public class DefaultDirectionalGraphEdge<N> extends DefaultGraphEdge<N>
		implements DirectionalGraphEdge<N>
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

	@Override
	public DefaultDirectionalGraphEdge<N> createReplacementEdge(N gn1, N gn2)
	{
		return new DefaultDirectionalGraphEdge<>(gn1, gn2);
	}

	@Override
	public String toString()
	{
		return "Edge: " + getNodeAt(0) + " -> " + getNodeAt(1);
	}
}
