/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.facade.core;

import java.util.List;

import pcgen.core.BodyStructure;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.facade.core.generator.StatGenerationFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;

public interface DataSetFacade
{

	public MapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>> getAbilities();

	//	public ListFacade<AbilityFacade> getAbilities(AbilityCategoryFacade category);
	//
	//	public ListFacade<AbilityCategoryFacade> getAbilityCategories();

	/**
	 * Retrieve the abilities that must be taken before this ability can be taken.
	 * Used when building up a tree of abilities by prerequisite. 
	 * @param abilityFacade The ability to be queried
	 * @return The list of prerequisite abilities
	 */
	public List<AbilityFacade> getPrereqAbilities(AbilityFacade abilityFacade);

	public ListFacade<Skill> getSkills();

	public ListFacade<Race> getRaces();

	public ListFacade<ClassFacade> getClasses();

	public ListFacade<Deity> getDeities();

	public ListFacade<PCTemplate> getTemplates();

	public ListFacade<CampaignFacade> getCampaigns();

	public GameModeFacade getGameMode();

	public ListFacade<PCAlignment> getAlignments();

	public ListFacade<PCStat> getStats();

	public ListFacade<StatGenerationFacade> getStatGenerators();

	public Skill getSpeakLanguageSkill();

	public ListFacade<EquipmentFacade> getEquipment();

	/**
	 * Add a new item of equipment (e.g. a new custom item) to the equipment list.
	 * @param equip The item of equipment to be added.
	 */
	public void addEquipment(EquipmentFacade equip);

	public ListFacade<BodyStructure> getEquipmentLocations();

	public ListFacade<String> getXPTableNames();

	public ListFacade<String> getCharacterTypes();

	/**
	 * @return The list of possible buy/sell rate schemes for this dataset. 
	 */
	public ListFacade<GearBuySellFacade> getGearBuySellSchemes();

	/**
	 * @return the list of kits
	 */
	public ListFacade<KitFacade> getKits();

	/**
	 * @return The list of sizes
	 */
	public ListFacade<SizeAdjustment> getSizes();

	/**
	 * Update the equipment list from the global equipment list. 
	 */
	public void refreshEquipment();

	/**
	 * Returns true if the DataSet understands Deities and Domains.
	 * 
	 * @return true if the DataSet understands Deities and Domains; false otherwise
	 */
	public boolean hasDeityDomain();
}
