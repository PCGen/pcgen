/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Iterator;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;

public final class BonusActivation
{

	private BonusActivation()
	{
	}

	public static void deactivateBonuses(CDOMObject po, PlayerCharacter aPC)
	{
		for (BonusObj bonus : po.getRawBonusList(aPC))
		{
			aPC.setApplied(bonus, false);
		}
	}

	public static void activateBonuses(CDOMObject po, PlayerCharacter aPC)
	{
		for (Iterator<BonusObj> ab = po.getRawBonusList(aPC).iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = ab.next();
			aPC.setApplied(aBonus, aBonus.qualifies(aPC, po));
		}
	}

}
