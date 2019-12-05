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
package plugin.lsttokens.template;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.DrLst;
import plugin.lsttokens.SabLst;
import plugin.lsttokens.SrLst;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreLevelWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepeatLevelTokenTest extends AbstractCDOMTokenTestCase<PCTemplate>
{
    static RepeatlevelToken token = new RepeatlevelToken();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    @BeforeEach
    public final void setUp() throws PersistenceLayerException,
            URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(new PreLevelParser());
        TokenRegistration.register(new PreLevelWriter());
        TokenRegistration.register(new CrToken());
        TokenRegistration.register(new DrLst());
        TokenRegistration.register(new SrLst());
        TokenRegistration.register(new SabLst());
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCTemplate> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidNoSubcommand()
    {
        assertFalse(parse("1|2|20:5:"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNumberOnly()
    {
        assertFalse(parse("1|2|20"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOneColon()
    {
        assertFalse(parse("1|2|20:5"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoColon()
    {
        assertFalse(parse("1|2|20:5:SAB"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyStartLevel()
    {
        assertFalse(parse("1|2|20::SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadStartLevel()
    {
        assertFalse(parse("1|2|20:StartLevel:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadIncrementLevel()
    {
        assertFalse(parse("IncrLevel|2|20:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadSkipLevel()
    {
        assertFalse(parse("1|SkipLevel|20:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadMaxLevel()
    {
        assertFalse(parse("1|2|MaxLevel:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeStartLevel()
    {
        assertFalse(parse("1|2|20:-4:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeIncrementLevel()
    {
        assertFalse(parse("-1|2|20:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeSkipLevel()
    {
        assertFalse(parse("1|-2|20:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyMaxLevel()
    {
        assertFalse(parse("1|2|:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyCommand()
    {
        assertFalse(parse("1|2|30:5::Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeMaxLevel()
    {
        assertFalse(parse("1|2|-5:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTooManyColons()
    {
        assertFalse(parse("1|2|20:4:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTooManyBars()
    {
        assertFalse(parse("1|2|20|40:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoMaxLevel()
    {
        assertFalse(parse("1|2:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoFirstToken()
    {
        assertFalse(parse(":5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoSkipLevel()
    {
        assertFalse(parse("1||20:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoIncrementLevel()
    {
        assertFalse(parse("|3|20:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoSubcommandArgs()
    {
        assertFalse(parse("1|2|20:5:SAB:"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidStartGreaterThanEnd()
    {
        assertFalse(parse("1|2|20:50:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoIncrement()
    {
        assertFalse(parse("10|2|20:15:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoSkipUse()
    {
        assertFalse(parse("5|4|20:5:SAB:Stuff"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadTemplateToken()
    {
        assertFalse(parse("5|0|10:5:CR:x"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinZeroConsecutive()
            throws PersistenceLayerException
    {
        runRoundRobin("5|0|10:5:SAB:Sample Spec Abil");
    }

    @Test
    public void testRoundRobinNoIncrementBorderCase()
            throws PersistenceLayerException
    {
        runRoundRobin("5|1|10:5:SAB:Sample Spec Abil");
    }

    @Test
    public void testRoundRobinNoSkipBorderCase()
            throws PersistenceLayerException
    {
        runRoundRobin("5|3|20:5:SAB:Sample Spec Abil");
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        runRoundRobin("1|2|20:5:SAB:Sample Spec Abil");
    }

    @Test
    public void testRoundRobinComplex() throws PersistenceLayerException
    {
        runRoundRobin("1|2|20:5:SAB:Sample Spec Abil",
                "2|4|20:5:SAB:Sample Spec Abil");
    }

    @Test
    public void testRoundRobinMultipleSame() throws PersistenceLayerException
    {
        runRoundRobin("1|2|20:5:CR:-1",
                "1|2|20:5:SAB:Special Ability, Man!");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "2|4|20:5:SAB:Sample Spec Abil";
    }

    @Override
    protected String getLegalValue()
    {
        return "1|2|20:5:CR:-1";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
