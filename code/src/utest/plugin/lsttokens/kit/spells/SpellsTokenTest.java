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
package plugin.lsttokens.kit.spells;

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PCClass;
import pcgen.core.kit.KitSpells;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.Test;

public class SpellsTokenTest extends AbstractKitTokenTestCase<KitSpells>
{

    static SpellsToken token = new SpellsToken();
    static CDOMSubLineLoader<KitSpells> loader = new CDOMSubLineLoader<>(
            "SPELLS", KitSpells.class);

    @Override
    public Class<KitSpells> getCDOMClass()
    {
        return KitSpells.class;
    }

    @Override
    public CDOMSubLineLoader<KitSpells> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitSpells> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmptySpellbook()
    {
        assertFalse(parse("SPELLBOOK=|CLASS=Wizard|Fireball=2"));
    }

    @Test
    public void testInvalidInputEmptyClass()
    {
        assertFalse(parse("SPELLBOOK=Personal|CLASS=|Fireball=2"));
    }

    @Test
    public void testInvalidInputTwoClass()
    {
        assertFalse(parse("SPELLBOOK=Personal|CLASS=Wizard|CLASS=Cleric|Fireball=2"));
    }

    @Test
    public void testInvalidInputTwoSpellbook()
    {
        assertFalse(parse("SPELLBOOK=Personal|SPELLBOOK=Other|CLASS=Wizard|Fireball=2"));
    }

    @Test
    public void testInvalidInputEmptySpell()
    {
        assertFalse(parse("SPELLBOOK=Personal|CLASS=Wizard|=2"));
    }

    @Test
    public void testInvalidInputEmptyCount()
    {
        assertFalse(parse("SPELLBOOK=Personal|CLASS=Wizard|Fireball="));
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        runRoundRobin("Fireball=2");
    }

    @Test
    public void testInvalidInputEmptyType()
    {
        assertFalse(parse("SPELLBOOK=Personal|CLASS=Wizard|TYPE."));
    }

    @Test
    public void testInvalidInputTrailingType()
    {
        assertFalse(parse("SPELLBOOK=Personal|CLASS=Wizard|TYPE.One."));
    }

    @Test
    public void testInvalidInputDoubleType()
    {
        assertFalse(parse("SPELLBOOK=Personal|CLASS=Wizard|TYPE.One..Two"));
    }

    @Test
    public void testRoundRobinType() throws PersistenceLayerException
    {
        Spell a = primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
        Spell b = secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
        runRoundRobin("TYPE.Foo=2");
    }

    @Test
    public void testRoundRobinFeat() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        constructCategorized(primaryContext, BuildUtilities.getFeatCat(), "EnhancedFeat");
        constructCategorized(secondaryContext, BuildUtilities.getFeatCat(), "EnhancedFeat");
        runRoundRobin("SPELLBOOK=Personal|CLASS=Wizard|Fireball[EnhancedFeat]=2");
    }

    @Test
    public void testRoundRobinCount() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        runRoundRobin("SPELLBOOK=Personal|CLASS=Wizard|Fireball=2");
    }

    @Test
    public void testRoundRobinSpellBookClass() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        runRoundRobin("SPELLBOOK=Personal|CLASS=Wizard|Fireball");
    }
}
