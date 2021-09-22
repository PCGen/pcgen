/*
 * Copyright (c) Thomas Parker, 2005-2007.
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
 * Created on Jan 8, 2005
 */
package pcgen.base.graph.base;

import java.util.List;

/**
 * A DirectionalEdge is a directional Edge in a Graph. A directional edge has
 * source and sink nodes. It is possible on a DirectionalEdge to have multiple
 * sources and/or multiple sinks.
 * 
 * @param <N>
 *            The type of Node stored in this Edge
 */
public interface DirectionalEdge<N> extends Edge<N>
{

	/**
	 * The bitmask for identifying when a Node is unconnected to a given
	 * DirectionalEdge.
	 */
	public static final int UNCONNECTED = 0;

	/**
	 * The bitmask for identifying when a Node is a source of a given
	 * DirectionalEdge.
	 */
	public static final int SOURCE = 1;

	/**
	 * The bitmask for identifying when a Node is a sink of a given
	 * DirectionalEdge.
	 */
	public static final int SINK = 2;

	/**
	 * Returns true if the given Node is a source for this DirectionalEdge.
	 * 
	 * @param node
	 *            The node to be checked if it is a source for this DirectionalEdge.
	 * @return true if the given Node is a source for this DirectionalEdge; false
	 *         otherwise
	 */
	public boolean isSource(N node);

	/**
	 * Returns true if the given Node is a sink for this DirectionalEdge.
	 * 
	 * @param node
	 *            The node to be checked if it is a sink for this DirectionalEdge.
	 * @return true if the given Node is a sink for this DirectionalEdge; false
	 *         otherwise
	 */
	public boolean isSink(N node);

	/**
	 * Returns a List of the Source Nodes of this DirectionalEdge.
	 * 
	 * Ownership of the returned List must be transferred to the calling Object. No
	 * reference to the List Object may be maintained by the DirectionalEdge. However, the
	 * Nodes contained in the List are returned BY REFERENCE, and modification of the
	 * returned Nodes will modify the Nodes contained within the DirectionalEdge.
	 * 
	 * @return A List of the Source Nodes of this DirectionalEdge
	 */
	public List<N> getSourceNodes();

	/**
	 * Returns a List of the Sink Nodes of this DirectionalEdge.
	 * 
	 * Ownership of the returned List must be transferred to the calling Object. No
	 * reference to the List Object may be maintained by the DirectionalEdge. However, the
	 * Nodes contained in the List are returned BY REFERENCE, and modification of the
	 * returned Nodes will modify the Nodes contained within the DirectionalEdge.
	 * 
	 * @return A List of the Sink Nodes of this DirectionalEdge
	 */
	public List<N> getSinkNodes();
	
	/**
	 * Returns an identifier indicating the type of interface between this
	 * DirectionalEdge and the given Node. Returns 0 (zero) if this
	 * DirectionalEdge is not attached to the given Node. If attached, the
	 * return value is a Bitmask of DirectionalEdge.SOURCE and
	 * DirectionalEdge.SINK to indicate whether the given Node was a Source, a
	 * Sink, or both.
	 * 
	 * @param node
	 *            The node for which the interface type should be returned
	 * @return A bitmask indicating the interface type between this
	 *         DirectionalEdge and the given Node
	 */
	public default int getNodeInterfaceType(N node)
	{
		int interfaceType = DirectionalEdge.UNCONNECTED;
		if (isSource(node))
		{
			interfaceType |= DirectionalEdge.SOURCE;
		}
		if (isSink(node))
		{
			interfaceType |= DirectionalEdge.SINK;
		}
		return interfaceType;
	}

}
