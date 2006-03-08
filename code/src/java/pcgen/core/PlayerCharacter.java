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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision: 1.1584 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/16 13:38:58 $
 *
 */
package pcgen.core;

import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.*;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.spell.PCSpellTracker;
import pcgen.core.spell.Spell;
import pcgen.core.utils.*;
import pcgen.gui.GuiConstants;
import pcgen.io.exporttoken.BonusToken;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Delta;
import pcgen.util.Logging;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * <code>PlayerCharacter</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1584 $
 */
public final class PlayerCharacter extends Observable implements Cloneable
{
	// Constants for use in getBonus
	/** ATTACKBONUS = 0 */
	public static final int         ATTACKBONUS  = 0;
	/** MONKBONUS = 4 */
	public static final int         MONKBONUS    = 4;
	private static final BigDecimal BIG_ONE      = new BigDecimal("1.00");
	private static String           lastVariable = null;
	//TODO: These are never actually set to a non empty/zero value. Can the code be removed?
	private static String           loopVariable = "";
	private static int              loopValue    = 0;

	// List of Armor Proficiencies
	private final ArrayList armorProfList = new ArrayList();

	// List of Feats
	private final ArrayList featList = new ArrayList();

	// List of misc items (Assets, Magic items, etc)
	private final ArrayList miscList = new ArrayList(3);

	// List of Note objects
	private final ArrayList notesList = new ArrayList();

	// This may be different from file name
	private final ArrayList primaryWeapons   = new ArrayList();
	private final ArrayList secondaryWeapons = new ArrayList();
	private final ArrayList shieldProfList   = new ArrayList();

	// List of Skills
	private final ArrayList skillList = new ArrayList();

	// Collections of String (probably should be full objects)
	private final ArrayList specialAbilityList = new ArrayList();

	// List of Template objects
	private final ArrayList templateList = new ArrayList(); // of Template

	// List of VARs
	private final ArrayList variableList = new ArrayList();
	private BigDecimal      gold         = new BigDecimal("0.00");
	private Deity           deity        = null;

	// source of granted domains
	private HashMap    domainSourceMap     = new HashMap();
	private List       activeBonusList     = new ArrayList();
	private final List characterDomainList = new ArrayList();

	// List of Classes
	private ArrayList classList = new ArrayList();

	// List of CompanionMods
	private final ArrayList companionModList = new ArrayList();
	private final List      followerList     = new ArrayList(); // of Followers
	private ArrayList       qualifyArrayList = new ArrayList();
	private Follower        followerMaster   = null; // Who is the master now?

	// List of Equip Sets
	private final List equipSetList = new ArrayList();

	// List of Equipment
	private List       equipmentList       = new ArrayList();
	private List       equipmentMasterList = new ArrayList();
	private List       pcLevelInfo         = new ArrayList();
	private List       processedBonusList  = new ArrayList();
	private final List spellBooks          = new ArrayList();
	private Map        spellBookMap        = new HashMap();
	private List       tempBonusItemList   = new ArrayList();
	private List       tempBonusList       = new ArrayList();
	private Set        tempBonusFilters    = new TreeSet();
	// Temporary Bonuses
	private Map             activeBonusMap = new TreeMap();
	private Race            race           = null;
	private final SortedSet favoredClasses = new TreeSet();
	private final StatList  statList       = new StatList(this);

	// List of Kit objects
	private List kitList                 = null;
	private List stableAggregateFeatList = null;
	private List stableAutomaticFeatList = null;
	private List stableVirtualFeatList   = null;

	// Spells
	private PCSpellTracker spellTracker = null;

	//
	// We don't want this list sorted until after it has been added
	// to the character,  The reason is that sorting prevents
	// .CLEAR-TEMPLATES from clearing the OLDER template languages.
	private final List      templateAutoLanguages = new ArrayList();
	private final SortedSet templateLanguages     = new TreeSet();
	private StringKeyMap    stringChar            = new StringKeyMap();
	private String          calcEquipSetId        = "0.1";
	private String          descriptionLst        = "EMPTY";
	private String          tabName               = "";
	private String          gender                = "Male";
	private final TreeSet   languages             = new TreeSet();
	private HashSet         variableSet           = new HashSet();

	// Weapon, Armor and Shield proficiencies
	private final TreeSet weaponProfList = new TreeSet();
	private Double[]      movementMult   = Globals.EMPTY_DOUBLE_ARRAY;
	private String[]      movementMultOp = Globals.EMPTY_STRING_ARRAY;
	private String[]      movementTypes  = Globals.EMPTY_STRING_ARRAY;

	// Movement lists
	private Double[] movements = Globals.EMPTY_DOUBLE_ARRAY;

	// Whether one can trust the most recently calculated aggregateFeatList
	private boolean aggregateFeatsStable = false;
	private boolean armorProfListStable  = false;

	// whether to add auto known spells each level
	private boolean autoKnownSpells = true;

	// should we also load companions on master load?
	private boolean autoLoadCompanion = false;

	// Should we sort the gear automatically?
	private boolean autoSortGear = true;

	// Whether one can trust the most recently calculated automaticFeatList
	private boolean       automaticFeatsStable = false;
	private boolean       qualifyListStable    = false;
	private final boolean useMonsterDefault    = SettingsHandler.isMonsterDefault();

	// output sheet locations
	private String    outputSheetHTML     = "";
	private String    outputSheetPDF      = "";
	private boolean[] ageSetKitSelections = new boolean[10];
	private boolean   dirtyFlag           = false;
	private int       serial              = 0;
	private boolean   displayUpdate       = false;
	private boolean   importing           = false;

	// Should temp mods/bonuses be used/saved?
	private boolean useTempMods = false;

	// Whether one can trust the most recently calculated virtualFeatList
	private boolean virtualFeatsStable = false;

	// pool of feats remaining to distribute
	private double feats = 0;
	private int    age   = 0;

	// 0 = LG to 8 = CE and 9 is <none selected>
	private int alignment             = 9;
	private int costPool              = 0;
	private int currentEquipSetNumber = 0;
	private int earnedXP              = 0;

	// order in which the equipment will be output.
	private int equipOutputOrder = GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC;
	private int freeLangs        = 0;
	private int heightInInches   = 0; // in inches

	// pool of stats remaining to distribute
	private int poolAmount = 0;

	// order in which the skills will be output.
	private int               skillsOutputOrder = GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC;
	private int               spellLevelTemp    = 0;
	private int               weightInPounds    = 0; // in pounds
	private VariableProcessor variableProcessor;

	// used by point buy. Total number of points for method, not points remaining
	private int pointBuyPoints = -1;

	private boolean processLevelAbilities = true;

	///////////////////////////////////////
	//operations
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
			final PCStat stat = (PCStat) SettingsHandler.getGame().getUnmodifiableStatList().get(i);
			statList.getStats().add(stat.clone());
		}

		setRace((Race) Globals.getRaceMap().get(Constants.s_NONESELECTED));
		setName("");
		setFeats(0);
		rollStats(SettingsHandler.getGame().getRollMethod());
		miscList.add("");
		miscList.add("");
		miscList.add("");
		addSpellBook(new SpellBook(Globals.getDefaultSpellBook(),
			SpellBook.TYPE_KNOWN_SPELLS));
		addSpellBook(new SpellBook(Globals.INNATE_SPELL_BOOK_NAME,
			SpellBook.TYPE_KNOWN_SPELLS));
		populateSkills(SettingsHandler.getSkillsTab_IncludeSkills());
		spellTracker = new PCSpellTracker(this);
		setStringFor(StringKey.HANDED, "Right");
	}

	/**
	 * Get the active bonus map
	 * @return active bonus map
	 */
	public Map getActiveBonusMap()
	{
		return activeBonusMap;
	}

	/**
	 * Set the age
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
	 * @return age
	 */
	public int getAge()
	{
		return age;
	}

	/**
	 * Set aggregate Feats stable
	 * @param stable
	 */
	public void setAggregateFeatsStable(final boolean stable)
	{
		aggregateFeatsStable = stable;
		//setDirty(true);
	}

	/**
	 * Returns TRUE if all types (automatic, virtual and aggregate)
	 * of feats are stable
	 * @return TRUE or FALSE
	 */
	public boolean isAggregateFeatsStable()
	{
		return automaticFeatsStable && virtualFeatsStable && aggregateFeatsStable;
	}

	/**
	 * Alignment of this PC
	 * @return alignment
	 */
	public int getAlignment()
	{
		return alignment;
	}

	/**
	 * if checkBonus is true, then search for all skills with a SKILLRANK bonus
	 * to include in list as well
	 *
	 * @param checkBonus
	 * @return ArrayList
	 */
	public ArrayList getAllSkillList(final boolean checkBonus)
	{
		if (!checkBonus)
		{
			return skillList;
		}

		for (Iterator i = Globals.getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();

			if (!hasSkill(aSkill.getName()))
			{
//				if (!CoreUtility.doublesEqual(getTotalBonusTo("SKILLRANK", aSkill.getName()), 0.0))
				if (!CoreUtility.doublesEqual(aSkill.getSkillRankBonusTo(this), 0.0))
				{
					addSkill(aSkill);
				}
			}
		}

		return skillList;
	}

	/**
	 * Retrieve those skills in the character's skill list that match the
	 * supplied visibiility level.
	 *
	 * @param   visibility  What level of visibility skills are desired.
	 *
	 * @return  A list of the character's skills matching the visibility
	 *          criteria.
	 */
	public ArrayList getPartialSkillList(final int visibility)
	{
		// Now select the required set of skills, based on their visibility.
		ArrayList aList = new ArrayList();

		for (Iterator iter = skillList.iterator(); iter.hasNext();)
		{
			final Skill aSkill   = (Skill) iter.next();
			final int   skillVis = aSkill.isVisible();

			if (
				(visibility == Skill.VISIBILITY_DEFAULT) ||
				(skillVis == Skill.VISIBILITY_DEFAULT) ||
				(skillVis == visibility))
			{
				aList.add(aSkill);
			}

		}
		return aList;
	}

	/**
	 * Get the armor proficiency list
	 * @return armor proficiency list
	 */
	public ArrayList getArmorProfList()
	{
		if (armorProfListStable)
		{
			return armorProfList;
		}

		final List autoArmorProfList = getAutoArmorProfList();
		addArmorProfs(autoArmorProfList);

		final List selectedProfList = getSelectedArmorProfList();
		addArmorProfs(selectedProfList);
		armorProfListStable = true;

		return armorProfList;
	}

	/**
	 * Sets a 'stable' list of armor profs
	 * @param arg
	 */
	public void setArmorProfListStable(final boolean arg)
	{
		armorProfListStable = arg;
		setDirty(true);
	}

	/**
	 * Sets a 'stable' list of automatic feats
	 * @param stable
	 */
	public void setAutomaticFeatsStable(final boolean stable)
	{
		automaticFeatsStable = stable;
		//setDirty(true);
	}

	/**
	 * Returns the Spell Stat bonus for a class
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
			if (statIndex>=0)
			{
				baseSpellStat = getStatList().getTotalStatFor(statString);
				baseSpellStat += (int) getTotalBonusTo("STAT", "BASESPELLSTAT");
				baseSpellStat += (int) getTotalBonusTo("STAT", "BASESPELLSTAT;CLASS." + aClass.getName());
				baseSpellStat += (int) getTotalBonusTo("STAT", "CAST." + statString);
				baseSpellStat =	getStatList().getModForNumber(baseSpellStat, statIndex);
			}

		}

		return baseSpellStat;
	}

	/**
	 * Set BIO
	 * @param aString
	 */
	public void setBio(final String aString)
	{
		setStringFor(StringKey.BIO, aString);
	}

	/**
	 * Get the BIO
	 * @return the BIO
	 */
	public String getBio()
	{
		return getSafeStringFor(StringKey.BIO);
	}

	/**
	 * Set the birthday
	 * @param aString
	 */
	public void setBirthday(final String aString)
	{
		setStringFor(StringKey.BIRTHDAY, aString);
	}

	/**
	 * Get the birthday
	 * @return birthday
	 */
	public String getBirthday()
	{
		return getSafeStringFor(StringKey.BIRTHDAY);
	}

	/**
	 * Set the birthplace
	 * @param aString
	 */
	public void setBirthplace(final String aString)
	{
		setStringFor(StringKey.BIRTHPLACE, aString);
	}

	/**
	 * Get the birthplace
	 * @return birthplace
	 */
	public String getBirthplace()
	{
		return getSafeStringFor(StringKey.BIRTHPLACE);
	}

	/**
	 * Set the current EquipSet that is used to Bonus/Equip calculations
	 * @param eqSetId
	 */
	public void setCalcEquipSetId(final String eqSetId)
	{
		calcEquipSetId = eqSetId;
		setDirty(true);
	}

	/**
	 * Get the id for the equipment set being used for calculation
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
			for (Iterator e = equipSetList.iterator(); e.hasNext();)
			{
				final EquipSet eSet = (EquipSet) e.next();

				if (eSet.getParentIdPath().equals("0"))
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
			Logging.debugPrint("No EquipSet has been selected for calculations yet.");
			return;
		}

		// new equipment list
		final List eqList = new ArrayList();

		// set PC's equipmentList to new one
		setEquipmentList(eqList);

		// get all the PC's EquipSet's
		final List pcEquipSetList = getEquipSet();

		if (pcEquipSetList.isEmpty())
		{
			return;
		}

		// make sure EquipSet's are in sorted order
		// (important for Containers contents)
		Collections.sort(pcEquipSetList);

		// loop through all the EquipSet's and create equipment
		// then set status to equipped and add to PC's eq list
		for (Iterator e = pcEquipSetList.iterator(); e.hasNext();)
		{
			final EquipSet es = (EquipSet) e.next();

			final String abCalcId = calcId + ".";
			final String abParentId = es.getParentIdPath() + ".";

			// calcId = 0.1.
			// parentIdPath = 0.10.
			//  OR
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
			final StringTokenizer aTok = new StringTokenizer(es.getIdPath(), ".");

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
				eq.setNumberCarried(new Float(0));
				eq.setQty(num);
			}
			else if (eq.isWeapon())
			{
				if (aLoc.equals(Constants.S_PRIMARY) || aLoc.equals(Constants.S_NATURAL_PRIMARY))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_PRIMARY);
					eq.setIsEquipped(true, this);
				}
				else if (aLoc.startsWith(Constants.S_SECONDARY) || aLoc.equals(Constants.S_NATURAL_SECONDARY))
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
		for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

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
			for (Iterator e = getTempBonusItemList().iterator(); e.hasNext();)
			{
				final Equipment eq = (Equipment) e.next();

				// make sure that this EquipSet is the one
				// this temporary bonus item comes from
				// to make sure we keep them together
				final Equipment anEquip = getEquipmentNamed(eq.getName(), getEquipmentList());

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
							anEquip.setNumberCarried(new Float(0));
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
		final List aList = getFollowerList();
		setDirty(true);

		if (aList.isEmpty())
		{
			return;
		}

		for (Iterator fm = aList.iterator(); fm.hasNext();)
		{
			final Follower aF = (Follower) fm.next();
			final String rType = aF.getType().toUpperCase();
			final String rName = aF.getRace().toUpperCase();

			for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
			{
				final CompanionMod aComp = (CompanionMod) cm.next();
				final String aType = aComp.getType().toUpperCase();
				final int iRace = aComp.getLevel(rName);

				if (aType.equals(rType) && (iRace == 1))
				{
					// Found race and type of follower
					// so add bonus to the master
					companionModList.add(aComp);
					aComp.activateBonuses(aPC);
				}
			}
		}
	}

	/**
	 * Set the catchphrase
	 * @param aString
	 */
	public void setCatchPhrase(final String aString)
	{
		setStringFor(StringKey.CATCH_PHRASE, aString);
	}

	/**
	 * Get the catchphrase
	 * @return catchphrase
	 */
	public String getCatchPhrase()
	{
		return getSafeStringFor(StringKey.CATCH_PHRASE);
	}

	/**
	 * Get the class givena key
	 * @param aString
	 * @return PCClass
	 */
	public PCClass getClassKeyed(final String aString)
	{
		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			final PCClass aClass = (PCClass) classIter.next();

			if (aClass.getKeyName().equalsIgnoreCase(aString))
			{
				return aClass;
			}
		}

		return null;
	}

	/**
	 * Get the class list
	 * @return classList
	 */
	public ArrayList getClassList()
	{
		return classList;
	}

	/**
	 * Get the class named
	 * @param aString
	 * @return PCClass
	 */
	public PCClass getClassNamed(final String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			if (aClass.getName().equalsIgnoreCase(aString))
			{
				return aClass;
			}
		}

		return null;
	}

	/**
	 * Set the cost pool
	 * @param i
	 */
	public void setCostPool(final int i)
	{
		costPool = i;
	}

	/**
	 * Get the cost pool
	 * @return costPool
	 */
	public int getCostPool()
	{
		return costPool;
	}

	/**
	 * Get the spell tracker
	 * @return spellTracker
	 */
	public PCSpellTracker getSpellTracker()
	{
		return spellTracker;
	}

	/**
	 * Get a list of types that apply to this character
	 *
	 * @return  a List&lt;String&gt; where each String is a type that the
	 *          character has. The list returned will never be null
	 */
	public List getTypes()
	{
		final List list = new ArrayList();

		if (race != null)
		{
			list.add(race.getType());
		}
		else
		{
			list.add("Humanoid");
		}

		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			final String     aType     = aTemplate.getType();
			list.add(aType);
		}

		return list;
	}


	public String getCritterType()
	{
		final StringBuffer critterType = new StringBuffer();

		//Not too sure about this if, but that's what the previous code implied...
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
			for (Iterator e = templateList.iterator(); e.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate) e.next();
				final String aType = aTemplate.getType();

				if (!"".equals(aType))
				{
					critterType.append('|').append(aType);
				}
			}
		}

		return critterType.toString();
	}

	public String getRaceType()
	{
		String raceType = "None";
		if (race != null)
		{
			raceType = race.getRaceType();
		}
		if (!companionModList.isEmpty())
		{
			for (Iterator i = companionModList.iterator(); i.hasNext(); )
			{
				final CompanionMod mod = (CompanionMod)i.next();
				final String aType = mod.getRaceType();
				if (!"".equals(aType))
				{
					raceType = aType;
				}
			}
		}
		if (!templateList.isEmpty())
		{
			for (Iterator e = templateList.iterator(); e.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate) e.next();
				final String aType = aTemplate.getRaceType();

				if (!"".equals(aType))
				{
					raceType = aType;
				}
			}
		}
		return raceType;
	}

	public List getRacialSubTypes()
	{
		ArrayList racialSubTypes = new ArrayList(race.getRacialSubTypes());
		if (!templateList.isEmpty())
		{
			Set added = new TreeSet();
			Set removed = new TreeSet();
			for (Iterator e = templateList.iterator(); e.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate) e.next();
				added.addAll(aTemplate.getAddedSubTypes());
				removed.addAll(aTemplate.getRemovedSubTypes());
			}
			for (Iterator i = added.iterator(); i.hasNext(); )
			{
				String subType = (String)i.next();
				if (!racialSubTypes.contains(subType))
				{
					racialSubTypes.add(subType);
				}
			}
			for (Iterator i = removed.iterator(); i.hasNext(); )
			{
				String subType = (String)i.next();
				racialSubTypes.remove(subType);
			}
		}

		return Collections.unmodifiableList(racialSubTypes);
	}

	/**
	 * Set the current equipment set name
	 * @param aName
	 */
	public void setCurrentEquipSetName(final String aName)
	{
		setStringFor(StringKey.CURRENT_EQUIP_SET_NAME, aName);
	}

	/**
	 * Get the current equipment set name
	 * @return equipment set name
	 */
	public String getCurrentEquipSetName()
	{
		return getSafeStringFor(StringKey.CURRENT_EQUIP_SET_NAME);
	}

	/**
	 * Get the deity
	 * @return deity
	 */
	public Deity getDeity()
	{
		return deity;
	}

	/**
	 * Set the description
	 * @param aString
	 */
	public void setDescription(final String aString)
	{
		setStringFor(StringKey.DESCRIPTION, aString);
	}

	/**
	 * Get the description
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
	 * @param dirtyState
	 */
	public void setDirty(final boolean dirtyState)
	{
		if (dirtyState)
		{
			serial++;
			getVariableProcessor().setSerial(serial);
		}

		if (dirtyFlag != dirtyState)
		{
			dirtyFlag = dirtyState;

			setChanged();
			notifyObservers();
		}
	}


	/**
	 * Gets whether the character has been changed since last saved.
	 * @return true if dirty
	 */
	public boolean isDirty()
	{
		return dirtyFlag;
	}

	/**
	 * Returns the serial for the instance - every time something changes the serial is incremented.
	 * Use to detect change in PlayerCharacter.
	 * @return serial
	 */
	public int getSerial() {
		return serial;
	}

	/**
	 * @return display name
	 */
	public String getDisplayName()
	{
		final String custom = getTabName();

		if (!"".equals(custom))
		{
			return custom;
		}

		final StringBuffer displayName = new StringBuffer().append(getName());

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
				displayName.append(" the ").append(getDisplayRaceName()).append(' ').append(getDisplayClassName());

				break;

			case Constants.DISPLAY_STYLE_NAME_FULL:
				return getFullDisplayName();

			default:
				break; // custom broken
		}

		return displayName.toString();
	}

	/**
	 * set display update
	 * @param displayUpdate
	 */
	public void setDisplayUpdate(final boolean displayUpdate)
	{
		this.displayUpdate = displayUpdate;
	}

	/**
	 * is display update
	 * @return True if display update
	 */
	public boolean isDisplayUpdate()
	{
		return displayUpdate;
	}

	/**
	 * Get the list of equipment sets
	 * @return List
	 */
	public List getEquipSet()
	{
		return equipSetList;
	}

	/**
	 * Get the equipment set given id
	 * @param id
	 * @return EquipSet
	 */
	public EquipSet getEquipSetByIdPath(final String id)
	{
		if (equipSetList.isEmpty())
		{
			return null;
		}

		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			final EquipSet eSet = (EquipSet) e.next();

			if (eSet.getIdPath().equals(id))
			{
				return eSet;
			}
		}

		return null;
	}

	/**
	 * Get the equipment set by name
	 * @param aName
	 * @return Equip set
	 */
	public EquipSet getEquipSetByName(final String aName)
	{
		if (equipSetList.isEmpty())
		{
			return null;
		}

		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			final EquipSet eSet = (EquipSet) e.next();

			if (eSet.getName().equals(aName))
			{
				return eSet;
			}
		}

		return null;
	}

	/**
	 * Set the number of the current equipset when exporting
	 * @param anInt
	 */
	public void setEquipSetNumber(final int anInt)
	{
		currentEquipSetNumber = anInt;
		setDirty(true);
	}

	/**
	 * Get the equipment set number
	 * @return equipset number
	 */
	public int getEquipSetNumber()
	{
		return currentEquipSetNumber;
	}

	/**
	 * gets the total weight in an EquipSet
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

		for (Iterator eSet = equipSetList.iterator(); eSet.hasNext();)
		{
			final EquipSet es = (EquipSet) eSet.next();
			final String abIdPath = idPath + ".";
			final String esIdPath = es.getIdPath() + ".";

			if (!esIdPath.startsWith(abIdPath))
			{
				continue;
			}

			final Equipment eqI = es.getItem();

			if (eqI != null)
			{
				if ((eqI.getCarried().floatValue() > 0.0f) && (eqI.getParent() == null))
				{
					if (eqI.getChildCount() > 0)
					{
						totalWeight += (eqI.getWeightAsDouble(this) + eqI.getContainedWeight(this).floatValue());
					}
					else
					{
						totalWeight += (eqI.getWeightAsDouble(this) * eqI.getCarried().floatValue());
					}
				}
			}
		}

		return totalWeight;
	}

	/**
	 * Count the total number of items of aName within EquipSet idPath
	 * @param idPath
	 * @param aName
	 * @return equipment set count
	 */
	public Float getEquipSetCount(final String idPath, final String aName)
	{
		float count = 0;
		for (Iterator es = getEquipSet().iterator(); es.hasNext();)
		{
			final EquipSet eSet = (EquipSet) es.next();
			final String esID = eSet.getIdPath() + ".";
			final String abID = idPath + ".";
			if (esID.startsWith(abID))
			{
				if (eSet.getValue().equals(aName))
				{
					count += eSet.getQty().floatValue();
				}
			}
		}
		return new Float(count);
	}

	/**
	 * List of Equipment objects
	 * @param eqList
	 */
	public void setEquipmentList(final List eqList)
	{
		equipmentList = eqList;
	}

	/**
	 * Get equipment list
	 * @return equipment list
	 */
	public List getEquipmentList()
	{
		return equipmentList;
	}

	/**
	 * Retrieves a list of the character's equipment in output order. This
	 * is in ascending order of the equipment's outputIndex field.
	 * If multiple items of equipment have the same outputIndex they will
	 * be ordered by name. Note hidden items (outputIndex = -1) are not
	 * included in this list.
	 *
	 * @return An ArrayList of the equipment objects in output order.
	 */
	public List getEquipmentListInOutputOrder()
	{
		return sortEquipmentList(getEquipmentList());
	}

	/**
	 * Retrieves a list of the character's equipment in output order. This
	 * is in ascending order of the equipment's outputIndex field.
	 * If multiple items of equipment have the same outputIndex they will
	 * be ordered by name. Note hidden items (outputIndex = -1) are not
	 * included in this list.
	 *
	 * Deals with merge as well
	 * @param merge
	 *
	 * @return An ArrayList of the equipment objects in output order.
	 */
	public List getEquipmentListInOutputOrder(final int merge)
	{
		return sortEquipmentList(getEquipmentList(), merge);
	}

	/**
	 * Get equipment master list
	 * @return equipment master list
	 */
	public List getEquipmentMasterList()
	{
		final ArrayList aList = new ArrayList(equipmentMasterList);

		// Try all possible POBjects
		for (Iterator i = getPObjectList().iterator(); i.hasNext();)
		{
			final PObject aPObj = (PObject) i.next();

			if (aPObj != null)
			{
				aPObj.addAutoTagsToList("EQUIP", aList, this, true);
			}
		}

		return aList;
	}

	/**
	 * Get equipment master list in output order
	 * @return equipment master list in output order
	 */
	public List getEquipmentMasterListInOutputOrder()
	{
		return EquipmentUtilities.mergeEquipmentList(getEquipmentMasterList(), Constants.MERGE_NONE);
	}

	public Equipment getEquipmentNamed(final String aString, final List aList)
	{
		if (aList.isEmpty())
		{
			return null;
		}

		Equipment match = null;

		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (aString.equalsIgnoreCase(eq.getName()))
			{
				match = eq;
			}
		}

		if (match != null)
		{
			return match;
		}

		return null;
	}

	/**
	 * Set the characters eye colour
	 *
	 * @param  aString  the colour of their eyes
	 */
	public void setEyeColor(final String aString)
	{
		setStringFor(StringKey.EYE_COLOR, aString);
	}

	/**
	 * Get the characters eye colour
	 *
	 * @return  the colour of their eyes
	 */
	public String getEyeColor()
	{
		return getSafeStringFor(StringKey.EYE_COLOR);
	}

	/**
	 * Add a "real" (not virtual or auto) feat to the character
	 *
	 * @param   aFeat  the Ability (of category FEAT) to add
	 *
	 * @return  true if added successfully
	 */
	public boolean addRealFeat(final Ability aFeat)
	{
		//return abilityStore.addCategorisable(aFeat)
		return featList.add(aFeat);
	}

	/**
	 * Remove all "real" (not auto or auto) feats from the character
	 */
	public void clearRealFeats()
	{
		featList.clear();
	}

	/**
	 * Get number of "real" (not virtual or auto) feats the character has
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getNumberOfRealFeats()
	{
		return featList.size();
	}

	public List getRealFeatsList()
	{
		return (List) featList.clone();
	}

	/**
	 * Get an iterator over all the feats "Real" feats For Example, not virtual or auto
	 *
	 * @return  an iterator
	 */
	public Iterator getRealFeatsIterator()
	{
		return featList.iterator();
	}


	/**
	 * Returns the Feat definition searching by key (not name), as contained in
	 * the <b>chosen</b> feat list.
	 *
	 * @param   featName  String key of the feat to check for.
	 *
	 * @return  the Feat (not the CharacterFeat) searched for, <code>null</code>
	 *          if not found.
	 */
	public Ability getRealFeatKeyed(final String featName)
	{
		return getFeatKeyed(featName, featList);
	}


	/**
	 * Returns the Feat definition searching by name, as contained in the <b>
	 * chosen</b> feat list.
	 *
	 * @param   featName  String key of the feat to check for.
	 *
	 * @return  the Feat (not the CharacterFeat) searched for, <code>null</code>
	 *          if not found.
	 */

	public Ability getRealFeatNamed(final String featName)
	{
		return AbilityUtilities.getFeatNamedInList(featList, featName);
	}


	/**
	 * Does the character have this feat (not virtual or auto).
	 *
	 * @param   aFeat  The Ability object (of category FEAT) to check
	 *
	 * @return  True if the character has the feat
	 */
	public boolean hasRealFeat(final Ability aFeat)
	{
		//return (abilityStore.getCategorisableNamed(aCategory, aFeat) != null);
		return featList.contains(aFeat);
	}


	/**
	 * Check if the characterFeat ArrayList contains the named Feat.
	 *
	 * @param featName String name of the feat to check for.
	 * @return <code>true</code> if the character has the feat,
	 *         <code>false</code> otherwise.
	 */

	public boolean hasRealFeatNamed(final String featName)
	{
		return AbilityUtilities.getFeatNamedInList(featList, featName) != null;
	}


	/**
	 * Remove a "real" (for example, not virtual or auto) feat from the character.
	 *
	 * @param   aFeat  the Ability (of category FEAT) to remove
	 * @return  True if successfully removed
	 */
	public boolean removeRealFeat(final Ability aFeat)
	{
		return featList.remove(aFeat);
	}


	public void adjustFeats(final double arg)
	{
		feats += arg;
		setDirty(true);
	}

	public void setFeats(final double arg)
	{
		feats = arg;
		setDirty(true);
	}

	public double getFeats()
	{
		if (Globals.getGameModeHasPointPool())
		{
			return getSkillPoints();
		}
		return getRawFeats(true);
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

	private double getBonusFeatPool()
	{
		return getTotalBonusTo("FEAT", "POOL");
	}

	/**
	 * Checks whether a PC is allowed to level up. A PC is not allowed to
	 * level up if the "Enforce Spending" option is set and he still has
	 * unallocated skill points and/or feat slots remaining.
	 * This can be used to enforce correct spending of these resources
	 * when creating high-level multiclass characters.
	 * @return true if the PC can level up
	 */
	public boolean canLevelUp()
	{
		if (SettingsHandler.getEnforceSpendingBeforeLevelUp() && (getSkillPoints() > 0 || getFeats() > 0))
		{
			return false;
		}
		return true;
	}

	/**
	 * Query whether this PC should be able to select the ability passed
	 * in.  That is, does the PC meet the prerequisites and is the feat not
	 * one the PC already has, or if the PC has the feat already, is it one that
	 * can be taken multiple times.
	 * @param anAbility the ability to test
	 * @return true if the PC can take, false otherwise
	 */
	public boolean canSelectAbility (final Ability anAbility)
	{
		return this.canSelectAbility(anAbility, false);
	}

	/**
	 * Query whether this PC should be able to select the ability passed
	 * in.  That is, does the PC meet the prerequisites and is the feat not
	 * one the PC already has, or if the PC has the feat already, is it one that
	 * can be taken multiple times.
	 * TODO: When the PlayerCharacter Object can have abilities of category
	 * other than "FEAT" it will likely have methods to test "hasRealAbility" and
	 * "hasVirtualAbilty", change this (or add another) to deal with them
	 *
	 * @param anAbility the ability to test
	 * @param autoQualify if true, the PC automatically meets the prerequisites
	 * @return true if the PC can take, false otherwise
	 */
	public boolean canSelectAbility (final Ability anAbility, final boolean autoQualify)
	{
		final boolean qualify     = this.qualifiesForFeat(anAbility);
		final boolean canTakeMult = anAbility.isMultiples();
		final boolean hasOrdinary = this.hasRealFeatNamed(anAbility.getName());
		final boolean hasAuto     = this.hasFeatAutomatic(anAbility.getName());

		final boolean notAlreadyHas  = !(hasOrdinary || hasAuto);

		return (autoQualify || qualify) && (canTakeMult || notAlreadyHas);
	}

	/**
	 * Sets the filename of the character.
	 * @param newFileName
	 */
	public void setFileName(final String newFileName)
	{
		setStringFor(StringKey.FILE_NAME, newFileName);
	}

	/**
	 * Gets the filename of the character.
	 * @return file name of character
	 */
	public String getFileName()
	{
		return getSafeStringFor(StringKey.FILE_NAME);
	}

	public List getFollowerList()
	{
		return followerList;
	}

	public String getFullDisplayName()
	{
		final int levels = getTotalLevels();

		// If you aren't multi-classed, don't display redundant class level information in addition to the total PC level
		return new StringBuffer().append(getName()).append(" the ").append(levels).append(getOrdinal(levels))
		.append(" level ").append(getDisplayRaceName()).append(' ')
		.append((classList.size() < 2) ? getDisplayClassName() : getFullDisplayClassName()).toString();
	}

	/**
	 * Selector
	 * <p/>
	 * Build on-the-fly so removing templates won't mess up region
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

	public String getGender()
	{
		final String tGender = findTemplateGender();

		return tGender.equals(Constants.s_NONE) ? gender : tGender;
	}

	public void setGold(final String aString)
	{
		gold = new BigDecimal(aString);
		setDirty(true);
	}

	public BigDecimal getGold()
	{
		return gold;
	}

	public void setHairColor(final String aString)
	{
		setStringFor(StringKey.HAIR_COLOR, aString);
	}

	public String getHairColor()
	{
		return getSafeStringFor(StringKey.HAIR_COLOR);
	}

	public void setHairStyle(final String aString)
	{
		setStringFor(StringKey.HAIR_STYLE, aString);
	}

	public String getHairStyle()
	{
		return getSafeStringFor(StringKey.HAIR_STYLE);
	}

	public void setHanded(final String aString)
	{
		setStringFor(StringKey.HANDED, aString);
	}

	public String getHanded()
	{
		return getSafeStringFor(StringKey.HANDED);
	}

	public void setHeight(final int i)
	{
		heightInInches = i;
		setDirty(true);
	}

	public int getHeight()
	{
		return heightInInches;
	}

	public void setImporting(final boolean newIsImporting)
	{
		this.importing = newIsImporting;
	}

	/**
	 * 0-level feat count (racial, templates, etc.), excluding any
	 * feats from leveling.
	 *
	 * @return count of initial, non-leveling feats
	 */
	public double getInitialFeats()
	{
		double initFeats = 0.0;

		final String monsterRace = getRace().getMonsterClass(this, false);
		if (monsterRace==null || !isMonsterDefault())
		{
			initFeats =  getRace().getBonusInitialFeats();
		}

		final List aList = getTemplateList();

		if (!aList.isEmpty() && PlayerCharacterUtilities.canReassignTemplateFeats())
		{
			for (Iterator e = aList.iterator(); e.hasNext();)
			{
				final PCTemplate template = (PCTemplate) e.next();

				if (template != null)
				{
					initFeats += template.getBonusInitialFeats();
				}
			}
		}

		return initFeats;
	}

	public void setInterests(final String aString)
	{
		setStringFor(StringKey.INTERESTS, aString);
	}

	public String getInterests()
	{
		return getSafeStringFor(StringKey.INTERESTS);
	}

	public TreeSet getLanguagesList()
	{
		return languages;
	}

	public String getLanguagesListNames()
	{
		final TreeSet aSet = getLanguagesList();
		final StringBuffer b = new StringBuffer();

		for (Iterator i = aSet.iterator(); i.hasNext();)
		{
			if (b.length() > 0)
			{
				b.append(", ");
			}

			b.append(i.next().toString());
		}

		return b.toString();
	}

	public void setLocation(final String aString)
	{
		setStringFor(StringKey.LOCATION, aString);
	}

	public String getLocation()
	{
		return getSafeStringFor(StringKey.LOCATION);
	}

	/**
	 * Set the master for this object
	 * also set the level dependent stats based on the masters level
	 * and info contained in the companionModList Array
	 * such as HitDie, SR, BONUS, SA, etc
	 * @param aM The master to be set.
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

		//
		// Get total wizard + sorcer levels as they stack like a mother
		// Doh!!
		int mTotalLevel = 0;
		int addHD = 0;

		for (Iterator c = mPC.getClassList().iterator(); c.hasNext();)
		{
			final PCClass mClass = (PCClass) c.next();
			boolean found = false;

			for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
			{
				final CompanionMod aComp = (CompanionMod) cm.next();
				final String aType = aComp.getType().toUpperCase();

				if (!(aType.equalsIgnoreCase(aM.getType())))
				{
					continue;
				}

				if ((aComp.getLevel(mClass.getName()) > 0) && !found)
				{
					mTotalLevel += mClass.getLevel();
					found = true;
				}
			}
		}

		// Clear the companionModList so we can add everything to it
		companionModList.clear();

		// This will be handled by getRaceType()
//		String newRaceType = "";
//		final String oldRaceType = race.getType();

		// New way of doing this. Through VARs on the Master
		for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
		{
			final CompanionMod aComp = (CompanionMod) cm.next();
			final String aType = aComp.getType().toUpperCase();

			if (!(aType.equalsIgnoreCase(aM.getType())))
			{
				continue;
			}

			for (Iterator iType = aComp.getVarMap().keySet().iterator(); iType.hasNext();)
			{
				final String varName = (String) iType.next();

				if (mPC.getVariableValue(varName, "").intValue() >= aComp.getLevel(varName))
				{
					if (!companionModList.contains(aComp))
					{
						companionModList.add(aComp);
						addHD += aComp.getHitDie();

						// if necessary, switch
						// the race type
//						if (aComp.getCompanionSwitch(oldRaceType) != null)
//						{
//							newRaceType = aComp.getCompanionSwitch(oldRaceType);
//						}
					}
				}
			}
		}

		// Old way of doing this. Through Class levels
		for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
		{
			final CompanionMod aComp = (CompanionMod) cm.next();
			final String aType = aComp.getType().toUpperCase();

			// This CompanionMod must be for this type of follower
			if (!(aType.equalsIgnoreCase(aM.getType())))
			{
				continue;
			}

			// Check all the masters classes
			for (Iterator c = mPC.getClassList().iterator(); c.hasNext();)
			{
				final PCClass mClass = (PCClass) c.next();
				final int mLev = mClass.getLevel();
				final int compLev = aComp.getLevel(mClass.getName());

				if (compLev < 0)
				{
					continue;
				}

				// This CompanionMod must be for this Class
				// and for the correct level or lower
				if ((compLev <= mLev) || (compLev <= mTotalLevel))
				{
					if (!companionModList.contains(aComp))
					{
						companionModList.add(aComp);
						addHD += aComp.getHitDie();

						// if necessary, switch
						// the race type
//						if (aComp.getCompanionSwitch(oldRaceType) != null)
//						{
//							newRaceType = aComp.getCompanionSwitch(oldRaceType);
//						}
					}
				}
			}
		}

/*
		PCClass newClass;

		if ((newRaceType != null) && (newRaceType.length() > 0))
		{
			newClass = Globals.getClassNamed(newRaceType);
			race.setTypeInfo(".CLEAR." + newRaceType);
			setDirty(true);

			// we now have to swap all the old "Race" levels
			final PCClass oldClass = getClassNamed(oldRaceType);
			int oldLevel = 0;

			if (oldClass != null)
			{
				oldLevel = oldClass.getLevel();
			}

			if ((oldLevel > 0) && (newClass != null))
			{
				// turn oldLevel negative
				final int negLevel = oldLevel * -1;

				// yes, it's weird that incrementClassLevel
				// can be called with a negative value
				incrementClassLevel(negLevel, oldClass, true);

				// now add levels back in the new class
				incrementClassLevel(oldLevel, newClass, true);
			}
		}
*/
		//
		// Add additional HD if required
//		newClass = Globals.getClassNamed(race.getType());
		PCClass aClass = Globals.getClassNamed(race.getMonsterClass(this, false));

		final int usedHD = followerMaster.getUsedHD();
		addHD -= usedHD;

//		if ((newClass != null) && (addHD != 0))
		if ((aClass != null) && (addHD != 0))
		{
			// set the new HD (but only do it once!)
//			incrementClassLevel(addHD, newClass, true);
			incrementClassLevel(addHD, aClass, true);
			followerMaster.setUsedHD(addHD+usedHD);
			setDirty(true);
		}

		// If it's a familiar, we need to change it's Skills
		if (getUseMasterSkill())
		{
			final List mList = mPC.getSkillList();
			final List sNameList = new ArrayList();

			// now we have to merge the two lists together and
			// take the higher rank of each skill for the Familiar
			for (Iterator a = getAllSkillList(true).iterator(); a.hasNext();)
			{
				final Skill fSkill = (Skill) a.next();

				for (Iterator b = mList.iterator(); b.hasNext();)
				{
					final Skill mSkill = (Skill) b.next();

					// first check to see if familiar
					// already has ranks in the skill
					if (mSkill.getName().equals(fSkill.getName()))
					{
						// need higher rank of the two
						if (mSkill.getRank().intValue() > fSkill.getRank().intValue())
						{
							// first zero current
//							fSkill.setZeroRanks(newClass, this);
//							fSkill.modRanks(mSkill.getRank().doubleValue(), newClass, true, this);
							fSkill.setZeroRanks(aClass, this);
							fSkill.modRanks(mSkill.getRank().doubleValue(), aClass, true, this);
						}
					}

					// build a list of all skills a master
					// posesses, but the familiar does not
					if (!hasSkill(mSkill.getName()) && !sNameList.contains(mSkill.getName()))
					{
						sNameList.add(mSkill.getName());
					}
				}
			}

			// now add all the skills only the master has
			for (Iterator sn = sNameList.iterator(); sn.hasNext();)
			{
				final String skillName = (String) sn.next();

				// familiar doesn't have skill,
				// but master does, so add it
				final Skill newSkill = (Skill) Globals.getSkillNamed(skillName).clone();
				final double sr = mPC.getSkillNamed(skillName).getRank().doubleValue();

				if ((newSkill.getChoiceString() != null) && (newSkill.getChoiceString().length() > 0))
				{
					continue;
				}

//				newSkill.modRanks(sr, newClass, true, this);
				newSkill.modRanks(sr, aClass, true, this);
				getSkillList().add(newSkill);
			}
		}
		setDirty(true);
	}

	/**
	 * Get the Follower object that is the "master" for this object
	 * @return follower master
	 */
	public Follower getMaster()
	{
		return followerMaster;
	}

	/**
	 * Get the PlayerCharacter that is the "master" for this object
	 * @return master PC
	 */
	public PlayerCharacter getMasterPC()
	{
		if (followerMaster == null)
		{
			return null;
		}

		for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
		{
			final PlayerCharacter nPC = (PlayerCharacter) p.next();

			if (followerMaster.getFileName().equals(nPC.getFileName()))
			{
				return nPC;
			}
		}

		// could not find a filename match, let's try the Name
		for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
		{
			final PlayerCharacter nPC = (PlayerCharacter) p.next();

			if (followerMaster.getName().equals(nPC.getName()))
			{
				return nPC;
			}
		}

		// no Name and no FileName match, so must not be loaded
		return null;
	}

	public boolean isMonsterDefault()
	{
		return useMonsterDefault;
	}

	public void setName(final String aString)
	{
		setStringFor(StringKey.NAME, aString);
	}

	public String getName()
	{
		return getSafeStringFor(StringKey.NAME);
	}

	/**
	 * Takes all the Temporary Bonuses and Merges
	 * them into just the unique named bonuses.
	 *
	 * @return    List of Strings
	 */
	public List getNamedTempBonusList()
	{
		final List aList = new ArrayList();

		for (Iterator ab = getTempBonusList().iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) ab.next();

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

			final String aName = aCreator.getName();

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
		int temp;

		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			temp = aTemplate.getNonProficiencyPenalty();
			if (temp <= 0)
			{
				npp = temp;
			}
		}

		return npp;
	}

	public ArrayList getNotesList()
	{
		return notesList;
	}

	public void setPhobias(final String aString)
	{
		setStringFor(StringKey.PHOBIAS, aString);
	}

	public String getPhobias()
	{
		return getSafeStringFor(StringKey.PHOBIAS);
	}

	public void setPlayersName(final String aString)
	{
		setStringFor(StringKey.PLAYERS_NAME, aString);
	}

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
	 * Selector
	 * Sets the path to the portrait of the character.
	 *
	 * @param newPortraitPath the path to the portrait file
	 */
	public void setPortraitPath(final String newPortraitPath)
	{
		setStringFor(StringKey.PORTRAIT_PATH, newPortraitPath);
	}

	/**
	 * Selector
	 * Gets the path to the portrait of the character.
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
	public List getPrimaryWeapons()
	{
		return primaryWeapons;
	}

	/**
	 * Get race
	 * @return race
	 */
	public Race getRace()
	{
		return race;
	}

	/**
	 * Set region
	 * @param arg
	 */
	public void setRegion(final String arg)
	{
		setStringFor(StringKey.REGION, arg);
	}

	/**
	 * Set sub region
	 * @param aString
	 */
	public void setSubRegion(final String aString)
	{
		setStringFor(StringKey.SUB_REGION, aString);
	}

	/**
	 * Selector
	 * <p/>
	 * Build on-the-fly so removing templates won't mess up region
	 *
	 * @return character region
	 */
	public String getRegion()
	{
		return getRegion(true);
	}

	/**
	 * Get region
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
			final PCTemplate template = (PCTemplate) templateList.get(i);
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
	 * @param aString
	 */
	public void setResidence(final String aString)
	{
		setStringFor(StringKey.RESIDENCE, aString);
	}

	/**
	 * Get residence
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
	public List getSecondaryWeapons()
	{
		return secondaryWeapons;
	}

	/**
	 * Get HTML sheet for selected character
	 * @param aString
	 */
	public void setSelectedCharacterHTMLOutputSheet(final String aString)
	{
		outputSheetHTML = aString;
	}

	/**
	 * Location of HTML Output Sheet
	 * @return HTML output sheet
	 */
	public String getSelectedCharacterHTMLOutputSheet()
	{
		return outputSheetHTML;
	}

	/**
	 * Set selected PDF character sheet for character
	 * @param aString
	 */
	public void setSelectedCharacterPDFOutputSheet(final String aString)
	{
		outputSheetPDF = aString;
	}

	/**
	 * Location of PDF Output Sheet
	 * @return pdf output sheet
	 */
	public String getSelectedCharacterPDFOutputSheet()
	{
		return outputSheetPDF;
	}

	/**
	 * Get list of shield proficiencies
	 * @return shield prof list
	 */
	public ArrayList getShieldProfList()
	{
		shieldProfList.clear();

		final List autoShieldProfList = getAutoShieldProfList();
		addShieldProfs(autoShieldProfList);

		final List selectedProfList = getSelectedShieldProfList();
		addShieldProfs(selectedProfList);

		return shieldProfList;
	}

	/**
	 * Get size
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
	 * @return list of skills
	 */
	public ArrayList getSkillList()
	{
		return getAllSkillList(false);
	}

	/**
	 * Retrieves a list of the character's skills in output order. This is in
	 * ascending order of the skill's outputIndex field. If skills have the
	 * same outputIndex they will be ordered by name. Note hidden skills
	 * (outputIndex = -1) are not included in this list.
	 *
	 * @return An ArrayList of the skill objects in output order.
	 */
	public ArrayList getSkillListInOutputOrder()
	{
		return getSkillListInOutputOrder((ArrayList) getSkillList().clone());
	}

	/**
	 * Retrieves a list of the character's skills in output order. This is in
	 * ascending order of the skill's outputIndex field. If skills have the
	 * same outputIndex they will be ordered by name. Note hidden skills
	 * (outputIndex = -1) are not included in this list.
	 *
	 * Deals with sorted list
	 * @param sortedList
	 *
	 * @return An ArrayList of the skill objects in output order.
	 */
	public ArrayList getSkillListInOutputOrder(final ArrayList sortedList)
	{
		Collections.sort(sortedList,
			new Comparator()
			{
				/**
				 * Comparator will be specific to Skill objects
				 */
				public int compare(final Object obj1, final Object obj2)
				{
					int obj1Index = ((Skill) obj1).getOutputIndex();
					int obj2Index = ((Skill) obj2).getOutputIndex();

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
						return ((Skill) obj1).getName().compareToIgnoreCase(((Skill) obj2).getName());
					}
				}
			});

		// Remove the hidden skills from the list
		for (Iterator i = sortedList.iterator(); i.hasNext();)
		{
			final Skill bSkill = (Skill) i.next();

			if (bSkill.getOutputIndex() == -1)
			{
				i.remove();
			}
		}

		return sortedList;
	}

	/**
	 * Set skill points
	 * @param anInt
	 */
	public void setSkillPoints(final int anInt)
	{
		setDirty(true);
	}

	/**
	 * Get skill points
	 * @return skill points
	 */
	public int getSkillPoints()
	{
		int returnValue = 0;

		// First compute gained points, and then remove the already spent ones.
		// We can't use Remaining points because the level may be removed, and then we have
		// to display this as -x on the "Total Skill Points" field
		for (Iterator e = getLevelInfo().iterator(); e.hasNext();)
		{
			final PCLevelInfo pcli = (PCLevelInfo) e.next();
			returnValue += pcli.getSkillPointsGained();
		}

		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill) e.next();

			for (int idx = 0; idx < aSkill.getRankList().size(); idx++)
			{
				final String bSkill = (String) aSkill.getRankList().get(idx);
				final int iOffs = bSkill.indexOf(':');
				final double curRank = Double.parseDouble(bSkill.substring(iOffs + 1));
				final PCClass pcClass = getClassKeyed(bSkill.substring(0, iOffs));
				if (pcClass != null)
				{
					// Only add the cost for skills associated with a class.
					// Skill ranks from feats etc are free.
					final double cost =  aSkill.costForPCClass(pcClass, this);
					returnValue -= (int) (cost * curRank);
				}
			}
		}
		if (Globals.getGameModeHasPointPool())
		{
			returnValue += (int) getRawFeats(false);		// DO NOT CALL getFeats() here! It will set up a recursive loop and result in a stack overflow!
		}
		return returnValue;
	}

	/**
	 * Set skin colour
	 * @param aString
	 */
	public void setSkinColor(final String aString)
	{
		setStringFor(StringKey.SKIN_COLOR, aString);
	}

	/**
	 * Get skin colour
	 * @return skin colour
	 */
	public String getSkinColor()
	{
		return getSafeStringFor(StringKey.SKIN_COLOR);
	}

	/**
	 * Get list of special abilities
	 * @return List of special abilities
	 */
	public List getSpecialAbilityList()
	{
		// aList will contain a list of SpecialAbility objects
		List aList = (ArrayList) specialAbilityList.clone();

		final int atl = getTotalLevels();
		final int thd = totalHitDice();

		// Try all possible POBjects
		for (Iterator i = getPObjectList().iterator(); i.hasNext();)
		{
			final PObject aPObj = (PObject) i.next();

			if (aPObj == null)
			{
				continue;
			}

			if (aPObj instanceof PCTemplate)
			{
				final PCTemplate bTemplate = Globals.getTemplateNamed(aPObj.getName());

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
	 * @return List of special abilities as Strings
	 */
	public ArrayList getSpecialAbilityListStrings()
	{
		final List aList = getSpecialAbilityList();
		final ArrayList bList = new ArrayList();

		if (!aList.isEmpty())
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final Object obj = i.next();
				final SpecialAbility sa = (SpecialAbility) obj;

				if (!PrereqHandler.passesAll( sa.getPreReqList(), this, sa ))
				{
					continue;
				}
				final String saText = sa.getParsedText(this);
				if (saText!=null && !saText.equals(""))
				{
					bList.add(saText);
				}
			}
		}

		return bList;
	}

	/**
	 * same as getSpecialAbilityList except if
	 * if you have the same ability twice, it only
	 * lists it once with (2) at the end.
	 * @return List
	 */
	public ArrayList getSpecialAbilityTimesList()
	{
		final ArrayList abilityList = getSpecialAbilityListStrings();
		final ArrayList sortList = new ArrayList();
		final int[] numTimes = new int[abilityList.size()];

		for(int i = 0; i < abilityList.size(); i++)
		{
			final String ability = (String)abilityList.get(i);
			if(!sortList.contains(ability)) {
				sortList.add(ability);
				numTimes[i] = 1;
			}
			else {
				for(int j = 0; j < sortList.size(); j++)
				{
					final String testAbility = (String)sortList.get(j);
					if(testAbility.equals(ability))
					{
						numTimes[j]++;
					}
				}
			}
		}

		final ArrayList retList = new ArrayList();
		for(int i = 0; i < sortList.size(); i++) {
			String ability = (String) sortList.get(i);
			if(numTimes[i] > 1) {
				ability = ability + " (" + numTimes[i] + ")";
			}
			retList.add(ability);
		}

		return retList;
	}

	/**
	 * Set speech tendency
	 * @param aString
	 */
	public void setSpeechTendency(final String aString)
	{
		setStringFor(StringKey.SPEECH_TENDENCY, aString);
	}

	/**
	 * Get speech tendency
	 * @return speech tendency
	 */
	public String getSpeechTendency()
	{
		return getSafeStringFor(StringKey.SPEECH_TENDENCY);
	}

	/**
	 * Set the name of the spellbook to auto add new known spells to.
	 * @param aString The new spellbook name.
	 */
	public void setSpellBookNameToAutoAddKnown(final String aString)
	{
		setStringFor(StringKey.SPELLBOOK_AUTO_ADD_KNOWN, aString);
	}

	/**
	 * Get the name of the spellbook to auto add new known spells to.
	 * @return spellbook name
	 */
	public String getSpellBookNameToAutoAddKnown()
	{
		return getSafeStringFor(StringKey.SPELLBOOK_AUTO_ADD_KNOWN);
	}

	
	/**
	 * Retrieve a spell book object given the name of the spell book.
	 * 
	 * @param name The name of the spell book to be retrieved.
	 * @return The spellbook (or null if not present).
	 */
	public SpellBook getSpellBookByName(final String name)
	{
		return (SpellBook) spellBookMap.get(name);
	}

	/**
	 * Get spell books
	 * @return spellBooks
	 */
	public List getSpellBooks()
	{
		return spellBooks;
	}

	/**
	 * Get spell class given an index
	 * @param ix
	 * @return spell class
	 */
	public PObject getSpellClassAtIndex(final int ix)
	{
		final List aList = getSpellClassList();

		if ((ix >= 0) && (ix < aList.size()))
		{
			return (PObject) aList.get(ix);
		}

		return null;
	}

	/**
	 * a temporary placeholder used for computing the DC of a spell
	 * Set from within Spell.java before the getVariableValue() call
	 * @param i
	 */
	public void setSpellLevelTemp(final int i)
	{
		//Explicitly should *not* set the dirty flag to true.
		spellLevelTemp = i;
	}

	/**
	 * Get spell level temp
	 * @return temp spell level
	 */
	public int getSpellLevelTemp()
	{
		return spellLevelTemp;
	}

	/**
	 * Get the stat list
	 * @return stat list
	 */
	public StatList getStatList()
	{
		return statList;
	}

	/**
	 * Selector
	 * <p/>
	 * Build on-the-fly so removing templates won't mess up subrace
	 *
	 * @return character subrace
	 */
	public String getSubRace()
	{
		String subRace = Constants.s_NONE;

		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate) templateList.get(i);
			final String tempSubRace = template.getSubRace();

			if (!tempSubRace.equals(Constants.s_NONE))
			{
				subRace = tempSubRace;
			}
		}

		return subRace;
	}

	/**
	 * Selector
	 * <p/>
	 * Build on-the-fly so removing templates won't mess up sub region
	 *
	 * @return character sub region
	 */
	public String getSubRegion()
	{
		return getSubRegion(true);
	}

	/**
	 * Set the name on the tab
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
	 * @return name on tab
	 */
	public String getTabName()
	{
		return tabName;
	}

	/**
	 * Temporary Bonuses
	 **/

	/**
	 * List if Items which have Temp Bonuses applied to them
	 * @return List
	 */
	private List getTempBonusItemList()
	{
		return tempBonusItemList;
	}

	/**
	 * Set temp bonus list
	 * @param aList
	 */
	public void setTempBonusList(final List aList)
	{
		tempBonusList = aList;
		setDirty(true);
	}

	/**
	 * Temp Bonus list
	 * @return List
	 */
	public List getTempBonusList()
	{
		return tempBonusList;
	}

	/**
	 * get filtered temp bonus list
	 * @return filtered temp bonus list
	 */
	public List getFilteredTempBonusList()
	{
		final List ret= new ArrayList();
		for(Iterator i=getTempBonusList().iterator(); i.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) i.next();
			if (!tempBonusFilters.contains(aBonus.getName()))
			{
				ret.add(aBonus);
			}
		}
		return ret;
	}

	/**
	 * get temp bonus filters
	 * @return temp bonus filters
	 */
	public Set getTempBonusFilters()
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
	 * @param aBonusStr
	 */
	public void setTempBonusFilter(final String aBonusStr)
	{
		tempBonusFilters.add(aBonusStr);
		calcActiveBonuses();
	}

	/**
	 * unset temp bonus filter
	 * @param aBonusStr
	 */
	public void unsetTempBonusFilter(final String aBonusStr)
	{
		tempBonusFilters.remove(aBonusStr);
		calcActiveBonuses();
	}

	/**
	 * Given a Source and a Target object, get a list of BonusObj's
	 * @param aCreator
	 * @param aTarget
	 *
	 * @return    List of BonusObj
	 */
	public List getTempBonusList(final String aCreator, final String aTarget)
	{
		final List aList = new ArrayList();

		for (Iterator i = getTempBonusList().iterator(); i.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) i.next();
			final Object aTO = aBonus.getTargetObject();
			final Object aCO = aBonus.getCreatorObject();

			String targetName = "";
			String creatorName = "";

			if (aCO instanceof PObject)
			{
				creatorName = ((PObject) aCO).getName();
			}

			if (aTO instanceof PlayerCharacter)
			{
				targetName = getName();
			}
			else if (aTO instanceof PObject)
			{
				targetName = ((PObject) aTO).getName();
			}

			if (creatorName.equals(aCreator) && targetName.equals(aTarget))
			{
				aList.add(aBonus);
			}
		}

		return aList;
	}


	/**
	 * Get the list of Templates applied to this PC
	 *
	 * @return    List of templates
	 **/
	public ArrayList getTemplateList()
	{
		return templateList;
	}

	/**
	 * Retrieve a list of the templates applied to this PC that should be
	 * visible on output.
	 * @return The list of templates visible on output sheets.
	 */
	public List getOutputVisibleTemplateList()
	{
		List tl = new ArrayList();
		PCTemplate template;

		for (Iterator it = getTemplateList().iterator(); it.hasNext();)
		{
			template = (PCTemplate) it.next();

			if ((template.isVisible() == PCTemplate.VISIBILITY_DEFAULT)
				|| (template.isVisible() == PCTemplate.VISIBILITY_OUTPUT_ONLY))
			{
				tl.add(template);
			}
		}
		return tl;
	}

	/**
	 * Get the template named aName from this PC
	 * @param aName
	 *
	 * @return    PC template or null if not found
	 */
	public PCTemplate getTemplateNamed(final String aName)
	{
		for (Iterator ti = templateList.iterator(); ti.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) ti.next();

			if (aTemplate.getName().equalsIgnoreCase(aName))
			{
				return aTemplate;
			}
		}

		return null;
	}

	/**
	 * Set trait 1
	 * @param aString
	 */
	public void setTrait1(final String aString)
	{
		setStringFor(StringKey.TRAIT1, aString);
	}

	/**
	 * Get trait 1
	 * @return trait 1
	 */
	public String getTrait1()
	{
		return getSafeStringFor(StringKey.TRAIT1);
	}

	/**
	 * Set trait 2
	 * @param aString
	 */
	public void setTrait2(final String aString)
	{
		setStringFor(StringKey.TRAIT2, aString);
	}

	/**
	 * Get trait 2
	 * @return trait 2
	 */
	public String getTrait2()
	{
		return getSafeStringFor(StringKey.TRAIT2);
	}

	/**
	 * get unused feat count
	 * @return unused feat count
	 */
	public double getUsedFeatCount()
	{
		double iCount = 0;

		Iterator it = getRealFeatsIterator();
		while (it.hasNext())
		{
			final Ability aFeat = (Ability) it.next();

			//
			// Don't increment the count for
			// hidden feats so the number
			// displayed matches this number
			//
			//if (aFeat.isVisible() == Feat.VISIBILITY_HIDDEN)
			//{
			//	continue;
			//}
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

	public Float getVariable(final String variableString, final boolean isMax, final boolean includeBonus, final String matchSrc, final String matchSubSrc, int decrement)
	{
		return getVariable(variableString, isMax, includeBonus, matchSrc, matchSubSrc, true, decrement);
	}

	private double getMinMaxFirstValue(final boolean isNewValue, final boolean isMax, final double oldValue, final double newValue)
	{
		if (!isNewValue)
			return newValue;
		if (isMax)
			return Math.max(oldValue, newValue);
		return Math.min(oldValue, newValue);
	}
	/**
	 * Should probably be refactored to return a String instead.
	 *
	 * @param variableString
	 * @param isMax
	 * @param includeBonus
	 * @param matchSrc
	 * @param matchSubSrc
	 * @param recurse
	 * @param decrement
	 * @return Float
	 */
	public Float getVariable(final String variableString, final boolean isMax, boolean includeBonus, final String matchSrc, final String matchSubSrc, final boolean recurse, int decrement)
	{
		double value = 0.0;
		boolean found = false;

		if (lastVariable != null)
		{
			if (lastVariable.equals(variableString))
			{
				Logging.debugPrint("This is a deliberate warning message, not an error - Avoiding infinite loop in getVariable: repeated lookup of \"" + lastVariable + "\" at " + value);
				lastVariable = null;
				return new Float(value);
			}
		}

		if (!variableList.isEmpty())
		{
			for (int i = 0; i < variableList.size(); i++)
			{
				final String vString = (String) variableList.get(i);
				final StringTokenizer aTok = new StringTokenizer(vString, "|");
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
					final double newValue = getVariableValue(sString, src).doubleValue();
					value = getMinMaxFirstValue(found, isMax, value, newValue);

					found = true;
					if (!"".equals(loopVariable))
					{
						while (loopValue > decrement)
						{
							loopValue -= decrement;
							value += getVariableValue(sString, src).doubleValue();
						}

						loopValue = 0;
						loopVariable = "";
					}
				}
			}
		}

		// Now check the feats to see if they modify the variable
		if (!aggregateFeatList().isEmpty())
		{
			List aggregateFeatList = aggregateFeatList();
			for (int i = 0; i < aggregateFeatList.size(); i++)
			{
				final Ability obj = (Ability)aggregateFeatList.get(i);
				final String varInList = checkForVariableInList(obj, variableString, isMax, "", "", found, value, decrement);

				if (varInList.length() > 0)
				{
					value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(varInList));
					found = true;
				}
			}
		}

		if (!getSkillList().isEmpty())
		{
			List aSkillList = getSkillList();
			for (int i = 0; i < aSkillList.size(); i++)
			{
				final Skill obj = (Skill)aSkillList.get(i);
				final String varInList = checkForVariableInList(obj, variableString, isMax, "", "", found, value, 0);

				if (varInList.length() > 0)
				{
					value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(varInList));
					found = true;
				}
			}
		}

		if (!equipmentList.isEmpty())
		{
			for (int i = 0; i < equipmentList.size(); i++)
			{
				final Equipment obj = (Equipment)equipmentList.get(i);
				final String eS = checkForVariableInList(obj, variableString, isMax, "", "", found, value, 0);

				if (eS.length() > 0)
				{
					value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(eS));
					found = true;
				}

				final List aList = obj.getEqModifierList(true);

				if (!aList.isEmpty())
				{
					for (Iterator el = aList.iterator(); el.hasNext();)
					{
						final EquipmentModifier em = (EquipmentModifier) el.next();
						final String varInList = checkForVariableInList(em, variableString, isMax, "", "", found, value, decrement);

						if (varInList.length() > 0)
						{
							value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(varInList));
							found = true;
						}
					}
				}

				final List aList2 = obj.getEqModifierList(false);

				if (!aList2.isEmpty())
				{
					for (Iterator el = aList2.iterator(); el.hasNext();)
					{
						final EquipmentModifier em = (EquipmentModifier) el.next();
						final String varInList = checkForVariableInList(em, variableString, isMax, "", "", found, value, decrement);

						if (varInList.length() > 0)
						{
							value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(varInList));
							found = true;
						}
					}
				}
			}
		}

		if (!templateList.isEmpty())
		{
			for (int i = 0; i < templateList.size(); i++)
			{
				final PCTemplate obj = (PCTemplate)templateList.get(i);
				final String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value, decrement);

				if (aString.length() > 0)
				{
					value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(aString));
					found = true;
				}
			}
		}

		if (!companionModList.isEmpty())
		{
			for (int i = 0; i < companionModList.size(); i++)
			{
				final CompanionMod obj = (CompanionMod)companionModList.get(i);
				final String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value, decrement);

				if (aString.length() > 0)
				{
					value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(aString));
					found = true;
				}
			}
		}

		if (race != null)
		{
			final String aString = checkForVariableInList(race, variableString, isMax, "", "", found, value, decrement);

			if (aString.length() > 0)
			{
					value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(aString));
					found = true;
			}
		}

		if (deity != null)
		{
			final String aString = checkForVariableInList(deity, variableString, isMax, "", "", found, value, decrement);

			if (aString.length() > 0)
			{
					value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(aString));
					found = true;
			}
		}

		if (!characterDomainList.isEmpty())
		{
			for (int i = 0; i < characterDomainList.size(); i++)
			{
				final CharacterDomain obj = (CharacterDomain)characterDomainList.get(i);

				if (obj.getDomain() == null)
				{
					continue;
				}

				final String aString = checkForVariableInList(obj.getDomain(), variableString, isMax, "", "", found, value, decrement);

				if (aString.length() > 0)
				{
					value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(aString));
					found = true;
				}
			}
		}

		if (!weaponProfList.isEmpty())
		{
			for (Iterator oi = weaponProfList.iterator(); oi.hasNext();)
			{
				final WeaponProf obj = Globals.getWeaponProfNamed((String) oi.next());

				if (obj == null)
				{
					continue;
				}

				final String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value, decrement);

				if (aString.length() > 0)
				{
					value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(aString));
					found = true;
				}
			}
		}

		for (int i = 0; i < statList.getStats().size(); i++)
		{
			final PCStat obj = (PCStat)statList.getStats().get(i);
			final String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value, decrement);

			if (aString.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(aString));
				found = true;
			}
		}

		for (int i = 0; i < SettingsHandler.getGame().getUnmodifiableAlignmentList().size(); i++)
		{
			final PCAlignment obj = (PCAlignment)SettingsHandler.getGame().getUnmodifiableAlignmentList().get(i);
			final String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value, decrement);

			if (aString.length() > 0)
			{
				value = getMinMaxFirstValue(found, isMax, value, Float.parseFloat(aString));
				found = true;
			}
		}


		if (!found)
		{
			if (recurse)
			{
				lastVariable = variableString;
				value = getVariableValue(variableString, "").floatValue();
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

	public void setVirtualFeatsStable(final boolean stable)
	{
		virtualFeatsStable = stable;
		//setDirty(true);
	}

	public TreeSet getWeaponProfList()
	{
		final TreeSet wp = new TreeSet(weaponProfList);

		// Try all possible PObjects
		for (Iterator i = getPObjectList().iterator(); i.hasNext();)
		{
			final PObject aPObj = (PObject) i.next();

			if (aPObj != null)
			{
				wp.addAll(aPObj.getSafeListFor(ListKey.SELECTED_WEAPON_PROF_BONUS));
			}
		}

		return wp;
	}

	public void setWeight(final int i)
	{
		weightInPounds = i ;
		setDirty(true);
	}

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
		// internal notion of experiene
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
		//setDirty(true);
	}

	public void addArmorProfs(final List aList)
	{
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			addArmorProf((String) i.next());
		}
	}

	public void addEquipSet(final EquipSet set)
	{
		equipSetList.add(set);
		setDirty(true);
	}

	public void addEquipment(final Equipment eq)
	{
		equipmentList.add(eq);

		if (!equipmentMasterList.contains(eq))
		{
			equipmentMasterList.add(eq);
		}
		
		if (eq.isType(Constants.s_TYPE_SPELLBOOK))
		{
			SpellBook book = new SpellBook(eq.getName(), SpellBook.TYPE_SPELL_BOOK);
			book.setEquip(eq);
			addSpellBook(book);
		}
		setDirty(true);
	}

	public void addFeat(final Ability aFeat, final PCLevelInfo playerCharacterLevelInfo)
	{
		if (hasRealFeat(aFeat))
		{
			Logging.errorPrint("Adding duplicate feat: " + aFeat.getName());
		}

		if (!addRealFeat(aFeat))
		{
			Logging.errorPrint("Problem adding feat: " + aFeat.getName());
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
	 * @param aList
	 * @return bonus from list
	 */
	public double calcBonusFromList(final List aList)
	{
		double iBonus = 0;

		if (aList.isEmpty())
		{
			return iBonus;
		}

		for (Iterator b = aList.iterator(); b.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) b.next();
			final PObject anObj = (PObject) aBonus.getCreatorObject();

			if (anObj == null)
			{
				continue;
			}

			iBonus += anObj.calcBonusFrom(aBonus, this, this);
		}

		return iBonus;
	}

	public void calcSizeAdjustmentBonuses()
	{
		activateAndAddBonusesFromPObject(getSizeAdjustment());
	}

	public void calcStatBonuses()
	{
		activateAndAddBonusesFromPObjectList(statList.getStats());
	}

	public boolean checkQualifyList(final String qualifierItem)
	{
		return getQualifyList().contains(qualifierItem);
	}

	/**
	 * Returns the number of hands required to wield weapon
	 * @param eq
	 * @param wp
	 * @return number of hands required
	 */
	public int handsRequired(final Equipment eq, final WeaponProf wp)
	{
		if (!hasWeaponProfNamed(wp.getName()))
		{
			return 2;
		}

		return Globals.handsRequired(this, eq, wp);
	}

	/**
	 * Checks to see if this PC has the weapon proficiency named aName
	 *
	 * @param aName
	 * @return boolean
	 */
	public boolean hasWeaponProfNamed(final String aName)
	{
		for (Iterator i = getWeaponProfList().iterator(); i.hasNext();)
		{
			if (aName.equalsIgnoreCase((String) i.next()))
			{
				return true;
			}
		}

		return false;
	}

	public Equipment getEquipmentNamed(final String aString)
	{
		return getEquipmentNamed(aString, getEquipmentMasterList());
	}

	public ArrayList getMiscList()
	{
		return miscList;
	}

	public void buildVariableSet()
	{
		// Building the PObject list relies on variables for evaluating prereqs,
		// so we have to grab it before clearing out the variables.
		List pObjList = getPObjectList();
		variableSet.clear();

		// Go through all objects that could add a VAR
		// and build the HashSet
		// Try all possible POBjects
		for (Iterator i = pObjList.iterator(); i.hasNext();)
		{
			final PObject aPObj = (PObject) i.next();

			if (aPObj != null)
			{
				variableSet.addAll(aPObj.getVariableNamesAsUnmodifiableSet());
			}
		}

		// Some vfeats rely on variables as prereqs, hence the need to
		// recalc them after we get all vars.
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
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			final EquipSet es = (EquipSet) e.next();
			final String abParentId = es.getParentIdPath() + ".";
			final String abPid = pid + ".";

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

		final List tmpList = new ArrayList();

		// now find and remove equipment from all EquipSet's
		for (Iterator eSet = equipSetList.iterator(); eSet.hasNext();)
		{
			final EquipSet es = (EquipSet) eSet.next();
			final Equipment eqI = es.getItem();

			if ((eqI != null) && eq.equals(eqI))
			{
				tmpList.add(es);
			}
		}

		for (Iterator eSet = tmpList.iterator(); eSet.hasNext();)
		{
			final EquipSet es = (EquipSet) eSet.next();
			delEquipSet(es);
		}
		setDirty(true);
	}

	public void delFollower(final Follower aFollower)
	{
		followerList.remove(aFollower);
		setDirty(true);
	}

	public void equipmentListAddAll(final List aList)
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
		if (!variableList.isEmpty())
		{
			for (Iterator e = variableList.iterator(); e.hasNext();)
			{
				final StringTokenizer aTok = new StringTokenizer((String) e.next(), "|");
				aTok.nextToken(); //src
				aTok.nextToken(); //subSrc

				if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
				{
					return true;
				}
			}
		}

		if (Globals.hasWeaponProfVariableNamed(weaponProfList, variableString))
		{
			return true;
		}

		return variableSet.contains(variableString.toUpperCase());
	}

	/**
	 * Put the provided bonus key and value into the supplied bonus map. Some
	 * sanity checking is done on the key.
	 *
	 * @param aKey The bonus key
	 * @param aVal The value of the bonus
	 * @param bonusMap The map of bonuses bieng built.
	 */
	private void putActiveBonusMap(final String aKey, final String aVal, Map bonusMap)
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
		//setDirty(true);
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
			if (!templateList.isEmpty())
			{
				for (Iterator e = getTemplateList().iterator(); e.hasNext();)
				{
					final PCTemplate template = (PCTemplate) e.next();
					final String templateSize = template.getTemplateSize();

					if (templateSize.length() != 0)
					{
						iSize = Globals.sizeInt(templateSize);
					}
				}
			}
		}

		return iSize;
	}

	/**
	 * @param aBonus This will be used when I expand the functionality of
	 *               the TempBonus tab. Please leave -- JSC 08/08/03
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
	 * begin breakdown of getAC info Arknight 08-09-02
	 * rework by Arknight 01-02-03
	 * rework by fdiniz 08-19-03
	 * Now we use the ACTYPE tag on miscinfo to determine the formula
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

	public void setAlignment(final int index, final boolean bLoading, final boolean bForce)
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
			if ((bLoading) && (index != SettingsHandler.getGame().getIndexOfAlignment(Constants.s_NONE)))
			{
				ShowMessageDelegate.showMessageDialog("Invalid alignment. Setting to <none selected>", Constants.s_APPNAME, MessageType.INFORMATION);
				alignment = SettingsHandler.getGame().getIndexOfAlignment(Constants.s_NONE);
			}

			//TODO raise an exception, once I define one. Maybe
			//ArrayIndexOutOfBounds?
		}
		setDirty(true);
	}

	public String getAttackString(final int index)
	{
		// index: 0 = melee; 1 = ranged; 2 = unarmed
		return getAttackString(index, 0);
	}

	public String getAttackString(final int index, final int bonus)
	{
		return getAttackString(index, bonus, 0);
	}

	/**
	 * Calculates and returns an attack string for one of Melee, Ranged or
	 * Unarmed damage.  This will be returned in attack string format i.e.
	 * +11/+6/+1.  The attack string returned by this function normally
	 * only includes the attacks generated by the characters Base Attack
	 * Bonus.  There are two bonuses to TOHIT that may be applied to the
	 * attack string returned by this function.  The first bonus increases
	 * only the size of the attacks generated.  The second increases both
	 * the size and number of attacks
	 *
	 * @param index The type of attack. Takes one of three values;
	 *              <ul>
	 *              <li> Constants.ATTACKSTRING_MELEE
	 *              <li> Constants.ATTACKSTRING_RANGED
	 *              <li> Constants.ATTACKSTRING_UNARMED
	 *              </ul>
	 * @param TOHITBonus A bonus that will be added to the TOHIT numbers.  This
	 *              bonus affects only the numbers produced, not the number
	 *              of attacks

	 * @param BABBonus This bonus will be added to BAB before the number of
	 *              attacks has been determined.
	 * @return The attack string for this character
	 */

	public String getAttackString(final int index, final int TOHITBonus, int BABBonus)
	{
		final String cacheLookup = "AttackString:" + index      + ","
											   + TOHITBonus + ","
							   + BABBonus;
		final String cached = getVariableProcessor().getCachedString(cacheLookup);

		if (cached != null)
		{
			return cached;
		}

		// index: 0 = melee; 1 = ranged; 2 = unarmed
		// now we see if this PC is a Familiar
		// initialize to some large negative number
		int masterBAB = -9999;
		int masterTotal = -9999;
		final PlayerCharacter nPC = getMasterPC();

		final int totalClassLevels = getTotalCharacterLevel();
		Map totalLvlMap = null;
		final Map classLvlMap;

		if (totalClassLevels > SettingsHandler.getGame().getBabMaxLvl())
		{
			totalLvlMap = getTotalLevelHashMap();
			classLvlMap = getCharacterLevelHashMap(SettingsHandler.getGame().getBabMaxLvl());

			// insure class-levels total is below some value (20)
			getVariableProcessor().pauseCache();
			setClassLevelsBrazenlyTo(classLvlMap);
		}

		if ((nPC != null) && (getCopyMasterBAB().length() > 0))
		{
			masterBAB = nPC.baseAttackBonus();

			final String copyMasterBAB = replaceMasterString(getCopyMasterBAB(), masterBAB);
			masterBAB = getVariableValue(copyMasterBAB, "").intValue();
			masterTotal = masterBAB + TOHITBonus;
		}

		final int BAB = baseAttackBonus();

		int attackCycle = 1;
		int workingBAB  = BAB + TOHITBonus;
		int subTotal    = BAB;
		int raceBAB     = getRace().getBAB(this);

		final List ab = new ArrayList(10);
		final StringBuffer attackString = new StringBuffer();

		// Assume a max of 10 attack cycles
		for (int total = 0; total < 10; ++total)
		{
			ab.add(new Integer(0));
		}

		// Some classes (like the Monk or Ranged Sniper) use
		// a different attack cycle than the standard classes
		// So compute the base attack for this type (BAB, RAB, UAB)
		for (int i = 0; i < classList.size(); ++i)
		{
			final PCClass aClass = (PCClass) classList.get(i);

			// Get the attack bonus
			final int b = aClass.baseAttackBonus(this);

			// Get the attack cycle
			final int c = aClass.attackCycle(index);

			// add to all other classes
			final int d = ((Integer) ab.get(c)).intValue() + b;

			// set new value for iteration
			ab.set(c, new Integer(d));

			if (c != 3)
			{
				raceBAB += b;
			}
		}

		// Iterate through all the possible attack cycle values
		// and find the one with the highest attack value
		for (int i = 2; i < 10; ++i)
		{
			final int newAttack = ((Integer) ab.get(i)).intValue();
			final int oldAttack = ((Integer) ab.get(attackCycle)).intValue();

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
		int attackTotal = ((Integer) ab.get(attackCycle)).intValue();

		// Default cut-off before multiple attacks (e.g. 5)
		final int defaultAttackCycle = SettingsHandler.getGame().getBabAttCyc();

		if (attackTotal == 0)
		{
			attackCycle = defaultAttackCycle;
		}

		// FAMILIAR: check to see if the masters BAB is better
		workingBAB = Math.max(workingBAB, masterTotal);
		subTotal   = Math.max(subTotal,   masterBAB);
		raceBAB    = Math.max(raceBAB,    masterBAB);

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
				subTotal   -= raceBAB;
			}
		}

		int maxAttacks = SettingsHandler.getGame().getBabMaxAtt();
		final int minMultiBab = SettingsHandler.getGame().getBabMinVal();

		// If there is a bonus to BAB, it needs to be added to ALL of
		// the variables used to determine the number of attacks
		attackTotal += BABBonus;
		workingBAB  += BABBonus;
		subTotal    += BABBonus;

		do
		{
			if (attackString.length() > 0)
			{
				attackString.append('/');
			}

			attackString.append(Delta.toString(workingBAB));
			workingBAB  -= attackCycle;
			attackTotal -= attackCycle;
			subTotal    -= attackCycle;
			maxAttacks--;
		}
		while (((attackTotal >= minMultiBab) || (subTotal >= minMultiBab)) && (maxAttacks > 0));

		getVariableProcessor().addCachedString(cacheLookup, attackString.toString());
		return attackString.toString();
	}


	public SortedSet getAutoLanguages()
	{
		// find list of all possible langauges
		boolean clearRacials = false;

		final SortedSet autoLangs = new TreeSet();

		// Search for a CLEAR in the list and
		// if found clear all BEFORE but not AFTER it.
		// ---arcady June 1, 2002
		for (Iterator e = templateAutoLanguages.iterator(); e.hasNext();)
		{
			final Language aLang = (Language) e.next();
			final String aString = aLang.toString();

			if (".CLEARRACIAL".equals(aString))
			{
				clearRacials = true;
				getLanguagesList().removeAll(getRace().getSafeListFor(ListKey.AUTO_LANGUAGES));
			}
			else if (".CLEARALL".equals(aString) || ".CLEAR".equals(aString))
			{
				clearRacials = true;
				autoLangs.clear();
				getLanguagesList().clear();
			}
			else if (".CLEARTEMPLATES".equals(aString))
			{
				autoLangs.clear();
				getLanguagesList().removeAll(templateAutoLanguages);
			}
			else
			{
				autoLangs.add(aLang);
			}
		}

		if (!clearRacials)
		{
			autoLangs.addAll(getRace().getSafeListFor(ListKey.AUTO_LANGUAGES));
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			autoLangs.addAll(aClass.getSafeListFor(ListKey.AUTO_LANGUAGES));
		}

		if (deity != null)
		{
			autoLangs.addAll(deity.getSafeListFor(ListKey.AUTO_LANGUAGES));
		}

		Iterator it = getRealFeatsIterator();
		while (it.hasNext())
		{
			final Ability aFeat = (Ability) it.next();
			autoLangs.addAll(aFeat.getSafeListFor(ListKey.AUTO_LANGUAGES));
		}

		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			autoLangs.addAll(aSkill.getSafeListFor(ListKey.AUTO_LANGUAGES));
		}

		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();

			if (aCD.getDomain() != null)
			{
				autoLangs.addAll(aCD.getDomain().getSafeListFor(ListKey.AUTO_LANGUAGES));
			}
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				autoLangs.addAll(eq.getSafeListFor(ListKey.AUTO_LANGUAGES));

				List aList = eq.getEqModifierList(true);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						autoLangs.addAll(eqMod.getSafeListFor(ListKey.AUTO_LANGUAGES));
					}
				}

				aList = eq.getEqModifierList(false);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						autoLangs.addAll(eqMod.getSafeListFor(ListKey.AUTO_LANGUAGES));
					}
				}
			}
		}

		if (getKitInfo() != null)
		{
			for (Iterator e = getKitInfo().iterator(); e.hasNext();)
			{
				final Kit kit = (Kit) e.next();
				autoLangs.addAll(kit.getSafeListFor(ListKey.AUTO_LANGUAGES));
			}
		}

		getLanguagesList().addAll(autoLangs);

		return autoLangs;
	}

	/**
	 * Sets the autoSortGear.
	 *
	 * @param autoSortGear The autoSortGear to set
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
	 * type 0 = attack bonus; 1 = check1; 2 = check2; 3 = check3; etc, last one is = Unarmed
	 * @param type
	 * @param addBonuses
	 * @return bonus
	 */
	public double getBonus(final int type, final boolean addBonuses)
	{
		final String cacheLookup = "getBonus:" + type + "," + addBonuses;
		final Float total = getVariableProcessor().getCachedVariable(cacheLookup);

		if (total != null)
		{
			return total.doubleValue();
		}

		double bonus = 0;
		final int totalClassLevels;
		Map totalLvlMap = null;
		final Map classLvlMap;

		if (type == 0)
		{
			bonus = race.getBAB(this);
		}
		else if (type <= SettingsHandler.getGame().getUnmodifiableCheckList().size())
		{
			totalClassLevels = getTotalCharacterLevel();
			if (totalClassLevels > SettingsHandler.getGame().getChecksMaxLvl())
			{
				totalLvlMap = getTotalLevelHashMap();
				classLvlMap = getCharacterLevelHashMap(SettingsHandler.getGame().getChecksMaxLvl());
				getVariableProcessor().pauseCache();
				setClassLevelsBrazenlyTo(classLvlMap); // insure class-levels total is below some value (e.g. 20)
			}

			bonus = getTotalBonusTo("CHECKS",
					"BASE." + SettingsHandler.getGame().getUnmodifiableCheckList().get(type - 1).toString());

			//
			// now we see if this PC is a Familiar/Mount
			final PlayerCharacter nPC = getMasterPC();

			if ((nPC != null) && (getCopyMasterCheck().length() > 0))
			{
				int masterBonus;
				final PlayerCharacter curPC = this;
				Globals.setCurrentPC(nPC);

				// calculate the Masters Save Bonus
				masterBonus = nPC.calculateSaveBonus(type,
						SettingsHandler.getGame().getUnmodifiableCheckList().get(type - 1).toString(), "BASE");
				Globals.setCurrentPC(curPC);

				final String copyMasterCheck = replaceMasterString(getCopyMasterCheck(), masterBonus);
				masterBonus = getVariableValue(copyMasterCheck, "").intValue();

				// use masters save if better
				bonus = Math.max(bonus, masterBonus);
			}

			if (totalLvlMap != null)
			{
				setClassLevelsBrazenlyTo(totalLvlMap);
				getVariableProcessor().restartCache();
			}
		}

		if (addBonuses)
		{
			if (type == 0)
			{
				bonus += getTotalBonusTo("TOHIT", "TOHIT");
				bonus += getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
			}
			else if (type <= SettingsHandler.getGame().getUnmodifiableCheckList().size())
			{
				bonus += getTotalBonusTo("CHECKS", SettingsHandler.getGame().getUnmodifiableCheckList().get(type - 1).toString());
			}
			else
			{
				bonus += getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
			}
		}

		int cBonus = 0;

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			if ((type == 0) || (type > SettingsHandler.getGame().getUnmodifiableCheckList().size()))
			{
				cBonus += aClass.baseAttackBonus(this);
			}
		}

		bonus += cBonus;

		getVariableProcessor().addCachedVariable(cacheLookup, new Float(bonus));
		return bonus;
	}

	/**
	 * return bonus total for a specific bonusType
	 * e.g: getBonusDueToType("COMBAT","AC","Armor") to get armor bonuses
	 * @param mainType
	 * @param subType
	 * @param bonusType
	 * @return bonus due to type
	 */
	public double getBonusDueToType(final String mainType, final String subType, final String bonusType)
	{
		final String typeString = mainType + "." + subType + ":" + bonusType;

		return sumActiveBonusMap(typeString);
	}

	/**
	 * If the class passed in has the Levels
	 * @param newLevelClass The class the new level has been taken in.
	 * @return bonus feats for new level
	 */
	public double getBonusFeatsForNewLevel(final PCClass newLevelClass)
	{
		double bonusFeats = 0.0;
		final Integer lpf = newLevelClass.getLevelsPerFeat();

		if (lpf!=null && lpf.intValue()>=0)
		{
			// If the class has levelsPerFeat set then the calculated
			// level for determining bonus feats = RacialHD+levels in this class
			int calculatedLevel=0;
			if (getRace()!=null && isMonsterDefault() )
			{
				// If we are a default monster then we will need to add the MonsterClassLevels
				// if we are not a default monster then these will be explicit class levels
				// and we do not want to add them twice.
				calculatedLevel += getRace().getMonsterClassLevels(this);
			}

			calculatedLevel += newLevelClass.getLevel();

			final int levelsPerFeat = lpf.intValue();
			if (levelsPerFeat>0)
			{
				bonusFeats = (calculatedLevel%levelsPerFeat==0) ? 1 : 0;
			}
		}
		else
		{
			// If the class does not have levelsPerFeat set then the calculated level
			// for determining bonus feats is RacialHD + sum of levels in all classes
			// that do not have levelsPerFeat set
			int nonSpecificLevels=0;
			if (getRace()!=null && isMonsterDefault() )
			{
				nonSpecificLevels += getRace().getMonsterClassLevels(this);
			}

			for (Iterator iter = classList.iterator(); iter.hasNext();)
			{
				final PCClass characterClass = (PCClass) iter.next();
				final Integer levelsPerFeatForClass = characterClass.getLevelsPerFeat();
				if (levelsPerFeatForClass==null || levelsPerFeatForClass.intValue()<0)
				{
					nonSpecificLevels += characterClass.getLevel();
				}
			}
			bonusFeats = Globals.getBonusFeatsForLevel(nonSpecificLevels);
		}
		return bonusFeats;
	}

	/**
	 * Get the list of WeaponName and Proficiency types
	 * from the changeProfMap of each granting object
	 * @return List
	 */
	public List getChangeProfList()
	{
		final List aList = new ArrayList();

		if (getRace() != null)
		{
			aList.addAll(getRace().getChangeProfList(this));
		}

		if (getDeity() != null)
		{
			aList.addAll(getDeity().getChangeProfList(this));
		}

		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			aList.addAll(aTemplate.getChangeProfList(this));
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			aList.addAll(aClass.getChangeProfList(this));
		}

		for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
		{
			final Ability aFeat = (Ability) e.next();
			aList.addAll(aFeat.getChangeProfList(this));
		}

		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill) e.next();
			aList.addAll(aSkill.getChangeProfList(this));
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				aList.addAll(eq.getChangeProfList(this));

				final List eqmList = eq.getEqModifierList(true);

				if (!eqmList.isEmpty())
				{
					for (Iterator e2 = eqmList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						aList.addAll(eqMod.getChangeProfList(this));
					}
				}
			}
		}

		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) e.next();
			final Domain aDomain = aCD.getDomain();

			if (aDomain != null)
			{
				aList.addAll(aDomain.getChangeProfList(this));
			}
		}

		// All done
		return aList;
	}

	public CharacterDomain getCharacterDomainForDomain(final String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();

			if ((aDomain != null) && aDomain.getName().equalsIgnoreCase(domainName))
			{
				return aCD;
			}
		}

		return null;
	}

	/**
	 * @return characterDomainList
	 */
	public List getCharacterDomainList()
	{
		return characterDomainList;
	}

	public Domain getCharacterDomainNamed(final String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();

			if ((aDomain != null) && aDomain.getName().equalsIgnoreCase(domainName))
			{
				return aCD.getDomain();
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

	public List getCompanionModList()
	{
		return companionModList;
	}

	public String getCopyMasterBAB()
	{
		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) e.next();

			if (cMod.getType().equalsIgnoreCase(getMaster().getType()))
			{
				if (cMod.getCopyMasterBAB() != null)
				{
					return cMod.getCopyMasterBAB();
				}
			}
		}

		return "";
	}

	public String getCopyMasterCheck()
	{
		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) e.next();

			if (cMod.getType().equalsIgnoreCase(getMaster().getType()))
			{
				if (cMod.getCopyMasterCheck() != null)
				{
					return cMod.getCopyMasterCheck();
				}
			}
		}

		return "";
	}

	public String getCopyMasterHP()
	{
		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) e.next();

			if (cMod.getType().equalsIgnoreCase(getMaster().getType()))
			{
				if (cMod.getCopyMasterHP() != null)
				{
					return cMod.getCopyMasterHP();
				}
			}
		}

		return "";
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
	 * @param className
	 * @return domain source
	 */
	public String getDomainSource(final String className)
	{
		for (Iterator i = domainSourceMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			final String aVal = domainSourceMap.get(aKey).toString();
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

		return "";
	}

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
	 * @param i The new output order
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
	 * Retrieves an unsorted list of the character's equipment matching
	 * the supplied type and status criteria.
	 *
	 * @param typeName The type of equipment to be selected
	 * @param status   The required status: 1 (equipped) 2 (not equipped) 3 (don't care)
	 * @return An ArrayList of the matching equipment objects.
	 */
	public List getEquipmentOfType(final String typeName, final int status)
	{
		return getEquipmentOfType(typeName, "", status);
	}

	/**
	 * Retrieves an unsorted list of the character's equipment matching
	 * the supplied type, sub type and status criteria.
	 *
	 * @param typeName    The type of equipment to be selected
	 * @param subtypeName The subtype of equipment to be selected (empty string for no subtype)
	 * @param status      The required status: 1 (equipped) 2 (not equipped) 3 (don't care)
	 * @return An ArrayList of the matching equipment objects.
	 */
	public List getEquipmentOfType(final String typeName, final String subtypeName, final int status)
	{
		final List aArrayList = new ArrayList();

		if (equipmentList.isEmpty())
		{
			return aArrayList;
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.typeStringContains(typeName) && ("".equals(subtypeName) || eq.typeStringContains(subtypeName))
				&& ((status == 3) || ((status == 2) && !eq.isEquipped()) || ((status == 1) && eq.isEquipped())))
			{
				aArrayList.add(eq);
			}
		}

		return aArrayList;
	}

	/**
	 * Retrieves a list, sorted in output order, of the character's equipment
	 * matching the supplied type and status criteria. This list is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name.
	 * Note hidden items (outputIndex = -1) are not included in this list.
	 *
	 * @param typeName The type of equipment to be selected
	 * @param status   The required status: 1 (equipped) 2 (not equipped) 3 (don't care)
	 * @return An ArrayList of the matching equipment objects in output order.
	 */
	public List getEquipmentOfTypeInOutputOrder(final String typeName, final int status)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, status), Constants.MERGE_ALL);
	}

	/**
	 * @param typeName The type of equipment to be selected
	 * @param status   The required status
	 * @param merge    What type of merge for like equipment
	 * @return An ArrayList of equipment objects
	 */
	public List getEquipmentOfTypeInOutputOrder(final String typeName, final int status, final int merge)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, status), merge);
	}

	/**
	 * @param typeName    The type of equipment to be selected
	 * @param subtypeName The subtype of equipment to be selected
	 * @param status      The required status
	 * @param merge       What sort of merging should occur
	 * @return An ArrayList of equipment objects
	 */
	public List getEquipmentOfTypeInOutputOrder(final String typeName, final String subtypeName, final int status, final int merge)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, subtypeName, status), Constants.MERGE_ALL);
	}

	/**
	 * Retrieve the expanded list of weapons
	 * Expanded weapons include: double weapons and melee+ranged weapons
	 * Output order is assumed
	 * Merge of like equipment depends on the passed in int
	 * @param merge
	 *
	 * @return the sorted list of weapons.
	 */
	public List getExpandedWeapons(final int merge)
	{
		final List weapList = sortEquipmentList(getEquipmentOfType("Weapon", 3), merge);

		//
		// If any weapon is both Melee and Ranged, then make 2 weapons
		// for list, one Melee only, the other Ranged and Thrown.
		// For double weapons, if wielded in two hands show attacks
		// for both heads, head 1 and head 2 else
		// if wielded in 1 hand, just show damage by head
		//
		for (int idx = 0; idx < weapList.size(); ++idx)
		{
			final Equipment equip = (Equipment) weapList.get(idx);

			if (equip.isDouble() && (equip.getLocation() == Equipment.EQUIPPED_TWO_HANDS))
			{
				Equipment eqm = (Equipment) equip.clone();
				eqm.removeType("Double");
				eqm.setTypeInfo("Head1");

				// Add "Head 1 only" to the name of the weapon
				eqm.setWholeItemName(eqm.getName());
				eqm.setName(PlayerCharacterUtilities.appendToName(eqm.getName(), "Head 1 only"));

				if (eqm.getOutputName().indexOf("Head 1 only") < 0)
				{
					eqm.setOutputName(
							PlayerCharacterUtilities.appendToName(eqm.getOutputName(), "Head 1 only"));
				}

				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.add(idx + 1, eqm);

				eqm = (Equipment) equip.clone();

				final String altType = eqm.getType(false);

				if (altType.length() != 0)
				{
					eqm.setTypeInfo(".CLEAR." + altType);
				}

				eqm.removeType("Double");
				eqm.setTypeInfo("Head2");
				eqm.setDamage(eqm.getAltDamage(this));
				eqm.setCritMult(eqm.getAltCritMult());
				eqm.setCritRange(Integer.toString(eqm.getRawCritRange(false)));
				eqm.getEqModifierList(true).clear();
				eqm.getEqModifierList(true).addAll(eqm.getEqModifierList(false));

				// Add "Head 2 only" to the name of the weapon
				eqm.setWholeItemName(eqm.getName());
				eqm.setName(PlayerCharacterUtilities.appendToName(eqm.getName(), "Head 2 only"));

				if (eqm.getOutputName().indexOf("Head 2 only") < 0)
				{
					eqm.setOutputName(PlayerCharacterUtilities.appendToName(eqm.getOutputName(), "Head 2 only"));
				}

				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.add(idx + 2, eqm);
			}

			//
			// Leave else here, as otherwise will show attacks
			// for both heads for thrown double weapons when
			// it should only show one
			//
			else if (equip.isMelee() && equip.isRanged() && (equip.getRange(this).intValue() != 0))
			{
				//
				// Strip off the Ranged portion, set range to 0
				//
				Equipment eqm = (Equipment) equip.clone();
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
					final Equipment teq = (Equipment) getPrimaryWeapons().get(iPrimary);

					if (teq.equalTo(equip))
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
					final Equipment teq = (Equipment) getSecondaryWeapons().get(iSecondary);

					if (teq.equalTo(equip))
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
				eqm = (Equipment) equip.clone();
				eqm.setTypeInfo("Ranged.Thrown.Both");
				eqm.removeType("Melee");

				// Add "Thrown" to the name of the weapon
				eqm.setName(PlayerCharacterUtilities.appendToName(eqm.getName(), "Thrown"));

				if (eqm.getOutputName().indexOf("Thrown") < 0)
				{
					eqm.setOutputName(PlayerCharacterUtilities.appendToName(eqm.getOutputName(), "Thrown"));
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

	/*
	 * renaming to standard convetion
	 * due to refactoring of export
	 *
	 * Build on-the-fly so removing templates doesn't mess up favored list
	 *
	 * author: Thomas Behr 08-03-02
	 */
	public SortedSet getFavoredClasses()
	{
		final SortedSet favored = new TreeSet(favoredClasses);

		for (int i = 0; i < templateList.size(); ++i)
		{
			final PCTemplate template = (PCTemplate) templateList.get(i);
			final String favoredClass = template.getFavoredClass();

			if ((favoredClass.length() != 0) && !favored.contains(favoredClass))
			{
				favored.add(favoredClass);
			}
		}

		return favored;
	}

	public Ability getFeatAutomaticNamed(final String featName)
	{
		return AbilityUtilities.getFeatNamedInList(featAutoList(), featName);
	}

	/**
	 * Calculates total bonus from Feats
	 * @param aType
	 * @param aName
	 * @param subSearch
	 * @return feat bonus to
	 */
	public double getFeatBonusTo(String aType, String aName, final boolean subSearch)
	{
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		return getPObjectWithCostBonusTo(aggregateFeatList(), aType, aName, subSearch);
	}

	/**
	 * Returns the Feat definition of a feat possessed by the character.
	 *
	 * @param featName String name of the feat to check for.
	 * @return the Feat (not the CharacterFeat) searched for,
	 *         <code>null</code> if not found.
	 */
	public Ability getFeatNamed(final String featName)
	{
		return AbilityUtilities.getFeatNamedInList(aggregateFeatList(), featName);
	}

	public Ability getFeatNamed(final String featName, final int featType)
	{
		return AbilityUtilities.getFeatNamedInList(aggregateFeatList(), featName, featType);
	}

	/**
	 * Searches the characters feats for an Ability object which is a clone of the
	 * same Base ability as the Ability passed in
	 * TODO Update this for categories when PlayerCharacter supports them properly
	 *
	 * @param anAbility
	 * @return the Ability if found, otherwise null
	 */
	public Ability getAbilityMatching(final Ability anAbility)
	{
		return AbilityUtilities.getMatchingFeatInList(aggregateFeatList(), anAbility);
	}

	/**
	 * Returns the Feat definition searching by key (not name), as
	 * found in the <b>aggregate</b> feat list.
	 *
	 * @param featName String key of the feat to check for.
	 * @return the Feat (not the CharacterFeat) searched for,
	 *         <code>null</code> if not found.
	 */
	public Ability getFeatKeyed(final String featName)
	{
		return getFeatKeyed(featName, aggregateFeatList());
	}

	public int getFirstSpellLevel(final Spell aSpell)
	{
		int anInt = 0;

		for (Iterator iClass = getClassList().iterator(); iClass.hasNext();)
		{
			final PCClass aClass = (PCClass) iClass.next();
			final String aKey = aClass.getSpellKey();
			final int temp = aSpell.getFirstLevelForKey(aKey, this);
			anInt = Math.min(anInt, temp);
		}

		return anInt;
	}

	public void setHasMadeKitSelectionForAgeSet(final int index, final boolean arg)
	{
		if ((index >= 0) && (index < 10))
		{
			ageSetKitSelections[index] = arg;
		}
		setDirty(true);
	}

	public List getKitInfo()
	{
		return kitList;
	}

	public int getLevelAdjustment(final PlayerCharacter aPC)
	{
		int levelAdj = race.getLevelAdjustment(aPC);

		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aT = (PCTemplate) e.next();
			levelAdj += aT.getLevelAdjustment(aPC);
		}

		return levelAdj;
	}

	public List getLevelInfo()
	{
		return pcLevelInfo;
	}

	public String getLevelInfoClassKeyName(final int idx)
	{
		if ((idx >= 0) && (idx < getLevelInfoSize()))
		{
			return ((PCLevelInfo) pcLevelInfo.get(idx)).getClassKeyName();
		}

		return "";
	}

	public int getLevelInfoClassLevel(final int idx)
	{
		if ((idx >= 0) && (idx < getLevelInfoSize()))
		{
			return ((PCLevelInfo) pcLevelInfo.get(idx)).getLevel();
		}

		return 0;
	}

	public PCLevelInfo getLevelInfoFor(final String className, int level)
	{
		for (Iterator i = pcLevelInfo.iterator(); i.hasNext();)
		{
			final PCLevelInfo pcl = (PCLevelInfo) i.next();

			if (pcl.getClassKeyName().equals(className))
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
	 * @param src
	 * @param aPC
	 * @return the number of Character Domains possible
	 * and check the level of the src class if the result is 0.
	 */
	public int getMaxCharacterDomains(final PCClass src, final PlayerCharacter aPC)
	{
		int i = getMaxCharacterDomains();
		if (i == 0 && domainSourceMap.size()==0)
			i = (int) src.getBonusTo("DOMAIN", "NUMBER", src.getLevel(), aPC);
		return i;
	}

	/**
	 * Calculate the maximum number of ranks the character is allowed to have
	 * in the specified skill.
	 *
	 * @param skillName The name of the skill being checked.
	 * @param aClass    The name of the current class in which points are being spent
	 *                  - only used to check cross-class skill cost.
	 * @return max rank
	 */
	public Float getMaxRank(final String skillName, final PCClass aClass)
	{
		int levelForSkillPurposes = getTotalLevels();
		final BigDecimal maxRanks;

		if (SettingsHandler.isMonsterDefault())
		{
			levelForSkillPurposes += totalHitDice();
		}

		final Skill aSkill = Globals.getSkillNamed(skillName);

		if (aSkill.isExclusive())
		{
			// Exclusive skills only count levels in classes which give access to the skill
			levelForSkillPurposes = 0;

			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				final PCClass bClass = (PCClass) e.next();

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
				// No classes qualify for this exclusive skill, so treat it as a cross-class skill
				// This does not seem right to me! JD
				if (SettingsHandler.isMonsterDefault())
				{
					levelForSkillPurposes = (getTotalLevels() + totalHitDice());
				}
				else
				{
					levelForSkillPurposes = (getTotalLevels());
				}

				maxRanks = SkillUtilities.maxCrossClassSkillForLevel(levelForSkillPurposes,this);
			}
			else
			{
				maxRanks = SkillUtilities.maxClassSkillForLevel(levelForSkillPurposes, this);
			}
		}
		else if (!aSkill.isClassSkill(classList, this) && (aSkill.costForPCClass(aClass, this) == Globals.getGameModeSkillCost_Class()))
		{
			// Cross class skill - but as cost is 1 only return a whole number
			maxRanks = new BigDecimal(SkillUtilities.maxCrossClassSkillForLevel(levelForSkillPurposes,this).intValue()); // This was (int) (i/2.0) previously
		}
		else if (!aSkill.isClassSkill(classList, this))
		{
			// Cross class skill
			maxRanks = SkillUtilities.maxCrossClassSkillForLevel(levelForSkillPurposes,this);
		}
		else
		{
			// Class skill
			maxRanks = SkillUtilities.maxClassSkillForLevel(levelForSkillPurposes, this);
		}

		return new Float(maxRanks.floatValue());
	}

	/**
	 * @param moveIdx
	 * @return the integer movement speed for Idx
	 */
	public Double getMovement(final int moveIdx)
	{
		if ((getMovements() != null) && (moveIdx < movements.length))
		{
			return movements[moveIdx];
		}
		return new Double(0);
	}

	public String getMovementType(final int moveIdx)
	{
		if ((movementTypes != null) && (moveIdx < movementTypes.length))
		{
			return movementTypes[moveIdx];
		}
		return "";
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
			final StringTokenizer aTok = new StringTokenizer(sDom, "|");
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

		for (int x = 0; x < templateList.size(); ++x)
		{
			if (((PCTemplate) templateList.get(x)).isNonAbility(i))
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
		final int div = getVariableValue("OFFHANDLIGHTBONUS", "").intValue();

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

		for (Iterator e = primaryWeapons.iterator(); e.hasNext();)
		{
			final Equipment eqI = (Equipment) e.next();

			if (eqI.getName().equalsIgnoreCase(eq.getName()) && (eqI.getLocation() == eq.getLocation()))
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
			return isProficientWithShield(eq);
		}
		else if (eq.isArmor())
		{
			return isProficientWithArmor(eq);
		}
		else if (eq.isWeapon())
		{
			return isProficientWithWeapon(eq);
		}

		return false;
	}

	/**
	 * Changes the race of the character. First it removes the
	 * current Race, and any bonus attributes (e.g. feats), then
	 * add the new Race.
	 * @param aRace
	 */
	public void setRace(final Race aRace)
	{
		final Race oldRace = getRace();
		final boolean raceIsNull = (oldRace == null); // needed because race is nulled later
		final boolean firstLevel = getTotalClassLevels()==1;

		// remove current race attributes
		if (!raceIsNull)
		{
			if (firstLevel)
			{
//				setFeats(feats - oldRace.getBonusInitialFeats());
				adjustFeats(-oldRace.getBonusInitialFeats());
			}
			oldRace.getSpellSupport().clearCharacterSpells();

			if (PlayerCharacterUtilities.canReassignRacialFeats())
			{
				final StringTokenizer aTok = new StringTokenizer(oldRace.getFeatList(this), "|");

				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();

					if (aString.endsWith(")") && (Globals.getAbilityNamed("FEAT", aString) == null))
					{
						final String featName = aString.substring(0, aString.indexOf('(') - 1);

						final Ability anAbility = Globals.getAbilityNamed("FEAT", featName);

						if (anAbility != null)
						{
							AbilityUtilities.modFeat(this, null, aString, true, false);
//							setFeats(feats - anAbility.getCost(this));
							adjustFeats(-anAbility.getCost(this));
						}
					}
					else
					{
						final Ability anAbility = Globals.getAbilityNamed("FEAT", aString);

						if (anAbility != null)
						{
							final String featName = anAbility.getName();

							if ((hasRealFeatNamed(featName) || hasFeatAutomatic(featName)))
							{
								AbilityUtilities.modFeat(this, null, aString, true, false);
//								setFeats(feats - anAbility.getCost(this));
								adjustFeats(-anAbility.getCost(this));
							}
						}
						else
						{
							ShowMessageDelegate.showMessageDialog("Removing unknown feat: " + aString, Constants.s_APPNAME, MessageType.INFORMATION);
						}
					}
				}
			}

			getLanguagesList().removeAll(oldRace.getSafeListFor(ListKey.AUTO_LANGUAGES));

			if (oldRace.getWeaponProfAutos() != null)
			{
				weaponProfList.removeAll(oldRace.getWeaponProfAutos());
			}

			if (stringChar.hasCharacteristic(StringKey.RACIAL_FAVORED_CLASS))
			{
				favoredClasses.remove(stringChar.getCharacteristic(StringKey.RACIAL_FAVORED_CLASS));
			}

			removeNaturalWeapons(race);

			for (int x = 0; x < race.templatesAdded().size(); ++x)
			{
				removeTemplate(getTemplateNamed((String) race.templatesAdded().get(x)));
			}

			if ((race.getMonsterClass(this) != null) && (race.getMonsterClassLevels(this) != 0))
			{
				final PCClass mclass = Globals.getClassNamed(race.getMonsterClass(this));

				if (mclass != null)
				{
					incrementClassLevel(race.getMonsterClassLevels(this) * -1, mclass, true);
				}
			}
		}

		// add new race attributes
		race = null;

		if (aRace != null)
		{
			race = (Race) aRace.clone();
		}

		if (race != null)
		{
			race.activateBonuses(this);

			if (!isImporting())
			{
				Globals.getBioSet().randomize("AGE.HT.WT", this);
				if (firstLevel)
				{
//					setFeats(feats + race.getBonusInitialFeats());
					adjustFeats(race.getBonusInitialFeats());
				}
			}

			// Get existing classes
			final List existingClasses = new ArrayList(classList);
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

			final List existingLevelInfo = new ArrayList(pcLevelInfo);
			pcLevelInfo.clear();

			// Make sure monster classes are added first
			if (!isImporting() && (race.getMonsterClass(this) != null) && (race.getMonsterClassLevels(this) != 0))
			{
				final PCClass mclass = Globals.getClassNamed(race.getMonsterClass(this));

				if (mclass != null)
				{
					incrementClassLevel(race.getMonsterClassLevels(this), mclass, true);
				}
			}

			pcLevelInfo.addAll(existingLevelInfo);

			//
			// If user has chosen a class before choosing a race,
			// we need to tweak the number of skill points and feats
			//
			if (!isImporting() && existingClasses.size()!=0)
			{
//				setFeats(feats + race.getBonusInitialFeats());
				adjustFeats(race.getBonusInitialFeats());

				int totalLevels = this.getTotalLevels();
//				final Integer zero = new Integer(0);

				for (int i = 0; i < existingClasses.size(); ++i)
				{
					final PCClass aClass = (PCClass) existingClasses.get(i);

					//
					// Don't add monster classes back in. This will possibly mess up feats earned by level
					// ?Possibly convert to mclass if not null?
					//
					if (!aClass.isMonster())
					{
						classList.add(aClass);

						final int cLevels = aClass.getLevel();

						//aClass.setLevel(0);
						aClass.setSkillPool(0);

						int cMod = 0;

						for (int j = 0; j < cLevels; ++j)
						{
							cMod += aClass.recalcSkillPointMod(this, ++totalLevels);
						}

						aClass.setSkillPool(cMod);
					}
				}
			}

			addNaturalWeapons(race.getNaturalWeapons());

			if (PlayerCharacterUtilities.canReassignRacialFeats())
			{
				final StringTokenizer aTok = new StringTokenizer(getRace().getFeatList(this), "|");

				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();

					if (aString.endsWith(")") && (Globals.getAbilityNamed("FEAT", aString) == null))
					{
						// we want the first instance of it, in case of Weapon Focus(Longbow (Composite))
						final String featName = aString.substring(0, aString.indexOf('(') - 1);

						final Ability anAbility = Globals.getAbilityNamed("FEAT", featName);

						if (anAbility != null)
						{
//							setFeats(feats + anAbility.getCost(this));
							adjustFeats(anAbility.getCost(this));
							AbilityUtilities.modFeat(this, null, aString, true, true);
						}
					}
					else
					{
						final Ability anAbility = Globals.getAbilityNamed("FEAT", aString);

						if (anAbility != null)
						{
							final String featName = anAbility.getName();

							if ((!this.hasRealFeatNamed(featName) && !this.hasFeatAutomatic(featName)))
							{
//								setFeats(feats + anAbility.getCost(this));
								adjustFeats(anAbility.getCost(this));

								//modFeat(featName, true, featName.endsWith("Proficiency"));
								AbilityUtilities.modFeat(this, null, aString, true, true);
							}
						}
						else
						{
							ShowMessageDelegate.showMessageDialog("Adding unknown feat: " + aString, Constants.s_APPNAME, MessageType.INFORMATION);
						}
					}
				}
			}

			getAutoLanguages();
			getRacialFavoredClasses();

			race.getTemplates(isImporting(), this); // gets and adds templates

			race.chooseLanguageAutos(isImporting(), this);
		}

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

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		final List tempList = getRace().getBonusListOfType(aType, aName);

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

		for (Iterator e = secondaryWeapons.iterator(); e.hasNext();)
		{
			final Equipment eqI = (Equipment) e.next();

			if (eqI.getName().equalsIgnoreCase(eq.getName()) && (eqI.getLocation() == eq.getLocation()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Calculates total bonus from Size adjustments
	 * @param aType
	 * @param aName
	 * @return size adjustment bonus to
	 */
	public double getSizeAdjustmentBonusTo(String aType, String aName)
	{
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		return getBonusDueToType(aType, aName, "SIZE");
	}

	public Skill getSkillKeyed(final String skillName)
	{
		if (getSkillList().isEmpty())
		{
			return null;
		}

		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill) e.next();

			if (aSkill.getKeyName().equalsIgnoreCase(skillName))
			{
				return aSkill;
			}
		}

		return null;
	}

	public Skill getSkillNamed(final String skillName)
	{
		if (getSkillList().isEmpty())
		{
			return null;
		}

		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill) e.next();

			if (aSkill.getName().equalsIgnoreCase(skillName))
			{
				return aSkill;
			}
		}

		return null;
	}

	/**
	 * Set the order in which skills should be sorted for output.
	 * @param i The new output order
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
	 * @param minLevel The desired caster level
	 * @param sumOfLevels True if all of the character caster levels should be
	 * added together before the comparison.
	 * @return boolean
	 */
	public boolean isSpellCaster(final int minLevel, final boolean sumOfLevels)
	{
		int runningTotal=0;

		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass) e1.next();
			if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE))
			{
				int classLevels = (int) getTotalBonusTo("CASTERLEVEL", aClass.getName());
				if ((classLevels == 0) && (canCastSpellTypeLevel(aClass.getSpellType(), 0, 1) || canCastSpellTypeLevel(aClass.getSpellType(), 1, 1)))
				{
					// missing CASTERLEVEL hack
					classLevels = aClass.getLevel();
				}
				classLevels +=  (int) getTotalBonusTo("PCLEVEL", aClass.getName());
				if (sumOfLevels)
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

		if (sumOfLevels)
		{
			return runningTotal > minLevel;
		}
		return false;
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster of the desired type and of the
	 * desired caster level.
	 *
	 * @param spellType The type of spellcaster (i.e. "ARCANE" or "devine")
	 * @param minLevel The desired caster level
	 * @return boolean
	 */
	public boolean isSpellCaster(final String spellType, final int minLevel)
	{
		return isSpellCaster(spellType, minLevel, false);
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster of the desired type and of the
	 * desired caster level.
	 *
	 * @param spellType The type of spellcaster (i.e. "ARCANE" or "devine")
	 * @param minLevel The desired caster level
	 * @param sumLevels True if all of the character caster levels should be
	 * added together before the comparison.
	 * @return boolean
	 */
	public boolean isSpellCaster(final String spellType, final int minLevel, final boolean sumLevels)
	{
		int runningTotal=0;

		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass) e1.next();

			if (spellType.equalsIgnoreCase(aClass.getSpellType()))
			{
				int classLevels = (int) getTotalBonusTo("CASTERLEVEL", aClass.getName());
				if ((classLevels == 0) && (canCastSpellTypeLevel(aClass.getSpellType(), 0, 1) || canCastSpellTypeLevel(aClass.getSpellType(), 1, 1)))
				{
					// missing CASTERLEVEL hack
					classLevels = aClass.getLevel();
				}
				classLevels +=  (int) getTotalBonusTo("PCLEVEL", aClass.getName());
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
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass) e1.next();

			if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE) && (aClass.getLevel() <= maxLevel))
			{
				return true;
			}
		}

		return false;
	}

	public Map getSpellInfoMap(final String key1, final String key2)
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

		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			addSpells(aClass);
		}

		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			final Ability aFeat = (Ability) i.next();
			addSpells(aFeat);
		}

		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			addSpells(aSkill);
		}

		// Domains are skipped - it's assumed that their spells are added to the first divine spellcasting
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				addSpells(eq);

				List aList = eq.getEqModifierList(true);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						addSpells(eqMod);
					}
				}

				aList = eq.getEqModifierList(false);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						addSpells(eqMod);
					}
				}
			}
		}

		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			addSpells(aTemplate);
		}
	}

	/**
	 * Parses a spells range (short, medium or long) into an Integer
	 * based on the spell and spell casters level
	 * @param aSpell
	 * @param aName
	 * @param si
	 * @return spell range
	 */
	public String getSpellRange(final Spell aSpell, final String aName, final SpellInfo si)
	{
		String aRange = aSpell.getRange();
		final String aSpellClass = "CLASS:" + aName;
		int rangeInFeet;
		String aString = Globals.getGameModeSpellRangeFormula(aRange.toUpperCase());

		if (aRange.equalsIgnoreCase("CLOSE") && (aString == null))
		{
			aString = "((CASTERLEVEL/2).TRUNC*5)+25";
		}
		else if (aRange.equalsIgnoreCase("MEDIUM") && (aString == null))
		{
			aString = "(CASTERLEVEL*10)+100";
		}
		else if (aRange.equalsIgnoreCase("LONG") && (aString == null))
		{
			aString = "(CASTERLEVEL*40)+400";
		}

		if (aString != null)
		{
			final List metaFeats = si.getFeatList();
			rangeInFeet = getVariableValue(aSpell, aString, aSpellClass).intValue();

			if ((metaFeats != null) && !metaFeats.isEmpty())
			{
				for (Iterator e = metaFeats.iterator(); e.hasNext();)
				{
					final Ability aFeat = (Ability) e.next();
					rangeInFeet += (int) aFeat.bonusTo("SPELL", "RANGE", this, this);

					final int iMult = (int) aFeat.bonusTo("SPELL", "RANGEMULT", this, this);

					if (iMult > 0)
					{
						rangeInFeet = (rangeInFeet * iMult);
					}
				}
			}

			aRange += (" (" + Globals.getGameModeUnitSet().displayDistanceInUnitSet(rangeInFeet) +
					Globals.getGameModeUnitSet().getDistanceUnit() + ")");
		}

		return aRange;
	}

	/**
	 * Computes the Caster Level for a Class
	 * @param aSpell
	 * @param aName
	 * @return caster level for spell
	 **/
	public int getCasterLevelForSpell(final Spell aSpell, final String aName)
	{
		final String aSpellClass = "CLASS:" + aName;
		int casterLevel = getVariableValue(aSpell, "CASTERLEVEL", aSpellClass).intValue();

		return casterLevel;
	}

	/**
	 * Computes the Caster Level for a Class
	 * @param aClass
	 * @return class caster level
	 **/
	public int getClassCasterLevel(final PCClass aClass)
	{
		final int casterLevel = getVariableValue("CASTERLEVEL", "CLASS:"+aClass.getName()).intValue();
		return casterLevel;
	}

	/**
	 * Computes the Caster Level for a race
	 * @param aRace
	 * @return race caster level
	 **/
	public int getRaceCasterLevel(final Race aRace)
	{
		final int casterLevel = getVariableValue("CASTERLEVEL", "RACE:"+aRace.getName()).intValue();
		return casterLevel;
	}

	/**
	 * Calculates total bonus from all stats
	 * @param aType
	 * @param aName
	 * @return stat bonus to
	 */
	public double getStatBonusTo(String aType, String aName)
	{
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		final List aList = statList.getBonusListOfType(aType, aName);

		return calcBonusFromList(aList);
	}

	/**
	 * return bonus from Temporary Bonuses
	 * @param aType
	 * @param aName
	 * @return temp bonus to
	 */
	public double getTempBonusTo(String aType, String aName)
	{
		double bonus = 0;

		if (getTempBonusList().isEmpty() || !getUseTempMods())
		{
			return bonus;
		}

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		for (Iterator b = getTempBonusList().iterator(); b.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) b.next();
			final String bString = aBonus.toString();

			if ((bString.indexOf(aType) < 0) || (bString.indexOf(aName) < 0))
			{
				continue;
			}

			final Object tarObj = aBonus.getTargetObject();
			final Object creObj = aBonus.getCreatorObject();

			if ((creObj == null) || (tarObj == null))
			{
				continue;
			}

			if (!(creObj instanceof PObject) || !(tarObj instanceof PlayerCharacter))
			{
				continue;
			}

			final PlayerCharacter bPC = (PlayerCharacter) tarObj;

			if (bPC != this)
			{
				continue;
			}

			final PObject aCreator = (PObject) creObj;
			bonus += aCreator.calcBonusFrom(aBonus, this, this);
		}

		return bonus;
	}

	/**
	 * Parses through all templates to calc total bonus
	 * @param aType
	 * @param aName
	 * @param subSearch
	 * @return template bonus to
	 */
	public double getTemplateBonusTo(String aType, String aName, final boolean subSearch)
	{
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		return getPObjectWithCostBonusTo(templateList, aType, aName, subSearch);
	}

	/**
	 * Get the total bonus from Stats, Size, Age, Alignment, Classes,
	 * companions, Equipment, Feats, Templates, Domains, Races, etc
	 * This value is taken from an already populated HashMap for speed
	 *
	 * @param bonusType Type of bonus ("COMBAT" or "SKILL")
	 * @param bonusName Name of bonus ("AC"    or "Hide");
	 * @return total bonus to
	 */
	public double getTotalBonusTo(final String bonusType, final String bonusName)
	{
		final String prefix = new StringBuffer(bonusType).append('.').append(bonusName).toString();

		return sumActiveBonusMap(prefix);
	}

	public int getTotalLevels()
	{
		int totalLevels = 0;

		totalLevels += totalNonMonsterLevels();

		// Monster hit dice count towards total levels -- was totalMonsterLevels()
		//  sage_sam changed 03 Dec 2002 for Bug #646816
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
	 * Get the value of the desired stat at the point just before
	 * the character was raised to the next level.
	 *
	 * @param statAbb The short name of the stat to calculate the value of.
	 * @param level The level we want to see the stat at.
	 * @param includePost Should stat mods that occurred after levelling be included?
	 * @return The stat as it was at the level
	 */
	public int getTotalStatAtLevel(final String statAbb, final int level, final boolean includePost)
	{
		int curStat = getStatList().getTotalStatFor(statAbb);
		for (int idx = getLevelInfoSize() - 1; idx >= level; --idx)
		{
			final int statLvlAdjust = ((PCLevelInfo) pcLevelInfo.get(idx)).getTotalStatMod(statAbb, true);
			curStat -= statLvlAdjust;
		}
		// If the user doesn't want POST changes, we remove any made in the target level only
		if (!includePost && level > 0)
		{
			int statLvlAdjust = ((PCLevelInfo) pcLevelInfo.get(level-1)).getTotalStatMod(statAbb, true);
			statLvlAdjust -= ((PCLevelInfo) pcLevelInfo.get(level-1)).getTotalStatMod(statAbb, false);
			curStat -= statLvlAdjust;

		}

		return curStat;
	}

	public int getTwoHandDamageDivisor()
	{
		int div = getVariableValue("TWOHANDDAMAGEDIVISOR", "").intValue();

		if (div == 0)
		{
			div = 2;
		}

		return div;
	}

	/**
	 * Get the unarmed damage string for this PC as adjusted by the booleans passed in.
	 * @param includeCrit
	 * @param includeStrBonus
	 * @param adjustForPCSize
	 * @return the unarmed damage string
	 */
	public String getUnarmedDamageString(
			final boolean includeCrit,
			final boolean includeStrBonus,
			final boolean adjustForPCSize)
	{
		String retString = "2|1d2";

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			retString = PlayerCharacterUtilities.getBestUDamString(
					retString,
					aClass.getUdamForLevel(aClass.getLevel(), includeCrit, includeStrBonus, this, adjustForPCSize));
		}

		retString = PlayerCharacterUtilities.getBestUDamString(
				retString,
				race.getUdamFor(includeCrit, includeStrBonus, this));

		if (deity != null)
		{
			retString = PlayerCharacterUtilities.getBestUDamString(
					retString,
					deity.getUdamFor(includeCrit, includeStrBonus, this));
		}


		for (Iterator it = getRealFeatsIterator(); it.hasNext(); )
		{
			final Ability aFeat = (Ability) it.next();
			retString = PlayerCharacterUtilities.getBestUDamString(
					retString,
					aFeat.getUdamFor(includeCrit, includeStrBonus, this));
		}

		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			retString = PlayerCharacterUtilities.getBestUDamString(
					retString,
					aSkill.getUdamFor(includeCrit, includeStrBonus, this));
		}

		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();

			if (aCD.getDomain() != null)
			{
				retString = PlayerCharacterUtilities.getBestUDamString(
						retString,
						aCD.getDomain().getUdamFor(includeCrit, includeStrBonus, this));
			}
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				retString = PlayerCharacterUtilities.getBestUDamString(
						retString,
						eq.getUdamFor(includeCrit, includeStrBonus, this));

				List aList = eq.getEqModifierList(true);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						retString = PlayerCharacterUtilities.getBestUDamString(
								retString,
								eqMod.getUdamFor(includeCrit, includeStrBonus, this));
					}
				}

				aList = eq.getEqModifierList(false);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						retString = PlayerCharacterUtilities.getBestUDamString(
								retString,
								eqMod.getUdamFor(includeCrit, includeStrBonus, this));
					}
				}
			}
		}

		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			retString = PlayerCharacterUtilities.getBestUDamString(
					retString,
					aTemplate.getUdamFor(includeCrit, includeStrBonus, this));
		}

		// string is in form sides|damage, just return damage portion
		return retString.substring(retString.indexOf('|') + 1);
	}

	public boolean getUseMasterSkill()
	{
		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) e.next();

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
	 * @param aBool
	 */
	public void setUseTempMods(final boolean aBool)
	{
		useTempMods = aBool;
// commented out setDirty because this causes a re-load of all tabs every time any tab is viewed! merton_monk
//		setDirty(true);
	}

	public boolean getUseTempMods()
	{
		return useTempMods;
	}

	/**
	 * Evaluates a variable for this character
	 * e.g: getVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 *
	 * @param aString The variable to be evaluated
	 * @param src     The source within which the variable is evaluated
	 * @return The value of the variable
	 */
	public Float getVariableValue(final String aString, final String src)
	{
		return getVariableValue(null, aString, src);
	}

	/**
	 * Evaluates a variable for this character
	 * e.g: getVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 *
	 * @param aSpell  This is specifically to compute bonuses to CASTERLEVEL for a specific spell.
	 * @param aString The variable to be evaluated
	 * @param src     The source within which the variable is evaluated
	 * @return The value of the variable
	 */
	private Float getVariableValue(final Spell aSpell, String aString, String src)
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






	int getTotalCasterLevelWithSpellBonus(final Spell aSpell, final String spellType, final String classOrRace, final int casterLev)
	{

		int tBonus = casterLev;

		String tType;
		String tStr;
		final ArrayList bonuses = new ArrayList();

		boolean replaceCasterLevel = false;

		if(aSpell != null && aSpell.getFixedCasterLevel() != null) {
			return getVariableValue(aSpell.getFixedCasterLevel(), "").intValue();
		}

		if (classOrRace != null)
		{
			tBonus = (int)getTotalBonusTo("CASTERLEVEL", classOrRace);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", classOrRace);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}

			//Support both types of syntax for CLASS:
			//BONUS:CASTERLEVEL|Sorcerer|1 and BONUS:CASTERLEVEL|CLASS.Sorcerer|1
			if (!classOrRace.startsWith("RACE.")) {
				tStr = "CLASS." + classOrRace;
				tBonus = (int)getTotalBonusTo("CASTERLEVEL", tStr);
				if (tBonus > 0)
				{
					tType = getSpellBonusType("CASTERLEVEL", tStr);
					bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
				}
			}
		}

		if (aSpell == null)
		{
			return(tBonus);
		}

		if (!spellType.equals(Constants.s_NONE))
		{
			tStr = "TYPE." + spellType;
			tBonus = (int)getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			tBonus =  (int)getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		tStr = "SPELL." + aSpell.getName();
		tBonus = (int)getTotalBonusTo("CASTERLEVEL", tStr);
		if (tBonus > 0)
		{
			tType = getSpellBonusType("CASTERLEVEL", tStr);
			bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
		}
		tStr += ".RESET";
		tBonus =  (int)getTotalBonusTo("CASTERLEVEL", tStr);
		if (tBonus > 0)
		{
			replaceCasterLevel = true;
			tType = getSpellBonusType("CASTERLEVEL", tStr);
			bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
		}

		final SortedSet school = aSpell.getSchools();
		for (Iterator i = school.iterator(); i.hasNext();)
		{
			final String sName = (String) i.next();
			tStr = "SCHOOL." + sName;
			tBonus = (int)getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			tBonus =  (int)getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		final SortedSet subschool = aSpell.getSubschools();
		for (Iterator i = subschool.iterator(); i.hasNext();)
		{
			final String sName = (String) i.next();
			tStr = "SUBSCHOOL." + sName;
			tBonus = (int)getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			tBonus =  (int)getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		final List descList = aSpell.getDescriptorList();
		if (descList != null)
		{
			for (int z = 0; z < descList.size(); z++)
			{
				tStr = "DESCRIPTOR." + descList.get(z);
				tBonus = (int)getTotalBonusTo("CASTERLEVEL", tStr);
				if (tBonus > 0)
				{
					tType = getSpellBonusType("CASTERLEVEL", tStr);
					bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
				}
				tStr += ".RESET";
				tBonus =  (int)getTotalBonusTo("CASTERLEVEL", tStr);
				if (tBonus > 0)
				{
					replaceCasterLevel = true;
					tType = getSpellBonusType("CASTERLEVEL", tStr);
					bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
				}
			}
		}

		final Map domainMap = aSpell.getLevelInfo(this);
		if (domainMap != null)
		{
			final Iterator mapKeys = domainMap.keySet().iterator();
			while (mapKeys.hasNext())
			{
				final String mKey = (String)mapKeys.next();
				if (mKey.startsWith("DOMAIN|"))
				{
					tStr = "DOMAIN." + mKey.substring(7);
					tBonus = (int)getTotalBonusTo("CASTERLEVEL", tStr);
					if (tBonus > 0)
					{
						tType = getSpellBonusType("CASTERLEVEL", tStr);
						bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
					}
					tStr += ".RESET";
					tBonus =  (int)getTotalBonusTo("CASTERLEVEL", tStr);
					if (tBonus > 0)
					{
						replaceCasterLevel = true;
						tType = getSpellBonusType("CASTERLEVEL", tStr);
						bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
					}
				}
			}
		}

		//now go through all bonuses, checking types to see what should add together
		for (int z = 0; z < bonuses.size()-1; z++)
		{
			final CasterLevelSpellBonus zBonus = (CasterLevelSpellBonus)bonuses.get(z);

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

			for (int k = z+1; k < bonuses.size(); k++)
			{
				final CasterLevelSpellBonus kBonus = (CasterLevelSpellBonus)bonuses.get(k);

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

				//if both end in ".REPLACE", add togther and save for later comparison
				if (zReplace && kReplace)
				{
					kBonus.setBonus(zBonus.getBonus() + kBonus.getBonus());
					zBonus.setBonus(0);
					continue;
				}

				//if either ends in ".STACK", then they will add
				if (zStack || kStack)
				{
					continue;
				}

				//otherwise, only keep max
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

		//Now go through bonuses and add it up
		for (int z = 0; z < bonuses.size(); z++)
		{
			final CasterLevelSpellBonus resultBonus = (CasterLevelSpellBonus)bonuses.get(z);
			result += resultBonus.getBonus();
		}

		if(result == 0) {
			result = 1;
		}

		return(result);

	}

	private String getSpellBonusType(final String bonusType, final String bonusName)
	{

		String prefix = new StringBuffer(bonusType).append('.').append(bonusName).toString();
		prefix = prefix.toUpperCase();

		for (Iterator i = getActiveBonusMap().keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();

			final String rString = aKey;
			String tString = aKey;

			// rString could be something like:
			//  COMBAT.AC:Armor.REPLACE
			// So need to remove the .STACK or .REPLACE
			// to get a match for prefix like: COMBAT.AC:Armor
			if (rString.endsWith(".STACK"))
			{
				tString = rString.substring(0, rString.length() - 6);
			}
			else if (rString.endsWith(".REPLACE"))
			{
				tString = rString.substring(0, rString.length() - 8);
			}

			// if prefix is of the form:
			// COMBAT.AC
			// then is must match rstring:
			//  COMBAT.AC
			//  COMBAT.AC:Luck
			//  COMBAT.AC:Armor.REPLACE
			// However, it must not match
			//  COMBAT.ACCHECK
			if ((tString.length() > prefix.length()) && !tString.startsWith(prefix + ":"))
			{
				continue;
			}

			if (tString.startsWith(prefix))
			{
				final int typeIndex = tString.indexOf(":");
				if (typeIndex > 0)
				{
					return(rString.substring(typeIndex+1)); //use rString to get .REPLACE or .STACK
				}
				return(""); //no type;
			}

		}

		return(""); //just return no type

	}

	public Iterator getVirtualFeatListIterator()
	{
		return this.getVirtualFeatList().iterator();
	}

	public List getVirtualFeatList()
	{
		List vFeatList = getStableVirtualFeatList();

		//Did we get a valid list? If so, return it.
		if (vFeatList != null)
		{
			return vFeatList;
		}
		setVirtualFeatsStable(true);
		vFeatList = new ArrayList();
		if (stableVirtualFeatList != null)
		{
			for (Iterator i = stableVirtualFeatList.iterator(); i.hasNext();)
			{
				final Ability aFeat = (Ability) i.next();
				if (aFeat.needsSaving())
				{
					if(PrereqHandler.passesAll(aFeat.getPreReqList(), this, aFeat)) {
						vFeatList.add(aFeat);
					}
				}
			}
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			final List aList = aClass.getVirtualFeatList(aClass.getLevel());

			for (Iterator e1 = aList.iterator(); e1.hasNext();)
			{
				final Ability aFeat = (Ability) e1.next();
				if(PrereqHandler.passesAll(aFeat.getPreReqList(), this, aFeat)) {
					vFeatList.add(aFeat);
				}
			}
		}

		for (Iterator it = getRealFeatsIterator(); it.hasNext();)
		{
			final Ability aFeat = (Ability) it.next();
			final List aList = aFeat.getVirtualFeatList();

			for (Iterator it1 = aList.iterator(); it1.hasNext();)
			{
				final Ability bFeat = (Ability) it1.next();
				if(PrereqHandler.passesAll(bFeat.getPreReqList(), this, bFeat)) {
					vFeatList.add(bFeat);
				}
			}
		}

		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			final List aList = aTemplate.getVirtualFeatList();

			for (Iterator e1 = aList.iterator(); e1.hasNext();)
			{
				final Ability aFeat = (Ability) e1.next();
				if(PrereqHandler.passesAll(aFeat.getPreReqList(), this, aFeat)) {
					vFeatList.add(aFeat);
				}
			}
		}

		if (!equipmentList.isEmpty())
		{
			for (Iterator e = equipmentList.iterator(); e.hasNext();)
			{
				final Equipment aE = (Equipment) e.next();

				if (aE.isEquipped())
				{
					for (Iterator e1 = aE.getVirtualFeatList().iterator(); e1.hasNext();)
					{
						final Ability aFeat = (Ability) e1.next();

						// TODO Check for dups?
						if(PrereqHandler.passesAll(aFeat.getPreReqList(), this, aFeat)) {
							vFeatList.add(aFeat);
						}
					}
				}
			}
		}

		if (getRace() != null)
		{
			for (Iterator e = getRace().getVirtualFeatList().iterator(); e.hasNext();)
			{
				final Ability aFeat = (Ability) e.next();

				// TODO Check for dups?
				if(PrereqHandler.passesAll(aFeat.getPreReqList(), this, aFeat)) {
					vFeatList.add(aFeat);
				}
			}
		}


		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			final List aList = aSkill.getVirtualFeatList();

			for (Iterator e1 = aList.iterator(); e1.hasNext();)
			{
				final Ability aFeat = (Ability) e1.next();
				if(PrereqHandler.passesAll(aFeat.getPreReqList(), this, aFeat)) {
					vFeatList.add(aFeat);
				}
			}
		}

		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();

			if (aCD.getDomain() != null)
			{
				final List aList = aCD.getDomain().getVirtualFeatList();

				for (Iterator e1 = aList.iterator(); e1.hasNext();)
				{
					final Ability aFeat = (Ability) e1.next();
					if(PrereqHandler.passesAll(aFeat.getPreReqList(), this, aFeat)) {
						vFeatList.add(aFeat);
					}
				}
			}
		}
		if (deity != null)
		{
			final List aList = deity.getVirtualFeatList();

			for (Iterator e1 = aList.iterator(); e1.hasNext();)
			{
				final Ability aFeat = (Ability) e1.next();
				if(PrereqHandler.passesAll(aFeat.getPreReqList(), this, aFeat)) {
					vFeatList.add(aFeat);
				}
			}
		}
		if (!companionModList.isEmpty())
		{
			for (Iterator e2 = companionModList.iterator(); e2.hasNext(); )
			{
				final CompanionMod aMod = (CompanionMod) e2.next();
				final List aList = aMod.getVirtualFeatList();

				for (Iterator e1 = aList.iterator(); e1.hasNext();)
				{
					final Ability aFeat = (Ability) e1.next();
					if(PrereqHandler.passesAll(aFeat.getPreReqList(), this, aFeat)) {
						vFeatList.add(aFeat);
					}
				}
			}
		}

		setStableVirtualFeatList(vFeatList);

		return vFeatList;
	}

	private Map getVisionMap()
	{
		Map visMap = new HashMap();

		if (race != null)
		{
			visMap = addStringToVisionMap(visMap, race.getVision());
		}

		if (deity != null)
		{
			visMap = addStringToVisionMap(visMap, deity.getVision());
		}

		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			visMap = addStringToVisionMap(visMap, aClass.getVision());
		}

		for (Iterator i = getRealFeatsIterator(); i.hasNext();)
		{
			final Ability aFeat = (Ability) i.next();
			visMap = addStringToVisionMap(visMap, aFeat.getVision());
		}

		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			visMap = addStringToVisionMap(visMap, aSkill.getVision());
		}

		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();

			if (aCD.getDomain() != null)
			{
				visMap = addStringToVisionMap(visMap, aCD.getDomain().getVision());
			}
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				visMap = addStringToVisionMap(visMap, eq.getVision());

				List aList = eq.getEqModifierList(true);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						visMap = addStringToVisionMap(visMap, eqMod.getVision());
					}
				}

				aList = eq.getEqModifierList(false);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						visMap = addStringToVisionMap(visMap, eqMod.getVision());
					}
				}
			}
		}

		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			visMap = addStringToVisionMap(visMap, aTemplate.getVision());
		}

		// parse through the global list of vision tags and see
		// if this PC has any BONUS:VISION tags which will create
		// a new visionMap entry
		for (Iterator i = Globals.getVisionMap().keySet().iterator(); i.hasNext();)
		{
			final String aKey = (String) i.next();
			final int aVal = (int) getTotalBonusTo("VISION", aKey);

			if (aVal > 0)
			{
				// add a 0 value, as the bonus is added
				// in the addStringToVisionMap() routine
				final HashMap newMap = new HashMap();
				newMap.put(aKey, "0");
				visMap = addStringToVisionMap(visMap, newMap);
			}
		}
		return visMap;
	}

	public String getVision()
	{
		final StringBuffer vision = new StringBuffer();

		final Map visMap = getVisionMap();
		for (Iterator i = visMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			final Object bObj = visMap.get(aKey);

			if (bObj == null)
			{
				continue;
			}

			final int val = Integer.parseInt(bObj.toString());

			if (vision.length() > 0)
			{
				vision.append(", ");
			}

			vision.append(aKey);

			if (val > 0)
			{
				vision.append(" (").append(val).append("')");
			}
		}

		return vision.toString();
	}

	public TreeSet getVisiontypeList()
	{
		final TreeSet visions = new TreeSet();

		final Map visMap = getVisionMap();
		for (Iterator i = visMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			final Object bObj = visMap.get(aKey);

			if (bObj == null)
			{
				continue;
			}

			final int val = Integer.parseInt(bObj.toString());

			final StringBuffer vision = new StringBuffer();
			vision.append(aKey);

			if (val > 0)
			{
				vision.append(" (").append(val).append("')");
			}
			visions.add(vision.toString());
		}

		return visions;
	}

	public int abilityAC()
	{
		return calcACOfType("Ability");
	}

	/**
	 * adds CharacterDomain to list
	 * @param aCD
	 */
	public void addCharacterDomain(final CharacterDomain aCD)
	{
		if ((aCD != null) && !characterDomainList.contains(aCD) && (aCD.getDomain() != null))
		{
			characterDomainList.add(aCD);
			final PCClass domainClass = getClassNamed(aCD.getObjectName());
			if (domainClass != null)
			{
				final int _maxLevel = domainClass.getMaxCastLevel();
				aCD.getDomain().addSpellsToClassForLevels(domainClass, 0, _maxLevel);
			}
			setDirty(true);
		}
	}

	/**
	 * Sets the source of granted domains
	 * @param aType
	 * @param aName
	 * @param aLevel
	 * @param dNum
	 */
	public void addDomainSource(final String aType, final String aName, final int aLevel, final int dNum)
	{
		final String aString = aType + "|" + aName + "|" + aLevel;
		final String sNum = Integer.toString(dNum);
		domainSourceMap.put(aString, sNum);
		setDirty(true);
	}

	/**
	 * returns all equipment (from the equipmentList) of type aString
	 * @param aList
	 * @param aString
	 * @return List
	 */
	public List addEqType(final List aList, final String aString)
	{
		for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.typeStringContains(aString))
			{
				aList.add(eq);
				setDirty(true);
			}
			else if (aString.equalsIgnoreCase("CONTAINED") && (eq.getParent() != null))
			{
				aList.add(eq);
				setDirty(true);
			}
		}

		return aList;
	}

	public void addKit(final Kit aKit)
	{
		if (kitList == null)
		{
			kitList = new ArrayList();
		}

		kitList.add(aKit);
		setDirty(true);
	}

	public void addLanguage(final String aString)
	{
		final Language aLang = Globals.getLanguageNamed(aString);

		if (aLang != null)
		{
			if (!getLanguagesList().contains(aLang))
			{
				getLanguagesList().add(aLang);
				setDirty(true);
			}
		}
	}

	public void addNaturalWeapons(final List weapons)
	{
		equipmentListAddAll(weapons);
		EquipSet eSet = getEquipSetByIdPath("0.1");
		if (eSet != null)
		{
			for (Iterator i = weapons.iterator(); i.hasNext(); )
			{
				Equipment eq = (Equipment) i.next();
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

	public void addShieldProfs(final List aList)
	{
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			addShieldProf((String) i.next());
		}
	}

	public Skill addSkill(final Skill addSkill)
	{
		Skill aSkill;

		//
		// First, check to see if skill is already in list
		//
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			aSkill = (Skill) e.next();

			if (aSkill.getKeyName().equals(addSkill.getKeyName()))
			{
				return aSkill;
			}
		}

		//
		// Skill not found, add to list
		//
		aSkill = (Skill) addSkill.clone();
		getSkillList().add(aSkill);
		setDirty(true);

		if (!isImporting())
		{
			aSkill.globalChecks(this);
			calcActiveBonuses();
		}

		return aSkill;
	}

	/**
	 * acs is the CharacterSpell object containing the spell which is to be modified
	 * @param acs
	 *
	 * @param aFeatList     is the list of feats to be added to the SpellInfo object added to acs
	 * @param className     is the name of the class whose list of characterspells will be modified
	 * @param bookName      is the name of the book for the SpellInfo object
	 * @param spellLevel    is the original (unadjusted) level of the spell not including feat adjustments
	 * @param adjSpellLevel is the adjustedLevel (including feat adjustments) of this spell, it may be higher if the user chooses a higher level.
	 * @return an empty string on successful completion, otherwise
	 *         the return value indicates the reason the add function failed.
	 */
	public String addSpell(CharacterSpell acs, final List aFeatList, final String className, final String bookName, final int adjSpellLevel,
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
			return "Invalid spell book name.";
		}

		if (className != null)
		{
			aClass = getClassNamed(className);

			if ((aClass == null) && (className.lastIndexOf('(') >= 0))
			{
				aClass = getClassNamed(className.substring(0, className.lastIndexOf('(')).trim());
			}
		}

		if (aClass == null)
		{
			return "No class named " + className;
		}

		if (!aClass.getMemorizeSpells() && !bookName.equals(Globals.getDefaultSpellBook()))
		{
			return aClass.getName() + " can only add to " + Globals.getDefaultSpellBook();
		}

		// Divine spellcasters get no bonus spells at level 0
		// TODO: allow classes to define how many bonus spells they get each level!
		//int numSpellsFromSpecialty = aClass.getNumSpellsFromSpecialty();
		//if (spellLevel == 0 && "Divine".equalsIgnoreCase(aClass.getSpellType()))
		//{
			//numSpellsFromSpecialty = 0;
		//}
		// all the exists checks are done.
		// now determine how many specialtySpells
		// of this level for this class in this book
		int spellsFromSpecialty = 0; // TODO: value never used

		// first we check this spell being added
		if (acs.isSpecialtySpell())
		{
			++spellsFromSpecialty;
		}

		// now all the rest of the already known spells
		final List sList = aClass.getSpellSupport().getCharacterSpell(null, bookName, adjSpellLevel);

		if (!sList.isEmpty())
		{
			for (Iterator i = sList.iterator(); i.hasNext();)
			{
				final CharacterSpell cs = (CharacterSpell) i.next();

				if (!cs.equals(acs) && cs.isSpecialtySpell())
				{
					++spellsFromSpecialty;
				}
			}
		}

		// don't allow adding spells which are prohibited
		// But if a spell is both prohibited and in a specialty
		// which can be the case for some spells, then allow it.
		if (!acs.isSpecialtySpell() && aClass.isProhibited(aSpell, this))
		{
			return acs.getSpell().getName() + " is prohibited.";
		}

		// Now let's see if they should be able to add this spell
		// first check for known/cast/threshold
		final int known = aClass.getKnownForLevel(aClass.getLevel(), spellLevel, this);
		int specialKnown = 0;
		final int cast = aClass.getCastForLevel(aClass.getLevel(), adjSpellLevel, bookName, true, true, this);
		aClass.memorizedSpellForLevelBook(adjSpellLevel, bookName);

		final boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());

		if (isDefault)
		{
			specialKnown = aClass.getSpecialtyKnownForLevel(aClass.getLevel(), spellLevel, this);
		}

		SpellBook spellBook = getSpellBookByName(bookName);
		int numPages = 0;
		
		// known is the maximun spells that can be known this level
		// listNum is the current spells already memorized this level
		// cast is the number of spells that can be cast at this level
		// Modified this to use new availableSpells() method so you can "blow" higher-level slots on
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
			if (numPages+spellBook.getNumPagesUsed() > spellBook.getNumPages())
			{
				return "There are not enough pages left to add this spell to the spell book."; 
			}
			spellBook.setNumPagesUsed(numPages+spellBook.getNumPagesUsed());
			spellBook.setNumSpells(spellBook.getNumSpells() + 1);
		}
		else if (!aClass.getMemorizeSpells()
			&& !availableSpells(adjSpellLevel, aClass, bookName, true, acs.isSpecialtySpell(), this))
		{
			// If this were a specialty spell, would there be room?
			//
			if (!acs.isSpecialtySpell() && availableSpells(adjSpellLevel, aClass, bookName, true, true, this))
			{
				return "Your remaining slot(s) must be filled with your specialty";
			}

			int memTot = aClass.memorizedSpellForLevelBook(adjSpellLevel, bookName);
			int spellDifference = (known + specialKnown) - memTot;

			String ret = "You can only learn " + (known + specialKnown) + " spells for level " + adjSpellLevel
			+ "\nand there are no higher-level slots available";
			if (spellDifference > 0)
			{
				ret += "\n" + spellDifference + " spells from lower levels are using slots for this level.";
			}
			return ret;
		}
		else if (aClass.getMemorizeSpells() && !isDefault
			&& !availableSpells(adjSpellLevel, aClass, bookName, false, acs.isSpecialtySpell(), this))
		{
			if (!acs.isSpecialtySpell() && availableSpells(adjSpellLevel, aClass, bookName, false, true, this))
			{
				return "Your remaining slot(s) must be filled with your specialty or domain";
			}
			return "You can only prepare " + cast + " spells for level " + adjSpellLevel
			+ "\nand there are no higher-level slots available";
		}

		// determine if this spell already exists
		// for this character in this book at this level
		SpellInfo si = null;
		final List acsList = aClass.getSpellSupport().getCharacterSpell(acs.getSpell(), bookName, adjSpellLevel);
		if (!acsList.isEmpty())
		{
			for (int x=acsList.size()-1; x>=0; x--)
			{
				final CharacterSpell c = (CharacterSpell)acsList.get(x);
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
			// bogus.  I am trying to break as little as possible so if
			// I have one matching spell I will use it otherwise I will
			// use the passed in spell.
			if (acsList.size() == 1)
			{
				final CharacterSpell tcs = (CharacterSpell)acsList.get(0);
				si = tcs.getSpellInfoFor(bookName, adjSpellLevel, -1, aFeatList);
			}
			else
			{
				si = acs.getSpellInfoFor(bookName, adjSpellLevel, -1, aFeatList);
			}
		}

		if (si != null)
		{
			// ok, we already known this spell, so if they are
			// trying to add it to the default spellBook, barf
			// otherwise increment the number of times memorized
			if (isDefault)
			{
				return "The Known Spells spellbook contains all spells of this level that you know. You cannot place spells in multiple times.";
			}
			si.setTimes(si.getTimes() + 1);
		}
		else
		{
			if (isEmpty)
			{
				acs = new CharacterSpell(acs.getOwner(), acs.getSpell());
				aClass.getSpellSupport().addCharacterSpell(acs);
			}
			si = acs.addInfo(adjSpellLevel, 1, bookName, aFeatList); // TODO: value never used

			//
			//
			if (Spell.hasPPCost())
			{
				final Spell theSpell = acs.getSpell();
				int ppCost = theSpell.getPPCost();
				for (Iterator fi = aFeatList.iterator(); fi.hasNext(); )
				{
					final Ability aFeat = (Ability) fi.next();
					ppCost += (int)aFeat.bonusTo("PPCOST", theSpell.getName(), this, this);
				}
				si.setActualPPCost(ppCost);
			}
		}
		// Set number of pages on the spell
		si.setNumPages(numPages);
		setDirty(true);
		return "";
	}

	/**
	 * return value indicates if book was actually added or not
	 * @param aName
	 * @return TRUE or FALSE
	 */
	public boolean addSpellBook(final String aName)
	{
		if (aName!=null && (aName.length() > 0) && !spellBooks.contains(aName))
		{
			return addSpellBook(new SpellBook(aName,
				SpellBook.TYPE_PREPARED_LIST));
		}

		return false;
	}

	/**
	 * return value indicates if book was actually added or not
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
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();

			if (aTemplate.getName().equals(inTemplate.getName()))
			{
				return null; // template with duplicate name
			}
		}

		// Add a clone of the template passed in
		int lockMonsterSkillPoints = 0; // this is what this value was before adding this template
		for (Iterator ci = classList.iterator(); ci.hasNext(); )
		{
			final PCClass aClass = (PCClass) ci.next();
			if (aClass.isMonster())
			{
				lockMonsterSkillPoints = (int) getTotalBonusTo("MONSKILLPTS", "LOCKNUMBER");
				break;
			}
		}

		final PCTemplate inTmpl;
		try
		{
			inTmpl = (PCTemplate) inTemplate.clone();
			templateList.add(inTmpl);
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}


		this.setArmorProfListStable(false);
		List l = inTmpl.getSafeListFor(ListKey.KITS);
		for (int i1 = 0; i1 < l.size(); i1++)
		{
			KitUtilities.makeKitSelections(0, (String) l.get(i1), i1, this);
		}

		calcActiveBonuses();

		templateAutoLanguages.addAll(inTmpl.getSafeListFor(ListKey.AUTO_LANGUAGES));
		templateLanguages.addAll(inTmpl.getLanguageBonus());
		getAutoLanguages();
		addNaturalWeapons(inTmpl.getNaturalWeapons());

		inTmpl.chooseLanguageAutos(isImporting(), this);

		if (PlayerCharacterUtilities.canReassignTemplateFeats())
		{
			final List templateFeats = inTmpl.feats(getTotalLevels(), totalHitDice(), this, true);

			for (int i = 0, x = templateFeats.size(); i < x; ++i)
			{
				modFeatsFromList(null, (String) templateFeats.get(i), true, false);
			}
		}
		else
		{
			setAutomaticFeatsStable(false);
		}

		final List templates = inTmpl.getTemplates(isImporting(), this);

		for (int i = 0, x = templates.size(); i < x; ++i)
		{
			addTemplateNamed((String) templates.get(i));
		}

		setQualifyListStable(false);

		if (!isImporting())
		{
			getSpellList();
			inTmpl.globalChecks(this);
			inTmpl.checkRemovals(this);
		}

		setAutomaticFeatsStable(false);
		setAggregateFeatsStable(false);
		rebuildFeatAutoList();
		rebuildFeatAggreagateList();

		calcActiveBonuses();
		int postLockMonsterSkillPoints; // this is what this value was before adding this template
		boolean first = true;
		for (Iterator ci = classList.iterator(); ci.hasNext(); )
		{
			final PCClass aClass = (PCClass) ci.next();

			if (aClass.isMonster())
			{
				postLockMonsterSkillPoints = (int) getTotalBonusTo("MONSKILLPTS", "LOCKNUMBER");

				if (postLockMonsterSkillPoints != lockMonsterSkillPoints && postLockMonsterSkillPoints > 0)
				{
					for (Iterator e = getLevelInfo().iterator(); e.hasNext();)
					{
						final PCLevelInfo pi = (PCLevelInfo) e.next();
						final int newSkillPointsGained = aClass.recalcSkillPointMod(this, pi.getLevel());
						if (pi.getClassKeyName().equals(aClass.getKeyName()))
						{
							final int formerGained = pi.getSkillPointsGained();
							pi.setSkillPointsGained(newSkillPointsGained);
							pi.setSkillPointsRemaining(pi.getSkillPointsRemaining() + newSkillPointsGained - formerGained);
							aClass.setSkillPool(aClass.getSkillPool(this) + newSkillPointsGained - formerGained);
							setSkillPoints(getSkillPoints() + newSkillPointsGained - formerGained);
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
					for (int level = 1; level <= aClass.getLevel(); level++)
					{
						int baseHD = aClass.getLevelHitDieUnadjusted(this,
							level);
						if (baseHD != aClass.getLevelHitDie(this, level))
						{
							// If the HD has changed from base reroll
							aClass.rollHP(this, level, first);
						}
					}
				}
			}
			first = false;
		}
		setDirty(true);

		return inTmpl;
	}

	public PCTemplate addTemplateNamed(String templateName)
	{
		if (templateName == null)
		{
			return null;
		}

		if (templateName.startsWith("CHOOSE:"))
		{
			templateName = PCTemplate.chooseTemplate(templateName, this);
		}

		PCTemplate aTemplate = Globals.getTemplateNamed(templateName);

		if ((aTemplate == null) && templateName.endsWith(".REMOVE"))
		{
			aTemplate = Globals.getTemplateNamed(templateName.substring(0, templateName.length() - 7));
			removeTemplate(aTemplate);
		}
		else
		{
			addTemplate(aTemplate);
		}

		if (aTemplate == null)
		{
			System.err.println("Template not found: '" + templateName + "'");
		}
		setDirty(true);

		return aTemplate;
	}

	public void addWeaponProf(final String aString)
	{
		addWeaponProfToChosenFeats(aString);
		setDirty(true);
	}

	/**
	 * Add the Weapon Proficiency named profName to this character's Abilities.
	 * @param profName
	 *
	 * The method needs access to a list of existing weapon proficiencies.  So,
	 * we make a list of the current Proficiencies before calling it and pass it
	 * in.  When it returns, we add any nonduplicate Abilities to the normal
	 * Abilities list.
	 *
	 * Added this private method which was very similar to addWeaponProf, except
	 * it doesn't set the character dirty.  Need it because the same code was
	 * being duplicated in several places and they didn't set the character dirty.
	 */
	void addWeaponProfToChosenFeats(final String profName) {
		ArrayList temp = new ArrayList();
		getWeaponProfAbilities(temp);
		addWeaponProfToList(temp, profName, false);
		addNonDuplicateAbilities(temp);
	}

	/**
	 * Get a list of all chosen Abilities this PC has which are Weapon Proficiencies
	 * @param temp
	 */
	private void getWeaponProfAbilities(ArrayList temp) {
		Iterator it = this.getRealFeatsIterator();
		while (it.hasNext())
		{
			Ability ab = (Ability) it.next();
			if (ab.isWeaponProficiency())
			{
				temp.add(ab);
			}
		}
	}

	/**
	 * Add all of the Abilities in aList which the PC does not have to the
	 * chosen Ability store.
	 * @param aList the list of Abilities to test
	 */
	private void addNonDuplicateAbilities(ArrayList aList) {
		Iterator it = aList.iterator();
		while (it.hasNext())
		{
			Ability ab = (Ability) it.next();
			if (!this.hasRealFeat(ab))
			{
				this.addRealFeat(ab);
			}
		}
	}

	public void adjustGold(final double delta)
	{
		//I don't really like this hack, but setScale just won't work right...
		gold = new BigDecimal(gold.doubleValue() + delta).divide(BIG_ONE, 2, BigDecimal.ROUND_HALF_EVEN);
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

		if (getRace() == null) {
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

		// template
		if (!getTemplateList().isEmpty())
		{
			setMoveFromList(getTemplateList());
		}

		// class
		if (!getClassList().isEmpty())
		{
			setMoveFromList(getClassList());
		}

		// feat
		if (!aggregateFeatList().isEmpty())
		{
			setMoveFromList(aggregateFeatList());
		}

		// equipment
		if (!getEquipmentList().isEmpty())
		{
			setMoveFromList(getEquipmentList());

			for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
			{
				final Equipment eq = (Equipment) e.next();

				if (eq.isEquipped())
				{
					List bList = eq.getEqModifierList(true);
					setMoveFromList(bList);
					bList = eq.getEqModifierList(false);
					setMoveFromList(bList);
				}
			}
		}

		// domain

		/*
		   if (!characterDomainList.isEmpty())
		   {
			   setMoveFromList(characterDomainList());
		   }
		 */

		// tempmods
		if (!getTempBonusList().isEmpty() && getUseTempMods())
		{
			setMoveFromList(getTempBonusList());
		}

		// Need to create movement entries if there is a BONUS:MOVEADD
		// associated with that type of movement
		for (Iterator b = getActiveBonusList().iterator(); b.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) b.next();

			if (aBonus.getTypeOfBonus().equals("MOVEADD"))
			{
				String moveType = aBonus.getBonusInfo();

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
					setMyMoveRates(moveType, 0.0, new Double(0.0), "", 1);
				}
			}
		}
		setDirty(true);
	}

	public List aggregateSpellList(final String aType, final String school, final String subschool, final String descriptor, final int minLevel, final int maxLevel)
	{
		final List aArrayList = new ArrayList();

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			String cName = aClass.getKeyName();

			if (aClass.getCastAs().length() > 0)
			{
				cName = aClass.getCastAs();
			}

			if ("Any".equalsIgnoreCase(aType) || aType.equalsIgnoreCase(aClass.getSpellType())
				|| aType.equalsIgnoreCase(cName))
			{
				for (int a = minLevel; a <= maxLevel; a++)
				{
					final List aList = aClass.getSpellSupport().getCharacterSpell(null, "", a);

					if (aList.isEmpty())
					{
						continue;
					}

					for (Iterator i = aList.iterator(); i.hasNext();)
					{
						final CharacterSpell cs = (CharacterSpell) i.next();
						final Spell aSpell = cs.getSpell();

						if ((((school.length() == 0) || aSpell.getSchools().contains(school))
							|| ((subschool.length() == 0) || aSpell.getSubschools().contains(subschool))
							|| ((descriptor.length() == 0) || aSpell.descriptorContains(descriptor))))
						{
							aArrayList.add(cs.getSpell());
						}
					}
				}
			}
		}

		return aArrayList;
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
	 * @return    Total base attack bonus as an int
	 */
	public int baseAttackBonus()
	{
	  final String cacheLookup = "BaseAttackBonus";
	  final Float total = getVariableProcessor().getCachedVariable(cacheLookup);
	  if (total != null)
	  {
		return total.intValue();
	  }

		final PlayerCharacter nPC = getMasterPC();

		if ((nPC != null) && (getCopyMasterBAB().length() > 0))
		{
			int masterBAB = nPC.baseAttackBonus();
			final String copyMasterBAB = replaceMasterString(getCopyMasterBAB(), masterBAB);
			masterBAB = getVariableValue(copyMasterBAB, "").intValue();

			getVariableProcessor().addCachedVariable(cacheLookup, new Float(masterBAB));
			return masterBAB;
		}

		final int totalClassLevels = getTotalCharacterLevel();
		Map totalLvlMap = null;
		final Map classLvlMap;

		if (totalClassLevels > SettingsHandler.getGame().getBabMaxLvl())
		{
			totalLvlMap = getTotalLevelHashMap();
			classLvlMap = getCharacterLevelHashMap(SettingsHandler.getGame().getBabMaxLvl());

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

		getVariableProcessor().addCachedVariable(cacheLookup, new Float(bab));
		return bab;
	}

	/**
	 * get the base MOVE: plus any bonuses from BONUS:MOVE additions
	 * does not take into account Armor penalties to movement
	 * does not take into account penalties due to load carried
	 * @param moveIdx
	 * @param iLoad
	 * @return base movement
	 */
	public int basemovement(final int moveIdx, final int iLoad)
	{
		// get base movement
		final int move = getMovement(moveIdx).intValue();

		return move;
	}


	public int calcACOfType(final String ACType)
	{
		final List addList = SettingsHandler.getGame().getACTypeAddString(ACType);
		final List removeList = SettingsHandler.getGame().getACTypeRemoveString(ACType);

		if ((addList == null) && (removeList == null))
		{
			Logging.errorPrint("Invalid ACType: " + ACType);

			return 0;
		}

		int AC = 0;

		if (addList != null)
		{
			for (Iterator e = addList.iterator(); e.hasNext();)
			{
				final String aString = (String) e.next();
				final PObject aPObj = new PObject();
				getPreReqFromACType(aString, aPObj);

				if (PrereqHandler.passesAll(aPObj.getPreReqList(), this, aPObj  ))
				{
					final StringTokenizer aTok = new StringTokenizer(aString, "|");
					AC += subCalcACOfType(aTok);
				}
			}
		}

		if (removeList != null)
		{
			for (Iterator e = removeList.iterator(); e.hasNext();)
			{
				final String aString = (String) e.next();
				final PObject aPObj = new PObject();
				getPreReqFromACType(aString, aPObj);

				if (PrereqHandler.passesAll(aPObj.getPreReqList(), this, aPObj ))
				{
					final StringTokenizer aTok = new StringTokenizer(aString, "|");
					AC -= subCalcACOfType(aTok);
				}
			}
		}

		return AC;
	}

	/**
	 * Creates the activeBonusList which is used to calculate all
	 * the bonuses to a PC
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
		String origMapVal = activeBonusMap.toString();

		// ensure that the values for the looked up varaibles are the most up to date
		setDirty(true);
		calcActiveBonusLoop();

		// Get the new contents of the map
		String mapVal = activeBonusMap.toString();

		// As the map is a TreeMap we know that the contents will be in
		// alphabetical order, so doing a straight string compare is
		// the easiest way to compare the whole tree.
		while (!mapVal.equals(origMapVal)) {
			// If the newly calculated bonus map is different to the old one
			// loop again until they are the same.
			setDirty(true);
			calcActiveBonusLoop();
			origMapVal = mapVal;
			mapVal = activeBonusMap.toString();
		}
	}

	private void calcActiveBonusLoop()
	{
		// First we clear the current list
		clearActiveBonuses();

		// walk through all the possible bonus granters
		// to build up the active list of bonuses
		//
		calcStatBonuses();
		calcSizeAdjustmentBonuses();
		calcAgeBonuses();
		calcCheckBonuses();
		calcAlignmentBonuses();
		calcFeatBonuses();
		calcClassBonuses();
		calcCompanionModBonuses();
		calcEquipmentBonuses();
		calcTemplateBonuses();
		calcDomainBonuses();
		calcRaceBonuses();
		calcDeityBonuses();
		calcSkillBonuses();

		calcPurchaseModeBonuses();

		//calcWeaponProfBonuses();
		//calcArmorProfBonuses();
		if (getUseTempMods())
		{
			calcTempBonuses();
		}

		// First order the bonusList for dependencies
		sortActiveBonusList();
		//
		// Now build the activeBonusMap from all the bonuses
		//
		buildActiveBonusMap();
	}

	/**
	 * Sorts the activeBonusList according to dependencies
	 **/
	private void sortActiveBonusList()
	{
		final List aList = new ArrayList();

		// 'BONUS:blah|blah|Foo' depends on
		// 'BONUS:VAR|Foo|MyGoo' which depends on
		// 'BONUS:VAR|MyGoo|2'

		// BONUS: type      | info           | value

		// BONUS:COMBAT     |TOHIT           |STR
		// BONUS:STAT       |STR             |rage
		// BONUS:VAR        |rage            |2

		int aSize = getActiveBonusList().size();
		int i;
		for (i = 0; i < aSize; i++)
		{
			final BonusObj aBonus = (BonusObj) getActiveBonusList().get(i);
			int iFound = 0;
			for (int ii = 0; ii < aList.size(); ii++)
			{
				final BonusObj tempBonus = (BonusObj) aList.get(ii);
				if (tempBonus.getDependsOn(aBonus.getBonusInfo()))
				{
					iFound = ii;
				}
			}
			aList.add(iFound, aBonus);
		}
		int iCount = aSize;
		for (i = 0; i < iCount; )
		{
			final BonusObj aBonus = (BonusObj) aList.get(i);
			//
			// Move to end of list
			//
			if (aBonus.getDependsOn("JEPFORMULA"))
			{
				aList.remove(i);
				aList.add(aBonus);
				--iCount;
			}
			else
			{
				++i;
			}
		}

		setActiveBonusList(aList);

		aList.clear();

		// go through and move all the static bonuses to the front
		aSize = getActiveBonusList().size();
		for (i = 0; i < aSize; i++)
		{
			final BonusObj aBonus = (BonusObj) getActiveBonusList().get(i);
			if (aBonus.isValueStatic())
			{
				aList.add(0, aBonus);
			}
			else
			{
				aList.add(aBonus);
			}
		}
		setActiveBonusList(aList);
	}

	/**
	 * Calculate the Challenge Rating
	 * @return CR
	 **/
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

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			CR += aClass.calcCR(this);
		}

		for (int x = 0; x < templateList.size(); ++x)
		{
			CR += ((PCTemplate) templateList.get(x)).getCR(getTotalLevels(), totalHitDice());
		}

		final int raceCR = race.getCR();

		if ((raceCR > 0) || (CR == 0))
		{
			CR += raceCR;
		}

		return CR;
	}

	/**
	 * Get all possible sources of Damage Resistance and calculate
	 * @return DR
	 **/
	public String calcDR()
	{
		Map drMap = new HashMap();
		drMap = addStringToDRMap(drMap, race.getDR());

		if (deity != null)
		{
			drMap = addStringToDRMap(drMap, deity.getDR());
		}

		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			drMap = addStringToDRMap(drMap, aClass.getDR());
		}

		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			final Ability aFeat = (Ability) i.next();
			drMap = addStringToDRMap(drMap, aFeat.getDR());
		}

		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			drMap = addStringToDRMap(drMap, aSkill.getDR());
		}

		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();

			if (aCD.getDomain() != null)
			{
				drMap = addStringToDRMap(drMap, aCD.getDomain().getDR());
			}
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				drMap = addStringToDRMap(drMap, eq.getDR());

				List aList = eq.getEqModifierList(true);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						drMap = addStringToDRMap(drMap, eqMod.getDR());
					}
				}

				aList = eq.getEqModifierList(false);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						drMap = addStringToDRMap(drMap, eqMod.getDR());
					}
				}
			}
		}

		final int atl = getTotalLevels();
		final int thd = totalHitDice();

		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			drMap = addStringToDRMap(drMap, aTemplate.getDR(atl, thd));
		}

		final StringBuffer DR = new StringBuffer();

		for (Iterator i = drMap.keySet().iterator(); i.hasNext();)
		{
			final String damageType = i.next().toString();
			String symbol = "";
			int protectionValue = Integer.parseInt(drMap.get(damageType).toString());
			int damageTypeAsInteger = 0;

			try
			{
				damageTypeAsInteger = Integer.parseInt(damageType);
			}
			catch (NumberFormatException ignore)
			{
				 //Do nothing, the damage type is some kind of special value like 'Silver'
			}

			if (damageTypeAsInteger > 0)
			{
				symbol = "+";
			}

			//
			// For some reason '+1' is coming out simply as '1', so need to tack on the
			// '+' again
			//
			protectionValue += (int) getTotalBonusTo("DR", symbol + damageType);

			if (DR.length() > 0)
			{
				DR.append(';');
			}

			DR.append(protectionValue).append('/').append(symbol).append(damageType);
		}

		return DR.toString();
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

		for (Iterator i = companionModList.iterator(); i.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) i.next();
			SR = Math.max(SR, cMod.getSR(this));
		}

		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			SR = Math.max(SR, aClass.getSR(this));
		}

		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			final Ability aFeat = (Ability) i.next();
			SR = Math.max(SR, aFeat.getSR(this));
		}

		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			SR = Math.max(SR, aSkill.getSR(this));
		}

		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();

			if (aCD.getDomain() != null)
			{
				SR = Math.max(aCD.getDomain().getSR(this), SR);
			}
		}

		if (includeEquipment)
		{
			for (Iterator e = equipmentList.iterator(); e.hasNext();)
			{
				final Equipment eq = (Equipment) e.next();

				if (eq.isEquipped())
				{
					SR = Math.max(SR, eq.getSR(this));

					List aList = eq.getEqModifierList(true);

					if (!aList.isEmpty())
					{
						for (Iterator e2 = aList.iterator(); e2.hasNext();)
						{
							final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
							SR = Math.max(SR, eqMod.getSR(this));
						}
					}

					aList = eq.getEqModifierList(false);

					if (!aList.isEmpty())
					{
						for (Iterator e2 = aList.iterator(); e2.hasNext();)
						{
							final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
							SR = Math.max(SR, eqMod.getSR(this));
						}
					}
				}
			}
		}

		final int atl = getTotalLevels();
		final int thd = totalHitDice();

		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			SR = Math.max(SR, aTemplate.getSR(atl, thd, this));
		}

		SR += (int) getTotalBonusTo("MISC", "SR");

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
	 * Method will go through the list of classes that the PC has
	 * and see if they can cast spells of desired type at desired
	 * <b>spell level</b>.
	 *
	 * @param spellType    Spell type to check for
	 * @param spellLevel   Desired spell level
	 * @param minNumSpells Minimum number of spells at the desired spell level
	 * @return boolean
	 *         <p/>
	 *         author David Wilson <eldiosyeldiablo@users.sourceforge.net>
	 */
	public boolean canCastSpellTypeLevel(final String spellType, final int spellLevel, final int minNumSpells)
	{
		final Iterator iter = classList.iterator();

		while (iter.hasNext())
		{
			final PCClass aClass = (PCClass) iter.next();

			// Check for Constants.s_NONE just incase
			// a programmer sends in a "" string
			if (aClass.getSpellType().equalsIgnoreCase(spellType)
				&& !aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE))
			{
				// Get the number of known spells for the level
				final int classLevel = aClass.getLevel();
				int knownForLevel = aClass.getKnownForLevel(classLevel, spellLevel, this);
				knownForLevel += aClass.getSpecialtyKnownForLevel(classLevel, spellLevel, this);
				if (knownForLevel >= minNumSpells)
				{
					return true;
				}

				// See if the character can cast
				// at the required spell level
				if (aClass.getCastForLevel(classLevel, spellLevel, this) >= minNumSpells)
				{
					return true;
				}

				// If they don't memorize spells and don't have
				// a CastList then they use something funky
				// like Power Points (psionic)
				if (!aClass.getMemorizeSpells() && aClass.getKnownList().isEmpty() && aClass.zeroCastSpells())
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
			// deity = null;  Removed 11/3/2002 - dhibbs
			return false;
		}

		return aDeity.canBeSelectedBy(classList, alignment,this);
	}

	public int classAC()
	{
		return calcACOfType("ClassDefense");
	}

	/**
	 * Return value indicates whether or not a spell was deleted.
	 * @param si
	 * @param aClass
	 * @param bookName
	 * @return String
	 */
	public String delSpell(SpellInfo si, final PCClass aClass, final String bookName)
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

		final boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());

		// yes, you can remove spells from the default spellbook,
		// but they will just get added back in when the character
		// is re-loaded. But, allow them to do it anyway, just in case
		// there is some wierd spell that keeps getting loaded by
		// accident (or is saved in the .pcg file)
		if (isDefault && aClass.isAutoKnownSpell(acs.getSpell().getName(), si.getActualLevel(), this))
		{
			Logging.errorPrint("Notice: removing " + acs.getSpell().getName()
				+ " even though it is an auto known spell");
		}

		SpellBook spellBook = getSpellBookByName(bookName);
		if (spellBook.getType() == SpellBook.TYPE_SPELL_BOOK)
		{
			spellBook.setNumPagesUsed(spellBook.getNumPagesUsed()
				- si.getNumPages());
			spellBook.setNumSpells(spellBook.getNumSpells() - 1);
		}
		si.setTimes(si.getTimes() - 1);

		if (si.getTimes() <= 0)
		{
			acs.removeSpellInfo(si);
		}

		si = acs.getSpellInfoFor("", -1, -1, null);

		if (si == null)
		{
			aClass.getSpellSupport().removeCharacterSpell(acs);
		}

		return "";
	}

	public Iterator aggregateFeatListIterator()
	{
		return aggregateFeatList().iterator();
	}

	public List aggregateFeatList()
	{
		final List aggregate = getStableAggregateFeatList();

		//Did we get a valid list? If so, return it.
		if (aggregate != null)
		{
			return aggregate;
		}

		return rebuildFeatAggreagateList();
	}

	private List rebuildFeatAggreagateList()
	{
		List aggregate = new ArrayList();
		final Map aHashMap = new HashMap();

		for (Iterator e = getRealFeatsIterator(); e.hasNext();)
		{
			final Ability aFeat = (Ability) e.next();

			if (aFeat != null)
			{
				aHashMap.put(aFeat.getKeyName(), aFeat);
			}
		}

		for (Iterator e = getVirtualFeatList().iterator(); e.hasNext();)
		{
			final Ability virtualFeat = (Ability) e.next();
			//if(PrereqHandler.passesAll(virtualFeat.getPreReqList(), this, virtualFeat)) {
				if (!aHashMap.containsKey(virtualFeat.getKeyName()))
				{
					aHashMap.put(virtualFeat.getKeyName(), virtualFeat);
				}
				else if (virtualFeat.isMultiples())
				{
					Ability aggregateFeat = (Ability) aHashMap.get(virtualFeat.getKeyName());
					aggregateFeat = (Ability) aggregateFeat.clone();

					for (int e1 = 0; e1 < virtualFeat.getAssociatedCount(); ++e1)
					{
						final String aString = virtualFeat.getAssociated(e1);

						if (aggregateFeat.isStacks() || !aggregateFeat.containsAssociated(aString))
						{
							aggregateFeat.addAssociated(aString);
						}
					}

					aHashMap.put(virtualFeat.getName(), aggregateFeat);
				}
			//}
		}

		aggregate.addAll(aHashMap.values());
		setStableAggregateFeatList(aggregate);

		for (Iterator e = featAutoList().iterator(); e.hasNext();)
		{
			final Ability autoFeat = (Ability) e.next();
			if (!aHashMap.containsKey(autoFeat.getKeyName()))
			{
				aHashMap.put(autoFeat.getName(), autoFeat);
			}

			else if (autoFeat.isMultiples())
			{
				Ability aggregateFeat = (Ability) aHashMap.get(autoFeat.getKeyName());
				aggregateFeat = (Ability) aggregateFeat.clone();

				for (int e1 = 0; e1 < autoFeat.getAssociatedCount(); ++e1)
				{
					final String aString = autoFeat.getAssociated(e1);
					if (aggregateFeat.isStacks() || !aggregateFeat.containsAssociated(aString))
					{
						aggregateFeat.addAssociated(aString);
					}
				}

				aHashMap.put(autoFeat.getName(), aggregateFeat);
			}
		}

		aggregate = new ArrayList();
		aggregate.addAll(aHashMap.values());
		setStableAggregateFeatList(aggregate);
		return aggregate;
	}

	public List aggregateVisibleFeatList()
	{
		final List tempFeatList = new ArrayList();

		for (Iterator e1 = aggregateFeatList().iterator(); e1.hasNext();)
		{
			final Ability aFeat = (Ability) e1.next();

			if ((aFeat.getVisible() == Ability.VISIBILITY_DEFAULT) || (aFeat.getVisible() == Ability.VISIBILITY_OUTPUT_ONLY))
			{
				tempFeatList.add(aFeat);
			}
		}

		return tempFeatList;
	}

	/**
	 * Calculate different kinds of boni to saves.
	 * possible tokens are
	 * save
	 * save.TOTAL
	 * save.BASE
	 * save.MISC
	 * save.list
	 * save.TOTAL.list
	 * save.BASE.list
	 * save.MISC.list
	 * where
	 * save    := "CHECK1"|"CHECK2"|"CHECK3"
	 * list    := ((include|exclude)del)*(include|exclude)
	 * include := "FEATS"|"MAGIC"|"RACE"
	 * exclude := "NOFEATS"|"NOMAGIC"|"NORACE"|"NOSTAT"
	 * del     := "."
	 * given as regular expression
	 * <p/>
	 * "include"-s will add the appropriate modifier
	 * "exclude"-s will subtract the appropriate modifier
	 * <p/>
	 * (This means save.MAGIC.NOMAGIC equals 0
	 * whereas save.RACE.RACE equals 2 times the racial bonus)
	 * <p/>
	 * If you use unrecognized terminals, their value will amount to 0
	 * This means save.BLABLA equals 0
	 * whereas save.MAGIC.BLABLA equals save.MAGIC
	 * <p/>
	 * <br>author: Thomas Behr 09-03-02
	 *
	 * @param saveIndex   See the appropriate gamemode file
	 * @param saveType    "CHECK1", "CHECK2", or "CHECK3";
	 *                    may not differ from saveIndex!
	 * @param tokenString tokenString to parse
	 * @return the calculated save bonus
	 */
	public int calculateSaveBonus(final int saveIndex, final String saveType, final String tokenString)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenString, ".");
		final String[] tokens = new String[aTok.countTokens()];
		final int checkIndex = SettingsHandler.getGame().getIndexOfCheck(saveType) + 1;
		int save = 0;

		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			tokens[i] = aTok.nextToken();

			if ("TOTAL".equals(tokens[i]))
			{
				save += (int) getBonus(checkIndex, true);
			}
			else if ("BASE".equals(tokens[i]))
			{
				save += (int) getBonus(checkIndex, false);
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
			 **/
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
	 * @param aName
	 * @return true or false
	 */
	public boolean delSpellBook(final String aName)
	{
		if ((aName.length() > 0)
			&& !aName.equals(Globals.getDefaultSpellBook())
			&& spellBooks.contains(aName))
		{
			return delSpellBook((SpellBook) spellBookMap.get(aName));
		}

		return false;
	}

	/**
	 * return value indicates whether or not a book was actually removed
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

				for (Iterator i = classList.iterator(); i.hasNext();)
				{
					final PCClass aClass = (PCClass) i.next();
					final List aList = aClass.getSpellSupport()
						.getCharacterSpell(null, aName, -1);

					for (int j = aList.size() - 1; j >= 0; --j)
					{
						final CharacterSpell cs = (CharacterSpell) aList.get(j);
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

		final List unequippedPrimary = new ArrayList();
		final List unequippedSecondary = new ArrayList();

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (!eq.isWeapon() || (eq.getSlots(this) < 1))
			{
				continue;
			}

			final boolean isEquipped = eq.isEquipped();

			if ((eq.getLocation() == Equipment.EQUIPPED_PRIMARY)
				|| ((eq.getLocation() == Equipment.EQUIPPED_BOTH) && primaryWeapons.isEmpty())
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
			else if ((eq.getLocation() == Equipment.EQUIPPED_BOTH) && !primaryWeapons.isEmpty())
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

	public Iterator featAutoListIterator()
	{
		return featAutoList().iterator();
	}

	public List featAutoList()
	{
		final List autoFeatList = getStableAutomaticFeatList();

		//Did we get a valid list? If so, return it.
		if (autoFeatList != null)
		{
			return autoFeatList;
		}

		return rebuildFeatAutoList();
	}

	/**
	 * @return List
	 */
	public List rebuildFeatAutoList() {
		final List autoFeatList;
		autoFeatList = new ArrayList();

		//
		// add racial feats
		//
		if ((race != null) && !PlayerCharacterUtilities.canReassignRacialFeats())
		{
			final StringTokenizer aTok = new StringTokenizer(race.getFeatList(this), "|");

			while (aTok.hasMoreTokens())
			{
				PlayerCharacterUtilities.addToFeatList(autoFeatList, aTok.nextToken());
			}
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			for (Iterator e1 = aClass.getFeatAutos().iterator(); e1.hasNext();)
			{
				//
				// PCClass object have auto feats stored in format:
				// lvl|feat_name
				//
				final String aString = (String) e1.next();

				if (aString.indexOf('|') < 1)
				{
					continue;
				}

				final StringTokenizer aTok = new StringTokenizer(aString, "|");
				int i;

				try
				{
					i = Integer.parseInt(aTok.nextToken());
				}
				catch (NumberFormatException exc)
				{
					i = 9999; //TODO: Replace magic value with an appropriate constant. Constants.INVALID_LEVEL perhaps?
				}

				if (i > aClass.getLevel())
				{
					continue;
				}

				String autoFeat = aTok.nextToken();
				final int idx = autoFeat.indexOf('[');

				if (idx >= 0)
				{
					final StringTokenizer bTok = new StringTokenizer(autoFeat.substring(idx + 1), "[]");
					final List preReqList = new ArrayList();

					while (bTok.hasMoreTokens())
					{
						final String prereqString = bTok.nextToken();
						Logging.debugPrint("Why is the prerequisite '"+prereqString+"' parsed in PlayerCharacter.featAutoList() rather than the persistence layer");
						try {
							final PreParserFactory factory = PreParserFactory.getInstance();
							final Prerequisite prereq = factory.parse(prereqString);
							preReqList.add(prereq);
						}
						catch (PersistenceLayerException ple){
							Logging.errorPrint(ple.getMessage(), ple);
						}
					}

					autoFeat = autoFeat.substring(0, idx);

					if (preReqList.size() != 0)
					{
						//
						// To avoid possible infinite loop
						//
						if (!isAutomaticFeatsStable())
						{
							setStableAutomaticFeatList(autoFeatList);
						}

						if (! PrereqHandler.passesAll(preReqList, this, null ))
						{
							continue;
						}
					}
				}

				PlayerCharacterUtilities.addToFeatList(autoFeatList, autoFeat);
			}
		}

		if (!PlayerCharacterUtilities.canReassignTemplateFeats() && !templateList.isEmpty())
		{
			for (Iterator e = templateList.iterator(); e.hasNext();)
			{
				setStableAutomaticFeatList(autoFeatList);
				final PCTemplate aTemplate = (PCTemplate) e.next();
				final List templateFeats = aTemplate.feats(getTotalLevels(), totalHitDice(), this, false);

				if (!templateFeats.isEmpty())
				{
					for (Iterator e2 = templateFeats.iterator(); e2.hasNext();)
					{
						final String aString = (String) e2.next();
						final StringTokenizer aTok = new StringTokenizer(aString, ",");

						while (aTok.hasMoreTokens())
						{
							PlayerCharacterUtilities.addToFeatList(autoFeatList, aTok.nextToken());
						}
					}
				}
			}
		}

		if (!characterDomainList.isEmpty())
		{
			for (Iterator e = characterDomainList.iterator(); e.hasNext();)
			{
				final CharacterDomain aCD = (CharacterDomain) e.next();
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
								PlayerCharacterUtilities.addToFeatList(autoFeatList, aString.substring(idx + 1));
							}
							else
							{
								Logging.errorPrint("no '?' in Domain assocatedList entry: " + aString);
							}
						}
					}

					final Iterator anIt = aDomain.getFeatIterator();

					for (; anIt.hasNext();)
					{
						final AbilityInfo abI = (AbilityInfo) anIt.next();
						PlayerCharacterUtilities.addToFeatList(autoFeatList, abI.getKeyName());
					}
				}
			}
		}

		//
		// Need to save current as stable as getAutoWeaponProfs() needs it
		//
		setStableAutomaticFeatList(autoFeatList);
		getAutoWeaponProfs(autoFeatList);
		setStableAutomaticFeatList(autoFeatList);
		return autoFeatList;
	}

	public int flatfootedAC()
	{
		return calcACOfType("Flatfooted");
	}

	/**
	 * Checks for existance of source in domainSourceMap
	 * @param aType
	 * @param aName
	 * @param aLevel
	 * @return TRUE if it has a domain source
	 */
	public boolean hasDomainSource(final String aType, final String aName, final int aLevel)
	{
		final String aKey = aType + "|" + aName + "|" + aLevel;

		return domainSourceMap.containsKey(aKey);
	}

	/**
	 * Check if the character has the feat 'automatically'
	 *
	 * @param featName String name of the feat to check for.
	 * @return <code>true</code> if the character has the feat,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasFeatAutomatic(final String featName)
	{
		return AbilityUtilities.getFeatNamedInList(featAutoList(), featName) != null;
	}

	/**
	 * Check if the character has the feat 'virtually'
	 *
	 * @param featName String name of the feat to check for.
	 * @return <code>true</code> if the character has the feat,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasFeatVirtual(final String featName)
	{
		return AbilityUtilities.getFeatNamedInList(getVirtualFeatList(), featName) != null;
	}

	public boolean hasMadeKitSelectionForAgeSet(final int index)
	{
		return ((index >= 0) && (index < 10) && ageSetKitSelections[index]);
	}

	public boolean hasSpecialAbility(final String abilityName)
	{
		for (Iterator e = getSpecialAbilityList().iterator(); e.hasNext();)
		{
			if (((SpecialAbility) e.next()).getName().equalsIgnoreCase(abilityName))
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
			for(;;)
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
				lookupString = pcgen.io.ExportHandler.getTokenString(this, lookupString);
				aString = aString.substring(0, startIdx) + lookupString + aString.substring(endIdx + 2);
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

			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				final PCClass aClass = (PCClass) e.next();
				total += aClass.hitPoints((int) iConMod);
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

		final String copyMasterHP = replaceMasterString(getCopyMasterHP(), masterHP);
		masterHP = getVariableValue(copyMasterHP, "").intValue();

		return masterHP;
	}

	/**
	 * Check to see if this PC should ignore Encumberance
	 * for a specified armor (Constants.HEAVY_LOAD, etc)
	 * If the check is more than the testing type, return true
	 * @param armorInt
	 * @return true or false
	 */
	public boolean ignoreEncumberedArmorMove(final int armorInt)
	{
		// Try all possible POBjects
		for (Iterator i = getPObjectList().iterator(); i.hasNext();)
		{
			final PObject aPObj = (PObject) i.next();

			if (aPObj != null)
			{
				final int encMove = aPObj.getEncumberedArmorMove();

				if (armorInt <= encMove)
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Check to see if this PC should ignore Encumberance
	 * for a specified load (Constants.HEAVY_LOAD, etc)
	 * If the check is more than the testing type, return true
	 * @param loadInt
	 * @return true or false
	 */
	public boolean ignoreEncumberedLoadMove(final int loadInt)
	{
		// Try all possible POBjects
		for (Iterator i = getPObjectList().iterator(); i.hasNext();)
		{
			final PObject aPObj = (PObject) i.next();

			if (aPObj != null)
			{
				final int encMove = aPObj.getEncumberedLoadMove();

				if (loadInt <= encMove)
				{
					return true;
				}
			}
		}

		return false;
	}

	public void incrementClassLevel(final int mod, final PCClass aClass)
	{
		incrementClassLevel(mod, aClass, false);
		setDirty(true);
	}

	/**
	 * TODO Why are we doing this, just add the domain
	 * @return index
	 */
	public int indexOfFirstEmptyCharacterDomain()
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);

			if (aCD.getDomain() == null)
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * Initiative Modifier
	 * @return initiative modifier
	 */
	public int initiativeMod()
	{
		final int initmod = (int) getTotalBonusTo("COMBAT", "Initiative") + getVariableValue("INITCOMP", "").intValue();

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
			for (Iterator a = getSkillList().iterator(); a.hasNext();)
			{
				final Skill aSkill = (Skill) a.next();

				if (aSkill.getChoiceString().indexOf("Language") >= 0)
				{
					i += aSkill.getTotalRank(this).intValue();
				}
			}
		}

		if (pcRace != null)
		{
			i += (pcRace.getLangNum() + (int) getTotalBonusTo("LANGUAGES", "NUMBER"));
		}

		//
		// Check all classes for ADD:LANGUAGE
		//
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			final int classLevel = aClass.getLevel();
			final List levelAbilityList = aClass.getLevelAbilityList();

			if (levelAbilityList != null)
			{
				for (int x = levelAbilityList.size() - 1; x >= 0; --x)
				{
					final LevelAbility la = (LevelAbility) levelAbilityList.get(x);

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
	 * @param prefix
	 * @return String
	 */
	public String listBonusesFor(final String prefix)
	{
		final StringBuffer buf = new StringBuffer();
		final List aList = new ArrayList();

		for (Iterator i = getActiveBonusMap().keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();

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
		if (!aList.isEmpty())
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final String replaceKey = (String) i.next();

				if (replaceKey.length() > 7)
				{
					final String aKey = replaceKey.substring(0, replaceKey.length() - 8);
					final double replaceBonus = getActiveBonusForMapKey(replaceKey, 0);
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

								//final BufferedReader descriptionReader = new BufferedReader(new FileReader(descriptionFile));
								descriptionReader = new BufferedReader(new InputStreamReader(
											new FileInputStream(descriptionFile), "UTF-8"));

								final int length = (int) descriptionFile.length();
								inputLine = new char[length];
								descriptionReader.read(inputLine, 0, length);
								setDescriptionLst( getDescriptionLst() + new String(inputLine) );
							}
						}
						catch (IOException exception)
						{
							Logging.errorPrint("IOException in PlayerCharacter.loadDescriptionFilesInDirectory",
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
									Logging.errorPrint("Couldn't close descriptionReader in PlayerCharacter.loadDescriptionFilesInDirectory",
										e);

									//Not much to do...
								}
							}
						}
					}
					else if (parentDir.isDirectory())
					{
						loadDescriptionFilesInDirectory(parentDir.getPath() + File.separator + fileName);
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
			PCClass bClass = getClassNamed(exClass);

			if (bClass == null)
			{
				bClass = Globals.getClassNamed(exClass);

				if (bClass == null)
				{
					return;
				}

				bClass = (PCClass) bClass.clone();

				rebuildLists(bClass, aClass, aClass.getLevel(), this);

				bClass.setLevel(aClass.getLevel(), this);
				bClass.setHitPointMap(aClass.getHitPointMap());

				final int idx = classList.indexOf(aClass);
				classList.set(idx, bClass);
			}
			else
			{
				rebuildLists(bClass, aClass, aClass.getLevel(), this);
				bClass.setLevel(bClass.getLevel() + aClass.getLevel(), this);

				for (int i = 0; i < aClass.getLevel(); ++i)
				{
					bClass.setHitPoint(bClass.getLevel() + i + 1, aClass.getHitPoint(i + 1));
				}

				classList.remove(aClass);
			}

			//
			// change all the levelling info to the ex-class as well
			//
			for (int idx = pcLevelInfo.size() - 1; idx >= 0; --idx)
			{
				final PCLevelInfo li = (PCLevelInfo) pcLevelInfo.get(idx);

				if (li.getClassKeyName().equals(aClass.getKeyName()))
				{
					li.setClassKeyName(bClass.getKeyName());
				}
			}

			//
			// Find all skills associated with old class and link them to new class
			//
			for (Iterator e = getSkillList().iterator(); e.hasNext();)
			{
				final Skill aSkill = (Skill) e.next();
				aSkill.replaceClassRank(aClass.getName(), exClass);
			}

			bClass.setSkillPool(aClass.getSkillPool(this));
		}
		catch (NumberFormatException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.INFORMATION);
		}
	}

	public int minXPForECL()
	{
		return PlayerCharacterUtilities.minXPForLevel(getECL(),this);
	}

	public int minXPForNextECL()
	{
		return PlayerCharacterUtilities.minXPForLevel(getECL() + 1,this);
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
		 * Equipped some armor that we're not proficient in?
		 * acCheck penalty to attack rolls
		 */
		for (Iterator e = getEquipmentOfType("Armor", 1).iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if ((eq != null) && (!isProficientWith(eq)))
			{
				bonus += eq.acCheck(this).intValue();
			}
		}

		/*
		 * Equipped a shield that we're not proficient in?
		 * acCheck penalty to attack rolls
		 */
		for (Iterator e = getEquipmentOfType("Shield", 1).iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if ((eq != null) && (!isProficientWithShield(eq)))
			{
				bonus += eq.acCheck(this).intValue();
			}
		}

		return bonus;
	}

	/**
	 * Figure out if Load should affect AC and Skills, if so, set the load
	 * appropiately, otherwise set a light load to eliminate the effects of
	 * heavier loads
	 * @return a loadType appropriate for this Pc
	 */
	private int getLoadType()
	{
		if (Globals.checkRule(RuleConstants.SYS_LDPACSK))
		{
			final int loadScore = getVariableValue("LOADSCORE", "").intValue();
			return Globals.loadTypeForLoadScore(loadScore, totalWeight(), this);
		}
		return Constants.LIGHT_LOAD;
	}

	/**
	 *  Calculate the AC bonus from equipped items.  Extracted from
	 * modToFromEquipment.
	 * @return PC's AC bonus from equipment
	 */
	private int modToACFromEquipment ()
	{
		int bonus = 0;
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped()) {
				bonus += eq.getACMod(this).intValue();
			}
		}
		return bonus;
	}

	/**
	 * Calculate the ACCHECK bonus from equipped items.  Extracted from
	 * modToFromEquipment.
	 *
	 * @return PC's ACCHECK bonus from equipment
	 */
	private int modToACCHECKFromEquipment ()
	{
		int load  = getLoadType();
		int bonus = 0;

		int penaltyForLoad = 	(Constants.MEDIUM_LOAD == load) ? -3 :
					(Constants.HEAVY_LOAD  == load) ? -6 : 0;

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped()) {
				bonus += eq.acCheck(this).intValue();
			}
		}

		bonus = Math.min(bonus, penaltyForLoad);
		bonus += (int) getTotalBonusTo("MISC", "ACCHECK");
		return bonus;
	}

	/**
	 * Calculate the SpellFailure bonus from equipped items.  Extracted
	 * from modToFromEquipment.
	 *
	 * @return PC's SpellFailure bonus from equipment
	 */
	private int modToSpellFailureFromEquipment ()
	{
		int bonus = 0;
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped()) {
				bonus += eq.spellFailure(this).intValue();
			}
		}
		bonus += (int) getTotalBonusTo("MISC", "SPELLFAILURE");
		return bonus;
	}

	/**
	 * Calculate the MAXDEX bonus taking account of equipped items.
	 * Extracted from modToFromEquipment.
	 *
	 * @return MAXDEX bonus
	 */
	private int modToMaxDexFromEquipment ()
	{
		final int statBonus = 	(int) getStatBonusTo("MISC", "MAXDEX");
		final int load      = 	getLoadType();
		int bonus           = 	(load == Constants.MEDIUM_LOAD) ? 3 :
					 (load == Constants.HEAVY_LOAD)  ? 1 :
					 (load == Constants.OVER_LOAD)   ? 0 : statBonus;

		// If this is still true after all the equipment has been
		// examined, then we should use the Maximum - Maxium Dex modifier.
		boolean useMax = (load == Constants.LIGHT_LOAD);

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped()) {
				final int potentialMax = eq.getMaxDex(this).intValue();
				if (potentialMax != Constants.MAX_MAXDEX) {
					if (useMax || bonus > potentialMax) {
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
	 * Figure the:
	 *  MAXDEX
	 *  ACCHECK
	 *  SPELLFAILURE
	 *  AC
	 * bonus from all currently equiped items
	 */
	public int modToFromEquipment(final String typeName)
	{
		if      (typeName.equals("AC")) 		{ return modToACFromEquipment(); }
		else if (typeName.equals("ACCHECK")) 		{ return modToACCHECKFromEquipment(); }
		else if (typeName.equals("MAXDEX")) 		{ return modToMaxDexFromEquipment(); }
		else if (typeName.equals("SPELLFAILURE")) 	{ return modToSpellFailureFromEquipment(); }
		else						{ return 0; }
	}

	/**
	 * get the base MOVE: plus any bonuses from BONUS:MOVE additions
	 * takes into account Armor restrictions to movement and load carried
	 * @param moveIdx
	 * @return movement
	 */
	public double movement(final int moveIdx)
	{
		// get base movement
		double moveInFeet = getMovement(moveIdx).doubleValue();

		// First get the MOVEADD bonus
		moveInFeet += getTotalBonusTo("MOVEADD", "TYPE." + getMovementType(moveIdx).toUpperCase());

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
		double moveMult = getTotalBonusTo("MOVEMULT", "TYPE." + getMovementType(moveIdx).toUpperCase());

		// also check for special case of TYPE=ALL
		moveMult += getTotalBonusTo("MOVEMULT", "TYPE.ALL");

		if (moveMult > 0)
		{
			calcMove = (int) (calcMove * moveMult);
		}

		double postMove = moveInFeet;

		// now add on any POSTMOVE bonuses
		postMove += getTotalBonusTo("POSTMOVEADD", "TYPE." + getMovementType(moveIdx).toUpperCase());

		// also check for special case of TYPE=ALL
		postMove += getTotalBonusTo("POSTMOVEADD", "TYPE.ALL");

		// because POSTMOVE is magical movement which should not be
		// multiplied by magial items, etc, we now see which is larger,
		// (baseMove + postMove)  or  (baseMove * moveMultiplier)
		// and keep the larger one, discarding the other
		moveInFeet = Math.max(calcMove, postMove);

		// get a list of all equipped Armor
		final List aList = getEquipmentOfType("Armor", 1);
		int armorLoad = Constants.LIGHT_LOAD;

		if (aList.size() > 0)
		{
			for (Iterator a = aList.iterator(); a.hasNext();)
			{
				final Equipment armor = (Equipment) a.next();

				if (armor.isShield())
					continue;
				if (armor.isHeavy() && !ignoreEncumberedArmorMove(Constants.HEAVY_LOAD))
				{
					armorLoad = Math.max(armorLoad, Constants.HEAVY_LOAD);
				}
				else if (armor.isMedium() && !ignoreEncumberedArmorMove(Constants.MEDIUM_LOAD))
				{
					armorLoad = Math.max(armorLoad, Constants.MEDIUM_LOAD);
				}
			}
		}

		final double armorMove = Globals.calcEncumberedMove(armorLoad, moveInFeet, true, null);

		final int pcLoad = Globals.loadTypeForLoadScore(getVariableValue("LOADSCORE", "").intValue(), totalWeight(), this);
		final double loadMove = Globals.calcEncumberedMove(pcLoad, moveInFeet, true, this);

		// It is possible to have a PC that is not encumbered by Armor
		// But is encumbered by Weight carried (and visa-versa)
		// So do two calcs and take the slowest
		moveInFeet = Math.min(armorMove, loadMove);

		return moveInFeet;
	}

	public double multiclassXPMultiplier()
	{
		final SortedSet unfavoredClasses = new TreeSet();
		final SortedSet aList = getFavoredClasses();
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

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			if (!aList.contains(aClass.getDisplayClassName()) && (!aList.contains(aClass.toString()))
				&& aClass.hasXPPenalty())
			{
				unfavoredClasses.add(aClass.getDisplayClassName());

				if (aClass.getLevel() > maxClassLevel)
				{
					if (hasAny)
					{
						secondClassLevel = maxClassLevel;
						secondClass = maxClass;
					}

					maxClassLevel = aClass.getLevel();
					maxClass = aClass.getDisplayClassName();
				}
				else if ((aClass.getLevel() > secondClassLevel) && (hasAny))
				{
					secondClassLevel = aClass.getLevel();
					secondClass = aClass.getDisplayClassName();
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

			for (Iterator e = unfavoredClasses.iterator(); e.hasNext();)
			{
				final PCClass aClass = getClassDisplayNamed((String) e.next());

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
	 * Takes a String and a Class name and computes spell based
	 * variable such as Class level
	 * @param aSpell
	 * @param aString
	 * @param anObj
	 * @return String
	 */
	public String parseSpellString(final Spell aSpell, String aString, final PObject anObj)
	{
		String aSpellClass = null;

		if (anObj instanceof Domain)
		{
			final CharacterDomain aCD = getCharacterDomainForDomain(anObj.getName());

			if ((aCD != null) && aCD.isFromPCClass())
			{
				aSpellClass = "CLASS:" + getClassNamed(aCD.getObjectName());
			}
		}
		else if (anObj instanceof PCClass)
		{
			aSpellClass = "CLASS:" + anObj.getName();
		}
		else if (anObj instanceof Race) //could be innate spell for race
		{
			aSpellClass = "RACE:" + anObj.getName();
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
					if(level == 0) {
						end = i;
						break;
					}
				}
			}

			/*int x = CoreUtility.innerMostStringStart(aString);
			int y = CoreUtility.innerMostStringEnd(aString);
			// bounds checking
			if ((start > end) || (start >= aString.length()))
			{
				break;
			}
			if ((end <= 0) || (end >= aString.length()))
			{
				break;
			}*/
			final String inCalc = aString.substring(start + 1, end);

			String replacement = "0";

			final Float fVal = getVariableValue(aSpell, inCalc, aSpellClass);
			if (!CoreUtility.doublesEqual(fVal.floatValue(), 0.0f))
			{
				found = true;
				replacement = fVal.intValue() + "";
			}
			else if ((inCalc.indexOf("MIN") >= 0) || (inCalc.indexOf("MAX") >= 0))
			{
				found = true;
				replacement = fVal.intValue() + "";
			}
			if (found)
			{
				aString = aString.substring(0, start) + replacement + aString.substring(end + 1);
			}
			else
			{
				aString = aString.substring(0, start) + "[" + inCalc + "]" + aString.substring(end + 1);
			}
		}

		return aString;
	}

	/**
	 * Populate the characters skills list with skill that the character does
	 * not have ranks in according to the required level. The levels are
	 * defined in constants in the Skill class, but are None, Untrained or All.
	 *
	 * @param level The level of extra skills to be added.
	 */
	public void populateSkills(final int level)
	{
		Globals.sortPObjectList(getSkillList());
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

		final List localSkillList = getSkillList();
		final SkillComparator comparator = new SkillComparator(sort, sortOrder);
		int nextOutputIndex = 1;
		Collections.sort(localSkillList, comparator);

		for (Iterator sI = localSkillList.iterator(); sI.hasNext();)
		{
			final Skill aSkill = (Skill) sI.next();

			if (aSkill.getOutputIndex() >= 0)
			{
				aSkill.setOutputIndex(nextOutputIndex++);
			}
		}
	}

	/**
	 * Removes a CharacterDomain
	 * @param aCD
	 */
	public void removeCharacterDomain(final CharacterDomain aCD)
	{
		if (!characterDomainList.isEmpty())
		{
			characterDomainList.remove(aCD);
			setDirty(true);
		}
	}

	public void removeNaturalWeapons(final PObject obj)
	{
		for (Iterator e = obj.getNaturalWeapons().iterator(); e.hasNext();)
		{
			// Need to make sure weapons are removed from
			// equip sets as well, or they will get added back
			// to the character.  sage_sam 20 March 2003
			final Equipment weapon = (Equipment) e.next();
			removeEquipment(weapon);
			delEquipSetItem(weapon);
			setDirty(true);
		}
	}

	/**
	 * Removes a "temporary" bonus
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

		if (inTmpl.getWeaponProfAutos() != null)
		{
			weaponProfList.removeAll(inTmpl.getWeaponProfAutos());
		}

		getLanguagesList().removeAll(inTmpl.getSafeListFor(ListKey.AUTO_LANGUAGES)); // remove template languages.
		templateAutoLanguages.removeAll(inTmpl.getSafeListFor(ListKey.AUTO_LANGUAGES)); // remove them from the local listing. Don't clear though in case of multiple templates.

		templateLanguages.removeAll(inTmpl.getLanguageBonus());
		removeNaturalWeapons(inTmpl);

		// It is hard to tell if removeTemplate() modifies
		// inTmpl.templatesAdded(), so not safe to optimize
		// the call to .size().	 XXX
		for (int i = 0; i < inTmpl.templatesAdded().size(); ++i)
		{
			removeTemplate(getTemplateNamed((String) inTmpl.templatesAdded().get(i)));
		}

		for (int i = 0; i < templateList.size(); ++i)
		{
			if (((PCTemplate) templateList.get(i)).getName().equals(inTmpl.getName()))
			{
				templateList.remove(i);

				break;
			}
		}

		if (!PlayerCharacterUtilities.canReassignTemplateFeats())
		{
			setAutomaticFeatsStable(false);
		}

		setQualifyListStable(false);

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

	public void saveStatIncrease(final String statAbb, final int mod, final boolean isPreMod)
	{
		final int idx = getLevelInfoSize() - 1;

		if (idx >= 0)
		{
			((PCLevelInfo) pcLevelInfo.get(idx)).addModifiedStat(statAbb, mod, isPreMod);
		}

		setDirty(true);
	}

	public int getStatIncrease(final String statAbb, final boolean includePost)
	{
		final int idx = getLevelInfoSize() - 1;

		if (idx >= 0)
		{
			return ((PCLevelInfo) pcLevelInfo.get(idx)).getTotalStatMod(statAbb, includePost);
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

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			if (!aClass.isMonster())
			{
				totalLevels += aClass.getLevel();
			}
		}

		return totalLevels;
	}

	public BigDecimal totalValue()
	{
		BigDecimal totalValue = BigDecimalHelper.ZERO;

		if (!getEquipmentMasterList().isEmpty())
		{
			for (Iterator e = getEquipmentMasterList().iterator(); e.hasNext();)
			{
				final Equipment eq = (Equipment) e.next();
				totalValue = totalValue.add(eq.getCost(this).multiply(new BigDecimal(eq.qty())));
			}
		}

		return totalValue;
	}

	public Float totalWeight()
	{
		float totalWeight = 0;
		final Float floatZero = new Float(0);
		boolean firstClothing = true;

		if (equipmentList.isEmpty())
		{
			return floatZero;
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			// Loop through the list of top
			if ((eq.getCarried().compareTo(floatZero) > 0) && (eq.getParent() == null))
			{
				if (eq.getChildCount() > 0)
				{
					totalWeight += (eq.getWeightAsDouble(this) + eq.getContainedWeight(this).floatValue());
				}
				else
				{
					if (firstClothing && eq.isEquipped() && eq.isType("CLOTHING"))
					{
						//The first equipped set of clothing should have a weight of 0. Feature #437410
						firstClothing = false;
						totalWeight += (eq.getWeightAsDouble(this) * Math.max(eq.getCarried().floatValue() - 1, 0));
					}
					else
					{
						totalWeight += (eq.getWeightAsDouble(this) * eq.getCarried().floatValue());
					}
				}
			}
		}

		return new Float(totalWeight);
	}

	public int touchAC()
	{
		return calcACOfType("Touch");
	}

	/**
	 * replaces oldItem with newItem in all EquipSets
	 * @param oldItem
	 * @param newItem
	 */
	public void updateEquipSetItem(final Equipment oldItem, final Equipment newItem)
	{
		if (equipSetList.isEmpty())
		{
			return;
		}

		final List tmpList = new ArrayList();

		// find all oldItem EquipSet's
		for (Iterator eSet = equipSetList.iterator(); eSet.hasNext();)
		{
			final EquipSet es = (EquipSet) eSet.next();
			final Equipment eqI = es.getItem();

			if ((eqI != null) && oldItem.equals(eqI))
			{
				tmpList.add(es);
			}
		}

		for (Iterator eSet = tmpList.iterator(); eSet.hasNext();)
		{
			final EquipSet es = (EquipSet) eSet.next();
			es.setValue(newItem.getName());
			es.setItem(newItem);
		}
		setDirty(true);
	}

	/**
	 * Gets whether the character has been changed since last saved.
	 * @return true or false
	 */
	public boolean wasEverSaved()
	{
		return !"".equals(getFileName());
	}

	/**
	 * Figures out if a bonus should stack based on type,
	 * then adds it to the supplied map.
	 *
	 * @param bonus The value of the bonus.
	 * @param bonusType The type of the bonus e.g. STAT.DEX:LUCK
	 * @param bonusMap The bonus map being built up.
	 */
	void setActiveBonusStack(double bonus, String bonusType, Map bonusMap)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();

			// only specific bonuses can actually be fractional
			// -> TODO should define this in external file
			if (!bonusType.startsWith("ITEMWEIGHT") && !bonusType.startsWith("ITEMCOST")
				&& !bonusType.startsWith("ACVALUE") && !bonusType.startsWith("ITEMCAPACITY")
				&& !bonusType.startsWith("LOADMULT") && (bonusType.indexOf("DAMAGEMULT") < 0))
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
		//  COMBAT.AC
		// or
		//  COMBAT.AC:Luck
		// or
		//  COMBAT.AC:Armor.REPLACE
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
				index = SettingsHandler.getGame().getUnmodifiableBonusStackList().indexOf(aString); // e.g. Dodge
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
			final String aVal = (String) bonusMap.get(bonusType);

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
		else // a stacking bonus
		{
			if (bonusType == null) // TODO: condition always true
			{
				bonusType = "";
			}

			final String aVal = (String) bonusMap.get(bonusType);

			if (aVal == null)
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus), bonusMap);
			}
			else
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus + Float.parseFloat(aVal)), bonusMap);
			}
		}
	}

	/**
	 * Returns a list of Ability Objects of the given Category from the global
	 * list, which 1) match the given abilityType, 2) the character qualifies
	 * for, and 3) the character does not already have.
	 * @param  category of ability to return
	 * @param  abilityType String type of ability to return.
	 * @param  autoQualify assume PC qualifies for feat.  Used for virtual feats
	 *
	 * @return List of Ability Objects.
	 */

	public List getAvailableAbilities(
			final String  category,
			final String  abilityType,
			final boolean autoQualify)
	{
		final List anAbilityList = new ArrayList();
		final Iterator it        = Globals.getAbilityKeyIterator(category);

		while (it.hasNext())
		{
			final Ability anAbility = (Ability) it.next();

			if (
				anAbility.matchesType(abilityType) &&
				canSelectAbility(anAbility, autoQualify))
			{
				anAbilityList.add(anAbility);
			}
		}

		return anAbilityList;
	}

	/**
	 * Returns the list of names of available feats of given type.
	 * That is, all feats from the global list, which match the
	 * given featType, the character qualifies for, and the
	 * character does not already have.
	 *
	 * @param featType String category of feat to list.
	 * @return List of Feats.
	 */
	public List getAvailableFeatNames(final String featType)
	{
		return(getAvailableFeatNames(featType, false));
	}

	/**
	 * Returns the list of names of available feats of given type.
	 * That is, all feats from the global list, which match the
	 * given featType, the character qualifies for, and the
	 * character does not already have.
	 *
	 * @param featType String category of feat to list.
	 * @param autoQualify assume PC qualifies for feat.  Used for virtual feats
	 * @return List of Feats.
	 */
	public List getAvailableFeatNames(final String featType, final boolean autoQualify)
	{
		final List anAbilityList = new ArrayList();
		final Iterator it        = Globals.getAbilityKeyIterator("FEAT");

		for (; it.hasNext(); )
		{
			final Ability anAbility = (Ability) it.next();

			if (
				anAbility.matchesType(featType) &&
				canSelectAbility(anAbility, autoQualify))
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
	 * @return the integer movement speed multiplier for Idx
	 */
	Double getMovementMult(final int moveIdx)
	{
		if ((getMovements() != null) && (moveIdx < movementMult.length))
		{
			return movementMult[moveIdx];
		}
		return new Double(0);
	}

	/*
	 * Build on-the-fly so removing templates won't mess up qualify list
	 */
	ArrayList getQualifyList()
	{
		if (!qualifyListStable)
		{
			qualifyArrayList = new ArrayList();

			// Try all possible POBjects
			for (Iterator i = getPObjectList().iterator(); i.hasNext();)
			{
				final PObject aPObj = (PObject) i.next();

				if (aPObj == null)
				{
					continue;
				}

				final String qString = aPObj.getQualifyString();
				final StringTokenizer aTok = new StringTokenizer(qString, "|");
				while (aTok.hasMoreTokens())
				{
					final String qualifier = aTok.nextToken();

					if (!qualifyArrayList.contains(qualifier))
					{
						qualifyArrayList.add(qualifier);
					}
				}
			}

			setQualifyListStable(true);
		}

		return qualifyArrayList;
	}

	void addVariable(final String variableString)
	{
		variableList.add(variableString);
		setDirty(true);
	}

	void giveClassesAway(final PCClass toClass, final PCClass fromClass, int iCount)
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
			toClass.setHitPoint(iToLevel + i, fromClass.getHitPoint(iFromLevel + i));
			fromClass.setHitPoint(iFromLevel + i, new Integer(0));
		}

		rebuildLists(toClass, fromClass, iCount, this);

		fromClass.setLevel(iFromLevel, this);

		// first, change the toClass current PCLevelInfo level
		for (Iterator li = pcLevelInfo.iterator(); li.hasNext();)
		{
			final PCLevelInfo pcl = (PCLevelInfo) li.next();

			if (pcl.getClassKeyName().equals(toClass.getKeyName()))
			{
				final int iTo = (pcl.getLevel() + toClass.getLevel()) - iToLevel;
				pcl.setLevel(iTo);
			}
		}

		// change old class PCLevelInfo to the new class
		for (Iterator li = pcLevelInfo.iterator(); li.hasNext();)
		{
			final PCLevelInfo pcl = (PCLevelInfo) li.next();

			if (pcl.getClassKeyName().equals(fromClass.getKeyName()) && (pcl.getLevel() > iFromLevel))
			{
				final int iFrom = pcl.getLevel() - iFromLevel;
				pcl.setClassKeyName(toClass.getKeyName());
				pcl.setLevel(iFrom);
			}
		}

		/*
		   // get skills associated with old class and link to new class
		   for (Iterator e = getSkillList().iterator(); e.hasNext();)
		   {
			   Skill aSkill = (Skill) e.next();
			   aSkill.replaceClassRank(fromClass.getName(), toClass.getName());
		   }
		   toClass.setSkillPool(fromClass.getSkillPool());
		 */
	}

//	boolean qualifiesForFeat(final String featName)
//	{
//		final Ability anAbility = Globals.getAbilityNamed("FEAT", featName);
//
//		if (anAbility != null)
//		{
//			return qualifiesForFeat(anAbility);
//		}
//
//		return false;
//	}

	/**
	 * return the index of CharacterDomain matching domainName
	 * else return -1
	 * @param domainName
	 * @return character domain index
	 */
	public int getCharacterDomainIndex(final String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();

			if ((aDomain != null) && aDomain.getName().equalsIgnoreCase(domainName))
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

	void addFreeLanguage(final String aString)
	{
		final Language aLang = Globals.getLanguageNamed(aString);

		if (aLang != null)
		{
			if (!getLanguagesList().contains(aLang))
			{
				getLanguagesList().add(aLang);
				++freeLangs;
				setDirty(true);
			}
		}
	}

	boolean hasSpecialAbility(final SpecialAbility sa)
	{
		final String saName = sa.getName();
		final String saDesc = sa.getSADesc();

		for (Iterator e = getSpecialAbilityList().iterator(); e.hasNext();)
		{
			final SpecialAbility saFromList = (SpecialAbility) e.next();

			if (saFromList.getName().equalsIgnoreCase(saName) && saFromList.getSADesc().equalsIgnoreCase(saDesc))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Add multiple feats from a String list separated by commas.
	 * @param playerCharacterLevelInfo
	 * @param aList
	 * @param addIt
	 * @param all
	 */
	void modFeatsFromList(final PCLevelInfo playerCharacterLevelInfo, final String aList, final boolean addIt, final boolean all)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, ",");

		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			Ability anAbility = getFeatNamed(aString);
			StringTokenizer bTok = null;

			if (anAbility != null)
			{
				continue;
			}

			// does not already have feat
			anAbility = Globals.getAbilityNamed("FEAT", aString);

			if (anAbility == null)
			{
				// could not find Feat, try trimming off contents of parenthesis
				bTok = new StringTokenizer(aString, "()", true);

				final String bString = bTok.nextToken();
				final int beginIndex = bString.length() + 1;
				final int endIndex = aString.lastIndexOf(')');

				if (beginIndex <= aString.length())
				{
					if (endIndex >= beginIndex)
					{
						bTok = new StringTokenizer(aString.substring(beginIndex, endIndex), ",");
					}
					else
					{
						bTok = new StringTokenizer(aString.substring(beginIndex), ",");
					}
				}
				else
				{
					bTok = null;
				}
				aString = bString.replace('(', ' ').replace(')', ' ').trim();
			}
			else
			{
				final Ability tempAbility = getFeatNamed(anAbility.getName());
				if (tempAbility != null)
				{
					anAbility = tempAbility;
				}
				else
				{
					// add the Feat found, as a CharacterFeat
					anAbility = (Ability) anAbility.clone();
					addFeat(anAbility, playerCharacterLevelInfo);
				}
			}

			if (anAbility == null)
			{
				// if we still haven't found it, try a different string
				if (!addIt)
				{
					return;
				}

				anAbility = Globals.getAbilityNamed("FEAT", aString);

				if (anAbility == null)
				{
					Logging.errorPrint("Feat not found in PlayerCharacter.modFeatsFromList: " + aString);

					return;
				}

				anAbility = (Ability) anAbility.clone();
				addFeat(anAbility, playerCharacterLevelInfo);
			}

			if ((bTok != null) && bTok.hasMoreTokens())
			{
				while (bTok.hasMoreTokens())
				{
					aString = bTok.nextToken();

					if ("DEITYWEAPON".equals(aString))
					{
						WeaponProf wp = null;

						if (getDeity() != null)
						{
							wp = Globals.getWeaponProfNamed(getDeity().getFavoredWeapon());
						}

						if (wp != null)
						{
							if (addIt) // TODO: condition always true
							{
								anAbility.addAssociated(wp.getName());
							}
							else
							{
								anAbility.removeAssociated(wp.getName());
							}
						}
					}
					else
					{
						if (addIt) // TODO: condition always true
						{
							anAbility.addAssociated(aString);
						}
						else
						{
							anAbility.removeAssociated(aString);
						}
					}
				}
			}
			else
			{
				if (!all && !anAbility.isMultiples())
				{
					if (addIt)
					{
//						setFeats(getFeats() + anAbility.getCost(this));
						adjustFeats(anAbility.getCost(this));
					}
					else
					{
//						setFeats(getFeats() - anAbility.getCost(this));
						adjustFeats(-anAbility.getCost(this));
					}
				}

				AbilityUtilities.modFeat(this, playerCharacterLevelInfo, aString, addIt, all);
			}

			if (anAbility.getName().endsWith("Weapon Proficiency"))
			{
				for (int e = 0; e < anAbility.getAssociatedCount(); ++e)
				{
					final String wprof = anAbility.getAssociated(e);
					final WeaponProf wp = Globals.getWeaponProfNamed(wprof);

					if (wp != null)
					{
						addWeaponProfToChosenFeats(wprof);
					}
				}
			}
		}

		setAutomaticFeatsStable(false);
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
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			final String aString = (String) e.next();

			if (aString.startsWith(variableString))
			{
				e.remove();
				setDirty(true);
			}
		}
	}

	/**
	 * Scan through the list of doains the character has to ensure that 
	 * they are all still valid. Any invalid domains will be removed from
	 * the character. 
	 */
	void validateCharacterDomains()
	{
		if (!isImporting())
		{
			getSpellList();
		}

		for (int i = characterDomainList.size() - 1; i >= 0; --i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);

			if (!aCD.isDomainValidFor(this))
			{
				characterDomainList.remove(aCD);
			}
		}
	}

	/**
	 * Searches the activeBonus HashMap for aKey
	 * @param aKey
	 * @param defaultValue
	 *
	 * @return defaultValue if aKey not found
	 */
	private double getActiveBonusForMapKey(String aKey, final double defaultValue)
	{
		aKey = aKey.toUpperCase();

		final String regVal = (String) getActiveBonusMap().get(aKey);

		if (regVal != null)
		{
			return Float.parseFloat(regVal);
		}

		return defaultValue;
	}

	/**
	 * Active BonusObj's
	 * @return List
	 */
	private List getActiveBonusList()
	{
		return activeBonusList;
	}

	private List getAutoArmorProfList()
	{
		final ArrayList aList = new ArrayList();

		// Try all possible PObjects
		for (Iterator i = getPObjectList().iterator(); i.hasNext();)
		{
			final PObject aPObj = (PObject) i.next();

			if (aPObj != null)
			{
				aPObj.addAutoTagsToList("ARMORPROF", aList, this, true);
			}
		}

		return aList;
	}

	private boolean isAutomaticFeatsStable()
	{
		return automaticFeatsStable;
	}

	/**
	 * Calculates total bonus from Checks
	 * @param aType
	 * @param aName
	 * @return check bonus to
	 */
	private double getCheckBonusTo(String aType, String aName)
	{
		double bonus = 0;
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		final List aList = SettingsHandler.getGame().getUnmodifiableCheckList();

		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			final PObject obj = (PObject) i.next();
			final List tempList = obj.getBonusListOfType(aType, aName);

			if (!tempList.isEmpty())
			{
				bonus += calcBonusFromList(tempList);
			}
		}

		return bonus;
	}

	private synchronized void setClassLevelsBrazenlyTo(final Map lvlMap)
	{
		// set class levels to classname,lvl pair
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			String lvl = (String) lvlMap.get(aClass.getName());

			if (lvl == null)
			{
				lvl = "0";
			}

			aClass.setLevelWithoutConsequence(Integer.parseInt(lvl));

		}
		// Recalculate bonuses, based on new level
		calcActiveBonuses();
		//setDirty(true);
	}

	private String getDisplayClassName()
	{
		return (classList.isEmpty() ? "Nobody" : ((PCClass) classList.get(classList.size() - 1)).getDisplayClassName());
	}

	private String getDisplayRaceName()
	{
		final String raceName = getRace().toString();

		return (raceName.equals(Constants.s_NONESELECTED) ? "Nothing" : raceName);
	}

	/**
	 * Get AUTO weapon proficiencies from all granting objects
	 * @param aFeatList
	 * @return Sorted Set
	 */
	private SortedSet getAutoWeaponProfs(final List aFeatList)
	{
		SortedSet results = new TreeSet();
		final Race aRace = getRace();

		ListKey weaponProfBonusKey = ListKey.SELECTED_WEAPON_PROF_BONUS;

		//
		// Add race-grantedweapon proficiencies
		//
		if (aRace != null)
		{
			results = addWeaponProfsLists(aRace.getWeaponProfAutos(), results, aFeatList, true);

			for (Iterator it = aRace.getSafeListFor(weaponProfBonusKey).iterator(); it.hasNext();)
			{
				final String aString = (String) it.next();
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}

			aRace.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
		}

		//
		// Add template-granted weapon proficiencies
		//
		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			results = addWeaponProfsLists(aTemplate.getWeaponProfAutos(), results, aFeatList, true);

			for (Iterator it = aTemplate.getSafeListFor(weaponProfBonusKey).iterator(); it.hasNext();)
			{
				final String aString = (String) it.next();
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}

			aTemplate.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
		}

		//
		// Add class-granted weapon proficiencies
		//
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			results = addWeaponProfsLists(aClass.getWeaponProfAutos(), results, aFeatList, true);

			for (Iterator it = aClass.getSafeListFor(weaponProfBonusKey).iterator(); it.hasNext();)
			{
				final String aString = (String) it.next();
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}

			aClass.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
		}

		//
		// Add feat-granted weapon proficiencies
		//
		setAggregateFeatsStable(false);

		for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
		{
			final Ability aFeat = (Ability) e.next();
			results = addWeaponProfsLists(aFeat.getWeaponProfAutos(), results, aFeatList, true);

			List staticProfList = new ArrayList();
			staticProfList.addAll(aFeat.getSafeListFor(weaponProfBonusKey));
			for (Iterator wpnProfIter = staticProfList.iterator(); wpnProfIter.hasNext();)
			{
				final String aString = (String) wpnProfIter.next();
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}

			aFeat.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
		}

		//
		// Add skill-granted weapon proficiencies
		//
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill) e.next();
			results = addWeaponProfsLists(aSkill.getWeaponProfAutos(), results, aFeatList, true);
			aSkill.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
		}

		//
		// Add equipment-granted weapon proficiencies
		//
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				results = addWeaponProfsLists(eq.getWeaponProfAutos(), results, aFeatList, true);
				eq.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);

				List aList = eq.getEqModifierList(true);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						results = addWeaponProfsLists(eqMod.getWeaponProfAutos(), results, aFeatList, true);
					}
				}

				aList = eq.getEqModifierList(false);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						results = addWeaponProfsLists(eqMod.getWeaponProfAutos(), results, aFeatList, true);
					}
				}
			}
		}

		//
		// Add deity-granted weapon proficiencies
		//
		if (deity != null)
		{
			results = addWeaponProfsLists(deity.getWeaponProfAutos(), results, aFeatList, true);
			deity.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
		}

		//
		// Add domain-granted weapon proficiencies
		//
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) e.next();
			final Domain aDomain = aCD.getDomain();

			if (aDomain != null)
			{
				results = addWeaponProfsLists(aDomain.getWeaponProfAutos(), results, aFeatList, true);

				for (Iterator it = aDomain.getSafeListFor(weaponProfBonusKey).iterator(); it.hasNext();)
				{
					final String aString = (String) it.next();
					results.add(aString);
					addWeaponProfToList(aFeatList, aString, true);
				}

				aDomain.addAutoTagsToList("WEAPONPROF", (TreeSet) results, this, true);
			}
		}

		//
		// Parse though aggregate feat list, looking for any feats that grant weapon proficiencies
		//
		//addFeatProfs(getStableAggregateFeatList(), aFeatList, results);
		//addFeatProfs(getStableAutomaticFeatList(), aFeatList, results);
		weaponProfList.clear(); // TheForken
		weaponProfList.addAll(results);

		return results;
	}

	/**
	 * Parses through all Equipment items and calculates total Bonus
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

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				final List tempList = eq.getBonusListOfType(aType, aName, true);

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
	 * Returns the Feat definition searching by key (not name),
	 * as contained in the specified list
	 *
	 * @param featName String key of the feat to check for.
	 * @param afeatList
	 * @return the Feat (not the CharacterFeat) searched for,
	 *         <code>null</code> if not found.
	 */
	private Ability getFeatKeyed(final String featName, final List afeatList)
	{
		if (afeatList.isEmpty())
		{
			return null;
		}

		for (Iterator e = afeatList.iterator(); e.hasNext();)
		{
			final Ability aFeat = (Ability) e.next();

			if (aFeat.getKeyName().equalsIgnoreCase(featName))
			{
				return aFeat;
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
		final Iterator it = classList.iterator();

		buf.append(((PCClass) it.next()).getFullDisplayClassName());

		while (it.hasNext())
		{
			buf.append("/").append(((PCClass) it.next()).getFullDisplayClassName());
		}

		return buf.toString();
	}

	/**
	 * Return a hashmap of the first maxCharacterLevel character levels that a character has taken
	 * This will be a hash of "Class name"=>"number of levels as a string". For example,
	 * {"Fighter"=>"2", "Cleric":"16"}
	 * 
	 * @param maxCharacterLevel the maximum character level that we can include in this map
	 * @return character level map
	 */
	private Map getCharacterLevelHashMap(final int maxCharacterLevel)
	{
		final Map lvlMap = new HashMap();

		int characterLevels=0;
		for (int i = 0; i < getLevelInfoSize(); ++i)
		{
			final String classKeyName = getLevelInfoClassKeyName(i);
			final PCClass aClass = Globals.getClassKeyed(classKeyName);

			if (aClass.isMonster() || characterLevels<maxCharacterLevel) {
				// we can use this class level if it is a monster level, or if
				// we have not yet hit our maximum number of characterLevels
				String val = (String) lvlMap.get(classKeyName);
				if (val == null)
				{
					val = "0";
				}

				val = String.valueOf(Integer.parseInt(val) + 1);
				lvlMap.put(classKeyName, val);
			}

			if (!aClass.isMonster()) {
				// If the class level was not a monster level then it counts
				// towards the total number of chracter levels
				characterLevels++;
			}
		}

		return lvlMap;
	}


	private void setMoveFromList(final List aList)
	{
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			final Object anObj = e.next();

			if (!(anObj instanceof PObject))
			{
				continue;
			}

			final PObject pObj = (PObject) anObj;
			final Movement movement = pObj.getMovement();

			if (movement == null || movement.getNumberOfMovements() < 1)
			{
				continue;
			}

			for (int i = 0; i < movement.getNumberOfMovements(); i++)
			{
				setMyMoveRates(movement.getMovementType(i), movement
						.getMovement(i).doubleValue(), movement
						.getMovementMult(i), movement.getMovementMultOp(i),
						movement.getMoveRatesFlag());
			}
		}
		//setDirty(true);
	}

	/**
	 * an array of movement speeds
	 *
	 * @return    array of Integer movement speeds
	 */
	public Double[] getMovements()
	{
		return movements;
	}

	/**
	 * sets up the movement arrays
	 * creates them if they do not exist
	 * @param moveType
	 * @param anDouble
	 * @param moveMult
	 * @param multOp
	 * @param moveFlag
	 */
	private void setMyMoveRates(final String moveType, final double anDouble, final Double moveMult, final String multOp, final int moveFlag)
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
	private double getPObjectWithCostBonusTo(final List aList, final String aType, final String aName, final boolean subSearch)
	{
		double iBonus = 0;

		if (aList.isEmpty())
		{
			return iBonus;
		}

		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			final PObject anObj = (PObject) e.next();
			final List tempList = anObj.getBonusListOfType(aType, aName);
			iBonus += calcBonusWithCostFromList(tempList, subSearch);
		}

		return iBonus;
	}

	private boolean isProficientWithArmor(final Equipment eq)
	{
		final ArrayList aList = getArmorProfList();

		// First, check to see if fits into any TYPE granted
		for (int i = 0; i < aList.size(); ++i)
		{
			final String aString = aList.get(i).toString();

			if ((aString.startsWith("TYPE=") || aString.startsWith("TYPE.")) && eq.isType(aString.substring(5)))
			{
				return true;
			}
		}

		return aList.contains(eq.profName(this));
	}

	private boolean isProficientWithShield(final Equipment eq)
	{
		final ArrayList aList = getShieldProfList();

		// First, check to see if fits into any TYPE granted
		for (int i = 0; i < aList.size(); ++i)
		{
			final String aString = aList.get(i).toString();

			if (!aString.startsWith("TYPE=") && !aString.startsWith("TYPE."))
			{
				break;
			}

			if (eq.isType(aString.substring(5)))
			{
				return true;
			}
		}

		return aList.contains(eq.profName(this));
	}

	private boolean isProficientWithWeapon(final Equipment eq)
	{
		final WeaponProf wp1 = Globals.getWeaponProfNamed(eq.profName(1, this));
		final WeaponProf wp2 = Globals.getWeaponProfNamed(eq.profName(2, this));

		if ((wp1 == null) || (wp2 == null))
		{
			return false;
		}

		if (eq.isNatural())
		{
			return true;
		}

		if (hasWeaponProfNamed(wp1.getName()) || hasWeaponProfNamed(wp2.getName()))
		{
			return true;
		}

		return false;
	}

	private void setQualifyListStable(final boolean state)
	{
		qualifyListStable = state;
		//setDirty(true);
	}

	private SortedSet getRacialFavoredClasses()
	{
		String rfc = getRace().getFavoredClass();

		if (rfc.startsWith("CHOOSE:"))
		{
			for (;;)
			{
				final String classChoice = Globals.chooseFromList("Select favored class", rfc.substring(7),
						null, 1);

				if (classChoice != null)
				{
					rfc = classChoice;

					break;
				}
			}
		}

		if (addFavoredClass(rfc))
		{
			setStringFor(StringKey.RACIAL_FAVORED_CLASS, rfc);
		} else {
			removeStringFor(StringKey.RACIAL_FAVORED_CLASS);
		}

		return favoredClasses;
	}

	private ArrayList getSelectedArmorProfList()
	{
		final ArrayList aList = new ArrayList();

		// Try all possible PObjects
		for (Iterator i = getPObjectList().iterator(); i.hasNext();)
		{
			final PObject aPObj = (PObject) i.next();

			if (aPObj == null)
			{
				continue;
			}

			List l = aPObj.getListFor(ListKey.SELECTED_ARMOR_PROF);
			if (l != null)
			{
				aList.addAll(l);
			}
		}

		return aList;
	}

	private List getStableAggregateFeatList()
	{
		if (isAggregateFeatsStable())
		{
			return stableAggregateFeatList;
		}
		return null;
	}

	private void setStableAutomaticFeatList(final List aFeatList)
	{
		stableAutomaticFeatList = aFeatList;
		setAutomaticFeatsStable(aFeatList != null);
	}

	private List getStableAutomaticFeatList()
	{
		if (isAutomaticFeatsStable())
		{
			return stableAutomaticFeatList;
		}
		return null;
	}

	private void setStableVirtualFeatList(final List aFeatList)
	{
		stableVirtualFeatList = aFeatList;
		setVirtualFeatsStable(aFeatList != null);
	}

	private List getStableVirtualFeatList()
	{
		if (isVirtualFeatsStable())
		{
			return stableVirtualFeatList;
		}
		return null;
	}

	/**
	 * Used to create the Bonus HashMap from all active bonuses
	 * @param aBonus
	 * @param anObj
	 *
	 * @return     a List of BONUS strings
	 */
	private List getStringListFromBonus(final BonusObj aBonus, final PObject anObj)
	{
		final List aList = new ArrayList();

		final String bInfoString = aBonus.getBonusInfo();
		final StringTokenizer aTok = new StringTokenizer(bInfoString, ",");
		int listindex = 0;

		while (aTok.hasMoreTokens())
		{
			final String bonusInfo = aTok.nextToken();

			// Some BONUS statements use %LIST to represent
			// a possible list or selection made
			// Need to deconstruct for proper bonus stacking
			if (anObj.getAssociatedCount() > 0)
			{
				// There are three forms:
				// 1) has %LIST in the bonusName
				// 2) has %LIST in the bonusInfo
				// 3) has no %LIST at all
				if (aBonus.getBonusName().indexOf("%LIST") >= 0)
				{
					// Must use getBonusName because it
					// contains the unaltered bonusType
					final String bonusName = aBonus.getBonusName();

					for (int i = 0; i < anObj.getAssociatedCount(); ++i)
					{
						final StringBuffer ab = new StringBuffer();
						final String tName = CoreUtility.replaceFirst(bonusName, "%LIST", anObj.getAssociated(i));
						ab.append(tName).append('.');
						ab.append(bonusInfo);

						if (aBonus.hasTypeString())
						{
							ab.append(':').append(aBonus.getTypeString());
						}

						aList.add(ab.toString().toUpperCase());
					}
				}
				else if (bonusInfo.indexOf("%LIST") >= 0)
				{
					for (int i = 0; i < anObj.getAssociatedCount(true); ++i)
					{
						final StringBuffer ab = new StringBuffer();
						final String tName = CoreUtility.replaceFirst(bonusInfo, "%LIST", anObj.getAssociated(i, true));
						ab.append(aBonus.getTypeOfBonus()).append('.');
						ab.append(tName);

						if (aBonus.hasTypeString())
						{
							ab.append(':').append(aBonus.getTypeString());
						}
						aList.add(ab.toString().toUpperCase());
					}
				}
				else
				{
					final int cnt = anObj.getAssociatedCount();

					if (cnt <= listindex && bonusInfo.equals("LIST"))
					{
						continue;
					}

					while (true)
					{
						final StringBuffer ab = new StringBuffer();
						ab.append(aBonus.getTypeOfBonus()).append('.');
						if (bonusInfo.equals("LIST"))
						{
							ab.append(anObj.getAssociated(listindex));
						}
						else
						{
							ab.append(bonusInfo);
						}

						if (aBonus.hasTypeString())
						{
							ab.append(':').append(aBonus.getTypeString());
						}

						listindex++;

						aList.add(ab.toString().toUpperCase());
						if (aTok.countTokens() > 0 || listindex >= cnt)
						{
							break;
						}
					}
				}
			}
			else if (aBonus.hasVariable())
			{
				// Some bonuses have a variable as part
				// of their name, such as
				//  BONUS:WEAPONPROF=AbcXyz|TOHIT|3
				// so parse out the correct value
				final StringBuffer ab = new StringBuffer();
				ab.append(aBonus.getTypeOfBonus());
				ab.append(aBonus.getVariable()).append('.');
				ab.append(bonusInfo);

				if (aBonus.hasTypeString())
				{
					ab.append(':').append(aBonus.getTypeString());
				}

				aList.add(ab.toString().toUpperCase());
			}
			else
			{
				final StringBuffer ab = new StringBuffer();
				ab.append(aBonus.getTypeOfBonus()).append('.');
				ab.append(bonusInfo);

				if (aBonus.hasTypeString())
				{
					ab.append(':').append(aBonus.getTypeString());
				}

				aList.add(ab.toString().toUpperCase());
			}
		}

		return aList;
	}

	private String getSubRegion(final boolean useTemplates)
	{
		String pcSubRegion = getStringFor(StringKey.SUB_REGION);
		if ((pcSubRegion != null) || !useTemplates)
		{
			return pcSubRegion; // character's subregion trumps any from templates
		}

		String s = Constants.s_NONE;

		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate) templateList.get(i);
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

		for (int i = 0; i < classList.size(); i++)
		{
			final PCClass aClass = (PCClass) classList.get(i);
			total += aClass.getLevel();
		}

		return total;
	}

	/**
	 * get the total number of character levels a character has.
	 * A character level is any class level that is not a monster level
	 * @return total character level
	 */
	private int getTotalCharacterLevel()
	{
		int total = 0;

		for (int i = 0; i < classList.size(); i++)
		{
			final PCClass aClass = (PCClass) classList.get(i);
			if (!aClass.isMonster())
			{
				total += aClass.getLevel();
			}
		}

		return total;
	}

	private HashMap getTotalLevelHashMap()
	{
		final HashMap lvlMap = new HashMap();

		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			lvlMap.put(aClass.getName(), String.valueOf(aClass.getLevel()));
		}

		return lvlMap;
	}

	private boolean isVirtualFeatsStable()
	{
		return virtualFeatsStable;
	}

	/**
	 * Adds the List to activeBonuses if it passes RereqToUse Test
	 * @param aList
	 */
	private void addListToActiveBonuses(final List aList)
	{
		if (aList==null) {
			return;
		}
		activeBonusList.addAll(aList);
		//setDirty(true);
	}

	private void addSpells(final PObject obj)
	{
		if ((race == null) || (obj == null) || (obj.getSpellList() == null) || obj.getSpellList().isEmpty())
		{
			return;
		}

		PObject owner;

		for (Iterator ri = obj.getSpellList().iterator(); ri.hasNext();)
		{
			final PCSpell pcSpell = (PCSpell) ri.next();
			final String spellName = pcSpell.getName();
			final Spell aSpell = Globals.getSpellNamed(spellName);

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
			else if (castCount.startsWith("LEVEL=") || castCount.startsWith("LEVEL."))
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
			if(dcFormula != null && !dcFormula.equals(""))
			{
				getVariableValue(dcFormula, "").intValue(); // TODO: value never used
			}

			if (PrereqHandler.passesAll(pcSpell.getPreReqList(), this, pcSpell))
			{
				final Spell newSpell = (Spell)aSpell.clone();
				aSpell.setFixedCasterLevel(pcSpell.getCasterLevelFormula());
				aSpell.setFixedDC(pcSpell.getDcFormula());
				final List sList = owner.getSpellSupport().getCharacterSpell(newSpell, book, spellLevel);

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

	private Map addStringToDRMap(final Map drMap, final String drString)
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

				// We use -1 as a starting value so as to allow DR:0/- to work. It
				// can then have bonuses added to improve it.
				int y = -1;
				final Object obj = drMap.get(key);

				if (obj != null)
				{
					y = Integer.parseInt(obj.toString());
				}

				final int z = getVariableValue(val, "").intValue();

				if (z > y)
				{
					drMap.put(key, String.valueOf(z));
				}
			}
		}
		//setDirty(true);

		return drMap;
	}

	/**
	 * create a map of key (vision-type string) and values (int)
	 * @param visMap
	 * @param aMap
	 * @return Map
	 */
	private Map addStringToVisionMap(final Map visMap, final Map aMap)
	{
		if ((aMap == null) || (aMap.size() == 0))
		{
			return visMap;
		}

		for (Iterator i = aMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			final String aVal = aMap.get(aKey).toString();
			final Object bObj = visMap.get(aKey);
			int b = 0;

			if (bObj != null)
			{
				b = getVariableValue(bObj.toString(), "").intValue();
			}

			int a = getVariableValue(aVal, "").intValue();

			// Add any bonuses to new value
			a += (int) getTotalBonusTo("VISION", aKey);

			if (a >= b)
			{
				visMap.put(aKey, String.valueOf(a));
			}
		}

		return visMap;
	}


	private void setStableAggregateFeatList(final List aFeatList)
	{
		stableAggregateFeatList = aFeatList;
		setAggregateFeatsStable(aFeatList != null);
	}

	private void addWeaponProfToList(final List aFeatList, final String aString, final boolean isAuto)
	{
		if (aString.startsWith("WEAPONTYPE=") || aString.startsWith("WEAPONTYPE."))
		{
			final List weapList = EquipmentList.getEquipmentOfType("WEAPON." + aString.substring(11), "");

			if (weapList.size() != 0)
			{
				for (Iterator e = weapList.iterator(); e.hasNext();)
				{
					final Equipment weap = (Equipment) e.next();
					final WeaponProf aProf = Globals.getWeaponProfNamed(weap.profName(this));

					if (aProf != null)
					{
						addWeaponProfToList(aFeatList, aProf.getName(), isAuto);
					}
				}
			}

			return;
		}

		// Add all weapons of type aString
		// (eg: Simple, Martial, Exotic, Ranged, etc.)
		else if (Globals.weaponTypesContains(aString))
		{
			final Collection weaponProfs = Globals.getAllWeaponProfsOfType(aString);

			for (Iterator e = weaponProfs.iterator(); e.hasNext();)
			{
				final WeaponProf weaponProf = (WeaponProf) e.next();
				addWeaponProfToList(aFeatList, weaponProf.getName(), isAuto);
			}

			return;
		}

		final WeaponProf wp = Globals.getWeaponProfNamed(aString);

		if (wp != null)
		{
			final StringTokenizer aTok = new StringTokenizer(wp.getType(), ".");
			String featName = aTok.nextToken() + " Weapon Proficiency";

			while (aTok.hasMoreTokens() || (featName.length() > 0))
			{
				if ("".equals(featName))
				{
					if (aTok.hasMoreTokens())
					{
						featName = aTok.nextToken() + " Weapon Proficiency";
					}
					else
					{
						break;
					}
				}

				Ability anAbility = AbilityUtilities.getFeatNamedInList(aFeatList, featName);
				if (anAbility != null)
				{
					// No need to add to list,
					// if multiples not allowed
					if (anAbility.isMultiples())
					{
						if (!anAbility.containsAssociated(aString))
						{
							anAbility.addAssociated(aString);
							anAbility.sortAssociated();
						}
					}
				}
				else
				{
					anAbility = Globals.getAbilityNamed("FEAT", featName);

					if (anAbility != null)
					{
						if (isAuto && !anAbility.isMultiples()
							&& !Constants.s_INTERNAL_WEAPON_PROF.equalsIgnoreCase(featName))
						{
							//
							// Only use catch-all if haven't taken feat that supercedes it
							//
							if(hasRealFeatNamed(featName))
							{
								featName = Constants.s_INTERNAL_WEAPON_PROF;

								continue;
							}

							featName = "";

							continue; // Don't add auto-feat
						}

						anAbility = (Ability) anAbility.clone();
						anAbility.addAssociated(aString);

						if (isAuto)
						{
							anAbility.setFeatType(Ability.ABILITY_AUTOMATIC);
						}

						aFeatList.add(anAbility);
					}

					/*else
					{
						if (!wp.isType("NATURAL"))
						{
							Logging.errorPrint("Weaponprof feat not found: " + featName + ":" + aString);
						}
					}*/
				}
				if(anAbility != null)
				{
					// TheForken 20050124 adds bonus to feat
					anAbility.addSelectedWeaponProfBonus(aString);
				}

				featName = "";
			}
		}

		if (!weaponProfList.contains(aString))
		{
			weaponProfList.add(aString);
		}
	}

	private SortedSet addWeaponProfsLists(final List aList, final SortedSet aSet, final List aFeatList, final boolean addIt)
	{
		if ((aList == null) || aList.isEmpty())
		{
			return aSet;
		}

		final String sizeString = "FDTSMLHGC";

		for (Iterator e1 = aList.iterator(); e1.hasNext();)
		{
			String aString = (String) e1.next();
			final int idx = aString.indexOf('[');

			if (idx >= 0)
			{
				final StringTokenizer bTok = new StringTokenizer(aString.substring(idx + 1), "[]");
				final List preReqList = new ArrayList();

				while (bTok.hasMoreTokens())
				{
					preReqList.add(bTok.nextToken());
				}

				aString = aString.substring(0, idx);

				if (preReqList.size() != 0)
				{
					if (!PrereqHandler.passesAll(preReqList, this, null ))
					{
						continue;
					}
				}
			}

			final int lastComma = aString.lastIndexOf(',');
			boolean flag = (lastComma < 0);

			if (!flag && (race != null))
			{
				final String eString = aString.substring(lastComma + 1);
				final int s = sizeInt();

				for (int i = 0; i < eString.length(); ++i)
				{
					if (sizeString.lastIndexOf(eString.charAt(i)) == s)
					{
						flag = true;

						break;
					}
				}

				aString = aString.substring(0, lastComma);
			}

			if (flag)
			{
				// 1.	Look for an exact equipment match
				// Can this be done?
				final Equipment eq = EquipmentList.getEquipmentKeyed(aString);

				//Equipment eq = Globals.getEquipmentNamed(aString);
				if (eq != null)
				{
					// Found an exact equipment match; use it
					if (addIt)
					{
						aSet.add(aString);
						addWeaponProfToList(aFeatList, aString, true);
					}
				}
				else // No exact equipment match found.
				{
					// Set up a place to store located profs
					final List addWPs = new ArrayList();

					// Check for type separators.
					final boolean dotsFound = aString.indexOf(".") >= 0;

					// 2.  If no dots found, try to find a weapon proficiency specification
					boolean loadedByProfs = false;

					if (!dotsFound)
					{
						// Look for an exact proficiency match
						final WeaponProf prof = Globals.getWeaponProfKeyed(aString);

						if (prof != null)
						{
							addWPs.add(aString);
							loadedByProfs = true;
						}

						// Look for proficiency type matches
						else
						{
							final Collection listFromWPType = Globals.getAllWeaponProfsOfType(aString);

							if ((listFromWPType != null) && (!listFromWPType.isEmpty()))
							{
								for (Iterator i = listFromWPType.iterator(); i.hasNext();)
								{
									addWPs.add(i.next().toString());
								}

								loadedByProfs = true;
							}
						}
					}

					// 3.  If dots found (or no profs found), assume weapon types
					if (dotsFound || !loadedByProfs)
					{
						final String desiredTypes = "Weapon." + aString;
						final List listFromEquipmentType = EquipmentList.getEquipmentOfType(desiredTypes, "");

						if ((listFromEquipmentType != null) && (!listFromEquipmentType.isEmpty()))
						{
							for (Iterator i = listFromEquipmentType.iterator(); i.hasNext();)
							{
								final String bString = ((Equipment) i.next()).profName(this);
								addWPs.add(bString);
							}
						}
					}

					// Add the located weapon profs to the prof list
					for (Iterator i = addWPs.iterator(); i.hasNext();)
					{
						if (addIt)
						{
							aSet.add(aString);
							addWeaponProfToList(aFeatList, (String) i.next(), true);
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
	 * @return List
	 */
	private List getAutoShieldProfList()
	{
		final ArrayList aList = new ArrayList();

		for (Iterator e = getPObjectList().iterator(); e.hasNext();)
		{
			final PObject aPObj = (PObject) e.next();

			if (aPObj != null)
			{
				aPObj.addAutoTagsToList("SHIELDPROF", aList, this, true);
			}
		}

		return aList;
	}

	/**
	 * Get the class level as a String
	 * @param className
	 * @param doReplace
	 * @return class level as String
	 */
	public String getClassLevelString(String className, final boolean doReplace)
	{
		int lvl = 0;
		int idx = className.indexOf(";BEFORELEVEL=");

		if (idx < 0)
		{
			idx = className.indexOf(";BEFORELEVEL.");
		}

		if (idx > 0)
		{
			lvl = Integer.parseInt(className.substring(idx + 13));
			className = className.substring(0, idx);
		}

		if (doReplace)
		{
			className = className.replace('{', '(').replace('}', ')');
		}

		final PCClass aClass = getClassNamed(className);

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

	private ArrayList getPObjectList()
	{
		// Posible object types include:
		//  Alignment (PCAlignment)
		//  ArmorProf
		//  BioSet (ageSet)
		//  Check (PObject)
		//  Class (PCClass)
		//  CompanionMod
		//  Deity
		//  Domain (CharacterDomain)
		//  Equipment
		//  Feat (virtual feats, auto feats)
		//  Race
		//  ShieldProf
		//  SizeAdjustment
		//  Skill
		//  Stat (PCStat)
		//  TempBonus
		//  Template (PCTemplate)
		//  WeaponProf
		//
		final ArrayList results = new ArrayList();

		//  Alignment
		results.add(SettingsHandler.getGame().getAlignmentAtIndex(getAlignment()));

		//  armorProfList is still just a list of Strings
		//results.addAll(getArmorProfList());
		//  BioSet
		results.add(Globals.getBioSet());

		//  Checks
		results.addAll(SettingsHandler.getGame().getUnmodifiableCheckList());

		//  Class
		results.addAll(classList);

		//  CompanionMod
		results.addAll(companionModList);

		//  Deity
		if (deity != null)
		{
			results.add(deity);
		}

		//  Domain (CharacterDomain)
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) e.next();
			final Domain aDomain = aCD.getDomain();

			if (aDomain != null)
			{
				results.add(aDomain);
			}
		}

		//  Equipment
		for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				results.add(eq);

				List aList = eq.getEqModifierList(true);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						results.add(eqMod);
					}
				}

				aList = eq.getEqModifierList(false);

				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						results.add(eqMod);
					}
				}
			}
		}

		//  Feat (virtual feats, auto feats)
		results.addAll(aggregateFeatList());

		//  Race
		results.add(getRace());

		//  SizeAdjustment
		results.add(getSizeAdjustment());

		//  Skill
		results.addAll(getSkillList());

		//  Stat (PCStat)
		results.addAll(statList.getStats());

		//  Template (PCTemplate)
		results.addAll(getTemplateList());

		//  weaponProfList is still just a list of Strings
		//results.addAll(getWeaponProfList());
		return results;
	}

	private void getPreReqFromACType(String aString, final PObject aPObj)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		String outputString = "|";

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if ((bString.startsWith("PRE") || bString.startsWith("!PRE")) && (bString.indexOf(':') >= 0))
			{
				try
				{
					Logging.debugPrint("Why is this prerequisite '"+bString+"' parsed in '"+getClass().getName()+".getPreReqFromACType()' rather than the persistence layer?");
					final PreParserFactory factory = PreParserFactory.getInstance();
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
				outputString += (bString + "|");
			}
		}

		aString = outputString.substring(1); // TODO: value never used
	}

	private ArrayList getSelectedShieldProfList()
	{
		final ArrayList aList = new ArrayList();

		for (Iterator i = getPObjectList().iterator(); i.hasNext();)
		{
			final PObject aPObj = (PObject) i.next();

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

	private void addNewSkills(final int level)
	{
		final List addItems = new ArrayList();
		final Iterator skillIter = Globals.getSkillList().iterator();
		Skill aSkill;

		while (skillIter.hasNext())
		{
			aSkill = (Skill) skillIter.next();

			if (includeSkill(aSkill, level)
				&& (Globals.binarySearchPObject(getSkillList(), aSkill.getKeyName()) == null))
			{
				addItems.add(aSkill.clone());
			}
		}

		getSkillList().addAll(addItems);
		//setDirty(true);
	}

	/**
	 * availableSpells
	 * sk4p 13 Dec 2002
	 * 
	 * For learning or preparing a spell: Are there slots available at this level or higher
	 * Fixes BUG [569517]
	 *
	 * @param level the level being checked for availability
	 *              aClass       the class under consideration
	 *              bookName       the name of the spellbook
	 *              knownLearned       "true" if this is learning a spell, "false" if prepping
	 *              isSpecialtySpell "true" iff this is a specialty for the given class
	 * @param aClass
	 * @param bookName
	 * @param knownLearned
	 * @param isSpecialtySpell
	 * @param aPC
	 * @return         true or false, a new spell can be added
	 */
	public boolean availableSpells(final int level, final PCClass aClass, final String bookName, final boolean knownLearned,
									final boolean isSpecialtySpell, final PlayerCharacter aPC)
	{
		boolean available = false;
		final boolean isDivine = ("Divine".equalsIgnoreCase(aClass.getSpellType()));
		int knownTot;
		int knownNon;
		int knownSpec;
		int i;
		int memTot;
		int memNon;
		int memSpec;

		//int excTot
		int excNon;

		//int excTot
		int excSpec;
		int lowExcSpec = 0;
		int lowExcNon = 0;
		int goodExcSpec = 0;
		int goodExcNon = 0;

		for (i = 0; i < level; ++i)
		{
			// Get the number of castable slots
			if (knownLearned)
			{
				knownNon = aClass.getKnownForLevel(aClass.getLevel(), i, bookName, aPC);
				knownSpec = aClass.getSpecialtyKnownForLevel(aClass.getLevel(), i, aPC);
				knownTot = knownNon + knownSpec; // TODO: : value never used
			}
			else
			{
				// Get the number of castable slots
				knownTot = aClass.getCastForLevel(aClass.getLevel(), i, bookName, true, true, aPC);
				knownNon = aClass.getCastForLevel(aClass.getLevel(), i, bookName, false, true, aPC);
				knownSpec = knownTot - knownNon;
			}

			// Now get the number of spells memorized, total and specialties
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
				// If I'm not divine, I can use non-specialty slots of this level
				// to take up the slack of my excess specialty spells from
				// lower levels.
				while ((excNon > 0) && (lowExcSpec < 0))
				{
					--excNon;
					++lowExcSpec;
				}

				// And I can use non-specialty slots of this level to take
				// up the slack of my excess specialty spells of this level.
				//
				while ((excNon > 0) && (excSpec < 0))
				{
					--excNon;
					++excSpec;
				}
			}

			// Now, if there are slots left over, I don't add them to the running totals.
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

		for (i = level;; ++i)
		{
			if (knownLearned)
			{
				knownNon = aClass.getKnownForLevel(aClass.getLevel(), i, bookName, aPC);
				knownSpec = aClass.getSpecialtyKnownForLevel(aClass.getLevel(), i, aPC);
				knownTot = knownNon + knownSpec; // for completeness
			}
			else
			{
				// Get the number of castable slots
				knownTot = aClass.getCastForLevel(aClass.getLevel(), i, bookName, true, true, aPC);
				knownNon = aClass.getCastForLevel(aClass.getLevel(), i, bookName, false, true, aPC);
				knownSpec = knownTot - knownNon;
			}

			// At the level currently being looped through, if the number of casts
			// is zero, that means we have reached a level beyond which no higher-level
			// casts are possible.	Therefore, it's time to break.
			//
			if ((knownLearned && ((knownNon + knownSpec) == 0)) || (!knownLearned && (knownTot == 0)))
			{
				break;
			}

			// Now get the number of spells memorized, total and specialties
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
				// If I'm not divine, I can use non-specialty slots of this level
				// to take up the slack of my excess specialty spells from
				// lower levels.
				while ((excNon > 0) && (lowExcSpec < 0))
				{
					--excNon;
					++lowExcSpec;
				}

				// And also for levels sufficiently high for the spell that got me
				// into this mess, but of lower level than the level currently
				// being calculated.
				while ((excNon > 0) && (goodExcSpec < 0))
				{
					--excNon;
					++goodExcSpec;
				}

				// And finally use non-specialty slots of this level to take
				// up the slack of excess specialty spells of this level.
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

			// Now, if there are slots left over, I don't add them to the running totals.
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
		List bonusListCopy = new ArrayList();
		bonusListCopy.addAll(getActiveBonusList());
		for (Iterator b = bonusListCopy.iterator(); b.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) b.next();

			if (!aBonus.isValueStatic())
			{
				continue;
			}

			final PObject anObj = (PObject) aBonus.getCreatorObject();

			if (anObj == null)
			{
				continue;
			}

			// Keep track of which bonuses have been calculated
			processedBonusList.add(aBonus);

			for (Iterator as = getStringListFromBonus(aBonus, anObj).iterator(); as.hasNext();)
			{
				final String bString = (String) as.next();
				final double iBonus = aBonus.getValueAsdouble();
				setActiveBonusStack(iBonus, bString, getActiveBonusMap());
				Logging.debugPrint("BONUS: " + anObj.getName() + " : " + iBonus + " : " + bString);
			}
		}

		//
		// Now we do all the BonusObj's that require calculations
		bonusListCopy = new ArrayList();
		bonusListCopy.addAll(getActiveBonusList());
		for (Iterator b = getActiveBonusList().iterator(); b.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) b.next();

			if (processedBonusList.contains(aBonus))
			{
				continue;
			}

			final PObject anObj = (PObject) aBonus.getCreatorObject();

			if (anObj == null)
			{
				continue;
			}

			processBonus(aBonus);
		}
	}

	private void calcAgeBonuses()
	{
		final String ageSetLine = Globals.getBioSet().getAgeSetLine(this);

		if (ageSetLine == null)
		{
			return;
		}

		final List tempList = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer(ageSetLine, "\t");
		aTok.nextToken(); // name of ageSet, eg: Middle Aged

		while (aTok.hasMoreTokens())
		{
			final String b = aTok.nextToken();

			if (b.startsWith("BONUS:"))
			{
				final BonusObj aBonus = Bonus.newBonus(b.substring(6));

				if (aBonus != null)
				{
					aBonus.setCreatorObject(Globals.getBioSet());
					aBonus.setApplied(true);
					tempList.add(aBonus);
				}
			}
		}

		if (!tempList.isEmpty())
		{
			addListToActiveBonuses(tempList);
		}
	}

	/**
	 * ###################################################
	 * Functions that get all the "active" bonuses and
	 * add them to the activeBonusList
	 * ###################################################
	 */
	private void calcAlignmentBonuses()
	{
		final PCAlignment aLine = SettingsHandler.getGame().getAlignmentAtIndex(getAlignment());

		if (aLine != null)
		{
			activateAndAddBonusesFromPObject(aLine);
		}
	}

	/**
	 * Currently unused
	 * But needed when ArmorProf's get converted to BonusObj's
	 * isntead of just a List of String
	 */
	/*private void calcArmorProfBonuses()
	{
		if (getArmorProfList().isEmpty())
		{
			return;
		}

		for (Iterator e = getArmorProfList().iterator(); e.hasNext();)
		{
			final ArmorProf ap = (ArmorProf) e.next();
			ap.activateBonuses(this);

			final List tempList = ap.getActiveBonuses(this);

			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}*/

	
	/**
	 * Compute total bonus from a List of BonusObj's Use cost of bonus to adjust
	 * total bonus up or down This method takes a list of bonus objects.
	 * 
	 * For each object in the list, it gets the creating object and queries it for
	 * its "COST".  It then multiplies the value of the bonus by this cost and
	 * adds it to the cumulative total so far.  If subSearch is true, the choices
	 * made in the object that the bonus originated in are searched, the effective
	 * bonus is multiplied by the number of times this bonus appears in the list. 
	 * 
	 * Note: This COST seems to be used for several different things
	 * in the codebase, in feats for instance, it is used to modify the feat
	 * pool by amounts other than 1 when selecting a given feat.  Here it is
	 * used as a multiplier to say how effective a given bonus is i.e. a bonus
	 * with a COST of 0.5 counts for half its normal value.  The COST is limitied
	 * to a max of 1, so it can only make bonuses less effective.
	 *
	 * @param   aList a list of bonus objects
	 * @param   subSearch whether to take account of how many times the bonus was chosen.
	 *
	 * @return  the calculated cumulative bonus
	 */
	
	private double calcBonusWithCostFromList(final List aList, final boolean subSearch)
	{
		double totalBonus = 0;

		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) e.next();
			final PObject anObj = (PObject) aBonus.getCreatorObject();

			if (anObj == null)
			{
				continue;
			}

			double iBonus = 0;

			if (aBonus.hasPreReqs())
			{
				if (PrereqHandler.passesAll(aBonus.getPrereqList(), this, null))
				{
					iBonus = anObj.calcBonusFrom(aBonus, this, this);
				}
			}
			else
			{
				iBonus = anObj.calcBonusFrom(aBonus, this, this);
			}

			int k = Math.max(1, (int) (anObj.getAssociatedCount() * ((HasCost) anObj).getCost()));

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

	private void calcCheckBonuses()
	{
		if (SettingsHandler.getGame().getUnmodifiableCheckList().isEmpty())
		{
			return;
		}

		for (Iterator e = SettingsHandler.getGame().getUnmodifiableCheckList().iterator(); e.hasNext();)
		{
			final PObject anObj = (PObject) e.next();

			activateAndAddBonusesFromPObject(anObj);
		}
	}

	private void calcClassBonuses()
	{
		if (classList.isEmpty())
		{
			return;
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			if (aClass.getLevel() > 0)
			{
				activateAndAddBonusesFromPObject(aClass);
			}
		}
	}

	private void calcCompanionModBonuses()
	{
		activateAndAddBonusesFromPObjectList(companionModList);
	}

	private void calcDeityBonuses()
	{
		activateAndAddBonusesFromPObject(getDeity());
	}

	private void calcDomainBonuses()
	{
		if (characterDomainList.isEmpty())
		{
			return;
		}

		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) e.next();
			final Domain aDomain = aCD.getDomain();

			activateAndAddBonusesFromPObject(aDomain);
		}
	}

	private void calcEquipmentBonuses()
	{
		if (getEquipmentList().isEmpty())
		{
			return;
		}

		for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (eq.isEquipped())
			{
				activateAndAddBonusesFromPObject(eq);
			}
		}
	}

	private void calcFeatBonuses()
	{
		activateAndAddBonusesFromPObjectList(aggregateFeatList());
	}

	private void calcRaceBonuses()
	{
		activateAndAddBonusesFromPObject(getRace());
	}

	private void calcSkillBonuses()
	{
		activateAndAddBonusesFromPObjectList(getSkillList());
	}

	private void calcPurchaseModeBonuses()
	{
		GameMode gm = SettingsHandler.getGame();
		final String purchaseMethodName = gm.getPurchaseModeMethodName();
		if (gm.isPurchaseStatMode())
		{
			final PointBuyMethod pbm = gm.getPurchaseMethodByName(purchaseMethodName);
			pbm.activateBonuses(this);

			final List tempList = pbm.getActiveBonuses();
			addListToActiveBonuses(tempList);
		}
	}

	private void calcTempBonuses()
	{
		final List tempList = getFilteredTempBonusList();
		if(tempList.isEmpty())
		{
			return;
		}
		for (Iterator tempIter = tempList.iterator(); tempIter.hasNext();)
		{
			final BonusObj bonus = (BonusObj) tempIter.next();
			bonus.setApplied(false);

			if (bonus.hasPreReqs())
			{
				if (PrereqHandler.passesAll(bonus.getPrereqList(), this, null))
				{
					bonus.setApplied(true);
				}
				else
				{
					bonus.setApplied(false);
				}
			}
			else
			{
				bonus.setApplied(true);
			}
			if (!bonus.isApplied())
			{
				tempIter.remove();
			}
		}
		addListToActiveBonuses(tempList);
	}

	private void calcTemplateBonuses()
	{
		activateAndAddBonusesFromPObjectList(getTemplateList());
	}

	private void activateAndAddBonusesFromPObjectList(final Collection objects)
	{
		if (objects==null || objects.isEmpty())
		{
			return;
		}

		for (Iterator e = objects.iterator(); e.hasNext();)
		{
			final PObject object = (PObject) e.next();
			activateAndAddBonusesFromPObject(object);
		}
	}

	private void activateAndAddBonusesFromPObject(final PObject object)
	{
		if (object==null)
		{
			return;
		}

		object.activateBonuses(this);

		final List tempList = object.getActiveBonuses(this);
		addListToActiveBonuses(tempList);
	}

	/**
	 * Currently unused
	 * But needed when WeaponProf's get converted to BonusObj's
	 * isntead of just a List of String
	 */
	/*private void calcWeaponProfBonuses()
	{
		if (getWeaponProfList().isEmpty())
		{
			return;
		}

		for (Iterator e = getWeaponProfList().iterator(); e.hasNext();)
		{
			final WeaponProf wp = (WeaponProf) e.next();
			wp.activateBonuses(this);

			final List tempList = wp.getActiveBonuses(this);

			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}*/

	/**
	 * calculate the total racial modifier to save:
	 * racial boni like the standard halfling's +1 on all saves
	 * template boni like the lightfoot halfling's +1 on all saves
	 * racial base modifiers for certain monsters
	 * @param saveIndex
	 * @return int
	 */
	private int calculateSaveBonusRace(final int saveIndex)
	{
		int save;

		if (((saveIndex - 1) < 0) || ((saveIndex - 1) >= SettingsHandler.getGame().getUnmodifiableCheckList().size()))
		{
			return 0;
		}

		final String sString = SettingsHandler.getGame().getUnmodifiableCheckList().get(saveIndex - 1).toString();
		save = (int) race.bonusTo("CHECKS", "BASE." + sString, this, this);
		save += (int) race.bonusTo("CHECKS", sString, this, this);

		return save;
	}

	private void clearActiveBonusMap()
	{
		activeBonusMap.clear();
	}

	private void clearActiveBonuses()
	{
		activeBonusList.clear();
	}

	private void setActiveBonusList(final List aList)
	{
		activeBonusList.clear();
		activeBonusList.addAll(aList);
	}

	/**
	 * returns the level of the highest spell in a given spellbook
	 * Yes, divine casters can have a "spellbook"
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
			bookName = (String) getSpellBooks().get(sbookNum);
		}

		final PObject aObject = getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			for (levelNum = 0; levelNum >= 0; ++levelNum)
			{
				final List aList = aObject.getSpellSupport().getCharacterSpell(null, bookName, levelNum);

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
	 * @param aString
	 * @return int
	 */
	int countSpellListBook(final String aString)
	{
		final int dot = aString.lastIndexOf('.');
		int spellCount = 0;

		if (dot < 0)
		{
			for (Iterator iClass = classList.iterator(); iClass.hasNext();)
			{
				final PCClass aClass = (PCClass) iClass.next();
				spellCount += aClass.getSpellSupport().getCharacterSpellCount();
			}
		}
		else
		{
			final int classNum = Integer.parseInt(aString.substring(17, dot));
			final int levelNum = Integer.parseInt(aString.substring(dot + 1, aString.length() - 1));

			final PObject aObject = getSpellClassAtIndex(classNum);

			if (aObject != null)
			{
				final List aList = aObject.getSpellSupport().getCharacterSpell(null, Globals.getDefaultSpellBook(), levelNum);
				spellCount = aList.size();
			}
		}

		return spellCount;
	}

	/**
	 * returns the number of times a spell is memorized
	 * Tag looks like: (SPELLTIMES%class&period;%book&period;%level&period;%spell)
	 * aString looks like: SPELLTIMES2&period;-1&period;4&period;15
	 * 
	 * where &period; is a fullstop (or period if you are from USA ;p)
	 * 
	 * heavily stolen from replaceTokenSpellMem in ExportHandler.java
	 * @param aString
	 * @return spell times
	 */
	int countSpellTimes(final String aString)
	{
		boolean found = false;
		final StringTokenizer aTok = new StringTokenizer(aString.substring(10), ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int bookNum = Integer.parseInt(aTok.nextToken());
		final int spellLevel = Integer.parseInt(aTok.nextToken());
		final int spellNumber = Integer.parseInt(aTok.nextToken());

		final PObject aObject = getSpellClassAtIndex(classNum);
		String bookName = Globals.getDefaultSpellBook();

		if (bookNum > 0)
		{
			bookName = (String) getSpellBooks().get(bookNum);
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
					final List charSpellList = new ArrayList();

					for (Iterator iClass = getClassList().iterator(); iClass.hasNext();)
					{
						final PCClass aClass = (PCClass) iClass.next();
						final List bList = aClass.getSpellSupport().getCharacterSpell(null, bookName, -1);

						for (Iterator bi = bList.iterator(); bi.hasNext();)
						{
							final CharacterSpell cs = (CharacterSpell) bi.next();

							if (!charSpellList.contains(cs))
							{
								charSpellList.add(cs);
							}
						}
					}

					Collections.sort(charSpellList);

					if (spellNumber < charSpellList.size())
					{
						final CharacterSpell cs = (CharacterSpell) charSpellList.get(spellNumber);
						si = cs.getSpellInfoFor(bookName, -1, -1);
						found = true;
					}
				}
				else if (aObject != null)
				{
					final List charSpells = aObject.getSpellSupport().getCharacterSpell(null, bookName, spellLevel);

					if (spellNumber < charSpells.size())
					{
						final CharacterSpell cs = (CharacterSpell) charSpells.get(spellNumber);
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
	 * Counts the number of spells inside a spellbook
	 * Yes, divine casters can have a "spellbook"
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

		String bookName = Globals.getDefaultSpellBook();

		if (sbookNum > 0)
		{
			bookName = (String) getSpellBooks().get(sbookNum);
		}

		final PObject aObject = getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			final List aList = aObject.getSpellSupport().getCharacterSpell(null, bookName, levelNum);

			return aList.size();
		}

		return 0;
	}




	private String findTemplateGender()
	{
		String templateGender = Constants.s_NONE;

		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			final String aString = aTemplate.getGenderLock();

			if (!aString.equals(Constants.s_NONE))
			{
				templateGender = aString;
			}
		}

		return templateGender;
	}

	private PCClass getClassDisplayNamed(final String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			if (aClass.getDisplayClassName().equalsIgnoreCase(aString))
			{
				return aClass;
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
		// Why +1?  Adjustments are deltas, not absolute
		// levels, so are not subject to the "back off one"
		// element of the * algorightm in minXPForLevel.  This
		// still means that levelAdjustment of 0 gives you 0
		// XP, but we need LA of 1 to give us 1,000 XP.
		return PlayerCharacterUtilities.minXPForLevel(getLevelAdjustment(this) + 1,this);
	}

	private SizeAdjustment getSizeAdjustment()
	{
		final SizeAdjustment sa = SettingsHandler.getGame().getSizeAdjustmentAtIndex(sizeInt());

		return sa;
	}

	int getSpellClassCount()
	{
		return getSpellClassList().size();
	}

	/**
	 * Get the spell class list
	 * @return List
	 */
	public List getSpellClassList()
	{
		final List aList = new ArrayList();

		if (!race.getSpellSupport().getCharacterSpell(null, "", -1).isEmpty())
		{
			aList.add(race);
		}

		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			final PObject aObject = (PObject) classIter.next();

			if (!aObject.getSpellSupport().getCharacterSpell(null, "", -1).isEmpty())
			{
				aList.add(aObject);
			}
			else if (aObject instanceof PCClass)
			{
				if (!((PCClass) aObject).getSpellType().equalsIgnoreCase("None"))
				{
					aList.add(aObject);
				}
			}
		}

		return aList;
	}

	private String checkForVariableInList(final PObject obj, final String variableString, final boolean isMax, final String matchSrc,
		final String matchSubSrc, boolean found, double value, int decrement)
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

				if (!"".equals(loopVariable))
				{
					while (loopValue > decrement)
					{
						loopValue -= decrement;
						value += getVariableValue(sString, src).doubleValue();
					}

					loopValue = 0;
					loopVariable = "";
				}
			}
		}

		if (flag)
		{
			return value + "";
		}
		return ""; // signifies that the variable was found in this list
	}

	/**
	 * Check if the character has the named Deity.
	 *
	 * @param deityName String name of the deity to check for.
	 * @return <code>true</code> if the character has the Deity,
	 *         <code>false</code> otherwise.
	 */
	boolean hasDeity(final String deityName)
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("DEITY");
		prereq.setOperand(deityName);
		prereq.setOperator(PrerequisiteOperator.EQ);

		return PrereqHandler.passes(prereq, this, null );
	}

	private boolean includeSkill(final Skill skill, final int level)
	{
		boolean UntrainedExclusiveClass = false;
		final String tempSkill = skill.getUntrained();

		if ((tempSkill.length() > 0) && (tempSkill.charAt(0) == 'Y') && skill.isExclusive())
		{
			if (skill.isClassSkill(classList, this))
			{
				UntrainedExclusiveClass = true;
			}
		}

		return (level == 2) || skill.isRequired() || (skill.getTotalRank(this).floatValue() > 0)
		|| ((level == 1) && (tempSkill.length() > 0) && (tempSkill.charAt(0) == 'Y') && !skill.isExclusive())
		|| ((level == 1) && UntrainedExclusiveClass);
	}

	private void increaseMoveArray(final Double moveRate, final String moveType, final Double moveMult, final String multOp)
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
	 * Change the number of levels a character has in a particular class.
	 * Note: It is assumed that this method is not used as part of loading
	 * a previously saved character.
	 *
	 * @param numberOfLevels
	 *            The number of levels to add or remove. If a positive number is
	 *            passed in then that many levels will be added. If the number
	 *            of levels passed in is negative then that many levels will be
	 *            removed from the specified class.
	 * @param globalClass
	 *            The global class from the datastore. The class as stored in
	 *            the character will be compared to this one using the
	 *            getClassNamed() method
	 * @param bSilent
	 *            If true do not display any warning messages about adding or
	 *            removing too many levels
	 */
	public void incrementClassLevel(final int numberOfLevels, final PCClass globalClass, final boolean bSilent)
	{
		// If not importing, load the spell list
		if (!isImporting())
		{
			getSpellList();
		}

		// Make sure the character qualifies for the class if adding it
		if (numberOfLevels > 0)
		{
			if (!globalClass.isQualified(this))
			{
				return;
			}

			if (globalClass.isMonster() && !SettingsHandler.isIgnoreMonsterHDCap() && !race.isAdvancementUnlimited()
				&& ((totalHitDice() + numberOfLevels) > race.maxHitDiceAdvancement()) && !bSilent)
			{
				ShowMessageDelegate.showMessageDialog("Cannot increase Monster Hit Dice for this character beyond " + race.maxHitDiceAdvancement()
				+ ". This character's current number of Monster Hit Dice is " + totalHitDice(),
					Constants.s_APPNAME,
					MessageType.INFORMATION);

				return;
			}
		}

		// Check if the character already has the class.
		PCClass pcClassClone = getClassNamed(globalClass.getName());

		// If the character did not already have the class...
		if (pcClassClone == null)
		{
			// add the class even if setting to level 0
			if (numberOfLevels >= 0)
			{
				// Get a clone of the class so we don't modify the globals!
				pcClassClone = (PCClass) globalClass.clone();

				// Make sure the clone was successful
				if (pcClassClone == null)
				{
					Logging.errorPrint("PlayerCharacter::incrementClassLevel => " + "Clone of class "
						+ globalClass.getName() + " failed!");

					return;
				}

				// Add the class to the character classes as level 0
				classList.add(pcClassClone);

				// do the following only if adding a level of a class for the first time
				if (numberOfLevels > 0)
				{
					getLanguagesList().addAll(pcClassClone.getSafeListFor(ListKey.AUTO_LANGUAGES));
				}
			}
			else
			{
				// mod is < 0 and character does not have class.  Return.
				return;
			}
		}

		// Add or remove levels as needed
		if (numberOfLevels > 0)
		{
			for (int i = 0; i < numberOfLevels; ++i)
			{
				final PCLevelInfo playerCharacterLevelInfo = saveLevelInfo(pcClassClone.getKeyName());
				// if we fail to add the level, remove and return
				if (!pcClassClone.addLevel(playerCharacterLevelInfo, false, bSilent, this, false))
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
			for (int i = 0, x = templateList.size(); i < x; ++i)
			{
				final PCTemplate aTemplate = (PCTemplate) templateList.get(i);
				final List templateFeats = aTemplate.feats(getTotalLevels(), totalHitDice(), this, true);

				for (int j = 0, y = templateFeats.size(); j < y; ++j)
				{
					modFeatsFromList(null, (String) templateFeats.get(j), true, false);
				}
			}
		}

		setAggregateFeatsStable(false);
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);
		calcActiveBonuses();
		//setDirty(true);
	}

	/**
	 * - Get's a list of dependencies from aBonus
	 * - Finds all active bonuses that add to those dependencies and
	 * have not been processed and recursivly calls itself
	 * - Once recursed in, it adds the computed bonus to activeBonusMap
	 * @param aBonus
	 */
	private void processBonus(final BonusObj aBonus)
	{
		final List aList = new ArrayList();

		// Go through all bonuses and check to see if they add to
		// aBonus's dependencies and have not already been processed
		for (Iterator ab = getActiveBonusList().iterator(); ab.hasNext();)
		{
			final BonusObj newBonus = (BonusObj) ab.next();

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
		for (Iterator ab = aList.iterator(); ab.hasNext();)
		{
			final BonusObj newBonus = (BonusObj) ab.next();

			// recursivly call itself
			processBonus(newBonus);
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
			return;
		}

		// calculate bonus and add to activeBonusMap
		for (Iterator as = getStringListFromBonus(aBonus, anObj).iterator(); as.hasNext();)
		{
			final String bString = (String) as.next();
			final double iBonus = anObj.calcBonusFrom(aBonus, this, bString, this);
			setActiveBonusStack(iBonus, bString, getActiveBonusMap());
			Logging.debugPrint("BONUS: " + anObj.getName() + " : " + iBonus + " : " + bString);
		}
	}

	private boolean qualifiesForFeat(final Ability aFeat)
	{
		return aFeat.canBeSelectedBy(this);
	}

	private boolean hasSkill(final String skillName)
	{
		return (getSkillNamed(skillName) != null);
	}

	private void rebuildLists(final PCClass toClass, final PCClass fromClass, final int iCount, final PlayerCharacter aPC)
	{
		final int fromLevel = fromClass.getLevel();
		final int toLevel = toClass.getLevel();

		for (int i = 0; i < iCount; ++i)
		{
			fromClass.doMinusLevelMods(this, fromLevel - i);

			final PCLevelInfo playerCharacterLevelInfo = aPC.getLevelInfoFor(toClass.getKeyName(), toLevel+i+1);
			toClass.doPlusLevelMods(toLevel + i + 1, aPC, playerCharacterLevelInfo);
		}
	}

	private void removeExcessSkills(final int level)
	{
		final Iterator skillIter = getSkillList().iterator();
		Skill skill;

		while (skillIter.hasNext())
		{
			skill = (Skill) skillIter.next();

			if (!includeSkill(skill, level))
			{
				skillIter.remove();
				setDirty(true);
			}
		}
	}

	private boolean removeLevelInfo(final String classKeyName)
	{
		for (int idx = pcLevelInfo.size() - 1; idx >= 0; --idx)
		{
			final PCLevelInfo li = (PCLevelInfo) pcLevelInfo.get(idx);

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
	private void removeObjectsForLevelInfo(final PCLevelInfo li) {
		for (Iterator iter = li.getObjects().iterator(); iter.hasNext();) {
			final PObject object = (PObject) iter.next();

			// remove this object from the feats lists
			for (Iterator iterator = getRealFeatsIterator(); iterator.hasNext();) {
				final Ability feat = (Ability) iterator.next();
				if (object==feat)
				{
					iterator.remove();
				}
			}
			// remove this object from the feats lists
			for (Iterator iterator = stableVirtualFeatList.iterator(); iterator.hasNext();) {
				final Ability feat = (Ability) iterator.next();
				if (object==feat)
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
	 * Method:
	 * 1: 4d6 Drop Lowest
	 * 2: 3d6
	 * 3: 5d6 Drop 2 Lowest
	 * 4: 4d6 reroll 1's drop lowest
	 * 5: 4d6 reroll 1's and 2's drop lowest
	 * 6: 3d6 +5
	 * 7: 5d6 Drop lowest and middle as per FREQ #458917
	 *
	 * @param method the method to be used for rolling.
	 */
	public void rollStats(final int method)
	{
		int roll;

		for (Iterator stat = statList.getStats().iterator(); stat.hasNext();)
		{
			final PCStat currentStat = (PCStat) stat.next();
			currentStat.setBaseScore(0);

			if (!currentStat.isRolled())
			{
				continue;
			}

			if (SettingsHandler.getGame().isPurchaseStatMode())
			{
				currentStat.setBaseScore(SettingsHandler.getGame().getPurchaseModeBaseStatScore(this));

				continue;
			}

			switch (method)
			{
				case Constants.CHARACTERSTATMETHOD_ALLSAME:
					roll = SettingsHandler.getGame().getAllStatsValue();
					break;

				case Constants.CHARACTERSTATMETHOD_ROLLED:
					final String diceExpression = SettingsHandler.getGame().getRollMethodExpression();
					roll = RollingMethods.roll(diceExpression);
					break;

				default:
					roll = 0;
					break;
			}

			roll += currentStat.getBaseScore();

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
		getLanguagesList().clear();
		getAutoLanguages();
		setPoolAmount(0);
	}

	/**
	 * Sorts the provided list of equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple
	 * items of equipment have the same outputIndex they will be ordered by
	 * name. Note hidden items (outputIndex = -1) are not included in list.
	 *
	 * @param unsortedEquipList An ArrayList of the equipment to be sorted.
	 * @return An ArrayList of the equipment objects in output order.
	 */
	private List sortEquipmentList(final List unsortedEquipList)
	{
		return sortEquipmentList(unsortedEquipList, Constants.MERGE_ALL);
	}

	/**
	 * Sorts the provided list of equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple
	 * items of equipment have the same outputIndex they will be ordered by
	 * name. Note hidden items (outputIndex = -1) are not included in list.
	 *
	 * @param unsortedEquipList An ArrayList of the equipment to be sorted.
	 * @param merge             How to merge.
	 * @return An ArrayList of the equipment objects in output order.
	 */
	private List sortEquipmentList(final List unsortedEquipList, final int merge)
	{
		if (unsortedEquipList.isEmpty())
		{
			return unsortedEquipList;
		}

		final List sortedList;

		// Merge list for duplicates
		// The sorting is done during the Merge
		sortedList = EquipmentUtilities.mergeEquipmentList(unsortedEquipList, merge);

		// Remove the hidden items from the list
		for (Iterator i = sortedList.iterator(); i.hasNext();)
		{
			final Equipment item = (Equipment) i.next();

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
			total += Integer.parseInt(BonusToken.getBonusToken("BONUS.COMBAT.AC." + aString, this));
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

		final List aList = new ArrayList();

		// There is a risk that the active bonus map may be modified by other
		// threads, so we use a for loop rather than an iterator so that we
		// still get an answer.
		Object[] keys = getActiveBonusMap().keySet().toArray();
		for (int i = 0; i < keys.length; i++)
		{
			final String aKey = (String) keys[i];

			// aKey is either of the form:
			//  COMBAT.AC
			// or
			//  COMBAT.AC:Luck
			// or
			//  COMBAT.AC:Armor.REPLACE
			if (aList.contains(aKey))
			{
				continue;
			}

			String rString = aKey;

			// rString could be something like:
			//  COMBAT.AC:Armor.REPLACE
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
			//  COMBAT.AC
			//  COMBAT.AC:Luck
			//  COMBAT.AC:Armor.REPLACE
			// However, it must not match
			//  COMBAT.ACCHECK
			if ((rString.length() > prefix.length()) && !rString.startsWith(prefix + ":"))
			{
				continue;
			}

			if (rString.startsWith(prefix))
			{
				aList.add(rString);
				aList.add(rString + ".STACK");
				aList.add(rString + ".REPLACE");

				final double aBonus = getActiveBonusForMapKey(rString, Double.NaN);
				final double replaceBonus = getActiveBonusForMapKey(rString + ".REPLACE", Double.NaN);
				final double stackBonus = getActiveBonusForMapKey(rString + ".STACK", 0);
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

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			if (aClass.isMonster())
			{
				totalLevels += aClass.getLevel();
			}
		}

		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) e.next();
			totalLevels += cMod.getHitDie();
		}

		return totalLevels;
	}
	/**
	 * @param descriptionLst The descriptionLst to set.
	 */
	private void setDescriptionLst(final String descriptionLst)
	{
		this.descriptionLst = descriptionLst;
	}

	private class CasterLevelSpellBonus
	{
		private int	bonus;
		private String type;

		/**
		 * Constructor
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
		 * @return bonus
		 */
		public int getBonus()
		{
			return(bonus);
		}

		/**
		 * Get type
		 * @return type
		 */
		public String getType()
		{
			return(type);
		}

		/**
		 * Set bonus
		 * @param newBonus
		 */
		public void setBonus(final int newBonus)
		{
			bonus = newBonus;
		}

		public String toString()
		{
			return("bonus: " + bonus + "    type: " + type);
		}

	}

	/**
	 * @param info
	 * @return character level
	 */
	public int getCharacterLevel(final PCLevelInfo info) {
		int i=1;
		for (Iterator iter = pcLevelInfo.iterator(); iter.hasNext(); i++) {
			final PCLevelInfo element = (PCLevelInfo) iter.next();
			if (info == element) {
			   return i;
			}
		}
		return -1;
	}


	  /**
	   * Return a list of bonus languages which the character may select from.
	   * This function is not efficient, but is sufficient for it's current
	   * use of only being called when the user requests the bonus language
	   * selection list.
	   * Note: A check will be made for the ALL language and it will be
	   * replaced with the current list of languages in globals. These should
	   * be further restricted by the prerequisites of the languages to ensure
	   * that 'secret' languages are not offered.
	   *
	   * @return List of bonus languages for the character.
	   */
	  public Set getLanguageBonusSelectionList()
	{
		Set languageList = new HashSet();

		// Race
		languageList.addAll(race.getLanguageBonus());

		// Templates
		for (Iterator tIter = templateList.iterator(); tIter.hasNext();)
		{
			PCTemplate template = (PCTemplate) tIter.next();
			languageList.addAll(template.getLanguageBonus());
		}

		// Classes
		for (Iterator cIter = classList.iterator(); cIter.hasNext();)
		{
			PCClass pcClass = (PCClass) cIter.next();
			languageList.addAll(pcClass.getLanguageBonus());
		}

		// Scan for the ALL language and if found replace it with all languages
		boolean addAll = false;
		for (Iterator iter = languageList.iterator(); iter.hasNext();)
		{
			Language lang = (Language) iter.next();
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
   * Retrieve the bonus for the stat excluding either temporary bonues,
   * equipment bonuses or both. This method ensure stacking rules are
   * applied to all included bonuses. If not excluding either, it is
   * quicker to use getTotalBonusTo.
   *
	 * @param statAbbr The short name of the stat to calculate the bonus for.
	 * @param useTemp Should temp bonuses be included?
	 * @param useEquip Should equipment bonuses be included?
	 * @return The bonus to the stat.
	 */
	public int getPartialStatBonusFor(String statAbbr, boolean useTemp, boolean useEquip)
  {
	  List abl = getActiveBonusList();
	  final String prefix = "STAT." + statAbbr;
	  Map bonusMap = new HashMap();
	  for (Iterator iter = abl.iterator(); iter.hasNext();)
		{
			BonusObj bonus = (BonusObj) iter.next();
			if (bonus.isApplied() && bonus.getBonusName().equals("STAT"))
			{
				boolean found = false;
				for (Iterator iterator = bonus.getBonusInfoList().iterator(); iterator.hasNext();)
				{
					Object element = iterator.next();
					if (element instanceof PCStat && ((PCStat) element).getAbb().equals(statAbbr))
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
				if (bonus.getCreatorObject() instanceof Equipment)
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
					// Grab the list of relevant types so that we can build up the
					// bonuses with the stacking rules applied.
					List typeList = getStringListFromBonus(bonus, (PObject) bonus.getCreatorObject());
					for (Iterator iterator = typeList.iterator(); iterator.hasNext();)
					{
						String element = (String) iterator.next();
						if (!element.startsWith(prefix))
						{
							iterator.remove();
						}
					}
					for (Iterator iterator = typeList.iterator(); iterator.hasNext();)
					{
						String element = (String) iterator.next();
						setActiveBonusStack(bonus.getCalculatedValue(this), element, bonusMap);
					}
				}
			}
		}

	  // Sum the included bonuses to the stat to get our result.
	  int total = 0;
	  for (Iterator iter = bonusMap.keySet().iterator(); iter.hasNext();)
		{
			String bKey = (String) iter.next();
			total += Float.parseFloat((String) bonusMap.get(bKey));
		}
	  return total;
  }

	/**
   * Retrieve the stat as it was at a particular level excluding either
   * temporary bonues, equipment bonuses or both. This method ensures
   * stacking rules are applied to all included bonuses. If not excluding
   * either, it is quicker to use getTotalStatAtLevel.
   *
	 * @param statAbb The short name of the stat to calculate the value of.
	 * @param level The level we want to see the stat at.
	 * @param usePost Should stat mods that occurred after levelling be included?
	 * @param useTemp Should temp bonuses be included?
	 * @param useEquip Should equipment bonuses be included?
	 * @return The stat as it was at the level
	 */
	public int getPartialStatAtLevel(String statAbb, int level,
		boolean usePost, boolean useTemp, boolean useEquip)
	{
		int curStat = getStatList().getPartialStatFor(statAbb, useTemp, useEquip);
		for (int idx = getLevelInfoSize() - 1; idx >= level; --idx)
		{
			final int statLvlAdjust = ((PCLevelInfo) pcLevelInfo.get(idx))
					.getTotalStatMod(statAbb, usePost);
			curStat -= statLvlAdjust;
		}

		return curStat;
	}

	/**
	 * Returns a deep copy of the PlayerCharacter.
	 * Note: This method does a shallow copy of many lists in here that seem
	 * to point to "system" objects.  These copies should be validated before
	 * using this method.
	 *
	 * @return a new deep copy of the <code>PlayerCharacter</code>
	 */
	public Object clone()
	{
		PlayerCharacter aClone = null;

		// calling super.clone won't work because it will not create
		// new data instances for all the final variables and I won't
		// be able to reset them.  Need to call new PlayerCharacter()
		//aClone = (PlayerCharacter)super.clone();
		aClone = new PlayerCharacter();

		aClone.addArmorProfs(getArmorProfList());

		Iterator it = this.getRealFeatsIterator();
		while (it.hasNext()) {aClone.addRealFeat((Ability) it.next()); }

		aClone.miscList.addAll(getMiscList());
		for (Iterator i = getNotesList().iterator(); i.hasNext(); )
		{
			aClone.addNotesItem((NoteItem)i.next());
		}
		aClone.primaryWeapons.addAll(getPrimaryWeapons());
		aClone.secondaryWeapons.addAll(getSecondaryWeapons());
		aClone.shieldProfList.addAll(getShieldProfList());
		aClone.skillList.addAll(getSkillList());
		aClone.specialAbilityList.addAll(getSpecialAbilityList());
		aClone.templateList.addAll(getTemplateList());
		aClone.variableList.addAll(this.variableList);
		aClone.gold = new BigDecimal(gold.toString());
		// Points to a global deity object so it doesn't need to be cloned.
		aClone.deity = deity;
		aClone.domainSourceMap = (HashMap)domainSourceMap.clone();
		aClone.characterDomainList.addAll(characterDomainList);
		for (Iterator i = classList.iterator(); i.hasNext(); )
		{
			aClone.classList.add(((PCClass)i.next()).clone());
		}
		aClone.companionModList.addAll(companionModList);
		aClone.qualifyArrayList.addAll(qualifyArrayList);
		if (followerMaster != null)
		{
			aClone.followerMaster = (Follower) followerMaster.clone();
		}
		else
		{
			aClone.followerMaster = null;
		}
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			final EquipSet eqSet = (EquipSet)e.next();
			aClone.addEquipSet((EquipSet) eqSet.clone());
		}
		aClone.equipmentList.addAll(equipmentList);
		aClone.equipmentMasterList.addAll(equipmentMasterList);
		for (Iterator i = pcLevelInfo.iterator(); i.hasNext(); )
		{
			final PCLevelInfo info = (PCLevelInfo)i.next();
			aClone.pcLevelInfo.add(info.clone());
		}
		for (Iterator i = spellBooks.iterator(); i.hasNext(); )
		{
			final String book = (String)i.next();
			aClone.addSpellBook(new String(book));
		}
		aClone.tempBonusItemList.addAll(tempBonusItemList);
		aClone.tempBonusList.addAll(tempBonusList);
		aClone.tempBonusFilters.addAll(tempBonusFilters);
		aClone.race = race;
		aClone.favoredClasses.addAll(favoredClasses);

		aClone.statList.getStats().clear();
		for (Iterator i = statList.getStats().iterator(); i.hasNext(); )
		{
			final PCStat stat = (PCStat)i.next();
			aClone.statList.getStats().add(stat.clone());
		}
		if (kitList != null)
		{
			aClone.kitList = new ArrayList();
			aClone.kitList.addAll(kitList);
		}
		// Not sure what this is.  It may need to be cloned.
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
		aClone.weaponProfList.addAll(weaponProfList);
		aClone.autoKnownSpells = autoKnownSpells;
		aClone.autoLoadCompanion = autoLoadCompanion;
		aClone.autoSortGear = autoSortGear;
		aClone.outputSheetHTML = new String(outputSheetHTML);
		aClone.outputSheetPDF = new String(outputSheetPDF);
		aClone.ageSetKitSelections = new boolean[10];
		for (int i = 0; i < ageSetKitSelections.length; i++ )
		{
			aClone.ageSetKitSelections[i] = ageSetKitSelections[i];
		}

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
		aClone.setQualifyListStable(false);
		aClone.adjustMoveRates();
		aClone.calcActiveBonuses();

		return aClone;
	}

	/**
	 * Set the string for the characteristic
	 * @param key
	 * @param s
	 */
	public void setStringFor(StringKey key, String s)
	{
		stringChar.setCharacteristic(key, s);
		setDirty(true);
	}

	/**
	 * Remove the string for the characteristic
	 * @param key
	 * @return string removed
	 */
	public String removeStringFor(StringKey key) {
		return stringChar.removeCharacteristic(key);
	}

	private Float getEquippedQty(EquipSet eSet, Equipment eqI)
	{
		final String rPath = eSet.getIdPath();

		for (Iterator e = this.getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			String esIdPath = es.getIdPath() + ".";
			String rIdPath = rPath + ".";

			if (!esIdPath.startsWith(rIdPath))
			{
				continue;
			}

			if (eqI.getName().equals(es.getValue()))
			{
				return es.getQty();
			}
		}

		return new Float(0);
	}
	/**
	 * This method gets a list of locations for a weapon
	 * @param hands
	 * @param multiHand
	 * @return weapon location choices
	 **/
	private static List getWeaponLocationChoices(final int hands, final String multiHand)
	{
		final List result = new ArrayList(hands + 2);

		if (hands > 0)
		{
			result.add(Constants.S_PRIMARY);

			for (int i = 1; i < hands; ++i)
			{
				if (i > 1)
				{
					result.add(Constants.S_SECONDARY + " " + i);
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
	 * If an item can only go in one location, return the name of that
	 * location to add to an EquipSet
	 * @param eqI
	 * @return single location
	 **/
	private String getSingleLocation(Equipment eqI)
	{
		// Handle natural weapons
		if (eqI.isNatural())
		{
			if (eqI.getSlots(this) == 0)
			{
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
			return "";
		}

		List eqSlotList = SystemCollections.getUnmodifiableEquipSlotList();

		if ((eqSlotList == null) || eqSlotList.isEmpty())
		{
			return "";
		}

		for (Iterator eI = eqSlotList.iterator(); eI.hasNext();)
		{
			EquipSlot es = (EquipSlot) eI.next();

			// see if this EquipSlot can contain this item TYPE
			if (es.canContainType(eqI.getType()))
			{
				return es.getSlotName();
			}
		}

		return "";
	}

	public List getEquippableLocations(EquipSet eqSet, Equipment eqI, List containers)
	{
		// Some Equipment locations are based on the number of hands
		int hands = 0;

		final Race aRace = getRace();

		if (aRace != null)
		{
			hands = aRace.getHands();
		}

		List aList = new ArrayList();

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
			else if (Globals.isWeaponOutsizedForPC(this, eqI))
			{
				// do nothing for outsized weapons
			}
			else
			{
				String wpSingle = eqI.profName(1, this);
				WeaponProf wp = Globals.getWeaponProfNamed(wpSingle);

				if (Globals.handsRequired(this, eqI, wp) == 1)
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
				for (Iterator iSet = getEquipSet().iterator(); iSet.hasNext(); )
				{
					EquipSet es = (EquipSet)iSet.next();
					String esID = es.getParentIdPath() + ".";
					String abID = idPath + ".";

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
	 * @param eSet
	 * @param locName
	 * @param eqI
	 * @param eqTarget
	 * @return true if equipment can be added
	 **/
	public  boolean canEquipItem(EquipSet eSet, String locName, Equipment eqI, Equipment eqTarget)
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
		if (locName.startsWith(Constants.S_CARRIED) || locName.startsWith(Constants.S_EQUIPPED)
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
		if (eqI.isWeapon() && Globals.isWeaponOutsizedForPC(this, eqI) && !eqI.isNatural())
		{
			return false;
		}

		// make a HashMap to keep track of the number of each
		// item that is already equipped to a slot
		Map slotMap = new HashMap();

		for (Iterator e = getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			String esID = es.getParentIdPath() + ".";
			String abID = idPath + ".";

			if (!esID.startsWith(abID))
			{
				continue;
			}

			// check to see if we already have
			// an item in that particular location
			if (es.getName().equals(locName))
			{
				final Equipment eItem = es.getItem();
				final String nString = (String) slotMap.get(locName);
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

		for (Iterator e = getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			String esID = es.getParentIdPath() + ".";
			String abID = idPath + ".";

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
				if ((locName.equals(Constants.S_BOTH) || locName.equals(Constants.S_DOUBLE))
					&& (es.getName().equals(Constants.S_PRIMARY) || es.getName().equals(Constants.S_SECONDARY)
					|| es.getName().equals(Constants.S_BOTH) || es.getName().equals(Constants.S_DOUBLE)))
				{
					return false;
				}

				// inverse of above case
				if ((locName.equals(Constants.S_PRIMARY) || locName.equals(Constants.S_SECONDARY))
					&& (es.getName().equals(Constants.S_BOTH) || es.getName().equals(Constants.S_DOUBLE)))
				{
					return false;
				}
			}

			// If we already have an item in that location
			// check to see how many are allowed in that slot
			if (es.getName().equals(locName))
			{
				final String nString = (String) slotMap.get(locName);
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
				if (existNum > (eSlot.getSlotCount() + (int) this.getTotalBonusTo("SLOTS", eSlot.getContainType())))
				{
					return false;
				}

				return true;
			}
		}

		return true;
	}

	/**
	 * Checks to see if Equipment exists in selected EquipSet
	 * and if so, then return the EquipSet containing eqI
	 * @param eSet
	 * @param eqI
	 * @return EquipSet
	 **/
	private EquipSet getEquipSetForItem(EquipSet eSet, Equipment eqI)
	{
		final String rPath = eSet.getIdPath();

		for (Iterator e = getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			String esIdPath = es.getIdPath() + ".";
			String rIdPath = rPath + ".";

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
	 * returns new id_Path with the last id one higher than the current
	 * highest id for EquipSets with the same ParentIdPath
	 * @param eSet
	 * @return new id path
	 **/
	private String getNewIdPath(EquipSet eSet)
	{
		String pid = "0";
		int newID = 0;

		if (eSet != null)
		{
			pid = eSet.getIdPath();
		}

		for (Iterator e = getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();

			if (es.getParentIdPath().equals(pid) && (es.getId() > newID))
			{
				newID = es.getId();
			}
		}

		++newID;

		return pid + '.' + newID;
	}

	public EquipSet addEquipToTarget(final EquipSet eSet, final Equipment eqTarget, String locName, final Equipment eqI, Float newQty)
	{
		float tempQty = 1.0f;
		if (newQty != null)
		{
			tempQty = newQty.floatValue();
		}
		else
		{
			newQty = new Float(tempQty);
		}
		boolean addAll = false;
		boolean mergeItem = false;

		Equipment masterEq = getEquipmentNamed(eqI.getName());
		if (masterEq == null)
		{
			return null;
		}
		float diffQty = masterEq.getQty().floatValue() - getEquippedQty(eSet, eqI).floatValue();

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
		if ((locName == null) || ((locName != null) && ("".equals(locName) || (locName.length() == 0))))
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
	 * @param key
	 * @return String
	 */
	public String getStringFor(StringKey key)
	{
		return stringChar.getCharacteristic(key);
	}

	/**
	 * Gets a 'safe' String representation
	 * @param key
	 * @return a 'safe' String
	 */
	public String getSafeStringFor(StringKey key)
	{
		String s = stringChar.getCharacteristic(key);
		if (s == null) {
			s = "";
		}
		return s;
	}

	public void setDoLevelAbilities(boolean yesNo)
	{
		processLevelAbilities = yesNo;
	}

	public boolean doLevelAbilities()
	{
		return processLevelAbilities;
	}
/*
 * For debugging purposes
 * Dumps contents of spellbooks to System.err
 *
	static public void dumpSpells(final PlayerCharacter pc)
	{
		final List bookList = pc.getSpellBooks();
		for(int bookIdx = 0; bookIdx < bookList.size(); ++bookIdx)
		{
			final String bookName = (String) pc.getSpellBooks().get(bookIdx);

			System.err.println("==========");
			System.err.println("Book:" + bookName);
			final List casterList = pc.getSpellClassList();
			for(int casterIdx = 0; casterIdx < casterList.size(); ++casterIdx)
			{
				final PObject aCaster = (PObject) casterList.get(casterIdx);
				final List spellList = aCaster.getCharacterSpellList();
				if (spellList == null)
				{
					continue;
}
				System.err.println("Class/Race:" + aCaster.getName());

				for (Iterator i = spellList.iterator(); i.hasNext();)
				{
					final CharacterSpell cs = (CharacterSpell) i.next();

					for (Iterator csi = cs.getInfoListIterator(); csi.hasNext();)
					{
						final SpellInfo sInfo = (SpellInfo) csi.next();
						if (bookName.equals(sInfo.getBook()))
						{
							System.err.println(cs.getSpell().getOutputName() + sInfo.toString() + " level:" + Integer.toString(sInfo.getActualLevel()));
						}
					}
				}
			}
		}
	}
*/
}
