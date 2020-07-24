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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the DefaultHyperEdge class
 */
public class DefaultHyperEdgeTest
{

	private DefaultHyperEdge<Integer> edge1, edge2, edge3, edge4, edge5, edge6;

	private Integer node3, node4, node5, node6;

	@BeforeEach
	void setUp()
	{
		node3 = new Integer(3);
		node4 = new Integer(4);
		node5 = new Integer(5);
		node6 = new Integer(6);
		edge1 =
				new DefaultHyperEdge<>(Arrays.asList(new Integer[]{
						node4, node3, node6}));
		edge2 =
				new DefaultHyperEdge<>(Arrays.asList(new Integer[]{
						node6, node5}));
		edge3 =
				new DefaultHyperEdge<>(Arrays.asList(new Integer[]{
						node6, node4, node3}));
		edge4 =
				new DefaultHyperEdge<>(Arrays.asList(new Integer[]{
						node6, node3}));
		edge5 =
				new DefaultHyperEdge<>(
						Arrays.asList(new Integer[]{node6}));
		edge6 =
				new DefaultHyperEdge<>(Arrays.asList(new Integer[]{
						node4, node6}));
	}

	@AfterEach
	void tearDown()
	{
		node3 = null;
		node4 = null;
		node5 = null;
		node6 = null;
		edge1 = null;
		edge2 = null;
		edge3 = null;
		edge4 = null;
		edge5 = null;
		edge6 = null;
	}

	@Test
	public void testDefaultHyperEdge()
	{
		assertThrows(NullPointerException.class, () -> new DefaultHyperEdge<Integer>(null));
		assertThrows(IllegalArgumentException.class, () -> new DefaultHyperEdge<>(Arrays.asList(new Integer[]{})));
		assertThrows(IllegalArgumentException.class, () -> new DefaultHyperEdge<>(Arrays.asList(new Integer[]{node4, null})));
	}

	@Test
	public void testGetNodeAt()
	{
		assertEquals(node6, edge3.getNodeAt(0));
		assertEquals(node4, edge3.getNodeAt(1));
		assertEquals(node3, edge3.getNodeAt(2));
		assertThrows(IndexOutOfBoundsException.class, () -> edge3.getNodeAt(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> edge3.getNodeAt(3));
	}

	@Test
	public void testGetAdjacentNodes()
	{
		List<Integer> l = edge3.getAdjacentNodes();
		assertEquals(3, l.size());
		assertTrue(l.contains(node3));
		assertTrue(l.contains(node4));
		assertTrue(l.contains(node6));
		l = edge5.getAdjacentNodes();
		assertEquals(1, l.size());
		assertTrue(l.contains(node6));
	}

	@Test
	public void testGetAdjacentNodeCount()
	{
		assertEquals(3, edge1.getAdjacentNodeCount());
		assertEquals(2, edge2.getAdjacentNodeCount());
		assertEquals(3, edge3.getAdjacentNodeCount());
		assertEquals(2, edge4.getAdjacentNodeCount());
		assertEquals(1, edge5.getAdjacentNodeCount());
		assertEquals(2, edge6.getAdjacentNodeCount());
	}

	@Test
	public void testIsAdjacentEdge()
	{
		assertTrue(edge1.isAdjacentNode(node3));
		assertTrue(edge1.isAdjacentNode(node4));
		assertFalse(edge1.isAdjacentNode(node5));
		assertTrue(edge1.isAdjacentNode(node6));
	}

	@Test
	public void testCreateReplacementEdge()
	{
		assertThrows(NullPointerException.class, () -> edge1.createReplacementEdge(null));
		assertThrows(IllegalArgumentException.class, () -> edge1.createReplacementEdge(new ArrayList<>()));
		DefaultHyperEdge<Integer> newEdge =
				edge1.createReplacementEdge(Arrays.asList(new Integer[]{4, 5}));
		assertTrue(newEdge.getClass().equals(edge1.getClass()));
		List<Integer> l = newEdge.getAdjacentNodes();
		assertEquals(2, l.size());
		assertTrue(l.contains(4));
		assertTrue(l.contains(5));
	}

}
