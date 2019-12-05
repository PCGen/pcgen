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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;
import util.TestURI;

public class AutoLangListTest extends AbstractTokenModelTest
{

    @Test
    public void testFromAbility()
    {
        Ability source = BuildUtilities.buildFeat(context, "Source");
        ParseResult result =
                new MultToken().parseToken(context, source, "YES");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        Language granted = createGrantedObject();
        processToken(source);
        assertEquals(0, getCount());
        CNAbilitySelection cas = new CNAbilitySelection(CNAbilityFactory.getCNAbility(
                BuildUtilities.getFeatCat(), Nature.AUTOMATIC, source), "Granted");
        directAbilityFacet.add(id, cas, UserSelection.getInstance());
        assertTrue(containsExpected(granted));
        assertEquals(1,
                getCount());
        directAbilityFacet.remove(id, cas, UserSelection.getInstance());
        assertEquals(0, getCount());
        assertTrue(cleanedSideEffects());
    }

    @Test
    public void testFromRace()
    {
        Race source = create(Race.class, "Source");
        Language granted = createGrantedObject();
        processToken(source);
        assertEquals(0, getCount());
        raceFacet.directSet(id, source, getAssoc());
        assertTrue(containsExpected(granted));
        assertEquals(1, getCount());
        raceFacet.remove(id);
        assertEquals(0, getCount());
        assertTrue(cleanedSideEffects());
    }

    @Test
    public void testFromTemplate()
    {
        PCTemplate source = create(PCTemplate.class, "Source");
        Language granted = createGrantedObject();
        processToken(source);
        assertEquals(0, getCount());
        templateInputFacet.directAdd(id, source, getAssoc());
        assertTrue(containsExpected(granted));
        assertEquals(1, getCount());
        templateInputFacet.remove(id, source);
        assertEquals(0, getCount());
        assertTrue(cleanedSideEffects());
    }

    protected boolean cleanedSideEffects()
    {
        return true;
    }

    protected Language createGrantedObject()
    {
        return create(getGrantClass(), "Granted");
    }

    public void processToken(CDOMObject source)
    {
        ParseResult result = AUTO_LANG_TOKEN.parseToken(context, source, "%LIST");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = CHOOSE_LANG_TOKEN.parseToken(context, source, "Granted");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
    }

    protected Class<Language> getGrantClass()
    {
        return Language.class;
    }

    protected LanguageFacet getTargetFacet()
    {
        return languageFacet;
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return AUTO_LANG_TOKEN;
    }

    protected int getCount()
    {
        return getTargetFacet().getCount(id);
    }

    protected boolean containsExpected(Language granted)
    {
        return getTargetFacet().contains(id, granted);
    }

    @Override
    protected Object getAssoc()
    {
        return context.getReferenceContext()
                .silentlyGetConstructedCDOMObject(Language.class, "Granted");
    }


}
