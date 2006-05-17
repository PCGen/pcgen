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

import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.levelability.LevelAbility;

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
	private ArrayList theAbilities = new ArrayList();

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

	class AbilityChoice
	{
		private ArrayList theChoices = new ArrayList();
		private String theAbilityName = "";

		AbilityChoice(final String ability, final List choices)
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
		public final List getChoices()
		{
			return theChoices;
		}
	}

	/**
	 * Add an ability
	 * @param anAbility
	 * @param choices
	 */
	public void addAbility(final String anAbility, final List choices)
	{
		theAbilities.add(new AbilityChoice(anAbility, choices));
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		boolean firstTimeI = true;
		for (Iterator i = theAbilities.iterator(); i.hasNext(); )
		{
			if (!firstTimeI)
			{
				buf.append(", ");
			}
			AbilityChoice choice = (AbilityChoice)i.next();
			buf.append(choice.getAbility());
			buf.append(": [");
			List choices = choice.getChoices();
			boolean firstTime = true;
			for (Iterator j = choices.iterator(); j.hasNext(); )
			{
				if (!firstTime)
				{
					buf.append(", ");
				}
				String choiceStr = (String)j.next();
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

		List choiceList = new ArrayList();

		la.process(choiceList, aPC, pcLevelInfo);
		choiceList.clear();

		for (Iterator j = ac.getChoices().iterator(); j.hasNext(); )
		{
			String choice = (String)j.next();
			// Remove CHOICE:
			choiceList.add(choice.substring(7));
		}
		la.processChoice(null, choiceList, aPC, pcLevelInfo);
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List warnings)
	{
		theClass = Globals.getClassKeyed(theClassName);
		if (theClass == null)
		{
			warnings.add("LEVELABILITY: Could not find class \"" + theClassName
						 + "\"");
			return false;
		}
		for (Iterator i = theAbilities.iterator(); i.hasNext(); )
		{
			AbilityChoice ac = (AbilityChoice)i.next();

			addLevelAbility(aPC, theClass, ac);
		}
		return true;
	}

	public void apply(PlayerCharacter aPC)
	{
		for (Iterator i = theAbilities.iterator(); i.hasNext(); )
		{
			AbilityChoice ac = (AbilityChoice)i.next();

			addLevelAbility(aPC, theClass, ac);
		}
	}

	public Object clone()
	{
		KitLevelAbility aClone = (KitLevelAbility)super.clone();

//		aClone.theClassName = theClassName;
//		aClone.theLevel = theLevel;
//		aClone.theAbilities.addAll(theAbilities);

		return aClone;
	}

	public String getObjectName()
	{
		return "Class Feature";
	}
}
