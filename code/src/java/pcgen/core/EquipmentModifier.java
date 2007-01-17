/*
 * EquipmentModifier.java
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
 * Created on November 19, 2001, 4:28 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.IntegerKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import java.math.BigDecimal;
import java.util.*;

/**
 * Definition and games rules for an equipment modifier.
 *
 * @author   Greg Bingleman <byngl@hotmail.com>
 * @version  $Revision$
 */
public final class EquipmentModifier extends PObject implements Comparable<Object>
{
	/** Value to indicate the modifier should not be visible. */
	protected static final int VISIBLE_NO        = 0;
	/** Value to indicate the modifier should be visible. */
	protected static final int VISIBLE_YES       = 1;
	/** Value to indicate the modifier should not be visible unless it is qualified for. */
	protected static final int VISIBLE_QUALIFIED = 2;

	private static final int NAMINGOPTION_NORMAL  = 0;
	private static final int NAMINGOPTION_NONAME  = 1;
	private static final int NAMINGOPTION_NOLIST  = 2;
	private static final int NAMINGOPTION_NOTHING = 3;
	private static final int NAMINGOPTION_SPELL   = 4;
	private static final int NAMINGOPTION_TEXT    = 5;

	public static final int FORMATCAT_FRONT  = 0;
	public static final int FORMATCAT_MIDDLE  = 1;
	public static final int FORMATCAT_PARENS  = 2;

	private static final String s_CHARGES           = "CHARGES";
	private List<String>                ignores             = new ArrayList<String>();
	private List<String>                itemType            = new ArrayList<String>();
	private List<String>                replaces            = new ArrayList<String>();
	private List<SpecialProperty>                specialPropertyList = new ArrayList<SpecialProperty>();
	private List<String>                vFeatList           = null; // virtual feat list
	private List<String>                armorType           = new ArrayList<String>();
	private String              cost                = "0";
	private String              preCost             = "0";
	private String              proficiency         = "";
	private boolean             assignToAll         = false;
	private int                 costDouble          = -1;
	private int                 equipmentVisible    = VISIBLE_YES;
	private int                 maxCharges          = 0;
	private int                 minCharges          = 0;
	private int                 namingOption        = NAMINGOPTION_NORMAL;
	private String              namingOptionText    = "";
	private int                 plus                = 0;
	private String              fumbleRange         = "";

	/**
	 * returns all BonusObj's that are "active", for example, ones that pass all
	 * prerequisite tests and should be applied.
	 *
	 * @param   caller  The object that will be used to test prerequisites
	 *                  against to determine if a bonus should be applied.
	 * @param   aPC     The PC that the prerequisites will be applied against to
	 *                  determine if a bonus is active
	 *
	 * @return  returns all BonusObj's that are "active"
	 */
	public List<BonusObj> getActiveBonuses(final PObject caller, final PlayerCharacter aPC)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : getBonusList() )
		{
			// TODO - This is either wrong or doesn't need to be in the loop.
			if (willIgnore(getKeyName()))
			{
				continue;
			}

			if (caller instanceof Equipment)
			{
				if ( bonus.passesPreReqToGain((Equipment)caller, aPC) )
				{
					bonus.setApplied(true);
					aList.add(bonus);
				}
			}
			else if ( bonus.qualifies(aPC) )
			{
				bonus.setApplied(true);
				aList.add(bonus);
			}
		}

		return aList;
	}

	/**
	 * Add a type to the collection of Armour Types
	 *
	 * @param  aString  the type to add to the collection of Armour Types
	 */
	public void setArmorType(final String aString)
	{
		armorType.add(aString.toUpperCase().trim());
	}

	/**
	 * Should this enhancement be applied to both ends of a double weapon?
	 *
	 * @return  boolean whether to apply to both ends of a double weapon.
	 */
	public boolean getAssignToAll()
	{
		return assignToAll;
	}

	/**
	 * Set the assign to all property.  When true this object will be applied to
	 * all heads of a multi headed weapon
	 *
	 * @param  aString  a string beginning with Y to set the property true
	 */
	public void setAssignment(final String aString)
	{
		assignToAll = (aString.length() > 0) && (aString.charAt(0) == 'Y');
	}

	/**
	 * This method assumes that there can only be one bonus in any given
	 * Equipment modifier that uses %CHOICE.  It retrieves the list of bonuses
	 * using the super classes getBonusList() and then examines each of them in
	 * turn.  If it finds that one of the bonuses contains %CHOICE, it replaces
	 * it with a one new bonus object for every entry in "associated".
	 *
	 * @return  a complete list of bonus objects with %CHOICE expanded to
	 *          include one entry for each associated choice.
	 */
	public List<BonusObj> getBonusList()
	{
		final List<BonusObj> myBonusList = new ArrayList<BonusObj>(super.getBonusList());

		for (int i = myBonusList.size() - 1; i > -1; i--)
		{
			final BonusObj aBonus  = myBonusList.get(i);
			final String   aString = aBonus.toString();

			final int idx = aString.indexOf("%CHOICE");

			if (idx >= 0)
			{
				// Add an entry for each of the associated list entries
				for (int j = getAssociatedCount() - 1; j >= 0; j--)
				{
					final BonusObj newBonus = Bonus.newBonus(
							aString.substring(0, idx) + getAssociated(j) +
							aString.substring(idx + 7));
					newBonus.setCreatorObject(this);

					if (aBonus.hasPreReqs())
					{
						newBonus.clearPreReq();
						for (Prerequisite prereq : aBonus.getPreReqList())
						{
							try
							{
								newBonus.addPreReq(prereq.clone());
							}
							catch (CloneNotSupportedException e)
							{
								// TODO Handle this?
							}
						}
					}

					// call expandToken to handle prereqs
					newBonus.expandToken("%CHOICE", getAssociated(j));
					myBonusList.add(newBonus);
				}

				myBonusList.remove(aBonus);
			}
		}

		return myBonusList;
	}

	/**
	 * Gets a list of bonuses held in this object that match both name and type
	 *
	 * @param   aType  The type to match
	 * @param   aName  The name to match
	 *
	 * @return  a List of bonuses mathing both name and type
	 */
	public List<BonusObj> getBonusListOfType(final String aType, final String aName)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : getBonusList() )
		{
			if (
				(bonus.getTypeOfBonus().indexOf(aType) >= 0) &&
				(bonus.getBonusInfo().indexOf(aName) >= 0) &&
				(!willIgnore(getKeyName())))
			{
				aList.add(bonus);
			}
		}

		return aList;
	}

	/**
	 * A test of whether this object has a bonus that stringifys to the same as
	 * the string passed in
	 *
	 * @param   aString  a string representation of the bonus we're looking for
	 *
	 * @return  True if the string matches, false otherwise
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
	 * Takes a string represnting the number of charges.  The string is split on
	 * | and then the first section is converted to an int.  The minimum number
	 * of charges is then set to this int or 0, whichever is greater.  If the
	 * string has a second protion, this is converted to an int and the maximum
	 * number of charges is set to this second int or it is set the same as the
	 * minimum charges, whichever is greater
	 *
	 * @param  charges  a string in the form "min" or "min|max"
	 */
	public void setChargeInfo(final String charges)
	{
		final StringTokenizer aTok = new StringTokenizer(charges, "|", false);

		try
		{
			minCharges = Integer.parseInt(aTok.nextToken());

			if (minCharges < 0)
			{
				minCharges = 0;
			}

			maxCharges = minCharges;

			if (aTok.hasMoreTokens())
			{
				maxCharges = Integer.parseInt(aTok.nextToken());
			}

			if (maxCharges < minCharges)
			{
				maxCharges = minCharges;
			}
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Invalid " + s_CHARGES + " tag value: " + charges);
		}
	}

	/**
	 * set the cost of this object
	 *
	 * @param  aString  representing the cost
	 */
	public void setCost(final String aString)
	{
		cost = aString;
	}

	/**
	 * get the cost of this object
	 *
	 * @return  the cost.
	 */
	public String getCost()
	{
		return cost;
	}

	/**
	 * Set cost double
	 * @param costDoubles
	 */
	public void setCostDouble(final boolean costDoubles)
	{
		costDouble = costDoubles ? 1 : 0;
	}

	/**
	 * Returns the fumbleRange for this item.
	 *
	 * @return  the fumbleRange for this item.
	 */
	public String getFumbleRange()
	{
		return fumbleRange;
	}

	/**
	 * Sets the fumbleRange for this item.
	 *
	 * @param  aString  the fumbleRange for this item.
	 */
	public void setFumbleRange(final String aString)
	{
		fumbleRange = aString;
	}

	/**
	 * Set the list of things to ignore
	 *
	 * @param  aString  a comma separated list of things to ignore
	 */
	public void setIgnores(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(
				aString.toUpperCase().trim(),
				",");
		ignores.clear();

		while (aTok.hasMoreTokens())
		{
			final String aReplace = aTok.nextToken();

			if (!ignores.contains(aReplace))
			{
				ignores.add(aReplace);
			}
		}
	}

	/**
	 * Add one or more types to the object
	 *
	 * @param  aString  a "." separated listof types to add
	 */
	public void setItemType(final String aString)
	{
		final String          typeString = aString.toUpperCase().trim();
		final StringTokenizer aTok       = new StringTokenizer(typeString, ".");
		itemType.clear();

		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();

			if (!itemType.contains(aType))
			{
				itemType.add(aType);
			}
		}
	}

	/**
	 * return a list of types
	 *
	 * @return  a list of this object's types
	 */
	public List<String> getItemType()
	{
		return itemType;
	}

	/**
	 * Does this Equipment Modifier add aType to the equipment it is applied
	 * to? If aType begins with an &#34; (Exclamation Mark) the &#34; will
	 * be removed before checking the type.
	 *
	 * @param aType
	 * @return Whether the item is of this type
	 */
	public boolean isIType(final String aType)
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

		return itemType.contains(myType);
	}


	/**
	 * Get the maximum number of charges added by this object
	 *
	 * @return  the maximum number of charges
	 */
	public int getMaxCharges()
	{
		return maxCharges;
	}

	/**
	 * Get the minimum number of charges added by this object
	 *
	 * @return  the minimum number of charges
	 */
	public int getMinCharges()
	{
		return minCharges;
	}

	/**
	 * Set the format of the string that this object will stringify to.  Valid
	 * options are "NOLIST", "NONAME", "NOTHING" and "SPELL".  If the option
	 * does not match one of these, then the standard naming convention is
	 * used.
	 *
	 *  Standard:  the name with a list of choices in parenthesis
	 *  NOTHING:   a blank string
	 *  NOLIST:    just the name of the object
	 *  NONAME:    just the list of choices
	 *  SPELL:     treats the first entry in associated as a spell, outputs
	 *             the details
	 *
	 * @param  option  a symbolic constant representing the style of
	 *                 stringification.
	 */
	public void setNamingOption(final String option)
	{
		namingOptionText = "";
		if ("NOLIST".equalsIgnoreCase(option))
		{
			namingOption = NAMINGOPTION_NOLIST;
		}
		else if ("NONAME".equalsIgnoreCase(option))
		{
			namingOption = NAMINGOPTION_NONAME;
		}
		else if ("NOTHING".equalsIgnoreCase(option))
		{
			namingOption = NAMINGOPTION_NOTHING;
		}
		else if ("SPELL".equalsIgnoreCase(option))
		{
			namingOption = NAMINGOPTION_SPELL;
		}
		else if (option != null && option.toUpperCase().startsWith("TEXT="))
		{
			namingOption = NAMINGOPTION_TEXT;
			namingOptionText = option.substring(5);
		}
		else
		{
			namingOption = NAMINGOPTION_NORMAL;
		}
	}

	/**
	 * Set plus
	 * @param aString
	 */
	public void setPlus(final String aString)
	{
		try
		{
			plus = Integer.parseInt(aString);
		}
		catch (NumberFormatException nfe)
		{
			// Ignore
		}
	}

	/**
	 * Get Plus
	 * @return plus
	 */
	public int getPlus()
	{
		return plus;
	}

	/**
	 * Set pre cost
	 *
	 * @param aString
	 */
	public void setPreCost(final String aString)
	{
		preCost = aString;
	}

	/**
	 * Get pre cost
	 * @return pre cost
	 */
	public String getPreCost()
	{
		return preCost;
	}

	/**
	 * Set proficiency
	 * @param prof
	 */
	public void setProficiency(final String prof)
	{
		proficiency = prof;
	}

	/**
	 * Set replacement
	 * @param aString
	 */
	public void setReplacement(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(
				aString.toUpperCase().trim(),
				",");
		replaces.clear();

		while (aTok.hasMoreTokens())
		{
			final String aReplace = aTok.nextToken();

			if (!replaces.contains(aReplace))
			{
				replaces.add(aReplace);
			}
		}
	}

	/**
	 * Add special property
	 * @param sprop
	 */
	public void addSpecialProperty(final SpecialProperty sprop)
	{
		specialPropertyList.add(sprop);
	}

	/**
	 * Get raw special properties
	 * @return raw special properties
	 */
	public List<String> getRawSpecialProperties()
	{
		final List<String> retList = new ArrayList<String>();

		for ( SpecialProperty sprop : specialPropertyList )
		{
			retList.add(sprop.getText());
		}

		return retList;
	}

	/**
	 * A list of Special properties tailored to the PC and the piece of
	 * equipment passed as arguments.
	 *
	 * @param   caller  The Equipment this modifier is applied to.
	 * @param   pc      The Pc that the Special Property will be tailored for
	 *
	 * @return  a list of strings representing Special properties to be
	 * applied to the Equipment
	 */
	public List<String> getSpecialProperties(final Equipment caller, final PlayerCharacter pc)
	{
		final List<String> retList = new ArrayList<String>();

		for (int i = 0; i < specialPropertyList.size(); i++)
		{
			String propName = specialPropertyList.get(i).getParsedText(pc, caller);

			// TODO WTF is this loop doing? how many times does it expect "%CHOICE" to
			// appear in the special property?

			for (int j = 0; j < getAssociatedCount(); j++)
			{
				propName = propName.replaceFirst("%CHOICE", getAssociated(j));
			}

			if ((propName != null) && !propName.equals(""))
			{
				retList.add(propName);
			}
		}

		return retList;
	}

	/**
	 * Here be dragons
	 *
	 * Builds up a big mad string representing the spell info and then stores
	 * it in the first entry of associated.
	 *
	 * TODO store this a separate fields or as a spell object or some other
	 * way that doesn't involve turning this into a string and then parsing
	 * the string when we want to do something with the info.
	 *
	 * @param  spellCastingClass    a PCClass Object, the class that this spell will be cast as
	 * @param  theSpell             a Spell Object
	 * @param  spellVariant         a string
	 * @param  spellType            arcane, divine, etc.
	 * @param  spellLevel           an int the level of the spell
	 * @param  spellCasterLevel     Caster level the spell is cast at
	 * @param  spellMetamagicFeats  Any metamagic feats applied
	 * @param  charges              how many times can it be cast
	 */
	public void setSpellInfo(
		final PObject spellCastingClass,
		final Spell   theSpell,
		final String  spellVariant,
		final String  spellType,
		final int     spellLevel,
		final int     spellCasterLevel,
		final Ability  spellMetamagicFeats[],
		final int     charges)
	{
		final StringBuffer spellInfo = new StringBuffer(100);
		spellInfo.append("SPELLNAME[").append(theSpell.getKeyName()).append("] ");
		spellInfo.append("CASTER[").append(spellCastingClass.getKeyName()).append("] ");

		if (spellVariant.length() != 0)
		{
			spellInfo.append("VARIANT[").append(spellVariant).append("] ");
		}

		spellInfo.append("SPELLTYPE[").append(spellType).append("] ");
		spellInfo.append("SPELLLEVEL[").append(spellLevel).append("] ");
		spellInfo.append("CASTERLEVEL[").append(spellCasterLevel).append("] ");

		if (charges > 0)
		{
			spellInfo.append(s_CHARGES).append('[').append(charges).append("] ");
		}

		if ((spellMetamagicFeats != null) && (spellMetamagicFeats.length > 0))
		{
			/* Have considered whether this needs to be expanded to include
			 * Category.  These are actually Feats and the information is
			 * only used by toString()*/
			spellInfo.append("METAFEATS[");

			for (int i = 0; i < spellMetamagicFeats.length; i++)
			{
				final Ability aFeat = spellMetamagicFeats[i];

				if (i != 0)
				{
					spellInfo.append(", ");
				}

				spellInfo.append(aFeat.getKeyName());
			}

			spellInfo.append("] ");
		}

		addAssociated(spellInfo.toString());
	}

	/**
	 * Set visible
	 * @param aString
	 */
	public void setVisible(final String aString)
	{
		if ((aString.length() > 0) && (aString.charAt(0) == 'Y'))
		{
			equipmentVisible = VISIBLE_YES;
		}
		else if ((aString.length() > 0) && (aString.charAt(0) == 'Q'))
		{
			equipmentVisible = VISIBLE_QUALIFIED;
		}
		else
		{
			equipmentVisible = VISIBLE_NO;
		}
	}

	/**
	 * Adds to the virtual feat list this item bestows upon its weilder
	 *
	 * @param  vList  a | delimited list of feats to add to the list
	 */
	public void addVFeatList(final String vList)
	{
		final StringTokenizer aTok = new StringTokenizer(vList, "|", false);

		while (aTok.hasMoreTokens())
		{
			if (vFeatList == null)
			{
				vFeatList = new ArrayList<String>();
			}

			vFeatList.add(aTok.nextToken());
		}
	}

	/**
	 * Add bonus to
	 * @param aPC
	 * @param aType
	 * @param aName
	 * @param obj
	 * @return bonus
	 */
	public double bonusTo(
		final PlayerCharacter aPC,
		final String          aType,
		final String          aName,
		final Object          obj)
	{
		return super.bonusTo(aType, aName, obj, getBonusList(), aPC);
	}

	/**
	 * Should use this instead of the current getBonusList() but have to find
	 * everywhere an EquipmentModifier is added from and call this function. JSC
	 * 08/20/03
	 */
	public void calcBonuses()
	{
		final List<BonusObj> addList = new ArrayList<BonusObj>();
		final List<BonusObj> delList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : getBonusList() )
		{
			final String   aString = bonus.toString();
			final int      idx     = aString.indexOf("%CHOICE");

			if (idx >= 0)
			{
				delList.add(bonus);

				// Add an entry for each of the
				// associated list entries
				for (int j = 0; j < getAssociatedCount(); j++)
				{
					final BonusObj newBonus = Bonus.newBonus(
							aString.substring(0, idx) + getAssociated(j) +
							aString.substring(idx + 7));
					newBonus.setCreatorObject(this);
					addList.add(newBonus);
				}
			}
		}

		if (delList.size() > 0)
		{
			for ( BonusObj bonus : delList )
			{
				removeBonusList(bonus);
			}

			for ( BonusObj bonus : addList )
			{
				addBonusList(bonus);
			}
		}
	}

	/**
	 * Clone an EquipmentModifier
	 *
	 * @return  a clone of the EquipmentModifier
	 */
	@Override
	public EquipmentModifier clone()
	{
		EquipmentModifier aObj = null;

		try
		{
			aObj                     = (EquipmentModifier) super.clone();
			aObj.itemType            = new ArrayList<String>(itemType);
			aObj.specialPropertyList = new ArrayList<SpecialProperty>(specialPropertyList);
			aObj.replaces            = new ArrayList<String>(replaces);
			aObj.ignores             = new ArrayList<String>(ignores);
			aObj.armorType           = new ArrayList<String>(armorType);
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(
				exc.getMessage(),
				Constants.s_APPNAME,
				MessageType.ERROR);
		}

		return aObj;
	}

	/**
	 * Does this object bestows virtual feats
	 *
	 * @return  Whether this object bestows virtual feats
	 */
	public boolean hasVFeats()
	{
		return (vFeatList != null) && (vFeatList.size() > 0);
	}

	/**
	 * Return a string representation of the EquipmentModifier
	 * TODO: This needs to call getEquipNamePortion until after 5.10, when it can be changed to a programmer useful string as per normal.
	 *
	 * @return  a String representation of the EquipmentModifier
	 */
	public String toString()
	{
		return getEquipNamePortion();
	}

	/**
	 * Returns the name that should be added to the equipment item as
	 * a result of the presence of this equipment modifier.
	 *
	 * @return The text to be added to the equipment name.
	 */
	public String getEquipNamePortion()
	{
		if (namingOption == NAMINGOPTION_NOTHING)
		{
			return "";
		}
		if (namingOption == NAMINGOPTION_TEXT)
		{
			return namingOptionText;
		}

		final StringBuffer aString = new StringBuffer(getDisplayName().length());

		if (namingOption == NAMINGOPTION_SPELL)
		{
			for (int i=0;i<getAssociatedCount();i++)
			{
				if (i>0)
				{
					aString.append(", ");
				}
				final String listEntry = getAssociated(i);

				String     spellName = getSpellName(listEntry);

				if (SettingsHandler.guiUsesOutputNameSpells())
				{
					final Spell aSpell = Globals.getSpellKeyed(spellName);

					if (aSpell != null)
					{
						spellName = aSpell.getOutputName();
					}
				}

				aString.append(spellName);

				final String info = getSpellVariant(listEntry);

				if (info.length() != 0)
				{
					aString.append(" (").append(info).append(')');
				}

				final List<String> metaFeats = getSpellMetafeats(listEntry);
				if (!metaFeats.isEmpty())
				{
					aString.append('/').append(CoreUtility.join(metaFeats, "/"));
				}

				aString.append('/').append(getSpellCaster(listEntry));
				aString.append('/').append(
					CoreUtility.ordinal(getSpellCasterLevel(listEntry)));
			}
		}
		else
		{
			if (namingOption != NAMINGOPTION_NONAME)
			{
				aString.append(getDisplayName());
			}

			if ((namingOption != NAMINGOPTION_NOLIST) && (getAssociatedCount() > 0))
			{
				if (namingOption != NAMINGOPTION_NONAME)
				{
					aString.append(" (");
				}

				boolean bFirst = true;

				for (int e = 0; e < getAssociatedCount(); e++)
				{
					if (!bFirst)
					{
						aString.append(", ");
					}

					aString.append(getAssociated(e));
					bFirst = false;
				}

				if (namingOption != NAMINGOPTION_NONAME)
				{
					aString.append(")");
				}
			}
		}

		return aString.toString().trim().replace('|', ' ');
	}

	protected int getSR(final PlayerCharacter aPC)
	{
		if (getSRFormula() == null)
		{
			return 0;
		}

		if ("%CHOICE".equals(getSRFormula()) && (getAssociatedCount() > 0))
		{
			return Delta.parseInt(associatedList.get(0).toString());
		}

		return super.getSR(aPC);
	}

	/**
	 * @param pool
	 * @param parent
	 * @param bAdd being added
	 * @return an integer where apparently (from how it's used) only 0 is significant
	 */
	int getChoice(final int pool, final Equipment parent, final boolean bAdd)
	{
		String choiceString = getChoiceString();

		if (choiceString.length() == 0)
		{
			return 1;
		}

		final boolean forEqBuilder = choiceString.startsWith("EQBUILDER.");

		if (bAdd && forEqBuilder)
		{
			return 1;
		}

		List<String> selectedList = new ArrayList<String>(); // selected list of choices

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false);
		chooser.setVisible(false);
		addAssociatedTo(selectedList);

		final EquipmentChoice equipChoice = buildEquipmentChoice(
				pool,
				parent,
				bAdd,
				forEqBuilder,
				selectedList.size());

		if (!equipChoice.isBAdd())
		{
			chooser.setPool(0);
		}
		else
		{
			chooser.setPool(equipChoice.getPool());
		}

		chooser.setAllowsDups(equipChoice.isAllowDuplicates());
		chooser.setSelectedListTerminator("|");
		chooser.setTitle("Select " + equipChoice.getTitle() + " (" + getDisplayName() + ")");
		Globals.sortChooserLists(equipChoice.getAvailableList(), selectedList);
		chooser.setAvailableList(equipChoice.getAvailableList());
		chooser.setSelectedList(selectedList);
		chooser.setVisible(true);

		selectedList = chooser.getSelectedList();
		setChoice(selectedList, equipChoice);

		return getAssociatedCount();
	}

	void setChoice(final String choice, final EquipmentChoice equipChoice)
	{
		final List<String> tempList = new ArrayList<String>();
		tempList.add(choice);
		setChoice(tempList, equipChoice);
	}

	void setChoice(final List<String> selectedList, final EquipmentChoice equipChoice)
	{
		clearAssociated();

		for (int i = 0; i < selectedList.size(); i++)
		{
			String aString = selectedList.get(i);

			if (equipChoice.getMinValue() < equipChoice.getMaxValue())
			{
				final int idx = aString.indexOf('|');

				if (idx < 0)
				{
					final List<String> secondaryChoice = new ArrayList<String>();

					for (
						int j = equipChoice.getMinValue();
						j <= equipChoice.getMaxValue();
						j += equipChoice.getIncValue())
					{
						if (j != 0)
						{
							secondaryChoice.add(Delta.toString(j));
						}
					}

					final ChooserInterface chooser = ChooserFactory.getChooserInstance();
					chooser.setPoolFlag(false);
					chooser.setVisible(false);
					chooser.setTitle("Select modifier (" + aString + ")");
					chooser.setAvailableList(secondaryChoice);
					chooser.setSelectedList(new ArrayList());
					chooser.setPool(1);
					chooser.setVisible(true);

					if (chooser.getSelectedList().size() == 0)
					{
						continue;
					}

					aString += ('|' + ((String) chooser.getSelectedList().get(0)));
				}
			}

			if (equipChoice.isAllowDuplicates() || !containsAssociated(aString))
			{
				addAssociated(aString);
			}
		}
	}

	/**
	 * Build up the details of a required choice
	 *
	 * @param   pool
	 * @param   parent the equipment this modifer will be applied to
	 * @param   bAdd is a choice being added or removed
	 * @param   forEqBuilder
	 * @param   numSelected
	 *
	 * @return  A populated EquipmentChoice object
	 */
	EquipmentChoice buildEquipmentChoice(
		final int       pool,
		final Equipment parent,
		final boolean   bAdd,
		final boolean   forEqBuilder,
		final int       numSelected)
	{
		final EquipmentChoice equipChoice  = new EquipmentChoice(bAdd, pool);
		String                choiceString = getChoiceString();

		if (choiceString.length() == 0)
		{
			return equipChoice;
		}

		equipChoice.constructFromChoiceString(
			choiceString,
			parent,
			pool,
			numSelected,
			forEqBuilder);

		return equipChoice;
	}

	private String replaceCostSpellLevel(String costFormula, final String listEntry)
	{
		String modChoice = "";

		while (costFormula.indexOf("%SPELLLEVEL") >= 0)
		{
			final int idx = costFormula.indexOf("%SPELLLEVEL");

			if (modChoice.length() == 0)
			{
				final int iLevel = getSpellLevel(listEntry);

				if (iLevel == 0)
				{
					modChoice = "0.5";
				}
				else
				{
					modChoice = Integer.toString(iLevel);
				}
			}

			costFormula = costFormula.substring(0, idx) + modChoice +
				costFormula.substring(idx + 11);
		}

		return costFormula;
	}

	private String replaceCostCasterLevel(String costFormula, final String listEntry)
	{
		String modChoice = "";

		while (costFormula.indexOf("%CASTERLEVEL") >= 0)
		{
			final int idx = costFormula.indexOf("%CASTERLEVEL");

			if (modChoice.length() == 0)
			{
				final int iCasterLevel = getSpellCasterLevel(listEntry);
				modChoice = Integer.toString(iCasterLevel);

				//
				// Tack on the item creation multiplier, if there is one
				//
				final String castClassKey = getSpellCaster(listEntry);

				if (castClassKey.length() != 0)
				{
					final PCClass castClass = Globals.getClassKeyed(castClassKey);

					if (castClass != null)
					{
						final StringBuffer multiple = new StringBuffer(200);
						String             aString  = castClass
							.getItemCreationMultiplier();

						if (aString.length() != 0)
						{
							final StringTokenizer aTok = new StringTokenizer(
									aString,
									"+-*/()",
									true);

							//
							// This is to support older versions of the
							// ITEMCREATE tag
							// that allowed 0.5, because it used to be just a
							// multiple
							//
							if (aTok.countTokens() == 1)
							{
								multiple.append(iCasterLevel).append('*').append(aString);
							}
							else
							{
								while (aTok.hasMoreTokens())
								{
									aString = aTok.nextToken();

									if (aString.equals("CL"))
									{
										multiple.append(iCasterLevel);
									}
									else
									{
										multiple.append(aString);
									}
								}
							}

							modChoice = multiple.toString();
						}
					}
				}
			}

			costFormula = costFormula.substring(0, idx) + "(" + modChoice + ")" +
				costFormula.substring(idx + 12);
		}

		return costFormula;
	}

	private String replaceCostCharges(String costFormula, final String listEntry)
	{
		String modChoice = "";

		while (costFormula.indexOf("%" + s_CHARGES) >= 0)
		{
			final int idx = costFormula.indexOf("%" + s_CHARGES);

			if (modChoice.length() == 0)
			{
				modChoice = Integer.toString(getSpellCharges(listEntry));
			}

			costFormula = costFormula.substring(0, idx) + modChoice +
				costFormula.substring(idx + 8);
		}

		return costFormula;
	}

	private String replaceCostSpellCost(String costFormula, final String listEntry)
	{
		String modChoice = "";

		while (costFormula.indexOf("%SPELLCOST") >= 0)
		{
			final int idx = costFormula.indexOf("%SPELLCOST");

			if (modChoice.length() == 0)
			{
				final String spellName = getSpellName(listEntry);
				final Spell  aSpell    = Globals.getSpellKeyed(spellName);

				if (aSpell != null)
				{
					modChoice = aSpell.getCost().toString();
				}
			}

			costFormula = costFormula.substring(0, idx) + modChoice +
				costFormula.substring(idx + 10);
		}

		return costFormula;
	}

	private String replaceCostChoice(String costFormula, final String listEntry)
	{
		String modChoice = "";

		while (costFormula.indexOf("%CHOICE") >= 0)
		{
			final int idx = costFormula.indexOf("%CHOICE");

			if (modChoice.length() == 0)
			{
				final int offs     = listEntry.lastIndexOf('|');
				int       modValue = 0;

				try
				{
					modValue = Delta.parseInt(listEntry.substring(offs + 1));
				}
				catch (NumberFormatException exc)
				{
					// TODO: Should this really be ignored?
				}

				modChoice = Integer.toString(modValue);
			}

			costFormula = costFormula.substring(0, idx) + modChoice +
				costFormula.substring(idx + 7);
		}

		return costFormula;
	}

	private String replaceCostSpellXPCost(String costFormula, final String listEntry)
	{
		String modChoice = "";

		while (costFormula.indexOf("%SPELLXPCOST") >= 0)
		{
			final int idx = costFormula.indexOf("%SPELLXPCOST");

			if (modChoice.length() == 0)
			{
				final String spellName = getSpellName(listEntry);
				final Spell  aSpell    = Globals.getSpellKeyed(spellName);

				if (aSpell != null)
				{
					modChoice = aSpell.getXPCost() + "";
				}
			}

			costFormula = costFormula.substring(0, idx) + modChoice +
				costFormula.substring(idx + 12);
		}

		return costFormula;
	}

	protected String getCost(final int eqIdx)
	{
		final String listEntry   = getAssociated(eqIdx);
		String       costFormula = cost;

		costFormula = replaceCostSpellLevel(costFormula, listEntry);
		costFormula = replaceCostSpellCost(costFormula, listEntry);
		costFormula = replaceCostSpellXPCost(costFormula, listEntry);
		costFormula = replaceCostCasterLevel(costFormula, listEntry);
		costFormula = replaceCostCharges(costFormula, listEntry);
		costFormula = replaceCostChoice(costFormula, listEntry);

		return costFormula;
	}

	boolean getCostDouble()
	{
		//
		// Uninitialized?
		//
		if (costDouble < 0)
		{
			if (isType("MagicalEnhancement") || isType("BaseMaterial"))
			{
				return false;
			}

			if (itemType.contains("MAGIC"))
			{
				return true;
			}

			for (Prerequisite preReq : getPreReqList())
			{
				if (
					"TYPE".equalsIgnoreCase(preReq.getKind()) &&
					(
						(preReq.getKey().equalsIgnoreCase(
								"EQMODTYPE=MagicalEnhancement")) ||
						(preReq.getKey().equalsIgnoreCase(
								"EQMODTYPE.MagicalEnhancement"))
					))
				{
					return true;
				}
			}
		}

		return costDouble == 1;
	}

	String getProficiency()
	{
		return proficiency;
	}

	void setRemainingCharges(final int remainingCharges)
	{
		if (getAssociatedCount() > 0)
		{
			String listEntry  = getAssociated(0);
			String chargeInfo = getSpellInfoString(listEntry, s_CHARGES);

			if (chargeInfo.length() != 0)
			{
				chargeInfo = s_CHARGES + '[' + chargeInfo + ']';

				final int idx = listEntry.indexOf(chargeInfo);
				listEntry = listEntry.substring(0, idx) +
					listEntry.substring(idx + chargeInfo.length());
				listEntry += (s_CHARGES + '[' + Integer.toString(remainingCharges) + ']');
				setAssociated(0, listEntry);
			}
		}
	}

	int getRemainingCharges()
	{
		if (getAssociatedCount() > 0)
		{
			return getSpellCharges(getAssociated(0));
		}

		return 0;
	}

	int getUsedCharges()
	{
		return maxCharges - getRemainingCharges();
	}

	/**
	 * Returns the list of virtual feats this item bestows upon its weilder
	 *
	 * @return  List of Feat objects
	 */
	List<String> getVFeatList()
	{
		if (vFeatList != null)
		{
			String choiceString = getChoiceString();

			if (choiceString.startsWith("FEAT") || (choiceString.indexOf("|FEAT") >= 0))
			{
				final List<String> vFeats = new ArrayList<String>();

				for (Iterator<String> e = vFeatList.iterator(); e.hasNext();)
				{
					final String aString = e.next();

					if (aString.equals("%CHOICE"))
					{
						for (int i = 0; i < getAssociatedCount(); i++)
						{
							vFeats.add(getAssociated(i));
						}
					}
					else
					{
						vFeats.add(aString);
					}
				}

				return vFeats;
			}
		}

		return vFeatList;
	}

	int getVisible()
	{
		return equipmentVisible;
	}

	String replaceArmorType(final List<String> aTypes)
	{
		for (int z = 0; z < armorType.size(); z++)
		{
			final StringTokenizer aTok = new StringTokenizer(
					armorType.get(z), "|");

			if (aTok.hasMoreTokens())
			{
				final int idx = aTypes.indexOf(aTok.nextToken());

				if (idx >= 0)
				{
					if (aTok.hasMoreTokens())
					{
						final String newArmorType = aTok.nextToken();
						aTypes.set(idx, newArmorType);

						return newArmorType;
					}
					aTypes.remove(idx);
				}
			}
		}

		return null;
	}

	boolean willIgnore(final String aString)
	{
		return ignores.contains(aString.toUpperCase().trim());
	}

	boolean willReplace(final String aString)
	{
		return replaces.contains(aString.toUpperCase().trim());
	}

	private static String getSpellCaster(final String listEntry)
	{
		return getSpellInfoString(listEntry, "CASTER");
	}

	private static int getSpellCasterLevel(final String listEntry)
	{
		return getSpellInfo(listEntry, "CASTERLEVEL");
	}

	private static int getSpellInfo(final String listEntry, final String desiredInfo)
	{
		int          modValue = 0;
		final String info     = getSpellInfoString(listEntry, desiredInfo);

		if (info.length() > 0)
		{
			try
			{
				modValue = Delta.parseInt(info);
			}
			catch (NumberFormatException exc)
			{
				// TODO: Should this really be ignored?
			}
		}

		return modValue;
	}

	private static String getSpellInfoString(
		final String listEntry,
		final String desiredInfo)
	{
		final int offs  = listEntry.indexOf(desiredInfo + "[");
		final int offs2 = listEntry.indexOf(']', offs + 1);

		if ((offs >= 0) && (offs2 > offs))
		{
			return listEntry.substring(offs + desiredInfo.length() + 1, offs2);
		}

		return "";
	}

	private static int getSpellLevel(final String listEntry)
	{
		return getSpellInfo(listEntry, "SPELLLEVEL");
	}

	private int getSpellCharges(final String listEntry)
	{
		return getSpellInfo(listEntry, s_CHARGES);
	}

	/* this is only used by toString, there is no point adding Category
	 * information since it is not needed by the toString function and
	 * these ability objects do actually represent Feats */
	private static List<String> getSpellMetafeats(final String listEntry)
	{
		final String metaFeat = getSpellInfoString(listEntry, "METAFEATS");

		return CoreUtility.split(metaFeat, ',');
	}

	private static String getSpellName(final String listEntry)
	{
		return getSpellInfoString(listEntry, "SPELLNAME");
	}

	private static String getSpellVariant(final String listEntry)
	{
		return getSpellInfoString(listEntry, "VARIANT");
	}

	/**
	 * Add item cost
	 * @param aPC
	 * @param bonusType
	 * @param qty
	 * @param parent
	 * @return added cost
	 */
	public BigDecimal addItemCosts(
		final PlayerCharacter aPC,
		final String          bonusType,
		final int             qty,
		final Equipment       parent)
	{
		double val = 0;

		List<String> typesToGetBonusesFor = new ArrayList<String>();

		for ( BonusObj bonus : getBonusList() )
		{
			boolean        meetsAll = true;

			if (bonus.getBonusName().equals(bonusType))
			{
				StringTokenizer aTok  = new StringTokenizer(
						bonus.toString().substring(bonusType.length()),
						"|",
						false);
				final String    bType = aTok.nextToken();
				aTok = new StringTokenizer(bType.substring(5), ".", false);

				String typeString = "TYPE";

				while (aTok.hasMoreTokens())
				{
					final String sub_type = aTok.nextToken();
					meetsAll = parent.isType(sub_type);

					if (!meetsAll)
					{
						break;
					}

					typeString += "." + sub_type;
				}

				if (meetsAll)
				{
					typesToGetBonusesFor.add(typeString);
				}
			}
		}

		for ( String typeString : typesToGetBonusesFor )
		{
			val += bonusTo(aPC, bonusType, typeString, parent);
		}

		return new BigDecimal(val * qty);
	}

	/**
	 * Set the eqmod's format category.
	 * @param cat
	 */
	public void setFormatCat(int cat)
	{
		integerChar.put(IntegerKey.FORMAT_CAT, cat);
	}
	
	/**
	 * Retrieve the eqmod's format category. Defaults to parens.
	 * @return The format category for this eqmod.
	 */
	public int getFormatCat()
	{
		final Integer characteristic = integerChar.get(IntegerKey.FORMAT_CAT);
		return characteristic == null ? FORMATCAT_PARENS : characteristic.intValue();
	}
	
	/**
	 * lets this object compare to others.
	 *
	 * @param   o  The object to compare to
	 *
	 * @return  -1, 0 or 1 as per Comparator
	 */
	public int compareTo(final Object o)
	{
		if (o instanceof EquipmentModifier)
		{
			return getKeyName().compareTo(((PObject) o).getKeyName());
		}

		return getKeyName().compareTo(o.toString());
	}
}
