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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.graph.base.NonDirectionalEdge;
import pcgen.base.util.ListUtilities;

/**
 * A DefaultHyperEdge is a default implementation of a non-directional
 * HyperEdge. As a HyperEdge, a DefaultHyperEdge can be connected to any
 * non-zero number of GraphNodes.
 * 
 * @param <N>
 *            The type of Node stored in this Edge
 */
public class DefaultHyperEdge<N> implements NonDirectionalEdge<N>
{

	/**
	 * The array of GraphNodes to which this DefaultHyperEdge is connected.
	 * These GraphNodes are not identified with any direction, as the
	 * DefaultHyperEdge is not a DirectionalEdge.
	 * 
	 * In normal operation, this array must not be null or empty (should be
	 * enforced in object construction).
	 */
	private final List<N> nodes;

	/**
	 * Creates a new DefaultHyperEdge connected to the Nodes in the given
	 * Collection. The Collection must not be empty or null.
	 * 
	 * @param nodes
	 *            The Collection of Nodes to which this DefaultHyperEdge is
	 *            connected
	 */
	public DefaultHyperEdge(Collection<N> nodes)
	{
		super();
		/*
		 * Copy before empty check for thread safety
		 */
		this.nodes = new ArrayList<>(nodes.size());
		this.nodes.addAll(nodes);
		if (this.nodes.isEmpty())
		{
			throw new IllegalArgumentException(
				"GraphNode List of DefaultHyperEdge cannot be empty");
		}
		if (ListUtilities.containsNull(this.nodes))
		{
			throw new IllegalArgumentException("Node List contains null");
		}
	}

	@Override
	public N getNodeAt(int index)
	{
		return nodes.get(index);
	}

	@Override
	public List<N> getAdjacentNodes()
	{
		return new ArrayList<>(nodes);
	}

	@Override
	public boolean isAdjacentNode(N node)
	{
		return nodes.contains(node);
	}

	@Override
	public int getAdjacentNodeCount()
	{
		/*
		 * CONSIDER This isn't ENTIRELY true, if this edge is connected to the
		 * same node more than once... what precisely should that corner case
		 * do? - thpr 11/20/06
		 */
		return nodes.size();
	}

	@Override
	public DefaultHyperEdge<N> createReplacementEdge(Collection<N> newNodes)
	{
		return new DefaultHyperEdge<>(newNodes);
	}
}
