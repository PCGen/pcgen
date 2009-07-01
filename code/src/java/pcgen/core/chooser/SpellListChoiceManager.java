/**
 * SpellListChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pcgen.base.util.FixedStringList;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.StatAnalysis;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;

/**
 * This is the chooser that deals with choosing a spell level.
 */
public class SpellListChoiceManager extends AbstractBasicStringChoiceManager
{
	FixedStringList selected = null;
	FixedStringList fmc         = null;
	int maxSpellListSelections = 0;


	/**
	 * Make a new spell level chooser.
	 *
	 * @param  aPObject
	 * @param  choiceString
	 * @param  aPC
	 */
	public SpellListChoiceManager(
		PObject         aPObject,
		String          choiceString,
		PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		setTitle("Spell choice");
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 *
	 * @param  aPc
	 * @param  availableList
	 * @param  selectedList
	 */
	@Override
	public void getChoices(
		final PlayerCharacter aPc,
		final List<String>            availableList,
		final List<String>            selectedList)
	{
		if (Ability.class.isInstance(pobject) && chooseAbility(aPc))
		{
			setSpellListSelections(aPc, availableList, selectedList);

			// Set up remaining choices for pre-existing selection

			if (selected != null)
			{
				fmc = selected;
				setMaxChoices(fmc.size());
			}
		}
		else
		{
			availableList.clear();
			selectedList.clear();
		}
		setPreChooserChoices(selectedList.size());
	}

	/**
	 * Perform any necessary clean up of the associated property of pobject.
	 *
	 * @param aPc
	 */
	protected void cleanUpAssociated(
			PlayerCharacter aPc,
			int             size)
	{
		if (selected != null)
		{
			aPc.removeAssociation(pobject, selected);

			if (size == 0)
			{
				aPc.adjustFeats(1);
			}
		}
		else if (size != 0)
		{
			aPc.adjustFeats(-1);
		}

		/* nulling this out because we can't do it in apply choices but we want
		 * it done before applyChoices calls associateChoice */
		fmc = null;
	}

	/**
	 * Associate a choice with the pobject.  Only here so we can override part
	 * of the behaviour of applyChoices
	 *
	 * @param aPc
	 * @param item the choice to associate
	 * @param prefix
	 */
	@Override
	protected void associateChoice(
			final PlayerCharacter aPc,
			final String          item)
	{
		if (fmc == null)
		{
			fmc = new FixedStringList(maxSpellListSelections);
			aPc.addAssociation(pobject, fmc);
		}

		fmc.add(item);
	}

	/**
	 * Adjust the number of feats the PC has available to take account of this choice
	 *
	 * @param aPC
	 * @param selected
	 */
	protected void adjustFeats(
			PlayerCharacter aPC,
			List<String>            selected)
	{
		// Nothing to do here.  The method this class replaces specifically checked
		// that it wasn't part of a SpellList chooser before it adjusted the Feat
		// Pool.  This empty method ensures the method is the super class is not
		// invoked.  The feat pool is modified by cleanUpAssociated
	}

	/**
	 * This type of chooser only works on Abilities that have sub abilities.  For
	 * instance, the Spell mastery ability can be chosen multiple times and each
	 * instance has its own list of choices.  This routine get the user to
	 * choose one of the instances.  It returns 1 + the instances index, this is
	 * the account for the item "New" which is first in the list.
	 * @param pc TODO
	 *
	 * @return  true if a valid sub ability was chosen
	 */
	private boolean chooseAbility(PlayerCharacter pc)
	{
		Ability    anAbility = (Ability) pobject;
		final List<String> aList     = new ArrayList<String>();
		aList.add("New");

		final StringBuffer sb = new StringBuffer(100);

		List<FixedStringList> origList = pc.getDetailedAssociations(anAbility);
		if (origList != null)
		{
			for (FixedStringList assoc : origList)
			{
				sb.append(anAbility.getKeyName()).append(" (");
				int chosen = 0;
				for (String s : assoc)
				{
					if (s != null)
					{
						chosen++;
					}
				}
				sb.append(chosen);
				sb.append(" of ").append(assoc.size()).append(") ");

				boolean needComma = false;
				for (String a : assoc)
				{
					if (a != null)
					{
						if (needComma)
						{
							sb.append(',');
						}
						needComma = true;
						sb.append(a);
					}
				}

				aList.add(sb.toString());
				sb.setLength(0);
			}
		}

		final Object selectedValue;

		if (aList.size() > 1)
		{
			final InputInterface ii = InputFactory.getInputInstance();
			selectedValue = ii.showInputDialog(
					null,
					"Please select the instance of the feat you wish to" +
					Constants.s_LINE_SEP +
					"modify, or New, from the list below.",
					Constants.s_APPNAME,
					MessageType.INFORMATION,
					aList.toArray(),
					aList.get(0));
		}
		else
		{
			selectedValue = aList.get(0);
		}

		if (selectedValue == null)
		{
			return false;
		}

		int idxSelected = aList.indexOf(selectedValue) - 1;
		if (idxSelected > 0)
		{
			selected = origList.get(idxSelected);
		}

		return true;
	}

	/**
	 * Add all spells from all the PCs classes that match the spellbook
	 * requirement.  Allow the number of selections to be the maximum allowed by
	 * the classes' spell base stat
	 *
	 * @param  aPC
	 * @param  availableList
	 * @param  selectedList
	 */
	private void setSpellListSelections(
		final PlayerCharacter aPC,
		final List<String>            availableList,
		final List<String>            selectedList)
	{
		Iterator<String> choicesIt = getChoiceList().iterator();

		final boolean needSpellbook;

		switch (choicesIt.next().charAt(0))
		{
			case '1':
			case 'Y':
				needSpellbook = true;

				break;

			default:
				needSpellbook = false;

				break;
		}

		List<PCClass>    classes = null;

		for (int j = 0;; ++j)
		{
			final PObject aClass = aPC.getSpellClassAtIndex(j);

			if (aClass == null)
			{
				break;
			}

			if (
				(aClass instanceof PCClass) &&
				(((PCClass) aClass).getSafe(ObjectKey.SPELLBOOK) == needSpellbook))
			{
				if (classes == null)
				{
					classes = new ArrayList<PCClass>();
				}

				classes.add((PCClass)aClass);
			}
		}

		// Add all spells from all classes that match the spellbook
		// requirement.  Allow the number of selections to be the
		// maximum allowed by the classes' spell base stat

		if (classes != null)
		{
			maxSpellListSelections = 0;

			for (int j = 0; j < classes.size(); ++j)
			{
				final PCClass aClass = classes.get(j);

				final List<CharacterSpell> aList = aPC.getCharacterSpells(
						aClass,
						null, Globals.getDefaultSpellBook(), -1);

				for ( CharacterSpell cs : aList )
				{
					final Spell          aSpell = cs.getSpell();

					if (!aPC.containsAssociated(pobject, aSpell.getKeyName()))
					{
						if (!availableList.contains(aSpell.getKeyName()))
						{
							availableList.add(aSpell.getKeyName());
						}
					}
				}

				PCStat ss = aClass.get(ObjectKey.SPELL_STAT);
				if (ss != null)
				{
					int statMod = StatAnalysis.getStatModFor(aPC, ss);

					if (statMod > 0)
					{
						maxSpellListSelections = statMod;
					}
				}
			}

			// Remove all previously selected items from the available list

			for (FixedStringList assoc : aPC.getDetailedAssociations(pobject))
			{
				if (assoc == selected)
				{
					for (String s : assoc)
					{
						if (s != null)
						{
							selectedList.add(s);
						}
					}
				}
				else
				{
					for (String s : assoc)
					{
						if (s != null)
						{
							availableList.remove(s);
						}
					}
				}
			}
		}
	}
}
