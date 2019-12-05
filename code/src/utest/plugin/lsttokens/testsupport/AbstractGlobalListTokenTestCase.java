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
package plugin.lsttokens.testsupport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.testsupport.ConsolidationRule.AppendingConsolidation;

import org.junit.jupiter.api.Test;

public abstract class AbstractGlobalListTokenTestCase<TC extends CDOMObject>
        extends AbstractGlobalTokenTestCase
{

    public abstract Class<TC> getTargetClass();

    public abstract boolean isTypeLegal();

    public abstract boolean isAllLegal();

    public abstract char getJoinCharacter();

    @Test
    public void testArchitecture()
    {
        /*
         * This case is not handled well by this generic tester, and thus should
         * be prohibited in this level of automation... - Tom Parker 2/24/2007
         */
        assertFalse(isTypeLegal() && (getJoinCharacter() == '.'));
    }

    @Test
    public void testInvalidInputString()
    {
        assertTrue(parse("String"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputType()
    {
        assertTrue(parse("TestType"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputJoinedComma()
    {
        if (getJoinCharacter() != ',')
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            assertTrue(parse("TestWP1,TestWP2"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputJoinedPipe()
    {
        if (getJoinCharacter() != '|')
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            assertTrue(parse("TestWP1|TestWP2"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputJoinedDot()
    {
        if (getJoinCharacter() != '.')
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            assertTrue(parse("TestWP1.TestWP2"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputTypeEmpty()
    {
        if (isTypeLegal())
        {
            assertFalse(parse("TYPE="));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTypeUnterminated()
    {
        if (isTypeLegal())
        {
            assertFalse(parse("TYPE=One."));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTypeDoubleSeparator()
    {
        if (isTypeLegal())
        {
            assertFalse(parse("TYPE=One..Two"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTypeFalseStart()
    {
        if (isTypeLegal())
        {
            assertFalse(parse("TYPE=.One"));
            assertNoSideEffects();
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
                boolean result = getReadToken().parseToken(primaryContext, primaryProf,
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
    public void testInvalidListEnd()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1" + getJoinCharacter()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListStart()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse(getJoinCharacter() + "TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListDoubleJoin()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
                + "TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputCheckMult()
    {
        // Explicitly do NOT build TestWP2
        construct(primaryContext, "TestWP1");
        assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
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
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TYPE=TestType"
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
            assertTrue(parse("TestWP1" + getJoinCharacter()
                    + "TYPE.TestType.OtherTestType" + getJoinCharacter()
                    + "TestWP2"));
            assertConstructionError();
        }
    }

    @Test
    public void testValidInputTypeDot()
    {
        if (isTypeLegal())
        {
            CDOMObject b = construct(primaryContext, "TestWP3");
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = construct(secondaryContext, "TestWP3");
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            assertTrue(parse("TYPE.TestType"));
            assertCleanConstruction();
        }
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("TestWP1");
    }

    @Test
    public void testRoundRobinOnePreFooler() throws PersistenceLayerException
    {
        construct(primaryContext, "Prefool");
        construct(secondaryContext, "Prefool");
        runRoundRobin("Prefool");
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
        runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP2"
                + getJoinCharacter() + "TestWP3");
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
            CDOMObject a = construct(primaryContext, "TestWP3");
            a.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            CDOMObject c = construct(secondaryContext, "TestWP3");
            c.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            CDOMObject b = construct(primaryContext, "TestWP4");
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = construct(secondaryContext, "TestWP4");
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP2"
                    + getJoinCharacter() + "TYPE=OtherTestType"
                    + getJoinCharacter() + "TYPE=TestType");
        }
    }

    @Test
    public void testRoundRobinTestEquals() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            CDOMObject b = construct(primaryContext, "TestWP3");
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = construct(secondaryContext, "TestWP3");
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin("TYPE=TestType");
        }
    }

    @Test
    public void testRoundRobinTestEqualThree() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            CDOMObject b = construct(primaryContext, "TestWP3");
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = construct(secondaryContext, "TestWP3");
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin("TYPE=TestAltType.TestThirdType.TestType");
        }
    }

    @Test
    public void testInvalidInputAnyItem()
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse("ALL" + getJoinCharacter() + "TestWP1"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputItemAny()
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse("TestWP1" + getJoinCharacter() + "ALL"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputAnyType()
    {
        if (isTypeLegal() && isAllLegal())
        {
            assertFalse(parse("ALL" + getJoinCharacter() + "TYPE=TestType"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTypeAny()
    {
        if (isTypeLegal() && isAllLegal())
        {
            assertFalse(parse("TYPE=TestType" + getJoinCharacter() + "ALL"));
            assertNoSideEffects();
        }
    }

    protected CDOMObject construct(LoadContext loadContext, String one)
    {
        return loadContext.getReferenceContext().constructCDOMObject(getTargetClass(), one);
    }

    public abstract boolean isClearLegal();

    public abstract boolean isClearDotLegal();

    @Test
    public void testInputInvalidClear()
    {
        if (isClearLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            assertFalse(parse("TestWP1" + getJoinCharacter() + Constants.LST_DOT_CLEAR));
            assertNoSideEffects();
        }
    }

    //TODO: This is commented out due to a design issue in the tokens that do not persist removal references
//	@Test
//	public void testInputInvalidClearDot()
//	{
//		if (isClearDotLegal())
//		{
//			// DoNotConstruct TestWP1
//			assertTrue(parse(".CLEAR.TestWP1"));
//			assertConstructionError();
//		}
//	}

    @Test
    public void testInputInvalidAddsAfterClearDotNoSideEffect()
    {
        if (isClearDotLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
                    + "TestWP2"));
            assertFalse(parse("TestWP3" + getJoinCharacter() + ".CLEAR.TestWP2"
                    + getJoinCharacter() + "ALL"));
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
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
                    + "TestWP2"));
            assertFalse(parse("TestWP3" + getJoinCharacter() + "TYPE="));
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
        assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertTrue(parseSecondary("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertFalse(parse("TestWP3" + getJoinCharacter() + getJoinCharacter()
                + "TestWP4"));
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
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
                    + "TestWP2"));
            assertFalse(parse("TestWP3" + getJoinCharacter() + "ALL"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidAddsAfterClearNoSideEffect()
    {
        if (isClearLegal() && isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
                    + "TestWP2"));
            assertFalse(parse(Constants.LST_DOT_CLEAR + getJoinCharacter() + "TestWP3"
                    + getJoinCharacter() + "ALL"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidTypeClearDotNoSideEffect()
    {
        if (isClearDotLegal() && isTypeLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
                    + "TestWP2"));
            assertFalse(parse("TestWP3" + getJoinCharacter() + ".CLEAR.TestWP1"
                    + getJoinCharacter() + ".CLEAR.TYPE="));
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinThreeDupe() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP3");
        runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP1"
                + getJoinCharacter() + "TestWP3");
    }

    @Test
    public void testRoundRobinTestAll() throws PersistenceLayerException
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin("ALL");
        }
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "TestWP2";
    }

    @Override
    protected String getLegalValue()
    {
        return "TestWP1";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return new AppendingConsolidation(getJoinCharacter());
    }

    @Test
    public void testValidInputClearWorking()
    {
        if (isClearLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            assertTrue(parse("TestWP1"));
            assertTrue(parse(getClearString()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testValidInputClearJoinWorking()
    {
        if (isClearLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            assertTrue(parse(getClearString() + getJoinCharacter() + "TestWP1"));
            assertTrue(parseSecondary("TestWP1"));
            assertNoSideEffects();
        }
    }

    protected static String getClearString()
    {
        return Constants.LST_DOT_CLEAR;
    }

}
