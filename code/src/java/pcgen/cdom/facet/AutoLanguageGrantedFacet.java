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
package pcgen.cdom.facet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.meta.CorePerspectiveDB;
import pcgen.cdom.meta.FacetBehavior;
import pcgen.cdom.meta.PerspectiveLocation;
import pcgen.core.Language;

/**
 * AutoLanguageFacet is a Facet that tracks the Languages that have been granted
 * to a Player Character through the AUTO:LANG and LANGAUTO tokens
 */
public class AutoLanguageGrantedFacet extends AbstractSourcedListFacet<CharID, Language> implements PerspectiveLocation
{

    private AutoLanguageFacet autoLanguageFacet;

    private LanguageFacet languageFacet;

    public boolean update(CharID id)
    {
        Collection<Language> current = getSet(id);
        Collection<Language> qualified = autoLanguageFacet.getAutoLanguage(id);
        List<Language> toRemove = new ArrayList<>(current);
        toRemove.removeAll(qualified);
        List<Language> toAdd = new ArrayList<>(qualified);
        toAdd.removeAll(current);
        for (Language lang : toRemove)
        {
            remove(id, lang, autoLanguageFacet);
        }
        for (Language lang : toAdd)
        {
            add(id, lang, autoLanguageFacet);
        }
        return !toRemove.isEmpty() || !toAdd.isEmpty();
    }

    public void setAutoLanguageFacet(AutoLanguageFacet autoLanguageFacet)
    {
        this.autoLanguageFacet = autoLanguageFacet;
    }

    public void setLanguageFacet(LanguageFacet languageFacet)
    {
        this.languageFacet = languageFacet;
    }

    public void init()
    {
        addDataFacetChangeListener(languageFacet);
        CorePerspectiveDB.register(CorePerspective.LANGUAGE, FacetBehavior.CONDITIONAL_GRANTED, this);
        CorePerspectiveDB.registerVirtualParent(this, autoLanguageFacet);
    }

    @Override
    public String getIdentity()
    {
        return "AUTO:LANG|<ref> (passed defined prerequisites)";
    }
}
