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
package pcgen.gui2.facade;

import java.util.List;

import pcgen.core.AbilityCategory;
import pcgen.core.GameMode;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.facade.core.AbilityCategoryFacade;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.BodyStructureFacade;
import pcgen.facade.core.CampaignFacade;
import pcgen.facade.core.ClassFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.DeityFacade;
import pcgen.facade.core.DomainFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.GameModeFacade;
import pcgen.facade.core.GearBuySellFacade;
import pcgen.facade.core.KitFacade;
import pcgen.facade.core.RaceFacade;
import pcgen.facade.core.SizeAdjustmentFacade;
import pcgen.facade.core.SkillFacade;
import pcgen.facade.core.StatFacade;
import pcgen.facade.core.TemplateFacade;
import pcgen.facade.core.generator.StatGenerationFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultMapFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;

/**
 * The Class <code></code> is ...
 *
 * <br/>
 * 
 */
public class MockDataSetFacade implements DataSetFacade
{

	private DefaultListFacade<BodyStructureFacade> equipmentLoc;
	private DefaultMapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>> abilityMap;
	private final GameMode game;
	private DefaultListFacade<RaceFacade> races;
	private DefaultListFacade<SkillFacade> skills;
	private DefaultListFacade<StatFacade> stats;
	private DefaultListFacade<GearBuySellFacade> gearBuySellSchemes;
	private DefaultListFacade<ClassFacade> classes;


	public MockDataSetFacade(GameMode gameMode)
	{
		this.game = gameMode;
		equipmentLoc = new DefaultListFacade<>();
		abilityMap = new DefaultMapFacade<>();
		races = new DefaultListFacade<>();
		skills = new DefaultListFacade<>();
		stats  = new DefaultListFacade<>();
		gearBuySellSchemes = new DefaultListFacade<>();
		classes  = new DefaultListFacade<>();
	}
	
	/**
	 * @see pcgen.core.facade.DataSetFacade#getAbilities(pcgen.core.facade.AbilityCategoryFacade)
	 */
    @Override
	public List<AbilityFacade> getPrereqAbilities(AbilityFacade abilityFacade)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Add an AbilityCategory to the list
	 * @param cat The AbilityCategory to be added.
	 */
	public void addAbilityCategory(AbilityCategory cat)
	{
		abilityMap.putValue(cat, null);
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getAlignments()
	 */
    @Override
	public ListFacade<PCAlignment> getAlignments()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getCampaigns()
	 */
    @Override
	public ListFacade<CampaignFacade> getCampaigns()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getClasses()
	 */
    @Override
	public ListFacade<ClassFacade> getClasses()
	{
		return classes;
	}

	/**
	 * Add a class to the list of classes
	 * @param cls The PCClass to be added.
	 */
	public void addClass(PCClass cls)
	{
		classes.addElement(cls);
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getDeities()
	 */
    @Override
	public ListFacade<DeityFacade> getDeities()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getDomains()
	 */
	public ListFacade<DomainFacade> getDomains()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getEquipment()
	 */
    @Override
	public ListFacade<EquipmentFacade> getEquipment()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addEquipment(EquipmentFacade equip)
	{
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getEquipmentLocations()
	 */
    @Override
	public ListFacade<BodyStructureFacade> getEquipmentLocations()
	{
		return equipmentLoc;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getXPTableNames()
	 */
    @Override
	public ListFacade<String> getXPTableNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getCharacterTypes()
	 */
    @Override
	public ListFacade<String> getCharacterTypes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void addEquipmentLocation(BodyStructureFacade elf)
	{
		equipmentLoc.addElement(elf);
	}
	
	/**
	 * @see pcgen.core.facade.DataSetFacade#getGameMode()
	 */
    @Override
	public GameModeFacade getGameMode()
	{
		return game;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getRaces()
	 */
    @Override
	public ListFacade<RaceFacade> getRaces()
	{
		return races;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getSkills()
	 */
    @Override
	public ListFacade<SkillFacade> getSkills()
	{
		return skills;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getSpeakLanguageSkill()
	 */
    @Override
	public SkillFacade getSpeakLanguageSkill()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getStatGenerators()
	 */
    @Override
	public ListFacade<StatGenerationFacade> getStatGenerators()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getStats()
	 */
    @Override
	public ListFacade<StatFacade> getStats()
	{
		return stats;
	}

	/**
	 * @see pcgen.core.facade.DataSetFacade#getTemplates()
	 */
    @Override
	public ListFacade<TemplateFacade> getTemplates()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListFacade<GearBuySellFacade> getGearBuySellSchemes()
	{
		return gearBuySellSchemes;
	}

	@Override
	public ListFacade<KitFacade> getKits()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshEquipment()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ListFacade<SizeAdjustmentFacade> getSizes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>> getAbilities()
	{
		return abilityMap;
	}

}
