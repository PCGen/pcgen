/*
 * PreAbilityTest.java
 * Copyright 2007 (C) James Dempsey
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
 * Created on 24/01/2007
 *
 * $Id: $
 */

package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.TestHelper;
import plugin.pretokens.parser.PreAbilityParser;

/**
 * <code>PreAbilityTest</code> verifies the function of the 
 * PreAbilityTester. 
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class PreAbilityTest extends AbstractCharacterTestCase
{

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreAbilityTest.class);
	}

	/**
	 * Test the function of the ANY key 
	 * @throws PersistenceLayerException
	 */
	public void testAnyMatch() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq =
				parser.parse("ability", "1,CATEGORY.ANY,ANY",
					false, false);
		assertFalse("Test any match with no abilities.", PrereqHandler.passes(
			prereq, character, null));

		Ability ab2 =
				TestHelper.makeAbility("Dancer", "BARDIC",
					"General.Bardic");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		character.addAbility(TestHelper.getAbilityCategory(ab2), ab2, null);

		assertTrue("Test any match with an ability.", PrereqHandler.passes(
			prereq, character, null));
		
	}

	/**
	 * Test the function of the category matching 
	 * @throws PersistenceLayerException
	 */
	public void testCategoryMatch() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq =
				parser.parse("ability", "1,CATEGORY.ANY,ANY",
					false, false);
		assertFalse("Test any match with no abilities.", PrereqHandler.passes(
			prereq, character, null));
		Prerequisite prereq2 =
			parser.parse("ability", "1,CATEGORY.BARDIC,ANY",
				false, false);
		assertFalse("Test bardic match with no abilities.", PrereqHandler.passes(
				prereq2, character, null));
		Prerequisite prereq3 =
			parser.parse("ability", "1,CATEGORY.FEAT,ANY",
				false, false);
		assertFalse("Test feat match with no abilities.", PrereqHandler.passes(
			prereq3, character, null));

		Ability ab2 =
				TestHelper.makeAbility("Dancer", "BARDIC",
					"General.Bardic");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		character.addAbility(TestHelper.getAbilityCategory(ab2), ab2, null);

		assertTrue("Test any match with an ability.", PrereqHandler.passes(
			prereq, character, null));
		assertTrue("Test bardic match with an ability.", PrereqHandler.passes(
			prereq2, character, null));
		assertFalse("Test feat match with an ability.", PrereqHandler.passes(
			prereq3, character, null));
		
	}

	/**
	 * Test the function of the catgeory matching 
	 * @throws PersistenceLayerException
	 */
	public void testKeyMatch() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq =
				parser.parse("ability", "1,CATEGORY.ANY,KEY_Dancer",
					false, false);
		assertFalse("Test any match with no abilities.", PrereqHandler.passes(
			prereq, character, null));
		Prerequisite prereq2 =
			parser.parse("ability", "1,KEY_Alertness",
				false, false);
		assertFalse("Test bardic match with no abilities.", PrereqHandler.passes(
				prereq2, character, null));
		Prerequisite prereq3 =
			parser.parse("ability", "1,CATEGORY.FEAT,KEY_Dancer",
				false, false);
		assertFalse("Test feat match with no abilities.", PrereqHandler.passes(
			prereq3, character, null));

		Ability ab2 =
				TestHelper.makeAbility("Dancer", "BARDIC",
					"General.Bardic");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		character.addAbility(TestHelper.getAbilityCategory(ab2), ab2, null);

		assertTrue("Test any match with an ability.", PrereqHandler.passes(
			prereq, character, null));
		assertFalse("Test bardic match with an ability.", PrereqHandler.passes(
			prereq2, character, null));
		assertFalse("Test feat match with an ability.", PrereqHandler.passes(
			prereq3, character, null));
		
	}

	/**
	 * Test the function of the type matching 
	 * @throws PersistenceLayerException
	 */
	public void testTypeMatch() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq =
				parser.parse("ability", "1,CATEGORY.ANY,TYPE.General", false,
					false);
		assertFalse("Test general type match with no abilities.", PrereqHandler
			.passes(prereq, character, null));
		Prerequisite prereq2 =
				parser.parse("ability", "1,CATEGORY.ANY,TYPE.Bardic", false,
					false);
		assertFalse("Test bardic type match with no abilities.", PrereqHandler
			.passes(prereq2, character, null));
		Prerequisite prereq3 =
				parser.parse("ability", "1,TYPE.Fighter", false, false);
		assertFalse("Test fighter type match with no abilities.", PrereqHandler
			.passes(prereq3, character, null));

		Ability ab2 =
				TestHelper.makeAbility("Dancer", "BARDIC", "General.Bardic");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		character.addAbility(TestHelper.getAbilityCategory(ab2), ab2, null);

		assertTrue("Test general type  match with an ability.", PrereqHandler
			.passes(prereq, character, null));
		assertTrue("Test bardic type match with an ability.", PrereqHandler
			.passes(prereq2, character, null));
		assertFalse("Test fighter type match with an ability.", PrereqHandler
			.passes(prereq3, character, null));
	}

	/**
	 * Test the function of the SERVESAS token with direct key matching 
	 * @throws PersistenceLayerException
	 */
	public void testKeyMatchWithServesAs() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq =
				parser.parse("ability", "1,CATEGORY.ANY,KEY_Dancer",
					false, false);
		assertFalse("Test any match with no abilities.", PrereqHandler.passes(
			prereq, character, null));
		Prerequisite prereq2 =
			parser.parse("ability", "1,KEY_Alertness",
				false, false);
		assertFalse("Test bardic match with no abilities.", PrereqHandler.passes(
				prereq2, character, null));
		Prerequisite prereq3 =
			parser.parse("ability", "1,CATEGORY.FEAT,KEY_Dancer",
				false, false);
		assertFalse("Test feat match with no abilities.", PrereqHandler.passes(
			prereq3, character, null));

		TestHelper.makeAbility("Dancer", "FEAT", "General");
		Ability ab2 =
				TestHelper.makeAbility("Dancer", "BARDIC",
					"General.Bardic");
		ab2.putServesAs("KEY_Alertness", "FEAT");
		ab2.putServesAs("KEY_Dancer", "FEAT");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		character.addAbility(TestHelper.getAbilityCategory(ab2), ab2, null);

		assertTrue("Test any match with an ability.", PrereqHandler.passes(
			prereq, character, null));
		assertFalse("Servesas non existant ability should not cause match.", PrereqHandler.passes(
			prereq2, character, null));
		assertTrue("Servesas should cause match.", PrereqHandler.passes(
			prereq3, character, null));
		
	}

	/**
	 * Test the function of the SERVESAS token with type matching 
	 * @throws PersistenceLayerException
	 */
	public void testTypeMatchWithServesAs() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq =
				parser.parse("ability", "1,CATEGORY.ANY,TYPE.General", false,
					false);
		assertFalse("Test general type match with no abilities.", PrereqHandler
			.passes(prereq, character, null));
		Prerequisite prereq2 =
				parser.parse("ability", "1,CATEGORY.ANY,TYPE.Bardic", false,
					false);
		assertFalse("Test bardic type match with no abilities.", PrereqHandler
			.passes(prereq2, character, null));
		Prerequisite prereq3 =
				parser.parse("ability", "1,TYPE.Fighter", false, false);
		assertFalse("Test fighter type match with no abilities.", PrereqHandler
			.passes(prereq3, character, null));

		TestHelper.makeAbility("Power Attack", "FEAT", "Fighter");
		Ability ab2 =
				TestHelper.makeAbility("Dancer", "BARDIC", "General.Bardic");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		ab2.putServesAs("KEY_Power Attack", "FEAT");
		character.addAbility(TestHelper.getAbilityCategory(ab2), ab2, null);

		assertTrue("Test general type  match with an ability.", PrereqHandler
			.passes(prereq, character, null));
		assertTrue("Test bardic type match with an ability.", PrereqHandler
			.passes(prereq2, character, null));
		assertTrue("Test fighter type match with SERVESAS ability.", PrereqHandler
			.passes(prereq3, character, null));
	}

	/**
	 * Test the function of the category matching 
	 * @throws PersistenceLayerException
	 */
	public void testCategoryMatchWithServesAs() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq =
				parser.parse("ability", "1,CATEGORY.ANY,ANY",
					false, false);
		assertFalse("Test any match with no abilities.", PrereqHandler.passes(
			prereq, character, null));
		Prerequisite prereq2 =
			parser.parse("ability", "1,CATEGORY.BARDIC,ANY",
				false, false);
		assertFalse("Test bardic match with no abilities.", PrereqHandler.passes(
				prereq2, character, null));
		Prerequisite prereq3 =
			parser.parse("ability", "1,CATEGORY.FEAT,ANY",
				false, false);
		assertFalse("Test feat match with no abilities.", PrereqHandler.passes(
			prereq3, character, null));

		TestHelper.makeAbility("Fascinate", "BARDIC", "Normal");
		Ability ab2 =
				TestHelper.makeAbility("Dancer", "FEAT",
					"General.Bardic");
		ab2.putServesAs("KEY_Fascinate", "BARDIC");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		character.addAbility(TestHelper.getAbilityCategory(ab2), ab2, null);

		assertTrue("Test any match with an ability.", PrereqHandler.passes(
			prereq, character, null));
		assertTrue("Test bardic match with an ability.", PrereqHandler.passes(
			prereq2, character, null));
		assertTrue("Test feat match with an ability.", PrereqHandler.passes(
			prereq3, character, null));
		
	}
	
}
