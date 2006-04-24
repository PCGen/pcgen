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

import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.FeatMultipleChoice;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing a spell level.
 */
public class SpellListChoiceManager extends AbstractComplexChoiceManager
{
	int                idxSelected = -1;
	FeatMultipleChoice fmc         = null;


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
		title          = "Spell choice";
		chooserHandled = "SPELLLIST";

		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 *
	 * @param  aPc
	 * @param  availableList
	 * @param  selectedList
	 */
	public void getChoices(
	    final PlayerCharacter aPc,
	    final List            availableList,
	    final List            selectedList)
	{
		if (Ability.class.isInstance(pobject) && chooseAbility())
		{
			setSpellListSelections(aPc, availableList, selectedList);

			// Set up remaining choices for pre-existing selection

			if (idxSelected >= 0)
			{
				fmc = (FeatMultipleChoice) pobject.getAssociatedObject(idxSelected);
				maxNewSelections    = fmc.getMaxChoices();
				requestedSelections = maxNewSelections;
			}
		}
		else
		{
			availableList.clear();
			selectedList.clear();
		}
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
		if (idxSelected >= 0)
		{
			pobject.removeAssociated(idxSelected);

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
	protected void associateChoice(
			final PlayerCharacter aPc,
			final String          item,
			final String          prefix)
	{
		if (fmc == null)
		{
			fmc = new FeatMultipleChoice();
			fmc.setMaxChoices(maxNewSelections);
			pobject.addAssociated(fmc);
		}

		fmc.addChoice(item);
	}

	/**
	 * Adjust the number of feats the PC has available to take account of this choice
	 *
	 * @param aPC
	 * @param selected
	 */
	protected void adjustFeats(
			PlayerCharacter aPC,
			List            selected)
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
	 *
	 * @return  true if a valid sub ability was chosen
	 */
	private boolean chooseAbility()
	{
		Ability    anAbility = (Ability) pobject;
		int        i;
		final List aList     = new ArrayList();
		aList.add("New");

		final StringBuffer sb = new StringBuffer(100);

		for (int j = 0; j < anAbility.getAssociatedCount(); ++j)
		{
			fmc = (FeatMultipleChoice) anAbility.getAssociatedList().get(j);
			sb.append(anAbility.getName()).append(" (");
			sb.append(fmc.getChoiceCount());
			sb.append(" of ").append(fmc.getMaxChoices()).append(") ");

			for (i = 0; i < fmc.getChoiceCount(); ++i)
			{
				if (i != 0)
				{
					sb.append(',');
				}

				sb.append(fmc.getChoice(i));
			}

			aList.add(sb.toString());
			sb.setLength(0);
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

		idxSelected = aList.indexOf(selectedValue) - 1;

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
	    final List            availableList,
	    final List            selectedList)
	{
		Iterator choicesIt = choices.iterator();

		Iterator      iter;
		final boolean needSpellbook;

		switch (((String) choicesIt.next()).charAt(0))
		{
			case '1':
			case 'Y':
				needSpellbook = true;

				break;

			default:
				needSpellbook = false;

				break;
		}

		List    classes = null;

		for (int j = 0;; ++j)
		{
			final PObject aClass = aPC.getSpellClassAtIndex(j);

			if (aClass == null)
			{
				break;
			}

			if (
			    (aClass instanceof PCClass) &&
			    (((PCClass) aClass).getSpellBookUsed() == needSpellbook))
			{
				if (classes == null)
				{
					classes = new ArrayList();
				}

				classes.add(aClass);
			}
		}

		// Add all spells from all classes that match the spellbook
		// requirement.  Allow the number of selections to be the
		// maximum allowed by the classes' spell base stat

		if (classes != null)
		{
			maxNewSelections = 0;

			for (int j = 0; j < classes.size(); ++j)
			{
				final PObject aClass = (PObject) classes.get(j);

				final List aList = aClass.getSpellSupport().getCharacterSpell(
					    null,
					    Globals.getDefaultSpellBook(),
					    -1);

				for (iter = aList.iterator(); iter.hasNext();)
				{
					final CharacterSpell cs     = (CharacterSpell) iter.next();
					final Spell          aSpell = cs.getSpell();

					if (!pobject.containsAssociated(aSpell.getKeyName()))
					{
						if (!availableList.contains(aSpell.getName()))
						{
							availableList.add(aSpell.getName());
						}
					}
				}

				int statMod = aPC.getStatList().getStatModFor(((PCClass) aClass).getSpellBaseStat());

				if (statMod > 0)
				{
					maxNewSelections = statMod;
				}
			}

			// Remove all previously selected items from the available list

			final List assocList = pobject.getAssociatedList();

			if (assocList != null)
			{
				for (int j = 0; j < assocList.size(); ++j)
				{
					final FeatMultipleChoice featMultChoice = (FeatMultipleChoice) assocList.get(j);
					final List               fmcChoices = featMultChoice.getChoices();

					if (fmcChoices != null)
					{
						for (int k = 0; k < fmcChoices.size(); ++k)
						{
							if (j == idxSelected)
							{
								selectedList.add(fmcChoices.get(k));
							}
							else
							{
								availableList.remove(fmcChoices.get(k));
							}
						}
					}
				}
			}
		}
	}
}
