/*
 * PlayerCharacterSpellTest.java
 * Copyright James Dempsey, 2012
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
 *
 * Created on 26/01/2012 11:09:46 AM
 *
 * $Id: PlayerCharacterSpellTest.java 19041 2013-01-06 18:52:59Z thpr $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.analysis.SpellLevel;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.util.TestHelper;

/**
 * The Class <code>ChallengeRatingPathfinderTest</code> checks the calculation
 * of challenge ratings for the Pathfinder RPG game mode
 *
 * <br/>
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2013-01-06 19:52:59 +0100 (So, 06 Jan 2013) $
 * 
 * @author Stefan Radermacher <zaister@users.sourceforge.net>
 * @version $Revision: 19041 $
 */

public class ChallengeRatingPathfinderTest extends AbstractCharacterTestCase
{
	private Race standardRace;
	private Race koboldRace;
	private Race drowNobleRace;
	private Race babauRace;
	private Race dryadRace;
	private Race companionRace;
	
	private PCClass pcClass;
	private PCClass pcClass2;
	private PCClass npcClass;
	private PCClass npcClass2;
	private PCClass monsterClass;
	private PCClass companionClass;

	/**
	 * Run the test
	 * @param args don't need args apparently
	 */
	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(ChallengeRatingPathfinderTest.class);
	}

	@Override
	protected void additionalSetUp() throws Exception
	{
		SettingsHandler.getGame().setCRSteps("1/2|1/3|1/4|1/6|1/8");
		SettingsHandler.getGame().setCRThreshold("BASECR");
		SettingsHandler.getGame().setMonsterRoleList(new ArrayList<String>(Arrays.asList("Combat","Skill","Druid")));
		SettingsHandler.getGame().addClassType("PC			CRFORMULA:CL	ISMONSTER:NO	CRMOD:-1	CRMODPRIORITY:1");
		SettingsHandler.getGame().addClassType("NPC			CRFORMULA:CL	ISMONSTER:NO	CRMOD:-2	CRMODPRIORITY:2");
		SettingsHandler.getGame().addClassType("Monster		CRFORMULA:0		ISMONSTER:YES");
		SettingsHandler.getGame().addClassType("Companion	CRFORMULA:NONE	ISMONSTER:YES");
		
		LoadContext context = Globals.getContext();
		CampaignSourceEntry source = TestHelper.createSource(getClass());
		GenericLoader<Race> raceLoader = new GenericLoader<Race>(Race.class);
		PCClassLoader classLoader = new PCClassLoader();

		final String standardRaceLine = "Standard Race";
		raceLoader.parseLine(context, null, standardRaceLine, source);
		standardRace = context.ref.silentlyGetConstructedCDOMObject(Race.class, "Standard Race");

		final String koboldRaceLine = "Kobold	CRMOD:NPC|-3";
		raceLoader.parseLine(context, null, koboldRaceLine, source);
		koboldRace = context.ref.silentlyGetConstructedCDOMObject(Race.class, "Kobold");

		final String drowNobleLine = "Drow Noble	CRMOD:PC.NPC|0";
		raceLoader.parseLine(context, null, drowNobleLine, source);
		drowNobleRace = context.ref.silentlyGetConstructedCDOMObject(Race.class, "Drow Noble");

		final String babauLine = "Babau	MONSTERCLASS:TestMonsterClass:7	CR:6	ROLE:Combat.Skill";
		raceLoader.parseLine(context, null, babauLine, source);
		babauRace = context.ref.silentlyGetConstructedCDOMObject(Race.class, "Babau");

		final String dryadLine = "Dryad	MONSTERCLASS:TestMonsterClass:8	CR:7	ROLE:Druid";
		raceLoader.parseLine(context, null, dryadLine, source);
		dryadRace = context.ref.silentlyGetConstructedCDOMObject(Race.class, "Dryad");

		final String companionLine = "TestCompanion MONSTERCLASS:TestCompanionClass:4";
		raceLoader.parseLine(context, null, companionLine, source);
		companionRace = context.ref.silentlyGetConstructedCDOMObject(Race.class, "TestCompanion");

		final String pcClassLine = "CLASS:TestPCClass	TYPE:PC		ROLE:Combat";
		pcClass = classLoader.parseLine(context, null, pcClassLine, source);
		context.ref.importObject(pcClass);
		
		final String pcClassLine2 = "CLASS:TestPCClass2	TYPE:PC		ROLE:Druid";
		pcClass2 = classLoader.parseLine(context, null, pcClassLine2, source);
		context.ref.importObject(pcClass2);
		
		final String npcClassLine = "CLASS:TestNPCClass2	TYPE:NPC";
		npcClass = classLoader.parseLine(context, null, npcClassLine, source);
		context.ref.importObject(npcClass);

		final String npcClassLine2 = "CLASS:TestNPCClass2	TYPE:NPC";
		npcClass2 = classLoader.parseLine(context, null, npcClassLine2, source);
		context.ref.importObject(npcClass2);

		final String monsterClassLine = "CLASS:TestMonsterClass	HD:8	CLASSTYPE:Monster";
		monsterClass = classLoader.parseLine(context, null, monsterClassLine, source);
		context.ref.importObject(monsterClass);

		final String companionClassLine = "CLASS:TestCompanionClass	HD:8	CLASSTYPE:Companion";
		companionClass = classLoader.parseLine(context, null, companionClassLine, source);
		context.ref.importObject(companionClass);

		context.commit();

		context.ref.buildDerivedObjects();
		context.resolveDeferredTokens();
		assertTrue(context.ref.resolveReferences(null));
	}

	/**
	 * Test PC class level 1 => CR 1/2
	 * @throws Exception If an error occurs.
	 */
	public void testPCClassLevel1() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(1, pcClass);
		assertEquals(0.5, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test PC class level 2 => CR 1/4
	 * @throws Exception If an error occurs.
	 */
	public void testPCClassLevel2() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(2, pcClass);
		assertEquals(1, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test NPC class level 1 => CR 1/3
	 * @throws Exception If an error occurs.
	 */
	public void testNPCClassLevel1() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(1, npcClass);
		assertEquals(0.333, pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test NPC class level 2 => CR 1/2
	 * @throws Exception If an error occurs.
	 */
	public void testNPCClassLevel2() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(2, npcClass);
		assertEquals(0.5, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test NPC class level 3 => CR 1
	 * @throws Exception If an error occurs.
	 */
	public void testNPCClassLevel3() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(3, npcClass);
		assertEquals(1, pc.getDisplay().calcCR(), 0.0);
	}
	
	
	/**
	 * Test PC class multiclass level 4/4 => CR 7
	 * @throws Exception If an error occurs.
	 */
	public void testMultiClassPCLevel4PCLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(4, pcClass);
		pc.incrementClassLevel(4, pcClass2);
		assertEquals(7, pc.getDisplay().calcCR(), 0.0);
	}
	
	/**
	 * Test NPC class multiclass level 4/4 => CR 6
	 * @throws Exception If an error occurs.
	 */
	public void testMultiClassNPCLevel4NPCLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(4, npcClass);
		pc.incrementClassLevel(4, npcClass2);
		assertEquals(6, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test NPC/PC class multiclass level 4/4 => CR 7
	 * @throws Exception If an error occurs.
	 */
	public void testMultiClassNPCLevel4PCLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(4, npcClass);
		pc.incrementClassLevel(4, pcClass);
		assertEquals(7, pc.getDisplay().calcCR(), 0.0);
	}
	

	/**
	 * Test NPC class level 1, kobold (CRMOD:NPC|-3) => CR 1/4
	 * @throws Exception If an error occurs.
	 */
	public void testNPCClassKoboldLevel1() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(1, npcClass);
		assertEquals(0.25, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test NPC class level 2, kobold (CRMOD:NPC|-3) => CR 1/3
	 * @throws Exception If an error occurs.
	 */
	public void testNPCClassKoboldLevel2() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(2, npcClass);
		assertEquals(0.333, pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test NPC class level 3, kobold (CRMOD:NPC|-3) => CR 1/2
	 * @throws Exception If an error occurs.
	 */
	public void testNPCClassKoboldLevel3() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(3, npcClass);
		assertEquals(0.5, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test NPC class level 4, kobold (CRMOD:NPC|-3) => CR 1
	 * @throws Exception If an error occurs.
	 */
	public void testNPCClassKoboldLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(4, npcClass);
		assertEquals(1, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test PC class level 4, kobold (CRMOD:NPC|-3) => CR 3
	 * @throws Exception If an error occurs.
	 */
	public void testPCClassKoboldLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(4, pcClass);
		assertEquals(3, pc.getDisplay().calcCR(), 0.0);
	}


	/**
	 * Test PC class level 4, drow noble (CRMOD:PC.NPC|0) => CR 4
	 * @throws Exception If an error occurs.
	 */
	public void testPCClassDrowNobleLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(drowNobleRace);
		pc.incrementClassLevel(4, pcClass);
		assertEquals(4, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test NPC class level 4, drow noble (CRMOD:PC.NPC|0) => CR 4
	 * @throws Exception If an error occurs.
	 */
	public void testNPCClassDrowNobleLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(drowNobleRace);
		pc.incrementClassLevel(4, npcClass);
		assertEquals(4, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test PC class key level 4 babau => CR 10
	 * @throws Exception If an error occurs.
	 */
	public void testPCClassBabauKeyLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(babauRace);
		pc.incrementClassLevel(4, pcClass);
		assertEquals(10, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test PC class key level 4 babau => CR 8
	 * @throws Exception If an error occurs.
	 */
	public void testPCClassBabauNonKeyLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(babauRace);
		pc.incrementClassLevel(4, pcClass2);
		assertEquals(8, pc.getDisplay().calcCR(), 0.0);
	}
	/**
	 * Test PC class key level 10 babau => CR 13
	 * @throws Exception If an error occurs.
	 */
	public void testPCClassBabauNonKeyLevel10() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(babauRace);
		pc.incrementClassLevel(10, pcClass2);
		assertEquals(13, pc.getDisplay().calcCR(), 0.0);
	}

	/**
	 * Test PC class key level 8 dryad => CR 15
	 * @throws Exception If an error occurs.
	 */
	public void testPCClassDyradKeyLevel8() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(dryadRace);
		pc.incrementClassLevel(8, pcClass2);
		assertEquals(15, pc.getDisplay().calcCR(), 0.0);
	}
	/**
	 * Test PC class non key level 8 dryad => CR 11
	 * @throws Exception If an error occurs.
	 */
	public void testPCClassDryadNonKeyLevel8() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(dryadRace);
		pc.incrementClassLevel(8, pcClass);
		assertEquals(11, pc.getDisplay().calcCR(), 0.0);
	}
	
	/**
	 * Test Companion => CR 0
	 * @throws Exception If an error occurs.
	 */
	public void testCompanion() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(companionRace);
		pc.incrementClassLevel(4, companionClass);
		assertEquals(Float.NaN, pc.getDisplay().calcCR(), 0.0);
	}
}
