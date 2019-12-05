/*
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package plugin.exporttokens;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.content.ACControl;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.EquipSet;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code ACTokenTest} tests the function of the AC token and
 * thus the calculations of armor class.
 */
public class ACTokenTest extends AbstractCharacterTestCase
{

    private EquipmentModifier masterwork;
    private EquipmentModifier plus1;
    private Equipment chainShirt;

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        LoadContext context = Globals.getContext();
        PlayerCharacter character = getCharacter();
        setPCStat(character, dex, 14);
        dex.removeListFor(ListKey.BONUS);
        BonusObj aBonus = Bonus.newBonus(context, "COMBAT|AC|10|TYPE=Base");

        if (aBonus != null)
        {
            dex.addToListFor(ListKey.BONUS, aBonus);
        }
        // Ignoring max dex
        aBonus = Bonus.newBonus(context, "COMBAT|AC|DEX|TYPE=Ability");

        if (aBonus != null)
        {
            dex.addToListFor(ListKey.BONUS, aBonus);
        }

        EquipSet def = new EquipSet("0.1", "Default");
        character.addEquipSet(def);
        character.setCalcEquipmentList();

        character.calcActiveBonuses();

        // Create non magic armor
        chainShirt = new Equipment();
        chainShirt.setName("Chain Shirt");
        chainShirt.put(StringKey.KEY_NAME, "KEY_Chain_Shirt");
        TestHelper.addType(chainShirt, "Armor.Light.Suit.Standard");
        chainShirt.put(IntegerKey.AC_CHECK, -2);
        aBonus = Bonus.newBonus(context, "COMBAT|AC|4|TYPE=Armor.REPLACE");

        if (aBonus != null)
        {
            chainShirt.addToListFor(ListKey.BONUS, aBonus);
        }

        // Create magic armor enhancement
        masterwork = new EquipmentModifier();
        masterwork.setName("Masterwork");
        masterwork.put(StringKey.KEY_NAME, "MWORKA");
        TestHelper.addType(masterwork, "Armor.Shield");
        masterwork.addToListFor(ListKey.ITEM_TYPES, Type.MASTERWORK);
        aBonus = Bonus.newBonus(context, "EQMARMOR|ACCHECK|1|TYPE=Enhancement");

        if (aBonus != null)
        {
            masterwork.addToListFor(ListKey.BONUS, aBonus);
        }
        context.getReferenceContext().importObject(masterwork);

        plus1 = new EquipmentModifier();
        plus1.setName("Plus 1 Enhancement");
        plus1.put(StringKey.KEY_NAME, "PLUS1A");
        TestHelper.addType(plus1, "Armor.Shield");
        plus1.put(IntegerKey.PLUS, 1);
        plus1.addToListFor(ListKey.ITEM_TYPES, Type.getConstant("Enhancement"));
        plus1.addToListFor(ListKey.ITEM_TYPES, Type.MAGIC);
        plus1.addToListFor(ListKey.ITEM_TYPES, Type.getConstant("Plus1"));
        aBonus = Bonus.newBonus(context, "COMBAT|AC|1|TYPE=Armor.REPLACE");

        if (aBonus != null)
        {
            plus1.addToListFor(ListKey.BONUS, aBonus);
        }
        Globals.getContext().getReferenceContext().importObject(plus1);

        // Load AC definitions - but only once
        final GameMode gamemode = SettingsHandler.getGame();
        if (!gamemode.isValidACType("Total"))
        {
            gamemode.addACAdds("Total", Collections.singletonList(new ACControl("TOTAL")));
            gamemode.addACAdds("Armor", Collections.singletonList(new ACControl("Armor")));
            gamemode.addACAdds("Ability", Collections.singletonList(new ACControl("Ability")));
        }

    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception
    {
        Globals.getContext().getReferenceContext().forget(masterwork);
        Globals.getContext().getReferenceContext().forget(plus1);
        masterwork = null;
        plus1 = null;

        super.tearDown();
    }

    /**
     * Test the character's AC calcs with no armor.
     */
    @Test
    public void testBase()
    {
        assertEquals("12", new ACToken().getToken(
                "AC.Total", getCharacter(), null), "Total AC no armor");

        assertEquals("0", new ACToken().getToken(
                "AC.Armor", getCharacter(), null), "Armor AC no armor");

        assertEquals("2", new ACToken().getToken(
                "AC.Ability", getCharacter(), null), "Ability AC no armor");
    }

    /**
     * Test the character's AC calcs with armor with no equip mods applied.
     */
    @Test
    public void testNonMagic()
    {
        PlayerCharacter character = getCharacter();
        EquipSet es =
                new EquipSet("0.1.2", "Chain Shirt", chainShirt.getName(),
                        chainShirt);
        character.addEquipSet(es);
        character.setCalcEquipmentList();
        character.calcActiveBonuses();

        assertEquals("2", new ACToken().getToken(
                "AC.Ability", getCharacter(), null), "Ability AC normal armor");

        assertEquals("4", new ACToken().getToken(
                "AC.Armor", getCharacter(), null), "Armor AC with normal armor");

        assertEquals("16", new ACToken()
                .getToken("AC.Total", getCharacter(), null), "Total AC with normal armor");
    }

    /**
     * Test the character's AC calcs with armor with equipmods applied, including magic.
     */
    @Test
    public void testMagic()
    {
        PlayerCharacter character = getCharacter();
        chainShirt.addEqModifiers("MWORKA.PLUS1A", true);
        EquipSet es =
                new EquipSet("0.1.2", "Chain Shirt", chainShirt.getName(),
                        chainShirt);
        character.addEquipSet(es);
        character.setCalcEquipmentList();
        character.calcActiveBonuses();

        assertEquals("2", new ACToken().getToken(
                "AC.Ability", getCharacter(), null), "Ability AC magic armor");

        assertEquals("5", new ACToken().getToken(
                "AC.Armor", getCharacter(), null), "Armor AC with magic armor");

        assertEquals("17", new ACToken().getToken(
                "AC.Total", getCharacter(), null), "Total AC with magic armor");
    }

}
