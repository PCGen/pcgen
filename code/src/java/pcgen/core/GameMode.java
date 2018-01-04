/*
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.content.ACControl;
import pcgen.cdom.content.RollMethod;
import pcgen.cdom.content.TabInfo;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.TransparentCategorizedReferenceManufacturer;
import pcgen.cdom.reference.TransparentReference;
import pcgen.core.character.WieldCategory;
import pcgen.core.system.LoadInfo;
import pcgen.facade.core.GameModeFacade;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.GameReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.context.TrackingReferenceContext;
import pcgen.system.PCGenSettings;
import pcgen.system.PropertyContext;
import pcgen.util.ComparableComparator;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

import org.apache.commons.lang3.StringUtils;

/**
 * Handles game modes.
 */
public final class GameMode implements Comparable<Object>, GameModeFacade
{
	private static PropertyContext prefsContext = PCGenSettings.getInstance().createChildContext("gameMode"); //$NON-NLS-1$

	private PropertyContext gamemodePrefsContext = prefsContext.createChildContext("gameMode"); //$NON-NLS-1$
	private List<String> allowedModes;
	private List<String> bonusFeatLevels = new ArrayList<>();
	private List<String> bonusStackList = new ArrayList<>();
	private List<String> bonusStatLevels = new ArrayList<>();
	private List<ClassType> classTypeList = new ArrayList<>();
	private List<String> defaultDataSetList = new ArrayList<>();
	private List<String> defaultDeityList = new ArrayList<>();
	private Map<String, XPTable> xpTableInfo = new HashMap<>();
	private List<String> loadStrings = new ArrayList<>();
	private List<String> skillMultiplierLevels = new ArrayList<>();
	@Deprecated
	private HashMapToList<String, ACControl> ACTypeAddMap = new HashMapToList<>();
	@Deprecated
	private HashMapToList<String, ACControl> ACTypeRemoveMap = new HashMapToList<>();
	private Map<String, String> plusCalcs;
	private Map<String, String> spellRangeMap = new HashMap<>();
	private String acAbbrev = "";
	private String acName = "";
	private String alignmentName = "";
	private String althpAbbrev = "";
	private String althpName = "";
	private String babAbbrev = null;
	private String currencyUnit = "";
	private String currencyUnitAbbrev = "";
	private String damageResistance = "";
	private String defaultSpellBook = "Known Spells";
	private String defaultUnitSet = Constants.STANDARD_UNITSET_NAME;
	private UnitSet selectedUnitSet = null;
	private String displayName = "";
	private String displayVariable2Name = "";
	private String displayVariable2Text = "";
	private String displayVariable3Name = "";
	private String displayVariable3Text = "";
	private String displayVariableName = "";
	private String displayVariableText = "";
	private String folderName = "";
	private String hpAbbrev = "";
	private String hpName = "";
	private String levelUpMessage = "";
	private String menuToolTip = "";
	private String name = "";
	private String spellBaseDC = "0";
	private String spellBaseConcentration = "";
	private String wcStepsFormula = "";
	private String weaponCategories = "";
	private String weaponTypes = "";
	private String weaponReachFormula = "";
	private Map<Integer, Integer> xpAwardsMap = new HashMap<>();
	private Map<Integer, String> crStepsMap = new HashMap<>();
	private String crThreshold = null;
	private String rankModFormula = "";
	private String addWithMetamagic = "";
	private boolean allowAutoResize = false;
	private boolean bonusStatAllowsStack = false;
	private boolean showClassDefense;
	private int babAttCyc = 5; //6
	private int babMaxAtt = Integer.MAX_VALUE; //4
	private int babMaxLvl = Integer.MAX_VALUE; //20
	private int babMinVal = 1;
	private int maxNonEpicLevel = Integer.MAX_VALUE;
	private int checksMaxLvl = Integer.MAX_VALUE; //20
	private int displayOrder = Integer.MAX_VALUE;
	private final List<String> schoolsList = new ArrayList<>(20);

	private int skillCosts_Class     = 1;
	private int skillCost_CrossClass = 2;
	private int skillCost_Exclusive  = 0;

	private String pointPoolName = "";
	private String hpFormula = "";

	private int nonProfPenalty = -4;

	private double squareSize = 5;

	/** no default distance for short range */
	private int shortRangeDistance;
	private int rangePenalty;

	private RollMethod activeRollMethod = null;

	private SortedMap<Integer, PointBuyCost> pointBuyStatCosts = null;
	private int[] abilityScoreCost = null;
	private String purchaseMethodName = ""; //$NON-NLS-1$

	private int rollMethod = Constants.CHARACTER_STAT_METHOD_USER;

	private int allStatsValue = 10;

	//
	// minimum and maximum stat values when creating new characters.
	//
	private int statMin = 3;
	private int statMax = 18;

	private TreeMap<Integer, String> statDisplayText = null;
	private String statDisplayTextAppend = "+";
	private TreeMap<Integer, String> skillRankDisplayText = null;

	private String thePreviewDir;
	private String theDefaultPreviewSheet;
	private String theInfoSheet;
	private String theInfoSheetSkill;
	
	private String outputSheetDirectory;
	private Map<String, String> outputSheetDefaultMap = new HashMap<>();

	private int [] dieSizes;
	private int maxDieSize = 12;
	private int minDieSize = 4;

	private List<String> resizableTypeList = new ArrayList<>();
	private List<String> characterTypeList = new ArrayList<>();
	private List<String> monsterRoleList = new ArrayList<>();
	private String monsterRoleDefault = "";
	private Map<Class<?>, Set<String>> hiddenTypes = new HashMap<>();

	private List<String> xpTableNames = new ArrayList<>();
	private String defaultXPTableName;
	private String defaultCharacterType;

	/** The BioSet used for age calculations */
	private BioSet bioSet = new BioSet();
	
	/** SHOWTAB compatibility */
	private Map<CDOMSingleRef<TabInfo>, Boolean> visibleTabs;

	private Map<String, String> equipTypeIconMap = new HashMap<>();

	/** Priority of the equipment types for icon use. */
	private Map<String, Integer> equipTypeIconPriorityMap = new HashMap<>();
	
	/** A container for feat settings for this game mode. */
	private AbilityCategory featTemplate;

	/**
	 * Creates a new instance of GameMode.
	 *
	 * @param modeName the mode name
	 */
	public GameMode(final String modeName)
	{
		name = modeName;
		folderName = modeName;
		displayName = modeName;
		thePreviewDir = modeName;
		theDefaultPreviewSheet = "preview.html"; //$NON-NLS-1$
	}

	/**
	 * Apply the stored preferences to the game mode. 
	 */
	public void applyPreferences()
	{
		gamemodePrefsContext = prefsContext.createChildContext(name);
		String rollMethodExpr = gamemodePrefsContext.getProperty("rollMethodExpression"); //$NON-NLS-1$
		if (StringUtils.isNotBlank(rollMethodExpr))
		{
			activeRollMethod =
					getModeContext().getReferenceContext().silentlyGetConstructedCDOMObject(
						RollMethod.class, rollMethodExpr);
			if (activeRollMethod == null)
			{
				Logging.errorPrint("Could not find roll method '" //$NON-NLS-1$
					+ rollMethodExpr + "' while loading game mode " + name); //$NON-NLS-1$
			}
		}
		rollMethod = gamemodePrefsContext.getInt("rollMethod"); //$NON-NLS-1$
		allStatsValue = gamemodePrefsContext.initInt("allStatsValue", 10); //$NON-NLS-1$
		purchaseMethodName =
				gamemodePrefsContext.getProperty("purchaseMethodName"); //$NON-NLS-1$
	}

	/**
	 * Set the AC Abbreviation.
	 * @param aString
	 */
	public void setACAbbrev(final String aString)
	{
		acAbbrev = aString;
	}

	/**
	 * Set the AC Text.
	 * @param aString
	 */
	public void setACText(final String aString)
	{
		acName = aString;
	}

	/**
	 * Add the AC Type as a string to the List.
	 * @param ACType
	 * @return List of AC Types
	 */
	@Deprecated
	public List<ACControl> getACTypeAddString(final String ACType)
	{
		return ACTypeAddMap.getListFor(ACType);
	}

	/**
	 * Remove the AC Type as a string to the List.
	 * @param ACType
	 * @return List of AC Types
	 */
	@Deprecated
	public List<ACControl> getACTypeRemoveString(final String ACType)
	{
		return ACTypeRemoveMap.getListFor(ACType);
	}

	/**
	 * Retrieve the correct case of the supplied ACType name. 
	 * @param acType The name to be found.
	 * @return The name in the correct case.
	 */
	@Deprecated
	public String getACTypeName(String acType)
	{
		if (ACTypeAddMap.containsListFor(acType)
			|| ACTypeRemoveMap.containsListFor(acType))
		{
			return acType;
		}
		for (String acKey : ACTypeAddMap.getKeySet())
		{
			if (acKey.equalsIgnoreCase(acType))
			{
				return acKey;
			}
		}
		for (String acKey : ACTypeRemoveMap.getKeySet())
		{
			if (acKey.equalsIgnoreCase(acType))
			{
				return acKey;
			}
		}
		
		return acType;
	}
	/**
	 * Set Alignment Text.
	 * @param aString
	 */
	public void setAlignmentText(final String aString)
	{
		alignmentName = aString;
	}

	/**
	 * Set Allowed Game modes.
	 * @param argAllowedModes
	 */
	public void setAllowedModes(final String argAllowedModes)
	{
		final StringTokenizer aTok = new StringTokenizer(argAllowedModes, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();

			if (allowedModes == null)
			{
				allowedModes = new ArrayList<>();
			}

			allowedModes.add(aString);
		}
	}

	/**
	 * Set the alternative HP Abbreviation.
	 * @param aString
	 */
	public void setAltHPAbbrev(final String aString)
	{
		althpAbbrev = aString;
	}

	/**
	 * Set the alternative HP Text.
	 * @param aString
	 */
	public void setAltHPText(final String aString)
	{
		althpName = aString;
	}

	/**
	 * Set flag to allow auto resizing or not.
	 * @param allow
	 */
	public void setAllowAutoResize(final boolean allow)
	{
		allowAutoResize = allow;
	}

	/**
	 * Get the allow auto resize flag.
	 * @return true if allowed to auto resize
	 */
	public boolean getAllowAutoResize()
	{
		return allowAutoResize;
	}

	/**
	 * Set BAB Abbreviation.
	 * @param aString
	 */
	public void setBabAbbrev(final String aString)
	{
		babAbbrev = aString;
	}

	/**
	 * Get BAB Abbreviation.
	 * @return BAB Abbreviation
	 */
	public String getBabAbbrev()
	{
		return babAbbrev;
	}

	/**
	 * Set BAB Minimum value
	 * @param arg
	 */
	public void setBabMinVal(final int arg)
	{
		babMinVal = arg;
	}

	/**
	 * Get BAB Minimum value.
	 * @return BAB Minimum value
	 */
	int getBabMinVal()
	{
		return babMinVal;
	}

	/**
	 * Set Level at which you gain a bonus stat.
	 * @param aString
	 */
	public void setBonusStatLevels(final String aString)
	{
		bonusStatLevels.add(aString);
	}

	/**
	 * Set Max Level check.
	 * @param arg
	 */
	public void setChecksMaxLvl(final int arg)
	{
		checksMaxLvl = arg;
	}

	/**
	 * Get max level check.
	 * @return max level check
	 */
	int getChecksMaxLvl()
	{
		return checksMaxLvl;
	}

	/**
	 * Get the class type by name.
	 * @param aClassKey
	 * @return ClassType
	 */
	public ClassType getClassTypeByName(final String aClassKey)
	{
		for ( ClassType classType : classTypeList )
		{
			if (classType.getName().equalsIgnoreCase(aClassKey))
			{
				return classType;
			}
		}

		return null;
	}

	/**
	 * Set the Currency Unit.
	 * @param aString
	 */
	public void setCurrencyUnit(final String aString)
	{
		currencyUnit = aString;
	}

	/**
	 * Set the currency Unit abbreviation.
	 * @param aString
	 */
	public void setCurrencyUnitAbbrev(final String aString)
	{
		currencyUnitAbbrev = aString;
	}

	/**
	 * Set DR text.
	 * @param aString
	 */
	public void setDamageResistanceText(final String aString)
	{
		damageResistance = aString;
	}

	/**
	 * Set the default spell book.
	 * @param aString
	 */
	public void setDefaultSpellBook(final String aString)
	{
		defaultSpellBook = aString;
	}

	/**
	 * Define the default unit set for a game mode.
	 * @param aString
	 */
	public void setDefaultUnitSet(final String aString)
	{
		defaultUnitSet = aString;
	}

	/**
	 * Get the display name of the game mode.
	 * @return displayName
	 */
    @Override
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Used for Output sheets and GUI to order items in a list.
	 * @param aString
	 */
	public void setDisplayOrder(final String aString)
	{
		try
		{
			displayOrder = Integer.parseInt(aString);
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Will use default for displayOrder: " + displayOrder, exc);
		}
	}

	/**
	 * Set the levels at which you gain bonus feats.
	 * @param aString
	 */
	public void setBonusFeatLevels(final String aString)
	{
		bonusFeatLevels.add(aString);
	}

	/**
	 * @return the folderName.
	 */
	public String getFolderName()
	{
		return folderName;
	}

	/**
	 * Set the HP Abbreviation.
	 * @param aString
	 */
	public void setHPAbbrev(final String aString)
	{
		hpAbbrev = aString;
	}

	/**
	 * Set the HP Text.
	 * @param aString
	 */
	public void setHPText(final String aString)
	{
		hpName = aString;
	}

	/**
	 * Set the Rank Modifier Formula.
	 * @param aString
	 */
	public void setRankModFormula(final String aString)
	{
		rankModFormula = aString;
	}

	/**
	 * Obtain a map of LevelInfo objects.
	 *
	 * @param xpTableName the name of the XP table to be used
	 *
	 * @return level info map
	 */
	public XPTable getLevelInfo(final String xpTableName)
	{
		return xpTableInfo.get(xpTableName);
	}

	/**
	 * Add new level info to an XP table.
	 *
	 * @param levInfo
	 */
	public void addLevelInfo(final String xpTableName, final LevelInfo levInfo)
	{
		XPTable xpTable = xpTableInfo.computeIfAbsent(xpTableName, XPTable::new);
		xpTable.addLevelInfo(levInfo.getLevelString(), levInfo);
	}

	/**
	 * Set the level up message.
	 * @param aString
	 */
	public void setLevelUpMessage(final String aString)
	{
		levelUpMessage = aString;
	}

	/**
	 * Get the level up message.
	 * @return the level up message
	 */
	public String getLevelUpMessage()
	{
		return levelUpMessage;
	}

	/**
	 * Get the menu tool tip.
	 * @return menu tool tip
	 */
	public String getMenuToolTip()
	{
		if (menuToolTip == null)
		{
			return "";
		}

		return menuToolTip;
	}

	/**
	 * Set the game mode name.
	 * @param modeName The MENUENTRY value.
	 */
	public void setModeName(final String modeName)
	{
		displayName = modeName;
	}

	/**
	 * Set the game mode tool tip.
	 * @param aString
	 */
	public void setModeToolTip(final String aString)
	{
		menuToolTip = aString;
	}

	/**
	 * Get the game mode name.
	 * @return game mode name
	 */
    @Override
	public String getName()
	{
		return name;
	}

	/**
	 * Set the game mode name (aka key). Should not
	 * be used after game mode is loaded.
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Get Plus calculation.
	 * @param type
	 * @return plus calculation
	 */
	String getPlusCalculation(final String type)
	{
		String aString = null;

		if (plusCalcs != null)
		{
			aString = plusCalcs.get(type.toUpperCase());
		}

		return aString;
	}

	/**
	 * Set the Short Range distance.
	 * @param aShortRange
	 */
	public void setShortRangeDistance(final int aShortRange)
	{
		shortRangeDistance = aShortRange;
	}

	/**
	 * Get the shortrange distance.
	 * @return the shortrange distance
	 */
	public int getShortRangeDistance()
	{
		return shortRangeDistance;
	}

	/**
	 * Set the show class defense parameter.
	 * @param argShowDef
	 */
	public void setShowClassDefense(final boolean argShowDef)
	{
		showClassDefense = argShowDef;
	}

	/**
	 * Set the skill multiplier levels.
	 * @param pipeList
	 */
	public void setSkillMultiplierLevels(final String pipeList)
	{
		final StringTokenizer aTok = new StringTokenizer(pipeList, "|", false);
		skillMultiplierLevels.clear();

		while (aTok.hasMoreTokens())
		{
			skillMultiplierLevels.add(aTok.nextToken());
		}
	}

	/**
	 * Set the base DC for spells.
	 * @param arg
	 */
	public void setSpellBaseDC(final String arg)
	{
		spellBaseDC = arg;
	}

	/**
	 * Get the base DC for Spells.
	 * @return the base DC for Spells
	 */
	String getSpellBaseDC()
	{
		return spellBaseDC;
	}

	/**
	 * Set the base concentration bonus for spells.
	 * @param arg
	 */
	public void setSpellBaseConcentration(final String arg)
	{
		spellBaseConcentration = arg;
	}

	/**
	 * Get the base concentration bonus for Spells.
	 * @return the base concentration bonus for Spells
	 */
	public String getSpellBaseConcentration()
	{
		return spellBaseConcentration;
	}

	/**
	 * The formula used to compute spell ranges.
	 * @param aString
	 */
	public void setSpellRangeFormula(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		if (aTok.countTokens() < 2)
		{
			return;
		}

		final String aRange = aTok.nextToken().toUpperCase();
		final String aFormula = aTok.nextToken();
		spellRangeMap.put(aRange, aFormula);
	}

	/**
	 * Returns the formula used for calculate the range of a spell.
	 * @param aRange
	 * @return spell range formula
	 */
	String getSpellRangeFormula(final String aRange)
	{
		String aString = null;

		if (spellRangeMap != null)
		{
			aString = spellRangeMap.get(aRange);
		}

		return aString;
	}

	/**
	 * Set the BAB Attack Bonus cycle.
	 * @param arg
	 */
	public void setBabAttCyc(final int arg)
	{
		babAttCyc = arg;
	}

	/**
	 * Get the BAB Attack Bonus cycle.
	 * @return the BAB Attack Bonus cycle
	 */
	int getBabAttCyc()
	{
		return babAttCyc;
	}

	/**
	 * Set the maximum BAB attacks allowed.
	 * @param arg
	 */
	public void setBabMaxAtt(final int arg)
	{
		babMaxAtt = arg;
	}

	/**
	 * Get the max BAB attacks allowed.
	 * @return the max BAB attacks allowed
	 */
	int getBabMaxAtt()
	{
		return babMaxAtt;
	}

	/**
	 * Set the max BAB level.
	 * @param arg
	 */
	public void setBabMaxLvl(final int arg)
	{
		babMaxLvl = arg;
	}

	/**
	 * Get the max BAB level.
	 * @return the max BAB level
	 */
	public int getBabMaxLvl()
	{
		return babMaxLvl;
	}

	/**
	 * Set a 2nd variable display name.
	 * @param aString
	 */
	public void setVariableDisplay2Name(final String aString)
	{
		displayVariable2Name = aString;
	}

	/**
	 * Set a 2nd variable display text.
	 * @param aString
	 */
	public void setVariableDisplay2Text(final String aString)
	{
		displayVariable2Text = aString;
	}

	/**
	 * Set a 3rd variable display name.
	 * @param aString
	 */
	public void setVariableDisplay3Name(final String aString)
	{
		displayVariable3Name = aString;
	}

	/**
	 * Set a 3rd variable display text.
	 * @param aString
	 */
	public void setVariableDisplay3Text(final String aString)
	{
		displayVariable3Text = aString;
	}

	/**
	 * Set a variable display name.
	 * @param aString
	 */
	public void setVariableDisplayName(final String aString)
	{
		displayVariableName = aString;
	}

	/**
	 * Set a 2nd variable display text.
	 * @param aString
	 */
	public void setVariableDisplayText(final String aString)
	{
		displayVariableText = aString;
	}

	/**
	 * Formula used to compute Wield Category steps.
	 * @param aString
	 */
	public void setWCStepsFormula(final String aString)
	{
		wcStepsFormula = aString;
	}

	/**
	 * Get the formula used to compute Wield Category steps.
	 * @return formula used to compute Wield Category steps
	 */
	public String getWCStepsFormula()
	{
		return wcStepsFormula;
	}

	/**
	 * Get the weapon categories.
	 * @return the weapon categories
	 */
	public String getWeaponCategories()
	{
		return weaponCategories;
	}

	/**
	 * Get the weapon types.
	 * @return the weapon types
	 */
	public String getWeaponTypes()
	{
		return weaponTypes;
	}

	/**
	 * Get the weapon reach formula.
	 * @return String the weaopn reach formula
	 * @deprecated due to EQREACH code control
	 */
	@Deprecated
	public String getWeaponReachFormula ()
	{
		return this.weaponReachFormula;
	}

	/**
	 * Get the XP awards.
	 * @return the XP awards
	 */
	public Map<Integer, Integer> getXPAwards()
	{
		return xpAwardsMap;
	}

	/**
	 * Get the CR steps for CRs lower than CR 1.
	 * @return the CR steps
	 */
	public Map<Integer, String> getCRSteps()
	{
		return crStepsMap;
	}

	/**
	 * Get the internal Integer representation for a CR.
	 * @return the CR steps
	 */
	public Integer getCRInteger(String cr)
	{
		if (cr.startsWith("1/"))
		{
			for (Map.Entry<Integer, String> entry : crStepsMap.entrySet())
			{
				if (entry.getValue().equals(cr))
				{
					return entry.getKey();
				}
			}
			return null;
		}
		return Integer.parseInt(cr);
	}

	/**
	 * Get the CR steps for CRs lower than CR 1.
	 * @return the CR steps
	 */
	public String getCRThreshold()
	{
		return crThreshold;
	}

	/**
	 * Return true if the AC Type is Valid.
	 * @param ACType
	 * @return true if the AC Type is Valid
	 */
	@Deprecated
	public boolean isValidACType(final String ACType)
	{
		return ACTypeAddMap.containsListFor(ACType)
				|| ACTypeRemoveMap.containsListFor(ACType);
	}

	/**
	 * Appends to the ACTypeRemoveMap.
	 * @param ACType
	 * @param controls
	 */
	@Deprecated
	public void addACRemoves(final String ACType,
			Collection<ACControl> controls)
	{
		ACTypeRemoveMap.addAllToListFor(ACType, controls);
	}

	/**
	 * Appends to the ACTypeAddMap.
	 * @param ACType
	 * @param controls
	 */
	@Deprecated
	public void addACAdds(final String ACType, Collection<ACControl> controls)
	{
		ACTypeAddMap.addAllToListFor(ACType, controls);
	}

	/**
	 * Add Class Type.
	 * 
	 * @param aString
	 */
	public void addClassType(final String aString)
	{
		if (Constants.LST_DOT_CLEAR.equals(aString))
		{
			classTypeList = null;

			return;
		}

		if (classTypeList == null)
		{
			classTypeList = new ArrayList<>();
		}

		final ClassType aClassType = new ClassType();
		final StringTokenizer aTok = new StringTokenizer(aString, "\t");
		aClassType.setName(aTok.nextToken().intern()); //Name of the Class Type

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if (bString.startsWith("CRFORMULA:"))
			{
				aClassType.setCRFormula(bString.substring(10));
			}
			else if (bString.startsWith("CRMOD:"))
			{
				aClassType.setCRMod(bString.substring(6));
			}
			else if (bString.startsWith("CRMODPRIORITY:"))
			{
				try 
				{
					aClassType.setCRModPriority(new Integer(bString.substring(14)));
				}
				catch(NumberFormatException e)
				{
					Logging.errorPrint("Illegal value for miscinfo.CLASSTYPE.CRMODPRIORITY: " + bString.substring(14));
				}
			}
			else if (bString.startsWith("XPPENALTY:"))
			{
				aClassType.setXPPenalty(bString.substring(10).equals("YES"));
			}
			else if (bString.startsWith("ISMONSTER:"))
			{
				aClassType.setMonster(bString.substring(10).equals("YES"));
			}
			else
			{
				Logging.errorPrint("Incorrect tag in miscinfo.CLASSTYPE: " + bString);
			}
		}

		classTypeList.add(aClassType);
	}

	/**
	 * Add a data set to the list of Default Data Sets.
	 * @param dataSetKey The key of the data set to add.
	 */
	public void addDefaultDataSet(final String dataSetKey)
	{
		if (defaultDataSetList == null)
		{
			defaultDataSetList = new ArrayList<>();
		}

		defaultDataSetList.add(dataSetKey);
	}

	/**
	 * Empty the list of Default Data Sets.
	 */
	public void clearDefaultDataSetList()
	{
		defaultDataSetList.clear();
	}

	/**
	 * Add a row to the Deity List. The list will be created if necessary.
	 * The special value ".CLEAR" disposes of the list.
	 * @param argDeityLine
	 */
	public void addDeityList(final String argDeityLine)
	{
		if (Constants.LST_DOT_CLEAR.equals(argDeityLine))
		{
			defaultDeityList = null;

			return;
		}

		if (defaultDeityList == null)
		{
			defaultDeityList = new ArrayList<>();
		}

		defaultDeityList.add(argDeityLine);
	}

	/**
	 * Add Load String.
	 * @param aString
	 */
	public void addLoadString(final String aString)
	{
		loadStrings.add(aString);
	}

	/**
	 * Add Plus calculation.
	 * @param aString
	 */
	public void addPlusCalculation(final String aString)
	{
		final int idx = aString.indexOf('|');

		if (idx > 0)
		{
			if (plusCalcs == null)
			{
				plusCalcs = new HashMap<>();
			}

			plusCalcs.put(aString.substring(0, idx).toUpperCase(), aString.substring(idx + 1));
		}
	}

	/**
	 * Add a Weapon Category.
	 * @param aString
	 */
	public void addWeaponCategory(final String aString)
	{
		weaponCategories += ('|' + aString);
	}

	/**
	 * Add a Weapon Type.
	 * @param aString
	 */
	public void addWeaponType(final String aString)
	{
		weaponTypes += ('|' + aString);
	}

	/**
	 * Wield Categories.
	 * @param wCat
	 */
	public void addWieldCategory(final WieldCategory wCat)
	{
		getModeContext().getReferenceContext().importObject(wCat);
	}

	/**
	 * Set the XP Awards.
	 * @param aString
	 */
	public void setXPAwards(final String aString)
	{
		String sTmp = "";
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		
		while (aTok.hasMoreTokens())
		{
			try
			{
				sTmp = aTok.nextToken();
				final String xpAward[] = sTmp.split("=");
				xpAwardsMap.put(getCRInteger(xpAward[0]), new Integer(xpAward[1]));
			}
			catch (ArrayIndexOutOfBoundsException | NumberFormatException e)
			{
				Logging.errorPrint("Illegal value for miscinfo.XPAWARD: " + sTmp);
			}
		}
	}

	/**
	 * Set the CR steps for CRs lower than CR 1.
	 * @param aString
	 */
	public void setCRSteps(final String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		
		for (Integer index = 0; aTok.hasMoreTokens(); index--)
		{
			crStepsMap.put(index, aTok.nextToken());
		}
	}

	/**
	 * Set the CR key class threshold.
	 * @param aString
	 */
	public void setCRThreshold(final String aString)
	{
			crThreshold = aString;
	}

	@Override
	public int compareTo(final Object obj)
	{
		if (obj != null)
		{
			final int iOrder = ((GameMode) obj).getDisplayOrder();

			if (iOrder < displayOrder)
			{
				return 1;
			}
			else if (iOrder > displayOrder)
			{
				return -1;
			}

			//
			// Order matches, so put in alphabetical order
			//
			// should throw a ClassCastException for non-PObjects,
			// like the Comparable interface calls for
			return name.compareToIgnoreCase(((GameMode) obj).name);
		}
		return 1;
	}

	/**
	 * Set the weapon reach forumla.
	 * @param aString	the new weapon reach formula
	 * @deprecated due to EQREACH code control
	 */
	@Deprecated
	public void setWeaponReachFormula (String aString)
	{
		this.weaponReachFormula = aString;
	}

	/**
	 * Set the acAbbrev field.
	 */
	String getACAbbrev()
	{
		return acAbbrev;
	}

	/**
	 * Answer the information about AC.
	 * @return AC text
	 */
	public String getACText()
	{
		return acName;
	}

	/**
	 * Answer with the alignment.
	 * @return alignment name
	 */
	String getAlignmentText()
	{
		return alignmentName;
	}


	public List<String> getAllowedModes()
	{
		if (allowedModes == null)
		{
			final List<String> modes = new ArrayList<>(1);
			modes.add(name);

			return modes;
		}

		return allowedModes;
	}


	String getAltHPAbbrev()
	{
		return althpAbbrev;
	}

	/**
	 * Wound Points.
	 * @return alt hp name
	 */
	String getAltHPText()
	{
		return althpName;
	}

	/**
	 * Levels at which all characters get bonus feats.
	 * @return List
	 */
	List<String> getBonusFeatLevels()
	{
		return bonusFeatLevels;
	}

	/**
	 * Levels at which all characters get bonus to stats.
	 * @return List
	 */
	List<String> getBonusStatLevels()
	{
		return bonusStatLevels;
	}

	/**
	 * Currency abbreviation.
	 * @return currency unit abbreviation
	 */
    @Override
	public String getCurrencyDisplay()
	{
		return currencyUnitAbbrev;
	}

	/**
	 * Get Damage Resistance Text.
	 * @return Get Damage Resistance Text
	 */
	public String getDamageResistanceText()
	{
		return damageResistance;
	}

	/**
	 * Default spell book name.
	 * @return default spell book
	 */
	String getDefaultSpellBook()
	{
		return defaultSpellBook;
	}

	/**
	 * Default unit set.
	 * @return default unit set
	 */
	String getDefaultUnitSet()
	{
		return defaultUnitSet;
	}

	/**
	 * Answer the deity list. May be null.
	 * @return default unit set
	 */
	List<String> getDeityList()
	{
		return defaultDeityList;
	}

	/**
	 * Gets the list of default data sets.
	 *
	 * @return the default data set list
	 */
    @Override
	public List<String> getDefaultDataSetList()
	{
		return defaultDataSetList;
	}

	/**
	 * Answer the preferred display order.
	 * @return default unit set
	 */
	int getDisplayOrder()
	{
		return displayOrder;
	}

	/**
	 * HP.
	 * @return HP abbreviation
	 */
	String getHPAbbrev()
	{
		return hpAbbrev;
	}


	String getHPText()
	{
		return hpName;
	}


	List<String> getLoadStrings()
	{
		return loadStrings;
	}

	/**
	 * Answer the unit of currency.
	 * @return currency unit
	 */
	String getLongCurrencyDisplay()
	{
		return currencyUnit;
	}


	public String getRankModFormula()
	{
		return rankModFormula;
	}

	boolean getShowClassDefense()
	{
		return showClassDefense;
	}

	List<String> getSkillMultiplierLevels()
	{
		return skillMultiplierLevels;
	}

	String getVariableDisplay2Name()
	{
		return displayVariable2Name;
	}

	String getVariableDisplay2Text()
	{
		return displayVariable2Text;
	}

	String getVariableDisplay3Name()
	{
		return displayVariable3Name;
	}

	String getVariableDisplay3Text()
	{
		return displayVariable3Text;
	}

	String getVariableDisplayName()
	{
		return displayVariableName;
	}

	/**
	 * Variable Display.
	 * @return variable display text
	 */
	String getVariableDisplayText()
	{
		return displayVariableText;
	}

	/**
	 * Set the range increment penalty for ranged weapons.
	 *
	 * @param value
	 *            For penalties set this to be a negative number, for bonuses to
	 *            long range set this to be a positive number, for no range
	 *            penalty set this to be 0
	 */
	public void setRangePenalty(int value) {
		rangePenalty = value;
	}

	/**
	 * Get the range penalty.
	 * @return range penalty
	 */
	public int getRangePenalty() {
		return rangePenalty;
	}


	/**
	 * Set the cost for class skills
	 * @param value
	 */
	public void setSkillCost_Class(int value)
	{
		skillCosts_Class = value;
	}

	/**
	 * Set the cost for cross class skills.
	 * @param value
	 */
	public void setSkillCost_CrossClass(int value)
	{
		skillCost_CrossClass = value;
	}

	/**
	 * Set the cost for exclusive skills.
	 * @param value
	 */
	public void setSkillCost_Exclusive(int value)
	{
		skillCost_Exclusive = value;
	}

	/**
	 * Get the cost for class skills.
	 * @return cost for class skills
	 */
	public int getSkillCost_Class()
	{
		return skillCosts_Class;
	}

	/**
	 * Get the cost for cross class skills.
	 * @return cost for cross class skills
	 */

	public int getSkillCost_CrossClass()
	{
		return skillCost_CrossClass;
	}

	/**
	 * Get the cost for exclusive skills.
	 * @return cost for exclusive skills
	 */
	public int getSkillCost_Exclusive()
	{
		return skillCost_Exclusive;
	}

	/**
	 * Set the point pool name.
	 * @param argPoolName
	 */
	public void setPointPoolName(final String argPoolName)
	{
		pointPoolName = argPoolName;
	}

	/**
	 * Get the point pool name.
	 * @return point pool name
	 */
	String getPointPoolName()
	{
		return pointPoolName;
	}

	/**
	 * Set the penalty for non proficiency.
	 * @param argPenalty
	 */
	public void setNonProfPenalty(final int argPenalty)
	{
		nonProfPenalty = argPenalty;
	}

	/**
	 * Get the penalty for non proficiency.
	 * @return the penalty for non proficiency
	 */
	public int getNonProfPenalty()
	{
		return nonProfPenalty;
	}

	/**
	 * Set the HP Formula.
	 * @param argFormula
	 */
	public void setHPFormula(final String argFormula)
	{
		hpFormula = argFormula;
	}

	/**
	 * Get the HP Formula.
	 * @return HP Formula
	 */
	public String getHPFormula()
	{
		return hpFormula;
	}

	/**
	 * Set Add with meta magic message.
	 * @param argMsg
	 */
	public void setAddWithMetamagicMessage(final String argMsg)
	{
		addWithMetamagic = argMsg;
	}

	/**
	 * Get add with meta magic message.
	 * @return add with meta magic message
	 */
    @Override
	public String getAddWithMetamagicMessage()
	{
		return addWithMetamagic;
	}

	/**
	 * Set square size.
	 * @param argSize
	 */
	public void setSquareSize(final double argSize)
	{
		squareSize = argSize;
	}

	/**
	 * Get square size.
	 * @return square size
	 */
	public double getSquareSize()
	{
		return squareSize;
	}

	/**
	 * Get the set unit.
	 * @return the unti that is set
	 */
	public UnitSet getUnitSet()
	{
		return selectedUnitSet;
	}

	/**
	 * Return true if the unit has been set.
	 * @param unitSetName
	 * @return true if the unit has been set
	 */
	public boolean selectUnitSet(final String unitSetName)
	{
		final UnitSet ui = getModeContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(UnitSet.class, unitSetName);
		if (ui == null)
		{
			return false;
		}
		selectedUnitSet = ui;
		return true;
	}

	/**
	 * Return true if the default unit has been set.
	 * @return true if the unit has been set
	 */
	public boolean selectDefaultUnitSet()
	{
		final UnitSet ui = getModeContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(UnitSet.class, defaultUnitSet);
		if (ui == null)
		{
			return false;
		}
		selectedUnitSet = ui;
		return true;
	}

	/**
	 * Add a PointBuyCost object to purchase mode stat costs.
	 * @param pbc
	 */
	public void addPointBuyStatCost(final PointBuyCost pbc)
	{
		if (pointBuyStatCosts == null)
		{
			// Sort NUMERICALLY, not alphabetically!
			// CONSIDER Huh? The natural order of Integer IS numerically... - thpr 10/20/06
			pointBuyStatCosts = new TreeMap<>(new ComparableComparator<>());
		}
		abilityScoreCost = null;
		pointBuyStatCosts.put(pbc.getStatValue(), pbc);
	}

	/**
	 * Clear purchase mode stat costs.
	 */
	public void clearPointBuyStatCosts()
	{
		pointBuyStatCosts = null;
		abilityScoreCost = null;
	}

	/**
	 * Get the point buy by stat mapping.
	 * @return the point buy by stat mapping
	 */
	SortedMap<Integer, PointBuyCost> getPointBuyStatCostMap()
	{
		return pointBuyStatCosts;
	}

	/**
	 * Find a user-defined purchase method by name.
	 * @param methodName
	 * @return the purchase method or null
	 */
	public PointBuyMethod getPurchaseMethodByName(final String methodName)
	{
		return getModeContext().getReferenceContext().silentlyGetConstructedCDOMObject(
				PointBuyMethod.class, methodName);
	}

	/**
	 * Set the purchase mode method name.
	 * @param argMethodName
	 */
	public void setPurchaseMethodName(final String argMethodName)
	{
		if (!argMethodName.isEmpty())
		{
			setRollMethod(Constants.CHARACTER_STAT_METHOD_PURCHASE);
		}

		purchaseMethodName = argMethodName;
		gamemodePrefsContext.setProperty("purchaseMethodName", argMethodName); //$NON-NLS-1$
	}

	/**
	 * Get the highest stat score that can be purchased free.
	 * @param aPC
	 * @return purchase mode base score
	 */
	public int getPurchaseModeBaseStatScore(final PlayerCharacter aPC)
	{
		int minVal = getPurchaseScoreMin(aPC);
		for (int i = 0, x = getPurchaseScoreMax() - getPurchaseScoreMin() + 1; i < x; ++i)
		{
			if (getAbilityScoreCost(i) == 0)
			{
				if ((getPurchaseScoreMin() + i) >= minVal)
				{
					return getPurchaseScoreMin() + i;
				}
			}
		}

		//
		// Make sure that the minimum stat value is legal. This could happen if there are no
		// stat values that are considered to be free.
		//
		if (getPurchaseScoreMin() == minVal)
		{
			minVal -= 1;
			if (minVal < statMin)
			{
				minVal = statMin;
			}
		}

		return minVal;
	}

	/**
	 * Get the purchase mode method name.
	 * @return the purchase mode method name
	 */
	public String getPurchaseModeMethodName()
	{
		if (!isPurchaseStatMode())
		{
			return null;
		}

		return purchaseMethodName;
	}

	/**
	 * Get the purchase mode method pool formula.
	 * @return the purchase mode method pool formula
	 */
	public String getPurchaseModeMethodPoolFormula()
	{
		if (!isPurchaseStatMode())
		{
			return "-1";
		}

		return getPurchaseMethodByName(purchaseMethodName).getPointFormula();
	}

	/**
	 * Get the highest stat value in the purchase mode stat table.
	 * @return purchase mode maximum
	 */
	public int getPurchaseScoreMax()
	{
		if (pointBuyStatCosts == null)
		{
			return -1;
		}

		return pointBuyStatCosts.lastKey();
	}

	/**
	 * Get the maximum score you can purchase.
	 * @param pc
	 * @return the maximum score you can purchase
	 */
	public int getPurchaseScoreMax(final PlayerCharacter pc)
	{
		if (pc == null)
		{
			return getPurchaseScoreMax();
		}

		int lastStat = -1;
		if (pointBuyStatCosts != null)
		{
			for ( Integer statValue : pointBuyStatCosts.keySet() )
			{
				final PointBuyCost pbc = pointBuyStatCosts.get(statValue);
				if (pbc.qualifies(pc, null))
				{
					lastStat = statValue;
				}
			}
		}
		return lastStat;
	}

	/**
	 * Get the lowest stat value in the purchase mode stat table.
	 * @return purchase score minimum
	 */
	public int getPurchaseScoreMin()
	{
		if (pointBuyStatCosts == null)
		{
			return -1;
		}

		return pointBuyStatCosts.firstKey();
	}

	/**
	 * Get the minimum score you can purchase.
	 * @param pc
	 * @return the minimum score you can purchase
	 */
	public int getPurchaseScoreMin(final PlayerCharacter pc)
	{
		if (pc == null)
		{
			return getPurchaseScoreMin();
		}

		int lastStat = -1;
		if (pointBuyStatCosts != null)
		{
			for ( int statValue : pointBuyStatCosts.keySet() )
			{
				final PointBuyCost pbc = pointBuyStatCosts.get(statValue);
				if (pbc.qualifies(pc, null))
				{
					lastStat = statValue;
					break;
				}
			}
		}
		return lastStat;
	}

	/**
	 * Returns true if we are in a stat purchase mode.
	 * @return true if we are in a stat purchase mode
	 */
	public boolean isPurchaseStatMode()
	{
		// Can't have purchase mode if no costs specified
		if ((pointBuyStatCosts == null) || (pointBuyStatCosts.isEmpty())
			|| (getRollMethod() != Constants.CHARACTER_STAT_METHOD_PURCHASE)
			|| (purchaseMethodName.isEmpty()))
		{
			return false;
		}

		return getPurchaseMethodByName(purchaseMethodName) != null;
	}

	/**
	 *
	 * @param argRollMethod
	 */
	public void setRollMethod(final int argRollMethod)
	{
		rollMethod = argRollMethod;
		gamemodePrefsContext.setInt("rollMethod", argRollMethod); //$NON-NLS-1$

		if (argRollMethod != Constants.CHARACTER_STAT_METHOD_PURCHASE)
		{
			setPurchaseMethodName(""); //$NON-NLS-1$
		}
	}

	/**
	 *
	 * @return roll method
	 */
	public int getRollMethod()
	{
		return rollMethod;
	}

	/**
	 * Get the cost for an ability score.
	 * @return the cost for an ability score
	 */
	public int[] getAbilityScoreCost()
	{
		if (!isPurchaseStatModeAllowed())
		{
			//better to return a Zero length array than null.
			return null;
		}

		// Only build this list once
		if (abilityScoreCost != null)
		{
			return abilityScoreCost;
		}

		abilityScoreCost = new int[getPurchaseScoreMax() - getPurchaseScoreMin() + 1]; // Should be 1 value for each stat in range

		int i = 0;
		int lastStat = Integer.MIN_VALUE;
		int lastCost = 0;

		for ( int statValue : pointBuyStatCosts.keySet() )
		{
			// Fill in any holes in the stat list by using the previous stat cost
			if ((lastStat != Integer.MIN_VALUE) && (lastStat + 1 != statValue))
			{
				for (int x = lastStat + 1; x < statValue; ++x)
				{
					abilityScoreCost[i++] = lastCost;
				}
			}

			final int statCost = pointBuyStatCosts.get(statValue).getBuyCost();
			lastStat = statValue;
			lastCost = statCost;
			abilityScoreCost[i++] = lastCost;
		}

		return abilityScoreCost;
	}

	/**
	 * Get the cost for an ability score.
	 * @param abilityScoreIndex
	 * @return the cost for an ability score
	 */
	public int getAbilityScoreCost(final int abilityScoreIndex)
	{
		final int[] asc = getAbilityScoreCost();

		if (asc == null)
		{
			return 0;
		}

		return asc[abilityScoreIndex];
	}

	/**
	 * Set the roll method expression by name.
	 * @param aString
	 */
	public void setRollMethodExpressionByName(final String aString)
	{
		activeRollMethod = getModeContext().getReferenceContext().silentlyGetConstructedCDOMObject(RollMethod.class, aString);
		if (activeRollMethod == null)
		{
			setRollMethod(Constants.CHARACTER_STAT_METHOD_USER);
		}
		else
		{
			setRollMethod(Constants.CHARACTER_STAT_METHOD_ROLLED);
		}
		gamemodePrefsContext.setProperty("rollMethodExpression", aString); //$NON-NLS-1$
	}

	/**
	 * Get the cost for an ability score.
	 * @return the cost for an ability score
	 */
	public String getRollMethodExpression()
	{
		if (activeRollMethod != null)
		{
			return activeRollMethod.getMethodRoll();
		}
		return "";
	}

	/**
	 * Get roll method expression name.
	 * @return roll method expression name
	 */
	public String getRollMethodExpressionName()
	{
		if (activeRollMethod != null)
		{
			return activeRollMethod.getDisplayName();
		}
		return "";
	}

	/**
	 * Return true if the purchasing of stats mode is allowed.
	 * @return true if the purchasing of stats mode is allowed
	 */
	private boolean isPurchaseStatModeAllowed()
	{
		return !((pointBuyStatCosts == null) || (pointBuyStatCosts.isEmpty()));
	}

	/**
	 * Set the value for all stats.
	 * @param argAllStatsValue
	 */
	public void setAllStatsValue(final int argAllStatsValue)
	{
		allStatsValue = argAllStatsValue;
		gamemodePrefsContext.setInt("allStatsValue", argAllStatsValue); //$NON-NLS-1$
	}

	/**
	 * Get the value of all stats.
	 * @return the value of all stats
	 */
	public int getAllStatsValue()
	{
		return allStatsValue;
	}

	/**
	 * Returns the currently set rolling method for character stats.
	 *
	 * @return RollMethod the current rolling method
	 */
	public RollMethod getCurrentRollingMethod()
	{
		return activeRollMethod;
	}

	/**
	 * Set the minimum stat.
	 * @param argMin
	 */
	public void setStatMin(final int argMin)
	{
		statMin  = argMin;
	}

	/**
	 * Get the minimum stat.
	 * @return minimum stat
	 */
	public int getStatMin()
	{
		return statMin;
	}

	/**
	 * Set the maximum stat.
	 * @param argMax
	 */
	public void setStatMax(final int argMax)
	{
		statMax  = argMax;
	}

	/**
	 * Get the maximum stat.
	 * @return maximum stat
	 */
	public int getStatMax()
	{
		return statMax;
	}

	/**
	 * Return an <b>unmodifiable</b> version of the schools list.
	 * @return an <b>unmodifiable</b> version of the schools list.
	 */
	public List<String> getUnmodifiableSchoolsList()
	{
		return Collections.unmodifiableList(schoolsList);
	}

	/**
	 * Add the school to the list.
	 * @param school
	 */
	public void addToSchoolList(final String school)
	{
		if (!schoolsList.contains(school))
		{
			schoolsList.add(school);
		}
	}

	/**
	 * Add the display text for a stat.
	 * @param statValue
	 * @param statText
	 */
	public void addStatDisplayText(final int statValue, final String statText)
	{
		if (statDisplayText == null)
		{
			// Sort NUMERICALLY, not alphabetically!
			// CONSIDER Huh? The natural order of Integer IS numerically... - thpr 10/20/06
			statDisplayText = new TreeMap<>(new ComparableComparator<>());
		}
		statDisplayText.put(statValue, statText);
	}

	/**
	 * Get the display text of a stat.
	 * @param statValue
	 * @return the display text of a stat
	 */
	public String getStatDisplayText(final int statValue)
	{
		String statText;
		//
		// If no alternate text available, then display the number only
		//
		if (statDisplayText == null)
		{
			statText = Integer.toString(statValue);
		}
		else
		{
			statText = statDisplayText.get(statValue);
			if (statText == null)
			{
				final int firstKey = statDisplayText.firstKey();
				if (statValue < firstKey)
				{
					statText = "???" + Integer.toString(statValue) + "???";
				}
				else
				{
					final int lastKey = statDisplayText.lastKey();

					statText = getStatDisplayText(lastKey) + statDisplayTextAppend + getStatDisplayText(statValue - lastKey);
				}
			}
		}
		return statText;
	}

	/**
	 * Return true if the skill rank display text is there.
	 * @return true if the skill rank display text is there
	 */
	public boolean hasSkillRankDisplayText()
	{
		return (skillRankDisplayText == null) ? false : true;
	}

	/**
	 * Add display text for a skill rank.
	 * @param rankValue
	 * @param rankText
	 */
	public void addSkillRankDisplayText(final int rankValue, final String rankText)
	{
		if (skillRankDisplayText == null)
		{
			// Sort NUMERICALLY, not alphabetically!
			// CONSIDER Huh? The natural order of Integer IS numerically... - thpr 10/20/06
			skillRankDisplayText = new TreeMap<>(new ComparableComparator<>());
		}
		skillRankDisplayText.put(rankValue, rankText);
	}

	/**
	 * Get display text for a skill rank.
	 * @param rankValue
	 * @return display text for a skill rank
	 */
	public String getSkillRankDisplayText(final int rankValue)
	{
		String rankText;
		//
		// If no alternate text available, then display the number only
		//
		if (skillRankDisplayText == null)
		{
			rankText = Integer.toString(rankValue);
		}
		else
		{
			rankText = skillRankDisplayText.get(rankValue);
			if (rankText == null)
			{
				final int firstKey = skillRankDisplayText.firstKey();
				if (rankValue < firstKey)
				{
					rankText = "???" + Integer.toString(rankValue) + "???";
				}
				else
				{
					final int lastKey = skillRankDisplayText.lastKey();

					rankText = getSkillRankDisplayText(lastKey) + statDisplayTextAppend + getSkillRankDisplayText(rankValue - lastKey);
				}
			}
		}
		return rankText;
	}


	//BONUSSTACKLIST

	/**
	 * Add an item to the bonus stacking list.
	 * @param item
	 */
	public void addToBonusStackList(final String item)
	{
		if(!bonusStackList.contains(item))
		{
			bonusStackList.add(item);
		}
	}

	/**
	 * Clears the bonus stacking list.
	 */
	public void clearBonusStacksList()
	{
		bonusStackList.clear();
	}

	/**
	 * Return an <b>unmodifiable</b> version of the bonus stacking list.
	 * @return an <b>unmodifiable</b> version of the bonus stacking list.
	 */
	List<String> getUnmodifiableBonusStackList()
	{
		return Collections.unmodifiableList(bonusStackList);
	}

	/**
	 * Gets the <tt>AbilityCategory</tt> for the given key.
	 *
	 * @param aKey The key of the <tt>AbilityCategory</tt> to retreive.
	 *
	 * @return The requested <tt>AbilityCategory</tt> or <tt>null</tt> if the
	 * category is not found in this game mode.
	 */
	public AbilityCategory getAbilityCategory(final String aKey)
	{
		AbilityCategory ac = silentlyGetAbilityCategory(aKey);
		// Empty aKey indicates return null because
		// PreAbilityTester.buildAbilityList uses that as a global
		// (all Category) getch
		if (aKey == null || (ac == null && !aKey.isEmpty()))
		{
			Logging.errorPrint("Attempt to fetch AbilityCategory: " + aKey
				+ "... but it does not exist");
		}
		return ac;
	}


	private AbilityCategory silentlyGetAbilityCategory(final String aKey)
	{
		AbilityCategory cat = getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(AbilityCategory.class, aKey);
		if (cat != null)
		{
			return cat;
		}
		if (AbilityCategory.LANGBONUS.getKeyName().equals(aKey))
		{
			return AbilityCategory.LANGBONUS;
		}
		return null;
	}

	/**
	 * Returns a <tt>Collection</tt> of <tt>AbilityCategory</tt> objects defined
	 * by this game mode.
	 *
	 * @return A <tt>Collection</tt> of <tt>AbilityCategory</tt> objects.
	 */
	public Collection<AbilityCategory> getAllAbilityCategories()
	{
		return getContext().getReferenceContext()
				.getConstructedCDOMObjects(AbilityCategory.class);
	}

	/**
	 * Returns a <tt>Collection</tt> of <tt>AbilityCategory</tt> objects
	 * defined by this game mode that match the category key.
	 *
	 * @param key The category key to filter for.
	 * @return A <tt>Collection</tt> of <tt>AbilityCategory</tt> objects.
	 */
	public Collection<AbilityCategory> getAllAbilityCatsForKey(String key)
	{
		if (key == null)
		{
			return Collections.emptyList();
		}
		List<AbilityCategory> catList = new ArrayList<>();
		for (AbilityCategory cat : getAllAbilityCategories())
		{
			if (key.equals(cat.getKeyName())
				|| key.equals(cat.getParentCategory().getKeyName()))
			{
				catList.add(cat);
			}
		}
		return Collections.unmodifiableCollection(catList);
	}


	public void setPreviewDir(final String aDir)
	{
		thePreviewDir = aDir;
	}


	private String getPreviewDir()
	{
		return thePreviewDir;
	}


	public void setDefaultPreviewSheet(final String aSheet)
	{
		theDefaultPreviewSheet = aSheet;
	}


	public String getDefaultPreviewSheet()
	{
		return theDefaultPreviewSheet;
	}

	/**
	 * Parses the DIESIZE tag's values to create
	 * the dieSizes array.
	 *
	 * @param value
	 */
	public void setDieSizes(final String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, ",", false);
		List<Integer> list = new ArrayList<>();
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			// in case there is training\leading whitespace after the comma split
			aString = aString.trim();

			try
			{
			if (aString.contains("MIN="))
				{
					String[] t = aString.split("MIN=");
					String minValue = t[1];
					int die = Integer.parseInt(minValue);
					setMinDieSize(die);
					list.add(die);
				}
				else if (aString.contains("MAX="))
				{
					String[] t = aString.split("MAX=");
					String maxValue = t[1];
					int die = Integer.parseInt(maxValue);
					setMaxDieSize(die);
					list.add(die);
				}
				else
				{
					int die = Integer.parseInt(aString);
					list.add(die);
				}
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Invalid integer value for DIESIZES: " + aString + ".  Original value: DIESIZES:"+ value);
			}

		}
		if (list.isEmpty())
		{
			return;
		}

		int[] dieSizes = new int[list.size()];

		for (int i = 0; i < list.size(); i++)
		{
			dieSizes[i] = list.get(i);
		}
		this.setDieSizes(dieSizes);
	}

	/**
	 * Get's current gamemodes MaxDieSize.
	 * @return maxDieSize
	 */
	public int getMaxDieSize()
	{
		return maxDieSize;
	}
	/**
	 * Sets's current gamemodes MaxDieSize.
	 * @param dice
	 */
	private void setMaxDieSize(final int dice)
	{
		maxDieSize = dice;
	}

	/**
	 * Get's current gamemodes MinDieSize.
	 * @return minDieSize
	 */
	public int getMinDieSize()
	{
		return minDieSize;
	}
	/**
	 * Sets's current gamemodes MinDieSize.
	 * @param dice
	 */
	private void setMinDieSize(final int dice)
	{
		minDieSize = dice;
	}


	/**
	 * Get's current gamemodes DieSizes.
	 * @return dieSizes array
	 */
	public int[] getDieSizes()
	{
		return dieSizes;
	}

	/**
	 * Set's DieSizes available for the gamemode.
	 * @param die The parsed integer diesizes
	 */
	public void setDieSizes(int[] die)
	{
		this.dieSizes = die;
	}

	/**
	 * Retrieve the list of equipment types which flag it as able to
	 * be resized by the automatic resize feature.
	 * @return the resizableTypeList
	 */
	List<String> getResizableTypeList()
	{
		return Collections.unmodifiableList(resizableTypeList);
	}

	/**
	 * Set the list of equipment types which flag it as able to
	 * be resized by the automatic resize feature.
	 *
	 * @param resizableTypeList the resizableTypeList to set
	 */
	public void setResizableTypeList(List<String> resizableTypeList)
	{
		this.resizableTypeList = resizableTypeList;
	}

	/**
	 * Retrieve the list of character types (e.g. PC or NPC).
	 * @return the characterTypeList
	 */
	public List<String> getCharacterTypeList()
	{
		return Collections.unmodifiableList(characterTypeList);
	}

	/**
	 * Set the list of character types (e.g. PC or NPC).
	 *
	 * @param characterTypeList the characterTypeList to set
	 */
	public void setCharacterTypeList(List<String> characterTypeList)
	{
		this.characterTypeList = characterTypeList;
	}

	/**
	 * Retrieve the list of monster roles.
	 * @return the monsterRoleList
	 */
	public List<String> getMonsterRoleList()
	{
		return Collections.unmodifiableList(monsterRoleList);
	}

	/**
	 * Set the list of known monster roles.
	 *
	 * @param monsterRoleList the monsterRoleList to set
	 */
	public void setMonsterRoleList(List<String> monsterRoleList)
	{
		this.monsterRoleList = monsterRoleList;
	}

	/**
	 * Retrieve the default monster role.
	 * @return the monsterRoleDefault
	 */
	public List<String> getMonsterRoleDefaultList()
	{
		return new ArrayList<>(Arrays.asList(monsterRoleDefault));
	}

	/**
	 * Set the list of known monster roles.
	 *
	 * @param monsterRoleDefault the monsterRoleDefault to set.
	 */
	public void setMonsterRoleDefault(String monsterRoleDefault)
	{
		this.monsterRoleDefault = monsterRoleDefault;
	}

	private ConsolidatedListCommitStrategy masterLCS = new ConsolidatedListCommitStrategy();
	private LoadContext context = new RuntimeLoadContext(getRefContext(), masterLCS);
	private GameReferenceContext gameRefContext = new GameReferenceContext();
	private LoadContext modeContext = new RuntimeLoadContext(gameRefContext, masterLCS);
	private String defaultSourceTitle;


	public void clearLoadContext()
	{
		masterLCS = new ConsolidatedListCommitStrategy();
		AbstractReferenceContext referenceContext = getRefContext();
		resolveInto(referenceContext);
		context = new RuntimeLoadContext(referenceContext, masterLCS);
	}

	/**
	 * Takes references and abbreviations that have been placed into the
	 * LoadContext for this GameMode and copies those references and
	 * abbreviations into the given ReferenceContext
	 * 
	 * @param referenceContext
	 *            The Reference Context into which the references from this
	 *            GameMode should be copied.
	 */
	public void resolveInto(AbstractReferenceContext referenceContext)
	{
		for (ReferenceManufacturer<?> rm : gameRefContext.getAllManufacturers())
		{
			resolveReferenceManufacturer(referenceContext, rm);
		}
	}

	private AbstractReferenceContext getRefContext()
	{
		return SettingsHandler.inputUnconstructedMessages() ?
				new TrackingReferenceContext() :
				new RuntimeReferenceContext();
	}


	static <T extends Loadable> void resolveReferenceManufacturer(
			AbstractReferenceContext rc, ReferenceManufacturer<T> rm)
	{
		Class<T> c = rm.getReferenceClass();
		ReferenceManufacturer<T> mfg;
		if (Categorized.class.isAssignableFrom(c))
		{
			TransparentCategorizedReferenceManufacturer tcrm = (TransparentCategorizedReferenceManufacturer) rm;
			String category = tcrm.getCDOMCategory();
			Class catClass = tcrm.getCategoryClass();
			mfg = (ReferenceManufacturer<T>) rc.getManufacturer((Class) c, catClass, category);
		}
		else
		{
			mfg = rc.getManufacturer(c);
		}
		for (CDOMReference<T> ref : rm.getAllReferences())
		{
			((TransparentReference<T>) ref).resolve(mfg);
		}
		rm.injectConstructed(mfg);
	}


	public LoadContext getContext()
	{
		return context;
	}


	public LoadContext getModeContext()
	{
		return modeContext;
	}


	public MasterListInterface getMasterLists()
	{
		return masterLCS;
	}


	public void addHiddenType(Class<?> cl, String s)
	{
		Set<String> set = hiddenTypes.computeIfAbsent(
				cl,
				k -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)
		);
		set.add(s);
	}

	/**
	 * Gets the name of the currently selected default XP table.
	 *
	 * @return the XP table name
	 */
	public String getDefaultXPTableName()
	{
		if (defaultXPTableName == null || defaultXPTableName.equals("") || !xpTableNames.contains(defaultXPTableName))
		{
			if (xpTableNames.isEmpty())
			{
				xpTableNames.add("Default");
			}
			defaultXPTableName = xpTableNames.get(0);
		}
		return defaultXPTableName;
	}

	/**
	 * Sets the default experience table by name.
	 *
	 * @param xpTableName the new XP table name
	 */
	public void setDefaultXPTableName(String xpTableName)
	{
		defaultXPTableName = xpTableName;
	}

	/**
	 * Gets a list of names of all defined XP tables.
	 *
	 * @return the list of XP table names
	 */
	public List<String> getXPTableNames()
	{
		return xpTableNames;
	}

	/**
	 * Add a name for an XP tables.
	 *
	 * @param xpTableName the new XP table name
	 */
	public void addXPTableName(String xpTableName)
	{
		xpTableNames.add(xpTableName);
	}

	/**
	 * Gets the name of the currently selected default character type.
	 *
	 * @return the character type
	 */
	public String getDefaultCharacterType()
	{
		if (defaultCharacterType == null || defaultCharacterType.equals("") || !characterTypeList.contains(defaultCharacterType))
		{
			if (characterTypeList.isEmpty())
			{
				characterTypeList.add("Default");
			}
			defaultCharacterType = characterTypeList.get(0);
		}
		return defaultCharacterType;
	}

	/**
	 * Sets the default character type.
	 *
	 * @param characterType the new character type
	 */
	public void setDefaultCharacterType(String characterType)
	{
		defaultCharacterType = characterType;
	}

	/**
	 * Checks if bonus stat stacking is allowed.
	 *
	 * @return true, if is bonus stat stacking allowed
	 */
	public boolean isBonusStatAllowsStack()
	{
		return bonusStatAllowsStack;
	}

	/**
	 * Sets the bonus stat stacking allowed value.
	 *
	 * @param bonusStatAllowsStack the new bonus stat allows stack
	 */
	public void setBonusStatAllowsStack(boolean bonusStatAllowsStack)
	{
		this.bonusStatAllowsStack = bonusStatAllowsStack;
	}

	/**
	 *
	 * @return the bioSet
	 */
	public BioSet getBioSet()
	{
		return bioSet;
	}

	/**
	 *
	 * @param bioSet the bioSet to set
	 */
	public void setBioSet(BioSet bioSet)
	{
		this.bioSet = bioSet;
	}

	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * Sets the title of the default source (used on the quick sources dialog).
	 *
	 * @param title the new title
	 */
	public void setDefaultSourceTitle(String title)
	{
		this.defaultSourceTitle = title;
	}

	/**
	 * Gets the default source title.
	 *
	 * @return the default source title
	 */
    @Override
	public String getDefaultSourceTitle()
	{
		return defaultSourceTitle;
	}


	public String getTabName(Tab tab)
	{
		TabInfo ti = getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
				TabInfo.class, tab.toString());
		return ti.getResolvedName();
	}


	public boolean getTabShown(Tab tab)
	{
		TabInfo ti = getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
				TabInfo.class, tab.toString());
		return ti.isVisible();
	}


	public void setTabVisible(CDOMSingleRef<TabInfo> ref, Boolean set)
	{
		if (visibleTabs == null)
		{
			visibleTabs = new HashMap<>();
		}
		visibleTabs.put(ref, set);
	}


	public Boolean getTabVisibility(TabInfo ti)
	{
		if (visibleTabs == null)
		{
			return null;
		}
		for (Map.Entry<CDOMSingleRef<TabInfo>, Boolean> me : visibleTabs.entrySet())
		{
			if (ti.equals(me.getKey().get()))
			{
				return me.getValue();
			}
		}
		return null;
	}

	/*
	 * End SHOWTAB compatibility
	 */


	public LoadInfo getLoadInfo()
	{
		return getModeContext().getReferenceContext().silentlyGetConstructedCDOMObject(
				LoadInfo.class, getName());
	}

	/**
	 *
	 * @return the file name of the InfoSheet relative to the base pcgen directory
	 */
    @Override
	public String getInfoSheet()
	{
		return theInfoSheet;
	}

	/**
	 *
	 * @param theInfoSheet the file name of the InfoSheet relative to the base pcgen directory
	 */
	public void setInfoSheet(String theInfoSheet)
	{
		this.theInfoSheet = theInfoSheet;
	}

	/**
	 *
	 * @return the file name of the skill InfoSheet relative to the base pcgen directory
	 */
    @Override
	public String getInfoSheetSkill()
	{
		return theInfoSheetSkill;
	}

	/**
	 *
	 * @param theInfoSheetSkill the file name of the skill InfoSheet relative to the base pcgen directory
	 */
	public void setInfoSheetSkill(String theInfoSheetSkill)
	{
		this.theInfoSheetSkill = theInfoSheetSkill;
	}
	
	/**
	 *
	 * @param theOutputSheetDirectory the directory for output sheets for the current game mode
	 */
	public void setOutputSheetDirectory(String theOutputSheetDirectory)
	{
		this.outputSheetDirectory = theOutputSheetDirectory;
	}

	/**
	 *
	 * @return the directory for output sheets for the current game mode
	 */
    @Override
	public String getOutputSheetDirectory()
	{
		return outputSheetDirectory;
	}

	/**
	 *
	 * @param sheet the file name of the InfoSheet relative to the base pcgen directory
	 */
	public void setOutputSheetDefault(String type, String sheet)
	{
		this.outputSheetDefaultMap.put(type, sheet);
	}
	
	/**
	 *
	 * @return the directory for output sheets for the current game mode
	 */
    @Override
	public String getOutputSheetDefault(String type)
	{
		return outputSheetDefaultMap.get(type);
	}

	/**
	 * Register an icon to be used for equipment of the listed type.
	 * @param equipType The equipment type
	 * @param iconPath The path relative to the pcgen folder of the icon.
	 * @param priority The importance of this icon, higher means more important
	 */
	public void setEquipTypeIcon(String equipType, String iconPath, int priority)
	{
		this.equipTypeIconMap.put(equipType.toUpperCase(), iconPath);
		this.equipTypeIconPriorityMap.put(equipType.toUpperCase(), priority);
	}

	/**
	 * Retrieve the default icon to be used for equipment of the listed type.
	 * @param equipType The equipment type
	 * @return The path relative to the pcgen folder of the icon, null if none exists.
	 */
	public String getEquipTypeIcon(String equipType)
	{
		return this.equipTypeIconMap.get(equipType.toUpperCase());
	}

	/**
	 * Retrieve the priority of the listed type;s icon. A higher number means a higher 
	 * priority, generally the highest priority icon will be used.
	 * @param equipType The equipment type
	 * @return The priority, or 0 if none is known.
	 */
	int getEquipTypeIconPriority(String equipType)
	{
		Integer priority =
				this.equipTypeIconPriorityMap.get(equipType.toUpperCase());
		return priority == null ? 0 : priority;
	}
	
	@Override
	public String getCharSheetDir()
	{
		return getPreviewDir();
	}

	@Override
	public String getDefaultCharSheet()
	{
		return getDefaultPreviewSheet();
	}

	@Override
	public String getHeightUnit()
	{
		return "ftin".equals(getUnitSet().getHeightUnit()) ?
				"inches" :
				getUnitSet().getHeightUnit();
	}

	@Override
	public String getWeightUnit()
	{
		return getUnitSet().getWeightUnit();
	}


	AbilityCategory getFeatTemplate()
	{
		return featTemplate;
	}


	public void setFeatTemplate(AbilityCategory featTemplate)
	{
		this.featTemplate = featTemplate;
	}

	public void setMaxNonEpicLevel(int i)
	{
		maxNonEpicLevel = i;
	}
	
	public int getMaxNonEpicLevel()
	{
		return maxNonEpicLevel;
	}
}
