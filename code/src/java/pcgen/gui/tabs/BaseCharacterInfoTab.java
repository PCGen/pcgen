/*
 * BaseCharacterInfoTab.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.gui.tabs;

import java.util.List;

import javax.swing.JComponent;

import pcgen.core.GameMode;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.util.enumeration.Tab;

/**
 * An abstract class that implements common behaviour for all CharacterInfoTabs.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public abstract class BaseCharacterInfoTab extends FilterAdapterPanel implements
		CharacterInfoTab
{
	private PlayerCharacter thePC = null;
	private int serial = 0;
	//	private boolean readyForRefresh = false;
	private boolean needsUpdate = true;

	/**
	 * Default constructor.
	 * 
	 * <p>Sets the PC being displayed on this tab and the name of the tab.
	 * 
	 * @param aPC The PC to display info for.
	 */
	public BaseCharacterInfoTab(final PlayerCharacter aPC)
	{
		thePC = aPC;
		setName(getTab().toString());
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#forceRefresh()
	 */
	public void forceRefresh()
	{
		needsUpdate = true;
		updateCharacterInfo();
	}

	/**
	 * Marks this tab as dirty so that the next time a refresh is done the
	 * tab will be updated.
	 * 
	 * @param flag <tt>true</tt> to mark the tab as needing an update.
	 */
	public final void setNeedsUpdate(final boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * Checks if this tab needs to be updated.
	 * 
	 * @return <tt>true</tt> if the tab needs to be updated.
	 */
	public final boolean needsUpdate()
	{
		return needsUpdate;
	}

	/**
	 * This method is called when the PC being displayed on the tab has changed.
	 */
	protected abstract void updateCharacterInfo();

	/**
	 * @see pcgen.gui.CharacterInfoTab#getPc()
	 */
	public PlayerCharacter getPc()
	{
		return thePC;
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getTabName()
	 */
	public String getTabName()
	{
		final GameMode game = SettingsHandler.getGame();
		return game.getTabName(getTab());
	}

	/**
	 * Return the <tt>Tab</tt> enum for this tab.
	 * 
	 * @return A <tt>Tab</tt> enum value.
	 */
	protected abstract Tab getTab();

	/**
	 * @see pcgen.gui.CharacterInfoTab#getTabOrder()
	 */
	public abstract int getTabOrder();

	/**
	 * @see pcgen.gui.CharacterInfoTab#getToDos()
	 */
	public abstract List<String> getToDos();

	/**
	 * @see pcgen.gui.CharacterInfoTab#getView()
	 */
	public JComponent getView()
	{
		return this;
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#isShown()
	 */
	public boolean isShown()
	{
		final GameMode game = SettingsHandler.getGame();
		return game.getTabShown(getTab());
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#refresh()
	 */
	public void refresh()
	{
		if (thePC.getSerial() > serial)
		{
			serial = thePC.getSerial();
			forceRefresh();
		}
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#setPc(pcgen.core.PlayerCharacter)
	 */
	public void setPc(final PlayerCharacter aPC)
	{
		if (thePC != aPC || thePC.getSerial() > serial)
		{
			thePC = aPC;
			serial = thePC.getSerial();
			forceRefresh();
		}
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#setTabOrder(int)
	 */
	public abstract void setTabOrder(int anOrder);
}
