/**
 * AbilityFromTemplateChoiceManagerTest.java
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
 *
 * Created on 17 March 2005
 *
 * $Author: nuance $
 * $Date: 2006-03-22 00:25:03 +0000 (Wed, 22 Mar 2006) $
 * $Revision: 362 $
 */
package pcgen.core.chooser;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Ability;
import pcgen.core.AbilityInfo;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.TestHelper;


/**
 * @author andrew
 *
 */
public class AbilityFromTemplateChoiceManagerTest extends
		AbstractCharacterTestCase {

	/**
	 * Test method for 'pcgen.core.chooser.AbilityFromTemplateChoiceManager.addToMaps(Categorisable)'
	 */
	public void testAddToMaps() {
		PCTemplate tem = new PCTemplate();
		tem.setName("Test Template 1");
		tem.setKeyName("KEY_Test Template 1");

		AbilityFromTemplateChoiceManager choiceManager = new AbilityFromTemplateChoiceManager(tem, getCharacter());

		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "nameMap");
			is (((HashMap) aField.get(choiceManager)).size(), eq(0), "Name map is empty");

			aField  = (Field) TestHelper.findField(cMClass, "catMap");
			is (((HashMap) aField.get(choiceManager)).size(), eq(0), "Category map is empty");

			aField  = (Field) TestHelper.findField(cMClass, "useNameMap");
			is (aField.get(choiceManager), eq(true), "using name map");

		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}

		AbilityInfo abInfo = new AbilityInfo("foo", "KEY_bar");

		choiceManager.addToMaps(abInfo);

		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "nameMap");
			is (((HashMap) aField.get(choiceManager)).size(), eq(0), "Name map is still empty");

			aField  = (Field) TestHelper.findField(cMClass, "catMap");
			is (((HashMap) aField.get(choiceManager)).size(), eq(0), "Category map is still empty");

			aField  = (Field) TestHelper.findField(cMClass, "useNameMap");
			is (aField.get(choiceManager), eq(true), "using name map (2)");
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}

		Ability ab = new Ability();
		ab.setName("bar");
		ab.setKeyName("KEY_bar");
		ab.setCategory("foo");

		is (Globals.addAbility(ab), eq(true), "First ability added correctly");

		choiceManager.addToMaps(abInfo);

		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "nameMap");
			is (((HashMap) aField.get(choiceManager)).size(), eq(1), "Name map is not empty");

			aField  = (Field) TestHelper.findField(cMClass, "catMap");
			is (((HashMap) aField.get(choiceManager)).size(), eq(1), "Category map is not empty");

			aField  = (Field) TestHelper.findField(cMClass, "useNameMap");
			is (aField.get(choiceManager), eq(true), "using name map (3)");
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}

		abInfo = new AbilityInfo("baz", "KEY_bar");

		ab = new Ability();
		ab.setName("bar");
		ab.setKeyName("KEY_bar");
		ab.setCategory("baz");

		is (Globals.addAbility(ab), eq(true), "Second ability added correctly");

		choiceManager.addToMaps(abInfo);

		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "nameMap");
			HashMap name  = (HashMap) aField.get(choiceManager);
			Set sName     = name.keySet();

			Object st[]   = sName.toArray();

			is (st[0], strEq("KEY_bar"), "One");

			/* these next two only have one entry because the first entry is discarded
			 * when the the second is added (which is why we also have cat maps!) */

			is (sName.size(), eq(1), "Name key set has only one entry");
			is (name.size(), eq(1), "Name map has only one entry");

			aField  = (Field) TestHelper.findField(cMClass, "catMap");
			is (((HashMap) aField.get(choiceManager)).size(), eq(2), "Category map has two entries");

			aField  = (Field) TestHelper.findField(cMClass, "useNameMap");
			is (aField.get(choiceManager), eq(false), "using name map (4)");
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}

	}

	/**
	 * Test method for 'pcgen.core.chooser.AbstractCategorisableChoiceManager.initialise(int, int, int)'
	 */
	public void testInitialise() {
		PCTemplate tem = new PCTemplate();
		tem.setName("Test Template 2");

		ChoiceManagerCategorisable choiceManager = new AbilityFromTemplateChoiceManager(tem, getCharacter());

		choiceManager.initialise(1,2,3);

		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "numberOfChoices");
			is (aField.get(choiceManager), eq(1), "Number of choices is set correctly");

			aField  = (Field) TestHelper.findField(cMClass, "requestedSelections");
			is (aField.get(choiceManager), eq(2), "Requested selections is set correctly");

			aField  = (Field) TestHelper.findField(cMClass, "maxNewSelections");
			is (aField.get(choiceManager), eq(3), "Max new Selections is set correctly");
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}

		choiceManager.initialise(23,17,7);

		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "numberOfChoices");
			is (aField.get(choiceManager), eq(23), "Number of choices is set correctly");

			aField  = (Field) TestHelper.findField(cMClass, "requestedSelections");
			is (aField.get(choiceManager), eq(17), "Requested selections is set correctly");

			aField  = (Field) TestHelper.findField(cMClass, "maxNewSelections");
			is (aField.get(choiceManager), eq(7), "Max new Selections is set correctly");
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}
	}

	/**
	 * Test method for 'pcgen.core.chooser.AbilityFromTemplateChoiceManager.AbilityFromTemplateChoiceManager(PObject, PlayerCharacter)'
	 */
	public void testAbilityFromTemplateChoiceManager() {
		PCTemplate tem = new PCTemplate();
		tem.setName("Test Template 3");

		ChoiceManagerCategorisable choiceManager = new AbilityFromTemplateChoiceManager(tem, getCharacter());

		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "pobject");
			PObject pobject = (PObject) aField.get(choiceManager);
			is(pobject.getKeyName(), strEq("Test Template 3"));

			aField  = (Field) TestHelper.findField(cMClass, "pc");
			PlayerCharacter aPc = (PlayerCharacter) aField.get(choiceManager);
			is(aPc.getName(), strEq(getCharacter().getName()));

		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}

	}

	/**
	 * Test method for 'pcgen.core.chooser.AbstractCategorisableChoiceManager.doChooser(List, List, List, PlayerCharacter)'
	 */
	public void testDoChooserListListListPlayerCharacter() {
		// TODO Do Nothing
	}

	/**
	 * Test method for 'pcgen.core.chooser.AbstractCategorisableChoiceManager.doChooser(CategorisableStore, List)'
	 */
	public void testDoChooserCategorisableStoreList() {
		// TODO Do Nothing
	}

}
