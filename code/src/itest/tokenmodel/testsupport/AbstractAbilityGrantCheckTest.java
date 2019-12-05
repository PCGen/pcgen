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
package tokenmodel.testsupport;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.ability.StackToken;
import plugin.lsttokens.choose.NoChoiceToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

public abstract class AbstractAbilityGrantCheckTest extends AbstractTokenModelTest
{

    protected static final plugin.lsttokens.AbilityLst ABILITY_TOKEN =
            new plugin.lsttokens.AbilityLst();
    private static final plugin.lsttokens.deprecated.AutoFeatToken AUTO_FEAT_TOKEN =
            new plugin.lsttokens.deprecated.AutoFeatToken();
    private static final plugin.lsttokens.deprecated.ChooseFeatSelectionToken CHOOSE_FEATSELECTION_TOKEN =
            new plugin.lsttokens.deprecated.ChooseFeatSelectionToken();
    private static final plugin.lsttokens.TypeLst TYPE_TOKEN =
            new plugin.lsttokens.TypeLst();
    private static final StackToken ABILITY_STACK_TOKEN = new StackToken();
    private static final NoChoiceToken CHOOSE_NOCHOICE_TOKEN =
            new NoChoiceToken();

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(ABILITY_TOKEN);
    }

    private Ability getMultNo(String s)
    {
        Ability a = BuildUtilities.buildFeat(context, s);
        ParseResult result = TYPE_TOKEN.parseToken(context, a, "Selectable");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        return a;
    }

    private Ability getMultYesStackNo(String s, String target)
    {
        Ability a = BuildUtilities.buildFeat(context, s);
        ParseResult result = AUTO_FEAT_TOKEN.parseToken(context, a, "FEAT|%LIST");
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
        result = CHOOSE_FEATSELECTION_TOKEN.parseToken(context, a, target);
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        return a;
    }

    private Ability getMultYesStackYes(String s, String target)
    {
        Ability a = BuildUtilities.buildFeat(context, s);
        ParseResult result = AUTO_FEAT_TOKEN.parseToken(context, a, "FEAT|%LIST");
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
        result = ABILITY_STACK_TOKEN.parseToken(context, a, "YES");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = CHOOSE_FEATSELECTION_TOKEN.parseToken(context, a, target);
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        return a;
    }

    private Ability getMultYesStackNoChooseNoChoice(String s)
    {
        Ability a = BuildUtilities.buildFeat(context, s);
        ParseResult result = ABILITY_MULT_TOKEN.parseToken(context, a, "YES");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = CHOOSE_NOCHOICE_TOKEN.parseToken(context, a, null);
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        return a;
    }

    private Ability getMultYesStackYesChooseNoChoice(String s)
    {
        Ability a = BuildUtilities.buildFeat(context, s);
        ParseResult result = ABILITY_MULT_TOKEN.parseToken(context, a, "YES");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = ABILITY_STACK_TOKEN.parseToken(context, a, "YES");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = CHOOSE_NOCHOICE_TOKEN.parseToken(context, a, null);
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        return a;
    }

    @Test
    public void testMultNo()
    {
        getMultNo("MultNo");
        Ability parent = getGrantor("MultNo");
        finishLoad();
        applyParent(parent);
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Parent"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Grantor"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "MultNo"));
    }

    @Test
    public void testNaturalParens()
    {
        getMultNo("Natural (Parens)");
        Ability parent = getGrantor("Natural (Parens)");
        finishLoad();
        applyParent(parent);
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Parent"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Grantor"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Natural (Parens)"));
    }

    @Test
    public void testMultYes()
    {
        getMultNo("Target");
        getMultYesStackNo("MultYes", "Target");
        Ability parent = getGrantor("MultYes (Target)");
        finishLoad();
        applyParent(parent);
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Parent"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Grantor"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "MultYes"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Target"));
    }

    @Test
    public void testMultYesTargetParens()
    {
        getMultNo("Target (Parens)");
        getMultYesStackNo("MultYes", "Target (Parens)");
        Ability parent = getGrantor("MultYes (Target (Parens))");
        finishLoad();
        applyParent(parent);
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Parent"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Grantor"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "MultYes"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Target (Parens)"));
    }

    @Test
    public void testMultYesNC()
    {
        getMultYesStackNoChooseNoChoice("MultYesNC");
        Ability parent = getGrantor("MultYesNC");
        finishLoad();
        applyParent(parent);
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Parent"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Grantor"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "MultYesNC"));
    }

    @Test
    public void testStackYes()
    {
        getMultNo("Target");
        getMultYesStackNo("MultYesStackYes", "Target");
        Ability parent = getGrantor("MultYesStackYes (Target)");
        finishLoad();
        applyParent(parent);
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Parent"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Grantor"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "MultYesStackYes"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Target"));
    }

    @Test
    public void testStackYesNC()
    {
        getMultYesStackNoChooseNoChoice("MultYesStackYesNC");
        Ability parent = getGrantor("MultYesStackYesNC");
        finishLoad();
        applyParent(parent);
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Parent"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "Grantor"));
        assertTrue(pc.hasAbilityKeyed(BuildUtilities.getFeatCat(), "MultYesStackYesNC"));
    }

    private void applyParent(Ability parent)
    {
        CNAbility cna = CNAbilityFactory.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, parent);
        CNAbilitySelection cnas = new CNAbilitySelection(cna);
        pc.addAbility(cnas, UserSelection.getInstance(), this);
    }

    public void generic()
    {
        //Need to do these with 2 choices and test :P
        //	6) Ability granted by a ADD:VFEAT token where the target (in parens)
        //		is MULT:YES STACK:YES CHOOSE:NOCHOICE and the stackable items
        //		are chosen more than once (STACK is used)
        //	7) Ability granted by a ADD:VFEAT token where the target (in parens)
        //		is MULT:YES STACK:YES and any CHOOSE except NOCHOICE or USERINPUT.
        //		and the stackable items are chosen more than once (STACK is used)
        Ability multyesstackyes = getMultYesStackYes("MultYes", "Target");
        Ability multyesstackyesNC = getMultYesStackYesChooseNoChoice("MultYes");
    }

    private Ability getGrantor(String s)
    {
        getMultYesStackNo("Grantor", s);
        Ability parent = getMultNo("Parent");
        ParseResult result =
                getGrantToken().parseToken(context, parent,
                        getGrantPrefix() + "Grantor (" + s + ")");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        return parent;
    }

    protected String getGrantPrefix()
    {
        return "";
    }

    protected abstract CDOMToken<? super Ability> getGrantToken();
//	{
//		//TODO Need to cycle through:
//		//ADD:FEAT
//		//ABILITY (Virtual)
//		//ABILITY (Normal)
//		//AUTO:FEAT
//		//ADD:VFEAT
//		//ADD:ABILITY (Virtual)
//		//ADD:ABILITY (Normal)
//		//VFEAT:
//		//(domain's Feat)
//		//(race's Feat)
//		//(template's Feat)
//		return ADD_FEAT_TOKEN;
//	}

    /*
     * Is it at all possible to deal with
     *
     * This (%LIST) in Ability, Domain Feat, Auto Feat
     */
    @Override
    public CDOMToken<?> getToken()
    {
        return getGrantToken();
    }


}
