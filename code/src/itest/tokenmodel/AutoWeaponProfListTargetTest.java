/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Collection;

import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.AbilityLst;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.choose.LangToken;
import plugin.lsttokens.deprecated.AutoFeatToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;
import util.TestURI;

public class AutoWeaponProfListTargetTest extends AbstractTokenModelTest
{
    private static AutoFeatToken token = new AutoFeatToken();
    private static AbilityLst abLst = new AbilityLst();

    @Test
    public void testFromTemplate()
    {
        PCTemplate source = create(PCTemplate.class, "Source");
        Ability granted = createGrantedObject();
        context.getReferenceContext().constructCDOMObject(Language.class, "English");
        ParseResult result =
                new MultToken().parseToken(context, granted, "YES");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = new LangToken().parseToken(context, granted, "ALL");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = new LangToken().parseToken(context, source, "ALL");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = token.parseToken(context, source, "FEAT|Granted (%LIST)");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
        assertEquals(0, directAbilityFacet.getCount(id));
        Object sel = getAssoc();
        templateInputFacet.directAdd(id, source, sel);
        assertTrue(containsExpected());
        assertEquals(1, directAbilityFacet.getCount(id));
        templateInputFacet.remove(id, source);
        assertEquals(0, directAbilityFacet.getCount(id));
    }

    @Test
    public void testFromAbility()
    {
        Ability source = BuildUtilities.buildFeat(context, "Source");
        Ability granted = createGrantedObject();
        context.getReferenceContext().constructCDOMObject(Language.class, "English");
        ParseResult result =
                new MultToken().parseToken(context, granted, "YES");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = new MultToken().parseToken(context, source, "YES");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = new LangToken().parseToken(context, granted, "ALL");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = new LangToken().parseToken(context, source, "ALL");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = token.parseToken(context, source, "FEAT|Granted (%LIST)");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
        assertEquals(0, directAbilityFacet.getCount(id));
        CNAbilitySelection cas = new CNAbilitySelection(CNAbilityFactory.getCNAbility(
                BuildUtilities.getFeatCat(), Nature.AUTOMATIC, source), "English");
        directAbilityFacet.add(id, cas, UserSelection.getInstance());
        assertTrue(containsExpected());
        assertEquals(2, directAbilityFacet.getCount(id));
        directAbilityFacet.remove(id, cas, UserSelection.getInstance());
        assertEquals(0, directAbilityFacet.getCount(id));
    }

    @Override
    protected Language getAssoc()
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(Language.class,
                "English");
    }

    private boolean containsExpected()
    {
        Collection<CNAbilitySelection> casSet =
                directAbilityFacet.getSet(id);
        for (CNAbilitySelection cnas : casSet)
        {
            CNAbility cas = cnas.getCNAbility();
            boolean featExpected =
                    cas.getAbilityCategory() == BuildUtilities.getFeatCat();
            boolean abilityExpected = cas.getAbility()
                    .equals(context.getReferenceContext()
                            .getManufacturerId(BuildUtilities.getFeatCat()).getActiveObject("Granted"));
            boolean natureExpected = cas.getNature() == Nature.AUTOMATIC;
            boolean selectionExpected = "English".equals(cnas.getSelection());
            if (featExpected && abilityExpected && natureExpected
                    && selectionExpected)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        directAbilityFacet = FacetLibrary.getFacet(DirectAbilityFacet.class);
        TokenRegistration.register(abLst);
    }

    protected Ability createGrantedObject()
    {
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName("Granted");
        context.getReferenceContext().importObject(a);
        return a;
    }
}
