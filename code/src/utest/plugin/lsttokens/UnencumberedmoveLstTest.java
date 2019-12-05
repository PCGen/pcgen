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
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class UnencumberedmoveLstTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new UnencumberedmoveLst();
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
    public void testInvalidInputPipeOnly()
    {
        assertFalse(parse("|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputRandomString()
    {
        assertFalse(parse("String"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEndPipe()
    {
        assertFalse(parse("HeavyLoad|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputStartPipe()
    {
        assertFalse(parse("|HeavyLoad"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoublePipe()
    {
        assertFalse(parse("HeavyLoad||HeavyArmor"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleLoad()
    {
        assertFalse(parse("HeavyLoad|MediumLoad"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleLoad2()
    {
        assertFalse(parse("MediumLoad|HeavyLoad"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleLoad3()
    {
        assertFalse(parse("HeavyLoad|Overload"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleLoad4()
    {
        assertFalse(parse("HeavyLoad|LightLoad"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleArmor()
    {
        assertFalse(parse("MediumArmor|HeavyArmor"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleArmor2()
    {
        assertFalse(parse("HeavyArmor|MediumArmor"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleArmor3()
    {
        assertFalse(parse("MediumArmor|LightArmor"));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputDoubleArmor()
    {
        assertTrue(parse("LightArmor"));
    }

    @Test
    public void testRoundRobinLightLoad() throws PersistenceLayerException
    {
        runRoundRobin("LightLoad");
    }

    @Test
    public void testRoundRobinLightLight() throws PersistenceLayerException
    {
        runRoundRobin("LightLoad|LightArmor");
    }

    @Test
    public void testRoundRobinMediumLight() throws PersistenceLayerException
    {
        runRoundRobin("MediumLoad|LightArmor");
    }

    @Test
    public void testRoundRobinLightMedium() throws PersistenceLayerException
    {
        runRoundRobin("LightLoad|MediumArmor");
    }

    @Test
    public void testRoundRobinArmor() throws PersistenceLayerException
    {
        runRoundRobin("HeavyArmor");
    }

    @Test
    public void testRoundRobinMediumLoad() throws PersistenceLayerException
    {
        runRoundRobin("MediumLoad");
    }

    @Test
    public void testRoundRobinOverload() throws PersistenceLayerException
    {
        runRoundRobin("Overload");
    }

    @Test
    public void testRoundRobinHeavyLoad() throws PersistenceLayerException
    {
        runRoundRobin("HeavyLoad");
    }

    @Test
    public void testRoundRobinLoadArmor() throws PersistenceLayerException
    {
        runRoundRobin("HeavyLoad|MediumArmor");
    }

    @Override
    protected String getLegalValue()
    {
        return "HeavyLoad|MediumArmor";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "MediumLoad";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

}
