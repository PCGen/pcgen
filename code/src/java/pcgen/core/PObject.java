/*
 * PObject.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
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

import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.*;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

/**
 * <code>PObject</code><br>
 * This is the base class for several objects in the PCGen database.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public class PObject implements Cloneable, Serializable, Comparable,
	SourcedObject, KeyedListContainer
{
	/** Standard serialVersionUID for Serializable objects */
	private static final long serialVersionUID = 1;

	/** a boolean for whether something should recurse, default is false */
	private static boolean dontRecurse = false;

	/** A map to hold items keyed by Strings for the object */
	protected StringKeyMap stringChar = new StringKeyMap();
	/** A map to hold items keyed by Integers for the object */
	protected IntegerKeyMap integerChar = new IntegerKeyMap();
	/** A map of Lists for the object */
	protected ListKeyMapToList listChar = new ListKeyMapToList();

	/** List of associated items for the object */
	protected ArrayList associatedList = null;

	/** List of Bonuses for the object */
	private ArrayList bonusList = new ArrayList();
	/** List of Level Abilities for the object  */
	private ArrayList levelAbilityList = null;

	/** The source campaign for the object */
	private Campaign sourceCampaign = null;
	private HashMap sourceMap = new HashMap();
	private HashMap modSourceMap = null;

	/**
	 * A map of vision types associated with the object,
	 * Key: vision type, Value: vision range.
	 */
	protected Map vision = null;
	private HashMap pluginDataMap = new HashMap();

	protected String keyName = "";
	protected String name = "";

	/** Indicates if this object should be displayed to the user in the UI. */
	protected boolean visible = true;

	/** List of Pre-Requesites for the object  */
	private ArrayList preReqList = null;
	/** Map of the bonuses for the object  */
	private HashMap bonusMap = null;
	private HashMap changeProfMap = new HashMap();

	private Movement movement;
	private SpellSupport spellSupport = new SpellSupport();

	private VariableList variableList = null;

	/** description is Product Identity */
	private boolean descIsPI = false;
	/** name is Product Identity */
	private boolean nameIsPI = false;

	private boolean isNewItem = true;

	/** Holds the level of encumberance due to armor for the object */
	private int encumberedArmorMoveInt = Constants.LIGHT_LOAD;
	/** Holds the level of encumberance due to load for the object */
	private int encumberedLoadMoveInt = Constants.LIGHT_LOAD;

	private ArrayList drList = new ArrayList();

	/* ************
	 * Methods
	 * ************/

	/**
	 * Set the associated list
	 * @param index
	 * @param aString
	 */
	public final void setAssociated(final int index, final String aString)
	{
		associatedList.set(index, aString);
	}

	/**
	 * Get the associated item, without expanding the list
	 * @param idx
	 * @return the associated item
	 */
	public final String getAssociated(final int idx)
	{
		return getAssociated(idx, false);
	}

	/**
	 * Get the associated item
	 * @param idx
	 * @param expand - whether to expand the choice
	 * @return associated item
	 */
	public final String getAssociated(int idx, final boolean expand)
	{
		if (associatedList == null) {
			return "";
		}

		if (expand && (associatedList.get(0) instanceof FeatMultipleChoice))
		{
			return dealWithFeatMultipleChoice(idx);
		}

		return associatedList.get(idx).toString();
	}

	/**
	 * Get associated count, without expanding
	 * @return associated count
	 */
	public final int getAssociatedCount()
	{
		return getAssociatedCount(false);
	}

	/**
	 * Get the associated count for the object
	 * @param expand
	 * @return associated count
	 */
	public final int getAssociatedCount(final boolean expand)
	{
		if (associatedList == null)
		{
			return 0;
		}

		if (expand && (associatedList.get(0) instanceof FeatMultipleChoice))
		{
			return dealWithFeatMultipleChoiceForCount();
		}

		return associatedList.size();
	}

	/**
	 * Get the associated object
	 * @param idx
	 * @return the associated object
	 */
	public final Object getAssociatedObject(final int idx)
	{
		return associatedList.get(idx);
	}

	/**
	 * Adds entry to the CSkill list
	 * @param entry skill to add
	 */
	public final void addCcSkill(String entry)
	{
		Skill skill;
		if (entry.startsWith(".CLEAR"))
		{
			if (".CLEAR".equals(entry))
			{
				clearCcSkills();
			}
			else
			{
				if (entry.startsWith(".CLEAR"))
				{
					entry = entry.substring(7);
				}

				if (entry.startsWith("TYPE.") || entry.startsWith("TYPE="))
				{
					final String typeString = entry.substring(5);

					for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						skill = (Skill) e1.next();
						boolean toClear = true;
						final StringTokenizer cTok = new StringTokenizer(typeString, ".");

						while (cTok.hasMoreTokens() && toClear)
						{
							if (!skill.isType(cTok.nextToken()))
							{
								toClear = false;
							}
						}

						if (toClear)
						{
							listChar.removeFromListFor(ListKey.CROSS_CLASS_SKILLS, skill.getName());
						}
					}
				}
				else
				{
					listChar.removeFromListFor(ListKey.CROSS_CLASS_SKILLS, entry);
				}
			}
		}
		else if (entry.startsWith("TYPE.") || entry.startsWith("TYPE="))
		{
			for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				skill = (Skill) e1.next();

				if (skill.isType(entry.substring(5)))
				{
					listChar.addToListFor(ListKey.CROSS_CLASS_SKILLS, skill.getName());
				}
			}
		}
		else if ("ALL".equals(entry))
		{
			for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				skill = (Skill) e1.next();
				listChar.addToListFor(ListKey.CROSS_CLASS_SKILLS, skill.getName());
			}
		}
		else
		{
			listChar.addToListFor(ListKey.CROSS_CLASS_SKILLS, entry);
		}
	}

	/**
	 * Adds all of the entries to the CSkills list
	 * @param entries list of entries
	 */
	public final void addAllCcSkills(final List entries)
	{
		listChar.addAllToListFor(ListKey.CROSS_CLASS_SKILLS, entries);
	}

	/**
	 * Clears the class skill list
	 */
	public void clearCcSkills()
	{
		listChar.removeListFor(ListKey.CROSS_CLASS_SKILLS);
	}

	/**
	 * Get the list of class skills for this object
	 * @return the list of class skills for this object
	 */
	public final List getCcSkillList()
	{
		return listChar.getListFor(ListKey.CROSS_CLASS_SKILLS);
	}

	/**
	 * Set whether the description of this object is Product Identity
	 * @param a
	 */
	public final void setDescIsPI(final boolean a)
	{
		descIsPI = a;
	}

	/**
	 * True if the description of this object is Product Identity
	 * @return if the description of this object is Product Identity
	 */
	public final boolean getDescIsPI()
	{
		return descIsPI;
	}

	/**
	 * Set the description of this object
	 * @param a
	 */
	public final void setDescription(final String a)
	{
		stringChar.setCharacteristic(StringKey.DESCRIPTION, a);
	}

	/**
	 * Get the description of this object
	 * @return the description of this object
	 */
	public final String getDescription()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.DESCRIPTION);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get the plugin data for this object
	 * @param key
	 * @return the plugin data for this object
	 */
	public final Object getPluginData(final String key) {
		return pluginDataMap.get(key);
	}

	/**
	 * Put into the map the plugin data for this object
	 * @param key
	 * @param value
	 */
	public final void putPluginData(final String key, final String value) {
		pluginDataMap.put(key, value);
	}

	/**
	 * Set the KIT string, should be in the form of #|KIT1|KIT2|KIT3|etc
	 * @param arg
	 */
	public final void setKitString(final String arg)
	{
		if (arg.equals(".CLEAR"))
		{
			listChar.removeListFor(ListKey.KITS);
		}
		else
		{
			if (!containsInList(ListKey.KITS, arg))
			{
				listChar.addToListFor(ListKey.KITS, arg);
			}
		}
	}

	/**
	 * Get the level ability list for this object
	 * @return the level ability list for this object
	 */
	public final List getLevelAbilityList()
	{
		return levelAbilityList;
	}

	/**
	 * Set whether the name of this object is Product Identity
	 * @param a
	 */
	public final void setNameIsPI(final boolean a)
	{
		nameIsPI = a;
	}

	/**
	 * True if the name of this object is Product Identity
	 * @return True if the name of this object is Product Identity
	 */
	public final boolean getNameIsPI()
	{
		return nameIsPI;
	}

	/**
	 * Get the list of bonuses for this object
	 * @return the list of bonuses for this object
	 */
	public List getBonusList()
	{
		return bonusList;
	}

	/**
	 * Get the list of bounuses of a particular type for this object
	 * @param aType
	 * @param aName
	 * @return the list of bounuses of a particular type for this object
	 */
	public List getBonusListOfType(final String aType, final String aName)
	{
		return BonusUtilities.getBonusFromList(getBonusList(), aType, aName);
	}

	/**
	 * Get the map of bonuses for this object
	 * @return bonusMap
	 */
	public HashMap getBonusMap()
	{
		if (bonusMap == null)
		{
			bonusMap = new HashMap();
		}

		return bonusMap;
	}

	/**
	 * Adds entry to the CSkill list
	 * @param entry skill to add
	 */
	public final void addCSkill(String entry)
	{
		Skill skill;
		if (entry.startsWith(".CLEAR"))
		{
			if (".CLEAR".equals(entry))
			{
				clearCSkills();
			}
			else
			{
				if (entry.startsWith(".CLEAR"))
				{
					entry = entry.substring(7);
				}

				if (entry.startsWith("TYPE.") || entry.startsWith("TYPE="))
				{
					final String typeString = entry.substring(5);

					for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						skill = (Skill) e1.next();
						boolean toClear = true;
						final StringTokenizer cTok = new StringTokenizer(typeString, ".");

						while (cTok.hasMoreTokens() && toClear)
						{
							if (!skill.isType(cTok.nextToken()))
							{
								toClear = false;
							}
						}

						if (toClear)
						{
							listChar.removeFromListFor(ListKey.CLASS_SKILLS, skill.getName());
						}
					}
				}
				else
				{
					listChar.removeFromListFor(ListKey.CLASS_SKILLS, entry);
				}
			}
		}
		else if (entry.startsWith("TYPE.") || entry.startsWith("TYPE="))
		{
			for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				skill = (Skill) e1.next();

				if (skill.isType(entry.substring(5)))
				{
					listChar.addToListFor(ListKey.CLASS_SKILLS, skill.getName());
				}
			}
		}
		else if ("ALL".equals(entry))
		{
			for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				skill = (Skill) e1.next();
				listChar.addToListFor(ListKey.CLASS_SKILLS, skill.getName());
			}
		}
		else
		{
			listChar.addToListFor(ListKey.CLASS_SKILLS, entry);
		}
	}

	/**
	 * Adds all of the entries to the CSkills list
	 * @param entries list of entries
	 */
	public final void addAllCSkills(final List entries)
	{
		listChar.addAllToListFor(ListKey.CLASS_SKILLS, entries);
	}

	/**
	 * Clears the class skill list
	 */
	public void clearCSkills()
	{
		listChar.removeListFor(ListKey.CLASS_SKILLS);
	}

	/**
	 * Get the list of class skills for this object
	 * @return the list of class skills for this object
	 */
	public final List getCSkillList()
	{
		return listChar.getListFor(ListKey.CLASS_SKILLS);
	}

	/**
	 * Get the movement for this object
	 * @return the movement for this object
	 */
	public final Movement getMovement()
	{
		return movement;
	}

	/**
	 * Get the encumberance due to armor
	 * @return the encumberance due to armor
	 */
	public int getEncumberedArmorMove()
	{
		return encumberedArmorMoveInt;
	}

	/**
	 * Get the encumberance due to load
	 * @return the encumberance due to load
	 */
	public int getEncumberedLoadMove()
	{
		return encumberedLoadMoveInt;
	}

	/**
	 * Set the encumberance due to armor
	 * @param encumberedArmorMoveInt
	 */
	public void setEncumberedArmorMove(int encumberedArmorMoveInt)
	{
		this.encumberedArmorMoveInt = encumberedArmorMoveInt;
	}

	/**
	 * Set the encumberance due to load
	 * @param encumberedLoadMoveInt
	 */
	public void setEncumberedLoadMove(int encumberedLoadMoveInt)
	{
		this.encumberedLoadMoveInt = encumberedLoadMoveInt;
	}

	/**
	 * Sets the natural weapon equipment items list for this object
	 * @param aList
	 */
	public void setNaturalWeapons(final List aList)
	{
		listChar.removeListFor(ListKey.NATURAL_WEAPONS);
		listChar.addAllToListFor(ListKey.NATURAL_WEAPONS, aList);
	}

	/**
	 * Get the natural weapons list for this object
	 * @return the natural weapons list for this object
	 */
	public List getNaturalWeapons()
	{
		return getSafeListFor(ListKey.NATURAL_WEAPONS);
	}

	/**
	 * Set the number of pages for this object
	 * @param value
	 */
	public final void setNumPages(final int value)
	{
		integerChar.setCharacteristic(IntegerKey.NUM_PAGES, value);
	}

	/**
	 * Get the number of pages of this object
	 * @return the number of pages of this object
	 */
	public final int getNumPages()
	{
		Integer characteristic = integerChar.getCharacteristic(IntegerKey.NUM_PAGES);
		return characteristic == null ? 0 : characteristic.intValue();
	}

	/**
	 * Set the page usage formula for this object
	 * @param aString
	 */
	public final void setPageUsage(final String aString)
	{
		stringChar.setCharacteristic(StringKey.PAGE_USAGE, aString);
	}

	/**
	 * Get the page usage formula of this object
	 * @return the page usage formula of this object
	 */
	public final String getPageUsage()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.PAGE_USAGE);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get the list of temporary bonuses for this list
	 * @return the list of temporary bonuses for this list
	 */
	public List getTempBonusList()
	{
		return getSafeListFor(ListKey.TEMP_BONUS);
	}

	/**
	 * Set the temporary description for this object
	 * @param aString
	 */
	public final void setTempDescription(final String aString)
	{
		stringChar.setCharacteristic(StringKey.TEMP_DESCRIPTION, aString);
	}

	/**
	 * Get the temporary description of this object
	 * @return the temporary description of this object
	 */
	public final String getTempDescription()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.TEMP_DESCRIPTION);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Used to ignore Encumbrance for specified load types
	 * @param aString
	 */
	public void setUnencumberedMove(final String aString)
	{
		this.setEncumberedLoadMove(Constants.LIGHT_LOAD);
		this.setEncumberedArmorMove(Constants.LIGHT_LOAD);

		final StringTokenizer st = new StringTokenizer(aString, "|");

		while (st.hasMoreTokens())
		{
			final String loadString = st.nextToken();

			if (loadString.equalsIgnoreCase("MediumLoad"))
			{
				this.setEncumberedLoadMove(Constants.MEDIUM_LOAD);
			}
			else if (loadString.equalsIgnoreCase("HeavyLoad"))
			{
				this.setEncumberedLoadMove(Constants.HEAVY_LOAD);
			}
			else if (loadString.equalsIgnoreCase("Overload"))
			{
				this.setEncumberedLoadMove(Constants.OVER_LOAD);
			}
			else if (loadString.equalsIgnoreCase("MediumArmor"))
			{
				this.setEncumberedArmorMove(Constants.MEDIUM_LOAD);
			}
			else if (loadString.equalsIgnoreCase("HeavyArmor"))
			{
				this.setEncumberedArmorMove(Constants.OVER_LOAD);
			}
			else if (loadString.equalsIgnoreCase("LightLoad") || loadString.equalsIgnoreCase("LightArmor"))
			{
				//do nothing, but accept values as valid
			}
			else
			{
				ShowMessageDelegate.showMessageDialog("Invalid value of \"" + loadString + "\" for UNENCUMBEREDMOVE in \"" + getName() + "\".",
					"PCGen", MessageType.ERROR);
			}
		}
	}

	/**
	 * Get the count of variables on this object
	 * @return the count of variables on this object
	 */
	public final int getVariableCount()
	{
		if (variableList == null)
		{
			return 0;
		}

		return variableList.size();
	}

	/**
	 * Get an unmodifiable set of variable names for this object
	 * @return an unmodifiable set of variable names for this object
	 */
	public final Set getVariableNamesAsUnmodifiableSet()
	{
		if (variableList == null)
		{
			variableList = new VariableList();
		}

		return variableList.getVariableNamesAsUnmodifiableSet();
	}

	/**
	 * Get the list of virtual feats for this object
	 * @return the list of virtual feats for this object
	 */
	public List getVirtualFeatList()
	{
		return getSafeListFor(ListKey.VIRTUAL_FEATS);
	}

	/**
	 * Add automatic weapon proficienies for this object
	 * @param aString
	 */
	public final void addWeaponProfAutos(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		ListKey weaponProfListKey = ListKey.WEAPON_PROF;

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if (".CLEAR".equals(bString))
			{
				listChar.removeListFor(weaponProfListKey);
			}
			else if (bString.startsWith("TYPE=") || bString.startsWith("TYPE."))
			{
				final Collection weaponProfsOfType = Globals.getAllWeaponProfsOfType(bString.substring(5));
				if (weaponProfsOfType != null)
				{
					for (Iterator e = weaponProfsOfType.iterator(); e.hasNext();)
					{
						final String cString = ((WeaponProf) e.next()).getName();

						if (!containsInList(weaponProfListKey, cString))
						{
							listChar.addToListFor(weaponProfListKey, cString);
						}
					}
				}
			}
			else
			{
				if (!containsInList(weaponProfListKey, bString))
				{
					listChar.addToListFor(weaponProfListKey, bString);
				}
			}
		}
	}

	/**
	 * Get the automatic weapon proficiencies for this object
	 * @return the automatic weapon proficiencies for this object
	 */
	public List getWeaponProfAutos()
	{
		return getSafeListFor(ListKey.WEAPON_PROF);
	}

	/**
	 * Add the collection passed in to the associated list for this object
	 * @param collection
	 */
	public final void addAllToAssociated(final Collection collection)
	{
		if (associatedList == null)
		{
			associatedList = new ArrayList();
		}

		associatedList.addAll(collection);
	}

	/**
	 * Add the item to the associated list for this object
	 * @param aString
	 */
	public final void addAssociated(final String aString)
	{
		if (associatedList == null)
		{
			associatedList = new ArrayList();
		}

		associatedList.add(aString);
	}

	/**
	 * Add a feat choice to the associated list for this object
	 * @param aFeatChoices
	 */
	public final void addAssociated(final FeatMultipleChoice aFeatChoices)
	{
		if (associatedList == null)
		{
			associatedList = new ArrayList();
		}

		associatedList.add(aFeatChoices);
	}

	/**
	 * Add all of the assocaited list to the collection
	 * @param collection
	 */
	public final void addAssociatedTo(final Collection collection)
	{
		if (associatedList != null)
		{
			collection.addAll(associatedList);
		}
	}

	/**
	 * Add a new bonus to the list of bonuses
	 * @param aString
	 * @return true if new bonus is not null
	 */
	public final boolean addBonusList(final String aString)
	{
		if (bonusList == null)
		{
			bonusList = new ArrayList();
		}

		final BonusObj aBonus = Bonus.newBonus(aString);

		if (aBonus != null)
		{
			aBonus.setCreatorObject(this);
			addBonusList(aBonus);
		}

		return (aBonus != null);
	}

	/**
	 * returns all BonusObj's that are "active"
	 * @param aPC A PlayerCharacter object.
	 * @return active bonuses
	 */
	public List getActiveBonuses(final PlayerCharacter aPC)
	{
		final List aList = new ArrayList();

		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) ab.next();

			if (aBonus.isApplied())
			{
				aList.add(aBonus);
			}
		}

		return aList;
	}

	/**
	 * Get the list of bonuses as a String
	 * @param aString
	 * @return the list of bonuses as a String
	 */
	public boolean getBonusListString(final String aString)
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) ab.next();

			if (aBonus.getBonusInfo().equalsIgnoreCase(aString))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets all the BonusObj's to "active"
	 * @param aPC
	 */
	public void activateBonuses(final PlayerCharacter aPC)
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) ab.next();
			aBonus.setApplied(false);

			if (aBonus.hasPreReqs())
			{
				//TODO: This is a hack to avoid VARs etc in feat defs being qualified
				// for when Bypass feat prereqs is selected. Should we be passing in
				// the BonusObj here to allow it to be referenced in Qualifies statements?
				if (PrereqHandler.passesAll(aBonus.getPrereqList(), aPC, null))
				{
					aBonus.setApplied(true);
				}
				else
				{
					aBonus.setApplied(false);
				}
			}
			else
			{
				aBonus.setApplied(true);
			}
		}
	}

	/**
	 * This function will be required during the continued re-write
	 * of the BonusObj code -- JSC 8/18/03
	 *
	 * @param aBonus
	 */
	public final void addBonusList(final BonusObj aBonus)
	{
		bonusList.add(aBonus);
	}

	/**
	 * Clear the pre requestite list
	 */
	public final void clearPreReq()
	{
		preReqList = null;
	}

	/**
	 * Add a Pre requesite to the prereq list with no level qualifier
	 * @param preReq
	 */
	public final void addPreReq(final Prerequisite preReq)
	{
		addPreReq(preReq, -1);
	}

	/**
	 * Add a Pre requesite to the prereq list with a level qualifier
	 * @param preReq
	 * @param levelQualifier
	 */
	public final void addPreReq(final Prerequisite preReq, final int levelQualifier)
	{
		if ("clear".equals(preReq.getKind()))
		{
			preReqList = null;
		}
		else
		{
			if (preReqList == null)
			{
				preReqList = new ArrayList();
			}
			if (levelQualifier > 0)
				preReq.setLevelQualifier(levelQualifier);
			preReqList.add(preReq);
		}
	}

	/**
	 * Add to the 'save' for the character list
	 * @param aString
	 */
	public final void addSave(final String aString)
	{
		listChar.addToListFor(ListKey.SAVE, aString);
	}

	/**
	 * Add the selected wepaon prof bonus to the character list
	 * @param entry
	 */
	public final void addSelectedWeaponProfBonus(final String entry)
	{
		listChar.addToListFor(ListKey.SELECTED_WEAPON_PROF_BONUS, entry);
	}

	/**
	 * Add the SA to the character list
	 * @param sa
	 */
	public final void addSpecialAbilityToList(final SpecialAbility sa)
	{
		listChar.addToListFor(ListKey.SPECIAL_ABILITY, sa);
	}

	/**
	 * Add to the list of temporary bonuses
	 * @param aBonus
	 */
	public void addTempBonus(final BonusObj aBonus)
	{
		listChar.addToListFor(ListKey.TEMP_BONUS, aBonus);
	}

	/**
	 * Remove from the list of temporary bonuses
	 * @param aBonus
	 */
	public void removeTempBonus(final BonusObj aBonus)
	{
		listChar.removeFromListFor(ListKey.TEMP_BONUS, aBonus);
	}

	/**
	 * Add to the unarmed damage list (or clear the whole list)
	 * @param addString
	 */
	public final void addUdamList(final String addString)
	{
		if (".CLEAR".equals(addString))
		{
			listChar.removeListFor(ListKey.UDAM);
		}
		else
		{
			listChar.addToListFor(ListKey.UDAM, addString);
		}
	}

	/**
	 * Add the U multiplier
	 * @param mult
	 */
	public final void addUmult(final String mult)
	{
		if (".CLEAR".equals(mult))
		{
			listChar.removeListFor(ListKey.UMULT);
		}
		else
		{
			listChar.addToListFor(ListKey.UMULT, mult);
		}
	}

	/**
	 * Add a variable to the variable list
	 * @param level
	 * @param variableName
	 * @param defaultFormula
	 */
	public final void addVariable(final int level, final String variableName, final String defaultFormula)
	{
		if (variableList == null)
		{
			variableList = new VariableList();
		}

		variableList.add(level, variableName, defaultFormula);
	}

	/**
	 * Add a virtual feat to the character list
	 * @param aFeat
	 */
	public final void addVirtualFeat(final Ability aFeat)
	{
		listChar.addToListFor(ListKey.VIRTUAL_FEATS, aFeat);
	}

	/**
	 * Add a list of virtual feats to the character list
	 * @param aFeatList
	 */
	public final void addVirtualFeats(final List aFeatList)
	{
		listChar.addAllToListFor(ListKey.VIRTUAL_FEATS, aFeatList);
	}

	/**
	 * Clear the variable list
	 */
	public final void clearVariableList()
	{
		if (variableList != null)
		{
			variableList.clear();
		}
	}

	/**
	 * Get a list of WeaponProf|ProfType strings from changeProfMap
	 * @param character
	 * @return List
	 */
	public List getChangeProfList(final PlayerCharacter character)
	{
		final List aList = new ArrayList();

		for (Iterator e = changeProfMap.keySet().iterator(); e.hasNext();)
		{
			// aKey will either be:
			//  TYPE.blah
			// or
			//  Weapon Name
			final String aKey = e.next().toString();

			// New proficiency type, such as Martial or Simple
			final String newProfType = changeProfMap.get(aKey).toString();

			if (aKey.startsWith("TYPE."))
			{
				// need to get all items of this TYPE
				for (Iterator eq = EquipmentList.getEquipmentOfType(aKey.substring(5), "").iterator(); eq.hasNext();)
				{
					final String aName = ((Equipment) eq.next()).profName(character);
					aList.add(aName + "|" + newProfType);
				}
			}
			else
			{
				final Equipment aEq = EquipmentList.getEquipmentNamed(aKey);

				if (aEq == null)
				{
					continue;
				}

				final String aName = aEq.profName(character);
				aList.add(aName + "|" + newProfType);
			}
		}

		return aList;
	}

	/**
	 * Takes a string of the form:
	 * Darkvision (60')|Low-light
	 * and builds a hashMap for this object.
	 * It also adds the type (such as Darkvision) to a Global hashMap
	 * @param aString
	 * @param aPC
	 */
	public void setVision(final String aString, final PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			// This is a hack to fix a specific bug.  It is
			// unintelligent.  FIXME XXX
			if (".CLEAR".equals(bString))
			{
				vision = null;

				continue;
			}

			final String cString;
			String dString;

			if (bString.indexOf(',') < 0)
			{
				cString = bString;
				dString = bString;
			}
			else
			{
				final StringTokenizer bTok = new StringTokenizer(bString, ",");
				cString = bTok.nextToken();
				dString = bTok.nextToken();
			}

			if (cString.startsWith(".CLEAR.") || "2".equals(cString))
			{
				if (vision == null)
				{
					continue;
				}

				if (cString.startsWith(".CLEAR."))
				{
					// Strip off the .CLEAR.
					dString = dString.substring(7);
				}

				final Object aKey = vision.get(dString);

				if (aKey != null)
				{
					vision.remove(dString);
				}
			}
			else if (cString.startsWith(".SET.") || "0".equals(cString))
			{
				if (vision == null)
				{
					vision = new HashMap();
				}

				vision.clear();

				if (cString.startsWith(".SET."))
				{
					// Strip off the .SET.
					dString = dString.substring(5);
				}

				// expecting value in form of Darkvision (60')
				final StringTokenizer cTok = new StringTokenizer(dString, "(')");
				final String aKey = cTok.nextToken().trim(); //	 e.g. Darkvision
				final String aVal = cTok.nextToken(); // e.g. 60
				vision.put(aKey, aVal);
			}
			else
			{
				if (vision == null)
				{
					vision = new HashMap();
				}

				// expecting value in form of: Darkvision (60')
				final StringTokenizer cTok = new StringTokenizer(dString, "(')");
				final String aKey = cTok.nextToken().trim(); //	 e.g. Darkvision
				Globals.putVisionMap(aKey);

				String aVal = "0";

				if (cTok.hasMoreTokens())
				{
					aVal = cTok.nextToken(); // e.g. 60
				}

				final Object bObj = vision.get(aKey);

				if (bObj == null)
				{
					vision.put(aKey, aVal);
				}
				else
				{
					if (aPC != null)
					{
						final int b = aPC.getVariableValue(bObj.toString(), "").intValue();

						if (b < aPC.getVariableValue(aVal, "").intValue())
						{
							vision.put(aKey, aVal);
						}
					}
					else
					{
						vision.put(aKey, aVal);
					}
				}
			}
		}
	}

	/**
	 * Retrieve the vision types associated with the object.
	 * Key: vision type, Value: vision range.
	 * @return Map of the vision types associated with the object.
	 */
	public Map getVision()
	{
		return vision;
	}

	/**
	 * Adds Weapons/Armor/Shield names/types to new Proficiency mapping
	 *
	 * @param aString is a list of equipment and new Profs
	 */
	public void addChangeProf(String eqString, String newProf)
	{
		changeProfMap.put(eqString, newProf);
	}

	/**
	 * Returns true if the assocaited item is in the associated list for this object
	 * @param associated
	 * @return true if the assocaited item is in the associated list for this object
	 */
	public final boolean containsAssociated(final String associated)
	{
		if (associatedList == null)
		{
			return false;
		}

		if (associatedList.get(0) instanceof FeatMultipleChoice)
		{
			FeatMultipleChoice fmc;

			for (int i = 0; i < associatedList.size(); ++i)
			{
				fmc = (FeatMultipleChoice) associatedList.get(i);

				final String aString = fmc.toString().toUpperCase();

				if (aString.indexOf(associated) >= 0)
				{
					return true;
				}
			}
		}
		else
		{
			for (int i = 0; i < associatedList.size(); ++i)
			{
				final String aString = (String) associatedList.get(i);

				if (aString.equalsIgnoreCase(associated))
				{
					return true;
				}
			}
		}

		return associatedList.contains(associated);
	}

	/**
	 * Add a natural weapon to the character list, also adds the
	 * appropriate weapon proficiency.
	 *
	 * @param weapon
	 * @param level
	 */
	public void addNaturalWeapon(final Equipment weapon, final int level)
	{
		listChar.addToListFor(ListKey.NATURAL_WEAPONS, weapon);
		addWeaponProfAutos( weapon.getSimpleName() );
	}

	/**
	 * Deactivate all of the bonuses
	 */
	public void deactivateBonuses()
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) ab.next();
			aBonus.setApplied(false);
		}
	}

	/**
	 * Returns true if this object has a pre requestie of the type that
	 * is passed in.
	 *
	 * @param matchType
	 * @return true if this object has a pre requestie of the type that
	 * is passed in
	 */
	public final boolean hasPreReqTypeOf(final String matchType)
	{
		if (getPreReqCount() == 0)
		{
			return false;
		}

		for (int i = 0; i < getPreReqCount(); ++i)
		{
			final Prerequisite prereq = getPreReq(i);

			if (prereq != null)
			{
				if (matchType == null && prereq.getKind() == null)
				{
					return true;
				}
				if (matchType.equalsIgnoreCase(prereq.getKind()))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns true if this object has a variable named variableName
	 * @param variableName
	 * @return true if this object has a variable named variableName
	 */
	public final boolean hasVariableNamed(final String variableName)
	{
		if (variableList == null)
		{
			return false;
		}

		return variableList.hasVariableNamed(variableName);
	}

	/**
	 * Apply the bonus to a PC, pass through object's default bonuslist
	 *
	 * @param aType
	 * @param aName
	 * @param obj
	 * @param aPC
	 * @return the bonus
	 */
	public double bonusTo(final String aType, final String aName, final Object obj, final PlayerCharacter aPC)
	{
		return bonusTo(aType, aName, obj, getBonusList(), aPC);
	}

	/**
	 * Apply the bonus to a PC
	 *
	 * @param aType
	 * @param aName
	 * @param obj
	 * @param aBonusList
	 * @param aPC
	 * @return the bonus
	 */
	public double bonusTo(String aType, String aName, final Object obj, final List aBonusList, final PlayerCharacter aPC)
	{
		if ((aBonusList == null) || (aBonusList.size() == 0))
		{
			return 0;
		}

		double retVal = 0;

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		final String aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();

		if (!dontRecurse && (this instanceof Ability) && !Globals.checkRule(RuleConstants.FEATPRE))
		{
			// SUCK!  This is horrid, but bonusTo is actually recursive with respect to
			// passesPreReqToGain and there is no other way to do this without decomposing the
			// dependencies.  I am loathe to break working code.
			// This addresses bug #709677 -- Feats give bonuses even if you no longer qualify
			dontRecurse = true;

			boolean returnZero = false;

			if (!PrereqHandler.passesAll(this.getPreReqList(), aPC, this))
			{
				returnZero = true;
			}

			dontRecurse = false;

			if (returnZero)
			{
				return 0;
			}
		}

		int iTimes = 1;

		if ("VAR".equals(aType))
		{
			iTimes = Math.max(1, getAssociatedCount());

			//
			// SALIST will stick BONUS:VAR|...
			// into bonus list so don't multiply
			//
			String choiceString = getChoiceString();
			if (choiceString.startsWith("SALIST|") && (choiceString.indexOf("|VAR|") >= 0))
			{
				iTimes = 1;
			}
		}

		for (Iterator b = aBonusList.iterator(); b.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) b.next();
			String bString = aBonus.toString().toUpperCase();

			if (getAssociatedCount() != 0)
			{
				int span = 4;
				int idx = bString.indexOf("%VAR");

				if (idx == -1)
				{
					idx = bString.indexOf("%LIST|");
					span = 5;
				}

				if (idx >= 0)
				{
					final String firstPart = bString.substring(0, idx);
					final String secondPart = bString.substring(idx + span);

					for (int i = 1; i < getAssociatedCount(); ++i)
					{
						final String xString = new StringBuffer().append(firstPart).append(getAssociated(i)).append(secondPart)
							.toString().toUpperCase();
						retVal += calcBonus(xString, aType, aName, aTypePlusName, obj, iTimes, aBonus, aPC);
					}

					bString = new StringBuffer().append(firstPart).append(getAssociated(0)).append(secondPart).toString()
						.toUpperCase();
				}
			}

			retVal += calcBonus(bString, aType, aName, aTypePlusName, obj, iTimes, aBonus, aPC);
		}

		return retVal;
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 * @param aBonus
	 * @param anObj
	 * @param aPC
	 * @return bonus
	 */
	public double calcBonusFrom(final BonusObj aBonus, final Object anObj, PlayerCharacter aPC)
	{
		return calcBonusFrom(aBonus, anObj, null, aPC);
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 * @param aBonus
	 * @param anObj
	 * @param listString
	 * @param aPC
	 * @return bonus
	 */
	public double calcBonusFrom(
			final BonusObj  aBonus,
			final Object    anObj,
			final String    listString,
			PlayerCharacter aPC)
	{
		int iTimes = 1;

		final String aType = aBonus.getTypeOfBonus();

		if ("VAR".equals(aType))
		{
			iTimes = Math.max(1, getAssociatedCount());

			String choiceString = getChoiceString();
			if (choiceString.startsWith("SALIST|") && (choiceString.indexOf("|VAR|") >= 0))
			{
				iTimes = 1;
			}
		}

		return calcPartialBonus(iTimes, aBonus, anObj, listString, aPC);
	}

	/**
	 * Clear the associated list for this object
	 */
	public final void clearAssociated()
	{
		associatedList = null;
	}

	/**
	 * if a class implements the Cloneable interface then it should have a
	 * public" 'clone ()' method It should be declared to throw
	 * CloneNotSupportedException', but subclasses do not need the "throws"
	 * declaration unless their 'clone ()' method will throw the exception
	 * Thus subclasses can decide to not support 'Cloneable' by implementing
	 * the 'clone ()' method to throw 'CloneNotSupportedException'
	 * If this rule were ignored and the parent did not have the "throws"
	 * declaration then subclasses that should not be cloned would be forced
	 * to implement a trivial 'clone ()' to satisfy inheritance
	 * final" classes implementing 'Cloneable' should not be declared to
	 * throw 'CloneNotSupportedException" because their implementation of
	 * clone ()' should be a fully functional method that will not
	 * throw the exception.
	 * @return cloned object
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException
	{
		final PObject retVal = (PObject) super.clone();
		retVal.stringChar = new StringKeyMap();
		retVal.stringChar.addAllCharacteristics(stringChar);
		retVal.integerChar = new IntegerKeyMap();
		retVal.integerChar.addAllCharacteristics(integerChar);
		retVal.listChar = new ListKeyMapToList();
		retVal.listChar.addAllLists(listChar);
		//SAVE is a special case: starts out empty
		// because the saveList is based on user selections (merton_monk@yahoo.com)
		retVal.listChar.removeListFor(ListKey.SAVE);

		retVal.setName(name);
		retVal.visible = visible;
		retVal.setKeyName(keyName);
		retVal.spellSupport = (SpellSupport) spellSupport.clone();

		// added 04 Aug 2003 by sage_sam -- bug#765749
		// need to copy map correctly during a clone
		if (sourceMap != null)
		{
			retVal.sourceMap = new HashMap();
			retVal.sourceMap.putAll(this.sourceMap);
		}

		retVal.changeProfMap = new HashMap(changeProfMap);

		if (preReqList != null)
		{
			retVal.preReqList = (ArrayList) preReqList.clone();
		}

		if (associatedList != null)
		{
			retVal.associatedList = (ArrayList) associatedList.clone();
		}

		if (bonusList != null)
		{
			retVal.bonusList = (ArrayList) bonusList.clone();
			retVal.ownBonuses();
		}

		if (variableList != null)
		{
			retVal.variableList = (VariableList) variableList.clone();
		}

		if (bonusMap != null)
		{
			retVal.bonusMap = new HashMap(bonusMap);
		}

		retVal.vision = vision;

		if ((levelAbilityList != null) && !levelAbilityList.isEmpty())
		{
			retVal.levelAbilityList = new ArrayList();

			for (Iterator it = levelAbilityList.iterator(); it.hasNext();)
			{
				LevelAbility ab = (LevelAbility) it.next();
				ab = (LevelAbility) ab.clone();
				ab.setOwner(retVal);
				retVal.levelAbilityList.add(ab);
			}
		}

		return retVal;
	}

	public int compareTo(final Object obj)
	{
		if (obj != null)
		{
			//this should throw a ClassCastException for non-PObjects, like the Comparable interface calls for
			return this.name.compareToIgnoreCase(((PObject) obj).name);
		}
		return 1;
	}

	/**
	 * Set's all the BonusObj's to this creator
	 */
	public void ownBonuses()
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) ab.next();
			aBonus.setCreatorObject(this);
		}
	}

	/**
	 * Returns the pre requesites as an HTML String
	 * @param aPC
	 * @return the pre requesites as an HTML String
	 */
	public final String preReqHTMLStrings(final PlayerCharacter aPC)
	{
		if (getPreReqCount() == 0)
		{
			return "";
		}

		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, null, preReqList, true);
	}

	/**
	 * Returns the pre requesites as an HTML String with a header
	 * @param aPC
	 * @param includeHeader
	 * @return the pre requesites as an HTML String
	 */
	public String preReqHTMLStrings(final PlayerCharacter aPC, final boolean includeHeader)
	{
		if (getPreReqCount() == 0)
		{
			return "";
		}

		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, null, preReqList, includeHeader);
	}

	/**
	 * Returns the pre requesites as an HTML String given an object
	 * @param aPC
	 * @param p
	 * @return the pre requesites as an HTML String given an object
	 */
	public final String preReqHTMLStrings(final PlayerCharacter aPC, final PObject p)
	{
		if (getPreReqCount() == 0)
		{
			return "";
		}

		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, p, preReqList, true);
	}

	/**
	 * Creates the requirement string for printing.
	 * @return the requirement string for printing
	 */
	public final String preReqStrings()
	{
		if (getPreReqCount() == 0)
		{
			return "";
		}

		return PrereqHandler.toHtmlString(preReqList);
	}

	/**
	 * Put the key/value pair into the bonus map
	 * @param aKey
	 * @param aVal
	 */
	public void putBonusMap(final String aKey, final String aVal)
	{
		getBonusMap().put(aKey, aVal);
	}

	/**
	 * Set the CHOICE string
	 * @param aString
	 */
	public void setChoiceString(final String aString)
	{
		stringChar.setCharacteristic(StringKey.CHOICE_STRING, aString);
	}

	/**
	 * Get the CHOICE string
	 * @return the CHOICE string
	 */
	public final String getChoiceString()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.CHOICE_STRING);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get the choices for this PC
	 * @param aChoice
	 * @param aPC
	 */
	public final void getChoices(
			final String          aChoice,
			final PlayerCharacter aPC)
	{
		final List availableList = new ArrayList();
		final List selectedList  = new ArrayList();
		ChooserUtilities.getChoices(this, aChoice, availableList, selectedList, aPC);
	}

	/**
	 * Get the choices for this PC
	 * @param aChoice
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 */
	final void getChoices(
			final String          aChoice,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC)
	{
		ChooserUtilities.getChoices(this, aChoice, availableList, selectedList, aPC);
	}

	/**
	 * Set the DR
	 * @param drString
	 */
//	public void setDR(String drString)
//	{
//		if (".CLEAR".equals(drString))
//		{
//			drString = null;
//		 }
//		stringChar.setCharacteristic(StringKey.DR_FORMULA, drString);
//	}

	public void addDR(DamageReduction aDR)
	{
		drList.add(aDR);
	}

	public void clearDR()
	{
		drList.clear();
	}

	/**
	 * Get the DR
	 * @return the DR
	 */
//	public String getDR()
//	{
//		return stringChar.getCharacteristic(StringKey.DR_FORMULA);
//	}

	public List getDRList()
	{
		return Collections.unmodifiableList(drList);
	}

	/**
	 * Set the Key Name
	 * @param aString
	 */
	public final void setKeyName(final String aString)
	{
		keyName = aString;
	}

	/**
	 * Get the Key Name
	 * @return Key Name
	 */
	public final String getKeyName()
	{
		return keyName;
	}

	/**
	 * Set the name (sets keyname also)
	 * @param aString
	 */
	public void setName(final String aString)
	{
		if (!aString.endsWith(".MOD"))
		{
			fireNameChanged(name, aString);
			name = aString;
			this.setKeyName(aString);
		}
	}

	/**
	 * Get name
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the item as new flag
	 * @param newItem
	 */
	public final void setNewItem(final boolean newItem)
	{
		this.isNewItem = newItem;
	}

	///////////////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	///////////////////////////////////////////////////////////////////////

	/**
	 * Returns true if the item is new
	 * @return true if the item is new
	 */
	public final boolean isNewItem()
	{
		return isNewItem;
	}

	/**
	 * Set the output name for the item
	 * @param aString
	 */
	public final void setOutputName(final String aString)
	{
		String newName = aString;

		//process the intended output name, replacing [NAME] token
		final int nameIndex = newName.indexOf("[NAME]");
		if (nameIndex >= 0)
		{
			final StringBuffer sb = new StringBuffer(newName.substring(0, nameIndex));

			//and rephrasing parenthetical name components
			sb.append(getPreFormatedOutputName());

			if (newName.length() > (nameIndex + 6))
			{
				sb.append(newName.substring(nameIndex + 6));
			}
			newName = sb.toString();
		}
		stringChar.setCharacteristic(StringKey.OUTPUT_NAME, newName);
	}

	/**
	 * Get the output name of the item
	 * @return the output name of the item
	 */
	public final String getOutputName()
	{
		String outputName = stringChar.getCharacteristic(StringKey.OUTPUT_NAME);
		// if no OutputName has been defined, just return the regular name
		if (outputName == null || outputName.length() == 0)
		{
			return name;
		}

		return outputName;
	}

	/**
	 * Get the pre requesite at an index
	 * @param i
	 * @return the pre requesite at an index
	 */
	public final Prerequisite getPreReq(final int i)
	{
		return (Prerequisite) preReqList.get(i);
	}

	/**
	 * Get the number of pre requesites
	 * @return the number of pre requesites
	 */
	public final int getPreReqCount()
	{
		if (preReqList == null)
		{
			return 0;
		}

		return preReqList.size();
	}

	/**
	 * Get the list of pre-requesites
	 * @return the list of pre-requesites
	 */
	public final ArrayList getPreReqList()
	{
		return preReqList;
	}

	/**
	 * Set the qualify string
	 * @param aString
	 */
	public final void setQualifyString(final String aString)
	{
		stringChar.setCharacteristic(StringKey.QUALIFY, aString);
	}

	/**
	 * Get the qualify string
	 * @return the qualify string
	 */
	public final String getQualifyString()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.QUALIFY);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Set the SR (or clear it)
	 * @param newSR
	 */
	public void setSR(String newSR)
	{
		if (".CLEAR".equals(newSR))
		{
			newSR = null;
		 }
		stringChar.setCharacteristic(StringKey.SR_FORMULA, newSR);
	}

	/**
	 * Get the SR Formula
	 * @return the SR Formula
	 */
	public String getSRFormula()
	{
		return stringChar.getCharacteristic(StringKey.SR_FORMULA);
	}

	/**
	 * Set the source file for this object
	 * @param sourceFile
	 */
	public final void setSourceFile(final String sourceFile)
	{
		stringChar.setCharacteristic(StringKey.SOURCE_FILE, sourceFile);
	}

	/**
	 * Get the source file for this object
	 * @return the source file for this object
	 */
	public final String getSourceFile()
	{
		return stringChar.getCharacteristic(StringKey.SOURCE_FILE);
	}

	/**
	 * Set the map of modfied sources
	 * @param arg
	 */
	public final void setModSourceMap(final Map arg)
	{
		if (arg != null)
		{
			if (modSourceMap == null)
			{
				modSourceMap = new HashMap();
			}

			modSourceMap.putAll(arg);
		}
	}

	/**
	 * Set the map of sources
	 * @param arg
	 */
	public final void setSourceMap(final Map arg)
	{
		// Don't clear the map, otherwise the SOURCEPAGE:
		// entries on each line will screw it all up

		// It may seem strange to cycle through the map and
		// not let the passed in source override the setting
		// of the already existing source. The only way this
		// happens is if a .MOD is used, but the way the
		// source loading happens, when a .MOD is loaded
		// after everything else is loaded, the source is
		// set to whatever source was loaded last, which may
		// not be the source of the .MOD.
		for (Iterator i = arg.keySet().iterator(); i.hasNext();)
		{
			final String key = (String) i.next();
			if (sourceMap.get(key) == null)
			{
				sourceMap.put(key, arg.get(key));
			}
		}

		// If this comes from a .MOD line and SOURCEPAGE is set,
		// copy the MOD's sourcemap and then set the PAGE too.
		if (modSourceMap != null) // && arg.get("PAGE") != null)
		{
			sourceMap.putAll(modSourceMap);
			sourceMap.putAll(arg);
		}
	}

	/**
	 * Get the map of sources
	 * @return the map of sources
	 */
	public final Map getSourceMap()
	{
		return sourceMap;
	}

	/**
	 * Get the source as a String in short form for display
	 * @param maxNumberofChars
	 * @return the source as a String in short form for display
	 */
	public final String getSourceShort(final int maxNumberofChars)
	{
		String shortString = SourceUtilities.returnSourceInForm(this, Constants.SOURCESHORT, false);

		// When I say short, I mean short!
		if (shortString.length() > maxNumberofChars)
		{
			shortString = shortString.substring(0, maxNumberofChars);
		}

		return shortString;
	}

	/**
	 * Get the source given a key
	 * @return source as a String
	 */
	public final String getSourceWithKey(final String key)
	{
		return (String) sourceMap.get(key);
	}

	/**
	 * Get the source
	 * @return the source
	 */
	public String getSource()
	{
		return SourceUtilities.returnSourceInForm(this, Globals.getSourceDisplay(), true);
	}

	/**
	 * Get the source date
	 * @return the source date
	 */
	public String getSourceDate()
	{
		return SourceUtilities.returnSourceInForm(this, Constants.SOURCEDATE, false);
	}

	/**
	 * Get the source date as an int
	 * @return the source date as an int
	 */
	public int getSourceDateValue()
	{
		String date = getSourceDate();
		if ("".equals(date))
		{
			return 0;
		}
		String[] dates = date.split("-");
		if (dates.length != 2)
		{
			return 0;
		}
		int year = (new Integer(dates[0])).intValue() - 2000;
		int month = (new Integer(dates[1])).intValue();

		return year*12 + month;
	}

	/**
	 * Get the SA by name
	 * @param aName
	 * @return the SA
	 */
	public final SpecialAbility getSpecialAbilityNamed(final String aName)
	{
		List l = getListFor(ListKey.SPECIAL_ABILITY);
		if (l != null)
		{
			for (Iterator i = l.iterator(); i.hasNext();)
			{
				final SpecialAbility sa = (SpecialAbility) i.next();

				if (sa.getName().equalsIgnoreCase(aName))
				{
					return sa;
				}
			}
		}

		return null;
	}

	/**
	 * Returns a hardcoded "POBJECT|" + name of this object
	 * @return "POBJECT|" + name of this object
	 */
	public String getSpellKey()
	{
		return "POBJECT|" + name;
	}

	/**
	 * Add automatic languages
	 * @param aString
	 */
	public final void addLanguageAuto(final String langName)
	{
		ListKey autoLanguageListKey = ListKey.AUTO_LANGUAGES;
		if (".CLEAR".equals(langName))
		{
			listChar.removeListFor(autoLanguageListKey);
		}
		else if ("ALL".equals(langName))
		{
			listChar.addAllToListFor(autoLanguageListKey, Globals.getLanguageList());
		}
		else if (langName.startsWith("TYPE=") || langName.startsWith("TYPE."))
		{
			final String type = langName.substring(5);
			List langList = Globals.getLanguageList();
			langList = Globals.getLanguagesFromListOfType(langList, type);
			listChar.addAllToListFor(autoLanguageListKey, langList);
		}
		else
		{
			final Language lang = Globals.getLanguageNamed(langName);

			if (lang != null)
			{
				listChar.addToListFor(autoLanguageListKey, lang);
			}
		}
	}

	public void clearLanguageAuto()
	{
		listChar.removeListFor(ListKey.AUTO_LANGUAGES);
	}

	/**
	 * Get the user defined type by index
	 * @param i
	 * @return the user defined type by index
	 */
	public String getMyType(final int i)
	{
		if (i < getMyTypeCount())
		{
			return (String) getElementInList(ListKey.TYPE, i);
		}

		return null;
	}

	/**
	 * Get the number of user defined types
	 * @return the number of user defined types
	 */
	public int getMyTypeCount()
	{
		return getSafeSizeOfListFor(ListKey.TYPE);
	}

	/**
	 * This method gets access to the spell list.
	 * @return List
	 */
	public List getSpellList()
	{
		return spellSupport.getSpellList(-1);
	}

	/**
	 * Clear the special ability list
	 */
	public final void clearSpecialAbilityList()
	{
		listChar.removeListFor(ListKey.SPECIAL_ABILITY);
	}

	/**
	 * Get the type of PObject
	 * @return the type of PObject
	 */
	public String getType()
	{
		return getTypeUsingFlag(false);
	}

	/**
	 * Returns false
	 *
	 * This method is meant to be overloaded by those classes that
	 * can have hidden types, which are currently Equipment, Feat and
	 * Skill.
	 *
	 * @param idx
	 * @return false
	 */
	boolean isTypeHidden(final int idx)
	{
		return false;
	}

	/**
	 *
	 * @param bIgnoreHidden Flag to ignore "hidden" types.
	 * @return type
	 */
	public String getTypeUsingFlag(final boolean bIgnoreHidden)
	{
		final int x = getMyTypeCount();

		if (x == 0)
		{
			return "";
		}

		final StringBuffer aType = new StringBuffer(x * 5);

		for (int i = 0; i < x; ++i)
		{
			if (bIgnoreHidden && isTypeHidden(i))
			{
				continue;
			}
				aType.append((i == 0) ? "" : ".").append(getMyType(i));
			}

		return aType.toString();
	}

	/**
	 * If aType begins with an &#34; (Exclamation Mark) the &#34; will be
	 * removed before checking the type.
	 *
	 * @param aType
	 * @return Whether the item is of this type
	 */
	public boolean isType(final String aType)
	{
		final String myType;

		if ((aType.length() > 0) && (aType.charAt(0) == '!'))
		{
			myType = aType.substring(1).toUpperCase();
		}
		else
		{
			myType = aType.toUpperCase();
		}

		return containsInList(ListKey.TYPE, myType);
	}

	/**
	 * Deal with the type, whether to ADD, REMOVE it etc.
	 * @param aString
	 */
	public void setTypeInfo(final String aString)
	{
		boolean bRemove = false;
		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase().trim(), ".");

		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();

			if (bRemove)
			{
				removeMyType(aType);
				bRemove = false;
			}
			else if ("ADD".equals(aType))
			{
				bRemove = false;
			}
			else if ("REMOVE".equals(aType))
			{
				bRemove = true;
			}
			else if ("CLEAR".equals(aType))
			{
				clearMyType();
			}
			else if (!containsInList(ListKey.TYPE, aType))
			{
				doGlobalTypeUpdate(aType);
				addMyType(aType);
			}
		}
	}

	/**
	 * Get the variable by index
	 * @param i
	 * @return the variable by index
	 */
	public final Variable getVariable(final int i)
	{
		if (variableList != null)
		{
			return variableList.getVariable(i);
		}
		return null;
	}

	/**
	 * This gets the entire definition for a variable, | values and all
	 * <p/>
	 * not-yet-deprecated This should be replaced by getVariable
	 * @param i
	 * @return variable definition
	 */
	public final String getVariableDefinition(final int i)
	{
		if (variableList != null)
		{
			return variableList.getDefinition(i);
		}
		return null;
	}

	/**
	 * This gets an unmodifiable representation of a variable
	 * @return Iterator
	 */
	public final Iterator getVariableIterator()
	{
		if (variableList == null)
		{
			return EmptyIterator.EMPTY_ITERATOR;
		}

		return variableList.iterator();
	}

	/**
	 * Parse the output name to get a useable Name token
	 * @param aString
	 * @param aPC
	 * @return the output name to get a useable Name token
	 */
	public String parseOutputName(final String aString, final PlayerCharacter aPC)
	{
		final int varIndex = aString.indexOf('|');

		if (varIndex <= 0)
		{
			return (aString);
		}

		final StringTokenizer varTokenizer = new StringTokenizer(aString, "|");

		final String preVarStr = varTokenizer.nextToken();

		final ArrayList varArray = new ArrayList();
		final ArrayList tokenList = new ArrayList();

		while (varTokenizer.hasMoreElements())
		{
			final String token = varTokenizer.nextToken();
			tokenList.add(token.toUpperCase());
			varArray.add(aPC.getVariableValue(token, ""));
		}

		final StringBuffer result = new StringBuffer();
		int varCount = 0;
		int subIndex = preVarStr.indexOf('%');
		int lastIndex = 0;

		while (subIndex >= 0)
		{
			if (subIndex > 0)
			{
				result.append(preVarStr.substring(lastIndex, subIndex));
			}

			final String token = (String) tokenList.get(varCount);
			final Float val = (Float) varArray.get(varCount);

			if (token.endsWith(".INTVAL"))
			{
				result.append(String.valueOf(val.intValue()));
			}
			else
			{
				result.append(val.toString());
			}

			lastIndex = subIndex + 1;
			varCount++;
			subIndex = preVarStr.indexOf('%', lastIndex);
		}

		if (preVarStr.length() > lastIndex)
		{
			result.append(preVarStr.substring(lastIndex));
		}

		return (result.toString());
	}

	/**
	 * remove the associated item from the list
	 * @param associated
	 * @return true if successful
	 */
	public final boolean removeAssociated(final String associated)
	{
		if (associatedList == null)
		{
			return false;
		}

		final boolean ret = associatedList.remove(associated);

		if (associatedList.size() == 0)
		{
			associatedList = null;
		}

		return ret;
	}

	/**
	 * Remove the bonus object from the bonus list
	 * @param aBonus
	 */
	public void removeBonusList(final BonusObj aBonus)
	{
		getBonusList().remove(aBonus);
	}

	/**
	 * Remove the save
	 * @param bonusString
	 */
	public final void removeSave(final String bonusString)
	{
		boolean b = listChar.removeFromListFor(ListKey.SAVE, bonusString);
		if (!b) {
			Logging.errorPrint("removeSave: Could not find bonus: " + bonusString + " in saveList.");
		}
	}

	/**
	 * Remove user defined types
	 * @param aString
	 */
	public final void removeType(final String aString)
	{
		final String typeString = aString.toUpperCase().trim();
		final StringTokenizer aTok = new StringTokenizer(typeString, ".");

		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();
			removeMyType(aType);
		}
	}

	/**
	 * Reset (Clear) the temporary bonus list
	 */
	public void resetTempBonusList()
	{
		listChar.removeListFor(ListKey.TEMP_BONUS);
	}

	/**
	 * Set the region string
	 * @param arg
	 */
	public final void setRegionString(final String arg)
	{
		stringChar.setCharacteristic(StringKey.REGION, arg);
	}

	/**
	 * Get the region string
	 * @return the region string
	 */
	public final String getRegionString()
	{
		return stringChar.getCharacteristic(StringKey.REGION);
	}

	/**
	 * Set the remove list for the character list
	 * @param arg
	 */
	public final void setRemoveString(final String arg)
	{
		if (arg.equals(".CLEAR"))
		{
			listChar.removeListFor(ListKey.REMOVE_STRING_LIST);
		}
		else
		{
			listChar.addToListFor(ListKey.REMOVE_STRING_LIST, arg);
		}
	}

	/**
	 * Check the removals list
	 * @param aPC
	 */
	public void checkRemovals(PlayerCharacter aPC)
	{
		// only feat removal is supported atm
		if (!containsListFor(ListKey.REMOVE_STRING_LIST))
			return;
		for (Iterator ri = getListFor(ListKey.REMOVE_STRING_LIST).iterator(); ri.hasNext();)
		{
			checkRemoval(aPC, (String)ri.next());
		}
	}

	/**
	 * Check the removal of x from a PC, only supports Feats at the moment
	 * @param aPC
	 * @param removeString
	 */
	private void checkRemoval(PlayerCharacter aPC, String removeString)
	{
		String remString = removeString.substring(removeString.indexOf("|")+1);
		if (this instanceof PCClass)
		{
			int lev = Integer.parseInt(removeString.substring(0, removeString.indexOf("|")));
			PCClass aClass = (PCClass)this;
			if (aClass.getLevel() != lev)
				return;
		}
		if (!remString.startsWith("FEAT("))
			return;
		int i = remString.indexOf("(");
		int k = remString.lastIndexOf(")");
		final StringTokenizer aTok = new StringTokenizer(remString.substring(i+1,k),"(),", false);
		if (aTok.countTokens() == 0)
			return; // nothing to do?
		List theFeatList = new ArrayList(); // don't remove virtual or mfeats
		while (aTok.hasMoreTokens())
		{
			final String arg = aTok.nextToken();
			// could be a TYPE of feat
			if (arg.startsWith("TYPE."))
			{
				final String theType = arg.substring(5);
				for (Iterator it = aPC.getRealFeatsIterator(); it.hasNext();)
				{
					Ability aFeat = (Ability)it.next();
					if (aFeat.isType(theType) && !theFeatList.contains(aFeat))
						theFeatList.add(aFeat);
				}
			}
			else if (arg.startsWith("CLASS."))
			{
				PCClass aClass = aPC.getClassNamed(arg.substring(6));
				if (aClass != null)
				{
					for (Iterator iter = aPC.getLevelInfo().iterator(); iter.hasNext();)
					{
						final PCLevelInfo element = (PCLevelInfo) iter.next();
						if (element.getClassKeyName().equalsIgnoreCase(aClass.getName()))
						{
							for (Iterator fi = element.getObjects().iterator(); fi.hasNext();)
							{
								Ability aFeat = (Ability)fi.next();
								if (!theFeatList.contains(aFeat))
									theFeatList.add(aFeat);
							}
						}
					}

				}
			}
			else if (arg.equals("CHOICE"))
			{
				Iterator anIt  = aPC.getRealFeatsIterator();

				while (anIt.hasNext())
				{
					theFeatList.add(anIt.next());
				}
			}
			// or it's a specifically named feat
			else
			{
				Ability aFeat = aPC.getFeatNamed(arg);
				if (aFeat != null && !theFeatList.contains(aFeat))
					theFeatList.add(aFeat);
			}
		}
		int remCount = theFeatList.size();
		if (remString.length() > k + 1)
		{
			final String rString = remString.substring(k+1);
			if (!rString.equalsIgnoreCase("ALL"))
				remCount = Integer.parseInt(rString);
		}
		if (remCount != theFeatList.size() && theFeatList.size()>0)
		{
			final ChooserInterface chooser = ChooserFactory.getChooserInstance();
			chooser.setPoolFlag(true); // user is not required to make any changes
			chooser.setAllowsDups(false); // only stackable feats can be duped
			chooser.setVisible(false);
			chooser.setPool(remCount);

			String title = "Select for removal";
			chooser.setTitle(title);
			ArrayList selectedList = new ArrayList();
			Globals.sortChooserLists(theFeatList, selectedList);

			for (; ;)
			{
				chooser.setAvailableList(theFeatList);
				chooser.setSelectedList(selectedList);
				chooser.setVisible(true);

				final int selectedSize = chooser.getSelectedList().size();

				if (remCount > 0)
				{
					if (selectedSize != remCount)
					{
						ShowMessageDelegate.showMessageDialog("You must make " + (remCount - selectedSize) + " more selection(s).",
							Constants.s_APPNAME, MessageType.INFORMATION);

						continue;
					}
				}

				break;
			}
			for (int ci = 0; ci < chooser.getSelectedList().size(); ci++)
			{
				final String chosenItem = (String) chooser.getSelectedList().get(ci);
				AbilityUtilities.modFeat(aPC, null, chosenItem, false, false);
			}
		}
		else if (remCount == theFeatList.size())
		{
			for (int ix = theFeatList.size() - 1; ix >= 0; ix--)
			{
				Ability aFeat = (Ability) theFeatList.get(ix);
				AbilityUtilities.modFeat(aPC, null, aFeat.getName(), false, false);
				if (ix > theFeatList.size())
					ix = theFeatList.size();
			}
		}
	}

	/**
	 * Add auto array
	 * @param aList
	 */
	public final void addAutoArray(final List aList)
	{
		listChar.addAllToListFor(ListKey.AUTO_ARRAY, aList);
	}

	/**
	 * Add auto array
	 * @param arg
	 */
	public final void addAutoArray(final String arg)
	{
		listChar.addToListFor(ListKey.AUTO_ARRAY, arg);
	}

	/**
	 * Add the select armor proficiencies to the list
	 * @param aList
	 */
	public final void addSelectedArmorProfs(final List aList)
	{
		//This can't do a direct addAll on listChar because this does duplication removal
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			final String aString = (String) i.next();

			if (!containsInList(ListKey.SELECTED_ARMOR_PROF, aString))
			{
				listChar.addToListFor(ListKey.SELECTED_ARMOR_PROF, aString);
			}
		}
	}

	/**
	 * Add the selected shield proficiencies to the list
	 * @param aList
	 */
	public final void addSelectedShieldProfs(final List aList)
	{
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			final String aString = (String) i.next();
			if (!containsInList(ListKey.SELECTED_SHIELD_PROFS, aString))
			{
				listChar.addToListFor(ListKey.SELECTED_SHIELD_PROFS, aString);
			}
		}
	}

	/**
	 * Clear the auto list
	 */
	public final void clearAutoList()
	{
		listChar.removeListFor(ListKey.AUTO_ARRAY);
	}

	/**
	 * This does a partial clear of the auto list, removing any entries
	 * carrying the supplied tag
	 * @param tag The type to be removed e.g. WEAPONPROF
	 */
	public final void clearAutoListForTag(String tag)
	{
		List autoList = getListFor(ListKey.AUTO_ARRAY);
		if (autoList == null)
		{
			return;
		}
		for (Iterator iter = autoList.iterator(); iter.hasNext();)
		{
			String element = (String) iter.next();
			if (element.startsWith(tag))
			{
				iter.remove();
			}
		}
	}

	/**
	 * Set the campaign source
	 * @param arg
	 */
	public void setSourceCampaign(final Campaign arg)
	{
		sourceCampaign = arg;
	}

	/**
	 * This method returns a reference to the Campaign that this object
	 * originated from
	 *
	 * @return Campaign instance referencing the file containing the
	 *         source for this object
	 */
	public Campaign getSourceCampaign()
	{
		return sourceCampaign;
	}

	/**
	 * gets the bonuses to a stat based on the stat Index
	 * @param statIdx
	 * @param aPC
	 * @return stat mod
	 */
	public int getStatMod(final int statIdx, final PlayerCharacter aPC)
	{
		final List statList = SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return 0;
		}

		final String aStat = ((PCStat) statList.get(statIdx)).getAbb();

		return (int) bonusTo("STAT", aStat, aPC, aPC);
	}

	/**
	 * Add the level and ability to the level ability list
	 * @param aLevel
	 * @param aString
	 * @return the LevelAbility
	 */
	public LevelAbility addAddList(final int aLevel, final String aString)
	{
		if (levelAbilityList == null)
		{
			levelAbilityList = new ArrayList();
		}

		if (aString.startsWith(".CLEAR"))
		{
			if (".CLEAR".equals(aString))
			{
				levelAbilityList.clear();
			}
			else if (aString.indexOf(".LEVEL") >= 0)
			{
				int level;

				try
				{
					level = Integer.parseInt(aString.substring(12));
				}
				catch (NumberFormatException e)
				{
					Logging.errorPrint("Badly formed addAddList attribute: " + aString.substring(12));
					level = -1;
				}

				if (level >= 0)
				{
					for (int x = levelAbilityList.size() - 1; x >= 0; --x)
					{
						final LevelAbility ability = (LevelAbility) levelAbilityList.get(x);

						if (ability.level() == level)
						{
							levelAbilityList.remove(x);
						}
					}
				}
			}
		}
		else
		{
			final LevelAbility la = LevelAbility.createAbility(this, aLevel, aString);
			levelAbilityList.add(la);

			return la;
		}

		return null;
	}

	/**
	 * Make choices for the PC (just calls getChoices)
	 * @param aPC
	 */
	public void makeChoices(final PlayerCharacter aPC)
	{
		getChoices(getChoiceString(), aPC);
	}

	/**
	 * Returns true if the PC has a bonus that is currently applied
	 * @param aPC
	 * @param anObj
	 * @return true if the PC has a bonus that is currently applied
	 */
	public boolean passesPreApplied(final PlayerCharacter aPC, final PObject anObj)
	{
		if (!aPC.getUseTempMods())
		{
			return false;
		}

		// If anObj is null, use this objects tempBonusList
		if (anObj == null)
		{
			for (Iterator aB = getTempBonusList().iterator(); aB.hasNext();)
			{
				final BonusObj aBonus = (BonusObj) aB.next();
				final Object abT = aBonus.getTargetObject();

				if (abT instanceof PlayerCharacter)
				{
					final PlayerCharacter bPC = (PlayerCharacter) abT;

					if (aBonus.isApplied() && (bPC == aPC))
					{
						return true;
					}
				}
			}

			return false;
		}

		// else use the anObj's tempBonusList
		for (Iterator aB = anObj.getTempBonusList().iterator(); aB.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) aB.next();
			final Object abT = aBonus.getTargetObject();

			if (abT instanceof Equipment)
			{
				final Equipment aTarget = (Equipment) abT;

				if (aBonus.isApplied() && aTarget.equals(anObj))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Get the Product Identity description String
	 * @return the Product Identity description String
	 */
	public String piDescString()
	{
		return piDescString(true);
	}

	/**
	 * In some cases, we need a PI-formatted string to place within a
	 * pre-existing <html> tag
	 * @return PI description
	 */
	public String piDescSubString()
	{
		return piDescString(false);
	}

	/**
	 * Get the Product Identity string
	 * @return the Product Identity string
	 */
	public String piString()
	{
		return piString(true);
	}

	/**
	 * In some cases, we need a PI-formatted string to place within
	 * a pre-existing <html> tag
	 * @return PI String with no header
	 */
	public String piSubString()
	{
		return piString(false);
	}

	/**
	 * Remove an ability gained via a level
	 * @param aLevel
	 * @param aString
	 * @return true if successful
	 */
	public boolean removeLevelAbility(final int aLevel, final String aString)
	{
		for (int x = levelAbilityList.size() - 1; x >= 0; --x)
		{
			final LevelAbility ability = (LevelAbility) levelAbilityList.get(x);

			if ((ability.level() == aLevel) && (ability.getTagData().equals(aString)))
			{
				levelAbilityList.remove(x);

				return true;
			}
		}

		return false;
	}

	public String toString()
	{
		return name;
	}

	protected int getSR(final PlayerCharacter aPC)
	{
		final String srFormula = getSRFormula();

		//if there's a current PC, go ahead and evaluate the formula
		if ((srFormula != null) && (aPC != null))
		{
			return aPC.getVariableValue(srFormula, "").intValue();
		}

		return 0;
	}

	protected final void setVariable(final int idx, final int level, final String variableName, final String defaultFormula)
	{
		if (variableList == null)
		{
			variableList = new VariableList();
		}

		variableList.set(idx, level, variableName, defaultFormula);
	}

	protected final void addAllVariablesFrom(final PObject other)
	{
		if (other.getVariableCount() > 0)
		{
			if (variableList == null)
			{
				variableList = new VariableList();
			}

			variableList.addAll(other.variableList);
		}
	}

	/**
	 * Get the PCC text with the saved name
	 * @return the PCC text with the saved name
	 */
	public String getPCCText()
	{
		return getPCCText(true);
	}

	/**
	 * Get the PCC text
	 * @param saveName
	 * @return PCC text
	 */
	protected String getPCCText(final boolean saveName)
	{
		Iterator e;
		String aString;
		final StringBuffer txt = new StringBuffer(200);

		if (saveName)
		{
			txt.append(getName());
		}

		if (getNameIsPI())
		{
			txt.append("\tNAMEISPI:Y");
		}

		String outputName = stringChar.getCharacteristic(StringKey.OUTPUT_NAME);
		if ((outputName != null) && (outputName.length() > 0) && !outputName.equals(getName()))
		{
			txt.append("\tOUTPUTNAME:").append(outputName);
		}

		aString = getDescription();

		if (aString.length() != 0)
		{
			txt.append("\tDESC:").append(pcgen.io.EntityEncoder.encode(aString));

			if (getDescIsPI())
			{
				txt.append("\tDESCISPI:Yes");
			}
		}

		if (!getName().equals(getKeyName()))
		{
			txt.append("\tKEY:").append(getKeyName());
		}

		for (e = getSafeListFor(ListKey.AUTO_ARRAY).iterator(); e.hasNext();)
		{
			txt.append("\tAUTO:").append(e.next().toString());
		}

		if (!(this instanceof PCClass) && (getBonusList().size() != 0))
		{
			for (e = getBonusList().iterator(); e.hasNext();)
			{
				BonusObj bonusobj = (BonusObj) e.next();
				txt.append("\tBONUS:").append(bonusobj.getPCCText()); //This formats the bonus items in the proper .lst manner
			}
		}

		List ccSkillList = getCcSkillList();
		if ((ccSkillList != null) && (ccSkillList.size() != 0))
		{
			txt.append("\tCCSKILL:").append(CoreUtility.join(ccSkillList, "|"));
		}

		List cSkillList = getCSkillList();
		if ((cSkillList != null) && (cSkillList.size() != 0))
		{
			txt.append("\tCSKILL:").append(CoreUtility.join(cSkillList, "|"));
		}

		aString = getChoiceString();

		if ((aString != null) && (aString.length() != 0))
		{
			txt.append("\tCHOOSE:").append(aString);
		}

		int iCount = getVariableCount();

		if (!(this instanceof PCClass) && (iCount != 0))
		{
			for (int i = 0; i < iCount; ++i)
			{
				aString = getVariableDefinition(i);

				if (aString.startsWith("-9|"))
				{
					aString = aString.substring(3);
				}

				txt.append("\tDEFINE:").append(aString);
			}
		}

		String DR = stringChar.getCharacteristic(StringKey.DR_FORMULA);
		if (!(this instanceof PCClass) && (DR != null) && (DR.length() != 0))
		{
			txt.append("\tDR:").append(DR);
		}

		final List langList = CoreUtility.toStringRepresentation(getSafeListFor(ListKey.AUTO_LANGUAGES));

		if (langList.size() != 0)
		{
			txt.append("\tLANGAUTO:").append(CoreUtility.join(langList, ","));
		}

		if (movement != null && movement.getNumberOfMovements() > 0)
		{
			txt.append(movement.toLSTString());
		}

		iCount = getPreReqCount();

		if (iCount != 0)
		{
			final StringWriter writer = new StringWriter();
			for (int i = 0; i < iCount; ++i)
			{
				final Prerequisite prereq = getPreReq(i);
				final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
				try
				{
					writer.write("\t");
					prereqWriter.write(writer, prereq);
				}
				catch (PersistenceLayerException e1)
				{
					e1.printStackTrace();
				}
			}
			txt.append(writer);
		}

		List specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);
		if (!(this instanceof PCClass) && (specialAbilityList != null) && (specialAbilityList.size() != 0))
		{
			for (e = specialAbilityList.iterator(); e.hasNext();)
			{
				final SpecialAbility sa = (SpecialAbility) e.next();
				txt.append("\tSA:").append(sa.toString());
			}
		}

		aString = getQualifyString();

		if (!aString.equals("alwaysValid") && aString.length() > 0)
		{
			txt.append("\tQUALIFY:").append(aString);
		}

		if (!(this instanceof PCClass))
		{
			List spellList = getSpellList();
			if (spellList != null)
			{
				for (Iterator it = spellList.iterator(); it.hasNext();)
				{
					txt.append("\tSPELLS:").append(((PCSpell) it.next()).getPCCText());
				}
			}
		}

		String SR = stringChar.getCharacteristic(StringKey.SR_FORMULA);
		if (!(this instanceof PCClass) && (SR != null) && (SR.length() != 0))
		{
			txt.append("\tSR:").append(SR);
		}

		if ((vision != null) && (vision.size() != 0))
		{
			final StringBuffer sb = new StringBuffer();

			for (e = vision.keySet().iterator(); e.hasNext();)
			{
				final String key = (String) e.next();
				final String val = (String) vision.get(key);

				if ((val.length() > 0) && !"0".equals(val))
				{
					if (sb.length() > 0)
					{
						sb.append('|');
					}

					sb.append(key).append(" (");
					sb.append(val).append("')");
				}
			}

			if (sb.length() > 0)
			{
				txt.append("\tVISION:").append(sb.toString());
			}
		}

		if (getMyTypeCount() != 0)
		{
			txt.append('\t').append(Constants.s_TAG_TYPE).append(getType());
		}

		aString = SourceUtilities.returnSourceInForm(this, Constants.SOURCEPAGE, false);

		if (aString.length() != 0)
		{
			txt.append("\tSOURCEPAGE:").append(aString);
		}

		String regionString = stringChar.getCharacteristic(StringKey.REGION);
		if ((regionString != null) && regionString.startsWith("0|"))
		{
			txt.append("\tREGION:").append(regionString.substring(2));
		}

		for (Iterator it = getSafeListFor(ListKey.KITS).iterator(); it.hasNext();)
		{
			aString = (String) it.next();

			if (aString.startsWith("0|"))
			{
				txt.append("\tKIT:").append(aString.substring(2));
			}
		}

		return txt.toString();
	}


	/**
	 * TODO DOCUMENT ME!
	 *
	 * @param  aLevel
	 * @param  aPC
	 * @param  pcLevelInfo
	 */
	protected void addAddsForLevel(
		final int             aLevel,
		final PlayerCharacter aPC,
		final PCLevelInfo     pcLevelInfo)
	{
		if (
			aPC == null ||
			aPC.isImporting() ||
			levelAbilityList == null   ||
			levelAbilityList.isEmpty() ||
			!aPC.doLevelAbilities())
		{
			return;
		}

		LevelAbility levAbility;

		for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
		{
			levAbility = (LevelAbility) e.next();
			levAbility.setOwner(this);

			if (
				!(this instanceof PCClass) ||
				((levAbility.level() == aLevel) && levAbility.canProcess()))
			{
				boolean canProcess = true;

				if (
					(levAbility.isFeat()) &&
					!SettingsHandler.getShowFeatDialogAtLevelUp())
				{
					// Check the list of feats for at least one that is hidden or for
					// output only Show the popup if there is one

					Logging.errorPrint("PObject addAddsForLevel");
					canProcess = false;

					final List featList = new ArrayList();
					levAbility.process(featList, aPC, pcLevelInfo);

					for (Iterator fe = featList.iterator(); fe.hasNext();)
					{
						final Ability anAbility = Globals.getAbilityNamed(
								"FEAT",
								(String) fe.next());

						if (anAbility != null)
						{
							switch (anAbility.getVisible())
							{
								case Ability.VISIBILITY_HIDDEN:
								case Ability.VISIBILITY_OUTPUT_ONLY:
									canProcess = true;

									break;

								default:

									continue;
							}

							break;
						}
					}
				}

				if (canProcess)
				{
					levAbility.process(aPC, pcLevelInfo);
				}
				else
				{
					aPC.adjustFeats(1); // need to add 1 feat to total available
				}
			}
		}
	}

	protected List addSpecialAbilitiesToList(final List aList, final PlayerCharacter aPC)
	{
		aList.addAll(getSafeListFor(ListKey.SPECIAL_ABILITY));
		return aList;
	}

	/**
	 * This method is used to add the type to the appropriate global list
	 * if we are ever interested in knowing what types are available for
	 * a particular object type (for example, all of the different equipment types)
	 *
	 * @param type The name of the type that is to be added to the global
	 * list of types.
	 */
	protected void doGlobalTypeUpdate(final String type)
	{
		// Override in any class that wants to store type information
	}

	protected void globalChecks(final PlayerCharacter aPC)
	{
		globalChecks(false, aPC);
	}

	protected void globalChecks(final boolean flag, final PlayerCharacter aPC)
	{
		aPC.setArmorProfListStable(false);
		List l = getSafeListFor(ListKey.KITS);
		for (int i = 0; i > l.size(); i++)
		{
			KitUtilities.makeKitSelections(0, (String) l.get(i), i, aPC);
		}
		makeRegionSelection(aPC);

		if (flag)
		{
			makeChoices(aPC);
		}

		if (this instanceof PCClass)
		{
			final PCClass aClass = (PCClass) this;
			final PCLevelInfo pcLevelInfo = aPC.getLevelInfoFor(getKeyName(), aClass.level);
			addAddsForLevel(aClass.level, aPC, pcLevelInfo);
		}
		else
		{
			addAddsForLevel(0, aPC, null);
		}

		activateBonuses(aPC);
	}

	protected void subAddsForLevel(final int aLevel, final PlayerCharacter aPC)
	{
		if ((aPC == null) || (levelAbilityList == null) || levelAbilityList.isEmpty())
		{
			return;
		}

		LevelAbility ability;

		for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
		{
			ability = (LevelAbility) e.next();

			if (ability.level() == aLevel)
			{
				ability.subForLevel(aPC);
			}
		}
	}

	/**
	 * Get the associated list
	 * @return the associated list
	 */
	public final ArrayList getAssociatedList()
	{
		if (associatedList == null)
		{
			return new ArrayList();
		}
		return associatedList;
	}

	/**
	 * @param bonus     a Number (such as 2)
	 * @param bonusType "COMBAT.AC.Dodge" or "COMBAT.AC.Dodge.STACK"
	 */
	final void setBonusStackFor(final double bonus, String bonusType)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();
		}

		// Default to non-stacking bonuses
		int index = -1;

		final StringTokenizer aTok = new StringTokenizer(bonusType, ".");

		// e.g. "COMBAT.AC.DODGE"
		if ((bonusType != null) && (aTok.countTokens() >= 2))
		{
			String aString;

			// we need to get the 3rd token to see
			// if it should .STACK or .REPLACE
			aTok.nextToken(); //Discard token
			aString = aTok.nextToken();

			// if the 3rd token is "BASE" we have something like
			// CHECKS.BASE.Fortitude
			if (aString.equals("BASE"))
			{
				if (aTok.hasMoreTokens())
				{
					// discard next token (Fortitude)
					aTok.nextToken();
				}

				if (aTok.hasMoreTokens())
				{
					// check for a TYPE
					aString = aTok.nextToken();
				}
				else
				{
					// all BASE type bonuses should stack
					aString = null;
				}
			}
			else
			{
				if (aTok.hasMoreTokens())
				{
					// Type: .DODGE
					aString = aTok.nextToken();
				}
				else
				{
					aString = null;
				}
			}

			if (aString != null)
			{
				index = SettingsHandler.getGame().getUnmodifiableBonusStackList().indexOf(aString); // e.g. Dodge
			}

			//
			// un-named (or un-TYPE'd) bonus should stack
			if (aString == null)
			{
				index = 1;
			}
			else if (aString.equals("NULL"))
			{
				index = 1;
			}
		}

		// .STACK means stack
		// .REPLACE stacks with other .REPLACE bonuses
		if ((bonusType != null) && (bonusType.endsWith(".STACK") || bonusType.endsWith(".REPLACE")))
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
			final String aVal = (String) getBonusMap().get(bonusType);

			if (aVal == null)
			{
				putBonusMap(bonusType, String.valueOf(bonus));
			}
			else
			{
				putBonusMap(bonusType, String.valueOf(Math.max(bonus, Float.parseFloat(aVal))));
			}
		}
		else // a stacking bonus
		{
			if (bonusType == null)
			{
				bonusType = "";
			}
			else if (bonusType.endsWith(".REPLACE.STACK"))
			{
				// Check for the special case of:
				// COMBAT.AC.Armor.REPLACE.STACK
				// and remove the .STACK
				bonusType = bonusType.substring(0, bonusType.length() - 6);
			}

			final String aVal = (String) getBonusMap().get(bonusType);

			if (aVal == null)
			{
				putBonusMap(bonusType, String.valueOf(bonus));
			}
			else
			{
				putBonusMap(bonusType, String.valueOf(bonus + Float.parseFloat(aVal)));
			}
		}
	}

	final void setPreReq(final int index, final Prerequisite aString)
	{
		preReqList.set(index, aString);
	}

	/**
	 * <p>Retrieves the unarmed damage information for this PObject.  This
	 * comes from the <code>UDAM</code> tag, and can be a simple die string
	 * as in <code>1d20</code>, or a list of size-modified data like is
	 * utilised for monk unarmed damage.</p>
	 *
	 * @param includeCrit Whether or not to include critical multiplier
	 * @param includeStrBonus Whether or not to include strength damage bonus
	 * @param aPC
	 *
	 * @return A string representing the unarmed damage dice of the object.
	 */
	final String getUdamFor(final boolean includeCrit, final boolean includeStrBonus, final PlayerCharacter aPC)
	{
		// the assumption is that there is only one UDAM: tag for things other than class
		if (!containsListFor(ListKey.UDAM))
		{
			// If no UDAM exists, just grab default damage for the race, Michael Osterlie
			return aPC.getRace().getUdam();
		}

		final StringBuffer aString = new StringBuffer(getElementInList(ListKey.UDAM, 0).toString());

		//Added to handle sizes for damage, Ross M. Lodge
		int iSize = Globals.sizeInt(aPC.getSize());
		final StringTokenizer aTok = new StringTokenizer(aString.toString(), ",", false);

		while ((iSize > -1) && aTok.hasMoreTokens())
		{
			aString.replace(0, aString.length(), aTok.nextToken());

			if (iSize == 0)
			{
				break;
			}

			iSize -= 1;
		}

		//End added
		final int b = (int) aPC.getStatBonusTo("DAMAGE", "TYPE=MELEE");

		if (includeStrBonus && (b > 0))
		{
			aString.append('+');
		}

		if (includeStrBonus && (b != 0))
		{
			aString.append(String.valueOf(b));
		}

		List umultList = getListFor(ListKey.UMULT);
		if (includeCrit && (umultList != null) && !umultList.isEmpty())
		{
			final String dString = umultList.get(0).toString();

			if (!"0".equals(dString))
			{
				aString.append("(x").append(dString).append(')');
			}
		}

		return aString.toString();
	}

	 /**
	  * Add automatic tags to a list
	  * For example, tag = "ARMORPROF", aList is list of armor proficiencies
	  * @param tag
	  * @param aList
	  * @param aPC
	  * @param expandWeaponTypes
	  */
	public final void addAutoTagsToList(final String tag, final AbstractCollection aList, final PlayerCharacter aPC, boolean expandWeaponTypes)
	{
		for (Iterator i = getSafeListFor(ListKey.AUTO_ARRAY).iterator(); i.hasNext();)
		{
			String aString = (String) i.next();

			if (!aString.startsWith(tag))
			{
				continue;
			}

			String preReqTag;
			final List aPreReqList = new ArrayList();
			final int j1 = aString.lastIndexOf('[');
			int j2 = aString.lastIndexOf(']');

			if (j2 < j1)
			{
				j2 = tag.length();
			}

			if (j1 >= 0)
			{
				preReqTag = aString.substring(j1 + 1, j2);
				aPreReqList.add(preReqTag);

				if (!PrereqHandler.passesAll(aPreReqList, aPC, null))
				{
					return;
				}

				aString = aString.substring(0, j1);
			}

			final StringTokenizer aTok = new StringTokenizer(aString, "|");
			aTok.nextToken(); // removes tag token

			String tok;

			while (aTok.hasMoreTokens())
			{
				tok = aTok.nextToken();

				if ((tok.startsWith("TYPE=") || tok.startsWith("TYPE."))
					&& tag.startsWith("WEAPON") && expandWeaponTypes)
				{
					final StringTokenizer bTok = new StringTokenizer(tok.substring(5), ".");
					List xList = null;

					while (bTok.hasMoreTokens())
					{
						final String bString = bTok.nextToken();
						final List bList = Globals.getWeaponProfs(bString, aPC);

						if (bList.size() == 0)
						{
							bList.addAll(EquipmentList.getEquipmentOfType("Weapon." + bString, ""));
						}

						if (xList == null)
						{
							xList = new ArrayList();

							for (Iterator e = bList.iterator(); e.hasNext();)
							{
								final Object obj = e.next();
								final String wprof;

								if (obj instanceof Equipment)
								{
									wprof = ((Equipment) obj).profName(aPC);
								}
								else
								{
									wprof = obj.toString();
								}

								if (!xList.contains(wprof))
								{
									xList.add(wprof);
								}
							}
						}
						else
						{
							final List removeList = new ArrayList();

							for (Iterator e = xList.iterator(); e.hasNext();)
							{
								final String wprof = (String) e.next();
								boolean contains = false;

								for (Iterator f = bList.iterator(); f.hasNext();)
								{
									final Object obj = f.next();
									final String wprof2;

									if (obj instanceof Equipment)
									{
										wprof2 = ((Equipment) obj).profName(aPC);
									}
									else
									{
										wprof2 = obj.toString();
									}

									if (wprof.equals(wprof2))
									{
										contains = true;

										break;
									}
								}

								if (!contains)
								{
									removeList.add(wprof);
								}
							}

							for (Iterator e = removeList.iterator(); e.hasNext();)
							{
								final String wprof = (String) e.next();
								xList.remove(wprof);
							}
						}
					}

					aList.addAll(xList);
				}
				else if ((tok.startsWith("TYPE=") || tok.startsWith("TYPE.")) && tag.startsWith("ARMOR"))
				{
					aList.add(tok);
				}
				else if (tag.startsWith("EQUIP"))
				{
					final Equipment aEq = EquipmentList.getEquipmentFromName(tok, aPC);

					if (aEq != null)
					{
						final Equipment newEq = (Equipment) aEq.clone();
						newEq.setQty(1);
						newEq.setAutomatic(true);
						newEq.setOutputIndex(aList.size());
						aList.add(newEq);
					}
				}
				else if ("%LIST".equals(tok))
				{
					for (Iterator e = getAssociatedList().iterator(); e.hasNext();)
					{
						final String wString = (String) e.next();
						aList.add(wString);
					}
				}
				else
				{
					// add tok to list
					aList.add(tok);
				}
			}
		}
	}

	/**
	 * Add a user defined type
	 * @param myType
	 */
	void addMyType(final String myType)
	{
		listChar.addToListFor(ListKey.TYPE, myType);
	}

	/**
	 * Add the pre-reqs to this collection
	 * @param collection
	 */
	final void addPreReqTo(final Collection collection)
	{
		if (preReqList != null)
		{
			collection.addAll(preReqList);
		}
	}

	/**
	 * Apply the bonus to a character
	 * @param bonusString
	 * @param chooseString
	 * @param aPC
	 */
	public final void applyBonus(String bonusString, final String chooseString, final PlayerCharacter aPC)
	{
		bonusString = makeBonusString(bonusString, chooseString, aPC);
		addBonusList(bonusString);
		addSave("BONUS|" + bonusString);
	}

	void fireNameChanged(final String oldName, final String newName)
	{
		// This method currently does nothing so it may be overriden in PCClass.
	}

	final boolean hasCcSkill(final String aName)
	{
		List ccSkillList = getCcSkillList();
		if ((ccSkillList == null) || ccSkillList.isEmpty())
		{
			return false;
		}

		if (ccSkillList.contains(aName))
		{
			return true;
		}

		String aString;

		for (Iterator e = getCcSkillList().iterator(); e.hasNext();)
		{
			aString = (String) e.next();

			if (aString.lastIndexOf('%') >= 0)
			{
				aString = aString.substring(0, aString.length() - 1);

				if (aName.startsWith(aString))
				{
					return true;
				}
			}
		}

		return false;
	}

	final boolean hasCSkill(final String aName)
	{
		List cSkillList = getCSkillList();
		if ((cSkillList == null) || cSkillList.isEmpty())
		{
			return false;
		}

		if (cSkillList.contains(aName))
		{
			return true;
		}

		if (cSkillList.contains("LIST"))
		{
			String aString;

			for (int e = 0; e < getAssociatedCount(); ++e)
			{
				aString = getAssociated(e);

				if (aName.startsWith(aString) || aString.startsWith(aName))
				{
					return true;
				}
			}
		}

		String aString;

		for (Iterator e = cSkillList.iterator(); e.hasNext();)
		{
			aString = (String) e.next();

			if (aString.lastIndexOf('%') >= 0)
			{
				aString = aString.substring(0, aString.length() - 1);

				if (aName.startsWith(aString))
				{
					return true;
				}
			}

			if (aName.equalsIgnoreCase(aString))
			{
				return true;
			}
		}

		return false;
	}

	String makeBonusString(String bonusString, final String chooseString, final PlayerCharacter aPC)
	{
		// assumption is that the chooseString is in the form class/type[space]level
		int i = chooseString.lastIndexOf(' ');
		String classString = "";
		String levelString = "";

		if (bonusString.startsWith("BONUS:"))
		{
			bonusString = bonusString.substring(6);
		}

		final boolean lockIt = bonusString.endsWith(".LOCK");

		if (lockIt)
		{
			bonusString = bonusString.substring(0, bonusString.lastIndexOf(".LOCK"));
		}

		if (i >= 0)
		{
			classString = chooseString.substring(0, i);

			if (i < chooseString.length())
			{
				levelString = chooseString.substring(i + 1);
			}
		}

		while (bonusString.lastIndexOf("TYPE=%") >= 0)
		{
			i = bonusString.lastIndexOf("TYPE=%");
			bonusString = bonusString.substring(0, i + 5) + classString + bonusString.substring(i + 6);
		}

		while (bonusString.lastIndexOf("CLASS=%") >= 0)
		{
			i = bonusString.lastIndexOf("CLASS=%");
			bonusString = bonusString.substring(0, i + 6) + classString + bonusString.substring(i + 7);
		}

		while (bonusString.lastIndexOf("LEVEL=%") >= 0)
		{
			i = bonusString.lastIndexOf("LEVEL=%");
			bonusString = bonusString.substring(0, i + 6) + levelString + bonusString.substring(i + 7);
		}

		if (lockIt)
		{
			i = bonusString.lastIndexOf('|');

			final Float val = aPC.getVariableValue(bonusString.substring(i + 1), "");
			bonusString = bonusString.substring(0, i) + "|" + val;
		}

		return bonusString;
	}

	final void makeRegionSelection(final PlayerCharacter aPC)
	{
		makeRegionSelection(0, aPC);
	}

	final void makeRegionSelection(final int arg, final PlayerCharacter aPC)
	{
		String regionString = stringChar.getCharacteristic(StringKey.REGION);
		if (regionString == null)
		{
			return;
		}

		final StringTokenizer aTok = new StringTokenizer(regionString, "|");

		// first element is prelevel - should be 0 for everything but PCClass entries
		String tok = aTok.nextToken();
		int aLevel;

		try
		{
			aLevel = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed preLevel attribute in makeRegionSelection: " + tok);
			aLevel = 0;
		}

		if (aLevel > arg)
		{
			return;
		}

		tok = aTok.nextToken();

		int num;

		try
		{
			num = Integer.parseInt(tok); // number of selections
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed number of selection attribute in makeRegionSelection: " + tok);
			num = -1;
		}

		List aList = new ArrayList();

		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}

		if (num != aList.size())
		{
			final ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setTitle("Region Selection");
			c.setPool(num);
			c.setPoolFlag(false);
			c.setAvailableList(aList);
			c.setVisible(true);
			aList = c.getSelectedList();
		}

		if (aList.size() > 0)
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final String aString = (String) i.next();

				if (aPC.getRegion().equalsIgnoreCase(aString))
				{
					continue;
				}

				aPC.setRegion(aString);
			}
		}
	}

	int numberInList(final String aType)
	{
		return 0;
	}

	final boolean passesPreReqToGain(final PObject p, PlayerCharacter currentPC)
	{
		if (getPreReqCount() == 0)
		{
			return true;
		}

		return PrereqHandler.passesAll(preReqList, (Equipment) p, currentPC);
	}

	public final Object removeAssociated(final int i)
	{
		if (associatedList == null)
		{
			throw new IndexOutOfBoundsException("size is 0, i=" + i);
		}

		return associatedList.remove(i);
	}

	/**
	 * Remove the bonus from this objects list of bonuses.
	 * 
	 * @param bonusString The string representing the bonus
	 * @param chooseString The choice that was made.
	 * @param aPC The player character to remove th bonus from. 
	 */
	public final void removeBonus(final String bonusString, final String chooseString, final PlayerCharacter aPC)
	{
		String bonus = makeBonusString(bonusString, chooseString, aPC);

		int index = -1;

		final BonusObj aBonus = Bonus.newBonus(bonus);
		String bonusStrRep = String.valueOf(aBonus);

		if (getBonusList() != null)
		{
			int count = 0;
			for (Iterator iter = getBonusList().iterator(); iter.hasNext();)
			{
				BonusObj listBonus = (BonusObj) iter.next();
				if (listBonus.getCreatorObject().equals(this)
					&& listBonus.toString().equals(bonusStrRep))
				{
					index = count;
				}
				count++;
			}
		}

		if (index >= 0)
		{
			getBonusList().remove(index);
		}
		else
		{
			Logging.errorPrint("removeBonus: Could not find bonus: " + bonus + " in bonusList " + getBonusList());
		}

		removeSave("BONUS|" + bonus);
	}

	final void sortAssociated()
	{
		if (associatedList != null)
		{
			Collections.sort(associatedList);
		}
	}

	/**
	 * rephrase parenthetical name components, if appropriate
	 * @return pre formatted output name
	 */
	private String getPreFormatedOutputName()
	{
		//if there are no () to pull from, just return the name
		if ((name.indexOf('(') < 0) || (name.indexOf(')') < 0))
		{
			return name;
		}

		//we just take from the first ( to the first ), typically there should only be one of each
		final String subName = name.substring(name.indexOf('(') + 1, name.indexOf(')')); //the stuff inside the ()
		final StringTokenizer tok = new StringTokenizer(subName, "/");
		final StringBuffer newNameBuff = new StringBuffer();

		while (tok.hasMoreTokens())
		{
			//build this new string from right to left
			newNameBuff.insert(0, tok.nextToken());

			if (tok.hasMoreTokens())
			{
				newNameBuff.insert(0, " ");
			}
		}

		return newNameBuff.toString();
	}

	/**
	 * calcBonus adds together all the bonuses for aType of aName
	 *
	 * @param bString       Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST or %VAR bonus section
	 * @param aType         Such as "COMBAT"
	 * @param aName         Such as "AC"
	 * @param aTypePlusName "COMBAT.AC."
	 * @param obj           The object to get the bonus from
	 * @param iTimes        multiply bonus * iTimes
	 * @param aBonusObj
	 * @param aPC
	 * @return bonus
	 */
	private double calcBonus(final String bString, final String aType, final String aName, String aTypePlusName, final Object obj, final int iTimes,
							 final BonusObj aBonusObj, final PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(bString, "|");

		if (aTok.countTokens() < 3)
		{
			Logging.errorPrint("Badly formed BONUS:" + bString);

			return 0;
		}

		String aString = aTok.nextToken();

		if ((!aString.equalsIgnoreCase(aType) && !aString.endsWith("%LIST"))
			|| (aString.endsWith("%LIST") && (numberInList(aType) == 0)) || (aName.equals("ALL")))
		{
			return 0;
		}

		final String aList = aTok.nextToken();

		if (!aList.equals("LIST") && !aList.equals("ALL") && (aList.toUpperCase().indexOf(aName.toUpperCase()) < 0))
		{
			return 0;
		}

		if (aList.equals("ALL")
			&& ((aName.indexOf("STAT=") >= 0) || (aName.indexOf("TYPE=") >= 0) || (aName.indexOf("LIST") >= 0)
			|| (aName.indexOf("VAR") >= 0)))
		{
			return 0;
		}

		if (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
		}

		double iBonus = 0;

		if (obj instanceof PlayerCharacter)
		{
			iBonus = ((PlayerCharacter) obj).getVariableValue(aString, "").doubleValue();
		}
		else if (obj instanceof Equipment)
		{
			iBonus = ((Equipment) obj).getVariableValue(aString, "", aPC).doubleValue();
		}
		else
		{
			try
			{
				iBonus = Float.parseFloat(aString);
			}
			catch (NumberFormatException e)
			{
				//Should this be ignored?
				Logging.errorPrint("calcBonus NumberFormatException in BONUS: " + aString, e);
			}
		}

		final List bonusPreReqList = aBonusObj.getPrereqList();
		final String possibleBonusTypeString = aBonusObj.getTypeString();

		// must meet criteria before adding any bonuses
		if (obj instanceof PlayerCharacter)
		{
			if (!PrereqHandler.passesAll(bonusPreReqList, (PlayerCharacter) obj, null))
			{
				return 0;
			}
		}
		else
		{
			if (!PrereqHandler.passesAll(bonusPreReqList, (Equipment) obj, aPC))
			{
				return 0;
			}
		}

		double bonus = 0;

		if ("LIST".equalsIgnoreCase(aList))
		{
			final int iCount = numberInList(aName);

			if (iCount != 0)
			{
				bonus += (iBonus * iCount);
			}
		}

		String bonusTypeString = null;

		final StringTokenizer bTok = new StringTokenizer(aList, ",");

		if (aList.equalsIgnoreCase("LIST"))
		{
			bTok.nextToken();
		}
		else if (aList.equalsIgnoreCase("ALL"))
		{
			// aTypePlusName looks like: "SKILL.ALL."
			// so we need to reset it to "SKILL.Hide."
			aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();
			bonus = iBonus;
			bonusTypeString = possibleBonusTypeString;
		}

		while (bTok.hasMoreTokens())
		{
			if (bTok.nextToken().equalsIgnoreCase(aName))
			{
				bonus += iBonus;
				bonusTypeString = possibleBonusTypeString;
			}
		}

		if (obj instanceof Equipment)
		{
			((Equipment) obj).setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
		}
		else
		{
			setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
		}

		// The "ALL" subtag is used to build the stacking bonusMap
		// not to get a bonus value, so just return
		if (aList.equals("ALL"))
		{
			return 0;
		}

		return bonus * iTimes;
	}

	/**
	 * calcPartialBonus calls appropriate getVariableValue() for a Bonus
	 *
	 * @param iTimes  		multiply bonus * iTimes
	 * @param aBonus  		The bonus Object used for calcs
	 * @param anObj
	 * @param listString 	String returned after %LIST substitution, if applicable
	 * @param aPC
	 * @return partial bonus
	 */
	private double calcPartialBonus(final int iTimes, final BonusObj aBonus, final Object anObj, final String listString, final PlayerCharacter aPC)
	{
		final String aList = aBonus.getBonusInfo();
		String aVal = aBonus.getValue();

		double iBonus = 0;

		if (aList.equals("ALL"))
		{
			return 0;
		}

		if (listString != null)
		{
			int listIndex = aVal.indexOf("%LIST");
			while (listIndex >= 0)
			{
				//A %LIST substitution also needs to be done in the val section
				//first, find out which one
				//this is a bit of a hack but it was the best I could figure out so far
				boolean found = false;
				for (int i = 0; i < getAssociatedCount(); ++i)
				{
					final String associatedStr = getAssociated(i).toUpperCase();
					if (listString.indexOf(associatedStr) >= 0)
					{
						final StringBuffer sb = new StringBuffer();
						if (listIndex > 0)
						{
							sb.append(aVal.substring(0, listIndex));
						}
						sb.append(associatedStr);
						if (aVal.length() > (listIndex + 5))
						{
							sb.append(aVal.substring(listIndex + 5));
						}
						aVal = sb.toString();
						found = true;
						break;
					}
				}

				listIndex = (found) ? aVal.indexOf("%LIST") : -1;
			}
		}

		if (aBonus.isValueStatic())
		{
			iBonus = aBonus.getValueAsdouble();
		}
		else if (anObj instanceof PlayerCharacter)
		{
			iBonus = ((PlayerCharacter) anObj).getVariableValue(aVal, "").doubleValue();
		}
		else if (anObj instanceof Equipment)
		{
			iBonus = ((Equipment) anObj).getVariableValue(aVal, "", aPC).doubleValue();
		}
		else
		{
			try
			{
				iBonus = Float.parseFloat(aVal);
			}
			catch (NumberFormatException e)
			{
				//Should this be ignored?
				Logging.errorPrint("calcPartialBonus NumberFormatException in BONUS: " + aVal);
			}
		}

		return iBonus * iTimes;
	}

	protected void clearMyType()
	{
		listChar.removeListFor(ListKey.TYPE);
	}

	/**
	 * Clear the selected weapon proficiency bonuses
	 *
	 */
	public void clearSelectedWeaponProfBonus()
	{
		listChar.removeListFor(ListKey.SELECTED_WEAPON_PROF_BONUS);
	}

	private String piDescString(final boolean useHeader)
	{
		String aString = stringChar.getCharacteristic(StringKey.DESCRIPTION);

		if (this instanceof Ability)
		{
			aString = ((Ability) this).getBenefitDescription();
		}

		if (descIsPI)
		{
			final StringBuffer sb = new StringBuffer(aString.length() + 30);

			if (useHeader)
			{
				sb.append("<html>");
			}

			sb.append("<b><i>").append(aString).append("</i></b>");

			if (useHeader)
			{
				sb.append("</html>");
			}

			return sb.toString();
		}

		return aString;
	}

	/**
	 * Returns the Product Identity string (with or without the header)
	 * @param useHeader
	 * @return the Product Identity string (with or without the header)
	 */
	private String piString(final boolean useHeader)
	{
		String aString = toString();

		if (SettingsHandler.guiUsesOutputName())
		{
			aString = getOutputName();
		}

		if (nameIsPI)
		{
			final StringBuffer sb = new StringBuffer(aString.length() + 30);

			if (useHeader)
			{
				sb.append("<html>");
			}

			sb.append("<b><i>").append(aString).append("</i></b>");

			if (useHeader)
			{
				sb.append("</html>");
			}

			return sb.toString();
		}

		return aString;
	}

	protected void removeMyType(final String myType)
	{
		listChar.removeFromListFor(ListKey.TYPE, myType);
	}

	/**
	 * Method getTemplateList. Returns an array list containing the raw
	 * templates granted by this race. This includes CHOOSE: strings
	 * which list templates a user will be asked to choose from.
	 *
	 * @return ArrayList of granted templates
	 */
	public List getTemplateList()
	{
		return getSafeListFor(ListKey.TEMPLATES);
	}

	/**
	 * @param templateList	A string containing a pipe-delimited list of templates to add
	 */
	public void addTemplate(final String templateList)
	{
		if (templateList.startsWith("CHOOSE:"))
		{
			listChar.addToListFor(ListKey.TEMPLATES, templateList);
		}
		else
		{
			final StringTokenizer aTok = new StringTokenizer(templateList, "|");

			while (aTok.hasMoreTokens())
			{
				String templateName = aTok.nextToken();

				// .CLEAR
				if (".CLEAR".equalsIgnoreCase(templateName))
				{
					listChar.removeListFor(ListKey.TEMPLATES);
				}

				// .CLEAR.<template_name>
				else if (templateName.regionMatches(true, 0, ".CLEAR.", 0, 7))
				{
					templateName = templateName.substring(7);
					if (!listChar.removeFromListFor(ListKey.TEMPLATES, templateName))
					{
						Logging.errorPrint("addTemplate: Could not find template: " + templateName + " in templateList.");
					}
				}
				else
				{
					listChar.addToListFor(ListKey.TEMPLATES, templateName);
				}
			}
		}
	}

	List getTemplates(final boolean isImporting, final PlayerCharacter aPC)
	{
		final List newTemplates = new ArrayList();
		listChar.removeListFor(ListKey.TEMPLATES_ADDED);

		if (!isImporting)
		{
			for (Iterator e = getTemplateList().iterator(); e.hasNext();)
			{
				String templateName = (String) e.next();

				if (templateName.startsWith("CHOOSE:"))
				{
					for (; ;)
					{
						final String newTemplate = Globals.chooseFromList("Template Choice (" + getName() + ")",
							templateName.substring(7), null, 1);

						if (newTemplate != null)
						{
							templateName = newTemplate;

							break;
						}
					}
				}

				if (templateName.length() != 0)
				{
					newTemplates.add(templateName);
					listChar.addToListFor(ListKey.TEMPLATES_ADDED, templateName);
					aPC.addTemplateNamed(templateName);

				}
			}
		}

		return newTemplates;
	}

	/**
	 * Get a list of the added templates
	 * @return a list of the added templates
	 */
	public List templatesAdded()
	{
		return getSafeListFor(ListKey.TEMPLATES_ADDED);
	}

	/**
	 * Set the movement
	 * @param cm
	 */
	public void setMovement(Movement cm)
	{
		movement = cm;
	}

	/**
	 * Set the list of Kits
	 * @param l
	 */
	public void setKitList(List l)
	{
		listChar.removeListFor(ListKey.KITS);
		listChar.addAllToListFor(ListKey.KITS, l);
	}

	/**
	 * Get the Spell Support for this object
	 * @return SpellSupport
	 */
	public SpellSupport getSpellSupport()
	{
		return spellSupport;
	}

	/**
	 * Remove the speical ability from the list
	 * @param sa
	 */
	public void removeSpecialAbility(SpecialAbility sa) {
		listChar.removeFromListFor(ListKey.SPECIAL_ABILITY, sa);
	}

	/**
	 * Set a string referenced by a key
	 * @param key
	 * @param s
	 */
	public void setStringFor(StringKey key, String s)
	{
		stringChar.setCharacteristic(key, s);
	}

	/**
	 * Get the string given a key
	 * @param key
	 * @return string
	 */
	public String getStringFor(StringKey key)
	{
		return stringChar.getCharacteristic(key);
	}

	/* *******************************************************************
	 * The following methods are part of the KeyedListContainer Interface
	 * ******************************************************************/
	public boolean containsListFor(ListKey key)
	{
		return listChar.containsListFor(key);
	}

	public List getListFor(ListKey key)
	{
		return listChar.getListFor(key);
	}

	public final List getSafeListFor(ListKey key)
	{
		return listChar.containsListFor(key) ? listChar.getListFor(key) : new ArrayList();
	}

	public int getSizeOfListFor(ListKey key)
	{
		return listChar.sizeOfListFor(key);
	}

	public int getSafeSizeOfListFor(ListKey key)
	{
		return listChar.containsListFor(key) ? listChar.sizeOfListFor(key) : 0;
	}

	public boolean containsInList(ListKey key, String value)
	{
		return listChar.containsInList(key, value);
	}

	public Object getElementInList(ListKey key, int i)
	{
		return listChar.getElementInList(key, i);
	}
	/* ************************************************
	 * End methods for the KeyedListContainer Interface
	 * ************************************************/

	 // Helper methods
	 // TODO  Will be possible to refactor these outside of PObject

	/**
	 * Deal with the FeatMultipleChoice case for getAssociatedList
	 * @param idx
	 * @return the assocaited choice
	 */
	private String dealWithFeatMultipleChoice(int idx) {
		FeatMultipleChoice fmc;
		int iCount;

		for (int i = 0; i < associatedList.size(); ++i)
		{
			fmc = (FeatMultipleChoice) associatedList.get(i);
			iCount = fmc.getChoiceCount();

			if (idx < iCount)
			{
				return fmc.getChoice(idx);
			}

			idx -= iCount;
		}

		return "";
	}

	/**
	 * Deal with the FeatMultipleChoice case for getAssociatedCount
	 * @return the associated count
	 */
	private int dealWithFeatMultipleChoiceForCount() {
		int iCount = 0;

		for (int i = 0; i < associatedList.size(); ++i)
		{
			iCount += ((FeatMultipleChoice) associatedList.get(i)).getChoiceCount();
		}

		return iCount;
	}

}
