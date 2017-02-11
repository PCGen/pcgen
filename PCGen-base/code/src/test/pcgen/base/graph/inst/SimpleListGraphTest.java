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

import java.util.Arrays;

import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.Graph;

public class SimpleListGraphTest extends AbstractGraphTestCase<Edge<Integer>>
{

	private Graph<Integer, Edge<Integer>> strategy;

	@Override
	protected DefaultHyperEdge<Integer> getLegalHyperEdge(Integer[] gna2)
	{
		return new DefaultHyperEdge<>(Arrays.asList(gna2));
	}

	@Override
	protected Edge<Integer> getLegalEdge(Integer node1, Integer node2)
	{
		return new DefaultGraphEdge<>(node1, node2);
	}

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		strategy = new SimpleListGraph<>();
	}

	/**
	 * @return Returns the strategy.
	 */
	@Override
	Graph<Integer, Edge<Integer>> getStrategy()
	{
		return strategy;
	}
}
