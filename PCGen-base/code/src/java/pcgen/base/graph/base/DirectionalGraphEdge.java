/*
 * Copyright (c) Thomas Parker, 2020.
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

import java.util.Collections;
import java.util.List;

/**
 * DirectionalGraphEdge is a combination of GraphEdge and DirectionalEdge and provides
 * default implementations for such.
 *
 * @param <N>
 *            The type of Node stored in this Edge
 */
public interface DirectionalGraphEdge<N> extends GraphEdge<N>, DirectionalEdge<N>
{
	@Override
	public default boolean isSource(N node)
	{
		return getNodeAt(0).equals(node);
	}

	@Override
	public default boolean isSink(N node)
	{
		return getNodeAt(1).equals(node);
	}

	@Override
	public default List<N> getSinkNodes()
	{
		return Collections.singletonList(getNodeAt(1));
	}

	@Override
	public default List<N> getSourceNodes()
	{
		return Collections.singletonList(getNodeAt(0));
	}
}
