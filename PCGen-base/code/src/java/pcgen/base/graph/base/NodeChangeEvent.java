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
 * Created on Aug 28, 2004
 */
package pcgen.base.graph.base;

import java.util.EventObject;
import java.util.Objects;

/**
 * An NodeChangeEvent is an event that indicates when the presence of a
 * GraphNode within a Graph has changed. The node could have been added to or
 * removed from the Graph.
 * 
 * The object that implements the GraphChangeListener interface gets this
 * NodeChangeEvent when the event occurs.
 * 
 * NOTE: This Object is reference-semantic. It carries references to both the
 * source object (presumably the Graph) and the affected GraphNode. Use of this
 * Event does not provide protection from mutability for those Objects by
 * listeners. NodeChangeEvent, however, makes the guarantee that no
 * modifications are made by NodeChangeEvent to either the given event source or
 * the GraphNode.
 * 
 * @param <N>
 *            The type of Node this Event is identifying
 */
public class NodeChangeEvent<N> extends EventObject
{

	/**
	 * The constant ID used by an NodeChangeEvent to indicate that a
	 * NodeChangeEvent was the result of a GraphNode being added to a Graph.
	 */
	public static final int NODE_ADDED = 0;

	/**
	 * The constant ID used by an NodeChangeEvent to indicate that a
	 * NodeChangeEvent was the result of a GraphNode being removed from a Graph.
	 */
	public static final int NODE_REMOVED = 1;

	/**
	 * The ID indicating the type of this NodeChangeEvent (addition to or
	 * removal from a Graph).
	 */
	private final int eventID;

	/**
	 * The GraphNode that was added to or removed from the Graph.
	 */
	private final N node;

	/**
	 * Constructs a new NodeChangeEvent that occurred in the given Graph. The
	 * Node which was added or removed and an indication of the action (Addition
	 * or Removal) is also provided.
	 * 
	 * @param graph
	 *            The Graph in which this NodeChangeEvent took place
	 * @param node
	 *            The Node which was added to or removed from the Graph
	 * @param id
	 *            An integer identifying whether the given Node was added or
	 *            removed from the Graph
	 */
	public NodeChangeEvent(Graph<N, ?> graph, N node, int id)
	{
		super(graph);
		this.node = Objects.requireNonNull(node);
		eventID = id;
	}

	/**
	 * Returns the Node which was added to or removed from the Graph.
	 * 
	 * @return The Node which was added to or removed from the Graph
	 */
	public N getGraphNode()
	{
		return node;
	}

	/**
	 * Returns an identifier indicating if the Node returned by getGraphNode()
	 * was added to or removed from the Graph. This identifier is either
	 * NodeChangeEvent.NODE_ADDED or NodeChangeEvent.NODE_REMOVED
	 * 
	 * @return A identifier indicating if the Node was added to or removed from
	 *         the Graph
	 */
	public int getID()
	{
		return eventID;
	}
}
