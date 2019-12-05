/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
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
package tokenmodel;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.model.ArmorProfProviderFacet;
import pcgen.cdom.helper.ProfProvider;
import pcgen.core.ArmorProf;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.auto.ArmorProfToken;

import tokenmodel.testsupport.AbstractGrantedListTokenTest;
import util.TestURI;

public class AutoArmorProfTest extends AbstractGrantedListTokenTest<ArmorProf>
{

    private static final ArmorProfToken AUTO_ARMORPROF_TOKEN =
            new ArmorProfToken();
    private final ArmorProfProviderFacet armorProfFacet = FacetLibrary
            .getFacet(ArmorProfProviderFacet.class);

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result =
                AUTO_ARMORPROF_TOKEN.parseToken(context, source, "Granted");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
    }

    @Override
    protected Class<ArmorProf> getGrantClass()
    {
        return ArmorProf.class;
    }

    @Override
    protected ArmorProfProviderFacet getTargetFacet()
    {
        return armorProfFacet;
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return AUTO_ARMORPROF_TOKEN;
    }

    @Override
    protected int getCount()
    {
        return getTargetFacet().getCount(id);
    }

    @Override
    protected boolean containsExpected(ArmorProf granted)
    {
        Collection<ProfProvider<ArmorProf>> qs =
                getTargetFacet().getQualifiedSet(id);
        if (qs.size() != 1)
        {
            return false;
        }
        ProfProvider<ArmorProf> pp = qs.iterator().next();
        return pp.providesProficiency(granted);
    }
}
