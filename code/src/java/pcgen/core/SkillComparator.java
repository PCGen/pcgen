/*
 * SkillComparator.java
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
 *
 * Created on December 25, 2003, 5:00 PM
 *
 * $Id$
 */
package pcgen.core;

import java.util.Comparator;

/**
 * <code>SkillComparator</code> is a comparator interface for sorting skills.
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public final class SkillComparator implements Comparator
{
	public static final int RESORT_NAME = 0;
	public static final int RESORT_TRAINED = 1;
	public static final boolean RESORT_ASCENDING = true;
	public static final boolean RESORT_DESCENDING = false;
	private boolean sortOrder = RESORT_ASCENDING;
	private int sort = RESORT_NAME;

	public SkillComparator(final int sort, final boolean sortOrder)
	{
		this.sort = sort;
		this.sortOrder = sortOrder;
	}

	// Comparator will be specific to Skill objects
	public int compare(final Object obj1, final Object obj2)
	{
		final Skill s1;
		final Skill s2;

		if ((sortOrder == RESORT_ASCENDING) || (sort == RESORT_TRAINED))
		{
			s1 = (Skill) obj1;
			s2 = (Skill) obj2;
		}
		else
		{
			s1 = (Skill) obj2;
			s2 = (Skill) obj1;
		}

		switch (sort)
		{
			case RESORT_TRAINED:

				if ((s1.getRank().floatValue() > 0.0f) && (s2.getRank().floatValue() <= 0.0f))
				{
					return ((sortOrder == RESORT_ASCENDING) ? (-1) : 1);
				}
				else if ((s1.getRank().floatValue() <= 0.0f) && (s2.getRank().floatValue() > 0.0f))
				{
					return ((sortOrder == RESORT_ASCENDING) ? 1 : (-1));
				}
				else
				{
					return s1.getDisplayName().compareToIgnoreCase(s2.getDisplayName());
				}

			case RESORT_NAME:default:
				return s1.getDisplayName().compareToIgnoreCase(s2.getDisplayName());
		}
	}
}
