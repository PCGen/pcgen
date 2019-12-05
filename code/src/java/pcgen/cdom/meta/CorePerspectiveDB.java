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
import java.util.Map;

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

    private static DoubleKeyMap<CorePerspective, Object, FacetView<?>> map = new DoubleKeyMap<>();
    private static HashMap<CorePerspective, FacetView<?>> rootmap = new HashMap<>();
    private static HashMap<Object, FacetView<?>> facetToView = new HashMap<>();
    private static Map<Object, CorePerspective> facetToPerspective = new HashMap<>();
    private static HashMapToList<Object, Object> virtualParents = new HashMapToList<>();

    public static <S, D> Object register(CorePerspective perspective, FacetBehavior behavior,
            AbstractItemConvertingFacet<S, D> facet)
    {
        FacetView<Object> view = new ConvertingFacetView<>(facet);
        finishRegistration(perspective, behavior, view, facet);
        return view;
    }

    public static <T> Object register(CorePerspective perspective, FacetBehavior behavior,
            AbstractSourcedListFacet<CharID, T> facet)
    {
        FacetView<T> view = new ListFacetView<>(facet);
        finishRegistration(perspective, behavior, view, facet);
        return view;
    }

    public static <T> Object register(CorePerspective perspective, FacetBehavior behavior,
            AbstractSingleSourceListFacet<T, ?> facet)
    {
        FacetView<T> view = new SingleSourceListFacetView<>(facet);
        finishRegistration(perspective, behavior, view, facet);
        return view;
    }

    public static <T extends QualifyingObject> Object register(CorePerspective perspective, FacetBehavior behavior,
            AbstractQualifiedListFacet<T> facet)
    {
        FacetView<T> view = new QualifiedFacetView<>(facet);
        finishRegistration(perspective, behavior, view, facet);
        return view;
    }

    private static void finishRegistration(CorePerspective perspective, FacetBehavior behavior, FacetView<?> view,
            Object f)
    {
        Object location = behavior;
        if (f instanceof PerspectiveLocation)
        {
            location = new Location(behavior, ((PerspectiveLocation) f).getIdentity());
        }
        map.put(perspective, location, view);
        facetToView.put(f, view);
        facetToPerspective.put(f, perspective);
        if (FacetBehavior.MODEL.equals(behavior))
        {
            rootmap.put(perspective, view);
        }
    }

    private static final class Location
    {

        private final String location;

        private Location(FacetBehavior behavior, String source)
        {
            location = String.valueOf(behavior) + " (" + source + ")";
        }

        @Override
        public String toString()
        {
            return location;
        }
    }

    public static Collection<Object> getLocations(CorePerspective perspective)
    {
        return map.getSecondaryKeySet(perspective);
    }

    public static <T> FacetView<T> getView(CorePerspective perspective, Object location)
    {
        @SuppressWarnings("unchecked")
        FacetView<T> facetView = (FacetView<T>) map.get(perspective, location);
        return facetView;
    }

    public static <T> FacetView<T> getRootFacet(CorePerspective perspective)
    {
        @SuppressWarnings("unchecked")
        FacetView<T> facetView = (FacetView<T>) rootmap.get(perspective);
        return facetView;
    }

    public static <T> FacetView<T> getViewOfFacet(Object o)
    {
        @SuppressWarnings("unchecked")
        FacetView<T> facetView = (FacetView<T>) facetToView.get(o);
        return facetView;
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
            throw new IllegalStateException("Cannot register virtual parent when object is not yet registered");
        }
        virtualParents.addToListFor(view, parent);
    }

    public static Collection<Object> getVirtualParents(Object obj)
    {
        return virtualParents.getListFor(obj);
    }
}
