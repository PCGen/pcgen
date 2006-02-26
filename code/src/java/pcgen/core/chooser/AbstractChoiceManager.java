/*
 * ChoiceManager.java
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
 * Current Version: $Revision: 1.8 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:37 $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class to handle generating a suitable list of choices, selecting from those
 * choices and potentially applying the choices to a PC
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision: 1.8 $
 */
public abstract class AbstractChoiceManager
{
	protected final PlayerCharacter pc;
	protected final PObject         pobject;
	protected List                  choices;

	protected String                chooserHandled   = "";
	protected int                   numberOfChoices  = 0;
	protected boolean               dupsAllowed      = false;
	protected String                title            = "";
	protected List                  uniqueList       = new ArrayList();

	/**
	 * Creates a new ChoiceManager object.
	 *
	 * @param  aPObject
	 * @param  theChoices
	 * @param  aPC
	 */
	public AbstractChoiceManager(
	    PObject         aPObject,
	    String          theChoices,
	    PlayerCharacter aPC)
	{
		super();
		pobject   = aPObject;
		pc        = aPC;

		final List   split = Arrays.asList(theChoices.split("[|]", 3));

		if (split.size() < 3)
		{
			choices = Collections.EMPTY_LIST;
			return;
		}

		chooserHandled     = (String) split.get(0);

		final String var   = (String) split.get(1);
		numberOfChoices    = aPC.getVariableValue(var, "").intValue();

		choices   = Arrays.asList(((String) split.get(2)).split("[|]"));
	}

	/**
	 * return handled chooser
	 * @return handled chooser
	 */
	public String typeHandled ()
	{
		return chooserHandled;
	}

	/**
	 * Get choices
	 *
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 */
	public abstract void getChoices(
		    final List            availableList,
		    final List            selectedList,
		    final PlayerCharacter aPC);

	/**
	 * Do chooser
	 * @param availableList
	 * @param selectedList
	 * @param selectedBonusList
	 * @param aPC
	 */
	public void doChooser (
		    final List            availableList,
		    final List            selectedList,
		    final List            selectedBonusList,
		    PlayerCharacter       aPC)
	{
		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setAllowsDups(dupsAllowed);

		if (title.length() != 0)
		{
			chooser.setTitle(title);
		}

		Globals.sortChooserLists(availableList, selectedList);
		chooser.setAvailableList(availableList);
		chooser.setSelectedList(selectedList);
		chooser.setUniqueList(uniqueList);

		numberOfChoices -= chooser.getSelectedList().size();

		chooser.setPool(Math.max(0, numberOfChoices));
		chooser.setPoolFlag(false); // Allow cancel as clicking the x will cancel anyway

		chooser.setVisible(true);

		applyChoices(aPC, chooser, selectedBonusList);
	}

	/*
	 * Apply the choices made to the PC.
	 *
	 * @param aPC
	 * @param chooser
	 * @param selectedBonusList
	 */

	protected abstract void applyChoices(
			final PlayerCharacter  aPC,
			final ChooserInterface chooser,
			List                   selectedBonusList);
}
