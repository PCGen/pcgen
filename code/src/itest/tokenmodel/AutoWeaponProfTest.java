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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.facet.model.WeaponProfModelFacet;
import pcgen.core.WeaponProf;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.auto.WeaponProfToken;

import tokenmodel.testsupport.AbstractGrantedListTokenTest;
import util.TestURI;

public class AutoWeaponProfTest extends
        AbstractGrantedListTokenTest<WeaponProf>
{

    private static final WeaponProfToken AUTO_WEAPONPROF_TOKEN =
            new WeaponProfToken();

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result =
                AUTO_WEAPONPROF_TOKEN.parseToken(context, source, "Granted");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
    }

    @Override
    protected Class<WeaponProf> getGrantClass()
    {
        return WeaponProf.class;
    }

    @Override
    protected WeaponProfModelFacet getTargetFacet()
    {
        return weaponProfModelFacet;
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return AUTO_WEAPONPROF_TOKEN;
    }

    @Override
    protected int getCount()
    {
        return getTargetFacet().getSet(id).size();
    }

    @Override
    protected boolean containsExpected(WeaponProf granted)
    {
        return getTargetFacet().containsProf(id, granted);
    }
}
