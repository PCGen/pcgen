/*
 * CharacterInfo.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.Filterable;
import pcgen.gui.tabs.*;
import pcgen.gui.utils.IconUtilitities;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;
import pcgen.util.SwingWorker;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentListener;
import java.util.*;

/**
 * <code>CharacterInfo</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class CharacterInfo extends JPanel {
	private List<Component> tempTabList;
	// Normal display has 10 items; if Abilities tab is added 11.  12 seems like
	// a safe number to avoid reallocation.
	private List<CharacterInfoTab> tabList = new ArrayList<CharacterInfoTab>(12);
	private BorderLayout borderLayout1 = new BorderLayout();
	private InfoDescription infoDesc;
	private InfoInventory infoInventory;
	private InfoSummary infoSummary;
	private InfoRace infoRace;
	private InfoSpecialAbilities infoSpecialAbilities;
	private InfoClasses infoClasses;
	private InfoSkills infoSkills;
	private InfoAbilities infoAbilities;
//	private InfoAbility infoFeats;
	private InfoDomain infoDomain;
	private InfoSpells infoSpells;
	private InfoCharacterSheet infoCharacterSheet;
	private JTabbedPane characterInfoTabbedPane = new JTabbedPane();
	private PlayerCharacter pc;
	private static Set<CharacterInfoTab> updateSet = new HashSet<CharacterInfoTab>();
	private boolean refresh = false;

	/**
	 * Constructor
	 * @param aPC
	 * @param aTempTabList
	 */
	public CharacterInfo(PlayerCharacter aPC, List<Component> aTempTabList) {
		this.pc = aPC;
		this.tempTabList = aTempTabList;
		infoDesc = new InfoDescription(pc);
		infoInventory = new InfoInventory(pc);
		infoSummary = new InfoSummary(pc);
		infoRace = new InfoRace(pc);
		infoSpecialAbilities = new InfoSpecialAbilities(pc);
		infoClasses = new InfoClasses(pc);
		infoSkills = new InfoSkills(pc);
		infoAbilities = new InfoAbilities(pc);
//		infoFeats = new InfoAbility(pc);
		infoDomain = new InfoDomain(pc);
		infoSpells = new InfoSpells(pc);
		infoCharacterSheet = new InfoCharacterSheet(pc);
		setName(""); //$NON-NLS-1$

		try {
			jbInit();
		} catch (Exception e) { //This is what jbInit actually throws...
			Logging.errorPrint(LanguageBundle.getString("in_CIerrorMess"), e);
		}
	}

	private void addListeners() {
		characterInfoTabbedPane.addChangeListener(new tabChangeListener());

		// since our filter icon changes if # of selected filters > 0
		// we register component listeners
		ComponentListener componentListener = PToolBar.getCurrentInstance().getComponentListener();
		addComponentListener(componentListener);
		for (int i = 0; i < tabList.size(); i++) {
			CharacterInfoTab tab = tabList.get(i);
			if (tab.isShown() && tab instanceof FilterAdapterPanel) {
				FilterAdapterPanel fpanel = (FilterAdapterPanel) tab;
				fpanel.addComponentListener(componentListener);
			}
		}
		infoInventory.getInfoGear().addComponentListener(componentListener);
	}

	/**
	 * Set refresh
	 * @param refresh
	 */
	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

	/**
	 * Returns the current active pane.
	 *
	 * @return Component
	 */
	public final Component getActivePane() {
		return characterInfoTabbedPane.getSelectedComponent();
	}

	/**
	 * this method provides access to the tabs for the Filter
	 *
	 * <br>
	 * author: Thomas Behr 09-02-02
	 *
	 * @return the selected tab as instance of Filterable
	 */
	public Filterable getSelectedFilterable() {
		Component c = characterInfoTabbedPane.getSelectedComponent();

		if (c instanceof Filterable) {
			return (Filterable) c;
		}

		return null;
	}

	/**
	 * return the currently selected Tab index
	 * @return selected index
	 */
	public int getSelectedIndex() {
		return characterInfoTabbedPane.getSelectedIndex();
	}

	/**
	 * Set the text name
	 * @param aString
	 */
	public void setTxtName(String aString) {
		infoSummary.getPcNameText().setText(aString);
		infoDesc.getTxtName().setText(aString);
		refreshToDosAsync();
	}

	/**
	 * return the Tab index that matches aString
	 * @param aString
	 * @return index of Tab
	 */
	public int indexOfTab(String aString) {
		int index = characterInfoTabbedPane.indexOfTab(aString);
		return index;
	}

	/**
	 * Refresh the names of the character tabs - they may be changed when 
	 * the game mode is changed. 
	 */
	public void refreshCharInfoTabs()
	{
		for (int i = 0; i < characterInfoTabbedPane.getTabCount(); i++)
		{
			CharacterInfoTab tab =
					(CharacterInfoTab) characterInfoTabbedPane
						.getComponentAt(i);
			characterInfoTabbedPane.setTitleAt(i, tab.getTabName());
		}
		infoAbilities().refreshAbilityCategories();
	}
	
	/**
	 * Get the info description
	 * @return info description
	 */
	public InfoDescription infoDesc() {
		return infoDesc;
	}

	/**
	 * Get the info inventory
	 * @return info inventory
	 */
	public InfoInventory infoInventory() {
		return infoInventory;
	}

	/**
	 * Get the info summary
	 * @return info summary
	 */
	public InfoSummary infoSummary() {
		return infoSummary;
	}

	/**
	 * Get the info race
	 * @return info race
	 */
	public InfoRace infoRace() {
		return infoRace;
	}

	/**
	 * Get the info special abilities
	 * @return info special abilities
	 */
	public InfoSpecialAbilities infoSpecialAbilities() {
		return infoSpecialAbilities;
	}

	/**
	 * Get the info classes
	 * @return info classes
	 */
	public InfoClasses infoClasses() {
		return infoClasses;
	}

	/**
	 * Get the character sheet panel
	 * @return character sheet panel
	 */
	public InfoCharacterSheet infoCharacterSheet() {
		return infoCharacterSheet;
	}

	/**
	 * Get the info skills
	 * @return info skills
	 */
	public InfoSkills infoSkills() {
		return infoSkills;
	}

	/**
	 * Get the info feats
	 * @return info feats
	 */
//	public InfoAbility infoFeats() {
//		return infoFeats;
//	}

	public InfoAbilities infoAbilities()
	{
		return infoAbilities;
	}
	/**
	 * Get the info domain
	 * @return info domain
	 */
	public InfoDomain infoDomain() {
		return infoDomain;
	}

	/**
	 * Get the info spells
	 * @return info spells
	 */
	public InfoSpells infoSpells() {
		return infoSpells;
	}

	/**
	 * Set the pane for update
	 * @param tab
	 */
	public void setPaneForUpdate(CharacterInfoTab tab) {
		updateSet.add(tab);
	}

	/**
	 * Start at the Summary (first) tab. This is useful for new characters.
	 */
	public void resetToSummaryTab() {
		characterInfoTabbedPane.setSelectedIndex(0);
	}

	/**
	 * update/restore filter settings from globally saved settings
	 *
	 * <br>
	 * author: Thomas Behr 24-02-02, 07-03-02
	 *
	 * @param filterableName
	 *          the name of the Filterable; <br>
	 *          if <code>null</code> then filters for all Filterables will be
	 *          updated, i.e. {@link #restoreAllFilterSettings}will be called
	 */
	public void restoreFilterSettings(String filterableName) {
		if (filterableName == null) {
			restoreAllFilterSettings();
			return;
		}

		Component c;

		for (int i = 0; i < characterInfoTabbedPane.getTabCount(); i++) {
			c = characterInfoTabbedPane.getComponentAt(i);

			if (c instanceof Filterable) {
				if ((c.getName() != null) && c.getName().equals(filterableName)) {
					FilterFactory.restoreFilterSettings((Filterable) c);
				}
			}
		}
	}

	/**
	 * call this method prior to closing the tab it will store the most recent
	 * filter settings
	 *
	 * <br>
	 * author: Thomas Behr 18-02-02
	 */
	public void storeFilterSettings() {
		for (int i = 0; i < tabList.size(); i++) {
			CharacterInfoTab tab = tabList.get(i);
			if (tab.isShown() && tab instanceof FilterAdapterPanel) {
				FilterAdapterPanel fpanel = (FilterAdapterPanel) tab;
				SettingsHandler.storeFilterSettings(fpanel);
			}
		}
		SettingsHandler.storeFilterSettings(infoInventory.getInfoGear());
	}

	/**
	 * Add a tab
	 * @param tab
	 */
	public void addTab(CharacterInfoTab tab) {
		if (tab.isShown()) {
			characterInfoTabbedPane.add(tab.getView(), tab.getTabName());
		}
		tabList.add(tab);
	}

	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		characterInfoTabbedPane.setPreferredSize(new Dimension(550, 350));
		this.setMinimumSize(new Dimension(550, 350));
		this.setPreferredSize(new Dimension(550, 350));
		this.add(characterInfoTabbedPane, BorderLayout.CENTER);

		characterInfoTabbedPane.setTabPlacement(SettingsHandler.getChaTabPlacement());

		addTab(infoSummary());
		addTab(infoRace());
		addTab(infoSpecialAbilities());
		addTab(infoClasses());
		addTab(infoSkills());
//		addTab(infoFeats());
		addTab(infoAbilities());
		addTab(infoDomain());
		addTab(infoSpells());
		addTab(infoInventory());
		addTab(infoDesc());
		addTab(infoCharacterSheet);
		for (int i = 0; i < tempTabList.size(); i++) {
			CharacterInfoTab tab = (CharacterInfoTab) tempTabList.get(i);
			addTab(tab);
		}

		addListeners();
	}

	/**
	 * update/restore filter settings from globally saved settings
	 *
	 * <br>
	 * author: Thomas Behr 07-03-02
	 */
	private void restoreAllFilterSettings() {
		FilterFactory.clearFilterCache();
		for (int i = 0; i < tabList.size(); i++) {
			CharacterInfoTab tab = tabList.get(i);
			if (tab.isShown() && tab instanceof FilterAdapterPanel) {
				FilterAdapterPanel fpanel = (FilterAdapterPanel) tab;
				FilterFactory.restoreFilterSettings(fpanel);
			}
		}
		FilterFactory.restoreFilterSettings(infoInventory.getInfoGear());
	}

	/**
	 * Refresh
	 */
	public void refresh() {
		new Refresher().start();
	}

	/**
	 * Set the pc
	 * @param pc
	 */
	public void setPc(PlayerCharacter pc) {
		this.pc = pc;
		CharacterInfoTab tab = (CharacterInfoTab) characterInfoTabbedPane.getSelectedComponent();
		tab.setPc(pc);
		new PcSetter().start();
	}

	/**
	 * Return the PC currently being displayed in the GUI.
	 * @return The current Player Character.
	 */
	protected PlayerCharacter getCurrentPC()
	{
		return pc;
	}

	/**
	 * Refresh the to do list and tab flags in a swing worker thread.
	 */
	public void refreshToDosAsync()
	{
		new ToDoRefresher().start();
	}

	private void flagToDos()
	{
		List<String> allToDos = new ArrayList<String>();
		for (int i = 0; i < tabList.size(); i++)
		{
			CharacterInfoTab tab = tabList.get(i);
			if (tab.isShown())
			{
				List<String> tabToDos = tab.getToDos();
				allToDos.addAll(tabToDos);
				if (tabToDos.isEmpty())
				{
					characterInfoTabbedPane.setIconAt(indexOfTab(tab.getTabName()), null);

				}
				else
				{
					characterInfoTabbedPane.setIconAt(indexOfTab(tab.getTabName()), IconUtilitities
						.getImageIcon("Checklist16.gif"));

				}

			}
			// Display list on summary panel
			infoSummary.setToDoList(allToDos);
		}
	}

	private class tabChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent evt) {
			CharacterInfoTab tab = (CharacterInfoTab) characterInfoTabbedPane.getSelectedComponent();
			tab.refresh();
			flagToDos();
		}
	}

	private class PcSetter extends SwingWorker {

		public Object construct() {
			return "";
		}

		public void finished() {
			for (int i = 0; i < tabList.size(); i++) {
				CharacterInfoTab tab = tabList.get(i);
				if (tab.isShown()) {
					tab.setPc(pc);
				}
			}
			flagToDos();
			PCGen_Frame1.getInst().repaint();
		}
	}

	private class Refresher extends SwingWorker {

		public Object construct() {
			return "";
		}

		public void finished() {
			if(refresh) {
				CharacterInfoTab tab = (CharacterInfoTab) characterInfoTabbedPane.getSelectedComponent();
				synchronized (updateSet) {
					if (updateSet.contains(tab)) {
						tab.refresh();
						updateSet.remove(tab);
					}
					for (Iterator<CharacterInfoTab> i = updateSet.iterator(); i.hasNext();) {
						tab = i.next();
						if (tab.isShown()) {
							try {
								tab.refresh();
							} catch (Exception e) {
								System.out.println(e.getMessage());
								e.printStackTrace();
							}
						}
						i.remove();
					}
				}
			}
		}
	}

	private class ToDoRefresher extends SwingWorker {

		public Object construct() {
			return "";
		}

		public void finished() {
			flagToDos();
		}
	}

}
