/*
 * PObjectUtilitiesTest.java
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
 * Created on Apr 1, 2006
 *
 * $Id:  $
 *
 */
package pcgen.core;

import pcgen.AbstractCharacterTestCase;
import pcgen.rules.context.LoadContext;

/**
 * <code>PObjectUtilitiesTest</code> verifies that the PObjectUtilities
 * class is working correctly.
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
@SuppressWarnings("nls")
public class PObjectUtilitiesTest extends AbstractCharacterTestCase
{
	private PCClass arcaneClass = null;
	private PCClass divineClass = null;
	private PCClass psionicClass = null;

	/**
	 * Default Constructor
	 */
	public PObjectUtilitiesTest()
	{
		// Do Nothing
	}

	/**
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		LoadContext context = Globals.getContext();
		arcaneClass = new PCClass();
		arcaneClass.setName("TestArcane");
		arcaneClass.setAbbrev("TA");
		arcaneClass.setSpellType("ARCANE");
		arcaneClass.setSpellBaseStat("CHA");
		arcaneClass.setSpellBookUsed(false);
		arcaneClass.setMemorizeSpells(false);
		context.unconditionallyProcess(arcaneClass.getClassLevel(1), "KNOWN", "4,2");
		context.unconditionallyProcess(arcaneClass.getClassLevel(1), "CAST", "3,1");
		context.unconditionallyProcess(arcaneClass.getClassLevel(2), "KNOWN", "4,3,1");
		context.unconditionallyProcess(arcaneClass.getClassLevel(2), "CAST", "3,2,1");
		context.unconditionallyProcess(arcaneClass.getClassLevel(3), "KNOWN", "5,5,2,1");
		context.unconditionallyProcess(arcaneClass.getClassLevel(3), "CAST", "5,5,3,1");
		Globals.getClassList().add(arcaneClass);

		divineClass = new PCClass();
		divineClass.setName("TestDivine");
		divineClass.setAbbrev("TD");
		divineClass.setSpellType("DIVINE");
		divineClass.setSpellBaseStat("WIS");
		divineClass.setSpellBookUsed(false);
		divineClass.setMemorizeSpells(true);
		context.unconditionallyProcess(divineClass.getClassLevel(3), "CAST", "3,1,0");
		Globals.getClassList().add(divineClass);

		psionicClass = new PCClass();
		psionicClass.setName("TestPsion");
		psionicClass.setAbbrev("TP");
		psionicClass.setSpellType("PSIONIC");
		psionicClass.setSpellBaseStat("CHA");
		psionicClass.setSpellBookUsed(false);
		psionicClass.setMemorizeSpells(false);
		context.unconditionallyProcess(psionicClass.getClassLevel(1), "KNOWN", "0,3");
		context.unconditionallyProcess(psionicClass.getClassLevel(2), "KNOWN", "0,5");
		context.unconditionallyProcess(psionicClass.getClassLevel(3), "KNOWN", "0,5,2");
		context.unconditionallyProcess(psionicClass.getClassLevel(4), "KNOWN", "0,5,4");
		context.unconditionallyProcess(psionicClass.getClassLevel(5), "KNOWN", "0,5,4,2");
		Globals.getClassList().add(psionicClass);

		super.setUp();
	}

	/**
	 * @see pcgen.AbstractCharacterTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test the functioning of the setSpellLevelSelections method.
	 * @throws Exception
	 */
	public void testSetSpellLevelSelections() throws Exception
	{
		//		PObject pObj = new PObject();
		//		pObj.setName("My PObject");
		//		pObj.setChoiceString("SPELLLEVEL|1|TYPE=ARCANE|0|MAXLEVEL");
		//
		//		PlayerCharacter aPC  = getCharacter();
		//		aPC.incrementClassLevel(2, arcaneClass);
		//		aPC.incrementClassLevel(4, psionicClass);
		//
		//		List availList = new ArrayList();
		//		List selectedList = new ArrayList();
		//
		//		// Test the calculation of max level for an arcane class.
		//		PObjectUtilities.setSpellLevelSelections(pObj, availList, selectedList, true, aPC, 
		//			"SPELLLEVEL|1|TYPE=ARCANE|0|MAXLEVEL", new ArrayList(), new ArrayList());
		//		assertEquals("Number of available choices", 3, availList.size());
		//		assertEquals("2nd choice", "TestArcane 1", availList.get(1));
		//		assertEquals("3rd choice", "TestArcane 2", availList.get(2));
		//
		//		availList = new ArrayList();
		//		selectedList = new ArrayList();
		//
		//		// Test the calculation of max level for an arcane class.
		//		PObjectUtilities.setSpellLevelSelections(pObj, availList, selectedList, true, aPC, 
		//			"SPELLLEVEL|1|TYPE=ARCANE|0|MAXLEVEL-1", new ArrayList(), new ArrayList());
		//		assertEquals("Number of available choices", 2, availList.size());
		//		assertEquals("2nd choice", "TestArcane 1", availList.get(1));
		//		assertEquals("1st choice", "TestArcane 0", availList.get(0));
		//
		//		availList = new ArrayList();
		//		selectedList = new ArrayList();
		//		
		//		// Test the calculation of max level with brackets for a psionic class.
		//		PObjectUtilities.setSpellLevelSelections(pObj, availList, selectedList, true, aPC, 
		//			"SPELLLEVEL|1|TYPE=PSIONIC|1|(MAXLEVEL-1)", new ArrayList(), new ArrayList());
		//		assertEquals("Number of available choices", 2, availList.size());
		//		assertEquals("1st choice", "TestPsion 1", availList.get(0));
		//
		//		availList = new ArrayList();
		//		selectedList = new ArrayList();
		//
		//		// Test the calculation of max level without brackets for a psionic class.
		//		PObjectUtilities.setSpellLevelSelections(pObj, availList, selectedList, true, aPC, 
		//			"SPELLLEVEL|1|TYPE=PSIONIC|1|MAXLEVEL-1", new ArrayList(), new ArrayList());
		//		assertEquals("Number of available choices", 2, availList.size());
		//		assertEquals("1st choice", "TestPsion 1", availList.get(0));

	}
}
