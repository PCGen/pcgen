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
package plugin.encounter;

import javax.swing.DefaultListModel;

import pcgen.core.Globals;
import pcgen.core.Race;
import pcgen.core.analysis.RaceUtilities;

public class RaceModel extends DefaultListModel
{
	/**
	 * Constructor for RaceModel.
	 */
	RaceModel()
	{
	}

	/**
	 * Performs an update of the RaceModel.
	 */
	public void update()
	{
		clear();

		for (final Race race : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Race.class))
		{
			if (!contains(race.toString()))
			{
				this.addElement(race.toString());
				this.removeElement(RaceUtilities.getUnselectedRace().toString());
			}
		}
	}
}
