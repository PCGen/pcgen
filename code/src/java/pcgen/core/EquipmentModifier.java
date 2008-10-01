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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Delta;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * Definition and games rules for an equipment modifier.
 *
 * @author   Greg Bingleman <byngl@hotmail.com>
 * @version  $Revision$
 */
public final class EquipmentModifier extends PObject implements Comparable<Object>
{
	private static final String s_CHARGES           = "CHARGES";
	private static final Formula CHOICE_FORMULA = FormulaFactory.getFormulaFor("%CHOICE");

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
	public List<BonusObj> getActiveBonuses(final Equipment caller, final PlayerCharacter aPC)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : getBonusList(caller) )
		{
			if (PrereqHandler.passesAll(bonus.getPrerequisiteList(), caller,
					aPC))
			{
				bonus.setApplied(true);
				aList.add(bonus);
			}
		}

		return aList;
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
	@Override
	public List<BonusObj> getBonusList(AssociationStore as)
	{
		final List<BonusObj> myBonusList = new ArrayList<BonusObj>(super.getBonusList(as));

		for (int i = myBonusList.size() - 1; i > -1; i--)
		{
			final BonusObj aBonus  = myBonusList.get(i);
			final String   aString = aBonus.toString();

			final int idx = aString.indexOf("%CHOICE");

			if (idx >= 0)
			{
				// Add an entry for each of the associated list entries
				for (String assoc : as.getAssociationList(this))
				{
					final BonusObj newBonus = Bonus.newBonus(
							aString.substring(0, idx) + assoc +
							aString.substring(idx + 7));
					newBonus.setCreatorObject(this);

					if (aBonus.hasPrerequisites())
					{
						newBonus.clearPrerequisiteList();
						for (Prerequisite prereq : aBonus.getPrerequisiteList())
						{
							try
							{
								newBonus.addPrerequisite(prereq.clone());
							}
							catch (CloneNotSupportedException e)
							{
								// TODO Handle this?
							}
						}
					}

					// call expandToken to handle prereqs
					newBonus.expandToken("%CHOICE", assoc);
					myBonusList.add(newBonus);
				}

				myBonusList.remove(aBonus);
			}
		}

		return myBonusList;
	}

	/**
	 * Gets a list of bonuses held in this object that match both name and type
	 * @param   aType  The type to match
	 * @param   aName  The name to match
	 *
	 * @return  a List of bonuses mathing both name and type
	 */
	@Override
	public List<BonusObj> getBonusListOfType(AssociationStore as, final String aType, final String aName)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : getBonusList(as) )
		{
			if (
				(bonus.getTypeOfBonus().indexOf(aType) >= 0) &&
				(bonus.getBonusInfo().indexOf(aName) >= 0))
			{
				aList.add(bonus);
			}
		}

		return aList;
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
		for (String s : getSafeListFor(ListKey.ITEM_TYPES))
		{
			if (aType.equalsIgnoreCase(s))
			{
				return true;
			}
		}
		return false;
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
		for (SpecialProperty sp : getSafeListFor(ListKey.SPECIAL_PROPERTIES))
		{
			String propName = sp.getParsedText(pc, caller);

			// TODO WTF is this loop doing? how many times does it expect "%CHOICE" to
			// appear in the special property?

			for (String assoc : caller.getAssociationList(this))
			{
				propName = propName.replaceFirst("%CHOICE", assoc);
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
	 * @param parent TODO
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
		Equipment parent,
		final PObject spellCastingClass,
		final Spell   theSpell,
		final String  spellVariant,
		final String  spellType,
		final int     spellLevel,
		final int     spellCasterLevel,
		final Object[]  spellMetamagicFeats, final int     charges)
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
				final Ability aFeat = (Ability) spellMetamagicFeats[i];

				if (i != 0)
				{
					spellInfo.append(", ");
				}

				spellInfo.append(aFeat.getKeyName());
			}

			spellInfo.append("] ");
		}

		parent.addAssociation(this, spellInfo.toString());
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
		final AssociationStore          obj)
	{
		return super.bonusTo(aType, aName, obj, getBonusList(obj), aPC);
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
	 * Return a string representation of the EquipmentModifier
	 * TODO: This needs to call getEquipNamePortion until after 5.10, when it can be changed to a programmer useful string as per normal.
	 *
	 * @return  a String representation of the EquipmentModifier
	 */
	@Override
	public String toString()
	{
		return getKeyName();
	}

	public int getSR(Equipment parent, PlayerCharacter aPC)
	{
		SpellResistance sr = get(ObjectKey.SR);
		if (sr == null)
		{
			return 0;
		}

		if (sr.getReduction().equals(CHOICE_FORMULA)&& parent.hasAssociations(this))
		{
			return Delta.parseInt(parent.getFirstAssociation(this));
		}

		return sr.getReduction().resolve(parent, true, aPC, getQualifiedKey()).intValue();
	}

	/**
	 * @param pool
	 * @param parent
	 * @param bAdd being added
	 * @return an integer where apparently (from how it's used) only 0 is significant
	 */
	boolean getChoice(final int pool, final Equipment parent, final boolean bAdd, PlayerCharacter pc)
	{
		String choiceString = getChoiceString();

		if (choiceString.length() == 0)
		{
			return true;
		}

		final boolean forEqBuilder = choiceString.startsWith("EQBUILDER.");

		if (bAdd && forEqBuilder)
		{
			return true;
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false);
		chooser.setVisible(false);
		List<String> selectedList = parent.getAssociationList(this);

		final EquipmentChoice equipChoice = buildEquipmentChoice(
				pool,
				parent,
				bAdd,
				forEqBuilder,
				selectedList.size(),
				pc);

		if (equipChoice.isBAdd())
		{
			chooser.setTotalChoicesAvail(selectedList.size() + equipChoice.getMaxSelect());
		}
		else
		{
			chooser.setTotalChoicesAvail(selectedList.size());
		}

		chooser.setAllowsDups(equipChoice.isAllowDuplicates());
		chooser.setSelectedListTerminator("|");
		chooser.setTitle("Select " + equipChoice.getTitle() + " (" + getDisplayName() + ")");
		Globals.sortChooserLists(equipChoice.getAvailableList(), selectedList);
		chooser.setAvailableList(equipChoice.getAvailableList());
		chooser.setSelectedList(selectedList);
		chooser.setVisible(true);

		selectedList = chooser.getSelectedList();
		setChoice(parent, selectedList, equipChoice);

		return parent.hasAssociations(this);
	}

	void setChoice(Equipment parent, final String choice, final EquipmentChoice equipChoice)
	{
		final List<String> tempList = new ArrayList<String>();
		tempList.add(choice);
		setChoice(parent, tempList, equipChoice);
	}

	void setChoice(Equipment parent, final List<String> selectedList, final EquipmentChoice equipChoice)
	{
		parent.removeAllAssociations(this);

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
					chooser.setTotalChoicesAvail(1);
					chooser.setVisible(true);

					if (chooser.getSelectedList().size() == 0)
					{
						continue;
					}

					aString += ('|' + ((String) chooser.getSelectedList().get(0)));
				}
			}

			if (equipChoice.isAllowDuplicates() || !parent.containsAssociated(this, aString))
			{
				parent.addAssociation(this, aString);
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
		final int       numSelected,
		PlayerCharacter pc)
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
			forEqBuilder,
			pc);

		return equipChoice;
	}

	private String replaceCostCasterLevel(String costFormula, final String listEntry)
	{
		String modChoice = "";

		while (costFormula.indexOf("%CASTERLEVEL") >= 0)
		{
			final int idx = costFormula.indexOf("%CASTERLEVEL");

			if (modChoice.length() == 0)
			{
				final int iCasterLevel = getSpellInfo(listEntry, "CASTERLEVEL");
				modChoice = Integer.toString(iCasterLevel);

				//
				// Tack on the item creation multiplier, if there is one
				//
				final String castClassKey = getSpellInfoString(listEntry, "CASTER");

				if (castClassKey.length() != 0)
				{
					final PCClass castClass = Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, castClassKey);

					if (castClass != null)
					{
						final StringBuffer multiple = new StringBuffer(200);
						String             aString  = castClass.get(StringKey.ITEMCREATE);

						if (aString != null && aString.length() != 0)
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
				modChoice = Integer.toString(getSpellInfo(listEntry, s_CHARGES));
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
				final String spellName = getSpellInfoString(listEntry, "SPELLNAME");
				final Spell  aSpell    = Globals.getSpellKeyed(spellName);

				if (aSpell != null)
				{
					modChoice = aSpell.getSafe(ObjectKey.COST).toString();
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
				final String spellName = getSpellInfoString(listEntry, "SPELLNAME");
				final Spell  aSpell    = Globals.getSpellKeyed(spellName);

				if (aSpell != null)
				{
					modChoice = Integer.toString(aSpell.getSafe(IntegerKey.XP_COST));
				}
			}

			costFormula = costFormula.substring(0, idx) + modChoice +
				costFormula.substring(idx + 12);
		}

		return costFormula;
	}

	protected String getCost(final String listEntry)
	{
		String       costFormula = getSafe(FormulaKey.COST).toString();
		String modChoice = "";
		
		while (costFormula.indexOf("%SPELLLEVEL") >= 0)
		{
			final int idx = costFormula.indexOf("%SPELLLEVEL");
		
			if (modChoice.length() == 0)
			{
				final int iLevel = getSpellInfo(listEntry, "SPELLLEVEL");
		
				if (iLevel == 0)
				{
					modChoice = "0.5";
				}
				else
				{
					modChoice = Integer.toString(iLevel);
				}
			}
		
			costFormula = costFormula.substring(0, idx) + modChoice
					+ costFormula.substring(idx + 11);
		}

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
		Boolean costdouble = get(ObjectKey.COST_DOUBLE);
		if (costdouble == null)
		{
			if (isType("MagicalEnhancement") || isType("BaseMaterial"))
			{
				return false;
			}

			if (isIType("MAGIC"))
			{
				return true;
			}

			for (Prerequisite preReq : getPrerequisiteList())
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
			return false;
		}

		return costdouble;
	}

	void setRemainingCharges(Equipment parent, final int remainingCharges)
	{
		if (parent.hasAssociations(this))
		{
			List<String> assoc = parent.removeAllAssociations(this);
			String listEntry  = assoc.get(0);
			String chargeInfo = getSpellInfoString(listEntry, s_CHARGES);
			 
			if (chargeInfo.length() != 0)
			{
				chargeInfo = s_CHARGES + '[' + chargeInfo + ']';

				final int idx = listEntry.indexOf(chargeInfo);
				listEntry = listEntry.substring(0, idx) +
					listEntry.substring(idx + chargeInfo.length());
				listEntry += (s_CHARGES + '[' + Integer.toString(remainingCharges) + ']');
				assoc.set(0, listEntry);
			}
			for (String s : assoc)
			{
				parent.addAssociation(this, s);
			}
		}
	}

	int getRemainingCharges(Equipment parent)
	{
		if (parent.hasAssociations(this))
		{
			return getSpellInfo(parent.getFirstAssociation(this), s_CHARGES);
		}

		return 0;
	}

	int getUsedCharges(Equipment parent)
	{
		return get(IntegerKey.MAX_CHARGES) - getRemainingCharges(parent);
	}

	public static int getSpellInfo(final String listEntry, final String desiredInfo)
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

	public static String getSpellInfoString(
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

		Set<String> typesToGetBonusesFor = new HashSet<String>();

		for ( BonusObj bonus : getBonusList(parent) )
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
