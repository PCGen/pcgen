/*
 * InfoAbilities.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE  See the GNU
 * Lesser General Public License for more details
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision$
 * Last Editor: $Author: zaister $
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.enumeration.Tab;

/**
 * This is a container tab that contains all the sub tabs for ability 
 * categories.
 *
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class InfoAbilities extends TabContainer
{
	private static final Tab tab = Tab.ABILITIES;

	/**
	 * Constructor
	 * 
	 * @param aPC The PC to display information for.
	 */
	public InfoAbilities(final PlayerCharacter aPC)
	{
		super(aPC);

		Map<String, AbilityCategory> acTabs = new HashMap<String, AbilityCategory>();
		
		final Collection<AbilityCategory> cats =
				SettingsHandler.getGame().getAllAbilityCategories();
		for (AbilityCategory cat : cats)
		{
			if (cat.isVisible())
			{
				if (acTabs.get(cat.getDisplayLocation()) == null)
				{
					acTabs.put(cat.getDisplayLocation(), cat);
					addSubTab(new InfoAbility(aPC, cat));
				}
			}
		}
	}

	/**
	 * @see pcgen.gui.tabs.TabContainer#getTab()
	 */
	@Override
	protected Tab getTab()
	{
		return tab;
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getTabOrder()
	 */
	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(
			".Panel.Abilities.Order", tab.ordinal()); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#setTabOrder(int)
	 */
	public void setTabOrder(final int anOrder)
	{
		SettingsHandler.setPCGenOption(".Panel.Abilities.Order", anOrder); //$NON-NLS-1$
	}

}
