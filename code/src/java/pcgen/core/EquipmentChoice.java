/*
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.EquipmentTypeFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.core.analysis.ChooseActivation;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.SignedInteger;
import pcgen.util.enumeration.Visibility;

/**
 * {{@code EquipmentChoice}} holds the details of a choice or
 * choices required for an Equipment. It is a java bean with a
 * couple of helper functions to support the users of the bean.
 * This supports either the user manually choosing which option
 * they want, or the generator creating one object for each
 * combination of choices.
 */

public final class EquipmentChoice
{
	private EquipmentTypeFacet equipmentTypeFacet = FacetLibrary.getFacet(EquipmentTypeFacet.class);

	private boolean allowDuplicates = false;
	private boolean noSign = false;
	private boolean bAdd;
	private boolean skipZero = false;

	private int minValue = 0;
	private int maxValue = 0;
	private int incValue = 1;
	private int maxSelect = 0;
	/*
	 * CONSIDER This is never read, which is probably a bug - thpr Dec 6, 2008
	 */
	private int pool;
	private String title = null;

	private List<Object> availableList = new ArrayList<>();

	/**
	 * Default constructor for the equipment choice class.
	 * @param bAdd
	 * @param pool
	 */
	public EquipmentChoice(final boolean bAdd, final int pool)
	{
		super();
		this.bAdd = bAdd;
		this.pool = pool;
	}

	/**
	 * Create an iterator over the available choices. This will either be
	 * an iterator of strings, should there be only one choice, or an array
	 * of strings where nested choices are required. The iterator will run
	 * through each possible combination in this case.
	 *
	 * @param neverEmpty True if a default record should be
	 *                    added if there are no choices.
	 * @return An iterator of choices
	 */
	EquipChoiceIterator getChoiceIterator(final boolean neverEmpty)
	{
		if (neverEmpty && availableList.isEmpty())
		{
			final List<Object> temp = new ArrayList<>();
			temp.add("");
			return new EquipChoiceIterator(temp);
		}
		final List<Object> finalList;

		// Account for secondary values (sent as <primary>|<secondary>)
		if (getMinValue() < getMaxValue())
		{
			finalList = new ArrayList<>();
            for (Object o : availableList)
            {
                final String choice = String.valueOf(o);
                if (choice.indexOf('|') < 0)
                {
                    for (int j = getMinValue();j <= getMaxValue();j += getIncValue())
                    {
                        if (!skipZero || j != 0)
                        {
                            finalList.add(choice + '|' + Delta.toString(j));
                        }
                    }
                } else
                {
                    finalList.add(choice);
                }
            }
		}
		else
		{
			finalList = availableList;
		}
		return new EquipChoiceIterator(finalList);
	}

	/**
	 * @return Returns the pool.
	 */
	int getPool()
	{
		return pool;
	}

	/**
	 * @param pool The pool to set.
	 */
	void setPool(final int pool)
	{
		this.pool = pool;
	}

	/**
	 * @return Returns the bAdd.
	 */
	public boolean isBAdd()
	{
		return bAdd;
	}

	/**
	 * @param add The bAdd to set.
	 */
	void setBAdd(final boolean add)
	{
		bAdd = add;
	}

	/**
	 * @return Returns the availableList.
	 */
	public List<Object> getAvailableList()
	{
		return availableList;
	}

	/**
	 * @return Returns the allowDuplicates.
	 */
	public boolean isAllowDuplicates()
	{
		return allowDuplicates;
	}

	/**
	 * @param allowDuplicates The allowDuplicates to set.
	 */
	void setAllowDuplicates(final boolean allowDuplicates)
	{
		this.allowDuplicates = allowDuplicates;
	}

	/**
	 * @return Returns the incValue.
	 */
	public int getIncValue()
	{
		return incValue;
	}

	/**
	 * @param incValue The incValue to set.
	 */
	void setIncValue(final int incValue)
	{
		this.incValue = incValue;
	}

	/**
	 * @return Returns the maxSelect.
	 */
	public int getMaxSelect()
	{
		return maxSelect;
	}

	/**
	 * @param maxSelect The maxSelect to set.
	 */
	void setMaxSelect(final int maxSelect)
	{
		this.maxSelect = maxSelect;
	}

	/**
	 * @return Returns the maxValue.
	 */
	public int getMaxValue()
	{
		return maxValue;
	}

	/**
	 * @param maxValue The maxValue to set.
	 */
	void setMaxValue(final int maxValue)
	{
		this.maxValue = maxValue;
	}

	/**
	 * @return Returns the minValue.
	 */
	public int getMinValue()
	{
		return minValue;
	}

	/**
	 * @param minValue The minValue to set.
	 */
	void setMinValue(final int minValue)
	{
		this.minValue = minValue;
	}

	/**
	 * @return Returns the noSign.
	 */
	boolean isNoSign()
	{
		return noSign;
	}

	/**
	 * @param noSign The noSign to set.
	 */
	void setNoSign(final boolean noSign)
	{
		this.noSign = noSign;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	void setTitle(final String title)
	{
		this.title = title;
	}

	/**
	 * Add a list of all skills to the available list of the EquipmentChoice object
	 */
	public void addSkills()
	{
		for (Skill skill : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Skill.class))
		{
			this.getAvailableList().add(skill.getKeyName());
		}
	}

	/**
	 * Set MinValue
	 * @param minString a string with the minimum value
	 */
	public void setMinValueFromString(String minString)
	{
		try
		{
			this.setMinValue(Delta.parseInt(minString.substring(4)));
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Bad MIN= value: " + minString);
		}
	}

	/**
	 * @param maxString a string with the maximum value
	 */
	public void setMaxValueFromString(String maxString)
	{
		try
		{
			this.setMaxValue(Delta.parseInt(maxString.substring(4)));
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Bad MAX= value: " + maxString);
		}
	}

	/**
	 *
	 * @param incString a string with the increment value
	 */
	public void setIncrementValueFromString(String incString)
	{
		try
		{
			this.setIncValue(Delta.parseInt(incString.substring(10)));

			if (this.getIncValue() < 1)
			{
				this.setIncValue(1);
			}
		}
		catch (NumberFormatException e)
		{
			// TODO Deal with Exception
		}
	}

	/**
	 * Add abilities of Category aCategory and Type typeString to the
	 * available list of the Equipment Chooser equipChoice
	 * @param typeString  the type of Ability to add to the chooser
	 * @param aCategory   the Category of Ability to add to the chooser
	 */
	public void addSelectableAbilities(final String typeString, final String aCategory)
	{
		AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
		AbilityCategory cat = ref.silentlyGetConstructedCDOMObject(AbilityCategory.class, aCategory);
		for (Ability anAbility : ref.getManufacturerId(cat).getAllObjects())
		{
			boolean matchesType = (typeString.equalsIgnoreCase("ALL") || anAbility.isType(typeString));

			if ((anAbility.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT)
				&& !this.getAvailableList().contains(anAbility.getKeyName()))
			{
				if (matchesType && !ChooseActivation.hasNewChooseToken(anAbility))
				{
					this.getAvailableList().add(anAbility.getKeyName());
				}
			}
		}
	}

	/**
	 * Add Equipment of Type typeString to the available list of
	 * the Equipment Chooser equipChoice
	 * @param typeString  the type of Equipment to add to the chooser
	 */
	public void addSelectableEquipment(final String typeString)
	{
		for (Equipment aEquip : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Equipment.class))
		{
			if (aEquip.isType(typeString) && !this.getAvailableList().contains(aEquip.getName()))
			{
				this.getAvailableList().add(aEquip.getName());
			}
		}
	}

	/**
	 * Add a list of skills of Type typeString to the available list of
	 * the EquipmentChoice object equipChoice
	 * @param typeString the type of Skill to add to the chooser
	 */
	public void addSelectableSkills(final String typeString)
	{
		for (Skill skill : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Skill.class))
		{
			if ((typeString.equalsIgnoreCase("ALL") || skill.isType(typeString))
				&& !this.getAvailableList().contains(skill.getKeyName()))
			{
				this.getAvailableList().add(skill.getKeyName());
			}
		}
	}

	/**
	 * @param parent The piece of Equipment that this Equipment Modifier will be added to
	 * @param choiceType the type of Skill to add to the chooser
	 */
	public void addParentsExistingEquipmentModifiersToChooser(final Equipment parent, String choiceType)
	{
		for (EquipmentModifier sibling : parent.getEqModifierList(true))
		{
			/*
			 * TODO sibling can't be this - different classes... so this is a
			 * bug of some form.
			 */
			if (!(sibling.equals(this)) && sibling.getSafe(StringKey.CHOICE_STRING).startsWith(choiceType))
			{
				getAvailableList().addAll(parent.getAssociationList(sibling));
			}
		}
	}

	/**
	 * Populate an EquipmentChoice object with choices based on kindToAdd (Skill,
	 * Feat, etc.) and filtered by filterBy.
	 * @param parent the piece of Equipment that this Equipment Modifier will
	 *        be added to
	 * @param numOfChoices the number of choices to make
	 * @param numChosen    the number of choices made up to this point
	 * @param filterBy     the type used to filter the kind of thing being
	 *                     chosen
	 * @param kindToAdd    what kind of choice are we adding?  skills,
	 *                     equipment, etc.
	 * @param category     if adding abilities, this will contain the category
	 *                     of ability to add
	 */
	public void addChoicesByType(final Equipment parent, final int numOfChoices, final int numChosen, String filterBy,
		String kindToAdd, String category)
	{
		if ((numOfChoices > 0) && (getMaxSelect() == 0))
		{
			setPool(numOfChoices - numChosen);
		}

		final String type = filterBy.substring(5);

		if (type.startsWith("LASTCHOICE"))
		{
			addParentsExistingEquipmentModifiersToChooser(parent, kindToAdd);
		}
		else if ("SKILL".equalsIgnoreCase(kindToAdd))
		{
			addSelectableSkills(type);
		}
		else if ("EQUIPMENT".equalsIgnoreCase(kindToAdd))
		{
			addSelectableEquipment(type);
		}
		else if ("ABILITY".equalsIgnoreCase(kindToAdd))
		{
			addSelectableAbilities(type, category);
		}
		else if ("FEAT".equalsIgnoreCase(kindToAdd))
		{
			addSelectableAbilities(type, "FEAT");
		}

		else if ("EQTYPES".equalsIgnoreCase(type))
		{
			Collection<Type> types = equipmentTypeFacet.getSet(Globals.getContext().getDataSetID());
			List<Object> list = getAvailableList();
			for (Type t : types)
			{
				list.add(t.toString());
			}
		}
		else
		{
			Logging.errorPrint("Unknown option in CHOOSE '" + filterBy + "'");
		}
	}

	/**
	 * Add the current character stats as defined in the game mode to the chooser
	 */
	public void addStats()
	{
		for (PCStat stat : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(PCStat.class))
		{
			this.getAvailableList().add(stat.getKeyName());
		}
	}

	/**
	 * @param available
	 * @param numSelected
	 */
	public void adjustPool(final int available, final int numSelected)
	{
		if ((available > 0) && (this.getMaxSelect() > 0) && (this.getMaxSelect() != Integer.MAX_VALUE))
		{
			this.setPool(this.getMaxSelect() - numSelected);
		}
	}

	/**
	 * Populate this EquipmentChoice object using data held in choiceString
	 * @param choiceString The string containing the info to be parsed and added
	 *                     to the chooser
	 * @param parent       the piece of Equipment that this Equipment Modifier
	 *                     will be added to
	 * @param available    used to adjust the pool
	 * @param numSelected  choices made so far
	 * @param forEqBuilder is this being constructed by the equipment builder,
	 *                      or for interaction with the user.
	 */
	public void constructFromChoiceString(String choiceString, final Equipment parent, final int available,
		final int numSelected, final boolean forEqBuilder, PlayerCharacter pc)
	{
		final StringTokenizer titleTok = new StringTokenizer(choiceString, "|", false);
		while (!forEqBuilder && titleTok.hasMoreTokens())
		{
			String workingkind = titleTok.nextToken();
			if (workingkind.startsWith("TITLE="))
			{
				this.setTitle(workingkind.substring(6));
			}
		}

		int select = parent.getSafe(FormulaKey.SELECT).resolve(parent, true, pc, "").intValue();
		setMaxSelect(select);

		String originalkind = null;
		final StringTokenizer aTok = new StringTokenizer(choiceString, "|", false);
		boolean needStats = false;
		boolean needSkills = false;
		String category = null;

		while (!forEqBuilder && aTok.hasMoreTokens())
		{
			String kind = aTok.nextToken();
			if (category == null)
			{
				if (kind.equals("ABILITY"))
				{
					category = aTok.nextToken();
				}
				else
				{
					category = "FEAT";
				}
			}

			this.adjustPool(available, numSelected);

			if (kind.startsWith("TITLE="))
			{
				//Do nothing, handled above
				Logging.log(Logging.DEBUG, "kind starts with TITLE= and we've already processed this.");
			}
			else if (kind.startsWith("COUNT="))
			{
				// Do nothing, handled above
				Logging.log(Logging.DEBUG, "kind starts with COUNT= and we've already processed this.");
			}
			else
			{
				if (originalkind == null)
				{
					originalkind = kind;
					needStats = originalkind.equals("STATBONUS");
					needSkills = originalkind.equals("SKILLBONUS");
				}
				else if (kind.startsWith("TYPE=") || kind.startsWith("TYPE."))
				{
					if (originalkind.equals("SKILLBONUS") || originalkind.equals("SKILL"))
					{
						//New Style
						needSkills = false;
						this.addChoicesByType(parent, available, numSelected, kind, "SKILL", "");
					}
					else if (originalkind.equals("EQUIPMENT") || originalkind.equals("FEAT")
						|| originalkind.equals("ABILITY"))
					{
						//New Style
						this.addChoicesByType(parent, available, numSelected, kind, originalkind, category);
					}
					else
					{
						//Old Style
						this.addChoicesByType(parent, available, numSelected, kind, getTitle(), category);
					}
				}
				else if ("STAT".equals(kind))
				{
					this.addStats();
				}
				else if ("SKILL".equals(kind))
				{
					this.addSkills();
				}
				else if ("SKIPZERO".equals(kind))
				{
					skipZero = originalkind.equals("NUMBER");
				}
				else if ("MULTIPLE".equals(kind))
				{
					this.setAllowDuplicates(true);
				}
				else if ("NOSIGN".equals(kind))
				{
					this.setNoSign(true);
				}
				else if (kind.startsWith("MIN="))
				{
					this.setMinValueFromString(kind);
				}
				else if (kind.startsWith("MAX="))
				{
					this.setMaxValueFromString(kind);
				}
				else if (kind.startsWith("INCREMENT="))
				{
					this.setIncrementValueFromString(kind);
				}
				else
				{
					needStats = false;
					needSkills = false;
					if (!this.getAvailableList().contains(kind))
					{
						this.getAvailableList().add(kind);
					}
				}
			}
		}

		if (needStats)
		{
			this.addStats();
		}
		else if (needSkills)
		{
			this.addSkills();
		}

		if (this.getTitle() == null)
		{
			this.setTitle(originalkind);
		}

		if (this.getMaxSelect() == Integer.MAX_VALUE)
		{
			this.setPool(this.getAvailableList().size() - numSelected);
			this.setBAdd(true);
		}

		if ((this.getAvailableList().isEmpty()) && (this.getMinValue() < this.getMaxValue()))
		{
			for (int j = this.getMinValue(); j <= this.getMaxValue(); j += this.getIncValue())
			{
				if (!skipZero || j != 0)
				{
					if (this.isNoSign())
					{
						this.getAvailableList().add(j);
					}
					else
					{
						this.getAvailableList().add(new SignedInteger(j));
					}
				}
			}

			this.setMinValue(this.getMaxValue());
		}
	}

	private static class EquipChoiceIterator implements Iterator<Object>
	{
		List<Object> choiceList;
		int currPos;

		EquipChoiceIterator(final List<Object> list)
		{
			choiceList = list;
			currPos = 0;
		}

		@Override
		public boolean hasNext()
		{
			return currPos < choiceList.size();
		}

		@Override
		public Object next()
		{
			return choiceList.get(currPos++);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

	}
}
