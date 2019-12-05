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
import static org.junit.Assert.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.list.CompanionList;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class FollowersLstTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new FollowersLst();
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
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTypeOnly()
    {
        assertFalse(parse("Follower"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTypeBarOnly()
    {
        assertFalse(parse("Follower|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyType()
    {
        assertFalse(parse("|4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoPipe()
    {
        assertFalse(parse("Follower||4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoPipeTypeTwo()
    {
        assertFalse(parse("Follower|Pet|4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBarEnding()
    {
        assertFalse(parse("Follower|4|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBarStarting()
    {
        assertFalse(parse("|Follower|4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidReversed()
    {
        primaryContext.getReferenceContext().constructCDOMObject(CompanionList.class, "Follower");
        assertTrue(parse("Formula|Follower"));
        assertConstructionError();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(CompanionList.class, "Follower");
        secondaryContext.getReferenceContext().constructCDOMObject(CompanionList.class,
                "Follower");
        runRoundRobin("Follower|4");
    }

    @Test
    public void testRoundRobinFormula() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(CompanionList.class, "Follower");
        secondaryContext.getReferenceContext().constructCDOMObject(CompanionList.class,
                "Follower");
        runRoundRobin("Follower|4+1");
    }

    @Test
    public void testRoundRobinComplexFormula() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(CompanionList.class, "Follower");
        secondaryContext.getReferenceContext().constructCDOMObject(CompanionList.class,
                "Follower");
        runRoundRobin("Follower|if(var(\"SIZE==3||SIZE==4\"),5,10)");
    }

    @Test
    public void testRoundRobinComplex() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(CompanionList.class, "Follower");
        secondaryContext.getReferenceContext().constructCDOMObject(CompanionList.class,
                "Follower");
        primaryContext.getReferenceContext().constructCDOMObject(CompanionList.class, "Pet");
        secondaryContext.getReferenceContext().constructCDOMObject(CompanionList.class, "Pet");
        runRoundRobin("Follower|4+1", "Pet|PetForm");
    }

    @Override
    protected String getLegalValue()
    {
        return "Follower|4+1";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Pet|PetForm";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
