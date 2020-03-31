/*
 * Copyright 2013 (C) James Dempsey
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
package plugin.exporttokens;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.cdom.helper.Aspect;
import pcgen.cdom.helper.CNAbilitySelection;
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
 * {@code VAbilityTokenTest} tests the functioning of the VABILITY
 * token processing code. 
 */
public class VAbilityTokenTest extends AbstractCharacterTestCase
{
	private Ability skillFocus;

	@BeforeEach
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();

		// Make some ability categories and add them to the game mode
		Ability ab1 = TestHelper.makeAbility("Perform (Dance)", BuildUtilities.getFeatCat(), "General.Fighter");
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
		CNAbility cna = CNAbilityFactory.getCNAbility(BuildUtilities.getFeatCat(), Nature.VIRTUAL, ab1);
		character.addAbility(new CNAbilitySelection(cna),
			UserSelection.getInstance(), UserSelection.getInstance());

		TestHelper.makeSkill("Bluff", "Charisma", cha, true,
			SkillArmorCheck.NONE);
		TestHelper.makeSkill("Listen", "Wisdom", wis, true,
			SkillArmorCheck.NONE);

		skillFocus =
				TestHelper.makeAbility("Skill Focus", BuildUtilities.getFeatCat(), "General");
		BonusObj aBonus = Bonus.newBonus(Globals.getContext(), "SKILL|LIST|3");
		if (aBonus != null)
		{
			skillFocus.addToListFor(ListKey.BONUS, aBonus);
		}
		skillFocus.put(ObjectKey.MULTIPLE_ALLOWED, true);
		Globals.getContext().unconditionallyProcess(skillFocus, "CHOOSE", "SKILL|ALL");
		cna = CNAbilityFactory.getCNAbility(BuildUtilities.getFeatCat(), Nature.VIRTUAL, skillFocus);
		character.addAbility(new CNAbilitySelection(cna, "KEY_Bluff"),
			UserSelection.getInstance(), UserSelection.getInstance());
		character.addAbility(new CNAbilitySelection(cna, "KEY_Listen"),
			UserSelection.getInstance(), UserSelection.getInstance());
		character.calcActiveBonuses();
	}

	/**
	 * Tests the aspect subtoken of VABILITY without a specific aspect.
	 */
	@Test
	public void testAspect()
	{
		VAbilityToken tok = new VAbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals(
			"Age In Years: 2000, Colour: Green, Shape: Icosahedron, Sides: 20, Size: L",
			tok.getToken("VABILITY.FEAT.0.ASPECT", character, eh));
	}

	/**
	 * Tests the ASPECTCOUNT subtoken of VABILITY.
	 */
	@Test
	public void testAspectCount()
	{
		VAbilityToken tok = new VAbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("5", tok.getToken("VABILITY.FEAT.0.ASPECTCOUNT", character,
			eh));
	}

	/**
	 * Tests the ASPECT subtoken of VABILITY with an aspect specified.
	 */
	@Test
	public void testSingleAspect()
	{
		VAbilityToken tok = new VAbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals(
			"Shape: Icosahedron",
			tok.getToken("VABILITY.FEAT.0.ASPECT.2", character, eh));
		assertEquals(
			"20",
			tok.getToken("VABILITY.FEAT.0.ASPECT.Sides", character, eh));
		assertEquals(
			"Green",
			tok.getToken("VABILITY.FEAT.0.ASPECT.Colour", character, eh));
		assertEquals(
			"2000",
			tok.getToken("VABILITY.FEAT.0.ASPECT.Age In Years", character, eh));
		
	}

	/**
	 * Tests the ASPECT subtoken of VABILITY with an invalid aspect specified.
	 */
	@Test
	public void testNonExistantSingleAspect()
	{
		VAbilityToken tok = new VAbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("", tok
			.getToken("VABILITY.FEAT.0.ASPECT.-5", character, eh));
		assertEquals("", tok.getToken("VABILITY.FEAT.0.ASPECT.5", character, eh));
		assertEquals("", tok.getToken("VABILITY.FEAT.0.ASPECT.500", character,
			eh));
		assertEquals("", tok.getToken("VABILITY.FEAT.0.ASPECT.Attack Type",
			character, eh));
		assertEquals("", tok.getToken("VABILITY.FEAT.0.ASPECT.Target",
			character, eh));
	}

	/**
	 * Tests the HASASPECT subtoken of VABILITY.
	 */
	@Test
	public void testHasAspect()
	{
		VAbilityToken tok = new VAbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("N", tok.getToken("VABILITY.FEAT.0.HASASPECT.3", character,
			eh));
		assertEquals("N", tok.getToken("VABILITY.FEAT.0.HASASPECT.5", character,
			eh));
		assertEquals("N", tok.getToken("VABILITY.FEAT.0.HASASPECT.Attack Type",
			character, eh));
		assertEquals("Y", tok.getToken("VABILITY.FEAT.0.HASASPECT.Colour",
			character, eh));
		assertEquals("Y", tok.getToken("VABILITY.FEAT.0.HASASPECT.Age In Years",
			character, eh));
	}

	/**
	 * Tests the name subtoken of VABILITY.
	 */
	@Test
	public void testName()
	{
		VAbilityToken tok = new VAbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals(
			"Perform (Dance)",
			tok.getToken("VABILITY.FEAT.0.NAME", character, eh));
		assertEquals(
			"Skill Focus",
			tok.getToken("VABILITY.FEAT.1.NAME", character, eh));
	}

	/**
	 * Tests the key subtoken of VABILITY.
	 */
	@Test
	public void testKey()
	{
		VAbilityToken tok = new VAbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals(
			"KEY_Perform (Dance)",
			tok.getToken("VABILITY.FEAT.0.KEY", character, eh));

		assertEquals(
			"KEY_Skill Focus",
			tok.getToken("VABILITY.FEAT.1.KEY", character, eh));
	}

	/**
	 * Tests the associated subtoken of VABILITY.
	 */
	@Test
	public void testAssociated()
	{
		VAbilityToken tok = new VAbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("",
			tok.getToken("VABILITY.FEAT.0.ASSOCIATED", character, eh));
		assertEquals("Bluff,Listen",
			tok.getToken("VABILITY.FEAT.1.ASSOCIATED", character, eh));
		assertEquals("Bluff",
			tok.getToken("VABILITY.FEAT.1.ASSOCIATED.0", character, eh));
		assertEquals("Listen",
			tok.getToken("VABILITY.FEAT.1.ASSOCIATED.1", character, eh));
	}

	/**
	 * Tests the ASSOCIATEDCOUNT subtoken of VABILITY.
	 */
	@Test
	public void testAssociatedCount()
	{
		VAbilityToken tok = new VAbilityToken();
		ExportHandler eh = ExportHandler.createExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("0",
			tok.getToken("VABILITY.FEAT.0.ASSOCIATEDCOUNT", character, eh));
		assertEquals("2",
			tok.getToken("VABILITY.FEAT.1.ASSOCIATEDCOUNT", character, eh));
	}
}
