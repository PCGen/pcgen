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

import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 *         Represents an implementation of a Directional GraphEdge where the
 *         nodes are compared by identity [== not .equals()]. A GraphEdge is an
 *         edge of a Graph which is connected to two (and only two) GraphNodes.
 */
public class IdentityDirectionalGraphEdge<N> extends DefaultGraphEdge<N>
		implements DirectionalEdge<N>
{

	/**
	 * Creates a new IdentityDirectionalGraphEdge which is connected to the
	 * given Nodes.
	 * 
	 * @param node1
	 *            The first Node (the source) to which this
	 *            IdentityDirectionalGraphEdge is connected
	 * @param node2
	 *            The second Node (the sink) to which this
	 *            IdentityDirectionalGraphEdge is connected
	 */
	public IdentityDirectionalGraphEdge(N node1, N node2)
	{
		super(node1, node2);
	}

	/**
	 * Creates a replacement IdentityDirectionalGraphEdge for this
	 * IdentityDirectionalGraphEdge, with the replacement connected to the two
	 * given Nodes.
	 * 
	 * @see pcgen.base.graph.core.GraphEdge#createReplacementEdge(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public IdentityDirectionalGraphEdge<N> createReplacementEdge(N gn1, N gn2)
	{
		return new IdentityDirectionalGraphEdge<N>(gn1, gn2);
	}

	/**
	 * Returns a bitmask indicating the interface type of the given Node with
	 * respect to this IdentityDirectionalGraphEdge. Comparison of the node to
	 * the ends of this edge are done with an identity comparision (==) not an
	 * equality comparison (.equals())
	 * 
	 * @see pcgen.base.graph.core.DirectionalEdge#getNodeInterfaceType(java.lang.Object)
	 */
	@Override
	public int getNodeInterfaceType(N node)
	{
		int interfaceType = DirectionalEdge.UNCONNECTED;
		if (getNodeAt(0) == node)
		{
			interfaceType |= DirectionalEdge.SOURCE;
		}
		if (getNodeAt(1) == node)
		{
			interfaceType |= DirectionalEdge.SINK;
		}
		return interfaceType;
	}

	/**
	 * Returns a List of the source Nodes of this IdentityDirectionalGraphEdge.
	 * Will always return a List of length one containing only the second Node.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by
	 * IdentityDirectionalGraphEdge. However, the Nodes contained in the List
	 * are returned BY REFERENCE, and modification of the returned Nodes will
	 * modify the Nodes contained within the IdentityDirectionalGraphEdge.
	 * 
	 * @see pcgen.base.graph.core.DirectionalEdge#getSourceNodes()
	 */
	@Override
	public List<N> getSinkNodes()
	{
		return Collections.singletonList(getNodeAt(1));
	}

	/**
	 * Returns a List of the source Nodes of this IdentityDirectionalGraphEdge.
	 * Will always return a List of length one containing only the first Node.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by
	 * IdentityDirectionalGraphEdge. However, the Nodes contained in the List
	 * are returned BY REFERENCE, and modification of the returned Nodes will
	 * modify the Nodes contained within the IdentityDirectionalGraphEdge.
	 * 
	 * @see pcgen.base.graph.core.DirectionalEdge#getSourceNodes()
	 */
	@Override
	public List<N> getSourceNodes()
	{
		return Collections.singletonList(getNodeAt(0));
	}

}