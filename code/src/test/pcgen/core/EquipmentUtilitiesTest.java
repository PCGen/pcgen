/*
 * Copyright 2006 (C) Andrew Wilson <nuance@sourceforge.net>
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
package pcgen.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pcgen.ControlTestSupport;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.core.system.LoadInfo;
import pcgen.persistence.GameModeFileLoader;
import pcgen.util.TestHelper;

import junit.framework.TestCase;


public class EquipmentUtilitiesTest extends TestCase
{

	/**
	 * Sets up some basic stuff that must be present for tests to work.
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final GameMode gamemode = new GameMode("3.5");
		gamemode.setBonusFeatLevels("3|3");
		ControlTestSupport.enableFeature(gamemode.getModeContext(), CControl.ALIGNMENTFEATURE);
		gamemode.addLevelInfo("Normal", new LevelInfo());
		gamemode.addXPTableName("Normal");
		gamemode.setDefaultXPTableName("Normal");
		gamemode.clearLoadContext();
		LoadInfo loadable =
				gamemode.getModeContext().getReferenceContext().constructNowIfNecessary(
						LoadInfo.class, gamemode.getName());
		loadable.addLoadScoreValue(0, BigDecimal.ONE);
		GameModeFileLoader.addDefaultTabInfo(gamemode);
		SystemCollections.addToGameModeList(gamemode);
		SettingsHandler.setGame("3.5");
	}

	/**
	 * Test method for 'pcgen.core.EquipmentUtilities.appendToName(String, String)'
	 */
	public void testAppendToName()
	{
		final String bare = "Bare Thing";
		final String decoration = "Mad cow";

		assertEquals("Choice appends to name correctly",
			"Bare Thing (Mad cow)",
			EquipmentUtilities.appendToName(bare, decoration));
	}

	public void testFindEquipmentByBaseKey()
	{
		TestHelper.makeSizeAdjustments();
		Equipment towel = new Equipment();
		towel.setName("Towel");
		Equipment backpackMed = new Equipment();
		backpackMed.setName("Backpack");
		final Equipment backpackSml = backpackMed.clone();
		backpackSml.put(ObjectKey.BASE_ITEM, CDOMDirectSingleRef.getRef(backpackMed));
		SizeAdjustment small = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			SizeAdjustment.class, "S");
		final String newName = backpackSml.createNameForAutoResize(small);
		backpackSml.setName(newName);
		backpackSml.setKeyName(backpackSml.createKeyForAutoResize(small));

		List<Equipment> eqList = new ArrayList<>();
		eqList.add(towel);
		eqList.add(backpackSml);
		assertEquals("Expected to find backpack", backpackSml,
			EquipmentUtilities.findEquipmentByBaseKey(eqList, "backpack"));
		assertEquals("Expected not to find torch", null,
			EquipmentUtilities.findEquipmentByBaseKey(eqList, "torch"));
		assertEquals("Expected to find towel", towel,
			EquipmentUtilities.findEquipmentByBaseKey(eqList, "ToWeL"));
	}
}
