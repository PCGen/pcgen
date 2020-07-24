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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pcgen.base.graph.inst.SimpleListMapGraph;

/**
 * Test the NodeChangeEvent class
 */
public class NodeChangeEventTest
{
	@Test
	public void testEdgeChangeEvent()
	{
		assertThrows(IllegalArgumentException.class, () -> new NodeChangeEvent<>(null, new Object(),
					NodeChangeEvent.NODE_ADDED
			));
		assertThrows(NullPointerException.class, () -> new NodeChangeEvent<>(
					new SimpleListMapGraph<>(), null,
					NodeChangeEvent.NODE_ADDED
			));
	}

}
