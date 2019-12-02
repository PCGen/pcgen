/*
 * Copyright 2014 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.GearBuySellFacade;
import pcgen.facade.util.AbstractMapFacade;
import pcgen.facade.util.DelegatingListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;
import pcgen.facade.util.event.MapEvent;
import pcgen.facade.util.event.MapListener;

/**
 * This class implements a {@code DataSetFacade} by delegating to another
 * DataSetFacade. This class is the DataSetFacade returned by the
 * {@code CharacterFacadeImpl} and is necessary to protect outside event
 * listeners from directly listening to the real DataSetFacade. By adding this
 * delegate layer, upon closing a character, the CFI can sever connections
 * between the DelegatingDataSet and the actual DataSetFacade thus preventing an
 * memory leaks that could occur from an outside event listener.
 */
public class DelegatingDataSet implements DataSetFacade
{

	private final DelegatingListFacade<Race> races;
	private final DelegatingListFacade<PCClass> classes;
	private final DelegatingListFacade<Skill> skills;
	private final DelegatingListFacade<Deity> deities;
	private final DelegatingListFacade<PCTemplate> templates;
	private final DelegatingListFacade<PCAlignment> alignments;
	private final DelegatingListFacade<Kit> kits;
	private final DelegatingListFacade<PCStat> stats;
	private final DelegatingAbilitiesMap abilities;
	private final DelegatingListFacade<Campaign> campaigns;
	private final DelegatingListFacade<BodyStructure> bodyStructures;
	private final DelegatingListFacade<EquipmentFacade> equipment;
	private final DelegatingListFacade<String> xpTableNames;
	private final DelegatingListFacade<GearBuySellFacade> gearBuySellSchemes;
	private final DelegatingListFacade<String> characterTypes;
	private final DelegatingListFacade<SizeAdjustment> sizes;

	private final DataSetFacade delegate;

	public DelegatingDataSet(DataSetFacade delegate)
	{
		this.delegate = delegate;
		this.abilities = new DelegatingAbilitiesMap(delegate.getAbilities());
		this.races = new DelegatingListFacade<>(delegate.getRaces());
		this.classes = new DelegatingListFacade<>(delegate.getClasses());
		this.deities = new DelegatingListFacade<>(delegate.getDeities());
		this.skills = new DelegatingListFacade<>(delegate.getSkills());
		this.templates = new DelegatingListFacade<>(delegate.getTemplates());
		this.alignments = new DelegatingListFacade<>(delegate.getAlignments());
		this.kits = new DelegatingListFacade<>(delegate.getKits());
		this.stats = new DelegatingListFacade<>(delegate.getStats());
		this.campaigns = new DelegatingListFacade<>(delegate.getCampaigns());
		this.bodyStructures = new DelegatingListFacade<>(delegate.getEquipmentLocations());
		this.equipment = new DelegatingListFacade<>(delegate.getEquipment());
		this.xpTableNames = new DelegatingListFacade<>(delegate.getXPTableNames());
		this.gearBuySellSchemes = new DelegatingListFacade<>(delegate.getGearBuySellSchemes());
		this.characterTypes = new DelegatingListFacade<>(delegate.getCharacterTypes());
		this.sizes = new DelegatingListFacade<>(delegate.getSizes());

	}

	public void detachDelegates()
	{
		abilities.detach();
		races.setDelegate(null);
		classes.setDelegate(null);
		deities.setDelegate(null);
		skills.setDelegate(null);
		templates.setDelegate(null);
		alignments.setDelegate(null);
		kits.setDelegate(null);
		stats.setDelegate(null);
		campaigns.setDelegate(null);
		bodyStructures.setDelegate(null);
		equipment.setDelegate(null);
		xpTableNames.setDelegate(null);
		gearBuySellSchemes.setDelegate(null);
		characterTypes.setDelegate(null);
		sizes.setDelegate(null);
	}

	@Override
	public MapFacade<AbilityCategory, ListFacade<AbilityFacade>> getAbilities()
	{
		return abilities;
	}

	private static class DelegatingAbilitiesMap extends AbstractMapFacade<AbilityCategory, ListFacade<AbilityFacade>>
			implements MapListener<AbilityCategory, ListFacade<AbilityFacade>>
	{

		private final Map<AbilityCategory, DelegatingListFacade<AbilityFacade>> abilitiesMap;

		private final MapFacade<AbilityCategory, ListFacade<AbilityFacade>> abilitiesDelegate;

		public DelegatingAbilitiesMap(MapFacade<AbilityCategory, ListFacade<AbilityFacade>> abilitiesDelegate)
		{
			this.abilitiesDelegate = abilitiesDelegate;
			this.abilitiesMap = new HashMap<>();
			populateMap();
			abilitiesDelegate.addMapListener(this);
		}

		public void detach()
		{
			abilitiesDelegate.removeMapListener(this);
			for (DelegatingListFacade<AbilityFacade> list : abilitiesMap.values())
			{
				list.setDelegate(null);
			}
		}

		@Override
		public Set<AbilityCategory> getKeys()
		{
			return abilitiesDelegate.getKeys();
		}

		@Override
		public ListFacade<AbilityFacade> getValue(AbilityCategory key)
		{
			return abilitiesMap.get(key);
		}

		@Override
		public void keyAdded(MapEvent<AbilityCategory, ListFacade<AbilityFacade>> e)
		{
			AbilityCategory key = e.getKey();
			DelegatingListFacade<AbilityFacade> newValue = new DelegatingListFacade<>(e.getNewValue());
			abilitiesMap.put(key, newValue);
			fireKeyAdded(this, key, newValue);
		}

		@Override
		public void keyRemoved(MapEvent<AbilityCategory, ListFacade<AbilityFacade>> e)
		{
			AbilityCategory key = e.getKey();
			DelegatingListFacade<AbilityFacade> oldValue = abilitiesMap.remove(key);
			fireKeyRemoved(this, key, oldValue);
			oldValue.setDelegate(null);
		}

		@Override
		public void keyModified(MapEvent<AbilityCategory, ListFacade<AbilityFacade>> e)
		{
			fireKeyModified(this, e.getKey(), abilitiesMap.get(e.getKey()));
		}

		@Override
		public void valueChanged(MapEvent<AbilityCategory, ListFacade<AbilityFacade>> e)
		{
			AbilityCategory key = e.getKey();
			DelegatingListFacade<AbilityFacade> oldValue = abilitiesMap.get(key);
			DelegatingListFacade<AbilityFacade> newValue = new DelegatingListFacade<>(e.getNewValue());
			abilitiesMap.put(key, newValue);
			fireValueChanged(this, key, oldValue, newValue);
			oldValue.setDelegate(null);
		}

		@Override
		public void valueModified(MapEvent<AbilityCategory, ListFacade<AbilityFacade>> e)
		{
			fireValueModified(this, e.getKey(), abilitiesMap.get(e.getKey()));
		}

		private void populateMap()
		{
			for (AbilityCategory key : abilitiesDelegate.getKeys())
			{
				DelegatingListFacade<AbilityFacade> value = new DelegatingListFacade<>(abilitiesDelegate.getValue(key));
				abilitiesMap.put(key, value);
			}
		}

		@Override
		public void keysChanged(MapEvent<AbilityCategory, ListFacade<AbilityFacade>> e)
		{
			ArrayList<DelegatingListFacade<AbilityFacade>> deadLists = new ArrayList<>(abilitiesMap.values());
			abilitiesMap.clear();
			populateMap();
			for (DelegatingListFacade<AbilityFacade> delegatingListFacade : deadLists)
			{
				delegatingListFacade.setDelegate(null);
			}
		}

	}

	@Override
	public List<AbilityFacade> getPrereqAbilities(AbilityFacade abilityFacade)
	{
		return delegate.getPrereqAbilities(abilityFacade);
	}

	@Override
	public ListFacade<Skill> getSkills()
	{
		return skills;
	}

	@Override
	public ListFacade<Race> getRaces()
	{
		return races;
	}

	@Override
	public ListFacade<PCClass> getClasses()
	{
		return classes;
	}

	@Override
	public ListFacade<Deity> getDeities()
	{
		return deities;
	}

	@Override
	public ListFacade<PCTemplate> getTemplates()
	{
		return templates;
	}

	@Override
	public ListFacade<Campaign> getCampaigns()
	{
		return campaigns;
	}

	@Override
	public GameMode getGameMode()
	{
		return delegate.getGameMode();
	}

	@Override
	public ListFacade<PCAlignment> getAlignments()
	{
		return alignments;
	}

	@Override
	public ListFacade<PCStat> getStats()
	{
		return stats;
	}

	@Override
	public Skill getSpeakLanguageSkill()
	{
		return delegate.getSpeakLanguageSkill();
	}

	@Override
	public ListFacade<EquipmentFacade> getEquipment()
	{
		return equipment;
	}

	@Override
	public void addEquipment(EquipmentFacade equip)
	{
		delegate.addEquipment(equip);
	}

	@Override
	public ListFacade<BodyStructure> getEquipmentLocations()
	{
		return bodyStructures;
	}

	@Override
	public ListFacade<String> getXPTableNames()
	{
		return xpTableNames;
	}

	@Override
	public ListFacade<String> getCharacterTypes()
	{
		return characterTypes;
	}

	@Override
	public ListFacade<GearBuySellFacade> getGearBuySellSchemes()
	{
		return gearBuySellSchemes;
	}

	@Override
	public ListFacade<Kit> getKits()
	{
		return kits;
	}

	@Override
	public ListFacade<SizeAdjustment> getSizes()
	{
		return sizes;
	}

	@Override
	public void refreshEquipment()
	{
		delegate.refreshEquipment();
	}
}
