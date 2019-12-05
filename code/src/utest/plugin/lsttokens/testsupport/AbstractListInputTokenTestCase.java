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
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.testsupport.ConsolidationRule.AppendingConsolidation;

import org.junit.jupiter.api.Test;

public abstract class AbstractListInputTokenTestCase<T extends CDOMObject, TC extends Loadable>
        extends AbstractCDOMTokenTestCase<T>
{

    public abstract Class<TC> getTargetClass();

    public abstract boolean allowDups();

    public abstract boolean isTypeLegal();

    public abstract boolean isAllLegal();

    public abstract char getJoinCharacter();

    protected TC construct(LoadContext loadContext, String one)
    {
        return loadContext.getReferenceContext().constructNowIfNecessary(getTargetClass(), one);
    }

    public abstract boolean isClearLegal();

    public abstract boolean isClearDotLegal();

    public String getClearString()
    {
        return Constants.LST_DOT_CLEAR;
    }

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
    public void testInvalidInputEmptyString()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputJoinOnly()
    {
        assertFalse(parse(Character.toString(getJoinCharacter())));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputString()
    {
        if (!isMaster())
        {
            assertTrue(parse("String"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputType()
    {
        if (!isMaster())
        {
            assertTrue(parse("TestType"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputJoinedComma()
    {
        if (!isMaster() && (getJoinCharacter() != ','))
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
        if (!isMaster() && (getJoinCharacter() != '|'))
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            boolean parse = parse("TestWP1|TestWP2");
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
    public void testInvalidInputJoinedDot()
    {
        if (!isMaster() && (getJoinCharacter() != '.'))
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
    public void testInvalidInputClearDotTypeEmpty()
    {
        if (isTypeLegal() && isClearDotLegal())
        {
            assertFalse(parse(".CLEAR.TYPE="));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputClearDotTypeUnterminated()
    {
        if (isTypeLegal() && isClearDotLegal())
        {
            assertFalse(parse(".CLEAR.TYPE=One."));
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
        if (isTypeLegal() && isClearDotLegal())
        {
            assertFalse(parse(".CLEAR.TYPE=One..Two"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputClearDotTypeDoubleSeparator()
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
        if (isTypeLegal() && isClearDotLegal())
        {
            assertFalse(parse(".CLEAR.TYPE=.One"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputClearDotTypeFalseStart()
    {
        if (isTypeLegal())
        {
            assertFalse(parse("TYPE=.One"));
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
                boolean parse = parse(getAllString());
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
                }
            } catch (IllegalArgumentException e)
            {
                //This is okay too
            }
        }
    }

    @Test
    public void testInvalidInputCheckType()
    {
        if (!isMaster() && !isTypeLegal())
        {
            try
            {
                boolean result = getToken().parseToken(primaryContext, primaryProf,
                        "TYPE=TestType").passed();
                if (result)
                {
                    assertConstructionError();
                }
            } catch (IllegalArgumentException e)
            {
                // This is okay too
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
        if (!isMaster())
        {
            // Explicitly do NOT build TestWP2
            construct(primaryContext, "TestWP1");
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputCheckTypeEqualLength()
    {
        // Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
        // consume the |
        if (!isMaster() && isTypeLegal())
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
    public void testValidInputTestDot()
    {
        if (isTypeLegal())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            assertTrue(parse("TYPE.TestType"));
            assertCleanConstruction();
        }
    }

    @Test
    public void testValidInputClear()
    {
        if (isClearLegal())
        {
            assertTrue(parse(getClearString()));
            assertCleanConstruction();
        }
    }

    @Test
    public void testValidInputClearJoin()
    {
        if (isClearLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            assertTrue(parse(getClearString() + getJoinCharacter() + "TestWP1"));
            assertCleanConstruction();
        }
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

    /*
     * TODO This needs to be tested for !allowDups to ensure it consolidates or
     * throws an error
     */
    @Test
    public void testRoundRobinThreeDupe() throws PersistenceLayerException
    {
        if (allowDups())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP3");
            runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP1"
                    + getJoinCharacter() + "TestWP3");
        }
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
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP2"
                    + getJoinCharacter() + "TYPE=OtherTestType"
                    + getJoinCharacter() + "TYPE=TestType");
        }
    }

    @Test
    public void testRoundRobinTestAll() throws PersistenceLayerException
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getAllString());
        }
    }

    @Test
    public void testRoundRobinTestEquals() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin("TYPE=TestType");
        }
    }

    @Test
    public void testRoundRobinTestEqualThree() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            runRoundRobin("TYPE=TestAltType.TestThirdType.TestType");
        }
    }

    @Test
    public void testInvalidInputAllItem()
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getAllString() + getJoinCharacter() + "TestWP1"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputItemAll()
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse("TestWP1" + getJoinCharacter() + getAllString()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputAnyType()
    {
        if (isTypeLegal() && isAllLegal())
        {
            assertFalse(parse(getAllString() + getJoinCharacter() + "TYPE=TestType"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTypeAny()
    {
        if (isTypeLegal() && isAllLegal())
        {
            assertFalse(parse("TYPE=TestType" + getJoinCharacter() + getAllString()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidClear()
    {
        if (isClearLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            boolean result = parse("TestWP1" + getJoinCharacter()
                    + getClearString());
            if (result)
            {
                assertConstructionError();
            }
            assertNoSideEffects();
        }
    }

    //TODO: This is commented out due to a design issue in the tokens that do not persist removal references
//	@Test
//	public void testInputInvalidClearDot()
//	{
//		if (isClearDotLegal())
//		{
//			// DoNotConstruct TestWP1NotConstructed
//			assertTrue(parse(".CLEAR.TestWP1NotConstructed"));
//			//Try to force the error if the token didn't capture the reference
//			System.gc();
//			assertFalse(
//				"Expected one of validate or resolve references to be false.",
//				primaryContext.getReferenceContext().validate(null)
//					&& primaryContext.getReferenceContext().resolveReferences(null));
//		}
//	}

    @Test
    public void testInputInvalidAddsAfterClearDotNoSideEffect()
    {
        if (isClearDotLegal() && isAllLegal())
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
                    + getJoinCharacter() + getAllString()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testValidClearDotAllNoSideEffects()
    {
        if (isClearDotLegal() && isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
                    + "TestWP2"));
            assertTrue(parse(getAllString()));
            assertTrue(parse(".CLEAR." + getAllString()));
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
            assertFalse(parse("TestWP3" + getJoinCharacter() + getAllString()));
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
            assertFalse(parse(getClearString() + getJoinCharacter() + "TestWP3"
                    + getJoinCharacter() + getAllString()));
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

    public boolean isMaster()
    {
        return false;
    }

    protected String getAllString()
    {
        return "ALL";
    }

}
