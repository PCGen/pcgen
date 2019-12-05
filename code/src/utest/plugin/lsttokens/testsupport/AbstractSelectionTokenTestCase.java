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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.util.Arrays;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractSelectionTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
        extends AbstractCDOMTokenTestCase<T>
{

    public abstract CDOMSecondaryToken<?> getSubToken();

    public String getSubTokenName()
    {
        return getSubToken().getTokenName();
    }

    public abstract Class<TC> getTargetClass();

    public abstract boolean isTypeLegal();

    public abstract boolean isAllLegal();

    public String getAllString()
    {
        return "ALL";
    }

    public String getTypePrefix()
    {
        return "";
    }

    public abstract boolean allowsParenAsSub();

    public abstract boolean allowsFormula();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(getSubToken());
    }

    public char getJoinCharacter()
    {
        return ',';
    }

    protected TC construct(LoadContext loadContext, String one)
    {
        return loadContext.getReferenceContext().constructCDOMObject(getTargetClass(), one);
    }

    protected CDOMObject constructTyped(LoadContext loadContext, String one)
    {
        return loadContext.getReferenceContext().constructCDOMObject(getTargetClass(), one);
    }

    @Test
    public void testInvalidInputEmptyString()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOnlySubToken()
    {
        assertFalse(parse(getSubTokenName()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOnlySubTokenPipe()
    {
        assertFalse(parse(getSubTokenName() + '|'));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputJoinOnly()
    {
        assertFalse(parse(getSubTokenName() + '|'
                + Character.toString(getJoinCharacter())));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputString()
    {
        assertTrue(parse(getSubTokenName() + '|' + "String"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputType()
    {
        assertTrue(parse(getSubTokenName() + '|' + "TestType"));
        assertConstructionError();
    }

    // TODO Allow this once a method checks to exist if TestWP1 is a formula vs.
    // an object
    // @Test
    // public void testInvalidInputJoinedPipe() throws PersistenceLayerException
    // {
    // construct(primaryContext, "TestWP1");
    // construct(primaryContext, "TestWP2");
    // boolean parse = parse(getSubTokenName() + '|' + "TestWP1|TestWP2");
    // if (parse)
    // {
    // assertFalse(primaryContext.ref.validate());
    // }
    // else
    // {
    // assertNoSideEffects();
    // }
    // }

    @Test
    public void testInvalidTooManyPipe()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean parse = parse(getSubTokenName() + "|Formula|TestWP1|TestWP2");
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputJoinedDot()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertTrue(parse(getSubTokenName() + '|' + "TestWP1.TestWP2"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputNegativeFormula()
    {
        if (allowsFormula())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "-1|TestWP1"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputZeroFormula()
    {
        if (allowsFormula())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "0|TestWP1"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTypeEmpty()
    {
        if (isTypeLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + getTypePrefix()
                    + "TYPE="));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTypeUnterminated()
    {
        if (isTypeLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + getTypePrefix()
                    + "TYPE=One."));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputClearDotTypeDoubleSeparator()
    {
        if (isTypeLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + getTypePrefix()
                    + "TYPE=One..Two"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputClearDotTypeFalseStart()
    {
        if (isTypeLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + getTypePrefix()
                    + "TYPE=.One"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputAll()
    {
        if (!isAllLegal())
        {
            try
            {
                boolean parse = parse(getSubTokenName() + '|' + "ALL");
                if (parse)
                {
                    // Only need to check if parsed as true
                    assertConstructionError();
                } else
                {
                    assertNoSideEffects();
                }
            } catch (IllegalArgumentException e)
            {
                // This is okay too
                assertNoSideEffects();
            }
        }
    }

    @Test
    public void testInvalidInputTypeEquals()
    {
        if (!isTypeLegal())
        {
            try
            {
                boolean parse = parse(getSubTokenName() + '|' + getTypePrefix()
                        + "TYPE=Foo");
                if (parse)
                {
                    // Only need to check if parsed as true
                    assertConstructionError();
                } else
                {
                    assertNoSideEffects();
                }
            } catch (IllegalArgumentException e)
            {
                // This is okay too
                assertNoSideEffects();
            }
        }
    }

    @Test
    public void testInvalidInputAny()
    {
        if (!isAllLegal())
        {
            try
            {
                boolean result = parse("ANY");
                if (result)
                {
                    assertConstructionError();
                } else
                {
                    assertNoSideEffects();
                }
            } catch (IllegalArgumentException e)
            {
                //This is okay too
                assertNoSideEffects();
            }
        }
    }

    @Test
    public void testInvalidInputCheckType()
    {
        if (!isTypeLegal())
        {
            try
            {
                boolean result = getToken().parseToken(primaryContext, primaryProf,
                        "TYPE=TestType").passed();
                if (result)
                {
                    assertConstructionError();
                } else
                {
                    assertNoSideEffects();
                }
            } catch (IllegalArgumentException e)
            {
                // This is okay too
                assertNoSideEffects();
            }
        }
    }

    @Test
    public void testInvalidDoubleList()
    {
        if (allowsParenAsSub())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            boolean ret = parse(getSubTokenName() + '|'
                    + "TestWP1 (Test,TestTwo)");
            if (ret)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }
        }
    }

    @Test
    public void testInvalidListEnd()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + "TestWP1"
                + getJoinCharacter()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListStart()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + getJoinCharacter()
                + "TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListDoubleJoin()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse(getSubTokenName() + '|' + "TestWP2"
                + getJoinCharacter() + getJoinCharacter() + "TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputCheckMult()
    {
        // Explicitly do NOT build TestWP2
        construct(primaryContext, "TestWP1");
        assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
                + getJoinCharacter() + "TestWP2"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputCheckTypeEqualLength()
    {
        // Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
        // consume the |
        if (isTypeLegal())
        {
            construct(primaryContext, "TestWP1");
            assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
                    + getJoinCharacter() + getTypePrefix() + "TYPE=TestType"
                    + getJoinCharacter() + "TestWP2"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputCheckTypeDotLength()
    {
        // Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
        // consume the |
        if (isTypeLegal())
        {
            construct(primaryContext, "TestWP1");
            assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
                    + getJoinCharacter() + getTypePrefix()
                    + "TYPE.TestType.OtherTestType" + getJoinCharacter()
                    + "TestWP2"));
            assertConstructionError();
        }
    }

    @Test
    public void testValidInputTestDot()
    {
        if (isTypeLegal())
        {
            CDOMObject a = constructTyped(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject c = constructTyped(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            assertTrue(parse(getSubTokenName() + '|' + getTypePrefix()
                    + "TYPE.TestType"));
            assertCleanConstruction();
        }
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1");
    }

    @Test
    public void testRoundRobinOnePreFooler() throws PersistenceLayerException
    {
        construct(primaryContext, "Prefool");
        construct(secondaryContext, "Prefool");
        runRoundRobin(getSubTokenName() + '|' + "Prefool");
    }

    @Test
    public void testRoundRobinParen() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1 (Test)");
        construct(secondaryContext, "TestWP1 (Test)");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1 (Test)");
    }

    @Test
    public void testRoundRobinCount() throws PersistenceLayerException
    {
        if (allowsFormula())
        {
            construct(primaryContext, "TestWP1 (Test)");
            construct(secondaryContext, "TestWP1 (Test)");
            runRoundRobin(getSubTokenName() + '|' + "4|TestWP1 (Test)");
        }
    }

    @Test
    public void testRoundRobinFormulaCount() throws PersistenceLayerException
    {
        if (allowsFormula())
        {
            construct(primaryContext, "TestWP1 (Test)");
            construct(secondaryContext, "TestWP1 (Test)");
            runRoundRobin(getSubTokenName() + '|' + "INT|TestWP1 (Test)");
        }
    }

    @Test
    public void testRoundRobinHardFormulaCount() throws PersistenceLayerException
    {
        if (allowsFormula())
        {
            construct(primaryContext, "TestWP1 (Test)");
            construct(secondaryContext, "TestWP1 (Test)");
            runRoundRobin(getSubTokenName() + '|'
                    + "if(var(\"SIZE==3||SIZE==4\"),5,0)|TestWP1 (Test)");
        }
    }

    @Test
    public void testRoundRobinParenSub() throws PersistenceLayerException
    {
        if (allowsParenAsSub())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getSubTokenName() + '|' + "TestWP1 (Test)");
        }
    }

    @Test
    public void testRoundRobinParenDoubleSub() throws PersistenceLayerException
    {
        if (allowsParenAsSub())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getSubTokenName() + '|' + "TestWP1 (Test(Two))");
        }
    }

    @Test
    public void testRoundRobinThree() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1" + getJoinCharacter()
                + "TestWP2" + getJoinCharacter() + "TestWP3");
    }

    @Test
    public void testRoundRobinWithEqualType() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            CDOMObject a = constructTyped(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject b = constructTyped(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            CDOMObject c = constructTyped(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = constructTyped(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            runRoundRobin(getSubTokenName() + '|' + "TestWP1"
                    + getJoinCharacter() + "TestWP2" + getJoinCharacter()
                    + getTypePrefix() + "TYPE=OtherTestType"
                    + getJoinCharacter() + getTypePrefix() + "TYPE=TestType");
        }
    }

    @Test
    public void testRoundRobinTestEquals() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            CDOMObject a = constructTyped(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject c = constructTyped(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin(getSubTokenName() + '|' + getTypePrefix()
                    + "TYPE=TestType");
        }
    }

    @Test
    public void testRoundRobinTestEqualThree() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            CDOMObject a = constructTyped(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject c = constructTyped(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin(getSubTokenName() + '|' + getTypePrefix()
                    + "TYPE=TestAltType.TestThirdType.TestType");
        }
    }

    @Test
    public void testInvalidInputAnyItem()
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + getAllString()
                    + getJoinCharacter() + "TestWP1"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputItemAny()
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "TestWP1"
                    + getJoinCharacter() + getAllString()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputAnyType()
    {
        if (isTypeLegal() && isAllLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + getAllString()
                    + getJoinCharacter() + getTypePrefix() + "TYPE=TestType"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTypeAny()
    {
        if (isTypeLegal() && isAllLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + getTypePrefix()
                    + "TYPE=TestType" + getJoinCharacter() + getAllString()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidAddsTypeNoSideEffect()
    {
        if (isTypeLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
                    + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1"
                    + getJoinCharacter() + "TestWP2"));
            assertFalse(parse(getSubTokenName() + '|' + "TestWP3"
                    + getJoinCharacter() + getTypePrefix() + "TYPE="));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidAddsBasicNoSideEffect()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP3");
        construct(primaryContext, "TestWP4");
        construct(secondaryContext, "TestWP4");
        assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
                + getJoinCharacter() + "TestWP2"));
        assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1"
                + getJoinCharacter() + "TestWP2"));
        assertFalse(parse(getSubTokenName() + '|' + "TestWP3"
                + getJoinCharacter() + getJoinCharacter() + "TestWP4"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidAddsAllNoSideEffect()
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
                    + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1"
                    + getJoinCharacter() + "TestWP2"));
            assertFalse(parse(getSubTokenName() + '|' + "TestWP3"
                    + getJoinCharacter() + getAllString()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinTestAll() throws PersistenceLayerException
    {
        if (isAllLegal())
        {
            construct(primaryContext, "Typed1");
            construct(secondaryContext, "Typed1");
            runRoundRobin(getSubTokenName() + '|' + getAllString());
        }
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return getSubTokenName() + '|' + "TestWP1" + getJoinCharacter()
                + "TestWP2" + getJoinCharacter() + "TestWP3";
    }

    @Override
    protected String getLegalValue()
    {
        return getSubTokenName() + '|' + "TestWP1" + getJoinCharacter()
                + "TestWP2";
    }

    protected PersistentTransitionChoice<TC> buildChoice(
            CDOMReference<TC>... refs)
    {
        ReferenceChoiceSet<TC> rcs = buildRCS(refs);
        assertTrue(rcs.getGroupingState().isValid());
        return buildTC(rcs);
    }

    protected PersistentTransitionChoice<TC> buildTC(ReferenceChoiceSet<TC> rcs)
    {
        ChoiceSet<TC> cs = new ChoiceSet<>(getSubTokenName(), rcs);
        cs.setTitle("Pick a " + getTargetClass().getSimpleName());
        return new ConcretePersistentTransitionChoice<>(
                cs, FormulaFactory.ONE);
    }

    protected ReferenceChoiceSet<TC> buildRCS(CDOMReference<TC>... refs)
    {
        return new ReferenceChoiceSet<>(Arrays.asList(refs));
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
