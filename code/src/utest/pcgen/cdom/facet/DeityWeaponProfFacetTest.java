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
package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.Deity;
import pcgen.core.WeaponProf;

import org.junit.jupiter.api.BeforeEach;

public class DeityWeaponProfFacetTest extends
        AbstractExtractingFacetTest<Deity, WeaponProf>
{
    private static int n = 0;

    private DeityWeaponProfFacet facet = new DeityWeaponProfFacet();
    private WeaponProf[] target;
    private Deity[] source;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        Deity cdo1 = new Deity();
        cdo1.setName("Deity1");
        Deity cdo2 = new Deity();
        cdo2.setName("Deity2");
        WeaponProf st1 = new WeaponProf();
        st1.setName("Prof1");
        WeaponProf st2 = new WeaponProf();
        st1.setName("Prof2");
        cdo1.addToListFor(ListKey.DEITYWEAPON, CDOMDirectSingleRef.getRef(st1));
        cdo2.addToListFor(ListKey.DEITYWEAPON, CDOMDirectSingleRef.getRef(st2));
        source = new Deity[]{cdo1, cdo2};
        target = new WeaponProf[]{st1, st2};
    }

    @Override
    protected AbstractSourcedListFacet<CharID, WeaponProf> getFacet()
    {
        return facet;
    }

    @Override
    protected WeaponProf getObject()
    {
        WeaponProf wp = new WeaponProf();
        wp.setName("WP" + n++);
        return wp;
    }

    @Override
    protected Deity getContainingObject(int i)
    {
        return source[i];
    }

    @Override
    protected DataFacetChangeListener<CharID, Deity> getListener()
    {
        return facet;
    }

    @Override
    protected WeaponProf getTargetObject(int i)
    {
        return target[i];
    }

}
