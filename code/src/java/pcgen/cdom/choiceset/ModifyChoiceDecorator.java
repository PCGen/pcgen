/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.choiceset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;

public class ModifyChoiceDecorator implements PrimitiveChoiceSet<Ability>
{

	private final PrimitiveChoiceSet<Ability> set;

	public <U> ModifyChoiceDecorator(PrimitiveChoiceSet<Ability> underlyingSet)
	{
		set = underlyingSet;
	}

	public Class<? super Ability> getChoiceClass()
	{
		return set.getChoiceClass();
	}

	public String getLSTformat()
	{
		return set.getLSTformat();
	}

	public Set<Ability> getSet(PlayerCharacter pc)
	{
		Set<Ability> ab = set.getSet(pc);
		List<Ability> pcfeats = pc.aggregateFeatList();
		Set<Ability> returnSet = new HashSet<Ability>();
		for (Ability a : pcfeats)
		{
			if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED) && ab.contains(a))
			{
				returnSet.add(a);
			}
		}
		return returnSet;
	}

}
