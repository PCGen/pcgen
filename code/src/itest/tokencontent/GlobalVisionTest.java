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
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.VisionFacet;
import pcgen.core.QualifiedObject;
import pcgen.core.Vision;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.VisionLst;

import org.junit.jupiter.api.BeforeEach;
import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalVisionTest extends AbstractContentTokenTest
{

    private static VisionLst token = new VisionLst();
    private VisionFacet visionFacet;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        visionFacet = FacetLibrary.getFacet(VisionFacet.class);
    }

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result = token.parseToken(context, source, "Normal (40)");
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
        return visionFacet.contains(id,
                new QualifiedObject<>(Vision.getVision("Normal (40')")));
    }

    @Override
    protected int targetFacetCount()
    {
        return visionFacet.getCount(id);
    }

    @Override
    protected int baseCount()
    {
        return 0;
    }

}
