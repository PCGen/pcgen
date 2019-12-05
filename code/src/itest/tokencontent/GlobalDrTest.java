/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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
package tokencontent;

import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.facet.DamageReductionFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.core.Race;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.DrLst;

import org.junit.jupiter.api.BeforeEach;
import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalDrTest extends AbstractContentTokenTest
{

    private static DrLst token = new DrLst();
    private DamageReductionFacet drFacet;

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        drFacet = FacetLibrary.getFacet(DamageReductionFacet.class);
        create(Race.class, "Ape");
    }

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result = token.parseToken(context, source, "5/Light");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }

    @Override
    protected boolean containsExpected()
    {
        DamageReduction dr = drFacet.getSet(id).iterator().next();
        boolean bypassMatches = dr.getBypass().equals("Light");
        boolean reductionMatches =
                dr.getReduction().equals(FormulaFactory.getFormulaFor("5"));
        return bypassMatches && reductionMatches;
    }

    @Override
    protected int targetFacetCount()
    {
        return drFacet.getCount(id);
    }

    @Override
    protected int baseCount()
    {
        return 0;
    }
}
