/*
 * AbstractComplexChoiceManager.java
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
 * Current Version: $Revision$
 * Last Editor:     $Author$
 * Last Edited:     $Date$
 *
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.*;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class to handle generating a suitable list of choices, selecting from
 * those choices and potentially applying the choices to a PC
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision$
 */
public abstract class AbstractComplexChoiceManager extends AbstractSimpleChoiceManager
{
	protected boolean multiples           = false;
	protected double  cost                = 1.0;
	/** Indicator that the choice definition is valid. */ 
	protected boolean valid               = true;

	protected int     requestedSelections = 0;
	protected int     maxNewSelections    = 0;
	protected int     maxSelections       = 0;
	protected boolean remove              = false;

	/**
	 * Creates a new ChoiceManager object.
	 *
	 * @param  aPObject
	 * @param  aPC
	 */
	public AbstractComplexChoiceManager(
	    PObject         aPObject,
	    PlayerCharacter aPC)
	{
		super(aPObject, aPC);
	}

	/**
	 * Creates a new ChoiceManager object.
	 *
	 * @param  aPObject
	 * @param choiceString
	 * @param  aPC
	 */
	public AbstractComplexChoiceManager(
	    PObject         aPObject,
	    String          choiceString,
	    PlayerCharacter aPC)
	{
		super(aPObject, aPC);
		initialise(choiceString, aPC);
	}

	/**
	 * @param choiceString
	 * @param aPC
	 */
	private void initialise(
			String          choiceString,
			PlayerCharacter aPC)
	{
		if (pobject instanceof Ability)
		{
			cost         = ((Ability) pobject).getCost();
			dupsAllowed  = ((Ability) pobject).isStacks();
			multiples    = ((Ability) pobject).isMultiples();
		}

		List mainList = Arrays.asList(choiceString.split("[|]"));
		List subList  = Arrays.asList(((String) mainList.get(0)).split("="));

		int i = -1;
		while (subList.get(0).equals("COUNT") || subList.get(0).equals("NUMCHOICES"))
		{
			if (++i == mainList.size())
			{
				Logging.errorPrint("not enough tokens: " + choiceString);
				valid = false;
				break;
			}
			// Yes this is redundant the first time round the loop
			subList  = Arrays.asList(((String) mainList.get(i)).split("="));

			if (subList.get(0).equals("COUNT"))
			{
				final String var = (String) subList.get(1);
				requestedSelections = aPC.getVariableValue(var, "").intValue();
			}
			else if (subList.get(0).equals("NUMCHOICES"))
			{
				final String var = (String) subList.get(1);
				numberOfChoices  = aPC.getVariableValue(var, "").intValue();
			}
			else
			{
				break;
			}
		}
		if (!valid || i >= mainList.size())
		{
			choices = Collections.EMPTY_LIST;
			return;
		}

		choices        = mainList.subList(i, mainList.size());

		maxSelections  = (cost <= 0)
				? (int)  (aPC.getRawFeats(false) + pobject.getAssociatedCount())
				: (int) ((aPC.getRawFeats(false) + pobject.getAssociatedCount()) / cost);

		maxNewSelections = (cost <= 0)
				? (int) (aPC.getRawFeats(false))
				: (int) (aPC.getRawFeats(false) / cost);
	}

	/**
	 * Identify if the choice definition is valid.
	 * @return true if the choice defintion is valid, false otherwise. 
	 */
	final boolean isValid()
	{
		return valid;
	}

	/**
	 * Remove a choice
	 *
	 * @param availableList
	 * @param selectedList
	 * @param selectedBonusList
	 * @param aPC
	 */
	public void doChooserRemove (
		    final List            availableList,
		    final List            selectedList,
		    final List            selectedBonusList,
		    PlayerCharacter       aPC)
	{
		remove = true;
		final List newSelections = doChooser (
				aPC,
				availableList,
				selectedList,
				selectedBonusList);

		applyChoices(aPC, newSelections);
		
		remove = false;
	}

	/**
	 * Do chooser.  SelectedBonusList is unused, it's only here so the interface is the
	 * same as AbstractSimpleChoiceManager
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 * @param selectedBonusList
	 */
	public List doChooser (
		    PlayerCharacter       aPc,
		    final List            availableList,
		    final List            selectedList,
		    final List            selectedBonusList)
	{

		if (requestedSelections < 0)
		{
			requestedSelections = maxNewSelections;
		}
		else
		{
			requestedSelections -= selectedList.size();
			requestedSelections = Math.min(requestedSelections, maxNewSelections);
		}

		final int preChooserChoices = selectedList.size();

		if (numberOfChoices > 0)
		{
			// Make sure that we don't try to make the user choose more selections
			// than are available or we'll be in an infinite loop...

			numberOfChoices = Math.min(numberOfChoices, availableList.size() - preChooserChoices);
			requestedSelections = numberOfChoices;
		}

		boolean showChooser = true;
		if (availableList.size() == 1 && "NOCHOICE".equals(availableList.get(0).toString())) {
			if (remove)
			{
				try
				{
					selectedList.remove(0);
				}
				catch (IndexOutOfBoundsException ioobe)
				{
					// ignore
				}
			}
			else
			{
				selectedList.add("");
			}
			showChooser     = false;
			numberOfChoices = 0;			// Make sure we are processing only 1 selection
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false);         // user is not required to make any changes
		chooser.setAllowsDups(dupsAllowed); // only stackable feats can be duped
		chooser.setVisible(false);
		chooser.setPool(requestedSelections);

		title = title + " (" + pobject.getName() + ')';
		chooser.setTitle(title);
		Globals.sortChooserLists(availableList, selectedList);

		while (true)
		{
			chooser.setAvailableList(availableList);
			chooser.setSelectedList(selectedList);
			chooser.setVisible(showChooser);

			final int selectedSize = chooser.getSelectedList().size() - preChooserChoices;

			if (numberOfChoices > 0)
			{
				if (selectedSize != numberOfChoices)
				{
					ShowMessageDelegate.showMessageDialog("You must make " +
							(numberOfChoices - selectedSize) + " more selection(s).",
							Constants.s_APPNAME, MessageType.INFORMATION);
					continue;
				}
			}

			break;
		}
		
		return chooser.getSelectedList();
	}

	/**
	 * what type of chooser does this handle
	 * 
	 * @return type of chooser
	 */
	public String typeHandled() {
		return chooserHandled;
	}
}
