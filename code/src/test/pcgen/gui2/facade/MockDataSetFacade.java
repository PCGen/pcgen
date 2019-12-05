/**
 * Copyright James Dempsey, 2010
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.facade;

import java.util.List;

import pcgen.core.AbilityCategory;
import pcgen.core.BodyStructure;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.GameMode;
import pcgen.core.Kit;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.DomainFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.GearBuySellFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultMapFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;

/**
 * The Class {@code} is ...
 * <p>
 * <br/>
 */
public class MockDataSetFacade implements DataSetFacade
{

    private DefaultListFacade<BodyStructure> equipmentLoc;
    private DefaultMapFacade<AbilityCategory, ListFacade<AbilityFacade>> abilityMap;
    private final GameMode game;
    private DefaultListFacade<Race> races;
    private DefaultListFacade<Skill> skills;
    private DefaultListFacade<PCStat> stats;
    private DefaultListFacade<GearBuySellFacade> gearBuySellSchemes;
    private DefaultListFacade<PCClass> classes;


    public MockDataSetFacade(GameMode gameMode)
    {
        this.game = gameMode;
        equipmentLoc = new DefaultListFacade<>();
        abilityMap = new DefaultMapFacade<>();
        races = new DefaultListFacade<>();
        skills = new DefaultListFacade<>();
        stats = new DefaultListFacade<>();
        gearBuySellSchemes = new DefaultListFacade<>();
        classes = new DefaultListFacade<>();
    }

    @Override
    public List<AbilityFacade> getPrereqAbilities(AbilityFacade abilityFacade)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Add an AbilityCategory to the list
     *
     * @param cat The AbilityCategory to be added.
     */
    public void addAbilityCategory(AbilityCategory cat)
    {
        abilityMap.putValue(cat, null);
    }

    @Override
    public ListFacade<PCAlignment> getAlignments()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListFacade<Campaign> getCampaigns()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListFacade<PCClass> getClasses()
    {
        return classes;
    }

    /**
     * Add a class to the list of classes
     *
     * @param cls The PCClass to be added.
     */
    public void addClass(PCClass cls)
    {
        classes.addElement(cls);
    }

    @Override
    public ListFacade<Deity> getDeities()
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

    @Override
    public ListFacade<BodyStructure> getEquipmentLocations()
    {
        return equipmentLoc;
    }

    @Override
    public ListFacade<String> getXPTableNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

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

    @Override
    public GameMode getGameMode()
    {
        return game;
    }

    @Override
    public ListFacade<Race> getRaces()
    {
        return races;
    }

    @Override
    public ListFacade<Skill> getSkills()
    {
        return skills;
    }

    @Override
    public Skill getSpeakLanguageSkill()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListFacade<PCStat> getStats()
    {
        return stats;
    }

    @Override
    public ListFacade<PCTemplate> getTemplates()
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
    public ListFacade<Kit> getKits()
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
    public ListFacade<SizeAdjustment> getSizes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MapFacade<AbilityCategory, ListFacade<AbilityFacade>> getAbilities()
    {
        return abilityMap;
    }
}
