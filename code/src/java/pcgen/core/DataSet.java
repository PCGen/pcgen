/*
 * DataSet.java
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
 * 
 * Created on Apr 27, 2010, 2:45:31 PM
 */
package pcgen.core;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.character.EquipSlot;
import pcgen.core.facade.AbilityCategoryFacade;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.AlignmentFacade;
import pcgen.core.facade.BodyStructureFacade;
import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.DataSetFacade;
import pcgen.core.facade.DeityFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.GameModeFacade;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.SkillFacade;
import pcgen.core.facade.StatFacade;
import pcgen.core.facade.TemplateFacade;
import pcgen.core.facade.generator.StatGenerationFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.LoadContext;
import pcgen.util.enumeration.View;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DataSet implements DataSetFacade
{

	private DefaultListFacade<RaceFacade> races;
	private DefaultListFacade<ClassFacade> classes;
	private DefaultListFacade<DeityFacade> deities;
	private DefaultListFacade<SkillFacade> skills;
	private DefaultListFacade<TemplateFacade> templates;
	private DefaultListFacade<AlignmentFacade> alignments;
	private DefaultListFacade<StatFacade> stats;
	private DefaultListFacade<AbilityCategoryFacade> categories;
	private Map<AbilityCategoryFacade, ListFacade<AbilityFacade>> abilityMap;
	private final LoadContext context;
	private final GameMode gameMode;
	private final ListFacade<CampaignFacade> campaigns;
	private SkillFacade speakLanguageSkill = null;
	private DefaultListFacade<BodyStructureFacade> bodyStructures;
	private DefaultListFacade<EquipmentFacade> equipment;
	private DefaultListFacade<String> xpTableNames;

	public DataSet( LoadContext context, GameMode gameMode, ListFacade<CampaignFacade> campaigns)
	{
		races = new DefaultListFacade<RaceFacade>();
		classes = new DefaultListFacade<ClassFacade>();
		deities = new DefaultListFacade<DeityFacade>();
		skills = new DefaultListFacade<SkillFacade>();
		templates = new DefaultListFacade<TemplateFacade>();
		alignments = new DefaultListFacade<AlignmentFacade>();
		stats = new DefaultListFacade<StatFacade>();
		categories = new DefaultListFacade<AbilityCategoryFacade>();
		abilityMap = new HashMap<AbilityCategoryFacade, ListFacade<AbilityFacade>>();
		bodyStructures = new DefaultListFacade<BodyStructureFacade>();
		equipment = new DefaultListFacade<EquipmentFacade>();
		xpTableNames = new DefaultListFacade<String>();
		this.context = context;
		this.gameMode = gameMode;
		this.campaigns = campaigns;
		initLists();
	}

	private void initLists()
	{
		List<Race> raceList = new ArrayList<Race>(context.ref.getConstructedCDOMObjects(Race.class));
		Collections.sort(raceList, new RaceComparator());
		races.setContents(raceList);
		
		List<PCClass> classList = new ArrayList<PCClass>(context.ref.getConstructedCDOMObjects(PCClass.class));
		Collections.sort(classList, new PCClassComparator());
		classes.setContents(classList);

		for (Skill skill : context.ref.getConstructedCDOMObjects(Skill.class))
		{
			if (skill.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE, false))
			{
				skills.addElement(skill);
			}
		}
		for (Deity deity : context.ref.getConstructedCDOMObjects(Deity.class))
		{
			deities.addElement(deity);
		}
		for (PCTemplate template : context.ref.getConstructedCDOMObjects(PCTemplate.class))
		{
			if (template.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE, false))
			{
				templates.addElement(template);
			}
		}
		for (PCAlignment alignment : context.ref.getOrderSortedCDOMObjects(PCAlignment.class))
		{
			alignments.addElement(alignment);
		}
		for (PCStat stat : context.ref.getOrderSortedCDOMObjects(PCStat.class))
		{
			stats.addElement(stat);
		}
		List<AbilityCategory> displayOrderCategories =
				new ArrayList<AbilityCategory>(
					gameMode.getAllAbilityCategories());
		Collections.sort(displayOrderCategories,
			new AbilityCategoryComparator());
		for (AbilityCategory category : displayOrderCategories)
		{
			if (category.isVisible())
			{
				categories.addElement(category);
				List<Ability> abList =
						new ArrayList<Ability>(Globals.getContext().ref
							.getManufacturer(Ability.class, category)
							.getAllObjects());
				Globals.sortPObjectListByName(abList);
				DefaultListFacade<AbilityFacade> abilityList =
						new DefaultListFacade<AbilityFacade>(abList);
				for (Iterator<AbilityFacade> iterator = abilityList.iterator(); iterator
					.hasNext();)
				{
					AbilityFacade facade = iterator.next();
					if (facade instanceof Ability)
					{
						Ability ability = (Ability) facade;
						if (!(ability.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE, false)))
						{
							iterator.remove();
						}
					}
				}
				abilityMap.put(category, abilityList);
			}
		}
		Map<String, BodyStructure> structMap =
				new HashMap<String, BodyStructure>(SystemCollections
					.getUnmodifiableBodyStructureList().size() + 3);
		for (String name : SystemCollections.getUnmodifiableBodyStructureList())
		{
			String displayName =
					name.substring(0, 1).toUpperCase()
						+ name.substring(1).toLowerCase();
			final BodyStructure bodyStructure = new BodyStructure(displayName);
			bodyStructures.addElement(bodyStructure);
			structMap.put(name, bodyStructure);
		}
		bodyStructures.addElement(new BodyStructure(Constants.EQUIP_LOCATION_EQUIPPED, true));
		bodyStructures.addElement(new BodyStructure(Constants.EQUIP_LOCATION_CARRIED, true));
		bodyStructures.addElement(new BodyStructure(Constants.EQUIP_LOCATION_NOTCARRIED, true));
		
		for (EquipSlot es : SystemCollections.getUnmodifiableEquipSlotList())
		{
			if (structMap.containsKey(es.getBodyStructureName()))
			{
				structMap.get(es.getBodyStructureName()).addEquipSlot(es);
			}
		}

		for(Equipment eq : context.ref.getConstructedCDOMObjects(Equipment.class))
		{
			equipment.addElement(eq);
		}
		for (String xpTableName : gameMode.getXPTableNames())
		{
			xpTableNames.addElement(xpTableName);
		}
	}

	public ListFacade<AbilityFacade> getAbilities(AbilityCategoryFacade category)
	{
		return abilityMap.get(category);
	}

	public ListFacade<AbilityCategoryFacade> getAbilityCategories()
	{
		return categories;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getPrereqAbilities(pcgen.core.facade.AbilityFacade)
	 */
	public List<AbilityFacade> getPrereqAbilities(AbilityFacade abilityFacade)
	{
		if (abilityFacade == null || !(abilityFacade instanceof Ability))
		{
			return Collections.emptyList();
		}

		Ability ability = (Ability) abilityFacade;
		List<AbilityFacade> prereqList = new ArrayList<AbilityFacade>();
		for (Prerequisite prereq : ability.getPrerequisiteList())
		{
			prereqList.addAll(getAbilitiesFromPrereq(prereq, ability.getCDOMCategory()));
		}
		return prereqList;
	}
	
	private List<AbilityFacade> getAbilitiesFromPrereq(Prerequisite prereq, Category<Ability> cat)
	{
		List<AbilityFacade> prereqList = new ArrayList<AbilityFacade>();
		if (prereq == null)
		{
			return prereqList;
		}

		
		if ("FEAT" == prereq.getKind()
			|| "FEAT".equalsIgnoreCase(prereq.getKind())
			|| "ABILITY" == prereq.getKind()
			|| "ABILITY".equalsIgnoreCase(prereq.getKind()))
		{
			Ability ability =
					Globals.getContext().ref
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

	public ListFacade<SkillFacade> getSkills()
	{
		return skills;
	}

	public ListFacade<RaceFacade> getRaces()
	{
		return races;
	}

	public ListFacade<ClassFacade> getClasses()
	{
		return classes;
	}

	public ListFacade<DeityFacade> getDeities()
	{
		return deities;
	}

	public ListFacade<TemplateFacade> getTemplates()
	{
		return templates;
	}

	public GameModeFacade getGameMode()
	{
		return gameMode;
	}

	public ListFacade<CampaignFacade> getCampaigns()
	{
		return campaigns;
	}

	public ListFacade<AlignmentFacade> getAlignments()
	{
		return alignments;
	}

	public ListFacade<StatFacade> getStats()
	{
		return stats;
	}

	public ListFacade<StatGenerationFacade> getStatGenerators()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public SkillFacade getSpeakLanguageSkill()
	{
		if (speakLanguageSkill != null)
		{
			return speakLanguageSkill ;
		}

		for (SkillFacade aSkillFacade : skills)
		{
			Skill aSkill = (Skill) aSkillFacade;
			if (aSkill.getSafe(StringKey.CHOICE_STRING).indexOf("Language") >= 0)
			{
				speakLanguageSkill = aSkillFacade;
			}
		}
		
		return speakLanguageSkill;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getEquipmentLocations()
	 */
	public ListFacade<BodyStructureFacade> getEquipmentLocations()
	{
		return bodyStructures;
	}

	public ListFacade<EquipmentFacade> getEquipment()
	{
		return equipment;
	}

	public ListFacade<String> getXPTableNames()
	{
		return xpTableNames;
	}


	/**
	 * The Class <code>RaceComparator</code> sorts races so that PC races come 
	 * at the top of the list, just after <None Selected>.
	 */
	class RaceComparator implements Comparator<Race>
	{

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Race r1, Race r2)
		{
		    final int BEFORE = -1;
		    final int EQUAL = 0;
		    final int AFTER = 1;

		    if (r1 == r2)
		    {
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
	 * The Class <code>PCClassComparator</code> sorts classes so that base  
	 * classes come at the top of the list.
	 */
	class PCClassComparator implements Comparator<PCClass>
	{

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(PCClass c1, PCClass c2)
		{
		    final int BEFORE = -1;
		    final int EQUAL = 0;
		    final int AFTER = 1;

		    if (c1 == c2)
		    {
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
	
	class AbilityCategoryComparator implements Comparator<AbilityCategory>
	{

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(AbilityCategory o1, AbilityCategory o2)
		{
		    final int BEFORE = -1;
		    final int EQUAL = 0;
		    final int AFTER = 1;
		    final String ORDER[] = {"FEATS", "RACIAL ABILITIES", "TRAITS", "CLASS ABILITIES"};

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
}
