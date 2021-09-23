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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import pcgen.base.graph.base.DirectionalEdge;
import pcgen.base.graph.base.DirectionalGraph;

/**
 * Test DirectionalMapGraph classes
 */
public abstract class AbstractDirectionalMapGraphTest extends
		AbstractGraphTestCase<DirectionalEdge<Integer>>
{

	//TODO Need to test .equals behavior on nodes and edges

	public class TestDirectionalGraphEdge extends DefaultGraphEdge<Integer>
			implements DirectionalEdge<Integer>
	{
		public TestDirectionalGraphEdge(Integer v1, Integer v2)
		{
			super(v1, v2);
		}

		public DirectionalEdge<Integer> createReplacementEdge(
			Collection<Integer> gn1, Collection<Integer> gn2)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Integer> getSinkNodes()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Integer> getSourceNodes()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isSource(Integer node)
		{
			return getNodeAt(0).equals(node);
		}

		@Override
		public boolean isSink(Integer node)
		{
			return getNodeAt(1).equals(node);
		}
	}

	@Override
	protected DirectionalEdge<Integer> getLegalHyperEdge(Integer[] gna2)
	{
		List<Integer> l = new ArrayList<>(Arrays.asList(gna2));
		l.remove(0);
		return new DefaultDirectionalHyperEdge<>(
				Collections.singletonList(gna2[0]), l);
	}

	@Override
	protected DirectionalEdge<Integer> getLegalEdge(Integer node1, Integer node2)
	{
		return new TestDirectionalGraphEdge(node1, node2);
	}

	// TODO Need to consider if - outside of Generics - we want to enforce
	// the directional limitation...
	// @Override
	// public void testAddEdge() {
	// super.testAddEdge();
	// listener.lastAddEdge = null;
	// Integer node1 = new Integer(1);
	// Integer node2 = new Integer(2);
	// DirectionalEdge<Integer> edge = new TestDirectionalGraphEdge(node1,
	// node2);
	// strategy.addGraphChangeListener(listener);
	// assertFalse(strategy.containsEdge(edge));
	// assertNull(listener.lastAddEdge);
	// assertFalse(strategy.addEdge(edge));
	// assertFalse(strategy.containsEdge(edge));
	// assertFalse(strategy.getEdgeList().contains(edge));
	// assertNull(listener.lastAddEdge);
	// }

	@Test
	public void testGetInwardEdgeList()
	{
		DirectionalGraph<Integer, DirectionalEdge<Integer>> strategy = getStrategy();
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		DirectionalEdge<Integer> edge1 = getLegalEdge(node1, node2);
		DirectionalEdge<Integer> edge2 = getLegalEdge(node2, node3);
		DirectionalEdge<Integer> edge3 = getLegalEdge(node3, node1);
		DirectionalEdge<Integer> edge4 = getLegalEdge(node1, node3);
		assertTrue(strategy.addNode(node1));
		assertTrue(strategy.addNode(node2));
		assertTrue(strategy.addNode(node3));
		assertTrue(strategy.addEdge(edge1));
		assertTrue(strategy.addEdge(edge2));
		assertTrue(strategy.addEdge(edge3));
		assertTrue(strategy.addEdge(edge4));
		List<DirectionalEdge<Integer>> l = strategy.getInwardEdgeList(node1);
		// order is not significant
		assertEquals(1, l.size());
		assertTrue(l.contains(edge3));
		l = strategy.getInwardEdgeList(node3);
		// order is not significant
		assertEquals(2, l.size());
		assertTrue(l.contains(edge2));
		assertTrue(l.contains(edge4));
		strategy.removeNode(node2);
		l = strategy.getInwardEdgeList(node3);
		// order is not significant
		assertEquals(1, l.size());
		assertTrue(l.contains(edge4));
		// not in graph
		assertNull(strategy.getInwardEdgeList(node2));
		// special case
		assertNull(strategy.getInwardEdgeList(null));
	}

	@Test
	public void testGetOutwardEdgeList()
	{
		DirectionalGraph<Integer, DirectionalEdge<Integer>> strategy = getStrategy();
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		DirectionalEdge<Integer> edge1 = getLegalEdge(node1, node2);
		DirectionalEdge<Integer> edge2 = getLegalEdge(node2, node3);
		DirectionalEdge<Integer> edge3 = getLegalEdge(node3, node1);
		DirectionalEdge<Integer> edge4 = getLegalEdge(node1, node3);
		assertTrue(strategy.addNode(node1));
		assertTrue(strategy.addNode(node2));
		assertTrue(strategy.addNode(node3));
		assertTrue(strategy.addEdge(edge1));
		assertTrue(strategy.addEdge(edge2));
		assertTrue(strategy.addEdge(edge3));
		assertTrue(strategy.addEdge(edge4));
		List<DirectionalEdge<Integer>> l = strategy.getOutwardEdgeList(node2);
		// order is not significant
		assertEquals(1, l.size());
		assertTrue(l.contains(edge2));
		l = strategy.getOutwardEdgeList(node1);
		// order is not significant
		assertEquals(2, l.size());
		assertTrue(l.contains(edge1));
		assertTrue(l.contains(edge4));
		strategy.removeNode(node2);
		l = strategy.getOutwardEdgeList(node1);
		// order is not significant
		assertEquals(1, l.size());
		assertTrue(l.contains(edge4));
		// not in graph
		assertNull(strategy.getOutwardEdgeList(node2));
		// special case
		assertNull(strategy.getOutwardEdgeList(null));
	}

	@Test
	public void testHasInwardEdgeList()
	{
		DirectionalGraph<Integer, DirectionalEdge<Integer>> strategy = getStrategy();
		Integer node0 = new Integer(0);
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		Integer node4 = new Integer(4);
		Integer node5 = new Integer(5);
		Integer node6 = new Integer(6);
		DirectionalEdge<Integer> edge1 = getLegalEdge(node1, node2);
		DirectionalEdge<Integer> edge2 = getLegalEdge(node2, node3);
		DirectionalEdge<Integer> edge3 = getLegalEdge(node3, node1);
		DirectionalEdge<Integer> edge4 = getLegalEdge(node1, node3);
		DirectionalEdge<Integer> edge5 =
				new DefaultDirectionalHyperEdge<>(
						Collections.singletonList(node4), new ArrayList<>());
		DirectionalEdge<Integer> edge6 =
				new DefaultDirectionalHyperEdge<>(
						new ArrayList<>(), Collections.singletonList(node4));
		DirectionalEdge<Integer> edge7 =
				new DefaultDirectionalHyperEdge<>(Arrays.asList(
						node3,
						node5
				), new ArrayList<>());
		DirectionalEdge<Integer> edge8 =
				new DefaultDirectionalHyperEdge<>(
						new ArrayList<>(), Arrays.asList(node3, node6));
		assertTrue(strategy.addNode(node1));
		assertTrue(strategy.addNode(node2));
		assertTrue(strategy.addNode(node3));
		assertTrue(strategy.addNode(node4));
		assertTrue(strategy.addNode(node5));
		assertTrue(strategy.addNode(node6));
		assertTrue(strategy.addEdge(edge1));
		assertTrue(strategy.addEdge(edge2));
		assertTrue(strategy.addEdge(edge3));
		assertTrue(strategy.addEdge(edge4));
		assertTrue(strategy.addEdge(edge5));
		assertTrue(strategy.addEdge(edge6));
		assertTrue(strategy.addEdge(edge7));
		assertTrue(strategy.addEdge(edge8));
		assertTrue(strategy.hasInwardEdge(node1));
		assertTrue(strategy.hasInwardEdge(node2));
		assertTrue(strategy.hasInwardEdge(node3));
		assertTrue(strategy.hasInwardEdge(node4));
		assertFalse(strategy.hasInwardEdge(node5));
		assertTrue(strategy.hasInwardEdge(node6));
		// not in graph
		assertFalse(strategy.hasInwardEdge(node0));
		// special case
		assertFalse(strategy.hasInwardEdge(null));
	}

	@Test
	public void testHasOutwardEdgeList()
	{
		DirectionalGraph<Integer, DirectionalEdge<Integer>> strategy = getStrategy();
		Integer node0 = new Integer(0);
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		Integer node4 = new Integer(4);
		Integer node5 = new Integer(5);
		Integer node6 = new Integer(6);
		DirectionalEdge<Integer> edge1 = getLegalEdge(node1, node2);
		DirectionalEdge<Integer> edge2 = getLegalEdge(node2, node3);
		DirectionalEdge<Integer> edge3 = getLegalEdge(node3, node1);
		DirectionalEdge<Integer> edge4 = getLegalEdge(node1, node3);
		DirectionalEdge<Integer> edge5 =
				new DefaultDirectionalHyperEdge<>(
						Collections.singletonList(node4), new ArrayList<>());
		DirectionalEdge<Integer> edge6 =
				new DefaultDirectionalHyperEdge<>(
						new ArrayList<>(), Collections.singletonList(node4));
		DirectionalEdge<Integer> edge7 =
				new DefaultDirectionalHyperEdge<>(Arrays.asList(
						node3,
						node5
				), new ArrayList<>());
		DirectionalEdge<Integer> edge8 =
				new DefaultDirectionalHyperEdge<>(
						new ArrayList<>(), Arrays.asList(node3, node6));
		assertTrue(strategy.addNode(node1));
		assertTrue(strategy.addNode(node2));
		assertTrue(strategy.addNode(node3));
		assertTrue(strategy.addNode(node4));
		assertTrue(strategy.addNode(node5));
		assertTrue(strategy.addNode(node6));
		assertTrue(strategy.addEdge(edge1));
		assertTrue(strategy.addEdge(edge2));
		assertTrue(strategy.addEdge(edge3));
		assertTrue(strategy.addEdge(edge4));
		assertTrue(strategy.addEdge(edge5));
		assertTrue(strategy.addEdge(edge6));
		assertTrue(strategy.addEdge(edge7));
		assertTrue(strategy.addEdge(edge8));
		assertTrue(strategy.hasOutwardEdge(node1));
		assertTrue(strategy.hasOutwardEdge(node2));
		assertTrue(strategy.hasOutwardEdge(node3));
		assertTrue(strategy.hasOutwardEdge(node4));
		assertTrue(strategy.hasOutwardEdge(node5));
		assertFalse(strategy.hasOutwardEdge(node6));
		// not in graph
		assertFalse(strategy.hasOutwardEdge(node0));
		// special case
		assertFalse(strategy.hasOutwardEdge(null));
	}

	@Override
	public abstract DirectionalGraph<Integer, DirectionalEdge<Integer>> getStrategy();
}
