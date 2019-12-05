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

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefineLstTest extends AbstractGlobalTokenTestCase
{

    static DefineLst token = new DefineLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        PCStat ps = BuildUtilities.createStat("Strength", "STR");
        primaryContext.getReferenceContext().importObject(ps);
        PCStat ss = BuildUtilities.createStat("Strength", "STR");
        secondaryContext.getReferenceContext().importObject(ss);
    }

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
    public void testInvalidInputEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputUnlockArg()
    {
        assertFalse(parse("UNLOCK.STR|3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoResult()
    {
        assertFalse(parse("Medium"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyFormula()
    {
        assertFalse(parse("Medium|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyVariable()
    {
        assertFalse(parse("|Medium"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoublePipe()
    {
        assertFalse(parse("Light||Medium"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTwoPipe()
    {
        assertFalse(parse("Light|Medium|Heavy"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        runRoundRobin("VariableName|Formula");
    }

    @Test
    public void testRoundRobinFormula() throws PersistenceLayerException
    {
        runRoundRobin("VariableName|1+2");
    }

    @Test
    public void testRoundRobinJEP() throws PersistenceLayerException
    {
        runRoundRobin("VariableName|CL(\"Fighter\")");
    }

    @Test
    public void testRoundRobinJEPPipe() throws PersistenceLayerException
    {
        runRoundRobin("VariableName|if(var(\"SIZE==3||SIZE==4\"),5,0)");
    }

//	DEFINE:LOCK deprecated and now changes to DEFINESTAT:LOCK or DEFINESTAT:NONSTAT
//	public void testRoundRobinLock() throws PersistenceLayerException
//	{
//		runRoundRobin("LOCK.STR|10");
//	}

//	DEFINE:UNLOCK deprecated and now changes to DEFINESTAT:STAT
//	public void testRoundRobinUnlock() throws PersistenceLayerException
//	{
//		runRoundRobin("UNLOCK.STR");
//	}

    @Override
    protected String getLegalValue()
    {
        return "FooVar|0";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "VariableName|0";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

}
