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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.graph.base.GraphEdge;

/**
 * Test the DefaultGraphEdge class
 */
public class DefaultGraphEdgeTest
{

	private Double node1, node2, node3, node4;

	private DefaultGraphEdge<Double> edge1, edge2, edge3, edge4, edge5;

	@BeforeEach
	void setUp()
	{
		node1 = new Double(1);
		node2 = new Double(2);
		node3 = new Double(5);
		node4 = new Double(16);
		edge1 = new DefaultGraphEdge<>(node1, node2);
		edge2 = new DefaultGraphEdge<>(node2, node1);
		edge3 = new DefaultGraphEdge<>(node1, node3);
		edge4 = new DefaultGraphEdge<>(node1, node3);
		edge5 = new DefaultGraphEdge<>(node4, node4);
	}

	@AfterEach
	void tearDown()
	{
		node1 = null;
		node2 = null;
		node3 = null;
		node4 = null;
		edge1 = null;
		edge2 = null;
		edge3 = null;
		edge4 = null;
		edge5 = null;
	}

	@Test
	public void testDefaultGraphEdge()
	{
		assertThrows(NullPointerException.class, () -> new DefaultGraphEdge<>(node1, null));
		assertThrows(NullPointerException.class, () -> new DefaultGraphEdge<>(null, node3));
		assertThrows(NullPointerException.class, () -> new DefaultGraphEdge<Double>(null, null));
	}

	@Test
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
		assertThrows(IndexOutOfBoundsException.class, () -> assertEquals(node1, edge1.getNodeAt(2)));
		assertThrows(IndexOutOfBoundsException.class, () -> assertEquals(node1, edge1.getNodeAt(-1)));
	}

	@Test
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

	@Test
	public void testCreateReplacementEdgeNodeNode()
	{
		GraphEdge<Double> ge = edge1.createReplacementEdge(node3, node4);
		assertTrue(ge instanceof DefaultGraphEdge);
		assertEquals(node3, ge.getNodeAt(0));
		assertEquals(node4, ge.getNodeAt(1));
		assertEquals(2, ge.getAdjacentNodeCount());
	}

	@Test
	public void testGetAdjacentNodeCount()
	{
		assertEquals(2, edge1.getAdjacentNodeCount());
		assertEquals(2, edge2.getAdjacentNodeCount());
	}

	@Test
	public void testIsAdjacentNode()
	{
		assertTrue(edge3.isAdjacentNode(node1));
		assertFalse(edge3.isAdjacentNode(node2));
		assertTrue(edge3.isAdjacentNode(node3));
		assertTrue(edge2.isAdjacentNode(node1));
		assertTrue(edge2.isAdjacentNode(node2));
		assertFalse(edge2.isAdjacentNode(node3));
	}
}
