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
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DrLstTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new DrLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    PreClassParser preclass = new PreClassParser();
    PreClassWriter preclasswriter = new PreClassWriter();
    PreRaceParser prerace = new PreRaceParser();
    PreRaceWriter preracewriter = new PreRaceWriter();

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(preclass);
        TokenRegistration.register(preclasswriter);
        TokenRegistration.register(prerace);
        TokenRegistration.register(preracewriter);
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
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidReductionOnly()
    {
        assertFalse(parse("10"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidReductionSlashOnly()
    {
        assertFalse(parse("10/"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidReductionTwoSlash()
    {
        assertFalse(parse("10//"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidReductionTrailingSlash()
    {
        assertFalse(parse("10/+1/"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidPre()
    {
        assertFalse(parse("10/+1|PREFOO:1,Weird"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidReductionTwoProductiveSlash()
    {
        assertFalse(parse("10/+1/5"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyReduction()
    {
        assertFalse(parse("/+1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBaseOnly()
    {
        assertFalse(parse("10/+1|"));
        assertNoSideEffects();
    }


    @Test
    public void testInvalidOnlyPre()
    {
        try
        {
            assertFalse(parse("PRERACE:1,Human"));
        } catch (IllegalArgumentException iae)
        {
            // This is ok too
        }
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinJustSpell() throws PersistenceLayerException
    {
        runRoundRobin("10/+1");
    }

    @Test
    public void testRoundRobinTwoSpell() throws PersistenceLayerException
    {
        runRoundRobin("10/+1 and Silver");
    }

    @Test
    public void testRoundRobinTimes() throws PersistenceLayerException
    {
        runRoundRobin("5/+2 or Hard");
    }

    @Test
    public void testRoundRobinPre() throws PersistenceLayerException
    {
        runRoundRobin("5/+3|PRERACE:1,Human");
    }

    @Override
    protected String getLegalValue()
    {
        return "5/+2 or Hard";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "5/+3|PRERACE:1,Human";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
