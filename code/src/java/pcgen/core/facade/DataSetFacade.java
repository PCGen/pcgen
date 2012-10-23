/*
 * DataSetFacade.java
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
 * Created on Aug 19, 2008, 3:31:30 PM
 */
package pcgen.core.facade;

import java.util.List;

import pcgen.core.facade.generator.StatGenerationFacade;
import pcgen.core.facade.util.ListFacade;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface DataSetFacade
{

	public ListFacade<AbilityFacade> getAbilities(AbilityCategoryFacade category);

	public ListFacade<AbilityCategoryFacade> getAbilityCategories();

	/**
	 * Retrieve the abilities that must be taken before this ability can be taken.
	 * Used when building up a tree of abilities by prerequisite. 
	 * @param abilityFacade The ability to be queried
	 * @return The list of prerequisite abilities
	 */
	public List<AbilityFacade> getPrereqAbilities(AbilityFacade abilityFacade);

	public ListFacade<SkillFacade> getSkills();

	public ListFacade<RaceFacade> getRaces();

	public ListFacade<ClassFacade> getClasses();

	public ListFacade<DeityFacade> getDeities();

	public ListFacade<TemplateFacade> getTemplates();

	public ListFacade<CampaignFacade> getCampaigns();

	public GameModeFacade getGameMode();

	public ListFacade<AlignmentFacade> getAlignments();

	public ListFacade<StatFacade> getStats();

	public ListFacade<StatGenerationFacade> getStatGenerators();

	public SkillFacade getSpeakLanguageSkill();

	public ListFacade<EquipmentFacade> getEquipment();

	public ListFacade<BodyStructureFacade> getEquipmentLocations();

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
	 * Update the equipment list from the global equipment list. 
	 */
	public void refreshEquipment();
}
