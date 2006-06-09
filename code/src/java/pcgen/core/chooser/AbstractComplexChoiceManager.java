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
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * A class to handle generating a suitable list of choices, selecting from
 * those choices and potentially applying the choices to a PC
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision$
 */
public abstract class AbstractComplexChoiceManager<T> extends AbstractSimpleChoiceManager<T>
{
	protected boolean multiples           = false;
	protected double  cost                = 1.0;
	/** Indicator that the choice definition is valid. */
	protected boolean valid               = true;

	protected int     requestedSelections = -1;
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

		List<String> mainList = Arrays.asList(choiceString.split("[|]"));
		List<String> subList  = Arrays.asList(mainList.get(0).split("="));

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
			subList  = Arrays.asList(mainList.get(i).split("="));

			if (subList.get(0).equals("COUNT"))
			{
				final String var = subList.get(1);
				requestedSelections = aPC.getVariableValue(var, "").intValue();
			}
			else if (subList.get(0).equals("NUMCHOICES"))
			{
				final String var = subList.get(1);
				numberOfChoices  = aPC.getVariableValue(var, "").intValue();
			}
			else
			{
				break;
			}
		}
		if (!valid || i >= mainList.size())
		{
			choices = Collections.emptyList();
			return;
		}

		i = (i >= 0) ? i : 0;

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
	 * @return true if the choice definition is valid, false otherwise.
	 */
	final boolean isValid()
	{
		return valid;
	}

	/**
	 * Remove a choice
	 * @param aPC
	 * @param availableList
	 * @param selectedList
	 */
	public void doChooserRemove (
			PlayerCharacter       aPC,
			final List<T>            availableList,
			final List<T>            selectedList)
	{
		remove = true;

		final List<T> newSelections = doChooser (
				aPC,
				availableList,
				selectedList);

		applyChoices(aPC, newSelections);

		remove = false;
	}

	/**
	 * Do chooser.
	 *
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public List<T> doChooser (
			PlayerCharacter       aPc,
			final List<T>            availableList,
			final List<T>            selectedList)
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
				selectedList.add(null);
			}
			showChooser     = false;
			numberOfChoices = 0;			// Make sure we are processing only 1 selection
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false);         // user is not required to make any changes
		chooser.setAllowsDups(dupsAllowed); // only stackable feats can be duped
		chooser.setVisible(false);
		chooser.setPool(requestedSelections);

		title = title + " (" + pobject.getDisplayName() + ')';
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

		List selected = chooser.getSelectedList();
		if (availableList.get(0) instanceof PObject && (selected.size() > 0
			&& !(selected.get(0) instanceof PObject)))
		{
			List newSelected = new ArrayList();
			// We started with an PObject list and got back a string list
			// Find the corresponding PObjects and return them instead.
			for (Iterator sel = selected.iterator(); sel.hasNext(); )
			{
				final String name = (String)sel.next();
				for (Iterator avail = availableList.iterator(); avail.hasNext();)
				{
					PObject pObj = (PObject)avail.next();
					if (name.equalsIgnoreCase(pObj.getDisplayName()))
					{
						newSelected.add(pObj);
						continue;
					}
				}
			}
			chooser.setSelectedList(newSelected);
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

	/**
	 * Apply the choices selected to the associated PObject (the one passed
	 * to the constructor)
	 * @param aPC
	 * @param selected
	 *
	 */
	public void applyChoices(
			PlayerCharacter  aPC,
			List<T>             selected)
	{
		cleanUpAssociated(aPC, selected.size());

		String objPrefix = (pobject instanceof Domain)
				? chooserHandled + '?'
				: "";

		if (pobject instanceof Ability)
		{
			((Ability)pobject).clearSelectedWeaponProfBonus(); //Cleans up the feat
		}

		Iterator<T> it = selected.iterator();
		while (it.hasNext())
		{
			Object choice = it.next();
			if ( choice != null )
			{
				String strChoice = choice.toString();
				if (choice instanceof PObject)
				{
					strChoice = ( (PObject) choice).getKeyName();
				}
				associateChoice(aPC, strChoice, objPrefix);
			}
		}

		adjustFeats(aPC, selected);

		if (objPrefix.length() != 0)
		{
			aPC.setAutomaticFeatsStable(false);
		}
	}

	/**
	 * Perform any necessary clean up of the associated property of pobject.
	 *
	 * @param aPc
	 * @param size
	 */
	protected void cleanUpAssociated(
			PlayerCharacter aPc,
			int             size)
	{
		pobject.clearAssociated();
	}

	/**
	 * Associate a choice with the pobject.
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
		final String name = prefix + item;

		if (multiples && !dupsAllowed)
		{
			if (!pobject.containsAssociated(name))
			{
				pobject.addAssociated(name);
			}
		}
		else
		{
			pobject.addAssociated(name);
		}
	}

	/**
	 * Adjust the number of feats the PC has available to take account of this choice
	 *
	 * @param aPC
	 * @param selected
	 */
	protected void adjustFeats(
			PlayerCharacter aPC,
			List<T>            selected)
	{
		double featCount = aPC.getFeats();

		if (cost > 0)
		{
			featCount = (numberOfChoices > 0)
					? featCount - cost
					: ((maxSelections - selected.size()) * cost);
		}

		aPC.adjustFeats(featCount - aPC.getFeats());
	}

}
