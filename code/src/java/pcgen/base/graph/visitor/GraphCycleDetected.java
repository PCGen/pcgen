/*
 * Copyright (c) Thomas Parker, 2013
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
 * 
 * Created on Oct 12, 2004
 */
package pcgen.base.graph.visitor;

import java.util.Collection;
import java.util.Collections;

public class GraphCycleDetected extends RuntimeException
{

	private final Collection<?> nodes;
	private final Object loopitem;

	public GraphCycleDetected(Object node, Collection<?> currentNodes)
	{
		nodes = Collections.unmodifiableCollection(currentNodes);
		loopitem = node;
	}

	public Collection<?> getNodes()
	{
		return nodes;
	}

	public Object getLoopItem()
	{
		return loopitem;
	}
}