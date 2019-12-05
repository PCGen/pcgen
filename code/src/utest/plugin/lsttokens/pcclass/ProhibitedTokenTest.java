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
package plugin.lsttokens.pcclass;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProhibitedTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{

    private static final ProhibitedToken token = new ProhibitedToken();
    private static final CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

    private final PreClassParser preclass = new PreClassParser();
    private final PreClassWriter preclasswriter = new PreClassWriter();
    private final PreRaceParser prerace = new PreRaceParser();
    private final PreRaceWriter preracewriter = new PreRaceWriter();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(preclass);
        TokenRegistration.register(preclasswriter);
        TokenRegistration.register(prerace);
        TokenRegistration.register(preracewriter);
    }

    @Override
    public Class<PCClass> getCDOMClass()
    {
        return PCClass.class;
    }

    @Override
    public CDOMLoader<PCClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClass> getToken()
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
    public void testInvalidInputLeadingComma()
    {
        assertFalse(parse(",Good"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTrailingComma()
    {
        assertFalse(parse("Fireball,"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleCommaSeparator()
    {
        assertFalse(parse("Fireball,,Lightning Bolt"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinDescriptorSimple()
            throws PersistenceLayerException
    {
        runRoundRobin("Fire");
    }

    @Test
    public void testRoundRobinDescriptorAnd() throws PersistenceLayerException
    {
        runRoundRobin("Fear,Fire");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Fire";
    }

    @Override
    protected String getLegalValue()
    {
        return "Fear";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return new ConsolidationRule.AppendingConsolidation(',');
    }
}
