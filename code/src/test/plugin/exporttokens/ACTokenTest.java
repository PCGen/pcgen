/*
 * ACTokenTest.java
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
 *
 * Created on Nov 12, 2006
 *
 * $Id: BioTokenTest.java 661 2006-04-06 14:35:49Z karianna $
 *
 */
package plugin.exporttokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.EquipmentModifier;
import pcgen.core.GameMode;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.character.EquipSet;

/**
 * <code>ACTokenTest</code> tests the function of the AC token and 
 * thus the calculations of armor class.  
 *
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006-04-07 00:35:49 +1000 (Fri, 07 Apr 2006) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 661 $
 */
public class ACTokenTest extends AbstractCharacterTestCase
{

	private EquipmentModifier masterwork;
	private EquipmentModifier plus1;
	private Equipment chainShirt;

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(ACTokenTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();
		setPCStat(character, "DEX", 14);
		PCStat stat =
				character.getStatList().getStatAt(
					SettingsHandler.getGame().getStatFromAbbrev("DEX"));
		stat.getBonusList().clear();
		stat.addBonusList("COMBAT|AC|10|TYPE=Base");
		// Ignoring max dex
		stat.addBonusList("COMBAT|AC|DEX|TYPE=Ability");

		EquipSet def = new EquipSet("0.1", "Default");
		character.addEquipSet(def);
		character.setCalcEquipmentList();

		character.calcActiveBonuses();

		// Create non magic armor
		chainShirt = new Equipment();
		chainShirt.setName("Chain Shirt");
		chainShirt.setKeyName("KEY_Chain_Shirt");
		chainShirt.setTypeInfo("Armor.Light.Suit.Standard");
		chainShirt.put(IntegerKey.AC_CHECK, -2);
		chainShirt.addBonusList("COMBAT|AC|4|TYPE=Armor.REPLACE");

		// Create magic armor enhancement
		masterwork = new EquipmentModifier();
		masterwork.setName("Masterwork");
		masterwork.setKeyName("MWORKA");
		masterwork.setTypeInfo("Armor.Shield");
		masterwork.addToListFor(ListKey.ITEM_TYPES, "Masterwork");
		masterwork.addBonusList("EQMARMOR|ACCHECK|1|TYPE=Enhancement");
		EquipmentList.addEquipmentModifier(masterwork);

		plus1 = new EquipmentModifier();
		plus1.setName("Plus 1 Enhancement");
		plus1.setKeyName("PLUS1A");
		plus1.setTypeInfo("Armor.Shield");
		plus1.put(IntegerKey.PLUS, 1);
		plus1.addToListFor(ListKey.ITEM_TYPES, "Enhancement");
		plus1.addToListFor(ListKey.ITEM_TYPES, "Magic");
		plus1.addToListFor(ListKey.ITEM_TYPES, "Plus1");
		plus1.addBonusList("COMBAT|AC|1|TYPE=Armor.REPLACE");
		EquipmentList.addEquipmentModifier(plus1);

		// Load AC definitions - but only once
		final GameMode gamemode = SettingsHandler.getGame();
		if (!gamemode.isValidACType("Total"))
		{
			gamemode.addACType("Total	ADD:TOTAL");
			gamemode.addACType("Armor	ADD:Armor");
			gamemode.addACType("Ability	ADD:Ability");
		}

	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		EquipmentList.removeEquipmentModifier(masterwork);
		EquipmentList.removeEquipmentModifier(plus1);
		masterwork = null;
		plus1 = null;

		super.tearDown();
	}

	/**
	 * Test the character's AC calcs with no armor.
	 * @throws Exception
	 */
	public void testBase() throws Exception
	{
		assertEquals("Total AC no armor", "12", new ACToken().getToken(
			"AC.Total", getCharacter(), null));

		assertEquals("Armor AC no armor", "0", new ACToken().getToken(
			"AC.Armor", getCharacter(), null));

		assertEquals("Ability AC no armor", "2", new ACToken().getToken(
			"AC.Ability", getCharacter(), null));
	}

	/**
	 * Test the character's AC calcs with armor with no equip mods applied.
	 * @throws Exception
	 */
	public void testNonMagic() throws Exception
	{
		PlayerCharacter character = getCharacter();
		EquipSet es =
				new EquipSet("0.1.2", "Chain Shirt", chainShirt.getName(),
					chainShirt);
		character.addEquipSet(es);
		character.setCalcEquipmentList();
		character.calcActiveBonuses();

		assertEquals("Ability AC normal armor", "2", new ACToken().getToken(
			"AC.Ability", getCharacter(), null));

		assertEquals("Armor AC with normal armor", "4", new ACToken().getToken(
			"AC.Armor", getCharacter(), null));

		assertEquals("Total AC with normal armor", "16", new ACToken()
			.getToken("AC.Total", getCharacter(), null));
	}

	/**
	 * Test the character's AC calcs with armor with equipmods applied, including magic.
	 * @throws Exception
	 */
	public void testMagic() throws Exception
	{
		PlayerCharacter character = getCharacter();
		chainShirt.addEqModifiers("MWORKA.PLUS1A", true);
		EquipSet es =
				new EquipSet("0.1.2", "Chain Shirt", chainShirt.getName(),
					chainShirt);
		character.addEquipSet(es);
		character.setCalcEquipmentList();
		character.calcActiveBonuses();

		assertEquals("Ability AC magic armor", "2", new ACToken().getToken(
			"AC.Ability", getCharacter(), null));

		assertEquals("Armor AC with magic armor", "5", new ACToken().getToken(
			"AC.Armor", getCharacter(), null));

		assertEquals("Total AC with magic armor", "17", new ACToken().getToken(
			"AC.Total", getCharacter(), null));
	}

}