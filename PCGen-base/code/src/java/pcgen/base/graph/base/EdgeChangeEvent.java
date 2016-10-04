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
 * An EdgeChangeEvent is an event that indicates when the presence of an Edge
 * within a Graph has changed. The edge could have been added to or removed from
 * the Graph.
 * 
 * The object that implements the GraphChangeListener interface gets this
 * EdgeChangeEvent when the event occurs.
 * 
 * NOTE: This Object is reference-semantic. It carries references to both the
 * source object (presumably the Graph) and the affected Edge. Use of this Event
 * does not provide protection from mutability for those Objects by listeners.
 * EdgeChangeEvent, however, makes the guarantee that no modifications are made
 * by EdgeChangeEvent to either the given event source or the Edge.
 * 
 * @param <N>
 *            The type of Node contained in the Edge about which this Event is
 *            reporting
 * @param <ET>
 *            The type of Edge about which this Event is reporting
 */
public class EdgeChangeEvent<N, ET extends Edge<N>> extends EventObject
{

	/**
	 * The constant ID used by an EdgeChangeEvent to indicate that an
	 * EdgeChangeEvent was the result of an Edge being added to a Graph.
	 */
	public static final int EDGE_ADDED = 0;

	/**
	 * The constant ID used by an EdgeChangeEvent to indicate that an
	 * EdgeChangeEvent was the result of an Edge being removed from a Graph.
	 */
	public static final int EDGE_REMOVED = 1;

	/**
	 * The ID indicating the type of this EdgeChangeEvent (addition to or
	 * removal from a Graph).
	 */
	private final int eventID;

	/**
	 * The Edge which was added to or removed from the Graph.
	 */
	private final ET edge;

	/**
	 * Constructs a new EdgeChangeEvent that occurred in the given Graph. The
	 * Edge which was added or removed and an indication of the action (Addition
	 * or Removal) is also provided.
	 * 
	 * @param graph
	 *            The Graph in which this EdgeChangeEvent took place
	 * @param edge
	 *            The Edge which was added to or removed from the Graph
	 * @param id
	 *            An integer identifying whether the given Edge was added or
	 *            removed from the Graph
	 */
	public EdgeChangeEvent(Graph<N, ET> graph, ET edge, int id)
	{
		super(graph);
		this.edge = Objects.requireNonNull(edge);
		eventID = id;
	}

	/**
	 * Returns the Edge which was added to or removed from the Graph.
	 * 
	 * @return The Edge which was added to or removed from the Graph
	 */
	public ET getGraphEdge()
	{
		return edge;
	}

	/**
	 * Returns an identifier indicating if the Edge returned by getGraphEdge()
	 * was added to or removed from the Graph. This identifier is either
	 * EdgeChangeEvent.EDGE_ADDED or EdgeChangeEvent.EDGE_REMOVED
	 * 
	 * @return A identifier indicating if the Edge was added to or removed from
	 *         the Graph
	 */
	public int getID()
	{
		return eventID;
	}
}
