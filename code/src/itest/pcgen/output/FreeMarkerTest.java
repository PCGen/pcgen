/*
 * Copyright (c) 2015 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output;

import pcgen.base.format.StringManager;
import pcgen.base.util.BasicIndirect;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.facet.CDOMWrapperInfoFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.model.CheckFacet;
import pcgen.core.PCCheck;
import pcgen.output.actor.FactKeyActor;
import pcgen.output.publish.OutputDB;
import pcgen.output.testsupport.AbstractOutputTestCase;
import pcgen.output.wrapper.CDOMObjectWrapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FreeMarkerTest extends AbstractOutputTestCase
{
    private static final CheckFacet CF = new CheckFacet();

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        CDOMWrapperInfoFacet wiFacet =
                FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);
        wiFacet.initialize(dsid);
    }

    @BeforeAll
    public static void classSetUp()
    {
        OutputDB.reset();
        CF.init();
    }

    @Test
    public void testBasic()
    {
        createChecks();
        processThroughFreeMarker("<#list checks as obj>" + "${obj.shortname}"
                + "</#list>", "WillRefFort");
    }

    @Test
    public void testNested()
    {
        createChecks();
        String macro = "<#macro getKeyed objlist key>" + "<#list objlist as obj>"
                + "<#if obj.key == key><#nested obj></#if>" + "</#list>"
                + "</#macro>";

        processThroughFreeMarker(macro
                        + "<@getKeyed checks \"Willpower\" ; ck>${ck.shortname}</@getKeyed>",
                "Will");
    }

    private void createChecks()
    {
        StringManager sm = new StringManager();
        FactKey<String> sn = FactKey.getConstant("ShortName", sm);

        PCCheck pcc = new PCCheck();
        pcc.setName("Willpower");
        pcc.put(sn, new BasicIndirect<>(sm, "Will"));
        CF.add(id, pcc);
        pcc = new PCCheck();
        pcc.setName("Reflex");
        pcc.put(sn, new BasicIndirect<>(sm, "Ref"));
        CF.add(id, pcc);
        pcc = new PCCheck();
        pcc.setName("Fortitude");
        pcc.put(sn, new BasicIndirect<>(sm, "Fort"));
        CF.add(id, pcc);

        FactKeyActor<?> fka = new FactKeyActor<>(sn);
        CDOMObjectWrapper.load(dsid, pcc.getClass(), "shortname", fka);
    }

}
