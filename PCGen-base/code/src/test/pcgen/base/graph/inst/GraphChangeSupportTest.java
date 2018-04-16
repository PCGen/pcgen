/*
 * Copyright (c) Thomas Parker, 2004, 2005.
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
 */
package pcgen.base.graph.inst;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.EdgeChangeEvent;
import pcgen.base.graph.base.Graph;
import pcgen.base.graph.base.GraphChangeListener;
import pcgen.base.graph.base.NodeChangeEvent;

/**
 * Test the GraphChangeSupport class
 */
public class GraphChangeSupportTest extends TestCase
{

	private GraphChangeSupport<Object, Edge<Object>> support;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		Graph<Object, Edge<Object>> source = new SimpleListGraph<Object, Edge<Object>>();
		support = new GraphChangeSupport<>(source);
	}

	@SuppressWarnings("unused")
	public void testGraphChangeSupport()
	{
		try
		{
			new GraphChangeSupport<>(null);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException npe)
		{
			//We expect this
		}
	}

	public void testAddGraphChangeListener()
	{
		try
		{
			support.addGraphChangeListener(null);
			//If silent, we need to ensure not captured
			assertEquals(0, support.getGraphChangeListeners().length);
		}
		catch (IllegalArgumentException npe)
		{
			//This is okay
		}
	}

	public void testGraphChangeListeners()
	{
		assertEquals(0, support.getGraphChangeListeners().length);
		GraphChangeListener<Object, Edge<Object>> listener = new TransparentGCL();
		GraphChangeListener<Object, Edge<Object>> alt = new TransparentGCL();
		support.addGraphChangeListener(listener);
		assertEquals(1, support.getGraphChangeListeners().length);
		assertEquals(listener, support.getGraphChangeListeners()[0]);
		support.removeGraphChangeListener(null);
		assertEquals(1, support.getGraphChangeListeners().length);
		assertEquals(listener, support.getGraphChangeListeners()[0]);
		support.removeGraphChangeListener(alt);
		assertEquals(1, support.getGraphChangeListeners().length);
		assertEquals(listener, support.getGraphChangeListeners()[0]);
		support.removeGraphChangeListener(listener);
		assertEquals(0, support.getGraphChangeListeners().length);
	}

	public void testFireGraphEdgeChangeEvent()
	{
		//NEEDTEST
	}

	public void testFireGraphNodeChangeEvent()
	{
		TransparentGCL listener = new TransparentGCL();
		support.addGraphChangeListener(listener);
		Object a = new Object();
		support.fireGraphNodeChangeEvent(a, NodeChangeEvent.NODE_ADDED);
		assertEquals(0, listener.eAdded.size());
		assertEquals(0, listener.eRemoved.size());
		assertEquals(0, listener.nRemoved.size());
		assertEquals(1, listener.nAdded.size());
		assertEquals(a, listener.nAdded.get(0));
	}

	private final class TransparentGCL implements GraphChangeListener<Object, Edge<Object>>
	{
		public List<Object> nAdded = new ArrayList<>();
		public List<Object> nRemoved = new ArrayList<>();
		public List<Object> eAdded = new ArrayList<>();
		public List<Object> eRemoved = new ArrayList<>();

		@Override
		public void nodeAdded(NodeChangeEvent<Object> event)
		{
			nAdded.add(event.getGraphNode());
		}

		@Override
		public void nodeRemoved(NodeChangeEvent<Object> event)
		{
			nRemoved.add(event.getGraphNode());
		}

		@Override
		public void edgeAdded(EdgeChangeEvent<Object, Edge<Object>> event)
		{
			eAdded.add(event.getGraphEdge());
		}

		@Override
		public void edgeRemoved(EdgeChangeEvent<Object, Edge<Object>> event)
		{
			eRemoved.add(event.getGraphEdge());
		}
	}

}
