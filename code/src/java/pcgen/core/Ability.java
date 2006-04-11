/*
 * Ability.java Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pcgen.core.levelability.LevelAbility;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.IntegerKey;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.core.utils.StringKey;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * Definition and games rules for an Ability.
 *
 * @author   ???
 * @version  $Revision$
 */
public final class Ability extends PObject implements HasCost, Categorisable
{
	/** Visibility is Hidden */
	public static final int VISIBILITY_HIDDEN = 0;
	/** Visibility is Default */
	public static final int VISIBILITY_DEFAULT = 1;
	/** Visibility is Output Sheets Only */
	public static final int VISIBILITY_OUTPUT_ONLY = 2;
	/** Visibility is GUI Only */
	public static final int VISIBILITY_DISPLAY_ONLY = 3;

	/** Ability is Normal */
	public static final int ABILITY_NORMAL = 0;
	/** Ability is Automatic */
	public static final int ABILITY_AUTOMATIC = 1;
	/** Ability is Virtual */
	public static final int ABILITY_VIRTUAL = 2;

	private boolean multiples = false;
	private boolean needsSaving = false;
	private boolean stacks = false;

	// /////////////////////////////////////
	// Fields - Associations

	/* no associations */

	// /////////////////////////////////////
	// Constructor
	/* default constructor only */

	// /////////////////////////////////////
	// Methods - Accessors
	/**
	 * Set the increase in spell level that this metamagic feat costs to apply
	 *
	 * @param  addSpellLevel  the increase to apply
	 */
	public void setAddSpellLevel(final int addSpellLevel)
	{
		integerChar.setCharacteristic(IntegerKey.ADD_SPELL_LEVEL, addSpellLevel);
	}

	/**
	 * for metamagic feats increase in spelllevel
	 *
	 * @return  The number of levels that this Ability increases the level of a
	 *          spell it is applied to
	 */
	public int getAddSpellLevel()
	{
		Integer characteristic = integerChar.getCharacteristic(IntegerKey.ADD_SPELL_LEVEL);
		return characteristic == null ? 0 : characteristic.intValue();
	}

	/**
	 * Set the attribute that controls what this ability adds, e.g. WEAPONPROF,
	 * TEMPLATE, etc.
	 *
	 * @param  add
	 */
	public void setAddString(final String add)
	{
		stringChar.setCharacteristic(StringKey.ADD, add);
	}

	/**
	 * Return the unparsed string that controls what this ability adds, e.g.
	 * WEAPONPROF, TEMPLATE, etc.
	 *
	 * @return  return the unparsed string that controls what this ability adds,
	 *          e.g. WEAPONPROF, TEMPLATE, etc.
	 */
	public String getAddString()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.ADD);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Set the short text snippet that briefly describes what this ability does.
	 *
	 * @param  benefit  the abbreviated description
	 */
	public void setBenefit(final String benefit)
	{
		stringChar.setCharacteristic(StringKey.BENEFIT, benefit);
	}

	/**
	 * Get the short text snippet that briefly describes what this ability does.
	 *
	 * @return  a short String describing this ability
	 */
	public String getBenefit()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.BENEFIT);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get a description of what this ability does
	 *
	 * @return  the benefit if it is set and they are turned on, otherwise
	 *          return the description
	 */

	public String getBenefitDescription()
	{
		if (SettingsHandler.useFeatBenefits() && getBenefit().length() > 1)
		{
			return getBenefit();
		}

		return getDescription();
	}

	/**
	 * Set the category of this Ability
	 *
	 * @param  category  the category of the ability
	 */
	public void setCategory(final String category)
	{
		stringChar.setCharacteristic(StringKey.CATEGORY, category);
	}

	/**
	 * Get the category of this ability
	 *
	 * @return  The category of this Ability
	 */
	public String getCategory()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.CATEGORY);
		return characteristic == null ? Constants.FEAT_CATEGORY : characteristic;
	}

	/**
	 * Set how many "points" this ability costs
	 *
	 * @param  cost  the cost of the ability
	 */
	public void setCost(final String cost)
	{
		stringChar.setCharacteristic(StringKey.COST, cost);
	}

	/**
	 * Get the cost of this ability
	 *
	 * @return  a double representing the cost of the ability
	 */
	public double getCost()
	{
		return Double.parseDouble(getCostString());
	}

	/**
	 * Get the cost of this ability
	 *
	 * @return  a String representing the cost of the ability
	 */
	public String getCostString()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.COST);
		return characteristic == null ? "1" : characteristic;
	}

	/**
	 * This version of getCost treats the thing stored in cost as the name of a
	 * variable which it looks up in the PlayerCharacter object passed in.
	 *
	 * @param   pc  a PlayerCharacter object to look up the cost in
	 *
	 * @return  the cost of the ability
	 */
	public double getCost(final PlayerCharacter pc)
	{
		return pc.getVariableValue(getCostString(), "").doubleValue();
	}

	/**
	 * Set the AbilityType property of this Ability
	 *
	 * @param  type  The type of this ability (normal, automatic, virtual (see
	 *               named constants))
	 */
	public void setFeatType(final int type)
	{
		// Sanity check
		switch (type)
		{
			case ABILITY_NORMAL:
			case ABILITY_AUTOMATIC:
			case ABILITY_VIRTUAL:
				break;

			default:
				return;
		}

		integerChar.setCharacteristic(IntegerKey.ABILITY_TYPE, type);
	}

	/**
	 * Really badly named method.
	 *
	 * @return  an integer representing the abilityType
	 */
	public int getFeatType()
	{
		Integer characteristic = integerChar.getCharacteristic(IntegerKey.ABILITY_TYPE);
		return characteristic == null ? ABILITY_NORMAL : characteristic.intValue();
	}

	/**
	 * Returns true if the feat matches the given type (the type is contained in
	 * the type string of the feat).
	 *
	 * @param   type  the type to test against
	 *
	 * @return  true if the Ability is of type abilityType
	 */
	boolean matchesType(final String type)
	{
		return isType(type);
	}

	/**
	 * Set whether or not a character may have multiple instances of this
	 * ability
	 *
	 * @param  aString  If this begins with Y the property will be set true
	 */
	public void setMultiples(final String aString)
	{
		multiples = (aString.length() > 0) && (aString.toUpperCase().charAt(0) == 'Y');
	}

	/**
	 * Whether or not we can have multiples of this ability
	 *
	 * @return  whether there can be multiples.
	 */
	public boolean isMultiples()
	{
		return multiples;
	}

	/**
	 * If this is a "virtual Ability", this property controls whether it will be
	 * saved with the character
	 *
	 * @param  save  whether to save the feat
	 */
	public void setNeedsSaving(final boolean save)
	{
		needsSaving = save;
	}

	/**
	 * If this is a "virtual Ability", this property controls whether it will be
	 * saved with the character
	 *
	 * @return  whether to save the feat
	 */
	public boolean needsSaving()
	{
		return needsSaving;
	}

	/**
	 * Whether this ability may be taken multiple times for enhanced effect
	 *
	 * @param  aString  To allow stacking, pass a string beginning with Y
	 */
	public void setStacks(final String aString)
	{
		stacks = (aString.length() > 0) && (aString.charAt(0) == 'Y');
	}

	/**
	 * Does this ability stack for enhanced effect?
	 *
	 * @return  Whether this ability stacks for enhanced effect.
	 */
	public boolean isStacks()
	{
		return stacks;
	}

	/**
	 * Set whether or not this Ability will be visible on character sheets and
	 * in the GUI
	 *
	 * @param  visible  the desired visibility, possible values are
	 *                  Ability.VISIBILITY_HIDDEN, Ability.VISIBILITY_DEFAULT,
	 *                  Ability.VISIBILITY_OUTPUT_ONLY,
	 *                  Ability.VISIBILITY_DISPLAY_ONLY
	 */
	public void setVisible(final int visible)
	{
		integerChar.setCharacteristic(IntegerKey.VISIBLE, visible);
	}

	/**
	 * How visible is this Ability in the GUI, output, etc
	 *
	 * @return  the visible property of this Ability
	 * @see     Ability#setVisible
	 */
	public int getVisible()
	{
		Integer characteristic = integerChar.getCharacteristic(IntegerKey.VISIBLE);
		return characteristic == null ? VISIBILITY_DEFAULT : characteristic.intValue();
	}

	/**
	 * Whether we can add newAssociation to the associated list of this
	 * Ability
	 *
	 * @param newAssociation
	 * @return true if we can add the association
	 */
	public boolean canAddAssociation(String newAssociation)
	{
		return 	this.isStacks() || (this.isMultiples() && !this.containsAssociated(newAssociation));
	}
	
	/**
	 * Bog standard clone method
	 *
	 * @return  a copy of this Ability
	 */
	public Object clone()
	{
		Ability anAbility = null;

		try
		{
			anAbility = (Ability) super.clone();
			anAbility.multiples = multiples;
			anAbility.stacks = stacks;
		}
		catch (CloneNotSupportedException e)
		{
			ShowMessageDelegate.showMessageDialog(
				e.getMessage(),
				Constants.s_APPNAME,
				MessageType.ERROR);
		}

		return anAbility;
	}

	/**
	 * Make a string that can be saved that will represent this Ability object
	 *
	 * @return  a string representation that can be parsed to rebuild the
	 *          Ability
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getName());
		txt.append("\tCATEGORY:").append(getCategory());
		txt.append("\tCOST:").append(String.valueOf(getCost()));

		if (isMultiples())
		{
			txt.append("\tMULT:Y");
		}

		if (isStacks())
		{
			txt.append("\tSTACK:Y");
		}

		if (getAddSpellLevel() != 0)
		{
			txt.append("\tADDSPELLLEVEL:").append(getAddSpellLevel());
		}

		if (getAddString().length() != 0)
		{
			txt.append("\tADD:").append(getAddString());
		}

		txt.append("\tVISIBLE:");

		switch (getVisible())
		{
			case VISIBILITY_HIDDEN:
				txt.append("EXPORT");
				break;

			case VISIBILITY_OUTPUT_ONLY:
				txt.append("EXPORT");
				break;

			case VISIBILITY_DISPLAY_ONLY:
				txt.append("DISPLAY");
				break;

			case VISIBILITY_DEFAULT:
			default:
				txt.append("YES");
				break;
		}

		if (getChoiceToModify().length() != 0)
		{
			txt.append("\tMODIFYABILITYCHOICE:" + getChoiceToModify());
		}

		txt.append(super.getPCCText(false));

		return txt.toString();
	}

	// /////////////////////////////////////////////
	// move to CharacterFeat
	/**
	 * This method generates a name for this Ability which includes any choices
	 * made and a count of how many times it has been applied.
	 *
	 * @return  The name of the full Ability, plus any sub-choices made for this
	 *          character. Starts with the name of the ability, and then (for
	 *          types other than weapon proficiencies), either appends a count
	 *          of the times the ability is applied e.g. " (3x)", or a list of
	 *          the sub-choices e.g. " (Sub1, Sub2, ...)".
	 */
	public String qualifiedName()
	{
		// start with the name of the ability
		// don't do for Weapon Profs
		final StringBuffer aStrBuf = new StringBuffer(getOutputName());

		if ((getAssociatedCount() > 0)
				&& !name.startsWith("Armor Proficiency")
				)
		{
			if ((getChoiceString().length() == 0) || (multiples && stacks))
			{
				if (getAssociatedCount() > 1)
				{
					// number of items only (ie stacking), e.g. " (1x)"
					aStrBuf.append(" (");
					aStrBuf.append((int) (getAssociatedCount() * getCost()));
					aStrBuf.append("x)");
				}
			}
			else
			{
				int i = 0;

				// has a sub-detail
				aStrBuf.append(" (");

				// list of items in associatedList, e.g. " (Sub1, Sub2, ...)"
				for (int e = 0; e < getAssociatedCount(true); ++e)
				{
					if (i > 0)
					{
						aStrBuf.append(", ");
					}

					aStrBuf.append(getAssociated(e, true));
					++i;
				}

				aStrBuf.append(')');
			}
		}

		return aStrBuf.toString();
	}

	// Overridden from PObject
	protected List addSpecialAbilitiesToList(final List aList, final PlayerCharacter aPC)
	{
		final List specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if (specialAbilityList != null)
		{
			final StringBuffer sb = new StringBuffer();

			for (Iterator it = specialAbilityList.iterator(); it.hasNext();)
			{
				SpecialAbility sa = (SpecialAbility) it.next();
				final String aName = sa.getName();
				final int idx = aName.indexOf("%CHOICE");

				if (idx >= 0)
				{
					sb.setLength(0);
					sb.append(aName.substring(0, idx));

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

					sb.append(aName.substring(idx + 7));
					sa = new SpecialAbility(
							sb.toString(),
							sa.getSASource(),
							sa.getSADesc());
				}

				aList.add(sa);
			}
		}

		return aList;
	}

	boolean canBeSelectedBy(final PlayerCharacter pc)
	{
		return PrereqHandler.passesAll(getPreReqList(), pc, this);
	}

	/**
	 * TODO Documents this 
	 *
	 * @param addIt
	 * @param pc
	 * 
	 * @deprecated no longer used ADD is processed by PObject there is no
	 * (reachable) code in the system to set addString
	 */
	void modAdds(final boolean addIt, final PlayerCharacter pc)
	{
//		String addString = getAddString();
//		if (addString.length() == 0)
//		{
//			return;
//		}
//
//		final PlayerCharacter aPC = pc;
//
//		if (aPC == null)
//		{
//			return;
//		}
//
//		final StringTokenizer aTok = new StringTokenizer(addString, "|", false);
//
//		if (aTok.countTokens() < 2)
//		{
//			Logging.errorPrint("Badly formed ADD. " + addString);
//			return;
//		}
//
//		final String addType = aTok.nextToken();
//		final String addSec  = aTok.nextToken();
//
//		if ("WEAPONPROFS".equals(addType))
//		{
//			aPC.setAutomaticFeatsStable(false);
//		}
//		else if ("FAVOREDCLASS".equals(addType))
//		{
//			if ("LIST".equals(addSec))
//			{
//				for (int e = 0; e < getAssociatedCount(); ++e)
//				{
//					if (addIt)
//					{
//						aPC.addFavoredClass(getAssociated(e));
//					}
//					else
//					{
//						aPC.removeFavoredClass(getAssociated(e));
//					}
//				}
//			}
//			else
//			{
//				if (addIt)
//				{
//					aPC.addFavoredClass(addSec);
//
//					while (aTok.countTokens() > 0)
//					{
//						aPC.addFavoredClass(aTok.nextToken());
//					}
//				}
//				else
//				{
//					aPC.removeFavoredClass(addSec);
//
//					while (aTok.countTokens() > 0)
//					{
//						aPC.removeFavoredClass(aTok.nextToken());
//					}
//				}
//			}
//		}
//
//		// This code needs to be made to add an assortment of special abilities
//		else if ("SPECIAL".equals(addType))
//		// Takes a \ delimited list of special abilities and lets you choose one to add.
//		// BUG: currently adds 2 items from the list. --- arcady 10/12/2001
//		{
//			final List saList = aPC.getSpecialAbilityList();
//
//			if ("LIST".equals(addSec))
//			{
//				for (int e = 0; e < getAssociatedCount(); ++e)
//				{
//					if (addIt)
//					{
//						final SpecialAbility sa = new SpecialAbility(getAssociated(e));
//						saList.add(sa);
//
//						// aPC.getSpecialAbilityList().add(getAssociated(e));
//					}
//					else
//					{
//						final SpecialAbility sa = new SpecialAbility(getAssociated(e));
//						saList.remove(sa);
//
//						// aPC.getSpecialAbilityList().remove(getAssociated(e));
//					}
//				}
//			}
//			else
//			{
//				if (addIt)
//				{
//					final SpecialAbility sa = new SpecialAbility(addSec);
//					saList.add(sa);
//
//					while (aTok.countTokens() > 0)
//					{
//						final SpecialAbility sa2 = new SpecialAbility(aTok.nextToken());
//						saList.add(sa2);
//					}
//				}
//				else
//				{
//					final SpecialAbility sa = new SpecialAbility(addSec);
//					saList.remove(sa);
//
//					while (aTok.countTokens() > 0)
//					{
//						final SpecialAbility sa2 = new SpecialAbility(aTok.nextToken());
//						saList.remove(sa2);
//					}
//				}
//			}
//		}
//		else if ("TEMPLATE".equals(addType))
//		{
//			if (addIt)
//			{
//				aPC.addTemplateNamed(addSec);
//			}
//			else
//			{
//				final PCTemplate aTemplate = Globals.getTemplateNamed(addSec);
//				aPC.removeTemplate(aTemplate);
//			}
//
//			while (aTok.countTokens() > 0)
//			{
//				final String templateName = aTok.nextToken();
//
//				if (addIt)
//				{
//					aPC.addTemplateNamed(templateName);
//				}
//				else
//				{
//					final PCTemplate aTemplate = Globals.getTemplateNamed(templateName);
//					aPC.removeTemplate(aTemplate);
//				}
//			}
//		}
//		else
//		{
//			Logging.debugPrint("WARNING: ADD:"
//					+ addType + "|" + addSec
//					+ " not handled for ability " + name);
//		}
	}

	/**
	 * Deal with CHOOSE tags.   The actual items the choice will be made from are
	 * based on this.choiceString, as applied to current character. Choices already
	 * made (getAssociatedList) are indicated in the selectedList.  This method
	 * always processes the choices.
	 *
	 * @param   aPC    The Player Character that we're opening the chooser for.
	 * @param   addIt  Whether to add or remove a choice from this Ability.
	 *
	 * @return  true if the Ability was modified, false otherwise
	 */
	public boolean modChoices(final PlayerCharacter aPC, final boolean addIt)
	{
		final List availableList = new ArrayList(); // available list of choices
		final List selectedList  = new ArrayList(); // selected list of choices

		return modChoices(
				availableList,
				selectedList,
				true,
				aPC,
				addIt);
	}

	/**
	 * Deal with CHOOSE tags. The actual items the choice will be made from are
	 * based on this.choiceString, as applied to current character. Choices already
	 * made (getAssociatedList) are indicated in the selectedList.  This method
	 * may also be used to build a list of choices available and choices
	 * already made by passing false in the process parameter 
	 *
	 * @param availableList
	 * @param selectedList
	 * @param process
	 * @param aPC
	 * @param addIt
	 * 
	 * @return true if we processed the list of choices, false if we used the routine to
	 * build the list of choices without processing them. 
	 */
	public boolean modChoices(
		final List            availableList,
		final List            selectedList,
		final boolean         process,
		final PlayerCharacter aPC,
		final boolean         addIt)
	{
		return PObjectUtilities.modChoices(
				this,
				availableList,
				selectedList,
				process,
				aPC,
				addIt);
	}

	/**
	 * Enhanced containsAssociated, which parses the input parameter for "=",
	 * "+num" and "-num" to extract the value to look for.
	 *
	 * @param   aType  The type we're looking for
	 *
	 * @return  enhanced containsAssociated, which parses the input parameter
	 *          for "=", "+num" and "-num" to extract the value to look for.
	 */
	int numberInList(String aType)
	{
		int iCount = 0;
		final String numString = "0123456789";

		if (aType.lastIndexOf('=') > -1)
		{
			aType = aType.substring(aType.lastIndexOf('=') + 1);
		}

		// truncate at + sign if following character is a number
		if (aType.lastIndexOf('+') > -1)
		{
			final String aString = aType.substring(aType.lastIndexOf('+') + 1);

			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
			{
				aType = aType.substring(0, aType.lastIndexOf('+'));
			}
		}

		// truncate at - sign if following character is a number
		if (aType.lastIndexOf('-') > -1)
		{
			final String aString = aType.substring(aType.lastIndexOf('-') + 1);

			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
			{
				aType = aType.substring(0, aType.lastIndexOf('-'));
			}
		}

		for (int i = 0; i < getAssociatedCount(); ++i)
		{
			if (getAssociated(i).equalsIgnoreCase(aType))
			{
				iCount += 1;
			}
		}

		return iCount;
	}

	/**
	 * Adds some info that controls what this ability adds, e.g. WEAPONPROF,
	 * TEMPLATE, etc.
	 *
	 * @param   aLevel   an int (think it represents the character level that
	 *                   this ability is granted at)
	 * @param   aString  the information about things (templates, weapon profs,
	 *                   etc.) this ability adds.
	 *
	 * @return  The new LevelAbility object if one was created.
	 */
	public LevelAbility addAddList(final int aLevel, final String aString)
	{
		if (aString.startsWith("TEMPLATE|"))
		{
			setAddString(aString);
			return null;
		}
		return super.addAddList(aLevel, aString);
	}

	boolean isTypeHidden(final int idx)
	{
		return Globals.isAbilityTypeHidden(getMyType(idx));
	}

	/**
	 * Simple setter method for a String representing a choice that must be
	 * made when applying this ability
	 *
	 * @param  choiceToModify sets the choice
	 */
	public void setChoiceToModify(final String choiceToModify)
	{
		stringChar.setCharacteristic(StringKey.CHOICE_TO_MODIFY, choiceToModify);
	}

	/**
	 * simple getter method for a string that represents a choice that must
	 * be made when applying this Ability.
	 *
	 * @return  The choice to be made.
	 */
	public String getChoiceToModify()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.CHOICE_TO_MODIFY);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Modify the Ability as per the info from this.getChoiceToModify() and the
	 * choices made by the user in the GUI.
	 *
	 * @param   aPC  The Player Character object this Ability belongs to.
	 *
	 * @return  whether we modified the Ability
	 */
	public boolean modifyChoice(PlayerCharacter aPC)
	{
		String abilityName = getChoiceToModify();

		if (abilityName.length() == 0)
		{
			return false;
		}

		ArrayList abilityList  = new ArrayList();
		ArrayList selectedList = new ArrayList();

		Ability anAbility;

		if (abilityName.startsWith("TYPE=") || abilityName.startsWith("TYPE="))
		{
			final String anAbilityType = abilityName.substring(5);

			//
			// Get a list of all ability possessed by the character that
			// are the specified type
			//
			for (Iterator it = aPC.aggregateFeatList().iterator(); it.hasNext();)
			{
				anAbility = (Ability) it.next();

				if (anAbility.isType(anAbilityType))
				{
					abilityList.add(anAbility.getName());
				}
			}

			//
			// Get the user to select one if there is more than 1.
			//
			switch (abilityList.size())
			{
				case 0:
					Logging.debugPrint("PC does not have an ability of type: "
							+ anAbilityType);
					return false; // no ability to modify

				case 1:
					abilityName = (String) abilityList.get(0);
					break;

				default:

					final ChooserInterface chooser = ChooserFactory.getChooserInstance();
					chooser.setPoolFlag(false); // user is not required to make any

					// changes
					chooser.setPool(1);

					chooser.setTitle("Select a "
							+ SettingsHandler.getGame().getSingularTabName(Constants.TAB_ABILITIES)
							+ " to modify");

					Globals.sortChooserLists(abilityList, selectedList);
					chooser.setAvailableList(abilityList);
					chooser.setSelectedList(selectedList);
					chooser.setVisible(true);

					final int selectedSize = chooser.getSelectedList().size();

					if (selectedSize == 0)
					{
						return false; // no ability chosen, so nothing was modified
					}

					abilityName = (String) chooser.getSelectedList().get(0);

					break;
			}
		}

		anAbility = aPC.getFeatNamed(abilityName);

		if (anAbility == null)
		{
			Logging.debugPrint("PC does not have ability: " + abilityName);

			return false;
		}

		//
		// Ability doesn't allow choices, so we cannot modify
		//
		if (!anAbility.isMultiples())
		{
			Logging.debugPrint("MULT:NO for: " + abilityName);

			return false;
		}

		// build a list of available choices and choices already made.
		anAbility.modChoices(
				abilityList,
				selectedList,
				false,
				aPC,
				true);

		final int currentSelections = selectedList.size();

		//
		// If nothing to choose, or nothing selected, then leave
		//
		if ((abilityList.size() == 0) || (currentSelections == 0))
		{
			return false;
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(true); // user is required to use all available
								   // pool points
		chooser.setPool(0); // need to remove 1 to add another

		chooser.setTitle("Modify selections for " + abilityName);
		Globals.sortChooserLists(abilityList, selectedList);
		chooser.setAvailableList(abilityList);
		chooser.setSelectedList(selectedList);
		chooser.setVisible(true);

		final int selectedSize = chooser.getSelectedList().size();

		if (selectedSize != currentSelections)
		{
			return false; // need to have the same number of selections when finished
		}

		// replace old selection(s) with new and update bonuses
		//
		anAbility.clearAssociated();

		for (int i = 0; i < selectedSize; ++i)
		{
			anAbility.addAssociated((String) chooser.getSelectedList().get(i));
		}

		// aPC.calcActiveBonuses();
		return true;
	}

	public int compareTo(final Object obj)
	{
		if (obj != null)
		{
			try
			{
				Ability ab = (Ability) obj;
				if (this.getCategory().compareToIgnoreCase(ab.getCategory()) != 0)
				{
					return this.getCategory().compareToIgnoreCase(ab.getCategory());
				}
			}
			catch (ClassCastException e)
			{
				// Do nothing.  If the cast to Ability doesn't work, we assume that
				// the category of the Object passed in matches the category of this
				// Ability and compare KeyNames
			}

			// this should throw a ClassCastException for non-PObjects, like the
			// Comparable interface calls for
			return this.name.compareToIgnoreCase(((PObject) obj).name);
		}
		return 1;
	}

	/**
	 * Test whether other is the same base ability as this (ignoring any changes
	 *  made to apply either to a PC)
	 *
	 * @param that
	 * @return true is the abilities are copies of the same base ability
	 */
	public boolean isSameBaseAbility(Ability that) {
		return AbilityUtilities.areSameAbility(this, that);
	}

}
