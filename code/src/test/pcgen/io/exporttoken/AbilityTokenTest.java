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

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.Aspect;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
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
		AbilityCategory featCategory =
				SettingsHandler.getGame().silentlyGetAbilityCategory("FEAT");
		if (featCategory == null)
		{
			featCategory = new AbilityCategory("FEAT");
			SettingsHandler.getGame().addAbilityCategory(featCategory);
		}

		Ability ab1 = TestHelper.makeAbility("Perform (Dance)", "FEAT", "General.Fighter");
		ab1.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		ab1.put(ObjectKey.VISIBILITY, Visibility.DEFAULT);
		ab1.addToListFor(ListKey.ASPECT, new Aspect("Colour", "Green"));
		ab1.addToListFor(ListKey.ASPECT, new Aspect("Size", "L"));
		ab1.addToListFor(ListKey.ASPECT, new Aspect("Shape", "Icosahedron"));
		ab1.addToListFor(ListKey.ASPECT, new Aspect("Sides", "20"));
		ab1.addToListFor(ListKey.ASPECT, new Aspect("Age In Years", "2000"));
		character.addAbility(featCategory, ab1, null);
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
			"Colour: Green, Size: L, Shape: Icosahedron, Sides: 20, Age In Years: 2000",
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