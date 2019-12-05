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
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
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

public class SpellLevelLstTest extends AbstractGlobalTokenTestCase
{
    static CDOMPrimaryToken<CDOMObject> token = new SpelllevelLst();
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
        TokenRegistration.register(prerace);
        TokenRegistration.register(preclasswriter);
        TokenRegistration.register(preracewriter);
    }

    @Test
    public void testRoundRobinSingleSpell() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Bless");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Bless");
        primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSpellList.class, "Wizard");
        runRoundRobin("CLASS|Wizard=3|Bless");
    }

    @Test
    public void testInvalidDoublePipe()
    {
        assertFalse(parse("CLASS||Cleric=1|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoSpell()
    {
        assertFalse(parse("CLASS|Cleric=1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoLevel()
    {
        assertFalse(parse("CLASS|Cleric=|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidClassOnly()
    {
        assertFalse(parse("DOMAIN|Cleric|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidPrefix()
    {
        assertFalse(parse("SKILL|Cleric=2|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoPrefix()
    {
        assertFalse(parse("|Cleric=2|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoClass()
    {
        assertFalse(parse("CLASS|=2|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyPre1()
    {
        assertFalse(parse("PRECLASS:1,Fighter"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyPre2()
    {
        assertFalse(parse("CLASS|PRECLASS:1,Fighter"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyPre3()
    {
        assertFalse(parse("CLASS|Cleric=2|PRECLASS:1,Fighter"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadCasterComma1()
    {
        assertFalse(parse("CLASS|,Cleric=2|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadCasterComma2()
    {
        assertFalse(parse("CLASS|Cleric,=2|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadCasterComma3()
    {
        assertFalse(parse("CLASS|Cleric,,Druid=2|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadComma1()
    {
        assertFalse(parse("CLASS|Cleric=2|,Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadComma2()
    {
        assertFalse(parse("CLASS|Cleric=2|Fireball,"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadComma3()
    {
        assertFalse(parse("CLASS|Cleric=2|Fireball,,Lightning Bolt"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinClass() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Cleric");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSpellList.class, "Cleric");
        runRoundRobin("CLASS|Cleric=2|Fireball|PRECLASS:1,Fighter=2");
    }

    @Test
    public void testRoundRobinSpellCaster() throws PersistenceLayerException
    {
        ClassSpellList a = primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
        a.addType(Type.getConstant("Arcane"));
        ClassSpellList b = secondaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
        b.addType(Type.getConstant("Arcane"));
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        runRoundRobin("CLASS|SPELLCASTER.Arcane=2|Fireball|PRECLASS:1,Fighter=2");
    }

    @Test
    public void testRoundRobinDomain() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
        secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
        runRoundRobin("DOMAIN|Fire=2|Fireball,Lightning Bolt|PRECLASS:1,Fighter=2");
    }

    @Override
    protected String getLegalValue()
    {
        return "CLASS|SPELLCASTER.Arcane=2|Fireball|PRECLASS:1,Fighter=2";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "DOMAIN|Fire=2|Fireball,Lightning Bolt|PRECLASS:1,Fighter=2";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
