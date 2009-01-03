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
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.WeaponProfType;

public class WeaponProfProvider extends ConcretePrereqObject
{

	private List<CDOMSingleRef<WeaponProf>> direct;

	private List<CDOMGroupRef<WeaponProf>> type;

	public void addWeaponProf(CDOMSingleRef<WeaponProf> ref)
	{
		if (direct == null)
		{
			direct = new ArrayList<CDOMSingleRef<WeaponProf>>();
		}
		direct.add(ref);
	}

	public void addWeaponProfType(CDOMGroupRef<WeaponProf> ref)
	{
		if (type == null)
		{
			type = new ArrayList<CDOMGroupRef<WeaponProf>>();
		}
		type.add(ref);
	}

	public Collection<WeaponProf> getContainedProficiencies(PlayerCharacter pc)
	{
		List<WeaponProf> list = new ArrayList<WeaponProf>();
		if (direct != null)
		{
			for (CDOMSingleRef<WeaponProf> ref : direct)
			{
				list.add(ref.resolvesTo());
			}
		}
		if (type != null)
		{
			for (CDOMGroupRef<WeaponProf> ref : type)
			{
				list.addAll(WeaponProfType.getWeaponProfsInTarget(pc, ref));
			}
		}
		return list;
	}

	public String getLstFormat()
	{
		StringBuilder sb = new StringBuilder();
		boolean typeEmpty = type == null || type.isEmpty();
		if (direct != null && !direct.isEmpty())
		{
			sb.append(ReferenceUtilities.joinLstFormat(direct, Constants.PIPE));
			if (!typeEmpty)
			{
				sb.append(Constants.PIPE);
			}
		}
		if (!typeEmpty)
		{
			sb.append(ReferenceUtilities.joinLstFormat(type, Constants.PIPE));
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof WeaponProfProvider)
		{
			WeaponProfProvider other = (WeaponProfProvider) obj;
			if (direct == null)
			{
				if (other.direct != null)
				{
					return false;
				}
			}
			else
			{
				if (!direct.equals(other.direct))
				{
					return false;
				}
			}
			if (type == null)
			{
				if (other.type != null)
				{
					return false;
				}
			}
			else
			{
				if (!type.equals(other.type))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return (direct == null ? 0 : direct.hashCode() * 29)
				+ (type == null ? 0 : type.hashCode());
	}

	public boolean isEmpty()
	{
		return (direct == null || direct.isEmpty())
				&& (type == null || type.isEmpty());
	}
}
