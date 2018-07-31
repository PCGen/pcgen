/**
 * Copyright James Dempsey, 2010
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
package pcgen.core;

import java.util.List;

import junit.framework.TestCase;
import pcgen.cdom.base.Constants;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.BodyStructureFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.persistence.PersistenceLayerException;
import pcgen.core.prereq.Prerequisite;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.pretokens.parser.PreAbilityParser;

/**
 * The Class <code>DataSetTest</code> check that the Dataset class is functioning 
 * correctly.
 *
 * <br/>
 * 
 */
@SuppressWarnings("nls")
public class DataSetTest extends TestCase
{

	public final void testGetEquipmentLocationsAll()
	{
		// if getEquipmentLocations is done first, the defaultonly test fails.
		getEquipmentLocationsDefaultOnly();
		getEquipmentLocations();
	}
	
	/**
	 * Test method for {@link pcgen.core.DataSet#getEquipmentLocations()}. Validate 
	 * that the default locations are added.
	 */
	public final void getEquipmentLocationsDefaultOnly()
	{
		DataSet dataset = new DataSet(Globals.getContext(), SettingsHandler.getGame(), new DefaultListFacade<>());
		ListFacade<BodyStructureFacade> locations = dataset.getEquipmentLocations();
		assertNotNull("Body Structure should not be null", locations);
		assertTrue("Expected to find Equipped",
			checkBodyStructurePresent(locations, Constants.EQUIP_LOCATION_EQUIPPED));
		assertTrue("Expected to find Carried",
			checkBodyStructurePresent(locations, Constants.EQUIP_LOCATION_CARRIED));
		assertTrue("Expected to find Not Carried",
			checkBodyStructurePresent(locations, Constants.EQUIP_LOCATION_NOTCARRIED));
		assertEquals("Incorrect size of body structures list", 3, locations.getSize());
	}

	/**
	 * Test method for {@link pcgen.core.DataSet#getEquipmentLocations()}. Validate 
	 * that known body structures get added.
	 */
	public final void getEquipmentLocations()
	{
		final String structName = "TestStruct";
		SystemCollections.addToBodyStructureList(structName, SettingsHandler.getGame().getName());
		DataSet dataset =
				new DataSet(Globals.getContext(), SettingsHandler.getGame(),
						new DefaultListFacade<>());
		ListFacade<BodyStructureFacade> locations =
				dataset.getEquipmentLocations();
		assertNotNull("Body Structure should not be null", locations);
		// TODO i18n this. It should be the same value as structname, not the localized value.
		assertTrue(
			"Expected to find added body structure '" + structName + "'",
			checkBodyStructurePresent(locations, "Teststruct"));
		assertTrue("Expected to find Equipped", checkBodyStructurePresent(
			locations, Constants.EQUIP_LOCATION_EQUIPPED));
		assertTrue("Expected to find Carried", checkBodyStructurePresent(
			locations, Constants.EQUIP_LOCATION_CARRIED));
		assertTrue("Expected to find Not Carried", checkBodyStructurePresent(
			locations, Constants.EQUIP_LOCATION_NOTCARRIED));
		assertEquals("Incorrect size of body structures list", 4, locations
			.getSize());
	}

	/**
	 * Verify the getPrereqAbilities method is functioning correctly.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void testGetPrereqAbilities() throws PersistenceLayerException
	{
		Ability acrobatics = TestHelper.makeAbility("Acrobatics", BuildUtilities.getFeatCat(), "general");
		Ability dodge = TestHelper.makeAbility("Dodge", BuildUtilities.getFeatCat(), "general");
		Ability mobility = TestHelper.makeAbility("Mobility", BuildUtilities.getFeatCat(), "general");
		Ability springAttack = TestHelper.makeAbility("Spring Attack", BuildUtilities.getFeatCat(), "general");
		PreAbilityParser parser = new PreAbilityParser();
		Prerequisite prereq =
				parser.parse("ability", "1,CATEGORY=FEAT,KEY_Dodge",
					false, false);
		mobility.addPrerequisite(prereq);
		prereq =
			parser.parse("ability", "2,CATEGORY=FEAT,KEY_Dodge,KEY_Mobility",
				false, false);
		springAttack.addPrerequisite(prereq);

		DataSet dataset =
			new DataSet(Globals.getContext(), SettingsHandler.getGame(),
					new DefaultListFacade<>());
		List<AbilityFacade> abilities = dataset.getPrereqAbilities(acrobatics);
		assertEquals("Acrobatics prereq should be empty", 0, abilities.size());
		abilities = dataset.getPrereqAbilities(dodge);
		assertEquals("Dodge prereq should be empty", 0, abilities.size());
		abilities = dataset.getPrereqAbilities(mobility);
		assertEquals("Mobility prereq should not be empty", 1, abilities.size());
		assertEquals("Mobility prereq should be dodge", dodge, abilities.get(0));
		abilities = dataset.getPrereqAbilities(springAttack);
		assertEquals("Spring Attack prereq should not be empty", 2, abilities.size());
		assertEquals("Spring Attack prereq should be dodge", dodge, abilities.get(0));
		assertEquals("Spring Attack prereq should be mobility", mobility, abilities.get(1));
	}
	
	/**
	 * @param locations
	 * @param name 
	 */
	private boolean checkBodyStructurePresent(
		ListFacade<BodyStructureFacade> locations, String name)
	{
		boolean foundRec = false;
		for (BodyStructureFacade equipmentLocFacade : locations)
		{
			if (equipmentLocFacade.toString().equals(name))
			{
				foundRec = true;
			}
		}
		
		return foundRec;
	}

}
