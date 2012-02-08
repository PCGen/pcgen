/*
 * Copyright (c) Thomas Parker, 2012.
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
package pcgen.cdom.facet;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.QualifiedActor;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;

public class UserSpecialAbilityFacet extends
		AbstractQualifiedListFacet<SpecialAbility>
{

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	public List<SpecialAbility> getResolved(CharID id, Object source)
	{
		List<SpecialAbility> returnList = new ArrayList<SpecialAbility>();
		SAProcessor proc = new SAProcessor(trackingFacet.getPC(id), returnList);
		for (SpecialAbility sa : getQualifiedSet(id, source))
		{
			proc.act(sa, source);
		}
		return returnList;
	}

	public List<SpecialAbility> getAllResolved(CharID id)
	{
		final List<SpecialAbility> returnList = new ArrayList<SpecialAbility>();
		actOnQualifiedSet(id, new SAProcessor(trackingFacet.getPC(id),
			returnList));
		return returnList;
	}

	private final class SAProcessor implements QualifiedActor<SpecialAbility>
	{
		private final PlayerCharacter pc;
		private final List<SpecialAbility> returnList;

		private SAProcessor(PlayerCharacter pc, List<SpecialAbility> returnList)
		{
			this.pc = pc;
			this.returnList = returnList;
		}

		@Override
		public void act(SpecialAbility sa, Object source)
		{
			final String key = sa.getKeyName();
			final int idx = key.indexOf("%CHOICE");

			if (idx >= 0)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(key.substring(0, idx));

				if (pc.hasAssociations(source))
				{
					sb.append(StringUtil.joinToStringBuffer(
						pc.getAssociationList(source), ", "));
				}
				else
				{
					sb.append("<undefined>");
				}

				sb.append(key.substring(idx + 7));
				sa = new SpecialAbility(sb.toString(), sa.getSADesc());
			}

			returnList.add(sa);
		}
	}

}
