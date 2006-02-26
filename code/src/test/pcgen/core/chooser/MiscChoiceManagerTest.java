/*
 * MiscChoiceManagerTest.java
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
 * $Author: nuance $ 
 * $Date: 2005/10/23 15:22:23 $
 * $Revision: 1.2 $
 *
 */
package pcgen.core.chooser;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.EquipmentList;
import pcgen.util.TestHelper;

import java.lang.Class;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * {@code MiscChoiceManagerTest} test that the MiscChoiceManager class is functioning correctly.
 *
 * @author Andrew Wilson <nuance@sourceforge.net>
 */

public class MiscChoiceManagerTest extends AbstractCharacterTestCase {

	/**
	 * Constructs a new {@code MiscChoiceManagerTest}.
	 */
	public MiscChoiceManagerTest()
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
	 * Test the constructor 
	 */
	public void test001()
	{
		PObject pObj = new PObject();
		pObj.setName("My PObject");
		pObj.setChoiceString("NUMCHOICES=1|Foo|Bar|Baz");
		is(pObj.getChoiceString(), strEq("NUMCHOICES=1|Foo|Bar|Baz"));

		PlayerCharacter aPC  = getCharacter();
		
		AbstractChoiceManager choiceManager = ChooserUtilities.getChoiceManager(pObj, null, aPC);
		is(choiceManager, not(eq(null)), "Found the chooser");

		is(choiceManager.typeHandled(), strEq("MISC"), "got expected chooser");
		
		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "numberOfChoices");
			is (aField.get(choiceManager), eq(1));
			
			aField  = (Field) TestHelper.findField(cMClass, "choices");
			List choices = (List) aField.get(choiceManager);
			is (choices.get(0), strEq("Foo"));
			is (choices.get(1), strEq("Bar"));
			is (choices.get(2), strEq("Baz"));
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}
	}

	/**
	 * test002
	 */
	public void test002()
	{
		PObject pObj = new PObject();
		pObj.setName("My PObject");
		pObj.setChoiceString("NUMCHOICES=1|Foo|Bar|Baz");
		is(pObj.getChoiceString(), strEq("NUMCHOICES=1|Foo|Bar|Baz"));

		PlayerCharacter aPC  = getCharacter();
		
		AbstractChoiceManager choiceManager = ChooserUtilities.getChoiceManager(pObj, null, aPC);
		is(choiceManager, not(eq(null)), "Found the chooser");

		is(choiceManager.typeHandled(), strEq("MISC"), "got expected chooser");
		
		pObj.addAssociated("Bar");

		List available = new ArrayList();
		List selected  = new ArrayList();
		
		choiceManager.getChoices(available, selected, aPC);
	
		Collections.sort(available);
		Collections.sort(selected);

		is(new Integer(available.size()), eq(3), "size of available list");
		is(new Integer(selected.size()),  eq(1), "size of selected list");

		is(available.get(0), strEq("Bar"), "first entry of available");
		is(available.get(1), strEq("Baz"), "second entry of available");
		is(available.get(2), strEq("Foo"), "third entry of available");
		is(selected.get(0),  strEq("Bar"), "first entry of selected");
	}
}
