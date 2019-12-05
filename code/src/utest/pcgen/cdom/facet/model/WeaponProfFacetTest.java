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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.WeaponProfFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.testsupport.AbstractConsolidatingFacetTest;
import pcgen.core.WeaponProf;

public class WeaponProfFacetTest extends
        AbstractConsolidatingFacetTest<WeaponProf, WeaponProf>
{

    private WeaponProfFacet facet = new WeaponProfFacet();

    @Override
    protected WeaponProfFacet getFacet()
    {
        return facet;
    }

    public static int n = 0;

    @Override
    protected WeaponProf getObject()
    {
        WeaponProf wp = new WeaponProf();
        wp.setName("WP" + n++);
        return wp;
    }

    @Override
    protected DataFacetChangeListener<CharID, WeaponProf> getListener()
    {
        return facet;
    }

    @Override
    protected WeaponProf getSourceObject()
    {
        return getObject();
    }

    @Override
    protected WeaponProf getConverted(WeaponProf wp)
    {
        return wp;
    }
}
