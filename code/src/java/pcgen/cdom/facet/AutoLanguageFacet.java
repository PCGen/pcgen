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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractQualifiedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.meta.CorePerspectiveDB;
import pcgen.cdom.meta.FacetBehavior;
import pcgen.cdom.meta.PerspectiveLocation;
import pcgen.core.Language;
import pcgen.core.QualifiedObject;

/**
 * AutoLanguageFacet is a Facet that tracks the Languages that have been granted
 * to a Player Character through the AUTO:LANG and LANGAUTO tokens
 */
public class AutoLanguageFacet extends AbstractQualifiedListFacet<QualifiedObject<CDOMReference<Language>>>
        implements DataFacetChangeListener<CharID, CDOMObject>, PerspectiveLocation
{

    private AutoLanguageUnconditionalFacet autoLanguageUnconditionalFacet;

    /**
     * Processes CDOMObjects added to a Player Character to extract Languages
     * granted to the Player Character through the AUTO:LANG: and LANGAUTO:
     * tokens. The extracted languages are added to the Player Character.
     * <p>
     * Triggered when one of the Facets to which AutoLanguageFacet listens fires
     * a DataFacetChangeEvent to indicate a CDOMObjectwas added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        CharID id = dfce.getCharID();
        // LANGAUTO
        List<QualifiedObject<CDOMReference<Language>>> list = cdo.getSafeListFor(ListKey.AUTO_LANGUAGES);
        if (list != null)
        {
            separateAll(id, list, cdo);
        }
        // AUTO:LANG
        list = cdo.getSafeListFor(ListKey.AUTO_LANGUAGE);
        if (list != null)
        {
            separateAll(id, list, cdo);
        }
    }

    private void separateAll(CharID id, List<QualifiedObject<CDOMReference<Language>>> list, CDOMObject cdo)
    {
        for (QualifiedObject<CDOMReference<Language>> qRef : list)
        {
            if (qRef.hasPrerequisites())
            {
                add(id, qRef, cdo);
            } else
            {
                autoLanguageUnconditionalFacet.addAll(id, qRef.getRawObject().getContainedObjects(), cdo);
            }
        }
    }

    /**
     * Processes CDOMObjects removed from a Player Character to extract
     * Languages granted to the Player Character through the AUTO:LANG: and
     * LANGAUTO: tokens. The extracted languages are removed from a Player
     * Character.
     * <p>
     * Triggered when one of the Facets to which AutoLanguageFacet listens fires
     * a DataFacetChangeEvent to indicate a CDOMObjectwas removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        CDOMObject cdo = dfce.getCDOMObject();
        removeAll(id, cdo);
        autoLanguageUnconditionalFacet.removeAll(id, cdo);
    }

    /**
     * Returns a List of Languages granted to the Player Character by all
     * AUTO:LANG tokens on objects added to the Player Character.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this AutoLanguageFacet and modification of
     * this AutoLanguageFacet will not modify the returned Collection.
     * Modifications to the returned List will also not modify any future or
     * previous objects returned by this (or other) methods on
     * AutoLanguageFacet. If you wish to modify the information stored in this
     * AutoLanguageFacet, you must use the add*() and remove*() methods of
     * AutoLanguageFacet.
     *
     * @param id The CharID identifying the Player Character for which the list
     *           of all equipment granted by AUTO:LANGUAGE will be returned.
     * @return The List of Languages granted by the Player Character by all
     * AUTO:LANGUAGE tokens on objects added to the Player Character.
     */
    public List<Language> getAutoLanguage(CharID id)
    {
        List<Language> list = new ArrayList<>();
        for (QualifiedObject<CDOMReference<Language>> qo : getQualifiedSet(id))
        {
            Collection<Language> langList = qo.getRawObject().getContainedObjects();
            list.addAll(langList);
        }
        return list;
    }

    public void setAutoLanguageUnconditionalFacet(AutoLanguageUnconditionalFacet autoLanguageUnconditionalFacet)
    {
        this.autoLanguageUnconditionalFacet = autoLanguageUnconditionalFacet;
    }

    public void init()
    {
        CorePerspectiveDB.register(CorePerspective.LANGUAGE, FacetBehavior.CONDITIONAL, this);
    }

    @Override
    public String getIdentity()
    {
        return "AUTO:LANG|<ref> (with prerequisite)";
    }
}
