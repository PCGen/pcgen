/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PCTemplate.java
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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;

public class TemplateSelect
{

	public static PCTemplate chooseTemplate(CDOMObject anOwner, List<PCTemplate> list,
			boolean forceChoice, PlayerCharacter aPC)
	{
		final List<PCTemplate> availableList = new ArrayList<PCTemplate>();
		for (PCTemplate pct : list)
		{
			if (pct.qualifies(aPC))
			{
				availableList.add(pct);
			}
		}
		if (availableList.size() == 1)
		{
			return availableList.get(0);
		}
		// If we are left without a choice, don't show the chooser.
		if (availableList.size() < 1)
		{
			return null;
		}
		final List<PCTemplate> selectedList = new ArrayList<PCTemplate>(1);
		String title = "Template Choice";
		if (anOwner != null)
		{
			title += " (" + anOwner.getDisplayName() + ")";
		}
		Globals.getChoiceFromList(title, availableList, selectedList, 1,
			forceChoice);
		if (selectedList.size() == 1)
		{
			return selectedList.get(0);
		}
	
		return null;
	}

}
