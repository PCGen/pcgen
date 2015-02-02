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

import java.util.Collection;
import java.util.HashMap;

import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemConvertingFacet;
import pcgen.cdom.facet.base.AbstractQualifiedListFacet;
import pcgen.cdom.facet.base.AbstractSingleSourceListFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;

public final class CorePerspectiveDB
{

	private CorePerspectiveDB()
	{
		//Do not construct utility class
	}

	private static DoubleKeyMap<CorePerspective, Object, FacetView<?>> map =
			new DoubleKeyMap<CorePerspective, Object, FacetView<?>>();
	private static HashMap<CorePerspective, FacetView<?>> rootmap =
			new HashMap<CorePerspective, FacetView<?>>();
	private static HashMap<Object, FacetView<?>> facetToView =
			new HashMap<Object, FacetView<?>>();
	private static HashMap<Object, CorePerspective> facetToPerspective =
			new HashMap<Object, CorePerspective>();
	private static HashMapToList<Object, Object> virtualParents =
			new HashMapToList<Object, Object>();

	public static <S, D> Object register(CorePerspective perspective,
		FacetBehavior behavior, AbstractItemConvertingFacet<S, D> facet)
	{
		FacetView<Object> view = new ConvertingFacetView<S, D>(facet);
		finishRegistration(perspective, behavior, view, facet);
		return view;
	}

	public static <T> Object register(CorePerspective perspective,
		FacetBehavior behavior, AbstractSourcedListFacet<CharID, T> facet)
	{
		FacetView<T> view = new ListFacetView<T>(facet);
		finishRegistration(perspective, behavior, view, facet);
		return view;
	}

	public static <T> Object register(CorePerspective perspective,
		FacetBehavior behavior, AbstractSingleSourceListFacet<T, ?> facet)
	{
		FacetView<T> view = new SingleSourceListFacetView<T>(facet);
		finishRegistration(perspective, behavior, view, facet);
		return view;
	}

	public static <T extends QualifyingObject> Object register(
		CorePerspective perspective, FacetBehavior behavior,
		AbstractQualifiedListFacet<T> facet)
	{
		FacetView<T> view = new QualifiedFacetView<T>(facet);
		finishRegistration(perspective, behavior, view, facet);
		return view;
	}

	private static void finishRegistration(CorePerspective perspective,
		FacetBehavior behavior, FacetView<?> view, Object f)
	{
		Object location = behavior;
		if (f instanceof PerspectiveLocation)
		{
			location =
					new Location(behavior,
						((PerspectiveLocation) f).getIdentity());
		}
		map.put(perspective, location, view);
		facetToView.put(f, view);
		facetToPerspective.put(f, perspective);
		if (FacetBehavior.MODEL.equals(behavior))
		{
			rootmap.put(perspective, view);
		}
	}

	private static class Location
	{

		private final String location;

		public Location(FacetBehavior behavior, String source)
		{
			location =
					new StringBuilder(40).append(behavior).append(" (")
						.append(source).append(")").toString();
		}

		@Override
		public String toString()
		{
			return location;
		}
	}

	public static Collection<CorePerspective> getPerspectives()
	{
		return map.getKeySet();
	}

	public static Collection<Object> getLocations(CorePerspective perspective)
	{
		return map.getSecondaryKeySet(perspective);
	}

	public static <T> FacetView<T> getView(CorePerspective perspective,
		Object location)
	{
		return (FacetView<T>) map.get(perspective, location);
	}

	public static <T> FacetView<T> getRootFacet(CorePerspective perspective)
	{
		return (FacetView<T>) rootmap.get(perspective);
	}

	public static <T> FacetView<T> getViewOfFacet(Object o)
	{
		return (FacetView<T>) facetToView.get(o);
	}

	public static <T> CorePerspective getPerspectiveOfFacet(Object o)
	{
		return facetToPerspective.get(o);
	}

	public static void registerVirtualParent(Object obj, Object parent)
	{
		FacetView<?> view = facetToView.get(obj);
		if (view == null)
		{
			throw new IllegalStateException(
				"Cannot register virtual parent when object is not yet registered");
		}
		virtualParents.addToListFor(view, parent);
	}

	public static Collection<Object> getVirtualParents(Object obj)
	{
		return virtualParents.getListFor(obj);
	}
}
