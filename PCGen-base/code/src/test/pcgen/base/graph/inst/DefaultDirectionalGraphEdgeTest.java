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
package pcgen.base.graph.inst;

import java.util.List;

import pcgen.base.graph.base.DirectionalEdge;
import pcgen.base.graph.base.GraphEdge;

import junit.framework.TestCase;

/**
 * Test the DefaultDirectionalGraphEdge class
 */
public class DefaultDirectionalGraphEdgeTest extends TestCase
{

	private Double node1, node2, node3, node4;

	private DefaultDirectionalGraphEdge<Double> edge1, edge2, edge3, edge4, edge5;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		node1 = new Double(1);
		node2 = new Double(2);
		node3 = new Double(5);
		node4 = new Double(16);
		edge1 = new DefaultDirectionalGraphEdge<>(node1, node2);
		edge2 = new DefaultDirectionalGraphEdge<>(node2, node1);
		edge3 = new DefaultDirectionalGraphEdge<>(node1, node3);
		edge4 = new DefaultDirectionalGraphEdge<>(node1, node3);
		edge5 = new DefaultDirectionalGraphEdge<>(node4, node4);
	}

	@SuppressWarnings("unused")
	public void testDefaultGraphEdge()
	{
		try
		{
			new DefaultDirectionalGraphEdge<>(node1, null);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			new DefaultDirectionalGraphEdge<>(null, node3);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			new DefaultDirectionalGraphEdge<Double>(null, null);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
	}

	public void testGetNodeAt()
	{
		assertEquals(node1, edge1.getNodeAt(0));
		assertEquals(node2, edge2.getNodeAt(0));
		assertEquals(node1, edge3.getNodeAt(0));
		assertEquals(node1, edge4.getNodeAt(0));
		assertEquals(node4, edge5.getNodeAt(0));
		assertEquals(node2, edge1.getNodeAt(1));
		assertEquals(node1, edge2.getNodeAt(1));
		assertEquals(node3, edge3.getNodeAt(1));
		assertEquals(node3, edge4.getNodeAt(1));
		assertEquals(node4, edge5.getNodeAt(1));
		try
		{
			assertEquals(node1, edge1.getNodeAt(2));
			fail();
		}
		catch (IndexOutOfBoundsException e)
		{
			//expected
		}
		try
		{
			assertEquals(node1, edge1.getNodeAt(-1));
			fail();
		}
		catch (IndexOutOfBoundsException e)
		{
			//expected
		}
	}

	public void testGetOppositeNode()
	{
		assertEquals(node1, edge1.getOppositeNode(node2));
		assertEquals(node2, edge2.getOppositeNode(node1));
		assertEquals(node1, edge3.getOppositeNode(node3));
		assertEquals(node1, edge4.getOppositeNode(node3));
		assertEquals(node2, edge1.getOppositeNode(node1));
		assertEquals(node1, edge2.getOppositeNode(node2));
		assertEquals(node3, edge3.getOppositeNode(node1));
		assertEquals(node3, edge4.getOppositeNode(node1));
		assertEquals(node4, edge5.getOppositeNode(node4));
		// ones that aren't in the edge return null
		assertNull(edge1.getOppositeNode(null));
		assertNull(edge1.getOppositeNode(node3));
	}

	public void testCreateReplacementEdgeNodeNode()
	{
		GraphEdge<Double> ge = edge1.createReplacementEdge(node3, node4);
		assertTrue(ge instanceof DefaultDirectionalGraphEdge);
		assertEquals(node3, ge.getNodeAt(0));
		assertEquals(node4, ge.getNodeAt(1));
		assertEquals(2, ge.getAdjacentNodeCount());
	}

	public void testGetAdjacentNodeCount()
	{
		assertEquals(2, edge1.getAdjacentNodeCount());
		assertEquals(2, edge2.getAdjacentNodeCount());
	}

	public void testIsAdjacentNode()
	{
		assertTrue(edge3.isAdjacentNode(node1));
		assertFalse(edge3.isAdjacentNode(node2));
		assertTrue(edge3.isAdjacentNode(node3));
		assertTrue(edge2.isAdjacentNode(node1));
		assertTrue(edge2.isAdjacentNode(node2));
		assertFalse(edge2.isAdjacentNode(node3));
	}

	public void testGetSourceNode()
	{
		List<Double> l;
		l = edge1.getSourceNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node1));
		l = edge2.getSourceNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node2));
		l = edge3.getSourceNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node1));
		l = edge4.getSourceNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node1));
		l = edge5.getSourceNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node4));
	}

	public void testGetSinkNode()
	{
		List<Double> l;
		l = edge1.getSinkNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node2));
		l = edge2.getSinkNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node1));
		l = edge3.getSinkNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node3));
		l = edge4.getSinkNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node3));
		l = edge5.getSinkNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node4));
	}

	public void testGetNodeInterfaceType()
	{
		assertEquals(DirectionalEdge.SOURCE, edge1.getNodeInterfaceType(node1));
		assertEquals(DirectionalEdge.SINK, edge1.getNodeInterfaceType(node2));
		assertEquals(DirectionalEdge.UNCONNECTED,
			edge1.getNodeInterfaceType(node3));
		assertEquals(DirectionalEdge.UNCONNECTED,
			edge1.getNodeInterfaceType(null));
		assertEquals(DirectionalEdge.SOURCE | DirectionalEdge.SINK,
			edge5.getNodeInterfaceType(node4));
	}
}
