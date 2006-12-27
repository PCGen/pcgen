/*
 * InfoRace.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * Created on May 1, 2001, 5:57 PM
 * ReCreated on Feb 22, 2002 7:45 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.enumeration.Tab;

/**
 *  <code>InfoRace</code> creates a new tabbed panel
 *  with all the race and template information on it
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision$
 **/
public final class InfoRace extends TabContainer
{
	private static final Tab tab = Tab.RACE_MASTER;

	/**
	 * Constructor
	 * @param aPC The PC to display information for.
	 */
	public InfoRace(final PlayerCharacter aPC)
	{
		super(aPC);

		addSubTab(new InfoRaces(aPC));
		addSubTab(new InfoTemplates(aPC));
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getTabOrder()
	 */
	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(
			".Panel.Race.Order", tab.ordinal()); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#setTabOrder(int)
	 */
	public void setTabOrder(final int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Race.Order", order); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.tabs.TabContainer#getTab()
	 */
	@Override
	public Tab getTab()
	{
		return tab;
	}
}