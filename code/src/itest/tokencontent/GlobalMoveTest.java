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
import pcgen.cdom.enumeration.MovementType;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.MovementFacet;
import pcgen.core.SimpleMovement;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;

import plugin.lsttokens.MoveLst;

import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalMoveTest extends AbstractContentTokenTest
{

    private static MoveLst token = new MoveLst();
    private MovementFacet moveFacet;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        moveFacet = FacetLibrary.getFacet(MovementFacet.class);
    }

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result = token.parseToken(context, source, "Fly,30");
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
        //Cannot use contains because facet is using instance identity
        SimpleMovement movement = moveFacet.getSet(id).iterator().next();
        return movement.getMovementType().equals(MovementType.getConstant("Fly"))
                && (movement.getMovement() == 30);
    }

    @Override
    protected int targetFacetCount()
    {
        return moveFacet.getCount(id);
    }

    @Override
    protected int baseCount()
    {
        return 0;
    }
}
