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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.base.Constants;
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

public class LevelTokenTest extends AbstractCDOMTokenTestCase<PCTemplate>
{

    static LevelToken token = new LevelToken();
    static CDOMTokenLoader<PCTemplate> loader =
            new CDOMTokenLoader<>();

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
    public void testInvalidInputHDonly()
    {
        assertFalse(parse("3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputPipe()
    {
        assertFalse(parse("3|SR|3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOneColon()
    {
        assertFalse(parse("3:SR|2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyHD()
    {
        assertFalse(parse(":DR:3/+1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptySubtype()
    {
        assertFalse(parse("4::3/+1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyDR()
    {
        assertFalse(parse("3:DR:"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyDRNoColon()
    {
        assertFalse(parse("3:DR"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoSlashDR()
    {
        assertFalse(parse("3:DR:1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTwoSlashDR()
    {
        assertFalse(parse("3+:DR:1/3/+4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptySR()
    {
        assertFalse(parse("3:SR:"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptySA()
    {
        assertFalse(parse("3:SAB:"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyCR()
    {
        assertFalse(parse("3:CR:"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptySRNoColon()
    {
        assertFalse(parse("3:SR"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptySANoColon()
    {
        assertFalse(parse("3:SAB"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyCRNoColon()
    {
        assertFalse(parse("3:CR"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoAbbrs()
    {
        assertFalse(parse("3:C:3"));
        assertFalse(parse("3:D:1/+2"));
        assertFalse(parse("3:CRA:3"));
        assertFalse(parse("3:DRA:1/+2"));
        assertFalse(parse("3:SABA:Special"));
        assertFalse(parse("3:SRA:1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputBadClear()
    {
        assertFalse(parse(".CLEARSTUFF"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoSpecificClear()
    {
        assertFalse(parse(".CLEAR.3:CR:3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputBadHDRangePlus()
    {
        assertFalse(parse("+3:SAB:Special Abil"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputBadHDRangeMult()
    {
        assertFalse(parse("*3:SAB:Special Abil"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputBadHDRangeTwoDash()
    {
        assertFalse(parse("1-3:SAB:Special Abil"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputBadHDRangeEndDash()
    {
        assertFalse(parse("4-:SAB:Special Abil"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputBadHDRangeEndPlus()
    {
        assertFalse(parse("4+:SAB:Special Abil"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputBadHDRangeUpTo()
    {
        assertFalse(parse("-4:SAB:Special Abil"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadTemplateToken()
    {
        assertFalse(parse("5:CR:x"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinDR() throws PersistenceLayerException
    {
        runRoundRobin("2:DR:1/+3");
    }

    @Test
    public void testRoundRobinSRNumber() throws PersistenceLayerException
    {
        runRoundRobin("3:SR:25");
    }

    @Test
    public void testRoundRobinSRFormula() throws PersistenceLayerException
    {
        runRoundRobin("3:SR:Formula");
    }

    @Test
    public void testRoundRobinSA() throws PersistenceLayerException
    {
        runRoundRobin("5:SAB:Special Ability, Man!");
    }

    @Test
    public void testRoundRobinCRNumber() throws PersistenceLayerException
    {
        runRoundRobin("4:CR:3");
    }

    @Test
    public void testRoundRobinCRNegative() throws PersistenceLayerException
    {
        runRoundRobin("4:CR:-1");
    }

    @Test
    public void testRoundRobinMultiple() throws PersistenceLayerException
    {
        runRoundRobin("3:SR:Formula", "4:CR:-4");
    }

    @Test
    public void testRoundRobinMultipleDifferent()
            throws PersistenceLayerException
    {
        runRoundRobin("4:CR:-5", "6:SAB:Special Ability, Man!");
    }

    @Test
    public void testRoundRobinMultipleSame() throws PersistenceLayerException
    {
        runRoundRobin("4:CR:-6", "4:SAB:Special Ability, Man!");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "6:SAB:Special Ability, Man!";
    }

    @Override
    protected String getLegalValue()
    {
        return "4:CR:-5";
    }

    @Test
    public void testParseClear()
    {
        assertTrue(parse(Constants.LST_DOT_CLEAR));
        assertCleanConstruction();
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
