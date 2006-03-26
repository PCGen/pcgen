/*
 * AbstractSimpleChoiceManager.java
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
 * Last Editor: $Author$
 * Last Edited: $Date$
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
 * @version  $Revision$
 */
public abstract class AbstractSimpleChoiceManager implements ChoiceManagerList
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
	 * Creates a new ChoiceManager object.  Without any choice initialisation
	 *
	 * @param  aPObject
	 * @param  aPC
	 */
	public AbstractSimpleChoiceManager(
	    PObject         aPObject,
	    PlayerCharacter aPC)
	{
		super();
		pobject   = aPObject;
		pc        = aPC;
	}

	/**
	 * Creates a new ChoiceManager object.
	 *
	 * @param  aPObject
	 * @param  theChoices
	 * @param  aPC
	 */
	public AbstractSimpleChoiceManager(
	    PObject         aPObject,
	    String          theChoices,
	    PlayerCharacter aPC)
	{
		super();
		pobject   = aPObject;
		pc        = aPC;

		initialise(theChoices, aPC);
	}

	/**
	 * @param theChoices
	 * @param aPC
	 */
	private void initialise(String theChoices, PlayerCharacter aPC) {
		final List   split = Arrays.asList(theChoices.split("[|]", 3));

		if (split.size() < 3)
		{
			choices = Collections.EMPTY_LIST;
			return;
		}

		this.chooserHandled = ((String) split.get(0));

		final String var   = (String) split.get(1);
		numberOfChoices    = aPC.getVariableValue(var, "").intValue();

		choices   = Arrays.asList(((String) split.get(2)).split("[|]"));
	}


	
	
	/**
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public abstract void getChoices(
		    final PlayerCharacter aPc,
		    final List            availableList,
		    final List            selectedList);


	
	/**
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 * @param selectedBonusList
	 * @return list
	 */
	public List doChooser (
		    PlayerCharacter       aPc,
		    final List            availableList,
		    final List            selectedList,
		    final List            selectedBonusList)
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

		return chooser.getSelectedList();
	}

	/*
	 * Apply the choices made to the PC.
	 *
	 * @param aPC
	 * @param chooser
	 * @param selectedBonusList
	 */

	/**
	 * 
	 * @param aPC
	 * @param selected
	 * @see pcgen.core.chooser.ChoiceManagerList#applyChoices(pcgen.core.PlayerCharacter, java.util.List)
	 */
	public abstract void applyChoices(
			final PlayerCharacter  aPC,
			final List             selected);
}
