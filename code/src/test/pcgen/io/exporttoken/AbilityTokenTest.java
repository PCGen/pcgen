/*
 * AbilityTokenTest.java
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
 *
 * Created on 17/08/2008 14:04:19
 *
 * $Id: $
 */
package pcgen.io.exporttoken;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.Aspect;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.AlignmentConverter;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.io.ExportHandler;
import pcgen.util.TestHelper;
import pcgen.util.enumeration.Visibility;

/**
 * <code>AbilityTokenTest</code> tests the functioning of the ABILITY 
 * token processing code. 
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class AbilityTokenTest extends AbstractCharacterTestCase
{

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(AbilityTokenTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();

		// Make some ability categories and add them to the game mode
		Ability ab1 = TestHelper.makeAbility("Perform (Dance)", AbilityCategory.FEAT, "General.Fighter");
		ab1.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		ab1.put(ObjectKey.VISIBILITY, Visibility.DEFAULT);
		List<Aspect> colourList = new ArrayList<Aspect>();
		colourList.add(new Aspect("Colour", "Green"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Colour"), colourList);
		List<Aspect> sizeList = new ArrayList<Aspect>();
		sizeList.add(new Aspect("Size", "L"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Size"), sizeList);
		List<Aspect> shapeList = new ArrayList<Aspect>();
		Aspect cube = new Aspect("Shape", "Cube");
		Prerequisite prereq = new Prerequisite();
		prereq.setKind("ALIGN");
		prereq.setKey(AlignmentConverter.getPCAlignment("LG").getAbb());
		prereq.setOperator(PrerequisiteOperator.EQ);
		cube.addPrerequisite(prereq);
		shapeList.add(cube);
		shapeList.add(new Aspect("Shape", "Icosahedron"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Shape"), shapeList);
		List<Aspect> sidesList = new ArrayList<Aspect>();
		sidesList.add(new Aspect("Sides", "20"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Sides"), sidesList);
		List<Aspect> ageList = new ArrayList<Aspect>();
		ageList.add(new Aspect("Age In Years", "2000"));
		ab1.addToMapFor(MapKey.ASPECT, AspectName.getConstant("Age In Years"), ageList);
		character.addAbilityNeedCheck(AbilityCategory.FEAT, ab1);
	}

	/**
	 * Tests the aspect subtoken of ABILITY without a specific aspect.
	 */
	public void testAspect()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = new ExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals(
			"Age In Years: 2000, Colour: Green, Shape: Icosahedron, Sides: 20, Size: L",
			tok.getToken("ABILITY.FEAT.0.ASPECT", character, eh));
	}

	/**
	 * Tests the ASPECTCOUNT subtoken of ABILITY.
	 */
	public void testAspectCount()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = new ExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("5", tok.getToken("ABILITY.FEAT.0.ASPECTCOUNT", character,
			eh));
	}

	/**
	 * Tests the ASPECT subtoken of ABILITY with an aspect specified.
	 */
	public void testSingleAspect()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = new ExportHandler(null);
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
	public void testNonExistantSingleAspect()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = new ExportHandler(null);
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
	public void testHasAspect()
	{
		AbilityToken tok = new AbilityToken();
		ExportHandler eh = new ExportHandler(null);
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
}