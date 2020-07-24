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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.Graph;
import pcgen.base.graph.base.GraphChangeListener;
import pcgen.base.graph.testsupport.TestGraphChangeListener;

/**
 * Shared class for testing various implementations of Graph
 * 
 * @param <T> The type of Edge in the Graph being tested
 */
public abstract class AbstractGraphTestCase<T extends Edge<Integer>>
{

	private SimpleListGraph<Integer, T> master = new SimpleListGraph<>();

	private TestGraphChangeListener<Integer, T> listener;

	@BeforeEach
	void setUp()
	{
		listener = new TestGraphChangeListener<>();
	}

	@AfterEach
	void tearDown()
	{
		listener = null;
	}

	@Test
	public void testAddNode()
	{
		getStrategy().addGraphChangeListener(listener);
		Integer node = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		assertEquals(0, getStrategy().getNodeCount());
		assertTrue(getStrategy().isEmpty());
		assertFalse(getStrategy().containsNode(node));
		assertFalse(getStrategy().containsNode(node2));
		assertNull(listener.lastAddNode);
		// don't allow a null
		assertFalse(getStrategy().addNode(null));
		assertEquals(0, getStrategy().getNodeCount());
		assertTrue(getStrategy().isEmpty());
		assertNull(listener.lastAddNode);
		// Now actually add one
		assertTrue(getStrategy().addNode(node));
		assertEquals(1, getStrategy().getNodeCount());
		assertFalse(getStrategy().isEmpty());
		assertTrue(getStrategy().containsNode(node));
		assertFalse(getStrategy().containsNode(node2));
		assertEquals(node, listener.lastAddNode);
		// don't allow a second time
		listener.lastAddNode = null;
		assertFalse(getStrategy().addNode(node));
		assertNull(listener.lastAddNode);
		assertEquals(1, getStrategy().getNodeCount());
		// check that something else works!
		assertTrue(getStrategy().addNode(node2));
		assertTrue(getStrategy().containsNode(node2));
		assertEquals(2, getStrategy().getNodeCount());
		assertEquals(node2, listener.lastAddNode);
		// check special case
		assertFalse(getStrategy().containsNode(null));
		assertEquals(2, getStrategy().getNodeList().size());
		assertEquals(2, getStrategy().getNodeCount());
		assertFalse(getStrategy().addNode(null));
		// ensure it didn't get stored!
		assertFalse(getStrategy().containsNode(null));
		assertEquals(2, getStrategy().getNodeList().size());
		assertEquals(2, getStrategy().getNodeCount());
		assertEquals(node2, listener.lastAddNode);
		//
		assertTrue(getStrategy().addNode(node3));
		assertTrue(getStrategy().containsNode(node3));
		assertEquals(3, getStrategy().getNodeList().size());
		assertEquals(3, getStrategy().getNodeCount());
		assertEquals(node3, listener.lastAddNode);
	}

	@Test
	public void testAddEdge()
	{
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		Integer node4 = new Integer(4);
		Integer node5 = new Integer(5);
		T edge = getLegalEdge(node1, node2);
		T edge2 = getLegalEdge(node1, node3);
		getStrategy().addGraphChangeListener(listener);
		assertFalse(getStrategy().containsEdge(edge));
		assertNull(listener.lastAddEdge);
		assertTrue(getStrategy().isEmpty());
		assertTrue(getStrategy().addEdge(edge));
		assertFalse(getStrategy().isEmpty());
		assertTrue(getStrategy().containsEdge(edge));
		assertTrue(getStrategy().getEdgeList().contains(edge));
		// implicit adding of nodes
		assertEquals(2, getStrategy().getNodeCount());
		assertTrue(getStrategy().containsNode(node1));
		assertTrue(getStrategy().getNodeList().contains(node1));
		assertTrue(getStrategy().containsNode(node2));
		assertTrue(getStrategy().getNodeList().contains(node2));
		assertEquals(edge, listener.lastAddEdge);
		// does not work a second time (multigraph not allowed THAT way!)
		listener.lastAddEdge = null;
		assertFalse(getStrategy().addEdge(edge));
		assertNull(listener.lastAddEdge);
		assertEquals(2, getStrategy().getNodeList().size());
		// check that something else works!
		assertFalse(getStrategy().containsEdge(edge2));
		assertTrue(getStrategy().addEdge(edge2));
		assertTrue(getStrategy().containsEdge(edge2));
		assertEquals(edge2, listener.lastAddEdge);
		// implicit add
		assertTrue(getStrategy().containsNode(node3));
		assertTrue(getStrategy().getNodeList().contains(node3));
		assertEquals(3, getStrategy().getNodeList().size());
		// check special case
		assertFalse(getStrategy().containsEdge(null));
		assertEquals(2, getStrategy().getEdgeList().size());
		assertFalse(getStrategy().addEdge(null));
		// ensure it didn't get stored!
		assertFalse(getStrategy().containsEdge(null));
		assertEquals(2, getStrategy().getEdgeList().size());
		assertEquals(edge2, listener.lastAddEdge);
		assertFalse(getStrategy().containsNode(node4));
		T he1 = getLegalHyperEdge(new Integer[]{node4});
		assertFalse(getStrategy().containsEdge(he1));
		assertTrue(getStrategy().addEdge(he1));
		assertTrue(getStrategy().containsEdge(he1));
		assertEquals(he1, listener.lastAddEdge);
		assertTrue(getStrategy().containsNode(node4));
		assertFalse(getStrategy().containsNode(node5));
		T he3 = getLegalHyperEdge(new Integer[]{node3, node4, node5});
		assertFalse(getStrategy().containsEdge(he3));
		assertTrue(getStrategy().addEdge(he3));
		assertTrue(getStrategy().containsEdge(he3));
		assertEquals(he3, listener.lastAddEdge);
		assertTrue(getStrategy().containsNode(node5));
	}

	@Test
	public void testClear()
	{
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		T edge = getLegalEdge(node1, node2);
		getStrategy().addGraphChangeListener(listener);
		assertFalse(getStrategy().containsEdge(edge));
		assertNull(listener.lastAddEdge);
		assertTrue(getStrategy().isEmpty());
		assertTrue(getStrategy().addEdge(edge));
		assertFalse(getStrategy().isEmpty());
		assertTrue(getStrategy().containsEdge(edge));
		assertTrue(getStrategy().getEdgeList().contains(edge));
		// implicit adding of nodes
		assertEquals(2, getStrategy().getNodeCount());
		assertTrue(getStrategy().containsNode(node1));
		assertTrue(getStrategy().getNodeList().contains(node1));
		assertTrue(getStrategy().containsNode(node2));
		assertTrue(getStrategy().getNodeList().contains(node2));
		assertEquals(edge, listener.lastAddEdge);
		// does not work a second time (multigraph not allowed THAT way!)
		listener.lastAddEdge = null;
		assertFalse(getStrategy().addEdge(edge));
		assertNull(listener.lastAddEdge);
		assertEquals(2, getStrategy().getNodeList().size());
		getStrategy().clear();
		assertEquals(0, getStrategy().getNodeList().size());
		assertTrue(getStrategy().getEdgeList().isEmpty());
		assertTrue(getStrategy().isEmpty());
		// TODO Need to check that clear triggered listener items
	}

	protected abstract T getLegalEdge(Integer node1, Integer node2);

	protected abstract T getLegalHyperEdge(Integer[] integers);

	@Test
	public void testContainsNode()
	{
		Integer node = new Integer(1);
		Integer node2 = new Integer(2);
		assertFalse(getStrategy().containsNode(node));
		assertFalse(getStrategy().containsNode(node2));
		getStrategy().addNode(node);
		assertTrue(getStrategy().containsNode(node));
		assertFalse(getStrategy().containsNode(node2));
		// special case
		assertFalse(getStrategy().containsNode(null));
	}

	@Test
	public void testContainsEdge()
	{
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		T edge = getLegalEdge(node1, node2);
		Integer node3 = new Integer(3);
		T edge2 = getLegalEdge(node1, node3);
		assertFalse(getStrategy().containsEdge(edge));
		assertFalse(getStrategy().containsEdge(edge2));
		assertTrue(getStrategy().addEdge(edge));
		assertTrue(getStrategy().containsEdge(edge));
		assertFalse(getStrategy().containsEdge(edge2));
		// special case
		assertFalse(getStrategy().containsEdge(null));
	}

	@Test
	public void testGetNodeList()
	{
		Integer node = new Integer(1);
		assertTrue(getStrategy().getNodeList().isEmpty());
		getStrategy().addNode(node);
		assertEquals(1, getStrategy().getNodeList().size());
		assertEquals(node, getStrategy().getNodeList().get(0));
		// transfer ownership: ensure I can modify without messing with
		// strategy's list or vice versa
		List<Integer> myList = getStrategy().getNodeList();
		myList.add(new Integer(4));
		assertEquals(1, getStrategy().getNodeList().size());
		assertEquals(2, myList.size());
	}

	@Test
	public void testGetEdgeList()
	{
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		T edge = getLegalEdge(node1, node2);
		getStrategy().addNode(node1);
		getStrategy().addNode(node2);
		assertTrue(getStrategy().getEdgeList().isEmpty());
		getStrategy().addEdge(edge);
		assertEquals(1, getStrategy().getEdgeList().size());
		assertEquals(edge, getStrategy().getEdgeList().get(0));
		// transfer ownership: ensure I can modify without messing with
		// strategy's list or vice versa
		List<T> myList = getStrategy().getEdgeList();
		myList.add(getLegalEdge(node1, new Integer(4)));
		assertEquals(1, getStrategy().getEdgeList().size());
		assertEquals(2, myList.size());
	}

	@Test
	public void testRemoveNode()
	{
		getStrategy().addGraphChangeListener(listener);
		Integer node = new Integer(1);
		Integer node2 = new Integer(2);
		// not in there!
		listener.lastRemoveNode = node2;
		assertFalse(getStrategy().removeNode(node));
		assertEquals(node2, listener.lastRemoveNode);
		assertTrue(getStrategy().addNode(node));
		// simple remove
		listener.lastRemoveNode = null;
		assertTrue(getStrategy().removeNode(node));
		assertEquals(node, listener.lastRemoveNode);
		assertFalse(getStrategy().containsNode(node));
		assertTrue(getStrategy().addNode(node));
		// don't allow a second time
		assertFalse(getStrategy().addNode(node));
		// simple remove after second attempted add
		listener.lastRemoveNode = null;
		assertTrue(getStrategy().removeNode(node));
		assertEquals(node, listener.lastRemoveNode);
		assertFalse(getStrategy().containsNode(node));
		assertTrue(getStrategy().addNode(node));
		// check that something else is not removed!
		assertTrue(getStrategy().addNode(node2));
		listener.lastRemoveNode = null;
		assertTrue(getStrategy().removeNode(node));
		assertEquals(node, listener.lastRemoveNode);
		assertFalse(getStrategy().containsNode(node));
		assertTrue(getStrategy().containsNode(node2));
		// node with adjacent edges (both first and second node)
		assertTrue(getStrategy().addNode(node));
		T tempEdge1 = getLegalEdge(node, node2);
		assertTrue(getStrategy().addEdge(tempEdge1));
		T tempEdge2 = getLegalEdge(node2, node);
		assertTrue(getStrategy().addEdge(tempEdge2));
		assertEquals(2, getStrategy().getNodeList().size());
		assertEquals(2, getStrategy().getEdgeList().size());
		listener.lastRemoveNode = null;
		listener.edgeCount = 0;
		listener.nodeCount = 0;
		assertTrue(getStrategy().removeNode(node));
		assertEquals(node, listener.lastRemoveNode);
		assertEquals(1, listener.nodeCount);
		assertEquals(2, listener.edgeCount);
		assertTrue(listener.lastRemoveEdge == tempEdge1
			|| listener.lastRemoveEdge == tempEdge2);
		// ASSUME the notification of edges works correctly (doesn't check
		// precisely (just that there were two)!)
		assertEquals(1, getStrategy().getNodeList().size());
		assertEquals(0, getStrategy().getEdgeList().size());
		// check special case (no byproducts - just ignore)
		assertEquals(1, getStrategy().getNodeList().size());
		assertFalse(getStrategy().removeNode(null));
		assertEquals(node, listener.lastRemoveNode);
		assertTrue(getStrategy().containsNode(node2));
		assertEquals(1, getStrategy().getNodeList().size());
	}

	@Test
	public void testRemoveEdge()
	{
		getStrategy().addGraphChangeListener(listener);
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		T edge = getLegalEdge(node1, node2);
		T edge2 = getLegalEdge(node2, node3);
		T edge3 = getLegalEdge(node1, node3);
		// not in there!
		listener.lastRemoveEdge = edge3;
		assertFalse(getStrategy().removeEdge(edge));
		// non-notification
		assertEquals(edge3, listener.lastRemoveEdge);
		assertTrue(getStrategy().addEdge(edge));
		// simple remove
		listener.lastRemoveEdge = null;
		assertTrue(getStrategy().removeEdge(edge));
		assertEquals(edge, listener.lastRemoveEdge);
		assertFalse(getStrategy().containsEdge(edge));
		assertTrue(getStrategy().addEdge(edge));
		// don't allow a second time
		assertFalse(getStrategy().addEdge(edge));
		// simple remove after second attempted add
		listener.lastRemoveEdge = null;
		assertTrue(getStrategy().removeEdge(edge));
		assertEquals(edge, listener.lastRemoveEdge);
		assertFalse(getStrategy().containsEdge(edge));
		assertTrue(getStrategy().addEdge(edge));
		// check that something else is not removed!
		assertTrue(getStrategy().addNode(node3));
		assertTrue(getStrategy().addEdge(edge2));
		listener.lastRemoveEdge = null;
		assertTrue(getStrategy().removeEdge(edge));
		assertEquals(edge, listener.lastRemoveEdge);
		assertFalse(getStrategy().containsEdge(edge));
		assertTrue(getStrategy().containsEdge(edge2));
		// node with adjacent edges (both first and second node)
		assertTrue(getStrategy().addEdge(edge3));
		assertEquals(2, getStrategy().getEdgeList().size());
		listener.lastRemoveEdge = null;
		assertTrue(getStrategy().removeEdge(edge2));
		assertEquals(edge2, listener.lastRemoveEdge);
		assertEquals(1, getStrategy().getEdgeList().size());
		// check special case (no byproducts - just ignore)
		assertEquals(1, getStrategy().getEdgeList().size());
		assertFalse(getStrategy().removeEdge(null));
		// non-notification
		assertEquals(edge2, listener.lastRemoveEdge);
		assertTrue(getStrategy().containsEdge(edge3));
		assertEquals(1, getStrategy().getEdgeList().size());
	}

	@Test
	public void testGetAdjacentEdgeList()
	{
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		T edge1 = getLegalEdge(node1, node2);
		T edge2 = getLegalEdge(node2, node3);
		T edge3 = getLegalEdge(node3, node1);
		T edge4 = getLegalEdge(node1, node3);
		assertTrue(getStrategy().addNode(node1));
		assertTrue(getStrategy().addNode(node2));
		assertTrue(getStrategy().addNode(node3));
		assertTrue(getStrategy().addEdge(edge1));
		assertTrue(getStrategy().addEdge(edge2));
		assertTrue(getStrategy().addEdge(edge3));
		assertTrue(getStrategy().addEdge(edge4));
		Collection<T> l = getStrategy().getAdjacentEdges(node1);
		// order is not significant
		assertEquals(3, l.size());
		assertTrue(l.contains(edge1));
		assertTrue(l.contains(edge3));
		assertTrue(l.contains(edge4));
		getStrategy().removeEdge(edge4);
		l = getStrategy().getAdjacentEdges(node1);
		// order is not significant
		assertEquals(2, l.size());
		assertTrue(l.contains(edge1));
		assertTrue(l.contains(edge3));
		getStrategy().removeNode(node2);
		l = getStrategy().getAdjacentEdges(node1);
		// order is not significant
		assertEquals(1, l.size());
		assertTrue(l.contains(edge3));
		// not in graph
		assertNull(getStrategy().getAdjacentEdges(node2));
		// special case
		assertNull(getStrategy().getAdjacentEdges(null));
	}

	@Test
	public void testHasAdjacentEdge()
	{
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		Integer node4 = new Integer(4);
		Integer node5 = new Integer(5);
		T edge1 = getLegalEdge(node1, node2);
		T edge2 = getLegalEdge(node2, node3);
		assertTrue(getStrategy().addNode(node1));
		assertTrue(getStrategy().addNode(node2));
		assertTrue(getStrategy().addNode(node3));
		assertTrue(getStrategy().addNode(node4));
		//Do not add node 5
		assertTrue(getStrategy().addEdge(edge1));
		assertTrue(getStrategy().addEdge(edge2));
		assertTrue(getStrategy().hasAdjacentEdge(node1));
		assertTrue(getStrategy().hasAdjacentEdge(node2));
		assertTrue(getStrategy().hasAdjacentEdge(node3));
		assertFalse(getStrategy().hasAdjacentEdge(node4));
		assertFalse(getStrategy().hasAdjacentEdge(node5));
	}

	@Test
	public void testAddGraphChangeListener()
	{
		assertEquals(0, getStrategy().getGraphChangeListeners().length);
		getStrategy().addGraphChangeListener(listener);
		assertEquals(1, getStrategy().getGraphChangeListeners().length);
		assertEquals(listener, getStrategy().getGraphChangeListeners()[0]);
		// ignore null
		getStrategy().addGraphChangeListener(null);
		assertEquals(1, getStrategy().getGraphChangeListeners().length);
	}

	@Test
	public void testGetGraphChangeListeners()
	{
		assertEquals(0, getStrategy().getGraphChangeListeners().length);
		getStrategy().addGraphChangeListener(listener);
		assertEquals(1, getStrategy().getGraphChangeListeners().length);
		assertEquals(listener, getStrategy().getGraphChangeListeners()[0]);
		TestGraphChangeListener<Integer, T> listener2 =
				new TestGraphChangeListener<>();
		getStrategy().addGraphChangeListener(listener2);
		assertEquals(2, getStrategy().getGraphChangeListeners().length);
		// order is not significant!
		List<GraphChangeListener<Integer, T>> l =
				Arrays.asList(getStrategy().getGraphChangeListeners());
		assertTrue(l.contains(listener));
		assertTrue(l.contains(listener2));
	}

	@Test
	public void testRemoveGraphChangeListener()
	{
		getStrategy().addGraphChangeListener(listener);
		assertEquals(1, getStrategy().getGraphChangeListeners().length);
		assertEquals(listener, getStrategy().getGraphChangeListeners()[0]);
		// ignore non-entry
		TestGraphChangeListener<Integer, T> listener2 =
				new TestGraphChangeListener<>();
		getStrategy().removeGraphChangeListener(listener2);
		assertEquals(1, getStrategy().getGraphChangeListeners().length);
		assertEquals(listener, getStrategy().getGraphChangeListeners()[0]);
		// ignore null
		getStrategy().removeGraphChangeListener(null);
		assertEquals(1, getStrategy().getGraphChangeListeners().length);
		// actually work!
		getStrategy().addGraphChangeListener(listener2);
		assertEquals(2, getStrategy().getGraphChangeListeners().length);
		getStrategy().removeGraphChangeListener(listener);
		assertEquals(1, getStrategy().getGraphChangeListeners().length);
		assertEquals(listener2, getStrategy().getGraphChangeListeners()[0]);
	}

	@Test
	public void testEquals()
	{
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		Integer node3 = new Integer(3);
		Integer node4 = new Integer(4);
		T edge1 = getLegalEdge(node1, node3);
		T edge2 = getLegalEdge(node3, node4);
		Graph<Integer, T> testGraph = getStrategy();
		assertNotNull(testGraph);
		assertFalse(testGraph.equals(node1));
		assertTrue(testGraph.equals(master));
		assertEquals(testGraph.hashCode(), master.hashCode());
		assertTrue(master.addNode(node1));
		assertEquals(1, master.getNodeList().size());
		assertFalse(testGraph.equals(master));
		master.removeNode(node1);
		assertTrue(master.getNodeList().isEmpty());
		assertEquals(0, master.getNodeList().size());
		assertTrue(testGraph.equals(master));
		assertEquals(testGraph.hashCode(), master.hashCode());
		assertTrue(testGraph.addNode(node1));
		assertEquals(1, testGraph.getNodeList().size());
		assertFalse(testGraph.equals(master));
		assertTrue(master.addNode(node4));
		assertEquals(1, master.getNodeList().size());
		assertEquals(1, testGraph.getNodeList().size());
		assertFalse(testGraph.equals(master));
		assertTrue(testGraph.addNode(node4));
		assertEquals(2, testGraph.getNodeList().size());
		assertFalse(testGraph.equals(master));
		assertTrue(master.addNode(node3));
		assertEquals(2, master.getNodeList().size());
		assertEquals(2, testGraph.getNodeList().size());
		assertFalse(testGraph.equals(master));
		assertTrue(testGraph.addNode(node3));
		assertEquals(2, master.getNodeList().size());
		assertEquals(3, testGraph.getNodeList().size());
		assertFalse(testGraph.equals(master));
		assertTrue(master.addNode(node1));
		assertEquals(3, master.getNodeList().size());
		assertEquals(3, testGraph.getNodeList().size());
		assertTrue(testGraph.equals(master));
		assertEquals(testGraph.hashCode(), master.hashCode());
		assertTrue(master.addEdge(edge1));
		assertFalse(testGraph.equals(master));
		master.removeEdge(edge1);
		assertTrue(testGraph.equals(master));
		assertEquals(testGraph.hashCode(), master.hashCode());
		assertTrue(testGraph.addEdge(edge2));
		assertFalse(testGraph.equals(master));
		assertTrue(master.addEdge(edge1));
		assertFalse(testGraph.equals(master));
		assertTrue(testGraph.addEdge(edge1));
		assertFalse(testGraph.equals(master));
		assertTrue(master.addEdge(edge2));
		assertTrue(testGraph.equals(master));
		assertEquals(testGraph.hashCode(), master.hashCode());
		assertTrue(testGraph.addNode(node2));
		assertFalse(testGraph.equals(master));
		// Yes, NEW, not valueOf
		assertTrue(master.addNode(new Integer(2)));
		// Test for .equals of the nodes
		assertTrue(testGraph.equals(master));
		assertEquals(testGraph.hashCode(), master.hashCode());
		// testGraph.addEdge(edge3);
		// assertFalse(testGraph.equals(master));
		// master.addEdge(altEdge3);
		// //Test for .equals of the edges
		// assertFalse(edge3 == altEdge3);
		// assertTrue(testGraph.equals(master));
		// assertEquals(testGraph.hashCode(), master.hashCode());
	}

	/**
	 * @return Returns the strategy.
	 */
	public abstract Graph<Integer, T> getStrategy();

}
