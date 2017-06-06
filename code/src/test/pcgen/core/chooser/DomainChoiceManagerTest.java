/*
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
 *
 * $Author: nuance $
 * $Date: 2006-03-26 08:00:03 +0100 (Sun, 26 Mar 2006) $
 * $Revision: 471 $
 */
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;

/**
 * {@code DomainChoiceManagerTest} test that the DomainChoiceManager class is
 * functioning correctly.
 * 
 */

public class DomainChoiceManagerTest extends AbstractCharacterTestCase
{

	/**
	 * Test the constructor
	 */
	public void test001()
	{
		Race pObj = new Race();
		pObj.setName("My PObject");
		LoadContext context = Globals.getContext();
		Domain foo = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Foo");
		Domain bar = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Bar");
		Domain baz = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Baz");
		Domain qux = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Qux");
		Domain quux = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Quux");
		context.unconditionallyProcess(pObj, "CHOOSE",
				"DOMAIN|KEY_Foo|KEY_Bar|KEY_Baz|KEY_Qux|KEY_Quux");
		assertTrue(context.getReferenceContext().resolveReferences(null));
		assertNotNull(pObj.get(ObjectKey.CHOOSE_INFO));
		pObj.put(FormulaKey.NUMCHOICES, FormulaFactory.getFormulaFor(4));
		PlayerCharacter aPC = getCharacter();

		ChoiceManagerList choiceManager = ChooserUtilities.getChoiceManager(
				pObj, aPC);
		is(choiceManager, not(eq(null)), "Found the chooser");

		List<Domain> aList = new ArrayList<>();
		List<Domain> sList = new ArrayList<>();
		choiceManager.getChoices(aPC, aList, sList);
		assertEquals(5, aList.size());
		assertTrue(aList.contains(foo));
		assertTrue(aList.contains(bar));
		assertTrue(aList.contains(baz));
		assertTrue(aList.contains(qux));
		assertTrue(aList.contains(quux));

		assertEquals(0, sList.size());
	}

}
