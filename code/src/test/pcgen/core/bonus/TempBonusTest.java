/*
 * Copyright 2012 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.bonus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.facade.core.InfoFacade;
import pcgen.gui2.facade.TempBonusHelper;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.Test;

class TempBonusTest extends AbstractCharacterTestCase
{

    @Test
    public void testPCTemporaryBonus()
    {
        LoadContext context = Globals.getContext();
        BonusObj bonus = Bonus.newBonus(context, "WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement");
        Spell spell = context.getReferenceContext().constructNowIfNecessary(Spell.class, "PCTempBonusItem");
        spell.addToListFor(ListKey.BONUS_PC, bonus);
        assertFalse(TempBonusHelper.hasAnyPCTempBonus(spell));
        assertTrue(TempBonusHelper.hasPCTempBonus(spell));
        assertFalse(TempBonusHelper.hasNonPCTempBonus(spell));
        assertTrue(TempBonusHelper.hasCharacterTempBonus(spell));
        assertFalse(TempBonusHelper.hasEquipmentTempBonus(spell));
        try
        {
            assertTrue(TempBonusHelper.getEquipmentApplyString(spell).isEmpty());
        } catch (NullPointerException e)
        {
            //This is appropriate too
        }
    }

    @Test
    public void testANYPCTemporaryBonus()
    {
        LoadContext context = Globals.getContext();
        BonusObj bonus = Bonus.newBonus(context, "WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement");
        Spell spell = context.getReferenceContext().constructNowIfNecessary(Spell.class, "PCTempBonusItem");
        spell.addToListFor(ListKey.BONUS_ANYPC, bonus);
        assertTrue(TempBonusHelper.hasAnyPCTempBonus(spell));
        assertFalse(TempBonusHelper.hasPCTempBonus(spell));
        assertTrue(TempBonusHelper.hasNonPCTempBonus(spell));
        assertTrue(TempBonusHelper.hasCharacterTempBonus(spell));
        assertFalse(TempBonusHelper.hasEquipmentTempBonus(spell));
        try
        {
            assertTrue(TempBonusHelper.getEquipmentApplyString(spell).isEmpty());
        } catch (NullPointerException e)
        {
            //This is appropriate too
        }
    }

    @Test
    public void testEquipmentTemporaryBonus()
    {
        PlayerCharacter character = getCharacter();
        LoadContext context = Globals.getContext();
        BonusObj bonus = Bonus.newBonus(context, "WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement");
        EquipBonus tb = new EquipBonus(bonus, "MARTIAL;SIMPLE;EXOTIC");
        Spell spell = context.getReferenceContext().constructNowIfNecessary(Spell.class, "PCTempBonusItem");
        spell.addToListFor(ListKey.BONUS_EQUIP, tb);
        assertFalse(TempBonusHelper.hasAnyPCTempBonus(spell));
        assertFalse(TempBonusHelper.hasPCTempBonus(spell));
        assertTrue(TempBonusHelper.hasNonPCTempBonus(spell));
        assertFalse(TempBonusHelper.hasCharacterTempBonus(spell));
        assertTrue(TempBonusHelper.hasEquipmentTempBonus(spell));
        Set<String> eaStringSet = TempBonusHelper.getEquipmentApplyString(spell);
        assertFalse(eaStringSet.isEmpty());
        assertEquals(1, eaStringSet.size());
        assertEquals("MARTIAL;SIMPLE;EXOTIC", eaStringSet.iterator().next());
        Equipment dagger = context.getReferenceContext().constructNowIfNecessary(Equipment.class, "Dagger");
        dagger.addToListFor(ListKey.TYPE, Type.WEAPON);
        dagger.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        character.addEquipment(dagger);
        List<InfoFacade> eList = TempBonusHelper.getListOfApplicableEquipment(spell, character);
        assertEquals(1, eList.size());
        assertEquals("Dagger", eList.iterator().next().getKeyName());
    }

}
