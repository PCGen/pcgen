/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import java.util.List;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.Qualifier;

/**
 * QualifyFacet is a Facet that tracks the objects to which the Player Character
 * should Qualify.
 */
public class QualifyFacet implements DataFacetChangeListener<CDOMObject>
{

	private final Class<?> thisClass = getClass();

	/**
	 * Triggered when one of the Facets to which QualifyFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		List<Qualifier> qualList = cdo.getListFor(ListKey.QUALIFY);
		CacheInfo ci = getCacheInfo(dfce.getCharID());

		if (qualList != null)
		{
			for (Qualifier q : qualList)
			{
				ci.add(q, cdo);
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which QualifyFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CacheInfo ci = (CacheInfo) FacetCache.get(id, thisClass);

		if (ci != null)
		{
			ci.removeAll(dfce.getCDOMObject());
		}
	}

	private CacheInfo getCacheInfo(CharID id)
	{
		CacheInfo ci = (CacheInfo) FacetCache.get(id, thisClass);
		if (ci == null)
		{
			ci = new CacheInfo();
			FacetCache.set(id, thisClass, ci);
		}
		return ci;
	}

	private static class CacheInfo
	{
		HashMapToList<Class<? extends Loadable>, Qualifier> hml = new HashMapToList<Class<? extends Loadable>, Qualifier>();
		HashMapToList<CDOMObject, Qualifier> sourceMap = new HashMapToList<CDOMObject, Qualifier>();

		public void add(Qualifier q, CDOMObject source)
		{
			hml.addToListFor(q.getQualifiedClass(), q);
			sourceMap.addToListFor(source, q);
		}

		public void removeAll(CDOMObject object)
		{
			List<Qualifier> list = sourceMap.removeListFor(object);
			if (list != null)
			{
				for (Qualifier q : list)
				{
					hml.removeFromListFor(q.getQualifiedClass(), q);
				}
			}
		}

		public boolean isQualified(Loadable qualTestObject)
		{
			Class<? extends Loadable> cl = qualTestObject.getClass();
			List<Qualifier> list = hml.getListFor(cl);
			if (list != null)
			{
				for (Qualifier q : list)
				{
					CDOMReference qRef = q.getQualifiedReference();
					if (qRef.contains(qualTestObject))
					{
						return true;
					}
				}
			}
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return hml.hashCode();
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			if (o instanceof CacheInfo)
			{
				CacheInfo ci = (CacheInfo) o;
				return ci.hml.equals(hml) && ci.sourceMap.equals(sourceMap);
			}
			return false;
		}
	}

	public boolean grantsQualify(CharID id, CDOMObject qualTestObject)
	{
		CacheInfo ci = (CacheInfo) FacetCache.get(id, thisClass);
		return (ci != null) && ci.isQualified(qualTestObject);
	}
}