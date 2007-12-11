/*
 * GameMode.java
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
 *
 * Created on September 22, 2002, 4:30 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.system.GameModeRollMethod;
import pcgen.util.ComparableComparator;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;


/**
 * Handles game modes.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class GameMode implements Comparable<Object>
{
	private static PObject eqSizePenalty = new PObject();
	private List<String> allowedModes;
	private List<String> bonusFeatLevels = new ArrayList<String>();
	private Map<String, String> bonusSpellMap = new HashMap<String, String>();		// key is level of bonus spell, value is "basestatscore|statrange"
	private List<String> bonusStackList = new ArrayList<String>();
	private List<String> bonusStatLevels = new ArrayList<String>();
	private List<ClassType> classTypeList = new ArrayList<ClassType>();
	private List<String> defaultDeityList = new ArrayList<String>();
	private List<String> hiddenEquipmentTypes = null;
	private List<String> hiddenAbilityTypes = null;
	private List<String> hiddenSkillTypes = null;
	private Map<String, LevelInfo> levelInfo = new HashMap<String, LevelInfo>();
	private List<String> loadStrings = new ArrayList<String>();
	private List<String> skillMultiplierLevels = new ArrayList<String>();
	private List<String> wcStepsList = new ArrayList<String>();
	private List<WieldCategory> wieldCategoryList = new ArrayList<WieldCategory>();
	private Map<String, List<String>> ACTypeAddMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> ACTypeRemoveMap = new HashMap<String, List<String>>();
	private Map<String, String> damageDownMap = new HashMap<String, String>();
	private Map<String, String> damageUpMap = new HashMap<String, String>();
	private Map<String, String> plusCalcs;
	private Map<String, String> spellRangeMap = new HashMap<String, String>();
	private String acAbbrev = "";
	private String acFlatBonus = "";
	private String acName = "";
	private String acTouchBonus = "";
	private String alignmentName = "";
	private String althpAbbrev = "";
	private String althpName = "";
	private String babAbbrev = null;
	private String currencyUnit = "";
	private String currencyUnitAbbrev = "";
	private String damageResistance = "";
	private String defaultSpellBook = "Known Spells";
	private String defaultUnitSet = Constants.s_STANDARD_UNITSET_NAME;
	private UnitSet selectedUnitSet = null;
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
	private String menuEntry = "";
	private String menuToolTip = "";
	private String name = "";
	private String spellBaseDC = "0";
	private String wcStepsFormula = "";
	private String weaponCategories = "";
	private String weaponTypes = "";
	private String weaponReachFormula = "";
	private String rankModFormula = "";
	private String addWithMetamagic = "";
	private SortedMap<String, RuleCheck> ruleCheckMap = new TreeMap<String, RuleCheck>();
	private Map<Tab,TabInfo> tInfo = new HashMap<Tab,TabInfo>(); 
	private boolean allowAutoResize = false;
	private boolean showClassDefense;
	private int babAttCyc = 5; //6
	private int babMaxAtt = Integer.MAX_VALUE; //4
	private int babMaxLvl = Integer.MAX_VALUE; //20
	private int babMinVal = 1;
	private int checksMaxLvl = Integer.MAX_VALUE; //20
	private int displayOrder = Integer.MAX_VALUE;
	private List<PCStat> statList = new ArrayList<PCStat>();
	/** String array of Attributes in long format */
	public String[] s_ATTRIBLONG;
	/** String array of Attributes in short format */
	public String[] s_ATTRIBSHORT;
	private final List<PObject> checkList = new ArrayList<PObject>();
	private final List<PCAlignment> alignmentList = new ArrayList<PCAlignment>(15);
	private final List<String> schoolsList = new ArrayList<String>(20);

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


	private List<GameModeRollMethod> rollingMethods = null;
	private int rollingMethodIndex = -1;

	private SortedMap<Integer, PointBuyCost> pointBuyStatCosts = null;
	private int[] abilityScoreCost = null;
	private List<PointBuyMethod> pointBuyMethods = null;
	private String purchaseMethodName = ""; //$NON-NLS-1$

	private int rollMethod = Constants.CHARACTERSTATMETHOD_USER;

	private final List<SizeAdjustment> sizeAdjustmentList = new ArrayList<SizeAdjustment>(9);
	private final SizeAdjustment spareSize = new SizeAdjustment();

	private int allStatsValue = 10;

	//
	// minimum and maximum stat values when creating new characters.
	//
	private int statMin = 3;
	private int statMax = 18;

	private TreeMap<Integer, String> statDisplayText = null;
	private String statDisplayTextAppend = "+";
	private TreeMap<Integer, String> skillRankDisplayText = null;

	private boolean [] summaryTabStatColumnVisible = { true, true, true, true, true, true, true };
	private boolean [] skillTabColumnVisible = { true, true, true, true, true, true, true };				// Skill, Modifier, Ranks, Total, Cost, Source, Order

	private List<AbilityCategory> theAbilityCategories = new ArrayList<AbilityCategory>(5);
	private List<AbilityCategory> theLstAbilityCategories = new ArrayList<AbilityCategory>();

	private String thePreviewDir;
	private String theDefaultPreviewSheet;
	private int [] dieSizes;
	private int maxDieSize = 12;
	private int minDieSize = 4;
	
	/**
	 * Creates a new instance of GameMode.
	 *
	 * @param modeName the mode name
	 */
	public GameMode(final String modeName)
	{
		name = modeName;
		folderName = modeName;
		thePreviewDir = modeName;
		theDefaultPreviewSheet = "preview.html"; //$NON-NLS-1$

		for (Tab aTab : Tab.values())
		{
			TabInfo ti = new TabInfo();
			ti.tabName = aTab.label();
			tInfo.put(aTab, ti);
		}
	}

	/**
	 * Set the AC Abbreviation
	 * @param aString
	 */
	public void setACAbbrev(final String aString)
	{
		acAbbrev = aString;
	}

	/**
	 * Set the AC Text
	 * @param aString
	 */
	public void setACText(final String aString)
	{
		acName = aString;
	}

	/**
	 * Add the AC Type as a string to the List
	 * @param ACType
	 * @return List of AC Types
	 */
	public List<String> getACTypeAddString(final String ACType)
	{
		if (ACTypeAddMap == null)
		{
			return new ArrayList<String>();
		}

		return ACTypeAddMap.get(ACType);
	}

	/**
	 * Remove the AC Type as a string to the List
	 * @param ACType
	 * @return List of AC Types
	 */
	public List<String> getACTypeRemoveString(final String ACType)
	{
		if (ACTypeRemoveMap == null)
		{
			return new ArrayList<String>();
		}

		return ACTypeRemoveMap.get(ACType);
	}

	/**
	 * Set the AC Flat Bonus
	 * @param arg
	 */
	public void setAcFlatBonus(final String arg)
	{
		acFlatBonus = arg;
	}

	/**
	 * Get the AC Flat Bonus
	 * @return acFlatBonus
	 */
	public String getAcFlatBonus()
	{
		return acFlatBonus;
	}

	/**
	 * Set the AC Touch Bonus
	 * @param arg
	 */
	public void setAcTouchBonus(final String arg)
	{
		acTouchBonus = arg;
	}

	/**
	 * Get the AC Touch Bonus
	 * @return ACTouchBonus
	 */
	public String getAcTouchBonus()
	{
		return acTouchBonus;
	}

	/**
	 * Set Alignment Text
	 * @param aString
	 */
	public void setAlignmentText(final String aString)
	{
		alignmentName = aString;
	}

	/**
	 * Set Allowed Game modes
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
				allowedModes = new ArrayList<String>();
			}

			allowedModes.add(aString);
		}
	}

	/**
	 * Set the alternative HP Abbreviation
	 * @param aString
	 */
	public void setAltHPAbbrev(final String aString)
	{
		althpAbbrev = aString;
	}

	/**
	 * Set the alternative HP Text
	 * @param aString
	 */
	public void setAltHPText(final String aString)
	{
		althpName = aString;
	}

	/**
	 * Set flag to allow auto resizing or not
	 * @param allow
	 */
	public void setAllowAutoResize(final boolean allow)
	{
		allowAutoResize = allow;
	}

	/**
	 * Get the allow auto resize flag
	 * @return true if allowed to auto resize
	 */
	public boolean getAllowAutoResize()
	{
		return allowAutoResize;
	}

	/**
	 * Set BAB Abbreviation
	 * @param aString
	 */
	public void setBabAbbrev(final String aString)
	{
		babAbbrev = aString;
	}

	/**
	 * Get BAB Abbreviation
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
	 * Get BAB Minimum value
	 * @return BAB Minimum value
	 */
	public int getBabMinVal()
	{
		return babMinVal;
	}

	/**
	 * Set Level at which you gain a bonus stat
	 * @param aString
	 */
	public void setBonusStatLevels(final String aString)
	{
		bonusStatLevels.add(aString);
	}

	/**
	 * Set Max Level check
	 * @param arg
	 */
	public void setChecksMaxLvl(final int arg)
	{
		checksMaxLvl = arg;
	}

	/**
	 * Get max level check
	 * @return max level check
	 */
	public int getChecksMaxLvl()
	{
		return checksMaxLvl;
	}

	/**
	 * Get the class type by name
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
	 * Get the Context Path
	 * @param aTab
	 * @return Context Path
	 */
	public String getContextPath(final Tab aTab)
	{
		return tInfo.get(aTab).contextPath;
	}

	/**
	 * Set the Currency Unit
	 * @param aString
	 */
	public void setCurrencyUnit(final String aString)
	{
		currencyUnit = aString;
	}

	/**
	 * Set the currency Unit abbreviation
	 * @param aString
	 */
	public void setCurrencyUnitAbbrev(final String aString)
	{
		currencyUnitAbbrev = aString;
	}

	/**
	 * Get Damage down map
	 * @return Map of damage downs
	 */
	public Map<String, String> getDamageDownMap()
	{
		return damageDownMap;
	}

	/**
	 * Set DR text
	 * @param aString
	 */
	public void setDamageResistanceText(final String aString)
	{
		damageResistance = aString;
	}

	/**
	 * Weapon size changes damage
	 * @return Map
	 */
	public Map<String, String> getDamageUpMap()
	{
		return damageUpMap;
	}

	/**
	 * Set the default spell book
	 * @param aString
	 */
	public void setDefaultSpellBook(final String aString)
	{
		defaultSpellBook = aString;
	}

	/**
	 * Define the default unit set for a game mode
	 * @param aString
	 **/
	public void setDefaultUnitSet(final String aString)
	{
		defaultUnitSet = aString;
	}

	/**
	 * Used for Output sheets and GUI to order items in a list
	 * @param aString
	 **/
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
	 * This is used to hold the weapon size penalty
	 * @param anOb
	 */
	public void setEqSizePenaltyObj(final PObject anOb)
	{
		eqSizePenalty = anOb;
	}

	/* *************************
	 * FLP WEAPONSIZEPENALTY3.5
	 * *************************/

	/**
	 * Get the equipment size penalty object
	 * @return PObject representing the equipment size penalty
	 */
	public static PObject getEqSizePenaltyObj()
	{
		return eqSizePenalty;
	}

	/**
	 * Set the levels at which you gain bonus feats
	 * @param aString
	 */
	public void setBonusFeatLevels(final String aString)
	{
		bonusFeatLevels.add(aString);
	}

	/**
	 * @return the folderName
	 */
	public String getFolderName()
	{
		return folderName;
	}

	/**
	 * Set the hidden equipment types
	 * @param pipeList
	 */
	public void setHiddenEquipmentTypes(final String pipeList)
	{
		if (hiddenEquipmentTypes == null)
		{
			hiddenEquipmentTypes = new ArrayList<String>();
		}
		final StringTokenizer aTok = new StringTokenizer(pipeList, "|");
		while (aTok.hasMoreTokens())
		{
			hiddenEquipmentTypes.add(aTok.nextToken().toUpperCase());
		}
	}

	/**
	 * Set the hidden ability types
	 * @param pipeList
	 */
	public void setHiddenAbilityTypes(final String pipeList)
	{
		if (hiddenAbilityTypes == null)
		{
			hiddenAbilityTypes = new ArrayList<String>();
		}
		final StringTokenizer aTok = new StringTokenizer(pipeList, "|");
		while (aTok.hasMoreTokens())
		{
			hiddenAbilityTypes.add(aTok.nextToken().toUpperCase());
		}
	}

	/**
	 * Set the hidden skill types
	 * @param pipeList
	 */
	public void setHiddenSkillTypes(final String pipeList)
	{
		if (hiddenSkillTypes == null)
		{
			hiddenSkillTypes = new ArrayList<String>();
		}
		final StringTokenizer aTok = new StringTokenizer(pipeList, "|");
		while (aTok.hasMoreTokens())
		{
			hiddenSkillTypes.add(aTok.nextToken().toUpperCase());
		}
	}

	/**
	 * Set the HP Abbreviation
	 * @param aString
	 */
	public void setHPAbbrev(final String aString)
	{
		hpAbbrev = aString;
	}

	/**
	 * Set the HP Text
	 * @param aString
	 */
	public void setHPText(final String aString)
	{
		hpName = aString;
	}

	/**
	 * Set the Rank Modifier Formula
	 * @param aString
	 */
	public void setRankModFormula(final String aString)
	{
		rankModFormula = aString;
	}

	/**
	 * map of LevelInfo objects
	 * @return level info map
	 */
	public Map<String, LevelInfo> getLevelInfo()
	{
		return levelInfo;
	}

	/**
	 * Add the level info
	 * @param levInfo
	 */
	public void addLevelInfo(final LevelInfo levInfo)
	{
		levelInfo.put(levInfo.getLevelString(),levInfo);
	}

	/**
	 * Set the level up message
	 * @param aString
	 */
	public void setLevelUpMessage(final String aString)
	{
		levelUpMessage = aString;
	}

	/**
	 * Get the level up message
	 * @return the level up message
	 */
	public String getLevelUpMessage()
	{
		return levelUpMessage;
	}

	/**
	 * Get the menu entry
	 * @return menu entry
	 */
	public String getMenuEntry()
	{
		if (menuEntry == null)
		{
			return name;
		}

		return menuEntry;
	}

	/**
	 * Get the menu tool tip
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
	 * Set the game mode name
	 * @param aString
	 */
	public void setModeName(final String aString)
	{
		menuEntry = aString;
	}

	/**
	 * Set the game mode tool tip
	 * @param aString
	 */
	public void setModeToolTip(final String aString)
	{
		menuToolTip = aString;
	}

	/**
	 * Get the game mode name
	 * @return game mode name
	 */
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
	 * Get Plus calculation
	 * @param type
	 * @return plus calculation
	 */
	public String getPlusCalculation(final String type)
	{
		String aString = null;

		if (plusCalcs != null)
		{
			aString = plusCalcs.get(type);
		}

		return aString;
	}

	/**
	 * Get a rule given a key
	 * @param aKey
	 * @return RucleCheck
	 */
	public RuleCheck getRuleByKey(final String aKey)
	{
		return ruleCheckMap.get(aKey);
	}

	/**
	 * Return true if a RuleCheck exists for a key
	 * @param aKey
	 * @return true if a RuleCheck exists for a key
	 */
	public boolean getRuleCheck(final String aKey)
	{
		if (ruleCheckMap.containsKey(aKey))
		{
			final RuleCheck aRule = ruleCheckMap.get(aKey);

			return aRule.getDefault();
		}

		return false;
	}

	/**
	 * Get the RuleCheck List
	 * @return the RuleCheck List
	 */
	public List<RuleCheck> getRuleCheckList()
	{
		return new ArrayList<RuleCheck>(ruleCheckMap.values());
	}

	/**
	 * Set the Short Range distance
	 * @param aShortRange
	 */
	public void setShortRangeDistance(final int aShortRange)
	{
		shortRangeDistance = aShortRange;
	}

	/**
	 * Get the shortrange distance
	 * @return the shortrange distance
	 */
	public int getShortRangeDistance()
	{
		return shortRangeDistance;
	}

	/**
	 * Set the show class defense parameter
	 * @param argShowDef
	 */
	public void setShowClassDefense(final boolean argShowDef)
	{
		showClassDefense = argShowDef;
	}

	/**
	 * Set the skill multiplier levels
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
	 * Set the base DC for spells
	 * @param arg
	 */
	public void setSpellBaseDC(final String arg)
	{
		spellBaseDC = arg;
	}

	/**
	 * Get the base DC for Spells
	 * @return the base DC for Spells
	 */
	public String getSpellBaseDC()
	{
		return spellBaseDC;
	}

	/**
	 * The formula used to compute spell ranges
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
	 * Returns the formula used for calculate the range of a spell
	 * @param aRange
	 * @return spell range formula
	 */
	public String getSpellRangeFormula(final String aRange)
	{
		String aString = null;

		if (spellRangeMap != null)
		{
			aString = spellRangeMap.get(aRange);
		}

		return aString;
	}

	/**
	 * Set the context for a tab
	 * @param aTab
	 * @param argTabContext
	 */
	public void setTabContext(final Tab aTab, final String argTabContext)
	{
		tInfo.get(aTab).contextPath = argTabContext;
	}

	/**
	 * Set the name of the tab
	 * @param aTab
	 * @param argTabName
	 */
	public void setTabName(final Tab aTab, final String argTabName)
	{
		tInfo.get(aTab).tabName = argTabName;
	}

	/**
	 * Get the name of the tab
	 * @param aTab
	 * @return tab name
	 */
	public String getTabName(final Tab aTab)
	{
		String temp = tInfo.get(aTab).tabName;

		if (temp.startsWith("in_"))
		{
			temp = PropertyFactory.getString(temp);
		}

		return temp;
	}

	/**
	 * Get the singular (e.g. No 's') verson of the tab name
	 * @param aTab
	 * @return the singular (e.g. No 's') verson of the tab name
	 */
	public String getSingularTabName(final Tab aTab)
	{
		String singularName = getTabName(aTab);
		if (singularName.endsWith("s"))
		{
			singularName = singularName.substring(0, singularName.length() - 1);
		}
		return singularName;
	}

	/**
	 * Get the tab number
	 * @param tabName
	 * @return tab number
	 */
	public static Tab getTab(final String tabName)
	{
		for (Tab aTab : Tab.values())
		{
			if (tabName.equalsIgnoreCase(aTab.toString()))
			{
				return aTab;
			}
		}

		return Tab.INVALID;
	}

	/**
	 * Set the BAB Attack Bonus cycle
	 * @param arg
	 */
	public void setBabAttCyc(final int arg)
	{
		babAttCyc = arg;
	}

	/**
	 * Get the BAB Attack Bonus cycle
	 * @return the BAB Attack Bonus cycle
	 */
	public int getBabAttCyc()
	{
		return babAttCyc;
	}

	/**
	 * Set the maximum BAB attacks allowed
	 * @param arg
	 */
	public void setBabMaxAtt(final int arg)
	{
		babMaxAtt = arg;
	}

	/**
	 * Get the max BAB attacks allowed
	 * @return the max BAB attacks allowed
	 */
	public int getBabMaxAtt()
	{
		return babMaxAtt;
	}

	/**
	 * Set the max BAB level
	 * @param arg
	 */
	public void setBabMaxLvl(final int arg)
	{
		babMaxLvl = arg;
	}

	/**
	 * Get the max BAB level
	 * @return the max BAB level
	 */
	public int getBabMaxLvl()
	{
		return babMaxLvl;
	}

	/**
	 * True if the tab is visible
	 * @param aTab
	 * @return True if the tab is visible
	 */
	public boolean getTabShown(final Tab aTab)
	{
		return tInfo.get(aTab).visible;
	}

	/**
	 * Set the visibility of a tab
	 * @param aTab
	 * @param visible
	 */
	public void setTabVisible(final Tab aTab, final boolean visible)
	{
		tInfo.get(aTab).visible = visible;
	}

	/**
	 * Set a 2nd variable display name
	 * @param aString
	 */
	public void setVariableDisplay2Name(final String aString)
	{
		displayVariable2Name = aString;
	}

	/**
	 * Set a 2nd variable display text
	 * @param aString
	 */
	public void setVariableDisplay2Text(final String aString)
	{
		displayVariable2Text = aString;
	}

	/**
	 * Set a 3rd variable display name
	 * @param aString
	 */
	public void setVariableDisplay3Name(final String aString)
	{
		displayVariable3Name = aString;
	}

	/**
	 * Set a 3rd variable display text
	 * @param aString
	 */
	public void setVariableDisplay3Text(final String aString)
	{
		displayVariable3Text = aString;
	}

	/**
	 * Set a variable display name
	 * @param aString
	 */
	public void setVariableDisplayName(final String aString)
	{
		displayVariableName = aString;
	}

	/**
	 * Set a 2nd variable display text
	 * @param aString
	 */
	public void setVariableDisplayText(final String aString)
	{
		displayVariableText = aString;
	}

	/**
	 * Formula used to compute Wield Category steps
	 * @param aString
	 **/
	public void setWCStepsFormula(final String aString)
	{
		wcStepsFormula = aString;
	}

	/**
	 * Get the formula used to compute Wield Category steps
	 * @return formula used to compute Wield Category steps
	 */
	public String getWCStepsFormula()
	{
		return wcStepsFormula;
	}

	/**
	 * Get the weapon categories
	 * @return the weapon categories
	 */
	public String getWeaponCategories()
	{
		return weaponCategories;
	}

	/**
	 * Get the weapon types
	 * @return the weapon types
	 */
	public String getWeaponTypes()
	{
		return weaponTypes;
	}

	/**
	 * Get the wield category
	 * @param aName
	 * @return wield category
	 */
	public WieldCategory getWieldCategory(final String aName)
	{
		for ( WieldCategory wCat : wieldCategoryList )
		{
			if (wCat.getName().equals(aName))
			{
				return wCat;
			}
		}

		return null;
	}

	/**
	 * Get the weapon reach formula
	 * @return String the weaopn reach formula
	 */
	public String getWeaponReachFormula ()
	{
		return this.weaponReachFormula;
	}
	
	/**
	 * Return true if an equipment type is hidden
	 * @param aType
	 * @return true if an equipment type is hidden
	 */
	public boolean isEquipmentTypeHidden(final String aType)
	{
		if (hiddenEquipmentTypes != null)
		{
			return hiddenEquipmentTypes.contains(aType);
		}
		return false;
	}

	/**
	 * Return true if an ability type is hidden
	 * @param aType
	 * @return true if an ability type is hidden
	 */
	public boolean isAbilityTypeHidden(final String aType)
	{
		if (hiddenAbilityTypes != null)
		{
			return hiddenAbilityTypes.contains(aType);
		}
		return false;
	}

	/**
	 * Return true if an skill type is hidden
	 * @param aType
	 * @return true if an skill type is hidden
	 */
	public boolean isSkillTypeHidden(final String aType)
	{
		if (hiddenSkillTypes != null)
		{
			return hiddenSkillTypes.contains(aType);
		}
		return false;
	}

	/**
	 * Return true if the AC Type is Valid
	 * @param ACType
	 * @return true if the AC Type is Valid
	 */
	public boolean isValidACType(final String ACType)
	{
		if (ACTypeAddMap.containsKey(ACType))
		{
			return true;
		}
		if (ACTypeRemoveMap.containsKey(ACType))
		{
			return true;
		}
		return false;
	}

	/**
	 * Add AC Type
	 * @param ACTypeLine
	 */
	public void addACType(final String ACTypeLine)
	{
		final StringTokenizer aTok = new StringTokenizer(ACTypeLine, "\t");

		if (!aTok.hasMoreTokens())
		{
			Logging.errorPrint("Empty tag in miscinfo.ACTYPE");

			return;
		}

		final String ACType = aTok.nextToken();

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();

			if (aString.startsWith("ADD:"))
			{
				final List<String> aList;

				if (ACTypeAddMap.containsKey(ACType))
				{
					aList = ACTypeAddMap.get(ACType);
				}
				else
				{
					aList = new ArrayList<String>();
				}

				aList.add(aString.substring(4));
				ACTypeAddMap.put(ACType, aList);
			}
			else if (aString.startsWith("REMOVE:"))
			{
				final List<String> aList;

				if (ACTypeRemoveMap.containsKey(ACType))
				{
					aList = ACTypeRemoveMap.get(ACType);
				}
				else
				{
					aList = new ArrayList<String>();
				}

				aList.add(aString.substring(7));
				ACTypeRemoveMap.put(ACType, aList);
			}
			else
			{
				Logging.errorPrint("Incorrect tag in miscinfo.ACTYPE: " + aString);
			}
		}
	}

	/**
	 * Add Class Type
	 * @param aString
	 */
	public void addClassType(final String aString)
	{
		if (".CLEAR".equals(aString))
		{
			classTypeList = null;

			return;
		}

		if (classTypeList == null)
		{
			classTypeList = new ArrayList<ClassType>();
		}

		final ClassType aClassType = new ClassType();
		final StringTokenizer aTok = new StringTokenizer(aString, "\t");
		aClassType.setName(aTok.nextToken()); //Name of the Class Type

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if (bString.startsWith("CRFORMULA:"))
			{
				aClassType.setCRFormula(bString.substring(10));
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
	 * Add Diety List
	 * @param argDeityLine
	 */
	public void addDeityList(final String argDeityLine)
	{
		if (".CLEAR".equals(argDeityLine))
		{
			defaultDeityList = null;

			return;
		}

		if (defaultDeityList == null)
		{
			defaultDeityList = new ArrayList<String>();
		}

		defaultDeityList.add(argDeityLine);
	}

	/**
	 * Add Load String
	 * @param aString
	 */
	public void addLoadString(final String aString)
	{
		loadStrings.add(aString);
	}

	/**
	 * Add Plus calculation
	 * @param aString
	 */
	public void addPlusCalculation(final String aString)
	{
		final int idx = aString.indexOf('|');

		if (idx > 0)
		{
			if (plusCalcs == null)
			{
				plusCalcs = new HashMap<String, String>();
			}

			plusCalcs.put(aString.substring(0, idx).toUpperCase(), aString.substring(idx + 1));
		}
	}

	/**
	 * RuleCheck lists.
	 *
	 * @param aRule the rule to check
	 */
	public void addRule(final RuleCheck aRule)
	{
		ruleCheckMap.put(aRule.getKey(), aRule);
	}

	/**
	 * Add a Weapon Category
	 * @param aString
	 */
	public void addWeaponCategory(final String aString)
	{
		weaponCategories += ('|' + aString);
	}

	/**
	 * Add a Weapon Type
	 * @param aString
	 */
	public void addWeaponType(final String aString)
	{
		weaponTypes += ('|' + aString);
	}

	/**
	 * Wield Categories
	 * @param wCat
	 */
	public void addWieldCategory(final WieldCategory wCat)
	{
		wieldCategoryList.add(wCat);
	}

	/**
	 * The "steps" up or down in the Wield Category chain
	 * @param aLine
	 **/
	public void addWieldCategorySteps(final String aLine)
	{
		wcStepsList.clear();

		final StringTokenizer aTok = new StringTokenizer(aLine, "|");

		while (aTok.hasMoreTokens())
		{
			final String aName = aTok.nextToken();
			wcStepsList.add(aName);
		}
	}

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
	 * Set the weapobn reach forumla
	 * @param aString	the new weapon reach formula
	 */
	public void setWeaponReachFormula (String aString)
	{
		this.weaponReachFormula = aString;
	}
	
	/**
	 * Return true if RuleCheck exists given a key
	 * @param aKey
	 * @return true if RuleCheck exists given a key
	 */
	public boolean hasRuleCheck(final String aKey)
	{
		if (ruleCheckMap.containsKey(aKey))
		{
			return true;
		}

		return false;
	}

	String getACAbbrev()
	{
		return acAbbrev;
	}

	/**
	 * AC Info
	 * @return AC text
	 */
	String getACText()
	{
		return acName;
	}

	/**
	 * Alignment
	 * @return alignment name
	 */
	String getAlignmentText()
	{
		return alignmentName;
	}

	List<String> getAllowedModes()
	{
		if (allowedModes == null)
		{
			final List<String> modes = new ArrayList<String>(1);
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
	 * Wound Points
	 * @return alt hp name
	 */
	String getAltHPText()
	{
		return althpName;
	}

	/**
	 * Levels at which all characters get bonus feats
	 * @return List
	 */
	List<String> getBonusFeatLevels()
	{
		return bonusFeatLevels;
	}

	/**
	 * Levels at which all characters get bonus to stats
	 * @return List
	 */
	List<String> getBonusStatLevels()
	{
		return bonusStatLevels;
	}

	/**
	 * Currency abbreviation
	 * @return currency unit abbreviation
	 */
	String getCurrencyDisplay()
	{
		return currencyUnitAbbrev;
	}

	/**
	 * Get Damage Resistance Text
	 * @return Get Damage Resistance Text
	 */
	String getDamageResistanceText()
	{
		return damageResistance;
	}

	/**
	 * Default spell book name
	 * @return default spell book
	 */
	String getDefaultSpellBook()
	{
		return defaultSpellBook;
	}

	/**
	 * Default unit set
	 * @return default unit set
	 */
	String getDefaultUnitSet()
	{
		return defaultUnitSet;
	}

	List<String> getDeityList()
	{
		return defaultDeityList;
	}

	int getDisplayOrder()
	{
		return displayOrder;
	}

	/**
	 * HP
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
	 * Currency unit
	 * @return currency unit
	 */
	String getLongCurrencyDisplay()
	{
		return currencyUnit;
	}

	String getRankModFormula()
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
	 * Variable Display
	 * @return variable display text
	 */
	String getVariableDisplayText()
	{
		return displayVariableText;
	}

	private static class TabInfo
	{
		String tabName = "";
		String contextPath = "";
		boolean visible = true;
	}


	/**
	 * Return an <b>unmodifiable</b> version of the stat list.
	 * @return List
	 */
	public List<PCStat> getUnmodifiableStatList()
	{
		return Collections.unmodifiableList(statList);
	}

	//STATLIST

	/**
	 * Add to the stat list.
	 *
	 * @param stat the PC stat
	 */
	public  void addToStatList(final PCStat stat)
	{
		statList.add(stat);
	}

	/**
	 * Clear out the stat list.
	 */
	public void clearStatList()
	{
		statList.clear();
	}


	/**
	 * Set the long description of attributes
	 * @param s
	 */
	public void setAttribLong(final String[] s)
	{
		s_ATTRIBLONG = s;
	}

	/**
	 * Set the long description of a particular attribute
	 * @param index
	 * @param s
	 */
	public void setAttribLong(final int index, final String s)
	{
		s_ATTRIBLONG[index] = s;
	}

	/**
	 * Set the short description of attributes
	 * @param s
	 */
	public void setAttribShort(final String[] s)
	{
		s_ATTRIBSHORT = s;
	}

	/**
	 * Set the short description of a particular attribute
	 * @param s
	 * @param index
	 */
	public void setAttribShort(final int index, final String s)
	{
		s_ATTRIBSHORT[index] = s;
	}

	/**
	 * Returns the index of the requested attribute abbreviation,
	 * The attributes used are loaded from a lst file
	 *
	 * @param attributeAbbreviation to find the index of
	 * @return the index of the attribute
	 *         returns -1 if the attribute is not matched (or null)
	 */
	public int getStatFromAbbrev(final String attributeAbbreviation)
	{
		if (s_ATTRIBSHORT != null)
		{
			for (int stat = 0; stat < s_ATTRIBSHORT.length; ++stat)
			{
				if (attributeAbbreviation.equalsIgnoreCase(s_ATTRIBSHORT[stat]))
				{
					return stat;
				}
			}
		}

		return -1;
	}

	//CHECKLIST

	/**
	 * Add a check to the list of checks.
	 *
	 * @param obj to add to the check list
	 */
	public void addToCheckList(final PObject obj)
	{
		checkList.add(obj);
	}

	/**
	 * Empty the check list.
	 */
	public void clearCheckList()
	{
		checkList.clear();
	}


	/**
	 * Return the requested object.
	 * @param aName
	 * @return PObject
	 */
	public PObject getCheckNamed(final String aName)
	{
		final PObject check;
		final int index = getIndexOfCheck(aName);

		if (index == -1)
		{
			check = null;
		}
		else
		{
			check = checkList.get(index);
		}

		return check;
	}

	/**
	 * Get the Index of a Check
	 * @param check
	 * @return index of a check
	 */
	public int getIndexOfCheck(final String check)
	{
		for (int i = 0; i < checkList.size(); ++i)
		{
			if (checkList.get(i).toString().equalsIgnoreCase(check))
			{
				return i;
			}
		}

		return -1; // not found
	}

	/**
	 * Return an <b>unmodifiable</b> version of the check list.
	 * @return an <b>unmodifiable</b> version of the check list.
	 */
	public List<PObject> getUnmodifiableCheckList()
	{
		return Collections.unmodifiableList(checkList);
	}

	/**
	 * Returns the check key of the check at the index specified.
	 * 
	 * @param anIndex Index of the check in the game mode check list.
	 * 
	 * @return A key for the check.
	 */
	public String getCheckKey( final int anIndex )
	{
		if (anIndex < 0 || anIndex >= checkList.size())
		{
			return "";
		}
		return checkList.get(anIndex).getKeyName();
	}
	
	//ALIGNMENTLIST

	/**
	 * Add to the alignment list.
	 *
	 * @param alignment the alignment
	 */
	public void addToAlignmentList(final PCAlignment alignment)
	{
		alignmentList.add(alignment);
	}

	/**
	 * Clear out the alignment list.
	 */
	public void clearAlignmentList()
	{
		alignmentList.clear();
	}

	/**
	 * Get the Alignment of the PC given an index
	 * @param index
	 * @return the Alignment of the PC
	 */
	public PCAlignment getAlignmentAtIndex(final int index)
	{
		final PCAlignment align;

		if ((index < 0) || (index >= alignmentList.size()))
		{
			align = null;
		}
		else
		{
			align = alignmentList.get(index);
		}

		return align;
	}
	/**
	 * Returns an array of alignment names.
	 * @param useLongForm True if the long names should be returned.
	 * @return alignment list Strings
	 */
	public String[] getAlignmentListStrings(final boolean useLongForm)
	{
		final String[] al = new String[alignmentList.size()];
		int x = 0;

		for ( PCAlignment alignment : alignmentList )
		{
			if (useLongForm)
			{
				al[x++] = alignment.getDisplayName();
			}
			else
			{
				al[x++] = alignment.getKeyName();
			}
		}

		return al;
	}
	/**
	 * Return the index of the alignment name (handles both short and long names.)
	 * @param anAlignmentName
	 * @return index
	 */
	public int getIndexOfAlignment(final String anAlignmentName)
	{
		for (int i = 0; i < alignmentList.size(); ++i)
		{
			final PCAlignment alignment = alignmentList.get(i);

			// if long name or short name of alignment matches, return index
			if (alignment.getDisplayName().equalsIgnoreCase(anAlignmentName)
					|| alignment.getKeyName().equalsIgnoreCase(anAlignmentName))
			{
				return i;
			}
		}

		return -1; // not found
	}

	/**
	 * Returns a <tt>PCAlignment</tt> object for the alignment key or name 
	 * passed in.
	 * 
	 * @param aKey A short alignment (LG) or long alignment (Lawful Good)
	 * 
	 * @return A <tt>PCAlignment</tt> object or null if no match is found.
	 */
	public PCAlignment getAlignment( final String aKey )
	{
		for ( final PCAlignment align : alignmentList )
		{
			// if long name or short name of alignment matches, return index
			if (align.getDisplayName().equalsIgnoreCase(aKey)
			 || align.getKeyName().equalsIgnoreCase(aKey))
			{
				return align;
			}
		}
		return null;
	}
	
	/**
	 * Return the long version of the alignment name found at the index. (E.g. Lawful Good)
	 * @param index
	 * @return String
	 */
	public String getLongAlignmentAtIndex(final int index)
	{
		final String alName;
		final PCAlignment al = getAlignmentAtIndex(index);

		if (al == null)
		{
			alName = "";
		}
		else
		{
			alName = al.getDisplayName();
		}

		return alName;
	}

	/**
	 * Return the short version of the alignment name found at the index. (E.g. LG)
	 * @param index
	 * @return String
	 */
	public String getShortAlignmentAtIndex(final int index)
	{
		final PCAlignment al = getAlignmentAtIndex(index);

		if (al == null)
		{
			return "";
		}

		return al.getKeyName();
	}

	/**
	 * Return an <b>unmodifiable</b> version of the alignmentlist.
	 * @return an <b>unmodifiable</b> version of the alignmentlist.
	 */
	public List<PCAlignment> getUnmodifiableAlignmentList()
	{
		return Collections.unmodifiableList(alignmentList);
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
	 * Get the range penalty
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
	 * Set the cost for cross class skills
	 * @param value
	 */
	public void setSkillCost_CrossClass(int value)
	{
		skillCost_CrossClass = value;
	}

	/**
	 * Set the cost for exclusive skills
	 * @param value
	 */
	public void setSkillCost_Exclusive(int value)
	{
		skillCost_Exclusive = value;
	}

	/**
	 * Get the cost for class skills
	 * @return cost for class skills
	 */
	public int getSkillCost_Class()
	{
		return skillCosts_Class;
	}

	/**
	 * Get the cost for cross class skills
	 * @return cost for cross class skills
	 */

	public int getSkillCost_CrossClass()
	{
		return skillCost_CrossClass;
	}

	/**
	 * Get the cost for exclusive skills
	 * @return cost for exclusive skills
	 */
	public int getSkillCost_Exclusive()
	{
		return skillCost_Exclusive;
	}

	/**
	 * Set the point pool name
	 * @param argPoolName
	 */
	public void setPointPoolName(final String argPoolName)
	{
		pointPoolName = argPoolName;
	}

	/**
	 * Get the point pool name
	 * @return point pool name
	 */
	public String getPointPoolName()
	{
		return pointPoolName;
	}

	/**
	 * Set the penalty for non proficiency
	 * @param argPenalty
	 */
	public void setNonProfPenalty(final int argPenalty)
	{
		nonProfPenalty = argPenalty;
	}

	/**
	 * Get the penalty for non proficiency
	 * @return the penalty for non proficiency
	 */
	public int getNonProfPenalty()
	{
		return nonProfPenalty;
	}

	/**
	 * Set the HP Formula
	 * @param argFormula
	 */
	public void setHPFormula(final String argFormula)
	{
		hpFormula = argFormula;
	}

	/**
	 * Get the HP Formula
	 * @return HP Formula
	 */
	public String getHPFormula()
	{
		return hpFormula;
	}

	/**
	 * Set Add with meta magic message
	 * @param argMsg
	 */
	public void setAddWithMetamagicMessage(final String argMsg)
	{
		addWithMetamagic = argMsg;
	}

	/**
	 * Get add with meta magic message
	 * @return add with meta magic message
	 */
	public String getAddWithMetamagicMessage()
	{
		return addWithMetamagic;
	}

	/**
	 * Add to the bonus spell map
	 * @param level
	 * @param baseStatScore
	 * @param statRange
	 */
	public void addToBonusSpellMap(final String level, final String baseStatScore, final String statRange)
	{
		bonusSpellMap.put(level, baseStatScore + '|' + statRange);
	}

	/**
	 * Get the bonus spell map
	 * @return the bonus spell map
	 */
	public Map<String, String> getBonusSpellMap()
	{
		return bonusSpellMap;
	}

	/**
	 * Set square size
	 * @param argSize
	 */
	public void setSquareSize(final double argSize)
	{
		squareSize = argSize;
	}

	/**
	 * Get square size
	 * @return square size
	 */
	public double getSquareSize()
	{
		return squareSize;
	}

	/**
	 * Get the set unit
	 * @return the unti that is set
	 */
	public UnitSet getUnitSet()
	{
		return selectedUnitSet;
	}

	/**
	 * Return true if the unit has been set
	 * @param unitSetName
	 * @return true if the unit has been set
	 */
	public boolean selectUnitSet(final String unitSetName)
	{
		final UnitSet ui = SystemCollections.getUnitSet(unitSetName, name);
		if (ui == null)
		{
			return false;
		}
		selectedUnitSet = ui;
		return true;
	}

	/**
	 * Return true if the default unit has been set
	 * @return true if the unit has been set
	 */
	public boolean selectDefaultUnitSet()
	{
		final UnitSet ui = SystemCollections.getUnitSetNamed(defaultUnitSet, name);
		if (ui == null)
		{
			return false;
		}
		selectedUnitSet = ui;
		return true;
	}

	/**
	 * Add a stat/cost pair to purchase mode stat costs.
	 * @param statValue
	 * @param cost
	 */
	public void addPointBuyStatCost(final int statValue, final int cost)
	{
		PointBuyCost pbc = new PointBuyCost(statValue);
		pbc.setStatCost(cost);
		addPointBuyStatCost(pbc);
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
			pointBuyStatCosts = new TreeMap<Integer, PointBuyCost>(new ComparableComparator<Integer>());
		}
		abilityScoreCost = null;
		pointBuyStatCosts.put(Integer.valueOf(pbc.getStatValue()), pbc);
	}

	/**
	 * Add a purchase mode method.
	 * @param methodName
	 * @param points
	 */
	public void addPurchaseModeMethod(final String methodName, final String points)
	{
		addPurchaseModeMethod(new PointBuyMethod(methodName, points));
	}

	/**
	 * Add a purchase mode method.
	 * @param pbm
	 */
	public void addPurchaseModeMethod(final PointBuyMethod pbm)
	{
		if (getPurchaseMethodByName(pbm.getMethodName()) == null)
		{
			if (pointBuyMethods == null)
			{
				pointBuyMethods = new ArrayList<PointBuyMethod>();
			}

			pointBuyMethods.add(pbm);
		}
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
	 * Get the point buy by stat mapping
	 * @return the point buy by stat mapping
	 */
	public SortedMap<Integer, PointBuyCost> getPointBuyStatCostMap()
	{
		return pointBuyStatCosts;
	}

	/**
	 * Clear the purchase mode methods
	 */
	public void clearPurchaseModeMethods()
	{
		pointBuyMethods = null;
	}

	/**
	 * Find a user-defined purchase method by name.
	 * @param methodName
	 * @return the purchase method or null
	 */
	public PointBuyMethod getPurchaseMethodByName(final String methodName)
	{
		if (pointBuyMethods != null)
		{
			for ( PointBuyMethod pbm : pointBuyMethods )
			{
				if (pbm.getMethodName().equalsIgnoreCase(methodName))
				{
					return pbm;
				}
			}
		}

		return null;
	}

	/**
	 * Get the number of user-defined purchase methods.
	 * @return purchase method count
	 */
	public int getPurchaseMethodCount()
	{
		if (pointBuyMethods != null)
		{
			return pointBuyMethods.size();
		}

		return 0;
	}

	/**
	 * Set the purchase mode method name
	 * @param argMethodName
	 */
	public void setPurchaseMethodName(final String argMethodName)
	{
		if (argMethodName.length() != 0)
		{
			setRollMethod(Constants.CHARACTERSTATMETHOD_PURCHASE);
		}

		purchaseMethodName = argMethodName;
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
	 * Get the purchase mode method name
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
	 * Get the purchase mode method pool formula
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
	 * Get the maximum score you can purchase
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
			boolean bPassed = false;
			for ( Integer statValue : pointBuyStatCosts.keySet() )
			{
				final PointBuyCost pbc = pointBuyStatCosts.get(statValue);
				if (!PrereqHandler.passesAll(pbc.getPreReqList(), pc, null))
				{
					//
					// If have passed any prereqs already, then stop looking and use the highest passing stat
					//
					if (bPassed)
					{
						break;
					}
				}
				else
				{
					lastStat = statValue.intValue();
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
	 * Get the minimum score you can purchase
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
				if (PrereqHandler.passesAll(pbc.getPreReqList(), pc, null))
				{
					lastStat = statValue;
					break;
				}
			}
		}
		return lastStat;
	}

	/**
	 * Returns true if we are in a stat purchase mode
	 * @return true if we are in a stat purchase mode
	 */
	public boolean isPurchaseStatMode()
	{
		// Can't have purchase mode if no costs specified
		if ((pointBuyStatCosts == null) || (pointBuyStatCosts.size() == 0)
			|| (getRollMethod() != Constants.CHARACTERSTATMETHOD_PURCHASE) || (purchaseMethodName.length() == 0))
		{
			return false;
		}

		return getPurchaseMethodByName(purchaseMethodName) != null;
	}

	/**
	 * Get the purchase method
	 * @param idx
	 * @return the purchase method
	 */
	public PointBuyMethod getPurchaseMethod(final int idx)
	{
		if ((pointBuyMethods == null) || (idx > pointBuyMethods.size()))
		{
			return null;
		}

		return pointBuyMethods.get(idx);
	}

	/**
	 * @param argRollMethod
	 */
	public void setRollMethod(final int argRollMethod)
	{
		rollMethod = argRollMethod;

		if (argRollMethod != Constants.CHARACTERSTATMETHOD_PURCHASE)
		{
			setPurchaseMethodName(""); //$NON-NLS-1$
		}
	}

	/**
	 * @return roll method
	 */
	public int getRollMethod()
	{
		return rollMethod;
	}

	/**
	 * Get the cost for an ability score
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

			final int statCost = pointBuyStatCosts.get(statValue).getStatCost();
			lastStat = statValue;
			lastCost = statCost;
			abilityScoreCost[i++] = lastCost;
		}

		return abilityScoreCost;
	}

	/**
	 * Get the cost for an ability score
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
	 * Set the roll method expression by name
	 * @param aString
	 */
	public void setRollMethodExpressionByName(final String aString)
	{
		if (aString.length() != 0)
		{
			rollingMethodIndex = findRollingMethodByName(aString);
			if (rollingMethodIndex >= 0)
			{
				setRollMethod(Constants.CHARACTERSTATMETHOD_ROLLED);
			}
			else
			{
				setRollMethod(Constants.CHARACTERSTATMETHOD_USER);
			}
		}
	}

	/**
	 * Get the cost for an ability score
	 * @return the cost for an ability score
	 */
	public String getRollMethodExpression()
	{
		final GameModeRollMethod rm = getRollingMethod(rollingMethodIndex);
		if (rm != null)
		{
			return rm.getMethodRoll();
		}
		return "";
	}

	/**
	 * Get roll method expression name
	 * @return roll method expression name
	 */
	public String getRollMethodExpressionName()
	{
		final GameModeRollMethod rm = getRollingMethod(rollingMethodIndex);
		if (rm != null)
		{
			return rm.getMethodName();
		}
		return "";
	}

	/**
	 * Return true if the purchasing of stats mode is allowed
	 * @return true if the purchasing of stats mode is allowed
	 */
	public boolean isPurchaseStatModeAllowed()
	{
		if ((pointBuyStatCosts == null) || (pointBuyStatCosts.size() == 0))
		{
			return false;
		}

		return true;
	}

	/**
	 * Adds the item to the sizeAdjustmentList.
	 * @param item
	 */
	public void addToSizeAdjustmentList(final SizeAdjustment item)
	{
		if (!sizeAdjustmentList.contains(item))
		{
			sizeAdjustmentList.add(item);
		}
	}

	/**
	 * Clears the sizeAdjustmentList.
	 */
	public void clearSizeAdjustmentList()
	{
		sizeAdjustmentList.clear();
	}

	/**
	 * Get the default size adjustment
	 * @return the default size adjustment
	 */
	public SizeAdjustment getDefaultSizeAdjustment()
	{
		for ( SizeAdjustment s : sizeAdjustmentList )
		{
			if (s.isDefaultSize())
			{
				return s;
			}
		}

		return null;
	}

	/**
	 * Returns the requested SizeAdjustment item, or null if the index is inappropriate.
	 * @param index
	 * @return the requested SizeAdjustment item, or null if the index is inappropriate.
	 */
	public SizeAdjustment getSizeAdjustmentAtIndex(final int index)
	{
		SizeAdjustment sa = null;

		if ((index >= 0) && (index < sizeAdjustmentList.size()))
		{
			sa = sizeAdjustmentList.get(index);
		}

		return sa;
	}

	/**
	 * Returns the size of the sizeAdjustmentList.
	 * @return the size of the sizeAdjustmentList.
	 */
	public int getSizeAdjustmentListSize()
	{
		return sizeAdjustmentList.size();
	}

	/**
	 * Returns the requested size adjustment.
	 * @param aName
	 * @return the requested size adjustment.
	 */
	public SizeAdjustment getSizeAdjustmentNamed(final String aName)
	{
		if (aName.trim().length() == 0)
		{
			return spareSize;
		}

		for ( SizeAdjustment s : sizeAdjustmentList )
		{
			if (s.getDisplayName().equalsIgnoreCase(aName)
			 || s.getAbbreviation().equalsIgnoreCase(aName))
			{
				return s;
			}
		}

		return null;
	}

	/**
	 * Set the value for all stats
	 * @param argAllStatsValue
	 */
	public void setAllStatsValue(final int argAllStatsValue)
	{
		allStatsValue = argAllStatsValue;
	}

	/**
	 * Get the value of all stats
	 * @return the value of all stats
	 */
	public int getAllStatsValue()
	{
		return allStatsValue;
	}

	/**
	 * Character generation random rolling techniques
	 * @param methodName The name of the method that will be visible to the user
	 * @param methodRoll A RollingMethods dice expression
	 * @return true if OK
	 */
	public boolean addRollingMethod(final String methodName, final String methodRoll)
	{
		boolean retStatus = false;
		//
		// Remove all roll methods?
		//
		if (".CLEAR".equals(methodName))
		{
			rollingMethods = null;
			retStatus = true;
		}
		//
		// Remove a specific roll method?
		//
		else if (methodName.endsWith(".CLEAR"))
		{
			/** @todo don't we need to chop off the .CLEAR portion? */
			final int idx = findRollingMethodByName(methodName);
			if (idx >= 0)
			{
				rollingMethods.remove(idx);
				retStatus = true;
			}
		}
		else
		{
			if (rollingMethods == null)
			{
				rollingMethods = new ArrayList<GameModeRollMethod>();
			}
			final int idx = findRollingMethodByName(methodName);
			if (idx < 0)
			{
				GameModeRollMethod rm = new GameModeRollMethod(methodName, methodRoll);
				rollingMethods.add(rm);
				retStatus = true;
			}
		}
		return retStatus;
	}

	private int findRollingMethodByName(final String methodName)
	{
		for(int i = 0;; ++i)
		{
			final GameModeRollMethod rm = getRollingMethod(i);
			if (rm == null)
			{
				return -1;
			}
			else if (methodName.equals(rm.getMethodName()))
			{
				return i;
			}
		}
	}

	/**
	 * Get the rolling method
	 * @param idx
	 * @return the rolling method
	 */
	public GameModeRollMethod getRollingMethod(final int idx)
	{
		if ((rollingMethods != null) && (idx >= 0) && (idx < rollingMethods.size()))
		{
			return rollingMethods.get(idx);
		}
		return null;
	}

	/**
	 * Returns the currently set rolling method for character stats.
	 *
	 * @return GameModeRollMethod the current rolling method
	 */
	public GameModeRollMethod getCurrentRollingMethod()
	{
		return getRollingMethod(rollingMethodIndex);
	}

	/**
	 * Set the minimum stat
	 * @param argMin
	 */
	public void setStatMin(final int argMin)
	{
		statMin  = argMin;
	}

	/**
	 * Get the minimum stat
	 * @return minimum stat
	 */
	public int getStatMin()
	{
		return statMin;
	}

	/**
	 * Set the maximum stat
	 * @param argMax
	 */
	public void setStatMax(final int argMax)
	{
		statMax  = argMax;
	}

	/**
	 * Get the maximum stat
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
	 * Add the dispaly text for a stat
	 * @param statValue
	 * @param statText
	 */
	public void addStatDisplayText(final int statValue, final String statText)
	{
		if (statDisplayText == null)
		{
			// Sort NUMERICALLY, not alphabetically!
			// CONSIDER Huh? The natural order of Integer IS numerically... - thpr 10/20/06
			statDisplayText = new TreeMap<Integer, String>(new ComparableComparator<Integer>());
		}
		statDisplayText.put(statValue, statText);
	}

	/**
	 * Get the display text of a stat
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
	 * Set the summary tab's stat column to (in)visible
	 * @param columnIndex
	 * @param bVisible
	 */
	public void setSummaryTabStatColumnVisible(final int columnIndex, final boolean bVisible)
	{
		if ((columnIndex >= 0) && (columnIndex <= 7))
		{
			summaryTabStatColumnVisible[columnIndex] = bVisible;
		}
	}

	/**
	 * True if the summary tab's stat column is visible
	 * @param columnIndex
	 * @return True if the summary tab's stat column is visible
	 */
	public boolean getSummaryTabStatColumnVisible(final int columnIndex)
	{
		if ((columnIndex >= 0) && (columnIndex <= 7))
		{
			return summaryTabStatColumnVisible[columnIndex];
		}
		return true;
	}
	
	/**
	 */
	public void setSkillTabColumnVisible(final int columnIndex, final boolean bVisible)
	{
		if ((columnIndex >= 0) && (columnIndex < skillTabColumnVisible.length))
		{
			skillTabColumnVisible[columnIndex] = bVisible;
		}
	}

	/**
	 */
	public boolean getSkillTabColumnVisible(final int columnIndex)
	{
		if ((columnIndex >= 0) && (columnIndex < skillTabColumnVisible.length))
		{
			return skillTabColumnVisible[columnIndex];
		}
		return true;
	}

	/**
	 * Return true if the skill rank display text is there
	 * @return true if the skill rank display text is there
	 */
	public boolean hasSkillRankDisplayText()
	{
		return (skillRankDisplayText == null) ? false : true;
	}

	/**
	 * Add display text for a skill rank
	 * @param rankValue
	 * @param rankText
	 */
	public void addSkillRankDisplayText(final int rankValue, final String rankText)
	{
		if (skillRankDisplayText == null)
		{
			// Sort NUMERICALLY, not alphabetically!
			// CONSIDER Huh? The natural order of Integer IS numerically... - thpr 10/20/06
			skillRankDisplayText = new TreeMap<Integer, String>(new ComparableComparator<Integer>());
		}
		skillRankDisplayText.put(rankValue, rankText);
	}

	/**
	 * Get display text for a skill rank
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
	 * Clears the bonus stacking list..
	 */
	public void clearBonusStacksList()
	{
		bonusStackList.clear();
	}

	/**
	 * Return an <b>unmodifiable</b> version of the bonus stacking list.
	 * @return an <b>unmodifiable</b> version of the bonus stacking list.
	 */
	public List<String> getUnmodifiableBonusStackList()
	{
		return Collections.unmodifiableList(bonusStackList);
	}

	/**
	 * Checks if a bonus type should stack for this game mode.
	 * 
	 * @param aBonusType The bonus type
	 * 
	 * @return <tt>true</tt> if bonuses of this type stack.
	 */
	public boolean bonusStacks( final String aBonusType )
	{
		return bonusStackList.indexOf(aBonusType) != -1; // e.g. Dodge
	}

	/**
	 * Adds an <tt>AbilityCategory</tt> definition to the game mode.
	 * 
	 * @param aCategory The <tt>AbilityCategory</tt> to add.
	 */
	public void addAbilityCategory(final AbilityCategory aCategory)
	{
		theAbilityCategories.add(aCategory);
	}

	/**
	 * Adds an <tt>AbilityCategory</tt> definition to the game mode.
	 * 
	 * @param aCategory The <tt>AbilityCategory</tt> to add.
	 */
	public void addLstAbilityCategory(final AbilityCategory aCategory)
	{
		theLstAbilityCategories.add(aCategory);
	}

	/**
	 * Clears all LST sourced <tt>AbilityCategory</tt> definitions from the 
	 * game mode. Used when unloading the data.
	 * 
	 * @param aCategory The <tt>AbilityCategory</tt> to add.
	 */
	public void clearLstAbilityCategories()
	{
		theLstAbilityCategories.clear();
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
		if (aKey == null || (ac == null && aKey.length() > 0))
		{
			Logging.errorPrint("Attempt to fetch AbilityCategory: " + aKey
				+ "... but it does not exist");
		}
		return ac;
	}
	
	public AbilityCategory silentlyGetAbilityCategory(final String aKey)
	{
		for ( final AbilityCategory cat : getAllAbilityCategories() )
		{
			if ( cat.getKeyName().equalsIgnoreCase(aKey) )
			{
				return cat;
			}
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
		if ( !theAbilityCategories.contains(AbilityCategory.FEAT) )
		{
			theAbilityCategories.add(0, AbilityCategory.FEAT);
		}
		List<AbilityCategory> allCats = new ArrayList<AbilityCategory>();
		allCats.addAll(theAbilityCategories);
		allCats.addAll(theLstAbilityCategories);
		return Collections.unmodifiableCollection(allCats);
	}
	
	/**
	 * Returns a <tt>Collection</tt> of <tt>AbilityCategory</tt> objects 
	 * defined by this game mode that match the display location.
	 * 
	 * @param displayLoc The display location to filter for.
	 * @return A <tt>Collection</tt> of <tt>AbilityCategory</tt> objects.
	 */
	public Collection<AbilityCategory> getAllAbilityCatsForDisplayLoc(String displayLoc)
	{
		if (displayLoc == null)
		{
			return Collections.emptyList();
		}
		List<AbilityCategory> catList = new ArrayList<AbilityCategory>();
		for (AbilityCategory cat : getAllAbilityCategories())
		{
			if (displayLoc.equals(cat.getDisplayLocation()))
			{
				catList.add(cat);
			}
		}
		return Collections.unmodifiableCollection(catList);
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
		List<AbilityCategory> catList = new ArrayList<AbilityCategory>();
		for (AbilityCategory cat : getAllAbilityCategories())
		{
			if (key.equals(cat.getKeyName())
				|| key.equals(cat.getAbilityCategory()))
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
	
	public String getPreviewDir()
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
	 * the dieSizes array
	 * 
	 * @param value
	 */
	public void setDieSizes(final String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, ",", false);
		List<Integer> list = new ArrayList<Integer>();
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			// in case there is training\leading whitespace after the comma split
			aString = aString.trim(); 
			String minValue;
			String maxValue;
			
			try
			{
			if (aString.contains("MIN="))
				{
					String[] t = aString.split("MIN=");
					minValue = t[1];
					int die = Integer.parseInt(minValue);
					setMinDieSize(die);
					list.add(die);
				}
				else if (aString.contains("MAX="))
				{
					String[] t = aString.split("MAX=");
					maxValue = t[1];
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
		if (list.size() == 0)
		{
			return;
		}
		
		int[] dieSizes = new int[list.size()];
		
		for (int i = 0; i < list.size(); i++)
		{
			dieSizes[i] = list.get(i);
		}
		list = null;
		this.setDieSizes(dieSizes);
	}
	
	/**
	 * Get's current gamemodes MaxDieSize
	 * @return maxDieSize
	 */
	public int getMaxDieSize()
	{
		return maxDieSize;
	}	
	/**
	 * Sets's current gamemodes MaxDieSize
	 * @param dice 
	 */	
	public void setMaxDieSize(final int dice)
	{
		maxDieSize = dice;
	}
	
	/**
	 * Get's current gamemodes MinDieSize
	 * @return minDieSize
	 */
	public int getMinDieSize()
	{
		return minDieSize;
	}
	/**
	 * Sets's current gamemodes MinDieSize
	 * @param dice 
	 */
	public void setMinDieSize(final int dice)
	{
		minDieSize = dice;
	}
		

	/**
	 * Get's current gamemodes DieSizes
	 * @return dieSizes array
	 */
	public int[] getDieSizes()
	{
		return dieSizes;
	}
	/**
	 * Set's DieSizes available for the gamemode
	 * @param die The parsed integer diesizes
	 */
	public void setDieSizes(int[] die)
	{
		this.dieSizes = die;
	}
}

