/*
 * Copyright 2005 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.io.exporttoken;

import static org.junit.Assert.assertEquals;

import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import plugin.exporttokens.SpellMemToken;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Verify the correct functioning of the SPELLMEM token.
 */

public class SpellMemTokenTest extends AbstractCharacterTestCase
{
    private PCClass arcaneClass = null;
    private PCClass divineClass = null;
    private Race human = null;
    private Spell testSpell = null;

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        LoadContext context = Globals.getContext();

        // Human
        human = new Race();
        final BonusObj bon = Bonus.newBonus(context, "FEAT|POOL|2");
        human.addToListFor(ListKey.BONUS, bon);

        testSpell = new Spell();
        testSpell.setName("Test Spell");
        testSpell.put(StringKey.KEY_NAME, "TEST_SPELL");
        context.unconditionallyProcess(testSpell, "CLASSES", "KEY_TEST_ARCANE=1");
        context.unconditionallyProcess(testSpell, "DOMAINS", "Fire=0");
        context.unconditionallyProcess(testSpell, "CLASSES", "KEY_TEST_DIVINE=1");

        arcaneClass = new PCClass();
        arcaneClass.setName("TestArcane");
        arcaneClass.put(StringKey.KEY_NAME, "KEY_TEST_ARCANE");
        BuildUtilities.setFact(arcaneClass, "SpellType", "Arcane");
        context.unconditionallyProcess(arcaneClass, "SPELLSTAT", "CHA");
        arcaneClass.put(ObjectKey.SPELLBOOK, false);
        arcaneClass.put(ObjectKey.MEMORIZE_SPELLS, false);
        context.unconditionallyProcess(arcaneClass.getOriginalClassLevel(1), "KNOWN", "4,2,1");
        context.unconditionallyProcess(arcaneClass.getOriginalClassLevel(1), "CAST", "3,1,0");
        context.getReferenceContext().importObject(arcaneClass);

        divineClass = new PCClass();
        divineClass.setName("TestDivine");
        divineClass.put(StringKey.KEY_NAME, "KEY_TEST_DIVINE");
        BuildUtilities.setFact(divineClass, "SpellType", "Divine");
        context.unconditionallyProcess(divineClass, "SPELLSTAT", "WIS");
        divineClass.put(ObjectKey.SPELLBOOK, false);
        divineClass.put(ObjectKey.MEMORIZE_SPELLS, true);
        context.unconditionallyProcess(divineClass.getOriginalClassLevel(1), "CAST", "3,1,0");

        context.getReferenceContext().constructCDOMObject(Domain.class, "Fire");

        context.getReferenceContext().importObject(divineClass);
        finishLoad();
    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception
    {
        Globals.getContext().getReferenceContext().forget(divineClass);
        Globals.getContext().getReferenceContext().forget(arcaneClass);

        super.tearDown();
    }

    /**
     * Test the SPELLMEM tag for a spontaneous caster. Checks that the
     * list of known spells is auto populated and that the spell can be
     * retrieved correctly.
     */
    @Test
    public void testSpontaneousCasterKnown()
    {
        PlayerCharacter character = getCharacter();
        String spellBook = "Travel";
        character.setRace(human);
        character.incrementClassLevel(1, arcaneClass, true);
        PCClass ac = character.getClassKeyed(arcaneClass.getKeyName());
        CharacterSpell aCharacterSpell =
                new CharacterSpell(ac, testSpell);
        aCharacterSpell.addInfo(1, 1, null);
        character.addCharacterSpell(ac, aCharacterSpell);
        character.addSpellBook(spellBook);
        List<CharacterSpell> spellList =
                character.getCharacterSpells(ac, testSpell, "", 1);
        CharacterSpell charSpell = spellList.get(0);

        String result =
                character.addSpell(charSpell, null, arcaneClass.getKeyName(),
                        Globals.getDefaultSpellBook(), 1, 1);
        assertEquals("No CHA, so should reject attempt to add spell",
                "You can only learn 0 spells for level 1 "
                        + "\nand there are no higher-level slots available.", result);

        SpellMemToken token = new SpellMemToken();
        assertEquals("Retrieve spell from known list of arcane caster.",
                "Test Spell", token.getToken("SPELLMEM.0.0.1.0.NAME", character,
                        null));
    }

    /**
     * Test the SPELLMEM tag for a spontaneous caster. Checks that the
     * list of known spells is auto populated and that a spell can be added to
     * a prepared list, and that the spell can be retrieved correctly from both
     * books.
     */
    @Test
    public void testPreparedCaster()
    {
        PlayerCharacter character = getCharacter();
        String spellBook = "Travel";
        character.setRace(human);
        character.incrementClassLevel(1, divineClass, true);
        PCClass dc = character.getClassKeyed(divineClass.getKeyName());
        CharacterSpell aCharacterSpell =
                new CharacterSpell(dc, testSpell);
        aCharacterSpell.addInfo(1, 1, null);
        character.addCharacterSpell(dc, aCharacterSpell);
        character.addSpellBook(spellBook);
        List<CharacterSpell> spellList =
                character.getCharacterSpells(dc, testSpell, "", 1);
        CharacterSpell charSpell = spellList.get(0);

        String result =
                character.addSpell(charSpell, null, divineClass.getKeyName(),
                        Globals.getDefaultSpellBook(), 1, 1);
        assertEquals("Known spells already has all spells, should reject.",
                "The Known Spells spellbook contains all spells of this level that you "
                        + "know. You cannot place spells in multiple times.", result);
        result =
                character.addSpell(charSpell, null, divineClass.getKeyName(),
                        spellBook, 1, 1);
        assertEquals("No WIS, so should reject attempt to add spell",
                "You can only prepare 0 spells for level 1 "
                        + "\nand there are no higher-level slots available.", result);

        setPCStat(character, wis, 12);
        character.calcActiveBonuses();
        result =
                character.addSpell(charSpell, null, divineClass.getKeyName(),
                        spellBook, 1, 1);
        assertEquals("Should be no error messages from adding spell", "",
                result);

        SpellMemToken token = new SpellMemToken();
        assertEquals("Retrieve spell from known list of divine caster.",
                "Test Spell", token.getToken("SPELLMEM.0.0.1.0.NAME", character,
                        null));
        assertEquals("Retrieve spell from prepared list of divine caster.",
                "Test Spell", token.getToken("SPELLMEM.0.2.1.0.NAME", character,
                        null));
    }

    @Override
    protected void defaultSetupEnd()
    {
        //Nothing, we will trigger ourselves
    }
}
