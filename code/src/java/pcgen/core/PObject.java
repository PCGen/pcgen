/*
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

import java.io.Serializable;
import java.io.StringWriter;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.EmptyIterator;
import pcgen.core.utils.IntegerKey;
import pcgen.core.utils.KeyedListContainer;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.ListKeyMapToList;
import pcgen.core.utils.MapKey;
import pcgen.core.utils.MapKeyMapToList;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.core.utils.StringKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.DoubleKeyMap;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;
import pcgen.util.enumeration.VisionType;

/**
 * <code>PObject</code><br>
 * This is the base class for several objects in the PCGen database.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public class PObject extends PrereqObject implements Cloneable, Serializable, Comparable<Object>,
	SourcedObject, KeyedListContainer, KeyedObject
{
	/** Standard serialVersionUID for Serializable objects */
	private static final long serialVersionUID = 1;

	/** a boolean for whether something should recurse, default is false */
	private static boolean dontRecurse = false;

	/** A map to hold items keyed by Strings for the object */
	protected Map<StringKey, String> stringChar = new HashMap<StringKey, String>();
	/** A map to hold items keyed by Integers for the object */
	protected Map<IntegerKey, Integer> integerChar = new HashMap<IntegerKey, Integer>();
	/** A map of Lists for the object */
	protected ListKeyMapToList listChar = new ListKeyMapToList();
	
	protected final MapKeyMapToList mapChar = new MapKeyMapToList();

	/** List of associated items for the object */
	// TODO Contains strings or FeatMultipleObjects
	private ArrayList<AssociatedChoice<String>> associatedList = null;

	/** List of Level Abilities for the object  */
	private List<LevelAbility> levelAbilityList = null;

	private SourceEntry theSource = new SourceEntry();

	/**
	 * A map of vision types associated with the object,
	 * Key: vision type, Value: vision range.
	 */
	protected List<Vision> vision = null;
	private HashMap<String, String> pluginDataMap = new HashMap<String, String>();

	/** The Non-internationalized name to use to refer to this object. */
	protected String keyName = Constants.EMPTY_STRING;
	/** The name to display to the user.  This should be internationalized. */
	protected String displayName = Constants.EMPTY_STRING;

	/** Indicates if this object should be displayed to the user in the UI. */
	protected Visibility visibility = Visibility.DEFAULT;

	/** Map of the bonuses for the object  */
	private HashMap<String, String> bonusMap = null;
	/** List of Bonuses for the object */
	private List<BonusObj> bonusList = new ArrayList<BonusObj>();

	private HashMap<String, String> changeProfMap = new HashMap<String, String>();

	private Movement movement;
	private SpellSupport spellSupport = new SpellSupport();
//	private List<SpellLikeAbility> spellLikeAbilities = null;
	
	private VariableList variableList = null;

	/** description is Product Identity */
	private boolean descIsPI = false;
	/** name is Product Identity */
	private boolean nameIsPI = false;

	private boolean isNewItem = true;

	/** Holds the level of encumberance due to armor for the object */
	private Load encumberedArmorMove = Load.LIGHT;
	/** Holds the level of encumberance due to load for the object */
	private Load encumberedLoadMove = Load.LIGHT;

	private List<DamageReduction> drList = new ArrayList<DamageReduction>();

	private String chooseLanguageAutos = Constants.EMPTY_STRING;
	private TreeSet<Language> theBonusLangs = null;

	/** Number of followers of each type allowed */
	private Map<String, List<String>> followerNumbers = null;
	/** List of followers of a type allowed to be selected. */
	private Map<String, List<FollowerOption>> theAvailableFollowers = null;

	private List<String> weaponProfBonus = null;

	private List<Description> theDescriptions = null;
	
	private DoubleKeyMap<Class, String, List<String>> qualifyKeys = null;
	
	private URI sourceURI = null;
	
	private Set<String> types = new LinkedHashSet<String>();
	
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
		associatedList.set(index, new AssociatedChoice<String>(aString));
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
		if (associatedList == null) 
		{
			return Constants.EMPTY_STRING;
		}

		if (expand)
		{
			int currentCount = 0;
			for ( final AssociatedChoice<String> choice : associatedList )
			{
				final int choiceInd = choice.size() - 1;
				if ( idx <= (currentCount + choiceInd) )
				{
					if ( choiceInd == 0 )
					{
						return choice.getDefaultChoice();
					}
					return choice.getChoice(String.valueOf(idx - currentCount));
				}
				currentCount += choice.size();
			}
		}
		else if (associatedList.get(idx) instanceof FeatMultipleChoice)
		{
			return associatedList.get(idx).toString();
		}

		return associatedList.get(idx).getDefaultChoice();
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

		if (expand)
		{
			int count = 0;
			for ( AssociatedChoice<String> choice : associatedList )
			{
				count += choice.size();
			}
			return count;
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
		if (associatedList == null) 
		{
			return Constants.EMPTY_STRING;
		}
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

					for ( Skill skill1 : Globals.getSkillList() )
					{
						boolean toClear = true;
						final StringTokenizer cTok = new StringTokenizer(typeString, ".");

						while (cTok.hasMoreTokens() && toClear)
						{
							if (!skill1.isType(cTok.nextToken()))
							{
								toClear = false;
							}
						}

						if (toClear)
						{
							listChar.removeFromListFor(ListKey.CROSS_CLASS_SKILLS, skill1.getKeyName());
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
			for (Iterator<Skill> e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				skill = e1.next();

				if (skill.isType(entry.substring(5)))
				{
					listChar.addToListFor(ListKey.CROSS_CLASS_SKILLS, skill.getKeyName());
				}
			}
		}
		else if ("ALL".equals(entry))
		{
			for (Iterator<Skill> e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				skill = e1.next();
				listChar.addToListFor(ListKey.CROSS_CLASS_SKILLS, skill.getKeyName());
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
	public final void addAllCcSkills(final List<String> entries)
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
	public final List<String> getCcSkillList()
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
		stringChar.put(StringKey.DESCRIPTION, a);
	}

	/**
	 * Adds a description for this object.  Multiple descriptions are allowed 
	 * and will be concatonated on output.
	 * 
	 * <p>The format of the description tag 
	 * @param aDesc
	 */
	public void addDescription( final Description aDesc )
	{
		if ( theDescriptions == null )
		{
			theDescriptions = new ArrayList<Description>();
		}
		aDesc.setOwner( this );
		theDescriptions.add( aDesc );
	}
	
	/**
	 * Clears all current descriptions for the object.
	 */
	public void removeAllDescriptions()
	{
		theDescriptions = null;
	}
	
	/**
	 * Removes <tt>Description</tt>s who's PCC Text matches the pattern
	 * specified.
	 *  
	 * @param aDescPattern The regular expression to search for.
	 */
	public void removeDescription( final String aDescPattern )
	{
		if ( theDescriptions == null )
		{
			return;
		}
		final Pattern pattern = Pattern.compile(aDescPattern);

		for ( final Iterator<Description> i = theDescriptions.iterator(); i.hasNext(); )
		{
			final String descText = i.next().getPCCText();
			final Matcher matcher = pattern.matcher(descText);
			if ( matcher.find() )
//			if ( descText.matches(aDescPattern) )
			{
				i.remove();
			}
		}
	}
	
	public final String getDescription()
	{
		String characteristic = stringChar.get(StringKey.DESCRIPTION);
		return characteristic == null ? Constants.EMPTY_STRING : characteristic;
	}
	
	/**
	 * Get the description of this object
	 * 
	 * @param aPC The PlayerCharacter this object is associated to.
	 * @return the description of this object
	 */
	public String getDescription(final PlayerCharacter aPC)
	{
		if ( theDescriptions == null )
		{
			return Constants.EMPTY_STRING;
		}
		final StringBuffer buf = new StringBuffer();
		boolean wrote = false;
		for ( final Description desc : theDescriptions )
		{
			final String str = desc.getDescription(aPC);
			if ( str.length() > 0 )
			{
				if ( wrote )
				{
					buf.append(Constants.COMMA + ' ');
				}
				buf.append(str);
				wrote = true;
			}
			else
			{
				wrote = false;
			}
		}
		return buf.toString();
	}

	public List<Description> getDescriptionList()
	{
		if ( theDescriptions == null )
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(theDescriptions);
	}
	
	/**
	 * Get the plugin data for this object
	 * @param key
	 * @return the plugin data for this object
	 */
	public final String getPluginData(final String key) {
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
	public final List<LevelAbility> getLevelAbilityList()
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

					for (Iterator<Skill> e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						skill = e1.next();
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
							listChar.removeFromListFor(ListKey.CLASS_SKILLS, skill.getKeyName());
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
			for (Iterator<Skill> e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				skill = e1.next();

				if (skill.isType(entry.substring(5)))
				{
					listChar.addToListFor(ListKey.CLASS_SKILLS, skill.getKeyName());
				}
			}
		}
		else if ("ALL".equals(entry))
		{
			for (Iterator<Skill> e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				skill = e1.next();
				listChar.addToListFor(ListKey.CLASS_SKILLS, skill.getKeyName());
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
	public final void addAllCSkills(final List<String> entries)
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
	public final List<String> getCSkillList()
	{
		return listChar.getListFor(ListKey.CLASS_SKILLS);
	}

	/**
	 * Get the movement for this object
	 * @return the movement for this object
	 */
	public List<Movement> getMovements()
	{
		if (movement == null)
		{
			return Collections.emptyList();
		}
		return Collections.singletonList(movement);
	}

	/**
	 * Get the encumberance due to armor
	 * @return the encumberance due to armor
	 */
	public Load getEncumberedArmorMove()
	{
		return encumberedArmorMove;
	}

	/**
	 * Get the encumberance due to load
	 * @return the encumberance due to load
	 */
	public Load getEncumberedLoadMove()
	{
		return encumberedLoadMove;
	}

	/**
	 * Set the encumberance due to armor
	 * @param encumberedArmorMove
	 * @param level FIXME
	 */
	public void setEncumberedArmorMove(Load encumberedArmorMove, int level)
	{
		this.encumberedArmorMove = encumberedArmorMove;
	}

	/**
	 * Set the encumberance due to load
	 * @param encumberedLoadMove
	 * @param level FIXME
	 */
	public void setEncumberedLoadMove(Load encumberedLoadMove, int level)
	{
		this.encumberedLoadMove = encumberedLoadMove;
	}

	/**
	 * Sets the natural weapon equipment items list for this object
	 * @param aList
	 */
	public void setNaturalWeapons(final List<Equipment> aList)
	{
		listChar.removeListFor(ListKey.NATURAL_WEAPONS);
		listChar.addAllToListFor(ListKey.NATURAL_WEAPONS, aList);
	}

	/**
	 * Get the natural weapons list for this object
	 * @return the natural weapons list for this object
	 */
	public List<Equipment> getNaturalWeapons()
	{
		return getSafeListFor(ListKey.NATURAL_WEAPONS);
	}

	/**
	 * Get the list of temporary bonuses for this list
	 * @return the list of temporary bonuses for this list
	 */
	public List<BonusObj> getTempBonusList()
	{
		return getSafeListFor(ListKey.TEMP_BONUS);
	}

	/**
	 * Set the temporary description for this object
	 * @param aString
	 */
	public final void setTempDescription(final String aString)
	{
		stringChar.put(StringKey.TEMP_DESCRIPTION, aString);
	}

	/**
	 * Get the temporary description of this object
	 * @return the temporary description of this object
	 */
	public final String getTempDescription()
	{
		String characteristic = stringChar.get(StringKey.TEMP_DESCRIPTION);
		return characteristic == null ? Constants.EMPTY_STRING : characteristic;
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
	public final Set<String> getVariableNamesAsUnmodifiableSet()
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
	public List<Ability> getVirtualFeatList()
	{
		return getSafeListFor(ListKey.VIRTUAL_FEATS);
	}
	
//	/**
//	 * Add automatic weapon proficienies for this object
//	 * @param aString
//	 */
//	public final void addWeaponProfAutos(final String aString)
//	{
//		final StringTokenizer aTok = new StringTokenizer(aString, "|");
//
//		ListKey<String> weaponProfListKey = ListKey.WEAPON_PROF;
//
//		while (aTok.hasMoreTokens())
//		{
//			final String bString = aTok.nextToken();
//
//			if (".CLEAR".equals(bString))
//			{
//				listChar.removeListFor(weaponProfListKey);
//			}
//			else if (bString.startsWith("TYPE=") || bString.startsWith("TYPE."))
//			{
//				final Collection<WeaponProf> weaponProfsOfType = Globals.getAllWeaponProfsOfType(bString.substring(5));
//				if (weaponProfsOfType != null)
//				{
//					for (Iterator<WeaponProf> e = weaponProfsOfType.iterator(); e.hasNext();)
//					{
//						final String cString = e.next().getKeyName();
//
//						if (!containsInList(weaponProfListKey, cString))
//						{
//							listChar.addToListFor(weaponProfListKey, cString);
//						}
//					}
//				}
//			}
//			else
//			{
//				if (!containsInList(weaponProfListKey, bString))
//				{
//					listChar.addToListFor(weaponProfListKey, bString);
//				}
//			}
//		}
//	}

//	/**
//	 * Get the automatic weapon proficiencies for this object
//	 * @return the automatic weapon proficiencies for this object
//	 */
//	public List<String> getWeaponProfAutos()
//	{
//		return getSafeListFor(ListKey.WEAPON_PROF);
//	}

	/**
	 * Add the collection passed in to the associated list for this object
	 * @param collection
	 */
	public final void addAllToAssociated(final Collection<String> collection)
	{
		if (associatedList == null)
		{
			associatedList = new ArrayList<AssociatedChoice<String>>();
		}

		for ( String choice : collection )
		{
			addAssociated( choice );
		}
	}

	/**
	 * Add the item to the associated list for this object
	 * @param aString
	 */
	public final void addAssociated(final String aString)
	{
		if (associatedList == null)
		{
			associatedList = new ArrayList<AssociatedChoice<String>>();
		}

		associatedList.add(new AssociatedChoice<String>(aString));
	}

	/**
	 * Add a feat choice to the associated list for this object
	 * @param aFeatChoices
	 */
	public final void addAssociated(final AssociatedChoice<String> aFeatChoices)
	{
		if (associatedList == null)
		{
			associatedList = new ArrayList<AssociatedChoice<String>>();
		}

		associatedList.add(aFeatChoices);
	}

	public final void addAssociatedTo( final List<String> choices )
	{
		if (associatedList != null)
		{
			for ( AssociatedChoice<String> choice : associatedList )
			{
				final String choiceStr = choice.getDefaultChoice();
				if ( choiceStr.equals(Constants.EMPTY_STRING) )
				{
					choices.add(null);
				}
				else
				{
					choices.add( choice.getDefaultChoice() );
				}
			}
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
	public final void addVirtualFeats(final List<Ability> aFeatList)
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
	public List<String> getChangeProfList(final PlayerCharacter character)
	{
		final List<String> aList = new ArrayList<String>();

		for (Iterator<String> e = changeProfMap.keySet().iterator(); e.hasNext();)
		{
			// aKey will either be:
			//  TYPE.blah
			// or
			//  Weapon Name
			final String aKey = e.next();

			// New proficiency type, such as Martial or Simple
			final String newProfType = changeProfMap.get(aKey);

			if (aKey.startsWith("TYPE."))
			{
				// need to get all items of this TYPE
				for (Iterator<Equipment> eq = EquipmentList.getEquipmentOfType(aKey.substring(5), "").iterator(); eq.hasNext();)
				{
					final String aName = eq.next().profKey(character);
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

				final String aName = aEq.profKey(character);
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
	public void addVision(Vision v)
	{
		//CONSIDER Need null check?
		if (vision == null) {
			vision = new ArrayList<Vision>();
		}
		vision.add(v);
	}
	
	public void clearVisionList() {
		if (vision != null)
		{
			vision.clear();
		}
	}
	
	public boolean removeVisionType(VisionType type) {
		if (vision == null) {
			return false;
		}
		for (Vision vis : vision) {
			if (vis.getType().equals(type)) {
				return vision.remove(vis);
			}
		}
		return false;
	}

	/**
	 * Retrieve the vision types associated with the object.
	 * 
	 * @return List of Vision objects associated with the object.
	 */
	public List<Vision> getVision()
	{
		return vision;
	}
	
	/**
	 * Retrieve this object's visibility in the GUI and on the output sheet
	 * @return Visibility in the GUI and on the output sheet 
	 */
	public Visibility getVisibility()
	{
		return visibility;
	}

	/**
	 * Set the object's visibility in the GUI and on the output sheet
	 * @param Visibility in the GUI and on the output sheet 
	 */
	public void setVisibility(Visibility argVisibility)
	{
		visibility = argVisibility;
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

		for ( AssociatedChoice<String> choice : associatedList )
		{
			for ( String val : choice.getChoices() )
			{
				if ( val.equalsIgnoreCase(associated) )
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Add a natural weapon to the character list.
	 *
	 * @param weapon
	 * @param level
	 */
	public void addNaturalWeapon(final Equipment weapon, final int level)
	{
		listChar.addToListFor(ListKey.NATURAL_WEAPONS, weapon);
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
	@Override
	public PObject clone() throws CloneNotSupportedException
	{
		final PObject retVal = (PObject) super.clone();
		retVal.stringChar = new HashMap<StringKey, String>();
		retVal.stringChar.putAll(stringChar);
		retVal.integerChar = new HashMap<IntegerKey, Integer>();
		retVal.integerChar.putAll(integerChar);
		retVal.listChar = new ListKeyMapToList();
		retVal.listChar.addAllLists(listChar);
		//SAVE is a special case: starts out empty
		// because the saveList is based on user selections (merton_monk@yahoo.com)
		retVal.listChar.removeListFor(ListKey.SAVE);
		retVal.types = new LinkedHashSet<String>();
		retVal.types.addAll(types);

		retVal.setName(displayName);
		retVal.visibility = visibility;
		retVal.setKeyName(keyName);
		retVal.spellSupport = (SpellSupport) spellSupport.clone();

		// added 04 Aug 2003 by sage_sam -- bug#765749
		// need to copy map correctly during a clone
//		if (sourceMap != null)
//		{
//			retVal.sourceMap = new HashMap<String, String>();
//			retVal.sourceMap.putAll(this.sourceMap);
//		}
		retVal.theSource = theSource;

		retVal.changeProfMap = new HashMap<String, String>(changeProfMap);

		if (associatedList != null)
		{
			retVal.associatedList = new ArrayList<AssociatedChoice<String>>(associatedList);
		}

		if (bonusList != null)
		{
			retVal.bonusList = new ArrayList<BonusObj>();
			for (BonusObj orig : bonusList)
			{
				retVal.bonusList.add((BonusObj)orig.clone());

			}
			retVal.ownBonuses();
		}

		if (drList != null)
		{
			retVal.drList = new ArrayList<DamageReduction>();
			for (DamageReduction orig : drList)
			{
				retVal.drList.add(orig.clone());

			}
		}

		if (variableList != null)
		{
			retVal.variableList = (VariableList) variableList.clone();
		}

		if (bonusMap != null)
		{
			retVal.bonusMap = new HashMap<String, String>(bonusMap);
		}

		retVal.vision = vision;

		if ((levelAbilityList != null) && !levelAbilityList.isEmpty())
		{
			retVal.levelAbilityList = new ArrayList<LevelAbility>();

			for ( LevelAbility ab : levelAbilityList )
			{
				ab = (LevelAbility) ab.clone();
				ab.setOwner(retVal);
				retVal.levelAbilityList.add(ab);
			}
		}
		if ( followerNumbers != null )
		{
			retVal.followerNumbers = new HashMap<String,List<String>>(followerNumbers);
		}
		if ( theAvailableFollowers != null )
		{
			retVal.theAvailableFollowers = new HashMap<String, List<FollowerOption>>( theAvailableFollowers );
		}

		if ( theBonusLangs != null )
		{
			retVal.theBonusLangs = new TreeSet<Language>( theBonusLangs );
		}
		
		if ( weaponProfBonus != null )
		{
			retVal.weaponProfBonus = new ArrayList<String>(weaponProfBonus);
		}
		
		if ( this.theDescriptions != null )
		{
			retVal.theDescriptions = new ArrayList<Description>();
			for ( final Description desc : theDescriptions )
			{
				desc.setOwner(retVal);
				retVal.theDescriptions.add(desc);
			}
		}
		return retVal;
	}

	/**
	 * Compares the keys of the object.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Object obj)
	{
		if (obj != null)
		{
			//this should throw a ClassCastException for non-PObjects, like the Comparable interface calls for
			return this.getKeyName().compareToIgnoreCase(((PObject) obj).getKeyName());
		}
		return 1;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object obj )
	{
		if ( obj == null )
		{
			return false;
		}
		final String thisKey;
		final String otherKey;
		if ( obj instanceof PObject )
		{
			thisKey = getKeyName();
			otherKey = ((PObject)obj).getKeyName();
		}
		else
		{
			thisKey = toString();
			otherKey = obj.toString();
		}
		return thisKey.equalsIgnoreCase( otherKey );
	}

	/**
	 * Set the CHOICE string
	 * @param aString
	 */
	public void setChoiceString(final String aString)
	{
		stringChar.put(StringKey.CHOICE_STRING, aString);
	}

	/**
	 * Get the CHOICE string
	 * @return the CHOICE string
	 */
	public final String getChoiceString()
	{
		String characteristic = stringChar.get(StringKey.CHOICE_STRING);
		return characteristic == null ? Constants.EMPTY_STRING : characteristic;
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

	public void addDR(DamageReduction aDR)
	{
		drList.add(aDR);
	}

	public void clearDR()
	{
		drList.clear();
	}

	public List<DamageReduction> getDRList()
	{
		return Collections.unmodifiableList(drList);
	}

	/**
	 * Set the Key Name
	 * @param aString
	 */
	public void setKeyName(final String aString)
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
	 * Return the qualified key, ususally used as the source in a 
	 * getVariableValue call. Always returns an empty string, but 
	 * may be overridden by subclasses to return a required value.
	 * 
	 * @return The qualified name of the object
	 */
	public String getQualifiedKey()
	{
		return Constants.EMPTY_STRING;
	}
	
	/**
	 * Set the name (sets keyname also)
	 * @param aString
	 */
	public void setName(final String aString)
	{
		if (!aString.endsWith(".MOD"))
		{
			fireNameChanged(displayName, aString);
			displayName = aString;
			this.setKeyName(aString);
		}
	}

	/**
	 * This method sets only the name not the key.
	 * @param aName Name to use for display
	 */
	public void setDisplayName( final String aName )
	{
		displayName = aName;
	}
	
	/**
	 * Get name
	 * @return name
	 */
	public String getDisplayName()
	{
		return displayName;
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
		stringChar.put(StringKey.OUTPUT_NAME, newName);
	}

	/**
	 * Get the output name of the item
	 * @return the output name of the item
	 */
	public final String getOutputName()
	{
		String outputName = stringChar.get(StringKey.OUTPUT_NAME);
		// if no OutputName has been defined, just return the regular name
		if (outputName == null || outputName.length() == 0)
		{
			return displayName;
		}
		else if (outputName.equalsIgnoreCase("[BASE]") && displayName.indexOf('(') != -1)
		{
			outputName = this.displayName.substring(0, displayName.indexOf('(')).trim();
		}
		
		return outputName;
	}

	/**
	 * Set the qualify string
	 */
	public void putQualifyString(Class cl, String category, String key) {
		if (qualifyKeys == null)
		{
			qualifyKeys = new DoubleKeyMap<Class, String, List<String>>();
		}
		List<String> list = qualifyKeys.get(cl, category);
		if (list == null) {
			list = new ArrayList<String>();
			qualifyKeys.put(cl, category, list);
		}
		list.add(key);
		//No need to put list back in qualifyKeys, it is fetched by reference
	}

	public final boolean grantsQualify(PObject qualTestObject)
	{
		if (qualifyKeys == null) {
			return false;
		}
		Class<? extends PObject> cl = qualTestObject.getClass();
		String key = qualTestObject.getKeyName();
		String category = Ability.class.equals(cl) ? ((Ability) qualTestObject)
				.getCategory() : null;
		List<String> directList = qualifyKeys.get(cl, category);
		List<String> oldSyntaxList = qualifyKeys
				.get(Object.class, category);
		return (directList != null && directList.contains(key))
				|| (oldSyntaxList != null && oldSyntaxList.contains(key));
	}
	
	//TODO This exposes internal structure - be careful.
	public final DoubleKeyMap<Class, String, List<String>> getQualifyMap()
	{
		return qualifyKeys;
	}

	/**
	 * Set the SR
	 * @param newSR
	 */
	public void setSR(int aLevel, String newSR)
	{
		stringChar.put(StringKey.SR_FORMULA, newSR);
	}
	
	/**
	 * Clear the SR
	 */
	public void clearSRList()
	{
		stringChar.remove(StringKey.SR_FORMULA);
	}

	/**
	 * Get the SR Formula
	 * @return the SR Formula
	 */
	public String getSRFormula()
	{
		return stringChar.get(StringKey.SR_FORMULA);
	}

	/**
	 * Set the source file for this object
	 * @param sourceFile
	 */
	public final void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	/**
	 * Get the source file for this object
	 * @return the source file for this object
	 */
	public final URI getSourceURI()
	{
		return sourceURI;
	}

	/**
	 * Set the source from a map of source values.
	 * 
	 * <p>The map has the form "source type" ==> "source value".  For example,
	 * <code>"SHORT" ==> "RSRD"</code>.
	 * 
	 * @param arg A <tt>Map</tt> containing source values. 
	 * @throws ParseException If the source date cannot be parsed.
	 * 
	 * @see pcgen.core.SourceEntry#setFromMap(Map)
	 */
	public final void setSourceMap(final Map<String, String> arg) 
		throws ParseException
	{
		theSource.setFromMap( arg );
	}

	/**
	 * Returns the source entry for this object.
	 * 
	 * @return a <tt>SourceEntry</tt>
	 * 
	 * @see pcgen.core.SourceEntry
	 */
	public SourceEntry getSourceEntry()
	{
		if ( theSource == null )
		{
			return new SourceEntry();
		}
		return theSource;
	}
	
	/**
	 * Sets the source entry for this object.
	 * 
	 * @param aSource A <tt>SourceEntry</tt> to set.
	 */
	public void setSource( final SourceEntry aSource )
	{
		theSource = aSource;
	}
	
	/**
	 * Gets the Source string for this object using the default source display
	 * mode.
	 * 
	 * @return The Source string.
	 */
	public String getDefaultSourceString()
	{
		return theSource.toString();

	}
	
	/**
	 * Get the SA by key
	 * @param aKey
	 * @return the SA
	 */
	public final SpecialAbility getSpecialAbilityKeyed(final String aKey)
	{
		for ( SpecialAbility sa : getListFor(ListKey.SPECIAL_ABILITY) )
		{
			if (sa.getKeyName().equalsIgnoreCase(aKey))
			{
				return sa;
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
		return "POBJECT|" + getKeyName(); //$NON-NLS-1$
	}

	/**
	 * Add automatic languages
	 * 
	 * @param aLangKey A language key.
	 */
	public final void addLanguageAuto(final String aLangKey)
	{
		ListKey<Language> autoLanguageListKey = ListKey.AUTO_LANGUAGES;
		if (".CLEAR".equals(aLangKey)) //$NON-NLS-1$
		{
			listChar.removeListFor(autoLanguageListKey);
		}
		else if ("ALL".equals(aLangKey))
		{
			listChar.addAllToListFor(autoLanguageListKey, Globals.getLanguageList());
		}
		else if (aLangKey.startsWith("TYPE=") || aLangKey.startsWith("TYPE."))
		{
			final String type = aLangKey.substring(5);
			List<Language> langList = Globals.getLanguageList();
			langList = Globals.getLanguagesFromListOfType(langList, type);
			listChar.addAllToListFor(autoLanguageListKey, langList);
		}
		else
		{
			final Language lang = Globals.getLanguageKeyed(aLangKey);

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
			//Yes, this in inefficient... it's done rarely enough it's ok for now
			//Best performance improvement to offset this would be to make Type
			// in campaigns NOT order sensitive...
			return new ArrayList<String>(types).get(i);
		}

		return null;
	}

	/**
	 * Get the number of user defined types
	 * @return the number of user defined types
	 */
	public int getMyTypeCount()
	{
		return types.size();
	}

	/**
	 * This method gets access to the spell list.
	 * @return List
	 */
	public List<PCSpell> getSpellList()
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

	public void addSAB(SpecialAbility sa, int level)
	{
		mapChar.addToListFor(MapKey.SAB, level, sa);
	}

	public void clearSABList(int level)
	{
		mapChar.removeListFor(MapKey.SAB, level);
	}
	
	public void clearAllSABLists()
	{
		mapChar.removeListsFor(MapKey.SAB);
	}
	
	public void removeSAB(String s, int level)
	{
		List<SpecialAbility> sabs = mapChar.getListFor(MapKey.SAB, level);
		if (sabs != null)
		{
			for (SpecialAbility sa : sabs)
			{
				if (sa.getDisplayName().equals(s)
					|| sa.getDisplayName().startsWith(s + "|"))
				{
					mapChar.removeFromListFor(MapKey.SAB, level, sa);
				}
			}
		}
	}

	public void addSABToList(List<SpecialAbility> saList, PlayerCharacter pc)
	{
		List<SpecialAbility> sabs = mapChar.getListFor(MapKey.SAB, -9);
		if (sabs != null)
		{
			for (SpecialAbility sa : sabs)
			{
				if (pc == null || sa.qualifies(pc))
				{
					saList.add(sa);
				}
			}
		}
	}

	/**
	 * Get the type of PObject
	 * @return the type of PObject
	 */
	public String getType()
	{
		return CoreUtility.join(getTypeList(false), ".");
	}

	/**
	 * Returns false
	 *
	 * This method is meant to be overloaded by those classes that
	 * can have hidden types, which are currently Equipment, Feat and
	 * Skill.
	 *
	 * @return false
	 */
	boolean isTypeHidden(final String type)
	{
		return false;
	}

	public List<String> getTypeList(final boolean visibleOnly)
	{
		final List<String> ret = new ArrayList<String>(types);
		if (visibleOnly )
		{
			for ( String type : types )
			{
				if ( isTypeHidden(type) )
				{
					ret.remove(type);
				}
			}
		}
		return Collections.unmodifiableList(ret);
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

		return types.contains(myType);
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
			else if (!types.contains(aType))
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
	public final Iterator<Variable> getVariableIterator()
	{
		if (variableList == null)
		{
			return EmptyIterator.emptyIterator();
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

		final ArrayList<Float> varArray = new ArrayList<Float>();
		final ArrayList<String> tokenList = new ArrayList<String>();

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

			final String token = tokenList.get(varCount);
			final Float val = varArray.get(varCount);

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
		boolean ret = false;
		if (associatedList == null)
		{
			return ret;
		}

		for ( Iterator<AssociatedChoice<String>> i = associatedList.iterator(); i.hasNext(); )
		{
			AssociatedChoice<String> choice = i.next();
			ret = choice.removeDefaultChoice( associated );
			if (ret )
			{
				if ( choice.size() == 0 )
				{
					i.remove();
				}
			}
		}

		if (associatedList.size() == 0)
		{
			associatedList = null;
		}

		return ret;
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
		stringChar.put(StringKey.REGION, arg);
	}

	/**
	 * Get the region string
	 * @return the region string
	 */
	public final String getRegionString()
	{
		return stringChar.get(StringKey.REGION);
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
		for (Iterator<String> ri = getListFor(ListKey.REMOVE_STRING_LIST).iterator(); ri.hasNext();)
		{
			checkRemoval(aPC, ri.next());
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
		List<Ability> theFeatList = new ArrayList<Ability>(); // don't remove virtual or mfeats
		while (aTok.hasMoreTokens())
		{
			final String arg = aTok.nextToken();
			// could be a TYPE of feat
			if (arg.startsWith("TYPE."))
			{
				final String theType = arg.substring(5);
				for (Ability aFeat : aPC.getRealAbilitiesList(AbilityCategory.FEAT))
				{
					if (aFeat.isType(theType) && !theFeatList.contains(aFeat))
						theFeatList.add(aFeat);
				}
			}
			else if (arg.startsWith("CLASS."))
			{
				PCClass aClass = aPC.getClassKeyed(arg.substring(6));
				if (aClass != null)
				{
					for (PCLevelInfo element : aPC.getLevelInfo())
					{
						if (element.getClassKeyName().equalsIgnoreCase(aClass.getKeyName()))
						{
							for (Ability aFeat : (List<Ability>)element.getObjects())
							{
								if (!theFeatList.contains(aFeat))
									theFeatList.add(aFeat);
							}
						}
					}

				}
			}
			else if (arg.equals("CHOICE"))
			{
				for (Ability aFeat : aPC.getRealAbilitiesList(AbilityCategory.FEAT))
				{
					theFeatList.add(aFeat);
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
				Ability aFeat = theFeatList.get(ix);
				AbilityUtilities.modFeat(aPC, null, aFeat.getKeyName(), false, false);
				if (ix > theFeatList.size())
					ix = theFeatList.size();
			}
		}
	}

	/**
	 * Add auto array
	 * @param arg
	 */
	public final void addAutoArray(String arrayName, String item)
	{
		mapChar.addToListFor(MapKey.AUTO_ARRAY, arrayName, item);
	}

	/**
	 * Add the select armor proficiencies to the list
	 * @param aList
	 */
	public final void addSelectedArmorProfs(final List<String> aList)
	{
		//This can't do a direct addAll on listChar because this does duplication removal
		for (String aString : aList)
		{
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
	public final void addSelectedShieldProfs(final List<String> aList)
	{
		for (String aString : aList)
		{
			if (!containsInList(ListKey.SELECTED_SHIELD_PROFS, aString))
			{
				listChar.addToListFor(ListKey.SELECTED_SHIELD_PROFS, aString);
			}
		}
	}

	/**
	 * Clear the auto list
	 */
	public final void clearAutoMap()
	{
		mapChar.removeListsFor(MapKey.AUTO_ARRAY);
	}

	/**
	 * This does a partial clear of the auto list, removing any entries
	 * carrying the supplied tag
	 * @param tag The type to be removed e.g. WEAPONPROF
	 */
	public final void clearAutoTag(String tag)
	{
		mapChar.removeListFor(MapKey.AUTO_ARRAY, tag);
	}

	public final Set<String> getAutoMapKeys()
	{
		return mapChar.getSecondaryKeySet(MapKey.AUTO_ARRAY);
	}
	
	public final List<String> getAuto(String tag)
	{
		return mapChar.getListFor(MapKey.AUTO_ARRAY, tag);
	}
	
	/**
	 * Set the campaign source
	 * @param arg
	 */
	public void setSourceCampaign(final Campaign arg)
	{
		theSource.getSourceBook().setCampaign( arg );
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
		return theSource.getSourceBook().getCampaign();
	}

	/**
	 * gets the bonuses to a stat based on the stat Index
	 * @param statIdx
	 * @param aPC
	 * @return stat mod
	 */
	public int getStatMod(final int statIdx, final PlayerCharacter aPC)
	{
		final List<PCStat> statList = SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return 0;
		}

		final String aStat = statList.get(statIdx).getAbb();

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
			levelAbilityList = new ArrayList<LevelAbility>();
		}

		if (aString.startsWith(".CLEAR"))
		{
			if (".CLEAR".equals(aString))
			{
				if (aLevel > 0)
				{
					Logging.errorPrint("Warning: You performed a Dangerous .CLEAR in a ADD: Token");
					Logging.errorPrint("  A non-level limited .CLEAR was used in a Class Level line");
					Logging.errorPrint("  Today, this performs a .CLEAR on the entire PCClass");
					Logging.errorPrint("  However, you are using undocumented behavior that is subject to change");
					Logging.errorPrint("  Hint: It will change after PCGen 5.14");
					Logging.errorPrint("  Please level limit the .CLEAR (e.g. .CLEAR.LEVEL2)");
					Logging.errorPrint("  ... or put the ADD:.CLEAR on a non-level Class line");
				}
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

				if (aLevel > 0 && aLevel != level)
				{
					Logging.errorPrint("Warning: You performed a Dangerous .CLEAR in a ADD: Token");
					Logging.errorPrint("  A level limited .CLEAR was used in a Class Level line");
					Logging.errorPrint("  But was asked to clear a different Class Level than the Class Level Line it appeared on");
					Logging.errorPrint("  However, you are using undocumented behavior");
					Logging.errorPrint("  Please match the level to the limit on the .CLEAR (e.g. 2<tab>ADD:.CLEAR.LEVEL2)");
					Logging.errorPrint("  ... or put the ADD:.CLEAR on a non-level Class line");
				}
				
				if (level >= 0)
				{
					for (int x = levelAbilityList.size() - 1; x >= 0; --x)
					{
						final LevelAbility ability = levelAbilityList.get(x);

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
			for (BonusObj aBonus : getTempBonusList())
			{
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
		for (BonusObj aBonus : anObj.getTempBonusList())
		{
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
	public String piDescString(final PlayerCharacter aPC)
	{
		return piDescString(aPC, true);
	}

	/**
	 * In some cases, we need a PI-formatted string to place within a
	 * pre-existing <html> tag
	 * @return PI description
	 */
	public String piDescSubString(final PlayerCharacter aPC)
	{
		return piDescString(aPC, false);
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
	 * Remove all abilities gained via a level
	 * @param aLevel
	 */
	public void removeAllLevelAbilities(final int aLevel)
	{
		if (levelAbilityList != null)
		{
			for (int x = levelAbilityList.size() - 1; x >= 0; --x)
			{
				if (levelAbilityList.get(x).level() == aLevel)
				{
					levelAbilityList.remove(x);
				}
			}
		}
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
			final LevelAbility ability = levelAbilityList.get(x);

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
		return displayName;
	}

	protected int getSR(final PlayerCharacter aPC)
	{
		final String srFormula = getSRFormula();

		//if there's a current PC, go ahead and evaluate the formula
		if ((srFormula != null) && (aPC != null))
		{
			return aPC.getVariableValue(srFormula, getQualifiedKey()).intValue();
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
//		Iterator e;
		String aString;
		final StringBuffer txt = new StringBuffer(200);

		if (saveName)
		{
			txt.append(getDisplayName());
		}

		if (getNameIsPI())
		{
			txt.append("\tNAMEISPI:Y");
		}

		String outputName = stringChar.get(StringKey.OUTPUT_NAME);
		if ((outputName != null) && (outputName.length() > 0) && !outputName.equals(getDisplayName()))
		{
			txt.append("\tOUTPUTNAME:").append(outputName);
		}

		for ( final Description desc : getDescriptionList() )
		{
			txt.append("\tDESC:").append(pcgen.io.EntityEncoder.encode(desc.getPCCText()));
			
			if (getDescIsPI())
			{
				txt.append("\tDESCISPI:Yes");
			}
		}

//		if (!getDisplayName().equals(getKeyName()))
//		{
			txt.append("\tKEY:").append(getKeyName());
//		}

		Set<String> aaKeys = mapChar.getSecondaryKeySet(MapKey.AUTO_ARRAY);
		if (aaKeys != null)
		{
			for (String s : aaKeys)
			{
				List<String> values = mapChar.getListFor(MapKey.AUTO_ARRAY, s);
				for (String value : values)
				{
					if (value != null && value.trim().length() > 0)
					{
						txt.append("\tAUTO:").append(s).append(Constants.PIPE)
							.append(value);
					}
				}
			}
		}

		if (!(this instanceof PCClass) && (getBonusList().size() != 0))
		{
			for (BonusObj bonusobj : getBonusList())
			{
				txt.append("\tBONUS:").append(bonusobj.getPCCText()); //This formats the bonus items in the proper .lst manner
			}
		}

		List<String> ccSkillList = getCcSkillList();
		if ((ccSkillList != null) && (ccSkillList.size() != 0))
		{
			txt.append("\tCCSKILL:").append(CoreUtility.join(ccSkillList, "|"));
		}

		List<String> cSkillList = getCSkillList();
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

		for (DamageReduction reduction : getDRList())
		{
			boolean levelBased = false;
			if (this instanceof PCClass)
			{
				levelBased = reduction.isForClassLevel(getKeyName());
			}
			if (!levelBased)
			{
				txt.append("\t").append(reduction.getPCCText(true));
			}
		}

		final List<String> langList = CoreUtility.toStringRepresentation(getSafeListFor(ListKey.AUTO_LANGUAGES));

		if (langList.size() != 0)
		{
			txt.append("\tLANGAUTO:").append(CoreUtility.join(langList, ","));
		}

		if (movement != null && movement.getNumberOfMovements() > 0)
		{
			txt.append(movement.toLSTString());
		}

		if (hasPreReqs())
		{
			final StringWriter writer = new StringWriter();
			for (Prerequisite prereq : getPreReqList())
			{
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

		List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);
		if (!(this instanceof PCClass) && (specialAbilityList != null) && (specialAbilityList.size() != 0))
		{
			for (SpecialAbility sa : specialAbilityList)
			{
				txt.append("\tSA:").append(sa.toString());
			}
		}
		
		if (!(this instanceof PCClass))
		{
			specialAbilityList = new ArrayList<SpecialAbility>();
			addSABToList(specialAbilityList, null);
			for (SpecialAbility sa : specialAbilityList)
			{
				txt.append("\tSAB:").append(sa.toString());
			}
		}

		DoubleKeyMap<Class, String, List<String>> dkm = getQualifyMap();
		if (dkm != null) 
		{
			for (Class cl : dkm.getKeySet())
			{
				String s = StringPClassUtil.getStringFor(cl);
				for (String category : dkm.getSecondaryKeySet(cl))
				{
					List<String> l = dkm.get(cl, category);
					if (l != null) {
						boolean started = false;
						for (String key : l) {
							if (!"alwaysValid".equals(key) && !"".equals(key)) {
								if (started) {
									txt.append(Constants.PIPE);
								} else {
									txt.append("\tQUALIFY:");
									if (s != null && s.length() > 0) {
										txt.append(s);
									}
									if (category != null) {
										txt.append('=').append(category);
									}
									txt.append(Constants.PIPE);
									started = true;
								}
								txt.append(key);
							}
						}
					}
				}
			}
		}

		if (!(this instanceof PCClass))
		{
			for (PCSpell s : getSpellList())
			{
				txt.append("\tSPELLS:").append(s.getPCCText());
			}
		}

		String SR = stringChar.get(StringKey.SR_FORMULA);
		if (!(this instanceof PCClass) && (SR != null) && (SR.length() != 0))
		{
			txt.append("\tSR:").append(SR);
		}

		if ((vision != null) && (vision.size() != 0))
		{
			final StringBuffer sb = new StringBuffer();

			for (Vision vis : vision)
			{
				if (!"0".equals(vis.getDistance()))
				{
					if (sb.length() > 0)
					{
						sb.append('|');
					}
					sb.append(vis);
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

		aString = theSource.getPageNumber();

		if (aString != null && aString.length() != 0)
		{
			txt.append("\tSOURCEPAGE:").append(aString);
		}

		String regionString = stringChar.get(StringKey.REGION);
		if ((regionString != null) && regionString.startsWith("0|"))
		{
			txt.append("\tREGION:").append(regionString.substring(2));
		}

		for (String s : getSafeListFor(ListKey.KITS))
		{
			if (s.startsWith("0|"))
			{
				txt.append("\tKIT:").append(s.substring(2));
			}
		}

		// SPELLLEVEL
		txt.append('\t').append(getSpellSupport().getPCCText());
		
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

		for ( LevelAbility levAbility : levelAbilityList )
		{
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

					final List<String> featList = new ArrayList<String>();
					levAbility.process(featList, aPC, pcLevelInfo);

					for ( String key : featList )
					{
						final Ability anAbility = Globals.getAbilityKeyed(
								"FEAT",
								key);

						if (anAbility != null)
						{
							switch (anAbility.getVisibility())
							{
								case HIDDEN:
								case OUTPUT_ONLY:
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

	public List<SpecialAbility> addSpecialAbilitiesToList(final List<SpecialAbility> aList, final PlayerCharacter aPC)
	{
		for ( SpecialAbility sa : getSafeListFor(ListKey.SPECIAL_ABILITY) )
		{
			if (sa.pcQualifiesFor(aPC))
			{
				final String key = sa.getKeyName();
				final int idx = key.indexOf("%CHOICE");

				if (idx >= 0)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(key.substring(0, idx));

					if (getAssociatedCount() != 0)
					{
						for (int i = 0; i < getAssociatedCount(); ++i)
						{
							if (i != 0)
							{
								sb.append(" ,");
							}

							sb.append(getAssociated(i));
						}
					}
					else
					{
						sb.append("<undefined>");
					}

					sb.append(key.substring(idx + 7));
					sa =
							new SpecialAbility(sb.toString(), sa.getSASource(),
								sa.getSADesc());
				}

				aList.add(sa);
			}
		}

		return aList;
	}

	/**
	 * This method is used to add the type to the appropriate global list if we
	 * are ever interested in knowing what types are available for a particular
	 * object type (for example, all of the different equipment types)
	 * 
	 * @param type
	 *            The name of the type that is to be added to the global list of
	 *            types.
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
		List<String> l = getSafeListFor(ListKey.KITS);
		for (int i = 0; i > l.size(); i++)
		{
			KitUtilities.makeKitSelections(0, l.get(i), i, aPC);
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

		for (LevelAbility ability : levelAbilityList)
		{
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
	public final ArrayList<AssociatedChoice<String>> getAssociatedList()
	{
		if (associatedList == null)
		{
			return new ArrayList<AssociatedChoice<String>>();
		}
		return associatedList;
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

		final StringBuffer aString = new StringBuffer(getElementInList(ListKey.UDAM, 0));

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

		List<String> umultList = getListFor(ListKey.UMULT);
		if (includeCrit && (umultList != null) && !umultList.isEmpty())
		{
			final String dString = umultList.get(0);

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
	public final void addAutoTagsToList(final String tag, final Collection aList, final PlayerCharacter aPC, boolean expandWeaponTypes)
	{
		List<String> list = mapChar.getListFor(MapKey.AUTO_ARRAY, tag);
		
		if (list == null)
		{
			return;
		}
		
		for (String val : list)
		{
			addAutoTagToList(tag, val, aList, aPC, expandWeaponTypes);
		}
	}

	private void addAutoTagToList(String tag, String aString, Collection aList,
		PlayerCharacter aPC, boolean expandWeaponTypes)
	{
		String preReqTag;
		final List<Prerequisite> aPreReqList = new ArrayList<Prerequisite>();
		final int j1 = aString.lastIndexOf('[');
		int j2 = aString.lastIndexOf(']');

		if (j2 < j1)
		{
			j2 = aString.length();
		}

		if (j1 >= 0)
		{
			preReqTag = aString.substring(j1 + 1, j2);
			Prerequisite prereq = null;
			try
			{
				final PreParserFactory factory = PreParserFactory.getInstance();
				prereq = factory.parse(preReqTag);
			}
			catch (PersistenceLayerException ple)
			{
				Logging.errorPrint(ple.getMessage(), ple);
			}

			if (prereq != null)
			{
				aPreReqList.add(prereq);
			}
			if (!PrereqHandler.passesAll(aPreReqList, aPC, null))
			{
				return;
			}

			aString = aString.substring(0, j1);
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		while (aTok.hasMoreTokens())
		{
			String tok = aTok.nextToken();

			if ((tok.startsWith("TYPE=") || tok.startsWith("TYPE."))
				&& tag.startsWith("WEAPON") && expandWeaponTypes)
			{
				List<String> xList = processWeaponAutoTags(aPC, tok);

				aList.addAll(xList);
			}
			else if ((tok.startsWith("TYPE=") || tok.startsWith("TYPE."))
				&& tag.startsWith("ARMOR"))
			{
				aList.add(tok);
			}
			else if (tag.startsWith("EQUIP"))
			{
				final Equipment aEq =
						EquipmentList.getEquipmentFromName(tok, aPC);

				if (aEq != null)
				{
					final Equipment newEq = aEq.clone();
					newEq.setQty(1);
					newEq.setAutomatic(true);
					int index = aPC.getCachedOutputIndex(newEq.getKeyName());
					newEq.setOutputIndex(index >= 0 ? index : aList.size()+1);
					aList.add(newEq);
				}
			}
			else if ("%LIST".equals(tok))
			{
				for (Iterator<AssociatedChoice<String>> e =
						getAssociatedList().iterator(); e.hasNext();)
				{
					aList.add(e.next().getDefaultChoice());
				}
			}
			else if ("DEITYWEAPONS".equals(tok))
			{
				if (aPC.getDeity() != null)
				{
					String weaponList = aPC.getDeity().getFavoredWeapon();

					if (!("ALL".equalsIgnoreCase(weaponList) || "ANY"
						.equalsIgnoreCase(weaponList)))
					{
						final StringTokenizer bTok =
								new StringTokenizer(weaponList, "|");

						while (bTok.hasMoreTokens())
						{
							final String bString = bTok.nextToken();
							final WeaponProf wp =
									Globals.getWeaponProfKeyed(bString);
							if (!wp.isType("Natural"))
							{
								aList.add(bString);
							}
						}
					}

				}
			}
			else
			{
				// add tok to list
				aList.add(tok);
			}
		}
	}

	/**
	 * @param aPC
	 * @param tok
	 * @return
	 */
	private List<String> processWeaponAutoTags(final PlayerCharacter aPC, String tok)
	{
		final StringTokenizer bTok = new StringTokenizer(tok.substring(5), ".");
		List<String> xList = null;

		while (bTok.hasMoreTokens())
		{
			final String bString = bTok.nextToken();
			final List<WeaponProf> pcWeapProfList = Globals.getWeaponProfs(bString, aPC);
			final List<Equipment> pcWeaponList = new ArrayList<Equipment>();
			if (pcWeapProfList.size() == 0)
			{
				pcWeaponList.addAll(EquipmentList.getEquipmentOfType("Weapon." + bString, ""));
			}

			if (xList == null)
			{
				xList = new ArrayList<String>();

				for (WeaponProf obj : pcWeapProfList)
				{
					final String wprof = obj.getKeyName();

					if (!xList.contains(wprof))
					{
						xList.add(wprof);
					}
				}
				
				for (Equipment obj : pcWeaponList)
				{
					final String wprof = obj.profKey(aPC);

					if (!xList.contains(wprof))
					{
						xList.add(wprof);
					}
				}
				
				
			}
			else
			{
				final List<String> removeList = new ArrayList<String>();

				for (Iterator<String> e = xList.iterator(); e.hasNext();)
				{
					final String wprof = e.next();
					boolean contains = false;

					for (WeaponProf obj : pcWeapProfList)
					{
						final String wprof2 = obj.getKeyName();

						if (wprof.equals(wprof2))
						{
							contains = true;

							break;
						}
					}
					if(!contains) {
						for (Equipment obj : pcWeaponList)
						{
							final String wprof2 = obj.profKey(aPC);

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
				}

				for (Iterator<String> e = removeList.iterator(); e.hasNext();)
				{
					final String wprof = e.next();
					xList.remove(wprof);
				}
			}
		}
		return xList;
	}

	/**
	 * Add a user defined type
	 * @param myType
	 */
	void addMyType(final String myType)
	{
		types.add(myType);
	}

	/*
	 * REFACTOR Get this OUT of PObject's interface since this is ONLY in PCClass.
	 * Not to mention that the overload code will probably be removed from PCClass.
	 */
	void fireNameChanged(final String oldName, final String newName)
	{
		// This method currently does nothing so it may be overriden in PCClass.
	}

	final boolean hasCcSkill(final String aName)
	{
		List<String> ccSkillList = getCcSkillList();
		if ((ccSkillList == null) || ccSkillList.isEmpty())
		{
			return false;
		}

		if (ccSkillList.contains(aName))
		{
			return true;
		}


		if (ccSkillList.contains("LIST"))
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
		
		for (String aString : getCcSkillList())
		{
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
		List<String> cSkillList = getCSkillList();
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

		for (String aString : cSkillList)
		{
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

	final void makeRegionSelection(final PlayerCharacter aPC)
	{
		makeRegionSelection(0, aPC);
	}

	final void makeRegionSelection(final int arg, final PlayerCharacter aPC)
	{
		String regionString = stringChar.get(StringKey.REGION);
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

		List<String> aList = new ArrayList<String>();

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
			for ( String region : aList )
			{
				if (aPC.getRegion().equalsIgnoreCase(region))
				{
					continue;
				}

				aPC.setRegion(region);
			}
		}
	}

	int numberInList(final String aType)
	{
		return 0;
	}

	public final Object removeAssociated(final int i)
	{
		if (associatedList == null)
		{
			throw new IndexOutOfBoundsException("size is 0, i=" + i);
		}

		return associatedList.remove(i);
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
		if ((displayName.indexOf('(') < 0) || (displayName.indexOf(')') < 0))
		{
			return displayName;
		}

		//we just take from the first ( to the first ), typically there should only be one of each
		final String subName = displayName.substring(displayName.indexOf('(') + 1, displayName.indexOf(')')); //the stuff inside the ()
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

	protected void clearMyType()
	{
		types.clear();
	}

	/**
	 * Clear the selected weapon proficiency bonuses
	 *
	 */
	public void clearSelectedWeaponProfBonus()
	{
		listChar.removeListFor(ListKey.SELECTED_WEAPON_PROF_BONUS);
	}

	private String piDescString(final PlayerCharacter aPC, final boolean useHeader)
	{
		final String desc = getDescription(aPC);

		if (descIsPI)
		{
			final StringBuffer sb = new StringBuffer(desc.length() + 30);

			if (useHeader)
			{
				sb.append("<html>");
			}

			sb.append("<b><i>").append(desc).append("</i></b>");

			if (useHeader)
			{
				sb.append("</html>");
			}

			return sb.toString();
		}

		return desc;
	}

	/**
	 * Returns the Product Identity string (with or without the header)
	 * @param useHeader
	 * @return the Product Identity string (with or without the header)
	 */
	private String piString(final boolean useHeader)
	{
		String aString = toString();

		if (SettingsHandler.guiUsesOutputNameEquipment())
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
		types.remove(myType);
	}

	/**
	 * Method getTemplateList. Returns an array list containing the raw
	 * templates granted by this race. This includes CHOOSE: strings
	 * which list templates a user will be asked to choose from.
	 *
	 * @return ArrayList of granted templates
	 */
	public List<String> getTemplateList()
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

	List<String> getTemplates(final boolean isImporting, final PlayerCharacter aPC)
	{
		final List<String> newTemplates = new ArrayList<String>();
		listChar.removeListFor(ListKey.TEMPLATES_ADDED);

		if (!isImporting)
		{
			for ( String templateKey : getTemplateList() )
			{
				if (templateKey.startsWith("CHOOSE:"))
				{
					templateKey = PCTemplate.chooseTemplate(this, templateKey.substring(7), true, aPC);
				}

				if (templateKey.length() != 0)
				{
					newTemplates.add(templateKey);
					listChar.addToListFor(ListKey.TEMPLATES_ADDED, templateKey);
					aPC.addTemplateKeyed(templateKey);

				}
			}
		}

		return newTemplates;
	}

	/**
	 * Get a list of the added templates
	 * @return a list of the added templates
	 */
	public List<String> templatesAdded()
	{
		return getSafeListFor(ListKey.TEMPLATES_ADDED);
	}

	/**
	 * Set the movement
	 * @param cm
	 */
	public void setMovement(Movement cm, int level)
	{
		movement = cm;
	}

	/**
	 * Set the list of Kits
	 * @param l
	 */
	public void setKitList(List<String> l)
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
		stringChar.put(key, s);
	}

	/**
	 * Get the string given a key
	 * @param key
	 * @return string
	 */
	public String getStringFor(StringKey key)
	{
		return stringChar.get(key);
	}

	/* *******************************************************************
	 * The following methods are part of the KeyedListContainer Interface
	 * ******************************************************************/
	public boolean containsListFor(ListKey key)
	{
		return listChar.containsListFor(key);
	}

	public <T> List<T> getListFor(ListKey<T> key)
	{
		return listChar.getListFor(key);
	}

	public final <T> List<T> getSafeListFor(ListKey<T> key)
	{
		return listChar.containsListFor(key) ? listChar.getListFor(key) : new ArrayList<T>();
	}

	public int getSizeOfListFor(ListKey key)
	{
		return listChar.sizeOfListFor(key);
	}

	public int getSafeSizeOfListFor(ListKey key)
	{
		return listChar.containsListFor(key) ? listChar.sizeOfListFor(key) : 0;
	}

	public <T> boolean containsInList(ListKey<T> key, T value)
	{
		return listChar.containsInList(key, value);
	}

	public <T> T getElementInList(ListKey<T> key, int i)
	{
		return listChar.getElementInList(key, i);
	}

	/**
	 * Set a list of languages that the character this Template is applied to
	 * automatically knows.
	 *
	 * @param  argChooseLanguageAutos  a comma separated list of languages to add
	 */
	public void setChooseLanguageAutos(final String argChooseLanguageAutos)
	{
		chooseLanguageAutos = argChooseLanguageAutos;
	}

	/**
	 * Get a list of languages that the character this Template is applied to
	 * automatically knows.
	 *
	 * @return  a comma separated list of languages automatically known
	 */
	public String getChooseLanguageAutos()
	{
		return chooseLanguageAutos;
	}

	/**
	 * Adds one chosen language.
	 *
	 * @param  flag
	 * @param  aPC
	 */
	void chooseLanguageAutos(final boolean flag, final PlayerCharacter aPC)
	{
		if (!flag && !"".equals(chooseLanguageAutos))
		{
			final List<Language> selectedList; // selected list of choices

			final ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setPool(1);
			c.setPoolFlag(false);
			c.setTitle("Pick a Language: ");

			Set<Language> list = Globals.getLanguagesFromString(chooseLanguageAutos);
			c.setAvailableList(new ArrayList<Language>(list));
			c.setVisible(true);
			selectedList = c.getSelectedList();

			if ((selectedList != null) && (selectedList.size() != 0))
			{
				aPC.addFreeLanguage(selectedList.get(0));
			}
		}
	}

	/* ************************************************
	 * End methods for the KeyedListContainer Interface
	 * ************************************************/

	/**
	 * Get a comma delimited list of the languages a character can choose from
	 * based upon their Intelligence stat
	 *
	 * @return A collection of languages
	 */
	public Set<Language> getLanguageBonus()
	{
		if ( theBonusLangs == null )
		{
			final Set<Language> ret = Collections.emptySet();
			return Collections.unmodifiableSet( ret );
		}
		return Collections.unmodifiableSortedSet(theBonusLangs);
	}

	/**
	 * Set a comma delimited list of the languages a character can choose from
	 * based upon their Intelligence stat
	 *
	 * @param  aString A string of langages
	 */
	public void setLanguageBonus( final String aString )
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);

		while (aTok.hasMoreTokens())
		{
			final String token = aTok.nextToken();

			if (".CLEAR".equals(token))
			{
				theBonusLangs = null;
			}
			else
			{
				if ( theBonusLangs == null )
				{
					theBonusLangs = new TreeSet<Language>();
				}
				
				Collection<Language> ll = Globals.getLanguagesFromString(token);
				
				if (ll != null)
				{
					theBonusLangs.addAll(ll);
				}
			}
		}
	}

	/**
	 * Clear out the list of bonus weapon proficiency keys that 
	 * this object will grant to characters. 
	 */
	public void clearWeaponProfBonus()
	{
		weaponProfBonus = null;
	}

	/**
	 * Add an entry to the list of bonus weapon proficiency keys that 
	 * this object will grant to characters. 
	 * @param aString The key of the weapon proficiency to be added.
	 */
	public void addWeaponProfBonus(final String aString)
	{
		if ( weaponProfBonus == null )
		{
			weaponProfBonus = new ArrayList<String>();
		}
		weaponProfBonus.add( aString );
	}

	/**
	 * Get a list of Weapon Proficiency keys that this object will grant to
	 * characters.
	 *
	 * @return a list of weapon proficiency keys
	 */
	public List<String> getWeaponProfBonus()
	{
		if ( weaponProfBonus == null )
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(weaponProfBonus);
	}

	/**
	 * Adds a formula to use to calculate the maximum number of followers of a
	 * given type for the character.
	 * @param aType Name of the follower type e.g. Familiar
	 * @param aFormula Formula, variable or number represent the max number
	 */
	public void setNumFollowers( final String aType, final String aFormula)
	{
		if ( followerNumbers == null )
		{
			followerNumbers = new HashMap<String, List<String>>();
		}
		List<String> numFollowers = followerNumbers.get( aType );
		if ( numFollowers == null )
		{
			numFollowers = new ArrayList<String>();
			followerNumbers.put( aType.toUpperCase(), numFollowers );
		}

		numFollowers.add( aFormula );
	}

	public List<String> getNumFollowers( final String aType )
	{
		if ( followerNumbers == null )
		{
			return null;
		}
		final List<String> formulas = followerNumbers.get( aType.toUpperCase() );
		if ( formulas == null )
		{
			return null;
		}
		return Collections.unmodifiableList( formulas );
	}

	public void addToFollowerList( final String aType, final FollowerOption anOption )
	{
		final String ucType = aType.toUpperCase();
		if ( theAvailableFollowers == null )
		{
			theAvailableFollowers = new HashMap<String, List<FollowerOption>>();
		}
		List<FollowerOption> followers = theAvailableFollowers.get( ucType );
		if ( followers == null )
		{
			followers = new ArrayList<FollowerOption>();
			theAvailableFollowers.put( ucType, followers );
		}
		followers.add( anOption );
	}
	
	/**
	 * Gets the list of potential followers of a given type.
	 * @param aType Type of follower to retrieve list for e.g. Familiar
	 * @return A List of FollowerOption objects representing the possible list
	 * of follower choices.
	 */
	public List<FollowerOption> getPotentialFollowers( final String aType )
	{
		if ( theAvailableFollowers == null )
		{
			return null;
		}
		final String ucType = aType.toUpperCase();
		List<FollowerOption> options = theAvailableFollowers.get( ucType );
		if ( options != null )
		{
			for ( int i = options.size() - 1; i >= 0; i-- )
			{
				FollowerOption opt = options.get(i);
				if ( opt.getRace() == null )
				{
					// This FollowerOption references more than one race.
					// We need to get the expanded versions and throw this one 
					// away
					final Collection<FollowerOption> newOpts = opt.getExpandedOptions();
					if ( newOpts != null )
					{
						options.addAll( newOpts );
					}
					options.remove(i);
				}
			}
		}
		return options;
	}

	private DoubleKeyMap<AbilityCategory, Ability.Nature, List<QualifiedObject<String>>> theAbilities = new DoubleKeyMap<AbilityCategory, Ability.Nature, List<QualifiedObject<String>>>();
	public void addAbility(final AbilityCategory aCategory, final Ability.Nature aNature, final QualifiedObject<String> anAbility)
	{
		List<QualifiedObject<String>> abilities = theAbilities.get(aCategory, aNature);
		if ( abilities == null )
		{
			abilities = new ArrayList<QualifiedObject<String>>();
			theAbilities.put(aCategory, aNature, abilities);
		}
		abilities.add(anAbility);
	}
	
	public List<QualifiedObject<String>> getRawAbilityObjects(
		final AbilityCategory aCategory, final Ability.Nature aNature)
	{
		List<QualifiedObject<String>> abilities = theAbilities.get(aCategory, aNature);
		if ( abilities == null )
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(theAbilities.get(aCategory, aNature));
	}
	
	public boolean removeAbility(final AbilityCategory aCategory,
		final Ability.Nature aNature, QualifiedObject<String> qo)
	{
		List<QualifiedObject<String>> abilities = theAbilities.get(aCategory, aNature);
		return abilities != null && abilities.remove(qo);
	}
	
	public List<String> getAbilityKeys(final PlayerCharacter aPC,
		final AbilityCategory aCategory, final Ability.Nature aNature)
	{
		final List<QualifiedObject<String>> abilities = theAbilities.get(aCategory, aNature);
		if ( abilities == null )
		{
			return Collections.emptyList();
		}
		final List<String> ret = new ArrayList<String>(abilities.size());
		for ( final QualifiedObject<String> str : abilities )
		{
			if ( str.qualifies(aPC) )
			{
				ret.add(str.getObject(aPC));
			}
		}
		return ret;
	}

	/**
	 * Get the list of bonuses for this object
	 * @return the list of bonuses for this object
	 */
	public List<BonusObj> getBonusList()
	{
		return bonusList;
	}

	/**
	 * Get the list of bounuses of a particular type for this object
	 * @param aType
	 * @param aName
	 * @return the list of bounuses of a particular type for this object
	 */
	public List<BonusObj> getBonusListOfType(final String aType, final String aName)
	{
		return BonusUtilities.getBonusFromList(getBonusList(), aType, aName);
	}

	/**
	 * Get the map of bonuses for this object
	 * @return bonusMap
	 */
	public HashMap<String, String> getBonusMap()
	{
		if (bonusMap == null)
		{
			bonusMap = new HashMap<String, String>();
		}

		return bonusMap;
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
	public double bonusTo(String aType, String aName, final Object obj, final List<BonusObj> aBonusList, final PlayerCharacter aPC)
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

		for ( BonusObj bonus : aBonusList )
		{
			String bString = bonus.toString().toUpperCase();

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
						retVal += calcBonus(xString, aType, aName, aTypePlusName, obj, iTimes, bonus, aPC);
					}

					bString = new StringBuffer().append(firstPart).append(getAssociated(0)).append(secondPart).toString()
						.toUpperCase();
				}
			}

			retVal += calcBonus(bString, aType, aName, aTypePlusName, obj, iTimes, bonus, aPC);
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
	 * Add a new bonus to the list of bonuses
	 * @param aString The unparsed bonus to be added
	 * @return true if new bonus is not null
	 */
	public final boolean addBonusList(final String aString)
	{
		return addBonusList(aString, false);
	}
	
	/**
	 * Add a new bonus to the list of bonuses. The bonus can optionally 
	 * only be added once no matter how many associated choices this 
	 * object has. This is normally used where a bonus is added for 
	 * each associated choice.
	 *  
	 * @param aString The unparsed bonus to be added
	 * @param addOnceOnly Should the bonus only be added once irrespective of number of choices 
	 * @return true if new bonus is not null
	 */
	private final boolean addBonusList(final String aString, final boolean addOnceOnly)
	{
		if (bonusList == null)
		{
			bonusList = new ArrayList<BonusObj>();
		}

		final BonusObj aBonus = Bonus.newBonus(aString);

		if (aBonus != null)
		{
			aBonus.setCreatorObject(this);
			aBonus.setAddOnceOnly(addOnceOnly);
			addBonusList(aBonus);
		}

		return (aBonus != null);
	}

	/**
	 * returns all BonusObj's that are "active"
	 * @param aPC A PlayerCharacter object.
	 * @return active bonuses
	 */
	public List<BonusObj> getActiveBonuses(final PlayerCharacter aPC)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : getBonusList() )
		{
			if (bonus.isApplied())
			{
				aList.add(bonus);
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
		for ( BonusObj bonus : getBonusList() )
		{
			if (bonus.getBonusInfo().equalsIgnoreCase(aString))
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
		for (Iterator<BonusObj> ab = getBonusList().iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = ab.next();
			aBonus.setApplied(false);

			if (aBonus.qualifies(aPC)
				&& aBonus.getPCLevel() <= aPC.getTotalLevels())
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
	 * Deactivate all of the bonuses
	 */
	public void deactivateBonuses()
	{
		for ( BonusObj bonus : getBonusList() )
		{
			bonus.setApplied(false);
		}
	}

	/**
	 * Set's all the BonusObj's to this creator
	 */
	public void ownBonuses()
	{
		for ( BonusObj bonus : getBonusList() )
		{
			bonus.setCreatorObject(this);
		}
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
	 * Remove the bonus object from the bonus list
	 * @param aBonus
	 */
	public void removeBonusList(final BonusObj aBonus)
	{
		getBonusList().remove(aBonus);
	}

	/**
	 * Remove all bonuses gained via a level
	 * @param aLevel
	 */
	public void removeAllBonuses(final int aLevel)
	{
		if (bonusList != null)
		{
			for (int x = bonusList.size() - 1; x >= 0; --x)
			{
				if (bonusList.get(x).getPCLevel() == aLevel)
				{
					bonusList.remove(x);
				}
			}
		}
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
			final String aVal = getBonusMap().get(bonusType);

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

			final String aVal = getBonusMap().get(bonusType);

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

	/**
	 * Apply the bonus to a character. The bonus can optionally 
	 * only be added once no matter how many associated choices this 
	 * object has. This is normally used where a bonus is added for 
	 * each associated choice.
	 * 
	 * @param bonusString The unparsed bonus to be added.
	 * @param chooseString The choice to be added.
	 * @param aPC The character to apply thr bonus to.
	 * @param addOnceOnly Should the bonus only be added once irrespective of number of choices 
	 */
	public final void applyBonus(String bonusString, final String chooseString,
		final PlayerCharacter aPC, final boolean addOnceOnly)
	{
		bonusString = makeBonusString(bonusString, chooseString, aPC);
		addBonusList(bonusString, addOnceOnly);
		addSave("BONUS|" + bonusString);
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
			for (BonusObj listBonus : getBonusList())
			{
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

		final String possibleBonusTypeString = aBonusObj.getTypeString();

		// must meet criteria before adding any bonuses
		if (obj instanceof PlayerCharacter)
		{
			if ( !aBonusObj.qualifies((PlayerCharacter)obj) )
			{
				return 0;
			}
		}
		else
		{
			if ( !aBonusObj.passesPreReqToGain((Equipment)obj, aPC) )
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

	public void clearAdds() {
		levelAbilityList.clear();
	}

//	public List<BonusObj> getActiveBonuses(final PlayerCharacter aPC, final String aBonusType, final String aBonusName)
//	{
//		if (!PrereqHandler.passesAll(this.getPreReqList(), aPC, this))
//		{
//			return Collections.emptyList();
//		}
//
//		for ( final BonusObj bonus : getBonusList() )
//		{
//			if ( bonus.getTypeOfBonus().equalsIgnoreCase(aBonusType) && bonus.getBonusName().equalsIgnoreCase(aBonusName) )
//			{
//				
//			}
//		}
//	}

//	/**
//	 * Add a Spell-Like Ability granted by this object.
//	 * 
//	 * @param anAbility The SLA to grant.
//	 */
//	public void addSpellLikeAbility( final SpellLikeAbility anAbility )
//	{
//		if ( spellLikeAbilities == null )
//		{
//			spellLikeAbilities = new ArrayList<SpellLikeAbility>();
//		}
//		spellLikeAbilities.add( anAbility );
//	}
//	
//	/**
//	 * Adds a list of Spell-Like Abilities to this object granted at a specific
//	 * level.
//	 * 
//	 * @param aLevel The level at which the SLAs will be granted.  For PCClass
//	 * this will be the level in the specified class for all other objects it
//	 * is total character level.
//	 * @param aList List of Spell-Like Abilities to add.
//	 */
//	public void addSpellLikeAbilities( final int aLevel, final List<SpellLikeAbility> aList )
//	{
//		Prerequisite minLevel = null;
//		if (aLevel > -9)
//		{
//			try
//			{
//				PreParserFactory factory = PreParserFactory.getInstance();
//				String preLevelString = "PRELEVEL:" + aLevel;
//				// TODO - Refactor this into an overridable method
//				// getLevelPrereq()
//				// TODO - Change this to not use the parser to build it.
//				if (this instanceof PCClass)
//				{
//					// Classes handle this differently
//					preLevelString = "PRECLASS:1," + this.getKeyName() + "=" + aLevel;
//				}
//				minLevel = factory.parse(preLevelString);
//			}
//			catch (PersistenceLayerException notUsed)
//			{
//				// This should never happen
//				assert false;
//			}
//		}
//		for ( SpellLikeAbility sla : aList )
//		{
//			if ( minLevel != null )
//			{
//				sla.addPreReq( minLevel );
//			}
//			if ( spellLikeAbilities == null )
//			{
//				spellLikeAbilities = new ArrayList<SpellLikeAbility>();
//			}
//			spellLikeAbilities.add( sla );
//		}
//	}
//	
//	/**
//	 * Gets an unmodifiable list of Spell-Like Abilities provided by this
//	 * object.
//	 * 
//	 * @return An unmodifiable Collection of SLAs.
//	 */
//	public Collection<SpellLikeAbility> getSpellLikeAbilities()
//	{
//		if ( spellLikeAbilities == null )
//		{
//			return Collections.emptyList();
//		}
//		return Collections.unmodifiableList(spellLikeAbilities);
//	}
}
