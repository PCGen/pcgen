/*
 * Copyright (c) Thomas Parker, 2013.
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
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Identified;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.helper.AllowUtilities;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.meta.CorePerspectiveDB;
import pcgen.cdom.meta.CoreViewNodeBase;
import pcgen.cdom.meta.FacetView;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.facade.core.CoreViewNodeFacade;
import pcgen.util.Logging;

final class CoreUtils
{
    private CoreUtils()
    {
    }

    static <T> List<CoreViewNodeFacade> buildCoreDebugList(PlayerCharacter pc, CorePerspective pers)
    {
        CharID id = pc.getCharID();
        List<CoreViewNodeFacade> coreViewList = new ArrayList<>();
        Collection<Object> locations = CorePerspectiveDB.getLocations(pers);
        MapToList<Object, FacetView<T>> sources = new HashMapToList<>();
        Map<FacetView<T>, CoreViewNodeBase> facetToNode = new HashMap<>();

        /*
         * Create the nodes that are part of this perspective.
         */
        for (Object location : locations)
        {
            //Create (w/ identifier)
            FacetView<T> view = CorePerspectiveDB.getView(pers, location);
            CoreViewNodeBase node = new LocationCoreViewNode<>(location);
            facetToNode.put(view, node);
            coreViewList.add(node);
            //Store what facets listen to my content (for use later)
            for (Object listener : view.getChildren())
            {
                Object lView = CorePerspectiveDB.getViewOfFacet(listener);
                Object src = (lView == null) ? listener : lView;
                sources.addToListFor(src, view);
            }
            Collection<Object> parents = CorePerspectiveDB.getVirtualParents(view);
            if (parents != null)
            {
                for (Object parent : parents)
                {
                    FacetView<T> parentView = CorePerspectiveDB.getViewOfFacet(parent);
                    if (parentView == null)
                    {
                        Logging.errorPrint("Expected " + parent + " to be a registered Facet in Perspective " + pers);
                    }
                    sources.addToListFor(view, parentView);
                }
            }
        }
        for (Object location : locations)
        {
            FacetView<T> view = CorePerspectiveDB.getView(pers, location);
            CoreViewNodeBase node = facetToNode.get(view);
            /*
             * Check the source of each child to identify if:
             *
             * (a) The source is a Loadable that can thus be identified as such
             *
             * (b) The source is a known facet (and thus is identified as such)
             *
             * (c) the source is not something recognized
             */
            for (T obj : view.getSet(id))
            {
                List<String> sourceDesc = new ArrayList<>();
                for (Object src : view.getSources(id, obj))
                {
                    if (src instanceof Identified)
                    {
                        sourceDesc.add(getLoadID(src));
                    } else
                    {
                        FacetView<Object> srcView = CorePerspectiveDB.getViewOfFacet(src);
                        if (srcView == null)
                        {
                            //Not a recognized view
                            sourceDesc.add("Orphaned [" + src.getClass().getSimpleName() + "]");
                        } else if (facetToNode.get(srcView) == null)
                        {
                            //A View, but not part of this perspective
                            sourceDesc.add("Other Perspective [" + CorePerspectiveDB.getPerspectiveOfFacet(src) + ": "
                                    + srcView.getDescription() + "]");
                        }
                    }
                }
                //Insert the contents of the facet as children of this node
                ObjectCoreViewNode<T> sourceNode = new ObjectCoreViewNode<>(pc, obj, sourceDesc);
                sourceNode.addGrantedByNode(node);
                coreViewList.add(sourceNode);
            }
        }
        /*
         * For each location, put sources as children in the tree
         */
        for (Object location : locations)
        {
            FacetView<T> view = CorePerspectiveDB.getView(pers, location);
            CoreViewNodeBase node = facetToNode.get(view);
            List<FacetView<T>> facetInputs = sources.getListFor(view);
            if (facetInputs != null)
            {
                for (FacetView<T> facet : facetInputs)
                {
                    facetToNode.get(facet).addGrantedByNode(node);
                }
            }
        }
        return coreViewList;
    }

    private static <T> String getLoadID(T obj)
    {
        if (obj instanceof Identified)
        {
            Identified l = (Identified) obj;
            String name = l.getDisplayName();
            String id = obj.getClass().getSimpleName() + ": " + name;
            if (!l.getKeyName().equals(name))
            {
                id = id + " [" + l.getKeyName() + "]";
            }
            return id;
        } else if (obj instanceof QualifiedObject)
        {
            QualifiedObject<?> qo = (QualifiedObject<?>) obj;
            return getLoadID(qo.getRawObject());
        } else if (obj instanceof CDOMReference)
        {
            CDOMReference<?> ref = (CDOMReference<?>) obj;
            return ref.getReferenceClass().getSimpleName() + " Primitive: " + ref.getLSTformat(false);
        } else
        {
            return obj.getClass().getSimpleName() + ": " + obj.toString();
        }
    }

    private static String getRequirementsInfo(PlayerCharacter pc, Object object)
    {
        if (object instanceof PrereqObject)
        {
            CDOMObject source = null;
            if (object instanceof CDOMObject)
            {
                source = ((CDOMObject) object);
            }
            String sb = "<html>"
                    + PrerequisiteUtilities.preReqHTMLStringsForList(pc, source, source.getPrerequisiteList(), false)
                    + AllowUtilities.getAllowInfo(pc, source)
                    + "</html>";
            return sb;
        }
        return "";
    }

    private static class LocationCoreViewNode<T> extends CoreViewNodeBase
    {

        private final Object object;

        /**
         * Create a new instance of CoreUtils.LocationCoreViewNode
         */
        public LocationCoreViewNode(Object object)
        {
            this.object = object;
        }

        @Override
        public String getNodeType()
        {
            return "Location";
        }

        @Override
        public String getKey()
        {
            return object.toString();
        }

        @Override
        public String getSource()
        {
            return "";
        }

        @Override
        public String getRequirements()
        {
            return "";
        }

        @Override
        public String toString()
        {
            return getLoadID(object);
        }

    }

    private static final class ObjectCoreViewNode<T> extends CoreViewNodeBase
    {

        private final T object;
        private final List<String> sourceDesc;
        private final PlayerCharacter pc;

        /**
         * Create a new instance of CoreUtils.LocationCoreViewNode
         */
        private ObjectCoreViewNode(PlayerCharacter pc, T object, List<String> sourceDesc)
        {
            this.pc = pc;
            this.object = object;
            this.sourceDesc = sourceDesc;
        }

        @Override
        public String getNodeType()
        {
            return "Source";
        }

        @Override
        public String getKey()
        {
            if (object instanceof CDOMObject)
            {
                return ((CDOMObject) object).getKeyName();
            }
            return object.toString();
        }

        @Override
        public String getSource()
        {
            return StringUtil.join(sourceDesc, ", ");
        }

        @Override
        public String getRequirements()
        {
            return CoreUtils.getRequirementsInfo(pc, object);
        }

        @Override
        public String toString()
        {
            return getLoadID(object);
        }
    }
}
