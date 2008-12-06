/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMReference;
import pcgen.core.Ability;

public class AbilityRef
{

	private final CDOMReference<Ability> abilities;
	private String choice = null;

	public AbilityRef(CDOMReference<Ability> ab)
	{
		abilities = ab;
	}

	public void addChoice(String s)
	{
		choice = s;
	}

	public CDOMReference<Ability> getRef()
	{
		return abilities;
	}
	
	public String getChoice()
	{
		return choice;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof AbilityRef)
		{
			AbilityRef other = (AbilityRef) obj;
			if (other.abilities.equals(abilities))
			{
				if (choice == null)
				{
					return other.choice == null;
				}
				else
				{
					return choice.equals(other.choice);
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return 3 - abilities.hashCode();
	}

}
