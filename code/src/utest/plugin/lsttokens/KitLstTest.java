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
package plugin.lsttokens;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Kit;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class KitLstTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new KitLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

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
    public void testInvalidInputNoNumber()
    {
        assertFalse(parse("TestType"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputMissingNumber()
    {
        assertFalse(parse("|TestType"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOnlyNumber()
    {
        assertFalse(parse("2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputMissingItem()
    {
        assertFalse(parse("2|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoublePipe()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("2||TestWP1|TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputJoinedComma()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        if (parse("1|TestWP1,TestWP2"))
        {
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputJoinedDot()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        if (parse("1|TestWP1.TestWP2"))
        {
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidListEnd()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("1|TestWP1|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListStart()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("1||TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNaNCount()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("Count|TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidZeroCount()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("0|TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeCount()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("-4|TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListDoubleJoin()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("1|TestWP2||TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputCheckMult()
    {
        // Explicitly do NOT build TestWP2
        construct(primaryContext, "TestWP1");
        assertTrue(parse("1|TestWP1|TestWP2"));
        assertConstructionError();
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("1|TestWP1");
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
        runRoundRobin("1|TestWP1|TestWP2|TestWP3");
    }

    @Test
    public void testRoundRobinThreeDupe() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP3");
        runRoundRobin("2|TestWP1|TestWP1|TestWP3");
    }

    @Test
    public void testRoundRobinTwoCountThree() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        runRoundRobin("2|TestWP1|TestWP2|TestWP3");
    }

    protected void construct(LoadContext loadContext, String one)
    {
        loadContext.getReferenceContext().constructCDOMObject(Kit.class, one);
    }

    @Test
    public void testInputInvalidAddsAllNoSideEffect()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP3");
        assertTrue(parse("1|TestWP1" + getJoinCharacter() + "TestWP2"));
        assertTrue(parseSecondary("1|TestWP1" + getJoinCharacter() + "TestWP2"));
        assertFalse(parse("1|TestWP3" + getJoinCharacter() + "ALL"));
        assertNoSideEffects();
    }

    private char getJoinCharacter()
    {
        return '|';
    }

    @Test
    public void testInvalidInputAllItem()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("1|ALL" + getJoinCharacter() + "TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputItemAll()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("1|TestWP1" + getJoinCharacter() + "ALL"));
        assertNoSideEffects();
    }

    @Override
    protected String getLegalValue()
    {
        return "1|TestWP1|TestWP2";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "2|TestWP1|TestWP3";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
