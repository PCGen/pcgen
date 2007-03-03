/*
 * SwingChooserUserInput.java
 * Copyright 2007 (C) James Dempsey
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
 * Created on 2 Mar 2007
 *
 * $$Id$$
 */
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * Handle the logic necessary to get a free text choice from the user.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class UserInputChoiceManager extends AbstractComplexChoiceManager<String>
{
	protected int     selectionsPerAbility  = 0;

	/**
	 * Creates a new UserInputChoiceManager object.
	 *
	 * @param  aPObject The object the chooser is for.
	 * @param  theChoices The LST definition of the choices to be offered.
	 * @param  aPC The character the chooser is for.
	 */
	public UserInputChoiceManager(PObject aPObject, String theChoices, PlayerCharacter aPC)
	{
		super(aPObject, aPC);
		chooserHandled = "USERINPUT";
		infiniteAvail = true;
		parseParams(theChoices, aPC);
	}

	/**
	 * Parse the parameters supplied to the chooser. Expected format
	 * is CHOOSE:USERINPUT|x|TITLE="y"
	 * 
	 * @param theChoices The list of parameters, seperated by |
	 * @param aPC The character the chooser is for.
	 */
	private void parseParams(String theChoices, PlayerCharacter aPC) 
	{
		final List<String>   split = Arrays.asList(theChoices.split("[|]"));

		choices = Collections.emptyList();

		if (split.size() < 1)
		{
			return;
		}
	
		for (int i = 0; i < split.size(); i++)
		{
			String param = split.get(i);
			if (i == 0)
			{
				continue;
			}
			else if (param.startsWith("TITLE="))
			{
				param = param.substring(6);
				if (param.startsWith("\""))
				{
					param = param.substring(1, param.length()-1);
				}
				title = param;
			}
			else if (i == 1)
			{
				requestedSelections    = aPC.getVariableValue(param, "").intValue();
			}
			else
			{
				Logging.errorPrintLocalised("in_uichooser_bad_param", param);
			}
		}
		selectionsPerAbility = requestedSelections;

		double pool = 0;
		if (pobject instanceof Ability)
		{
			pool =
					pc.getAvailableAbilityPool(
						SettingsHandler.getGame().getAbilityCategory(
							((Ability) pobject).getCategory())).doubleValue();
		}
		else
		{
			pool = aPC.getRawFeats(true);
		}
		maxSelections =
				(int) ((pool* requestedSelections) + pobject.getAssociatedCount());

		maxNewSelections = (int) (pool * requestedSelections);

		if (cost == 0 && maxNewSelections == 0)
		{
			maxNewSelections = requestedSelections;
		}
		
	}

	/**
	 * Construct the choices available from this ChoiceManager in availableList.
	 * Any Feats that are eligible to be added to availableList that the PC
	 * already has will also be added to selectedList.
	 * 
	 * @param  aPc The PC the chooser is for. 
	 * @param  availableList The list to be populated with available items.
	 * @param  selectedList The list to be populated with already selected items.
	 */
	public void getChoices(
		final PlayerCharacter aPc,
		final List<String>            availableList,
		final List<String>            selectedList)
	{
		if (pobject.getAssociatedCount() != 0)
		{
			List<String> abilityKeys = new ArrayList<String>();
			pobject.addAssociatedTo( abilityKeys );
			selectedList.addAll(abilityKeys);
		}
		availableList.clear();
		availableList.add(Constants.EMPTY_STRING);
	}

	/**
	 * Retrieve the appropriate chooser to use and set its title.
	 *  
	 * @return The chooser to be displayed to the user.
	 */
	protected ChooserInterface getChooserInstance()
	{
		final ChooserInterface chooser = ChooserFactory.getUserInputInstance();
		chooser.setTitle(title);
		return chooser;
	}
	

	/**
	 * Adjust the number of feats the PC has available to take account of this choice
	 *
	 * @param aPC The PC the chooser is for
	 * @param selected The list of selected items.
	 */
	protected void adjustFeats(
			PlayerCharacter aPC,
			List<String>            selected)
	{
		double featCount = aPC.getFeats();

		if (cost > 0)
		{
			featCount =
					(selectionsPerAbility > 0)
						? featCount
							- (((selected.size() - preChooserChoices) / selectionsPerAbility) * cost)
						: ((maxSelections - selected.size()) * cost);
		}

		aPC.adjustFeats(featCount - aPC.getFeats());
	}
	
}