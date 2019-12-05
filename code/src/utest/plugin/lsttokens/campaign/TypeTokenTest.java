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
package plugin.lsttokens.campaign;

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class TypeTokenTest extends AbstractCDOMTokenTestCase<Campaign>
{

    static TypeToken token = new TypeToken();
    static CDOMTokenLoader<Campaign> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Campaign> getCDOMClass()
    {
        return Campaign.class;
    }

    @Override
    public CDOMLoader<Campaign> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Campaign> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidListEmpty()
    {
        assertFalse(parse("."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListTrailing()
    {
        assertFalse(parse("Type."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListLeading()
    {
        assertFalse(parse(".Type"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListDouble()
    {
        assertFalse(parse("One..Type"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListTooMany()
    {
        assertFalse(parse("One.Two.Three.Oops"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        runRoundRobin("Rheinhessen");
    }

    @Test
    public void testRoundRobinWithSpaceInternational() throws PersistenceLayerException
    {
        runRoundRobin("Finger Lakes.Niederösterreich");
    }

    @Test
    public void testRoundRobinHyphen() throws PersistenceLayerException
    {
        runRoundRobin("Languedoc-Roussillon.Two.Yarra Valley");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Finger Lakes.Niederösterreich";
    }

    @Override
    protected String getLegalValue()
    {
        return "Languedoc-Roussillon.Two.Yarra Valley";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
