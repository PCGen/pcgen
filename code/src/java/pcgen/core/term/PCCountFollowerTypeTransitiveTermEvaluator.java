/**
 * pcgen.core.term.PCCountFollowerTypeTransitiveTermEvaluator.java
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
 * Created 07-Aug-2008 00:47:38
 *
 * Current Ver: $Revision:$
 * Last Editor: $Author:$
 * Last Edited: $Date:$
 *
 */

package pcgen.core.term;

import java.util.List;
import java.util.ArrayList;

import pcgen.core.PlayerCharacter;
import pcgen.core.Globals;
import pcgen.core.character.Follower;

public class PCCountFollowerTypeTransitiveTermEvaluator
		extends BasePCTermEvaluator implements TermEvaluator
{

	private final String type;
	private final int index;
	private final String newCount;

	public PCCountFollowerTypeTransitiveTermEvaluator(
			String originalText,
			String type,
			int index,
			String newCount)
	{
		this.originalText = originalText;
		this.type         = type;
		this.index        = index;
		this.newCount     = newCount;
	}

	@Override
	public Float resolve(PlayerCharacter apc)
	{
		if (apc.hasFollowers())
		{
			final List<Follower> aList = new ArrayList<Follower>();

			for ( Follower follower : apc.getFollowerList() )
			{
				if (follower.getType().getKeyName().equalsIgnoreCase(type))
				{
					aList.add(follower);
				}
			}

			if (index < aList.size())
			{
				final Follower follower = aList.get(index);

				for ( PlayerCharacter pc : Globals.getPCList() )
				{
					if (follower.getFileName().equals(pc.getFileName())
							&& follower.getName().equals(pc.getName()))
					{
						return pc.getVariableValue(newCount, "");
					}
				}
			}
		}

		return 0f;
	}

	@Override
	public boolean isSourceDependant()
	{
		return false;
	}

	public boolean isStatic()
	{
		return false;
	}
}
