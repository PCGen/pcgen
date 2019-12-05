/*
 * Copyright (c) 2007-12 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreLevelWriter;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbilityLstTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new AbilityLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(new PreRaceParser());
        TokenRegistration.register(new PreRaceWriter());
        TokenRegistration.register(new PreLevelParser());
        TokenRegistration.register(new PreLevelWriter());
        TokenRegistration.register(new PreClassParser());
        TokenRegistration.register(new PreClassWriter());
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getWriteToken()
    {
        return token;
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNotANature()
    {
        assertFalse(parse("FEAT|NotANature|,TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNotaCategory()
    {
        assertFalse(parse("NotaCategory|NORMAL|,TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoAbility()
    {
        assertFalse(parse("FEAT|NORMAL"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidCategoryOnly()
    {
        assertFalse(parse("FEAT"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidCategoryBarOnly()
    {
        assertFalse(parse("FEAT|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyCategory()
    {
        assertFalse(parse("|NORMAL|Abil"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyNature()
    {
        assertFalse(parse("FEAT||Abil"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyAbility()
    {
        assertFalse(parse("FEAT|NORMAL|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyPre()
    {
        assertFalse(parse("FEAT|NORMAL|PRERACE:1,Human"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoubleBarAbility()
    {
        assertFalse(parse("FEAT|NORMAL|Abil1||Abil2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidClearDotPre()
    {
        assertFalse(parse("FEAT|NORMAL|.CLEAR.Abil1|PRELEVEL:MIN=4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidClearPre()
    {
        assertFalse(parse("FEAT|NORMAL|.CLEAR|PRELEVEL:MIN=4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInsertedPre()
    {
        assertFalse(parse("FEAT|NORMAL|Abil1|PRELEVEL:MIN=4|Abil2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoubleBarStartAbility()
    {
        assertFalse(parse("FEAT|NORMAL||Abil1|Abil2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBarEndAbility()
    {
        assertFalse(parse("FEAT|NORMAL|Abil1|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidAnyNature()
    {
        assertFalse(parse("FEAT|ANY|Abil1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListPre()
    {
        assertFalse(parse("FEAT|AUTOMATIC|%LIST|PRERACE:1,Human"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinJustSpell() throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        runRoundRobin("FEAT|NORMAL|Abil1");
    }

    @Test
    public void testRoundRobinJustTwoPrereq() throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        runRoundRobin("FEAT|NORMAL|Abil1|PRELEVEL:MIN=5|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinTwoSpell() throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        runRoundRobin("FEAT|NORMAL|Abil1|Abil2");
    }

    @Test
    public void testRoundRobinTwoNature() throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        construct(primaryContext, "Abil3");
        construct(secondaryContext, "Abil3");
        construct(primaryContext, "Abil4");
        construct(secondaryContext, "Abil4");
        runRoundRobin("FEAT|NORMAL|Abil1|Abil2", "FEAT|VIRTUAL|Abil3|Abil4");
    }

    @Test
    public void testRoundRobinTwoCategory() throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        AbilityCategory pac = primaryContext.getReferenceContext().constructCDOMObject(
                AbilityCategory.class, "NEWCAT");
        AbilityCategory sac = secondaryContext.getReferenceContext().constructCDOMObject(
                AbilityCategory.class, "NEWCAT");
        BuildUtilities.buildAbility(primaryContext, pac, "Abil3");
        BuildUtilities.buildAbility(primaryContext, pac, "Abil4");
        BuildUtilities.buildAbility(secondaryContext, sac, "Abil3");
        BuildUtilities.buildAbility(secondaryContext, sac, "Abil4");
        runRoundRobin("FEAT|VIRTUAL|Abil1|Abil2", "NEWCAT|VIRTUAL|Abil3|Abil4");
    }

    @Test
    public void testRoundRobinDupe() throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        runRoundRobin("FEAT|VIRTUAL|Abil1|Abil1");
    }

    @Test
    public void testRoundRobinList() throws PersistenceLayerException
    {
        runRoundRobin("FEAT|VIRTUAL|%LIST");
    }

    @Test
    public void testRoundRobinDupeDiffNature() throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        runRoundRobin("FEAT|NORMAL|Abil1", "FEAT|VIRTUAL|Abil1");
    }

    @Test
    public void testRoundRobinDupeOnePrereq() throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        runRoundRobin("FEAT|VIRTUAL|Abil1|Abil1|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinDupeDiffPrereqs()
            throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        runRoundRobin("FEAT|VIRTUAL|Abil1",
                "FEAT|VIRTUAL|Abil1|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinDupeTwoDiffPrereqs()
            throws PersistenceLayerException
    {
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        runRoundRobin("FEAT|VIRTUAL|Abil1|Abil1|PRERACE:1,Elf",
                "FEAT|VIRTUAL|Abil1|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinListPrereq()
            throws PersistenceLayerException
    {
        construct(primaryContext, "Improved Critical");
        construct(secondaryContext, "Improved Critical");
        runRoundRobin("FEAT|AUTOMATIC|Improved Critical(%LIST)|PRECLASS:1,Oracle=8");
    }

    private static Ability construct(LoadContext context, String name)
    {
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName(name);
        context.getReferenceContext().importObject(a);
        return a;
    }

    @Test
    public void testRoundRobinOneParen() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("FEAT|VIRTUAL|TestWP1 (Paren)");
    }

    @Test
    public void testRoundRobinTwoParen() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("FEAT|VIRTUAL|TestWP1 (Paren)|TestWP2 (Other)");
    }

    @Test
    public void testRoundRobinDupeParen() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("FEAT|VIRTUAL|TestWP1 (Other)|TestWP1 (That)");
    }

    @Test
    public void testInputInvalidAddsTypeNoSideEffect()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP3");
        assertTrue(parse("Feat|VIRTUAL|TestWP1|TestWP2"));
        assertTrue(parseSecondary("Feat|VIRTUAL|TestWP1|TestWP2"));
        assertFalse(parse("Feat|VIRTUAL|TestWP3|TYPE="));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidTypeClearDotNoSideEffect()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP3");
        assertTrue(parse("Feat|VIRTUAL|TestWP1|TestWP2"));
        assertTrue(parseSecondary("Feat|VIRTUAL|TestWP1|TestWP2"));
        assertFalse(parse("Feat|VIRTUAL|TestWP3|.CLEAR.TestWP1|.CLEAR.TYPE="));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinTestEquals() throws PersistenceLayerException
    {
        Ability a = construct(primaryContext, "TestWP1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        Ability b = construct(secondaryContext, "TestWP1");
        b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        runRoundRobin("FEAT|VIRTUAL|TYPE=TestType");
    }

    @Test
    public void testRoundRobinTestEqualThree() throws PersistenceLayerException
    {
        Ability a = construct(primaryContext, "TestWP1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
        a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
        Ability b = construct(secondaryContext, "TestWP1");
        b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        b.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
        b.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
        runRoundRobin("FEAT|VIRTUAL|TYPE=TestAltType.TestThirdType.TestType");
    }

    @Test
    public void testRoundRobinWithEqualType() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        Ability a = construct(primaryContext, "Typed1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
        Ability b = construct(secondaryContext, "Typed1");
        b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
        Ability c = construct(primaryContext, "Typed2");
        c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        Ability d = construct(secondaryContext, "Typed2");
        d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        runRoundRobin("FEAT|VIRTUAL|TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType");
    }

    @Test
    public void testInvalidInputCheckTypeEqualLength()
    {
        // Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
        // consume the |
        construct(primaryContext, "TestWP1");
        assertTrue(parse("Feat|VIRTUAL|TestWP1|TYPE=TestType|TestWP2"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputCheckTypeDotLength()
    {
        // Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
        // consume the |
        construct(primaryContext, "TestWP1");
        assertTrue(parse("Feat|VIRTUAL|TestWP1|TYPE.TestType.OtherTestType|TestWP2"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputTypeEmpty()
    {
        assertFalse(parse("Feat|VIRTUAL|TYPE="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTypeUnterminated()
    {
        assertFalse(parse("Feat|VIRTUAL|TYPE=One."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTypeDoubleSeparator()
    {
        assertFalse(parse("Feat|VIRTUAL|TYPE=One..Two"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTypeFalseStart()
    {
        assertFalse(parse("Feat|VIRTUAL|TYPE=.One"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinListParen() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("FEAT|VIRTUAL|TestWP1 (%LIST)");
    }

    @Override
    protected String getLegalValue()
    {
        return "FEAT|VIRTUAL|Abil1|PRERACE:1,Human";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "FEAT|VIRTUAL|TYPE=TestType";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

    @Test
    public void testValidInputClearWorking()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        assertTrue(parse("FEAT|VIRTUAL|TestWP1"));
        assertTrue(parse("FEAT|VIRTUAL|" + getClearString()));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputClearJoinWorking()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        assertTrue(parse("FEAT|VIRTUAL|" + getClearString()
                + getJoinCharacter() + "TestWP1"));
        assertTrue(parseSecondary("FEAT|VIRTUAL|TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testListTargetClearWorking()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        assertTrue(parse("FEAT|VIRTUAL|TestWP1(%LIST)"));
        assertTrue(parse("FEAT|VIRTUAL|" + getClearString()));
        assertNoSideEffects();
    }

    @Test
    public void testClearMixedWorking()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        assertTrue(parse("FEAT|VIRTUAL|TestWP2|TestWP1(%LIST)"));
        assertTrue(parse("FEAT|VIRTUAL|" + getClearString()));
        assertNoSideEffects();
    }

    private static String getJoinCharacter()
    {
        return Constants.PIPE;
    }

    private static String getClearString()
    {
        return Constants.LST_DOT_CLEAR;
    }

    @Override
    protected void additionalSetup(LoadContext context)
    {
        super.additionalSetup(context);
        //We build dummy objects so that AbilityCategory.FEAT has been loaded properly
        construct(context, "Dummy");
    }


}
