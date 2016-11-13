/*
 * KitBio.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on February 16, 2006, 11:43 AM
 *
 * $Id$
 */
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.NumericPCAttribute;
import pcgen.cdom.enumeration.PCAttribute;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

/**
 * Code to represent a bio setting choices for a Kit.
 *
 * @author Aaron Divinsky &lt;boomer70@yahoo.com&gt;
 */
public class KitBio extends BaseKit
{
	private String theCharacterName = null;
	private Integer theCharacterAge = null;
	private List<Gender> theGenders = null;
	private transient Gender selectedGender = null;

	/**
	 * Set the character name to set for this kit item.
	 * @param aName Name to use.  Can be any string.
	 */
	public void setCharacterName(final String aName)
	{
		theCharacterName = aName;
	}

	public String getCharacterName()
	{
		return theCharacterName;
	}

	/**
	 * Set the character's age to set for this kit item.
	 * @param age The age to use.
	 */
	public void setCharacterAge(final Integer age)
	{
		theCharacterAge = age;
	}

	public Integer getCharacterAge()
	{
		return theCharacterAge;
	}

	/**
	 * This method actually applies any changes that can be made by the
	 * kit to the specified PlayerCharacter.
	 *
	 * @param aPC The character to apply the kit to.
	 */
	@Override
	public void apply(PlayerCharacter aPC)
	{
		if (theCharacterName != null)
		{
			aPC.setPCAttribute(PCAttribute.NAME, theCharacterName);
		}
		if (theCharacterAge != null)
		{
			aPC.setPCAttribute(NumericPCAttribute.AGE, theCharacterAge);
		}
		if (selectedGender != null)
		{
			aPC.setGender(selectedGender);
		}
	}

	/**
	 * The display name to represent what this kit item represents.
	 *
	 * @return object name
	 */
	@Override
	public String getObjectName()
	{
		return "Bio Settings";
	}

	/**
	 * Try and apply the selected gender to the character.  Any problems
	 * encountered should be logged as a string in the
	 * {@code warnings} list.
	 *
	 * @param aKit The owning kit for this item
	 * @param aPC The character the kit is being applied to
	 * @param warnings A list of warnings generated while attempting to
	 *   apply the kit
	 * @return true if OK
	 */
	@Override
	public boolean testApply(Kit aKit, PlayerCharacter aPC,
		List<String> warnings)
	{
		selectedGender = null;
		if (theGenders != null && !theGenders.isEmpty())
		{
			if (theGenders.size() > 1)
			{
				List<Gender> selList = new ArrayList<>(1);
				selList = Globals.getChoiceFromList("Choose Gender", theGenders, selList,
					1, aPC);
				if (selList.size() == 1)
				{
					selectedGender = selList.get(0);
				}
			}
			else
			{
				selectedGender = theGenders.get(0);
			}
		}
		apply(aPC);

		return true;
	}

	@Override
	public String toString()
	{
		final StringBuilder info = new StringBuilder();

		if (theCharacterName != null)
		{
			info.append(" Name: " + theCharacterName);
		}
		if (theGenders != null)
		{
			info.append(" Gender: " + StringUtil.join(theGenders, ", "));
		}
		if (theCharacterAge != null)
		{
			info.append(" Age: " + theCharacterAge);
		}

		return info.toString();
	}

	public void addGender(Gender gender)
	{
		if (theGenders == null)
		{
			theGenders = new ArrayList<>();
		}
		if (theGenders.contains(gender))
		{
			throw new IllegalArgumentException("Cannot add Gender: " + gender
				+ " twice");
		}
		theGenders.add(gender);
	}

	public Collection<Gender> getGenders()
	{
		return theGenders;
	}
}
