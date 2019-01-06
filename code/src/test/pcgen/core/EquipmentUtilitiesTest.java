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

import java.util.ArrayList;
import java.util.List;

import pcgen.PCGenTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.util.TestHelper;


public class EquipmentUtilitiesTest extends PCGenTestCase
{
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
