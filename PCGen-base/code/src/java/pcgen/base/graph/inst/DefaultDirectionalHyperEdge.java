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
import java.util.Objects;

import pcgen.base.graph.base.DirectionalEdge;
import pcgen.base.graph.base.DirectionalHyperEdge;

/**
 * A DefaultDirectionalHyperEdge is a default implementation of a
 * DirectionalHyperEdge. As a HyperEdge, a DefaultDirectionalHyperEdge can be
 * connected to any non-zero number of GraphNodes. As a DiretionalEdge, each
 * Node connected to this DefaultDirectionalHyperEdge is identified as a source
 * Node, a sink Node or both.
 * 
 * A DefaultDirectionalHyperEdge is not required to have both source and sink
 * nodes. It is legal for a DefaultDirectionalHyperEdge to have only source
 * nodes (being a 'dangling leaf' on the Graph) or only sink nodes (being a form
 * of 'root' on the Graph)
 * 
 * @param <N>
 *            The type of Node stored in this Edge
 */
public class DefaultDirectionalHyperEdge<N> implements DirectionalHyperEdge<N>
{

	/**
	 * The List of source Nodes to which this DefaultDirectionalHyperEdge is
	 * connected.
	 * 
	 * In normal operation, this List must not be empty (should be enforced in
	 * object construction). This List may be null if the
	 * DefaultDirectionalHyperEdge has no source Nodes.
	 */
	private final List<N> sourceNodes;

	/**
	 * The List of sink Nodes to which this DefaultDirectionalHyperEdge is
	 * connected.
	 * 
	 * In normal operation, this List must not be null or empty (should be
	 * enforced in object construction).
	 */
	private final List<N> sinkNodes;

	/**
	 * Constructs a DefaultDirectionalHyperEdge with the given Nodes as source
	 * Nodes and Sink Nodes. Either parameter individually may be null or an
	 * empty Collection; the only restriction is that both Collections cannot be
	 * null or empty. (A DefaultDirectionalHyperEdge must connect to at least
	 * one Node)
	 * 
	 * @param sourceNode
	 *            The Collection of source Nodes for this
	 *            DefaultDirectionalHyperEdge.
	 * @param sinkNode
	 *            The Collection of source Nodes for this
	 *            DefaultDirectionalHyperEdge.
	 */
	public DefaultDirectionalHyperEdge(Collection<N> sourceNode,
		Collection<N> sinkNode)
	{
		super();
		if (sourceNode == null && sinkNode == null)
		{
			throw new IllegalArgumentException(
				"Both Collections to DefaultDirectionalGraphEdge cannot be null");
		}
		sourceNodes = setNodes(sourceNode);
		sinkNodes = setNodes(sinkNode);
		if (sourceNodes == null && sinkNodes == null)
		{
			throw new IllegalArgumentException(
				"GraphNode List of DefaultHyperEdge cannot be empty");
		}
	}

	/**
	 * Sets the nodes for this Edge (internal use).
	 * 
	 * @param nodes
	 *            The nodes to be set for this Edge
	 * @return The resulting List of nodes to be used/saved internally by this
	 *         Edge
	 */
	private List<N> setNodes(Collection<N> nodes)
	{
		if (nodes == null || nodes.isEmpty())
		{
			return null;
		}
		/*
		 * Copy before content check for thread safety
		 */
		List<N> returnList = new ArrayList<>(nodes.size());
		returnList.addAll(nodes);
		for (N node : returnList)
		{
			if (node == null)
			{
				throw new IllegalArgumentException("List contains null");
			}
		}
		return returnList;
	}

	/**
	 * Returns the Node at the given index.
	 */
	@Override
	public N getNodeAt(int index)
	{
		if (sourceNodes != null && index < sourceNodes.size())
		{
			return sourceNodes.get(index);
		}
		if (sinkNodes != null)
		{
			int sinkIndex =
					sourceNodes == null ? index : index - sourceNodes.size();
			return sinkNodes.get(sinkIndex);
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * Returns a List of the Nodes which are adjacent (connected) to this
	 * DefaultDirectionalHyperEdge.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by
	 * DefaultDirectionalHyperEdge. However, the Edges contained in the List are
	 * returned BY REFERENCE, and modification of the returned Edges will modify
	 * the Edges contained within the DefaultDirectionalHyperEdge.
	 */
	@Override
	public List<N> getAdjacentNodes()
	{
		ArrayList<N> returnList = new ArrayList<>(getAdjacentNodeCount());
		if (sourceNodes != null)
		{
			returnList.addAll(sourceNodes);
		}
		if (sinkNodes != null)
		{
			returnList.addAll(sinkNodes);
		}
		return returnList;
	}

	/**
	 * Returns true if the given Node is adjacent (connected) to this
	 * DefaultDirectionalHyperEdge.
	 */
	@Override
	public boolean isAdjacentNode(N node)
	{
		if (sourceNodes != null && sourceNodes.contains(node))
		{
			return true;
		}
		if (sinkNodes != null && sinkNodes.contains(node))
		{
			return true;
		}
		return false;
	}

	/*
	 * FIXME TODO This is DECEPTIVE, given that it does not return what one
	 * would expect to be able to use in getNodeAt :(
	 * 
	 * The question is accuracy vs. storage vs. ??? Should the List that is
	 * storing items here really store a special object that has a key for
	 * source/sink/both and the items? What does that mean for the speed of the
	 * get lists? Should the source/sink/both be a separate list/array?
	 */

	/**
	 * Returns a count of the number of adjacent (connected) Nodes to this
	 * DefaultDirectionalHyperEdge.
	 */
	@Override
	public int getAdjacentNodeCount()
	{
		if (sourceNodes == null)
		{
			return sinkNodes.size();
		}
		else if (sinkNodes == null)
		{
			return sourceNodes.size();
		}
		// Neither is null
		int size = sourceNodes.size();
		for (N node : sinkNodes)
		{
			if (!sourceNodes.contains(node))
			{
				size++;
			}
		}
		return size;
	}

	/**
	 * Returns a bitmask indicating the interface type of the given Node with
	 * respect to this DefaultDirectionalHyperEdge.
	 */
	@Override
	public int getNodeInterfaceType(N node)
	{
		int type = 0;
		if (sourceNodes != null && sourceNodes.contains(node))
		{
			type |= DirectionalEdge.SOURCE;
		}
		if (sinkNodes != null && sinkNodes.contains(node))
		{
			type |= DirectionalEdge.SINK;
		}
		return type;
	}

	/**
	 * Returns a List of the sink Nodes of this DefaultDirectionalHyperEdge.
	 * Will return null if there are no sink Nodes.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by
	 * DefaultDirectionalHyperEdge. However, the Edges contained in the List are
	 * returned BY REFERENCE, and modification of the returned Edges will modify
	 * the Edges contained within the DefaultDirectionalHyperEdge.
	 */
	@Override
	public List<N> getSinkNodes()
	{
		return sinkNodes == null ? null : new ArrayList<>(sinkNodes);
	}

	/**
	 * Returns a List of the source Nodes of this DefaultDirectionalHyperEdge.
	 * Will return null if there are no source Nodes.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by
	 * DefaultDirectionalHyperEdge. However, the Nodes contained in the List are
	 * returned BY REFERENCE, and modification of the returned Nodes will modify
	 * the Nodes contained within the DefaultDirectionalHyperEdge.
	 */
	@Override
	public List<N> getSourceNodes()
	{
		return sourceNodes == null ? null : new ArrayList<>(sourceNodes);
	}

	/**
	 * Creates a replacement DefaultDirectionalHyperEdge with the given Nodes as
	 * source Nodes and Sink Nodes. Either parameter individually may be null or
	 * an empty Collection; the only restriction is that both Collections cannot
	 * be null or empty. (A DefaultDirectionalHyperEdge must connect to at least
	 * one Node)
	 */
	@Override
	public DefaultDirectionalHyperEdge<N> createReplacementEdge(
		Collection<N> newSourceNodes, Collection<N> newSinkNodes)
	{
		return new DefaultDirectionalHyperEdge<>(
				Objects.requireNonNull(newSourceNodes),
				Objects.requireNonNull(newSinkNodes)
		);
	}
}
