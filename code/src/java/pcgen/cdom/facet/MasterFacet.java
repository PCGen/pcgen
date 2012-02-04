/*
 * Copyright (c) Thomas Parker, 2009.
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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.Follower;

/**
 * MasterFacet is a Facet that tracks the Master of a Player Character.
 */
public class MasterFacet extends AbstractItemFacet<Follower>
{
	private CompanionModFacet companionModFacet;

	public String getCopyMasterCheck(CharID id)
	{
		for (CompanionMod cMod : companionModFacet.getSet(id))
		{
			if (cMod.getType().equalsIgnoreCase(get(id).getType().getKeyName()))
			{
				if (cMod.get(StringKey.MASTER_CHECK_FORMULA) != null)
				{
					return cMod.get(StringKey.MASTER_CHECK_FORMULA);
				}
			}
		}

		return Constants.EMPTY_STRING;
	}

	public String getCopyMasterHP(CharID id)
	{
		for (CompanionMod cMod : companionModFacet.getSet(id))
		{
			if (cMod.getType().equalsIgnoreCase(get(id).getType().getKeyName()))
			{
				if (cMod.get(StringKey.MASTER_HP_FORMULA) != null)
				{
					return cMod.get(StringKey.MASTER_HP_FORMULA);
				}
			}
		}

		return Constants.EMPTY_STRING;
	}

	public String getCopyMasterBAB(CharID id)
	{
		for (CompanionMod cMod : companionModFacet.getSet(id))
		{
			/*
			 * TODO This is the "slow" method - proper solution here is to get
			 * TYPE in CompanionMod to be "special" and actually store a
			 * CompanionList object, not a String
			 */
			if (cMod.getType().equalsIgnoreCase(get(id).getType().getKeyName()))
			{
				String copyMasterBAB = cMod.get(StringKey.MASTER_BAB_FORMULA);
				if (copyMasterBAB != null)
				{
					return copyMasterBAB;
				}
			}
		}

		return Constants.EMPTY_STRING;
	}

	public boolean getUseMasterSkill(CharID id)
	{
		for (CompanionMod cMod : companionModFacet.getSet(id))
		{
			if (cMod.getType().equalsIgnoreCase(get(id).getType().getKeyName()))
			{
				if (cMod.getSafe(ObjectKey.USE_MASTER_SKILL))
				{
					return true;
				}
			}
		}

		return false;
	}

	public void setCompanionModFacet(CompanionModFacet companionModFacet)
	{
		this.companionModFacet = companionModFacet;
	}

}
