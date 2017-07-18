/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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

import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.character.EquipSlot;
import pcgen.core.prereq.Prerequisite;
import pcgen.facade.core.AbilityCategoryFacade;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.AlignmentFacade;
import pcgen.facade.core.BodyStructureFacade;
import pcgen.facade.core.CampaignFacade;
import pcgen.facade.core.ClassFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.DeityFacade;
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
import pcgen.facade.util.AbstractMapFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.rules.context.LoadContext;
import pcgen.util.enumeration.View;

public class DataSet implements DataSetFacade
{

	private final DefaultListFacade<RaceFacade> races;
	private final DefaultListFacade<ClassFacade> classes;
	private final DefaultListFacade<DeityFacade> deities;
	private final DefaultListFacade<SkillFacade> skills;
	private final DefaultListFacade<TemplateFacade> templates;
	private final DefaultListFacade<AlignmentFacade> alignments;
	private final DefaultListFacade<KitFacade> kits;
	private final DefaultListFacade<StatFacade> stats;
    private final AbilityMap abilityMap;
//	private DefaultListFacade<AbilityCategoryFacade> categories;
//	private Map<AbilityCategoryFacade, ListFacade<AbilityFacade>> abilityMap;
	private final LoadContext context;
	private final GameMode gameMode;
	private final ListFacade<CampaignFacade> campaigns;
	private SkillFacade speakLanguageSkill = null;
	private final DefaultListFacade<BodyStructureFacade> bodyStructures;
	private final DefaultListFacade<EquipmentFacade> equipment;
	private final DefaultListFacade<String> xpTableNames;
	private DefaultListFacade<GearBuySellFacade> gearBuySellSchemes;
	private final DefaultListFacade<String> characterTypes;
	private final DefaultListFacade<SizeAdjustmentFacade> sizes;

	public DataSet( LoadContext context, GameMode gameMode, ListFacade<CampaignFacade> campaigns)
	{
		races = new DefaultListFacade<>();
		classes = new DefaultListFacade<>();
		deities = new DefaultListFacade<>();
		skills = new DefaultListFacade<>();
		templates = new DefaultListFacade<>();
		alignments = new DefaultListFacade<>();
		stats = new DefaultListFacade<>();
//		categories = new DefaultListFacade<AbilityCategoryFacade>();
//		abilityMap = new HashMap<AbilityCategoryFacade, ListFacade<AbilityFacade>>();
        abilityMap = new AbilityMap();
		bodyStructures = new DefaultListFacade<>();
		equipment = new DefaultListFacade<>();
		xpTableNames = new DefaultListFacade<>();
		characterTypes = new DefaultListFacade<>();
		kits = new DefaultListFacade<>();
		sizes = new DefaultListFacade<>();
		this.context = context;
		this.gameMode = gameMode;
		this.campaigns = campaigns;
		initLists();
	}

	private void initLists()
	{
		List<Race> raceList = new ArrayList<>(context.getReferenceContext().getConstructedCDOMObjects(Race.class));
		raceList.sort(new RaceComparator());
		for (Race race : raceList)
		{
			if (race.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_DISPLAY))
			{
				races.addElement(race);
			}
		}
		
		List<PCClass> classList = new ArrayList<>(context.getReferenceContext().getConstructedCDOMObjects(PCClass.class));
		classList.sort(new PCClassComparator());
		for (PCClass pcClass : classList)
		{
			if (pcClass.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_DISPLAY))
			{
				classes.addElement(pcClass);
			}
		}

		for (Skill skill : context.getReferenceContext().getConstructedCDOMObjects(Skill.class))
		{
			if (skill.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_DISPLAY))
			{
				skills.addElement(skill);
			}
		}
		for (Deity deity : context.getReferenceContext().getConstructedCDOMObjects(Deity.class))
		{
			deities.addElement(deity);
		}
		for (PCTemplate template : context.getReferenceContext().getConstructedCDOMObjects(PCTemplate.class))
		{
			if (template.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_DISPLAY))
			{
				templates.addElement(template);
			}
		}
		for (Kit kit : context.getReferenceContext().getConstructedCDOMObjects(Kit.class))
		{
			kits.addElement(kit);
		}
		for (PCAlignment alignment : context.getReferenceContext().getOrderSortedCDOMObjects(PCAlignment.class))
		{
			alignments.addElement(alignment);
		}
		for (PCStat stat : context.getReferenceContext().getOrderSortedCDOMObjects(PCStat.class))
		{
			stats.addElement(stat);
		}
//		List<AbilityCategory> displayOrderCategories =
//				new ArrayList<AbilityCategory>(
//					gameMode.getAllAbilityCategories());
//		Collections.sort(displayOrderCategories,
//			new AbilityCategoryComparator());
		for (AbilityCategory category : gameMode.getAllAbilityCategories())
		{
			if (category.isVisibleTo(View.VISIBLE_DISPLAY))
			{
//				categories.addElement(category);
				List<Ability> abList =
                        new ArrayList<>(Globals.getContext().getReferenceContext()
                                .getManufacturer(Ability.class, category)
                                .getAllObjects());
				Globals.sortPObjectListByName(abList);
				DefaultListFacade<AbilityFacade> abilityList =
                        new DefaultListFacade<>(abList);
				for (Iterator<AbilityFacade> iterator = abilityList.iterator(); iterator
					.hasNext();)
				{
					AbilityFacade facade = iterator.next();
					if (facade instanceof Ability)
					{
						Ability ability = (Ability) facade;
						if (!(ability.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_DISPLAY)))
						{
							iterator.remove();
						}
					}
				}
				abilityMap.put(category, abilityList);
			}
		}
		Map<String, BodyStructure> structMap =
                new HashMap<>(SystemCollections
                        .getUnmodifiableBodyStructureList().size() + 3);
		for (String name : SystemCollections.getUnmodifiableBodyStructureList())
		{
			// TODO i18n the display name and correct the DataSetTest
			String displayName =
					name.substring(0, 1).toUpperCase()
						+ name.substring(1).toLowerCase();
			final BodyStructure bodyStructure = new BodyStructure(displayName);
			bodyStructures.addElement(bodyStructure);
			structMap.put(name, bodyStructure);
		}
		Set<Type> typesWithDesignatedSlots = buildSlottedTypeList();
		bodyStructures.addElement(new BodyStructure(
			Constants.EQUIP_LOCATION_EQUIPPED, true, typesWithDesignatedSlots));
		bodyStructures.addElement(new BodyStructure(Constants.EQUIP_LOCATION_CARRIED, true));
		bodyStructures.addElement(new BodyStructure(Constants.EQUIP_LOCATION_NOTCARRIED, true));
		
		for (EquipSlot es : SystemCollections.getUnmodifiableEquipSlotList())
		{
			if (structMap.containsKey(es.getBodyStructureName()))
			{
				structMap.get(es.getBodyStructureName()).addEquipSlot(es);
			}
		}

		for (Equipment eq : context.getReferenceContext().getConstructedCDOMObjects(Equipment.class))
		{
			if (eq.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_DISPLAY))
			{
				equipment.addElement(eq);
			}
		}
		for (String xpTableName : gameMode.getXPTableNames())
		{
			xpTableNames.addElement(xpTableName);
		}
		for (String characterType : gameMode.getCharacterTypeList())
		{
			characterTypes.addElement(characterType);
		}
		for (SizeAdjustment size : context.getReferenceContext().getSortedList(
			SizeAdjustment.class, IntegerKey.SIZEORDER))
		{
			sizes.addElement(size);
		}
		
		createGearBuySellSchemes();
		
	}

	/**
	 * @return
	 */
	private Set<Type> buildSlottedTypeList()
	{
		Set<Type> typeList = new HashSet<>();
		for (EquipSlot es : SystemCollections.getUnmodifiableEquipSlotList())
		{
			
			for (String typeString : es.getContainType())
			{
				typeList.add(Type.getConstant(typeString));
			}
		}
		
		return typeList;
	}

	/**
	 * Create the default set of GearBuySellSchemes.
	 * TODO: This should be loaded from the game mode and allow for user additions.
	 */
	private void createGearBuySellSchemes()
	{
		BigDecimal fullPrice = new BigDecimal("100.0");
		BigDecimal halfPrice = new BigDecimal("50.0");
		BigDecimal free = BigDecimal.ZERO;
		gearBuySellSchemes = new DefaultListFacade<>();
		// TODO i18n this
		gearBuySellSchemes.addElement(new GearBuySellScheme("Market price", fullPrice, halfPrice, fullPrice));
		gearBuySellSchemes.addElement(new GearBuySellScheme("Character build", fullPrice, fullPrice, fullPrice));
		gearBuySellSchemes.addElement(new GearBuySellScheme("Cashless", free, free, free));
		gearBuySellSchemes.addElement(new GearBuySellScheme("Crafting", halfPrice, halfPrice, fullPrice));
	}

    @Override
    public MapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>> getAbilities()
    {
        return abilityMap;
    }

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getPrereqAbilities(pcgen.core.facade.AbilityFacade)
	 */
    @Override
	public List<AbilityFacade> getPrereqAbilities(AbilityFacade abilityFacade)
	{
		if (abilityFacade == null || !(abilityFacade instanceof Ability))
		{
			return Collections.emptyList();
		}

		Ability ability = (Ability) abilityFacade;
		List<AbilityFacade> prereqList = new ArrayList<>();
		for (Prerequisite prereq : ability.getPrerequisiteList())
		{
			prereqList.addAll(getAbilitiesFromPrereq(prereq, ability.getCDOMCategory()));
		}
		return prereqList;
	}
	
	private List<AbilityFacade> getAbilitiesFromPrereq(Prerequisite prereq, Category<Ability> cat)
	{
		List<AbilityFacade> prereqList = new ArrayList<>();
		// Exclude negated prereqs
		if (prereq == null || (prereq.getOperator() == PrerequisiteOperator.LT && "1".equals(prereq.getOperand())))
		{
			return prereqList;
		}

		
		if ("FEAT" == prereq.getKind()
			|| "FEAT".equalsIgnoreCase(prereq.getKind())
			|| "ABILITY" == prereq.getKind()
			|| "ABILITY".equalsIgnoreCase(prereq.getKind()))
		{
			Ability ability =
					Globals.getContext().getReferenceContext()
						.getManufacturer(Ability.class, cat).getObject(
							prereq.getKey());
			if (ability != null)
			{
				prereqList.add(ability);
			}
		}
		
		for (Prerequisite childPrereq : prereq.getPrerequisites())
		{
			prereqList.addAll(getAbilitiesFromPrereq(childPrereq, cat));
		}
		
		return prereqList;
	}

    @Override
	public ListFacade<SkillFacade> getSkills()
	{
		return skills;
	}

    @Override
	public ListFacade<RaceFacade> getRaces()
	{
		return races;
	}

    @Override
	public ListFacade<ClassFacade> getClasses()
	{
		return classes;
	}

    @Override
	public ListFacade<DeityFacade> getDeities()
	{
		return deities;
	}

    @Override
	public ListFacade<TemplateFacade> getTemplates()
	{
		return templates;
	}

    @Override
	public GameModeFacade getGameMode()
	{
		return gameMode;
	}

    @Override
	public ListFacade<CampaignFacade> getCampaigns()
	{
		return campaigns;
	}

    @Override
	public ListFacade<AlignmentFacade> getAlignments()
	{
		return alignments;
	}

    @Override
	public ListFacade<StatFacade> getStats()
	{
		return stats;
	}

    @Override
	public ListFacade<StatGenerationFacade> getStatGenerators()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

    @Override
	public SkillFacade getSpeakLanguageSkill()
	{
		if (speakLanguageSkill != null)
		{
			return speakLanguageSkill ;
		}

		for (SkillFacade aSkillFacade : skills)
		{
			Skill aSkill = (Skill) aSkillFacade;
			ChooseInformation<?> chooseInfo = aSkill.get(ObjectKey.CHOOSE_INFO);
			if ((chooseInfo != null) && "LANG".equals(chooseInfo.getName()))
			{
				speakLanguageSkill = aSkillFacade;
			}
		}
		
		return speakLanguageSkill;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getEquipmentLocations()
	 */
    @Override
	public ListFacade<BodyStructureFacade> getEquipmentLocations()
	{
		return bodyStructures;
	}

    @Override
	public ListFacade<EquipmentFacade> getEquipment()
	{
		return equipment;
	}

	@Override
	public void addEquipment(EquipmentFacade equip)
	{
		equipment.addElement(equip);
	}

    @Override
	public void refreshEquipment()
	{
		equipment.updateContents(new ArrayList<EquipmentFacade>(context.getReferenceContext()
			.getConstructedCDOMObjects(Equipment.class)));
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


	/**
	 * The Class {@code RaceComparator} sorts races so that PC races come
	 * at the top of the list, just after <None Selected>.
	 */
	class RaceComparator implements Comparator<Race>
	{

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
        @Override
		public int compare(Race r1, Race r2)
		{
		    final int BEFORE = -1;
			final int AFTER = 1;

		    if (r1 == r2)
		    {
			    final int EQUAL = 0;
			    return EQUAL;
		    }

		    final String NONE_SELECTED = "<none selected>";
			if (r1.getKeyName().equals(NONE_SELECTED)
				&& !r2.getKeyName().equals(NONE_SELECTED))
			{
				return BEFORE;
			}
			if (!r1.getKeyName().equals(NONE_SELECTED)
				&& r2.getKeyName().equals(NONE_SELECTED))
			{
				return AFTER;
			}
		    	
		    final String PC_TYPE = "PC";
			if (r1.isType(PC_TYPE) && !r2.isType(PC_TYPE))
			{
				return BEFORE;
			}
			if (!r1.isType(PC_TYPE) && r2.isType(PC_TYPE))
			{
				return AFTER;
			}
	    	
		    final String HUMANOID = "Humanoid";
			if (r1.isType(HUMANOID) && !r2.isType(HUMANOID))
			{
				return BEFORE;
			}
			if (!r1.isType(HUMANOID) && r2.isType(HUMANOID))
			{
				return AFTER;
			}
			
			
			// Check sort keys 
			String key1 = r1.get(StringKey.SORT_KEY);
			if (key1 == null)
			{
				key1 = r1.getDisplayName();
			}
			String key2 = r2.get(StringKey.SORT_KEY);
			if (key2 == null)
			{
				key2 = r2.getDisplayName();
			}
			final Collator collator = Collator.getInstance();
			return collator.compare(key1, key2);
		}
		
	}
	
	/**
	 * The Class {@code PCClassComparator} sorts classes so that base
	 * classes come at the top of the list.
	 */
	class PCClassComparator implements Comparator<PCClass>
	{

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
        @Override
		public int compare(PCClass c1, PCClass c2)
		{
		    final int BEFORE = -1;
			final int AFTER = 1;

		    if (c1 == c2)
		    {
			    final int EQUAL = 0;
			    return EQUAL;
		    }
		    	
		    final String BASE_TYPE = "BASE.PC";
			if (c1.isType(BASE_TYPE) && !c2.isType(BASE_TYPE))
			{
				return BEFORE;
			}
			if (!c1.isType(BASE_TYPE) && c2.isType(BASE_TYPE))
			{
				return AFTER;
			}
	    	
		    final String PRESTIGE_TYPE = "Prestige";
			if (c1.isType(PRESTIGE_TYPE) && !c2.isType(PRESTIGE_TYPE))
			{
				return BEFORE;
			}
			if (!c1.isType(PRESTIGE_TYPE) && c2.isType(PRESTIGE_TYPE))
			{
				return AFTER;
			}
	    	
		    final String NPC_TYPE = "NPC";
			if (c1.isType(NPC_TYPE) && !c2.isType(NPC_TYPE))
			{
				return BEFORE;
			}
			if (!c1.isType(NPC_TYPE) && c2.isType(NPC_TYPE))
			{
				return AFTER;
			}
			
			// Check sort keys 
			String key1 = c1.get(StringKey.SORT_KEY);
			if (key1 == null)
			{
				key1 = c1.getDisplayName();
			}
			String key2 = c2.get(StringKey.SORT_KEY);
			if (key2 == null)
			{
				key2 = c2.getDisplayName();
			}
			final Collator collator = Collator.getInstance();
			return collator.compare(key1, key2);
		}
		
	}
	class AbilityMap extends AbstractMapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>>
    {

        private final TreeMap<AbilityCategoryFacade, ListFacade<AbilityFacade>> map;

        AbilityMap()
        {
            map = new TreeMap<>(new AbilityCategoryComparator());
        }
        
        @Override
        public Set<AbilityCategoryFacade> getKeys()
        {
            return Collections.unmodifiableSet(map.keySet());
        }

        @Override
        public ListFacade<AbilityFacade> getValue(AbilityCategoryFacade key)
        {
            return map.get(key);
        }

        public void put(AbilityCategoryFacade key, ListFacade<AbilityFacade> value)
        {
            map.put(key, value);
        }
        
    }
	class AbilityCategoryComparator implements Comparator<AbilityCategoryFacade>
	{

		@Override
		public int compare(AbilityCategoryFacade f1, AbilityCategoryFacade f2)
		{
            AbilityCategory o1 = (AbilityCategory) f1;
            AbilityCategory o2 = (AbilityCategory) f2;
		    final int BEFORE = -1;
		    final int EQUAL = 0;
		    final int AFTER = 1;

			if (o1 == o2)
		    {
		    	return EQUAL;
		    }
		    
		    String ac1Key = o1.getKeyName();
		    String ac2Key = o2.getKeyName();
		    String ac1Display = o1.getDisplayLocation().toString().toUpperCase();
		    String ac2Display = o2.getDisplayLocation().toString().toUpperCase();

		    if (ac1Display == null && ac2Display != null)
		    {
		    	return AFTER;
		    }
		    if (ac1Display != null && ac2Display == null)
		    {
		    	return BEFORE;
		    }
		    if ((ac1Display != null && ac2Display != null) && !ac1Display.equals(ac2Display))
		    {
			    final String[] ORDER =
					    {"FEATS", "RACIAL ABILITIES", "TRAITS", "CLASS ABILITIES"};
			    for (String displayOrder : ORDER)
				{
					if (ac1Display.equals(displayOrder))
					{
						return BEFORE;
					}
					if (ac2Display.equals(displayOrder))
					{
						return AFTER;
					}
				}
		    	return ac1Display.compareTo(ac2Display);
		    }

		    if (ac1Key == null && ac2Key != null)
		    {
		    	return AFTER;
		    }
		    if (ac1Key != null && ac2Key == null)
		    {
		    	return BEFORE;
		    }
		    if ((ac1Key == null && ac2Key == null) || ac1Key.equals(ac2Key))
		    {
		    	return EQUAL;
		    }
		    
			return ac1Key.compareTo(ac2Key);
		}
		
	}

	@Override
	public ListFacade<KitFacade> getKits()
	{
		return kits;
	}

	@Override
	public ListFacade<SizeAdjustmentFacade> getSizes()
	{
		return sizes;
	}

	@Override
	public String toString()
	{
		return "DataSet [gameMode=" +
				gameMode +
				", campaigns=" +
				campaigns +
				"]";
	}
}
