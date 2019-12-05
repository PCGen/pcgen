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

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.GrantedAbilityFacet;
import pcgen.core.Ability;
import pcgen.core.Language;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.add.AbilityToken;
import plugin.lsttokens.testsupport.BuildUtilities;

import tokenmodel.testsupport.AbstractAddListTokenTest;
import util.TestURI;

public class AddTargetedAbilityNormalTest extends AbstractAddListTokenTest<Ability>
{

    private static final AbilityToken ADD_ABILITY_TOKEN = new AbilityToken();
    private GrantedAbilityFacet grantedAbilityFacet = FacetLibrary
            .getFacet(GrantedAbilityFacet.class);

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result =
                ADD_ABILITY_TOKEN.parseToken(context, source, "FEAT|NORMAL|Granted (English)");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
    }

    @Override
    protected Class<Ability> getGrantClass()
    {
        return Ability.class;
    }

    @Override
    protected GrantedAbilityFacet getTargetFacet()
    {
        return grantedAbilityFacet;
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return AUTO_LANG_TOKEN;
    }

    @Override
    protected int getCount()
    {
        return getTargetFacet().getPoolAbilities(id, BuildUtilities.getFeatCat(), Nature.NORMAL)
                .size();
    }

    @Override
    protected boolean containsExpected(Ability granted)
    {
        Collection<CNAbility> abilities =
                getTargetFacet().getPoolAbilities(id, BuildUtilities.getFeatCat(), Nature.NORMAL);
        for (CNAbility a : abilities)
        {
            boolean abilityExpected = a.getAbility().equals(context.getReferenceContext()
                    .getManufacturerId(BuildUtilities.getFeatCat()).getActiveObject("Granted"));
            if (abilityExpected)
            {
                if (pc.getDetailedAssociationCount(a) == 1)
                {
                    if (!pc.getAssociationList(a).get(0).equals("English"))
                    {
                        continue;
                    }
                    Language english =
                            context.getReferenceContext().silentlyGetConstructedCDOMObject(
                                    Language.class, "English");
                    languageFacet.contains(id, english);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean cleanedSideEffects()
    {
        Language english =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(Language.class,
                        "English");
        return !languageFacet.contains(id, english)
                && super.cleanedSideEffects();
    }

    @Override
    protected Ability createGrantedObject()
    {
        context.getReferenceContext().constructCDOMObject(Language.class, "English");
        Ability a = BuildUtilities.buildFeat(context, "Granted");
        ParseResult result = AUTO_LANG_TOKEN.parseToken(context, a, "%LIST");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = ABILITY_MULT_TOKEN.parseToken(context, a, "YES");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = CHOOSE_LANG_TOKEN.parseToken(context, a, "ALL");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        return a;
    }

    //TODO CODE-2016/CODE-1921 (needs to be consistent with other methods of ADD:)
    @Override
    public void testFromAbility()
    {
        //Not supported equivalent to other methods
    }

    //TODO CODE-2016 (needs to be consistent with other methods of ADD:)
    @Override
    public void testFromClass()
    {
        //Not supported equivalent to other methods
    }
}
