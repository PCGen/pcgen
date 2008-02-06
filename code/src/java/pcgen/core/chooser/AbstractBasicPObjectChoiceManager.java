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
 * Current Version: $Revision: 1172 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006-07-06 10:55:31 -0400 (Thu, 06 Jul 2006) $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.List;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * A class to handle generating a suitable list of choices, selecting from those
 * choices and potentially applying the choices to a PC
 */
public abstract class AbstractBasicPObjectChoiceManager<T extends PObject>
		extends AbstractBasicChoiceManager<T>
{

	public AbstractBasicPObjectChoiceManager(PObject object, String theChoices,
			PlayerCharacter apc)
	{
		super(object, theChoices, apc);
	}

	/**
	 * Add the selected Feat proficiencies
	 * 
	 * @param aPC
	 * @param selected
	 */
	@Override
	public void applyChoices(PlayerCharacter aPC, List<T> selected)
	{
		pobject.clearAssociated();
		for (T obj : selected)
		{
			String st = obj.getKeyName();
			if (isMultYes() && !isStackYes())
			{
				if (!pobject.containsAssociated(st))
				{
					pobject.addAssociated(st);
				}
			}
			else
			{
				pobject.addAssociated(st);
			}
		}
		adjustPool(selected);
	}

}
