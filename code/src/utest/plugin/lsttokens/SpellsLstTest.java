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

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.PCTemplate;
import pcgen.core.spell.Spell;
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

public class SpellsLstTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new SpellsLst();
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
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
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
    public void testInvalidSpellbookOnly()
    {
        assertFalse(parse("SpellBook"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellbookBarOnly()
    {
        assertFalse(parse("SpellBook|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptySpellbook()
    {
        assertFalse(parse("|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellbookAndSpellBarOnly()
    {
        assertFalse(parse("SpellBook|Fireball|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellCommaStarting()
    {
        assertFalse(parse("SpellBook|,Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellCommaEnding()
    {
        assertFalse(parse("SpellBook|Fireball,"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellDoubleComma()
    {
        assertFalse(parse("SpellBook|Fireball,,DCFormula"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellDoublePipe()
    {
        assertFalse(parse("SpellBook|Fireball||Lightning Bolt"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellEmbeddedPre()
    {
        assertFalse(parse("SpellBook|Fireball|PRERACE:1,Human|Lightning Bolt"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadTimes()
    {
        assertFalse(parse("SpellBook|TIMES=|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyTimes()
    {
        try
        {
            assertFalse(parse("SpellBook|TIMES=3"));
        } catch (IllegalArgumentException iae)
        {
            // OK
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyTimesBar()
    {
        assertFalse(parse("SpellBook|TIMES=3|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyLevel()
    {
        try
        {
            assertFalse(parse("SpellBook|CASTERLEVEL=3"));
        } catch (IllegalArgumentException iae)
        {
            // This is ok too
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyTimeUnit()
    {
        assertFalse(parse("SpellBook|TIMEUNIT=|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoTimeUnit()
    {
        assertFalse(parse("SpellBook|TIMEUNIT=Hour|TIMEUNIT=Day|Fireball"));
        assertNoSideEffects();
    }

    // TODO Once TIMEUNIT is fixed for i18n this is valuable
    // @Test
    // public void testInvalidTimeUnit() throws PersistenceLayerException
    // {
    // assertFalse(parse("SpellBook|TIMEUNIT=Fortnight|Fireball"));
    // assertNoSideEffects();
    //	}

    @Test
    public void testInvalidTimeUnitNoSpell()
    {
        try
        {
            assertFalse(parse("SpellBook|TIMEUNIT=Day"));
        } catch (IllegalArgumentException iae)
        {
            // This is ok too
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyTimes()
    {
        assertFalse(parse("SpellBook|TIMES=|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyCasterLevel()
    {
        assertFalse(parse("SpellBook|CASTERLEVEL=|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoTimes()
    {
        assertFalse(parse("SpellBook|TIMES=3|TIMES=4|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoCasterLevel()
    {
        assertFalse(parse("SpellBook|CASTERLEVEL=3|CASTERLEVEL=4|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyLevelBar()
    {
        assertFalse(parse("SpellBook|CASTERLEVEL=3|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoubleBar()
    {
        assertFalse(parse("SpellBook||Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadCasterLevel()
    {
        assertFalse(parse("SpellBook|CASTERLEVEL=|Fireball"));
        assertNoSideEffects();
    }

    // @Test
    // public void testInvalidOutOfOrder() throws PersistenceLayerException
    // {
    // try
    // {
    // assertFalse(parse("SpellBook|CASTERLEVEL=4|TIMES=2|Fireball"));
    // }
    // catch (IllegalArgumentException iae)
    // {
    // // This is ok too
    // }
    // assertNoSideEffects();
    // }

    @Test
    public void testInvalidOnlyPre()
    {
        try
        {
            assertFalse(parse("SpellBook|TIMES=2|PRERACE:1,Human"));
        } catch (IllegalArgumentException iae)
        {
            // This is ok too
        }
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinJustSpell() throws PersistenceLayerException
    {
        runRoundRobin("SpellBook|TIMES=1|Fireball");
    }

    @Test
    public void testRoundRobinTwoSpell() throws PersistenceLayerException
    {
        runRoundRobin("SpellBook|TIMES=1|Fireball|Lightning Bolt");
    }

    @Test
    public void testRoundRobinTimes() throws PersistenceLayerException
    {
        runRoundRobin("SpellBook|TIMES=3|Fireball");
    }

    @Test
    public void testRoundRobinDC() throws PersistenceLayerException
    {
        runRoundRobin("SpellBook|TIMES=1|Fireball,CL+5");
    }

    @Test
    public void testRoundRobinPre() throws PersistenceLayerException
    {
        runRoundRobin("SpellBook|TIMES=1|Fireball|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinTimeUnit() throws PersistenceLayerException
    {
        runRoundRobin("SpellBook|TIMES=1|TIMEUNIT=Hour|Fireball");
    }

    @Test
    public void testRoundRobinComplexFormulaTimes() throws PersistenceLayerException
    {
        System.err.println("!");
        runRoundRobin("SpellBook|TIMES=if(var(\"SIZE==3||SIZE==4\"),5,10)|TIMEUNIT=Hour|Fireball");
    }

    @Test
    public void testRoundRobinCasterLevel() throws PersistenceLayerException
    {
        runRoundRobin("SpellBook|TIMES=1|CASTERLEVEL=15|Fireball");
    }

    @Test
    public void testRoundRobinComplex() throws PersistenceLayerException
    {
        runRoundRobin(
                "SpellBook|TIMES=2|TIMEUNIT=Week|CASTERLEVEL=15|Fireball,CL+5|Lightning Bolt,25|!PRECLASS:1,Cleric=1|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinTwoBooksJustSpell()
            throws PersistenceLayerException
    {
        runRoundRobin("OtherBook|TIMES=1|Lightning Bolt", "SpellBook|TIMES=1|Fireball");
    }

    @Test
    public void testRoundRobinTwoTimesJustSpell()
            throws PersistenceLayerException
    {
        runRoundRobin("SpellBook|TIMES=2|Fireball",
                "SpellBook|TIMES=3|Lightning Bolt");
    }

    @Test
    public void testRoundRobinTwoLevelJustSpell()
            throws PersistenceLayerException
    {
        runRoundRobin("SpellBook|TIMES=1|CASTERLEVEL=12|Fireball",
                "SpellBook|TIMES=1|CASTERLEVEL=15|Lightning Bolt");
    }

    @Test
    public void testValidClear()
    {
        assertTrue(parse(Constants.LST_DOT_CLEAR_ALL));
    }

    @Test
    public void testInvalidChainedClear()
    {
        assertFalse(parse(Constants.LST_DOT_CLEAR_ALL + "|" + getLegalValue()));
        assertNoSideEffects();
    }

    @Override
    protected String getLegalValue()
    {
        return "SpellBook|TIMES=1|CASTERLEVEL=12|Fireball";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "SpellBook|TIMES=1|CASTERLEVEL=15|Lightning Bolt";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
