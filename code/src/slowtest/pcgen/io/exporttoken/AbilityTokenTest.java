/*
 * Copyright 2008 (C) James Dempsey
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
 */
package pcgen.io.exporttoken;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.cdom.helper.Aspect;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.io.ExportHandler;
import pcgen.util.TestHelper;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code AbilityTokenTest} tests the functioning of the ABILITY
 * token processing code. 
 */
public class AbilityTokenTest extends AbstractCharacterTestCase
{
	@BeforeEach
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();

		// Make some ability categories and add them to the game mode
		Ability ab1 = TestHelper.makeAbility("Perform (Dance)",
			BuildUtilities.getFeatCat(), "General.Fighter");
		ab1.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		ab1.put(ObjectKey.VISIBILITY, Visibility.DEFAULT);
		List<Aspect> colourList = new ArrayList<>();
		colourList.add(new Aspect("Colour", "Green"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Colour"), colourList);
		List<Aspect> sizeList = new ArrayList<>();
		sizeList.add(new Aspect("Size", "L"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Size"), sizeList);
		List<Aspect> shapeList = new ArrayList<>();
		Aspect cube = new Aspect("Shape", "Cube");
		Prerequisite prereq = new Prerequisite();
		prereq.setKind("ALIGN");
		prereq.setKey("LG");
		prereq.setOperator(PrerequisiteOperator.EQ);
		cube.addPrerequisite(prereq);
		shapeList.add(cube);
		shapeList.add(new Aspect("Shape", "Icosahedron"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Shape"), shapeList);
		List<Aspect> sidesList = new ArrayList<>();
		sidesList.add(new Aspect("Sides", "20"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Sides"), sidesList);
		List<Aspect> ageList = new ArrayList<>();
		ageList.add(new Aspect("Age In Years", "2000"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Age In Years"), ageList);
		addAbility(BuildUtilities.getFeatCat(), ab1);

		TestHelper.makeSkill("Bluff", "Charisma", cha, true,
			SkillArmorCheck.NONE);
		TestHelper.makeSkill("Listen", "Wisdom", wis, true,
			SkillArmorCheck.NONE);

		Ability skillFocus = TestHelper.makeAbility("Skill Focus", BuildUtilities.getFeatCat(), "General");
		BonusObj aBonus = Bonus.newBonus(Globals.getContext(), "SKILL|LIST|3");
		if (aBonus != null)
		{
			skillFocus.addToListFor(ListKey.BONUS, aBonus);
		}
		skillFocus.put(ObjectKey.MULTIPLE_ALLOWED, true);
		Globals.getContext().unconditionallyProcess(skillFocus, "CHOOSE", "SKILL|ALL");
		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(),
				skillFocus, "KEY_Bluff");
		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(),
				skillFocus, "KEY_Listen");
		character.calcActiveBonuses();
	}

	/**
	 * Tests the aspect subtoken of ABILITY without a specific aspect.
	 */
	@Test
	public void testAspect()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals(
			"Age In Years: 2000, Colour: Green, Shape: Icosahedron, Sides: 20, Size: L",
			tok.getToken("ABILITY.FEAT.0.ASPECT", character, eh));
	}

	/**
	 * Tests the ASPECTCOUNT subtoken of ABILITY.
	 */
	@Test
	public void testAspectCount()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("5", tok.getToken("ABILITY.FEAT.0.ASPECTCOUNT", character,
			eh));
	}

	/**
	 * Tests the ASPECT subtoken of ABILITY with an aspect specified.
	 */
	@Test
	public void testSingleAspect()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals(
			"Shape: Icosahedron",
			tok.getToken("ABILITY.FEAT.0.ASPECT.2", character, eh));
		assertEquals(
			"20",
			tok.getToken("ABILITY.FEAT.0.ASPECT.Sides", character, eh));
		assertEquals(
			"Green",
			tok.getToken("ABILITY.FEAT.0.ASPECT.Colour", character, eh));
		assertEquals(
			"2000",
			tok.getToken("ABILITY.FEAT.0.ASPECT.Age In Years", character, eh));
		
	}

	/**
	 * Tests the ASPECT subtoken of ABILITY with an invalid aspect specified.
	 */
	@Test
	public void testNonExistantSingleAspect()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("", tok
			.getToken("ABILITY.FEAT.0.ASPECT.-5", character, eh));
		assertEquals("", tok.getToken("ABILITY.FEAT.0.ASPECT.5", character, eh));
		assertEquals("", tok.getToken("ABILITY.FEAT.0.ASPECT.500", character,
			eh));
		assertEquals("", tok.getToken("ABILITY.FEAT.0.ASPECT.Attack Type",
			character, eh));
		assertEquals("", tok.getToken("ABILITY.FEAT.0.ASPECT.Target",
			character, eh));
	}

	/**
	 * Tests the HASASPECT subtoken of ABILITY.
	 */
	@Test
	public void testHasAspect()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("N", tok.getToken("ABILITY.FEAT.0.HASASPECT.3", character,
			eh));
		assertEquals("N", tok.getToken("ABILITY.FEAT.0.HASASPECT.5", character,
			eh));
		assertEquals("N", tok.getToken("ABILITY.FEAT.0.HASASPECT.Attack Type",
			character, eh));
		assertEquals("Y", tok.getToken("ABILITY.FEAT.0.HASASPECT.Colour",
			character, eh));
		assertEquals("Y", tok.getToken("ABILITY.FEAT.0.HASASPECT.Age In Years",
			character, eh));
	}

	/**
	 * Tests the name subtoken of ABILITY.
	 */
	@Test
	public void testName()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals(
			"Perform (Dance)",
			tok.getToken("ABILITY.FEAT.0.NAME", character, eh));
		assertEquals(
			"Skill Focus",
			tok.getToken("ABILITY.FEAT.1.NAME", character, eh));
	}

	/**
	 * Tests the key subtoken of ABILITY.
	 */
	@Test
	public void testKey()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals(
			"KEY_Perform (Dance)",
			tok.getToken("ABILITY.FEAT.0.KEY", character, eh));

		assertEquals(
			"KEY_Skill Focus",
			tok.getToken("ABILITY.FEAT.1.KEY", character, eh));
	}

	/**
	 * Tests the associated subtoken of ABILITY.
	 */
	@Test
	public void testAssociated()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("",
			tok.getToken("ABILITY.FEAT.0.ASSOCIATED", character, eh));
		assertEquals("Bluff,Listen",
			tok.getToken("ABILITY.FEAT.1.ASSOCIATED", character, eh));
		assertEquals("Bluff",
			tok.getToken("ABILITY.FEAT.1.ASSOCIATED.0", character, eh));
		assertEquals("Listen",
			tok.getToken("ABILITY.FEAT.1.ASSOCIATED.1", character, eh));
	}

	/**
	 * Tests the ASSOCIATEDCOUNT subtoken of ABILITY.
	 */
	@Test
	public void testAssociatedCount()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("0",
			tok.getToken("ABILITY.FEAT.0.ASSOCIATEDCOUNT", character, eh));
		assertEquals("2",
			tok.getToken("ABILITY.FEAT.1.ASSOCIATEDCOUNT", character, eh));
	}
}
