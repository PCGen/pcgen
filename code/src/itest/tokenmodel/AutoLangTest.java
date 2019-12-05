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
package tokenmodel;

import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.core.Language;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;

import tokenmodel.testsupport.AbstractGrantedListTokenTest;
import util.TestURI;

public class AutoLangTest extends AbstractGrantedListTokenTest<Language>
{

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result = AUTO_LANG_TOKEN.parseToken(context, source, "Granted");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
    }

    @Override
    protected Class<Language> getGrantClass()
    {
        return Language.class;
    }

    @Override
    protected LanguageFacet getTargetFacet()
    {
        return languageFacet;
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return AUTO_LANG_TOKEN;
    }

    @Override
    protected int getCount()
    {
        return getTargetFacet().getCount(id);
    }

    @Override
    protected boolean containsExpected(Language granted)
    {
        return getTargetFacet().contains(id, granted);
    }
}
