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

import java.util.Collection;
import java.util.List;

import pcgen.core.AssociatedChoice;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * Deal with choosing a Generic, Basic Object
 */
public abstract class AbstractEasyStringChoiceManager<T extends PObject>
		extends AbstractBasicStringChoiceManager
{
	/**
	 * Creates a new AbstractBasicStringChoiceManager object.
	 * 
	 * @param aPObject
	 * @param theChoices
	 * @param aPC
	 */
	public AbstractEasyStringChoiceManager(PObject aPObject, String theChoices,
			PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
	}

	/**
	 * Get a list of Feats
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
	public void getChoices(PlayerCharacter aPc, List<String> availableList,
			List<String> selectedList)
	{
		initializeSelected(selectedList);
		setPreChooserChoices(selectedList.size());
		for (String tempString : getChoiceList())
		{
			processChoice(tempString, aPc, availableList);
		}
	}

	protected void processChoice(String tempString, PlayerCharacter aPc,
			List<String> availableList)
	{
		if (tempString.equals("ANY") || tempString.startsWith("ALL"))
		{
			for (T obj : getAllObjects())
			{
				String objKey = obj.getKeyName();
				if (!availableList.contains(objKey))
				{
					availableList.add(objKey);
				}
			}
		}
		else if (tempString.startsWith("TYPE=")
				|| tempString.startsWith("TYPE."))
		{
			processType(tempString.substring(5), availableList);
		}
		else
		{
			processOther(tempString, availableList, aPc);
		}
	}

	protected void processType(String tempString, List<String> availableList)
	{
		for (T obj : getAllObjects())
		{
			String objKey = obj.getKeyName();
			if (obj.isType(tempString) && !availableList.contains(objKey))
			{
				availableList.add(objKey);
			}
		}
	}

	protected void initializeSelected(List<String> selectedList)
	{
		for (AssociatedChoice<String> choice : pobject.getAssociatedList())
		{
			selectedList.add(choice.getDefaultChoice());
		}
	}

	protected void processOther(String tempString, List<String> availableList,
			PlayerCharacter aPC)
	{
		T feat = getSpecificObject(tempString);
		String featKey = feat.getKeyName();
		if (!availableList.contains(featKey))
		{
			availableList.add(featKey);
		}
	}

	public abstract Collection<T> getAllObjects();

	public abstract T getSpecificObject(String key);

}
