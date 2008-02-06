/*
 * AbstractBasicStringChoiceManager.java
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
 * Current Version: $Revision: 1062 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2006-06-10 00:29:06 -0400 (Sat, 10 Jun 2006) $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.List;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * Deal with choosing a Generic, Basic Object
 */
public abstract class AbstractBasicStringChoiceManager extends
		AbstractBasicChoiceManager<String>
{
	/**
	 * Creates a new AbstractBasicStringChoiceManager object.
	 * 
	 * @param aPObject
	 * @param theChoices
	 * @param aPC
	 */
	public AbstractBasicStringChoiceManager(PObject aPObject,
			String theChoices, PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
	}

	/**
	 * Add the selected Feat proficiencies
	 * 
	 * @param aPC
	 * @param selected
	 */
	@Override
	public void applyChoices(PlayerCharacter aPC, List<String> selected)
	{
		cleanUpAssociated(aPC);
		for (String st : selected)
		{
			if (isMultYes() && !isStackYes())
			{
				if (!pobject.containsAssociated(st))
				{
					associateChoice(aPC, st);
				}
			}
			else
			{
				associateChoice(aPC, st);
			}
		}
		adjustPool(selected);
	}

	protected void cleanUpAssociated(PlayerCharacter aPC)
	{
		pobject.clearAssociated();
	}

	protected void associateChoice(PlayerCharacter pc, String st)
	{
		pobject.addAssociated(st);
	}
}
