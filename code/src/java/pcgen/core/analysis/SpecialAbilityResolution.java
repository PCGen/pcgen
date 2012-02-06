/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PObject.java
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

import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;

public class SpecialAbilityResolution
{
	public static List<SpecialAbility> addSABToList(List<SpecialAbility> saList, PlayerCharacter pc, CDOMObject cdo)
	{
		for (SpecialAbility sa : cdo.getSafeListFor(ListKey.SAB))
		{
			if (pc == null || sa.qualifies(pc, cdo))
			{
				final String key = sa.getKeyName();
				final int idx = key.indexOf("%CHOICE");

				if (idx >= 0)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(key.substring(0, idx));

					if (pc.hasAssociations(cdo))
					{
						sb.append(StringUtil.joinToStringBuffer(pc
								.getAssociationList(cdo), ", "));
					}
					else
					{
						sb.append("<undefined>");
					}

					sb.append(key.substring(idx + 7));
					sa = new SpecialAbility(sb.toString(), sa.getSADesc());
					saList.add(sa);
				}
				else
				{
					saList.add(sa);
				}
			}
		}
		return saList;
	}

	public static List<SpecialAbility> addSpecialAbilitiesToList(final List<SpecialAbility> aList, final PlayerCharacter aPC, CDOMObject cdo)
	{
		List<SpecialAbility> salist = aPC.getUserSpecialAbilityList(cdo);
		if (salist == null)
		{
			return aList;
		}
		for ( SpecialAbility sa : salist )
		{
			if (sa.qualifies(aPC, cdo))
			{
				final String key = sa.getKeyName();
				final int idx = key.indexOf("%CHOICE");
	
				if (idx >= 0)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(key.substring(0, idx));
	
					if (aPC.hasAssociations(cdo))
					{
						sb.append(StringUtil.joinToStringBuffer(aPC.getAssociationList(cdo), ", "));
					}
					else
					{
						sb.append("<undefined>");
					}
	
					sb.append(key.substring(idx + 7));
					sa = new SpecialAbility(sb.toString(), sa.getSADesc());
				}
	
				aList.add(sa);
			}
		}
	
		return aList;
	}

}
