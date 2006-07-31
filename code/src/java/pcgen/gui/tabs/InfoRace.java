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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import pcgen.core.GameMode;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.Filterable;
import pcgen.gui.filter.PObjectFilter; 
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

/**
 *  <code>InfoRace</code> creates a new tabbed panel
 *  with all the race and template information on it
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision$
 **/
public final class InfoRace extends JTabbedPane implements Filterable, CharacterInfoTab 
{
	private static final Tab tab = Tab.RACE_MASTER;
	
	private static final int RACES_INDEX = 0;
	private static final int TEMPLATES_INDEX = 1;

	private InfoRaces races;
	private InfoTemplates templates;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoRace(PlayerCharacter pc)
	{
		this.pc = pc;
		races = new InfoRaces(pc);
		templates = new InfoTemplates(pc);
		// do not remove this
		// we will use the component's name to save component specific settings
		setName(tab.toString());

		initComponents();
		initActionListeners();
	}

	private void initActionListeners() 
	{
		// TODO Do Nothing
	}

	private void initComponents() 
	{
		readyForRefresh = true;
		add(races, RACES_INDEX);
		setTitleAt(RACES_INDEX, PropertyFactory.getString("in_Info" + races.getName()));
		add(templates, TEMPLATES_INDEX);
		setTitleAt(TEMPLATES_INDEX, PropertyFactory.getString("in_Info" + templates.getName()));

		addFocusListener(new FocusAdapter()	{
				public void focusGained(FocusEvent evt) {
					refresh();
				}
			});
	}

	public List<PObjectFilter> getAvailableFilters() 
	{
		if (getSelectedIndex() == RACES_INDEX) {
			return races.getAvailableFilters();
		}
		else if (getSelectedIndex() == TEMPLATES_INDEX) {
			return templates.getAvailableFilters();
		}

		return null;
	}

	public void setFilterMode(int mode) {
		if (getSelectedIndex() == RACES_INDEX) {
			races.setFilterMode(mode);
		}
		else if (getSelectedIndex() == TEMPLATES_INDEX) {
			templates.setFilterMode(mode);
		}
	}

	public int getFilterMode() {
		if (getSelectedIndex() == RACES_INDEX) {
			return races.getFilterMode();
		}
		else if (getSelectedIndex() == TEMPLATES_INDEX) {
			return templates.getFilterMode();
		}

		return FilterConstants.MATCH_ALL;
	}

	public boolean isMatchAnyEnabled() {
		if (getSelectedIndex() == RACES_INDEX) {
			return races.isMatchAnyEnabled();
		}
		else if (getSelectedIndex() == TEMPLATES_INDEX) {
			return templates.isMatchAnyEnabled();
		}

		return true;
	}

	public boolean isNegateEnabled() {
		if (getSelectedIndex() == RACES_INDEX) {
			return races.isNegateEnabled();
		}
		else if (getSelectedIndex() == TEMPLATES_INDEX) {
			return templates.isNegateEnabled();
		}

		return true;
	}

	public List<PObjectFilter> getRemovedFilters() 
	{
		if (getSelectedIndex() == RACES_INDEX) 
		{
			return races.getRemovedFilters();
		}
		else if (getSelectedIndex() == TEMPLATES_INDEX) 
		{
			return templates.getRemovedFilters();
		}

		return null;
	}

	public List<PObjectFilter> getSelectedFilters() 
	{
		if (getSelectedIndex() == RACES_INDEX) 
		{
			return races.getSelectedFilters();
		}
		else if (getSelectedIndex() == RACES_INDEX) 
		{
			return templates.getSelectedFilters();
		}

		return null;
	}

	public int getSelectionMode() {
		if (getSelectedIndex() == RACES_INDEX) {
			return races.getSelectionMode();
		}
		else if (getSelectedIndex() == TEMPLATES_INDEX) {
			return templates.getSelectionMode();
		}

		return FilterConstants.DISABLED_MODE;
	}

	public void initializeFilters() {
		if (getSelectedIndex() == RACES_INDEX) {
			races.initializeFilters();
		}
		else if (getSelectedIndex() == TEMPLATES_INDEX) {
			templates.initializeFilters();
		}
	}

	public void refreshFiltering() {
		if (getSelectedIndex() == RACES_INDEX) {
			races.refreshFiltering();
		}
		else if (getSelectedIndex() == TEMPLATES_INDEX) {
			templates.refreshFiltering();
		}
	}

	public void setPc(PlayerCharacter pc) {
		if(this.pc != pc || pc.getSerial() > serial) {
			this.pc = pc;
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public PlayerCharacter getPc() {
		return pc;
	}

	public int getTabOrder() {
		return SettingsHandler.getPCGenOption(".Panel.Race.Order", tab.ordinal());
	}

	public void setTabOrder(int order) {
		SettingsHandler.setPCGenOption(".Panel.Race.Order", order);
	}

	public String getTabName() {
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	public boolean isShown() {
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(tab);
	}

	public void refresh() {
		if(pc.getSerial() > serial) {
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public void forceRefresh() {
		if(readyForRefresh) {
			setNeedsUpdate(true);

			races.setPc(pc);
			templates.setPc(pc);
		}
		else {
			serial = 0;
		}
	}

	/**
	 * Sets the needsUpdate flag for Race and Tempalte tabs
	 * @param b
	 */
	public static void setNeedsUpdate(boolean b) {
		InfoRaces.setNeedsUpdate(b);
		InfoTemplates.setNeedsUpdate(b);
	}

	public JComponent getView() {
		return this;
	}

	public List<String> getToDos() 
	{
		List<String> toDoList = new ArrayList<String>();
		toDoList.addAll(races.getToDos());
		toDoList.addAll(templates.getToDos());
		return toDoList;
	}
}