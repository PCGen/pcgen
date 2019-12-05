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
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.VariableFacet;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.DefineLst;

import org.junit.jupiter.api.BeforeEach;
import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalDefineVariableTest extends AbstractContentTokenTest
{

    private static DefineLst token = new DefineLst();
    private VariableFacet variableFacet;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        variableFacet = FacetLibrary.getFacet(VariableFacet.class);
    }

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result = token.parseToken(context, source, "This|0");
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
        //TODO Test the contents somehow? (constant or ??)
        return variableFacet.contains(id, VariableKey.getConstant("This"));
    }

    @Override
    protected int targetFacetCount()
    {
        return variableFacet.getVariableCount(id);
    }

    @Override
    protected int baseCount()
    {
        return 8;
    }

}
