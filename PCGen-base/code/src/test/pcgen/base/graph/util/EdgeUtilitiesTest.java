/*
 * Copyright (c) Thomas Parker, 2018.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.graph.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.graph.base.Edge;
import pcgen.base.graph.inst.DefaultHyperEdge;
import pcgen.testsupport.TestSupport;

/**
 * Test the EdgeUtilities class
 */
public class EdgeUtilitiesTest
{

	private DefaultHyperEdge<Integer> edge1;

	private Integer node1, node2, node3;

	@BeforeEach
	void setUp()
	{
		node1 = new Integer(3);
		node2 = new Integer(4);
		node3 = new Integer(6);
		edge1 = new DefaultHyperEdge<>(Arrays.asList(new Integer[]{node2, node1, node3}));
	}
	
	@AfterEach
	void tearDown()
	{
		node1 = null;
		node2 = null;
		node3 = null;
		edge1 = null;
	}

	@Test
	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(EdgeUtilities.class);
	}
	
	@Test
	public void testGetNodeAt()
	{
		Function<Edge<Integer>, Integer> get0 = EdgeUtilities.getNode(0);
		assertEquals(node2, get0.apply(edge1));
		Function<Edge<Integer>, Integer> get1 = EdgeUtilities.getNode(1);
		assertEquals(node1, get1.apply(edge1));
		Function<Edge<Integer>, Integer> get2 = EdgeUtilities.getNode(2);
		assertEquals(node3, get2.apply(edge1));
	}
}
