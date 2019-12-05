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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.ConsolidationRule.AppendingConsolidation;

import org.junit.jupiter.api.Test;

public abstract class AbstractTypeSafeListTestCase<T extends CDOMObject, LT>
        extends AbstractCDOMTokenTestCase<T>
{

    protected abstract boolean requiresPreconstruction();

    public abstract LT getConstant(String string);

    public abstract char getJoinCharacter();

    public abstract ListKey<LT> getListKey();

    public boolean clearsByDefault()
    {
        return false;
    }

    @Test
    public void testValidInputSimple()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        List<?> coll;
        assertTrue(parse("Rheinhessen"));
        coll = getUnparseTarget().getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Rheinhessen")));
        assertCleanConstruction();
    }

    @Test
    public void testValidInputNonEnglish()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Niederösterreich");
        List<?> coll;
        assertTrue(parse("Niederösterreich"));
        coll = getUnparseTarget().getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
        assertCleanConstruction();
    }

    @Test
    public void testValidInputSpace()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
        List<?> coll;
        assertTrue(parse("Finger Lakes"));
        coll = getUnparseTarget().getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Finger Lakes")));
        assertCleanConstruction();
    }

    @Test
    public void testValidInputHyphen()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        List<?> coll;
        assertTrue(parse("Languedoc-Roussillon"));
        coll = getUnparseTarget().getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
        assertCleanConstruction();
    }

    @Test
    public void testValidInputY()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Yarra Valley");
        List<?> coll;
        assertTrue(parse("Yarra Valley"));
        coll = getUnparseTarget().getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Yarra Valley")));
        assertCleanConstruction();
    }

    @Test
    public void testValidInputList()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Niederösterreich");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
        List<?> coll;
        assertTrue(parse("Niederösterreich" + getJoinCharacter()
                + "Finger Lakes"));
        coll = getUnparseTarget().getListFor(getListKey());
        assertEquals(2, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
        assertTrue(coll.contains(getConstant("Finger Lakes")));
        assertCleanConstruction();
    }

    @Test
    public void testValidInputMultList()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Niederösterreich");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        List<?> coll;
        assertTrue(parse("Niederösterreich" + getJoinCharacter()
                + "Finger Lakes"));
        assertTrue(parse("Languedoc-Roussillon" + getJoinCharacter()
                + "Rheinhessen"));
        coll = getUnparseTarget().getListFor(getListKey());
        assertEquals(clearsByDefault() ? 2 : 4, coll.size());
        if (!clearsByDefault())
        {
            assertTrue(coll.contains(getConstant("Niederösterreich")));
            assertTrue(coll.contains(getConstant("Finger Lakes")));
        }
        assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
        assertTrue(coll.contains(getConstant("Rheinhessen")));
        assertCleanConstruction();
    }

    @Test
    public void testInvalidListEmpty()
    {
        assertFalse(parse(""));
        assertNull(primaryProf.getListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmpty()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse(""));
        assertNull(primaryProf.getListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListEnd()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse("TestWP1" + getJoinCharacter()));
        assertNull(primaryProf.getListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListStart()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse(getJoinCharacter() + "TestWP1"));
        assertNull(primaryProf.getListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListDoubleJoin()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP2");
        assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
                + "TestWP1"));
        assertNull(primaryProf.getListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        runRoundRobin("Rheinhessen");
    }

    @Test
    public void testRoundRobinWithSpace() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(getCDOMClass(), "Finger Lakes");
        runRoundRobin("Finger Lakes");
    }

    @Test
    public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Niederösterreich");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Niederösterreich");
        runRoundRobin("Niederösterreich");
    }

    @Test
    public void testRoundRobinHyphen() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        runRoundRobin("Languedoc-Roussillon");
    }

    @Test
    public void testRoundRobinY() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Yarra Valley");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(getCDOMClass(), "Yarra Valley");
        runRoundRobin("Yarra Valley");
    }

    @Test
    public void testRoundRobinThree() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Yarra Valley");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(getCDOMClass(), "Yarra Valley");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        runRoundRobin("Rheinhessen" + getJoinCharacter() + "Yarra Valley"
                + getJoinCharacter() + "Languedoc-Roussillon");
    }

    @Test
    public void testRoundRobinThreeDupe() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        runRoundRobin("Rheinhessen" + getJoinCharacter() + "Rheinhessen"
                + getJoinCharacter() + "Languedoc-Roussillon");
    }

    public abstract boolean isClearLegal();

    public abstract boolean isClearDotLegal();

    @Test
    public void testReplacementInputs()
    {
        String[] unparsed;
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        if (isClearDotLegal())
        {
            assertTrue(parse(".CLEAR.TestWP1"));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        assertTrue(parse("TestWP1"));
        assertTrue(parse("TestWP2"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals("TestWP1"
                + getJoinCharacter() + "TestWP2", unparsed[0]);
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
    }

    @Test
    public void testReplacementInputsTwo()
    {
        String[] unparsed;
        assertTrue(parse("TestWP1"));
        assertTrue(parse("TestWP2"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals("TestWP1"
                + getJoinCharacter() + "TestWP2", unparsed[0]);
        if (isClearDotLegal())
        {
            assertTrue(parse(".CLEAR.TestWP1"));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertEquals("TestWP2", unparsed[0]);
        }
    }

    @Test
    public void testInputInvalidClear()
    {
        if (isClearLegal())
        {
            assertFalse(parse("TestWP1" + getJoinCharacter() + Constants.LST_DOT_CLEAR));
            assertNull(getUnparseTarget().getListFor(getListKey()));
            assertNoSideEffects();
        }
    }

    //TODO: This is commented out due to a design issue in the tokens that do not persist removal references
//	@Test
//	public void testInputInvalidClearDot()
//	{
//		if (isClearDotLegal())
//		{
//			assertTrue(parse(".CLEAR.TestWP1"));
//			if (requiresPreconstruction())
//			{
//				assertConstructionError();
//			}
//		}
//	}

    // TODO This is only invalid if ALL is legal
    // @Test
    // public void testInputInvalidAddsAfterClearDotNoSideEffect()
    // throws PersistenceLayerException
    // {
    // if (isClearDotLegal() && isAllLegal())
    // {
    // assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
    // assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
    // + "TestWP2"));
    // assertFalse(parse("TestWP3" + getJoinCharacter() + ".CLEAR.TestWP2"
    // + getJoinCharacter() + "ALL"));
    // assertNoSideEffects();
    // }
    // }

    @Test
    public void testInputInvalidAddsBasicNoSideEffect()
    {
        assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertTrue(parseSecondary("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertFalse(parse("TestWP3" + getJoinCharacter() + getJoinCharacter()
                + "TestWP4"));
        assertNoSideEffects();
    }

    // TODO This is only invalid if ALL is legal
    // @Test
    // public void testInputInvalidAddsAfterClearNoSideEffect()
    // throws PersistenceLayerException
    // {
    // if (isClearLegal())
    // {
    // assertTrue(parse(
    // "TestWP1" + getJoinCharacter() + "TestWP2"));
    // assertTrue(getToken().parse(secondaryContext, secondaryProf,
    // "TestWP1" + getJoinCharacter() + "TestWP2"));
    // assertEquals("Test setup failed", primaryGraph, secondaryGraph);
    // assertFalse(getToken().parse(
    // primaryContext,
    // primaryProf,
    // Constants.LST_DOT_CLEAR + getJoinCharacter() + "TestWP3" + getJoinCharacter()
    // + "ALL"));
    // assertEquals("Bad Clear had Side Effects", primaryGraph,
    // secondaryGraph);
    // }
    // }

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

    protected CDOMObject getUnparseTarget()
    {
        return primaryProf;
    }

    @Test
    public void testUnparseNull()
    {
        getUnparseTarget().removeListFor(getListKey());
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        getUnparseTarget().addToListFor(getListKey(),
                getConstant(getLegalValue()));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue());
    }

    @Test
    public void testUnparseNullInList()
    {
        getUnparseTarget().addToListFor(getListKey(), null);
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testUnparseMultiple()
    {
        getUnparseTarget().addToListFor(getListKey(),
                getConstant(getLegalValue()));
        getUnparseTarget().addToListFor(getListKey(),
                getConstant(getAlternateLegalValue()));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue() + getJoinCharacter()
                + getAlternateLegalValue());
    }

    /*
     * TODO Need to define the appropriate behavior here - is this the token's responsibility?
     */
    // @Test
    // public void testUnparseGenericsFail() throws PersistenceLayerException
    // {
    // ListKey objectKey = getListKey();
    // primaryProf.addToListFor(objectKey, new Object());
    // try
    // {
    // String[] unparsed = getToken().unparse(primaryContext, primaryProf);
    // fail();
    // }
    // catch (ClassCastException e)
    // {
    // //Yep!
    //		}
    //	}

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return new AppendingConsolidation(getJoinCharacter());
    }
}
