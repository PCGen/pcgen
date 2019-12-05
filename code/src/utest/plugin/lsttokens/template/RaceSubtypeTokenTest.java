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
package plugin.lsttokens.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class RaceSubtypeTokenTest extends
        AbstractTypeSafeListTestCase<PCTemplate, RaceSubType>
{

    static RacesubtypeToken token = new RacesubtypeToken();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCTemplate> getToken()
    {
        return token;
    }

    @Override
    public RaceSubType getConstant(String string)
    {
        return RaceSubType.getConstant(string);
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Override
    public ListKey<RaceSubType> getListKey()
    {
        return ListKey.RACESUBTYPE;
    }

    @Test
    public void testValidRemoveInputSimple()
    {
        List<?> coll;
        assertTrue(parse(".REMOVE.Rheinhessen"));
        coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Rheinhessen")));
    }

    @Test
    public void testValidRemoveInputNonEnglish()
    {
        List<?> coll;
        assertTrue(parse(".REMOVE.Niederösterreich"));
        coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
    }

    @Test
    public void testValidRemoveInputSpace()
    {
        List<?> coll;
        assertTrue(parse(".REMOVE.Finger Lakes"));
        coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Finger Lakes")));
    }

    @Test
    public void testValidRemoveInputHyphen()
    {
        List<?> coll;
        assertTrue(parse(".REMOVE.Languedoc-Roussillon"));
        coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
    }

    @Test
    public void testValidRemoveInputList()
    {
        List<?> coll;
        assertTrue(parse(".REMOVE.Niederösterreich" + getJoinCharacter()
                + ".REMOVE.Finger Lakes"));
        coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
        assertEquals(2, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
        assertTrue(coll.contains(getConstant("Finger Lakes")));
    }

    @Test
    public void testValidInputMultRemoveList()
    {
        List<?> coll;
        assertTrue(parse(".REMOVE.Niederösterreich" + getJoinCharacter()
                + ".REMOVE.Finger Lakes"));
        assertTrue(parse(".REMOVE.Languedoc-Roussillon" + getJoinCharacter()
                + ".REMOVE.Rheinhessen"));
        coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
        assertEquals(4, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
        assertTrue(coll.contains(getConstant("Finger Lakes")));
        assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
        assertTrue(coll.contains(getConstant("Rheinhessen")));
    }

    @Test
    public void testInvalidRemoveEmpty()
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCTemplate.class, "TestWP1");
        assertFalse(parse(".REMOVE."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidRemoveListEnd()
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCTemplate.class, "TestWP1");
        assertFalse(parse("TestWP1" + getJoinCharacter() + ".REMOVE."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidRemoveListStart()
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCTemplate.class, "TestWP1");
        assertFalse(parse(".REMOVE." + getJoinCharacter() + "TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidRemoveListDoubleJoin()
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCTemplate.class, "TestWP1");
        primaryContext.getReferenceContext().constructCDOMObject(PCTemplate.class, "TestWP2");
        assertFalse(parse(".REMOVE.TestWP2" + getJoinCharacter()
                + getJoinCharacter() + ".REMOVE.TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testRemoveRoundRobinBase() throws PersistenceLayerException
    {
        runRoundRobin(".REMOVE.Rheinhessen");
    }

    @Test
    public void testRemoveRoundRobinWithSpace()
            throws PersistenceLayerException
    {
        runRoundRobin(".REMOVE.Finger Lakes");
    }

    @Test
    public void testRemoveRoundRobinNonEnglish()
            throws PersistenceLayerException
    {
        runRoundRobin(".REMOVE.Niederösterreich");
    }

    @Test
    public void testRemoveRoundRobinHyphen() throws PersistenceLayerException
    {
        runRoundRobin(".REMOVE.Languedoc-Roussillon");
    }

    @Test
    public void testRemoveRoundRobinThree() throws PersistenceLayerException
    {
        runRoundRobin(".REMOVE.TestWP1" + getJoinCharacter()
                + ".REMOVE.TestWP2" + getJoinCharacter() + ".REMOVE.TestWP3");
    }

    @Test
    public void testMixRoundRobinThree() throws PersistenceLayerException
    {
        runRoundRobin(".REMOVE.TestWP3" + getJoinCharacter() + "TestWP1"
                + getJoinCharacter() + "TestWP2");
    }

    @Test
    public void testMixRoundRobinWithSpace() throws PersistenceLayerException
    {
        runRoundRobin(".REMOVE.Finger Lakes" + getJoinCharacter()
                + "Languedoc-Roussillon");
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
    }

    @Test
    public void testOverwriteRemove()
    {
        parse(".REMOVE.TestWP2");
        validateUnparsed(primaryContext, primaryProf, ".REMOVE.TestWP2");
        parse("TestWP1");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer(".REMOVE.TestWP2|TestWP1"));
    }

    @Test
    public void testOverwriteWithRemove()
    {
        parse("TestWP1");
        validateUnparsed(primaryContext, primaryProf, "TestWP1");
        parse(".REMOVE.TestWP2");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer(".REMOVE.TestWP2|TestWP1"));
    }

    @Test
    public void testUnparseRemoveSingle()
    {
        getUnparseTarget().addToListFor(ListKey.REMOVED_RACESUBTYPE,
                getConstant(getLegalValue()));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, ".REMOVE." + getLegalValue());
    }

    @Test
    public void testUnparseNullInRemoveList()
    {
        getUnparseTarget().addToListFor(ListKey.REMOVED_RACESUBTYPE, null);
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
    public void testUnparseRemoveMultiple()
    {
        getUnparseTarget().addToListFor(ListKey.REMOVED_RACESUBTYPE,
                getConstant(getLegalValue()));
        getUnparseTarget().addToListFor(ListKey.REMOVED_RACESUBTYPE,
                getConstant(getAlternateLegalValue()));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, ".REMOVE." + getLegalValue()
                + getJoinCharacter() + ".REMOVE." + getAlternateLegalValue());
    }

    @Test
    public void testUnparseMixedMultiple()
    {
        getUnparseTarget().addToListFor(getListKey(),
                getConstant(getLegalValue()));
        getUnparseTarget().addToListFor(ListKey.REMOVED_RACESUBTYPE,
                getConstant(getAlternateLegalValue()));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, ".REMOVE." + getAlternateLegalValue()
                + getJoinCharacter() + getLegalValue());
    }
}
