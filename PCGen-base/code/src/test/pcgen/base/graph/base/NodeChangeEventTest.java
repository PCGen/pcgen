/*
 * Copyright (c) Thomas Parker, 2015.
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
package pcgen.base.graph.base;

import pcgen.base.graph.inst.SimpleListMapGraph;
import junit.framework.TestCase;

public class NodeChangeEventTest extends TestCase
{

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		// No setup required
	}

	@SuppressWarnings("unused")
	public void testEdgeChangeEvent()
	{
		try
		{
			new NodeChangeEvent<>(null, new Object(),
					NodeChangeEvent.NODE_ADDED
			);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			new NodeChangeEvent<>(
					new SimpleListMapGraph<>(), null,
					NodeChangeEvent.NODE_ADDED
			);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
	}

}
