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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Identified;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.GrantedAbilityFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.AbilityLst;
import plugin.lsttokens.add.AbilityToken;
import plugin.lsttokens.deprecated.VFeatLst;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import tokenmodel.testsupport.AbstractTokenModelTest;
import tokenmodel.testsupport.AssocCheck;
import tokenmodel.testsupport.NoAssociations;
import util.TestURI;

public final class AbilityDepthTest extends AbstractTokenModelTest
{

    private static final GrantedAbilityFacet GRANTED_ABILITY_FACET = FacetLibrary
            .getFacet(GrantedAbilityFacet.class);

    //Registration by super.setUpContext()
    private static final CDOMToken<CDOMObject> VFEAT_TOKEN = new VFeatLst();

    //Registration required locally
    private static final CDOMToken<CDOMObject> ABILITY_LST = new AbilityLst();
    private static final CDOMToken<CDOMObject> ADD_ABILITY_TOKEN = new AbilityToken();

    private final CDOMToken<? super Ability> firstToken;
    private final String firstPrefix;
    private final CDOMToken<? super Ability> secondToken;
    private final String secondPrefix;

    private AssocCheck assocCheck;

    private AbilityDepthTest(String name, CDOMToken<? super Ability> firstToken,
            String firstPrefix, CDOMToken<? super Ability> secondToken,
            String secondPrefix)
    {
        this.firstToken = firstToken;
        this.firstPrefix = firstPrefix;
        this.secondToken = secondToken;
        this.secondPrefix = secondPrefix;
    }

    @Override
    protected void setUpContext()
    {
        super.setUpContext();
        TokenRegistration.register(ABILITY_LST);
    }

    private Ability createAbility(String key)
    {
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName(key);
        context.getReferenceContext().importObject(a);
        return a;
    }


    protected void runTest()
    {
        Ability top = createAbility("TopAbility");
        Ability mid = createAbility("MidAbility");
        Ability target = createAbility("TargetAbility");
        ParseResult result =
                firstToken.parseToken(context, top,
                        firstPrefix + mid.getKeyName());
        if (!result.passed())
        {
            result.printMessages(TestURI.getURI());
            fail();
        }
        result =
                secondToken.parseToken(context, mid,
                        secondPrefix + target.getKeyName());
        if (!result.passed())
        {
            result.printMessages(TestURI.getURI());
            fail();
        }

        finishLoad();
        assocCheck = new NoAssociations(pc);

        CNAbilitySelection cas =
                new CNAbilitySelection(CNAbilityFactory.getCNAbility(
                        BuildUtilities.getFeatCat(), Nature.AUTOMATIC, top));

        assertEquals(0, getCount());
        pc.addAbility(cas, "This", "That");
        //		directAbilityFacet.add(id, cas, UserSelection.getInstance());
        assertTrue(containsExpected(mid));
        assertTrue(containsExpected(target));
        assertEquals(3, getCount());
        pc.removeAbility(cas, "This", "That");
        //		directAbilityFacet.remove(id, cas, UserSelection.getInstance());
        assertEquals(0, getCount());
    }

    private boolean containsExpected(Identified granted)
    {
        Collection<CNAbility> abilities =
                GRANTED_ABILITY_FACET.getPoolAbilities(id, BuildUtilities.getFeatCat());
        if (abilities.isEmpty())
        {
            System.err.println("No Abilities");
            return false;
        }
        for (CNAbility a : abilities)
        {
            boolean abilityExpected = a.getAbility().equals(granted);
            if (abilityExpected)
            {
                boolean c = assocCheck.check(a);
                if (!c)
                {
                    System.err.println("Incorrect Associations");
                }
                return c;
            }
        }
        System.err.println("Did not find Ability: " + granted.getKeyName());
        return false;
    }

    private int getCount()
    {
        return GRANTED_ABILITY_FACET.getPoolAbilities(id, BuildUtilities.getFeatCat())
                .size();
    }

    private static final CDOMToken[] tokens = {ABILITY_LST,
            ADD_ABILITY_TOKEN, ADD_ABILITY_TOKEN};
    private static final String[] prefix = {"FEAT|VIRTUAL|",
            "FEAT|NORMAL|STACKS,", "FEAT|VIRTUAL|STACKS,"};
    private static final CDOMToken[] targetProhibited = {ADD_ABILITY_TOKEN};

    @Override
    public CDOMToken<?> getToken()
    {
        return VFEAT_TOKEN;
    }

}
