/*
 * ArmorProfChoiceManagerTest.java
 * Copyright 2005 (C) Andrew Wilson <nuance@sourceforge.net>
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
 * Created on Oct 7, 2005
 *
 * $Author$ 
 * $Date$
 * $Revision$
 *
 */
package pcgen.core.chooser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.TestHelper;

/**
 * {@code ArmorProfChoiceManagerTest} test that the SimpleArmorProfChoiceManager class is functioning correctly.
 *
 * @author Andrew Wilson <nuance@sourceforge.net>
 */

public class ArmorProfChoiceManagerTest extends AbstractCharacterTestCase
{

	/**
	 * Constructs a new {@code ArmorProfChoiceManagerTest}.
	 */
	public ArmorProfChoiceManagerTest()
	{
		// Do Nothing
	}

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
		EquipmentList.clearEquipmentMap();
	}

	/**
	 * 
	 */
	public void test001()
	{
		PObject pObj = new PObject();
		pObj.setName("My PObject");
		pObj.setChoiceString("ARMORPROF|1|TYPE=Light");
		is(pObj.getChoiceString(), strEq("ARMORPROF|1|TYPE=Light"));

		PlayerCharacter aPC = getCharacter();

		ChoiceManagerList choiceManager =
				ChooserUtilities.getChoiceManager(pObj, null, aPC);
		is(choiceManager, not(eq(null)));

		try
		{
			Class<? extends ChoiceManagerList> cMClass =
					choiceManager.getClass();

			Field aField =
					(Field) TestHelper.findField(cMClass, "numberOfChoices");
			is(aField.get(choiceManager), eq(1));

			aField = (Field) TestHelper.findField(cMClass, "choices");
			List choices = (List) aField.get(choiceManager);
			is(choices.get(0), strEq("TYPE=Light"));
		}
		catch (IllegalAccessException e)
		{
			System.out.println(e);
		}
	}

	/**
	 * test002
	 */
	public void test002()
	{
		TestHelper.makeEquipment("Armor one\tKEY:Arm001\tTYPE:Armor.Light");
		TestHelper.makeEquipment("Armor two\tKEY:Arm002\tTYPE:Armor.Light");
		TestHelper.makeEquipment("Armor three\tKEY:Arm003\tTYPE:Armor.Medium");
		Equipment eq = EquipmentList.getEquipmentKeyed("Arm001");
		is(eq.isArmor(), eq(true));
		is(eq.isType("Light"), eq(true));
		eq = EquipmentList.getEquipmentKeyed("Arm002");
		is(eq.isArmor(), eq(true));
		is(eq.isType("Light"), eq(true));
		eq = EquipmentList.getEquipmentKeyed("Arm003");
		is(eq.isArmor(), eq(true));
		is(eq.isType("Light"), eq(false));

		PObject pObj = new PObject();
		pObj.setName("My PObject");
		pObj.setChoiceString("ARMORPROF|1|TYPE=Light");

		PlayerCharacter aPC = getCharacter();
		List Lone = new ArrayList();
		List Ltwo = new ArrayList();

		ChoiceManagerList choiceManager =
				ChooserUtilities.getChoiceManager(pObj, null, aPC);
		choiceManager.getChoices(aPC, Lone, Ltwo);

		is(Lone.size(), eq(2), "Available list has 2 elements");
		is(Ltwo.size(), eq(0), "Selected list has no elements");

		is(Lone.contains("Armor one"), eq(true), "First Available list test");
		is(Lone.contains("Armor two"), eq(true), "Second Available list test");
		is(Lone.contains("Armor three"), eq(false), "Third Available list test");
	}
}
