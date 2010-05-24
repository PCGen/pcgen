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
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.Qualifier;

/**
 * ShieldProfFacet is a Facet that tracks the ShieldProfs that have been granted
 * to a Player Character.
 */
public class QualifyFacet implements DataFacetChangeListener<CDOMObject>
{

	private final Class<?> thisClass = getClass();

	/**
	 * Triggered when one of the Facets to which ShieldProfFacet listens fires a
	 * DataFacetChangeEvent to indicate a ShieldProf was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
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
	 * Triggered when one of the Facets to which ShieldProfFacet listens fires a
	 * DataFacetChangeEvent to indicate a ShieldProf was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
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

	private class CacheInfo
	{
		HashMapToList<Class<? extends CDOMObject>, Qualifier> hml = new HashMapToList<Class<? extends CDOMObject>, Qualifier>();
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

		public boolean isQualified(CDOMObject qualTestObject)
		{
			Class<? extends CDOMObject> cl = qualTestObject.getClass();
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
	}

	public boolean grantsQualify(CharID id, CDOMObject qualTestObject)
	{
		CacheInfo ci = (CacheInfo) FacetCache.get(id, thisClass);
		return (ci != null) && ci.isQualified(qualTestObject);
	}
}