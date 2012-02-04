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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.Component;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.RaceAlignment;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Visibility;

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
	private static List<PObjectFilter> campaignFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> classFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> deityFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> equipmentFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> featFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> prereqAlignmentFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> raceFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> sizeFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> skillFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> sourceFilters = new ArrayList<PObjectFilter>();
	private static List<PObjectFilter> spellFilters = new ArrayList<PObjectFilter>();
	private static Map<String, String> filterSettings = new HashMap<String, String>();

	private static final String MODE_SETTING = "mode"; //$NON-NLS-1$
	private static final String AVAILABLE_SETTING = "available"; //$NON-NLS-1$
	private static final String SELECTED_SETTING = "selected"; //$NON-NLS-1$
	private static final String REMOVED_SETTING = "removed"; //$NON-NLS-1$
	
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
	public static String filterListToString(final List<?> filterList)
	{
		StringBuffer buffer = new StringBuffer();

		for ( final Object filter : filterList )
		{
			buffer.append('[').append(filter.getClass().getName());
			buffer.append(Constants.PIPE).append(filter.toString()).append(']');
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

			Set<String> typeSet = new HashSet<String>();
			for (PCClass cl : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class))
			{
				for (Type t : cl.getTrueTypeList(false))
				{
					typeSet.add(t.toString());
				}
			}
			// e.g. "Base", "Monster", "NPC", "PC", "Prestige" + more
			for ( final String subType : typeSet )
			{
				//All TYPEs should already be tokenized into subtypes by the "."
				classFilters.add(FilterFactory.createTypeFilter(subType, subType.length() > 3));

				//If 2nd param of createTypeFilter is "false" then the filter created
				//  will preserve capitalization of subtypes
				//In this case, types of 3-letters or less will be preserved (typically, "PC" and "NPC")
			}

			// TODO - Create Globals.getAllSpellTypes()
			classFilters.add(FilterFactory.createSpellTypeFilter("Arcane")); //$NON-NLS-1$
			classFilters.add(FilterFactory.createSpellTypeFilter("Divine")); //$NON-NLS-1$
			classFilters.add(FilterFactory.createSpellTypeFilter("Psionic")); //$NON-NLS-1$
		}

		for ( final PObjectFilter filter : classFilters )
		{
			fap.registerFilter(filter);
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

			for (PCAlignment align : Globals.getContext().ref.getOrderSortedCDOMObjects(PCAlignment.class))
			{
				deityFilters.add(FilterFactory.createAlignmentFilter(align));
			}

			for ( final Domain domain : Globals.getContext().ref.getConstructedCDOMObjects(Domain.class) )
			{
				deityFilters.add(FilterFactory.createDomainFilter(domain));
			}

			deityFilters.add(FilterFactory.createPantheonFilter(Constants.LST_ALL, PantheonFilter.Detail.HIGH));

			for ( final Pantheon pantheon : Pantheon.getAllConstants() )
			{
				// TODO - What are we doing with the indexOf " ("??
				deityFilters.add(FilterFactory.createPantheonFilter(pantheon.toString(),
							(pantheon.toString().indexOf(" (") > -1)  //$NON-NLS-1$
							? PantheonFilter.Detail.HIGH 
							: PantheonFilter.Detail.LOW));
			}
		}

		for ( final PObjectFilter filter : deityFilters )
		{
			fap.registerFilter(filter);
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

			for ( final String type : Globals.getEquipmentTypes() )
			{
				equipmentFilters.add(FilterFactory.createTypeFilter(type));
			}

			for (final String s : SettingsHandler.getGame().getContext().getTypes(WeaponProf.class))
			{
				// weapon types come in pairs:
				// first UPPERCASE only, than Capitalized
				// so only register the Capitalized one
				equipmentFilters.add(FilterFactory.createWeaponFilter(s));
			}

			equipmentFilters.add(FilterFactory.createPCSizeFilter());
		}

		for ( final PObjectFilter filter : equipmentFilters )
		{
			fap.registerFilter(filter);
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

		for ( final PObjectFilter filter : featFilters )
		{
			fap.registerFilter(filter);
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
			for (PCAlignment align : Globals.getContext().ref.getOrderSortedCDOMObjects(PCAlignment.class))
			{
				prereqAlignmentFilters.add(FilterFactory.createAlignmentFilter(align, AlignmentFilter.Mode.ALLOWED));
				prereqAlignmentFilters.add(FilterFactory.createAlignmentFilter(align, AlignmentFilter.Mode.REQUIRED));
			}

			prereqAlignmentFilters.add(FilterFactory.createPCAlignmentFilter(AlignmentFilter.Mode.ALLOWED));
			prereqAlignmentFilters.add(FilterFactory.createPCAlignmentFilter(AlignmentFilter.Mode.REQUIRED));
		}

		for ( final PObjectFilter filter : prereqAlignmentFilters )
		{
			fap.registerFilter(filter);
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
			raceFilters.add(FilterFactory.createQualifyFilter());

			// Create filters for race type and sub type
			for (RaceType raceType : RaceType.getAllConstants())
			{
				raceFilters.add(FilterFactory.createRaceTypeFilter(raceType));
			}
			for (RaceSubType raceSubType : RaceSubType.getAllConstants())
			{
				raceFilters.add(FilterFactory.createRaceSubTypeFilter(raceSubType));
			}
			
			// Create a favored class filter for each visible Base.PC class.
			// TODO - Fix this hardcoding
			PObjectFilter filter = FilterFactory.createCompoundFilter(new TypeFilter("Base"), new TypeFilter("PC"), AND); //$NON-NLS-1$ //$NON-NLS-2$

			for ( final PCClass pcClass : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class) )
			{
				if (pcClass.getSafe(ObjectKey.VISIBILITY).equals(Visibility.DEFAULT) && filter.accept(null, pcClass))
				{
					raceFilters.add(FilterFactory.createFavoredClassFilter(pcClass.getKeyName()));
				}
			}

			raceFilters.add(FilterFactory.createPCTemplateFilter());
			raceFilters.add(FilterFactory.createRaceFilter());
		}

		for ( final PObjectFilter filter : raceFilters )
		{
			fap.registerFilter(filter);
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
			for (SizeAdjustment sadj : Globals.getContext().ref.getOrderSortedCDOMObjects(SizeAdjustment.class))
			{
				sizeFilters.add(FilterFactory.createSizeFilter(sadj));
			}
		}

		for ( final PObjectFilter filter : sizeFilters )
		{
			fap.registerFilter(filter);
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

			for (PCStat stat : Globals.getContext().ref.getConstructedCDOMObjects(PCStat.class))
			{
				skillFilters.add(FilterFactory.createStatFilter(stat.getAbb()));
			}
		}

		for ( final PObjectFilter filter : skillFilters )
		{
			fap.registerFilter(filter);
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
			sourceFilters.add(FilterFactory.createQualifyFilter());
			for ( final String source : PersistenceManager.getInstance().getSources() )
			{
				sourceFilters.add(FilterFactory.createSourceFilter(source));
			}
		}

		List<PObjectFilter> settingList = new ArrayList<PObjectFilter>();
		List<PObjectFilter> genreList = new ArrayList<PObjectFilter>();

		for ( final Campaign campaign : Globals.getCampaignList() )
		{
			if (!campaign.getSafe(StringKey.SETTING).equals(Constants.EMPTY_STRING))
			{
				settingList.add(FilterFactory.createSettingFilter(campaign.getSafe(StringKey.SETTING)));
			}

			if (!campaign.getSafe(StringKey.GENRE).equals(Constants.EMPTY_STRING))
			{
				genreList.add(FilterFactory.createGenreFilter(campaign.getSafe(StringKey.GENRE)));
			}
		}

		sourceFilters.addAll(settingList);
		sourceFilters.addAll(genreList);

		for ( final PObjectFilter filter : sourceFilters )
		{
			fap.registerFilter(filter);
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
			spellFilters.add(FilterFactory.createComponentFilter(Component.VERBAL.toString()));
			spellFilters.add(FilterFactory.createComponentFilter(Component.SOMATIC.toString()));
			spellFilters.add(FilterFactory.createComponentFilter(Component.MATERIAL.toString()));
			spellFilters.add(FilterFactory.createComponentFilter(Component.DIVINEFOCUS.toString()));
			spellFilters.add(FilterFactory.createComponentFilter(Component.FOCUS.toString()));
			spellFilters.add(FilterFactory.createComponentFilter(Component.EXPERIENCE.toString()));

			for ( final String castTime : Globals.getCastingTimesSet() )
			{
				spellFilters.add(FilterFactory.createCastingTimeFilter(castTime));
			}

			for ( final String descriptor : Globals.getDescriptorSet() )
			{
				spellFilters.add(FilterFactory.createDescriptorFilter(descriptor));
			}

			for ( final String target : Globals.getTargetSet() )
			{
				spellFilters.add(FilterFactory.createEffectTypeFilter(target));
			}

			for ( final String range : Globals.getRangesSet() )
			{
				spellFilters.add(FilterFactory.createRangeFilter(range));
			}

			for ( final String school : SettingsHandler.getGame().getUnmodifiableSchoolsList() )
			{
				spellFilters.add(FilterFactory.createSchoolFilter(school));
			}

			for ( final String sr : Globals.getSrSet() )
			{
				spellFilters.add(FilterFactory.createSpellResistanceFilter(sr));
			}

			for ( final String subschool : Globals.getSubschools() )
			{
				spellFilters.add(FilterFactory.createSubschoolFilter(subschool));
			}
		}

		for ( final PObjectFilter filter : spellFilters )
		{
			fap.registerFilter(filter);
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
			filterable.setFilterMode(Integer.parseInt(SettingsHandler.retrieveFilterSettings(name + '.' + MODE_SETTING)));
		}
		catch (NumberFormatException ex)
		{
			filterable.setFilterMode(FilterConstants.MATCH_ALL);
		}

		filterSettings.clear();

		final List<String[]> customAvailable = preprocessFilterList(AVAILABLE_SETTING,
				SettingsHandler.retrieveFilterSettings(name + '.' + AVAILABLE_SETTING));
		final List<String[]> customSelected = preprocessFilterList(SELECTED_SETTING,
				SettingsHandler.retrieveFilterSettings(name + '.' + SELECTED_SETTING));
		final List<String[]> customRemoved = preprocessFilterList(REMOVED_SETTING, 
				SettingsHandler.retrieveFilterSettings(name + '.' + REMOVED_SETTING));

		/*
		 * move the filters to the appropriate list
		 */
		for (final Iterator<PObjectFilter> it = filterable.getAvailableFilters().iterator(); it.hasNext();)
		{
			final PObjectFilter filter = it.next();
			final String listType = filterSettings.get(filter.toString());

			// TODO - Change filterable to not give out references to the lists
			if ((listType == null) || (AVAILABLE_SETTING.equals(listType)))
			{
				// this is our default case
				// do nothing - leave the filter in the available list
			}
			else if (SELECTED_SETTING.equals(listType))
			{
				it.remove();
				filterable.getSelectedFilters().add(filter);
			}
			else if (REMOVED_SETTING.equals(listType))
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
	private static PObjectFilter createAlignmentFilter(PCAlignment alignment)
	{
		return new DeityAlignmentFilter(alignment);
	}

	/*
	 * #################################################################
	 * stats tab factory methods
	 * #################################################################
	 */
	private static PObjectFilter createAlignmentFilter(PCAlignment alignment, AlignmentFilter.Mode mode)
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

	private static PObjectFilter createPCAlignmentFilter(final AlignmentFilter.Mode mode)
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

	private static PObjectFilter createPantheonFilter(String race, PantheonFilter.Detail detailLevel)
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

	private static PObjectFilter createRaceSubTypeFilter(RaceSubType subType)
	{
		return new RaceSubTypeFilter(subType);
	}

	private static PObjectFilter createRaceTypeFilter(RaceType type)
	{
		return new RaceTypeFilter(type);
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

	private static PObjectFilter createSizeFilter(SizeAdjustment size)
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
	private static void parseCustomFilterList(FilterParser parser, List<PObjectFilter> filterList, List<String[]> filterDefinitions)
	{
		for ( final String[] filterData : filterDefinitions )
		{
			final StringBuffer filterDefinition = new StringBuffer();
			// TODO - Create constant for this.
			final StringTokenizer tokens = new StringTokenizer(filterData[0], "()", true); //$NON-NLS-1$

			while (tokens.hasMoreTokens())
			{
				final String token = tokens.nextToken();

				if (FilterParser.isLegalToken(token.trim()))
				{
					filterDefinition.append(token);
				}
				else
				{
					filterDefinition.append('[');
					filterDefinition.append(token);
					filterDefinition.append(']');
				}
			}

			try
			{
				PObjectFilter filter = parser.parse(filterDefinition.toString());

				if ((filterData[1] + filterData[2]).length() > 0)
				{
					filter = FilterFactory.createNamedFilter(filter, filterData[1], filterData[2]);
				}

				filterList.add(filter);
			}
			catch (FilterParseException ex)
			{
				//Shouldn't something more be done here?
				Logging.errorPrintLocalised("Errors.FilterFactory.ParseError", ex); //$NON-NLS-1$
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
	private static List<String[]> preprocessFilterList(String list, String filterString)
	{
		List<String[]> customFilters = new ArrayList<String[]>();

		if (filterString.length() == 0)
		{
			return customFilters;
		}

		// TODO - Make constants for this.
		final StringTokenizer filterTokens = new StringTokenizer(filterString, "[]"); //$NON-NLS-1$

		while (filterTokens.hasMoreTokens())
		{
			final StringTokenizer tokens = new StringTokenizer(filterTokens.nextToken(), Constants.PIPE);
			final String className = tokens.nextToken();
			final String classDef = tokens.nextToken();
			final String filterName = (tokens.hasMoreTokens()) ? tokens.nextToken() : Constants.EMPTY_STRING;
			final String filterDesc = (tokens.hasMoreTokens()) ? tokens.nextToken() : Constants.EMPTY_STRING;

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
			// TODO - This should be handled better.
			if (className.endsWith("CompoundFilter")  //$NON-NLS-1$
			  || className.endsWith("InverseFilter") //$NON-NLS-1$
			  || className.endsWith("NamedFilter")) //$NON-NLS-1$
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
		super(LanguageBundle.getString("Filters.Category.Object"), LanguageBundle.getString("in_class")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 * @see pcgen.gui.filter.PObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
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
		super(LanguageBundle.getString("Filters.Category.Object"),  //$NON-NLS-1$
			  LanguageBundle.getString("in_template")); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
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
		super(LanguageBundle.getString("Filters.Category.Object"),  //$NON-NLS-1$
			  LanguageBundle.getString("in_race")); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
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

	SpellTypeFilter(final String aType)
	{
		super(LanguageBundle.getString("in_spellType"), aType); //$NON-NLS-1$
		this.type = aType.toUpperCase();
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
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
	enum Mode {
		/** Objects with this alignment are allowed */
		ALLOWED("in_Filters.Mode.Allowed"), //$NON-NLS-1$
		/** Objects with this alignment are required */
		REQUIRED("in_Filters.Mode.Required"), //$NON-NLS-1$
		/** Not sure */
		DEFAULT("in_Filters.Mode.Default"); //$NON-NLS-1$
		
		private String theName;
		
		Mode(final String aResourceId)
		{
			theName = LanguageBundle.getString(aResourceId);
		}
		
		/**
		 * Returns the display name for this mode.
		 * 
		 * @return A string to display for this mode.
		 */
		@Override
		public String toString()
		{
			return theName;
		}
	}
	
	/** The <tt>Mode</tt> for this filter */
	protected Mode theMode;

	/**
	 * Construct an AlignmentFilter with the specified category and name.
	 * 
	 * @param argCategory The category.
	 * @param argName The name.
	 */
	protected AlignmentFilter(final String argCategory, final String argName)
	{
		super(argCategory, argName);
	}

	/**
	 * Tests to see if this Filter applies to this PObject.
	 * 
	 * @param pObject The Object to test
	 * @param alignment The alignment index to test for
	 * @return <tt>true</tt> if the object passes.
	 */
	protected boolean passesAlignmentPrereqs(PObject pObject, PCAlignment alignment)
	{
		boolean allowed = false;

		switch ( theMode )
		{
		case ALLOWED:
			allowed = true;
			break;

		case REQUIRED:
			break;

		default:
			return false;
		}

		if (RaceAlignment.hasAlignmentRestriction(pObject))
		{
			return RaceAlignment.canBeAlignment(pObject, alignment);
		}
		return allowed;
	}
}


final class DeityAlignmentFilter extends AlignmentFilter
{
	private PCAlignment alignment;

	DeityAlignmentFilter(PCAlignment anAlignment)
	{
		this(anAlignment, Mode.DEFAULT);
	}

	DeityAlignmentFilter(PCAlignment anAlignment, Mode mode)
	{
		super(LanguageBundle.getString("in_alignLabel"),  //$NON-NLS-1$
				anAlignment.getDisplayName());
		this.alignment = anAlignment;
		theMode = mode;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#getCategory()
	 */
	@Override
	public String getCategory()
	{
		if ( theMode.equals(Mode.DEFAULT) )
		{
			return super.getCategory();
		}
		return super.getCategory() + " (" + theMode.toString() + ')'; //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Deity)
		{
			return alignment.equals(pObject.get(ObjectKey.ALIGNMENT));
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

	DomainFilter(final Domain aDomain)
	{
		super(LanguageBundle.getString("in_domains"), aDomain.getDisplayName()); //$NON-NLS-1$
		this.domain = aDomain;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if ((pObject == null) || (domain == null))
		{
			return false;
		}

		if (pObject instanceof Deity)
		{
			return ((Deity) pObject).hasObjectOnList(Deity.DOMAINLIST, domain);
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
	enum Detail {
		/** Provide a high level of detail */
		HIGH, 
		/** Provide a lower level of detail */
		LOW;
	}

	private String pantheon;
	private Detail detailLevel;

	PantheonFilter(final String aPantheon, Detail argDetailLevel)
	{
		super();
		this.detailLevel = argDetailLevel;
		this.pantheon = ((this.detailLevel == Detail.LOW) ? normalizePantheon(aPantheon) : aPantheon);
		this.pantheon = ((this.pantheon.equalsIgnoreCase(Constants.LST_ALL)) ? Constants.LST_ALL : aPantheon);
		setCategory(LanguageBundle.getString("in_pantheon") //$NON-NLS-1$
			+ ((detailLevel == Detail.LOW) 
				? String.format("(%1$s)", LanguageBundle.getString("in_general")) //$NON-NLS-1$ //$NON-NLS-2$
				: String.format("(%1$s)", LanguageBundle.getString("in_specific"))));  //$NON-NLS-1$//$NON-NLS-2$
		setName(this.pantheon);

		setDescription((this.pantheon.equalsIgnoreCase(Constants.LST_ALL)) 
				? LanguageBundle.getString("in_acceptPantAll") //$NON-NLS-1$
				: LanguageBundle.getFormattedString("Filters.Pantheon.Description", pantheon)); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Deity)
		{
			final Deity aDeity = (Deity) pObject;

			if (pantheon.equals(Constants.LST_ALL) && (aDeity.getSafeListFor(ListKey.PANTHEON).size() == 0))
			{
				return true;
			}

			for ( final Pantheon pantheon : aDeity.getSafeListFor(ListKey.PANTHEON) )
			{
				String tmp = pantheon.toString();
				if (detailLevel == Detail.LOW)
				{
					tmp = normalizePantheon(tmp);
				}

				if (pantheon.toString().startsWith(tmp))
				{
					return true;
				}
			}

			return false;
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

		if (work.indexOf("(") > 0) //$NON-NLS-1$
		{
			work = (new StringTokenizer(work, "()")).nextToken().trim(); //$NON-NLS-1$
		}

		return work;
	}
}


final class PCAlignmentFilter extends AlignmentFilter
{
	PCAlignmentFilter()
	{
		this(Mode.DEFAULT);
	}

	PCAlignmentFilter(final Mode aMode)
	{
		super(LanguageBundle.getString("in_alignLabel"), LanguageBundle.getString("in_pc"));  //$NON-NLS-1$//$NON-NLS-2$
		theMode = aMode;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#getCategory()
	 */
	@Override
	public String getCategory()
	{
		if (theMode.equals(Mode.DEFAULT))
		{
			return super.getCategory();
		}
		return super.getCategory() + " (" + theMode.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Returns the name of this filter.
	 * 
	 * @see pcgen.gui.filter.AbstractPObjectFilter#getName(pcgen.core.PlayerCharacter)
	 */
	@Override
	public String getName(PlayerCharacter aPC)
	{
		if (aPC != null)
		{
			return super.getName(aPC) + " (" //$NON-NLS-1$
			+ aPC.getPCAlignment().getDisplayName() + ")"; //$NON-NLS-1$
		}
		return super.getName(aPC);
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		PCAlignment alignment = aPC.getPCAlignment();

		if (pObject instanceof Deity)
		{
			return alignment.equals(pObject.get(ObjectKey.ALIGNMENT));
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
		super(LanguageBundle.getString("in_feats"), LanguageBundle.getString("in_Automatic")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Ability)
		{
			return aPC.hasAutomaticAbility(AbilityCategory.FEAT, ((Ability) pObject));
		}

		return true;
	}
}


final class NormalFeatFilter extends AbstractPObjectFilter
{
	NormalFeatFilter()
	{
		super(LanguageBundle.getString("in_feats"), LanguageBundle.getString("in_Normal"));  //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(final PlayerCharacter aPC, final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Ability)
		{
			return aPC.hasRealAbility(AbilityCategory.FEAT, (Ability) pObject);
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
		super(LanguageBundle.getString("in_feats"), LanguageBundle.getString("in_Virtual")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see pcgen.gui.filter.PObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Ability)
		{
			return aPC.hasVirtualAbility(AbilityCategory.FEAT, ((Ability) pObject));
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
		super(LanguageBundle.getString("in_miscel"), LanguageBundle.getString("in_Affordable"));  //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(final PlayerCharacter aPC, final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Equipment)
		{
			// TODO - Should this check to see if ignore cost is on?
			return ((Equipment) pObject).getCost(aPC).compareTo(aPC.getGold()) < 1;
		}

		return true;
	}
}


final class NonMagicFilter extends AbstractPObjectFilter
{
	NonMagicFilter()
	{
		super(LanguageBundle.getString("in_miscel"), LanguageBundle.getString("in_Non-Magic"));  //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
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
		super(LanguageBundle.getString("in_size"), LanguageBundle.getString("PC"));  //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#getName(pcgen.core.PlayerCharacter)
	 */
	@Override
	public String getName(final PlayerCharacter aPC)
	{
		String pcName = super.getName(aPC);
		if (aPC != null)
		{
			pcName += " ("; //$NON-NLS-1$
			final SizeAdjustment sizeAdj = aPC.getSizeAdjustment();
			if (sizeAdj != null)
			{
				pcName += sizeAdj.getDisplayName();
			}
			pcName += ')';
		}
		return pcName;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(final PlayerCharacter aPC, final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Equipment)
		{
			return ((Equipment) pObject).sizeInt() == aPC.sizeInt();
		}

		return true;
	}
}


final class TypeFilter extends AbstractPObjectFilter
{
	private String type;

	TypeFilter(final String argType)
	{
		this(argType, true);
	}

	TypeFilter(final String argType, final boolean capitalize)
	{
		super(LanguageBundle.getString("in_type"), //$NON-NLS-1$
			(capitalize) ? CoreUtility.capitalizeFirstLetter(argType) : argType);
		this.type = argType.toUpperCase();
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		// TODO - Should this just return isType() for any PObject?
		if (pObject instanceof Equipment
		||  pObject instanceof PCClass
		||  pObject instanceof Race)
		{
			return pObject.isType(type);
		}

		return true;
	}
}


final class WeaponFilter extends AbstractPObjectFilter
{
	private String type;

	WeaponFilter(final String argType)
	{
		super(LanguageBundle.getString("in_weapon"), //$NON-NLS-1$
			CoreUtility.capitalizeFirstLetter(argType));
		this.type = argType.toUpperCase();
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
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

	RankFilter(final double aMin)
	{
		super(LanguageBundle.getString("in_skills"),  //$NON-NLS-1$
			  LanguageBundle.getFormattedString("Filters.Rank.Name", aMin)); //$NON-NLS-1$
		this.min = aMin;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(final PlayerCharacter aPC, final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Skill)
		{
			return SkillRankControl.getTotalRank(aPC, (Skill) pObject).doubleValue() > min;
		}

		return true;
	}
}


final class RankModifierFilter extends AbstractPObjectFilter
{
	private double min;

	RankModifierFilter(final double aMin)
	{
		super(LanguageBundle.getString("in_skills"), //$NON-NLS-1$
			LanguageBundle.getFormattedString("Filters.RankMod.Name", aMin)); //$NON-NLS-1$
		this.min = aMin;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(final PlayerCharacter aPC, final PObject pObject)
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

			return (SkillRankControl.getTotalRank(aPC, aSkill).doubleValue() + SkillModifier.modifier(aSkill, aPC).doubleValue()) > min;
		}

		return true;
	}
}


final class StatFilter extends AbstractPObjectFilter
{
	StatFilter(final String stat)
	{
		super(LanguageBundle.getString("in_keyAbility"), stat.toUpperCase()); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(final PlayerCharacter aPC, final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Skill)
		{
			return ((Skill) pObject).getKeyStatAbb().toUpperCase().equals(getName(aPC));
		}

		return true;
	}
}


final class UntrainedSkillFilter extends AbstractPObjectFilter
{
	UntrainedSkillFilter()
	{
		super(LanguageBundle.getString("in_skills"), LanguageBundle.getString("in_untrained")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Skill)
		{
			return ((Skill) pObject).getSafe(ObjectKey.USE_UNTRAINED);
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

	CastingTimeFilter(final String argCastingTime)
	{
		super(LanguageBundle.getString("in_castingTime"), argCastingTime); //$NON-NLS-1$
		castingTime = argCastingTime;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return ((Spell) pObject).getListAsString(ListKey.CASTTIME).equals(castingTime);
		}

		return true;
	}
}


final class ComponentFilter extends AbstractPObjectFilter
{
	private String component;

	ComponentFilter(String argComponent)
	{
		super(LanguageBundle.getString("in_component"), argComponent); //$NON-NLS-1$
		this.component = argComponent;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			StringTokenizer tokens = new StringTokenizer(((Spell) pObject).getListAsString(ListKey.COMPONENTS));

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

	DescriptorFilter(final String aDescriptor)
	{
		super(LanguageBundle.getString("in_descriptor"), aDescriptor); //$NON-NLS-1$
		this.descriptor = aDescriptor;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return pObject.containsInList(ListKey.SPELL_DESCRIPTOR, descriptor);
		}

		return true;
	}
}


final class EffectTypeFilter extends AbstractPObjectFilter
{
	private String effectType;

	EffectTypeFilter(final String anEffectType)
	{
		super(LanguageBundle.getString("in_effectType"), anEffectType); //$NON-NLS-1$
		this.effectType = anEffectType;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return ((Spell) pObject).getSafe(StringKey.TARGET_AREA).equals(effectType);
		}

		return true;
	}
}


final class RangeFilter extends AbstractPObjectFilter
{
	private String range;

	RangeFilter(final String aRange)
	{
		super();
		this.range = aRange;
		this.range = normalizeCategory(this.range);
		this.range = normalizeRange(this.range);

		setCategory(LanguageBundle.getString("in_range")); //$NON-NLS-1$
		setName(this.range);
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return normalizeRange(((Spell) pObject).getListAsString(ListKey.RANGE)).indexOf(range) > -1;
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

		if (work.startsWith("CLOSE")) //$NON-NLS-1$
		{
			return "Close";
		}

		if (work.startsWith("MEDIUM")) //$NON-NLS-1$
		{
			return "Medium";
		}

		if (work.startsWith("LONG")) //$NON-NLS-1$
		{
			return "Long";
		}

		if (work.startsWith("PERSONAL")) //$NON-NLS-1$
		{
			return "Personal";
		}

		if (work.startsWith("TOUCH")) //$NON-NLS-1$
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
	// TODO - This doesn't belong here.  Move to spell code.
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

		del = Constants.EMPTY_STRING;

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

	SchoolFilter(final String aSchool)
	{
		super(LanguageBundle.getString("in_school"), aSchool); //$NON-NLS-1$
		this.school = aSchool;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return ((Spell) pObject).getListAsString(ListKey.SPELL_SCHOOL).equals(school);
		}

		return true;
	}
}


final class SpellResistanceFilter extends AbstractPObjectFilter
{
	private String sr;

	SpellResistanceFilter(final String anSR)
	{
		super();
		this.sr = normalizeSpellResistance(anSR);
		setCategory(LanguageBundle.getString("in_spellRes")); //$NON-NLS-1$
		setName(this.sr);
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return ((Spell) pObject).getListAsString(ListKey.SPELL_RESISTANCE).indexOf(sr) > -1;
		}

		return true;
	}

	/*
	 * removed internationalization here
	 *
	 * author: Thomas Behr 02-09-20
	 */
	private static String normalizeSpellResistance(final String s)
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

	SubschoolFilter(final String aSchool)
	{
		super();
		this.school = normalizeSubschool(aSchool);
		setCategory(LanguageBundle.getString("in_subschool")); //$NON-NLS-1$
		setName(this.school);
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Spell)
		{
			return normalizeSubschool(((Spell) pObject).getListAsString(ListKey.SPELL_SUBSCHOOL)).equals(school);
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

	FavoredClassFilter(final String aClassName)
	{
		super(LanguageBundle.getString("in_favoredClass"), aClassName); //$NON-NLS-1$
		this.className = aClassName.toUpperCase();
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Race)
		{
			List<CDOMReference<? extends PCClass>> favClass = pObject
					.getListFor(ListKey.FAVORED_CLASS);
			/*
			 * CONSIDER Given SubClass issues, this is probably wrong
			 */
			if (favClass != null)
			{
				for (CDOMReference<? extends PCClass> ref : favClass)
				{
					for (PCClass cl : ref.getContainedObjects())
					{
						if (cl.getKeyName().equalsIgnoreCase(className))
						{
							return true;
						}
					}
				}
			}
			return false;
		}

		return true;
	}
}


final class SizeFilter extends AbstractPObjectFilter
{
	private SizeAdjustment size;

	SizeFilter(final SizeAdjustment aSize)
	{
		super(LanguageBundle.getString("in_size"), aSize.getDisplayName()); //$NON-NLS-1$
		this.size = aSize;
	}

	/**
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(@SuppressWarnings("unused")final PlayerCharacter aPC, 
							final PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Equipment)
		{
			return ((Equipment) pObject).getSafe(ObjectKey.SIZE).equals(size);
		}
		else if (pObject instanceof Race)
		{
			Formula sz = pObject.get(FormulaKey.SIZE);
			final String aRaceSize = sz == null ? "" : sz.toString();

			return aRaceSize.equals(size.getAbbreviation())
					|| aRaceSize.equals(size.getDisplayName());
		}

		return true;
	}
}
