/*
 * SimpleFeatChoiceManager.java
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

import java.util.Collection;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * Deal with choosing a Feat
 */
public class SimpleFeatChoiceManager extends
		AbstractEasyStringChoiceManager<Ability>
{
	/**
	 * Creates a new SimpleFeatChoiceManager object.
	 *
	 * @param  aPObject
	 * @param  theChoices
	 * @param  aPC
	 */
	public SimpleFeatChoiceManager(PObject aPObject, String theChoices, PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
	}

	@Override
	public Collection<Ability> getAllObjects()
	{
		return Globals.getAbilityList(AbilityCategory.FEAT);
	}

	@Override
	public Ability getSpecificObject(String key)
	{
		return Globals.getAbilityKeyed(AbilityCategory.FEAT, key);
	}

}
