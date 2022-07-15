/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.graph.base.DirectionalEdge;

/**
 * Test the DefaultDirectionalHyperEdge class
 */
public class DefaultDirectionalHyperEdgeTest
{

	private Integer node1, node2, node3, node4, node5, node6;

	private DefaultDirectionalHyperEdge<Integer> edge1, edge2, edge3, edge4, edge5;

	@BeforeEach
	void setUp()
	{
		node1 = 1;
		node2 = 2;
		node3 = 3;
		node4 = 4;
		node5 = 5;
		node6 = 6;
		edge1 =
				new DefaultDirectionalHyperEdge<>(
						Arrays.asList(new Integer[]{node1}),
						Arrays.asList(new Integer[]{node2, node3})
				);
		edge2 =
				new DefaultDirectionalHyperEdge<>(
						Arrays.asList(new Integer[]{node3, node4, node1}),
						Arrays.asList(new Integer[]{node5, node6})
				);
		edge3 =
				new DefaultDirectionalHyperEdge<>(
						null,
						Arrays.asList(new Integer[]{node5, node6})
				);
		edge4 =
				new DefaultDirectionalHyperEdge<>(
						Arrays.asList(new Integer[]{node3, node4, node1}), null);
		edge5 =
				new DefaultDirectionalHyperEdge<>(
						Arrays.asList(new Integer[]{node3, node4, node1}),
						Arrays.asList(new Integer[]{node2, node3})
				);
	}

	@AfterEach
	void tearDown()
	{
		node1 = null;
		node2 = null;
		node3 = null;
		node4 = null;
		node5 = null;
		node6 = null;
		edge1 = null;
		edge2 = null;
		edge3 = null;
		edge4 = null;
		edge5 = null;
	}

	@Test
	public void testDefaultDirectionalHyperEdge()
	{
		assertThrows(IllegalArgumentException.class, () -> new DefaultDirectionalHyperEdge<Integer>(null, null));
		assertThrows(IllegalArgumentException.class, () -> new DefaultDirectionalHyperEdge<>(null, new ArrayList<>()));
		assertThrows(IllegalArgumentException.class, () -> new DefaultDirectionalHyperEdge<>(new ArrayList<>(), null));
		assertThrows(IllegalArgumentException.class, () -> new DefaultDirectionalHyperEdge<>(
					new ArrayList<>(),
					new ArrayList<>()
			));
		assertThrows(IllegalArgumentException.class, () -> new DefaultDirectionalHyperEdge<>(
					Arrays.asList(new Integer[]{3, 4, 5}),
					Arrays.asList(new Integer[]{6, null})
			));
		assertThrows(IllegalArgumentException.class, () -> new DefaultDirectionalHyperEdge<>(
					Arrays.asList(new Integer[]{null, 4, 5}),
					Arrays.asList(new Integer[]{6})
			));
	}

	@Test
	public void testGetNodeAt()
	{
		assertEquals(node1, edge1.getNodeAt(0));
		assertEquals(node2, edge1.getNodeAt(1));
		assertEquals(node3, edge1.getNodeAt(2));
		assertThrows(IndexOutOfBoundsException.class, () -> edge1.getNodeAt(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> edge1.getNodeAt(3));
		assertEquals(node5, edge3.getNodeAt(0));
		assertEquals(node6, edge3.getNodeAt(1));
		assertThrows(IndexOutOfBoundsException.class, () -> edge3.getNodeAt(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> edge3.getNodeAt(2));
		assertEquals(node3, edge4.getNodeAt(0));
		assertEquals(node4, edge4.getNodeAt(1));
		assertEquals(node1, edge4.getNodeAt(2));
		assertThrows(IndexOutOfBoundsException.class, () -> edge4.getNodeAt(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> edge4.getNodeAt(3));
	}

	@Test
	public void testGetAdjacentNodes()
	{
		List<Integer> l = edge5.getAdjacentNodes();
		assertEquals(5, l.size());
		assertTrue(l.remove(node1));
		assertTrue(l.remove(node2));
		assertTrue(l.remove(node3));
		// 3 present twice - once as source, once as sink
		assertTrue(l.remove(node3));
		assertTrue(l.remove(node4));
		l.add(node5);
		// Returned List is not saved:
		assertFalse(edge5.isAdjacentNode(node5));
		l = edge5.getAdjacentNodes();
		assertEquals(5, l.size());
	}

	@Test
	public void testIsAdjacentNode()
	{
		assertTrue(edge1.isAdjacentNode(node1));
		assertTrue(edge1.isAdjacentNode(node2));
		assertTrue(edge1.isAdjacentNode(node3));
		assertFalse(edge1.isAdjacentNode(node4));
		assertFalse(edge1.isAdjacentNode(node5));
		assertFalse(edge1.isAdjacentNode(node6));
		assertTrue(edge2.isAdjacentNode(node1));
		assertFalse(edge2.isAdjacentNode(node2));
		assertTrue(edge2.isAdjacentNode(node3));
		assertTrue(edge2.isAdjacentNode(node4));
		assertTrue(edge2.isAdjacentNode(node5));
		assertTrue(edge2.isAdjacentNode(node6));
		assertFalse(edge3.isAdjacentNode(node1));
		assertFalse(edge3.isAdjacentNode(node2));
		assertFalse(edge3.isAdjacentNode(node3));
		assertFalse(edge3.isAdjacentNode(node4));
		assertTrue(edge3.isAdjacentNode(node5));
		assertTrue(edge3.isAdjacentNode(node6));
		assertTrue(edge4.isAdjacentNode(node1));
		assertFalse(edge4.isAdjacentNode(node2));
		assertTrue(edge4.isAdjacentNode(node3));
		assertTrue(edge4.isAdjacentNode(node4));
		assertFalse(edge4.isAdjacentNode(node5));
		assertFalse(edge4.isAdjacentNode(node6));
		assertFalse(edge1.isAdjacentNode(null));
		assertFalse(edge2.isAdjacentNode(null));
		assertFalse(edge3.isAdjacentNode(null));
		assertFalse(edge4.isAdjacentNode(null));
		assertTrue(edge5.isAdjacentNode(node1));
		assertTrue(edge5.isAdjacentNode(node2));
		assertTrue(edge5.isAdjacentNode(node3));
		assertTrue(edge5.isAdjacentNode(node4));
		assertFalse(edge5.isAdjacentNode(node5));
		assertFalse(edge5.isAdjacentNode(node6));
		assertFalse(edge5.isAdjacentNode(null));
	}

	@Test
	public void testGetAdjacentNodeCount()
	{
		assertEquals(3, edge1.getAdjacentNodeCount());
		assertEquals(5, edge2.getAdjacentNodeCount());
		assertEquals(2, edge3.getAdjacentNodeCount());
		assertEquals(3, edge4.getAdjacentNodeCount());
		assertEquals(4, edge5.getAdjacentNodeCount());
	}

	@Test
	public void testGetNodeInterfaceType()
	{
		assertEquals(DirectionalEdge.SOURCE, edge5.getNodeInterfaceType(node1));
		assertEquals(DirectionalEdge.SINK, edge5.getNodeInterfaceType(node2));
		assertEquals(DirectionalEdge.SOURCE | DirectionalEdge.SINK,
			edge5.getNodeInterfaceType(node3));
		assertEquals(DirectionalEdge.UNCONNECTED,
			edge5.getNodeInterfaceType(node5));
		assertEquals(DirectionalEdge.UNCONNECTED,
			edge5.getNodeInterfaceType(null));
	}

	@Test
	public void testGetSinkNodes()
	{
		Collection<Integer> l = edge1.getSinkNodes();
		assertEquals(2, l.size());
		assertTrue(l.contains(node2));
		assertTrue(l.contains(node3));
		// ensure independent of outside write
		assertEquals(3, edge1.getAdjacentNodeCount());
		l.add(node4);
		assertEquals(3, edge1.getAdjacentNodeCount());
		Collection<Integer> l2 = edge1.getSinkNodes();
		assertEquals(2, l2.size());
		assertTrue(l2.contains(node2));
		assertTrue(l2.contains(node3));
		assertFalse(l2.contains(node4));
		l = edge3.getSinkNodes();
		assertEquals(2, l.size());
		assertTrue(l.contains(node5));
		assertTrue(l.contains(node6));
		l = edge4.getSinkNodes();
		assertNull(l);
		l = edge5.getSinkNodes();
		assertEquals(2, l.size());
		assertTrue(l.contains(node2));
		assertTrue(l.contains(node3));
	}

	@Test
	public void testGetSourceNodes()
	{
		Collection<Integer> l = edge1.getSourceNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node1));
		// ensure independent of outside write
		assertEquals(3, edge1.getAdjacentNodeCount());
		l.add(node4);
		assertEquals(3, edge1.getAdjacentNodeCount());
		Collection<Integer> l2 = edge1.getSourceNodes();
		assertEquals(1, l2.size());
		assertTrue(l2.contains(node1));
		assertFalse(l2.contains(node4));
		l = edge3.getSourceNodes();
		assertNull(l);
		l = edge4.getSourceNodes();
		assertEquals(3, l.size());
		assertTrue(l.contains(node1));
		assertTrue(l.contains(node3));
		assertTrue(l.contains(node4));
		l = edge5.getSourceNodes();
		assertEquals(3, l.size());
		assertTrue(l.contains(node1));
		assertTrue(l.contains(node4));
		assertTrue(l.contains(node3));
	}

	@Test
	public void testCreateReplacementEdge()
	{
		assertThrows(NullPointerException.class, () -> edge1.createReplacementEdge(null, null));
		assertThrows(NullPointerException.class, () -> edge1.createReplacementEdge(null, new ArrayList<>()));
		assertThrows(NullPointerException.class, () -> edge1.createReplacementEdge(new ArrayList<>(), null));
		assertThrows(IllegalArgumentException.class, () -> edge1.createReplacementEdge(
					new ArrayList<>(),
					new ArrayList<>()));
		assertThrows(IllegalArgumentException.class, () -> edge1.createReplacementEdge(Arrays.asList(new Integer[]{3, 4, 5}),
				Arrays.asList(new Integer[]{6, null})));
		assertThrows(IllegalArgumentException.class, () -> edge1.createReplacementEdge(
				Arrays.asList(new Integer[]{null, 4, 5}),
				Arrays.asList(new Integer[]{6})));
	}

	@Test
	public void testCreateReplacementEdgeSemantics()
	{
		List<Integer> sourceL =
				new ArrayList<>(
						Arrays.asList(new Integer[]{node3, node4}));
		List<Integer> sinkL =
				new ArrayList<>(Arrays.asList(new Integer[]{node5}));
		DefaultDirectionalHyperEdge<Integer> edge =
				edge1.createReplacementEdge(sourceL, sinkL);
		assertEquals(3, edge.getAdjacentNodeCount());
		List<Integer> l = edge.getSourceNodes();
		assertEquals(2, l.size());
		assertTrue(l.contains(node3));
		assertTrue(l.contains(node4));
		sourceL.add(node1);
		// Not passed through
		assertEquals(2, l.size());
		assertTrue(l.contains(node3));
		assertTrue(l.contains(node4));
		// Not stored
		l = edge.getSourceNodes();
		assertEquals(2, l.size());
		assertTrue(l.contains(node3));
		assertTrue(l.contains(node4));
		l = edge.getSinkNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node5));
		sinkL.add(node2);
		// Not passed through
		assertEquals(1, l.size());
		assertTrue(l.contains(node5));
		// Not stored
		l = edge.getSinkNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node5));
	}

	@Test
	public void testConstructorSemantics()
	{
		List<Integer> sourceL =
				new ArrayList<>(
						Arrays.asList(new Integer[]{node3, node4}));
		List<Integer> sinkL =
				new ArrayList<>(Arrays.asList(new Integer[]{node5}));
		DefaultDirectionalHyperEdge<Integer> edge =
				new DefaultDirectionalHyperEdge<>(sourceL, sinkL);
		assertEquals(3, edge.getAdjacentNodeCount());
		List<Integer> l = edge.getSourceNodes();
		assertEquals(2, l.size());
		assertTrue(l.contains(node3));
		assertTrue(l.contains(node4));
		sourceL.add(node1);
		// Not passed through
		assertEquals(2, l.size());
		assertTrue(l.contains(node3));
		assertTrue(l.contains(node4));
		// Not stored
		l = edge.getSourceNodes();
		assertEquals(2, l.size());
		assertTrue(l.contains(node3));
		assertTrue(l.contains(node4));
		l = edge.getSinkNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node5));
		sinkL.add(node2);
		// Not passed through
		assertEquals(1, l.size());
		assertTrue(l.contains(node5));
		// Not stored
		l = edge.getSinkNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node5));
	}
}
