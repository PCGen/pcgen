/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.graph.util;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import pcgen.base.graph.base.Edge;
import pcgen.base.graph.base.Graph;
import pcgen.base.graph.base.GraphChangeListener;

/**
 * Constructs an unmodifiable view of a Graph.
 *
 * @param <N>
 *            The format of the nodes of this UnmodifiableGraph
 * @param <E>
 *            The format of the edges of this UnmodifiableGraph
 */
public class UnmodifiableGraph<N, E extends Edge<N>> implements Graph<N, E>
{

	/**
	 * The underlying Graph for this UnmodifiableGraph;
	 */
	private final Graph<N, E> underlying;

	/**
	 * Constructs a new UnmodifiableGraph from the given underlying Graph.
	 * 
	 * @param graph
	 *            The underlying Graph for this UnmodifiableGraph
	 */
	public UnmodifiableGraph(Graph<N, E> graph)
	{
		this.underlying = Objects.requireNonNull(graph);
	}

	@Override
	public boolean addNode(N node)
	{
		throw new UnsupportedOperationException("addNode");
	}

	@Override
	public boolean addEdge(E edge)
	{
		throw new UnsupportedOperationException("addEdge");
	}

	@Override
	public boolean containsNode(Object obj)
	{
		return underlying.containsNode(obj);
	}

	@Override
	public boolean containsEdge(Edge<?> edge)
	{
		return underlying.containsEdge(edge);
	}

	@Override
	public int getNodeCount()
	{
		return underlying.getNodeCount();
	}

	@Override
	public List<N> getNodeList()
	{
		return underlying.getNodeList();
	}

	@Override
	public List<E> getEdgeList()
	{
		return underlying.getEdgeList();
	}

	@Override
	public boolean removeNode(N node)
	{
		throw new UnsupportedOperationException("removeNode");
	}

	@Override
	public boolean removeEdge(E edge)
	{
		throw new UnsupportedOperationException("removeEdge");
	}

	@Override
	public boolean hasAdjacentEdge(N node)
	{
		return underlying.hasAdjacentEdge(node);
	}

	@Override
	public Collection<E> getAdjacentEdges(N node)
	{
		return underlying.getAdjacentEdges(node);
	}

	@Override
	public void addGraphChangeListener(GraphChangeListener<N, E> listener)
	{
		throw new UnsupportedOperationException("addGraphChangeListener");
	}

	@Override
	public GraphChangeListener<N, E>[] getGraphChangeListeners()
	{
		return underlying.getGraphChangeListeners();
	}

	@Override
	public void removeGraphChangeListener(GraphChangeListener<N, E> listener)
	{
		throw new UnsupportedOperationException("removeGraphChangeListener");
	}

	@Override
	public boolean isEmpty()
	{
		return underlying.isEmpty();
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException("clear");
	}
}
