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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.ConsolidationRule.AppendingConsolidation;

import org.junit.jupiter.api.Test;

public abstract class AbstractGlobalTypeSafeListTestCase<T> extends
        AbstractGlobalTokenTestCase
{

    public abstract T getConstant(String string);

    public abstract char getJoinCharacter();

    public abstract ListKey<T> getListKey();

    @Test
    public void testValidInputSimple()
    {
        List<?> coll;
        assertTrue(parse("Rheinhessen"));
        coll = primaryProf.getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Rheinhessen")));
    }

    @Test
    public void testValidInputNonEnglish()
    {
        List<?> coll;
        assertTrue(parse("Niederösterreich"));
        coll = primaryProf.getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
    }

    @Test
    public void testValidInputSpace()
    {
        List<?> coll;
        assertTrue(parse("Finger Lakes"));
        coll = primaryProf.getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Finger Lakes")));
    }

    @Test
    public void testValidInputHyphen()
    {
        List<?> coll;
        assertTrue(parse("Languedoc-Roussillon"));
        coll = primaryProf.getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
    }

    @Test
    public void testValidInputY()
    {
        List<?> coll;
        assertTrue(parse("Yarra Valley"));
        coll = primaryProf.getListFor(getListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Yarra Valley")));
    }

    @Test
    public void testValidInputList()
    {
        List<?> coll;
        assertTrue(parse("Niederösterreich" + getJoinCharacter()
                + "Finger Lakes"));
        coll = primaryProf.getListFor(getListKey());
        assertEquals(2, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
        assertTrue(coll.contains(getConstant("Finger Lakes")));
    }

    @Test
    public void testValidInputMultList()
    {
        List<?> coll;
        assertTrue(parse("Niederösterreich" + getJoinCharacter()
                + "Finger Lakes"));
        assertTrue(parse("Languedoc-Roussillon" + getJoinCharacter()
                + "Rheinhessen"));
        coll = primaryProf.getListFor(getListKey());
        assertEquals(4, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
        assertTrue(coll.contains(getConstant("Finger Lakes")));
        assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
        assertTrue(coll.contains(getConstant("Rheinhessen")));
    }

    // FIXME Someday, when PCGen doesn't write out crappy stuff into custom
    // items
    // @Test
    // public void testInvalidListEmpty() throws PersistenceLayerException
    // {
    // primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
    // assertFalse(parse( ""));
    // }

    @Test
    public void testInvalidEmpty()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListEnd()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse("TestWP1" + getJoinCharacter()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListStart()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse(getJoinCharacter() + "TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListDoubleJoin()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP2");
        assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
                + "TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        runRoundRobin("Rheinhessen");
    }

    @Test
    public void testRoundRobinWithSpace() throws PersistenceLayerException
    {
        runRoundRobin("Finger Lakes");
    }

    @Test
    public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
    {
        runRoundRobin("Niederösterreich");
    }

    @Test
    public void testRoundRobinHyphen() throws PersistenceLayerException
    {
        runRoundRobin("Languedoc-Roussillon");
    }

    @Test
    public void testRoundRobinY() throws PersistenceLayerException
    {
        runRoundRobin("Yarra Valley");
    }

    @Test
    public void testRoundRobinThree() throws PersistenceLayerException
    {
        runRoundRobin("Rheinhessen" + getJoinCharacter() + "Yarra Valley"
                + getJoinCharacter() + "Languedoc-Roussillon");
    }

    public static String[] getConstants()
    {
        return new String[]{"Niederösterreich", "Finger Lakes",
                "Languedoc-Roussillon", "Rheinhessen", "Yarra Valley"};
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
            unparsed = getWriteToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        if (isClearDotLegal())
        {
            assertTrue(parse(".CLEAR.TestWP1"));
            unparsed = getWriteToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        assertTrue(parse("TestWP1"));
        assertTrue(parse("TestWP2"));
        unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        assertEquals("TestWP1"
                + getJoinCharacter() + "TestWP2", unparsed[0]);
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getWriteToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
    }

    @Test
    public void testReplacementInputsTwo()
    {
        String[] unparsed;
        assertTrue(parse("TestWP1"));
        assertTrue(parse("TestWP2"));
        unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        assertEquals("TestWP1"
                + getJoinCharacter() + "TestWP2", unparsed[0]);
        if (isClearDotLegal())
        {
            assertTrue(parse(".CLEAR.TestWP1"));
            unparsed = getWriteToken().unparse(primaryContext, primaryProf);
            assertEquals("TestWP2", unparsed[0]);
        }
    }

    @Test
    public void testInputInvalidClear()
    {
        if (isClearLegal())
        {
            assertFalse(parse("TestWP1" + getJoinCharacter() + Constants.LST_DOT_CLEAR));
            assertNoSideEffects();
        }
    }

    //TODO: This is commented out due to a design issue in the tokens that do not persist removal references
//	@Test
//	public void testInputInvalidClearDot()
//	{
//		if (isClearDotLegal() && requiresPreconstruction())
//		{
//			// DoNotConstruct TestWP1
//			assertTrue(parse(".CLEAR.TestWP1"));
//			assertConstructionError();
//		}
//	}

    protected abstract boolean requiresPreconstruction();

    @Test
    public void testInputInvalidAddsAfterClearDotNoSideEffect()
    {
        if (isClearDotLegal())
        {
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
                    + "TestWP2"));
            assertFalse(parse("TestWP3" + getJoinCharacter() + ".CLEAR.TestWP2"
                    + getJoinCharacter() + "ALL"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidAddsBasicNoSideEffect()
    {
        assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertTrue(parseSecondary("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertFalse(parse("TestWP3" + getJoinCharacter() + getJoinCharacter()
                + "TestWP4"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidAddsAfterClearNoSideEffect()
    {
        if (isClearLegal() && isAllLegal())
        {
            assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
            assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
                    + "TestWP2"));
            assertFalse(parse(Constants.LST_DOT_CLEAR + getJoinCharacter() + "TestWP3"
                    + getJoinCharacter() + "ALL"));
            assertNoSideEffects();
        }
    }

    protected abstract boolean isAllLegal();

    @Test
    public void testRoundRobinTestAll() throws PersistenceLayerException
    {
        if (isAllLegal())
        {
            runRoundRobin("ALL");
        }
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

    @Test
    public void testUnparseNull()
    {
        primaryProf.removeListFor(getListKey());
        assertNull(getWriteToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        primaryProf.addToListFor(getListKey(),
                getConstant(getLegalValue()));
        String[] unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue());
    }

    @Test
    public void testUnparseNullInList()
    {
        primaryProf.addToListFor(getListKey(), null);
        try
        {
            getWriteToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testUnparseMultiple()
    {
        primaryProf.addToListFor(getListKey(),
                getConstant(getLegalValue()));
        primaryProf.addToListFor(getListKey(),
                getConstant(getAlternateLegalValue()));
        String[] unparsed = getWriteToken().unparse(primaryContext, primaryProf);
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
