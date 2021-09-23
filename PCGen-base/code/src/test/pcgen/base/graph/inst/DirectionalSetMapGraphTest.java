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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.graph.base.DirectionalEdge;
import pcgen.base.graph.base.DirectionalGraph;

/**
 * Test the DirectionalSetMapGraph class
 */
public class DirectionalSetMapGraphTest extends AbstractDirectionalMapGraphTest
{

	//TODO Need to test .equals behavior on nodes and edges

	private DirectionalSetMapGraph<Integer, DirectionalEdge<Integer>> strategy;

	@Override
	@BeforeEach
	void setUp()
	{
		super.setUp();
		strategy = new DirectionalSetMapGraph<>();
	}

	@Override
	@AfterEach
	void tearDown()
	{
		super.tearDown();
		strategy = null;
	}

	@Override
	public DirectionalGraph<Integer, DirectionalEdge<Integer>> getStrategy()
	{
		return strategy;
	}

	@Test
	public void testGetInternalizedNode()
	{
		Integer node = new Integer(1);
		Integer node2 = new Integer(2);
		//This must be NEW integer to test Instances not equals equality
		Integer falseNode1 = new Integer(1);
		assertFalse(strategy.containsNode(node));
		assertFalse(strategy.containsNode(node2));
		assertFalse(strategy.containsNode(falseNode1));
		assertEquals(0, strategy.getNodeList().size());
		// No nodes are in the graph, so response is null
		assertNull(strategy.getInternalizedNode(null));
		assertNull(strategy.getInternalizedNode(node));
		assertNull(strategy.getInternalizedNode(node2));
		assertNull(strategy.getInternalizedNode(falseNode1));

		assertTrue(strategy.addNode(node));
		assertTrue(strategy.containsNode(node));
		assertFalse(strategy.containsNode(node2));
		// Note that this returns true due to .equals()
		assertTrue(strategy.containsNode(falseNode1));
		// But that an instance test will fail
		for (Integer i : strategy.getNodeList())
		{
			//Note: Must be INSTANCE IDENTITY, not .equals
			assertTrue(i == node || i == node2);
			//Note: Must be INSTANCE IDENTITY, not .equals
			assertTrue(i != falseNode1);
		}
		assertTrue(node == strategy.getInternalizedNode(node));
		assertNull(strategy.getInternalizedNode(node2));
		// And getInternalizedNode will actually return the instance
		// that is .equal() to the given node, not the given node.
		assertTrue(node == strategy.getInternalizedNode(falseNode1));
	}

}
