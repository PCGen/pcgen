/*
 * Copyright 2003 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import java.util.Comparator;

import pcgen.core.analysis.SkillRankControl;

/**
 * {@code SkillComparator} is a comparator interface for sorting skills.
 */
public final class SkillComparator implements Comparator<Skill>
{
	public static final int RESORT_NAME = 0;
	public static final int RESORT_TRAINED = 1;
	public static final boolean RESORT_ASCENDING = true;
	public static final boolean RESORT_DESCENDING = false;
	private boolean sortOrder;
	private int sort;
	private final PlayerCharacter pc;

	public SkillComparator(PlayerCharacter aPC, final int sort, final boolean sortOrder)
	{
		this.sort = sort;
		this.sortOrder = sortOrder;
		pc = aPC;
	}

	// Comparator will be specific to Skill objects
	@Override
	public int compare(final Skill obj1, final Skill obj2)
	{
		final Skill s1;
		final Skill s2;

		if ((sortOrder == RESORT_ASCENDING) || (sort == RESORT_TRAINED))
		{
			s1 = obj1;
			s2 = obj2;
		}
		else
		{
			s1 = obj2;
			s2 = obj1;
		}

		switch (sort)
		{
			case RESORT_TRAINED: {

				float r1 = SkillRankControl.getTotalRank(pc, s1);
				float r2 = SkillRankControl.getTotalRank(pc, s2);
				if ((r1 > 0.0f) && (r2 <= 0.0f))
				{
					return ((sortOrder == RESORT_ASCENDING) ? (-1) : 1);
				}
				else if ((r1 <= 0.0f) && (r2 > 0.0f))
				{
					return ((sortOrder == RESORT_ASCENDING) ? 1 : (-1));
				}
				else
				{
					return s1.getOutputName().compareToIgnoreCase(s2.getOutputName());
				}
			}
			case RESORT_NAME:
			default:
				return s1.getOutputName().compareToIgnoreCase(s2.getOutputName());
		}
	}
}
