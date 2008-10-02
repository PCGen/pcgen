/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from WeaponProf.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.ChangeProf;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.rules.context.AbstractReferenceContext;

public class WeaponProfType
{
	/**
	 * Get the Weapon Profs
	 * @param type
	 * @param aPC
	 * @return List of Weapon Profs
	 */
	public static List<WeaponProf> getWeaponProfs(final String type,
		final PlayerCharacter aPC)
	{
		AbstractReferenceContext ref = Globals.getContext().ref;
		CDOMGroupRef<WeaponProf> master =
				ref.getCDOMTypeReference(WeaponProf.class, type.split("\\."));
		List<WeaponProf> aList = new ArrayList<WeaponProf>();
		//Can't use master because late called references may not have been initialized, see 2001287
		Collection<WeaponProf> weaponProfsOfType =
				Globals.getPObjectsOfType(ref
					.getConstructedCDOMObjects(WeaponProf.class), type);
		for (CDOMObject cdo : aPC.getCDOMObjectList())
		{
			List<ChangeProf> changes = cdo.getListFor(ListKey.CHANGEPROF);
			if (changes != null)
			{
				for (ChangeProf cp : changes)
				{
					if (cp.getResult().equals(master))
					{
						aList.addAll(cp.getSource().getContainedObjects());
					}
					else if (weaponProfsOfType != null)
					{
						weaponProfsOfType.removeAll(cp.getSource()
							.getContainedObjects());
					}
				}
			}
		}
		aList.addAll(weaponProfsOfType);
		return aList;
	}

}
