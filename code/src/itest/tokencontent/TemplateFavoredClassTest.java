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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.FavoredClassFacet;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.choose.ClassToken;
import plugin.lsttokens.template.FavoredclassToken;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;
import util.TestURI;

public class TemplateFavoredClassTest extends AbstractTokenModelTest
{

    private static FavoredclassToken token = new FavoredclassToken();
    private static ClassToken CHOOSE_CLASS_TOKEN = new ClassToken();

    private FavoredClassFacet fcFacet;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        fcFacet = FacetLibrary.getFacet(FavoredClassFacet.class);
        context.getReferenceContext().constructCDOMObject(PCClass.class, "Favorite");
        TokenRegistration.register(CHOOSE_CLASS_TOKEN);
        ChooserFactory.setDelegate(new MockUIDelegate());
    }

    @Test
    public void testDirect()
    {
        PCTemplate source = create(PCTemplate.class, "Source");
        ParseResult result = token.parseToken(context, source, "Favorite");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
        assertEquals(baseCount(), targetFacetCount());
        templateInputFacet.directAdd(id, source, getAssoc());
        assertTrue(containsExpected());
        assertEquals(baseCount() + 1, targetFacetCount());
        templateInputFacet.remove(id, source);
        assertEquals(baseCount(), targetFacetCount());
    }

    @Test
    public void testList()
    {
        PCTemplate source = create(PCTemplate.class, "Source");
        ParseResult result = token.parseToken(context, source, "%LIST");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = CHOOSE_CLASS_TOKEN.parseToken(context, source, "Favorite");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
        assertEquals(baseCount(), targetFacetCount());
        templateInputFacet.add(id, source);
        assertTrue(containsExpected());
        assertEquals(baseCount() + 1, targetFacetCount());
        templateInputFacet.remove(id, source);
        assertEquals(baseCount(), targetFacetCount());
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }

    protected boolean containsExpected()
    {
        return fcFacet.contains(id, context.getReferenceContext()
                .silentlyGetConstructedCDOMObject(PCClass.class, "Favorite"));
    }

    protected int targetFacetCount()
    {
        return fcFacet.getCount(id);
    }

    protected int baseCount()
    {
        return 0;
    }
}
