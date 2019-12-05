/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet.input;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.testsupport.AbstractSourcedListFacetTest;
import pcgen.core.Language;

public class FreeLanguageFacetTest extends
        AbstractSourcedListFacetTest<Language>
{

    private FreeLanguageFacet facet = new FreeLanguageFacet();

    @Override
    protected AbstractSourcedListFacet<CharID, Language> getFacet()
    {
        return facet;
    }

    private int n = 0;

    @Override
    protected Language getObject()
    {
        Language t = new Language();
        t.setName("Language" + n++);
        return t;
    }
}
