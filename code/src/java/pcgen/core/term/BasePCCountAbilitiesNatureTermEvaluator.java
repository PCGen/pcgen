/**
 * pcgen.core.term.PCCountAbilitiesNatureBaseEvaluator.java
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 09-Aug-2008 19:06:01
 *
 *
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;

public abstract class BasePCCountAbilitiesNatureTermEvaluator
		extends BasePCCountAbilitiesTermEvaluator
{
	public BasePCCountAbilitiesNatureTermEvaluator()
	{
	}

	@Override
	public Float resolve(PlayerCharacter pc)
	{
		return countVisibleAbilities(pc, getAbilities(pc), visible, hidden);
	}
}
