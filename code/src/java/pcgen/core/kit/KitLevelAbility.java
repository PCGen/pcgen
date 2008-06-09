/*
 * KitLevelAbility.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on December 21, 2005
 *
 * $Id$
 */
package pcgen.core.kit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;

/**
 * <code>KitLevelAbility</code>.
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public final class KitLevelAbility extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String theClassName = "";
	private int theLevel = -1;
	private List<AbilityChoice> theAbilities = new ArrayList<AbilityChoice>();

	private transient PCClass theClass = null;

	/** Constructor */
	public KitLevelAbility()
	{
		// Empty Constructor
	}

	/**
	 * Set the class
	 * @param className
	 */
	public void setClass(final String className)
	{
		theClassName = className;
	}

	/**
	 * Set the level
	 * @param level
	 */
	public void setLevel(final int level)
	{
		theLevel = level;
	}

	static class AbilityChoice
	{
		private List<String> theChoices = new ArrayList<String>();
		private String theAbilityName = "";

		AbilityChoice(final String ability, final List<String> choices)
		{
			// Chop off the PROMPT:
			theAbilityName = ability.substring(7);
			theChoices.addAll(choices);
		}

		/**
		 * Get the ability
		 * @return ability
		 */
		public final String getAbility()
		{
			return theAbilityName;
		}

		/**
		 * Get choices
		 * @return choices
		 */
		public final List<String> getChoices()
		{
			return theChoices;
		}
	}

	/**
	 * Add an ability
	 * @param anAbility
	 * @param choices
	 */
	public void addAbility(final String anAbility, final List<String> choices)
	{
		theAbilities.add(new AbilityChoice(anAbility, choices));
	}

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		boolean firstTimeI = true;
		for ( AbilityChoice choice : theAbilities )
		{
			if (!firstTimeI)
			{
				buf.append(", ");
			}
			buf.append(choice.getAbility());
			buf.append(": [");
			List<String> choices = choice.getChoices();
			boolean firstTime = true;
			for ( String choiceStr : choices )
			{
				if (!firstTime)
				{
					buf.append(", ");
				}
				buf.append(choiceStr);

				firstTime = false;
			}
			buf.append("]");
			firstTimeI = false;
		}
		return buf.toString();
	}

	private void addLevelAbility(final PlayerCharacter aPC,
								 final PCClass pcClass,
								 final AbilityChoice ac)
	{
		LevelAbility la = theClass.addAddList(theLevel, ac.getAbility());
		PCLevelInfo pcLevelInfo = aPC.getLevelInfoFor(theClass.getKeyName(), theLevel);

		List<String> choiceList = new ArrayList<String>();

		la.process(choiceList, aPC, pcLevelInfo);
		choiceList.clear();

		for ( String choice : ac.getChoices() )
		{
			// Remove CHOICE:
			choiceList.add(choice.substring(7));
		}
		la.processChoice(null, choiceList, aPC, pcLevelInfo);
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		theClass = Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, theClassName);
		if (theClass == null)
		{
			warnings.add("LEVELABILITY: Could not find class \"" + theClassName
						 + "\"");
			return false;
		}
		for ( AbilityChoice ac : theAbilities )
		{
			addLevelAbility(aPC, theClass, ac);
		}
		return true;
	}

	public void apply(PlayerCharacter aPC)
	{
		for ( AbilityChoice ac : theAbilities )
		{
			addLevelAbility(aPC, theClass, ac);
		}
	}

	@Override
	public KitLevelAbility clone()
	{
		return (KitLevelAbility) super.clone();
	}

	public String getObjectName()
	{
		return "Class Feature";
	}
}
