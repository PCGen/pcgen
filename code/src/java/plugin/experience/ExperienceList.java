/*
 * Copyright 2003 (C) Devon Jones
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
 package plugin.experience;

import javax.swing.DefaultListModel;

import gmgen.plugin.Combatant;

/**
 * ExperienceList. This class holds all the characters that are to be displayed in a JList.
 */
class ExperienceList extends DefaultListModel
{

	/**
	 * Gets the average level for the party.
	 * @return the average party level.
	 */
	int averageCR()
	{
		float groupLevel = 0;
		int num = 0;



		for (int i = 0; i < size(); i++)
		{
			Combatant cbt = ((ExperienceListItem) get(i)).getCombatant();
			groupLevel += cbt.getCR();
			num++;
		}

		if (num == 0)
		{
			return 0;
		}

		return ((int) groupLevel) / num;
	}
}
