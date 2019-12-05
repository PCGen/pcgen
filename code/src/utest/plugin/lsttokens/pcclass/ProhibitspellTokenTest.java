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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.ProhibitedSpellType;
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

public class ProhibitspellTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{

    static ProhibitspellToken token = new ProhibitspellToken();
    static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

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
    public void testInvalidInputOnlyType()
    {
        assertFalse(parse("ALIGNMENT"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoValue()
    {
        assertFalse(parse("ALIGNMENT."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoType()
    {
        assertFalse(parse(".Good"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputLeadingPipe()
    {
        assertFalse(parse("|ALIGNMENT.Good"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTrailingPipe()
    {
        assertFalse(parse("ALIGNMENT.Good|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleDot()
    {
        assertFalse(parse("ALIGNMENT..Good"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTrailingDot()
    {
        assertFalse(parse("ALIGNMENT.Lawful."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTrailingDotContinued()
    {
        assertFalse(parse("ALIGNMENT.Lawful.|PRECLASS:1,Fighter"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleDotSeparator()
    {
        assertFalse(parse("ALIGNMENT.Lawful..Good"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDotComma()
    {
        assertFalse(parse("SPELL.,Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTrailingComma()
    {
        assertFalse(parse("SPELL.Fireball,"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTrailingCommaContinued()
    {
        assertFalse(parse("SPELL.Fireball,|PRECLASS:1,Fighter"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoubleCommaSeparator()
    {
        assertFalse(parse("SPELL.Fireball,,Lightning Bolt"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoublePipe()
    {
        assertFalse(parse("ALIGNMENT.Good||PRECLASS:1,Fighte"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNeutral()
    {
        assertFalse(parse("ALIGNMENT.Neutral"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNotAType()
    {
        assertFalse(parse("NOTATYPE.Good"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTwoLimits()
    {
        assertFalse(parse("DESCRIPTOR.Fear|DESCRIPTOR.Fire"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOnlyPre()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf,
                "PRECLASS:1,Fighter=1").passed());
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinAlignment() throws PersistenceLayerException
    {
        runRoundRobin("ALIGNMENT.Good");
    }

    @Test
    public void testRoundRobinDescriptorSimple()
            throws PersistenceLayerException
    {
        runRoundRobin("DESCRIPTOR.Fire");
    }

    @Test
    public void testRoundRobinDescriptorAnd() throws PersistenceLayerException
    {
        runRoundRobin("DESCRIPTOR.Fear.Fire");
    }

    @Test
    public void testRoundRobinSchoolSimple() throws PersistenceLayerException
    {
        runRoundRobin("SCHOOL.Evocation");
    }

    @Test
    public void testRoundRobinSubSchoolSimple()
            throws PersistenceLayerException
    {
        runRoundRobin("SUBSCHOOL.Subsch");
    }

    @Test
    public void testRoundRobinSpellSimple() throws PersistenceLayerException
    {
        runRoundRobin("SPELL.Fireball");
    }

    @Test
    public void testRoundRobinSpellComplex() throws PersistenceLayerException
    {
        runRoundRobin("SPELL.Fireball,Lightning Bolt");
    }

    @Test
    public void testInvalidInputEmbeddedPre()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf,
                "SPELL.Fireball,Lightning Bolt|PRECLASS:1,Fighter=1|TestWP2").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoublePipePre()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf,
                "SPELL.Fireball||PRECLASS:1,Fighter=1").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputPostPrePipe()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf,
                "TestWP1|PRECLASS:1,Fighter=1|").passed());
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinPre() throws PersistenceLayerException
    {
        runRoundRobin("SUBSCHOOL.Subsch|PRECLASS:1,Fighter=1");
    }

    @Test
    public void testRoundRobinTwoPre() throws PersistenceLayerException
    {
        runRoundRobin("DESCRIPTOR.Fear.Fire|!PRERACE:1,Human|PRECLASS:1,Fighter=1");
    }

    @Test
    public void testRoundRobinNotPre() throws PersistenceLayerException
    {
        runRoundRobin("DESCRIPTOR.Fear.Fire|!PRECLASS:1,Fighter=1");
    }

    @Test
    public void testRoundRobinWWoPre() throws PersistenceLayerException
    {
        runRoundRobin("SPELL.Fireball,Lightning Bolt|PRECLASS:1,Fighter=1",
                "SUBSCHOOL.Subsch");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "SUBSCHOOL.Subsch";
    }

    @Override
    protected String getLegalValue()
    {
        return "SPELL.Fireball,Lightning Bolt|PRECLASS:1,Fighter=1";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.removeListFor(getListKey());
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    private static ListKey<SpellProhibitor> getListKey()
    {
        return ListKey.SPELL_PROHIBITOR;
    }

    @Test
    public void testUnparseLegalSchool()
    {
        SpellProhibitor o = getConstant(ProhibitedSpellType.SCHOOL, "Public");
        primaryProf.addToListFor(getListKey(), o);
        expectSingle(getToken().unparse(primaryContext, primaryProf), "SCHOOL.Public");
    }

    @Test
    public void testUnparseLegalSubSchool()
    {
        SpellProhibitor o = getConstant(ProhibitedSpellType.SUBSCHOOL, "Elementary");
        primaryProf.addToListFor(getListKey(), o);
        expectSingle(getToken().unparse(primaryContext, primaryProf), "SUBSCHOOL.Elementary");
    }

    @Test
    public void testUnparseLegalDescriptor()
    {
        SpellProhibitor o = getConstant(ProhibitedSpellType.DESCRIPTOR, "Fire");
        primaryProf.addToListFor(getListKey(), o);
        expectSingle(getToken().unparse(primaryContext, primaryProf), "DESCRIPTOR.Fire");
    }

    private static SpellProhibitor getConstant(ProhibitedSpellType type, String args)
    {
        SpellProhibitor spellProb = new SpellProhibitor();
        spellProb.setType(type);
        spellProb.addValue(args);
        return spellProb;
    }

    @Test
    public void testUnparseNullInList()
    {
        primaryProf.addToListFor(getListKey(), null);
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey objectKey = getListKey();
        primaryProf.addToListFor(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            //Yep!
        }
    }
}
