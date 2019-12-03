/*
 * Copyright (c) Thomas Parker, 2013-14.
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
package pcgen.cdom.meta;

import java.util.ArrayList;
import java.util.Collection;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemConvertingFacet;

public class ConvertingFacetView<S, D> implements FacetView<Object>
{

	private AbstractItemConvertingFacet<S, D> facet;

	ConvertingFacetView(AbstractItemConvertingFacet<S, D> facet)
	{
		this.facet = facet;
	}

	@Override
	public Collection<?> getSet(CharID id)
	{
		Collection<S> sources = facet.getSourceObjects(id);
		Collection<SourceDest> list = new ArrayList<>(sources.size());
		for (S src : sources)
		{
			D dest = facet.getResultFor(id, src);
			list.add(new SourceDest(src, dest));
		}
		return list;
	}

	@Override
	public Collection<Object> getSources(CharID id, Object obj)
	{
		return facet.getSourcesFor(id, ((SourceDest) obj).getSource());
	}

	@Override
	public Object[] getChildren()
	{
		return facet.getDataFacetChangeListeners();
	}

	@Override
	public String getDescription()
	{
		return facet.getClass().getSimpleName();
	}

	@Override
	public boolean represents(Object src)
	{
		return facet.equals(src);
	}

	@Override
	public String toString()
	{
		return "Facet: " + facet.getClass().getSimpleName();
	}

	private final class SourceDest
	{
		private final S source;
		private final D destination;

		public S getSource()
		{
			return source;
		}

		private SourceDest(S source, D destination)
		{
			this.source = source;
			this.destination = destination;
		}

		@Override
		public String toString()
		{
			return source + " -> " + destination;
		}
	}
}
