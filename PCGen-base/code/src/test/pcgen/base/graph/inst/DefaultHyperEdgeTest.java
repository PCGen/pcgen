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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class DefaultHyperEdgeTest extends TestCase
{

	private DefaultHyperEdge<Integer> edge1, edge2, edge3, edge4, edge5, edge6;

	private Integer node3, node4, node5, node6;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		node3 = new Integer(3);
		node4 = new Integer(4);
		node5 = new Integer(5);
		node6 = new Integer(6);
		edge1 =
				new DefaultHyperEdge<Integer>(Arrays.asList(new Integer[]{
					node4, node3, node6}));
		edge2 =
				new DefaultHyperEdge<Integer>(Arrays.asList(new Integer[]{
					node6, node5}));
		edge3 =
				new DefaultHyperEdge<Integer>(Arrays.asList(new Integer[]{
					node6, node4, node3}));
		edge4 =
				new DefaultHyperEdge<Integer>(Arrays.asList(new Integer[]{
					node6, node3}));
		edge5 =
				new DefaultHyperEdge<Integer>(
					Arrays.asList(new Integer[]{node6}));
		edge6 =
				new DefaultHyperEdge<Integer>(Arrays.asList(new Integer[]{
					node4, node6}));
	}

	public void testDefaultHyperEdge()
	{
		try
		{
			new DefaultHyperEdge<Integer>(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			new DefaultHyperEdge<Integer>(Arrays.asList(new Integer[]{}));
			fail();
		}
		catch (IllegalArgumentException iae)
		{
			// OK
		}
		try
		{
			new DefaultHyperEdge<Integer>(Arrays.asList(new Integer[]{node4,
				null}));
			fail();
		}
		catch (IllegalArgumentException iae)
		{
			// OK
		}
	}

	public void testGetNodeAt()
	{
		assertEquals(node6, edge3.getNodeAt(0));
		assertEquals(node4, edge3.getNodeAt(1));
		assertEquals(node3, edge3.getNodeAt(2));
		try
		{
			edge3.getNodeAt(-1);
			fail();
		}
		catch (IndexOutOfBoundsException e)
		{
			// OK
		}
		try
		{
			edge3.getNodeAt(3);
			fail();
		}
		catch (IndexOutOfBoundsException e)
		{
			// OK
		}
	}

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

	public void testGetAdjacentNodeCount()
	{
		assertEquals(3, edge1.getAdjacentNodeCount());
		assertEquals(2, edge2.getAdjacentNodeCount());
		assertEquals(3, edge3.getAdjacentNodeCount());
		assertEquals(2, edge4.getAdjacentNodeCount());
		assertEquals(1, edge5.getAdjacentNodeCount());
		assertEquals(2, edge6.getAdjacentNodeCount());
	}

	public void testIsAdjacentEdge()
	{
		assertTrue(edge1.isAdjacentNode(node3));
		assertTrue(edge1.isAdjacentNode(node4));
		assertFalse(edge1.isAdjacentNode(node5));
		assertTrue(edge1.isAdjacentNode(node6));
	}

	public void testCreateReplacementEdge()
	{
		try
		{
			edge1.createReplacementEdge(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK
		}
		try
		{
			edge1.createReplacementEdge(new ArrayList<Integer>());
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK
		}
		DefaultHyperEdge<Integer> newEdge =
				edge1.createReplacementEdge(Arrays.asList(new Integer[]{4, 5}));
		assertTrue(newEdge.getClass().equals(edge1.getClass()));
		List<Integer> l = newEdge.getAdjacentNodes();
		assertEquals(2, l.size());
		assertTrue(l.contains(4));
		assertTrue(l.contains(5));
	}

}
