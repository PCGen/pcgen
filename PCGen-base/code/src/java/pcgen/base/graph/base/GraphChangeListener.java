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
 * Created on Aug 27, 2004
 */
package pcgen.base.graph.base;

import java.util.EventListener;

/**
 * The listener interface for receiving EdgeChangeEvents and NodeChangeEvents.
 * When an Edge or Node has been added to or removed from a Graph, the
 * respective method in the listener object is invoked, and the EdgeChangeEvent
 * or NodeChangeEvent is passed to it.
 * 
 * @param <N>
 *            The type of Node stored in the Graph to which this Listener will
 *            listen
 * @param <ET>
 *            The type of Edge stored in the Graph to which this Listener will
 *            listen
 */
public interface GraphChangeListener<N, ET extends Edge<N>> extends
		EventListener
{
	/**
	 * Method called when a Node has been added to a Graph and this
	 * GraphChangeListener has been added as a GraphChangeListener to the source
	 * Graph.
	 * 
	 * @param event
	 *            The NodeChangeEvent that occurred.
	 */
	public void nodeAdded(NodeChangeEvent<N> event);

	/**
	 * Method called when a Node has been removed from a Graph and this
	 * GraphChangeListener has been added as a GraphChangeListener to the source
	 * Graph.
	 * 
	 * @param event
	 *            The NodeChangeEvent that occurred.
	 */
	public void nodeRemoved(NodeChangeEvent<N> event);

	/**
	 * Method called when an Edge has been added to a Graph and this
	 * GraphChangeListener has been added as a GraphChangeListener to the source
	 * Graph.
	 * 
	 * @param event
	 *            The EdgeChangeEvent that occurred.
	 */
	public void edgeAdded(EdgeChangeEvent<N, ET> event);

	/**
	 * Method called when an Edge has been removed from a Graph and this
	 * GraphChangeListener has been added as a GraphChangeListener to the source
	 * Graph.
	 * 
	 * @param event
	 *            The EdgeChangeEvent that occurred.
	 */
	public void edgeRemoved(EdgeChangeEvent<N, ET> event);
}
