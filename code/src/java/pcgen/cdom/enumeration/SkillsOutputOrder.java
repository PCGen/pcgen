/*
 * SkillsOutputOrder.java
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 */
package pcgen.cdom.enumeration;

import pcgen.core.PlayerCharacter;
import pcgen.core.SkillComparator;

/**
 * SkillsOutputOrder defines the possible orders of skill in output sheets. 
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public enum SkillsOutputOrder
{
	NAME_ASC {
		@Override
		public SkillComparator getComparator(PlayerCharacter pc)
		{
			return new SkillComparator(pc, SkillComparator.RESORT_NAME,
				SkillComparator.RESORT_ASCENDING);
		}
	},

	NAME_DSC {
		@Override
		public SkillComparator getComparator(PlayerCharacter pc)
		{
			return new SkillComparator(pc, SkillComparator.RESORT_NAME,
				SkillComparator.RESORT_DESCENDING);
		}
	},

	TRAINED_ASC {
		@Override
		public SkillComparator getComparator(PlayerCharacter pc)
		{
			return new SkillComparator(pc, SkillComparator.RESORT_TRAINED,
				SkillComparator.RESORT_ASCENDING);
		}
	},

	TRAINED_DSC {
		@Override
		public SkillComparator getComparator(PlayerCharacter pc)
		{
			return new SkillComparator(pc, SkillComparator.RESORT_TRAINED,
				SkillComparator.RESORT_DESCENDING);
		}
	},

	MANUAL {
		@Override
		public SkillComparator getComparator(PlayerCharacter pc)
		{
			return null;
		}
	};

	public abstract SkillComparator getComparator(PlayerCharacter pc);
}
