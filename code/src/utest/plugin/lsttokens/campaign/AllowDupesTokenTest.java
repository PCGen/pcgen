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

public class AllowDupesTokenTest extends AbstractCDOMTokenTestCase<Campaign>
{

    static CDOMPrimaryToken<Campaign> token = new AllowDupesToken();
    static CDOMTokenLoader<Campaign> loader = new CDOMTokenLoader<>();

    @Override
    public CDOMLoader<Campaign> getLoader()
    {
        return loader;
    }

    @Override
    public Class<Campaign> getCDOMClass()
    {
        return Campaign.class;
    }

    @Override
    public CDOMPrimaryToken<Campaign> getToken()
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
    public void testInvalidType()
    {
        assertFalse(parse("SKILL"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinSpell() throws PersistenceLayerException
    {
        runRoundRobin("SPELL");
    }

    @Test
    public void testRoundRobinLanguage() throws PersistenceLayerException
    {
        runRoundRobin("LANGUAGE");
    }

    @Test
    public void testRoundRobinBoth() throws PersistenceLayerException
    {
        runRoundRobin("LANGUAGE", "SPELL");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "SPELL";
    }

    @Override
    protected String getLegalValue()
    {
        return "LANGUAGE";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
