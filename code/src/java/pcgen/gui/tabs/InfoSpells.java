/*
 * InfoSpells.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Written by Bryan McRoberts <merton_monk@users.sourceforge.net>,
 * Re-written by Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on April 21, 2001, 2:15 PM
 * Re-created on April 1st, 2002, 2:15 am
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import pcgen.core.GameMode;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.Filterable;
import pcgen.gui.filter.PObjectFilter;
import pcgen.gui.tabs.spells.InfoKnownSpells;
import pcgen.gui.tabs.spells.InfoPreparedSpells;
import pcgen.gui.tabs.spells.InfoSpellBooks;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 *  <code>InfoSpells</code> creates a new tabbed panel.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>, Jayme Cox <jaymecox@netscape.net>, James Dempsey <jdempsey@users.sourceforge.net>
 * @version    $Revision$
 */
public class InfoSpells extends JTabbedPane implements CharacterInfoTab,
		Filterable
{
	static final long serialVersionUID = 755097384157285101L;

	private static final Tab tab = Tab.SPELLS;

	private static final int KNOWN_INDEX = 0;
	private static final int PREPARED_INDEX = 1;
	private static final int SPELLBOOKS_INDEX = 2;

	private InfoKnownSpells known;
	private InfoPreparedSpells prepared;
	private InfoSpellBooks spellbooks;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 *  Constructor for the InfoSpells object
	 * @param pc
	 *
	 */
	public InfoSpells(PlayerCharacter pc)
	{
		this.pc = pc;
		known = new InfoKnownSpells(pc);
		prepared = new InfoPreparedSpells(pc);
		spellbooks = new InfoSpellBooks(pc);

		// do not remove this as we will use the component's name
		// to save component specific settings
		setName(tab.toString());

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				//	initActionListeners();
			}
		});
	}

	public void setPc(PlayerCharacter pc)
	{
		if (this.pc != pc || pc.getSerial() > serial)
		{
			this.pc = pc;
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(
			".Panel.Spells.Order", tab.ordinal()); //$NON-NLS-1$
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Spells.Order", order); //$NON-NLS-1$
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(tab);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();
		toDoList.addAll(known.getToDos());
		toDoList.addAll(prepared.getToDos());
		toDoList.addAll(spellbooks.getToDos());
		return toDoList;
	}

	public void refresh()
	{
		if (pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public void forceRefresh()
	{
		if (readyForRefresh)
		{
			setNeedsUpdate(true);

			known.setPc(pc);
			prepared.setPc(pc);
			spellbooks.setPc(pc);
		}
		else
		{
			serial = 0;
		}
	}

	/**
	 * Sets whether or not this tab (and its sub tabs) needs an update
	 * @param b
	 */
	public void setNeedsUpdate(boolean b)
	{
		if (known == null)
		{
			return;
		}

		known.setNeedsUpdate(b);
		prepared.setNeedsUpdate(b);
		spellbooks.setNeedsUpdate(b);
	}

	public JComponent getView()
	{
		return this;
	}

	/**
	 * specifies whether the "match any" option should be available
	 * @return true
	 **/
	public final boolean isMatchAnyEnabled()
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			return known.isMatchAnyEnabled();
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			return prepared.isMatchAnyEnabled();
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			return spellbooks.isMatchAnyEnabled();
		}

		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return true
	 **/
	public final boolean isNegateEnabled()
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			return known.isNegateEnabled();
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			return prepared.isNegateEnabled();
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			return spellbooks.isNegateEnabled();
		}

		return true;
	}

	/**
	 * specifies the filter selection mode
	 * @return FilterConstants.DISABLED_MODE = -2
	 **/
	public final int getSelectionMode()
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			return known.getSelectionMode();
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			return prepared.getSelectionMode();
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			return spellbooks.getSelectionMode();
		}

		return FilterConstants.DISABLED_MODE;
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void initializeFilters()
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			known.initializeFilters();
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			prepared.initializeFilters();
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			spellbooks.initializeFilters();
		}
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void refreshFiltering()
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			known.refreshFiltering();
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			prepared.refreshFiltering();
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			spellbooks.refreshFiltering();
		}
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return removed filters
	 **/
	public List<PObjectFilter> getRemovedFilters()
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			return known.getRemovedFilters();
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			return prepared.getRemovedFilters();
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			return spellbooks.getRemovedFilters();
		}

		return null;
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return selected filters
	 **/
	public List<PObjectFilter> getSelectedFilters()
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			return known.getSelectedFilters();
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			return prepared.getSelectedFilters();
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			return spellbooks.getSelectedFilters();
		}

		return null;
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return filter mode
	 **/
	public int getFilterMode()
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			return known.getFilterMode();
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			return prepared.getFilterMode();
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			return spellbooks.getFilterMode();
		}

		return FilterConstants.MATCH_ALL;
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 **/
	private void initComponents()
	{
		readyForRefresh = true;
		add(known, KNOWN_INDEX);
		setTitleAt(KNOWN_INDEX, LanguageBundle
			.getString("in_Info" + known.getName())); //$NON-NLS-1$
		add(prepared, PREPARED_INDEX);
		setTitleAt(PREPARED_INDEX, LanguageBundle
			.getString("in_Info" + prepared.getName())); //$NON-NLS-1$
		add(spellbooks, SPELLBOOKS_INDEX);
		setTitleAt(SPELLBOOKS_INDEX, LanguageBundle
			.getString("in_Info" + spellbooks.getName())); //$NON-NLS-1$

		addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				refresh();
			}
		});
	}

	/**
	 * @see pcgen.gui.filter.Filterable#getAvailableFilters()
	 */
	public List<PObjectFilter> getAvailableFilters()
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			return known.getAvailableFilters();
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			return prepared.getAvailableFilters();
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			return spellbooks.getAvailableFilters();
		}

		return null;
	}

	/**
	 * @see pcgen.gui.filter.Filterable#setFilterMode(int)
	 */
	public void setFilterMode(int mode)
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			known.setFilterMode(mode);
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			prepared.setFilterMode(mode);
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			spellbooks.setFilterMode(mode);
		}
	}

	/**
	 * @see pcgen.gui.filter.Filterable#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (getSelectedIndex() == KNOWN_INDEX)
		{
			return known.accept(aPC, pObject);
		}
		else if (getSelectedIndex() == PREPARED_INDEX)
		{
			return prepared.accept(aPC, pObject);
		}
		else if (getSelectedIndex() == SPELLBOOKS_INDEX)
		{
			return spellbooks.accept(aPC, pObject);
		}
		return false;
	}
}