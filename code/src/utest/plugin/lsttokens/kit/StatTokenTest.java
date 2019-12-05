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
package plugin.lsttokens.kit;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.core.PCStat;
import pcgen.core.kit.KitStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatTokenTest extends AbstractKitTokenTestCase<KitStat>
{

    static StatToken token = new StatToken();
    static CDOMSubLineLoader<KitStat> loader = new CDOMSubLineLoader<>(
            "SPELLS", KitStat.class);

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        PCStat ps = BuildUtilities.createStat("Strength", "STR");
        primaryContext.getReferenceContext().importObject(ps);
        PCStat pi = BuildUtilities.createStat("Intelligence", "INT");
        primaryContext.getReferenceContext().importObject(pi);
        PCStat ss = BuildUtilities.createStat("Strength", "STR");
        secondaryContext.getReferenceContext().importObject(ss);
        PCStat si = BuildUtilities.createStat("Intelligence", "INT");
        secondaryContext.getReferenceContext().importObject(si);
    }

    @Override
    public Class<KitStat> getCDOMClass()
    {
        return KitStat.class;
    }

    @Override
    public CDOMSubLineLoader<KitStat> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitStat> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmptyValue()
    {
        assertFalse(parse("STR="));
    }

    @Test
    public void testInvalidInputEmptyStat()
    {
        assertFalse(parse("=2"));
    }


    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        runRoundRobin("STR=2");
    }


    @Test
    public void testRoundRobinTwo() throws PersistenceLayerException
    {
        runRoundRobin("INT=Wizard|STR=2");
    }

}
