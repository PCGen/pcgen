/*
 * Copyright (c) Thomas Parker, 2004, 2005.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.graph.testsupport;

import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.EdgeChangeEvent;
import pcgen.base.graph.base.GraphChangeListener;
import pcgen.base.graph.base.NodeChangeEvent;

/**
 * A "transparent" GraphChangeListener that is available for tests to use to diagnose
 * other classes
 */
public class TestGraphChangeListener<T, ET extends Edge<T>> implements
		GraphChangeListener<T, ET>
{

	/**
	 * Last Added Node, public for diagnostics
	 */
	public T lastAddNode;

	/**
	 * Last Added edge, public for diagnostics
	 */
	public ET lastAddEdge;

	/**
	 * Last Removed Node, public for diagnostics
	 */
	public T lastRemoveNode;

	/**
	 * Last Removed Edge, public for diagnostics
	 */
	public ET lastRemoveEdge;

	/**
	 * Added Node Count, public for diagnostics
	 */
	public int nodeCount = 0;

	/**
	 * Added Edge Count, public for diagnostics
	 */
	public int edgeCount = 0;

	@Override
	public void nodeAdded(NodeChangeEvent<T> gce)
	{
		lastAddNode = gce.getGraphNode();
		nodeCount++;
	}

	@Override
	public void nodeRemoved(NodeChangeEvent<T> gce)
	{
		lastRemoveNode = gce.getGraphNode();
		nodeCount++;
	}

	@Override
	public void edgeAdded(EdgeChangeEvent<T, ET> gce)
	{
		lastAddEdge = gce.getGraphEdge();
		edgeCount++;
	}

	@Override
	public void edgeRemoved(EdgeChangeEvent<T, ET> gce)
	{
		lastRemoveEdge = gce.getGraphEdge();
		edgeCount++;
	}
}
