/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet.model;

import pcgen.cdom.facet.base.AbstractSingleSourceListFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.testsupport.AbstractSingleSourceListFacetTest;
import pcgen.core.Domain;
import pcgen.core.PCClass;

public class DomainFacetTest extends
        AbstractSingleSourceListFacetTest<Domain, ClassSource>
{
    private static int n = 0;
    private DomainFacet facet = new DomainFacet();

    @Override
    protected ClassSource developSource()
    {
        PCClass cl = new PCClass();
        cl.setName("Class" + n++);
        return new ClassSource(cl);
    }

    @Override
    protected AbstractSingleSourceListFacet<Domain, ClassSource> getFacet()
    {
        return facet;
    }

    @Override
    protected Domain getTypeObj()
    {
        Domain d = new Domain();
        d.setName("Domain" + n++);
        return d;
    }
}
