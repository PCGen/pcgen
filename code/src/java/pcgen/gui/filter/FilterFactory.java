/*
 * FilterFactory.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on February 12, 2002, 13:30 PM
 */
package pcgen.gui.filter;

import pcgen.core.*;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import java.util.*;

/**
 * <code>FilterFactory</code>
 * <br>
 * Factory class for creating standard PObjectFilter objects
 * 
 * Wherever possible, this factory will hand out references to shared 
 * PObjectFilter instances
 *
 * @author Thomas Behr
 * @version $Revision$
 */
public final class FilterFactory implements FilterConstants
{
	private static List campaignFilters = new ArrayList();
	private static List classFilters = new ArrayList();
	private static List deityFilters = new ArrayList();
	private static List equipmentFilters = new ArrayList();
	private static List featFilters = new ArrayList();
	private static List prereqAlignmentFilters = new ArrayList();
	private static List raceFilters = new ArrayList();
	private static List sizeFilters = new ArrayList();
	private static List skillFilters = new ArrayList();
	private static List sourceFilters = new ArrayList();
	private static List spellFilters = new ArrayList();
	private static Map filterSettings = new HashMap();

	/**
	 * clears the filter cache for updates/re-initialization
	 * of filter settings
	 *
	 * <br>author: Thomas Behr 07-03-02
	 */
	public static void clearFilterCache()
	{
		campaignFilters.clear();
		classFilters.clear();
		deityFilters.clear();
		equipmentFilters.clear();
		featFilters.clear();
		prereqAlignmentFilters.clear();
		raceFilters.clear();
		skillFilters.clear();
		sourceFilters.clear();
		spellFilters.clear();
	}

	/**
	 * Create a PC Class Filter
	 * @return PC Class Filter
	 */
	public static PObjectFilter createPCClassFilter()
	{
		return new PCClassFilter();
	}

	/**
	 * convience method
	 *
	 * <br>author: Thomas Behr 20-02-02
	 * @param filterList
	 * @return String
	 */
	public static String filterListToString(List filterList)
	{
		Object filter;
		StringBuffer buffer = new StringBuffer();

		for (Iterator it = filterList.iterator(); it.hasNext();)
		{
			filter = it.next();
			buffer.append("[").append(filter.getClass().getName());
			buffer.append("|").append(filter.toString()).append("]");
		}

		return buffer.toString();
	}

	/** 
	 * Register all class filters
	 * @param fap
	 */
	public static void registerAllClassFilters(FilterAdapterPanel fap)
	{
		if (classFilters.size() == 0)
		{
			classFilters.add(FilterFactory.createQualifyFilter());

			// e.g. "Base", "Monster", "NPC", "PC", "Prestige" + more
			Iterator iter = Globals.getPCClassTypeList().iterator();

			while (iter.hasNext())
			{
				//All TYPEs should already be tokenized into subtypes by the "."
				String subType = (String) iter.next();
				classFilters.add(FilterFactory.createTypeFilter(subType, subType.length() > 3));

				//If 2nd param of createTypeFilter is "false" then the filter created
				//  will preserve capitalization of subtypes
				//In this case, types of 3-letters or less will be preserved (typically, "PC" and "NPC")
			}

			classFilters.add(FilterFactory.createSpellTypeFilter("Arcane"));
			classFilters.add(FilterFactory.createSpellTypeFilter("Divine"));
			classFilters.add(FilterFactory.createSpellTypeFilter("Psionic"));
		}

		for (Iterator it = classFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * Register all of the deity filters
	 * @param fap
	 */
	public static void registerAllDeityFilters(FilterAdapterPanel fap)
	{
		if (deityFilters.size() == 0)
		{
			deityFilters.add(FilterFactory.createQualifyFilter());
			deityFilters.add(FilterFactory.createPCAlignmentFilter());

			for (int i = 0; i < 9; i++)
			{
				deityFilters.add(FilterFactory.createAlignmentFilter(i));
			}

			for (Iterator it = Globals.getDomainList().iterator(); it.hasNext();)
			{
				deityFilters.add(FilterFactory.createDomainFilter((Domain) it.next()));
			}

			deityFilters.add(FilterFactory.createPantheonFilter(PantheonFilter.ALL, PantheonFilter.HIGH));

			String tmp;

			for (Iterator it = Globals.getPantheons().iterator(); it.hasNext();)
			{
				tmp = (String) it.next();

				if (!"DROW".equals(tmp.toUpperCase()))
				{
					deityFilters.add(FilterFactory.createPantheonFilter(tmp,
					        (tmp.indexOf(" (") > -1) ? PantheonFilter.HIGH : PantheonFilter.LOW));
				}
				else
				{
					deityFilters.add(FilterFactory.createPantheonFilter("Drow", PantheonFilter.HIGH));
				}
			}
		}

		for (Iterator it = deityFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * Register all of the equipment filters
	 * @param fap
	 */
	public static void registerAllEquipmentFilters(FilterAdapterPanel fap)
	{
		if (equipmentFilters.size() == 0)
		{
			equipmentFilters.add(FilterFactory.createQualifyFilter());

			equipmentFilters.add(FilterFactory.createNonMagicFilter());
			equipmentFilters.add(FilterFactory.createAffordableFilter());

			for (Iterator it = Equipment.getEquipmentTypes().iterator(); it.hasNext();)
			{
				equipmentFilters.add(FilterFactory.createTypeFilter((String) it.next()));
			}

			for (Iterator it = Globals.getWeaponTypesIterator(); it.hasNext();)
			{
				// weapon types come in pairs:
				// first UPPERCASE only, than Capitalized
				// so only register the Capitalized one
				equipmentFilters.add(FilterFactory.createWeaponFilter((String) it.next()));
			}

			equipmentFilters.add(FilterFactory.createPCSizeFilter());
		}

		for (Iterator it = equipmentFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * Register all feat filters
	 * @param fap
	 */
	public static void registerAllFeatFilters(FilterAdapterPanel fap)
	{
		if (featFilters.size() == 0)
		{
			featFilters.add(FilterFactory.createQualifyFilter());
			featFilters.add(FilterFactory.createAutomaticFeatFilter());
			featFilters.add(FilterFactory.createNormalFeatFilter());
			featFilters.add(FilterFactory.createVirtualFeatFilter());
		}

		for (Iterator it = featFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * Register all pre req alignment filters
	 * @param fap
	 */
	public static void registerAllPrereqAlignmentFilters(FilterAdapterPanel fap)
	{
		if (prereqAlignmentFilters.size() == 0)
		{
			for (int i = 0; i < 9; i++)
			{
				prereqAlignmentFilters.add(FilterFactory.createAlignmentFilter(i, ALLOWED));
				prereqAlignmentFilters.add(FilterFactory.createAlignmentFilter(i, REQUIRED));
			}

			prereqAlignmentFilters.add(FilterFactory.createPCAlignmentFilter(ALLOWED));
			prereqAlignmentFilters.add(FilterFactory.createPCAlignmentFilter(REQUIRED));
		}

		for (Iterator it = prereqAlignmentFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * Register all race filters
	 * @param fap
	 */
	public static void registerAllRaceFilters(FilterAdapterPanel fap)
	{
		if (raceFilters.size() == 0)
		{
			/*
			 * this is done locally in InfoStats, since we have to populate that combobox!
			 * author: Thomas Behr 17-02-02
			 *
			 * not anymore - we now have InfoRace, which does not have that combobox!
			 * author: Thomas Behr 04-03-02
			 */

			// ensure that the default race type filter is created!!
			Globals.getRaceTypes().add("Humanoid");

			for (Iterator it = Globals.getRaceTypes().iterator(); it.hasNext();)
			{
				raceFilters.add(FilterFactory.createTypeFilter((String) it.next()));
			}

			raceFilters.add(FilterFactory.createQualifyFilter());

			PObjectFilter filter = FilterFactory.createCompoundFilter(new TypeFilter("Base"), new TypeFilter("PC"), AND);
			PCClass aPCClass;

			for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
			{
				aPCClass = (PCClass) it.next();

				if (aPCClass.isVisible() && filter.accept(null, aPCClass))
				{
					raceFilters.add(FilterFactory.createFavoredClassFilter(aPCClass.getName()));
				}
			}

			raceFilters.add(FilterFactory.createPCTemplateFilter());
			raceFilters.add(FilterFactory.createRaceFilter());
		}

		for (Iterator it = raceFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * Register all size filters
	 * @param fap
	 */
	public static void registerAllSizeFilters(FilterAdapterPanel fap)
	{
		if (sizeFilters.size() == 0)
		{
			for (int i = 0; i < SettingsHandler.getGame().getSizeAdjustmentListSize(); i++)
			{
				sizeFilters.add(FilterFactory.createSizeFilter(i));
			}
		}

		for (Iterator it = sizeFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * Register all skill filters
	 * @param fap
	 */
	public static void registerAllSkillFilters(FilterAdapterPanel fap)
	{
		if (skillFilters.size() == 0)
		{
			skillFilters.add(FilterFactory.createUntrainedSkillFilter());
			skillFilters.add(FilterFactory.createRankFilter(0.0d));
			skillFilters.add(FilterFactory.createRankModifierFilter(0.0d));

			for (int i = 0; i < SettingsHandler.getGame().s_ATTRIBSHORT.length; i++)
			{
				skillFilters.add(FilterFactory.createStatFilter(SettingsHandler.getGame().s_ATTRIBSHORT[i]));
			}
		}

		for (Iterator it = skillFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * Register all source filters
	 * @param fap
	 */
	public static void registerAllSourceFilters(FilterAdapterPanel fap)
	{
		if (sourceFilters.size() == 0)
		{
			for (Iterator it = PersistenceManager.getInstance().getSources().iterator(); it.hasNext();)
			{
				String source = (String) it.next();
				sourceFilters.add(FilterFactory.createSourceFilter(source));
			}
		}

		List aList = new ArrayList();
		List bList = new ArrayList();

		for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
		{
			Campaign c = (Campaign) it.next();

			if (!c.getSetting().equals(""))
			{
				aList.add(FilterFactory.createSettingFilter(c.getSetting()));
			}

			if (!c.getGenre().equals(""))
			{
				bList.add(FilterFactory.createGenreFilter(c.getGenre()));
			}
		}

		sourceFilters.addAll(aList);
		sourceFilters.addAll(bList);

		for (Iterator it = sourceFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * Register all spell filters
	 * @param fap
	 */
	public static void registerAllSpellFilters(FilterAdapterPanel fap)
	{
		if (spellFilters.size() == 0)
		{
			spellFilters.add(FilterFactory.createComponentFilter("V"));
			spellFilters.add(FilterFactory.createComponentFilter("S"));
			spellFilters.add(FilterFactory.createComponentFilter("M"));
			spellFilters.add(FilterFactory.createComponentFilter("DF"));
			spellFilters.add(FilterFactory.createComponentFilter("F"));
			spellFilters.add(FilterFactory.createComponentFilter("XP"));

			for (Iterator it = Globals.getCastingTimesSet().iterator(); it.hasNext();)
			{
				spellFilters.add(FilterFactory.createCastingTimeFilter((String) it.next()));
			}

			for (Iterator it = Globals.getDescriptorSet().iterator(); it.hasNext();)
			{
				spellFilters.add(FilterFactory.createDescriptorFilter((String) it.next()));
			}

			for (Iterator it = Globals.getTargetSet().iterator(); it.hasNext();)
			{
				spellFilters.add(FilterFactory.createEffectTypeFilter((String) it.next()));
			}

			for (Iterator it = Globals.getRangesSet().iterator(); it.hasNext();)
			{
				spellFilters.add(FilterFactory.createRangeFilter((String) it.next()));
			}

			for (Iterator it = SettingsHandler.getGame().getUnmodifiableSchoolsList().iterator(); it.hasNext();)
			{
				spellFilters.add(FilterFactory.createSchoolFilter((String) it.next()));
			}

			for (Iterator it = Globals.getSrSet().iterator(); it.hasNext();)
			{
				spellFilters.add(FilterFactory.createSpellResistanceFilter((String) it.next()));
			}

			for (Iterator it = Globals.getSubschools().iterator(); it.hasNext();)
			{
				spellFilters.add(FilterFactory.createSubschoolFilter((String) it.next()));
			}
		}

		for (Iterator it = spellFilters.iterator(); it.hasNext();)
		{
			fap.registerFilter((PObjectFilter) it.next());
		}
	}

	/**
	 * reads filter.ini to restore the previous filter settings
	 * for a given Filterable
	 *
	 * @param filterable   the Filterable whose settings will be loaded/restored
	 *
	 * @return true, if the settings could be restored, false otherwise
	 */
	public static boolean restoreFilterSettings(Filterable filterable)
	{
		/*
		 * bug fix #523167
		 */
		filterable.getAvailableFilters().clear();
		filterable.getSelectedFilters().clear();
		filterable.getRemovedFilters().clear();

		/*
		 * initialize standard filters
		 */
		filterable.initializeFilters();

		String name = filterable.getName();

		if (name == null)
		{
			return false;
		}

		try
		{
			filterable.setFilterMode(Integer.parseInt(SettingsHandler.retrieveFilterSettings(name + ".mode")));
		}
		catch (NumberFormatException ex)
		{
			filterable.setFilterMode(FilterConstants.MATCH_ALL);
		}

		filterSettings.clear();

		List customAvailable = preprocessFilterList("available",
			    SettingsHandler.retrieveFilterSettings(name + ".available"));
		List customSelected = preprocessFilterList("selected",
			    SettingsHandler.retrieveFilterSettings(name + ".selected"));
		List customRemoved = preprocessFilterList("removed", SettingsHandler.retrieveFilterSettings(name + ".removed"));

		/*
		 * move the filters to the appropriate list
		 */
		Object filter;
		String listType;

		for (Iterator it = filterable.getAvailableFilters().iterator(); it.hasNext();)
		{
			filter = it.next();
			listType = (String) filterSettings.get(filter.toString());

			if ((listType == null) || ("available".equals(listType)))
			{
				// this is our default case
				// do nothing - leave the filter in the available list
			}
			else if ("selected".equals(listType))
			{
				it.remove();
				filterable.getSelectedFilters().add(filter);
			}
			else if ("removed".equals(listType))
			{
				it.remove();
				filterable.getRemovedFilters().add(filter);
			}
		}

		/*
		 * restore the custom filters
		 */
		FilterParser fp = new FilterParser(new List[]
			    {
				    filterable.getAvailableFilters(), filterable.getSelectedFilters(), filterable.getRemovedFilters()
			    });

		parseCustomFilterList(fp, filterable.getAvailableFilters(), customAvailable);
		parseCustomFilterList(fp, filterable.getSelectedFilters(), customSelected);
		parseCustomFilterList(fp, filterable.getRemovedFilters(), customRemoved);

		filterable.refreshFiltering();

		return (filterable.getAvailableFilters().size() + filterable.getSelectedFilters().size()) > 0;
	}

	/*
	 * #################################################################
	 * factory methods
	 *   all the factory methods are named according to the following
	 *   scheme:
	 *   "create" + <filter class name> + "(" + <arguments>+ ")"
	 *   where <filter class name> refers to the class name of
	 *   the created PObjectFilter.
	 *   for each PObjectFilter factory method there MUST be one
	 *   public PObjectFilter method called:
	 *   "create" + <filter class name> + "(String filterDefinition)"
	 *   this is needed for reflection method calls!
	 *
	 *   if PObjectFilters are defined locally in Filterables,
	 *   there MUST be one public PObjectFilter method called
	 *   "create" + <filter class name> + "()"
	 *   again, this is needed for reflection method calls!
	 * #################################################################
	 */
	/*
	 * #################################################################
	 * general factory methods
	 * #################################################################
	 */
	static PObjectFilter createCompoundFilter(PObjectFilter filter1, PObjectFilter filter2, String connect)
	{
		return new CompoundFilter(filter1, filter2, connect);
	}

	static PObjectFilter createInverseFilter(PObjectFilter filter)
	{
		return new InverseFilter(filter);
	}

	static PObjectFilter createNamedFilter(PObjectFilter filter, String name, String description)
	{
		return new NamedFilter(filter, name, description);
	}

	/*
	 * #################################################################
	 * inventory tab factory methods
	 * #################################################################
	 */
	private static PObjectFilter createAffordableFilter()
	{
		return new AffordableFilter();
	}

	/*
	 * #################################################################
	 * domain tab factory methods
	 * #################################################################
	 */
	private static PObjectFilter createAlignmentFilter(int alignment)
	{
		return new DeityAlignmentFilter(alignment);
	}

	/*
	 * #################################################################
	 * stats tab factory methods
	 * #################################################################
	 */
	private static PObjectFilter createAlignmentFilter(int alignment, String mode)
	{
		return new DeityAlignmentFilter(alignment, mode);
	}

	/*
	 * #################################################################
	 * feats tab factory methods
	 * #################################################################
	 */
	private static PObjectFilter createAutomaticFeatFilter()
	{
		return new AutomaticFeatFilter();
	}

	/*
	 * #################################################################
	 * spell tab factory methods
	 * #################################################################
	 */
	private static PObjectFilter createCastingTimeFilter(String castingTime)
	{
		return new CastingTimeFilter(castingTime);
	}

	private static PObjectFilter createComponentFilter(String component)
	{
		return new ComponentFilter(component);
	}

	private static PObjectFilter createDescriptorFilter(String descriptor)
	{
		return new DescriptorFilter(descriptor);
	}

	private static PObjectFilter createDomainFilter(Domain domain)
	{
		return new DomainFilter(domain);
	}

	private static PObjectFilter createEffectTypeFilter(String effect)
	{
		return new EffectTypeFilter(effect);
	}

	private static PObjectFilter createFavoredClassFilter(String className)
	{
		return new FavoredClassFilter(className);
	}

	private static PObjectFilter createGenreFilter(String genre)
	{
		return new GenreFilter(genre);
	}

	private static PObjectFilter createNonMagicFilter()
	{
		return new NonMagicFilter();
	}

	private static PObjectFilter createNormalFeatFilter()
	{
		return new NormalFeatFilter();
	}

	private static PObjectFilter createPCAlignmentFilter()
	{
		return new PCAlignmentFilter();
	}

	private static PObjectFilter createPCAlignmentFilter(String mode)
	{
		return new PCAlignmentFilter(mode);
	}

	private static PObjectFilter createPCSizeFilter()
	{
		return new PCSizeFilter();
	}

	private static PObjectFilter createPCTemplateFilter()
	{
		return new PCTemplateFilter();
	}

	private static PObjectFilter createPantheonFilter(String race, int detailLevel)
	{
		return new PantheonFilter(race, detailLevel);
	}

	private static PObjectFilter createQualifyFilter()
	{
		return new QualifyFilter();
	}

	private static PObjectFilter createRaceFilter()
	{
		return new RaceFilter();
	}

	private static PObjectFilter createRangeFilter(String range)
	{
		return new RangeFilter(range);
	}

	/*
	 * #################################################################
	 * skill tab factory methods
	 * #################################################################
	 */
	private static PObjectFilter createRankFilter(double min)
	{
		return new RankFilter(min);
	}

	private static PObjectFilter createRankModifierFilter(double min)
	{
		return new RankModifierFilter(min);
	}

	private static PObjectFilter createSchoolFilter(String school)
	{
		return new SchoolFilter(school);
	}

	private static PObjectFilter createSettingFilter(String setting)
	{
		return new SettingFilter(setting);
	}

	private static PObjectFilter createSizeFilter(int size)
	{
		return new SizeFilter(size);
	}

	private static PObjectFilter createSourceFilter(String source)
	{
		return new SourceFilter(source, SourceFilter.LOW);
	}

	private static PObjectFilter createSpellResistanceFilter(String sr)
	{
		return new SpellResistanceFilter(sr);
	}

	/*
	 * #################################################################
	 * classes tab factory methods
	 * #################################################################
	 */
	private static PObjectFilter createSpellTypeFilter(String type)
	{
		return new SpellTypeFilter(type);
	}

	private static PObjectFilter createStatFilter(String stat)
	{
		return new StatFilter(stat);
	}

	private static PObjectFilter createSubschoolFilter(String school)
	{
		return new SubschoolFilter(school);
	}

	private static PObjectFilter createTypeFilter(String type)
	{
		return new TypeFilter(type);
	}

	private static PObjectFilter createTypeFilter(String type, boolean capitalize)
	{
		return new TypeFilter(type, capitalize);
	}

	private static PObjectFilter createUntrainedSkillFilter()
	{
		return new UntrainedSkillFilter();
	}

	private static PObjectFilter createVirtualFeatFilter()
	{
		return new VirtualFeatFilter();
	}

	private static PObjectFilter createWeaponFilter(String type)
	{
		return new WeaponFilter(type);
	}

	/**
	 * restore custom filters
	 *
	 * <br>author: Thomas Behr
	 *
	 * @param parser              the parser used to recreate the filter
	 * @param filterList          the list in which to store the filter
	 * @param filterDefinitions   a list with all the difining strings of the filters to recreate
	 */
	private static void parseCustomFilterList(FilterParser parser, List filterList, List filterDefinitions)
	{
		PObjectFilter filter;

		String token;
		String[] filterData;
		StringTokenizer tokens;
		StringBuffer filterDefinition;

		for (Iterator it = filterDefinitions.iterator(); it.hasNext();)
		{
			filterData = (String[]) it.next();
			filterDefinition = new StringBuffer();
			tokens = new StringTokenizer(filterData[0], "()", true);

			while (tokens.hasMoreTokens())
			{
				token = tokens.nextToken();

				if (FilterParser.isLegalToken(token.trim()))
				{
					filterDefinition.append(token);
				}
				else
				{
					filterDefinition.append("[");
					filterDefinition.append(token);
					filterDefinition.append("]");
				}
			}

			try
			{
				filter = parser.parse(filterDefinition.toString());

				if ((filterData[1] + filterData[2]).length() > 0)
				{
					filter = FilterFactory.createNamedFilter(filter, filterData[1], filterData[2]);
				}

				filterList.add(filter);
			}
			catch (FilterParseException ex)
			{
				//Shouldn't something more be done here?
				Logging.errorPrint("Error in FilterFactory::parseCustomFilterList", ex);
			}
		}
	}

	/**
	 * process filterString to determine into which list a filter shall be stored
	 * according to the previously saved filter settings
	 *
	 * <br>author: Thomas Behr 01-03-02
	 *
	 * @param list           the list to store the newly restored filters
	 * @param filterString   the property string from which the filters will be restored
	 *
	 * @return a list containing the definitions of yet to be restored custom filters
	 */
	private static List preprocessFilterList(String list, String filterString)
	{
		List customFilters = new ArrayList();

		if (filterString.length() == 0)
		{
			return customFilters;
		}

		String classDef;
		String className;
		String filterName;
		String filterDesc;

		StringTokenizer tokens;

		StringTokenizer filterTokens = new StringTokenizer(filterString, "[]");

		while (filterTokens.hasMoreTokens())
		{
			tokens = new StringTokenizer(filterTokens.nextToken(), "|");
			className = tokens.nextToken();
			classDef = tokens.nextToken();
			filterName = (tokens.hasMoreTokens()) ? tokens.nextToken() : "";
			filterDesc = (tokens.hasMoreTokens()) ? tokens.nextToken() : "";

			/*
			 * case:
			 * custom filters, i.e.
			 *   pcgen.gui.filter.CompoundFilter,
			 *   pcgen.gui.filter.InverseFilter
			 *   pcgen.gui.filter.NamedFilter
			 * since custom filters rely upon the standard filters,
			 * we need to have all standard filters restored before
			 * we restore the custom filters
			 */
			if (className.endsWith("CompoundFilter") || className.endsWith("InverseFilter")
			    || className.endsWith("NamedFilter"))
			{
				customFilters.add(new String[]{ classDef, filterName, filterDesc });
			}

			/*
			 * case:
			 * standard filter
			 */
			else
			{
				filterSettings.put(classDef, list);
			}
		}

		return customFilters;
	}
}


final class PCClassFilter extends AbstractPObjectFilter
{
	PCClassFilter()
	{
		super("Object", PropertyFactory.getString("in_class"));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof PCClass)
		{
			return true;
		}

		return false;
	}
}


final class PCTemplateFilter extends AbstractPObjectFilter
{
	PCTemplateFilter()
	{
		super("Object", PropertyFactory.getString("in_template"));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof PCTemplate)
		{
			return true;
		}

		return false;
	}
}


final class RaceFilter extends AbstractPObjectFilter
{
	RaceFilter()
	{
		super("Object", PropertyFactory.getString("in_race"));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Race)
		{
			return true;
		}

		return false;
	}
}


/*
 * #################################################################
 * classes tab filters
 * #################################################################
 */
final class SpellTypeFilter extends AbstractPObjectFilter
{
	private String type;

	SpellTypeFilter(String type)
	{
		super(PropertyFactory.getString("in_spellType"), type);
		this.type = type.toUpperCase();
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof PCClass)
		{
			return ((PCClass) pObject).getSpellType().toUpperCase().equals(type);
		}

		return true;
	}
}


/*
 * #################################################################
 * domain tab filters
 * #################################################################
 */
abstract class AlignmentFilter extends AbstractPObjectFilter
{
	protected String mode;

	protected AlignmentFilter(String argCategory, String argName)
	{
		super(argCategory, argName);
	}

	protected boolean passesAlignmentPrereqs(PObject pObject, int alignment)
	{
		StringBuffer prealign;

		if ("allowed".equals(mode))
		{
			prealign = new StringBuffer("0");

			for (int i = 1; i < SettingsHandler.getGame().getUnmodifiableAlignmentList().size(); i++)
			{
				prealign.append(",").append(i);
			}
		}
		else if ("required".equals(mode))
		{
			prealign = new StringBuffer("");
		}
		else
		{
			return false;
		}


		for (int it = 0; it < pObject.getPreReqCount(); it++)
		{
			Prerequisite tmp = pObject.getPreReq(it);
			if ("ALIGN".equalsIgnoreCase( tmp.getKind() ))
			{
				prealign = new StringBuffer(tmp.getKey() );

				break;
			}
		}

		if (prealign.toString().indexOf(Integer.toString(alignment)) > -1)
		{
			return true;
		}

		return false;
	}
}


final class DeityAlignmentFilter extends AlignmentFilter
{
	private int alignment;

	DeityAlignmentFilter(int alignment)
	{
		this(alignment, "default");
	}

	DeityAlignmentFilter(int alignment, String mode)
	{
		super(PropertyFactory.getString("in_alignLabel"), SettingsHandler.getGame().getLongAlignmentAtIndex(alignment));
		this.alignment = alignment;
		this.mode = mode;
	}

	public String getCategory()
	{
		if ("default".equals(mode))
		{
			return super.getCategory();
		}
		return super.getCategory() + " (" + mode + ')';
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Deity)
		{
			String deityAlign = ((Deity) pObject).getAlignment();

			if (deityAlign.equals(SettingsHandler.getGame().getShortAlignmentAtIndex(alignment))
			    || deityAlign.equals(SettingsHandler.getGame().getLongAlignmentAtIndex(alignment)))
			{
				return true;
			}

			return false;
		}
		else if (pObject instanceof PCClass)
		{
			return passesAlignmentPrereqs(pObject);
		}
		else if (pObject instanceof PCTemplate)
		{
			return passesAlignmentPrereqs(pObject);
		}
		else if (pObject instanceof Race)
		{
			return passesAlignmentPrereqs(pObject);
		}

		return true;
	}

	private boolean passesAlignmentPrereqs(PObject pObject)
	{
		return passesAlignmentPrereqs(pObject, alignment);
	}
}


final class DomainFilter extends AbstractPObjectFilter
{
	private Domain domain;

	DomainFilter(Domain domain)
	{
		super(PropertyFactory.getString("in_domains"), domain.getName());
		this.domain = domain;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if ((pObject == null) || (domain == null))
		{
			return false;
		}

		if (pObject instanceof Deity)
		{
			return ((Deity) pObject).hasDomain(domain);
		}
		else if (pObject instanceof Domain)
		{
			return true;
		}

		return true;
	}
}


final class PantheonFilter extends AbstractPObjectFilter
{
	/** ALL = PropertyFactory.getString("in_allPanth") */
	public static final String ALL = PropertyFactory.getString("in_allPanth");
	/** HIGH = 0 */
	public static final int HIGH = 0;
	/** LOW = 1 */
	public static final int LOW = 1;
	private String pantheon;
	private int detailLevel;

	PantheonFilter(String pantheon, int argDetailLevel)
	{
		super();
		this.detailLevel = argDetailLevel;
		this.pantheon = ((this.detailLevel == LOW) ? normalizePantheon(pantheon) : pantheon);
		this.pantheon = ((this.pantheon.equalsIgnoreCase(ALL)) ? ALL : pantheon);
		setCategory(PropertyFactory.getString("in_pantheon")
		    + ((detailLevel == LOW) ? (" (" + PropertyFactory.getString("in_general") + ")")
		                            : (" (" + PropertyFactory.getString("in_specific") + ")")));
		setName(this.pantheon);
		setDescription((this.pantheon.equalsIgnoreCase(ALL)) ? PropertyFactory.getString("in_acceptPantAll")
		                                                     : (PropertyFactory.getString("in_acceptPantOne")
		    + pantheon + " " + PropertyFactory.getString("in_acceptPantTwo")));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Deity)
		{
			final Deity aDeity = (Deity) pObject;

			if (pantheon.equals(ALL) && (aDeity.getPantheonList().size() == 0))
			{
				return true;
			}

			String tmp;

			for (Iterator it = aDeity.getPantheonList().iterator(); it.hasNext();)
			{
				tmp = (String) it.next();

				if (detailLevel == LOW)
				{
					tmp = normalizePantheon(tmp);
				}

				if (pantheon.startsWith(tmp))
				{
					return true;
				}
			}

			return false;

//  			return (aDeity.getPantheonList().size() == 0) || aDeity.getPantheonList().contains(pantheon);
		}

		return true;
	}

	/**
	 * see comments in GameFilter, regarding overriding private methods from the superclass - gorm
	 * @param s
	 * @return String
	 */
	private static String normalizePantheon(String s)
	{
		String work = s;

		if (work.indexOf("(") > 0)
		{
			work = (new StringTokenizer(work, "()")).nextToken().trim();
		}

		return work;
	}
}


final class PCAlignmentFilter extends AlignmentFilter
{
	PCAlignmentFilter()
	{
		this("default");
	}

	PCAlignmentFilter(String mode)
	{
		super(PropertyFactory.getString("in_alignLabel"), PropertyFactory.getString("in_pc"));
		this.mode = mode;
	}

	public String getCategory()
	{
		if ("default".equals(mode))
		{
			return super.getCategory();
		}
		return super.getCategory() + " (" + mode + ")";
	}

	public String getName(PlayerCharacter aPC)
	{
		if (aPC != null)
		{
			return super.getName(aPC) + " ("
			+ SettingsHandler.getGame().getLongAlignmentAtIndex(aPC.getAlignment()) + ")";
		}
		return super.getName(aPC);
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		int alignment = aPC.getAlignment();

		if (pObject instanceof Deity)
		{
			String deityAlign = ((Deity) pObject).getAlignment();

			if (deityAlign.equals(SettingsHandler.getGame().getShortAlignmentAtIndex(alignment))
			    || deityAlign.equals(SettingsHandler.getGame().getLongAlignmentAtIndex(alignment)))
			{
				return true;
			}

			return false;
		}
		else if (pObject instanceof PCClass)
		{
			return passesAlignmentPrereqs(pObject, alignment);
		}
		else if (pObject instanceof PCTemplate)
		{
			return passesAlignmentPrereqs(pObject, alignment);
		}
		else if (pObject instanceof Race)
		{
			return passesAlignmentPrereqs(pObject, alignment);
		}

		return true;
	}
}


/*
 * #################################################################
 * feat tab filters
 * #################################################################
 */
final class AutomaticFeatFilter extends AbstractPObjectFilter
{
	AutomaticFeatFilter()
	{
		super(PropertyFactory.getString("in_feats"), PropertyFactory.getString("in_Automatic"));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Ability)
		{
			return aPC.hasFeatAutomatic(pObject.getName());
		}

		return true;
	}
}


final class NormalFeatFilter extends AbstractPObjectFilter
{
	NormalFeatFilter()
	{
		super(PropertyFactory.getString("in_feats"), PropertyFactory.getString("in_Normal"));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Ability)
		{
			return aPC.hasRealFeatNamed(pObject.getName());
		}

		return true;
	}
}


/**
 * <code>VirtualFeatFilter</code> is a filter object which allows 
 * through feats that are allocated as virtual feats to the PC.
 */
final class VirtualFeatFilter extends AbstractPObjectFilter
{
	/** 
	 * Create a new VirtualFeatFilter instance. 
	 */
	VirtualFeatFilter()
	{
		super(PropertyFactory.getString("in_feats"), PropertyFactory.getString("in_Virtual"));
	}

	/**
	 * @see pcgen.gui.filter.PObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Ability)
		{
			return aPC.hasFeatVirtual(pObject.getName());
		}

		return true;
	}
}


/*
 * #################################################################
 * inventory tab filters
 * #################################################################
 */
final class AffordableFilter extends AbstractPObjectFilter
{
	AffordableFilter()
	{
		super(PropertyFactory.getString("in_miscel"), PropertyFactory.getString("in_Affordable"));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Equipment)
		{
			return ((Equipment) pObject).getCost(aPC).compareTo(aPC.getGold()) < 1;
		}

		return true;
	}
}


final class NonMagicFilter extends AbstractPObjectFilter
{
	NonMagicFilter()
	{
		super(PropertyFactory.getString("in_miscel"), PropertyFactory.getString("in_Non-Magic"));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Equipment)
		{
			return !((Equipment) pObject).isMagic();
		}

		return true;
	}
}


final class PCSizeFilter extends AbstractPObjectFilter
{
	PCSizeFilter()
	{
		super(PropertyFactory.getString("in_size"), PropertyFactory.getString("PC"));
	}

	public String getName(PlayerCharacter aPC)
	{
		String pcName = super.getName(aPC);
		if (aPC != null)
		{
			pcName += " (";
			final SizeAdjustment sizeAdj = SettingsHandler.getGame().getSizeAdjustmentAtIndex(aPC.sizeInt());
			if (sizeAdj != null)
			{
				pcName += sizeAdj.getName();
			}
			pcName += ')';
		}
		return pcName;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Equipment)
		{
			return ((Equipment) pObject).getSize().toUpperCase().equals(SettingsHandler.getGame().getSizeAdjustmentAtIndex(
			        aPC.sizeInt()).getAbbreviation());
		}

		return true;
	}
}


final class TypeFilter extends AbstractPObjectFilter
{
	private String type;

	TypeFilter(String argType)
	{
		this(argType, true);
	}

	TypeFilter(String argType, boolean capitalize)
	{
		super(PropertyFactory.getString("in_type"),
		    (capitalize) ? (argType.substring(0, 1).toUpperCase() + argType.substring(1).toLowerCase()) : argType);
		this.type = argType.toUpperCase();
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Equipment)
		{
			return pObject.isType(type);
		}
		else if (pObject instanceof PCClass)
		{
			/*
			 * removed old code due to the fact, that all PCClasses now
			 * need a type string for the new class tab to work properly
			 *
			 * author: Thomas Behr 21-02-02
			 */

//                          if (type.equals("BASE")) {
//                                  PCClass aPCClass = (PCClass)pObject;
//                                  return !aPCClass.isPrestige() && !aPCClass.isMonster();
//                          } else if (type.equals("PC")) {
//                                  return ((PCClass)pObject).isPC();
//                          }
			return pObject.isType(type);
		}
		else if (pObject instanceof Race)
		{
			return pObject.getType().toUpperCase().equals(type);
		}

		return true;
	}
}


final class WeaponFilter extends AbstractPObjectFilter
{
	private String type;

	WeaponFilter(String argType)
	{
		super(PropertyFactory.getString("in_weapon"),
		    argType.substring(0, 1).toUpperCase() + argType.substring(1).toLowerCase());
		this.type = argType.toUpperCase();
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Equipment)
		{
			return (pObject).isType(type);
		}

		return true;
	}
}


/*
 * #################################################################
 * skill tab filters
 * #################################################################
 */
final class RankFilter extends AbstractPObjectFilter
{
	private double min;

	RankFilter(double min)
	{
		super(PropertyFactory.getString("in_skills"), PropertyFactory.getString("in_rank") + " > " + min);
		this.min = min;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Skill)
		{
			/*
			 * since InfoSkills was revamped and the concept
			 * of displaying skills was changed,
			 * we have to break this clean design :-(
			 *
			 * author: Thomas Behr 21-03-02
			 */
			Skill aSkill = (Skill) pObject;

			if (aPC.getSkillKeyed(aSkill.getKeyName()) != null)
			{
				aSkill = aPC.getSkillKeyed(aSkill.getKeyName());
			}

			return aSkill.getTotalRank(aPC).doubleValue() > min;
		}

		return true;
	}
}


final class RankModifierFilter extends AbstractPObjectFilter
{
	private double min;

	RankModifierFilter(double min)
	{
		super(PropertyFactory.getString("in_skills"),
		    PropertyFactory.getString("in_rank") + " + " + PropertyFactory.getString("in_modifier") + " > " + min);
		this.min = min;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Skill)
		{
			/*
			 * since InfoSkills was revamped and the concept
			 * of displaying skills was changed,
			 * we have to break this clean design :-(
			 *
			 * author: Thomas Behr 21-03-02
			 */
			Skill aSkill = (Skill) pObject;

			if (aPC.getSkillKeyed(aSkill.getKeyName()) != null)
			{
				aSkill = aPC.getSkillKeyed(aSkill.getKeyName());
			}

			return (aSkill.getTotalRank(aPC).doubleValue() + aSkill.modifier(aPC).doubleValue()) > min;
		}

		return true;
	}
}


final class StatFilter extends AbstractPObjectFilter
{
	StatFilter(String stat)
	{
		super(PropertyFactory.getString("in_keyAbility"), stat.toUpperCase());
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Skill)
		{
			return ((Skill) pObject).getKeyStat().toUpperCase().equals(getName(aPC));
		}

		return true;
	}
}


final class UntrainedSkillFilter extends AbstractPObjectFilter
{
	UntrainedSkillFilter()
	{
		super(PropertyFactory.getString("in_skills"), PropertyFactory.getString("in_untrained"));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Skill)
		{
			return ((Skill) pObject).getUntrained().startsWith("Y");
		}

		return true;
	}
}


/*
 * #################################################################
 * spell tab filters
 * #################################################################
 */
final class CastingTimeFilter extends AbstractPObjectFilter
{
	private String castingTime;

	CastingTimeFilter(String argCastingTime)
	{
		super(PropertyFactory.getString("in_castingTime"), argCastingTime);
		castingTime = argCastingTime;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return ((Spell) pObject).getCastingTime().equals(castingTime);
		}

		return true;
	}
}


final class ComponentFilter extends AbstractPObjectFilter
{
	private String component;

	ComponentFilter(String argComponent)
	{
		super(PropertyFactory.getString("in_component"), argComponent);
		this.component = argComponent;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			StringTokenizer tokens = new StringTokenizer(((Spell) pObject).getComponentList());

			while (tokens.hasMoreTokens())
			{
				if (tokens.nextToken().equals(component))
				{
					return true;
				}
			}

			return false;
		}

		return true;
	}
}


final class DescriptorFilter extends AbstractPObjectFilter
{
	private String descriptor;

	DescriptorFilter(String descriptor)
	{
		super(PropertyFactory.getString("in_descriptor"), descriptor);
		this.descriptor = descriptor;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return ((Spell) pObject).getDescriptorList().contains(descriptor);
		}

		return true;
	}
}


final class EffectTypeFilter extends AbstractPObjectFilter
{
	private String effectType;

	EffectTypeFilter(String effectType)
	{
		super(PropertyFactory.getString("in_effectType"), effectType);
		this.effectType = effectType;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return ((Spell) pObject).getTarget().equals(effectType);
		}

		return true;
	}
}


final class RangeFilter extends AbstractPObjectFilter
{
	private String range;

	RangeFilter(String range)
	{
		super();
		this.range = range;
		this.range = normalizeCategory(this.range);
		this.range = normalizeRange(this.range);

		setCategory(PropertyFactory.getString("in_range"));
		setName(this.range);
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return normalizeRange(((Spell) pObject).getRange()).indexOf(range) > -1;
		}

		return true;
	}

	/*
	 * removed internationalization here
	 *
	 * author: Thomas Behr 02-09-20
	 */
	private static String normalizeCategory(String s)
	{
		String work = s.trim().toUpperCase();

		if (work.startsWith("CLOSE"))
		{
			return "Close";
		}

		if (work.startsWith("MEDIUM"))
		{
			return "Medium";
		}

		if (work.startsWith("LONG"))
		{
			return "Long";
		}

		if (work.startsWith("PERSONAL"))
		{
			return "Personal";
		}

		if (work.startsWith("TOUCH"))
		{
			return "Touch";
		}

		return s;
	}

	/*
	 * removed internationalization here
	 * see comments in GameFilter - gorm
	 *
	 * author: Thomas Behr 02-09-20
	 */
	private static String normalizeRange(String s)
	{
		String work = s.trim();
		String del;
		StringBuffer buffer;
		StringTokenizer tokens;

		if ("SEE TEXT".equals(work.toUpperCase()))
		{
			return "See text";
		}

		int ftIndex = work.indexOf("ft");
		int abbIndex = work.indexOf("'");

		while ((ftIndex + abbIndex) > -2)
		{
			if (abbIndex > -1)
			{
				work = work.substring(0, abbIndex) + " feet" + work.substring(abbIndex + 1);
			}

			if (ftIndex > -1)
			{
				int length = 2;

				if (work.indexOf("ft.") == ftIndex)
				{
					length = 3;
				}

				work = work.substring(0, ftIndex) + " feet" + work.substring(ftIndex + length);
			}

			ftIndex = work.indexOf("ft");
			abbIndex = work.indexOf("'");
		}

		int plusIndex = work.indexOf("+");

		del = "";

		if (plusIndex > -1)
		{
			buffer = new StringBuffer();
			tokens = new StringTokenizer(work, "+");

			while (tokens.hasMoreTokens())
			{
				buffer.append(del).append(tokens.nextToken());
				del = " + ";
			}

			work = buffer.toString();
		}

		int slashIndex = work.indexOf("/");

		del = "";

		if (slashIndex > -1)
		{
			buffer = new StringBuffer();
			tokens = new StringTokenizer(work, "/");

			while (tokens.hasMoreTokens())
			{
				buffer.append(del).append(tokens.nextToken());
				del = " / ";
			}

			work = buffer.toString();
		}

		int lvlIndex = work.indexOf("lvl");

		while (lvlIndex > -1)
		{
			work = work.substring(0, lvlIndex) + "level" + work.substring(lvlIndex + 3);
			lvlIndex = work.indexOf("lvl");
		}

		int touchIndex = work.indexOf("touch");

		if (touchIndex > -1)
		{
			buffer = new StringBuffer();
			tokens = new StringTokenizer(work, " ", true);

			String tokenNew;

			while (tokens.hasMoreTokens())
			{
				tokenNew = tokens.nextToken();

				if ("touch".equals(tokenNew))
				{
					tokenNew = "Touch";
				}

				buffer.append(tokenNew);
			}

			work = buffer.toString();
		}

// change "  " to " "
		if (work.length() > 0)
		{
			buffer = new StringBuffer();
			tokens = new StringTokenizer(work);

			while (tokens.hasMoreTokens())
			{
				buffer.append(tokens.nextToken()).append(" ");
			}

			work = buffer.toString().trim();
		}

		return work;
	}
}


final class SchoolFilter extends AbstractPObjectFilter
{
	private String school;

	SchoolFilter(String school)
	{
		super(PropertyFactory.getString("in_school"), school);
		this.school = school;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return ((Spell) pObject).getSchool().equals(school);
		}

		return true;
	}
}


final class SpellResistanceFilter extends AbstractPObjectFilter
{
	private String sr;

	SpellResistanceFilter(String sr)
	{
		super();
		this.sr = normalizeSpellResistance(sr);
		setCategory(PropertyFactory.getString("in_spellRes"));
		setName(this.sr);
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return ((Spell) pObject).getSpellResistance().indexOf(sr) > -1;
		}

		return true;
	}

	/*
	 * removed internationalization here
	 *
	 * author: Thomas Behr 02-09-20
	 */
	private static String normalizeSpellResistance(String s)
	{
		String work = s.trim().toUpperCase();

		if (work.startsWith("YES"))
		{
			return "Yes";
		}

		if (work.startsWith("NO"))
		{
			return "No";
		}

		return s;
	}
}


final class SubschoolFilter extends AbstractPObjectFilter
{
	private String school;

	SubschoolFilter(String school)
	{
		super();
		this.school = normalizeSubschool(school);
		setCategory(PropertyFactory.getString("in_subschool"));
		setName(this.school);
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return normalizeSubschool(((Spell) pObject).getSubschool()).equals(school);
		}

		return true;
	}

	/*
	 * removed internationalization here
	 *
	 * author: Thomas Behr 02-09-20
	 */
	private static String normalizeSubschool(String s)
	{
		String work = s.trim().toUpperCase();

		if ("NO".equals(work) || "NONE".equals(work))
		{
			return "None";
		}

		return s.trim();
	}
}


/*
 * #################################################################
 * stats tab filters
 * #################################################################
 */
final class FavoredClassFilter extends AbstractPObjectFilter
{
	private String className;

	FavoredClassFilter(String className)
	{
		super(PropertyFactory.getString("in_favoredClass"), className);
		this.className = className.toUpperCase();
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Race)
		{
			return ((Race) pObject).getFavoredClass().toUpperCase().equals(className);
		}

		return true;
	}
}


final class SizeFilter extends AbstractPObjectFilter
{
	private int size;

	SizeFilter(int size)
	{
		super(PropertyFactory.getString("in_size"), SettingsHandler.getGame().getSizeAdjustmentAtIndex(size).getName());
		this.size = size;
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Equipment)
		{
			final String aEquipSize = ((Equipment) pObject).getSize();

			return aEquipSize.equals(SettingsHandler.getGame().getSizeAdjustmentAtIndex(size).getAbbreviation())
			|| aEquipSize.equals(SettingsHandler.getGame().getSizeAdjustmentAtIndex(size).getName());
		}
		else if (pObject instanceof Race)
		{
			final String aRaceSize = ((Race) pObject).getSize();

			return aRaceSize.equals(SettingsHandler.getGame().getSizeAdjustmentAtIndex(size).getAbbreviation())
			|| aRaceSize.equals(SettingsHandler.getGame().getSizeAdjustmentAtIndex(size).getName());
		}

		return true;
	}
}
