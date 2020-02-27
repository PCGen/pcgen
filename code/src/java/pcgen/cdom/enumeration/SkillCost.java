/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import pcgen.core.SettingsHandler;

/**
 * Holds the Skill Costs in type safe form.
 * 
 * The actual point cost of the SkillCost can be acquired through the getCost()
 * method. A numerical cost of -1 represents that a Skill cannot be taken.
 */
public enum SkillCost
{

	CLASS
	{
		@Override
		public int getCost()
		{
			return SettingsHandler.getGameAsProperty().get().getSkillCost_Class();
		}
	},

	CROSS_CLASS
	{
		@Override
		public int getCost()
		{
			return SettingsHandler.getGameAsProperty().get().getSkillCost_CrossClass();
		}
	},

	EXCLUSIVE
	{
		@Override
		public int getCost()
		{
			return SettingsHandler.getGameAsProperty().get().getSkillCost_Exclusive();
		}
	};

	public abstract int getCost();
}
