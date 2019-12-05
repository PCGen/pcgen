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
package plugin.lsttokens.spell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class ItemTokenTest extends AbstractTypeSafeListTestCase<Spell, Type>
{

    static ItemToken token = new ItemToken();
    static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Spell> getCDOMClass()
    {
        return Spell.class;
    }

    @Override
    public CDOMLoader<Spell> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Spell> getToken()
    {
        return token;
    }

    @Override
    public Type getConstant(String string)
    {
        return Type.getConstant(string);
    }

    @Override
    public char getJoinCharacter()
    {
        return ',';
    }

    @Override
    public ListKey<Type> getListKey()
    {
        return ListKey.ITEM;
    }

    public static ListKey<?> getNegativeListKey()
    {
        return ListKey.PROHIBITED_ITEM;
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

    @Test
    public void testValidInputNegativeSimple()
    {
        List<?> coll;
        assertTrue(parse("[Rheinhessen]"));
        coll = primaryProf.getListFor(getNegativeListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Rheinhessen")));
    }

    @Test
    public void testValidInputNegativeNonEnglish()
    {
        List<?> coll;
        assertTrue(parse("[Niederösterreich]"));
        coll = primaryProf.getListFor(getNegativeListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
    }

    @Test
    public void testValidInputNegativeSpace()
    {
        List<?> coll;
        assertTrue(parse("[Finger Lakes]"));
        coll = primaryProf.getListFor(getNegativeListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Finger Lakes")));
    }

    @Test
    public void testValidInputNegativeHyphen()
    {
        List<?> coll;
        assertTrue(parse("[Languedoc-Roussillon]"));
        coll = primaryProf.getListFor(getNegativeListKey());
        assertEquals(1, coll.size());
        assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
    }

    @Test
    public void testValidInputNegativeList()
    {
        List<?> coll;
        assertTrue(parse("[Niederösterreich]" + getJoinCharacter()
                + "[Finger Lakes]"));
        coll = primaryProf.getListFor(getNegativeListKey());
        assertEquals(2, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
        assertTrue(coll.contains(getConstant("Finger Lakes")));
    }

    @Test
    public void testValidInputMultNegativeList()
    {
        List<?> coll;
        assertTrue(parse("[Niederösterreich]" + getJoinCharacter()
                + "[Finger Lakes]"));
        assertTrue(parse("[Languedoc-Roussillon]" + getJoinCharacter()
                + "[Rheinhessen]"));
        coll = primaryProf.getListFor(getNegativeListKey());
        assertEquals(4, coll.size());
        assertTrue(coll.contains(getConstant("Niederösterreich")));
        assertTrue(coll.contains(getConstant("Finger Lakes")));
        assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
        assertTrue(coll.contains(getConstant("Rheinhessen")));
    }

    @Test
    public void testInvalidNegativeEmpty()
    {
        assertFalse(parse("[]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativePrefix()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP2");
        assertFalse(parse("TestWP2[TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeSuffix()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP2");
        assertFalse(parse("[TestWP1]TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeStart()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse("TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeEnd()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse("[TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeListEnd()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse("[TestWP1]" + getJoinCharacter()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeListStart()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        assertFalse(parse(getJoinCharacter() + "[TestWP1]"));
        assertNoSideEffects();
    }

    // FUTURE This is a subtle set of errors, catch in the future
    // @Test
    // public void testInvalidAddRemove() throws PersistenceLayerException
    // {
    // primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
    // assertFalse(parse("TestWP1" + getJoinCharacter() + "[TestWP1]"));
    // assertNoSideEffects();
    // }
    //
    // @Test
    // public void testInvalidRemoveAdd() throws PersistenceLayerException
    // {
    // primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
    // assertFalse(parse("[TestWP1]" + getJoinCharacter() + "TestWP1"));
    // assertNoSideEffects();
    // }

    @Test
    public void testInvalidNegativeListDoubleJoin()
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP2");
        assertFalse(parse("[TestWP2]" + getJoinCharacter() + getJoinCharacter()
                + "[TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinNegativeBase() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        runRoundRobin("[Rheinhessen]");
    }

    @Test
    public void testRoundRobinNegativeWithSpace()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(getCDOMClass(), "Finger Lakes");
        runRoundRobin("[Finger Lakes]");
    }

    @Test
    public void testRoundRobinNegativeNonEnglishAndN()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Niederösterreich");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Niederösterreich");
        runRoundRobin("[Niederösterreich]");
    }

    @Test
    public void testRoundRobinNegativeThree() throws PersistenceLayerException
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
        runRoundRobin("[Rheinhessen]" + getJoinCharacter() + "[Yarra Valley]"
                + getJoinCharacter() + "[Languedoc-Roussillon]");
    }

    @Test
    public void testRoundRobinMixed() throws PersistenceLayerException
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
                + getJoinCharacter() + "[Languedoc-Roussillon]");
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
    }

    @Test
    public void testUnparseRemoveNull()
    {
        getUnparseTarget().removeListFor(ListKey.PROHIBITED_ITEM);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingleRemove()
    {
        getUnparseTarget().addToListFor(ListKey.PROHIBITED_ITEM,
                getConstant(getLegalValue()));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "[" + getLegalValue() + "]");
    }

    @Test
    public void testUnparseNullInRemoveList()
    {
        getUnparseTarget().addToListFor(ListKey.PROHIBITED_ITEM, null);
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
    public void testUnparseMultipleRemove()
    {
        getUnparseTarget().addToListFor(ListKey.PROHIBITED_ITEM,
                getConstant(getLegalValue()));
        getUnparseTarget().addToListFor(ListKey.PROHIBITED_ITEM,
                getConstant(getAlternateLegalValue()));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "[" + getLegalValue() + "]" + getJoinCharacter()
                + "[" + getAlternateLegalValue() + "]");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFailRemove()
    {
        ListKey objectKey = ListKey.PROHIBITED_ITEM;
        primaryProf.addToListFor(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }
}
