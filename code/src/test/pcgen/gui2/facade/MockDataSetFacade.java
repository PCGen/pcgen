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
import pcgen.core.BodyStructure;
import pcgen.core.GameMode;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.facade.core.AbilityCategoryFacade;
import pcgen.facade.core.AbilityFacade;
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

	private DefaultListFacade<BodyStructure> equipmentLoc;
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
	 * @see pcgen.facade.core.DataSetFacade#getPrereqAbilities(AbilityFacade)
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
	 * @see pcgen.facade.core.DataSetFacade#getAlignments()
	 */
    @Override
	public ListFacade<PCAlignment> getAlignments()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getCampaigns()
	 */
    @Override
	public ListFacade<CampaignFacade> getCampaigns()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getClasses()
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
	 * @see pcgen.facade.core.DataSetFacade#getDeities()
	 */
    @Override
	public ListFacade<DeityFacade> getDeities()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	public ListFacade<DomainFacade> getDomains()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getEquipment()
	 */
    @Override
	public ListFacade<EquipmentFacade> getEquipment()
	{
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * @see pcgen.facade.core.DataSetFacade#addEquipment(EquipmentFacade)
     */
	@Override
	public void addEquipment(EquipmentFacade equip)
	{
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getEquipmentLocations()
	 */
    @Override
	public ListFacade<BodyStructure> getEquipmentLocations()
	{
		return equipmentLoc;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getXPTableNames()
	 */
    @Override
	public ListFacade<String> getXPTableNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getCharacterTypes()
	 */
    @Override
	public ListFacade<String> getCharacterTypes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void addEquipmentLocation(BodyStructure elf)
	{
		equipmentLoc.addElement(elf);
	}
	
	/**
	 * @see pcgen.facade.core.DataSetFacade#getGameMode()
	 */
    @Override
	public GameModeFacade getGameMode()
	{
		return game;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getRaces()
	 */
    @Override
	public ListFacade<RaceFacade> getRaces()
	{
		return races;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getSkills()
	 */
    @Override
	public ListFacade<SkillFacade> getSkills()
	{
		return skills;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getSpeakLanguageSkill()
	 */
    @Override
	public SkillFacade getSpeakLanguageSkill()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getStatGenerators()
	 */
    @Override
	public ListFacade<StatGenerationFacade> getStatGenerators()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getStats()
	 */
    @Override
	public ListFacade<StatFacade> getStats()
	{
		return stats;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getTemplates()
	 */
    @Override
	public ListFacade<PCTemplate> getTemplates()
	{
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * @see pcgen.facade.core.DataSetFacade#getGearBuySellSchemes()
     */
	@Override
	public ListFacade<GearBuySellFacade> getGearBuySellSchemes()
	{
		return gearBuySellSchemes;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getKits()
	 */
	@Override
	public ListFacade<KitFacade> getKits()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#refreshEquipment()
	 */
	@Override
	public void refreshEquipment()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getSizes()
	 */
	@Override
	public ListFacade<SizeAdjustmentFacade> getSizes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#getAbilities()
	 */
	@Override
	public MapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>> getAbilities()
	{
		return abilityMap;
	}

	/**
	 * @see pcgen.facade.core.DataSetFacade#hasDeityDomain()
	 */
	@Override
	public boolean hasDeityDomain()
	{
		return game.hasDeityDomain();
	}

}
