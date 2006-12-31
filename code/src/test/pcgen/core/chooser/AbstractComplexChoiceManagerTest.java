/*
 * AbstractComplexChoiceManagerTest.java
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

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Ability;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.EquipmentList;
import pcgen.util.TestHelper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * {@code AbstractComplexChoiceManagerTest} test that the AbstractComplexChoiceManager class is functioning correctly.
 *
 * @author Andrew Wilson <nuance@sourceforge.net>
 */
@SuppressWarnings("nls")
public class AbstractComplexChoiceManagerTest extends AbstractCharacterTestCase {

	/**
	 * Constructs a new {@code AbstractComplexChoiceManagerTest}.
	 */
	public AbstractComplexChoiceManagerTest()
	{
		// Do Nothing
	}


	/**
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}
	
	/**
	 * @see pcgen.AbstractCharacterTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		EquipmentList.clearEquipmentMap();
	}

	/**
	 * Test that the Miscellaneous choice manager is returned.  Also test
	 * that NUMCHOICES= and COUNT= are working correctly and that they are
	 * removed from the choices list by the constructor.
	 */
	
	public void test001()
	{
		PObject pObj = new PObject();
		pObj.setName("My PObject");
		pObj.setChoiceString("COUNT=1|NUMCHOICES=2|Foo|Bar|Baz");
		is(pObj.getChoiceString(), strEq("COUNT=1|NUMCHOICES=2|Foo|Bar|Baz"));

		PlayerCharacter aPC  = getCharacter();
		
		ChoiceManagerList choiceManager = ChooserUtilities.getChoiceManager(pObj, null, aPC);
		is(choiceManager, not(eq(null)), "Found the chooser");

		is(choiceManager.typeHandled(), strEq("MISC"), "got expected chooser");
		is(((AbstractComplexChoiceManager) choiceManager).isValid(), 
				eq(true), "chooser is valid");
		
		try
		{
			Class<? extends ChoiceManagerList> cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "numberOfChoices");
			is (aField.get(choiceManager), eq(2), "Number of choices is set correctly");

			aField  = (Field) TestHelper.findField(cMClass, "requestedSelections");
			is (aField.get(choiceManager), eq(1), "Requested selections are set correctly");
			
			aField  = (Field) TestHelper.findField(cMClass, "choices");
			List<?> choices = (List) aField.get(choiceManager);
			is (choices.size(), eq(3), "There are three elements in the chooser");
			is (choices.get(0), strEq("Foo"));
			is (choices.get(1), strEq("Bar"));
			is (choices.get(2), strEq("Baz"));
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}
	}

	/** 
	 * Test that if an Ability object is pased as the first parameter, the
	 * cost, dupsAllowed and multiples felds are set correctly.
	 */

	public void test002()
	{
		Ability anAbility = new Ability();
		anAbility.setName("Random Ability");
		anAbility.setCost("2.5");
		anAbility.setStacks("Y");
		anAbility.setMultiples("Y");
		anAbility.setChoiceString("COUNT=1|NUMCHOICES=2|Foo|Bar|Baz");
		is(anAbility.getChoiceString(), strEq("COUNT=1|NUMCHOICES=2|Foo|Bar|Baz"));

		PlayerCharacter aPC  = getCharacter();
		
		ChoiceManagerList choiceManager = ChooserUtilities.getChoiceManager(anAbility, null, aPC);
		is(choiceManager, not(eq(null)), "Found the chooser");

		is(choiceManager.typeHandled(), strEq("MISC"), "got expected chooser");
		is(((AbstractComplexChoiceManager) choiceManager).isValid(), 
				eq(true), "chooser is valid");
		
		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "cost");
			is (aField.get(choiceManager), eq(2.5), "cost is set correctly");

			aField  = (Field) TestHelper.findField(cMClass, "dupsAllowed");
			is (aField.get(choiceManager), eq(true), "dupsAllowed is set correctly");
			
			aField  = (Field) TestHelper.findField(cMClass, "multiples");
			is (aField.get(choiceManager), eq(true), "multiples is set correctly");
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}
	}

	/**
	 * Test that a choice string with fewer tokens than necessary returns a
	 * chooser with an empty choices list.
	 */	

	public void test003()
	{
		PObject pObj = new PObject();
		pObj.setName("My PObject");
		pObj.setChoiceString("COUNT=1");
		is(pObj.getChoiceString(), strEq("COUNT=1"));

		PlayerCharacter aPC  = getCharacter();
		
		ChoiceManagerList choiceManager = ChooserUtilities.getChoiceManager(pObj, null, aPC);
		is(choiceManager, not(eq(null)), "Found the chooser");

		is(choiceManager.typeHandled(), strEq("MISC"), "got expected chooser");
		is(((AbstractComplexChoiceManager) choiceManager).isValid(), 
				eq(false), "chooser is not valid");
		
		try
		{
			Class cMClass = choiceManager.getClass();

			Field aField  = (Field) TestHelper.findField(cMClass, "requestedSelections");
			is (aField.get(choiceManager), eq(1), "Requested selections are set correctly");

			aField  = (Field) TestHelper.findField(cMClass, "choices");
			List choices = (List) aField.get(choiceManager);
			is (choices.size(), eq(0), "Choices list is empty");
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
		}
	}
}
