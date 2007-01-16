/*
 * PlayerCharacter.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import pcgen.core.Ability.Nature;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
// import pcgen.core.bonus.TypedBonus;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.EquipSet;
import pcgen.core.character.EquipSlot;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.spell.PCSpellTracker;
import pcgen.core.spell.Spell;
import pcgen.core.system.GameModeRollMethod;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.core.utils.StringKey;
import pcgen.gui.GuiConstants;
import pcgen.io.PCGFile;
import pcgen.io.exporttoken.BonusToken;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Delta;
import pcgen.util.DoubleKeyMap;
import pcgen.util.HashMapToList;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.AttackType;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;
import pcgen.util.enumeration.VisionType;

/**
 * <code>PlayerCharacter</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class PlayerCharacter extends Observable implements Cloneable,
		VariableContainer
{
	// Constants for use in getBonus
	/** ATTACKBONUS = 0 */
	public static final int ATTACKBONUS = 0;
	/** MONKBONUS = 4 */
	public static final int MONKBONUS = 4;
	private static String lastVariable = null;

	// List of Armor Proficiencies
	private final List<String> armorProfList = new ArrayList<String>();

	// List of misc items (Assets, Magic items, etc)
	private final ArrayList<String> miscList = new ArrayList<String>(3);

	// List of Note objects
	private final ArrayList<NoteItem> notesList = new ArrayList<NoteItem>();

	// This may be different from file name
	private final ArrayList<Equipment> primaryWeapons = new ArrayList<Equipment>();
	private final ArrayList<Equipment> secondaryWeapons = new ArrayList<Equipment>();
	private final ArrayList<String> shieldProfList = new ArrayList<String>();

	// List of Skills
	private final ArrayList<Skill> skillList = new ArrayList<Skill>();

	// Collections of String (probably should be full objects)
	private final ArrayList<SpecialAbility> specialAbilityList = new ArrayList<SpecialAbility>();

	// List of Template objects
	private final ArrayList<PCTemplate> templateList = new ArrayList<PCTemplate>(); // of
																					// Template

	// List of VARs
	private final ArrayList<String> variableList = new ArrayList<String>();
	private BigDecimal gold = new BigDecimal(0);
	private Deity deity = null;

	// source of granted domains
	private HashMap<String, String> domainSourceMap = new HashMap<String, String>();
	private List<BonusObj> activeBonusList = new ArrayList<BonusObj>();
	private final List<CharacterDomain> characterDomainList = new ArrayList<CharacterDomain>();

	// List of Classes
	private ArrayList<PCClass> classList = new ArrayList<PCClass>();

	// List of CompanionMods
	private final ArrayList<CompanionMod> companionModList = new ArrayList<CompanionMod>();
	/** This character's list of followers */
	private final List<Follower> followerList = new ArrayList<Follower>();
	private HashMapToList<Class, String> qualifyArrayMap = new HashMapToList<Class, String>();
	private Follower followerMaster = null; // Who is the master now?

	// List of Equip Sets
	private final List<EquipSet> equipSetList = new ArrayList<EquipSet>();

	// List of Equipment
	private List<Equipment> equipmentList = new ArrayList<Equipment>();
	private List<Equipment> equipmentMasterList = new ArrayList<Equipment>();
	private List<PCLevelInfo> pcLevelInfo = new ArrayList<PCLevelInfo>();
	// TODO This probably should not be a member but should be passed around
	private List<BonusObj> processedBonusList = new ArrayList<BonusObj>();
	private final List<String> spellBooks = new ArrayList<String>();
	private Map<String, SpellBook> spellBookMap = new HashMap<String, SpellBook>();

	// Temporary Bonuses
	private List<Equipment> tempBonusItemList = new ArrayList<Equipment>();
	private List<BonusObj> tempBonusList = new ArrayList<BonusObj>();
	private Set<String> tempBonusFilters = new TreeSet<String>();

	private Map<String, String> activeBonusMap = new TreeMap<String, String>();
	private Race race = null;
	private final SortedSet<String> favoredClasses = new TreeSet<String>();
	private final StatList statList = new StatList(this);

	// List of Kit objects
	private List<Kit> kitList = null;

	// Spells
	private PCSpellTracker spellTracker = null;

	//
	// We don't want this list sorted until after it has been added
	// to the character, The reason is that sorting prevents
	// .CLEAR-TEMPLATES from clearing the OLDER template languages.
	private final List<Language> templateAutoLanguages = new ArrayList<Language>();
	private final SortedSet<Language> templateLanguages = new TreeSet<Language>();
	private final SortedSet<Language> languages = new TreeSet<Language>();
	private Map<StringKey, String> stringChar = new HashMap<StringKey, String>();
	private String calcEquipSetId = "0.1"; //$NON-NLS-1$
	private String descriptionLst = "EMPTY"; //$NON-NLS-1$
	private String tabName = Constants.EMPTY_STRING;
	private String gender = Globals.getAllGenders().get(0);
	private HashSet<String> variableSet = new HashSet<String>();

	// Weapon, Armor and Shield proficiencies
	// private final TreeSet<WeaponProf> weaponProfList = new
	// TreeSet<WeaponProf>();
	private Double[] movementMult = Globals.EMPTY_DOUBLE_ARRAY;
	private String[] movementMultOp = Globals.EMPTY_STRING_ARRAY;
	private String[] movementTypes = Globals.EMPTY_STRING_ARRAY;

	// Movement lists
	private Double[] movements = Globals.EMPTY_DOUBLE_ARRAY;

	private boolean armorProfListStable = false;

	// whether to add auto known spells each level
	private boolean autoKnownSpells = true;

	// whether higher level spell slots should be used for lower levels
	private boolean useHigherKnownSlots = SettingsHandler
		.isUseHigherLevelSlotsDefault();
	private boolean useHigherPreppedSlots = SettingsHandler
		.isUseHigherLevelSlotsDefault();

	// should we also load companions on master load?
	private boolean autoLoadCompanion = false;

	// Should we sort the gear automatically?
	private boolean autoSortGear = true;

	private boolean qualifyListStable = false;
	private final boolean useMonsterDefault = SettingsHandler
		.isMonsterDefault();

	// output sheet locations
	private String outputSheetHTML = Constants.EMPTY_STRING;
	private String outputSheetPDF = Constants.EMPTY_STRING;
	private boolean[] ageSetKitSelections = new boolean[10];
	private boolean dirtyFlag = false;
	private int serial = 0;
	private boolean displayUpdate = false;
	private boolean importing = false;

	// Should temp mods/bonuses be used/saved?
	private boolean useTempMods = true;

	private int age = 0;

	// 0 = LG to 8 = CE and 9 is <none selected>
	private int alignment = 9;
	private int costPool = 0;
	private int currentEquipSetNumber = 0;
	private int earnedXP = 0;

	// order in which the equipment will be output.
	private int equipOutputOrder = GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC;
	private int freeLangs = 0;
	private int heightInInches = 0; // in inches

	// pool of stats remaining to distribute
	private int poolAmount = 0;

	// order in which the skills will be output.
	private int skillsOutputOrder = GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC;
	private int spellLevelTemp = 0;
	private int weightInPounds = 0; // in pounds
	private VariableProcessor variableProcessor;

	// used by point buy. Total number of points for method, not points
	// remaining
	private int pointBuyPoints = -1;

	private boolean processLevelAbilities = true;

	/**
	 * Abilities stored as a double key map. The keys are the Category and type
	 * (Normal, Virtual, or Automatic). The value is a list of abilities that
	 * match the keys.
	 */
	private DoubleKeyMap<AbilityCategory, Ability.Nature, List<Ability>> theAbilities = new DoubleKeyMap<AbilityCategory, Ability.Nature, List<Ability>>();

	/**
	 * This map stores any user bonuses (entered through the GUI) to the
	 * corrisponding ability pool.
	 */
	private Map<AbilityCategory, BigDecimal> theUserPoolBonuses = null;

	private Set<WeaponProf> theWeaponProfs = null;
	private Map<String, WeaponProf> cachedWeaponProfs = null;

	// private Map<String, List<TypedBonus>> theBonusMap = new HashMap<String,
	// List<TypedBonus>>();

	// /////////////////////////////////////
	// operations
	/**
	 * Constructor
	 */
	public PlayerCharacter()
	{
		variableProcessor = new VariableProcessorPC(this);

		for (int i = 0; i < 10; i++)
		{
			ageSetKitSelections[i] = false;
		}

		Globals.setCurrentPC(this);

		for (int i = 0, x = SettingsHandler.getGame().s_ATTRIBLONG.length; i < x; ++i)
		{
			final PCStat stat = SettingsHandler.getGame()
				.getUnmodifiableStatList().get(i);
			statList.addStat(stat.clone());
		}

		setRace(Globals.s_EMPTYRACE);
		setName(Constants.EMPTY_STRING);
		setFeats(0);
		rollStats(SettingsHandler.getGame().getRollMethod());
		miscList.add(Constants.EMPTY_STRING);
		miscList.add(Constants.EMPTY_STRING);
		miscList.add(Constants.EMPTY_STRING);
		addSpellBook(new SpellBook(Globals.getDefaultSpellBook(),
			SpellBook.TYPE_KNOWN_SPELLS));
		addSpellBook(new SpellBook(Globals.INNATE_SPELL_BOOK_NAME,
			SpellBook.TYPE_KNOWN_SPELLS));
		populateSkills(SettingsHandler.getSkillsTab_IncludeSkills());
		spellTracker = new PCSpellTracker(this);
		setStringFor(StringKey.HANDED, PropertyFactory.getString("in_right")); //$NON-NLS-1$
	}

	/**
	 * Get the active bonus map
	 * 
	 * @return active bonus map
	 */
	public Map<String, String> getActiveBonusMap()
	{
		return activeBonusMap;
	}

	/**
	 * Set the age
	 * 
	 * @param i
	 */
	public void setAge(final int i)
	{
		age = i;
		setDirty(true);
		calcActiveBonuses();

		if (!isImporting())
		{
			Globals.getBioSet().makeKitSelectionFor(this);
		}
	}

	/**
	 * Get the age
	 * 
	 * @return age
	 */
	public int getAge()
	{
		return age;
	}

	/**
	 * Alignment of this PC
	 * 
	 * @return alignment
	 */
	public int getAlignment()
	{
		return alignment;
	}

	/**
	 * if checkBonus is true, then search for all skills with a SKILLRANK bonus
	 * to include in list as well TODO This is bogus. Not only does it return
	 * skills with a bonus but it modifies the PC's skill list to include them.
	 * 
	 * @param checkBonus
	 * @return ArrayList
	 */
	public ArrayList<Skill> getAllSkillList(final boolean checkBonus)
	{
		if (!checkBonus)
		{
			return skillList;
		}

		for (final Skill skill : Globals.getSkillList())
		{
			if (!hasSkill(skill.getKeyName()))
			{
				if (!CoreUtility.doublesEqual(skill.getSkillRankBonusTo(this),
					0.0))
				{
					addSkill(skill);
				}
			}
		}

		return skillList;
	}

	/**
	 * Retrieve those skills in the character's skill list that match the
	 * supplied visibility level.
	 * 
	 * @param vis
	 *            What level of visibility skills are desired.
	 * 
	 * @return A list of the character's skills matching the visibility
	 *         criteria.
	 */
	public ArrayList<Skill> getPartialSkillList(Visibility vis)
	{
		// Now select the required set of skills, based on their visibility.
		ArrayList<Skill> aList = new ArrayList<Skill>();

		for (Skill aSkill : skillList)
		{
			final Visibility skillVis = aSkill.getVisibility();

			if ((vis == Visibility.DEFAULT) || (skillVis == Visibility.DEFAULT)
				|| (skillVis == vis))
			{
				aList.add(aSkill);
			}

		}
		return aList;
	}

	/**
	 * Get the armor proficiency list
	 * 
	 * @return armor proficiency list
	 */
	public List<String> getArmorProfList()
	{
		if (armorProfListStable)
		{
			return armorProfList;
		}

		final List<String> autoArmorProfList = getAutoArmorProfList();
		addArmorProfs(autoArmorProfList);

		final List<String> selectedProfList = getSelectedArmorProfList();
		addArmorProfs(selectedProfList);
		armorProfListStable = true;

		return armorProfList;
	}

	/**
	 * Sets a 'stable' list of armor profs
	 * 
	 * @param arg
	 */
	public void setArmorProfListStable(final boolean arg)
	{
		armorProfListStable = arg;
		setDirty(true);
	}

	/**
	 * Returns the Spell Stat bonus for a class
	 * 
	 * @param aClass
	 * @return base spell stat bonus
	 */
	public int getBaseSpellStatBonus(final PCClass aClass)
	{
		if (aClass == null)
		{
			return 0;
		}

		int baseSpellStat = 0;
		final String statString = aClass.getSpellBaseStat();

		if (!statString.equals(Constants.s_NONE))
		{
			final int statIndex = getStatList().getIndexOfStatFor(statString);
			if (statIndex >= 0)
			{
				baseSpellStat = getStatList().getTotalStatFor(statString);
				// final List<TypedBonus> bonuses = getBonusesTo("STAT",
				// "BASESPELLSTAT");
				// bonuses.addAll( getBonusesTo("STAT",
				// "BASESPELLSTAT;CLASS."+aClass.getKeyName()) );
				// bonuses.addAll( getBonusesTo("STAT", "CAST." + statString) );
				// baseSpellStat += TypedBonus.totalBonuses(bonuses);
				baseSpellStat += (int) getTotalBonusTo("STAT", "BASESPELLSTAT");
				baseSpellStat += (int) getTotalBonusTo("STAT",
					"BASESPELLSTAT;CLASS." + aClass.getKeyName());
				baseSpellStat += (int) getTotalBonusTo("STAT", "CAST."
					+ statString);
				baseSpellStat = getStatList().getModForNumber(baseSpellStat,
					statIndex);
			}

		}

		return baseSpellStat;
	}

	/**
	 * Set BIO
	 * 
	 * @param aString
	 */
	public void setBio(final String aString)
	{
		setStringFor(StringKey.BIO, aString);
	}

	/**
	 * Get the BIO
	 * 
	 * @return the BIO
	 */
	public String getBio()
	{
		return getSafeStringFor(StringKey.BIO);
	}

	/**
	 * Set the birthday
	 * 
	 * @param aString
	 */
	public void setBirthday(final String aString)
	{
		setStringFor(StringKey.BIRTHDAY, aString);
	}

	/**
	 * Get the birthday
	 * 
	 * @return birthday
	 */
	public String getBirthday()
	{
		return getSafeStringFor(StringKey.BIRTHDAY);
	}

	/**
	 * Set the birthplace
	 * 
	 * @param aString
	 */
	public void setBirthplace(final String aString)
	{
		setStringFor(StringKey.BIRTHPLACE, aString);
	}

	/**
	 * Get the birthplace
	 * 
	 * @return birthplace
	 */
	public String getBirthplace()
	{
		return getSafeStringFor(StringKey.BIRTHPLACE);
	}

	/**
	 * Set the current EquipSet that is used to Bonus/Equip calculations
	 * 
	 * @param eqSetId
	 */
	public void setCalcEquipSetId(final String eqSetId)
	{
		calcEquipSetId = eqSetId;
		setDirty(true);
	}

	/**
	 * Get the id for the equipment set being used for calculation
	 * 
	 * @return id
	 */
	public String getCalcEquipSetId()
	{
		if (equipSetList.isEmpty())
		{
			return calcEquipSetId;
		}

		if (getEquipSetByIdPath(calcEquipSetId) == null)
		{
			// PC does not have that equipset ID
			// so we need to find one they do have
			for (EquipSet eSet : equipSetList)
			{
				if (eSet.getParentIdPath().equals(EquipSet.ROOT_ID))
				{
					calcEquipSetId = eSet.getIdPath();

					return calcEquipSetId;
				}
			}
		}

		return calcEquipSetId;
	}

	/**
	 * Set's current equipmentList to selected output EquipSet then loops
	 * through all the equipment and sets the correct status of each (equipped,
	 * carried, etc)
	 */
	public void setCalcEquipmentList()
	{
		setCalcEquipmentList(false);
	}

	/**
	 * Set's current equipmentList to selected output EquipSet then loops
	 * through all the equipment and sets the correct status of each (equipped,
	 * carried, etc)
	 * 
	 * @param useTempBonuses
	 */
	public void setCalcEquipmentList(final boolean useTempBonuses)
	{
		// First we get the EquipSet that is going to be used
		// to calculate everything from
		final String calcId = getCalcEquipSetId();
		final EquipSet eSet = getEquipSetByIdPath(calcId);

		if (eSet == null)
		{
			Logging
				.debugPrint("No EquipSet has been selected for calculations yet."); //$NON-NLS-1$
			return;
		}

		// new equipment list
		final List<Equipment> eqList = new ArrayList<Equipment>();

		// set PC's equipmentList to new one
		setEquipmentList(eqList);

		// get all the PC's EquipSet's
		final List<EquipSet> pcEquipSetList = getEquipSet();

		if (pcEquipSetList.isEmpty())
		{
			return;
		}

		// make sure EquipSet's are in sorted order
		// (important for Containers contents)
		Collections.sort(pcEquipSetList);

		// loop through all the EquipSet's and create equipment
		// then set status to equipped and add to PC's equipment list
		for (EquipSet es : pcEquipSetList)
		{
			final String abCalcId = calcId + EquipSet.PATH_SEPARATOR;
			final String abParentId = es.getParentIdPath()
				+ EquipSet.PATH_SEPARATOR;

			// calcId = 0.1.
			// parentIdPath = 0.10.
			// OR
			// calcId = 0.10.
			// parentIdPath = 0.1.
			if (!abParentId.startsWith(abCalcId))
			{
				continue;
			}

			final Equipment eqI = es.getItem();

			if (eqI == null)
			{
				continue;
			}

			final Equipment eq = es.getItem();
			final String aLoc = es.getName();
			final String aNote = es.getNote();
			Float num = es.getQty();
			final StringTokenizer aTok = new StringTokenizer(es.getIdPath(),
				EquipSet.PATH_SEPARATOR);

			// if the eSet.getIdPath() is longer than 3
			// it's inside a container, don't try to equip
			if (aTok.countTokens() > 3)
			{
				eq.setLocation(Equipment.CONTAINED);
				eq.setIsEquipped(false, this);
				eq.setNumberCarried(num);
				eq.setQty(num);
			}
			else if (aLoc.startsWith(Constants.S_CARRIED))
			{
				eq.setLocation(Equipment.CARRIED_NEITHER);
				eq.setIsEquipped(false, this);
				eq.setNumberCarried(num);
				eq.setQty(num);
			}
			else if (aLoc.startsWith(Constants.S_NOTCARRIED))
			{
				eq.setLocation(Equipment.NOT_CARRIED);
				eq.setIsEquipped(false, this);
				eq.setNumberCarried(Float.valueOf(0));
				eq.setQty(num);
			}
			else if (eq.isWeapon())
			{
				if (aLoc.equals(Constants.S_PRIMARY)
					|| aLoc.equals(Constants.S_NATURAL_PRIMARY))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_PRIMARY);
					eq.setIsEquipped(true, this);
				}
				else if (aLoc.startsWith(Constants.S_SECONDARY)
					|| aLoc.equals(Constants.S_NATURAL_SECONDARY))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_SECONDARY);
					eq.setIsEquipped(true, this);
				}
				else if (aLoc.equals(Constants.S_BOTH))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_BOTH);
					eq.setIsEquipped(true, this);
				}
				else if (aLoc.equals(Constants.S_DOUBLE))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(2);
					eq.setLocation(Equipment.EQUIPPED_TWO_HANDS);
					eq.setIsEquipped(true, this);
				}
				else if (aLoc.equals(Constants.S_UNARMED))
				{
					eq.setLocation(Equipment.EQUIPPED_NEITHER);
					eq.setNumberEquipped(num.intValue());
				}
				else if (aLoc.equals(Constants.S_TWOWEAPONS))
				{
					if (num.doubleValue() < 2.0)
					{
						num = new Float(2.0);
					}

					es.setQty(num);
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(2);
					eq.setLocation(Equipment.EQUIPPED_TWO_HANDS);
					eq.setIsEquipped(true, this);
				}
				else if (aLoc.equals(Constants.S_SHIELD))
				{
					eq.setLocation(Equipment.EQUIPPED_NEITHER);
					eq.setNumberEquipped(num.intValue());
				}
			}
			else
			{
				eq.setLocation(Equipment.EQUIPPED_NEITHER);
				eq.setIsEquipped(true, this);
				eq.setNumberCarried(num);
				eq.setQty(num);
			}

			if ((aNote != null) && (aNote.length() > 0))
			{
				eq.setNote(aNote);
			}

			addLocalEquipment(eq);
		}

		// loop through all equipment and make sure that
		// containers contents are updated
		for (Equipment eq : getEquipmentList())
		{
			if (eq.isContainer())
			{
				eq.updateContainerContentsString(this);
			}

			// also make sure the masterList output order is
			// preserved as this equipmentList is a modified
			// clone of the original
			final Equipment anEquip = getEquipmentNamed(eq.getName());

			if (anEquip != null)
			{
				eq.setOutputIndex(anEquip.getOutputIndex());
			}
		}

		// if temporary bonuses, read the bonus equipList
		if (useTempBonuses)
		{
			for (Equipment eq : getTempBonusItemList())
			{
				// make sure that this EquipSet is the one
				// this temporary bonus item comes from
				// to make sure we keep them together
				final Equipment anEquip = getEquipmentNamed(eq.getName(),
					getEquipmentList());

				if (anEquip != null)
				{
					eq.setQty(anEquip.getQty());
					eq.setNumberCarried(anEquip.getCarried());

					if (anEquip.isEquipped())
					{
						if (eq.isWeapon())
						{
							eq.setSlots(0);
							eq.setCost("0");
							eq.setWeight("0");
							eq.setLocation(anEquip.getLocation());
						}
						else
						{
							// replace the orig item
							// with the bonus item
							eq.setLocation(anEquip.getLocation());
							removeLocalEquipment(anEquip);
							anEquip.setIsEquipped(false, this);
							anEquip.setLocation(Equipment.NOT_CARRIED);
							anEquip.setNumberCarried(Float.valueOf(0));
						}

						eq.setIsEquipped(true, this);
						eq.setNumberEquipped(1);
					}
					else
					{
						eq.setCost("0");
						eq.setWeight("0");
						eq.setLocation(Equipment.EQUIPPED_TEMPBONUS);
						eq.setIsEquipped(false, this);
					}

					// Adding this type to be
					// correctly treated by Merge
					eq.setTypeInfo("TEMPORARY");
					addLocalEquipment(eq);
				}
			}
		}

		// all done!
	}

	/**
	 * 
	 * @param aPC
	 */
	public void setCalcFollowerBonus(final PlayerCharacter aPC)
	{
		setDirty(true);

		for (Follower aF : getFollowerList())
		{
			final String rType = aF.getType().toUpperCase();
			final String rName = aF.getRace().toUpperCase();

			for (CompanionMod cm : Globals.getCompanionMods(rType))
			{
				final String aType = cm.getType().toUpperCase();
				final int iRace = cm.getLevel(rName);

				if (aType.equals(rType) && (iRace == 1))
				{
					// Found race and type of follower
					// so add bonus to the master
					addCompanionMod(cm);
					cm.activateBonuses(aPC);
				}
			}
		}
	}

	/**
	 * Adds a <tt>CompanionMod</tt> to the character.
	 * 
	 * @param aMod
	 *            The <tt>CompanionMod</tt> to add.
	 */
	public void addCompanionMod(final CompanionMod aMod)
	{
		companionModList.add(aMod);
	}

	/**
	 * Set the catchphrase
	 * 
	 * @param aString
	 */
	public void setCatchPhrase(final String aString)
	{
		setStringFor(StringKey.CATCH_PHRASE, aString);
	}

	/**
	 * Get the catchphrase
	 * 
	 * @return catchphrase
	 */
	public String getCatchPhrase()
	{
		return getSafeStringFor(StringKey.CATCH_PHRASE);
	}

	/**
	 * Get the class given a key
	 * 
	 * @param aString
	 * @return PCClass
	 */
	public PCClass getClassKeyed(final String aString)
	{
		for (PCClass aClass : classList)
		{
			if (aClass.getKeyName().equalsIgnoreCase(aString))
			{
				return aClass;
			}
		}

		return null;
	}

	/**
	 * Get the class list
	 * 
	 * @return classList
	 */
	public ArrayList<PCClass> getClassList()
	{
		return classList;
	}

	/**
	 * Set the cost pool
	 * 
	 * @param i
	 */
	public void setCostPool(final int i)
	{
		costPool = i;
	}

	/**
	 * Get the cost pool
	 * 
	 * @return costPool
	 */
	public int getCostPool()
	{
		return costPool;
	}

	/**
	 * Get the spell tracker
	 * 
	 * @return spellTracker
	 */
	public PCSpellTracker getSpellTracker()
	{
		return spellTracker;
	}

	/**
	 * Get a list of types that apply to this character
	 * 
	 * @return a List of Strings where each String is a type that the character
	 *         has. The list returned will never be null
	 * 
	 * @deprecated Use getRaceType() and getRaceSubTypes() instead
	 */
	@Deprecated
	public List<String> getTypes()
	{
		final List<String> list = new ArrayList<String>();

		if (race != null)
		{
			list.add(race.getType());
		}
		else
		{
			list.add("Humanoid");
		}

		for (PCTemplate t : templateList)
		{
			list.add(t.getType());
		}

		return list;
	}

	@Deprecated
	public String getCritterType()
	{
		final StringBuffer critterType = new StringBuffer();

		// Not too sure about this if, but that's what the previous code
		// implied...
		if (race != null)
		{
			critterType.append(race.getType());
		}
		else
		{
			critterType.append("Humanoid");
		}

		if (!templateList.isEmpty())
		{
			for (PCTemplate t : templateList)
			{
				final String aType = t.getType();

				if (!"".equals(aType))
				{
					critterType.append('|').append(aType);
				}
			}
		}

		return critterType.toString();
	}

	/**
	 * Returns a String with the characters Race Type (e.g. Humanoid).
	 * 
	 * @return The character's race type or &quot;None&quot;
	 */
	public String getRaceType()
	{
		String raceType = Constants.s_NONE;
		if (race != null)
		{
			raceType = race.getRaceType();
		}
		if (!companionModList.isEmpty())
		{
			for (CompanionMod cm : companionModList)
			{
				final String aType = cm.getRaceType();
				if (!Constants.EMPTY_STRING.equals(aType))
				{
					raceType = aType;
				}
			}
		}
		if (!templateList.isEmpty())
		{
			for (PCTemplate t : templateList)
			{
				final String aType = t.getRaceType();

				if (!Constants.EMPTY_STRING.equals(aType))
				{
					raceType = aType;
				}
			}
		}
		return raceType;
	}

	/**
	 * Gets a <tt>List</tt> of racial subtypes for the character (e.g. Good).
	 * 
	 * @return A unmodifiable <tt>List</tt> of subtypes.
	 */
	public List<String> getRacialSubTypes()
	{
		final ArrayList<String> racialSubTypes = new ArrayList<String>(race
			.getRacialSubTypes());
		if (!templateList.isEmpty())
		{
			final Set<String> added = new TreeSet<String>();
			final Set<String> removed = new TreeSet<String>();
			for (PCTemplate aTemplate : templateList)
			{
				added.addAll(aTemplate.getAddedSubTypes());
				removed.addAll(aTemplate.getRemovedSubTypes());
			}
			for (final String subType : added)
			{
				if (!racialSubTypes.contains(subType))
				{
					racialSubTypes.add(subType);
				}
			}
			for (String s : removed)
			{
				racialSubTypes.remove(s);
			}
		}

		return Collections.unmodifiableList(racialSubTypes);
	}

	/**
	 * Set the current equipment set name
	 * 
	 * @param aName
	 */
	public void setCurrentEquipSetName(final String aName)
	{
		setStringFor(StringKey.CURRENT_EQUIP_SET_NAME, aName);
	}

	/**
	 * Get the current equipment set name
	 * 
	 * @return equipment set name
	 */
	public String getCurrentEquipSetName()
	{
		return getSafeStringFor(StringKey.CURRENT_EQUIP_SET_NAME);
	}

	/**
	 * Get the deity
	 * 
	 * @return deity
	 */
	public Deity getDeity()
	{
		return deity;
	}

	/**
	 * Set the description
	 * 
	 * @param aString
	 */
	public void setDescription(final String aString)
	{
		setStringFor(StringKey.DESCRIPTION, aString);
	}

	/**
	 * Get the description
	 * 
	 * @return description
	 */
	public String getDescription()
	{
		return getSafeStringFor(StringKey.DESCRIPTION);
	}

	/**
	 * Selector
	 * 
	 * @return description lst
	 */
	public String getDescriptionLst()
	{
		return descriptionLst;
	}

	/**
	 * Sets the character changed since last save.
	 * 
	 * @param dirtyState
	 */
	public void setDirty(final boolean dirtyState)
	{
		if (dirtyState)
		{
			cachedWeaponProfs = null;
			serial++;
			getVariableProcessor().setSerial(serial);
			setAggregateAbilitiesStable(null, false);
		}

		// TODO - This is kind of strange. We probably either only want to
		// notify our observers if we have gone from not dirty to dirty and not
		// the reverse case. At a minimum we should probably tell them the
		// state anyway.
		if (dirtyFlag != dirtyState)
		{
			dirtyFlag = dirtyState;

			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets whether the character has been changed since last saved.
	 * 
	 * @return true if dirty
	 */
	public boolean isDirty()
	{
		return dirtyFlag;
	}

	/**
	 * Returns the serial for the instance - every time something changes the
	 * serial is incremented. Use to detect change in PlayerCharacter.
	 * 
	 * @return serial
	 */
	public int getSerial()
	{
		return serial;
	}

	/**
	 * @return display name
	 */
	public String getDisplayName()
	{
		final String custom = getTabName();

		if (!Constants.EMPTY_STRING.equals(custom))
		{
			return custom;
		}

		final StringBuffer displayName = new StringBuffer().append(getName());

		// TODO - i18n
		switch (SettingsHandler.getNameDisplayStyle())
		{
			case Constants.DISPLAY_STYLE_NAME:
				break;

			case Constants.DISPLAY_STYLE_NAME_CLASS:
				displayName.append(" the ").append(getDisplayClassName());

				break;

			case Constants.DISPLAY_STYLE_NAME_RACE:
				displayName.append(" the ").append(getDisplayRaceName());

				break;

			case Constants.DISPLAY_STYLE_NAME_RACE_CLASS:
				displayName.append(" the ").append(getDisplayRaceName())
					.append(' ').append(getDisplayClassName());

				break;

			case Constants.DISPLAY_STYLE_NAME_FULL:
				return getFullDisplayName();

			default:
				break; // custom broken
		}

		return displayName.toString();
	}

	/**
	 * set display update TODO - This probably doesn't belong here. It seems to
	 * only be used by InfoSkills.
	 * 
	 * @param aDisplayUpdate
	 */
	public void setDisplayUpdate(final boolean aDisplayUpdate)
	{
		this.displayUpdate = aDisplayUpdate;
	}

	/**
	 * is display update
	 * 
	 * @return True if display update
	 */
	public boolean isDisplayUpdate()
	{
		return displayUpdate;
	}

	/**
	 * Get the list of equipment sets
	 * 
	 * @return List
	 */
	public List<EquipSet> getEquipSet()
	{
		return equipSetList;
	}

	/**
	 * Get the equipment set given id
	 * 
	 * @param id
	 * @return EquipSet
	 */
	public EquipSet getEquipSetByIdPath(final String id)
	{
		if (equipSetList.isEmpty())
		{
			return null;
		}

		for (EquipSet eSet : equipSetList)
		{
			if (eSet.getIdPath().equals(id))
			{
				return eSet;
			}
		}

		return null;
	}

	/**
	 * Get the equipment set by name
	 * 
	 * @param aName
	 * @return Equip set
	 */
	public EquipSet getEquipSetByName(final String aName)
	{
		if (equipSetList.isEmpty())
		{
			return null;
		}

		for (EquipSet eSet : equipSetList)
		{
			if (eSet.getName().equals(aName))
			{
				return eSet;
			}
		}

		return null;
	}

	/**
	 * Set the number of the current equipset when exporting
	 * 
	 * @param anInt
	 */
	public void setEquipSetNumber(final int anInt)
	{
		currentEquipSetNumber = anInt;
		setDirty(true);
	}

	/**
	 * Get the equipment set number
	 * 
	 * @return equipset number
	 */
	public int getEquipSetNumber()
	{
		return currentEquipSetNumber;
	}

	/**
	 * gets the total weight in an EquipSet
	 * 
	 * @param idPath
	 * @return equipment set weight
	 */
	public double getEquipSetWeightDouble(final String idPath)
	{
		if (equipSetList.isEmpty())
		{
			return 0.0;
		}

		double totalWeight = 0.0;

		for (EquipSet es : equipSetList)
		{
			final String abIdPath = idPath + EquipSet.PATH_SEPARATOR;
			final String esIdPath = es.getIdPath() + EquipSet.PATH_SEPARATOR;

			if (!esIdPath.startsWith(abIdPath))
			{
				continue;
			}

			final Equipment eqI = es.getItem();

			if (eqI != null)
			{
				if ((eqI.getCarried().floatValue() > 0.0f)
					&& (eqI.getParent() == null))
				{
					if (eqI.getChildCount() > 0)
					{
						totalWeight += (eqI.getWeightAsDouble(this) + eqI
							.getContainedWeight(this).floatValue());
					}
					else
					{
						totalWeight += (eqI.getWeightAsDouble(this) * eqI
							.getCarried().floatValue());
					}
				}
			}
		}

		return totalWeight;
	}

	/**
	 * Count the total number of items of aName within EquipSet idPath
	 * 
	 * @param idPath
	 * @param aName
	 * @return equipment set count
	 */
	public Float getEquipSetCount(final String idPath, final String aName)
	{
		float count = 0;
		for (EquipSet eSet : getEquipSet())
		{
			final String esID = eSet.getIdPath() + EquipSet.PATH_SEPARATOR;
			final String abID = idPath + EquipSet.PATH_SEPARATOR;
			if (esID.startsWith(abID))
			{
				if (eSet.getValue().equals(aName))
				{
					count += eSet.getQty().floatValue();
				}
			}
		}
		return Float.valueOf(count);
	}

	/**
	 * List of Equipment objects
	 * 
	 * @param eqList
	 */
	public void setEquipmentList(final List<Equipment> eqList)
	{
		equipmentList = eqList;
	}

	/**
	 * Get equipment list
	 * 
	 * @return equipment list
	 */
	public List<Equipment> getEquipmentList()
	{
		return equipmentList;
	}

	/**
	 * Retrieves a list of the character's equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in this list.
	 * 
	 * @return An ArrayList of the equipment objects in output order.
	 */
	public List<Equipment> getEquipmentListInOutputOrder()
	{
		return sortEquipmentList(getEquipmentList());
	}

	/**
	 * Retrieves a list of the character's equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in this list.
	 * 
	 * Deals with merge as well
	 * 
	 * @param merge
	 * 
	 * @return An ArrayList of the equipment objects in output order.
	 */
	public List<Equipment> getEquipmentListInOutputOrder(final int merge)
	{
		return sortEquipmentList(getEquipmentList(), merge);
	}

	/**
	 * Get equipment master list
	 * 
	 * @return equipment master list
	 */
	public List<Equipment> getEquipmentMasterList()
	{
		final ArrayList<Equipment> aList = new ArrayList<Equipment>(
			equipmentMasterList);

		// Try all possible POBjects
		for (PObject aPObj : getPObjectList())
		{
			if (aPObj != null)
			{
				aPObj.addAutoTagsToList("EQUIP", aList, this, true);
			}
		}

		return aList;
	}

	/**
	 * Get equipment master list in output order
	 * 
	 * @return equipment master list in output order
	 */
	public List<Equipment> getEquipmentMasterListInOutputOrder()
	{
		return EquipmentUtilities.mergeEquipmentList(getEquipmentMasterList(),
			Constants.MERGE_NONE);
	}

	/**
	 * Search for a piece of equipment in the specified list by name.
	 * 
	 * TODO - This does not belong in PlayerCharacter. Move to Equipment if
	 * needed.
	 * 
	 * TODO - This probably won't work with i18n. Should always search by key.
	 * 
	 * @param aString
	 *            The name of the equipment.
	 * @param aList
	 *            The list of equipment to search in.
	 * 
	 * @return The <tt>Equipment</tt> object or <tt>null</tt>
	 */
	public Equipment getEquipmentNamed(final String aString,
		final List<Equipment> aList)
	{
		Equipment match = null;

		for (Equipment eq : aList)
		{
			if (aString.equalsIgnoreCase(eq.getName()))
			{
				match = eq;
			}
		}

		return match;
	}

	/**
	 * Set the characters eye colour
	 * 
	 * @param aString
	 *            the colour of their eyes
	 */
	public void setEyeColor(final String aString)
	{
		setStringFor(StringKey.EYE_COLOR, aString);
	}

	/**
	 * Get the characters eye colour
	 * 
	 * @return the colour of their eyes
	 */
	public String getEyeColor()
	{
		return getSafeStringFor(StringKey.EYE_COLOR);
	}

	/**
	 * Get a number that represents the number of feats added to this character
	 * by BONUS statements.
	 * 
	 * @return the number of feats added by bonus statements
	 */
	private double getBonusFeatPool()
	{
		String aString = Globals.getBonusFeatString();

		final StringTokenizer aTok = new StringTokenizer(aString,
			Constants.PIPE, false);
		final int startLevel = Integer.parseInt(aTok.nextToken());
		final int rangeLevel = Integer.parseInt(aTok.nextToken());

		// TODO - Should this stuff follow stacking rules?
		// TODO - Does it even matter anymore.
		// double pool = getBonusValue("FEAT", "POOL");
		// double pcpool = getBonusValue("FEAT", "PCPOOL");
		// double mpool = getBonusValue("FEAT", "MONSTERPOOL");
		double pool = getTotalBonusTo("FEAT", "POOL");
		double pcpool = getTotalBonusTo("FEAT", "PCPOOL");
		double mpool = getTotalBonusTo("FEAT", "MONSTERPOOL");

		Logging.debugPrint(""); //$NON-NLS-1$
		Logging.debugPrint("=============="); //$NON-NLS-1$
		Logging.debugPrint("level " + this.getTotalPlayerLevels()); //$NON-NLS-1$

		Logging.debugPrint("POOL:   " + pool); //$NON-NLS-1$
		Logging.debugPrint("PCPOOL: " + pcpool); //$NON-NLS-1$
		Logging.debugPrint("MPOOL:  " + mpool); //$NON-NLS-1$

		double startAdjust = startLevel / rangeLevel;

		pool += Math.floor((this.getTotalCharacterLevel() >= startLevel) ? 1.0d
			+ pcpool - startAdjust + 0.0001 : pcpool + 0.0001);
		pool += Math.floor(mpool + 0.0001);

		Logging.debugPrint(""); //$NON-NLS-1$
		Logging.debugPrint("Total Bonus: " + pool); //$NON-NLS-1$
		Logging.debugPrint("=============="); //$NON-NLS-1$
		Logging.debugPrint(""); //$NON-NLS-1$

		return pool;
	}

	/**
	 * Checks whether a PC is allowed to level up. A PC is not allowed to level
	 * up if the "Enforce Spending" option is set and he still has unallocated
	 * skill points and/or feat slots remaining. This can be used to enforce
	 * correct spending of these resources when creating high-level multiclass
	 * characters.
	 * 
	 * @return true if the PC can level up
	 */
	public boolean canLevelUp()
	{
		if (SettingsHandler.getEnforceSpendingBeforeLevelUp()
			&& (getSkillPoints() > 0 || getFeats() > 0))
		{
			return false;
		}
		return true;
	}

	/**
	 * Sets the filename of the character.
	 * 
	 * @param newFileName
	 */
	public void setFileName(final String newFileName)
	{
		setStringFor(StringKey.FILE_NAME, newFileName);
	}

	/**
	 * Gets the filename of the character.
	 * 
	 * @return file name of character
	 */
	public String getFileName()
	{
		return getSafeStringFor(StringKey.FILE_NAME);
	}

	/**
	 * Returns the followers associated with this character.
	 * 
	 * @return A <tt>List</tt> of <tt>Follower</tt> objects.
	 */
	public List<Follower> getFollowerList()
	{
		return followerList;
	}

	/**
	 * Returns a very descriptive name for the character.
	 * 
	 * <p>
	 * The format is [name] the [level]th level [race name] [classes]
	 * 
	 * @return A descriptive string name for the character.
	 */
	public String getFullDisplayName()
	{
		final int levels = getTotalLevels();

		// If you aren't multi-classed, don't display redundant class level
		// information in addition to the total PC level
		return new StringBuffer().append(getName()).append(" the ").append(
			levels).append(getOrdinal(levels)).append(" level ").append(
			getDisplayRaceName()).append(' ').append(
			(classList.size() < 2) ? getDisplayClassName()
				: getFullDisplayClassName()).toString();
	}

	/**
	 * Returns a region (including subregion) string for the character.
	 * 
	 * <p/> Build on-the-fly so removing templates won't mess up region
	 * 
	 * @return character region
	 */
	public String getFullRegion()
	{
		final String sub = getSubRegion();
		final StringBuffer tempRegName = new StringBuffer().append(getRegion());

		if (!sub.equals(Constants.s_NONE))
		{
			tempRegName.append(" (").append(sub).append(')');
		}

		return tempRegName.toString();
	}

	/**
	 * Sets the character's gender.
	 * 
	 * <p>
	 * The gender will only be set if the character does not have a template
	 * that locks the character's gender.
	 * 
	 * <p>
	 * <b>WARNING:</b> This method has a side effect that it will actually set
	 * the gender to the locked template gender.
	 * 
	 * @param argGender
	 *            A gender to try and set.
	 */
	public void setGender(final String argGender)
	{
		final String templateGender = findTemplateGender();

		if (templateGender.equals(Constants.s_NONE))
		{
			gender = argGender;
		}
		else
		{
			gender = templateGender;
		}

		setDirty(true);
	}

	/**
	 * Returns a string for the character's gender.
	 * 
	 * <p>
	 * This method will return the stored gender or the template locked gender
	 * if there is one. This means the <tt>setGender()</tt> side effect is not
	 * really required.
	 * 
	 * @return A <tt>String</tt> version of the character's gender. TODO -
	 *         Gender should be an object so it can be i18n.
	 */
	public String getGender()
	{
		final String tGender = findTemplateGender();

		return tGender.equals(Constants.s_NONE) ? gender : tGender;
	}

	/**
	 * Checks if the user is allowed to change the character's gender.
	 * 
	 * <p>
	 * That is, if no template with a gender lock has been specified.
	 * 
	 * @return <tt>true</tt> if the user can freely set the character's
	 *         gender.
	 */
	public boolean canSetGender()
	{
		final String tGender = findTemplateGender();

		return tGender.equals(Constants.s_NONE);
	}

	/**
	 * Sets the character's wealth.
	 * 
	 * <p>
	 * Gold here is used as a character's total purchase power not actual gold
	 * pieces.
	 * 
	 * @param aString
	 *            A String gold amount. TODO - Do this parsing elsewhere.
	 */
	public void setGold(final String aString)
	{
		gold = new BigDecimal(aString);
		setDirty(true);
	}

	/**
	 * Returns the character's total wealth.
	 * 
	 * @see pcgen.core.PlayerCharacter#setGold(String)
	 * 
	 * @return A <tt>BigDecimal</tt> value for the character's wealth.
	 */
	public BigDecimal getGold()
	{
		return gold;
	}

	/**
	 * Sets the character's hair color as a string.
	 * 
	 * @param aString
	 *            The hair color to set.
	 */
	public void setHairColor(final String aString)
	{
		setStringFor(StringKey.HAIR_COLOR, aString);
	}

	/**
	 * Gets the character's hair color.
	 * 
	 * @return A hair color string.
	 */
	public String getHairColor()
	{
		return getSafeStringFor(StringKey.HAIR_COLOR);
	}

	/**
	 * Sets the character's hair style.
	 * 
	 * @param aString
	 *            A hair style.
	 */
	public void setHairStyle(final String aString)
	{
		setStringFor(StringKey.HAIR_STYLE, aString);
	}

	/**
	 * Gets the character's hair style.
	 * 
	 * @return The character's hair style.
	 */
	public String getHairStyle()
	{
		return getSafeStringFor(StringKey.HAIR_STYLE);
	}

	/**
	 * Sets the character's handedness.
	 * 
	 * @param aString
	 *            A String to use as a handedness.
	 * 
	 * TODO - This should probably be an object as some systems may use the
	 * information.
	 */
	public void setHanded(final String aString)
	{
		setStringFor(StringKey.HANDED, aString);
	}

	/**
	 * Returns the character's handedness string.
	 * 
	 * @return A String for handedness.
	 */
	public String getHanded()
	{
		return getSafeStringFor(StringKey.HANDED);
	}

	/**
	 * Sets the character's height in inches.
	 * 
	 * @param i
	 *            A height in inches.
	 * 
	 * TODO - This should be a double value stored in CM
	 */
	public void setHeight(final int i)
	{
		heightInInches = i;
		setDirty(true);
	}

	/**
	 * Gets the character's height in inches.
	 * 
	 * @return The character's height in inches.
	 */
	public int getHeight()
	{
		return heightInInches;
	}

	/**
	 * Marks the character as being in the process of being loaded.
	 * 
	 * <p>
	 * This information is used to prevent the system from trying to calculate
	 * values on partial information or values that should be set from the saved
	 * character.
	 * 
	 * <p>
	 * TODO - This is pretty dangerous.
	 * 
	 * @param newIsImporting
	 *            <tt>true</tt> to mark the character as being imported.
	 */
	public void setImporting(final boolean newIsImporting)
	{
		this.importing = newIsImporting;
	}

	/**
	 * Sets the character's interests.
	 * 
	 * @param aString
	 *            A string of interests for the character.
	 */
	public void setInterests(final String aString)
	{
		setStringFor(StringKey.INTERESTS, aString);
	}

	/**
	 * Gets a string of interests for the character.
	 * 
	 * @return A String of interests or an empty string.
	 */
	public String getInterests()
	{
		return getSafeStringFor(StringKey.INTERESTS);
	}

	/**
	 * Gets the character's list of languages.
	 * 
	 * @return An unmodifiable language set.
	 */
	public SortedSet<Language> getLanguagesList()
	{
		return Collections.unmodifiableSortedSet(languages);
	}

	/**
	 * Removes all the character's languages.
	 */
	public void clearLanguages()
	{
		languages.clear();
	}

	/**
	 * Adds a <tt>Collection</tt> of languages to the character.
	 * 
	 * @param aList
	 *            A <tt>Collection</tt> of <tt>Language</tt> objects.
	 */
	public void addLanguages(final Collection<Language> aList)
	{
		languages.addAll(aList);
	}

	/**
	 * TODO - This doesn't need to be a PlayerCharacter method.
	 * 
	 * @return
	 */
	public String getLanguagesListNames()
	{
		final StringBuffer b = new StringBuffer();

		for (final Language l : languages)
		{
			if (b.length() > 0)
			{
				b.append(", ");
			}

			b.append(l.toString());
		}

		return b.toString();
	}

	/**
	 * Sets the character's location.
	 * 
	 * @param aString
	 *            A location.
	 */
	public void setLocation(final String aString)
	{
		setStringFor(StringKey.LOCATION, aString);
	}

	/**
	 * Gets the character's location.
	 * 
	 * @return The character's location.
	 */
	public String getLocation()
	{
		return getSafeStringFor(StringKey.LOCATION);
	}

	/**
	 * This method returns the effective level of this character for purposes of
	 * applying companion mods to a companion of the specified type.
	 * <p>
	 * <b>Note</b>: This whole structure is kind of messed up since nothing
	 * enforces that a companion mod of a given type always looks at the same
	 * variable (either Class or Variable).
	 * 
	 * @param compType
	 *            A type of companion to get level for
	 * @return The effective level for this companion type
	 */
	public int getEffectiveCompanionLevel(final String compType)
	{
		final Collection<CompanionMod> mods = Globals
			.getCompanionMods(compType);
		for (CompanionMod cMod : mods)
		{
			for (Iterator<String> iType = cMod.getVarMap().keySet().iterator(); iType
				.hasNext();)
			{
				final String varName = iType.next();
				final int lvl = this.getVariableValue(varName,
					Constants.EMPTY_STRING).intValue();
				if (lvl > 0)
				{
					return lvl;
				}
			}
			for (final String classKey : cMod.getClassMap().keySet())
			{
				final int lvl = this.getClassKeyed(classKey).getLevel();
				if (lvl > 0)
				{
					return lvl;
				}
			}
		}
		return 0;
	}

	/**
	 * Removes all <tt>CompanionMod</tt>s from the character.
	 */
	public void clearCompanionMods()
	{
		companionModList.clear();
	}

	/**
	 * Set the master for this object also set the level dependent stats based
	 * on the masters level and info contained in the companionModList Array
	 * such as HitDie, SR, BONUS, SA, etc
	 * 
	 * @param aM
	 *            The master to be set.
	 */
	public void setMaster(final Follower aM)
	{
		followerMaster = aM;

		final PlayerCharacter mPC = getMasterPC();

		if (mPC == null)
		{
			return;
		}

		// make sure masters Name and fileName are correct
		if (!aM.getFileName().equals(mPC.getFileName()))
		{
			aM.setFileName(mPC.getFileName());
			setDirty(true);
		}

		if (!aM.getName().equals(mPC.getName()))
		{
			aM.setName(mPC.getName());
			setDirty(true);
		}

		// Get total wizard + sorcerer levels as they stack like a mother
		int mTotalLevel = 0;
		int addHD = 0;

		for (PCClass mClass : mPC.getClassList())
		{
			boolean found = false;

			for (CompanionMod cMod : Globals.getCompanionMods(aM.getType()))
			{
				if ((cMod.getLevel(mClass.getKeyName()) > 0) && !found)
				{
					mTotalLevel += mClass.getLevel();
					found = true;
				}
			}
		}

		// Clear the companionModList so we can add everything to it
		clearCompanionMods();

		for (CompanionMod cMod : Globals.getCompanionMods(aM.getType()))
		{
			// Check all the masters classes
			for (PCClass mClass : mPC.getClassList())
			{
				final int mLev = mClass.getLevel() + aM.getAdjustment();
				final int compLev = cMod.getLevel(mClass.getKeyName());

				if (compLev < 0)
				{
					continue;
				}

				// This CompanionMod must be for this Class
				// and for the correct level or lower
				if ((compLev <= mLev) || (compLev <= mTotalLevel))
				{
					if (PrereqHandler.passesAll(cMod.getPreReqList(), this,
						cMod))
					// if (!companionModList.contains(aComp))
					{
						addCompanionMod(cMod);
						addHD += cMod.getHitDie();
					}
				}
			}
			for (String varName : cMod.getVarMap().keySet())
			{
				final int mLev = mPC.getVariableValue(varName,
					Constants.EMPTY_STRING).intValue()
					+ aM.getAdjustment();

				if (mLev >= cMod.getLevel(varName))
				{
					if (PrereqHandler.passesAll(cMod.getPreReqList(), this,
						cMod))
					// if (!companionModList.contains(aComp))
					{
						addCompanionMod(cMod);
						addHD += cMod.getHitDie();
					}
				}
			}
		}

		//
		// Add additional HD if required
		// newClass = Globals.getClassNamed(race.getType());
		PCClass aClass = Globals.getClassKeyed(race
			.getMonsterClass(this, false));

		final int usedHD = followerMaster.getUsedHD();
		addHD -= usedHD;

		// if ((newClass != null) && (addHD != 0))
		if ((aClass != null) && (addHD != 0))
		{
			// set the new HD (but only do it once!)
			// incrementClassLevel(addHD, newClass, true);
			incrementClassLevel(addHD, aClass, true);
			followerMaster.setUsedHD(addHD + usedHD);
			setDirty(true);
		}

		// If it's a familiar, we need to change it's Skills
		if (getUseMasterSkill())
		{
			final List<Skill> mList = mPC.getSkillList();
			final List<String> sKeyList = new ArrayList<String>();

			// now we have to merge the two lists together and
			// take the higher rank of each skill for the Familiar
			for (Skill fSkill : getAllSkillList(true))
			{
				for (Skill mSkill : mList)
				{
					// first check to see if familiar
					// already has ranks in the skill
					if (mSkill.getKeyName().equals(fSkill.getKeyName()))
					{
						// need higher rank of the two
						if (mSkill.getRank().intValue() > fSkill.getRank()
							.intValue())
						{
							// first zero current
							fSkill.setZeroRanks(aClass, this);
							// We don't pass in a class here so that the real
							// skills can be distinguished from the ones from
							// the master.
							fSkill.modRanks(mSkill.getRank().doubleValue(),
								null, true, this);
						}
					}

					// build a list of all skills a master
					// Possesses, but the familiar does not
					if (!hasSkill(mSkill.getKeyName())
						&& !sKeyList.contains(mSkill.getKeyName()))
					{
						sKeyList.add(mSkill.getKeyName());
					}
				}
			}

			// now add all the skills only the master has
			for (String skillKey : sKeyList)
			{
				// familiar doesn't have skill,
				// but master does, so add it
				final Skill newSkill = Globals.getSkillKeyed(skillKey)
					.clone();
				final double sr = mPC.getSkillKeyed(skillKey).getRank()
					.doubleValue();

				if ((newSkill.getChoiceString() != null)
					&& (newSkill.getChoiceString().length() > 0))
				{
					continue;
				}

				// We don't pass in a class here so that the real skills can be
				// distinguished from the ones form the master.
				newSkill.modRanks(sr, null, true, this);
				getSkillList().add(newSkill);
			}
		}
		for (CompanionMod cMod : companionModList)
		{
			cMod.addAddsForLevel(-9, this, null);

			for (String key : cMod.getTemplateList())
			{
				addTemplateKeyed(key);
			}

			final List<String> kits = cMod.getSafeListFor(ListKey.KITS);
			for (int i1 = 0; i1 < kits.size(); i1++)
			{
				KitUtilities.makeKitSelections(0, kits.get(i1), i1, this);
			}
		}
		setDirty(true);
	}

	/**
	 * Returns the maximum number of followers of the specified type this
	 * character can have. This method does not adjust for any followers already
	 * selected by the character.
	 * 
	 * @param aType
	 *            The follower type to check e.g. Familiar
	 * @return The max number of followers -1 for any number
	 */
	public int getMaxFollowers(final String aType)
	{
		int ret = -1;

		List<? extends PObject> pobjList = getPObjectList();
		for (PObject pobj : pobjList)
		{
			if (pobj == null)
			{
				continue;
			}

			final List<String> formulas = pobj.getNumFollowers(aType);
			if (formulas == null)
			{
				continue;
			}
			for (String formula : formulas)
			{
				final int val = this.getVariableValue(formula,
					Constants.EMPTY_STRING, this).intValue();
				ret = Math.max(ret, val);
			}
		}

		if (ret != -1)
		{
			// ret += (int)getBonusValue("FOLLOWERS", aType.toUpperCase());
			ret += this.getTotalBonusTo("FOLLOWERS", aType.toUpperCase());
		}
		else
		{
			// Old way of handling this
			// If the character qualifies for any companion mod of this type
			// they can take unlimited number of them.
			for (CompanionMod cMod : Globals.getCompanionMods(aType))
			{
				for (String varName : cMod.getVarMap().keySet())
				{
					if (this.getVariableValue(varName, Constants.EMPTY_STRING)
						.intValue() > 0)
					{
						return -1;
					}
				}
				for (String key : cMod.getClassMap().keySet())
				{
					for (PCClass pcClass : getClassList())
					{
						if (pcClass.getKeyName().equals(key))
						{
							return -1;
						}
					}
				}
			}

			return 0;
		}
		return ret;
	}

	/**
	 * Gets the list of potential followers of a given type.
	 * 
	 * @param aType
	 *            Type of follower to retrieve list for e.g. Familiar
	 * @return A List of FollowerOption objects representing the possible list
	 *         of follower choices.
	 */
	public List<FollowerOption> getAvailableFollowers(final String aType)
	{
		final List<FollowerOption> ret = new ArrayList<FollowerOption>();

		final List<? extends PObject> pobjList = getPObjectList();
		for (PObject pobj : pobjList)
		{
			if (pobj == null)
			{
				continue;
			}

			final List<FollowerOption> followers = pobj
				.getPotentialFollowers(aType);
			if (followers != null)
			{
				ret.addAll(followers);
			}
		}

		return ret;
	}

	/**
	 * Get the Follower object that is the "master" for this object
	 * 
	 * @return follower master
	 */
	public Follower getMaster()
	{
		return followerMaster;
	}

	/**
	 * Get the PlayerCharacter that is the "master" for this object
	 * 
	 * @return master PC
	 */
	public PlayerCharacter getMasterPC()
	{
		if (followerMaster == null)
		{
			return null;
		}

		for (PlayerCharacter nPC : Globals.getPCList())
		{
			if (followerMaster.getFileName().equals(nPC.getFileName()))
			{
				return nPC;
			}
		}

		// could not find a filename match, let's try the Name
		for (PlayerCharacter nPC : Globals.getPCList())
		{
			if (followerMaster.getName().equals(nPC.getName()))
			{
				return nPC;
			}
		}

		// no Name and no FileName match, so must not be loaded
		return null;
	}

	/**
	 * Returns the state of the default monster flag when the character was
	 * created.
	 * 
	 * @return <tt>true</tt> if the character used the default monster flag.
	 */
	public boolean isMonsterDefault()
	{
		return useMonsterDefault;
	}

	/**
	 * Sets the character's name.
	 * 
	 * @param aString
	 *            A name to set.
	 */
	public void setName(final String aString)
	{
		setStringFor(StringKey.NAME, aString);
	}

	/**
	 * Gets the character's name.
	 * 
	 * @return The name
	 */
	public String getName()
	{
		return getSafeStringFor(StringKey.NAME);
	}

	/**
	 * Takes all the Temporary Bonuses and Merges them into just the unique
	 * named bonuses.
	 * 
	 * @return List of Strings
	 */
	public List<String> getNamedTempBonusList()
	{
		final List<String> aList = new ArrayList<String>();

		for (BonusObj aBonus : getTempBonusList())
		{
			if (aBonus == null)
			{
				continue;
			}

			if (!aBonus.isApplied())
			{
				continue;
			}

			final PObject aCreator = (PObject) aBonus.getCreatorObject();

			if (aCreator == null)
			{
				continue;
			}

			final String aName = aCreator.getKeyName();

			if (!aList.contains(aName))
			{
				aList.add(aName);
			}
		}

		return aList;
	}

	/**
	 * @return nonProficiencyPenalty. Searches templates first.
	 */
	public int getNonProficiencyPenalty()
	{
		int npp = Globals.getGameModeNonProfPenalty();

		for (PCTemplate t : templateList)
		{
			final int temp = t.getNonProficiencyPenalty();
			if (temp <= 0)
			{
				npp = temp;
			}
		}

		return npp;
	}

	/**
	 * Gets a list of notes associated with the character.
	 * 
	 * @return A list of <tt>NoteItem</tt> objects.
	 */
	public ArrayList<NoteItem> getNotesList()
	{
		return notesList;
	}

	/**
	 * Sets a string of phobias for the character.
	 * 
	 * @param aString
	 *            A string to set.
	 */
	public void setPhobias(final String aString)
	{
		setStringFor(StringKey.PHOBIAS, aString);
	}

	/**
	 * Gets the phobia string for the character.
	 * 
	 * @return A phobia string.
	 */
	public String getPhobias()
	{
		return getSafeStringFor(StringKey.PHOBIAS);
	}

	/**
	 * Sets the name of the player for this character.
	 * 
	 * @param aString
	 *            A name to set.
	 */
	public void setPlayersName(final String aString)
	{
		setStringFor(StringKey.PLAYERS_NAME, aString);
	}

	/**
	 * Gets the name of the player for this character.
	 * 
	 * @return The player's name.
	 */
	public String getPlayersName()
	{
		return getSafeStringFor(StringKey.PLAYERS_NAME);
	}

	public void setPoolAmount(final int anInt)
	{
		poolAmount = anInt;
	}

	public int getPoolAmount()
	{
		return poolAmount;
	}

	/**
	 * Selector Sets the path to the portrait of the character.
	 * 
	 * @param newPortraitPath
	 *            the path to the portrait file
	 */
	public void setPortraitPath(final String newPortraitPath)
	{
		setStringFor(StringKey.PORTRAIT_PATH, newPortraitPath);
	}

	/**
	 * Selector Gets the path to the portrait of the character.
	 * 
	 * @return the path to the portrait file
	 */
	public String getPortraitPath()
	{
		return getSafeStringFor(StringKey.PORTRAIT_PATH);
	}

	/**
	 * Selector
	 * 
	 * @return primary weapons
	 */
	public List<Equipment> getPrimaryWeapons()
	{
		return primaryWeapons;
	}

	/**
	 * Get race
	 * 
	 * @return race
	 */
	public Race getRace()
	{
		return race;
	}

	/**
	 * Set region
	 * 
	 * @param arg
	 */
	public void setRegion(final String arg)
	{
		setStringFor(StringKey.REGION, arg);
	}

	/**
	 * Set sub region
	 * 
	 * @param aString
	 */
	public void setSubRegion(final String aString)
	{
		setStringFor(StringKey.SUB_REGION, aString);
	}

	/**
	 * Selector <p/> Build on-the-fly so removing templates won't mess up region
	 * 
	 * @return character region
	 */
	public String getRegion()
	{
		return getRegion(true);
	}

	/**
	 * Get region
	 * 
	 * @param useTemplates
	 * @return region
	 */
	public String getRegion(final boolean useTemplates)
	{
		String pcRegion = getStringFor(StringKey.REGION);
		if ((pcRegion != null) || !useTemplates)
		{
			return pcRegion; // character's region trumps any from templates
		}

		String r = Constants.s_NONE;

		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = templateList.get(i);
			final String tempRegion = template.getRegion();

			if (!tempRegion.equals(Constants.s_NONE))
			{
				r = tempRegion;
			}
		}

		return r;
	}

	/**
	 * Set residence
	 * 
	 * @param aString
	 */
	public void setResidence(final String aString)
	{
		setStringFor(StringKey.RESIDENCE, aString);
	}

	/**
	 * Get residence
	 * 
	 * @return residence
	 */
	public String getResidence()
	{
		return getSafeStringFor(StringKey.RESIDENCE);
	}

	/**
	 * Selector
	 * 
	 * @return secondary weapons
	 */
	public List<Equipment> getSecondaryWeapons()
	{
		return secondaryWeapons;
	}

	/**
	 * Get HTML sheet for selected character
	 * 
	 * @param aString
	 */
	public void setSelectedCharacterHTMLOutputSheet(final String aString)
	{
		outputSheetHTML = aString;
	}

	/**
	 * Location of HTML Output Sheet
	 * 
	 * @return HTML output sheet
	 */
	public String getSelectedCharacterHTMLOutputSheet()
	{
		return outputSheetHTML;
	}

	/**
	 * Set selected PDF character sheet for character
	 * 
	 * @param aString
	 */
	public void setSelectedCharacterPDFOutputSheet(final String aString)
	{
		outputSheetPDF = aString;
	}

	/**
	 * Location of PDF Output Sheet
	 * 
	 * @return pdf output sheet
	 */
	public String getSelectedCharacterPDFOutputSheet()
	{
		return outputSheetPDF;
	}

	/**
	 * Get list of shield proficiencies
	 * 
	 * @return shield prof list
	 */
	public List<String> getShieldProfList()
	{
		shieldProfList.clear();

		final List<String> autoShieldProfList = getAutoShieldProfList();
		addShieldProfs(autoShieldProfList);

		final List<String> selectedProfList = getSelectedShieldProfList();
		addShieldProfs(selectedProfList);

		return shieldProfList;
	}

	/**
	 * Get size
	 * 
	 * @return size
	 */
	public String getSize()
	{
		final SizeAdjustment sa = getSizeAdjustment();

		if (sa != null)
		{
			return sa.getAbbreviation();
		}

		return " ";
	}

	/**
	 * Get skill list
	 * 
	 * @return list of skills
	 */
	public ArrayList<Skill> getSkillList()
	{
		return getAllSkillList(false);
	}

	/**
	 * Retrieves a list of the character's skills in output order. This is in
	 * ascending order of the skill's outputIndex field. If skills have the same
	 * outputIndex they will be ordered by name. Note hidden skills (outputIndex =
	 * -1) are not included in this list.
	 * 
	 * @return An ArrayList of the skill objects in output order.
	 */
	public ArrayList<Skill> getSkillListInOutputOrder()
	{
		return getSkillListInOutputOrder(new ArrayList<Skill>(getSkillList()));
	}

	/**
	 * Retrieves a list of the character's skills in output order. This is in
	 * ascending order of the skill's outputIndex field. If skills have the same
	 * outputIndex they will be ordered by name. Note hidden skills (outputIndex =
	 * -1) are not included in this list.
	 * 
	 * Deals with sorted list
	 * 
	 * @param sortedList
	 * 
	 * @return An ArrayList of the skill objects in output order.
	 */
	public ArrayList<Skill> getSkillListInOutputOrder(
		final ArrayList<Skill> sortedList)
	{
		Collections.sort(sortedList, new Comparator<Skill>()
		{
			/**
			 * Comparator will be specific to Skill objects
			 */
			public int compare(final Skill skill1, final Skill skill2)
			{
				int obj1Index = skill1.getOutputIndex();
				int obj2Index = skill2.getOutputIndex();

				// Force unset items (index of 0) to appear at the end
				if (obj1Index == 0)
				{
					obj1Index = 999;
				}

				if (obj2Index == 0)
				{
					obj2Index = 999;
				}

				if (obj1Index > obj2Index)
				{
					return 1;
				}
				else if (obj1Index < obj2Index)
				{
					return -1;
				}
				else
				{
					return skill1.getDisplayName().compareToIgnoreCase(
						skill2.getDisplayName());
				}
			}
		});

		// Remove the hidden skills from the list
		for (Iterator<Skill> i = sortedList.iterator(); i.hasNext();)
		{
			final Skill bSkill = i.next();

			if (bSkill.getOutputIndex() == -1)
			{
				i.remove();
			}
		}

		return sortedList;
	}

	/**
	 * Set skill points
	 * 
	 * @param anInt
	 */
	public void setSkillPoints(final int anInt)
	{
		setDirty(true);
	}

	/**
	 * Get skill points
	 * 
	 * @return skill points
	 */
	public int getSkillPoints()
	{
		int returnValue = 0;

		// First compute gained points, and then remove the already spent ones.
		// We can't use Remaining points because the level may be removed, and
		// then we have
		// to display this as -x on the "Total Skill Points" field
		for (PCLevelInfo li : getLevelInfo())
		{
			returnValue += li.getSkillPointsGained();
		}

		for (Skill aSkill : getSkillList())
		{
			for (String bSkill : aSkill.getRankList())
			{
				final int iOffs = bSkill.indexOf(':');
				final double curRank = Double.parseDouble(bSkill
					.substring(iOffs + 1));
				final PCClass pcClass = getClassKeyed(bSkill
					.substring(0, iOffs));
				if (pcClass != null)
				{
					// Only add the cost for skills associated with a class.
					// Skill ranks from feats etc are free.
					final double cost = aSkill.costForPCClass(pcClass, this);
					returnValue -= (int) (cost * curRank);
				}
			}
		}
		if (Globals.getGameModeHasPointPool())
		{
			returnValue += (int) getRawFeats(false); // DO NOT CALL
														// getFeats() here! It
														// will set up a
														// recursive loop and
														// result in a stack
														// overflow!
		}
		return returnValue;
	}

	/**
	 * Set skin colour
	 * 
	 * @param aString
	 */
	public void setSkinColor(final String aString)
	{
		setStringFor(StringKey.SKIN_COLOR, aString);
	}

	/**
	 * Get skin colour
	 * 
	 * @return skin colour
	 */
	public String getSkinColor()
	{
		return getSafeStringFor(StringKey.SKIN_COLOR);
	}

	/**
	 * Get list of special abilities
	 * 
	 * @return List of special abilities
	 */
	public List<SpecialAbility> getSpecialAbilityList()
	{
		// aList will contain a list of SpecialAbility objects
		List<SpecialAbility> aList = new ArrayList<SpecialAbility>(
			specialAbilityList);

		final int atl = getTotalLevels();
		final int thd = totalHitDice();

		// Try all possible POBjects
		for (PObject aPObj : getPObjectList())
		{
			if (aPObj == null)
			{
				continue;
			}

			if (aPObj instanceof PCTemplate)
			{
				final PCTemplate bTemplate = Globals.getTemplateKeyed(aPObj
					.getKeyName());

				if (bTemplate == null)
				{
					continue;
				}

				aList = bTemplate.addSpecialAbilitiesToList(aList, atl, thd);
			}
			else
			{
				aList = aPObj.addSpecialAbilitiesToList(aList, this);
			}
		}

		Collections.sort(aList);

		return aList;
	}

	/**
	 * Get list of special abilities as Strings
	 * 
	 * @return List of special abilities as Strings
	 */
	public List<String> getSpecialAbilityListStrings()
	{
		final ArrayList<String> bList = new ArrayList<String>();

		for (SpecialAbility sa : getSpecialAbilityList())
		{
			if (!PrereqHandler.passesAll(sa.getPreReqList(), this, sa))
			{
				continue;
			}
			final String saText = sa.getParsedText(this, this);
			if (saText != null && !saText.equals(""))
			{
				bList.add(saText);
			}
		}

		return bList;
	}

	/**
	 * same as getSpecialAbilityList except if if you have the same ability
	 * twice, it only lists it once with (2) at the end.
	 * 
	 * @return List
	 */
	public ArrayList<String> getSpecialAbilityTimesList()
	{
		final List<String> abilityList = getSpecialAbilityListStrings();
		final List<String> sortList = new ArrayList<String>();
		final int[] numTimes = new int[abilityList.size()];

		for (int i = 0; i < abilityList.size(); i++)
		{
			final String ability = abilityList.get(i);
			if (!sortList.contains(ability))
			{
				sortList.add(ability);
				numTimes[i] = 1;
			}
			else
			{
				for (int j = 0; j < sortList.size(); j++)
				{
					final String testAbility = sortList.get(j);
					if (testAbility.equals(ability))
					{
						numTimes[j]++;
					}
				}
			}
		}

		final ArrayList<String> retList = new ArrayList<String>();
		for (int i = 0; i < sortList.size(); i++)
		{
			String ability = sortList.get(i);
			if (numTimes[i] > 1)
			{
				ability = ability + " (" + numTimes[i] + ")";
			}
			retList.add(ability);
		}

		return retList;
	}

	/**
	 * Set speech tendency
	 * 
	 * @param aString
	 */
	public void setSpeechTendency(final String aString)
	{
		setStringFor(StringKey.SPEECH_TENDENCY, aString);
	}

	/**
	 * Get speech tendency
	 * 
	 * @return speech tendency
	 */
	public String getSpeechTendency()
	{
		return getSafeStringFor(StringKey.SPEECH_TENDENCY);
	}

	/**
	 * Set the name of the spellbook to auto add new known spells to.
	 * 
	 * @param aString
	 *            The new spellbook name.
	 */
	public void setSpellBookNameToAutoAddKnown(final String aString)
	{
		setStringFor(StringKey.SPELLBOOK_AUTO_ADD_KNOWN, aString);
	}

	/**
	 * Get the name of the spellbook to auto add new known spells to.
	 * 
	 * @return spellbook name
	 */
	public String getSpellBookNameToAutoAddKnown()
	{
		return getSafeStringFor(StringKey.SPELLBOOK_AUTO_ADD_KNOWN);
	}

	/**
	 * Retrieve a spell book object given the name of the spell book.
	 * 
	 * @param name
	 *            The name of the spell book to be retrieved.
	 * @return The spellbook (or null if not present).
	 */
	public SpellBook getSpellBookByName(final String name)
	{
		return spellBookMap.get(name);
	}

	/**
	 * Get spell books
	 * 
	 * @return spellBooks
	 */
	public List<String> getSpellBooks()
	{
		return spellBooks;
	}

	/**
	 * Get spell class given an index
	 * 
	 * @param ix
	 * @return spell class
	 */
	public PObject getSpellClassAtIndex(final int ix)
	{
		final List<? extends PObject> aList = getSpellClassList();

		if ((ix >= 0) && (ix < aList.size()))
		{
			return aList.get(ix);
		}

		return null;
	}

	/**
	 * a temporary placeholder used for computing the DC of a spell Set from
	 * within Spell.java before the getVariableValue() call
	 * 
	 * @param i
	 */
	public void setSpellLevelTemp(final int i)
	{
		// Explicitly should *not* set the dirty flag to true.
		spellLevelTemp = i;
	}

	/**
	 * Get spell level temp
	 * 
	 * @return temp spell level
	 */
	public int getSpellLevelTemp()
	{
		return spellLevelTemp;
	}

	/**
	 * Get the stat list
	 * 
	 * @return stat list
	 */
	public StatList getStatList()
	{
		return statList;
	}

	/**
	 * Selector <p/> Build on-the-fly so removing templates won't mess up
	 * subrace
	 * 
	 * @return character subrace
	 */
	public String getSubRace()
	{
		String subRace = Constants.s_NONE;

		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = templateList.get(i);
			final String tempSubRace = template.getSubRace();

			if (!tempSubRace.equals(Constants.s_NONE))
			{
				subRace = tempSubRace;
			}
		}

		return subRace;
	}

	/**
	 * Selector <p/> Build on-the-fly so removing templates won't mess up sub
	 * region
	 * 
	 * @return character sub region
	 */
	public String getSubRegion()
	{
		return getSubRegion(true);
	}

	/**
	 * Set the name on the tab
	 * 
	 * @param aString
	 */
	public void setTabName(final String aString)
	{
		tabName = aString;
		setDirty(true);
		setChanged();
		notifyObservers("TabName");
	}

	/**
	 * Get tab name
	 * 
	 * @return name on tab
	 */
	public String getTabName()
	{
		return tabName;
	}

	/**
	 * Temporary Bonuses
	 */

	/**
	 * List if Items which have Temp Bonuses applied to them
	 * 
	 * @return List
	 */
	private List<Equipment> getTempBonusItemList()
	{
		return tempBonusItemList;
	}

	/**
	 * Set temp bonus list
	 * 
	 * @param aList
	 */
	public void setTempBonusList(final List<BonusObj> aList)
	{
		tempBonusList = aList;
		setDirty(true);
	}

	/**
	 * Temp Bonus list
	 * 
	 * @return List
	 */
	public List<BonusObj> getTempBonusList()
	{
		return tempBonusList;
	}

	/**
	 * get filtered temp bonus list
	 * 
	 * @return filtered temp bonus list
	 */
	public List<BonusObj> getFilteredTempBonusList()
	{
		final List<BonusObj> ret = new ArrayList<BonusObj>();
		for (BonusObj bonus : getTempBonusList())
		{
			if (!tempBonusFilters.contains(bonus.getName()))
			{
				ret.add(bonus);
			}
		}
		return ret;
	}

	/**
	 * get temp bonus filters
	 * 
	 * @return temp bonus filters
	 */
	public Set<String> getTempBonusFilters()
	{
		return tempBonusFilters;
	}

	/**
	 * Clear temp bonus filters
	 * 
	 */
	public void clearTempBonusFilters()
	{
		tempBonusFilters.clear();
	}

	/**
	 * set temp bonus filter
	 * 
	 * @param aBonusStr
	 */
	public void setTempBonusFilter(final String aBonusStr)
	{
		tempBonusFilters.add(aBonusStr);
		calcActiveBonuses();
	}

	/**
	 * unset temp bonus filter
	 * 
	 * @param aBonusStr
	 */
	public void unsetTempBonusFilter(final String aBonusStr)
	{
		tempBonusFilters.remove(aBonusStr);
		calcActiveBonuses();
	}

	/**
	 * Given a Source and a Target object, get a list of BonusObj's
	 * 
	 * @param aCreator
	 * @param aTarget
	 * 
	 * @return List of BonusObj
	 */
	public List<BonusObj> getTempBonusList(final String aCreator,
		final String aTarget)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for (BonusObj bonus : getTempBonusList())
		{
			final Object aTO = bonus.getTargetObject();
			final Object aCO = bonus.getCreatorObject();

			String targetName = Constants.EMPTY_STRING;
			String creatorName = Constants.EMPTY_STRING;

			if (aCO instanceof PObject)
			{
				creatorName = ((PObject) aCO).getKeyName();
			}

			if (aTO instanceof PlayerCharacter)
			{
				targetName = getName();
			}
			else if (aTO instanceof PObject)
			{
				targetName = ((PObject) aTO).getKeyName();
			}

			if (creatorName.equals(aCreator) && targetName.equals(aTarget))
			{
				aList.add(bonus);
			}
		}

		return aList;
	}

	/**
	 * Get the list of Templates applied to this PC
	 * 
	 * @return List of templates
	 */
	public ArrayList<PCTemplate> getTemplateList()
	{
		return templateList;
	}

	/**
	 * Retrieve a list of the templates applied to this PC that should be
	 * visible on output.
	 * 
	 * @return The list of templates visible on output sheets.
	 */
	public List<PCTemplate> getOutputVisibleTemplateList()
	{
		List<PCTemplate> tl = new ArrayList<PCTemplate>();

		for (PCTemplate template : getTemplateList())
		{
			if ((template.getVisibility() == Visibility.DEFAULT)
				|| (template.getVisibility() == Visibility.OUTPUT_ONLY))
			{
				tl.add(template);
			}
		}
		return tl;
	}

	/**
	 * Get the template keyed aKey from this PC
	 * 
	 * @param aKey
	 * 
	 * @return PC template or null if not found
	 */
	public PCTemplate getTemplateKeyed(final String aKey)
	{
		for (PCTemplate template : templateList)
		{
			if (template.getKeyName().equalsIgnoreCase(aKey))
			{
				return template;
			}
		}

		return null;
	}

	/**
	 * Set trait 1
	 * 
	 * @param aString
	 */
	public void setTrait1(final String aString)
	{
		setStringFor(StringKey.TRAIT1, aString);
	}

	/**
	 * Get trait 1
	 * 
	 * @return trait 1
	 */
	public String getTrait1()
	{
		return getSafeStringFor(StringKey.TRAIT1);
	}

	/**
	 * Set trait 2
	 * 
	 * @param aString
	 */
	public void setTrait2(final String aString)
	{
		setStringFor(StringKey.TRAIT2, aString);
	}

	/**
	 * Get trait 2
	 * 
	 * @return trait 2
	 */
	public String getTrait2()
	{
		return getSafeStringFor(StringKey.TRAIT2);
	}

	public Float getVariable(final String variableString, final boolean isMax,
		final boolean includeBonus, final String matchSrc,
		final String matchSubSrc, int decrement)
	{
		return getVariable(variableString, isMax, includeBonus, matchSrc,
			matchSubSrc, true, decrement);
	}

	private double getMinMaxFirstValue(final boolean isNewValue,
		final boolean isMax, final double oldValue, final double newValue)
	{
		if (!isNewValue)
			return newValue;
		if (isMax)
			return Math.max(oldValue, newValue);
		return Math.min(oldValue, newValue);
	}

	/**
	 * Should probably be refactored to return a String instead. TODO This
	 * should call getPObjectList() to get a list of PObjects to test against. I
	 * don't want to change the behaviour for now however.
	 * 
	 * @param variableString
	 * @param isMax
	 * @param includeBonus
	 *            Should bonus tokens be added to this variables value
	 * @param matchSrc
	 * @param matchSubSrc
	 * @param recurse
	 * @param decrement
	 * @return Float
	 */
	public Float getVariable(final String variableString, final boolean isMax,
		boolean includeBonus, final String matchSrc, final String matchSubSrc,
		final boolean recurse, int decrement)
	{
		double value = 0.0;
		boolean found = false;

		if (lastVariable != null)
		{
			if (lastVariable.equals(variableString))
			{
				StringBuffer sb = new StringBuffer(256);
				sb
					.append("This is a deliberate warning message, not an error - ");
				sb
					.append("Avoiding infinite loop in getVariable: repeated lookup ");
				sb.append("of \"").append(lastVariable).append("\" at ")
					.append(value);
				Logging.debugPrint(sb.toString());
				lastVariable = null;
				return new Float(value);
			}
		}

		for (String vString : variableList)
		{
			final StringTokenizer aTok = new StringTokenizer(vString,
				Constants.PIPE);
			final String src = aTok.nextToken();

			if ((matchSrc.length() > 0) && !src.equals(matchSrc))
			{
				continue;
			}

			final String subSrc = aTok.nextToken();

			if ((matchSubSrc.length() > 0) && !subSrc.equals(matchSubSrc))
			{
				continue;
			}

			final String nString = aTok.nextToken();

			if (nString.equalsIgnoreCase(variableString))
			{
				final String sString = aTok.nextToken();
				final double newValue = getVariableValue(sString, src)
					.doubleValue();
				value = getMinMaxFirstValue(found, isMax, value, newValue);

				found = true;
			}
		}

		// Now check the feats to see if they modify the variable
		for (Ability obj : aggregateFeatList())
		{
			final String varInList = checkForVariableInList(obj,
				variableString, isMax, Constants.EMPTY_STRING,
				Constants.EMPTY_STRING, found, value, decrement);

			if (varInList.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(varInList));
				found = true;
			}
		}

		for (Skill obj : getSkillList())
		{
			final String varInList = checkForVariableInList(obj,
				variableString, isMax, Constants.EMPTY_STRING,
				Constants.EMPTY_STRING, found, value, 0);

			if (varInList.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(varInList));
				found = true;
			}
		}

		for (Equipment obj : equipmentList)
		{
			final String eS = checkForVariableInList(obj, variableString,
				isMax, Constants.EMPTY_STRING, Constants.EMPTY_STRING, found,
				value, 0);

			if (eS.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(eS));
				found = true;
			}

			for (EquipmentModifier em : (obj.getEqModifierList(true)))
			{
				final String varInList = checkForVariableInList(em,
					variableString, isMax, Constants.EMPTY_STRING,
					Constants.EMPTY_STRING, found, value, decrement);

				if (varInList.length() > 0)
				{
					value = getMinMaxFirstValue(found, isMax, value, Float
						.parseFloat(varInList));
					found = true;
				}
			}

			for (EquipmentModifier em : (obj.getEqModifierList(false)))
			{
				final String varInList = checkForVariableInList(em,
					variableString, isMax, Constants.EMPTY_STRING,
					Constants.EMPTY_STRING, found, value, decrement);

				if (varInList.length() > 0)
				{
					value = getMinMaxFirstValue(found, isMax, value, Float
						.parseFloat(varInList));
					found = true;
				}
			}
		}

		for (PCTemplate obj : templateList)
		{
			final String aString = checkForVariableInList(obj, variableString,
				isMax, Constants.EMPTY_STRING, Constants.EMPTY_STRING, found,
				value, decrement);

			if (aString.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(aString));
				found = true;
			}
		}

		for (CompanionMod obj : companionModList)
		{
			final String aString = checkForVariableInList(obj, variableString,
				isMax, Constants.EMPTY_STRING, Constants.EMPTY_STRING, found,
				value, decrement);

			if (aString.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(aString));
				found = true;
			}
		}

		if (race != null)
		{
			final String aString = checkForVariableInList(race, variableString,
				isMax, Constants.EMPTY_STRING, Constants.EMPTY_STRING, found,
				value, decrement);

			if (aString.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(aString));
				found = true;
			}
		}

		if (deity != null)
		{
			final String aString = checkForVariableInList(deity,
				variableString, isMax, Constants.EMPTY_STRING,
				Constants.EMPTY_STRING, found, value, decrement);

			if (aString.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(aString));
				found = true;
			}
		}

		for (CharacterDomain obj : characterDomainList)
		{
			if (obj.getDomain() == null)
			{
				continue;
			}

			final String aString = checkForVariableInList(obj.getDomain(),
				variableString, isMax, Constants.EMPTY_STRING,
				Constants.EMPTY_STRING, found, value, decrement);

			if (aString.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(aString));
				found = true;
			}
		}

		// for ( final WeaponProf obj : getWeaponProfs() )
		// {
		// if (obj == null)
		// {
		// continue;
		// }
		//
		// final String aString = checkForVariableInList(obj, variableString,
		// isMax, Constants.EMPTY_STRING,
		// Constants.EMPTY_STRING, found,
		// value, decrement);
		//
		// if (aString.length() > 0)
		// {
		// value = getMinMaxFirstValue(found, isMax, value,
		// Float.parseFloat(aString));
		// found = true;
		// }
		// }

		for (PCStat obj : statList)
		{
			final String aString = checkForVariableInList(obj, variableString,
				isMax, Constants.EMPTY_STRING, Constants.EMPTY_STRING, found,
				value, decrement);

			if (aString.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(aString));
				found = true;
			}
		}

		for (PCAlignment obj : SettingsHandler.getGame()
			.getUnmodifiableAlignmentList())
		{
			final String aString = checkForVariableInList(obj, variableString,
				isMax, Constants.EMPTY_STRING, Constants.EMPTY_STRING, found,
				value, decrement);

			if (aString.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float
					.parseFloat(aString));
				found = true;
			}
		}

		if (!found)
		{
			if (recurse)
			{
				lastVariable = variableString;
				value = getVariableValue(variableString, Constants.EMPTY_STRING)
					.floatValue();
				includeBonus = false;
				found = true;
				lastVariable = null;
			}
			else
			{
				return null;
			}
		}

		if (found && includeBonus) // TODO: condition always true
		{
			value += getTotalBonusTo("VAR", variableString);
		}

		return new Float(value);
	}

	// /**
	// * Returns the <tt>Set</tt> of <tt>WeaponProf</tt> objects for the
	// character.
	// *
	// * @return A sorted <tt>Set</tt> of weapon proficiencies.
	// */
	// public TreeSet<WeaponProf> getWeaponProfList()
	// {
	// final TreeSet<WeaponProf> wp = new TreeSet<WeaponProf>(weaponProfList);
	//
	// // Try all possible PObjects
	// for (PObject pobj : getPObjectList())
	// {
	// if (pobj != null)
	// {
	// final List<String> profKeyList =
	// pobj.getSafeListFor(ListKey.SELECTED_WEAPON_PROF_BONUS);
	// for (String profKey : profKeyList)
	// {
	// final WeaponProf prof = Globals.getWeaponProfKeyed(profKey);
	// if (prof != null)
	// {
	// wp.add(prof);
	// }
	// }
	// }
	// }
	//
	// return wp;
	// }

	private Map<String, WeaponProf> buildWeaponProfCache()
	{
		final Map<String, WeaponProf> ret = new HashMap<String, WeaponProf>();
		if (theWeaponProfs != null)
		{
			for (final WeaponProf wp : this.theWeaponProfs)
			{
				ret.put(wp.getKeyName(), wp);
			}
		}

		// Try all possible PObjects
		for (final PObject pobj : getPObjectList())
		{
			if (pobj != null)
			{
				// results = addWeaponProfsLists(aRace.getWeaponProfAutos(),
				// results, aFeatList, true);
				//
				// for (String aString :
				// aRace.getSafeListFor(weaponProfBonusKey))
				// {
				// results.add(aString);
				// addWeaponProfToList(aFeatList, aString, true);
				// }
				final Set<String> profKeyList = new TreeSet<String>(pobj
					.getSafeListFor(ListKey.SELECTED_WEAPON_PROF_BONUS));
				// TODO - Need to handle more crap here.
				pobj.addAutoTagsToList("WEAPONPROF", profKeyList, this, true);
				// TODO: Selected bonus weapon prof is stored in the associated
				// list
				for (final String profKey : profKeyList)
				{
					final WeaponProf prof = Globals.getWeaponProfKeyed(profKey);
					if (prof != null)
					{
						ret.put(prof.getKeyName(), prof);
					}
				}
			}
		}
		return ret;
	}

	public SortedSet<WeaponProf> getWeaponProfs()
	{
		if (this.cachedWeaponProfs == null)
		{
			cachedWeaponProfs = buildWeaponProfCache();
		}
		return Collections.unmodifiableSortedSet(new TreeSet<WeaponProf>(
			cachedWeaponProfs.values()));
	}

	/**
	 * Sets the character's weight in pounds.
	 * 
	 * @param i
	 *            A weight to set.
	 */
	public void setWeight(final int i)
	{
		weightInPounds = i;
		setDirty(true);
	}

	/**
	 * Gets the character's weight in pounds.
	 * 
	 * @return The character's weight.
	 */
	public int getWeight()
	{
		return weightInPounds;
	}

	public void setPointBuyPoints(final int argPointBuyPoints)
	{
		pointBuyPoints = argPointBuyPoints;
	}

	public int getPointBuyPoints()
	{
		return pointBuyPoints + (int) getTotalBonusTo("POINTBUY", "POINTS");
	}

	public void setXP(final int xp)
	{
		// Remove the effect of LEVELADJ when storing our
		// internal notion of experience
		int realXP = xp - getLAXP();

		if (realXP < 0)
		{
			Logging.errorPrint("ERROR: too little experience: " + realXP);
			realXP = 0;
		}

		setEarnedXP(realXP);
	}

	public int getXP()
	{
		// Add the effect of LEVELADJ when
		// showing our external notion of XP.
		return earnedXP + getLAXP();
	}

	public void addArmorProf(final String aProf)
	{
		if (!armorProfList.contains(aProf))
		{
			//
			// Insert all types at the head of the list
			//
			if (aProf.startsWith("TYPE=") || aProf.startsWith("TYPE."))
			{
				armorProfList.add(0, aProf);
			}
			else
			{
				armorProfList.add(aProf);
			}
		}
		// setDirty(true);
	}

	public void addArmorProfs(final List<String> aList)
	{
		for (String prof : aList)
		{
			addArmorProf(prof);
		}
	}

	public void addEquipSet(final EquipSet set)
	{
		equipSetList.add(set);
		setDirty(true);
	}

	/**
	 * Add an item of equipment to the character.
	 * 
	 * @param eq
	 *            The equipment to be added.
	 */
	public void addEquipment(final Equipment eq)
	{
		equipmentList.add(eq);

		if (!equipmentMasterList.contains(eq))
		{
			equipmentMasterList.add(eq);
		}

		if (eq.isType(Constants.s_TYPE_SPELLBOOK))
		{
			String baseBookname = eq.getName();
			String bookName = eq.getName();
			int qty = (int) eq.qty();
			for (int i = 0; i < qty; i++)
			{
				if (i > 0)
				{
					bookName = baseBookname + " #" + (i + 1);
				}
				SpellBook book = new SpellBook(bookName,
					SpellBook.TYPE_SPELL_BOOK);
				book.setEquip(eq);
				addSpellBook(book);
			}
		}
		setDirty(true);
	}

	/**
	 * Update the number of a particular equipment item the character possesses.
	 * Mostly concerned with ensuring that the spellbook objects remain in sync
	 * with the number of equipment spellbooks.
	 * 
	 * @param eq
	 *            The Equipment being updated.
	 * @param oldQty
	 *            The original number of items.
	 * @param newQty
	 *            The new number of items.
	 */
	public void updateEquipmentQty(final Equipment eq, double oldQty,
		double newQty)
	{
		if (eq.isType(Constants.s_TYPE_SPELLBOOK))
		{
			String baseBookname = eq.getName();
			String bookName = eq.getName();
			int old = (int) oldQty;
			int newQ = (int) newQty;

			// Add any new items
			for (int i = old; i < newQ; i++)
			{
				if (i > 0)
				{
					bookName = baseBookname + " #" + (i + 1);
				}
				SpellBook book = new SpellBook(bookName,
					SpellBook.TYPE_SPELL_BOOK);
				book.setEquip(eq);
				addSpellBook(book);
			}

			// Remove any old items
			for (int i = old; i > newQ; i--)
			{
				if (i > 0)
				{
					bookName = baseBookname + " #" + i;
				}
				delSpellBook(bookName);
			}
		}
		setDirty(true);
	}

	public void addFollower(final Follower aFollower)
	{
		followerList.add(aFollower);
		setDirty(true);
	}

	public void addLocalEquipment(final Equipment eq)
	{
		equipmentList.add(eq);
	}

	public void addNotesItem(final NoteItem item)
	{
		notesList.add(item);
		setDirty(true);
	}

	/**
	 * Adds a "temporary" bonus
	 * 
	 * @param aBonus
	 */
	public void addTempBonus(final BonusObj aBonus)
	{
		getTempBonusList().add(aBonus);
		setDirty(true);
	}

	public void addTempBonusItemList(final Equipment aEq)
	{
		getTempBonusItemList().add(aEq);
		setDirty(true);
	}

	/**
	 * Compute total bonus from a List of BonusObj's
	 * 
	 * @param aList
	 * @return bonus from list
	 */
	public double calcBonusFromList(final List<BonusObj> aList)
	{
		double iBonus = 0;

		for (BonusObj bonus : aList)
		{
			final PObject anObj = (PObject) bonus.getCreatorObject();

			if (anObj == null)
			{
				continue;
			}

			iBonus += anObj.calcBonusFrom(bonus, this, this);
		}

		return iBonus;
	}

	public boolean checkQualifyList(Class cl, final String qualifierItem)
	{
		/*
		 * The use of Object.class here is the "universalizer" to account
		 * for the 5.10.* format of Qualify - which is "allow anything all at once"
		 */
		return getQualifyMap().containsInList(cl, qualifierItem)
				|| getQualifyMap().containsInList(Object.class, qualifierItem);
	}

	/**
	 * Checks to see if this PC has the weapon proficiency key aKey
	 * 
	 * @param aKey
	 * @return boolean
	 */
	public boolean hasWeaponProfKeyed(final String aKey)
	{
		// for ( WeaponProf wp : getWeaponProfList() )
		// {
		// if (aKey.equalsIgnoreCase(wp.getKeyName()))
		// {
		// return true;
		// }
		// }
		//
		// return false;
		if (cachedWeaponProfs == null)
		{
			cachedWeaponProfs = buildWeaponProfCache();
		}
		return cachedWeaponProfs.get(aKey) != null;
	}

	public boolean hasWeaponProf(final WeaponProf wp)
	{
		// return hasWeaponProfKeyed( wp.getKeyName() );
		if (cachedWeaponProfs == null)
		{
			cachedWeaponProfs = buildWeaponProfCache();
		}
		return cachedWeaponProfs.get(wp.getKeyName()) != null;
	}

	public Equipment getEquipmentNamed(final String aString)
	{
		return getEquipmentNamed(aString, getEquipmentMasterList());
	}

	public List<String> getMiscList()
	{
		return miscList;
	}

	public void buildVariableSet()
	{
		// Building the PObject list relies on variables for evaluating prereqs,
		// so we have to grab it before clearing out the variables.
		List<? extends PObject> pObjList = getPObjectList();
		variableSet.clear();

		// Go through all objects that could add a VAR
		// and build the HashSet
		// Try all possible POBjects
		for (PObject aPObj : pObjList)
		{
			if (aPObj != null)
			{
				variableSet.addAll(aPObj.getVariableNamesAsUnmodifiableSet());
			}
		}

		// Some virtual feats rely on variables as prereqs, hence the need to
		// Recalculate them after we get all vars.
		setVirtualFeatsStable(false);

	}

	public boolean delEquipSet(final EquipSet eSet)
	{
		if (equipSetList.isEmpty())
		{
			return false;
		}

		boolean found = false;
		final String pid = eSet.getIdPath();

		// first remove this EquipSet
		equipSetList.remove(eSet);

		// now find and remove all it's children
		for (Iterator<EquipSet> e = equipSetList.iterator(); e.hasNext();)
		{
			final EquipSet es = e.next();
			final String abParentId = es.getParentIdPath()
				+ EquipSet.PATH_SEPARATOR;
			final String abPid = pid + EquipSet.PATH_SEPARATOR;

			if (abParentId.startsWith(abPid))
			{
				e.remove();
				found = true;
			}
		}
		setDirty(true);

		return found;
	}

	public void delEquipSetItem(final Equipment eq)
	{
		if (equipSetList.isEmpty())
		{
			return;
		}

		final List<EquipSet> tmpList = new ArrayList<EquipSet>();

		// now find and remove equipment from all EquipSet's
		for (EquipSet es : equipSetList)
		{
			final Equipment eqI = es.getItem();

			if ((eqI != null) && eq.equals(eqI))
			{
				tmpList.add(es);
			}
		}

		for (EquipSet es : tmpList)
		{
			delEquipSet(es);
		}
		setDirty(true);
	}

	public void delFollower(final Follower aFollower)
	{
		followerList.remove(aFollower);
		setDirty(true);
	}

	public void equipmentListAddAll(final List<Equipment> aList)
	{
		if (aList.isEmpty())
		{
			return;
		}

		equipmentList.addAll(aList);
		equipmentMasterList.addAll(aList);
		setDirty(true);
	}

	public boolean hasVariable(final String variableString)
	{
		for (String var : variableList)
		{
			final StringTokenizer aTok = new StringTokenizer(var,
				Constants.PIPE);
			aTok.nextToken(); // source
			aTok.nextToken(); // subSource

			if ((aTok.nextToken()).equalsIgnoreCase(variableString)) // nString
			{
				return true;
			}
		}

		// if (Globals.hasWeaponProfVariableNamed(getWeaponProfs(),
		// variableString))
		// {
		// return true;
		// }

		return variableSet.contains(variableString.toUpperCase());
	}

	/**
	 * Put the provided bonus key and value into the supplied bonus map. Some
	 * sanity checking is done on the key.
	 * 
	 * @param aKey
	 *            The bonus key
	 * @param aVal
	 *            The value of the bonus
	 * @param bonusMap
	 *            The map of bonuses being built.
	 */
	private void putActiveBonusMap(final String aKey, final String aVal,
		Map<String, String> bonusMap)
	{
		//
		// This is a bad idea...will add whatever the bonus is to ALL skills
		//
		if (aKey.equalsIgnoreCase("SKILL.LIST"))
		{
			displayUpdate = true;
			return;
		}
		bonusMap.put(aKey, aVal);
		// setDirty(true);
	}

	public int racialSizeInt()
	{
		int iSize = 0;

		if (race != null)
		{
			// get the base size for the race
			iSize = Globals.sizeInt(race.getSize());

			// now check and see if a template has set the
			// size of the character in question
			// with something like SIZE:L
			for (PCTemplate template : getTemplateList())
			{
				final String templateSize = template.getTemplateSize();

				if (templateSize.length() != 0)
				{
					iSize = Globals.sizeInt(templateSize);
					if (iSize == 0 && !templateSize.equals("F"))
					{
						// We failed to get a size try and parse it as JEP
						iSize = getVariableValue(templateSize,
							template.getKeyName()).intValue();
					}
				}
			}
		}

		return iSize;
	}

	/**
	 * @param aBonus
	 *            This will be used when I expand the functionality of the
	 *            TempBonus tab. Please leave -- JSC 08/08/03
	 */
	public void removeActiveBonus(final BonusObj aBonus)
	{
		activeBonusList.remove(aBonus);
	}

	public void removeEquipment(final Equipment eq)
	{
		if (eq.isType(Constants.s_TYPE_SPELLBOOK))
		{
			delSpellBook(eq.getName());
		}

		equipmentList.remove(eq);
		equipmentMasterList.remove(eq);
		setDirty(true);
	}

	public void removeLocalEquipment(final Equipment eq)
	{
		equipmentList.remove(eq);
		setDirty(true);
	}

	/**
	 * Now we use the ACTYPE tag on misc info to determine the formula
	 * 
	 * @return ac total
	 */
	public int getACTotal()
	{
		return calcACOfType("Total");
	}

	public void setAlignment(final int index, final boolean bLoading)
	{
		setAlignment(index, bLoading, false);
	}

	public void setAlignment(final int index, final boolean bLoading,
		final boolean bForce)
	{
		// Anyone every heard of constants!?
		// 0 = LG, 3 = NG, 6 = CG
		// 1 = LN, 4 = TN, 7 = CN
		// 2 = LE, 5 = NE, 8 = CE
		if (bForce || this.race.canBeAlignment(Integer.toString(index)))
		{
			alignment = index;
		}
		else
		{
			if ((bLoading)
				&& (index != SettingsHandler.getGame().getIndexOfAlignment(
					Constants.s_NONE)))
			{
				ShowMessageDelegate.showMessageDialog(
					"Invalid alignment. Setting to <none selected>",
					Constants.s_APPNAME, MessageType.INFORMATION);
				alignment = SettingsHandler.getGame().getIndexOfAlignment(
					Constants.s_NONE);
			}

			throw new IllegalArgumentException("Invalid alignment");
		}

		setDirty(true);
	}

	public String getAttackString(AttackType at)
	{
		return getAttackString(at, 0);
	}

	public String getAttackString(AttackType at, final int bonus)
	{
		return getAttackString(at, bonus, 0);
	}

	/**
	 * Calculates and returns an attack string for one of Melee, Ranged or
	 * Unarmed damage. This will be returned in attack string format i.e.
	 * +11/+6/+1. The attack string returned by this function normally only
	 * includes the attacks generated by the characters Base Attack Bonus. There
	 * are two bonuses to TOHIT that may be applied to the attack string
	 * returned by this function. The first bonus increases only the size of the
	 * attacks generated. The second increases both the size and number of
	 * attacks
	 * 
	 * @param index
	 *            The type of attack. Takes an AttackType (an enumeration)
	 * 
	 * @param TOHITBonus
	 *            A bonus that will be added to the TOHIT numbers. This bonus
	 *            affects only the numbers produced, not the number of attacks
	 * 
	 * @param BABBonus
	 *            This bonus will be added to BAB before the number of attacks
	 *            has been determined.
	 * @return The attack string for this character
	 */

	public String getAttackString(AttackType at, final int TOHITBonus,
		int BABBonus)
	{
		final String cacheLookup = "AttackString:" + at.getIdentifier() + ","
			+ TOHITBonus + "," + BABBonus;
		final String cached = getVariableProcessor().getCachedString(
			cacheLookup);

		if (cached != null)
		{
			return cached;
		}

		// index: 0 = melee; 1 = ranged; 2 = unarmed
		// now we see if this PC is a Familiar
		// Initialise to some large negative number
		int masterBAB = -9999;
		int masterTotal = -9999;
		final PlayerCharacter nPC = getMasterPC();

		final int totalClassLevels = getTotalCharacterLevel();
		Map<String, String> totalLvlMap = null;
		final Map<String, String> classLvlMap;

		if (totalClassLevels > SettingsHandler.getGame().getBabMaxLvl())
		{
			totalLvlMap = getTotalLevelHashMap();
			classLvlMap = getCharacterLevelHashMap(SettingsHandler.getGame()
				.getBabMaxLvl());

			// insure class-levels total is below some value (20)
			getVariableProcessor().pauseCache();
			setClassLevelsBrazenlyTo(classLvlMap);
		}

		if ((nPC != null) && (getCopyMasterBAB().length() > 0))
		{
			masterBAB = nPC.baseAttackBonus();

			final String copyMasterBAB = replaceMasterString(
				getCopyMasterBAB(), masterBAB);
			masterBAB = getVariableValue(copyMasterBAB, Constants.EMPTY_STRING)
				.intValue();
			masterTotal = masterBAB + TOHITBonus;
		}

		final int BAB = baseAttackBonus();

		int attackCycle = 1;
		int workingBAB = BAB + TOHITBonus;
		int subTotal = BAB;
		int raceBAB = getRace().getBAB(this);

		final List<Integer> ab = new ArrayList<Integer>(10);
		final StringBuffer attackString = new StringBuffer();

		// Assume a max of 10 attack cycles
		for (int total = 0; total < 10; ++total)
		{
			ab.add(Integer.valueOf(0));
		}

		// Some classes (like the Monk or Ranged Sniper) use
		// a different attack cycle than the standard classes
		// So compute the base attack for this type (BAB, RAB, UAB)
		for (PCClass pcClass : classList)
		{
			// Get the attack bonus
			final int b = pcClass.baseAttackBonus(this);

			// Get the attack cycle
			final int c = pcClass.attackCycle(at);

			// add to all other classes
			final int d = ab.get(c).intValue() + b;

			// set new value for iteration
			ab.set(c, Integer.valueOf(d));

			if (c != 3)
			{
				raceBAB += b;
			}
		}

		// Iterate through all the possible attack cycle values
		// and find the one with the highest attack value
		for (int i = 2; i < 10; ++i)
		{
			final int newAttack = ab.get(i).intValue();
			final int oldAttack = ab.get(attackCycle).intValue();

			if ((newAttack / i) > (oldAttack / attackCycle))
			{
				attackCycle = i;
			}
		}

		// restore class levels to original value if altered
		if (totalLvlMap != null)
		{
			setClassLevelsBrazenlyTo(totalLvlMap);
			getVariableProcessor().restartCache();
		}

		// total Number of Attacks for this PC
		int attackTotal = ab.get(attackCycle).intValue();

		// Default cut-off before multiple attacks (e.g. 5)
		final int defaultAttackCycle = SettingsHandler.getGame().getBabAttCyc();

		if (attackTotal == 0)
		{
			attackCycle = defaultAttackCycle;
		}

		// FAMILIAR: check to see if the masters BAB is better
		workingBAB = Math.max(workingBAB, masterTotal);
		subTotal = Math.max(subTotal, masterBAB);
		raceBAB = Math.max(raceBAB, masterBAB);

		if (attackCycle != defaultAttackCycle)
		{
			if ((attackTotal / attackCycle) < (subTotal / defaultAttackCycle))
			{
				attackCycle = defaultAttackCycle;
				attackTotal = subTotal;
			}
			else
			{
				workingBAB -= raceBAB;
				subTotal -= raceBAB;
			}
		}

		int maxAttacks = SettingsHandler.getGame().getBabMaxAtt();
		final int minMultiBab = SettingsHandler.getGame().getBabMinVal();

		// If there is a bonus to BAB, it needs to be added to ALL of
		// the variables used to determine the number of attacks
		attackTotal += BABBonus;
		workingBAB += BABBonus;
		subTotal += BABBonus;

		do
		{
			if (attackString.length() > 0)
			{
				attackString.append('/');
			}

			attackString.append(Delta.toString(workingBAB));
			workingBAB -= attackCycle;
			attackTotal -= attackCycle;
			subTotal -= attackCycle;
			maxAttacks--;
		}
		while (((attackTotal >= minMultiBab) || (subTotal >= minMultiBab))
			&& (maxAttacks > 0));

		getVariableProcessor().addCachedString(cacheLookup,
			attackString.toString());
		return attackString.toString();
	}

	public SortedSet<Language> getAutoLanguages()
	{
		// find list of all possible languages
		boolean clearRacials = false;

		final SortedSet<Language> autoLangs = new TreeSet<Language>();

		// Search for a CLEAR in the list and
		// if found clear all BEFORE but not AFTER it.
		// ---arcady June 1, 2002
		for (Language lang : templateAutoLanguages)
		{
			final String aString = lang.toString();

			if (".CLEARRACIAL".equals(aString))
			{
				clearRacials = true;
				languages.removeAll(getRace().getSafeListFor(
					ListKey.AUTO_LANGUAGES));
			}
			else if (".CLEARALL".equals(aString) || ".CLEAR".equals(aString))
			{
				clearRacials = true;
				autoLangs.clear();
				languages.clear();
			}
			else if (".CLEARTEMPLATES".equals(aString))
			{
				autoLangs.clear();
				languages.removeAll(templateAutoLanguages);
			}
			else
			{
				autoLangs.add(lang);
			}
		}

		for (PObject pObj : getPObjectList())
		{
			if (clearRacials && pObj instanceof Race)
			{
				clearRacials = false;
				continue;
			}
			autoLangs.addAll(pObj.getSafeListFor(ListKey.AUTO_LANGUAGES));
		}

		languages.addAll(autoLangs);

		return autoLangs;
	}

	/**
	 * Sets the autoSortGear.
	 * 
	 * @param autoSortGear
	 *            The autoSortGear to set
	 */
	public void setAutoSortGear(final boolean autoSortGear)
	{
		this.autoSortGear = autoSortGear;
		setDirty(true);
	}

	/**
	 * Returns the autoSortGear.
	 * 
	 * @return boolean
	 */
	public boolean isAutoSortGear()
	{
		return autoSortGear;
	}

	/**
	 * whether we should add auto known spells at level up
	 * 
	 * @param aBool
	 */
	public void setAutoSpells(final boolean aBool)
	{
		autoKnownSpells = aBool;
		setDirty(true);
	}

	public boolean getAutoSpells()
	{
		return autoKnownSpells;
	}

	/**
	 * Determine whether higher level known spell slots can be used for lower
	 * level spells, or if known spells are restricted to their own level only.
	 * 
	 * @return Returns the useHigherKnownSlots.
	 */
	public boolean getUseHigherKnownSlots()
	{
		return useHigherKnownSlots;
	}

	/**
	 * Set whether higher level known spell slots can be used for lower level
	 * spells, or if known spells are restricted to their own level only.
	 * 
	 * @param useHigher
	 *            Can higher level known spell slots be used?
	 */
	public void setUseHigherKnownSlots(boolean useHigher)
	{
		this.useHigherKnownSlots = useHigher;
	}

	/**
	 * Determine whether higher level prepared spell slots can be used for lower
	 * level spells, or if prepared spells are restricted to their own level
	 * only.
	 * 
	 * @return Returns the useHigherPreppedSlots.
	 */
	public boolean getUseHigherPreppedSlots()
	{
		return useHigherPreppedSlots;
	}

	/**
	 * Set whether higher level prepared spell slots can be used for lower level
	 * spells, or if prepared spells are restricted to their own level only.
	 * 
	 * @param useHigher
	 *            Can higher level prepared spell slots be used?
	 */
	public void setUseHigherPreppedSlots(boolean useHigher)
	{
		this.useHigherPreppedSlots = useHigher;
	}

	/**
	 * Returns the &quot;Base&quot; check value for the check at the index
	 * specified.
	 * 
	 * <p>
	 * This method caps the base check based on the game mode setting for
	 * {@link pcgen.core.GameMode#getChecksMaxLvl() checks max level}.
	 * 
	 * @param checkInd
	 *            The index of the check to get
	 * 
	 * @return The base check value.
	 */
	public int getBaseCheck(final int checkInd)
	{
		final String cacheLookup = "getBaseCheck:" + checkInd; //$NON-NLS-1$
		final Float total = getVariableProcessor().getCachedVariable(
			cacheLookup);

		if (total != null)
		{
			return total.intValue();
		}

		double bonus = 0;
		final int totalClassLevels;
		Map<String, String> totalLvlMap = null;
		final Map<String, String> classLvlMap;

		if (checkInd < SettingsHandler.getGame().getUnmodifiableCheckList()
			.size())
		{
			totalClassLevels = getTotalCharacterLevel();
			if (totalClassLevels > SettingsHandler.getGame().getChecksMaxLvl())
			{
				totalLvlMap = getTotalLevelHashMap();
				classLvlMap = getCharacterLevelHashMap(SettingsHandler
					.getGame().getChecksMaxLvl());
				getVariableProcessor().pauseCache();
				setClassLevelsBrazenlyTo(classLvlMap); // insure class-levels
														// total is below some
														// value (e.g. 20)
			}

			final String checkName = SettingsHandler.getGame()
				.getUnmodifiableCheckList().get(checkInd).toString();
			bonus = getTotalBonusTo("CHECKS", "BASE." + checkName);

			//
			// now we see if this PC is a Familiar/Mount
			final PlayerCharacter nPC = getMasterPC();

			if ((nPC != null) && (getCopyMasterCheck().length() > 0))
			{
				int masterBonus = nPC.getBaseCheck(checkInd);

				final String copyMasterCheck = replaceMasterString(
					getCopyMasterCheck(), masterBonus);
				masterBonus = getVariableValue(copyMasterCheck,
					Constants.EMPTY_STRING).intValue();

				// use masters save if better
				bonus = Math.max(bonus, masterBonus);
			}

			if (totalLvlMap != null)
			{
				setClassLevelsBrazenlyTo(totalLvlMap);
				getVariableProcessor().restartCache();
			}
		}
		return (int) bonus;
	}

	/**
	 * Returns the total check value for the check index specified for the
	 * character.
	 * 
	 * <p>
	 * This total includes all check bonuses the character has.
	 * 
	 * @param aCheck
	 *            The index of the check to get.
	 * 
	 * @return A check value.
	 */
	public int getTotalCheck(final int aCheck)
	{
		int bonus = getBaseCheck(aCheck);
		return bonus
			+ (int) getTotalBonusTo("CHECKS", SettingsHandler.getGame()
				.getCheckKey(aCheck));
	}

	// /**
	// * type 0 = attack bonus; 1 = check1; 2 = check2; 3 = check3; etc, last
	// one is = Unarmed
	// * @param type
	// * @param addBonuses
	// * @return bonus
	// */
	// public double getBonus(final int type, final boolean addBonuses)
	// {
	// final String cacheLookup = "getBonus:" + type + "," + addBonuses;
	// final Float total =
	// getVariableProcessor().getCachedVariable(cacheLookup);
	//
	// if (total != null)
	// {
	// return total.doubleValue();
	// }
	//
	// double bonus = 0;
	// final int totalClassLevels;
	// Map<String, String> totalLvlMap = null;
	// final Map<String, String> classLvlMap;
	//
	// if (type == 0)
	// {
	// // bonus = race.getBAB(this);
	// }
	// else if (type <=
	// SettingsHandler.getGame().getUnmodifiableCheckList().size())
	// {
	// totalClassLevels = getTotalCharacterLevel();
	// if (totalClassLevels > SettingsHandler.getGame().getChecksMaxLvl())
	// {
	// totalLvlMap = getTotalLevelHashMap();
	// classLvlMap =
	// getCharacterLevelHashMap(SettingsHandler.getGame().getChecksMaxLvl());
	// getVariableProcessor().pauseCache();
	// setClassLevelsBrazenlyTo(classLvlMap); // insure class-levels total is
	// below some value (e.g. 20)
	// }
	//
	// bonus = getTotalBonusTo("CHECKS",
	// "BASE." + SettingsHandler.getGame().getUnmodifiableCheckList().get(type -
	// 1).toString());
	//
	// //
	// // now we see if this PC is a Familiar/Mount
	// final PlayerCharacter nPC = getMasterPC();
	//
	// if ((nPC != null) && (getCopyMasterCheck().length() > 0))
	// {
	// int masterBonus;
	// final PlayerCharacter curPC = this;
	// Globals.setCurrentPC(nPC);
	//
	// // calculate the Masters Save Bonus
	// masterBonus = nPC.calculateSaveBonus(type,
	// SettingsHandler.getGame().getUnmodifiableCheckList().get(type -
	// 1).toString(), "BASE");
	// Globals.setCurrentPC(curPC);
	//
	// final String copyMasterCheck = replaceMasterString(getCopyMasterCheck(),
	// masterBonus);
	// masterBonus = getVariableValue(copyMasterCheck, "").intValue();
	//
	// // use masters save if better
	// bonus = Math.max(bonus, masterBonus);
	// }
	//
	// if (totalLvlMap != null)
	// {
	// setClassLevelsBrazenlyTo(totalLvlMap);
	// getVariableProcessor().restartCache();
	// }
	// }
	//
	// if (addBonuses)
	// {
	// if (type == 0)
	// {
	// bonus += getTotalBonusTo("TOHIT", "TOHIT");
	// bonus += getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
	// }
	// else if (type <=
	// SettingsHandler.getGame().getUnmodifiableCheckList().size())
	// {
	// bonus += getTotalBonusTo("CHECKS",
	// SettingsHandler.getGame().getUnmodifiableCheckList().get(type -
	// 1).toString());
	// }
	// else
	// {
	// bonus += getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
	// }
	// }
	//
	// int cBonus = 0;
	//
	// for ( PCClass pcClass : classList )
	// {
	// if ((type == 0) || (type >
	// SettingsHandler.getGame().getUnmodifiableCheckList().size()))
	// {
	// cBonus += pcClass.baseAttackBonus(this);
	// }
	// }
	//
	// bonus += cBonus;
	//
	// getVariableProcessor().addCachedVariable(cacheLookup, new Float(bonus));
	// return bonus;
	// }

	/**
	 * return bonus total for a specific bonusType e.g:
	 * getBonusDueToType("COMBAT","AC","Armor") to get armor bonuses
	 * 
	 * @param mainType
	 * @param subType
	 * @param bonusType
	 * @return bonus due to type
	 */
	public double getBonusDueToType(final String mainType,
		final String subType, final String bonusType)
	{
		final String typeString = mainType + "." + subType + ":" + bonusType;

		return sumActiveBonusMap(typeString);
	}

	// public List<TypedBonus> getBonusesOfType(final String aBonusName, final
	// String aBonusType, final String aType )
	// {
	// final List<TypedBonus> bonuses = this.getBonusesTo(aBonusName,
	// aBonusType);
	// final List<TypedBonus> ret = new ArrayList<TypedBonus>(bonuses.size());
	// for ( final TypedBonus bonus : bonuses )
	// {
	// if ( bonus.getType().equals(aType) )
	// {
	// ret.add( bonus );
	// }
	// }
	// return ret;
	// }
	/**
	 * If the class passed in has the Levels
	 * 
	 * @param newLevelClass
	 *            The class the new level has been taken in.
	 * @return bonus feats for new level
	 * @deprecated
	 */
	@Deprecated
	public double getBonusFeatsForNewLevel(final PCClass newLevelClass)
	{
		double bonusFeats = 0.0;
		final Integer lpf = newLevelClass.getLevelsPerFeat();

		if (lpf != null && lpf.intValue() >= 0)
		{
			// If the class has levelsPerFeat set then the calculated
			// level for determining bonus feats = RacialHD+levels in this class
			int calculatedLevel = 0;
			if (getRace() != null && isMonsterDefault())
			{
				// If we are a default monster then we will need to add the
				// MonsterClassLevels
				// if we are not a default monster then these will be explicit
				// class levels
				// and we do not want to add them twice.
				calculatedLevel += getRace().getMonsterClassLevels(this);
			}

			calculatedLevel += newLevelClass.getLevel();

			final int levelsPerFeat = lpf.intValue();
			if (levelsPerFeat > 0)
			{
				bonusFeats = (calculatedLevel % levelsPerFeat == 0) ? 1 : 0;
			}
		}
		else
		{
			// If the class does not have levelsPerFeat set then the calculated
			// level
			// for determining bonus feats is RacialHD + sum of levels in all
			// classes
			// that do not have levelsPerFeat set
			int nonSpecificLevels = 0;
			if (getRace() != null && isMonsterDefault())
			{
				nonSpecificLevels += getRace().getMonsterClassLevels(this);
			}

			for (PCClass pcClass : classList)
			{
				final Integer levelsPerFeatForClass = pcClass
					.getLevelsPerFeat();
				if (levelsPerFeatForClass == null
					|| levelsPerFeatForClass.intValue() < 0)
				{
					nonSpecificLevels += pcClass.getLevel();
				}
			}
			bonusFeats = Globals.getBonusFeatsForLevel(nonSpecificLevels);
		}
		return bonusFeats;
	}

	/**
	 * Get the list of WeaponName and Proficiency types from the changeProfMap
	 * of each granting object
	 * 
	 * @return List
	 */
	public List<String> getChangeProfList()
	{
		final List<String> aList = new ArrayList<String>();

		for (PObject pObj : getPObjectList())
		{
			aList.addAll(pObj.getChangeProfList(this));
		}
		return aList;
	}

	public CharacterDomain getCharacterDomainForDomain(final String domainKey)
	{
		for (CharacterDomain cd : characterDomainList)
		{
			final Domain aDomain = cd.getDomain();

			if ((aDomain != null)
				&& aDomain.getKeyName().equalsIgnoreCase(domainKey))
			{
				return cd;
			}
		}

		return null;
	}

	public List<CharacterDomain> getCharacterDomainList()
	{
		return Collections.unmodifiableList(characterDomainList);
	}

	public boolean hasCharacterDomainList()
	{
		return characterDomainList != null && !characterDomainList.isEmpty();
	}

	public Domain getCharacterDomainKeyed(final String domainKey)
	{
		for (CharacterDomain cd : characterDomainList)
		{
			final Domain aDomain = cd.getDomain();

			if ((aDomain != null)
				&& aDomain.getKeyName().equalsIgnoreCase(domainKey))
			{
				return cd.getDomain();
			}
		}

		return null;
	}

	/**
	 * @return the number of Character Domains used
	 */
	public int getCharacterDomainUsed()
	{
		return characterDomainList.size();
	}

	public List<CompanionMod> getCompanionModList()
	{
		return Collections.unmodifiableList(companionModList);
	}

	public String getCopyMasterBAB()
	{
		for (CompanionMod cMod : companionModList)
		{
			if (cMod.getType().equalsIgnoreCase(getMaster().getType()))
			{
				if (cMod.getCopyMasterBAB() != null)
				{
					return cMod.getCopyMasterBAB();
				}
			}
		}

		return Constants.EMPTY_STRING;
	}

	public String getCopyMasterCheck()
	{
		for (CompanionMod cMod : companionModList)
		{
			if (cMod.getType().equalsIgnoreCase(getMaster().getType()))
			{
				if (cMod.getCopyMasterCheck() != null)
				{
					return cMod.getCopyMasterCheck();
				}
			}
		}

		return Constants.EMPTY_STRING;
	}

	public String getCopyMasterHP()
	{
		for (CompanionMod cMod : companionModList)
		{
			if (cMod.getType().equalsIgnoreCase(getMaster().getType()))
			{
				if (cMod.getCopyMasterHP() != null)
				{
					return cMod.getCopyMasterHP();
				}
			}
		}

		return Constants.EMPTY_STRING;
	}

	public void setCurrentHP(final int currentHP)
	{
		setDirty(true);
	}

	public boolean setDeity(final Deity aDeity)
	{
		if (!canSelectDeity(aDeity))
		{
			return false;
		}

		deity = aDeity;

		if (!isImporting())
		{
			getSpellList();
			deity.globalChecks(this);
		}
		setDirty(true);

		calcActiveBonuses();

		return true;
	}

	/**
	 * return the first source that matches className
	 * 
	 * @param className
	 * @return domain source
	 */
	public String getDomainSource(final String className)
	{
		for (String aKey : domainSourceMap.keySet())
		{
			final String aVal = domainSourceMap.get(aKey);
			final int aNum = Integer.parseInt(aVal);

			if ((className == null) && (aNum > 0))
			{
				return aKey;
			}
			else if (aKey.indexOf(className) >= 0)
			{
				return aKey;
			}
		}

		return Constants.EMPTY_STRING;
	}

	/**
	 * Returns the character's Effective Character Level.
	 * 
	 * <p>
	 * The level is calculated by adding total non-monster levels, total
	 * hitdice, and level adjustment.
	 * 
	 * @return The ECL of the character.
	 */
	public int getECL()
	{
		int totalLevels = 0;
		totalLevels += totalNonMonsterLevels();
		totalLevels += totalHitDice();
		totalLevels += getLevelAdjustment(this);

		return totalLevels;
	}

	/**
	 * Set the order in which equipment should be sorted for output.
	 * 
	 * @param i
	 *            The new output order
	 */
	public void setEquipOutputOrder(final int i)
	{
		equipOutputOrder = i;
		setDirty(true);
	}

	/**
	 * @return The selected Output Order for equipment.
	 */
	public int getEquipOutputOrder()
	{
		return equipOutputOrder;
	}

	/**
	 * Retrieves an unsorted list of the character's equipment matching the
	 * supplied type and status criteria.
	 * 
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param status
	 *            The required status: 1 (equipped) 2 (not equipped) 3 (don't
	 *            care)
	 * @return An ArrayList of the matching equipment objects.
	 */
	public List<Equipment> getEquipmentOfType(final String typeName,
		final int status)
	{
		return getEquipmentOfType(typeName, Constants.EMPTY_STRING, status);
	}

	/**
	 * Retrieves an unsorted list of the character's equipment matching the
	 * supplied type, sub type and status criteria.
	 * 
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param subtypeName
	 *            The subtype of equipment to be selected (empty string for no
	 *            subtype)
	 * @param status
	 *            The required status: 1 (equipped) 2 (not equipped) 3 (don't
	 *            care)
	 * @return An ArrayList of the matching equipment objects.
	 */
	public List<Equipment> getEquipmentOfType(final String typeName,
		final String subtypeName, final int status)
	{
		final List<Equipment> aArrayList = new ArrayList<Equipment>();

		for (Equipment eq : equipmentList)
		{
			if (eq.typeStringContains(typeName)
				&& (Constants.EMPTY_STRING.equals(subtypeName) || eq
					.typeStringContains(subtypeName))
				&& ((status == 3) || ((status == 2) && !eq.isEquipped()) || ((status == 1) && eq
					.isEquipped())))
			{
				aArrayList.add(eq);
			}
		}

		return aArrayList;
	}

	/**
	 * Retrieves a list, sorted in output order, of the character's equipment
	 * matching the supplied type and status criteria. This list is in ascending
	 * order of the equipment's outputIndex field. If multiple items of
	 * equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in this list.
	 * 
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param status
	 *            The required status: 1 (equipped) 2 (not equipped) 3 (don't
	 *            care)
	 * @return An ArrayList of the matching equipment objects in output order.
	 */
	public List<Equipment> getEquipmentOfTypeInOutputOrder(
		final String typeName, final int status)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, status),
			Constants.MERGE_ALL);
	}

	/**
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param status
	 *            The required status
	 * @param merge
	 *            What type of merge for like equipment
	 * @return An ArrayList of equipment objects
	 */
	public List<Equipment> getEquipmentOfTypeInOutputOrder(
		final String typeName, final int status, final int merge)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, status), merge);
	}

	/**
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param subtypeName
	 *            The subtype of equipment to be selected
	 * @param status
	 *            The required status
	 * @param merge
	 *            What sort of merging should occur
	 * @return An ArrayList of equipment objects
	 */
	public List<Equipment> getEquipmentOfTypeInOutputOrder(
		final String typeName, final String subtypeName, final int status,
		final int merge)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, subtypeName,
			status), Constants.MERGE_ALL);
	}

	/**
	 * Retrieve the expanded list of weapons Expanded weapons include: double
	 * weapons and melee+ranged weapons Output order is assumed Merge of like
	 * equipment depends on the passed in int
	 * 
	 * @param merge
	 * 
	 * @return the sorted list of weapons.
	 */
	public List<Equipment> getExpandedWeapons(final int merge)
	{
		final List<Equipment> weapList = sortEquipmentList(getEquipmentOfType(
			"Weapon", 3), merge);

		//
		// If any weapon is both Melee and Ranged, then make 2 weapons
		// for list, one Melee only, the other Ranged and Thrown.
		// For double weapons, if wielded in two hands show attacks
		// for both heads, head 1 and head 2 else
		// if wielded in 1 hand, just show damage by head
		//
		for (int idx = 0; idx < weapList.size(); ++idx)
		{
			final Equipment equip = weapList.get(idx);

			if (equip.isDouble()
				&& (equip.getLocation() == Equipment.EQUIPPED_TWO_HANDS))
			{
				Equipment eqm = equip.clone();
				eqm.removeType("Double");
				eqm.setTypeInfo("Head1");

				// Add "Head 1 only" to the name of the weapon
				eqm.setWholeItemName(eqm.getName());
				eqm.setName(EquipmentUtilities.appendToName(eqm.getName(),
					"Head 1 only"));

				if (eqm.getOutputName().indexOf("Head 1 only") < 0)
				{
					eqm.setOutputName(EquipmentUtilities.appendToName(eqm
						.getOutputName(), "Head 1 only"));
				}

				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.add(idx + 1, eqm);

				eqm = equip.clone();

				final String altType = eqm.getType(false);

				if (altType.length() != 0)
				{
					eqm.setTypeInfo(".CLEAR." + altType);
				}

				eqm.removeType("Double");
				eqm.setTypeInfo("Head2");
				eqm.setDamage(eqm.getAltDamage(this));
				eqm.setCritMult(eqm.getRawAltCritMult());
				eqm.setCritRange(Integer.toString(eqm.getRawCritRange(false)));
				eqm.getEqModifierList(true).clear();
				eqm.getEqModifierList(true)
					.addAll(eqm.getEqModifierList(false));

				// Add "Head 2 only" to the name of the weapon
				eqm.setWholeItemName(eqm.getName());
				eqm.setName(EquipmentUtilities.appendToName(eqm.getName(),
					"Head 2 only"));

				if (eqm.getOutputName().indexOf("Head 2 only") < 0)
				{
					eqm.setOutputName(EquipmentUtilities.appendToName(eqm
						.getOutputName(), "Head 2 only"));
				}

				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.add(idx + 2, eqm);
			}

			//
			// Leave else here, as otherwise will show attacks
			// for both heads for thrown double weapons when
			// it should only show one
			//
			else if (equip.isMelee() && equip.isRanged()
				&& (equip.getRange(this).intValue() != 0))
			{
				//
				// Strip off the Ranged portion, set range to 0
				//
				Equipment eqm = equip.clone();
				eqm.setTypeInfo("Both");
				eqm.removeType("Ranged.Thrown");
				eqm.setRange("0");
				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.set(idx, eqm);

				//
				// Replace any primary weapons
				int iPrimary;

				for (iPrimary = getPrimaryWeapons().size() - 1; iPrimary >= 0; --iPrimary)
				{
					final Equipment teq = getPrimaryWeapons().get(iPrimary);

					if (teq == equip)
					{
						break;
					}
				}

				if (iPrimary >= 0)
				{
					getPrimaryWeapons().set(iPrimary, eqm);
				}

				//
				// Replace any secondary weapons
				int iSecondary;

				for (iSecondary = getSecondaryWeapons().size() - 1; iSecondary >= 0; --iSecondary)
				{
					final Equipment teq = getSecondaryWeapons().get(iSecondary);

					if (teq == equip)
					{
						break;
					}
				}

				if (iSecondary >= 0)
				{
					getSecondaryWeapons().set(iSecondary, eqm);
				}

				//
				// Add thrown portion, strip Melee
				//
				eqm = equip.clone();
				eqm.setTypeInfo("Ranged.Thrown.Both");
				eqm.removeType("Melee");

				// Add "Thrown" to the name of the weapon
				eqm.setName(EquipmentUtilities.appendToName(eqm.getName(),
					"Thrown"));

				if (eqm.getOutputName().indexOf("Thrown") < 0)
				{
					eqm.setOutputName(EquipmentUtilities.appendToName(eqm
						.getOutputName(), "Thrown"));
				}

				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.add(++idx, eqm);

				if (iPrimary >= 0)
				{
					getPrimaryWeapons().add(++iPrimary, eqm);
				}
				else if (iSecondary >= 0)
				{
					getSecondaryWeapons().add(++iSecondary, eqm);
				}
			}
		}

		return weapList;
	}

	/**
	 * renaming to standard convention due to refactoring of export
	 * 
	 * Build on-the-fly so removing templates doesn't mess up favoured list
	 * 
	 * @author Thomas Behr 08-03-02
	 */
	public SortedSet<String> getFavoredClasses()
	{
		final SortedSet<String> favored = new TreeSet<String>(favoredClasses);

		for (PCTemplate template : templateList)
		{
			final String favoredClass = template.getFavoredClass();

			if ((favoredClass.length() != 0) && !favored.contains(favoredClass))
			{
				favored.add(favoredClass);
			}
		}

		return favored;
	}

	/**
	 * Calculates total bonus from Feats
	 * 
	 * @param aType
	 * @param aName
	 * @param subSearch
	 * @return feat bonus to
	 */
	public double getFeatBonusTo(String aType, String aName,
		final boolean subSearch)
	{
		return getPObjectWithCostBonusTo(aggregateFeatList(), aType
			.toUpperCase(), aName.toUpperCase(), subSearch);
	}

	/**
	 * Returns the Feat definition of a feat possessed by the character.
	 * 
	 * @param featName
	 *            String name of the feat to check for.
	 * @return the Feat (not the CharacterFeat) searched for, <code>null</code>
	 *         if not found.
	 */
	public Ability getFeatNamed(final String featName)
	{
		return AbilityUtilities.getAbilityFromList(aggregateFeatList(),
			Constants.FEAT_CATEGORY, featName, Ability.Nature.ANY);
	}

	public Ability getFeatNamed(final String featName,
		final Ability.Nature featType)
	{
		return AbilityUtilities.getAbilityFromList(aggregateFeatList(),
			Constants.FEAT_CATEGORY, featName, featType);
	}

	/**
	 * Searches the characters feats for an Ability object which is a clone of
	 * the same Base ability as the Ability passed in
	 * 
	 * @param anAbility
	 * @return the Ability if found, otherwise null
	 */
	public Ability getAbilityMatching(final Ability anAbility)
	{
		return AbilityUtilities.getAbilityFromList(aggregateFeatList(),
			anAbility);
	}

	public int getFirstSpellLevel(final Spell aSpell)
	{
		int anInt = 0;

		for (PCClass pcClass : getClassList())
		{
			final String aKey = pcClass.getSpellKey();
			final int temp = aSpell.getFirstLevelForKey(aKey, this);
			anInt = Math.min(anInt, temp);
		}

		return anInt;
	}

	public void setHasMadeKitSelectionForAgeSet(final int index,
		final boolean arg)
	{
		if ((index >= 0) && (index < 10))
		{
			ageSetKitSelections[index] = arg;
		}
		setDirty(true);
	}

	public List<Kit> getKitInfo()
	{
		List<Kit> returnList;
		if (kitList != null)
		{
			returnList = kitList;
		}
		else
		{
			returnList = Collections.emptyList();
		}

		return returnList;
	}

	public int getLevelAdjustment(final PlayerCharacter aPC)
	{
		int levelAdj = race.getLevelAdjustment(aPC);

		for (PCTemplate template : templateList)
		{
			levelAdj += template.getLevelAdjustment(aPC);
		}

		return levelAdj;
	}

	public List<PCLevelInfo> getLevelInfo()
	{
		return pcLevelInfo;
	}

	public String getLevelInfoClassKeyName(final int idx)
	{
		if ((idx >= 0) && (idx < getLevelInfoSize()))
		{
			return pcLevelInfo.get(idx).getClassKeyName();
		}

		return Constants.EMPTY_STRING;
	}

	public int getLevelInfoClassLevel(final int idx)
	{
		if ((idx >= 0) && (idx < getLevelInfoSize()))
		{
			return pcLevelInfo.get(idx).getLevel();
		}

		return 0;
	}

	public PCLevelInfo getLevelInfoFor(final String classKey, int level)
	{
		for (PCLevelInfo pcl : pcLevelInfo)
		{
			if (pcl.getClassKeyName().equals(classKey))
			{
				level--;
			}

			if (level <= 0)
			{
				return pcl;
			}
		}

		return null;
	}

	public int getLevelInfoSize()
	{
		return pcLevelInfo.size();
	}

	/**
	 * whether we should load companions on master load
	 * 
	 * @param aBool
	 */
	public void setLoadCompanion(final boolean aBool)
	{
		autoLoadCompanion = aBool;
		setDirty(true);
	}

	public boolean getLoadCompanion()
	{
		return autoLoadCompanion;
	}

	/**
	 * @return the number of Character Domains possible
	 */
	public int getMaxCharacterDomains()
	{
		return (int) getTotalBonusTo("DOMAIN", "NUMBER");
	}

	/**
	 * @param source
	 * @param aPC
	 * @return the number of Character Domains possible and check the level of
	 *         the source class if the result is 0.
	 */
	public int getMaxCharacterDomains(final PCClass source,
		final PlayerCharacter aPC)
	{
		int i = getMaxCharacterDomains();
		if (i == 0 && domainSourceMap.size() == 0)
			i = (int) source.getBonusTo("DOMAIN", "NUMBER", source.getLevel(),
				aPC);
		return i;
	}

	/**
	 * Calculate the maximum number of ranks the character is allowed to have in
	 * the specified skill.
	 * 
	 * @param skillKey
	 *            The key of the skill being checked.
	 * @param aClass
	 *            The name of the current class in which points are being spent -
	 *            only used to check cross-class skill cost.
	 * @return max rank
	 */
	public Float getMaxRank(final String skillKey, final PCClass aClass)
	{
		int levelForSkillPurposes = getTotalLevels();
		final BigDecimal maxRanks;

		if (SettingsHandler.isMonsterDefault())
		{
			levelForSkillPurposes += totalHitDice();
		}

		final Skill aSkill = Globals.getSkillKeyed(skillKey);

		if (aSkill == null)
		{
			return 0.0f;
		}

		if (aSkill.isExclusive())
		{
			// Exclusive skills only count levels in classes which give access
			// to the skill
			levelForSkillPurposes = 0;

			for (PCClass bClass : classList)
			{
				if (aSkill.isClassSkill(bClass, this))
				{
					levelForSkillPurposes += bClass.getLevel();
				}
			}

			if (SettingsHandler.isMonsterDefault())
			{
				levelForSkillPurposes += totalHitDice();
			}

			if (levelForSkillPurposes == 0)
			{
				// No classes qualify for this exclusive skill, so treat it as a
				// cross-class skill
				// This does not seem right to me! JD
				if (SettingsHandler.isMonsterDefault())
				{
					levelForSkillPurposes = (getTotalLevels() + totalHitDice());
				}
				else
				{
					levelForSkillPurposes = (getTotalLevels());
				}

				maxRanks = SkillUtilities.maxCrossClassSkillForLevel(
					levelForSkillPurposes, this);
			}
			else
			{
				maxRanks = SkillUtilities.maxClassSkillForLevel(
					levelForSkillPurposes, this);
			}
		}
		else if (!aSkill.isClassSkill(classList, this)
			&& (aSkill.costForPCClass(aClass, this) == Globals
				.getGameModeSkillCost_Class()))
		{
			// Cross class skill - but as cost is 1 only return a whole number
			maxRanks = new BigDecimal(SkillUtilities
				.maxCrossClassSkillForLevel(levelForSkillPurposes, this)
				.intValue()); // This was (int) (i/2.0) previously
		}
		else if (!aSkill.isClassSkill(classList, this))
		{
			// Cross class skill
			maxRanks = SkillUtilities.maxCrossClassSkillForLevel(
				levelForSkillPurposes, this);
		}
		else
		{
			// Class skill
			maxRanks = SkillUtilities.maxClassSkillForLevel(
				levelForSkillPurposes, this);
		}

		return new Float(maxRanks.floatValue());
	}

	/**
	 * @param moveIdx
	 * @return the integer movement speed for Index
	 */
	public Double getMovement(final int moveIdx)
	{
		if ((getMovements() != null) && (moveIdx < movements.length))
		{
			return movements[moveIdx];
		}
		return Double.valueOf(0);
	}

	public String getMovementType(final int moveIdx)
	{
		if ((movementTypes != null) && (moveIdx < movementTypes.length))
		{
			return movementTypes[moveIdx];
		}
		return Constants.EMPTY_STRING;
	}

	public double movementOfType(final String moveType)
	{
		if (movementTypes == null)
			return 0.0;
		for (int moveIdx = 0; moveIdx < movementTypes.length; moveIdx++)
		{
			if (movementTypes[moveIdx].equalsIgnoreCase(moveType))
				return movement(moveIdx);
		}
		return 0.0;
	}

	/**
	 * gets first domain with remaining slots, creates an CharacterDomain
	 * object, sets all the correct info and returns it
	 * 
	 * @return Character Domain
	 */
	public CharacterDomain getNewCharacterDomain()
	{
		return getNewCharacterDomain(null);
	}

	public CharacterDomain getNewCharacterDomain(final String className)
	{
		final String sDom = getDomainSource(className);

		if (sDom.length() > 0)
		{
			final StringTokenizer aTok = new StringTokenizer(sDom,
				Constants.PIPE);
			final String aType = aTok.nextToken();
			final String aName = aTok.nextToken();
			final int aLevel = Integer.parseInt(aTok.nextToken());
			final CharacterDomain aCD = new CharacterDomain();

			if (aType.equalsIgnoreCase("PCClass"))
			{
				aCD.setFromPCClass(true);
			}
			else
			{
				aCD.setFromPCClass(false);
			}

			aCD.setObjectName(aName);
			aCD.setLevel(aLevel);

			return aCD;
		}

		return null;
	}

	public boolean isNonAbility(final int i)
	{
		if (race.isNonAbility(i))
		{
			return true;
		}

		for (PCTemplate template : templateList)
		{
			if (template.isNonAbility(i))
			{
				return true;
			}
		}

		return false;
	}

	public int getNumberOfMovements()
	{
		return (movements != null) ? movements.length : 0;
	}

	public int getOffHandLightBonus()
	{
		final int div = getVariableValue("OFFHANDLIGHTBONUS",
			Constants.EMPTY_STRING).intValue();

		return div;
	}

	/*
	 * returns true if Equipment is in the primary weapon list
	 */
	public boolean isPrimaryWeapon(final Equipment eq)
	{
		if (eq == null)
		{
			return false;
		}

		for (Equipment eqI : primaryWeapons)
		{
			if (eqI.getName().equalsIgnoreCase(eq.getName())
				&& (eqI.getLocation() == eq.getLocation()))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isProficientWith(final Equipment eq)
	{
		if (eq.isShield())
		{
			final List<String> aList = getShieldProfList();
			return isProficientWith(eq, aList);
		}
		else if (eq.isArmor())
		{
			final List<String> aList = getArmorProfList();
			return isProficientWith(eq, aList);
		}
		else if (eq.isWeapon())
		{
			return isProficientWithWeapon(eq);
		}

		return false;
	}

	/**
	 * Changes the race of the character. First it removes the current Race, and
	 * any bonus attributes (e.g. feats), then add the new Race.
	 * 
	 * @param aRace
	 */
	public void setRace(final Race aRace)
	{
		final Race oldRace = getRace();
		final boolean raceIsNull = (oldRace == null); // needed because race
														// is set to null later
		final boolean firstLevel = getTotalClassLevels() == 1;

		// remove current race attributes
		if (!raceIsNull)
		{
			oldRace.getSpellSupport().clearCharacterSpells();

			if (PlayerCharacterUtilities.canReassignRacialFeats())
			{
				final StringTokenizer aTok = new StringTokenizer(oldRace
					.getFeatList(this), Constants.PIPE);

				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();

					if (aString.endsWith(")")
						&& (Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
							aString) == null))
					{
						final String featKey = aString.substring(0, aString
							.indexOf('(') - 1);

						final Ability anAbility = Globals.getAbilityKeyed(
							Constants.FEAT_CATEGORY, featKey);

						if (anAbility != null)
						{
							AbilityUtilities.modFeat(this, null, aString, true,
								false);
							// setFeats(feats - anAbility.getCost(this));
							adjustFeats(-anAbility.getCost(this));
						}
					}
					else
					{
						final Ability anAbility = Globals.getAbilityKeyed(
							Constants.FEAT_CATEGORY, aString);

						if (anAbility != null)
						{
							final String featKey = anAbility.getKeyName();

							if ((hasRealFeat(anAbility) || hasFeatAutomatic(featKey)))
							{
								AbilityUtilities.modFeat(this, null, aString,
									true, false);
								// setFeats(feats - anAbility.getCost(this));
								adjustFeats(-anAbility.getCost(this));
							}
						}
						else
						{
							ShowMessageDelegate.showMessageDialog(
								"Removing unknown feat: " + aString,
								Constants.s_APPNAME, MessageType.INFORMATION);
						}
					}
				}
			}

			languages.removeAll(oldRace.getSafeListFor(ListKey.AUTO_LANGUAGES));

			cachedWeaponProfs = null;

			if (stringChar.containsKey(StringKey.RACIAL_FAVORED_CLASS))
			{
				favoredClasses.remove(stringChar
						.get(StringKey.RACIAL_FAVORED_CLASS));
			}

			removeNaturalWeapons(race);

			for (String s : race.templatesAdded())
			{
				removeTemplate(getTemplateKeyed(s));
			}

			if ((race.getMonsterClass(this) != null)
				&& (race.getMonsterClassLevels(this) != 0))
			{
				final PCClass mclass = Globals.getClassKeyed(race
					.getMonsterClass(this));

				if (mclass != null)
				{
					incrementClassLevel(race.getMonsterClassLevels(this) * -1,
						mclass, true);
				}
			}
		}

		// add new race attributes
		race = null;

		if (aRace != null)
		{
			race = aRace.clone();
		}

		if (race != null)
		{
			race.activateBonuses(this);

			if (!isImporting())
			{
				Globals.getBioSet().randomize("AGE.HT.WT", this);
			}

			// Get existing classes
			final List<PCClass> existingClasses = new ArrayList<PCClass>(
				classList);
			classList.clear();

			//
			// Remove all saved monster level information
			//
			for (int i = getLevelInfoSize() - 1; i >= 0; --i)
			{
				final String classKeyName = getLevelInfoClassKeyName(i);
				final PCClass aClass = Globals.getClassKeyed(classKeyName);

				if ((aClass == null) || aClass.isMonster())
				{
					removeLevelInfo(i);
				}
			}

			final List<PCLevelInfo> existingLevelInfo = new ArrayList<PCLevelInfo>(
				pcLevelInfo);
			pcLevelInfo.clear();

			// Make sure monster classes are added first
			if (!isImporting() && (race.getMonsterClass(this) != null)
				&& (race.getMonsterClassLevels(this) != 0))
			{
				final PCClass mclass = Globals.getClassKeyed(race
					.getMonsterClass(this));

				if (mclass != null)
				{
					incrementClassLevel(race.getMonsterClassLevels(this),
						mclass, true);
				}
			}

			pcLevelInfo.addAll(existingLevelInfo);

			//
			// If user has chosen a class before choosing a race,
			// we need to tweak the number of skill points and feats
			//
			if (!isImporting() && existingClasses.size() != 0)
			{
				int totalLevels = this.getTotalLevels();
				// final Integer zero = Integer.valueOf(0);

				for (PCClass pcClass : existingClasses)
				{
					//
					// Don't add monster classes back in. This will possibly
					// mess up feats earned by level
					// ?Possibly convert to mclass if not null?
					//
					if (!pcClass.isMonster())
					{
						classList.add(pcClass);

						final int cLevels = pcClass.getLevel();

						// aClass.setLevel(0);
						pcClass.setSkillPool(0);

						int cMod = 0;

						for (int j = 0; j < cLevels; ++j)
						{
							cMod += pcClass.recalcSkillPointMod(this,
								++totalLevels);
						}

						pcClass.setSkillPool(cMod);
					}
				}
			}

			addNaturalWeapons(race.getNaturalWeapons());

			if (PlayerCharacterUtilities.canReassignRacialFeats())
			{
				final StringTokenizer aTok = new StringTokenizer(getRace()
					.getFeatList(this), Constants.PIPE);

				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();

					if (aString.endsWith(")")
						&& (Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
							aString) == null))
					{
						// we want the first instance of it, in case of Weapon
						// Focus(Longbow (Composite))
						final String featKey = aString.substring(0, aString
							.indexOf('(') - 1);

						final Ability anAbility = Globals.getAbilityKeyed(
							Constants.FEAT_CATEGORY, featKey);

						if (anAbility != null)
						{
							// setFeats(feats + anAbility.getCost(this));
							adjustFeats(anAbility.getCost(this));
							AbilityUtilities.modFeat(this, null, aString, true,
								true);
						}
					}
					else
					{
						final Ability anAbility = Globals.getAbilityKeyed(
							Constants.FEAT_CATEGORY, aString);

						if (anAbility != null)
						{
							final String featKey = anAbility.getKeyName();

							if ((!this.hasRealFeat(anAbility) && !this
								.hasFeatAutomatic(featKey)))
							{
								// setFeats(feats + anAbility.getCost(this));
								adjustFeats(anAbility.getCost(this));

								// modFeat(featName, true,
								// featName.endsWith("Proficiency"));
								AbilityUtilities.modFeat(this, null, aString,
									true, true);
							}
						}
						else
						{
							ShowMessageDelegate.showMessageDialog(
								"Adding unknown feat: " + aString,
								Constants.s_APPNAME, MessageType.INFORMATION);
						}
					}
				}
			}

			getAutoLanguages();
			getRacialFavoredClasses();

			race.getTemplates(isImporting(), this); // gets and adds templates

			race.chooseLanguageAutos(isImporting(), this);
		}

		// TODO - Change this back
		// setAggregateAbilitiesStable(null, false);
		setAggregateFeatsStable(false);
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);

		if (!isImporting())
		{
			getSpellList();
			race.globalChecks(this);
			adjustMoveRates();
			calcActiveBonuses();
		}

		setDirty(true);
	}

	/**
	 * return bonus from a Race
	 * 
	 * @param aType
	 * @param aName
	 * @return race bonus to
	 */
	public double getRaceBonusTo(String aType, String aName)
	{
		if (getRace() == null)
		{
			return 0;
		}

		final List<BonusObj> tempList = getRace().getBonusListOfType(
			aType.toUpperCase(), aName.toUpperCase());

		return calcBonusFromList(tempList);
	}

	public int getSR()
	{
		return calcSR(true);
	}

	/*
	 * returns true if Equipment is in the secondary weapon list
	 */
	public boolean isSecondaryWeapon(final Equipment eq)
	{
		if (eq == null)
		{
			return false;
		}

		for (Equipment eqI : secondaryWeapons)
		{
			if (eqI.getName().equalsIgnoreCase(eq.getName())
				&& (eqI.getLocation() == eq.getLocation()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Calculates total bonus from Size adjustments
	 * 
	 * @param aType
	 * @param aName
	 * @return size adjustment bonus to
	 */
	public double getSizeAdjustmentBonusTo(String aType, String aName)
	{
		return getBonusDueToType(aType.toUpperCase(), aName.toUpperCase(),
			"SIZE");
	}

	public Skill getSkillKeyed(final String skillKey)
	{
		for (Skill skill : getSkillList())
		{
			if (skill.getKeyName().equalsIgnoreCase(skillKey))
			{
				return skill;
			}
		}

		return null;
	}

	/**
	 * Set the order in which skills should be sorted for output.
	 * 
	 * @param i
	 *            The new output order
	 */
	public void setSkillsOutputOrder(final int i)
	{
		skillsOutputOrder = i;
		setDirty(true);
	}

	/**
	 * @return The selected Output Order for skills.
	 */
	public int getSkillsOutputOrder()
	{
		return skillsOutputOrder;
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster and of the desired caster level.
	 * 
	 * @param minLevel
	 * @return boolean
	 */
	public boolean isSpellCaster(final int minLevel)
	{
		return isSpellCaster(minLevel, false);
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster and of the total of all of their
	 * spellcasting levels is at least the desired caster level.
	 * 
	 * @param minLevel
	 *            The desired caster level
	 * @param sumOfLevels
	 *            True if all of the character caster levels should be added
	 *            together before the comparison.
	 * @return boolean
	 */
	public boolean isSpellCaster(final int minLevel, final boolean sumOfLevels)
	{
		return isSpellCaster(null, minLevel, sumOfLevels);
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster of the desired type and of the desired
	 * caster level.
	 * 
	 * @param spellType
	 *            The type of spellcaster (i.e. "ARCANE" or "Divine")
	 * @param minLevel
	 *            The desired caster level
	 * @return boolean
	 */
	public boolean isSpellCaster(final String spellType, final int minLevel)
	{
		return isSpellCaster(spellType, minLevel, false);
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster of the desired type and of the desired
	 * caster level.
	 * 
	 * @param spellType
	 *            The type of spellcaster (i.e. "Arcane" or "Divine")
	 * @param minLevel
	 *            The desired caster level
	 * @param sumLevels
	 *            True if all of the character caster levels should be added
	 *            together before the comparison.
	 * @return boolean
	 */
	public boolean isSpellCaster(final String spellType, final int minLevel,
		final boolean sumLevels)
	{
		int runningTotal = 0;

		for (PCClass pcClass : classList)
		{
			if (spellType == null
				|| spellType.equalsIgnoreCase(pcClass.getSpellType()))
			{
				int classLevels = (int) getTotalBonusTo("CASTERLEVEL", pcClass
					.getKeyName());
				if ((classLevels == 0)
					&& (canCastSpellTypeLevel(pcClass.getSpellType(), 0, 1) || canCastSpellTypeLevel(
						pcClass.getSpellType(), 1, 1)))
				{
					// missing CASTERLEVEL hack
					classLevels = pcClass.getLevel();
				}
				classLevels += (int) getTotalBonusTo("PCLEVEL", pcClass
					.getKeyName());
				if (sumLevels)
				{
					runningTotal += classLevels;
				}
				else
				{
					if (classLevels >= minLevel)
					{
						return true;
					}
				}
			}
		}

		if (sumLevels)
		{
			return runningTotal >= minLevel;
		}
		return false;
	}

	public boolean isSpellCastermax(final int maxLevel)
	{
		for (PCClass pcClass : classList)
		{
			if (!pcClass.getSpellType().equalsIgnoreCase(Constants.s_NONE)
				&& (pcClass.getLevel() <= maxLevel))
			{
				return true;
			}
		}

		return false;
	}

	public Map<String, Integer> getSpellInfoMap(final String key1,
		final String key2)
	{
		return spellTracker.getSpellInfoMap(key1, key2);
	}

	public boolean isSpellLevelforKey(final String key, final int levelMatch)
	{
		return spellTracker.isSpellLevelforKey(key, levelMatch);
	}

	public int getSpellLevelforKey(final String key, final int levelMatch)
	{
		return spellTracker.getSpellLevelforKey(key, levelMatch);
	}

	public void getSpellList()
	{
		// all non-spellcaster spells are added to race
		// so return if it's null
		if (race == null)
		{
			return;
		}

		race.getSpellSupport().clearCharacterSpells();
		addSpells(race);

		if (deity != null)
		{
			addSpells(deity);
		}

		for (CharacterDomain cd : characterDomainList)
		{
			addSpells(cd.getDomain());
		}

		for (PCClass pcClass : classList)
		{
			addSpells(pcClass);
		}

		for (Ability feat : aggregateFeatList())
		{
			addSpells(feat);
		}

		for (Skill skill : getSkillList())
		{
			addSpells(skill);
		}

		// Domains are skipped - it's assumed that their spells are added to the
		// first divine spellcasting
		for (Equipment eq : equipmentList)
		{
			if (eq.isEquipped())
			{
				addSpells(eq);

				for (EquipmentModifier eqMod : eq.getEqModifierList(true))
				{
					addSpells(eqMod);
				}

				for (EquipmentModifier eqMod : eq.getEqModifierList(false))
				{
					addSpells(eqMod);
				}
			}
		}

		for (PCTemplate template : templateList)
		{
			addSpells(template);
		}
	}

	/**
	 * Parses a spells range (short, medium or long) into an Integer based on
	 * the spell and spell casters level
	 * 
	 * @param aSpell
	 *            The spell being output.
	 * @param owner
	 *            The class providing the spell.
	 * @param si
	 *            The info about conditions applied to the spell
	 * @return spell range
	 */
	public String getSpellRange(final Spell aSpell, final PObject owner,
		final SpellInfo si)
	{
		String aRange = aSpell.getRange();
		final String aSpellClass = "CLASS:"
			+ (owner != null ? owner.getKeyName() : "");
		int rangeInFeet = 0;
		String aString = Globals.getGameModeSpellRangeFormula(aRange
			.toUpperCase());

		if (aRange.equalsIgnoreCase("CLOSE") && (aString == null))
		{
			aString = "((CASTERLEVEL/2).TRUNC*5)+25"; //$NON-NLS-1$
		}
		else if (aRange.equalsIgnoreCase("MEDIUM") && (aString == null))
		{
			aString = "(CASTERLEVEL*10)+100"; //$NON-NLS-1$
		}
		else if (aRange.equalsIgnoreCase("LONG") && (aString == null))
		{
			aString = "(CASTERLEVEL*40)+400"; //$NON-NLS-1$
		}

		if (aString != null)
		{
			List<Ability> metaFeats = null;
			if (si != null)
			{
				metaFeats = si.getFeatList();
				rangeInFeet = getVariableValue(aSpell, aString, aSpellClass)
					.intValue();
			}
			if ((metaFeats != null) && !metaFeats.isEmpty())
			{
				for (Ability feat : metaFeats)
				{
					rangeInFeet += (int) feat.bonusTo("SPELL", "RANGE", this,
						this);

					final int iMult = (int) feat.bonusTo("SPELL", "RANGEMULT",
						this, this);

					if (iMult > 0)
					{
						rangeInFeet = (rangeInFeet * iMult);
					}
				}
			}

			aRange += (" ("
				+ Globals.getGameModeUnitSet().displayDistanceInUnitSet(
					rangeInFeet)
				+ Globals.getGameModeUnitSet().getDistanceUnit() + ")");
		}
		else
		{
			aRange = parseSpellString(aSpell, aRange, owner);
		}

		return aRange;
	}

	/**
	 * Computes the Caster Level for a Class
	 * 
	 * @param aSpell
	 * @param aName
	 * @return caster level for spell
	 */
	public int getCasterLevelForSpell(final Spell aSpell, final String aName)
	{
		final String aSpellClass = "CLASS:" + aName;
		int casterLevel = getVariableValue(aSpell, "CASTERLEVEL", aSpellClass)
			.intValue();

		return casterLevel;
	}

	/**
	 * Computes the Caster Level for a Class
	 * 
	 * @param aClass
	 * @return class caster level
	 */
	public int getClassCasterLevel(final PCClass aClass)
	{
		final int casterLevel = getVariableValue("CASTERLEVEL",
			"CLASS:" + aClass.getKeyName()).intValue();
		return casterLevel;
	}

	/**
	 * Computes the Caster Level for a race
	 * 
	 * @param aRace
	 * @return race caster level
	 */
	public int getRaceCasterLevel(final Race aRace)
	{
		final int casterLevel = getVariableValue("CASTERLEVEL",
			"RACE:" + aRace.getKeyName()).intValue();
		return casterLevel;
	}

	/**
	 * Calculates total bonus from all stats
	 * 
	 * @param aType
	 * @param aName
	 * @return stat bonus to
	 */
	public double getStatBonusTo(String aType, String aName)
	{
		final List<BonusObj> aList = statList.getBonusListOfType(aType
			.toUpperCase(), aName.toUpperCase());

		return calcBonusFromList(aList);
	}

	/**
	 * return bonus from Temporary Bonuses
	 * 
	 * @param aType
	 * @param aName
	 * @return temp bonus to
	 */
	public double getTempBonusTo(String aType, String aName)
	{
		double bonus = 0;

		if (!getUseTempMods())
		{
			return bonus;
		}

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		for (BonusObj bonusObj : getTempBonusList())
		{
			final String bString = bonusObj.toString();

			if ((bString.indexOf(aType) < 0) || (bString.indexOf(aName) < 0))
			{
				continue;
			}

			final Object tarObj = bonusObj.getTargetObject();
			final Object creObj = bonusObj.getCreatorObject();

			if ((creObj == null) || (tarObj == null))
			{
				continue;
			}

			if (!(creObj instanceof PObject)
				|| !(tarObj instanceof PlayerCharacter))
			{
				continue;
			}

			final PlayerCharacter bPC = (PlayerCharacter) tarObj;

			if (bPC != this)
			{
				continue;
			}

			final PObject aCreator = (PObject) creObj;
			bonus += aCreator.calcBonusFrom(bonusObj, this, this);
		}

		return bonus;
	}

	/**
	 * Parses through all templates to calculate total bonus
	 * 
	 * @param aType
	 * @param aName
	 * @param subSearch
	 * @return template bonus to
	 */
	public double getTemplateBonusTo(String aType, String aName,
		final boolean subSearch)
	{
		return getPObjectWithCostBonusTo(templateList, aType.toUpperCase(),
			aName.toUpperCase(), subSearch);
	}

	/**
	 * Get the total bonus from Stats, Size, Age, Alignment, Classes,
	 * companions, Equipment, Feats, Templates, Domains, Races, etc This value
	 * is taken from an already populated HashMap for speed
	 * 
	 * @param bonusType
	 *            Type of bonus ("COMBAT" or "SKILL")
	 * @param bonusName
	 *            Name of bonus ("AC" or "Hide");
	 * @return total bonus to
	 */
	public double getTotalBonusTo(final String bonusType, final String bonusName)
	{
		final String prefix = new StringBuffer(bonusType).append('.').append(
			bonusName).toString();

		return sumActiveBonusMap(prefix);
	}

	// public List<TypedBonus> getBonusesTo(final String bonusType, final String
	// bonusName)
	// {
	// final String prefix = new
	// StringBuffer(bonusType).append('.').append(bonusName).toString();
	//
	// final List<TypedBonus> ret = theBonusMap.get(prefix);
	// if ( ret == null )
	// {
	// return Collections.emptyList();
	// }
	//		
	// return ret;
	// }

	public int getTotalLevels()
	{
		int totalLevels = 0;

		totalLevels += totalNonMonsterLevels();

		// Monster hit dice count towards total levels -- was
		// totalMonsterLevels()
		// sage_sam changed 03 Dec 2002 for Bug #646816
		totalLevels += totalHitDice();

		return totalLevels;
	}

	public int getTotalPlayerLevels()
	{
		int totalLevels = 0;

		totalLevels += totalNonMonsterLevels();

		return totalLevels;
	}

	/**
	 * Get the value of the desired stat at the point just before the character
	 * was raised to the next level.
	 * 
	 * @param statAbb
	 *            The short name of the stat to calculate the value of.
	 * @param level
	 *            The level we want to see the stat at.
	 * @param includePost
	 *            Should stat mods that occurred after levelling be included?
	 * @return The stat as it was at the level
	 */
	public int getTotalStatAtLevel(final String statAbb, final int level,
		final boolean includePost)
	{
		int curStat = getStatList().getTotalStatFor(statAbb);
		for (int idx = getLevelInfoSize() - 1; idx >= level; --idx)
		{
			final int statLvlAdjust = pcLevelInfo.get(idx).getTotalStatMod(
				statAbb, true);
			curStat -= statLvlAdjust;
		}
		// If the user doesn't want POST changes, we remove any made in the
		// target level only
		if (!includePost && level > 0)
		{
			int statLvlAdjust = pcLevelInfo.get(level - 1).getTotalStatMod(
				statAbb, true);
			statLvlAdjust -= pcLevelInfo.get(level - 1).getTotalStatMod(
				statAbb, false);
			curStat -= statLvlAdjust;

		}

		return curStat;
	}

	public int getTwoHandDamageDivisor()
	{
		int div = getVariableValue("TWOHANDDAMAGEDIVISOR",
			Constants.EMPTY_STRING).intValue();

		if (div == 0)
		{
			div = 2;
		}

		return div;
	}

	/**
	 * Get the unarmed damage string for this PC as adjusted by the booleans
	 * passed in.
	 * 
	 * @param includeCrit
	 * @param includeStrBonus
	 * @param adjustForPCSize
	 * @return the unarmed damage string
	 */
	public String getUnarmedDamageString(final boolean includeCrit,
		final boolean includeStrBonus, final boolean adjustForPCSize)
	{
		String retString = "2|1d2";

		for (PCClass pcClass : classList)
		{
			retString = PlayerCharacterUtilities.getBestUDamString(retString,
				pcClass.getUdamForLevel(pcClass.getLevel(), includeCrit,
					includeStrBonus, this, adjustForPCSize));
		}

		for (PObject pObj : getPObjectList())
		{
			if (pObj == null || pObj instanceof PCClass)
			{
				continue;
			}
			retString = PlayerCharacterUtilities.getBestUDamString(retString,
				pObj.getUdamFor(includeCrit, includeStrBonus, this));
		}

		// string is in form sides|damage, just return damage portion
		return retString.substring(retString.indexOf('|') + 1);
	}

	public boolean getUseMasterSkill()
	{
		for (CompanionMod cMod : companionModList)
		{
			if (cMod.getType().equalsIgnoreCase(getMaster().getType()))
			{
				if (cMod.getUseMasterSkill())
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * whether we should use/save Temporary bonuses
	 * 
	 * @param aBool
	 */
	public void setUseTempMods(final boolean aBool)
	{
		useTempMods = aBool;
		// commented out setDirty because this causes a re-load of all tabs
		// every time any tab is viewed! merton_monk
		// setDirty(true);
	}

	public boolean getUseTempMods()
	{
		return useTempMods;
	}

	/**
	 * Evaluates a variable for this character e.g:
	 * getVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 * 
	 * @param aString
	 *            The variable to be evaluated
	 * @param src
	 *            The source within which the variable is evaluated
	 * @return The value of the variable
	 */
	public Float getVariableValue(final String aString, final String src)
	{
		return getVariableValue(null, aString, src);
	}

	public Float getVariableValue(final String varName, final String src,
		final PlayerCharacter aPC)
	{
		return getVariableValue(null, varName, src);
	}

	/**
	 * Evaluates a variable for this character e.g:
	 * getVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 * 
	 * @param aSpell
	 *            This is specifically to compute bonuses to CASTERLEVEL for a
	 *            specific spell.
	 * @param aString
	 *            The variable to be evaluated
	 * @param src
	 *            The source within which the variable is evaluated
	 * @return The value of the variable
	 */
	private Float getVariableValue(final Spell aSpell, String aString,
		String src)
	{
		VariableProcessor vp = getVariableProcessor();
		return vp.getVariableValue(aSpell, aString, src, getSpellLevelTemp());
	}

	/**
	 * @return VariableProcessor
	 */
	public VariableProcessor getVariableProcessor()
	{
		return variableProcessor;
	}

	int getTotalCasterLevelWithSpellBonus(final Spell aSpell,
		final String spellType, final String classOrRace, final int casterLev)
	{
		if (aSpell != null && aSpell.getFixedCasterLevel() != null)
		{
			return getVariableValue(aSpell.getFixedCasterLevel(),
				Constants.EMPTY_STRING).intValue();
		}

		int tBonus = casterLev;
		boolean replaceCasterLevel = false;

		String tType;
		String tStr;
		// final List<TypedBonus> bonuses = new ArrayList<TypedBonus>();
		final List<CasterLevelSpellBonus> bonuses = new ArrayList<CasterLevelSpellBonus>();

		if (classOrRace != null)
		{
			// bonuses.addAll(getBonusesTo("CASTERLEVEL", classOrRace));
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", classOrRace);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", classOrRace);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}

			// Support both types of syntax for CLASS:
			// BONUS:CASTERLEVEL|Sorcerer|1 and
			// BONUS:CASTERLEVEL|CLASS.Sorcerer|1
			if (!classOrRace.startsWith("RACE."))
			{
				tStr = "CLASS." + classOrRace;
				// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
				tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
				if (tBonus > 0)
				{
					tType = getSpellBonusType("CASTERLEVEL", tStr);
					bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
				}
			}
		}

		if (aSpell == null)
		{
			return (tBonus);
		}

		if (!spellType.equals(Constants.s_NONE))
		{
			tStr = "TYPE." + spellType;
			// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			// final List<TypedBonus> reset = getBonusesTo("CASTERLEVEL", tStr);
			// if ( reset.size() > 0 )
			// {
			// bonuses.addAll(reset);
			// replaceCasterLevel = true;
			// }
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		tStr = "SPELL." + aSpell.getKeyName();
		// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
		tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
		if (tBonus > 0)
		{
			tType = getSpellBonusType("CASTERLEVEL", tStr);
			bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
		}
		tStr += ".RESET";
		// final List<TypedBonus> reset = getBonusesTo("CASTERLEVEL", tStr);
		// if ( reset.size() > 0 )
		// {
		// bonuses.addAll(reset);
		// replaceCasterLevel = true;
		// }
		tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
		if (tBonus > 0)
		{
			replaceCasterLevel = true;
			tType = getSpellBonusType("CASTERLEVEL", tStr);
			bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
		}

		for (String school : aSpell.getSchools())
		{
			tStr = "SCHOOL." + school;
			// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			// final List<TypedBonus> reset1 = getBonusesTo("CASTERLEVEL",
			// tStr);
			// if ( reset.size() > 0 )
			// {
			// bonuses.addAll(reset1);
			// replaceCasterLevel = true;
			// }
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		for (String subschool : aSpell.getSubschools())
		{
			tStr = "SUBSCHOOL." + subschool;
			// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			// final List<TypedBonus> reset1 = getBonusesTo("CASTERLEVEL",
			// tStr);
			// if ( reset.size() > 0 )
			// {
			// bonuses.addAll(reset1);
			// replaceCasterLevel = true;
			// }
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		for (String desc : aSpell.getDescriptorList())
		{
			tStr = "DESCRIPTOR." + desc;
			// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			// final List<TypedBonus> reset1 = getBonusesTo("CASTERLEVEL",
			// tStr);
			// if ( reset.size() > 0 )
			// {
			// bonuses.addAll(reset1);
			// replaceCasterLevel = true;
			// }
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		final Map<String, Integer> domainMap = aSpell.getLevelInfo(this);
		if (domainMap != null)
		{
			for (String mKey : domainMap.keySet())
			{
				if (mKey.startsWith("DOMAIN|"))
				{
					tStr = "DOMAIN." + mKey.substring(7);
					// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
					tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
					if (tBonus > 0)
					{
						tType = getSpellBonusType("CASTERLEVEL", tStr);
						bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
					}
					tStr += ".RESET";
					// final List<TypedBonus> reset1 =
					// getBonusesTo("CASTERLEVEL", tStr);
					// if ( reset.size() > 0 )
					// {
					// bonuses.addAll(reset1);
					// replaceCasterLevel = true;
					// }
					tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
					if (tBonus > 0)
					{
						replaceCasterLevel = true;
						tType = getSpellBonusType("CASTERLEVEL", tStr);
						bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
					}
				}
			}
		}

		// now go through all bonuses, checking types to see what should add
		// together
		for (int z = 0; z < bonuses.size() - 1; z++)
		{
			final CasterLevelSpellBonus zBonus = bonuses.get(z);

			String zType = zBonus.getType();
			if ((zBonus.getBonus() == 0) || zType.equals(""))
			{
				continue;
			}

			boolean zReplace = false;
			boolean zStack = false;
			if (zType.endsWith(".REPLACE"))
			{
				zType = zType.substring(0, zType.length() - 8);
				zReplace = true;
			}
			else
			{
				if (zType.endsWith(".STACK"))
				{
					zType = zType.substring(0, zType.length() - 6);
					zStack = true;
				}
			}

			for (int k = z + 1; k < bonuses.size(); k++)
			{
				final CasterLevelSpellBonus kBonus = bonuses.get(k);

				String kType = kBonus.getType();
				if ((kBonus.getBonus() == 0) || kType.equals(""))
				{
					continue;
				}

				boolean kReplace = false;
				boolean kStack = false;
				if (kType.endsWith(".REPLACE"))
				{
					kType = kType.substring(0, kType.length() - 8);
					kReplace = true;
				}
				else
				{
					if (kType.endsWith(".STACK"))
					{
						kType = kType.substring(0, kType.length() - 6);
						kStack = true;
					}
				}

				if (!zType.equals(kType))
				{
					continue;
				}

				// if both end in ".REPLACE", add together and save for later
				// comparison
				if (zReplace && kReplace)
				{
					kBonus.setBonus(zBonus.getBonus() + kBonus.getBonus());
					zBonus.setBonus(0);
					continue;
				}

				// if either ends in ".STACK", then they will add
				if (zStack || kStack)
				{
					continue;
				}

				// otherwise, only keep max
				if (zBonus.getBonus() > kBonus.getBonus())
				{
					kBonus.setBonus(0);
				}
				else
				{
					zBonus.setBonus(0);
				}
			}
		}

		int result = 0;
		if (!replaceCasterLevel)
		{
			result += casterLev;
		}

		// result += TypedBonus.totalBonuses(bonuses);
		// Now go through bonuses and add it up
		for (CasterLevelSpellBonus resultBonus : bonuses)
		{
			result += resultBonus.getBonus();
		}

		if (result == 0)
		{
			result = 1;
		}

		return (result);
	}

	private String getSpellBonusType(final String bonusType,
		final String bonusName)
	{
		String prefix = new StringBuffer(bonusType).append('.').append(
			bonusName).toString();
		prefix = prefix.toUpperCase();

		for (String aKey : getActiveBonusMap().keySet())
		{
			String aString = aKey;

			// rString could be something like:
			// COMBAT.AC:Armor.REPLACE
			// So need to remove the .STACK or .REPLACE
			// to get a match for prefix like: COMBAT.AC:Armor
			if (aKey.endsWith(".STACK"))
			{
				aString = aKey.substring(0, aKey.length() - 6);
			}
			else if (aKey.endsWith(".REPLACE"))
			{
				aString = aKey.substring(0, aKey.length() - 8);
			}

			// if prefix is of the form:
			// COMBAT.AC
			// then it must match
			// COMBAT.AC
			// COMBAT.AC:Luck
			// COMBAT.AC:Armor.REPLACE
			// However, it must not match
			// COMBAT.ACCHECK
			if ((aString.length() > prefix.length())
				&& !aString.startsWith(prefix + ":"))
			{
				continue;
			}

			if (aString.startsWith(prefix))
			{
				final int typeIndex = aString.indexOf(":");
				if (typeIndex > 0)
				{
					return (aKey.substring(typeIndex + 1)); // use aKey to get
															// .REPLACE or
															// .STACK
				}
				return Constants.EMPTY_STRING; // no type;
			}

		}

		return Constants.EMPTY_STRING; // just return no type

	}

	public List<Vision> getVisionList()
	{
		List<Vision> visionList = new ArrayList<Vision>();

		for (PObject pObj : getPObjectList())
		{
			if (pObj != null)
			{
				visionList = addStringToVisionList(visionList, pObj.getVision());
			}
		}

		// parse through the global list of vision tags and see
		// if this PC has any BONUS:VISION tags which will create
		// a new visionMap entry
		for (VisionType vType : VisionType.getAllVisionTypes())
		{
			final int aVal = (int) getTotalBonusTo("VISION", vType.toString());

			if (aVal > 0)
			{
				// add a 0 value, as the bonus is added
				// in the addStringToVisionMap() routine
				final List<Vision> newList = new ArrayList<Vision>();
				newList.add(new Vision(vType, "0"));
				visionList = addStringToVisionList(visionList, newList);
			}
		}

		// CONSIDER Is this sort really necessary?
		if (visionList.size() > 1)
		{
			Collections.sort(visionList);
		}

		return visionList;
	}

	public String getVision()
	{
		final StringBuffer visionString = new StringBuffer();

		final List<Vision> visionList = getVisionList();
		for (Vision vision : visionList)
		{
			if (visionString.length() > 0)
			{
				visionString.append(", ");
			}

			visionString.append(vision);
		}

		return visionString.toString();
	}

	public int abilityAC()
	{
		return calcACOfType("Ability");
	}

	/**
	 * adds CharacterDomain to list
	 * 
	 * @param aCD
	 */
	public void addCharacterDomain(final CharacterDomain aCD)
	{
		if ((aCD != null) && !characterDomainList.contains(aCD)
			&& (aCD.getDomain() != null))
		{
			characterDomainList.add(aCD);
			final PCClass domainClass = getClassKeyed(aCD.getObjectName());
			if (domainClass != null)
			{
				final int _maxLevel = domainClass.getMaxCastLevel();
				aCD.getDomain().addSpellsToClassForLevels(domainClass, 0,
					_maxLevel);
			}
			setDirty(true);
		}
	}

	/**
	 * Sets the source of granted domains
	 * 
	 * @param aType
	 * @param aName
	 * @param aLevel
	 * @param dNum
	 */
	public void addDomainSource(final String aType, final String aName,
		final int aLevel, final int dNum)
	{
		final String aString = aType + Constants.PIPE + aName + Constants.PIPE
			+ aLevel;
		final String sNum = Integer.toString(dNum);
		domainSourceMap.put(aString, sNum);
		setDirty(true);
	}

	/**
	 * returns all equipment (from the equipmentList) of type aString
	 * 
	 * @param aList
	 * @param aType
	 * @return List
	 */
	public List<Equipment> addEqType(final List<Equipment> aList,
		final String aType)
	{
		for (Equipment eq : getEquipmentList())
		{
			if (eq.typeStringContains(aType))
			{
				aList.add(eq);
				setDirty(true);
			}
			else if (aType.equalsIgnoreCase("CONTAINED")
				&& (eq.getParent() != null))
			{
				aList.add(eq);
				setDirty(true);
			}
		}

		return aList;
	}

	/**
	 * Adds a <tt>Kit</tt> to the applied list of kits for the character.
	 * 
	 * @param aKit
	 *            The <tt>Kit</tt> to add.
	 */
	public void addKit(final Kit aKit)
	{
		if (kitList == null)
		{
			kitList = new ArrayList<Kit>();
		}

		kitList.add(aKit);
		setDirty(true);
	}

	public void addLanguage(final Language aLang)
	{
		languages.add(aLang);
		setDirty(true);
	}

	public void addLanguageKeyed(final String aKey)
	{
		final Language aLang = Globals.getLanguageKeyed(aKey);

		if (aLang != null)
		{
			addLanguage(aLang);
		}
	}

	public void addNaturalWeapons(final List<Equipment> weapons)
	{
		equipmentListAddAll(weapons);
		EquipSet eSet = getEquipSetByIdPath("0.1");
		if (eSet != null)
		{
			for (Equipment eq : weapons)
			{
				addEquipToTarget(eSet, null, "", eq, null);
			}
		}
		setDirty(true);
	}

	public void addShieldProf(final String aProf)
	{
		if (!shieldProfList.contains(aProf))
		{
			//
			// Insert all types at the head of the list
			//
			if (aProf.startsWith("TYPE=") || aProf.startsWith("TYPE."))
			{
				shieldProfList.add(0, aProf);
			}
			else
			{
				shieldProfList.add(aProf);
			}
		}
	}

	public void addShieldProfs(final List<String> aList)
	{
		for (String prof : aList)
		{
			addShieldProf(prof);
		}
	}

	public Skill addSkill(final Skill addSkill)
	{
		Skill retSkill;

		//
		// First, check to see if skill is already in list
		//
		for (Skill skill : getSkillList())
		{
			if (skill.getKeyName().equals(addSkill.getKeyName()))
			{
				return skill;
			}
		}

		//
		// Skill not found, add to list
		//
		retSkill = addSkill.clone();
		getSkillList().add(retSkill);
		setDirty(true);

		if (!isImporting())
		{
			retSkill.globalChecks(this);
			calcActiveBonuses();
		}

		return retSkill;
	}

	/**
	 * @param acs
	 *            is the CharacterSpell object containing the spell which is to
	 *            be modified
	 * @param aFeatList
	 *            is the list of feats to be added to the SpellInfo object
	 * @param className
	 *            is the name of the class whose list of character spells will
	 *            be modified
	 * @param bookName
	 *            is the name of the book for the SpellInfo object
	 * @param spellLevel
	 *            is the original (unadjusted) level of the spell not including
	 *            feat adjustments
	 * @param adjSpellLevel
	 *            is the adjustedLevel (including feat adjustments) of this
	 *            spell, it may be higher if the user chooses a higher level.
	 * 
	 * @return an empty string on successful completion, otherwise the return
	 *         value indicates the reason the add function failed.
	 */
	public String addSpell(CharacterSpell acs, final List<Ability> aFeatList,
		final String classKey, final String bookName, final int adjSpellLevel,
		final int spellLevel)
	{
		if (acs == null)
		{
			return "Invalid parameter to add spell";
		}

		PCClass aClass = null;
		final Spell aSpell = acs.getSpell();

		if ((bookName == null) || (bookName.length() == 0))
		{
			return "Invalid spell list/book name.";
		}

		SpellBook spellBook = getSpellBookByName(bookName);
		if (spellBook == null)
		{
			return "Could not find spell list/book " + bookName;
		}

		if (classKey != null)
		{
			aClass = getClassKeyed(classKey);

			if ((aClass == null) && (classKey.lastIndexOf('(') >= 0))
			{
				aClass = getClassKeyed(classKey.substring(0,
					classKey.lastIndexOf('(')).trim());
			}
		}

		// If this is a spellbook, the class doesn't have to be one the PC has
		// already.
		if (aClass == null && spellBook.getType() == SpellBook.TYPE_SPELL_BOOK)
		{
			aClass = Globals.getClassKeyed(classKey);
			if ((aClass == null) && (classKey.lastIndexOf('(') >= 0))
			{
				aClass = Globals.getClassKeyed(classKey.substring(0,
					classKey.lastIndexOf('(')).trim());
			}
		}

		if (aClass == null)
		{
			return "No class keyed " + classKey;
		}

		if (!aClass.getMemorizeSpells()
			&& !bookName.equals(Globals.getDefaultSpellBook()))
		{
			return aClass.getDisplayName() + " can only add to "
				+ Globals.getDefaultSpellBook();
		}

		// Divine spellcasters get no bonus spells at level 0
		// TODO: allow classes to define how many bonus spells they get each
		// level!
		// int numSpellsFromSpecialty = aClass.getNumSpellsFromSpecialty();
		// if (spellLevel == 0 &&
		// "Divine".equalsIgnoreCase(aClass.getSpellType()))
		// {
		// numSpellsFromSpecialty = 0;
		// }
		// all the exists checks are done.

		// don't allow adding spells which are prohibited to known
		// or prepared lists
		// But if a spell is both prohibited and in a speciality
		// which can be the case for some spells, then allow it.
		if (spellBook.getType() != SpellBook.TYPE_SPELL_BOOK
			&& !acs.isSpecialtySpell() && aClass.isProhibited(aSpell, this))
		{
			return acs.getSpell().getDisplayName() + " is prohibited.";
		}

		// Now let's see if they should be able to add this spell
		// first check for known/cast/threshold
		final int known = aClass.getKnownForLevel(spellLevel, this);
		int specialKnown = 0;
		final int cast = aClass.getCastForLevel(adjSpellLevel, bookName, true,
			true, this);
		aClass.memorizedSpellForLevelBook(adjSpellLevel, bookName);

		final boolean isDefault = bookName
			.equals(Globals.getDefaultSpellBook());

		if (isDefault)
		{
			specialKnown = aClass.getSpecialtyKnownForLevel(spellLevel, this);
		}

		int numPages = 0;

		// known is the maximum spells that can be known this level
		// listNum is the current spells already memorized this level
		// cast is the number of spells that can be cast at this level
		// Modified this to use new availableSpells() method so you can "blow"
		// higher-level slots on
		// lower-level spells
		// in re BUG [569517]
		// sk4p 13 Dec 2002
		if (spellBook.getType() == SpellBook.TYPE_SPELL_BOOK)
		{
			// If this is a spellbook rather than known spells
			// or prepared spells, then let them add spells up to
			// the page limit of the book.
			setSpellLevelTemp(spellLevel);
			numPages = getVariableValue(acs.getSpell(),
				spellBook.getPageFormula(), "").intValue();
			// Check number of pages remaining in the book
			if (numPages + spellBook.getNumPagesUsed() > spellBook
				.getNumPages())
			{
				return "There are not enough pages left to add this spell to the spell book.";
			}
			spellBook.setNumPagesUsed(numPages + spellBook.getNumPagesUsed());
			spellBook.setNumSpells(spellBook.getNumSpells() + 1);
		}
		else if (!aClass.getMemorizeSpells()
			&& !availableSpells(adjSpellLevel, aClass, bookName, true, acs
				.isSpecialtySpell()))
		{
			String ret;
			int maxAllowed;
			// If this were a specialty spell, would there be room?
			if (!acs.isSpecialtySpell()
				&& availableSpells(adjSpellLevel, aClass, bookName, true, true))
			{
				ret = "Your remaining slot(s) must be filled with your speciality.";
				maxAllowed = known;
			}
			else
			{
				ret = "You can only learn " + (known + specialKnown)
					+ " spells for level " + adjSpellLevel
					+ "\nand there are no higher-level slots available.";
				maxAllowed = known + specialKnown;
			}
			int memTot = aClass.memorizedSpellForLevelBook(adjSpellLevel,
				bookName);
			int spellDifference = maxAllowed - memTot;
			if (spellDifference > 0)
			{
				ret += "\n"
					+ spellDifference
					+ " spells from lower levels are using slots for this level.";
			}
			return ret;
		}
		else if (aClass.getMemorizeSpells()
			&& !isDefault
			&& !availableSpells(adjSpellLevel, aClass, bookName, false, acs
				.isSpecialtySpell()))
		{
			String ret;
			int maxAllowed;
			if (!acs.isSpecialtySpell()
				&& availableSpells(adjSpellLevel, aClass, bookName, false, true))
			{
				ret = "Your remaining slot(s) must be filled with your speciality or domain.";
				maxAllowed = aClass.getCastForLevel(adjSpellLevel, bookName,
					false, true, this);
			}
			else
			{
				ret = "You can only prepare " + cast + " spells for level "
					+ adjSpellLevel
					+ "\nand there are no higher-level slots available.";
				maxAllowed = cast;
			}
			int memTot = aClass.memorizedSpellForLevelBook(adjSpellLevel,
				bookName);
			int spellDifference = maxAllowed - memTot;
			if (spellDifference > 0)
			{
				ret += "\n"
					+ spellDifference
					+ " spells from lower levels are using slots for this level.";
			}
			return ret;
		}

		// determine if this spell already exists
		// for this character in this book at this level
		SpellInfo si = null;
		final List<CharacterSpell> acsList = aClass.getSpellSupport()
			.getCharacterSpell(acs.getSpell(), bookName, adjSpellLevel);
		if (!acsList.isEmpty())
		{
			for (int x = acsList.size() - 1; x >= 0; x--)
			{
				final CharacterSpell c = acsList.get(x);
				if (!c.equals(acs))
				{
					acsList.remove(x);
				}
			}
		}
		final boolean isEmpty = acsList.isEmpty();
		if (!isEmpty)
		{
			// I am not sure why this code is set up like this but it is
			// bogus. I am trying to break as little as possible so if
			// I have one matching spell I will use it otherwise I will
			// use the passed in spell.
			if (acsList.size() == 1)
			{
				final CharacterSpell tcs = acsList.get(0);
				si = tcs
					.getSpellInfoFor(bookName, adjSpellLevel, -1, aFeatList);
			}
			else
			{
				si = acs
					.getSpellInfoFor(bookName, adjSpellLevel, -1, aFeatList);
			}
		}

		if (si != null)
		{
			// ok, we already known this spell, so if they are
			// trying to add it to the default spellBook, barf
			// otherwise increment the number of times memorised
			if (isDefault)
			{
				return "The Known Spells spellbook contains all spells of this level that you know. You cannot place spells in multiple times.";
			}
			si.setTimes(si.getTimes() + 1);
		}
		else
		{
			if (isEmpty
				&& !aClass.getSpellSupport().containsCharacterSpell(acs))
			{
				aClass.getSpellSupport().addCharacterSpell(acs);
			}
			si = acs.addInfo(adjSpellLevel, 1, bookName, aFeatList);

			//
			//
			if (Spell.hasPPCost())
			{
				final Spell theSpell = acs.getSpell();
				int ppCost = theSpell.getPPCost();
				for (Ability feat : aFeatList)
				{
					ppCost += (int) feat.bonusTo("PPCOST", theSpell
						.getKeyName(), this, this);
				}
				si.setActualPPCost(ppCost);
			}
		}
		// Set number of pages on the spell
		si.setNumPages(si.getNumPages() + numPages);
		setDirty(true);
		return "";
	}

	/**
	 * return value indicates if book was actually added or not
	 * 
	 * @param aName
	 * @return TRUE or FALSE
	 */
	public boolean addSpellBook(final String aName)
	{
		if (aName != null && (aName.length() > 0)
			&& !spellBooks.contains(aName))
		{
			return addSpellBook(new SpellBook(aName,
				SpellBook.TYPE_PREPARED_LIST));
		}

		return false;
	}

	/**
	 * return value indicates if book was actually added or not
	 * 
	 * @param book
	 * @return TRUE or FALSE
	 */
	public boolean addSpellBook(final SpellBook book)
	{

		if (book != null)
		{
			String aName = book.getName();
			if (!spellBooks.contains(aName))
			{
				spellBooks.add(aName);
				spellBookMap.put(aName, book);
				setDirty(true);

				return true;
			}
		}

		return false;
	}

	public PCTemplate addTemplate(final PCTemplate inTemplate)
	{
		return addTemplate(inTemplate, true);
	}

	public PCTemplate addTemplate(final PCTemplate inTemplate, boolean doChoose)
	{
		if (inTemplate == null)
		{
			return null;
		}

		// Don't allow multiple copies of template.
		// Given that we clone everything will this ever
		// evaluate to true ?
		if (templateList.contains(inTemplate))
		{
			return null;
		}

		// Search for a template with this name already
		// assigned to this character
		for (PCTemplate template : templateList)
		{
			if (template.getKeyName().equals(inTemplate.getKeyName()))
			{
				return null; // template with duplicate name
			}
		}

		// Add a clone of the template passed in
		int lockMonsterSkillPoints = 0; // this is what this value was before
										// adding this template
		for (PCClass pcClass : classList)
		{
			if (pcClass.isMonster())
			{
				lockMonsterSkillPoints = (int) getTotalBonusTo("MONSKILLPTS",
					"LOCKNUMBER");
				break;
			}
		}

		final PCTemplate inTmpl;
		try
		{
			inTmpl = inTemplate.clone();
			templateList.add(inTmpl);
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}

		// If we are importing these levels will have been saved with the
		// character so don't apply them again.
		if (!isImporting())
		{
			for (String modString : inTemplate.getLevelMods())
			{
				StringTokenizer tok = new StringTokenizer(modString, "|");
				while (tok.hasMoreTokens())
				{
					final String colString = tok.nextToken();
					if (colString.startsWith("ADD"))
					{
						final String classKey = tok.nextToken();
						final int level = getVariableValue(tok.nextToken(), "")
							.intValue();

						PCClass aClass = Globals.getClassKeyed(classKey);

						boolean tempShowHP = SettingsHandler
							.getShowHPDialogAtLevelUp();
						SettingsHandler.setShowHPDialogAtLevelUp(false);
						boolean tempFeatDlg = SettingsHandler
							.getShowFeatDialogAtLevelUp();
						SettingsHandler.setShowFeatDialogAtLevelUp(false);
						int tempChoicePref = SettingsHandler
							.getSingleChoicePreference();
						SettingsHandler
							.setSingleChoicePreference(Constants.CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT);

						incrementClassLevel(level, aClass, true, true);

						SettingsHandler
							.setSingleChoicePreference(tempChoicePref);
						SettingsHandler.setShowFeatDialogAtLevelUp(tempFeatDlg);
						SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
					}
				}
			}
		}
		this.setArmorProfListStable(false);
		List<String> l = inTmpl.getSafeListFor(ListKey.KITS);
		for (int i1 = 0; i1 < l.size(); i1++)
		{
			KitUtilities.makeKitSelections(0, l.get(i1), i1, this);
		}

		calcActiveBonuses();

		templateAutoLanguages.addAll(inTmpl
			.getSafeListFor(ListKey.AUTO_LANGUAGES));
		templateLanguages.addAll(inTmpl.getLanguageBonus());
		getAutoLanguages();
		addNaturalWeapons(inTmpl.getNaturalWeapons());

		inTmpl.chooseLanguageAutos(isImporting(), this);

		if (PlayerCharacterUtilities.canReassignTemplateFeats())
		{
			final List<String> templateFeats = inTmpl.feats(getTotalLevels(),
				totalHitDice(), this, true);

			for (int i = 0, x = templateFeats.size(); i < x; ++i)
			{
				AbilityUtilities.modFeatsFromList(this, null, templateFeats
					.get(i), true, false);
			}
		}
		else
		{
			setAutomaticAbilitiesStable(null, false);
			// setAutomaticFeatsStable(false);
		}

		List<String> templates;
		if (doChoose)
		{
			templates = inTmpl.getTemplates(isImporting(), this);
		}
		else
		{
			templates = inTmpl.templatesAdded();
		}
		for (int i = 0, x = templates.size(); i < x; ++i)
		{
			addTemplateKeyed(templates.get(i));
		}

		setQualifyListStable(false);

		if (!isImporting())
		{
			getSpellList();
			inTmpl.globalChecks(this);
			inTmpl.checkRemovals(this);
		}

		setAggregateAbilitiesStable(null, false);
		// setAutomaticFeatsStable(false);
		// setAggregateFeatsStable(false);
		getAutomaticAbilityList(AbilityCategory.FEAT);
		// rebuildFeatAggreagateList();

		calcActiveBonuses();
		int postLockMonsterSkillPoints; // this is what this value was before
										// adding this template
		boolean first = true;
		for (PCClass pcClass : classList)
		{
			if (pcClass.isMonster())
			{
				postLockMonsterSkillPoints = (int) getTotalBonusTo(
					"MONSKILLPTS", "LOCKNUMBER");

				if (postLockMonsterSkillPoints != lockMonsterSkillPoints
					&& postLockMonsterSkillPoints > 0)
				{
					for (PCLevelInfo pi : getLevelInfo())
					{
						final int newSkillPointsGained = pcClass
							.recalcSkillPointMod(this, pi.getLevel());
						if (pi.getClassKeyName().equals(pcClass.getKeyName()))
						{
							final int formerGained = pi.getSkillPointsGained();
							pi.setSkillPointsGained(newSkillPointsGained);
							pi.setSkillPointsRemaining(pi
								.getSkillPointsRemaining()
								+ newSkillPointsGained - formerGained);
							pcClass.setSkillPool(pcClass.getSkillPool(this)
								+ newSkillPointsGained - formerGained);
							setSkillPoints(getSkillPoints()
								+ newSkillPointsGained - formerGained);
						}
					}
				}
			}
			//
			// Recalculate HPs in case HD have changed.
			//
			if (!isImporting())
			{
				if (inTemplate.getHitDieLock().length() != 0)
				{
					for (int level = 1; level <= pcClass.getLevel(); level++)
					{
						int baseHD = pcClass.getBaseHitDie();
						if (baseHD != pcClass.getLevelHitDie(this, level))
						{
							// If the HD has changed from base reroll
							pcClass.rollHP(this, level, first);
						}
					}
				}
			}
			first = false;
		}

		// karianna bug 1184888
		adjustMoveRates();

		setDirty(true);

		return inTmpl;
	}

	public PCTemplate addTemplateKeyed(String templateKey)
	{
		if (templateKey == null)
		{
			return null;
		}

		if (templateKey.startsWith("CHOOSE:"))
		{
			templateKey = PCTemplate.chooseTemplate(null, templateKey
				.substring(7), true, this);
		}

		PCTemplate aTemplate = Globals.getTemplateKeyed(templateKey);

		if ((aTemplate == null) && templateKey.endsWith(".REMOVE"))
		{
			aTemplate = Globals.getTemplateKeyed(templateKey.substring(0,
				templateKey.length() - 7));
			removeTemplate(aTemplate);
		}
		else
		{
			addTemplate(aTemplate);
		}

		if (aTemplate == null)
		{
			System.err.println("Template not found: '" + templateKey + "'");
		}
		setDirty(true);

		return aTemplate;
	}

	public void addWeaponProf(final String aProfKey)
	{
		final WeaponProf wp = Globals.getWeaponProfKeyed(aProfKey);
		if (wp != null)
		{
			// weaponProfList.add(wp);
			if (theWeaponProfs == null)
			{
				theWeaponProfs = new TreeSet<WeaponProf>();
			}
			theWeaponProfs.add(wp);
			setDirty(true);
		}
	}

	public void adjustGold(final double delta)
	{
		// I don't really like this hack, but setScale just won't work right...
		gold = new BigDecimal(gold.doubleValue() + delta).divide(BigDecimal.ONE, 2,
			BigDecimal.ROUND_HALF_EVEN);
		setDirty(true);
	}

	/**
	 * recalculate all the move rates and modifiers
	 */
	public void adjustMoveRates()
	{
		movements = null;
		movementTypes = null;
		movementMult = null;
		movementMultOp = null;

		if (getRace() == null)
		{
			return;
		}

		Movement movement = getRace().getMovement();
		if (movement == null || (!movement.isInitialized()))
		{
			return;
		}

		movements = movement.getMovements();
		movementTypes = movement.getMovementTypes();
		movementMult = movement.getMovementMult();
		movementMultOp = movement.getMovementMultOp();

		setMoveFromList(getPObjectList());

		// temp mods
		// TODO This would never do anything since setMoveFromList only
		// handles PObjects
		// if (!getTempBonusList().isEmpty() && getUseTempMods())
		// {
		// setMoveFromList(getTempBonusList());
		// }

		// Need to create movement entries if there is a BONUS:MOVEADD
		// associated with that type of movement
		for (final BonusObj bonus : getActiveBonusList())
		// for ( final BonusObj bonus : getAllActiveBonuses() )
		{
			if (bonus.getTypeOfBonus().equals("MOVEADD"))
			{
				String moveType = bonus.getBonusInfo();

				if (moveType.startsWith("TYPE"))
				{
					moveType = moveType.substring(5);
				}

				moveType = CoreUtility.capitalizeFirstLetter(moveType);

				boolean found = false;

				for (int i = 0; i < movements.length; i++)
				{
					if (moveType.equals(movementTypes[i]))
					{
						found = true;
					}
				}

				if (!found)
				{
					setMyMoveRates(moveType, 0.0, Double.valueOf(0.0), "", 1);
				}
			}
		}
		setDirty(true);
	}

	public List<Spell> aggregateSpellList(final String aType,
		final String school, final String subschool, final String descriptor,
		final int minLevel, final int maxLevel)
	{
		final List<Spell> retList = new ArrayList<Spell>();

		for (PCClass pcClass : classList)
		{
			String cName = pcClass.getKeyName();

			if (pcClass.getCastAs().length() > 0)
			{
				cName = pcClass.getCastAs();
			}

			if ("Any".equalsIgnoreCase(aType)
				|| aType.equalsIgnoreCase(pcClass.getSpellType())
				|| aType.equalsIgnoreCase(cName))
			{
				for (int a = minLevel; a <= maxLevel; a++)
				{
					final List<CharacterSpell> aList = pcClass
						.getSpellSupport().getCharacterSpell(null, "", a);

					for (CharacterSpell cs : aList)
					{
						final Spell aSpell = cs.getSpell();

						if ((((school.length() == 0) || aSpell.getSchools()
							.contains(school))
							|| ((subschool.length() == 0) || aSpell
								.getSubschools().contains(subschool)) || ((descriptor
							.length() == 0) || aSpell
							.descriptorContains(descriptor))))
						{
							retList.add(aSpell);
						}
					}
				}
			}
		}

		return retList;
	}

	public int altHP()
	{
		final int i = (int) getTotalBonusTo("HP", "ALTHP");

		return i;
	}

	public int baseAC()
	{
		return calcACOfType("Base");
	}

	/**
	 * @return Total base attack bonus as an int
	 */
	public int baseAttackBonus()
	{
		final String cacheLookup = "BaseAttackBonus";
		final Float total = getVariableProcessor().getCachedVariable(
			cacheLookup);
		if (total != null)
		{
			return total.intValue();
		}

		final PlayerCharacter nPC = getMasterPC();

		if ((nPC != null) && (getCopyMasterBAB().length() > 0))
		{
			int masterBAB = nPC.baseAttackBonus();
			final String copyMasterBAB = replaceMasterString(
				getCopyMasterBAB(), masterBAB);
			masterBAB = getVariableValue(copyMasterBAB, "").intValue();

			getVariableProcessor().addCachedVariable(cacheLookup,
				Float.valueOf(masterBAB));
			return masterBAB;
		}

		final int totalClassLevels = getTotalCharacterLevel();
		Map<String, String> totalLvlMap = null;
		final Map<String, String> classLvlMap;

		if (totalClassLevels > SettingsHandler.getGame().getBabMaxLvl())
		{
			totalLvlMap = getTotalLevelHashMap();
			classLvlMap = getCharacterLevelHashMap(SettingsHandler.getGame()
				.getBabMaxLvl());

			// insure total class-levels below some value (e.g. 20)
			getVariableProcessor().pauseCache();
			setClassLevelsBrazenlyTo(classLvlMap);
		}

		final int bab = (int) getTotalBonusTo("COMBAT", "BAB");

		if (totalLvlMap != null)
		{
			setClassLevelsBrazenlyTo(totalLvlMap);
			getVariableProcessor().restartCache();
		}

		getVariableProcessor().addCachedVariable(cacheLookup, Float.valueOf(bab));
		return bab;
	}

	/**
	 * get the base MOVE: plus any bonuses from BONUS:MOVE additions does not
	 * take into account Armor penalties to movement does not take into account
	 * penalties due to load carried
	 * 
	 * @param moveIdx
	 * @param load
	 * @return base movement
	 */
	public int basemovement(final int moveIdx, final Load load)
	{
		// get base movement
		final int move = getMovement(moveIdx).intValue();

		return move;
	}

	// TODO - Fix this to do 90% of the parsing work up front.
	public int calcACOfType(final String ACType)
	{
		final List<String> addList = SettingsHandler.getGame()
			.getACTypeAddString(ACType);
		final List<String> removeList = SettingsHandler.getGame()
			.getACTypeRemoveString(ACType);

		if ((addList == null) && (removeList == null))
		{
			Logging.errorPrint("Invalid ACType: " + ACType);

			return 0;
		}

		int AC = 0;

		if (addList != null)
		{
			// final List<TypedBonus> bonuses = new ArrayList<TypedBonus>();
			for (String aString : addList)
			{
				final PObject aPObj = new PObject();
				getPreReqFromACType(aString, aPObj);

				if (PrereqHandler.passesAll(aPObj.getPreReqList(), this, aPObj))
				{
					final StringTokenizer aTok = new StringTokenizer(aString,
						"|");
					AC += subCalcACOfType(aTok);
					// while ( aTok.hasMoreTokens() )
					// {
					// bonuses.addAll(
					// TypedBonus.getBonusesOfType(getBonusesTo("COMBAT", "AC"),
					// aTok.nextToken()) );
					//						
					// }
					// AC += TypedBonus.totalBonuses(bonuses);
				}
			}
		}

		if (removeList != null)
		{
			// final List<TypedBonus> bonuses = new ArrayList<TypedBonus>();
			for (String rString : removeList)
			{
				final PObject aPObj = new PObject();
				getPreReqFromACType(rString, aPObj);

				if (PrereqHandler.passesAll(aPObj.getPreReqList(), this, aPObj))
				{
					final StringTokenizer aTok = new StringTokenizer(rString,
						"|");
					AC -= subCalcACOfType(aTok);
					// while ( aTok.hasMoreTokens() )
					// {
					// bonuses.addAll(
					// TypedBonus.getBonusesOfType(getBonusesTo("COMBAT", "AC"),
					// aTok.nextToken()) );
					//						
					// }
					// AC -= TypedBonus.totalBonuses(bonuses);
				}
			}
		}

		return AC;
	}

	/**
	 * Creates the activeBonusList which is used to calculate all the bonuses to
	 * a PC
	 */
	public void calcActiveBonuses()
	{
		if (isImporting() || (race == null))
		{
			return;
		}

		// build the Variable HashSet
		buildVariableSet();

		// Keep rebuilding the active bonus map until the
		// contents do not change. This is to cope with the
		// situation where we have a variable A that has a prereq
		// that depends on variable B that will not be the correct
		// value until after the map has been completely created.

		// Get the original value of the map.
		// String origMapVal = theBonusMap.toString();
		String origMapVal = activeBonusMap.toString();

		// ensure that the values for the looked up variables are the most up to
		// date
		setDirty(true);
		calcActiveBonusLoop();

		// Get the new contents of the map
		// String mapVal = theBonusMap.toString();
		String mapVal = activeBonusMap.toString();

		// As the map is a TreeMap we know that the contents will be in
		// alphabetical order, so doing a straight string compare is
		// the easiest way to compare the whole tree.
		while (!mapVal.equals(origMapVal))
		{
			// If the newly calculated bonus map is different to the old one
			// loop again until they are the same.
			setDirty(true);
			calcActiveBonusLoop();
			origMapVal = mapVal;
			// mapVal = theBonusMap.toString();
			mapVal = activeBonusMap.toString();
		}
	}

	private List<BonusObj> getAllActiveBonuses()
	{
		List<BonusObj> ret = new ArrayList<BonusObj>();

		for (final PObject pobj : getPObjectList())
		{
			// We exclude equipmods here as their bonuses are already counted in
			// the equipment they belong to.
			if (pobj != null && !(pobj instanceof EquipmentModifier))
			{
				pobj.activateBonuses(this);

				// TODO - Class bonuses only get added if level is greater than
				// zero. Is this check required? Should it be part of
				// getPObjectList()?
				ret.addAll(pobj.getActiveBonuses(this));
			}
		}

		ret.addAll(getPurchaseModeBonuses());

		if (getUseTempMods())
		{
			ret.addAll(getTempBonuses());
		}
		ret = Bonus.sortBonusList(ret);
		return ret;
	}

	private void calcActiveBonusLoop()
	{
		final List<BonusObj> bonuses = getAllActiveBonuses();
		activeBonusList = bonuses;
		// buildBonusMap(bonuses);
		buildActiveBonusMap();
	}

	/**
	 * Calculate the Challenge Rating
	 * 
	 * @return CR
	 */
	public int calcCR()
	{
		int CR = 0;
		final int rhd = race.hitDice(this);

		if (rhd > 0)
		{
			float hitDieRatio = (float) totalHitDice() / rhd;

			while (hitDieRatio >= 2)
			{
				CR += 2;
				hitDieRatio /= 2;
			}

			if (hitDieRatio >= 1.5)
			{
				++CR;
			}
		}

		for (PCClass pcClass : classList)
		{
			CR += pcClass.calcCR(this);
		}

		for (PCTemplate template : templateList)
		{
			CR += template.getCR(getTotalLevels(), totalHitDice());
		}

		final int raceCR = race.getCR();

		if ((raceCR > 0) || (CR == 0))
		{
			CR += raceCR;
		}

		return CR;
	}

	/**
	 * Gets a list of all sources of DRs.
	 * 
	 * @return List of DRs
	 */
	public List<DamageReduction> getDRList()
	{
		List<DamageReduction> drList = new ArrayList<DamageReduction>();
		for (PObject obj : getPObjectList())
		{
			if (obj != null)
			{
				drList.addAll(obj.getDRList());
			}
		}
		return DamageReduction.getDRList(this, drList);
	}

	/**
	 * Get all possible sources of Damage Resistance and calculate
	 * 
	 * @return DR
	 */
	public String calcDR()
	{
		return DamageReduction.getDRString(this, getDRList());
	}

	public double calcMoveMult(final double move, final int index)
	{
		double iMove = 0;

		if (movementMultOp[index].charAt(0) == '*')
		{
			iMove = move * movementMult[index].doubleValue();
		}
		else if (movementMultOp[index].charAt(0) == '/')
		{
			iMove = move / movementMult[index].doubleValue();
		}

		if (iMove > 0)
		{
			return iMove;
		}

		return move;
	}

	public int calcSR(final boolean includeEquipment)
	{
		int SR = race.getSR(this);

		if (deity != null)
		{
			SR = Math.max(SR, deity.getSR(this));
		}

		for (CompanionMod cMod : companionModList)
		{
			SR = Math.max(SR, cMod.getSR(this));
		}

		for (PCClass pcClass : classList)
		{
			SR = Math.max(SR, pcClass.getSR(this));
		}

		for (Ability aFeat : aggregateFeatList())
		{
			SR = Math.max(SR, aFeat.getSR(this));
		}

		for (Skill skill : getSkillList())
		{
			SR = Math.max(SR, skill.getSR(this));
		}

		for (CharacterDomain cd : characterDomainList)
		{
			if (cd.getDomain() != null)
			{
				SR = Math.max(cd.getDomain().getSR(this), SR);
			}
		}

		if (includeEquipment)
		{
			for (Equipment eq : equipmentList)
			{
				if (eq.isEquipped())
				{
					SR = Math.max(SR, eq.getSR(this));

					for (EquipmentModifier eqMod : eq.getEqModifierList(true))
					{
						SR = Math.max(SR, eqMod.getSR(this));
					}

					for (EquipmentModifier eqMod : eq.getEqModifierList(false))
					{
						SR = Math.max(SR, eqMod.getSR(this));
					}
				}
			}
		}

		final int atl = getTotalLevels();
		final int thd = totalHitDice();

		for (PCTemplate template : templateList)
		{
			SR = Math.max(SR, template.getSR(atl, thd, this));
		}

		SR += (int) getTotalBonusTo("MISC", "SR");
		// SR += (int) getBonusValue("MISC", "SR");

		//
		// This would make more sense to just not add in the first place...
		//
		if (!includeEquipment)
		{
			SR -= (int) getEquipmentBonusTo("MISC", "SR");
		}

		return SR;
	}

	/**
	 * Method will go through the list of classes that the PC has and see if
	 * they can cast spells of desired type at desired <b>spell level</b>.
	 * 
	 * @param spellType
	 *            Spell type to check for
	 * @param spellLevel
	 *            Desired spell level
	 * @param minNumSpells
	 *            Minimum number of spells at the desired spell level
	 * @return boolean <p/> author David Wilson
	 *         <eldiosyeldiablo@users.sourceforge.net>
	 */
	public boolean canCastSpellTypeLevel(final String spellType,
		final int spellLevel, final int minNumSpells)
	{
		for (PCClass aClass : classList)
		{
			// Check for Constants.s_NONE just in case
			// a programmer sends in a "" string
			if (aClass.getSpellType().equalsIgnoreCase(spellType)
				&& !aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE))
			{
				// Get the number of known spells for the level
				int knownForLevel = aClass.getKnownForLevel(spellLevel, this);
				knownForLevel += aClass.getSpecialtyKnownForLevel(spellLevel,
					this);
				if (knownForLevel >= minNumSpells)
				{
					return true;
				}

				// See if the character can cast
				// at the required spell level
				if (aClass.getCastForLevel(spellLevel, this) >= minNumSpells)
				{
					return true;
				}

				// If they don't memorise spells and don't have
				// a CastList then they use something funky
				// like Power Points (psionic)
				if (!aClass.getMemorizeSpells() && !aClass.hasKnownList()
					&& aClass.zeroCastSpells())
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean canSelectDeity(final Deity aDeity)
	{
		if (aDeity == null)
		{
			return false;
		}

		return aDeity.canBeSelectedBy(classList, alignment, this);
	}

	public int classAC()
	{
		return calcACOfType("ClassDefense");
	}

	/**
	 * Return value indicates whether or not a spell was deleted.
	 * 
	 * @param si
	 * @param aClass
	 * @param bookName
	 * @return String
	 */
	public String delSpell(SpellInfo si, final PCClass aClass,
		final String bookName)
	{
		if ((bookName == null) || (bookName.length() == 0))
		{
			return "Invalid spell book name.";
		}

		if (aClass == null)
		{
			return "Error: Class is null";
		}

		final CharacterSpell acs = si.getOwner();

		final boolean isDefault = bookName
			.equals(Globals.getDefaultSpellBook());

		// yes, you can remove spells from the default spellbook,
		// but they will just get added back in when the character
		// is re-loaded. But, allow them to do it anyway, just in case
		// there is some weird spell that keeps getting loaded by
		// accident (or is saved in the .pcg file)
		if (isDefault
			&& aClass.isAutoKnownSpell(acs.getSpell().getKeyName(), si
				.getActualLevel(), this))
		{
			Logging.errorPrint("Notice: removing "
				+ acs.getSpell().getDisplayName()
				+ " even though it is an auto known spell");
		}

		SpellBook spellBook = getSpellBookByName(bookName);
		if (spellBook.getType() == SpellBook.TYPE_SPELL_BOOK)
		{
			int pagesPerSpell = si.getNumPages() / si.getTimes();
			spellBook.setNumPagesUsed(spellBook.getNumPagesUsed()
				- pagesPerSpell);
			spellBook.setNumSpells(spellBook.getNumSpells() - 1);
			si.setNumPages(si.getNumPages() - pagesPerSpell);
		}
		si.setTimes(si.getTimes() - 1);

		if (si.getTimes() <= 0)
		{
			acs.removeSpellInfo(si);
		}

		// Remove the spell form the character's class instance if it
		// is no longer present in any book
		aClass.getSpellSupport().removeSpellIfUnused(acs);

		return "";
	}

	/**
	 * Calculate different kinds of bonuses to saves. possible tokens are
	 * <ul>
	 * <li>save</li>
	 * <li>save.TOTAL</li>
	 * <li>save.BASE</li>
	 * <li>save.MISC</li>
	 * <li>save.list</li>
	 * <li>save.TOTAL.list</li>
	 * <li>save.BASE.list</li>
	 * <li>save.MISC.list</li>
	 * </ul>
	 * where<br />
	 * save := "CHECK1"|"CHECK2"|"CHECK3"<br />
	 * list := ((include|exclude)del)*(include|exclude)<br />
	 * include := "FEATS"|"MAGIC"|"RACE"<br />
	 * exclude := "NOFEATS"|"NOMAGIC"|"NORACE"|"NOSTAT" <br />
	 * del := "." <br />
	 * given as regular expression. <p/> "include"-s will add the appropriate
	 * modifier "exclude"-s will subtract the appropriate modifier <p/> (This
	 * means <tt>save.MAGIC.NOMAGIC</tt> equals 0, whereas
	 * <tt>save.RACE.RACE</tt> equals 2 times the racial bonus) <p/> If you
	 * use unrecognised terminals, their value will amount to 0 This means
	 * <tt>save.BLABLA</tt> equals 0 whereas <tt>save.MAGIC.BLABLA</tt>
	 * equals <tt>save.MAGIC</tt> <p/> <br>
	 * author: Thomas Behr 09-03-02
	 * 
	 * @param saveIndex
	 *            See the appropriate gamemode file
	 * @param saveType
	 *            "CHECK1", "CHECK2", or "CHECK3"; may not differ from
	 *            saveIndex!
	 * @param tokenString
	 *            tokenString to parse
	 * @return the calculated save bonus
	 */
	public int calculateSaveBonus(final int saveIndex, final String saveType,
		final String tokenString)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenString, ".");
		final String[] tokens = new String[aTok.countTokens()];
		final int checkIndex = SettingsHandler.getGame().getIndexOfCheck(
			saveType);
		int save = 0;

		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			tokens[i] = aTok.nextToken();

			if ("TOTAL".equals(tokens[i]))
			{
				save += getTotalCheck(checkIndex);
			}
			else if ("BASE".equals(tokens[i]))
			{
				save += getBaseCheck(checkIndex);
			}
			else if ("MISC".equals(tokens[i]))
			{
				save += (int) getTotalBonusTo("CHECKS", saveType);
			}

			if ("EPIC".equals(tokens[i]))
			{
				save += (int) getBonusDueToType("CHECKS", saveType, "EPIC");
			}

			if ("MAGIC".equals(tokens[i]))
			{
				save += (int) getEquipmentBonusTo("CHECKS", saveType);
			}

			if ("RACE".equals(tokens[i]))
			{
				save += calculateSaveBonusRace(saveIndex);
			}

			if ("FEATS".equals(tokens[i]))
			{
				save += (int) getFeatBonusTo("CHECKS", saveType, true);
			}

			if ("STATMOD".equals(tokens[i]))
			{
				save += (int) getCheckBonusTo("CHECKS", saveType);
			}

			/**
			 * exclude stuff
			 */
			if ("NOEPIC".equals(tokens[i]))
			{
				save -= (int) getBonusDueToType("CHECKS", saveType, "EPIC");
			}

			if ("NOMAGIC".equals(tokens[i]))
			{
				save -= (int) getEquipmentBonusTo("CHECKS", saveType);
			}

			if ("NORACE".equals(tokens[i]))
			{
				save -= calculateSaveBonusRace(saveIndex);
			}

			if ("NOFEATS".equals(tokens[i]))
			{
				save -= (int) getFeatBonusTo("CHECKS", saveType, true);
			}

			if ("NOSTAT".equals(tokens[i]) || "NOSTATMOD".equals(tokens[i]))
			{
				save -= (int) getCheckBonusTo("CHECKS", saveType);
			}
		}

		return save;
	}

	/**
	 * return value indicates whether or not a book was actually removed
	 * 
	 * @param aName
	 * @return true or false
	 */
	public boolean delSpellBook(final String aName)
	{
		if ((aName.length() > 0)
			&& !aName.equals(Globals.getDefaultSpellBook())
			&& spellBooks.contains(aName))
		{
			return delSpellBook(spellBookMap.get(aName));
		}

		return false;
	}

	/**
	 * return value indicates whether or not a book was actually removed
	 * 
	 * @param book
	 * @return true or false
	 */
	public boolean delSpellBook(final SpellBook book)
	{
		if (book != null)
		{
			String aName = book.getName();
			if (!aName.equals(Globals.getDefaultSpellBook())
				&& spellBooks.contains(aName))
			{
				spellBooks.remove(aName);
				spellBookMap.remove(aName);
				setDirty(true);

				for (PCClass pcClass : classList)
				{
					final List<CharacterSpell> aList = pcClass
						.getSpellSupport().getCharacterSpell(null, aName, -1);

					for (int j = aList.size() - 1; j >= 0; --j)
					{
						final CharacterSpell cs = aList.get(j);
						cs.removeSpellInfo(cs.getSpellInfoFor(aName, -1, -1));
					}
				}

				return true;
			}
		}

		return false;
	}

	public void determinePrimaryOffWeapon()
	{
		primaryWeapons.clear();
		secondaryWeapons.clear();

		if (equipmentList.isEmpty())
		{
			return;
		}

		final List<Equipment> unequippedPrimary = new ArrayList<Equipment>();
		final List<Equipment> unequippedSecondary = new ArrayList<Equipment>();

		for (Equipment eq : equipmentList)
		{
			if (!eq.isWeapon() || (eq.getSlots(this) < 1))
			{
				continue;
			}

			final boolean isEquipped = eq.isEquipped();

			if ((eq.getLocation() == Equipment.EQUIPPED_PRIMARY)
				|| ((eq.getLocation() == Equipment.EQUIPPED_BOTH) && primaryWeapons
					.isEmpty())
				|| (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS))
			{
				if (isEquipped)
				{
					primaryWeapons.add(eq);
				}
				else
				{
					unequippedPrimary.add(eq);
				}
			}
			else if ((eq.getLocation() == Equipment.EQUIPPED_BOTH)
				&& !primaryWeapons.isEmpty())
			{
				if (isEquipped)
				{
					secondaryWeapons.add(eq);
				}
				else
				{
					unequippedSecondary.add(eq);
				}
			}

			if (eq.getLocation() == Equipment.EQUIPPED_SECONDARY)
			{
				if (isEquipped)
				{
					secondaryWeapons.add(eq);
				}
				else
				{
					unequippedSecondary.add(eq);
				}
			}

			if (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS)
			{
				for (int y = 0; y < (eq.getNumberEquipped() - 1); ++y)
				{
					if (isEquipped)
					{
						secondaryWeapons.add(eq);
					}
					else
					{
						unequippedSecondary.add(eq);
					}
				}
			}
		}

		if (Globals.checkRule(RuleConstants.EQUIPATTACK))
		{
			if (unequippedPrimary.size() != 0)
			{
				primaryWeapons.addAll(unequippedPrimary);
			}

			if (unequippedSecondary.size() != 0)
			{
				secondaryWeapons.addAll(unequippedSecondary);
			}
		}
	}

	public int dodgeAC()
	{
		return calcACOfType("Dodge");
	}

	public int equipmentAC()
	{
		return calcACOfType("Equipment") + calcACOfType("Armor");
	}

	public int flatfootedAC()
	{
		return calcACOfType("Flatfooted");
	}

	/**
	 * Checks for existence of source in domainSourceMap
	 * 
	 * @param aType
	 * @param aName
	 * @param aLevel
	 * @return TRUE if it has a domain source
	 */
	public boolean hasDomainSource(final String aType, final String aName,
		final int aLevel)
	{
		final String aKey = aType + "|" + aName + "|" + aLevel;

		return domainSourceMap.containsKey(aKey);
	}

	/**
	 * Check if the character has the feat 'automatically'
	 * 
	 * @param featName
	 *            String name of the feat to check for.
	 * @return <code>true</code> if the character has the feat,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasFeatAutomatic(final String featName)
	{
		return AbilityUtilities.getAbilityFromList(featAutoList(),
			Constants.FEAT_CATEGORY, featName, Ability.Nature.ANY) != null;
	}

	/**
	 * Check if the character has the feat 'virtually'
	 * 
	 * @param featName
	 *            String name of the feat to check for.
	 * @return <code>true</code> if the character has the feat,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasFeatVirtual(final String featName)
	{
		return AbilityUtilities.getAbilityFromList(getVirtualFeatList(),
			Constants.FEAT_CATEGORY, featName, Ability.Nature.ANY) != null;
	}

	/**
	 * Does the character have this ability as an auto ability.
	 * 
	 * @param aCategory
	 *            The ability category to check.
	 * @param anAbility
	 *            The Ability object to check
	 * 
	 * @return <tt>true</tt> if the character has the ability
	 */
	public boolean hasAutomaticAbility(final AbilityCategory aCategory,
		final Ability anAbility)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return hasFeatAutomatic(anAbility.getKeyName());
		}
		final List<Ability> abilities = theAbilities.get(aCategory,
			Ability.Nature.AUTOMATIC);
		if (abilities == null)
		{
			return false;
		}
		return abilities.contains(anAbility);
	}

	/**
	 * Does the character have this ability as a virtual ability.
	 * 
	 * @param aCategory
	 *            The ability category to check.
	 * @param anAbility
	 *            The Ability object to check
	 * 
	 * @return <tt>true</tt> if the character has the ability
	 */
	public boolean hasVirtualAbility(final AbilityCategory aCategory,
		final Ability anAbility)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return hasFeatVirtual(anAbility.getKeyName());
		}
		final List<Ability> abilities = theAbilities.get(aCategory,
			Ability.Nature.VIRTUAL);
		if (abilities == null)
		{
			return false;
		}
		return abilities.contains(anAbility);
	}

	public boolean hasMadeKitSelectionForAgeSet(final int index)
	{
		return ((index >= 0) && (index < 10) && ageSetKitSelections[index]);
	}

	public boolean hasSpecialAbility(final String abilityKey)
	{
		for (SpecialAbility sa : getSpecialAbilityList())
		{
			if (sa.getKeyName().equalsIgnoreCase(abilityKey))
			{
				return true;
			}
		}

		return false;
	}

	public int hitPoints()
	{
		int total = 0;

		String aString = Globals.getGameModeHPFormula();
		if (aString.length() != 0)
		{
			for (;;)
			{
				int startIdx = aString.indexOf("$$");
				if (startIdx < 0)
				{
					break;
				}
				int endIdx = aString.indexOf("$$", startIdx + 2);
				if (endIdx < 0)
				{
					break;
				}

				String lookupString = aString.substring(startIdx + 2, endIdx);
				lookupString = pcgen.io.ExportHandler.getTokenString(this,
					lookupString);
				aString = aString.substring(0, startIdx) + lookupString
					+ aString.substring(endIdx + 2);
			}
			total = getVariableValue(aString, "").intValue();
		}
		else
		{
			final double iConMod = getStatBonusTo("HP", "BONUS");

			if (race.hitDice(this) != 0)
			{
				total = race.calcHitPoints((int) iConMod);
			}

			for (PCClass pcClass : classList)
			{
				total += pcClass.hitPoints((int) iConMod);
			}

		}
		total += (int) getTotalBonusTo("HP", "CURRENTMAX");

		//
		// now we see if this PC is a Familiar
		final PlayerCharacter nPC = getMasterPC();

		if (nPC == null)
		{
			return total;
		}

		if (getCopyMasterHP().length() == 0)
		{
			return total;
		}
		//
		// In order for the BONUS's to work, the PC we want
		// to get the hit points for must be the "current" one.
		//
		final PlayerCharacter curPC = this;
		Globals.setCurrentPC(nPC);

		int masterHP = nPC.hitPoints();
		Globals.setCurrentPC(curPC);

		final String copyMasterHP = replaceMasterString(getCopyMasterHP(),
			masterHP);
		masterHP = getVariableValue(copyMasterHP, "").intValue();

		return masterHP;
	}

	/**
	 * Check to see if this PC should ignore Encumbrance for a specified armor
	 * (Constants.HEAVY_LOAD, etc) If the check is more than the testing type,
	 * return true
	 * 
	 * @param armor
	 * @return true or false
	 */
	public boolean ignoreEncumberedArmorMove(final Load armor)
	{
		// Try all possible POBjects
		for (PObject pObj : getPObjectList())
		{
			if (pObj != null)
			{
				if (armor.checkLtEq(pObj.getEncumberedArmorMove()))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Check to see if this PC should ignore Encumbrance for a specified load
	 * (Constants.HEAVY_LOAD, etc) If the check is more than the testing type,
	 * return true
	 * 
	 * @param loadInt
	 * @return true or false
	 */
	public boolean ignoreEncumberedLoadMove(final Load load)
	{
		// Try all possible POBjects
		for (PObject pObj : getPObjectList())
		{
			if (pObj != null)
			{
				if (load.checkLtEq(pObj.getEncumberedLoadMove()))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Change the number of levels a character has in a particular class. Note:
	 * It is assumed that this method is not used as part of loading a
	 * previously saved character. there is no way to bypass the prerequisites
	 * with this method, also this method does not print warning messages see:
	 * incrementClassLevel(int, PCClass, boolean, boolean);
	 * 
	 * @param mod
	 *            the number of levels to add/remove
	 * @param aClass
	 *            the class to adjust
	 */
	public void incrementClassLevel(final int mod, final PCClass aClass)
	{
		incrementClassLevel(mod, aClass, false);
		setDirty(true);
	}

	/**
	 * TODO Why are we doing this, just add the domain
	 * 
	 * @return index
	 */
	public int indexOfFirstEmptyCharacterDomain()
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = characterDomainList.get(i);

			if (aCD.getDomain() == null)
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * Initiative Modifier
	 * 
	 * @return initiative modifier
	 */
	public int initiativeMod()
	{
		final int initmod = (int) getTotalBonusTo("COMBAT", "Initiative")
			+ getVariableValue("INITCOMP", "").intValue();

		return initmod;
	}

	public int languageNum(final boolean includeSpeakLanguage)
	{
		int i = (int) getStatBonusTo("LANG", "BONUS");
		final Race pcRace = getRace();

		if (i < 0)
		{
			i = 0;
		}

		if (includeSpeakLanguage)
		{
			for (Skill skill : getSkillList())
			{
				if (skill.getChoiceString().indexOf("Language") >= 0)
				{
					i += skill.getTotalRank(this).intValue();
				}
			}
		}

		if (pcRace != null)
		{
			i += (pcRace.getLangNum() + (int) getTotalBonusTo("LANGUAGES",
				"NUMBER"));
		}

		//
		// Check all classes for ADD:LANGUAGE
		//
		for (PCClass pcClass : classList)
		{
			final int classLevel = pcClass.getLevel();
			final List<LevelAbility> laList = pcClass.getLevelAbilityList();

			if (laList != null)
			{
				for (int x = laList.size() - 1; x >= 0; --x)
				{
					final LevelAbility la = laList.get(x);

					if (la.isLanguage() && classLevel >= la.level())
					{
						++i;
					}
				}
			}
		}

		i += freeLangs;

		return i;
	}

	/**
	 * Lists all the tokens that match prefix with associated values
	 * 
	 * @param prefix
	 * @return String TODO - Not sure what this is trying to do.
	 */
	public String listBonusesFor(final String prefix)
	{
		final StringBuffer buf = new StringBuffer();
		final List<String> aList = new ArrayList<String>();

		// final List<TypedBonus> bonuses = theBonusMap.get(prefix);
		// if ( bonuses == null )
		// {
		// return Constants.EMPTY_STRING;
		// }
		// final List<String> bonusStrings =
		// TypedBonus.totalBonusesByType(bonuses);
		// return CoreUtility.commaDelimit(bonusStrings);

		for (String aKey : getActiveBonusMap().keySet())
		{
			if (aKey.startsWith(prefix))
			{
				// make a list of keys that end with .REPLACE
				if (aKey.endsWith(".REPLACE"))
				{
					aList.add(aKey);
				}
				else
				{
					String reason = "";

					if (aKey.length() > prefix.length())
					{
						reason = aKey.substring(prefix.length() + 1);
					}

					final int b = (int) getActiveBonusForMapKey(aKey, 0);

					if (b == 0)
					{
						continue;
					}

					if (!"NULL".equals(reason) && (reason.length() > 0))
					{
						if (buf.length() > 0)
						{
							buf.append(", ");
						}
						buf.append(reason).append(' ');
					}
					buf.append(Delta.toString(b));
				}
			}
		}

		// Now adjust the bonus if the .REPLACE value
		// replaces the value without .REPLACE
		for (String replaceKey : aList)
		{
			if (replaceKey.length() > 7)
			{
				final String aKey = replaceKey.substring(0,
					replaceKey.length() - 8);
				final double replaceBonus = getActiveBonusForMapKey(replaceKey,
					0);
				double aBonus = getActiveBonusForMapKey(aKey, 0);
				aBonus += getActiveBonusForMapKey(aKey + ".STACK", 0);

				final int b = (int) Math.max(aBonus, replaceBonus);

				if (b == 0)
				{
					continue;
				}

				if (buf.length() > 0)
				{
					buf.append(", ");
				}

				final String reason = aKey.substring(prefix.length() + 1);

				if (!"NULL".equals(reason))
				{
					buf.append(reason).append(' ');
				}

				buf.append(Delta.toString(b));
			}
		}

		return buf.toString();
	}

	/*
	 */
	public boolean loadDescriptionFilesInDirectory(final String aDirectory)
	{
		new File(aDirectory).list(new FilenameFilter()
		{
			public boolean accept(final File parentDir, final String fileName)
			{
				final File descriptionFile = new File(parentDir, fileName);

				if (PCGFile.isPCGenListFile(descriptionFile))
				{
					BufferedReader descriptionReader = null;

					try
					{
						if (descriptionFile.exists())
						{
							final char[] inputLine;

							// final BufferedReader descriptionReader = new
							// BufferedReader(new FileReader(descriptionFile));
							descriptionReader = new BufferedReader(
								new InputStreamReader(new FileInputStream(
									descriptionFile), "UTF-8"));

							final int length = (int) descriptionFile.length();
							inputLine = new char[length];
							descriptionReader.read(inputLine, 0, length);
							setDescriptionLst(getDescriptionLst()
								+ new String(inputLine));
						}
					}
					catch (IOException exception)
					{
						Logging
							.errorPrint(
								"IOException in PlayerCharacter.loadDescriptionFilesInDirectory",
								exception);
					}
					finally
					{
						if (descriptionReader != null)
						{
							try
							{
								descriptionReader.close();
							}
							catch (IOException e)
							{
								Logging
									.errorPrint(
										"Couldn't close descriptionReader in PlayerCharacter.loadDescriptionFilesInDirectory",
										e);

								// Not much to do...
							}
						}
					}
				}
				else if (parentDir.isDirectory())
				{
					loadDescriptionFilesInDirectory(parentDir.getPath()
						+ File.separator + fileName);
				}

				return false;
			}
		});

		return false;
	}

	public void makeIntoExClass(final PCClass aClass)
	{
		final String exClass = aClass.getExClass();

		if (exClass.length() == 0)
		{
			return;
		}

		try
		{
			PCClass bClass = getClassKeyed(exClass);

			if (bClass == null)
			{
				bClass = Globals.getClassKeyed(exClass);

				if (bClass == null)
				{
					return;
				}

				bClass = bClass.clone();

				rebuildLists(bClass, aClass, aClass.getLevel(), this);

				bClass.setLevel(aClass.getLevel(), this);
				bClass.setHitPointMap(aClass.getHitPointMap());

				final int idx = classList.indexOf(aClass);
				classList.set(idx, bClass);
				// thePObjectList.remove(aClass);
				// thePObjectList.add(bClass);
			}
			else
			{
				rebuildLists(bClass, aClass, aClass.getLevel(), this);
				bClass.setLevel(bClass.getLevel() + aClass.getLevel(), this);

				for (int i = 0; i < aClass.getLevel(); ++i)
				{
					bClass.setHitPoint(bClass.getLevel() + i + 1, aClass
						.getHitPoint(i + 1));
				}

				classList.remove(aClass);
				// thePObjectList.remove(aClass);
			}

			//
			// change all the levelling info to the ex-class as well
			//
			for (int idx = pcLevelInfo.size() - 1; idx >= 0; --idx)
			{
				final PCLevelInfo li = pcLevelInfo.get(idx);

				if (li.getClassKeyName().equals(aClass.getKeyName()))
				{
					li.setClassKeyName(bClass.getKeyName());
				}
			}

			//
			// Find all skills associated with old class and link them to new
			// class
			//
			for (Skill skill : getSkillList())
			{
				skill.replaceClassRank(aClass.getKeyName(), exClass);
			}

			bClass.setSkillPool(aClass.getSkillPool(this));
		}
		catch (NumberFormatException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(),
				Constants.s_APPNAME, MessageType.INFORMATION);
		}
	}

	public int minXPForECL()
	{
		return PlayerCharacterUtilities.minXPForLevel(getECL(), this);
	}

	public int minXPForNextECL()
	{
		return PlayerCharacterUtilities.minXPForLevel(getECL() + 1, this);
	}

	public int miscAC()
	{
		return calcACOfType("Misc");
	}

	/*
	 */
	public int modFromArmorOnWeaponRolls()
	{
		int bonus = 0;

		/*
		 * Equipped some armor that we're not proficient in? acCheck penalty to
		 * attack rolls
		 */
		for (Equipment eq : getEquipmentOfType("Armor", 1))
		{
			if ((eq != null) && (!isProficientWith(eq)))
			{
				bonus += eq.acCheck(this).intValue();
			}
		}

		/*
		 * Equipped a shield that we're not proficient in? acCheck penalty to
		 * attack rolls
		 */
		for (Equipment eq : getEquipmentOfType("Shield", 1))
		{
			if ((eq != null) && (!isProficientWith(eq)))
			{
				bonus += eq.acCheck(this).intValue();
			}
		}

		return bonus;
	}

	/**
	 * Figure out if Load should affect AC and Skills, if so, set the load
	 * appropriately, otherwise set a light load to eliminate the effects of
	 * heavier loads
	 * 
	 * @return a loadType appropriate for this Pc
	 */
	private Load getLoadType()
	{
		if (Globals.checkRule(RuleConstants.SYS_LDPACSK))
		{
			final int loadScore = getVariableValue("LOADSCORE", "").intValue();
			return Globals.loadTypeForLoadScore(loadScore, totalWeight(), this);
		}
		return Load.LIGHT;
	}

	/**
	 * Calculate the AC bonus from equipped items. Extracted from
	 * modToFromEquipment.
	 * 
	 * @return PC's AC bonus from equipment
	 */
	private int modToACFromEquipment()
	{
		int bonus = 0;
		for (Equipment eq : equipmentList)
		{
			if (eq.isEquipped())
			{
				bonus += eq.getACMod(this).intValue();
			}
		}
		return bonus;
	}

	/**
	 * Calculate the ACCHECK bonus from equipped items. Extracted from
	 * modToFromEquipment.
	 * 
	 * @return PC's ACCHECK bonus from equipment
	 */
	private int modToACCHECKFromEquipment()
	{
		Load load = getLoadType();
		int bonus = 0;

		int penaltyForLoad = (Load.MEDIUM == load) ? -3
			: (Load.HEAVY == load) ? -6 : 0;

		for (Equipment eq : equipmentList)
		{
			if (eq.isEquipped())
			{
				bonus += eq.acCheck(this).intValue();
			}
		}

		bonus = Math.min(bonus, penaltyForLoad);
		bonus += (int) getTotalBonusTo("MISC", "ACCHECK");
		return bonus;
	}

	/**
	 * Calculate the SpellFailure bonus from equipped items. Extracted from
	 * modToFromEquipment.
	 * 
	 * @return PC's SpellFailure bonus from equipment
	 */
	private int modToSpellFailureFromEquipment()
	{
		int bonus = 0;
		for (Equipment eq : equipmentList)
		{
			if (eq.isEquipped())
			{
				bonus += eq.spellFailure(this).intValue();
			}
		}
		bonus += (int) getTotalBonusTo("MISC", "SPELLFAILURE");
		return bonus;
	}

	/**
	 * Calculate the MAXDEX bonus taking account of equipped items. Extracted
	 * from modToFromEquipment.
	 * 
	 * @return MAXDEX bonus
	 */
	private int modToMaxDexFromEquipment()
	{
		final int statBonus = (int) getStatBonusTo("MISC", "MAXDEX");
		final Load load = getLoadType();
		int bonus = (load == Load.MEDIUM) ? 3 : (load == Load.HEAVY) ? 1
			: (load == Load.OVERLOAD) ? 0 : statBonus;

		// If this is still true after all the equipment has been
		// examined, then we should use the Maximum - Maximum Dex modifier.
		boolean useMax = (load == Load.LIGHT);

		for (Equipment eq : equipmentList)
		{
			if (eq.isEquipped())
			{
				final int potentialMax = eq.getMaxDex(this).intValue();
				if (potentialMax != Constants.MAX_MAXDEX)
				{
					if (useMax || bonus > potentialMax)
					{
						bonus = potentialMax;
					}
					useMax = false;
				}
			}
		}

		if (useMax)
		{
			bonus = Constants.MAX_MAXDEX;
		}

		bonus += ((int) getTotalBonusTo("MISC", "MAXDEX") - statBonus);

		if (bonus < 0)
		{
			bonus = 0;
		}
		else if (bonus > Constants.MAX_MAXDEX)
		{
			bonus = Constants.MAX_MAXDEX;
		}
		return bonus;
	}

	/*
	 * Figure the: MAXDEX ACCHECK SPELLFAILURE AC bonus from all currently
	 * equipped items
	 */
	public int modToFromEquipment(final String typeName)
	{
		if (typeName.equals("AC"))
		{
			return modToACFromEquipment();
		}
		if (typeName.equals("ACCHECK"))
		{
			return modToACCHECKFromEquipment();
		}
		if (typeName.equals("MAXDEX"))
		{
			return modToMaxDexFromEquipment();
		}
		if (typeName.equals("SPELLFAILURE"))
		{
			return modToSpellFailureFromEquipment();
		}
		return 0;
	}

	/**
	 * get the base MOVE: plus any bonuses from BONUS:MOVE additions takes into
	 * account Armor restrictions to movement and load carried
	 * 
	 * @param moveIdx
	 * @return movement
	 */
	public double movement(final int moveIdx)
	{
		// get base movement
		double moveInFeet = getMovement(moveIdx).doubleValue();

		// First get the MOVEADD bonus
		moveInFeet += getTotalBonusTo("MOVEADD", "TYPE."
			+ getMovementType(moveIdx).toUpperCase());

		// also check for special case of TYPE=ALL
		moveInFeet += getTotalBonusTo("MOVEADD", "TYPE.ALL");

		double calcMove = moveInFeet;

		// now we apply any multipliers to the BASE move + MOVEADD move
		// First we get possible multipliers/divisors from the MOVE:
		// MOVEA: and MOVECLONE: tags
		if (getMovementMult(moveIdx).doubleValue() > 0)
		{
			calcMove = calcMoveMult(moveInFeet, moveIdx);
		}

		// Now we get the BONUS:MOVEMULT multipliers
		double moveMult = getTotalBonusTo("MOVEMULT", "TYPE."
			+ getMovementType(moveIdx).toUpperCase());

		// also check for special case of TYPE=ALL
		moveMult += getTotalBonusTo("MOVEMULT", "TYPE.ALL");

		if (moveMult > 0)
		{
			calcMove = (int) (calcMove * moveMult);
		}

		double postMove = moveInFeet;

		// now add on any POSTMOVE bonuses
		postMove += getTotalBonusTo("POSTMOVEADD", "TYPE."
			+ getMovementType(moveIdx).toUpperCase());

		// also check for special case of TYPE=ALL
		postMove += getTotalBonusTo("POSTMOVEADD", "TYPE.ALL");

		// because POSTMOVE is magical movement which should not be
		// multiplied by magical items, etc, we now see which is larger,
		// (baseMove + postMove) or (baseMove * moveMultiplier)
		// and keep the larger one, discarding the other
		moveInFeet = Math.max(calcMove, postMove);

		// get a list of all equipped Armor
		Load armorLoad = Load.LIGHT;

		for (Equipment armor : getEquipmentOfType("Armor", 1))
		{
			if (armor.isShield())
			{
				continue;
			}
			if (armor.isHeavy() && !ignoreEncumberedArmorMove(Load.HEAVY))
			{
				armorLoad = armorLoad.max(Load.HEAVY);
			}
			else if (armor.isMedium()
				&& !ignoreEncumberedArmorMove(Load.MEDIUM))
			{
				armorLoad = armorLoad.max(Load.MEDIUM);
			}
		}

		final double armorMove = Globals.calcEncumberedMove(armorLoad,
			moveInFeet, true, null);

		final Load pcLoad = Globals.loadTypeForLoadScore(getVariableValue(
			"LOADSCORE", "").intValue(), totalWeight(), this);
		final double loadMove = Globals.calcEncumberedMove(pcLoad, moveInFeet,
			true, this);

		// It is possible to have a PC that is not encumbered by Armor
		// But is encumbered by Weight carried (and visa-versa)
		// So do two calcs and take the slowest
		moveInFeet = Math.min(armorMove, loadMove);

		return moveInFeet;
	}

	public double multiclassXPMultiplier()
	{
		final SortedSet<String> unfavoredClasses = new TreeSet<String>();
		final SortedSet<String> aList = getFavoredClasses();
		boolean hasAny = false;
		String maxClass = "";
		String secondClass = "";
		int maxClassLevel = 0;
		int secondClassLevel = 0;
		int xpPenalty = 0;
		double xpMultiplier = 1.0;

		if (aList.contains("Any"))
		{
			hasAny = true;
		}

		for (PCClass pcClass : classList)
		{
			// TODO Fix this to use keys
			if (!aList.contains(pcClass.getDisplayClassName())
				&& (!aList.contains(pcClass.toString()))
				&& pcClass.hasXPPenalty())
			{
				unfavoredClasses.add(pcClass.getDisplayClassName());

				if (pcClass.getLevel() > maxClassLevel)
				{
					if (hasAny)
					{
						secondClassLevel = maxClassLevel;
						secondClass = maxClass;
					}

					maxClassLevel = pcClass.getLevel();
					maxClass = pcClass.getDisplayClassName();
				}
				else if ((pcClass.getLevel() > secondClassLevel) && (hasAny))
				{
					secondClassLevel = pcClass.getLevel();
					secondClass = pcClass.getDisplayClassName();
				}
			}
		}

		if ((hasAny) && (secondClassLevel > 0))
		{
			maxClassLevel = secondClassLevel;
			unfavoredClasses.remove(maxClass);
			maxClass = secondClass;
		}

		if (maxClassLevel > 0)
		{
			unfavoredClasses.remove(maxClass);

			for (String className : unfavoredClasses)
			{
				final PCClass aClass = getClassDisplayNamed(className);

				if (aClass != null)
				{
					if ((maxClassLevel - (aClass.getLevel())) > 1)
					{
						++xpPenalty;
					}
				}
			}

			xpMultiplier = 1.0 - (xpPenalty * 0.2);

			if (xpMultiplier < 0)
			{
				xpMultiplier = 0;
			}
		}

		return xpMultiplier;
	}

	public int naturalAC()
	{
		return calcACOfType("NaturalArmor");
	}

	/**
	 * Takes a String and a Class name and computes spell based variable such as
	 * Class level
	 * 
	 * @param aSpell
	 * @param aString
	 * @param anObj
	 * @return String
	 */
	public String parseSpellString(final Spell aSpell, String aString,
		final PObject anObj)
	{
		String aSpellClass = null;

		if (anObj instanceof Domain)
		{
			final CharacterDomain aCD = getCharacterDomainForDomain(anObj
				.getKeyName());

			if ((aCD != null) && aCD.isFromPCClass())
			{
				aSpellClass = "CLASS:" + getClassKeyed(aCD.getObjectName());
			}
		}
		else if (anObj instanceof PCClass)
		{
			aSpellClass = "CLASS:" + anObj.getKeyName();
		}
		else if (anObj instanceof Race) // could be innate spell for race
		{
			aSpellClass = "RACE:" + anObj.getKeyName();
		}

		if (aSpellClass == null)
		{
			return aString;
		}

		// Only want to replace items between ()'s
		while (aString.lastIndexOf('(') >= 0)
		{
			boolean found = false;

			final int start = aString.indexOf('(');
			int end = 0;
			int level = 0;

			for (int i = start; i < aString.length(); i++)
			{
				if (aString.charAt(i) == '(')
				{
					level++;
				}
				else if (aString.charAt(i) == ')')
				{
					level--;
					if (level == 0)
					{
						end = i;
						break;
					}
				}
			}

			/*
			 * int x = CoreUtility.innerMostStringStart(aString); int y =
			 * CoreUtility.innerMostStringEnd(aString); // bounds checking if
			 * ((start > end) || (start >= aString.length())) { break; } if
			 * ((end <= 0) || (end >= aString.length())) { break; }
			 */
			final String inCalc = aString.substring(start + 1, end);

			String replacement = "0";

			final Float fVal = getVariableValue(aSpell, inCalc, aSpellClass);
			if (!CoreUtility.doublesEqual(fVal.floatValue(), 0.0f))
			{
				found = true;
				replacement = fVal.intValue() + "";
			}
			else if ((inCalc.indexOf("MIN") >= 0)
				|| (inCalc.indexOf("MAX") >= 0))
			{
				found = true;
				replacement = fVal.intValue() + "";
			}
			if (found)
			{
				aString = aString.substring(0, start) + replacement
					+ aString.substring(end + 1);
			}
			else
			{
				aString = aString.substring(0, start) + "[" + inCalc + "]"
					+ aString.substring(end + 1);
			}
		}

		return aString;
	}

	/**
	 * Populate the characters skills list with skill that the character does
	 * not have ranks in according to the required level. The levels are defined
	 * in constants in the Skill class, but are None, Untrained or All.
	 * 
	 * @param level
	 *            The level of extra skills to be added.
	 */
	public void populateSkills(final int level)
	{
		Globals.sortPObjectListByName(getSkillList());
		removeExcessSkills(level);
		addNewSkills(level);

		// Now regenerate the output order
		final int sort;
		final boolean sortOrder;

		switch (getSkillsOutputOrder())
		{
			case GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC:
				sort = SkillComparator.RESORT_NAME;
				sortOrder = SkillComparator.RESORT_ASCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_DSC:
				sort = SkillComparator.RESORT_NAME;
				sortOrder = SkillComparator.RESORT_DESCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_TRAINED_ASC:
				sort = SkillComparator.RESORT_TRAINED;
				sortOrder = SkillComparator.RESORT_ASCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_TRAINED_DSC:
				sort = SkillComparator.RESORT_TRAINED;
				sortOrder = SkillComparator.RESORT_DESCENDING;

				break;

			default:

				// Manual sort, or unrecognised, so do no sorting.
				return;
		}

		final List<Skill> localSkillList = getSkillList();
		final SkillComparator comparator = new SkillComparator(sort, sortOrder);
		int nextOutputIndex = 1;
		Collections.sort(localSkillList, comparator);

		for (Skill skill : localSkillList)
		{
			if (skill.getOutputIndex() >= 0)
			{
				skill.setOutputIndex(nextOutputIndex++);
			}
		}
	}

	/**
	 * Removes a CharacterDomain
	 * 
	 * @param aCD
	 */
	public void removeCharacterDomain(final CharacterDomain aCD)
	{
		if (!characterDomainList.isEmpty())
		{
			characterDomainList.remove(aCD);
			// thePObjectList.remove(aCD);
			setDirty(true);
		}
	}

	public void removeCharacterDomain(final String aDomainKey)
	{
		final CharacterDomain cd = getCharacterDomainForDomain(aDomainKey);
		characterDomainList.remove(cd);
		// thePObjectList.remove(cd);
	}

	public void removeNaturalWeapons(final PObject obj)
	{
		for (Equipment weapon : obj.getNaturalWeapons())
		{
			// Need to make sure weapons are removed from
			// equip sets as well, or they will get added back
			// to the character. sage_sam 20 March 2003
			removeEquipment(weapon);
			delEquipSetItem(weapon);
			setDirty(true);
		}
	}

	/**
	 * Removes a "temporary" bonus
	 * 
	 * @param aBonus
	 */
	public void removeTempBonus(final BonusObj aBonus)
	{
		getTempBonusList().remove(aBonus);
		setDirty(true);
	}

	public void removeTempBonusItemList(final Equipment aEq)
	{
		getTempBonusItemList().remove(aEq);
		setDirty(true);
	}

	public void removeTemplate(final PCTemplate inTmpl)
	{
		if (inTmpl == null)
		{
			return;
		}

		cachedWeaponProfs = null;

		languages.removeAll(inTmpl.getSafeListFor(ListKey.AUTO_LANGUAGES)); // remove
																			// template
																			// languages.
		templateAutoLanguages.removeAll(inTmpl
			.getSafeListFor(ListKey.AUTO_LANGUAGES)); // remove them from the
														// local listing. Don't
														// clear though in case
														// of multiple
														// templates.

		templateLanguages.removeAll(inTmpl.getLanguageBonus());
		removeNaturalWeapons(inTmpl);

		PCTemplate t = this.getTemplateKeyed(inTmpl.getKeyName());
		for (int i = inTmpl.getLevelMods().size() - 1; i >= 0; i--)
		{
			String modString = (inTmpl.getLevelMods().get(i));
			StringTokenizer tok = new StringTokenizer(modString, "|");
			while (tok.hasMoreTokens())
			{
				final String colString = tok.nextToken();
				if (colString.startsWith("ADD"))
				{
					final String classKey = tok.nextToken();
					final int level = getVariableValue(tok.nextToken(), "")
						.intValue();

					PCClass aClass = this.getClassKeyed(classKey);

					boolean tempShowHP = SettingsHandler
						.getShowHPDialogAtLevelUp();
					SettingsHandler.setShowHPDialogAtLevelUp(false);
					boolean tempFeatDlg = SettingsHandler
						.getShowFeatDialogAtLevelUp();
					SettingsHandler.setShowFeatDialogAtLevelUp(false);
					int tempChoicePref = SettingsHandler
						.getSingleChoicePreference();
					SettingsHandler
						.setSingleChoicePreference(Constants.CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT);

					incrementClassLevel(-level, aClass, true, true);

					SettingsHandler.setSingleChoicePreference(tempChoicePref);
					SettingsHandler.setShowFeatDialogAtLevelUp(tempFeatDlg);
					SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
				}
			}
		}

		// It is hard to tell if removeTemplate() modifies
		// inTmpl.templatesAdded(), so not safe to optimise
		// the call to .size(). XXX
		for (int i = 0; i < inTmpl.templatesAdded().size(); ++i)
		{
			removeTemplate(getTemplateKeyed(inTmpl.templatesAdded().get(i)));
		}

		for (PCTemplate template : templateList)
		{
			if (template.getKeyName().equals(inTmpl.getKeyName()))
			{
				templateList.remove(template);

				break;
			}
		}

		if (!PlayerCharacterUtilities.canReassignTemplateFeats())
		{
			// TODO - ABILITYOBJECT
			// setAutomaticAbilitiesStable( null, false );
			setAutomaticFeatsStable(false);
		}

		setQualifyListStable(false);

		// karianna 1184888
		adjustMoveRates();

		// re-evaluate non-spellcaster spell lists
		getSpellList();
		calcActiveBonuses();
		setDirty(true);
	}

	public String replaceMasterString(String aString, final int aNum)
	{
		while (true)
		{
			final int x = aString.indexOf("MASTER");

			if (x == -1)
			{
				break;
			}

			final String leftString = aString.substring(0, x);
			final String rightString = aString.substring(x + 6);
			aString = leftString + Integer.toString(aNum) + rightString;
		}

		return aString;
	}

	public PCLevelInfo saveLevelInfo(final String classKeyName)
	{
		final PCLevelInfo li = new PCLevelInfo(this, classKeyName);
		pcLevelInfo.add(li);

		return li;
	}

	public void saveStatIncrease(final String statAbb, final int mod,
		final boolean isPreMod)
	{
		final int idx = getLevelInfoSize() - 1;

		if (idx >= 0)
		{
			pcLevelInfo.get(idx).addModifiedStat(statAbb, mod, isPreMod);
		}

		setDirty(true);
	}

	public int getStatIncrease(final String statAbb, final boolean includePost)
	{
		final int idx = getLevelInfoSize() - 1;

		if (idx >= 0)
		{
			return pcLevelInfo.get(idx).getTotalStatMod(statAbb, includePost);
		}
		return 0;
	}

	public int sizeAC()
	{
		return calcACOfType("Size");
	}

	public int sizeInt()
	{
		int iSize = racialSizeInt();

		if (race != null)
		{
			// Now check and see if a class has modified
			// the size of the character with something like:
			// BONUS:SIZEMOD|NUMBER|+1
			iSize += (int) getTotalBonusTo("SIZEMOD", "NUMBER");

			// Now see if there is a HD advancement in size
			// (Such as for Dragons)
			for (int i = 0; i < race.sizesAdvanced(totalHitDice()); ++i)
			{
				++iSize;
			}

			//
			// Must still be between 0 and 8
			//
			if (iSize < 0)
			{
				iSize = 0;
			}

			if (iSize >= SettingsHandler.getGame().getSizeAdjustmentListSize())
			{
				iSize = SettingsHandler.getGame().getSizeAdjustmentListSize() - 1;
			}
		}

		return iSize;
	}

	public int totalHitDice()
	{
		return race.hitDice(this) + totalMonsterLevels();
	}

	public int totalNonMonsterLevels()
	{
		int totalLevels = 0;

		for (PCClass pcClass : classList)
		{
			if (!pcClass.isMonster())
			{
				totalLevels += pcClass.getLevel();
			}
		}

		return totalLevels;
	}

	public BigDecimal totalValue()
	{
		BigDecimal totalValue = BigDecimal.ZERO;

		for (Equipment eq : getEquipmentMasterList())
		{
			totalValue = totalValue.add(eq.getCost(this).multiply(
				new BigDecimal(eq.qty())));
		}

		return totalValue;
	}

	public Float totalWeight()
	{
		float totalWeight = 0;
		final Float floatZero = Float.valueOf(0);
		boolean firstClothing = true;

		if (equipmentList.isEmpty())
		{
			return floatZero;
		}

		for (Equipment eq : equipmentList)
		{
			// Loop through the list of top
			if ((eq.getCarried().compareTo(floatZero) > 0)
				&& (eq.getParent() == null))
			{
				if (eq.getChildCount() > 0)
				{
					totalWeight += (eq.getWeightAsDouble(this) + eq
						.getContainedWeight(this).floatValue());
				}
				else
				{
					if (firstClothing && eq.isEquipped()
						&& eq.isType("CLOTHING"))
					{
						// The first equipped set of clothing should have a
						// weight of 0. Feature #437410
						firstClothing = false;
						totalWeight += (eq.getWeightAsDouble(this) * Math.max(
							eq.getCarried().floatValue() - 1, 0));
					}
					else
					{
						totalWeight += (eq.getWeightAsDouble(this) * eq
							.getCarried().floatValue());
					}
				}
			}
		}

		return Float.valueOf(totalWeight);
	}

	public int touchAC()
	{
		return calcACOfType("Touch");
	}

	/**
	 * replaces oldItem with newItem in all EquipSets
	 * 
	 * @param oldItem
	 * @param newItem
	 */
	public void updateEquipSetItem(final Equipment oldItem,
		final Equipment newItem)
	{
		if (equipSetList.isEmpty())
		{
			return;
		}

		final List<EquipSet> tmpList = new ArrayList<EquipSet>();

		// find all oldItem EquipSet's
		for (EquipSet es : equipSetList)
		{
			final Equipment eqI = es.getItem();

			if ((eqI != null) && oldItem.equals(eqI))
			{
				tmpList.add(es);
			}
		}

		for (EquipSet es : tmpList)
		{
			es.setValue(newItem.getName());
			es.setItem(newItem);
		}
		setDirty(true);
	}

	/**
	 * Gets whether the character has been changed since last saved.
	 * 
	 * @return true or false
	 */
	public boolean wasEverSaved()
	{
		return !Constants.EMPTY_STRING.equals(getFileName());
	}

	/**
	 * Figures out if a bonus should stack based on type, then adds it to the
	 * supplied map.
	 * 
	 * @param bonus
	 *            The value of the bonus.
	 * @param bonusType
	 *            The type of the bonus e.g. STAT.DEX:LUCK
	 * @param bonusMap
	 *            The bonus map being built up.
	 */
	void setActiveBonusStack(double bonus, String bonusType,
		Map<String, String> bonusMap)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();

			// only specific bonuses can actually be fractional
			// -> TODO should define this in external file
			if (!bonusType.startsWith("ITEMWEIGHT")
				&& !bonusType.startsWith("ITEMCOST")
				&& !bonusType.startsWith("ACVALUE")
				&& !bonusType.startsWith("ITEMCAPACITY")
				&& !bonusType.startsWith("LOADMULT")
				&& !bonusType.startsWith("FEAT")
				&& (bonusType.indexOf("DAMAGEMULT") < 0))
			{
				bonus = ((int) bonus); // TODO: never used
			}
		}
		else
		{
			return;
		}

		// default to non-stacking bonuses
		int index = -1;

		// bonusType is either of form:
		// COMBAT.AC
		// or
		// COMBAT.AC:Luck
		// or
		// COMBAT.AC:Armor.REPLACE
		//
		final StringTokenizer aTok = new StringTokenizer(bonusType, ":");

		if (aTok.countTokens() == 2)
		{
			// need 2nd token to see if it should stack
			final String aString;
			aTok.nextToken();
			aString = aTok.nextToken();

			if (aString != null)
			{
				index = SettingsHandler.getGame()
					.getUnmodifiableBonusStackList().indexOf(aString); // e.g.
																		// Dodge
			}
		}
		else
		{
			// un-named (or un-TYPE'd) bonuses stack
			index = 1;
		}

		// .STACK means stack with everything
		// .REPLACE means stack with other .REPLACE
		if (bonusType.endsWith(".STACK") || bonusType.endsWith(".REPLACE"))
		{
			index = 1;
		}

		// If it's a negative bonus, it always needs to be added
		if (bonus < 0)
		{
			index = 1;
		}

		if (index == -1) // a non-stacking bonus
		{
			final String aVal = bonusMap.get(bonusType);

			if (aVal == null)
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus), bonusMap);
			}
			else
			{
				putActiveBonusMap(bonusType, String.valueOf(Math.max(bonus,
					Float.parseFloat(aVal))), bonusMap);
			}
		}
		else
		// a stacking bonus
		{
			if (bonusType == null) 
			{
				bonusType = Constants.EMPTY_STRING;
			}

			final String aVal = bonusMap.get(bonusType);

			if (aVal == null)
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus), bonusMap);
			}
			else
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus
					+ Float.parseFloat(aVal)), bonusMap);
			}
		}
	}

	/**
	 * Returns a list of Ability Objects of the given Category from the global
	 * list, which 1) match the given abilityType, 2) the character qualifies
	 * for, and 3) the character does not already have.
	 * 
	 * @param category
	 *            of ability to return
	 * @param abilityType
	 *            String type of ability to return.
	 * @param autoQualify
	 *            assume PC qualifies for feat. Used for virtual feats
	 * 
	 * @return List of Ability Objects.
	 */

	public List<Ability> getAvailableAbilities(final String category,
		final String abilityType, final boolean autoQualify)
	{
		final List<Ability> anAbilityList = new ArrayList<Ability>();
		final Iterator<? extends Categorisable> it = Globals
			.getAbilityKeyIterator(category);

		while (it.hasNext())
		{
			final Ability anAbility = (Ability) it.next();

			if (anAbility.matchesType(abilityType)
				&& canSelectAbility(anAbility, autoQualify))
			{
				anAbilityList.add(anAbility);
			}
		}

		return anAbilityList;
	}

	/**
	 * Returns the list of names of available feats of given type. That is, all
	 * feats from the global list, which match the given featType, the character
	 * qualifies for, and the character does not already have.
	 * 
	 * @param featType
	 *            String category of feat to list.
	 * @return List of Feats.
	 */
	public List<String> getAvailableFeatNames(final String featType)
	{
		return (getAvailableFeatNames(featType, false));
	}

	/**
	 * Returns the list of names of available feats of given type. That is, all
	 * feats from the global list, which match the given featType, the character
	 * qualifies for, and the character does not already have.
	 * 
	 * @param featType
	 *            String category of feat to list.
	 * @param autoQualify
	 *            assume PC qualifies for feat. Used for virtual feats
	 * @return List of Feats.
	 */
	public List<String> getAvailableFeatNames(final String featType,
		final boolean autoQualify)
	{
		final List<String> anAbilityList = new ArrayList<String>();
		final Iterator<? extends Categorisable> it = Globals
			.getAbilityKeyIterator("FEAT");

		for (; it.hasNext();)
		{
			final Ability anAbility = (Ability) it.next();

			if (anAbility.matchesType(featType)
				&& canSelectAbility(anAbility, autoQualify))
			{
				anAbilityList.add(anAbility.getKeyName());
			}
		}

		return anAbilityList;
	}

	/**
	 * @return true if character is not currently being read from file.
	 */
	public boolean isNotImporting()
	{
		return !importing;
	}

	/**
	 * @return true if character is currently being read from file.
	 */
	public boolean isImporting()
	{
		return importing;
	}

	/**
	 * @param moveIdx
	 * @return the integer movement speed multiplier for Index
	 */
	Double getMovementMult(final int moveIdx)
	{
		if ((getMovements() != null) && (moveIdx < movementMult.length))
		{
			return movementMult[moveIdx];
		}
		return Double.valueOf(0);
	}

	/*
	 * Build on-the-fly so removing templates won't mess up qualify list
	 */
	HashMapToList<Class, String> getQualifyMap()
	{
		if (!qualifyListStable)
		{
			qualifyArrayMap = new HashMapToList<Class, String>();

			// Try all possible POBjects
			for (PObject pObj : getPObjectList())
			{
				if (pObj == null)
				{
					continue;
				}

				if (pObj.containsQualify())
				{
					qualifyArrayMap.addAllLists(pObj.getQualifyMap());
				}
			}

			setQualifyListStable(true);
		}

		return qualifyArrayMap;
	}

	void addVariable(final String variableString)
	{
		variableList.add(variableString);
		setDirty(true);
	}

	void giveClassesAway(final PCClass toClass, final PCClass fromClass,
		int iCount)
	{
		if ((toClass == null) || (fromClass == null))
		{
			return;
		}

		// Will take destination class over maximum?
		if ((toClass.getLevel() + iCount) > toClass.getMaxLevel())
		{
			iCount = toClass.getMaxLevel() - toClass.getLevel();
		}

		// Enough levels to move?
		if ((fromClass.getLevel() <= iCount) || (iCount < 1))
		{
			return;
		}

		final int iFromLevel = fromClass.getLevel() - iCount;
		final int iToLevel = toClass.getLevel();

		toClass.setLevel(iToLevel + iCount, this);

		for (int i = 0; i < iCount; ++i)
		{
			toClass.setHitPoint(iToLevel + i, fromClass.getHitPoint(iFromLevel
				+ i));
			fromClass.setHitPoint(iFromLevel + i, Integer.valueOf(0));
		}

		rebuildLists(toClass, fromClass, iCount, this);

		fromClass.setLevel(iFromLevel, this);

		// first, change the toClass current PCLevelInfo level
		for (PCLevelInfo pcl : pcLevelInfo)
		{
			if (pcl.getClassKeyName().equals(toClass.getKeyName()))
			{
				final int iTo = (pcl.getLevel() + toClass.getLevel())
					- iToLevel;
				pcl.setLevel(iTo);
			}
		}

		// change old class PCLevelInfo to the new class
		for (PCLevelInfo pcl : pcLevelInfo)
		{
			if (pcl.getClassKeyName().equals(fromClass.getKeyName())
				&& (pcl.getLevel() > iFromLevel))
			{
				final int iFrom = pcl.getLevel() - iFromLevel;
				pcl.setClassKeyName(toClass.getKeyName());
				pcl.setLevel(iFrom);
			}
		}

		/*
		 * // get skills associated with old class and link to new class for
		 * (Iterator e = getSkillList().iterator(); e.hasNext();) { Skill aSkill =
		 * (Skill) e.next(); aSkill.replaceClassRank(fromClass.getName(),
		 * toClass.getName()); } toClass.setSkillPool(fromClass.getSkillPool());
		 */
	}

	// boolean qualifiesForFeat(final String featName)
	// {
	// final Ability anAbility = Globals.getAbilityNamed("FEAT", featName);
	//
	// if (anAbility != null)
	// {
	// return qualifiesForFeat(anAbility);
	// }
	//
	// return false;
	// }

	/**
	 * Returns true if this PlayerCharacter contains a Domain with a key that
	 * matches the given Domain
	 */
	public boolean containsCharacterDomain(String aDomainKey)
	{
		for (CharacterDomain cd : characterDomainList)
		{
			Domain d = cd.getDomain();
			if (d.getKeyName().equalsIgnoreCase(aDomainKey))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * return the index of CharacterDomain matching domainName else return -1
	 * 
	 * @param domainName
	 * @return character domain index
	 * @deprecated 10/21/06 thpr as part of PCClass rebuilding
	 */
	@Deprecated
	public int getCharacterDomainIndex(final String domainKey)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();

			if ((aDomain != null)
				&& aDomain.getKeyName().equalsIgnoreCase(domainKey))
			{
				return i;
			}
		}

		return -1;
	}

	boolean addFavoredClass(final String aString)
	{
		if ((aString.length() != 0) && !favoredClasses.contains(aString))
		{
			favoredClasses.add(aString);
			setDirty(true);

			return true;
		}

		return false;
	}

	void addFreeLanguage(final Language aLang)
	{
		this.languages.add(aLang);
		++freeLangs;
		setDirty(true);
	}

	void addFreeLanguageKeyed(final String aKey)
	{
		final Language aLang = Globals.getLanguageKeyed(aKey);

		if (aLang != null)
		{
			addFreeLanguage(aLang);
		}
	}

	boolean hasSpecialAbility(final SpecialAbility sa)
	{
		final String saKey = sa.getKeyName();
		final String saDesc = sa.getSADesc();

		for (SpecialAbility saFromList : getSpecialAbilityList())
		{
			if (saFromList.getKeyName().equalsIgnoreCase(saKey)
				&& saFromList.getSADesc().equalsIgnoreCase(saDesc))
			{
				return true;
			}
		}

		return false;
	}

	boolean removeFavoredClass(final String aString)
	{
		if (favoredClasses.contains(aString))
		{
			favoredClasses.remove(aString);
			setDirty(true);

			return true;
		}

		return false;
	}

	void removeVariable(final String variableString)
	{
		for (Iterator<String> e = variableList.iterator(); e.hasNext();)
		{
			final String aString = e.next();

			if (aString.startsWith(variableString))
			{
				e.remove();
				setDirty(true);
			}
		}
	}

	/**
	 * Scan through the list of domains the character has to ensure that they
	 * are all still valid. Any invalid domains will be removed from the
	 * character.
	 */
	void validateCharacterDomains()
	{
		if (!isImporting())
		{
			getSpellList();
		}

		for (CharacterDomain cd : characterDomainList)
		{
			if (!cd.isDomainValidFor(this))
			{
				removeCharacterDomain(cd);
			}
		}
	}

	/**
	 * Searches the activeBonus HashMap for aKey
	 * 
	 * @param aKey
	 * @param defaultValue
	 * 
	 * @return defaultValue if aKey not found
	 */
	private double getActiveBonusForMapKey(String aKey,
		final double defaultValue)
	{
		aKey = aKey.toUpperCase();

		final String regVal = getActiveBonusMap().get(aKey);

		if (regVal != null)
		{
			return Double.parseDouble(regVal);
		}

		return defaultValue;
	}

	/**
	 * Active BonusObj's
	 * 
	 * @return List
	 */
	public List<BonusObj> getActiveBonusList()
	{
		return activeBonusList;
	}

	private List<String> getAutoArmorProfList()
	{
		final ArrayList<String> aList = new ArrayList<String>();

		// Try all possible PObjects
		for (PObject pObj : getPObjectList())
		{
			if (pObj != null)
			{
				pObj.addAutoTagsToList("ARMORPROF", aList, this, true);
			}
		}

		return aList;
	}

	/**
	 * Calculates total bonus from Checks
	 * 
	 * @param aType
	 * @param aName
	 * @return check bonus to
	 */
	private double getCheckBonusTo(String aType, String aName)
	{
		double bonus = 0;
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		final List<PObject> aList = SettingsHandler.getGame()
			.getUnmodifiableCheckList();

		for (PObject obj : aList)
		{
			final List<BonusObj> tempList = obj
				.getBonusListOfType(aType, aName);

			if (!tempList.isEmpty())
			{
				bonus += calcBonusFromList(tempList);
			}
		}

		return bonus;
	}

	private synchronized void setClassLevelsBrazenlyTo(
		final Map<String, String> lvlMap)
	{
		// set class levels to class name,level pair
		for (PCClass pcClass : classList)
		{
			String lvl = lvlMap.get(pcClass.getKeyName());

			if (lvl == null)
			{
				lvl = "0";
			}

			pcClass.setLevelWithoutConsequence(Integer.parseInt(lvl));

		}
		// Recalculate bonuses, based on new level
		calcActiveBonuses();
		// setDirty(true);
	}

	private String getDisplayClassName()
	{
		return (classList.isEmpty() ? "Nobody" : classList.get(
			classList.size() - 1).getDisplayClassName());
	}

	private String getDisplayRaceName()
	{
		final String raceName = getRace().toString();

		return (raceName.equals(Constants.s_NONESELECTED) ? "Nothing"
			: raceName);
	}

	// /**
	// * Get AUTO weapon proficiencies from all granting objects
	// * @param aFeatList
	// * @return Sorted Set
	// */
	// SortedSet<String> getAutoWeaponProfs(final List<Ability> aFeatList)
	// {
	// SortedSet<String> results = new TreeSet<String>();
	// final Race aRace = getRace();
	//
	// ListKey<String> weaponProfBonusKey = ListKey.SELECTED_WEAPON_PROF_BONUS;
	//
	// //
	// // Add race-grantedweapon proficiencies
	// //
	// if (aRace != null)
	// {
	// results = addWeaponProfsLists(aRace.getWeaponProfAutos(), results,
	// aFeatList, true);
	//
	// for (String aString : aRace.getSafeListFor(weaponProfBonusKey))
	// {
	// results.add(aString);
	// addWeaponProfToList(aFeatList, aString, true);
	// }
	//
	// aRace.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
	// }
	//
	// //
	// // Add template-granted weapon proficiencies
	// //
	// for ( PCTemplate template : getTemplateList() )
	// {
	// results = addWeaponProfsLists(template.getWeaponProfAutos(), results,
	// aFeatList, true);
	//
	// for (String aString : template.getSafeListFor(weaponProfBonusKey))
	// {
	// results.add(aString);
	// addWeaponProfToList(aFeatList, aString, true);
	// }
	//
	// template.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
	// }
	//
	// //
	// // Add class-granted weapon proficiencies
	// //
	// for ( PCClass pcClass : classList )
	// {
	// results = addWeaponProfsLists(pcClass.getWeaponProfAutos(), results,
	// aFeatList, true);
	//
	// for (String aString : pcClass.getSafeListFor(weaponProfBonusKey))
	// {
	// results.add(aString);
	// addWeaponProfToList(aFeatList, aString, true);
	// }
	//
	// pcClass.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
	// }
	//
	// //
	// // Add feat-granted weapon proficiencies
	// //
	// setAggregateFeatsStable(false);
	// // TODO - ABILITYOBJECT
	// // setAggregateAbilitiesStable(null, false);
	//
	// for ( Ability feat : aggregateFeatList() )
	// {
	// results = addWeaponProfsLists(feat.getWeaponProfAutos(), results,
	// aFeatList, true);
	//
	// List<String> staticProfList = new ArrayList<String>();
	// staticProfList.addAll(feat.getSafeListFor(weaponProfBonusKey));
	// for (String aString : staticProfList)
	// {
	// results.add(aString);
	// addWeaponProfToList(aFeatList, aString, true);
	// }
	//
	// feat.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
	// }
	//
	// //
	// // Add skill-granted weapon proficiencies
	// //
	// for ( Skill skill : getSkillList() )
	// {
	// results = addWeaponProfsLists(skill.getWeaponProfAutos(), results,
	// aFeatList, true);
	// // TODO Should skills grant BONUS profs?
	// skill.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
	// }
	//
	// //
	// // Add equipment-granted weapon proficiencies
	// //
	// for ( Equipment eq : equipmentList )
	// {
	// if (eq.isEquipped())
	// {
	// results = addWeaponProfsLists(eq.getWeaponProfAutos(), results,
	// aFeatList, true);
	// eq.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
	//
	// // TODO Should eqMods add to Auto list or BONUS profs?
	// for ( EquipmentModifier eqMod : eq.getEqModifierList(true) )
	// {
	// results = addWeaponProfsLists(eqMod.getWeaponProfAutos(), results,
	// aFeatList, true);
	// }
	//
	// for ( EquipmentModifier eqMod : eq.getEqModifierList(false) )
	// {
	// results = addWeaponProfsLists(eqMod.getWeaponProfAutos(), results,
	// aFeatList, true);
	// }
	// }
	// }
	//
	// //
	// // Add deity-granted weapon proficiencies
	// //
	// if (deity != null)
	// {
	// results = addWeaponProfsLists(deity.getWeaponProfAutos(), results,
	// aFeatList, true);
	// deity.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
	// // TODO Should deity add BONUS profs
	// }
	//
	// //
	// // Add domain-granted weapon proficiencies
	// //
	// for ( CharacterDomain cd : characterDomainList )
	// {
	// final Domain aDomain = cd.getDomain();
	//
	// if (aDomain != null)
	// {
	// results = addWeaponProfsLists(aDomain.getWeaponProfAutos(), results,
	// aFeatList, true);
	//
	// for (String aString : aDomain.getSafeListFor(weaponProfBonusKey))
	// {
	// results.add(aString);
	// addWeaponProfToList(aFeatList, aString, true);
	// }
	//
	// aDomain.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
	// }
	// }
	//
	// //
	// // Parse though aggregate feat list, looking for any feats that grant
	// weapon proficiencies
	// //
	// //addFeatProfs(getStableAggregateFeatList(), aFeatList, results);
	// //addFeatProfs(getStableAutomaticFeatList(), aFeatList, results);
	//
	// // Why do we clear the list and then add it all again?
	// weaponProfList.clear(); // TheForken
	// for ( String profKey : results )
	// {
	// final WeaponProf wp = Globals.getWeaponProfKeyed(profKey);
	// if (wp != null)
	// {
	// weaponProfList.add(wp);
	// }
	// }
	//
	// return results;
	// }

	/**
	 * Parses through all Equipment items and calculates total Bonus
	 * 
	 * @param aType
	 * @param aName
	 * @return equipment bonus to
	 */
	public double getEquipmentBonusTo(String aType, String aName)
	{
		double bonus = 0;

		if (equipmentList.isEmpty())
		{
			return bonus;
		}

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		for (Equipment eq : equipmentList)
		{
			if (eq.isEquipped())
			{
				final List<BonusObj> tempList = eq.getBonusListOfType(aType,
					aName, true);

				if (eq.isWeapon() && eq.isDouble())
				{
					tempList.addAll(eq.getBonusListOfType(aType, aName, false));
				}

				bonus += calcBonusFromList(tempList);
			}
		}

		return bonus;
	}

	/**
	 * Returns the Feat definition searching by key (not name), as contained in
	 * the specified list
	 * 
	 * @param featName
	 *            String key of the feat to check for.
	 * @param afeatList
	 * @return the Feat (not the CharacterFeat) searched for, <code>null</code>
	 *         if not found.
	 */
	private Ability getFeatKeyed(final String featName,
		final List<Ability> afeatList)
	{
		if (afeatList.isEmpty())
		{
			return null;
		}

		for (Ability feat : afeatList)
		{
			if (feat.getKeyName().equalsIgnoreCase(featName))
			{
				return feat;
			}
		}

		return null;
	}

	private String getFullDisplayClassName()
	{
		if (classList.isEmpty())
		{
			return "Nobody";
		}

		final StringBuffer buf = new StringBuffer();

		boolean first = true;
		for (PCClass c : classList)
		{
			if (!first)
			{
				buf.append('/');
				first = false;
			}
			buf.append(c.getFullDisplayClassName());
		}

		return buf.toString();
	}

	/**
	 * Return a hashmap of the first maxCharacterLevel character levels that a
	 * character has taken This will be a hash of "Class name"=>"number of
	 * levels as a string". For example, {"Fighter"=>"2", "Cleric":"16"}
	 * 
	 * @param maxCharacterLevel
	 *            the maximum character level that we can include in this map
	 * @return character level map
	 */
	private Map<String, String> getCharacterLevelHashMap(
		final int maxCharacterLevel)
	{
		final Map<String, String> lvlMap = new HashMap<String, String>();

		int characterLevels = 0;
		for (int i = 0; i < getLevelInfoSize(); ++i)
		{
			final String classKeyName = getLevelInfoClassKeyName(i);
			final PCClass aClass = Globals.getClassKeyed(classKeyName);

			if (aClass.isMonster() || characterLevels < maxCharacterLevel)
			{
				// we can use this class level if it is a monster level, or if
				// we have not yet hit our maximum number of characterLevels
				String val = lvlMap.get(classKeyName);
				if (val == null)
				{
					val = "0";
				}

				val = String.valueOf(Integer.parseInt(val) + 1);
				lvlMap.put(classKeyName, val);
			}

			if (!aClass.isMonster())
			{
				// If the class level was not a monster level then it counts
				// towards the total number of character levels
				characterLevels++;
			}
		}

		return lvlMap;
	}

	private void setMoveFromList(final List<? extends PObject> aList)
	{
		for (PObject pObj : aList)
		{
			final Movement movement = pObj.getMovement();

			if (movement == null || movement.getNumberOfMovements() < 1)
			{
				continue;
			}

			for (int i = 0; i < movement.getNumberOfMovements(); i++)
			{
				setMyMoveRates(movement.getMovementType(i), movement
					.getMovement(i).doubleValue(), movement.getMovementMult(i),
					movement.getMovementMultOp(i), movement.getMoveRatesFlag());
			}
		}
		// setDirty(true);
	}

	/**
	 * an array of movement speeds
	 * 
	 * @return array of Integer movement speeds
	 */
	public Double[] getMovements()
	{
		return movements;
	}

	/**
	 * sets up the movement arrays creates them if they do not exist
	 * 
	 * @param moveType
	 * @param anDouble
	 * @param moveMult
	 * @param multOp
	 * @param moveFlag
	 */
	private void setMyMoveRates(final String moveType, final double anDouble,
		final Double moveMult, final String multOp, final int moveFlag)
	{
		//
		// NOTE: can not use getMovements() accessor as it calls
		// this function, so use the variable: movements
		//
		Double moveRate;

		// The ALL type can only be applied to existing movement
		// so just loop and add or set as appropriate
		if ("ALL".equals(moveType))
		{
			if (moveFlag == 0)
			{ // set all types of movement to moveRate

				for (int i = 0; i < movements.length; i++)
				{
					moveRate = new Double(anDouble);
					movements[i] = moveRate;
				}
			}
			else
			{ // add moveRate to all types of movement.

				for (int i = 0; i < movements.length; i++)
				{
					moveRate = new Double(anDouble + movements[i].doubleValue());
					movements[i] = moveRate;
				}
			}
		}
		else
		{
			if (moveFlag == 0)
			{ // set movement to moveRate
				moveRate = new Double(anDouble);

				for (int i = 0; i < movements.length; i++)
				{
					if (moveType.equals(movementTypes[i]))
					{
						movements[i] = moveRate;
						movementMult[i] = moveMult;
						movementMultOp[i] = multOp;

						return;
					}
				}

				increaseMoveArray(moveRate, moveType, moveMult, multOp);
			}
			else if (moveFlag == 1)
			{ // add moveRate to movement.

				for (int i = 0; i < movements.length; i++)
				{
					moveRate = new Double(anDouble + movements[i].doubleValue());

					if (moveType.equals(movementTypes[i]))
					{
						movements[i] = moveRate;
						movementMult[i] = moveMult;
						movementMultOp[i] = multOp;

						return;
					}

					increaseMoveArray(moveRate, moveType, moveMult, multOp);
				}
			}
			else
			{ // get base movement, then add moveRate
				moveRate = new Double(anDouble + movements[0].doubleValue());

				// for existing types of movement:
				for (int i = 0; i < movements.length; i++)
				{
					if (moveType.equals(movementTypes[i]))
					{
						movements[i] = moveRate;
						movementMult[i] = moveMult;
						movementMultOp[i] = multOp;

						return;
					}
				}

				increaseMoveArray(moveRate, moveType, moveMult, multOp);
			}
		}
		setDirty(true);
	}

	int getNumAttacks()
	{
		return Math.min(Math.max(baseAttackBonus() / 5, 4), 1);
	}

	private String getOrdinal(final int cardinal)
	{
		switch (cardinal)
		{
			case 1:
				return "st";

			case 2:
				return "nd";

			case 3:
				return "rd";

			default:
				return "th";
		}
	}

	/**
	 * Returns a bonus.
	 * 
	 * @param aList
	 * @param aType
	 * @param aName
	 * @param subSearch
	 * @return double
	 */
	private double getPObjectWithCostBonusTo(
		final List<? extends PObject> aList, final String aType,
		final String aName, final boolean subSearch)
	{
		double iBonus = 0;

		if (aList.isEmpty())
		{
			return iBonus;
		}

		for (PObject anObj : aList)
		{
			final List<BonusObj> tempList = anObj.getBonusListOfType(aType,
				aName);
			iBonus += calcBonusWithCostFromList(tempList, subSearch);
		}

		return iBonus;
	}

	private boolean isProficientWith(final Equipment eq,
		final List<String> aList)
	{
		// First, check to see if fits into any TYPE granted
		for (int i = 0; i < aList.size(); ++i)
		{
			final String aString = aList.get(i);

			if ((aString.startsWith("TYPE=") || aString.startsWith("TYPE.")))
			{
				int matches = 0;
				final StringTokenizer tok = new StringTokenizer(aString
					.substring(5), ".");
				final int minMatches = tok.countTokens();
				while (tok.hasMoreTokens())
				{
					final String aType = tok.nextToken();
					if (eq.isType(aType))
					{
						matches++;
					}
				}
				// We have to match all the tokens.
				if (matches == minMatches)
				{
					return true;
				}
			}
			else
			{
				// All TYPE profs are at the beginning of the list
				break;
			}
		}

		return aList.contains(eq.profKey(this));
	}

	private boolean isProficientWithWeapon(final Equipment eq)
	{
		if (eq.isNatural())
		{
			return true;
		}

		final WeaponProf wp = eq.getExpandedWeaponProf(this);

		if (wp == null)
		{
			return false;
		}

		return hasWeaponProfKeyed(wp.getKeyName());
	}

	private void setQualifyListStable(final boolean state)
	{
		qualifyListStable = state;
		// setDirty(true);
	}

	private SortedSet<String> getRacialFavoredClasses()
	{
		String rfc = getRace().getFavoredClass();

		if (rfc.startsWith("CHOOSE:"))
		{
			final List<PCClass> availableList = new ArrayList<PCClass>();
			final StringTokenizer tok = new StringTokenizer(rfc.substring(7),
				"|");
			while (tok.hasMoreTokens())
			{
				final PCClass pcClass = Globals.getClassKeyed(tok.nextToken());
				if (pcClass != null)
				{
					availableList.add(pcClass);
				}
			}
			final List<PCClass> selectedList = new ArrayList<PCClass>(1);
			Globals.getChoiceFromList("Select favored class", availableList,
				selectedList, 1, true);
			rfc = selectedList.get(0).getKeyName();
		}

		if (addFavoredClass(rfc))
		{
			setStringFor(StringKey.RACIAL_FAVORED_CLASS, rfc);
		}
		else
		{
			removeStringFor(StringKey.RACIAL_FAVORED_CLASS);
		}

		return favoredClasses;
	}

	private List<String> getSelectedArmorProfList()
	{
		final ArrayList<String> aList = new ArrayList<String>();

		// Try all possible PObjects
		for (PObject pObj : getPObjectList())
		{
			if (pObj == null)
			{
				continue;
			}

			List<String> l = pObj.getListFor(ListKey.SELECTED_ARMOR_PROF);
			if (l != null)
			{
				aList.addAll(l);
			}
		}

		return aList;
	}

	private String getSubRegion(final boolean useTemplates)
	{
		String pcSubRegion = getStringFor(StringKey.SUB_REGION);
		if ((pcSubRegion != null) || !useTemplates)
		{
			return pcSubRegion; // character's subregion trumps any from
								// templates
		}

		String s = Constants.s_NONE;

		for (PCTemplate template : templateList)
		{
			final String tempSubRegion = template.getSubRegion();

			if (!tempSubRegion.equals(Constants.s_NONE))
			{
				s = tempSubRegion;
			}
		}

		return s;
	}

	private int getTotalClassLevels()
	{
		int total = 0;

		for (PCClass pcClass : classList)
		{
			total += pcClass.getLevel();
		}

		return total;
	}

	/**
	 * get the total number of character levels a character has. A character
	 * level is any class level that is not a monster level
	 * 
	 * @return total character level
	 */
	private int getTotalCharacterLevel()
	{
		int total = 0;

		for (PCClass pcClass : classList)
		{
			if (!pcClass.isMonster())
			{
				total += pcClass.getLevel();
			}
		}

		return total;
	}

	private HashMap<String, String> getTotalLevelHashMap()
	{
		final HashMap<String, String> lvlMap = new HashMap<String, String>();

		for (PCClass aClass : classList)
		{
			lvlMap.put(aClass.getKeyName(), String.valueOf(aClass.getLevel()));
		}

		return lvlMap;
	}

	private boolean isVirtualFeatsStable()
	{
		return virtualFeatsStable;
	}

	/**
	 * Adds the List to activeBonuses if it passes RereqToUse Test
	 * 
	 * @param aList
	 */
	private void addListToActiveBonuses(final List<BonusObj> aList)
	{
		if (aList == null)
		{
			return;
		}
		activeBonusList.addAll(aList);
		// setDirty(true);
	}

	private void addSpells(final PObject obj)
	{
		if ((race == null) || (obj == null) || (obj.getSpellList() == null)
			|| obj.getSpellList().isEmpty())
		{
			return;
		}

		PObject owner;

		List<PCSpell> spellList = obj.getSpellList();
		for (PCSpell pcSpell : spellList)
		{
			final String spellKey = pcSpell.getKeyName();
			final Spell aSpell = Globals.getSpellKeyed(spellKey);

			if (aSpell == null)
			{
				return;
			}

			final String castCount = pcSpell.getTimesPerDay();
			int spellLevel = -1;
			int times = 1;
			int slotLevel = 0;
			owner = race;

			if (castCount == null || castCount.equals(""))
			{
				times = 1;
			}
			else if (castCount.startsWith("LEVEL=")
				|| castCount.startsWith("LEVEL."))
			{
				spellLevel = Integer.parseInt(castCount.substring(6));
				slotLevel = spellLevel;

				if (obj instanceof PCClass)
				{
					owner = obj;
				}
			}
			else
			{
				times = getVariableValue(castCount, "").intValue();
			}

			final String book = pcSpell.getSpellbook();

			final String dcFormula = pcSpell.getDcFormula();
			if (dcFormula != null && !dcFormula.equals(""))
			{
				getVariableValue(dcFormula, "").intValue(); // TODO: value never
															// used
			}

			if (PrereqHandler.passesAll(pcSpell.getPreReqList(), this, pcSpell))
			{
				final Spell newSpell = aSpell.clone();
				aSpell.setFixedCasterLevel(pcSpell.getCasterLevelFormula());
				aSpell.setFixedDC(pcSpell.getDcFormula());
				final List<CharacterSpell> sList = owner.getSpellSupport()
					.getCharacterSpell(newSpell, book, spellLevel);

				if (!sList.isEmpty())
				{
					continue;
				}

				final CharacterSpell cs = new CharacterSpell(owner, aSpell);
				cs.addInfo(slotLevel, times, book);
				addSpellBook(book);
				owner.getSpellSupport().addCharacterSpell(cs);
			}
		}
		setDirty(true);
	}

	private Map<String, String> addStringToDRMap(
		final Map<String, String> drMap, final String drString)
	{
		if ((drString == null) || (drString.length() == 0))
		{
			return drMap;
		}

		final StringTokenizer aTok = new StringTokenizer(drString, "|");

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			final int x = aString.indexOf('/');
			String key;
			final String val;

			if ((x > 0) && (x < aString.length()))
			{
				val = aString.substring(0, x);
				key = aString.substring(x + 1);

				// some DR: are DR:val/key and others are DR:val/+key,
				// so remove the + to make them equivalent
				if ((key.length() > 0) && (key.charAt(0) == '+'))
				{
					key = key.substring(1);
				}

				// We use -1 as a starting value so as to allow DR:0/- to work.
				// It
				// can then have bonuses added to improve it.
				int y = -1;
				final String obj = drMap.get(key);

				if (obj != null)
				{
					y = Integer.parseInt(obj);
				}

				final int z = getVariableValue(val, "").intValue();

				if (z > y)
				{
					drMap.put(key, String.valueOf(z));
				}
			}
		}
		// setDirty(true);

		return drMap;
	}

	/**
	 * create a map of key (vision-type string) and values (int)
	 * 
	 * @param visMap
	 * @param aMap
	 * @return Map
	 */
	private List<Vision> addStringToVisionList(final List<Vision> visionList,
		final List<Vision> addList)
	{
		if ((addList == null) || (addList.size() == 0))
		{
			return visionList;
		}

		for (Vision vis : addList)
		{
			final VisionType visType = vis.getType();
			if (!vis.qualifies(this))
			{
				continue;
			}
			Vision foundVision = null;
			for (Vision baseVis : visionList)
			{
				if (baseVis.getType() == visType)
				{
					foundVision = baseVis;
					break;
				}
			}

			int a = getVariableValue(vis.getDistance(), "").intValue();
			// Add any bonuses to new value
			a += (int) getTotalBonusTo("VISION", visType.toString());

			if (foundVision == null)
			{
				visionList.add(new Vision(visType, String.valueOf(a)));
			}
			else
			{
				if (a > Integer.parseInt(foundVision.getDistance()))
				{
					visionList.remove(foundVision);
					visionList.add(new Vision(visType, String.valueOf(a)));
				}
			}
		}

		return visionList;
	}

	private void setStableAggregateFeatList(final List<Ability> aFeatList)
	{
		stableAggregateFeatList = aFeatList;
		setAggregateFeatsStable(aFeatList != null);
	}

	/**
	 * Not sure what this does yet.
	 * 
	 * @param aFeatList
	 * @param aString
	 * @param isAuto
	 */
	private void addWeaponProfToList(final List<Ability> aFeatList,
		final String aString, final boolean isAuto)
	{
		if (aString.startsWith("WEAPONTYPE=")
			|| aString.startsWith("WEAPONTYPE."))
		{
			for (Equipment weap : EquipmentList.getEquipmentOfType("WEAPON."
				+ aString.substring(11), ""))
			{
				final WeaponProf aProf = Globals.getWeaponProfKeyed(weap
					.profKey(this));

				if (aProf != null)
				{
					addWeaponProfToList(aFeatList, aProf.getKeyName(), isAuto);
				}
			}

			return;
		}

		// Add all weapons of type aString
		// (e.g.: Simple, Martial, Exotic, Ranged, etc.)
		else if (Globals.weaponTypesContains(aString))
		{
			for (WeaponProf weaponProf : Globals
				.getAllWeaponProfsOfType(aString))
			{
				addWeaponProfToList(aFeatList, weaponProf.getKeyName(), isAuto);
			}

			return;
		}

		final WeaponProf wp = Globals.getWeaponProfKeyed(aString);

		if (wp != null)
		{
			final StringTokenizer aTok = new StringTokenizer(wp.getType(), ".");
			String featKey = aTok.nextToken() + " Weapon Proficiency";

			while (aTok.hasMoreTokens() || (featKey.length() > 0))
			{
				if ("".equals(featKey))
				{
					if (aTok.hasMoreTokens())
					{
						featKey = aTok.nextToken() + " Weapon Proficiency";
					}
					else
					{
						break;
					}
				}

				Ability anAbility = AbilityUtilities.getAbilityFromList(
					aFeatList, "FEAT", featKey, Ability.Nature.ANY);

				if (anAbility != null)
				{
					if (anAbility.isMultiples()
						&& !anAbility.containsAssociated(aString))
					{
						anAbility.addAssociated(aString);
						anAbility.sortAssociated();
					}
				}
				else
				{
					anAbility = Globals.getAbilityKeyed("FEAT", featKey);

					if (anAbility != null)
					{
						if (isAuto
							&& !anAbility.isMultiples()
							&& !Constants.s_INTERNAL_WEAPON_PROF
								.equalsIgnoreCase(featKey))
						{
							//
							// Only use catch-all if haven't taken feat that
							// supersedes it
							//
							if (hasRealFeat(anAbility))
							{
								featKey = Constants.s_INTERNAL_WEAPON_PROF;

								continue;
							}

							featKey = "";

							continue; // Don't add auto-feat
						}

						anAbility = anAbility.clone();
						anAbility.addAssociated(aString);

						if (isAuto)
						{
							anAbility.setFeatType(Ability.Nature.AUTOMATIC);
						}

						aFeatList.add(anAbility);
					}

					/*
					 * else { if (!wp.isType("NATURAL")) {
					 * Logging.errorPrint("Weaponprof feat not found: " +
					 * featName + ":" + aString); } }
					 */
				}
				if (anAbility != null)
				{
					// TheForken 20050124 adds bonus to feat
					anAbility.addSelectedWeaponProfBonus(aString);
				}

				featKey = "";
			}
		}

		if (wp != null && cachedWeaponProfs != null)
		{
			cachedWeaponProfs.put(wp.getKeyName(), wp);
		}
		// if (wp != null && !weaponProfList.contains(wp))
		// {
		// weaponProfList.add(wp);
		// }
	}

	private SortedSet<String> addWeaponProfsLists(final List<String> aList,
		final SortedSet<String> aSet, final List<Ability> aFeatList,
		final boolean addIt)
	{
		if ((aList == null) || aList.isEmpty())
		{
			return aSet;
		}

		final String sizeString = "FDTSMLHGC";

		PreParserFactory factory = null;
		try
		{
			factory = PreParserFactory.getInstance();
		}
		catch (PersistenceLayerException e)
		{
			// We won't do prereq testing if we can't get the factory
		}
		for (String profKey : aList)
		{
			final int idx = profKey.indexOf('[');

			if (idx >= 0 && factory != null)
			{
				final StringTokenizer bTok = new StringTokenizer(profKey
					.substring(idx + 1), "[]");
				final List<String> preReqStrings = new ArrayList<String>();

				while (bTok.hasMoreTokens())
				{
					preReqStrings.add(bTok.nextToken());
				}

				profKey = profKey.substring(0, idx);

				if (preReqStrings.size() != 0)
				{
					final List<Prerequisite> preReqList = new ArrayList<Prerequisite>(
						preReqStrings.size());
					for (String preStr : preReqStrings)
					{
						try
						{
							preReqList.add(factory.parse(preStr));
						}
						catch (PersistenceLayerException e)
						{
							// Just skip this one
						}
					}
					if (!PrereqHandler.passesAll(preReqList, this, null))
					{
						continue;
					}
				}
			}

			final int lastComma = profKey.lastIndexOf(',');
			boolean flag = (lastComma < 0);

			if (!flag && (race != null))
			{
				final String eString = profKey.substring(lastComma + 1);
				final int s = sizeInt();

				for (int i = 0; i < eString.length(); ++i)
				{
					if (sizeString.lastIndexOf(eString.charAt(i)) == s)
					{
						flag = true;

						break;
					}
				}

				profKey = profKey.substring(0, lastComma);
			}

			if (flag)
			{
				// 1. Look for an exact equipment match
				// TODO This doesn't make a whole bunch of sense
				final Equipment eq = EquipmentList.getEquipmentKeyed(profKey);

				if (eq != null)
				{
					// Found an exact equipment match; use it
					if (addIt)
					{
						aSet.add(profKey);
						addWeaponProfToList(aFeatList, profKey, true);
					}
				}
				else
				// No exact equipment match found.
				{
					// Set up a place to store located profs
					final List<WeaponProf> addWPs = new ArrayList<WeaponProf>();

					// Check for type separators.
					final boolean dotsFound = profKey.indexOf(".") >= 0;

					// 2. If no dots found, try to find a weapon proficiency
					// specification
					boolean loadedByProfs = false;

					if (!dotsFound)
					{
						// Look for an exact proficiency match
						final WeaponProf prof = Globals
							.getWeaponProfKeyed(profKey);

						if (prof != null)
						{
							addWPs.add(prof);
							loadedByProfs = true;
						}

						// Look for proficiency type matches
						else
						{
							final Collection<WeaponProf> listFromWPType = Globals
								.getAllWeaponProfsOfType(profKey);

							if ((listFromWPType != null)
								&& (!listFromWPType.isEmpty()))
							{
								for (WeaponProf wp : listFromWPType)
								{
									addWPs.add(wp);
								}

								loadedByProfs = true;
							}
						}
					}

					// 3. If dots found (or no profs found), assume weapon types
					if (dotsFound || !loadedByProfs)
					{
						final String desiredTypes = "Weapon." + profKey;
						final List<Equipment> listFromEquipmentType = EquipmentList
							.getEquipmentOfType(desiredTypes, "");

						if ((listFromEquipmentType != null)
							&& (!listFromEquipmentType.isEmpty()))
						{
							for (Equipment e : listFromEquipmentType)
							{
								final WeaponProf prof = e
									.getExpandedWeaponProf(this);
								addWPs.add(prof);
							}
						}
					}

					// Add the located weapon profs to the prof list
					for (WeaponProf wp : addWPs)
					{
						if (addIt)
						{
							final String subProfKey = wp.getKeyName();
							// aSet.add(profKey);
							aSet.add(subProfKey);
							addWeaponProfToList(aFeatList, subProfKey, true);
						}
					}
				}
				// end else( No exact equipment match found )
			}
			// end if(flag)
		}
		// end for()

		// return result set
		return aSet;
	}

	/**
	 * Gets SHIELDPROF strings from all possible PObjects
	 * 
	 * @return List
	 */
	private List<String> getAutoShieldProfList()
	{
		final ArrayList<String> aList = new ArrayList<String>();

		for (PObject aPObj : getPObjectList())
		{
			if (aPObj != null)
			{
				// TODO this is going to just add an empty list
				aPObj.addAutoTagsToList("SHIELDPROF", aList, this, true);
			}
		}

		return aList;
	}

	/**
	 * Get the class level as a String
	 * 
	 * @param className
	 * @param doReplace
	 * @return class level as String
	 */
	public String getClassLevelString(String aClassKey, final boolean doReplace)
	{
		int lvl = 0;
		int idx = aClassKey.indexOf(";BEFORELEVEL=");

		if (idx < 0)
		{
			idx = aClassKey.indexOf(";BEFORELEVEL.");
		}

		if (idx > 0)
		{
			lvl = Integer.parseInt(aClassKey.substring(idx + 13));
			aClassKey = aClassKey.substring(0, idx);
		}

		if (doReplace)
		{
			aClassKey = aClassKey.replace('{', '(').replace('}', ')');
		}

		final PCClass aClass = getClassKeyed(aClassKey);

		if (aClass != null)
		{
			if (lvl > 0)
			{
				return getLevelBefore(aClass.getKeyName(), lvl);
			}

			return Integer.toString(aClass.getLevel());
		}

		return "0";
	}

	private String getLevelBefore(final String classKey, final int charLevel)
	{
		String thisClassKey;
		int lvl = 0;

		for (int idx = 0; idx < charLevel; ++idx)
		{
			thisClassKey = getLevelInfoClassKeyName(idx);

			if (thisClassKey.length() == 0)
			{
				break;
			}

			if (thisClassKey.equals(classKey))
			{
				++lvl;
			}
		}

		return Integer.toString(lvl);
	}

	private List<? extends PObject> getPObjectList()
	{
		// Possible object types include:
		// Campaigns
		// Alignment (PCAlignment)
		// BioSet (ageSet)
		// Check (PObject)
		// Class (PCClass)
		// CompanionMod
		// Deity
		// Domain (CharacterDomain)
		// Equipment (includes EqMods)
		// Feat (virtual feats, auto feats)
		// Race
		// SizeAdjustment
		// Skill
		// Stat (PCStat)
		// Template (PCTemplate)
		//

		final ArrayList<PObject> results = new ArrayList<PObject>();

		// Loaded campaigns
		final List<Campaign> campaigns = Globals.getCampaignList();
		for (final Campaign campaign : campaigns)
		{
			if (campaign != null && campaign.isLoaded())
			{
				results.add(campaign);
				results.addAll(campaign.getSubCampaigns());
			}
		}

		// Alignment
		PCAlignment align = SettingsHandler.getGame().getAlignmentAtIndex(
			getAlignment());
		if (align != null)
		{
			results.add(align);
		}

		// armorProfList is still just a list of Strings
		// results.addAll(getArmorProfList());
		// BioSet
		results.add(Globals.getBioSet());

		results.addAll(SettingsHandler.getGame().getUnmodifiableCheckList());

		// Class
		results.addAll(classList);

		// CompanionMod
		results.addAll(companionModList);

		// Deity
		if (deity != null)
		{
			results.add(deity);
		}

		// Domain (CharacterDomain)
		for (CharacterDomain aCD : characterDomainList)
		{
			final Domain aDomain = aCD.getDomain();

			if (aDomain != null)
			{
				results.add(aDomain);
			}
		}

		// Equipment
		for (Equipment eq : getEquipmentList())
		{
			// Include natural weapons by default as they have an effect even if
			// not equipped.
			if (eq.isEquipped() || eq.isNatural())
			{
				results.add(eq);

				for (EquipmentModifier eqMod : eq.getEqModifierList(true))
				{
					results.add(eqMod);
				}

				for (EquipmentModifier eqMod : eq.getEqModifierList(false))
				{
					results.add(eqMod);
				}
			}
		}

		// Feat (virtual feats, auto feats)
		results.addAll(aggregateFeatList());

		// Race
		if (getRace() != null)
		{
			results.add(getRace());
		}

		// SizeAdjustment
		if (getSizeAdjustment() != null)
		{
			results.add(getSizeAdjustment());
		}

		// Skill
		results.addAll(getSkillList());

		// Stat (PCStat)
		results.addAll(statList.getStatList());

		// Template (PCTemplate)
		results.addAll(getTemplateList());

		// weaponProfList is still just a list of Strings
		// results.addAll(getWeaponProfList());
		return results;
	}

	private void getPreReqFromACType(String aString, final PObject aPObj)
	{
		final StringTokenizer aTok = new StringTokenizer(aString,
			Constants.PIPE);
		String outputString = Constants.PIPE;

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if ((bString.startsWith("PRE") || bString.startsWith("!PRE"))
				&& (bString.indexOf(':') >= 0))
			{
				try
				{
					Logging
						.debugPrint("Why is this prerequisite '"
							+ bString
							+ "' parsed in '"
							+ getClass().getName()
							+ ".getPreReqFromACType()' rather than the persistence layer?");
					final PreParserFactory factory = PreParserFactory
						.getInstance();
					final Prerequisite prereq = factory.parse(bString);
					aPObj.addPreReq(prereq);
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
			else
			{
				outputString += (bString + Constants.PIPE);
			}
		}

		aString = outputString.substring(1); // TODO: value never used
	}

	private List<String> getSelectedShieldProfList()
	{
		final ArrayList<String> aList = new ArrayList<String>();

		for (PObject aPObj : getPObjectList())
		{
			if (aPObj == null)
			{
				continue;
			}

			if (aPObj.containsListFor(ListKey.SELECTED_SHIELD_PROFS))
			{
				aList.addAll(aPObj.getListFor(ListKey.SELECTED_SHIELD_PROFS));
			}
		}

		return aList;
	}

	/**
	 * @todo Need to confirm that getSkillList is sorted, or switch to brute
	 *       force search
	 * @param level
	 */
	private void addNewSkills(final int level)
	{
		final List<Skill> addItems = new ArrayList<Skill>();

		for (Skill aSkill : Globals.getSkillList())
		{
			if (includeSkill(aSkill, level)
				&& (Globals.binarySearchPObject(getSkillList(), aSkill
					.getKeyName()) == null))
			{
				addItems.add((aSkill.clone()));
			}
		}

		getSkillList().addAll(addItems);
		// setDirty(true);
	}

	/**
	 * availableSpells sk4p 13 Dec 2002
	 * 
	 * For learning or preparing a spell: Are there slots available at this
	 * level or higher Fixes BUG [569517]
	 * 
	 * @param level
	 *            the level being checked for availability
	 * @param aClass
	 *            the class under consideration
	 * @param bookName
	 *            the name of the spellbook
	 * @param knownLearned
	 *            "true" if this is learning a spell, "false" if prepping
	 * @param isSpecialtySpell
	 *            "true" if this is a speciality for the given class
	 * @return true or false, a new spell can be added
	 */
	public boolean availableSpells(final int level, final PCClass aClass,
		final String bookName, final boolean knownLearned,
		final boolean isSpecialtySpell)
	{
		boolean available = false;
		final boolean isDivine = ("Divine".equalsIgnoreCase(aClass
			.getSpellType()));
		final boolean canUseHigher = knownLearned ? getUseHigherKnownSlots()
			: getUseHigherPreppedSlots();
		int knownTot;
		int knownNon;
		int knownSpec;
		int memTot;
		int memNon;
		int memSpec;

		// int excTot
		int excNon;

		// int excTot
		int excSpec;
		int lowExcSpec = 0;
		int lowExcNon = 0;
		int goodExcSpec = 0;
		int goodExcNon = 0;

		for (int i = 0; i < level; ++i)
		{
			// Get the number of castable slots
			if (knownLearned)
			{
				knownNon = aClass.getKnownForLevel(i, bookName, this);
				knownSpec = aClass.getSpecialtyKnownForLevel(i, this);
				knownTot = knownNon + knownSpec; // TODO: : value never used
			}
			else
			{
				// Get the number of castable slots
				knownTot = aClass
					.getCastForLevel(i, bookName, true, true, this);
				knownNon = aClass.getCastForLevel(i, bookName, false, true,
					this);
				knownSpec = knownTot - knownNon;
			}

			// Now get the number of spells memorised, total and specialities
			memTot = aClass.memorizedSpellForLevelBook(i, bookName);
			memSpec = aClass.memorizedSpecialtiesForLevelBook(i, bookName);
			memNon = memTot - memSpec;

			// Excess castings
			excSpec = knownSpec - memSpec;
			excNon = knownNon - memNon;

			// Now we spend these slots making up any deficits in lower levels
			//
			while ((excNon > 0) && (lowExcNon < 0))
			{
				--excNon;
				++lowExcNon;
			}

			while ((excSpec > 0) && (lowExcSpec < 0))
			{
				--excSpec;
				++lowExcSpec;
			}

			if (!isDivine || knownLearned)
			{
				// If I'm not divine, I can use non-specialty slots of this
				// level
				// to take up the slack of my excess speciality spells from
				// lower levels.
				while ((excNon > 0) && (lowExcSpec < 0))
				{
					--excNon;
					++lowExcSpec;
				}

				// And I can use non-specialty slots of this level to take
				// up the slack of my excess speciality spells of this level.
				//
				while ((excNon > 0) && (excSpec < 0))
				{
					--excNon;
					++excSpec;
				}
			}

			// Now, if there are slots left over, I don't add them to the
			// running totals.
			// Spell slots of this level won't help me at the next level.
			// Deficits, however, will have to be made up at the next level.
			//
			if (excSpec < 0)
			{
				lowExcSpec += excSpec;
			}

			if (excNon < 0)
			{
				lowExcNon += excNon;
			}
		}

		for (int i = level;; ++i)
		{
			if (knownLearned)
			{
				knownNon = aClass.getKnownForLevel(i, bookName, this);
				knownSpec = aClass.getSpecialtyKnownForLevel(i, this);
				knownTot = knownNon + knownSpec; // for completeness
			}
			else
			{
				// Get the number of castable slots
				knownTot = aClass
					.getCastForLevel(i, bookName, true, true, this);
				knownNon = aClass.getCastForLevel(i, bookName, false, true,
					this);
				knownSpec = knownTot - knownNon;
			}

			// At the level currently being looped through, if the number of
			// casts
			// is zero, that means we have reached a level beyond which no
			// higher-level
			// casts are possible. Therefore, it's time to break.
			// Likewise if we aren't allowed to use higher level slots, no sense
			// in
			// going higher than the spell's level.
			//
			if (knownTot == 0 || (!canUseHigher && i > level))
			{
				break;
			}

			// Now get the number of spells memorised, total and specialities
			memTot = aClass.memorizedSpellForLevelBook(i, bookName);
			memSpec = aClass.memorizedSpecialtiesForLevelBook(i, bookName);
			memNon = memTot - memSpec;

			// Excess castings
			excSpec = knownSpec - memSpec;
			excNon = knownNon - memNon;

			// Now we spend these slots making up any deficits in lower levels
			//
			while ((excNon > 0) && (lowExcNon < 0))
			{
				--excNon;
				++lowExcNon;
			}

			while ((excNon > 0) && (goodExcNon < 0))
			{
				--excNon;
				++goodExcNon;
			}

			while ((excSpec > 0) && (lowExcSpec < 0))
			{
				--excSpec;
				++lowExcSpec;
			}

			while ((excSpec > 0) && (goodExcSpec < 0))
			{
				--excSpec;
				++goodExcSpec;
			}

			if (!isDivine)
			{
				// If I'm not divine, I can use non-specialty slots of this
				// level
				// to take up the slack of my excess speciality spells from
				// lower levels.
				while ((excNon > 0) && (lowExcSpec < 0))
				{
					--excNon;
					++lowExcSpec;
				}

				// And also for levels sufficiently high for the spell that got
				// me
				// into this mess, but of lower level than the level currently
				// being calculated.
				while ((excNon > 0) && (goodExcSpec < 0))
				{
					--excNon;
					++goodExcSpec;
				}

				// And finally use non-specialty slots of this level to take
				// up the slack of excess speciality spells of this level.
				//
				while ((excNon > 0) && (excSpec < 0))
				{
					--excNon;
					++excSpec;
				}
			}

			// Right now, if there are slots left over at this level,
			// it means that there are slots left to add the spell that started
			// all of this.
			if (isDivine)
			{
				if (isSpecialtySpell && (excSpec > 0))
				{
					available = true;
				}

				if (!isSpecialtySpell && (excNon > 0))
				{
					available = true;
				}
			}
			else
			{
				if (!isSpecialtySpell && (excNon > 0))
				{
					available = true;
				}

				if (isSpecialtySpell && ((excNon > 0) || (excSpec > 0)))
				{
					available = true;
				}
			}

			// If we found a slot, we need look no further.
			if (available)
			{
				break;
			}

			// Now, if there are slots left over, I don't add them to the
			// running totals.
			// Spell slots of this level won't help me at the next level.
			// Deficits, however, will have to be made up at the next level.
			//
			if (excSpec < 0)
			{
				goodExcSpec += excSpec;
			}

			if (excNon < 0)
			{
				goodExcNon += excNon;
			}
		}

		return available;
	}

	// private Map<String, List<TypedBonus>> buildBonusMap( final List<BonusObj>
	// aBonusList )
	// {
	// final Map<String, List<TypedBonus>> ret = new HashMap<String,
	// List<TypedBonus>>();
	//		
	// final List<BonusObj> processedList = new ArrayList<BonusObj>();
	//		
	// for ( final BonusObj bonus : aBonusList )
	// {
	// // TODO - The list should be sorted so that static bonuses come first
	// // we could break out of the loop at this point.
	// if (!bonus.isValueStatic())
	// {
	// continue;
	// }
	//			
	// final PObject anObj = (PObject) bonus.getCreatorObject();
	//
	// if (anObj == null)
	// {
	// continue;
	// }
	//
	// // Keep track of which bonuses have been calculated
	// processedList.add(bonus);
	//			
	// ret.putAll(bonus.getTypedBonuses(this));
	// }
	//		
	// // TODO - I don't understand this. The list is sorted with static
	// // bonuses coming first, why not just process the list once?
	// for ( final BonusObj bonus : aBonusList )
	// {
	// if ( processedList.contains(bonus) )
	// {
	// continue;
	// }
	//
	// final PObject anObj = (PObject) bonus.getCreatorObject();
	//
	// if (anObj == null)
	// {
	// continue;
	// }
	// processBonus(bonus, ret, aBonusList, new ArrayList<BonusObj>(),
	// processedList);
	// }
	// return ret;
	// }
	/**
	 * Build the bonus HashMap from all active BonusObj's
	 */
	private void buildActiveBonusMap()
	{
		clearActiveBonusMap();
		processedBonusList.clear();
		setQualifyListStable(false);

		//
		// We do a first pass of just the "static" bonuses
		// as they require less computation and no recursion
		List<BonusObj> bonusListCopy = new ArrayList<BonusObj>();
		bonusListCopy.addAll(getActiveBonusList());
		for (BonusObj bonus : bonusListCopy)
		{
			if (!bonus.isValueStatic())
			{
				continue;
			}

			final PObject anObj = (PObject) bonus.getCreatorObject();

			if (anObj == null)
			{
				continue;
			}

			// Keep track of which bonuses have been calculated
			processedBonusList.add(bonus);

			for (String bString : bonus.getStringListFromBonus(anObj))
			{
				final double iBonus = bonus.getValueAsdouble();
				setActiveBonusStack(iBonus, bString, getActiveBonusMap());
				Logging.debugPrint("BONUS: " + anObj.getDisplayName() + " : "
					+ iBonus + " : " + bString);
			}
		}

		//
		// Now we do all the BonusObj's that require calculations
		bonusListCopy = new ArrayList<BonusObj>();
		bonusListCopy.addAll(getActiveBonusList());
		for (BonusObj bonus : getActiveBonusList())
		{
			if (processedBonusList.contains(bonus))
			{
				continue;
			}

			final PObject anObj = (PObject) bonus.getCreatorObject();

			if (anObj == null)
			{
				continue;
			}

			processBonus(bonus, new ArrayList<BonusObj>());
		}
	}

	/**
	 * Compute total bonus from a List of BonusObj's Use cost of bonus to adjust
	 * total bonus up or down This method takes a list of bonus objects.
	 * 
	 * For each object in the list, it gets the creating object and queries it
	 * for its "COST". It then multiplies the value of the bonus by this cost
	 * and adds it to the cumulative total so far. If subSearch is true, the
	 * choices made in the object that the bonus originated in are searched, the
	 * effective bonus is multiplied by the number of times this bonus appears
	 * in the list.
	 * 
	 * Note: This COST seems to be used for several different things in the code
	 * base, in feats for instance, it is used to modify the feat pool by
	 * amounts other than 1 when selecting a given feat. Here it is used as a
	 * multiplier to say how effective a given bonus is i.e. a bonus with a COST
	 * of 0.5 counts for half its normal value. The COST is limited to a max of
	 * 1, so it can only make bonuses less effective.
	 * 
	 * @param aList
	 *            a list of bonus objects
	 * @param subSearch
	 *            whether to take account of how many times the bonus was
	 *            chosen.
	 * 
	 * @return the calculated cumulative bonus
	 */

	private double calcBonusWithCostFromList(final List<BonusObj> aList,
		final boolean subSearch)
	{
		double totalBonus = 0;

		for (BonusObj aBonus : aList)
		{
			final PObject anObj = (PObject) aBonus.getCreatorObject();

			if (anObj == null)
			{
				continue;
			}

			double iBonus = 0;

			if (aBonus.qualifies(this))
			{
				iBonus = anObj.calcBonusFrom(aBonus, this, this);
			}

			int k = Math
				.max(1, (int) (anObj.getAssociatedCount() * ((HasCost) anObj)
					.getCost()));

			if (subSearch && (anObj.getAssociatedCount() > 0))
			{
				k = 0;

				for (int f = 0; f < anObj.getAssociatedCount(); ++f)
				{
					final String aString = anObj.getAssociated(f);

					if (aString.equalsIgnoreCase(aBonus.getBonusInfo()))
					{
						++k;
					}
				}
			}

			if ((k == 0) && !CoreUtility.doublesEqual(iBonus, 0))
			{
				totalBonus += iBonus;
			}
			else
			{
				totalBonus += (iBonus * k);
			}
		}

		return totalBonus;
	}

	// private void calcPurchaseModeBonuses()
	private List<BonusObj> getPurchaseModeBonuses()
	{
		final GameMode gm = SettingsHandler.getGame();
		final String purchaseMethodName = gm.getPurchaseModeMethodName();
		if (gm.isPurchaseStatMode())
		{
			final PointBuyMethod pbm = gm
				.getPurchaseMethodByName(purchaseMethodName);
			pbm.activateBonuses(this);

			// final List<BonusObj> tempList = pbm.getActiveBonuses();
			// addListToActiveBonuses(tempList);
			return pbm.getActiveBonuses();
		}
		return Collections.emptyList();
	}

	// private void calcTempBonuses()
	private List<BonusObj> getTempBonuses()
	{
		final List<BonusObj> tempList = getFilteredTempBonusList();
		if (tempList.isEmpty())
		{
			return Collections.emptyList();
			// return;
		}
		for (final Iterator<BonusObj> tempIter = tempList.iterator(); tempIter
			.hasNext();)
		{
			final BonusObj bonus = tempIter.next();
			bonus.setApplied(false);

			if (bonus.qualifies(this))
			{
				bonus.setApplied(true);
			}

			if (!bonus.isApplied())
			{
				tempIter.remove();
			}
		}
		// addListToActiveBonuses(tempList);
		return tempList;
	}

	/**
	 * calculate the total racial modifier to save: racial bonuses like the
	 * standard halfling's +1 on all saves template bonuses like the Lightfoot
	 * halfling's +1 on all saves racial base modifiers for certain monsters
	 * 
	 * @param saveIndex
	 * @return int
	 */
	private int calculateSaveBonusRace(final int saveIndex)
	{
		int save;

		if (((saveIndex - 1) < 0)
			|| ((saveIndex - 1) >= SettingsHandler.getGame()
				.getUnmodifiableCheckList().size()))
		{
			return 0;
		}

		final String sString = SettingsHandler.getGame()
			.getUnmodifiableCheckList().get(saveIndex - 1).toString();
		save = (int) race.bonusTo("CHECKS", "BASE." + sString, this, this);
		save += (int) race.bonusTo("CHECKS", sString, this, this);

		return save;
	}

	private void clearActiveBonusMap()
	{
		activeBonusMap.clear();
	}

	/**
	 * returns the level of the highest spell in a given spellbook Yes, divine
	 * casters can have a "spellbook"
	 * 
	 * @param aString
	 * @return spell levels in book
	 */
	int countSpellLevelsInBook(final String aString)
	{
		int levelNum = 0;

		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int sbookNum = Integer.parseInt(aTok.nextToken());

		String bookName = Globals.getDefaultSpellBook();

		if (sbookNum > 0)
		{
			bookName = getSpellBooks().get(sbookNum);
		}

		final PObject aObject = getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			for (levelNum = 0; levelNum >= 0; ++levelNum)
			{
				final List<CharacterSpell> aList = aObject.getSpellSupport()
					.getCharacterSpell(null, bookName, levelNum);

				if (aList.size() < 1)
				{
					break;
				}
			}
		}

		return levelNum;
	}

	/**
	 * returns the number of spells based on class, level and spellbook
	 * 
	 * @param aString
	 * @return int
	 */
	int countSpellListBook(final String aString)
	{
		final int dot = aString.lastIndexOf('.');
		int spellCount = 0;

		if (dot < 0)
		{
			for (PCClass pcClass : classList)
			{
				spellCount += pcClass.getSpellSupport()
					.getCharacterSpellCount();
			}
		}
		else
		{
			final int classNum = Integer.parseInt(aString.substring(17, dot));
			final int levelNum = Integer.parseInt(aString.substring(dot + 1,
				aString.length() - 1));

			final PObject aObject = getSpellClassAtIndex(classNum);

			if (aObject != null)
			{
				final List<CharacterSpell> aList = aObject.getSpellSupport()
					.getCharacterSpell(null, Globals.getDefaultSpellBook(),
						levelNum);
				spellCount = aList.size();
			}
		}

		return spellCount;
	}

	/**
	 * returns the number of times a spell is memorised Tag looks like:
	 * (SPELLTIMES%class&period;%book&period;%level&period;%spell) aString looks
	 * like: SPELLTIMES2&period;-1&period;4&period;15
	 * 
	 * where &period; is a full stop (or period if you are from USA ;p)
	 * 
	 * heavily stolen from replaceTokenSpellMem in ExportHandler.java
	 * 
	 * @param aString
	 * @return spell times
	 */
	int countSpellTimes(final String aString)
	{
		boolean found = false;
		final StringTokenizer aTok = new StringTokenizer(aString.substring(10),
			".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int bookNum = Integer.parseInt(aTok.nextToken());
		final int spellLevel = Integer.parseInt(aTok.nextToken());
		final int spellNumber = Integer.parseInt(aTok.nextToken());

		final PObject aObject = getSpellClassAtIndex(classNum);
		String bookName = Globals.getDefaultSpellBook();

		if (bookNum > 0)
		{
			bookName = getSpellBooks().get(bookNum);
		}

		if ((aObject != null) || (classNum == -1))
		{
			if (classNum == -1)
			{
				bookName = Globals.getDefaultSpellBook();
			}

			if (!"".equals(bookName))
			{
				SpellInfo si = null;

				if (classNum == -1)
				{
					final List<CharacterSpell> charSpellList = new ArrayList<CharacterSpell>();

					for (PCClass pcClass : getClassList())
					{
						final List<CharacterSpell> bList = pcClass
							.getSpellSupport().getCharacterSpell(null,
								bookName, -1);

						for (CharacterSpell cs : bList)
						{
							if (!charSpellList.contains(cs))
							{
								charSpellList.add(cs);
							}
						}
					}

					Collections.sort(charSpellList);

					if (spellNumber < charSpellList.size())
					{
						final CharacterSpell cs = charSpellList
							.get(spellNumber);
						si = cs.getSpellInfoFor(bookName, -1, -1);
						found = true;
					}
				}
				else if (aObject != null)
				{
					final List<CharacterSpell> charSpells = aObject
						.getSpellSupport().getCharacterSpell(null, bookName,
							spellLevel);

					if (spellNumber < charSpells.size())
					{
						final CharacterSpell cs = charSpells.get(spellNumber);
						si = cs.getSpellInfoFor(bookName, spellLevel, -1);
						found = true;
					}
				}

				if (found && (si != null))
				{
					return si.getTimes();
				}
			}
		}

		return 0;
	}

	/**
	 * Counts the number of spells inside a spellbook Yes, divine casters can
	 * have a "spellbook"
	 * 
	 * @param aString
	 * @return spells in a book
	 */
	int countSpellsInBook(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int sbookNum = Integer.parseInt(aTok.nextToken());
		final int levelNum;

		if (sbookNum >= getSpellBooks().size())
		{
			return 0;
		}

		if (aTok.hasMoreTokens())
		{
			levelNum = Integer.parseInt(aTok.nextToken());
		}
		else
		{
			levelNum = -1;
		}

		/*
		 * // Class 0 is special Abilities. Obvious isn't it. if ( classNum == 0 ) {
		 * int count = 0; final Collection<SpellLikeAbility> slas =
		 * this.getSpellLikeAbilities();
		 * 
		 * int categoryCount = -1; String currentCategory =
		 * Constants.EMPTY_STRING; for ( final SpellLikeAbility sla : slas ) {
		 * if ( !currentCategory.equals(sla.getCategory()) ) { categoryCount++;
		 * currentCategory = sla.getCategory(); } if ( categoryCount > sbookNum ) {
		 * break; } if ( categoryCount == sbookNum ) { // This is the
		 * "spellbook" we are looking for count++; } } return count; }
		 */

		String bookName = Globals.getDefaultSpellBook();

		if (sbookNum > 0)
		{
			bookName = getSpellBooks().get(sbookNum);
		}

		final PObject aObject = getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			final List<CharacterSpell> aList = aObject.getSpellSupport()
				.getCharacterSpell(null, bookName, levelNum);

			return aList.size();
		}

		return 0;
	}

	private String findTemplateGender()
	{
		String templateGender = Constants.s_NONE;

		for (PCTemplate template : templateList)
		{
			final String aString = template.getGenderLock();

			if (!aString.equals(Constants.s_NONE))
			{
				templateGender = aString;
			}
		}

		return templateGender;
	}

	private PCClass getClassDisplayNamed(final String aString)
	{
		for (PCClass pcClass : classList)
		{
			if (pcClass.getDisplayClassName().equalsIgnoreCase(aString))
			{
				return pcClass;
			}
		}

		return null;
	}

	private void setEarnedXP(final int argEarnedXP)
	{
		earnedXP = argEarnedXP;
		setDirty(true);
	}

	private int getLAXP()
	{
		// Why +1? Adjustments are deltas, not absolute
		// levels, so are not subject to the "back off one"
		// element of the * algorithm in minXPForLevel. This
		// still means that levelAdjustment of 0 gives you 0
		// XP, but we need LA of 1 to give us 1,000 XP.
		return PlayerCharacterUtilities.minXPForLevel(
			getLevelAdjustment(this) + 1, this);
	}

	private SizeAdjustment getSizeAdjustment()
	{
		final SizeAdjustment sa = SettingsHandler.getGame()
			.getSizeAdjustmentAtIndex(sizeInt());

		return sa;
	}

	int getSpellClassCount()
	{
		return getSpellClassList().size();
	}

	/**
	 * Get the spell class list
	 * 
	 * @return List
	 */
	public List<? extends PObject> getSpellClassList()
	{
		final ArrayList<PObject> aList = new ArrayList<PObject>();

		if (!race.getSpellSupport().getCharacterSpell(null,
			Constants.EMPTY_STRING, -1).isEmpty())
		{
			aList.add(race);
		}

		for (PCClass pcClass : classList)
		{
			if (!pcClass.getSpellType().equalsIgnoreCase(Constants.s_NONE))
			{
				aList.add(pcClass);
			}
		}

		return aList;
	}

	private String checkForVariableInList(final PObject obj,
		final String variableString, final boolean isMax,
		final String matchSrc, final String matchSubSrc, boolean found,
		double value, int decrement)
	{
		boolean flag = false;

		for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
		{
			final String vString = obj.getVariableDefinition(i);
			final StringTokenizer aTok = new StringTokenizer(vString, "|");
			final String src = aTok.nextToken();

			if ((matchSrc.length() > 0) && !src.equals(matchSrc))
			{
				continue;
			}

			if ((matchSubSrc.length() > 0) || (matchSrc.length() > 0))
			{
				final String subSrc = aTok.nextToken();

				if ((matchSubSrc.length() > 0) && !subSrc.equals(matchSubSrc))
				{
					continue;
				}
			}

			if (!aTok.hasMoreTokens())
			{
				continue;
			}

			final String nString = aTok.nextToken();

			if (!aTok.hasMoreTokens())
			{
				continue;
			}

			if (nString.equalsIgnoreCase(variableString))
			{
				final String sString = aTok.nextToken();
				final Float newValue = getVariableValue(sString, src);

				if (!found)
				{
					value = newValue.floatValue();
				}
				else if (isMax)
				{
					value = Math.max(value, newValue.doubleValue());
				}
				else
				{
					value = Math.min(value, newValue.doubleValue());
				}

				found = true;
				flag = true;
			}
		}

		if (flag)
		{
			return value + Constants.EMPTY_STRING;
		}
		return Constants.EMPTY_STRING; // signifies that the variable was found
										// in this list
	}

	/**
	 * Check if the character has the named Deity.
	 * 
	 * @param deityName
	 *            String name of the deity to check for.
	 * @return <code>true</code> if the character has the Deity,
	 *         <code>false</code> otherwise.
	 */
	boolean hasDeity(final String deityName)
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("DEITY");
		prereq.setOperand(deityName);
		prereq.setOperator(PrerequisiteOperator.EQ);

		return PrereqHandler.passes(prereq, this, null);
	}

	private boolean includeSkill(final Skill skill, final int level)
	{
		boolean UntrainedExclusiveClass = false;

		if (skill.isUntrained() && skill.isExclusive())
		{
			if (skill.isClassSkill(classList, this))
			{
				UntrainedExclusiveClass = true;
			}
		}

		return (level == 2) || skill.isRequired()
			|| (skill.getTotalRank(this).floatValue() > 0)
			|| ((level == 1) && skill.isUntrained() && !skill.isExclusive())
			|| ((level == 1) && UntrainedExclusiveClass);
	}

	private void increaseMoveArray(final Double moveRate,
		final String moveType, final Double moveMult, final String multOp)
	{
		// could not find an existing one so
		// need to add new item to array
		//
		final Double[] tempMove = movements;
		final String[] tempType = movementTypes;
		final Double[] tempMult = movementMult;
		final String[] tempMultOp = movementMultOp;

		// now increase the size of the array by one
		movements = new Double[tempMove.length + 1];
		movementTypes = new String[tempMove.length + 1];
		movementMult = new Double[tempMove.length + 1];
		movementMultOp = new String[tempMove.length + 1];

		System.arraycopy(tempMove, 0, movements, 0, tempMove.length);
		System.arraycopy(tempType, 0, movementTypes, 0, tempMove.length);
		System.arraycopy(tempMult, 0, movementMult, 0, tempMove.length);
		System.arraycopy(tempMultOp, 0, movementMultOp, 0, tempMove.length);

		// the size is larger, but arrays start at 0
		// so an array length=3 would have 0, 1, 2 as the targets
		movements[tempMove.length] = moveRate;
		movementTypes[tempMove.length] = moveType;
		movementMult[tempMove.length] = moveMult;
		movementMultOp[tempMove.length] = multOp;
	}

	/**
	 * Change the number of levels a character has in a particular class. Note:
	 * It is assumed that this method is not used as part of loading a
	 * previously saved character. there is no way to bypass the prerequisites
	 * with this method, see: incrementClassLevel(int, PCClass, boolean,
	 * boolean);
	 * 
	 * 
	 * @param numberOfLevels
	 *            number of levels to add
	 * @param globalClass
	 *            the class to add the levels to
	 * @param bSilent
	 *            whether or not to display warning messages
	 */
	public void incrementClassLevel(final int numberOfLevels,
		final PCClass globalClass, final boolean bSilent)
	{
		incrementClassLevel(numberOfLevels, globalClass, bSilent, false);
	}

	/**
	 * Change the number of levels a character has in a particular class. Note:
	 * It is assumed that this method is not used as part of loading a
	 * previously saved character.
	 * 
	 * @param numberOfLevels
	 *            The number of levels to add or remove. If a positive number is
	 *            passed in then that many levels will be added. If the number
	 *            of levels passed in is negative then that many levels will be
	 *            removed from the specified class.
	 * @param globalClass
	 *            The global class from the data store. The class as stored in
	 *            the character will be compared to this one using the
	 *            getClassNamed() method
	 * @param bSilent
	 *            If true do not display any warning messages about adding or
	 *            removing too many levels
	 * @param bypassPrereqs
	 *            Whether we should bypass the checks as to whether or not the
	 *            PC qualifies to take this class. If true, the checks will be
	 *            bypassed
	 */
	public void incrementClassLevel(final int numberOfLevels,
		final PCClass globalClass, final boolean bSilent,
		final boolean bypassPrereqs)
	{
		// If not importing, load the spell list
		if (!isImporting())
		{
			getSpellList();
		}

		// Make sure the character qualifies for the class if adding it
		if (numberOfLevels > 0)
		{
			if (!bypassPrereqs && !globalClass.isQualified(this))
			{
				return;
			}

			if (globalClass.isMonster()
				&& !SettingsHandler.isIgnoreMonsterHDCap()
				&& !race.isAdvancementUnlimited()
				&& ((totalHitDice() + numberOfLevels) > race
					.maxHitDiceAdvancement()) && !bSilent)
			{
				ShowMessageDelegate
					.showMessageDialog(
						"Cannot increase Monster Hit Dice for this character beyond "
							+ race.maxHitDiceAdvancement()
							+ ". This character's current number of Monster Hit Dice is "
							+ totalHitDice(), Constants.s_APPNAME,
						MessageType.INFORMATION);

				return;
			}
		}

		// Check if the character already has the class.
		PCClass pcClassClone = getClassKeyed(globalClass.getKeyName());

		// If the character did not already have the class...
		if (pcClassClone == null)
		{
			// add the class even if setting to level 0
			if (numberOfLevels >= 0)
			{
				// Get a clone of the class so we don't modify the globals!
				pcClassClone = globalClass.clone();

				// Make sure the clone was successful
				if (pcClassClone == null)
				{
					Logging
						.errorPrint("PlayerCharacter::incrementClassLevel => "
							+ "Clone of class " + globalClass.getKeyName()
							+ " failed!");

					return;
				}

				// Add the class to the character classes as level 0
				classList.add(pcClassClone);

				// do the following only if adding a level of a class for the
				// first time
				if (numberOfLevels > 0)
				{
					languages.addAll(pcClassClone
						.getSafeListFor(ListKey.AUTO_LANGUAGES));
				}
			}
			else
			{
				// mod is < 0 and character does not have class. Return.
				return;
			}
		}

		// Add or remove levels as needed
		if (numberOfLevels > 0)
		{
			for (int i = 0; i < numberOfLevels; ++i)
			{
				final PCLevelInfo playerCharacterLevelInfo = saveLevelInfo(pcClassClone
					.getKeyName());
				// if we fail to add the level, remove and return
				if (!pcClassClone.addLevel(playerCharacterLevelInfo, false,
					bSilent, this, false))
				{
					removeLevelInfo(pcClassClone.getKeyName());
					return;
				}
			}
		}
		else if (numberOfLevels < 0)
		{
			for (int i = 0; i < -numberOfLevels; ++i)
			{
				pcClassClone.subLevel(bSilent, this);
				removeLevelInfo(pcClassClone.getKeyName());
			}
		}

		// Handle any feat changes as a result of level changes
		if (!PlayerCharacterUtilities.canReassignTemplateFeats())
		{
			for (PCTemplate template : templateList)
			{
				final List<String> templateFeats = template.feats(
					getTotalLevels(), totalHitDice(), this, true);

				for (int j = 0, y = templateFeats.size(); j < y; ++j)
				{
					AbilityUtilities.modFeatsFromList(this, null, templateFeats
						.get(j), true, false);
				}
			}
		}

		setAggregateAbilitiesStable(null, false);
		// setAggregateFeatsStable(false);
		// setAutomaticFeatsStable(false);
		// setVirtualFeatsStable(false);
		calcActiveBonuses();
		// getAutoWeaponProfs(featAutoList());
		// setDirty(true);
	}

	// private void processBonus( final BonusObj aBonus,
	// Map<String, List<TypedBonus>> aBonusMap,
	// final List<BonusObj> aBonusList,
	// final List<BonusObj> prevProcessed,
	// final List<BonusObj> processed )
	// {
	// // Make sure we don't get into an infinite loop - can occur due to LST
	// coding or best guess dependancy mapping
	// if (prevProcessed.contains(aBonus))
	// {
	// Logging.debugPrint("Ignoring bonus loop for " + aBonus + " as it was
	// already processed. Bonuses already processed: " + prevProcessed);
	// //$NON-NLS-1$//$NON-NLS-2$
	// return;
	// }
	// prevProcessed.add(aBonus);
	//	
	// final List<BonusObj> dependantBonuses = new ArrayList<BonusObj>();
	//
	// // Go through all bonuses and check to see if they add to
	// // aBonus's dependencies and have not already been processed
	// for ( final BonusObj newBonus : aBonusList )
	// {
	// if (processed.contains(newBonus))
	// {
	// continue;
	// }
	//
	// if (aBonus.getDependsOn(newBonus.getBonusInfo()))
	// {
	// dependantBonuses.add(newBonus);
	// }
	// }
	//
	// // go through all the BonusObj's that aBonus depends on
	// // and process them first
	// for ( final BonusObj newBonus : dependantBonuses )
	// {
	// // Recursively call itself
	// processBonus(newBonus, aBonusMap, aBonusList, prevProcessed, processed);
	// }
	//
	// // Double check that it hasn't been processed yet
	// if (processed.contains(aBonus))
	// {
	// return;
	// }
	//
	// // Add to processed list
	// processed.add(aBonus);
	//
	// final PObject anObj = (PObject) aBonus.getCreatorObject();
	//
	// if (anObj == null)
	// {
	// prevProcessed.remove(aBonus);
	// return;
	// }
	//		
	// aBonusMap.putAll(aBonus.getTypedBonuses(this));
	//
	// prevProcessed.remove(aBonus);
	// }

	/**
	 * - Get's a list of dependencies from aBonus - Finds all active bonuses
	 * that add to those dependencies and have not been processed and
	 * recursively calls itself - Once recursed in, it adds the computed bonus
	 * to activeBonusMap
	 * 
	 * @param aBonus
	 *            The bonus to be processed.
	 * @param prevProcessed
	 *            The list of bonuses which have already been processed in this
	 *            run.
	 */
	private void processBonus(final BonusObj aBonus,
		final ArrayList<BonusObj> prevProcessed)
	{
		// Make sure we don't get into an infinite loop - can occur due to LST
		// coding or best guess dependancy mapping
		if (prevProcessed.contains(aBonus))
		{
			Logging
				.debugPrint("Ignoring bonus loop for " + aBonus + " as it was already processed. Bonuses already processed: " + prevProcessed); //$NON-NLS-1$//$NON-NLS-2$
			return;
		}
		prevProcessed.add(aBonus);

		final List<BonusObj> aList = new ArrayList<BonusObj>();

		// Go through all bonuses and check to see if they add to
		// aBonus's dependencies and have not already been processed
		for (BonusObj newBonus : getActiveBonusList())
		{
			if (processedBonusList.contains(newBonus))
			{
				continue;
			}

			if (aBonus.getDependsOn(newBonus.getBonusInfo()))
			{
				aList.add(newBonus);
			}
		}

		// go through all the BonusObj's that aBonus depends on
		// and process them first
		for (BonusObj newBonus : aList)
		{
			// Recursively call itself
			processBonus(newBonus, prevProcessed);
		}

		// Double check that it hasn't been processed yet
		if (processedBonusList.contains(aBonus))
		{
			return;
		}

		// Add to processed list
		processedBonusList.add(aBonus);

		final PObject anObj = (PObject) aBonus.getCreatorObject();

		if (anObj == null)
		{
			prevProcessed.remove(aBonus);
			return;
		}

		// calculate bonus and add to activeBonusMap
		for (String bString : aBonus.getStringListFromBonus(anObj))
		{
			final double iBonus = anObj.calcBonusFrom(aBonus, this, bString,
				this);
			setActiveBonusStack(iBonus, bString, getActiveBonusMap());
			Logging.debugPrint("BONUS: " + anObj.getDisplayName() + " : "
				+ iBonus + " : " + bString);
		}
		prevProcessed.remove(aBonus);
	}

	private boolean qualifiesForFeat(final Ability aFeat)
	{
		return aFeat.canBeSelectedBy(this);
	}

	private boolean hasSkill(final String aSkillKey)
	{
		return (getSkillKeyed(aSkillKey) != null);
	}

	private void rebuildLists(final PCClass toClass, final PCClass fromClass,
		final int iCount, final PlayerCharacter aPC)
	{
		final int fromLevel = fromClass.getLevel();
		final int toLevel = toClass.getLevel();

		for (int i = 0; i < iCount; ++i)
		{
			fromClass.doMinusLevelMods(this, fromLevel - i);

			final PCLevelInfo playerCharacterLevelInfo = aPC.getLevelInfoFor(
				toClass.getKeyName(), toLevel + i + 1);
			toClass.doPlusLevelMods(toLevel + i + 1, aPC,
				playerCharacterLevelInfo);
		}
	}

	private void removeExcessSkills(final int level)
	{
		// Elaborate code here is in order to avoid a
		// ConcurrentModificationException
		List<Skill> skills = getSkillList();
		List<Skill> skillIndexList = new ArrayList<Skill>();
		skillIndexList.addAll(skills);
		final Iterator<Skill> skillIter = skillIndexList.iterator();
		boolean modified = false;
		while (skillIter.hasNext())
		{
			Skill skill = skillIter.next();

			if (!includeSkill(skill, level))
			{
				skills.remove(skill);
				modified = true;
			}
		}

		if (modified)
		{
			setDirty(true);
		}
	}

	private boolean removeLevelInfo(final String classKeyName)
	{
		for (int idx = pcLevelInfo.size() - 1; idx >= 0; --idx)
		{
			final PCLevelInfo li = pcLevelInfo.get(idx);

			if (li.getClassKeyName().equals(classKeyName))
			{
				removeObjectsForLevelInfo(li);

				removeLevelInfo(idx);
				setDirty(true);

				return true;
			}
		}

		return false;
	}

	/**
	 * @param li
	 */
	private void removeObjectsForLevelInfo(final PCLevelInfo li)
	{
		for (PObject object : li.getObjects())
		{

			// remove this object from the feats lists
			for (Iterator<Ability> iterator = getRealFeatList().iterator(); iterator
				.hasNext();)
			{
				final Ability feat = iterator.next();
				if (object == feat)
				{
					iterator.remove();
				}
			}
			// remove this object from the feats lists
			for (Iterator<Ability> iterator = stableVirtualFeatList.iterator(); iterator
				.hasNext();)
			{
				final Ability feat = iterator.next();
				if (object == feat)
				{
					iterator.remove();
				}
			}
		}
	}

	private void removeLevelInfo(final int idx)
	{
		pcLevelInfo.remove(idx);
		setDirty(true);
	}

	/**
	 * <code>rollStats</code> roll Globals.s_ATTRIBLONG.length random stats
	 * Method: 1: 4d6 Drop Lowest 2: 3d6 3: 5d6 Drop 2 Lowest 4: 4d6 reroll 1's
	 * drop lowest 5: 4d6 reroll 1's and 2's drop lowest 6: 3d6 +5 7: 5d6 Drop
	 * lowest and middle as per FREQ #458917
	 * 
	 * @param method
	 *            the method to be used for rolling.
	 */
	public void rollStats(final int method)
	{
		int aMethod = method;
		if (SettingsHandler.getGame().isPurchaseStatMode())
		{
			aMethod = Constants.CHARACTERSTATMETHOD_PURCHASE;
		}
		rollStats(aMethod, statList.getStatList(), SettingsHandler.getGame()
			.getCurrentRollingMethod(), false);
	}

	public void rollStats(final int method, final List<PCStat> aStatList,
		final GameModeRollMethod rollMethod, boolean aSortedFlag)
	{
		int[] rolls = new int[aStatList.size()];

		for (int i = 0; i < rolls.length; i++)
		{
			switch (method)
			{
				case Constants.CHARACTERSTATMETHOD_PURCHASE:
					rolls[i] = SettingsHandler.getGame()
						.getPurchaseModeBaseStatScore(this);
					break;
				case Constants.CHARACTERSTATMETHOD_ALLSAME:
					rolls[i] = SettingsHandler.getGame().getAllStatsValue();
					break;

				case Constants.CHARACTERSTATMETHOD_ROLLED:
					final String diceExpression = rollMethod.getMethodRoll();
					rolls[i] = RollingMethods.roll(diceExpression);
					break;

				default:
					rolls[i] = 0;
					break;
			}
		}
		if (aSortedFlag)
		{
			Arrays.sort(rolls);
		}

		for (int stat = aStatList.size() - 1, i = 0; stat >= 0; stat--, i++)
		{
			PCStat currentStat = aStatList.get(stat);

			currentStat.setBaseScore(0);

			if (!currentStat.isRolled())
			{
				continue;
			}

			int roll = rolls[i] + currentStat.getBaseScore();

			if (roll < currentStat.getMinValue())
			{
				roll = currentStat.getMinValue();
			}

			if (roll > currentStat.getMaxValue())
			{
				roll = currentStat.getMaxValue();
			}

			currentStat.setBaseScore(roll);
		}

		this.setPoolAmount(0);
		this.costPool = 0;
		languages.clear();
		getAutoLanguages();
		setPoolAmount(0);
	}

	/**
	 * Sorts the provided list of equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in list.
	 * 
	 * @param unsortedEquipList
	 *            An ArrayList of the equipment to be sorted.
	 * @return An ArrayList of the equipment objects in output order.
	 */
	private List<Equipment> sortEquipmentList(
		final List<Equipment> unsortedEquipList)
	{
		return sortEquipmentList(unsortedEquipList, Constants.MERGE_ALL);
	}

	/**
	 * Sorts the provided list of equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in list.
	 * 
	 * @param unsortedEquipList
	 *            An ArrayList of the equipment to be sorted.
	 * @param merge
	 *            How to merge.
	 * @return An ArrayList of the equipment objects in output order.
	 */
	private List<Equipment> sortEquipmentList(
		final List<Equipment> unsortedEquipList, final int merge)
	{
		if (unsortedEquipList.isEmpty())
		{
			return unsortedEquipList;
		}

		final List<Equipment> sortedList;

		// Merge list for duplicates
		// The sorting is done during the Merge
		sortedList = EquipmentUtilities.mergeEquipmentList(unsortedEquipList,
			merge);

		// Remove the hidden items from the list
		for (Iterator<Equipment> i = sortedList.iterator(); i.hasNext();)
		{
			final Equipment item = i.next();

			if (item.getOutputIndex() == -1)
			{
				i.remove();
			}
		}

		return sortedList;
	}

	private int subCalcACOfType(final StringTokenizer aTok)
	{
		int total = 0;

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			total += Integer.parseInt(BonusToken.getBonusToken(
				"BONUS.COMBAT.AC." + aString, this));
		}

		return total;
	}

	/**
	 * @param prefix
	 * @return Total bonus for prefix from the activeBonus HashMap
	 */
	private double sumActiveBonusMap(String prefix)
	{
		double bonus = 0;
		prefix = prefix.toUpperCase();

		final List<String> aList = new ArrayList<String>();

		// There is a risk that the active bonus map may be modified by other
		// threads, so we use a for loop rather than an iterator so that we
		// still get an answer.
		Object[] keys = getActiveBonusMap().keySet().toArray();
		for (int i = 0; i < keys.length; i++)
		{
			final String aKey = (String) keys[i];

			// aKey is either of the form:
			// COMBAT.AC
			// or
			// COMBAT.AC:Luck
			// or
			// COMBAT.AC:Armor.REPLACE
			if (aList.contains(aKey))
			{
				continue;
			}

			String rString = aKey;

			// rString could be something like:
			// COMBAT.AC:Armor.REPLACE
			// So need to remove the .STACK or .REPLACE
			// to get a match for prefix like: COMBAT.AC:Armor
			if (rString.endsWith(".STACK"))
			{
				rString = rString.substring(0, rString.length() - 6);
			}
			else if (rString.endsWith(".REPLACE"))
			{
				rString = rString.substring(0, rString.length() - 8);
			}

			// if prefix is of the form:
			// COMBAT.AC
			// then is must match rstring:
			// COMBAT.AC
			// COMBAT.AC:Luck
			// COMBAT.AC:Armor.REPLACE
			// However, it must not match
			// COMBAT.ACCHECK
			if ((rString.length() > prefix.length())
				&& !rString.startsWith(prefix + ":"))
			{
				continue;
			}

			if (rString.startsWith(prefix))
			{
				aList.add(rString);
				aList.add(rString + ".STACK");
				aList.add(rString + ".REPLACE");

				final double aBonus = getActiveBonusForMapKey(rString,
					Double.NaN);
				final double replaceBonus = getActiveBonusForMapKey(rString
					+ ".REPLACE", Double.NaN);
				final double stackBonus = getActiveBonusForMapKey(rString
					+ ".STACK", 0);
				//
				// Using NaNs in order to be able to get the max
				// between an undefined bonus and a negative
				//
				if (Double.isNaN(aBonus)) // no bonusKey
				{
					if (!Double.isNaN(replaceBonus))
					{
						// no bonusKey, but there
						// is a replaceKey
						bonus += replaceBonus;
					}
				}
				else if (Double.isNaN(replaceBonus))
				{
					// is a bonusKey and no replaceKey
					bonus += aBonus;
				}
				else
				{
					// is a bonusKey and a replaceKey
					bonus += Math.max(aBonus, replaceBonus);
				}

				// always add stackBonus
				bonus += stackBonus;
			}
		}

		return bonus;
	}

	private int totalMonsterLevels()
	{
		int totalLevels = 0;

		for (PCClass pcClass : classList)
		{
			if (pcClass.isMonster())
			{
				totalLevels += pcClass.getLevel();
			}
		}

		// This is already accounted for in the monster levels above
		// for (Iterator e = companionModList.iterator(); e.hasNext();)
		// {
		// final CompanionMod cMod = (CompanionMod) e.next();
		// totalLevels += cMod.getHitDie();
		// }

		return totalLevels;
	}

	/**
	 * @param descriptionLst
	 *            The descriptionLst to set.
	 */
	private void setDescriptionLst(final String descriptionLst)
	{
		this.descriptionLst = descriptionLst;
	}

	private class CasterLevelSpellBonus
	{
		private int bonus;
		private String type;

		/**
		 * Constructor
		 * 
		 * @param b
		 * @param t
		 */
		public CasterLevelSpellBonus(final int b, final String t)
		{
			bonus = b;
			type = t;
		}

		/**
		 * Get bonus
		 * 
		 * @return bonus
		 */
		public int getBonus()
		{
			return (bonus);
		}

		/**
		 * Get type
		 * 
		 * @return type
		 */
		public String getType()
		{
			return (type);
		}

		/**
		 * Set bonus
		 * 
		 * @param newBonus
		 */
		public void setBonus(final int newBonus)
		{
			bonus = newBonus;
		}

		@Override
		public String toString()
		{
			return ("bonus: " + bonus + "    type: " + type);
		}

	}

	/**
	 * @param info
	 * @return character level
	 */
	public int getCharacterLevel(final PCLevelInfo info)
	{
		int i = 1;
		for (PCLevelInfo element : pcLevelInfo)
		{
			if (info == element)
			{
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Return a list of bonus languages which the character may select from.
	 * This function is not efficient, but is sufficient for it's current use of
	 * only being called when the user requests the bonus language selection
	 * list. Note: A check will be made for the ALL language and it will be
	 * replaced with the current list of languages in globals. These should be
	 * further restricted by the prerequisites of the languages to ensure that
	 * 'secret' languages are not offered.
	 * 
	 * @return List of bonus languages for the character.
	 */
	public Set<Language> getLanguageBonusSelectionList()
	{
		Set<Language> languageList = new HashSet<Language>();

		// Race
		languageList.addAll(race.getLanguageBonus());

		// Templates
		for (PCTemplate template : templateList)
		{
			languageList.addAll(template.getLanguageBonus());
		}

		// Classes
		for (PCClass pcClass : classList)
		{
			languageList.addAll(pcClass.getLanguageBonus());
		}

		// Scan for the ALL language and if found replace it with all languages
		boolean addAll = false;
		for (Iterator<Language> iter = languageList.iterator(); iter.hasNext();)
		{
			final Language lang = iter.next();
			if (lang.isAllLang())
			{
				iter.remove();
				addAll = true;
			}
		}
		if (addAll)
		{
			languageList.addAll(Globals.getLanguageList());
		}

		return languageList;
	}

	/**
	 * Retrieve the bonus for the stat excluding either temporary bonuses,
	 * equipment bonuses or both. This method ensure stacking rules are applied
	 * to all included bonuses. If not excluding either, it is quicker to use
	 * getTotalBonusTo.
	 * 
	 * @param statAbbr
	 *            The short name of the stat to calculate the bonus for.
	 * @param useTemp
	 *            Should temp bonuses be included?
	 * @param useEquip
	 *            Should equipment bonuses be included?
	 * @return The bonus to the stat.
	 */
	@Deprecated
	public int getPartialStatBonusFor(String statAbbr, boolean useTemp,
		boolean useEquip)
	{
		// List<BonusObj> abl = getAllActiveBonuses();
		List<BonusObj> abl = getActiveBonusList();
		final String prefix = "STAT." + statAbbr;
		Map<String, String> bonusMap = new HashMap<String, String>();

		for (BonusObj bonus : abl)
		{
			if (bonus.isApplied() && bonus.getBonusName().equals("STAT"))
			{
				boolean found = false;
				for (Object element : bonus.getBonusInfoList())
				{
					if (element instanceof PCStat
						&& ((PCStat) element).getAbb().equals(statAbbr))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					continue;
				}

				// The bonus has been applied to the target stat
				// Should it be included?
				boolean addIt = false;
				if (bonus.getCreatorObject() instanceof Equipment
					|| bonus.getCreatorObject() instanceof EquipmentModifier)
				{
					addIt = useEquip;
				}
				else if (tempBonusList.contains(bonus))
				{
					addIt = useTemp;
				}
				else
				{
					addIt = true;
				}
				if (addIt)
				{
					// Grab the list of relevant types so that we can build up
					// the
					// bonuses with the stacking rules applied.
					List<String> typeList = bonus
						.getStringListFromBonus((PObject) bonus
							.getCreatorObject());
					for (String element : typeList)
					{
						if (element.startsWith(prefix))
						{
							setActiveBonusStack(bonus.getCalculatedValue(this),
								element, bonusMap);
						}
					}
				}
			}
		}

		// Sum the included bonuses to the stat to get our result.
		int total = 0;
		for (String bKey : bonusMap.keySet())
		{
			total += Float.parseFloat(bonusMap.get(bKey));
		}
		return total;
	}

	/**
	 * Retrieve the stat as it was at a particular level excluding either
	 * temporary bonuses, equipment bonuses or both. This method ensures
	 * stacking rules are applied to all included bonuses. If not excluding
	 * either, it is quicker to use getTotalStatAtLevel.
	 * 
	 * @param statAbb
	 *            The short name of the stat to calculate the value of.
	 * @param level
	 *            The level we want to see the stat at.
	 * @param usePost
	 *            Should stat mods that occurred after levelling be included?
	 * @param useTemp
	 *            Should temp bonuses be included?
	 * @param useEquip
	 *            Should equipment bonuses be included?
	 * @return The stat as it was at the level
	 */
	public int getPartialStatAtLevel(String statAbb, int level,
		boolean usePost, boolean useTemp, boolean useEquip)
	{
		int curStat = getStatList().getPartialStatFor(statAbb, useTemp,
			useEquip);
		for (int idx = getLevelInfoSize() - 1; idx >= level; --idx)
		{
			final int statLvlAdjust = pcLevelInfo.get(idx).getTotalStatMod(
				statAbb, usePost);
			curStat -= statLvlAdjust;
		}

		return curStat;
	}

	/**
	 * Returns a deep copy of the PlayerCharacter. Note: This method does a
	 * shallow copy of many lists in here that seem to point to "system"
	 * objects. These copies should be validated before using this method.
	 * 
	 * @return a new deep copy of the <code>PlayerCharacter</code>
	 */
	@Override
	public Object clone()
	{
		PlayerCharacter aClone = null;

		// calling super.clone won't work because it will not create
		// new data instances for all the final variables and I won't
		// be able to reset them. Need to call new PlayerCharacter()
		// aClone = (PlayerCharacter)super.clone();
		aClone = new PlayerCharacter();

		aClone.addArmorProfs(getArmorProfList());

		for (final Ability a : this.getRealFeatList())
		{
			aClone.addRealAbility(AbilityCategory.FEAT, (a.clone()));
		}
		for (final AbilityCategory cat : theAbilities.getKeySet())
		{
			for (final Ability a : getRealAbilityList(cat))
			{
				aClone.addRealAbility(cat, a.clone());
			}
		}

		aClone.miscList.addAll(getMiscList());
		for (NoteItem n : getNotesList())
		{
			aClone.addNotesItem(n);
		}
		aClone.primaryWeapons.addAll(getPrimaryWeapons());
		aClone.secondaryWeapons.addAll(getSecondaryWeapons());
		aClone.shieldProfList.addAll(getShieldProfList());
		for (Skill skill : getSkillList())
		{
			aClone.skillList.add((skill.clone()));
		}
		aClone.specialAbilityList.addAll(getSpecialAbilityList());
		aClone.templateList.addAll(getTemplateList());
		for (String s : this.variableList)
		{
			aClone.variableList.add(new String(s));
		}
		aClone.gold = new BigDecimal(gold.toString());
		// Points to a global deity object so it doesn't need to be cloned.
		aClone.deity = deity;
		aClone.domainSourceMap = new HashMap<String, String>(domainSourceMap);
		aClone.characterDomainList.addAll(characterDomainList);
		for (PCClass pcClass : classList)
		{
			aClone.classList.add((pcClass.clone()));
		}
		aClone.companionModList.addAll(companionModList);
		aClone.qualifyArrayMap.addAllLists(qualifyArrayMap);
		if (followerMaster != null)
		{
			aClone.followerMaster = (Follower) followerMaster.clone();
		}
		else
		{
			aClone.followerMaster = null;
		}
		for (EquipSet eqSet : equipSetList)
		{
			aClone.addEquipSet((EquipSet) eqSet.clone());
		}
		aClone.equipmentList.addAll(equipmentList);
		aClone.equipmentMasterList.addAll(equipmentMasterList);
		for (PCLevelInfo info : pcLevelInfo)
		{
			aClone.pcLevelInfo.add((PCLevelInfo) info.clone());
		}
		for (String book : spellBooks)
		{
			aClone.addSpellBook(new String(book));
		}
		aClone.tempBonusItemList.addAll(tempBonusItemList);
		aClone.tempBonusList.addAll(tempBonusList);
		aClone.tempBonusFilters.addAll(tempBonusFilters);
		aClone.race = race;
		aClone.favoredClasses.addAll(favoredClasses);

		aClone.statList.clear();
		for (PCStat stat : statList)
		{
			aClone.statList.addStat(stat.clone());
		}
		if (kitList != null)
		{
			aClone.kitList = new ArrayList<Kit>();
			aClone.kitList.addAll(kitList);
		}
		// Not sure what this is. It may need to be cloned.
		aClone.spellTracker = spellTracker;
		aClone.templateAutoLanguages.addAll(templateAutoLanguages);
		aClone.templateLanguages.addAll(templateLanguages);
		aClone.setBio(new String(getBio()));
		aClone.setBirthday(new String(getBirthday()));
		aClone.setBirthplace(new String(getBirthplace()));
		aClone.setCatchPhrase(new String(getCatchPhrase()));
		aClone.setCurrentEquipSetName(new String(getCurrentEquipSetName()));
		aClone.setDescription(new String(getDescription()));
		aClone.setDescriptionLst(new String(getDescriptionLst()));
		aClone.setEyeColor(new String(getEyeColor()));
		aClone.setFileName(new String(getFileName()));
		aClone.setGender(new String(getGender()));
		aClone.setHairColor(new String(getHairColor()));
		aClone.setHairStyle(new String(getHairStyle()));
		aClone.setHanded(new String(getHanded()));
		aClone.setInterests(new String(getInterests()));
		aClone.setLocation(new String(getLocation()));
		aClone.setName(new String(getName()));
		aClone.setPhobias(new String(getPhobias()));
		aClone.setPlayersName(new String(getPlayersName()));
		aClone.setPortraitPath(new String(getPortraitPath()));
		if (getRegion() != null)
		{
			aClone.setRegion(new String(getRegion()));
		}
		aClone.setResidence(new String(getResidence()));
		aClone.setSkinColor(new String(getSkinColor()));
		aClone.setSpeechTendency(new String(getSpeechTendency()));
		if (getSubRegion() != null)
		{
			aClone.setSubRegion(new String(getSubRegion()));
		}
		aClone.tabName = new String(tabName);
		aClone.setTrait1(new String(getTrait1()));
		aClone.setTrait2(new String(getTrait2()));
		aClone.languages.addAll(languages);
		if (theWeaponProfs != null)
		{
			aClone.theWeaponProfs = new TreeSet<WeaponProf>();
			aClone.theWeaponProfs.addAll(theWeaponProfs);
		}
		aClone.autoKnownSpells = autoKnownSpells;
		aClone.autoLoadCompanion = autoLoadCompanion;
		aClone.autoSortGear = autoSortGear;
		aClone.outputSheetHTML = new String(outputSheetHTML);
		aClone.outputSheetPDF = new String(outputSheetPDF);
		aClone.ageSetKitSelections = new boolean[10];

		System.arraycopy(ageSetKitSelections, 0, aClone.ageSetKitSelections, 0, ageSetKitSelections.length);

		aClone.serial = serial;
		// Not sure what this is for
		aClone.displayUpdate = displayUpdate;
		aClone.setImporting(false);
		aClone.useTempMods = useTempMods;
		aClone.setFeats(feats);
		aClone.age = age;
		aClone.alignment = alignment;
		aClone.costPool = costPool;
		aClone.currentEquipSetNumber = currentEquipSetNumber;
		aClone.earnedXP = earnedXP;
		aClone.equipOutputOrder = equipOutputOrder;
		aClone.freeLangs = freeLangs;
		aClone.heightInInches = heightInInches;
		aClone.poolAmount = poolAmount;

		// order in which the skills will be output.
		aClone.skillsOutputOrder = skillsOutputOrder;
		aClone.spellLevelTemp = spellLevelTemp;
		aClone.weightInPounds = weightInPounds;
		// Is this OK?
		aClone.variableProcessor = new VariableProcessorPC(aClone);
		aClone.pointBuyPoints = pointBuyPoints;

		aClone.setDirty(true);
		// TODO - ABILITYOBJECT
		// aClone.setAggregateAbilitiesStable(null, false);
		aClone.setAutomaticFeatsStable(false);
		aClone.setVirtualFeatsStable(false);
		aClone.setQualifyListStable(false);
		aClone.adjustMoveRates();
		aClone.calcActiveBonuses();

		return aClone;
	}

	/**
	 * Set the string for the characteristic
	 * 
	 * @param key
	 * @param s
	 */
	public void setStringFor(StringKey key, String s)
	{
		stringChar.put(key, s);
		setDirty(true);
	}

	/**
	 * Remove the string for the characteristic
	 * 
	 * @param key
	 * @return string removed
	 */
	public String removeStringFor(StringKey key)
	{
		return stringChar.remove(key);
	}

	private Float getEquippedQty(EquipSet eSet, Equipment eqI)
	{
		final String rPath = eSet.getIdPath();

		for (EquipSet es : getEquipSet())
		{
			String esIdPath = es.getIdPath() + EquipSet.PATH_SEPARATOR;
			String rIdPath = rPath + EquipSet.PATH_SEPARATOR;

			if (!esIdPath.startsWith(rIdPath))
			{
				continue;
			}

			if (eqI.getName().equals(es.getValue()))
			{
				return es.getQty();
			}
		}

		return Float.valueOf(0);
	}

	/**
	 * This method gets a list of locations for a weapon
	 * 
	 * @param hands
	 * @param multiHand
	 * @return weapon location choices
	 */
	private static List<String> getWeaponLocationChoices(final int hands,
		final String multiHand)
	{
		final List<String> result = new ArrayList<String>(hands + 2);

		if (hands > 0)
		{
			result.add(Constants.S_PRIMARY);

			for (int i = 1; i < hands; ++i)
			{
				if (i > 1)
				{
					result.add(Constants.S_SECONDARY + ' ' + i);
				}
				else
				{
					result.add(Constants.S_SECONDARY);
				}
			}

			if (multiHand.length() > 0)
			{
				result.add(multiHand);
			}
		}

		return result;
	}

	/**
	 * If an item can only go in one location, return the name of that location
	 * to add to an EquipSet
	 * 
	 * @param eqI
	 * @return single location
	 */
	private String getSingleLocation(Equipment eqI)
	{
		// Handle natural weapons
		if (eqI.isNatural())
		{
			if (eqI.getSlots(this) == 0)
			{
				// TODO - Yuck. This should not look at the name!!
				if (eqI.modifiedName().endsWith("Primary"))
				{
					return Constants.S_NATURAL_PRIMARY;
				}
				return Constants.S_NATURAL_SECONDARY;
			}
		}

		// Always force weapons to go through the chooser dialog
		// unless they are also armor (ie: with Armor Spikes)
		if ((eqI.isWeapon()) && !(eqI.isArmor()))
		{
			return Constants.EMPTY_STRING;
		}

		List<EquipSlot> eqSlotList = SystemCollections
			.getUnmodifiableEquipSlotList();

		if ((eqSlotList == null) || eqSlotList.isEmpty())
		{
			return Constants.EMPTY_STRING;
		}

		for (EquipSlot es : eqSlotList)
		{
			// see if this EquipSlot can contain this item TYPE
			if (es.canContainType(eqI.getType()))
			{
				return es.getSlotName();
			}
		}

		return Constants.EMPTY_STRING;
	}

	/**
	 * Returns a list of String locations the sepcified Equipment can be
	 * equipped to.
	 * 
	 * @param eqSet
	 * @param eqI
	 * @param containers
	 * @return
	 */
	public List<String> getEquippableLocations(EquipSet eqSet, Equipment eqI,
		List<String> containers)
	{
		// Some Equipment locations are based on the number of hands
		int hands = getHands();

		List<String> aList = new ArrayList<String>();

		if (eqI.isWeapon())
		{
			if (eqI.isUnarmed())
			{
				aList.add(Constants.S_UNARMED);
			}
			else if (eqI.isShield())
			{
				aList.add(Constants.S_SHIELD);
			}
			else if (eqI.isWeaponOutsizedForPC(this))
			{
				// do nothing for outsized weapons
			}
			else
			{
				if (eqI.isWeaponOneHanded(this))
				{
					aList = getWeaponLocationChoices(hands, Constants.S_BOTH);
				}
				else
				{
					aList.add(Constants.S_BOTH);
				}

				if (eqI.isMelee() && eqI.isDouble())
				{
					aList.add(Constants.S_DOUBLE);
				}
			}
		}
		else
		{
			String locName = getSingleLocation(eqI);

			if (locName.length() != 0)
			{
				aList.add(locName);
			}
			else
			{
				aList.add(Constants.S_EQUIPPED);
			}
		}

		if (!eqI.isUnarmed())
		{
			aList.add(Constants.S_CARRIED);
			aList.add(Constants.S_NOTCARRIED);
		}

		//
		// Generate a list of containers
		//
		if (containers != null)
		{
			if (eqSet != null)
			{
				final String idPath = eqSet.getIdPath();

				// process all EquipSet Items
				for (EquipSet es : getEquipSet())
				{
					String esID = es.getParentIdPath()
						+ EquipSet.PATH_SEPARATOR;
					String abID = idPath + EquipSet.PATH_SEPARATOR;

					if (esID.startsWith(abID))
					{
						Equipment eq = es.getItem();
						if ((eq != null) && eq.isContainer())
						{
							containers.add(eq.getName());
						}
					}
				}
			}
		}

		return aList;
	}

	/**
	 * returns true if you can put Equipment into a location in EquipSet
	 * 
	 * @param eSet
	 * @param locName
	 * @param eqI
	 * @param eqTarget
	 * @return true if equipment can be added
	 */
	public boolean canEquipItem(EquipSet eSet, String locName, Equipment eqI,
		Equipment eqTarget)
	{
		final String idPath = eSet.getIdPath();

		// If target is a container, allow it
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			// TODO - Should make sure eqI can be contained by eqTarget
			return true;
		}

		// If Carried/Equipped/Not Carried slot
		// allow as many as they would like
		if (locName.startsWith(Constants.S_CARRIED)
			|| locName.startsWith(Constants.S_EQUIPPED)
			|| locName.startsWith(Constants.S_NOTCARRIED))
		{
			return true;
		}

		// allow as many unarmed items as you'd like
		if (eqI.isUnarmed())
		{
			return true;
		}

		// allow many Secondary Natural weapons
		if (locName.equals(Constants.S_NATURAL_SECONDARY))
		{
			return true;
		}

		// Don't allow weapons that are too large for PC
		if (eqI.isWeapon() && eqI.isWeaponOutsizedForPC(this)
			&& !eqI.isNatural())
		{
			return false;
		}

		// make a HashMap to keep track of the number of each
		// item that is already equipped to a slot
		Map<String, String> slotMap = new HashMap<String, String>();

		for (EquipSet es : getEquipSet())
		{
			String esID = es.getParentIdPath() + EquipSet.PATH_SEPARATOR;
			String abID = idPath + EquipSet.PATH_SEPARATOR;

			if (!esID.startsWith(abID))
			{
				continue;
			}

			// check to see if we already have
			// an item in that particular location
			if (es.getName().equals(locName))
			{
				final Equipment eItem = es.getItem();
				final String nString = slotMap.get(locName);
				int existNum = 0;

				if (nString != null)
				{
					existNum = Integer.parseInt(nString);
				}

				if (eItem != null)
				{
					existNum += eItem.getSlots(this);
				}

				slotMap.put(locName, String.valueOf(existNum));
			}
		}

		for (EquipSet es : getEquipSet())
		{
			String esID = es.getParentIdPath() + EquipSet.PATH_SEPARATOR;
			String abID = idPath + EquipSet.PATH_SEPARATOR;

			if (!esID.startsWith(abID))
			{
				continue;
			}

			// if it's a weapon we have to do some
			// checks for hands already in use
			if (eqI.isWeapon())
			{
				// weapons can never occupy the same slot
				if (es.getName().equals(locName))
				{
					return false;
				}

				// if Double Weapon or Both Hands, then no
				// other weapon slots can be occupied
				if ((locName.equals(Constants.S_BOTH) || locName
					.equals(Constants.S_DOUBLE))
					&& (es.getName().equals(Constants.S_PRIMARY)
						|| es.getName().equals(Constants.S_SECONDARY)
						|| es.getName().equals(Constants.S_BOTH) || es
						.getName().equals(Constants.S_DOUBLE)))
				{
					return false;
				}

				// inverse of above case
				if ((locName.equals(Constants.S_PRIMARY) || locName
					.equals(Constants.S_SECONDARY))
					&& (es.getName().equals(Constants.S_BOTH) || es.getName()
						.equals(Constants.S_DOUBLE)))
				{
					return false;
				}
			}

			// If we already have an item in that location
			// check to see how many are allowed in that slot
			if (es.getName().equals(locName))
			{
				final String nString = slotMap.get(locName);
				int existNum = 0;

				if (nString != null)
				{
					existNum = Integer.parseInt(nString);
				}

				existNum += eqI.getSlots(this);

				EquipSlot eSlot = Globals.getEquipSlotByName(locName);

				if (eSlot == null)
				{
					return true;
				}

				// if the item takes more slots, return false
				if (existNum > (eSlot.getSlotCount() + (int) getTotalBonusTo(
					"SLOTS", eSlot.getContainType())))
				// if (existNum > (eSlot.getSlotCount() + (int)
				// getBonusValue("SLOTS", eSlot.getContainType())))
				{
					return false;
				}

				return true;
			}
		}

		return true;
	}

	/**
	 * Checks to see if Equipment exists in selected EquipSet and if so, then
	 * return the EquipSet containing eqI
	 * 
	 * @param eSet
	 * @param eqI
	 * @return EquipSet
	 */
	private EquipSet getEquipSetForItem(EquipSet eSet, Equipment eqI)
	{
		final String rPath = eSet.getIdPath();

		for (EquipSet es : getEquipSet())
		{
			String esIdPath = es.getIdPath() + EquipSet.PATH_SEPARATOR;
			String rIdPath = rPath + EquipSet.PATH_SEPARATOR;

			if (!esIdPath.startsWith(rIdPath))
			{
				continue;
			}

			if (eqI.getName().equals(es.getValue()))
			{
				return es;
			}
		}

		return null;
	}

	/**
	 * returns new id_Path with the last id one higher than the current highest
	 * id for EquipSets with the same ParentIdPath
	 * 
	 * @param eSet
	 * @return new id path
	 */
	private String getNewIdPath(EquipSet eSet)
	{
		String pid = EquipSet.ROOT_ID;
		int newID = 0;

		if (eSet != null)
		{
			pid = eSet.getIdPath();
		}

		for (EquipSet es : getEquipSet())
		{
			if (es.getParentIdPath().equals(pid) && (es.getId() > newID))
			{
				newID = es.getId();
			}
		}

		++newID;

		return pid + EquipSet.PATH_SEPARATOR + newID;
	}

	public EquipSet addEquipToTarget(final EquipSet eSet,
		final Equipment eqTarget, String locName, final Equipment eqI,
		Float newQty)
	{
		float tempQty = 1.0f;
		if (newQty != null)
		{
			tempQty = newQty.floatValue();
		}
		else
		{
			newQty = Float.valueOf(tempQty);
		}
		boolean addAll = false;
		boolean mergeItem = false;

		Equipment masterEq = getEquipmentNamed(eqI.getName());
		if (masterEq == null)
		{
			return null;
		}
		float diffQty = masterEq.getQty().floatValue()
			- getEquippedQty(eSet, eqI).floatValue();

		// if newQty is less than zero, we want to
		// add all of this item to the EquipSet
		// or all remaining items that havn't already
		// been added to the EquipSet
		if (newQty.floatValue() < 0.0f)
		{
			tempQty = diffQty;
			newQty = new Float(tempQty + getEquippedQty(eSet, eqI).floatValue());
			addAll = true;
		}

		// Check to make sure this EquipSet does not exceed
		// the PC's equipmentList number for this item
		if (tempQty > diffQty)
		{
			return null;
		}

		// check to see if the target item is a container
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			// set these to newQty just for testing
			eqI.setQty(newQty);
			eqI.setNumberCarried(newQty);

			// Make sure the container accepts items
			// of this type and is not full
			if (eqTarget.canContain(this, eqI) == 1)
			{
				locName = eqTarget.getName();
				addAll = true;
				mergeItem = true;
			}
			else
			{
				return null;
			}
		}

		// If locName is empty equip this item to its default location.
		// If there is more than one option return with an error.
		if ((locName == null)
			|| ((locName != null) && ("".equals(locName) || (locName.length() == 0))))
		{
			locName = getSingleLocation(eqI);

			if (locName.length() == 0)
			{
				return null;
			}
		}

		// make sure we can add item to that slot in this EquipSet
		if (!canEquipItem(eSet, locName, eqI, eqTarget))
		{
			return null;
		}

		if (eqI.isContainer())
		{
			// don't merge containers
			mergeItem = false;
		}

		EquipSet existingSet = getEquipSetForItem(eSet, eqI);

		if (addAll && mergeItem && (existingSet != null))
		{
			newQty = new Float(tempQty + getEquippedQty(eSet, eqI).floatValue());
			existingSet.setQty(newQty);
			eqI.setQty(newQty);
			eqI.setNumberCarried(newQty);
			setDirty(true);

			if ((eqTarget != null) && eqTarget.isContainer())
			{
				eqTarget.updateContainerContentsString(this);
			}

			return existingSet;
		}
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			eqTarget.insertChild(this, eqI);
			eqI.setParent(eqTarget);
		}

		// construct the new IdPath
		// new id is one larger than any
		// other id at this path level
		String id = getNewIdPath(eSet);

		// now create a new EquipSet to add
		// this Equipment item to
		EquipSet newSet = new EquipSet(id, locName, eqI.getName(), eqI);

		// set the Quantity of equipment
		eqI.setQty(newQty);
		newSet.setQty(newQty);

		addEquipSet(newSet);
		setDirty(true);

		return newSet;
	}

	/**
	 * Get the String for a characteristic
	 * 
	 * @param key
	 * @return String
	 */
	public String getStringFor(StringKey key)
	{
		return stringChar.get(key);
	}

	/**
	 * Gets a 'safe' String representation
	 * 
	 * @param key
	 * @return a 'safe' String
	 */
	public String getSafeStringFor(StringKey key)
	{
		String s = stringChar.get(key);
		if (s == null)
		{
			s = Constants.EMPTY_STRING;
		}
		return s;
	}

	/**
	 * Sets if ADD: level abilities should be processed when incrementing a
	 * level.
	 * 
	 * <p>
	 * <b>Note</b>: This is kind of a hack used by the Kit code to allow a kit
	 * to specify what the level abilities are.
	 * 
	 * @param yesNo
	 *            Yes if level increases should process ADD: level abilities.
	 */
	public void setDoLevelAbilities(boolean yesNo)
	{
		processLevelAbilities = yesNo;
	}

	/**
	 * Returns if level increases will process ADD: level abilities.
	 * 
	 * @return <tt>true</tt> if ADD: level abilites will be processed.
	 */
	public boolean doLevelAbilities()
	{
		return processLevelAbilities;
	}

	/**
	 * Whether to allow adjustment of the Global Feat pool
	 * 
	 * @param allow
	 */
	public void setAllowFeatPoolAdjustment(boolean allow)
	{
		this.allowFeatPoolAdjustment = allow;
	}

	// /**
	// * Returns a list of Spell-Like Abilities for the character.
	// *
	// * <p>The list is sorted by Category, Frequency (most to least), and
	// * Spell name.
	// *
	// * @return A Sorted Set of Spell-Like Abilities.
	// */
	// public Collection<SpellLikeAbility> getSpellLikeAbilities()
	// {
	// final SortedSet<SpellLikeAbility> ret = new TreeSet<SpellLikeAbility>(new
	// Comparator<SpellLikeAbility>() {
	//
	// public int compare(final SpellLikeAbility anO1, final SpellLikeAbility
	// anO2)
	// {
	// final Collator collator = Collator.getInstance();
	// // Sort order is Category, Frequency (most -> least), Spell Name
	// int iRet = collator.compare( anO1.getCategory(), anO2.getCategory() );
	// if ( iRet == 0 )
	// {
	// final int freq1 = getVariableValue(anO1.getNumUses(),
	// this.getClass().getName()).intValue();
	// final int freq2 = getVariableValue(anO2.getNumUses(),
	// this.getClass().getName()).intValue();
	// // TODO - Handle Units
	// if ( freq1 == freq2 )
	// {
	// final Spell spell1 = Globals.getSpellKeyed(anO1.getSpellKey());
	// final Spell spell2 = Globals.getSpellKeyed(anO2.getSpellKey());
	// if ( spell1 != null )
	// {
	// if ( spell2 != null )
	// {
	// return collator.compare( spell1.getDisplayName(), spell2.getDisplayName()
	// );
	// }
	// else
	// {
	// return 1;
	// }
	// }
	// else if ( spell2 != null )
	// {
	// return -1;
	// }
	// else
	// {
	// return 0;
	// }
	// }
	// return freq1 > freq2 ? 1 : -1;
	// }
	//				
	// return iRet;
	// }
	//			
	// });
	// for ( final PObject pobj : getPObjectList() )
	// {
	// ret.addAll( pobj.getSpellLikeAbilities() );
	// }
	//		
	// return ret;
	// }
	//
	// /**
	// * Gets a <tt>Collection</tt> of category names for the Spell-Like
	// Abilities
	// * for the character.
	// *
	// * @return List of category names.
	// */
	// public List<String> getSpellLikeAbilityCategories()
	// {
	// final Collection<SpellLikeAbility> slas = this.getSpellLikeAbilities();
	// final List<String> ret = new ArrayList<String>();
	//		
	// String currentCategory = Constants.EMPTY_STRING;
	// for ( final SpellLikeAbility sla : slas )
	// {
	// if ( !currentCategory.equals(sla.getCategory()) )
	// {
	// currentCategory = sla.getCategory();
	// ret.add(currentCategory);
	// }
	// }
	//		
	// return ret;
	// }
	//	
	// /**
	// * Returns a <tt>List</tt> of Spell-Like Abilities for the specified
	// * category (spellbook).
	// *
	// * @param aCategory A category (spellbook) to return SLAs for.
	// *
	// * @return An unmodifialbe <tt>List</tt> of <tt>SpellLikeAbility</tt>
	// * objects.
	// */
	// public List<SpellLikeAbility> getSpellLikeAbilities( final String
	// aCategory )
	// {
	// final List<SpellLikeAbility> slas = new ArrayList<SpellLikeAbility>();
	//		
	// for ( final SpellLikeAbility sla : slas )
	// {
	// if ( sla.getCategory().equals( aCategory ) )
	// {
	// slas.add( sla );
	// }
	// }
	// return Collections.unmodifiableList( slas );
	// }

	/*
	 * For debugging purposes Dumps contents of spell books to System.err
	 * 
	 * static public void dumpSpells(final PlayerCharacter pc) { final List
	 * bookList = pc.getSpellBooks(); for(int bookIdx = 0; bookIdx <
	 * bookList.size(); ++bookIdx) { final String bookName = (String)
	 * pc.getSpellBooks().get(bookIdx);
	 * 
	 * System.err.println("=========="); System.err.println("Book:" + bookName);
	 * final List casterList = pc.getSpellClassList(); for(int casterIdx = 0;
	 * casterIdx < casterList.size(); ++casterIdx) { final PObject aCaster =
	 * (PObject) casterList.get(casterIdx); final List spellList =
	 * aCaster.getCharacterSpellList(); if (spellList == null) { continue; }
	 * System.err.println("Class/Race:" + aCaster.getName());
	 * 
	 * for (Iterator i = spellList.iterator(); i.hasNext();) { final
	 * CharacterSpell cs = (CharacterSpell) i.next();
	 * 
	 * for (Iterator csi = cs.getInfoListIterator(); csi.hasNext();) { final
	 * SpellInfo sInfo = (SpellInfo) csi.next(); if
	 * (bookName.equals(sInfo.getBook())) {
	 * System.err.println(cs.getSpell().getOutputName() + sInfo.toString() + "
	 * level:" + Integer.toString(sInfo.getActualLevel())); } } } } } }
	 */

	// --------------------------------------------------
	// Feat/Ability stuff
	// --------------------------------------------------
	// List of Feats
	private final ArrayList<Ability> featList = new ArrayList<Ability>();
	private List<Ability> stableAggregateFeatList = null;
	private List<Ability> stableAutomaticFeatList = null;
	private List<Ability> stableVirtualFeatList = null;

	// Whether one can trust the most recently calculated aggregateFeatList
	private boolean aggregateFeatsStable = false;
	// Whether one can trust the most recently calculated automaticFeatList
	// private boolean automaticFeatsStable = false;
	// Whether one can trust the most recently calculated virtualFeatList
	private boolean virtualFeatsStable = false;

	// whether to adjust the feat pool when requested
	private boolean allowFeatPoolAdjustment = true;

	// pool of feats remaining to distribute
	private double feats = 0;

	/**
	 * Set aggregate Feats stable
	 * 
	 * @param stable
	 */
	private void setAggregateFeatsStable(final boolean stable)
	{
		aggregateFeatsStable = stable;
		setVirtualFeatsStable(stable);
		setAutomaticFeatsStable(stable);
		if (!stable)
		{
			cachedWeaponProfs = null;
		}
		// setDirty(true);
	}

	public void setAggregateAbilitiesStable(final AbilityCategory aCategory,
		final boolean stable)
	{
		if (!stable)
		{
			cachedWeaponProfs = null;
		}
		if (aCategory == AbilityCategory.FEAT)
		{
			setAggregateFeatsStable(stable);
			return;
		}
		if (aCategory == null)
		{
			if (!stable)
			{
				// Clear all the categories
				for (final AbilityCategory cat : theAbilities.getKeySet())
				{
					setAggregateAbilitiesStable(cat, stable);
				}
			}
			setAggregateFeatsStable(stable);
			return;
		}
		if (!stable)
		{
			theAbilities.put(aCategory, Ability.Nature.AUTOMATIC, null);
			// TODO - Deal with non-aggregate virtual abilities (i.e. from ADD:)
			theAbilities.put(aCategory, Ability.Nature.VIRTUAL, null);
		}
	}

	/**
	 * Returns TRUE if all types (automatic, virtual and aggregate) of feats are
	 * stable
	 * 
	 * @return TRUE or FALSE
	 */
	private boolean isAggregateFeatsStable()
	{
		return (stableAutomaticFeatList != null) && virtualFeatsStable
			&& aggregateFeatsStable;
	}

	public boolean isAggregateAbilitiesStable(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return isAggregateFeatsStable();
		}
		return theAbilities.get(aCategory, Ability.Nature.NORMAL) != null
			&& theAbilities.get(aCategory, Ability.Nature.AUTOMATIC) != null
			&& theAbilities.get(aCategory, Ability.Nature.VIRTUAL) != null;
	}

	/**
	 * Sets a 'stable' list of automatic feats
	 * 
	 * @param stable
	 */
	private void setAutomaticFeatsStable(final boolean stable)
	{
		if (!stable)
		{
			stableAutomaticFeatList = null;
		}
	}

	public void setAutomaticAbilitiesStable(final AbilityCategory aCategory,
		final boolean stable)
	{
		if (aCategory == null)
		{
			if (!stable)
			{
				for (final AbilityCategory cat : theAbilities.getKeySet())
				{
					theAbilities.put(cat, Ability.Nature.AUTOMATIC, null);
				}
			}
			setAutomaticFeatsStable(stable);
			return;
		}
		if (aCategory == AbilityCategory.FEAT)
		{
			setAutomaticFeatsStable(stable);
			return;
		}
		if (!stable)
		{
			theAbilities.put(aCategory, Ability.Nature.AUTOMATIC, null);
		}
	}

	/**
	 * Add a "real" (not virtual or auto) feat to the character
	 * 
	 * @param aFeat
	 *            the Ability (of category FEAT) to add
	 * 
	 * @return true if added successfully
	 */
	private boolean addRealFeat(final Ability aFeat)
	{
		// return abilityStore.addCategorisable(aFeat)
		return featList.add(aFeat);
	}

	public boolean addRealAbility(final AbilityCategory aCategory,
		final Ability anAbility)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return addRealFeat(anAbility);
		}

		if (anAbility == null)
		{
			return false;
		}
		anAbility.setFeatType(Ability.Nature.NORMAL);
		List<Ability> abilities = theAbilities.get(aCategory,
			Ability.Nature.NORMAL);
		if (abilities == null)
		{
			abilities = new ArrayList<Ability>();
			theAbilities.put(aCategory, Ability.Nature.NORMAL, abilities);
		}
		abilities.add(anAbility);
		return true;
	}

	/**
	 * Remove all "real" (not auto or auto) feats from the character
	 */
	private void clearRealFeats()
	{
		featList.clear();
	}

	public void clearRealAbilities(final AbilityCategory aCategory)
	{
		if (aCategory == null)
		{
			for (final AbilityCategory cat : theAbilities.getKeySet())
			{
				theAbilities.put(cat, Ability.Nature.NORMAL, null);
			}
			return;
		}
		if (aCategory == AbilityCategory.FEAT)
		{
			clearRealFeats();
			return;
		}

		theAbilities.put(aCategory, Ability.Nature.NORMAL, null);
	}

	/**
	 * Get number of "real" (not virtual or auto) feats the character has
	 * 
	 * @return DOCUMENT ME!
	 */
	private int getNumberOfRealFeats()
	{
		return featList.size();
	}

	public int getNumberOfRealAbilities(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getNumberOfRealFeats();
		}

		final List<Ability> abilities = theAbilities.get(aCategory,
			Ability.Nature.NORMAL);
		if (abilities == null)
		{
			return 0;
		}
		return abilities.size();
	}

	private List<Ability> getRealFeatsList()
	{
		return new ArrayList<Ability>(featList);
	}

	public List<Ability> getRealAbilitiesList(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getRealFeatsList();
		}

		final List<Ability> abilities = theAbilities.get(aCategory,
			Ability.Nature.NORMAL);
		if (abilities == null)
		{
			return Collections.emptyList();
		}
		return new ArrayList<Ability>(abilities);
	}

	/**
	 * Get an iterator over all the feats "Real" feats For Example, not virtual
	 * or auto
	 * 
	 * @return an iterator
	 */
	public List<Ability> getRealFeatList()
	{
		return featList;
	}

	public List<Ability> getRealAbilityList(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getRealFeatList();
		}
		final List<Ability> ret = theAbilities.get(aCategory,
			Ability.Nature.NORMAL);
		if (ret == null)
		{
			return Collections.emptyList();
		}
		return ret;
	}

	/**
	 * Returns the Feat definition searching by key (not name), as contained in
	 * the <b>chosen</b> feat list.
	 * 
	 * @param featName
	 *            String key of the feat to check for.
	 * 
	 * @return the Feat (not the CharacterFeat) searched for, <code>null</code>
	 *         if not found.
	 */
	public Ability getRealFeatKeyed(final String featName)
	{
		return getFeatKeyed(featName, featList);
	}

	public Ability getRealAbilityKeyed(final AbilityCategory aCategory,
		final String aKey)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getRealFeatKeyed(aKey);
		}

		final List<Ability> abilities = theAbilities.get(aCategory,
			Ability.Nature.NORMAL);

		if (abilities != null)
		{
			for (final Ability ability : abilities)
			{
				if (ability.getKeyName().equals(aKey))
				{
					return ability;
				}
			}
		}

		return null;
	}

	/**
	 * Returns the Feat definition searching by name, as contained in the <b>
	 * chosen</b> feat list.
	 * 
	 * @param featName
	 *            String key of the feat to check for.
	 * 
	 * @return the Feat (not the CharacterFeat) searched for, <code>null</code>
	 *         if not found.
	 */

	public Ability getRealFeatNamed(final String featName)
	{
		return AbilityUtilities.getAbilityFromList(featList, "FEAT", featName,
			Ability.Nature.ANY);
	}

	/**
	 * Does the character have this feat (not virtual or auto).
	 * 
	 * @param aFeat
	 *            The Ability object (of category FEAT) to check
	 * 
	 * @return True if the character has the feat
	 */
	public boolean hasRealFeat(final Ability aFeat)
	{
		// return (abilityStore.getCategorisableNamed(aCategory, aFeat) !=
		// null);
		return featList.contains(aFeat);
	}

	/**
	 * Does the character have this ability (not virtual or auto).
	 * 
	 * @param aCategory
	 *            The ability category to check.
	 * @param anAbility
	 *            The Ability object (of category FEAT) to check
	 * 
	 * @return True if the character has the feat
	 */
	public boolean hasRealAbility(final AbilityCategory aCategory,
		final Ability anAbility)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return hasRealFeat(anAbility);
		}

		final List<Ability> abilities = theAbilities.get(aCategory,
			Ability.Nature.NORMAL);
		if (abilities == null)
		{
			return false;
		}
		return abilities.contains(anAbility);
	}

	/**
	 * Checks if the character has the specified ability.
	 * 
	 * <p>
	 * If <tt>aCategory</tt> is <tt>null</tt> then all categories that have
	 * the same innate ability category will be checked.
	 * <p>
	 * If <tt>anAbilityType</tt> is <tt>ANY</tt> then all Natures will be
	 * checked for the ability.
	 * 
	 * @param aCategory
	 *            An <tt>AbilityCategory</tt> or <tt>null</tt>
	 * @param anAbilityType
	 *            A <tt>Nature</tt>.
	 * @param anAbility
	 *            The <tt>Ability</tt> to check for.
	 * 
	 * @return <tt>true</tt> if the character has the ability with the
	 *         criteria specified.
	 */
	public boolean hasAbility(final AbilityCategory aCategory,
		final Ability.Nature anAbilityType, final Ability anAbility)
	{
		final List<AbilityCategory> categories;
		if (aCategory == null)
		{
			// A null category means we have to check all categories for
			// abilities of the same innate category as the passed in one.
			categories = new ArrayList<AbilityCategory>();
			final Collection<AbilityCategory> allCategories = SettingsHandler
				.getGame().getAllAbilityCategories();
			for (final AbilityCategory cat : allCategories)
			{
				if (cat.getAbilityCategory().equals(anAbility.getCategory()))
				{
					categories.add(cat);
				}
			}
		}
		else
		{
			categories = new ArrayList<AbilityCategory>();
			categories.add(aCategory);
		}

		final int start, end;
		if (anAbilityType == Ability.Nature.ANY)
		{
			start = 0;
			end = Ability.Nature.values().length - 2;
		}
		else
		{
			start = end = anAbilityType.ordinal();
		}
		for (int i = start; i <= end; i++)
		{
			final Ability.Nature nature = Ability.Nature.values()[i];
			boolean hasIt = false;
			for (final AbilityCategory cat : categories)
			{
				if (cat == AbilityCategory.FEAT)
				{
					switch (nature)
					{
						case NORMAL:
							hasIt = hasRealAbility(cat, anAbility);
							break;
						case AUTOMATIC:
							hasIt = hasAutomaticAbility(cat, anAbility);
							break;
						case VIRTUAL:
							hasIt = hasVirtualAbility(cat, anAbility);
							break;
					}
				}
				else
				{
					final List<Ability> abilities = theAbilities.get(cat,
						nature);
					if (abilities == null)
					{
						continue;
					}
					if (abilities.contains(anAbility))
					{
						hasIt = true;
					}
				}
				if (hasIt)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if the characterFeat ArrayList contains the named Feat.
	 * 
	 * @param featName
	 *            String name of the feat to check for.
	 * @return <code>true</code> if the character has the feat,
	 *         <code>false</code> otherwise.
	 */

	public boolean hasRealFeatNamed(final String featName)
	{
		return AbilityUtilities.getAbilityFromList(featList, "FEAT", featName,
			Ability.Nature.ANY) != null;
	}

	/**
	 * Remove a "real" (for example, not virtual or auto) feat from the
	 * character.
	 * 
	 * @param aFeat
	 *            the Ability (of category FEAT) to remove
	 * @return True if successfully removed
	 */
	public boolean removeRealFeat(final Ability aFeat)
	{
		return featList.remove(aFeat);
	}

	public boolean removeRealAbility(final AbilityCategory aCategory,
		final Ability anAbility)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return removeRealFeat(anAbility);
		}

		final List<Ability> abilities = theAbilities.get(aCategory,
			Ability.Nature.NORMAL);
		if (abilities == null)
		{
			return false;
		}
		return abilities.remove(anAbility);
	}

	public void adjustFeats(final double arg)
	{
		if (allowFeatPoolAdjustment)
		{
			feats += arg;
		}
		setDirty(true);
	}

	public void adjustAbilities(final AbilityCategory aCategory,
		final BigDecimal arg)
	{
		if (arg.equals(BigDecimal.ZERO))
		{
			return;
		}
		if (aCategory == AbilityCategory.FEAT)
		{
			adjustFeats(arg.doubleValue());
			return;
		}
		if (theUserPoolBonuses == null)
		{
			theUserPoolBonuses = new HashMap<AbilityCategory, BigDecimal>();
		}
		BigDecimal userMods = theUserPoolBonuses.get(aCategory);
		if (userMods != null)
		{
			userMods = userMods.add(arg);
		}
		else
		{
			userMods = arg;
		}
		theUserPoolBonuses.put(aCategory, userMods);
		setDirty(true);
	}

	// TODO - This method is ridiculously dangerous.
	public void setFeats(final double arg)
	{
		if (allowFeatPoolAdjustment)
		{
			feats = arg;
		}
		setDirty(true);
	}

	public void setAbilities(final AbilityCategory aCategory, final double arg)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			setFeats(arg);
			return;
		}

		// TODO: What about other types of ability pools?
	}

	public void setUserPoolBonus(final AbilityCategory aCategory,
		final BigDecimal anAmount)
	{
		if (theUserPoolBonuses == null)
		{
			theUserPoolBonuses = new HashMap<AbilityCategory, BigDecimal>();
		}
		theUserPoolBonuses.put(aCategory, anAmount);
	}

	public double getUserPoolBonus(final AbilityCategory aCategory)
	{
		BigDecimal userBonus = null;
		if (theUserPoolBonuses != null)
		{
			userBonus = theUserPoolBonuses.get(aCategory);
		}
		if (userBonus == null)
		{
			return 0.0d;
		}
		return userBonus.doubleValue();
	}

	public BigDecimal getTotalAbilityPool(final AbilityCategory aCategory)
	{
		Float basePool = this.getVariableValue(aCategory.getPoolFormula(),
			getClass().toString());
		if (!aCategory.allowFractionalPool())
		{
			basePool = new Float(basePool.intValue());
		}
		double bonus = getTotalBonusTo("ABILITYPOOL", aCategory.getKeyName());
		// double bonus = getBonusValue("ABILITYPOOL", aCategory.getKeyName());
		if (!aCategory.allowFractionalPool())
		{
			bonus = Math.floor(bonus);
		}
		// User bonuses already handle the fractional pool flag.
		final double userBonus = getUserPoolBonus(aCategory);
		return BigDecimal.valueOf(basePool + bonus + userBonus);
	}

	public List<Ability> getSelectedAbilities(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getRealFeatList();
		}
		return getRealAbilityList(aCategory);
	}

	public double getFeats()
	{
		if (Globals.getGameModeHasPointPool())
		{
			return getSkillPoints();
		}
		return getRawFeats(true);
	}

	public BigDecimal getAvailableAbilityPool(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return BigDecimal.valueOf(getFeats());
		}
		return getTotalAbilityPool(aCategory).subtract(
			getAbilityPoolSpent(aCategory));
	}

	public double getRawFeats(final boolean bIncludeBonus)
	{
		double retVal = feats;
		if (bIncludeBonus)
		{
			retVal += getBonusFeatPool();
		}
		return retVal;
	}

	/**
	 * Query whether this PC should be able to select the ability passed in.
	 * That is, does the PC meet the prerequisites and is the feat not one the
	 * PC already has, or if the PC has the feat already, is it one that can be
	 * taken multiple times.
	 * 
	 * @param anAbility
	 *            the ability to test
	 * @return true if the PC can take, false otherwise
	 */
	public boolean canSelectAbility(final Ability anAbility)
	{
		return this.canSelectAbility(anAbility, false);
	}

	/**
	 * Query whether this PC should be able to select the ability passed in.
	 * That is, does the PC meet the prerequisites and is the feat not one the
	 * PC already has, or if the PC has the feat already, is it one that can be
	 * taken multiple times. TODO: When the PlayerCharacter Object can have
	 * abilities of category other than "FEAT" it will likely have methods to
	 * test "hasRealAbility" and "hasVirtualAbilty", change this (or add
	 * another) to deal with them
	 * 
	 * @param anAbility
	 *            the ability to test
	 * @param autoQualify
	 *            if true, the PC automatically meets the prerequisites
	 * @return true if the PC can take, false otherwise
	 */
	public boolean canSelectAbility(final Ability anAbility,
		final boolean autoQualify)
	{
		final boolean qualify = this.qualifiesForFeat(anAbility);
		final boolean canTakeMult = anAbility.isMultiples();
		final boolean hasOrdinary = this.hasRealFeat(anAbility);
		final boolean hasAuto = this.hasFeatAutomatic(anAbility.getKeyName());

		final boolean notAlreadyHas = !(hasOrdinary || hasAuto);

		return (autoQualify || qualify) && (canTakeMult || notAlreadyHas);
	}

	/**
	 * get unused feat count
	 * 
	 * @return unused feat count
	 */
	public double getUsedFeatCount()
	{
		double iCount = 0;

		for (Ability aFeat : getRealFeatList())
		{
			//
			// Don't increment the count for
			// hidden feats so the number
			// displayed matches this number
			//
			// if (aFeat.getVisibility() == Visibility.HIDDEN)
			// {
			// continue;
			// }
			final int subfeatCount = aFeat.getAssociatedCount();

			if (subfeatCount > 1)
			{
				iCount += subfeatCount;
			}
			else
			{
				iCount += aFeat.getCost(this);
			}
		}

		return iCount;
	}

	public BigDecimal getAbilityPoolSpent(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return BigDecimal.valueOf(getUsedFeatCount());
		}

		double spent = 0.0d;

		final List<Ability> abilities = getSelectedAbilities(aCategory);
		if (abilities != null)
		{
			for (final Ability ability : abilities)
			{
				final int subfeatCount = ability.getAssociatedCount();

				if (subfeatCount > 1)
				{
					spent += subfeatCount;
				}
				else
				{
					if (!aCategory.allowFractionalPool())
					{
						spent += (int) Math.ceil(ability.getCost(this));
					}
					else
					{
						spent += ability.getCost(this);
					}
				}
			}
		}
		if (!aCategory.allowFractionalPool())
		{
			return BigDecimal.valueOf((int) Math.ceil(spent));
		}
		return BigDecimal.valueOf(spent);
	}

	public double getUsedAbilityCount(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getUsedFeatCount();
		}

		return getAbilityPoolSpent(aCategory).doubleValue();
	}

	private void setVirtualFeatsStable(final boolean stable)
	{
		virtualFeatsStable = stable;
		// setDirty(true);
	}

	public void setVirtualAbilitiesStable(final AbilityCategory aCategory,
		final boolean stable)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			setVirtualFeatsStable(stable);
			return;
		}
		if (!stable)
		{
			theAbilities.put(aCategory, Ability.Nature.VIRTUAL, null);
		}
	}

	public void addFeat(final Ability aFeat,
		final PCLevelInfo playerCharacterLevelInfo)
	{
		if (hasRealFeat(aFeat))
		{
			Logging.errorPrint("Adding duplicate feat: "
				+ aFeat.getDisplayName());
		}

		if (!addRealAbility(AbilityCategory.FEAT, aFeat))
		{
			Logging
				.errorPrint("Problem adding feat: " + aFeat.getDisplayName());
		}

		if (playerCharacterLevelInfo != null)
		{
			// Add this feat to the level Info
			playerCharacterLevelInfo.addObject(aFeat);
		}
		addNaturalWeapons(aFeat.getNaturalWeapons());
		setAggregateFeatsStable(false);
		calcActiveBonuses();
	}

	public void addAbility(final AbilityCategory aCategory,
		final Ability anAbility, final PCLevelInfo aLevelInfo)
	{
		if (hasRealAbility(aCategory, anAbility))
		{
			Logging.errorPrint("Adding duplicate ability: "
				+ anAbility.getDisplayName());
		}

		if (!addRealAbility(aCategory, anAbility))
		{
			Logging.errorPrint("Problem adding ability: "
				+ anAbility.getDisplayName());
		}
		if (aLevelInfo != null)
		{
			// Add this feat to the level Info
			aLevelInfo.addObject(anAbility);
		}
		addNaturalWeapons(anAbility.getNaturalWeapons());
		setAggregateAbilitiesStable(aCategory, false);
		calcActiveBonuses();
	}

	public Ability getFeatAutomaticKeyed(final String aFeatKey)
	{
		return AbilityUtilities.getAbilityFromList(featAutoList(), "FEAT",
			aFeatKey, Ability.Nature.ANY);
	}

	public Ability getAutomaticAbilityKeyed(final AbilityCategory aCategory,
		final String anAbilityKey)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getFeatAutomaticKeyed(anAbilityKey);
		}
		final List<Ability> abilities = getAutomaticAbilityList(aCategory);
		for (final Ability ability : abilities)
		{
			if (ability.getKeyName().equals(anAbilityKey))
			{
				return ability;
			}
		}
		return null;
	}

	public Ability getVirtualFeatKeyed(final String aKey)
	{
		return AbilityUtilities.getAbilityFromList(getVirtualFeatList(),
			"FEAT", aKey, Ability.Nature.ANY);

	}

	// TODO - Consolidate the various getXXXAbility functions to take a ability
	// type parameter.
	public Ability getVirtualAbilityKeyed(final AbilityCategory aCategory,
		final String aKey)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getVirtualFeatKeyed(aKey);
		}

		final List<Ability> abilities = getVirtualAbilityList(aCategory);
		for (final Ability ability : abilities)
		{
			if (ability.getKeyName().equals(aKey))
			{
				return ability;
			}
		}
		return null;
	}

	public int addAbility(final PCLevelInfo LevelInfo,
		final AbilityCategory aCategory, final String aKey,
		final boolean addIt, final boolean singleChoice)
	{
		boolean singleChoice1 = !singleChoice;
		if (!isImporting())
		{
			getSpellList();
		}

		final ArrayList<String> choices = new ArrayList<String>();
		final String undoctoredKey = aKey;
		final String baseKey = EquipmentUtilities.getUndecoratedName(aKey,
			choices);
		String subKey = choices.size() > 0 ? choices.get(0)
			: Constants.EMPTY_STRING;

		// See if our choice is not auto or virtual
		Ability anAbility = getRealAbilityKeyed(aCategory, undoctoredKey);

		// if a feat keyed aFeatKey doesn't exist, and aFeatKey
		// contains a (blah) descriptor, try removing it.
		if (anAbility == null)
		{
			anAbility = getRealAbilityKeyed(aCategory, baseKey);

			if (!singleChoice1 && (anAbility != null) && (subKey.length() != 0))
			{
				singleChoice1 = true;
			}
		}

		// (anAbility == null) means we don't have this feat, so we need to add
		// it
		if ((anAbility == null) && addIt)
		{
			// Adding feat for first time
			anAbility = Globals.getAbilityKeyed(aCategory, baseKey);

			if (anAbility == null)
			{
				anAbility = Globals.getAbilityKeyed(aCategory, undoctoredKey);

				if (anAbility != null)
				{
					subKey = Constants.EMPTY_STRING;
				}
			}

			if (anAbility == null)
			{
				Logging.errorPrint("Ability not found: " + undoctoredKey);

				return addIt ? 1 : 0;
			}

			anAbility = anAbility.clone();

			// addFeat(anAbility, LevelInfo);
			addAbility(aCategory, anAbility, LevelInfo);
			anAbility.getTemplates(isImporting(), this);
		}

		/*
		 * Could not find the Ability: addIt true means that no global Ability
		 * called featName exists, addIt false means that the PC does not have
		 * this ability
		 */
		if (anAbility == null)
		{
			return addIt ? 1 : 0;
		}

		return finaliseAbility(aCategory, anAbility, subKey, addIt,
			singleChoice1);
	}

	private int finaliseAbility(final AbilityCategory aCategory,
		final Ability ability, final String choice, final boolean addIt,
		final boolean singleChoice)
	{
		// how many sub-choices to make
		double abilityCount = (ability.getAssociatedCount() * ability
			.getCost(this));

		boolean adjustedAbilityPool = false;

		// adjust the associated List
		if (singleChoice && (addIt || ability.isMultiples()))
		{
			if ("".equals(choice))
			{
				// Get modChoices to adjust the associated list and Feat Pool
				adjustedAbilityPool = ability.modChoices(this, addIt);
			}
			else if (addIt)
			{
				if (ability.canAddAssociation(choice))
				{
					ability.addAssociated(choice);
				}
			}
			else
			{
				ability.removeAssociated(choice);
			}
		}

		/*
		 * This modifyChoice method is a bit like mod choices, but it uses a
		 * different tag to set the chooser string. The Tag MODIFYABILITYCHOICE
		 * which doesn't appear to be used anywhere, so this code is totally
		 * redundant.
		 */
		ability.modifyChoice(this);

		if (addIt)
		{
			final List<String> kitList = ability.getSafeListFor(ListKey.KITS);
			for (int i = 0; i < kitList.size(); i++)
			{
				KitUtilities.makeKitSelections(0, kitList.get(i), 1, this);
			}
		}

		// if no sub choices made (i.e. all of them removed in Chooser box),
		// then remove the Feat
		boolean removed = false;
		boolean result = (ability.isMultiples() && singleChoice) ? (ability
			.getAssociatedCount() > 0) : addIt;

		if (!result)
		{
			removed = removeRealAbility(aCategory, ability);
			removeNaturalWeapons(ability);

			for (int x = 0; x < ability.templatesAdded().size(); ++x)
			{
				removeTemplate(getTemplateKeyed(ability.templatesAdded().get(x)));
			}
			ability.subAddsForLevel(-9, this);
		}

		if (singleChoice && !adjustedAbilityPool)
		{
			if (!addIt && !ability.isMultiples() && removed)
			{
				abilityCount += ability.getCost(this);
			}
			else if (addIt && !ability.isMultiples())
			{
				abilityCount -= ability.getCost(this);
			}
			else
			{
				int listSize = ability.getAssociatedCount();

				for (final Ability myAbility : getRealAbilityList(aCategory))
				{
					if (myAbility.getKeyName().equalsIgnoreCase(
						ability.getKeyName()))
					{
						listSize = myAbility.getAssociatedCount();
					}
				}

				abilityCount -= (listSize * ability.getCost(this));
			}

			if (aCategory == AbilityCategory.FEAT)
			{
				adjustAbilities(aCategory, new BigDecimal(abilityCount));
			}
		}

		setAutomaticAbilitiesStable(aCategory, false);

		if (addIt && !isImporting())
		{
			ability.globalChecks(false, this);
			ability.checkRemovals(this);
		}

		return result ? 1 : 0;
	}

	/**
	 * Returns the Feat definition searching by key (not name), as found in the
	 * <b>aggregate</b> feat list.
	 * 
	 * @param featName
	 *            String key of the feat to check for.
	 * @return the Feat (not the CharacterFeat) searched for, <code>null</code>
	 *         if not found.
	 */
	public Ability getFeatKeyed(final String featName)
	{
		return getFeatKeyed(featName, aggregateFeatList());
	}

	public Ability getAbilityKeyed(final AbilityCategory aCategory,
		final String aKey)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getFeatKeyed(aKey);
		}

		final List<Ability> abilities = getAggregateAbilityList(aCategory);
		for (final Ability ability : abilities)
		{
			if (ability.getKeyName().equals(aKey))
			{
				return ability;
			}
		}

		return null;
	}

	public List<Ability> aggregateFeatList()
	{
		final List<Ability> aggregate = getStableAggregateFeatList();

		// Did we get a valid list? If so, return it.
		if (aggregate != null)
		{
			return aggregate;
		}

		return rebuildFeatAggreagateList();
	}

	public List<Ability> getAggregateAbilityList(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return aggregateFeatList();
		}

		final List<Ability> abilities = new ArrayList<Ability>(
			getRealAbilityList(aCategory));
		abilities.addAll(getVirtualAbilityList(aCategory));
		abilities.addAll(getAutomaticAbilityList(aCategory));

		return abilities;
	}

	private List<Ability> rebuildFeatAggreagateList()
	{
		List<Ability> aggregate = new ArrayList<Ability>();
		final Map<String, Ability> aHashMap = new HashMap<String, Ability>();

		for (Ability aFeat : getRealFeatList())
		{
			if (aFeat != null)
			{
				aHashMap.put(aFeat.getKeyName(), aFeat);
			}
		}

		for (Ability vFeat : getVirtualFeatList())
		{
			if (!aHashMap.containsKey(vFeat.getKeyName()))
			{
				aHashMap.put(vFeat.getKeyName(), vFeat);
			}
			else if (vFeat.isMultiples())
			{
				Ability aggregateFeat = aHashMap.get(vFeat.getKeyName());
				aggregateFeat = aggregateFeat.clone();

				for (int e1 = 0; e1 < vFeat.getAssociatedCount(); ++e1)
				{
					final String aString = vFeat.getAssociated(e1);

					if (aggregateFeat.isStacks()
						|| !aggregateFeat.containsAssociated(aString))
					{
						aggregateFeat.addAssociated(aString);
					}
				}

				aHashMap.put(vFeat.getKeyName(), aggregateFeat);
			}
		}

		aggregate.addAll(aHashMap.values());
		setStableAggregateFeatList(aggregate);

		for (Ability autoFeat : featAutoList())
		{
			if (!aHashMap.containsKey(autoFeat.getKeyName()))
			{
				aHashMap.put(autoFeat.getKeyName(), autoFeat);
			}

			else if (autoFeat.isMultiples())
			{
				Ability aggregateFeat = aHashMap.get(autoFeat.getKeyName());
				aggregateFeat = aggregateFeat.clone();

				for (int e1 = 0; e1 < autoFeat.getAssociatedCount(); ++e1)
				{
					final String aString = autoFeat.getAssociated(e1);
					if (aggregateFeat.isStacks()
						|| !aggregateFeat.containsAssociated(aString))
					{
						aggregateFeat.addAssociated(aString);
					}
				}

				aHashMap.put(autoFeat.getKeyName(), aggregateFeat);
			}
		}

		aggregate = new ArrayList<Ability>();
		aggregate.addAll(aHashMap.values());
		setStableAggregateFeatList(aggregate);
		return aggregate;
	}

	public List<Ability> aggregateVisibleFeatList()
	{
		final List<Ability> tempFeatList = new ArrayList<Ability>();

		for (Ability feat : aggregateFeatList())
		{
			if ((feat.getVisibility() == Visibility.DEFAULT)
				|| (feat.getVisibility() == Visibility.OUTPUT_ONLY))
			{
				tempFeatList.add(feat);
			}
		}

		return tempFeatList;
	}

	public List<Ability> getAggregateVisibleAbilityList(
		final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return aggregateVisibleFeatList();
		}
		final List<Ability> abilities = getAggregateAbilityList(aCategory);
		final List<Ability> ret = new ArrayList<Ability>(abilities.size());
		for (final Ability ability : abilities)
		{
			if (ability.getVisibility() == Visibility.DEFAULT
				|| ability.getVisibility() == Visibility.OUTPUT_ONLY)
			{
				ret.add(ability);
			}
		}
		return Collections.unmodifiableList(ret);
	}

	// boolean isAutomaticFeatsStable()
	// {
	// return automaticFeatsStable;
	// }

	boolean isAutomaticAbilitiesStable(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return stableAutomaticFeatList != null;
		}
		return theAbilities.get(aCategory, Ability.Nature.AUTOMATIC) != null;
	}

	public List<Ability> getVirtualFeatList()
	{
		List<Ability> vFeatList = getStableVirtualFeatList();

		// Did we get a valid list? If so, return it.
		if (vFeatList != null)
		{
			return vFeatList;
		}
		setVirtualFeatsStable(true);
		vFeatList = new ArrayList<Ability>();
		if (stableVirtualFeatList != null)
		{
			for (Ability feat : stableVirtualFeatList)
			{
				if (feat.needsSaving())
				{
					if (PrereqHandler.passesAll(feat.getPreReqList(), this,
						feat))
					{
						vFeatList.add(feat);
					}
				}
			}
		}

		for (PCClass pcClass : classList)
		{
			final List<Ability> aList = pcClass.getVirtualFeatList(pcClass
				.getLevel());

			for (Ability feat : aList)
			{
				if (PrereqHandler.passesAll(feat.getPreReqList(), this, feat))
				{
					vFeatList.add(feat);
				}
			}
		}

		for (Ability aFeat : getRealFeatList())
		{
			final List<Ability> aList = aFeat.getVirtualFeatList();

			for (Ability feat : aList)
			{
				if (PrereqHandler.passesAll(feat.getPreReqList(), this, feat))
				{
					vFeatList.add(feat);
				}
			}
		}

		for (PCTemplate template : templateList)
		{
			final List<Ability> aList = template.getVirtualFeatList();

			for (Ability feat : aList)
			{
				if (PrereqHandler.passesAll(feat.getPreReqList(), this, feat))
				{
					vFeatList.add(feat);
				}
			}
		}

		for (Equipment eq : equipmentList)
		{
			if (eq.isEquipped())
			{
				// This already includes the EqMods
				final List<Ability> aList = eq.getVirtualFeatList();
				for (Ability feat : aList)
				{
					// TODO Check for duplicates?
					if (PrereqHandler.passesAll(feat.getPreReqList(), this,
						feat))
					{
						vFeatList.add(feat);
					}
				}
			}
		}

		if (getRace() != null)
		{
			final List<Ability> aList = getRace().getVirtualFeatList();
			for (Ability feat : aList)
			{
				// TODO Check for duplicates?
				if (PrereqHandler.passesAll(feat.getPreReqList(), this, feat))
				{
					vFeatList.add(feat);
				}
			}
		}

		for (Skill skill : getSkillList())
		{
			final List<Ability> aList = skill.getVirtualFeatList();

			for (Ability feat : aList)
			{
				if (PrereqHandler.passesAll(feat.getPreReqList(), this, feat))
				{
					vFeatList.add(feat);
				}
			}
		}

		for (CharacterDomain cd : characterDomainList)
		{
			if (cd.getDomain() != null)
			{
				final List<Ability> aList = cd.getDomain().getVirtualFeatList();

				for (Ability feat : aList)
				{
					if (PrereqHandler.passesAll(feat.getPreReqList(), this,
						feat))
					{
						vFeatList.add(feat);
					}
				}
			}
		}
		if (deity != null)
		{
			final List<Ability> aList = deity.getVirtualFeatList();

			for (Ability feat : aList)
			{
				if (PrereqHandler.passesAll(feat.getPreReqList(), this, feat))
				{
					vFeatList.add(feat);
				}
			}
		}
		for (CompanionMod cMod : companionModList)
		{
			final List<Ability> aList = cMod.getVirtualFeatList();

			for (Ability feat : aList)
			{
				if (PrereqHandler.passesAll(feat.getPreReqList(), this, feat))
				{
					vFeatList.add(feat);
				}
			}
		}

		setStableVirtualFeatList(vFeatList);

		return vFeatList;
	}

	public List<Ability> getVirtualAbilityList(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return getVirtualFeatList();
		}
		List<Ability> ret = theAbilities.get(aCategory, Ability.Nature.VIRTUAL);

		return Collections.unmodifiableList(ret);
	}

	public List<Ability> featAutoList()
	{
		if (stableAutomaticFeatList == null)
		{
			stableAutomaticFeatList = rebuildAutoFeatList();
			setAutomaticFeatsStable(true);
		}
		if (stableAutomaticFeatList == null)
		{
			return Collections.emptyList();
		}
		return stableAutomaticFeatList;
	}

	/**
	 * Returns the list of automatic abilities of the specified category the
	 * character possesses.
	 * 
	 * @param aCategory
	 *            The <tt>AbilityCategory</tt> to check.
	 * 
	 * @return A <tt>List</tt> of <tt>Abiltity</tt> objects.
	 * 
	 * @author boomer70
	 * @since 5.11.1
	 */
	public List<Ability> getAutomaticAbilityList(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return featAutoList();
		}

		List<Ability> abilities = theAbilities.get(aCategory,
			Ability.Nature.AUTOMATIC);
		if (abilities == null)
		{
			abilities = new ArrayList<Ability>();
			theAbilities.put(aCategory, Ability.Nature.AUTOMATIC, abilities);

			for (final PObject pobj : getPObjectList())
			{
				final List<String> abilityKeys = pobj.getAbilityKeys(this,
					aCategory, Ability.Nature.AUTOMATIC);
				for (final String key : abilityKeys)
				{
					final Ability added = AbilityUtilities
						.addCloneOfGlobalAbilityToListWithChoices(abilities,
							aCategory, key);
					if (added != null)
					{
						added.setFeatType(Ability.Nature.AUTOMATIC);
					}
				}
			}
			//
			// add racial feats
			//
			// if ((getRace() != null) &&
			// !PlayerCharacterUtilities.canReassignRacialFeats())
			// {
			// final List<String> abilityKeys =
			// getRace().getAutoAbilityList(aCategory);
			// for ( final String key : abilityKeys )
			// {
			// final Ability added =
			// AbilityUtilities.addCloneOfGlobalAbilityToListWithChoices(abilities,
			// aCategory, key);
			// added.setFeatType(Ability.Nature.AUTOMATIC);
			// }
			// }
			//
			// for (final PCClass aClass : getClassList())
			// {
			// final Collection<String> abilityKeys =
			// aClass.getAutoAbilityList(aCategory);
			// for ( String key : abilityKeys )
			// {
			// final int idx = key.indexOf('[');
			//
			// if (idx >= 0)
			// {
			// final StringTokenizer bTok = new
			// StringTokenizer(key.substring(idx + 1), "[]");
			// final List<Prerequisite> preReqList = new
			// ArrayList<Prerequisite>();
			//
			// while (bTok.hasMoreTokens())
			// {
			// final String prereqString = bTok.nextToken();
			// Logging.debugPrint("Why is the prerequisite '"+prereqString+
			// "' parsed in PlayerCharacter.featAutoList() rather than the
			// persistence layer");
			// try {
			// final PreParserFactory factory = PreParserFactory.getInstance();
			// final Prerequisite prereq = factory.parse(prereqString);
			// preReqList.add(prereq);
			// }
			// catch (PersistenceLayerException ple){
			// Logging.errorPrint(ple.getMessage(), ple);
			// }
			// }
			//
			// key = key.substring(0, idx);
			//
			// if (preReqList.size() != 0)
			// {
			// if (! PrereqHandler.passesAll(preReqList, this, null ))
			// {
			// continue;
			// }
			// }
			// }
			//
			// final Ability added =
			// AbilityUtilities.addCloneOfGlobalAbilityToListWithChoices(abilities,
			// aCategory, key);
			// added.setFeatType(Ability.Nature.AUTOMATIC);
			// }
			// }
			//
			// if (!PlayerCharacterUtilities.canReassignTemplateFeats() &&
			// !getTemplateList().isEmpty())
			// {
			// for (final PCTemplate template : getTemplateList())
			// {
			// final List<String> abilityKeys =
			// template.getAutoAbilityKeys(aCategory, this, false);
			//
			// for ( final String key : abilityKeys )
			// {
			// // TODO - Not sure if we need to tokenize on comma
			// final Ability added =
			// AbilityUtilities.addCloneOfGlobalAbilityToListWithChoices(abilities,
			// aCategory, key);
			// added.setFeatType(Ability.Nature.AUTOMATIC);
			// }
			// }
			// }
			//
			// for (final CharacterDomain aCD : getCharacterDomainList())
			// {
			// final Domain domain = aCD.getDomain();
			//
			// if (domain != null)
			// {
			// for (int e2 = 0; e2 < domain.getAssociatedCount(); ++e2)
			// {
			// final String aString = domain.getAssociated(e2);
			//
			// // TODO - This is not working
			// if (aString.startsWith("ABILITY"))
			// {
			// final int idx = aString.indexOf('?');
			//
			// if (idx > -1)
			// {
			// final Ability added =
			// AbilityUtilities.addCloneOfGlobalAbilityToListWithChoices(abilities,
			// aCategory, aString.substring(idx + 1));
			// added.setFeatType(Ability.Nature.AUTOMATIC);
			// }
			// else
			// {
			// Logging.errorPrint("no '?' in Domain assocatedList entry: " +
			// aString);
			// }
			// }
			// }
			//
			// // TODO - Need to change Domain to take AbilityCategories
			// final Iterator<Categorisable> anIt =
			// domain.getAbilityIterator(aCategory.getAbilityCategory());
			//
			// for (; anIt.hasNext();)
			// {
			// final Categorisable c = anIt.next();
			// final Ability added =
			// AbilityUtilities.addCloneOfGlobalAbilityToListWithChoices(abilities,
			// aCategory, c.getKeyName());
			// added.setFeatType(Ability.Nature.AUTOMATIC);
			// }
			// }
			// }

			cachedWeaponProfs = null;
		}
		return abilities;
	}

	private List<Ability> getStableAggregateFeatList()
	{
		if (isAggregateFeatsStable())
		{
			return stableAggregateFeatList;
		}
		return null;
	}

	void setStableAutomaticFeatList(final List<Ability> aFeatList)
	{
		stableAutomaticFeatList = aFeatList;
		cachedWeaponProfs = null;
		// setAutomaticFeatsStable( aFeatList != null );
	}

	// private List<Ability> getStableAutomaticFeatList()
	// {
	// if (isAutomaticFeatsStable())
	// {
	// return stableAutomaticFeatList;
	// }
	// return null;
	// }

	private void setStableVirtualFeatList(final List<Ability> aFeatList)
	{
		stableVirtualFeatList = aFeatList;
		setVirtualFeatsStable(aFeatList != null);
		if (aFeatList == null)
		{
			cachedWeaponProfs = null;
		}
	}

	private List<Ability> getStableVirtualFeatList()
	{
		if (isVirtualFeatsStable())
		{
			return stableVirtualFeatList;
		}
		return null;
	}

	private List<Ability> rebuildAutoFeatList()
	{
		stableAutomaticFeatList = new ArrayList<Ability>();

		//
		// add racial feats
		//
		if ((getRace() != null)
			&& !PlayerCharacterUtilities.canReassignRacialFeats())
		{
			final StringTokenizer aTok = new StringTokenizer(getRace()
				.getFeatList(this), Constants.PIPE);

			while (aTok.hasMoreTokens())
			{
				Ability added = AbilityUtilities
					.addCloneOfGlobalAbilityToListWithChoices(
						stableAutomaticFeatList, Constants.FEAT_CATEGORY, aTok
							.nextToken());
				if (added != null)
				{
					added.setFeatType(Ability.Nature.AUTOMATIC);
				}
			}

			addAutoProfsToList(getRace().getSafeListFor(
				ListKey.SELECTED_WEAPON_PROF_BONUS), stableAutomaticFeatList);

		}

		for (final PCClass aClass : getClassList())
		{
			List<String> classFeatList = new ArrayList<String>();
			for (int lvl = 0; lvl <= aClass.getLevel(); lvl++)
			{
				for (String st : aClass.getFeatAutos(lvl))
				{
					classFeatList.add(st);
				}
			}
			for (String autoFeat : classFeatList)
			{
				final int idx = autoFeat.indexOf('[');

				if (idx >= 0)
				{
					final StringTokenizer bTok = new StringTokenizer(autoFeat
						.substring(idx + 1), "[]");
					final List<Prerequisite> preReqList = new ArrayList<Prerequisite>();

					while (bTok.hasMoreTokens())
					{
						final String prereqString = bTok.nextToken();
						Logging
							.debugPrint("Why is the prerequisite '"
								+ prereqString
								+ "' parsed in PlayerCharacter.featAutoList() rather than the persistence layer");
						try
						{
							final PreParserFactory factory = PreParserFactory
								.getInstance();
							final Prerequisite prereq = factory
								.parse(prereqString);
							preReqList.add(prereq);
						}
						catch (PersistenceLayerException ple)
						{
							Logging.errorPrint(ple.getMessage(), ple);
						}
					}

					autoFeat = autoFeat.substring(0, idx);

					if (preReqList.size() != 0)
					{
						if (!PrereqHandler.passesAll(preReqList, this, null))
						{
							continue;
						}
					}
				}

				Ability added = AbilityUtilities
					.addCloneOfGlobalAbilityToListWithChoices(
						stableAutomaticFeatList, Constants.FEAT_CATEGORY,
						autoFeat);
				if (added != null)
				{
					added.setFeatType(Ability.Nature.AUTOMATIC);
				}
			}
			addAutoProfsToList(aClass
				.getSafeListFor(ListKey.SELECTED_WEAPON_PROF_BONUS),
				stableAutomaticFeatList);
		}

		if (!PlayerCharacterUtilities.canReassignTemplateFeats()
			&& !getTemplateList().isEmpty())
		{
			for (final PCTemplate aTemplate : getTemplateList())
			{
				final List<String> templateFeats = aTemplate.feats(
					getTotalLevels(), totalHitDice(), this, false);

				if (!templateFeats.isEmpty())
				{
					for (Iterator<String> e2 = templateFeats.iterator(); e2
						.hasNext();)
					{
						final String aString = e2.next();
						final StringTokenizer aTok = new StringTokenizer(
							aString, Constants.COMMA);

						while (aTok.hasMoreTokens())
						{
							Ability added = AbilityUtilities
								.addCloneOfGlobalAbilityToListWithChoices(
									stableAutomaticFeatList,
									Constants.FEAT_CATEGORY, aTok.nextToken());
							if (added != null)
							{
								added.setFeatType(Ability.Nature.AUTOMATIC);
							}
						}
					}
				}
				addAutoProfsToList(aTemplate
					.getSafeListFor(ListKey.SELECTED_WEAPON_PROF_BONUS),
					stableAutomaticFeatList);

			}
		}

		/*
		 * BUG I think -> if it's empty, we step through it?!? - thpr 11/3/06
		 */
		if (getCharacterDomainList().isEmpty())
		{
			for (final CharacterDomain aCD : getCharacterDomainList())
			{
				final Domain aDomain = aCD.getDomain();

				if (aDomain != null)
				{
					for (int e2 = 0; e2 < aDomain.getAssociatedCount(); ++e2)
					{
						final String aString = aDomain.getAssociated(e2);

						if (aString.startsWith("FEAT"))
						{
							final int idx = aString.indexOf('?');

							if (idx > -1)
							{
								Ability added = AbilityUtilities
									.addCloneOfGlobalAbilityToListWithChoices(
										stableAutomaticFeatList,
										Constants.FEAT_CATEGORY, aString
											.substring(idx + 1));
								if (added != null)
								{
									added.setFeatType(Ability.Nature.AUTOMATIC);
								}
							}
							else
							{
								Logging
									.errorPrint("no '?' in Domain assocatedList entry: "
										+ aString);
							}
						}
					}

					final Iterator<Categorisable> anIt = aDomain
						.getFeatIterator();

					for (; anIt.hasNext();)
					{
						final Ability abI = (Ability) anIt.next();
						Ability added = AbilityUtilities
							.addCloneOfGlobalAbilityToListWithChoices(
								stableAutomaticFeatList,
								Constants.FEAT_CATEGORY, abI.getKeyName());
						if (added != null)
						{
							added.setFeatType(Ability.Nature.AUTOMATIC);
						}
					}

					addAutoProfsToList(aDomain
						.getSafeListFor(ListKey.SELECTED_WEAPON_PROF_BONUS),
						stableAutomaticFeatList);

				}
			}
		}
		
		// Grab any Abilities of category FEAT lurking in any PObjects
		for (PObject pobj : getPObjectList())
		{
			for (String key : pobj.getAbilityKeys(this, AbilityCategory.FEAT, Nature.AUTOMATIC))
			{
				Logging.errorPrint("Got ability key " + key + " from " + pobj + ".");
				Ability added =
						AbilityUtilities
							.addCloneOfGlobalAbilityToListWithChoices(
								stableAutomaticFeatList,
								Constants.FEAT_CATEGORY, key);
			}
		}
		return stableAutomaticFeatList;
	}

	/**
	 * Add the listed automatic weapon proficiencies to the list of abilities.
	 * 
	 * @param autoProfList
	 *            The list of weapon profs to be added.
	 * @param abilityList
	 *            The list to add the new entries to.
	 */
	private void addAutoProfsToList(List<String> autoProfList,
		List<Ability> abilityList)
	{
		for (Iterator<String> iter = autoProfList.iterator(); iter.hasNext();)
		{
			String prof = iter.next();
			addWeaponProfToList(abilityList, prof, true);
		}
	}

	/**
	 * Determine the character's facing. This is based on their race and any
	 * applied templates.
	 * 
	 * @return The facing.
	 */
	public Point2D.Double getFace()
	{
		final Race aRace = getRace();
		// Default to 5' by 5'
		Point2D.Double face = new Point2D.Double(5, 0);
		if (aRace != null)
		{
			face = aRace.getFace();
		}

		// Scan templates for any overrides
		for (PCTemplate template : getTemplateList())
		{
			if (template.getFace() != null)
			{
				face = template.getFace();
			}
		}
		return face;
	}

	/**
	 * Determine the number of hands the character has. This is based on their
	 * race and any applied templates.
	 * 
	 * @return The number of hands.
	 */
	public int getHands()
	{
		final Race aRace = getRace();
		int hands = 0;
		if (aRace != null)
		{
			hands = aRace.getHands();
		}

		// Scan templates for any overrides
		for (PCTemplate template : getTemplateList())
		{
			if (template.getHands() != null)
			{
				hands = template.getHands();
			}
		}
		return hands;
	}

	/**
	 * Determine the number of legs the character has. This is based on their
	 * race and any applied templates.
	 * 
	 * @return The number of legs.
	 */
	public int getLegs()
	{
		final Race aRace = getRace();
		int legs = 0;
		if (aRace != null)
		{
			legs = aRace.getLegs();
		}

		// Scan templates for any overrides
		for (PCTemplate template : getTemplateList())
		{
			if (template.getLegs() != null)
			{
				legs = template.getLegs();
			}
		}
		return legs;
	}

	/**
	 * Determine the character's reach. This is based on their race, any applied
	 * templates and any other bonuses to reach.
	 * 
	 * @return The reach radius.
	 */
	public int getReach()
	{
		final Race aRace = getRace();
		int reach = 0;
		if (aRace != null)
		{
			reach = aRace.getReach();
		}

		// Scan templates for any overrides
		for (PCTemplate template : getTemplateList())
		{
			if (template.getReach() != null)
			{
				reach = template.getReach();
			}
		}
		reach += (int) getTotalBonusTo("COMBAT", "REACH");
		return reach;
	}

	// public double getBonusValue(final String aBonusType, final String
	// aBonusName )
	// {
	// return TypedBonus.totalBonuses(getBonusesTo(aBonusType, aBonusName));
	// }
}
