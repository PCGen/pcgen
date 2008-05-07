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
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
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
public class UserInputChoiceManager extends AbstractBasicStringChoiceManager
{

	/**
	 * Creates a new UserInputChoiceManager object.
	 *
	 * @param  aPObject The object the chooser is for.
	 * @param  theChoices The LST definition of the choices to be offered.
	 * @param  aPC The character the chooser is for.
	 */
	public UserInputChoiceManager(PObject aPObject, String theChoices, PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
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
	@Override
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
		setPreChooserChoices(selectedList.size());
	}

	/**
	 * Retrieve the appropriate chooser to use and set its title.
	 *  
	 * @return The chooser to be displayed to the user.
	 */
	@Override
	protected ChooserInterface getChooserInstance()
	{
		return ChooserFactory.getUserInputInstance();
	}
}