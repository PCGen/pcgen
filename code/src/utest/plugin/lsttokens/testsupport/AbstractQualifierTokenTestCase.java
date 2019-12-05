/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.ability.VisibleToken;
import plugin.lsttokens.auto.LangToken;
import plugin.lsttokens.equipment.ProficiencyToken;
import plugin.primitive.language.LangBonusToken;
import plugin.qualifier.pobject.QualifiedToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractQualifierTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
        extends AbstractCDOMTokenTestCase<T>
{

    private static QualifierToken<CDOMObject> qt = new QualifiedToken<>();

    public abstract CDOMSecondaryToken<?> getSubToken();

    private final String qualifier;
    private final String target;
    private final String token;

    protected AbstractQualifierTokenTestCase(String tok, String tgt)
    {
        token = tok;
        target = tgt;
        qualifier = (tgt == null) ? token : (token + "=" + target);
    }

    @Override
    protected LoadContext getPrimaryContext()
    {
        GameMode game = SettingsHandler.getGame();
        game.clearLoadContext();
        return game.getContext();
    }

    public String getSubTokenName()
    {
        return getSubToken().getTokenName();
    }

    public abstract Class<TC> getTargetClass();

    protected abstract boolean allowsNotQualifier();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(getSubToken());
        TokenRegistration.register(qt);
    }

    protected CDOMObject construct(LoadContext loadContext, String one)
    {
        return construct(loadContext, getTargetClass(), one);
    }

    protected CDOMObject construct(LoadContext loadContext,
            Class<? extends CDOMObject> cl, String one)
    {
        return loadContext.getReferenceContext().constructCDOMObject(cl, one);
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return getSubTokenName() + '|' + qualifier + "[TestWP1]";
    }

    @Override
    protected String getLegalValue()
    {
        return getSubTokenName() + '|' + qualifier;
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    protected ReferenceManufacturer<TC> getManufacturer()
    {
        return primaryContext.getReferenceContext().getManufacturer(getTargetClass());
    }

    @Test
    public void testQualifierOpenBracket()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "["));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierCloseBracket()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierEmptyBrackets()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierPipeInBrackets()
    {
        assertFalse(parse(getSubTokenName() + "|" + qualifier + "[|]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierCommaInBrackets()
    {
        assertFalse(parse(getSubTokenName() + "|" + qualifier + "[,]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierEmptyType()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierEmptyNotType()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierTypeDot()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=One.]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierNotTypeDot()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=One.]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierNotTypeDoubleDot()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[!TYPE=One..Two]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierTypeEqualDot()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=.One]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierTypeDoubleDot()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=One..Two]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierNotTypeEqualDot()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=.One]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierPrimitivePipe()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierPrimitiveComma()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1,]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierPipePrim()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[|TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierCommaPrim()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[,TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierDoublePipe()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        construct(primaryContext, getTargetClass(), "TestWP2");
        construct(secondaryContext, getTargetClass(), "TestWP2");
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP2||TestWP1]]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierDoubleComma()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=Foo,,!TYPE=Bar]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierAllType()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[ALL|TYPE=TestType]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierTypeAll()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=TestType|ALL]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierTypePrimBad()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=Foo]TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierPrimTypeBadPipe()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1]TYPE=Foo|TYPE=Bar"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierPrimTypeBad()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1]TYPE=Foo"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierTypePrimComma()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=Foo]TestWP1,TYPE=Bar"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierAllPrim()
    {
        assertFalse(parse(getSubTokenName() + "|" + qualifier + "[ALL|TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierPrimAll()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|ALL]"));
        assertNoSideEffects();
    }

    @Test
    public void testBadNoSideEffect()
    {
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP3|TYPE=]"));
        assertNoSideEffects();
    }

    @Test
    public void testQualifierDot()
    {
        boolean parse = parse(getSubTokenName() + '|' + qualifier + "." + qualifier);
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testQualifierAsPrim()
    {
        try
        {
            boolean parse = parse(getSubTokenName() + '|' + qualifier + "[" + qualifier
                    + "]");
            if (parse)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }
        } catch (IllegalArgumentException e)
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testQualifierBadPrim()
    {
        assertTrue(parse(getSubTokenName() + '|' + qualifier + "[String]"));
        assertConstructionError();
    }

    @Test
    public void testQualifierNoConstruct()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        // Explicitly do NOT build TestWP0
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP0|TestWP1]"));
        assertConstructionError();
    }

    @Test
    public void testQualifierTypeCheck()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        // this checks that the TYPE= doesn't consume the |
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TYPE=TestType|TestWP0]"));
        assertConstructionError();
    }

    @Test
    public void testQualifierTypeDotCheck()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        // this checks that the TYPE. doesn't consume the |
        assertTrue(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|"
                + "TYPE.TestType.OtherTestType|TestWP0]"));
        assertConstructionError();
    }

    @Test
    public void testQualifierBadAllNoSideEffect()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        construct(primaryContext, getTargetClass(), "TestWP2");
        construct(secondaryContext, getTargetClass(), "TestWP2");
        // Test with All
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP3|ALL]"));
        assertNoSideEffects();
    }

    @Test
    public void testNegatedQualifierPipe()
    {
        if (!allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + "|!" + qualifier
                    + "[TYPE=Bar|TYPE=Goo]|" + qualifier
                    + "[TYPE=Foo|TYPE=Yea]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testNegatedQualifierPrim()
    {
        if (!allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            construct(secondaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + "|!" + qualifier
                    + "[TestWP1]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testNegatedQualifierParenPrim()
    {
        if (!allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1 (Test)");
            construct(secondaryContext, getTargetClass(), "TestWP1 (Test)");
            assertFalse(parse(getSubTokenName() + "|!" + qualifier
                    + "[TestWP1 (Test)]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testNegatedQualifierAll()
    {
        if (!allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + "|!" + qualifier + "[ALL]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputJoinedDotQualifier()
    {
        boolean parse = parse(getSubTokenName() + '|' + "PC." + qualifier);
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifierOpenBracket()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "["));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifierCloseBracket()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifierEmptyBracket()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifierQualifier()
    {
        try
        {
            boolean parse = parse(getSubTokenName() + '|' + qualifier + "[" + qualifier
                    + "]");
            if (parse)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }
        } catch (IllegalArgumentException e)
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputJoinQualifiedOnlyPipe()
    {
        assertFalse(parse(getSubTokenName() + "|" + qualifier + "[|]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputJoinQualifiedOnlyComma()
    {
        assertFalse(parse(getSubTokenName() + "|" + qualifier + "[,]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputStringQualified()
    {
        assertTrue(parse(getSubTokenName() + '|' + qualifier + "[String]"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputJoinedDotQualified()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(primaryContext, getTargetClass(), "TestWP2");
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1.TestWP2]"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputQualifiedTypeEmpty()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifiedNotTypeEmpty()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifiedTypeUnterminated()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=One.]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifiedNotTypeUnterminated()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=One.]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifiedTypeDoubleSeparator()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=One..Two]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifiedNotTypeDoubleSeparator()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[!TYPE=One..Two]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifiedTypeFalseStart()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=.One]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputQualifiedNotTypeFalseStart()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=.One]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedListEndPipe()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedListEndComma()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1,]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedListStartPipe()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[|TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedListStartComma()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[,TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedListDoubleJoinPipe()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(primaryContext, getTargetClass(), "TestWP2");
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP2||TestWP1]]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedListDoubleJoinComma()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=Foo,,!TYPE=Bar]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedInputNotBuilt()
    {
        // Explicitly do NOT build TestWP2
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertConstructionError();
    }

    @Test
    public void testInvalidQualifiedDanglingType()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1]TYPE=Foo"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedDanglingPrimitive()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=Foo]TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedDanglingTypePipe()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1]TYPE=Foo|TYPE=Bar"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedDanglingPrimitiveComma()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=Foo]TestWP1,TYPE=Bar"));
        assertNoSideEffects();
    }

    @Test
    public void testValidQualifiedInputLotsOr()
            throws PersistenceLayerException
    {
        CDOMObject a = construct(primaryContext, "Typed1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
        CDOMObject b = construct(primaryContext, "Typed2");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
        CDOMObject c = construct(secondaryContext, "Typed1");
        c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
        CDOMObject d = construct(secondaryContext, "Typed2");
        d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
        CDOMObject e = construct(primaryContext, "Typed3");
        e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
        CDOMObject f = construct(primaryContext, "Typed4");
        f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
        CDOMObject g = construct(secondaryContext, "Typed3");
        g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
        CDOMObject h = construct(secondaryContext, "Typed4");
        h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
        runRoundRobin(getSubTokenName() + '|' + qualifier
                + "[TYPE=Bar|TYPE=Goo]|" + qualifier + "[TYPE=Foo|TYPE=Yea]");
    }

    @Test
    public void testValidQualifiedInputLotsAnd()
            throws PersistenceLayerException
    {
        CDOMObject a = construct(primaryContext, "Typed1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
        CDOMObject b = construct(primaryContext, "Typed2");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
        CDOMObject c = construct(secondaryContext, "Typed1");
        c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
        CDOMObject d = construct(secondaryContext, "Typed2");
        d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
        CDOMObject e = construct(primaryContext, "Typed3");
        e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
        CDOMObject f = construct(primaryContext, "Typed4");
        f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
        CDOMObject g = construct(secondaryContext, "Typed3");
        g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
        CDOMObject h = construct(secondaryContext, "Typed4");
        h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
        runRoundRobin(getSubTokenName() + '|' + qualifier
                + "[TYPE=Bar,TYPE=Goo]," + qualifier + "[TYPE=Foo,TYPE=Yea]");
    }

    @Test
    public void testInvalidQualifiedInputCheckTypeEqualLengthBar()
    {
        /*
         * Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
         * consume the |
         */
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TYPE=TestType|TestWP2]"));
        assertConstructionError();
    }

    @Test
    public void testInvalidQualifiedInputCheckTypeDotLengthPipe()
    {
        /*
         * Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
         * consume the |
         */
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertTrue(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|"
                + "TYPE.TestType.OtherTestType|TestWP2]"));
        assertConstructionError();
    }

    @Test
    public void testRoundRobinQualifiedOne() throws PersistenceLayerException
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + qualifier + "[TestWP1]");
    }

    @Test
    public void testRoundRobinQualifiedParen() throws PersistenceLayerException
    {
        construct(primaryContext, getTargetClass(), "TestWP1 (Test)");
        construct(secondaryContext, getTargetClass(), "TestWP1 (Test)");
        runRoundRobin(getSubTokenName() + '|' + qualifier + "[TestWP1 (Test)]");
    }

    @Test
    public void testRoundRobinQualifiedThreeOr()
            throws PersistenceLayerException
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(primaryContext, getTargetClass(), "TestWP2");
        construct(primaryContext, getTargetClass(), "TestWP3");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP2");
        construct(secondaryContext, getTargetClass(), "TestWP3");
        runRoundRobin(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2|TestWP3]");
    }

    @Test
    public void testRoundRobinQualifiedThreeAnd()
            throws PersistenceLayerException
    {
        CDOMObject a = construct(primaryContext, "Typed1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
        CDOMObject b = construct(primaryContext, "Typed2");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
        CDOMObject c = construct(secondaryContext, "Typed1");
        c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
        CDOMObject d = construct(secondaryContext, "Typed2");
        d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
        CDOMObject e = construct(primaryContext, "Typed3");
        e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
        CDOMObject g = construct(secondaryContext, "Typed3");
        g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
        runRoundRobin(getSubTokenName() + '|' + qualifier
                + "[!TYPE=Type1,TYPE=Type2,TYPE=Type3]");
    }

    @Test
    public void testRoundRobinQualifiedFourAndOr()
            throws PersistenceLayerException
    {
        CDOMObject a = construct(primaryContext, "Typed1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
        CDOMObject b = construct(primaryContext, "Typed2");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
        CDOMObject c = construct(secondaryContext, "Typed1");
        c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
        CDOMObject d = construct(secondaryContext, "Typed2");
        d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
        CDOMObject e = construct(primaryContext, "Typed3");
        e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
        CDOMObject f = construct(primaryContext, "Typed4");
        f.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
        CDOMObject g = construct(secondaryContext, "Typed3");
        g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
        CDOMObject h = construct(secondaryContext, "Typed4");
        h.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
        runRoundRobin(getSubTokenName() + '|' + qualifier
                + "[!TYPE=Type1,TYPE=Type2|!TYPE=Type3,TYPE=Type4]");
    }

    @Test
    public void testRoundRobinQualifiedWithEqualType()
            throws PersistenceLayerException
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(primaryContext, getTargetClass(), "TestWP2");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP2");
        CDOMObject a = construct(primaryContext, "Typed1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        CDOMObject b = construct(primaryContext, "Typed2");
        b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
        CDOMObject c = construct(secondaryContext, "Typed1");
        c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        CDOMObject d = construct(secondaryContext, "Typed2");
        d.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
        runRoundRobin(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType]");
    }

    @Test
    public void testRoundRobinQualifiedTestEquals()
            throws PersistenceLayerException
    {
        CDOMObject a = construct(primaryContext, "Typed1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        CDOMObject c = construct(secondaryContext, "Typed1");
        c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        runRoundRobin(getSubTokenName() + '|' + qualifier + "[TYPE=TestType]");
    }

    @Test
    public void testRoundRobinQualifiedTestEqualThree()
            throws PersistenceLayerException
    {
        CDOMObject a = construct(primaryContext, "Typed1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
        a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
        CDOMObject c = construct(secondaryContext, "Typed1");
        c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        c.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
        c.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
        runRoundRobin(getSubTokenName() + '|' + qualifier
                + "[TYPE=TestAltType.TestThirdType.TestType]");
    }

    @Test
    public void testInvalidQualifiedInputAnyItem()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + "|" + qualifier + "[ALL|TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedInputItemAny()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|ALL]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedInputAnyType()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[ALL|TYPE=TestType]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidQualifiedInputTypeAny()
    {
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TYPE=TestType|ALL]"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidQualifiedAddsTypeNoSideEffect()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        construct(primaryContext, getTargetClass(), "TestWP2");
        construct(secondaryContext, getTargetClass(), "TestWP2");
        construct(primaryContext, getTargetClass(), "TestWP3");
        construct(secondaryContext, getTargetClass(), "TestWP3");
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP3|TYPE=]"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidQualifiedAddsBasicNoSideEffect()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        construct(primaryContext, getTargetClass(), "TestWP2");
        construct(secondaryContext, getTargetClass(), "TestWP2");
        construct(primaryContext, getTargetClass(), "TestWP3");
        construct(secondaryContext, getTargetClass(), "TestWP3");
        construct(primaryContext, getTargetClass(), "TestWP4");
        construct(secondaryContext, getTargetClass(), "TestWP4");
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertFalse(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP3||TestWP4]"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidQualifiedAddsAllNoSideEffect()
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        construct(primaryContext, getTargetClass(), "TestWP2");
        construct(secondaryContext, getTargetClass(), "TestWP2");
        construct(primaryContext, getTargetClass(), "TestWP3");
        construct(secondaryContext, getTargetClass(), "TestWP3");
        assertTrue(parse(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
                + "[TestWP1|TestWP2]"));
        assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP3|ALL]"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinTestQualifiedAll()
            throws PersistenceLayerException
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        runRoundRobin(getSubTokenName() + "|" + qualifier + "[ALL]");
    }

    @Test
    public void testInvalidInputJoinedDotNotQualifierAlone()
    {
        boolean parse = parse(getSubTokenName() + '|' + "PC.!" + qualifier);
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierOpenBracket()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier + "["));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierCloseBracket()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier + "]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierEmptyBracket()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier + "[]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierNotQualifier()
    {
        if (allowsNotQualifier())
        {
            boolean parse = parse(getSubTokenName() + '|' + "!" + qualifier + "[!"
                    + qualifier + "]");
            if (parse)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }
        }
    }

    @Test
    public void testInvalidInputJoinNotQualifierOnlyPipe()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + "|!" + qualifier + "[|]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputJoinNotQualifierOnlyComma()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + "|!" + qualifier + "[,]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputStringNotQualifier()
    {
        if (allowsNotQualifier())
        {
            assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[String]"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputJoinedDotNotQualifier()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            construct(primaryContext, getTargetClass(), "TestWP2");
            assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1.TestWP2]"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputNotQualifierTypeEmpty()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierNotTypeEmpty()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[!TYPE=]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierTypeUnterminated()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=One.]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierNotTypeUnterminated()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[!TYPE=One.]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierTypeDoubleSeparator()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=One..Two]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierNotTypeDoubleSeparator()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[!TYPE=One..Two]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierTypeFalseStart()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=.One]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputNotQualifierNotTypeFalseStart()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[!TYPE=.One]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierListEndPipe()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierListEndComma()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1,]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierListStartPipe()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[|TestWP1]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierListStartComma()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[,TestWP1]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierListDoubleJoinPipe()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            construct(primaryContext, getTargetClass(), "TestWP2");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP2||TestWP1]]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierListDoubleJoinComma()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=Foo,,!TYPE=Bar]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierInputNotBuilt()
    {
        if (allowsNotQualifier())
        {
            // Explicitly do NOT build TestWP2
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TestWP2]"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidNotQualifierDanglingType()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1]TYPE=Foo"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierDanglingPrimitive()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=Foo]TestWP1"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierDanglingTypePipe()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1]TYPE=Foo|TYPE=Bar"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierDanglingPrimitiveComma()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=Foo]TestWP1,TYPE=Bar"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testValidNotQualifierInputLotsOr()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            CDOMObject a = construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject b = construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject c = construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject d = construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject e = construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject f = construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            CDOMObject g = construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject h = construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=Bar|TYPE=Goo]|!" + qualifier
                    + "[TYPE=Foo|TYPE=Yea]");
        }
    }

    @Test
    public void testValidNotQualifierInputLotsAnd()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            CDOMObject a = construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject b = construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject c = construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject d = construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject e = construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject f = construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            CDOMObject g = construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject h = construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=Bar,TYPE=Goo],!" + qualifier
                    + "[TYPE=Foo,TYPE=Yea]");
        }
    }

    @Test
    public void testInvalidNotQualifierInputCheckTypeEqualLengthBar()
    {
        if (allowsNotQualifier())
        {
            /*
             * Explicitly do NOT build TestWP2 (this checks that the TYPE=
             * doesn't consume the |
             */
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TYPE=TestType|TestWP2]"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidNotQualifierInputCheckTypeDotLengthPipe()
    {
        if (allowsNotQualifier())
        {
            /*
             * Explicitly do NOT build TestWP2 (this checks that the TYPE=
             * doesn't consume the |
             */
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|" + "TYPE.TestType.OtherTestType|TestWP2]"));
            assertConstructionError();
        }
    }

    @Test
    public void testRoundRobinNotQualifierOne()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            construct(secondaryContext, getTargetClass(), "TestWP1");
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1]");
        }
    }

    @Test
    public void testRoundRobinNotQualifierParen()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1 (Test)");
            construct(secondaryContext, getTargetClass(), "TestWP1 (Test)");
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1 (Test)]");
        }
    }

    @Test
    public void testRoundRobinNotQualifierThreeOr()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            construct(primaryContext, getTargetClass(), "TestWP2");
            construct(primaryContext, getTargetClass(), "TestWP3");
            construct(secondaryContext, getTargetClass(), "TestWP1");
            construct(secondaryContext, getTargetClass(), "TestWP2");
            construct(secondaryContext, getTargetClass(), "TestWP3");
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TestWP2|TestWP3]");
        }
    }

    @Test
    public void testRoundRobinNotQualifierThreeAnd()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            CDOMObject a = construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject b = construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject c = construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject d = construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject e = construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject g = construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[!TYPE=Type1,TYPE=Type2,TYPE=Type3]");
        }
    }

    @Test
    public void testRoundRobinNotQualifierFourAndOr()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            CDOMObject a = construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject b = construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject c = construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject d = construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject e = construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject f = construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
            CDOMObject g = construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject h = construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[!TYPE=Type1,TYPE=Type2|!TYPE=Type3,TYPE=Type4]");
        }
    }

    @Test
    public void testRoundRobinNotQualifierWithEqualType()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            construct(primaryContext, getTargetClass(), "TestWP2");
            construct(secondaryContext, getTargetClass(), "TestWP1");
            construct(secondaryContext, getTargetClass(), "TestWP2");
            CDOMObject a = construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject b = construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            CDOMObject c = construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType]");
        }
    }

    @Test
    public void testRoundRobinNotQualifierTestEquals()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            CDOMObject a = construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject c = construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=TestType]");
        }
    }

    @Test
    public void testRoundRobinNotQualifierTestEqualThree()
            throws PersistenceLayerException
    {
        if (allowsNotQualifier())
        {
            CDOMObject a = construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            CDOMObject c = construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=TestAltType.TestThirdType.TestType]");
        }
    }

    @Test
    public void testInvalidNotQualifierInputAnyItem()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + "|!" + qualifier
                    + "[ALL|TestWP1]"));
            assertNoSideEffects();
        }

    }

    @Test
    public void testInvalidNotQualifierInputItemAny()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|ALL]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierInputAnyType()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[ALL|TYPE=TestType]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidNotQualifierInputTypeAny()
    {
        if (allowsNotQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TYPE=TestType|ALL]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidNotQualifierAddsTypeNoSideEffect()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            construct(secondaryContext, getTargetClass(), "TestWP1");
            construct(primaryContext, getTargetClass(), "TestWP2");
            construct(secondaryContext, getTargetClass(), "TestWP2");
            construct(primaryContext, getTargetClass(), "TestWP3");
            construct(secondaryContext, getTargetClass(), "TestWP3");
            assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TestWP2]"));
            assertTrue(parseSecondary(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TestWP2]"));
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP3|TYPE=]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidNotQualifierAddsBasicNoSideEffect()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            construct(secondaryContext, getTargetClass(), "TestWP1");
            construct(primaryContext, getTargetClass(), "TestWP2");
            construct(secondaryContext, getTargetClass(), "TestWP2");
            construct(primaryContext, getTargetClass(), "TestWP3");
            construct(secondaryContext, getTargetClass(), "TestWP3");
            construct(primaryContext, getTargetClass(), "TestWP4");
            construct(secondaryContext, getTargetClass(), "TestWP4");
            assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TestWP2]"));
            assertTrue(parseSecondary(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TestWP2]"));
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP3||TestWP4]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidNotQualifierAddsAllNoSideEffect()
    {
        if (allowsNotQualifier())
        {
            construct(primaryContext, getTargetClass(), "TestWP1");
            construct(secondaryContext, getTargetClass(), "TestWP1");
            construct(primaryContext, getTargetClass(), "TestWP2");
            construct(secondaryContext, getTargetClass(), "TestWP2");
            construct(primaryContext, getTargetClass(), "TestWP3");
            construct(secondaryContext, getTargetClass(), "TestWP3");
            assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TestWP2]"));
            assertTrue(parseSecondary(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP1|TestWP2]"));
            assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
                    + "[TestWP3|ALL]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinTestNotQualifierAll()
            throws PersistenceLayerException
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        if (allowsNotQualifier())
        {
            runRoundRobin(getSubTokenName() + "|!" + qualifier + "[ALL]");
        } else
        {
            boolean parse = parse(getSubTokenName() + "|!" + qualifier + "[ALL]");
            if (parse)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }
        }
    }

    @Test
    public void testRoundRobinTestQualifierRaw()
            throws PersistenceLayerException
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        if (allowsLoneQualifier())
        {
            runRoundRobin(getSubTokenName() + '|' + qualifier);
        } else
        {
            boolean parse = parse(getSubTokenName() + '|' + qualifier);
            if (parse)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }
        }
    }

    @Test
    public void testRoundRobinTestNotQualifierRaw()
            throws PersistenceLayerException
    {
        construct(primaryContext, getTargetClass(), "TestWP1");
        construct(secondaryContext, getTargetClass(), "TestWP1");
        if (allowsNotQualifier() && allowsLoneQualifier())
        {
            runRoundRobin(getSubTokenName() + "|!" + qualifier);
        } else
        {
            boolean parse = parse(getSubTokenName() + "|!" + qualifier);
            if (parse)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }
        }
    }

    protected boolean allowsLoneQualifier()
    {
        return true;
    }

    @Test
    public void testRoundRobinMultTypes()
            throws PersistenceLayerException
    {
        CDOMObject a = construct(primaryContext, "Typed1");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Buckler"));
        CDOMObject b = construct(primaryContext, "Typed2");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        CDOMObject c = construct(secondaryContext, "Typed1");
        c.addToListFor(ListKey.TYPE, Type.getConstant("Buckler"));
        CDOMObject d = construct(secondaryContext, "Typed2");
        d.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        CDOMObject e = construct(primaryContext, "Typed3");
        e.addToListFor(ListKey.TYPE, Type.getConstant("Light"));
        CDOMObject g = construct(secondaryContext, "Typed3");
        g.addToListFor(ListKey.TYPE, Type.getConstant("Light"));
        runRoundRobin(getSubTokenName() + '|' + qualifier + "[TYPE=Buckler|TYPE=Heavy|TYPE=Light]");
    }

    @Test
    public void testTargetCheck()
    {
        if (target == null)
        {
            assertFalse(parse(getSubTokenName() + '|' + token + "=Tgt"
                    + "[TYPE=TestType|ALL]"));
        } else
        {
            assertFalse(parse(getSubTokenName() + '|' + token
                    + "[TYPE=TestType|ALL]"));
        }
        assertNoSideEffects();
    }

    protected static final MultToken ABILITY_MULT_TOKEN = new plugin.lsttokens.ability.MultToken();
    protected static final plugin.lsttokens.choose.LangToken CHOOSE_LANG_TOKEN =
            new plugin.lsttokens.choose.LangToken();
    protected static final plugin.lsttokens.ChooseLst CHOOSE_TOKEN = new plugin.lsttokens.ChooseLst();
    protected static final VisibleToken ABILITY_VISIBLE_TOKEN = new plugin.lsttokens.ability.VisibleToken();
    protected static final AutoLst AUTO_TOKEN = new plugin.lsttokens.AutoLst();
    protected static final LangToken AUTO_LANG_TOKEN = new plugin.lsttokens.auto.LangToken();
    protected static final ProficiencyToken EQUIP_PROFICIENCY_TOKEN = new plugin.lsttokens.equipment.ProficiencyToken();
    protected static final TypeLst EQUIP_TYPE_TOKEN = new plugin.lsttokens.TypeLst();
    protected static final LangBonusToken LANGBONUS_PRIM = new plugin.primitive.language.LangBonusToken();
    protected static final plugin.qualifier.language.PCToken PC_QUAL = new plugin.qualifier.language.PCToken();

    protected void finishLoad()
    {
        BuildUtilities.createFact(primaryContext, "ClassType", getTargetClass());
        FactDefinition<?, ?> fd =
                BuildUtilities.createFact(primaryContext, "SpellType", getTargetClass());
        fd.setSelectable(true);
        SourceFileLoader.processFactDefinitions(primaryContext);
        primaryContext.getReferenceContext().buildDeferredObjects();
        primaryContext.getReferenceContext().buildDerivedObjects();
        primaryContext.resolveDeferredTokens();
        assertTrue(primaryContext.getReferenceContext().resolveReferences(null));
        primaryContext.resolvePostValidationTokens();
        primaryContext.resolvePostDeferredTokens();
        primaryContext.loadCampaignFacets();
    }

    protected PCStat str;
    protected PCStat cha;
    protected PCStat dex;
    protected PCStat wis;
    protected PCStat intel;
    protected PCAlignment lg;
    protected PCAlignment ln;
    protected PCAlignment le;
    protected PCAlignment ng;
    protected PCAlignment tn;
    protected PCAlignment ne;
    protected PCAlignment cg;
    protected PCAlignment cn;
    protected PCAlignment ce;
    protected SizeAdjustment colossal;
    protected SizeAdjustment gargantuan;
    protected SizeAdjustment huge;
    protected SizeAdjustment large;
    protected SizeAdjustment medium;
    protected SizeAdjustment small;
    protected SizeAdjustment tiny;
    protected SizeAdjustment diminutive;
    protected SizeAdjustment fine;

    protected void setUpPC()
    {
        TokenRegistration.register(AUTO_LANG_TOKEN);
        TokenRegistration.register(ABILITY_VISIBLE_TOKEN);
        TokenRegistration.register(AUTO_TOKEN);
        TokenRegistration.register(CHOOSE_TOKEN);
        TokenRegistration.register(CHOOSE_LANG_TOKEN);
        TokenRegistration.register(ABILITY_MULT_TOKEN);
        TokenRegistration.register(EQUIP_TYPE_TOKEN);
        TokenRegistration.register(EQUIP_PROFICIENCY_TOKEN);
        TokenRegistration.register(LANGBONUS_PRIM);
        TokenRegistration.register(PC_QUAL);

        Globals.setUseGUI(false);
        Globals.emptyLists();
        resetContext();
        BuildUtilities.buildUnselectedRace(Globals.getContext());
        GameMode gamemode = SettingsHandler.getGame();

        str = BuildUtilities.createStat("Strength", "STR", "A");
        str.put(VariableKey.getConstant("LOADSCORE"), FormulaFactory
                .getFormulaFor("STRSCORE"));
        str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"), FormulaFactory
                .getFormulaFor(2));
        dex = BuildUtilities.createStat("Dexterity", "DEX", "B");
        PCStat con = BuildUtilities.createStat("Constitution", "CON", "C");
        intel = BuildUtilities.createStat("Intelligence", "INT", "D");
        wis = BuildUtilities.createStat("Wisdom", "WIS", "E");
        cha = BuildUtilities.createStat("Charisma", "CHA", "F");

        AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
        lg = BuildUtilities.createAlignment("Lawful Good", "LG");
        ref.importObject(lg);
        ln = BuildUtilities.createAlignment("Lawful Neutral", "LN");
        ref.importObject(ln);
        le = BuildUtilities.createAlignment("Lawful Evil", "LE");
        ref.importObject(le);
        ng = BuildUtilities.createAlignment("Neutral Good", "NG");
        ref.importObject(ng);
        tn = BuildUtilities.createAlignment("True Neutral", "TN");
        ref.importObject(tn);
        ne = BuildUtilities.createAlignment("Neutral Evil", "NE");
        ref.importObject(ne);
        cg = BuildUtilities.createAlignment("Chaotic Good", "CG");
        ref.importObject(cg);
        cn = BuildUtilities.createAlignment("Chaotic Neutral", "CN");
        ref.importObject(cn);
        ce = BuildUtilities.createAlignment("Chaotic Evil", "CE");
        ref.importObject(ce);
        ref.importObject(BuildUtilities.createAlignment("None", "NONE"));
        ref.importObject(BuildUtilities.createAlignment("Deity's", "Deity"));

        gamemode.setBonusFeatLevels("3|3");

        SettingsHandler.setGame("3.5");

        ref.importObject(str);
        ref.importObject(dex);
        ref.importObject(con);
        ref.importObject(intel);
        ref.importObject(wis);
        ref.importObject(cha);

        fine = BuildUtilities.createSize("Fine", 0);
        diminutive = BuildUtilities.createSize("Diminutive", 1);
        tiny = BuildUtilities.createSize("Tiny", 2);
        small = BuildUtilities.createSize("Small", 3);
        medium = BuildUtilities.createSize("Medium", 4);
        medium.put(ObjectKey.IS_DEFAULT_SIZE, true);
        large = BuildUtilities.createSize("Large", 5);
        huge = BuildUtilities.createSize("Huge", 6);
        gargantuan = BuildUtilities.createSize("Gargantuan", 7);
        colossal = BuildUtilities.createSize("Colossal", 8);

        SourceFileLoader.createLangBonusObject(Globals.getContext());
        ref.constructNowIfNecessary(Language.class, "DummyLanguage");
    }

    public void testEmptyIdentity() throws InstantiationException,
            IllegalAccessException
    {
        QualifierToken<?> one = getQualifierClass().newInstance();
        QualifierToken<?> two = getQualifierClass().newInstance();
        assertEquals(one, two);
    }

    protected abstract Class<? extends QualifierToken> getQualifierClass();
}
