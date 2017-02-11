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
package pcgen.base.graph.inst;

import java.util.Objects;

import javax.swing.event.EventListenerList;

import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.EdgeChangeEvent;
import pcgen.base.graph.base.Graph;
import pcgen.base.graph.base.GraphChangeListener;
import pcgen.base.graph.base.NodeChangeEvent;

/**
 * A GraphChangeSupport object is an object which is designed to assist in
 * monitoring changes to a Graph. This class serves a similar purpose to
 * PropertyChangeSupport (in the java.beans package of Java), but in this case,
 * is facilitating listening to and throwing GraphNodeEvents and
 * GraphEdgeEvents.
 * 
 * @param <N>
 *            The type of Node stored in the Graph supported by this
 *            GraphChangeSupport
 * @param <ET>
 *            The type of Edge stored in the Graph supported by this
 *            GraphChangeSupport
 */
public class GraphChangeSupport<N, ET extends Edge<N>>
{

	/**
	 * The listeners to which GrapeNodeEvents and GraphEdgeEvents will be fired
	 * when a change in the source Graph occurs.
	 */
	private final EventListenerList listenerList;

	/**
	 * The source graph to monitor for changes.
	 */
	private final Graph<N, ET> source;

	/**
	 * Creates a new GraphChangeSupport object for use in supporting the given
	 * Graph.
	 * 
	 * @param sourceObject
	 *            The Graph to be used as the source of events.
	 */
	public GraphChangeSupport(Graph<N, ET> sourceObject)
	{
		super();
		source = Objects.requireNonNull(sourceObject);
		listenerList = new EventListenerList();
	}

	/**
	 * Adds a new GraphChangeListener to receive GraphChangeEvents
	 * (EdgeChangeEvent and NodeChangeEvent) from the source Graph.
	 * 
	 * @param listener
	 *            The GraphChangeListener to receive GraphChangeEvents
	 */
	public void addGraphChangeListener(GraphChangeListener<N, ET> listener)
	{
		listenerList.add(GraphChangeListener.class, listener);
	}

	/**
	 * Returns an Array of GraphChangeListeners receiving Graph Change Events
	 * from the source Graph.
	 * 
	 * Ownership of the returned Array is transferred to the calling Object. No
	 * reference to the Array is maintained by GraphChangeSupport. However, the
	 * GraphChangeListeners contained in the Array are (obviously!) returned BY
	 * REFERENCE, and care should be taken with modifying those
	 * GraphChangeListeners. F *
	 * 
	 * @return An Array of GraphChangeListeners receiving Graph Change Events
	 *         from the source Graph
	 */
	@SuppressWarnings("unchecked")
	public synchronized GraphChangeListener<N, ET>[] getGraphChangeListeners()
	{
		return listenerList.getListeners(GraphChangeListener.class);
	}

	/**
	 * Removes a GraphChangeListener so that it will no longer receive Graph
	 * Change Events from the source Graph.
	 * 
	 * @param listener
	 *            The GraphChangeListener to be removed
	 */
	public void removeGraphChangeListener(GraphChangeListener<N, ET> listener)
	{
		listenerList.remove(GraphChangeListener.class, listener);
	}

	/**
	 * Sends an EdgeChangeEvent to the GraphChangeListeners that are receiving
	 * Graph Change Events from the source Graph.
	 * 
	 * @param edge
	 *            The Edge that has beed added to or removed from the source
	 *            Graph
	 * @param id
	 *            An identifier indicating whether the given Edge was added to
	 *            or removed from the source Graph
	 */
	@SuppressWarnings({"rawtypes", "unchecked", "PMD.AvoidInstantiatingObjectsInLoops"})
	public void fireGraphEdgeChangeEvent(ET edge, int id)
	{
		GraphChangeListener[] listeners =
				listenerList.getListeners(GraphChangeListener.class);
		/*
		 * This list is decremented from the end of the list to the beginning in
		 * order to maintain consistent operation with how Java AWT and Swing
		 * listeners are notified of Events (they are in reverse order to how
		 * they were added to the Event-owning object).
		 */
		EdgeChangeEvent<N, ET> ccEvent = null;
		for (int i = listeners.length - 1; i >= 0; i--)
		{
			// Lazily create event
			if (ccEvent == null)
			{
				ccEvent = new EdgeChangeEvent<>(source, edge, id);
			}
			switch (ccEvent.getID())
			{
				case EdgeChangeEvent.EDGE_ADDED:
					listeners[i].edgeAdded(ccEvent);
					break;
				case EdgeChangeEvent.EDGE_REMOVED:
					listeners[i].edgeRemoved(ccEvent);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Sends a NodeChangeEvent to the GraphChangeListeners that are receiving
	 * Graph Change Events from the source Graph.
	 * 
	 * @param node
	 *            The Node that has beed added to or removed from the source
	 *            Graph
	 * @param id
	 *            An identifier indicating whether the given Node was added to
	 *            or removed from the source Graph
	 */
	@SuppressWarnings({"rawtypes", "unchecked", "PMD.AvoidInstantiatingObjectsInLoops"})
	public void fireGraphNodeChangeEvent(N node, int id)
	{
		GraphChangeListener[] listeners =
				listenerList.getListeners(GraphChangeListener.class);
		/*
		 * This list is decremented from the end of the list to the beginning in
		 * order to maintain consistent operation with how Java AWT and Swing
		 * listeners are notified of Events (they are in reverse order to how
		 * they were added to the Event-owning object).
		 */
		NodeChangeEvent<N> ccEvent = null;
		for (int i = listeners.length - 1; i >= 0; i--)
		{
			// Lazily create event
			if (ccEvent == null)
			{
				ccEvent = new NodeChangeEvent<>(source, node, id);
			}
			switch (ccEvent.getID())
			{
				case NodeChangeEvent.NODE_ADDED:
					listeners[i].nodeAdded(ccEvent);
					break;
				case NodeChangeEvent.NODE_REMOVED:
					listeners[i].nodeRemoved(ccEvent);
					break;
				default:
					break;
			}
		}
	}
}
