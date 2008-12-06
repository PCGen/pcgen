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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.EquipmentModifier;

public class EqModRef
{

	private final CDOMSingleRef<EquipmentModifier> eqMod;
	private List<String> choices = null;

	public EqModRef(CDOMSingleRef<EquipmentModifier> ab)
	{
		eqMod = ab;
	}

	public void addChoice(String s)
	{
		if (choices == null)
		{
			choices = new LinkedList<String>();
		}
		choices.add(s);
	}

	public CDOMSingleRef<EquipmentModifier> getRef()
	{
		return eqMod;
	}

	public List<String> getChoices()
	{
		return choices == null ? null : new ArrayList<String>(choices);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof EqModRef)
		{
			EqModRef other = (EqModRef) obj;
			if (other.eqMod.equals(eqMod))
			{
				if (choices == null)
				{
					return other.choices == null;
				}
				else
				{
					return choices.equals(other.choices);
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return 3 - eqMod.hashCode();
	}

}
