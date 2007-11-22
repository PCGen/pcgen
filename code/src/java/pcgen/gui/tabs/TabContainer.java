/*
 * TabContainer.java
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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import pcgen.core.GameMode;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.Filterable;
import pcgen.gui.filter.PObjectFilter;
import pcgen.util.enumeration.Tab;

/**
 * This is an abstract class that implements support for containing other
 * character info tabs.
 * 
 * <p>The class simply delegates all the required methods to the currently
 * active tab.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11
 */
public abstract class TabContainer extends JTabbedPane implements Filterable,
		CharacterInfoTab
{
	// We keep track of PC changes at the higher level.
	private PlayerCharacter thePC = null;
	private int theSerial = 0;
	private boolean theReadyForRefreshFlag = false;

	private List<BaseCharacterInfoTab> theSubTabs =
			new ArrayList<BaseCharacterInfoTab>();

	/**
	 * Default constructor.
	 * 
	 * @param aPC The PC to display information for.
	 */
	public TabContainer(final PlayerCharacter aPC)
	{
		thePC = aPC;
		setName(getTab().toString());

		addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(@SuppressWarnings("unused")
			FocusEvent evt)
			{
				refresh();
			}
		});
	}

	/**
	 * Adds a subtab to be managed by this container.
	 * 
	 * @param aTab The subtab to add.
	 */
	public void addSubTab(final BaseCharacterInfoTab aTab)
	{
		theReadyForRefreshFlag = true;
		theSubTabs.add(aTab);
		add(aTab);
		setTitleAt(getTabCount() - 1, aTab.getTabName());
	}

	/**
	 *	Remove all existing sub tabs ready for the new set. 
	 */
	protected void clearSubTabs()
	{
		for (BaseCharacterInfoTab subtab : theSubTabs)
		{
			remove(subtab);
		}
		theSubTabs.clear();
	}
	
	/**
	 * @see pcgen.gui.filter.Filterable#getAvailableFilters()
	 */
	public List<PObjectFilter> getAvailableFilters()
	{
		final int ind = getSelectedIndex();
		if (ind < 0 || ind >= theSubTabs.size())
		{
			return null;
		}
		return theSubTabs.get(ind).getAvailableFilters();
	}

	/**
	 * @see pcgen.gui.filter.Filterable#getFilterMode()
	 */
	public int getFilterMode()
	{
		final int ind = getSelectedIndex();
		if (ind < 0 || ind >= theSubTabs.size())
		{
			return FilterConstants.MATCH_ALL;
		}
		return theSubTabs.get(ind).getFilterMode();
	}

	/**
	 * @see pcgen.gui.filter.Filterable#getRemovedFilters()
	 */
	public List<PObjectFilter> getRemovedFilters()
	{
		final int ind = getSelectedIndex();
		if (ind < 0 || ind >= theSubTabs.size())
		{
			return null;
		}
		return theSubTabs.get(ind).getRemovedFilters();
	}

	/**
	 * @see pcgen.gui.filter.Filterable#getSelectedFilters()
	 */
	public List<PObjectFilter> getSelectedFilters()
	{
		final int ind = getSelectedIndex();
		if (ind < 0 || ind >= theSubTabs.size())
		{
			return null;
		}
		return theSubTabs.get(ind).getSelectedFilters();
	}

	/**
	 * @see pcgen.gui.filter.Filterable#getSelectionMode()
	 */
	public int getSelectionMode()
	{
		final int ind = getSelectedIndex();
		if (ind < 0 || ind >= theSubTabs.size())
		{
			return FilterConstants.DISABLED_MODE;
		}
		return theSubTabs.get(ind).getSelectionMode();
	}

	/**
	 * @see pcgen.gui.filter.Filterable#initializeFilters()
	 */
	public void initializeFilters()
	{
		final int ind = getSelectedIndex();
		if (ind >= 0 && ind < theSubTabs.size())
		{
			theSubTabs.get(ind).initializeFilters();
		}
	}

	/**
	 * @see pcgen.gui.filter.Filterable#isMatchAnyEnabled()
	 */
	public boolean isMatchAnyEnabled()
	{
		final int ind = getSelectedIndex();
		if (ind < 0 || ind >= theSubTabs.size())
		{
			return true;
		}
		return theSubTabs.get(ind).isMatchAnyEnabled();
	}

	/**
	 * @see pcgen.gui.filter.Filterable#isNegateEnabled()
	 */
	public boolean isNegateEnabled()
	{
		final int ind = getSelectedIndex();
		if (ind < 0 || ind >= theSubTabs.size())
		{
			return true;
		}
		return theSubTabs.get(ind).isNegateEnabled();
	}

	/**
	 * @see pcgen.gui.filter.Filterable#refreshFiltering()
	 */
	public void refreshFiltering()
	{
		final int ind = getSelectedIndex();
		if (ind >= 0 && ind < theSubTabs.size())
		{
			theSubTabs.get(ind).refreshFiltering();
		}
	}

	/**
	 * @see pcgen.gui.filter.Filterable#setFilterMode(int)
	 */
	public void setFilterMode(int aMode)
	{
		final int ind = getSelectedIndex();
		if (ind >= 0 && ind < theSubTabs.size())
		{
			theSubTabs.get(ind).setFilterMode(aMode);
		}
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#forceRefresh()
	 */
	public void forceRefresh()
	{
		if (theReadyForRefreshFlag == true)
		{
			setNeedsUpdate(true);

			for (final BaseCharacterInfoTab tab : theSubTabs)
			{
				tab.setPc(thePC);
			}
		}
		else
		{
			theSerial = 0;
		}
	}

	/**
	 * Sets the needsUpdate flag for Race and Tempalte tabs
	 * @param update <tt>true</tt> to mark all the subtabs dirty.
	 */
	public void setNeedsUpdate(final boolean update)
	{
		for (final BaseCharacterInfoTab tab : theSubTabs)
		{
			tab.setNeedsUpdate(update);
		}
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getPc()
	 */
	public PlayerCharacter getPc()
	{
		return thePC;
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getToDos()
	 */
	public List<String> getToDos()
	{
		final List<String> ret = new ArrayList<String>();
		for (final BaseCharacterInfoTab tab : theSubTabs)
		{
			ret.addAll(tab.getToDos());
		}
		return ret;
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getView()
	 */
	public JComponent getView()
	{
		return this;
	}

	/**
	 * Returns the Tab enum value associated with this tab.
	 * 
	 * @return A <tt>Tab</tt> value.
	 */
	protected abstract Tab getTab();

	/**
	 * @see pcgen.gui.CharacterInfoTab#isShown()
	 */
	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(getTab());
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#refresh()
	 */
	public void refresh()
	{
		if (thePC.getSerial() > theSerial)
		{
			theSerial = thePC.getSerial();
			forceRefresh();
		}
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#setPc(pcgen.core.PlayerCharacter)
	 */
	public void setPc(PlayerCharacter aPC)
	{
		if (thePC != aPC || aPC.getSerial() > theSerial)
		{
			thePC = aPC;
			theSerial = thePC.getSerial();
			forceRefresh();
		}
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
	 * @see pcgen.gui.filter.Filterable#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	public boolean accept(final PlayerCharacter aPC, final PObject pObject)
	{
		final int ind = getSelectedIndex();
		if (ind < 0 || ind >= theSubTabs.size())
		{
			return true;
		}
		return theSubTabs.get(ind).accept(aPC, pObject);
	}
}
