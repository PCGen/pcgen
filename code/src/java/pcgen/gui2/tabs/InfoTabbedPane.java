/*
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.gui2.tabs;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.util.CControl;
import pcgen.core.GameMode;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.TodoFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tabs.CharacterInfoTab.ModelMap;
import pcgen.gui2.tools.CharacterSelectionListener;
import pcgen.gui2.util.DisplayAwareTab;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

/**
 * This class is the tabbed pane that contains all of the CharacterInfoTabs and
 * manages the models for those tabs.
 */
@SuppressWarnings("serial")
public final class InfoTabbedPane extends JTabbedPane implements CharacterSelectionListener, ChangeListener
{

	public static final int SUMMARY_TAB = 0;
	public static final int RACE_TAB = 1;
	public static final int TEMPLATE_TAB = 2;
	public static final int CLASS_TAB = 3;
	public static final int SKILL_TAB = 4;
	public static final int ABILITIES_TAB = 5;
	public static final int DOMAIN_TAB = 6;
	public static final int SPELLS_TAB = 7;
	public static final int INVENTORY_TAB = 8;
	public static final int DESCRIPTION_TAB = 9;
	public static final int CHARACTER_SHEET_TAB = 10;
	private final DoubleKeyMap<CharacterFacade, CharacterInfoTab, ModelMap> stateMap;
	private final Map<CharacterFacade, Integer> tabSelectionMap;
	private final TabModelService modelService;
	private final List<CharacterInfoTab> fullTabList = new ArrayList<>();
	private final DomainInfoTab domainInfoTab;
	private int domainTabLocation;
	private CharacterFacade currentCharacter = null;

	public InfoTabbedPane()
	{
		this.stateMap = new DoubleKeyMap<>();
		this.tabSelectionMap = new WeakHashMap<>();
		this.modelService = new TabModelService();
		this.domainInfoTab = new DomainInfoTab();
		initComponent();
	}

	public void clearStateMap()
	{
		//Make sure that models get a chance to detach themselves from the UI before disgarding them
		if (currentCharacter != null)
		{
			Map<CharacterInfoTab, ModelMap> states = stateMap.getMapFor(currentCharacter);
			states.forEach(CharacterInfoTab::storeModels);
		}
		stateMap.clear();
		tabSelectionMap.clear();
		currentCharacter = null;
	}

	private void initComponent()
	{
		setTabPlacement(SwingConstants.TOP);

		SummaryInfoTab tab = new SummaryInfoTab();
		addTab(tab);
		tab.addPropertyChangeListener(new TabActionListener(tab));
		addTab(new RaceInfoTab());
		addTab(new TemplateInfoTab());
		addTab(new ClassInfoTab());
		addTab(new SkillInfoTab());
		addTab(new AbilitiesInfoTab());
		domainTabLocation = getTabCount();
		addTab(domainInfoTab);
		addTab(new SpellsInfoTab());
		addTab(new InventoryInfoTab());
		addTab(new DescriptionInfoTab());
		addTab(new TempBonusInfoTab());
		addTab(new CompanionInfoTab());
		addTab(new CharacterSheetInfoTab());
		addChangeListener(this);
	}

	private <T extends Component & CharacterInfoTab> void addTab(T tab)
	{
		TabTitle tabTitle = tab.getTabTitle();
		String title = (String) tabTitle.getValue(TabTitle.TITLE);
		String tooltip = (String) tabTitle.getValue(TabTitle.TOOLTIP);
		Icon icon = (Icon) tabTitle.getValue(TabTitle.ICON);
		addTab(title, icon, tab, tooltip);
		fullTabList.add(tab);
		tabTitle.addPropertyChangeListener(new TabActionListener(tab));
	}

	@Override
	public void setCharacter(CharacterFacade character)
	{
		modelService.cancelRestoreTasks();
		if (!stateMap.containsKey(character))
		{
			//This is the first time this character has been added, so initialize the tab states.
			for (int i = 0; i < getTabCount(); i++)
			{
				CharacterInfoTab tab = (CharacterInfoTab) getComponentAt(i);
				ModelMap models = tab.createModels(character);
				stateMap.put(character, tab, models);
			}
			String key = UIPropertyContext.C_PROP_INITIAL_TAB;
			key = UIPropertyContext.createCharacterPropertyKey(character, key);
			//defaults to the summary tab if prop doesn't exist
			int startingTab = UIPropertyContext.getInstance().getInt(key, SUMMARY_TAB);
			tabSelectionMap.put(character, startingTab);
		}
		if (currentCharacter != null)
		{
			Map<CharacterInfoTab, ModelMap> states = stateMap.getMapFor(currentCharacter);
			modelService.storeModels(states);
			//Save tabSelection for this character
			tabSelectionMap.put(currentCharacter, getSelectedIndex());
		}
		currentCharacter = character;

		Map<CharacterInfoTab, ModelMap> states = stateMap.getMapFor(character);
		updateTabsForCharacter(character);
		int selectedIndex = tabSelectionMap.get(character);
		modelService.restoreModels(states, selectedIndex);
	}

	/**
	 * Update the displayed tabs to reflect the settings of the game mode of the
	 * character to which we are switching.
	 *
	 * @param character The character being displayed.
	 */
	private void updateTabsForCharacter(CharacterFacade character)
	{
		GameMode gameMode = character.getDataSet().getGameMode();
		int tabIndex = 0;
		for (CharacterInfoTab charInfoTab : fullTabList)
		{
			TabTitle tabTitle = charInfoTab.getTabTitle();
			Tab tab = tabTitle.getTab();
			String newName = gameMode.getTabName(tab);
			if (!newName.equals(tabTitle.getValue(TabTitle.TITLE)))
			{
				tabTitle.putValue(TabTitle.TITLE, newName);
			}
			if (gameMode.getTabShown(tab))
			{
				if (getComponentAt(tabIndex) != charInfoTab)
				{
					String title = (String) tabTitle.getValue(TabTitle.TITLE);
					String tooltip = (String) tabTitle.getValue(TabTitle.TOOLTIP);
					Icon icon = (Icon) tabTitle.getValue(TabTitle.ICON);
					insertTab(title, icon, (Component) charInfoTab, tooltip, tabIndex);
				}
				tabIndex++;
			}
			else
			{
				if (getComponentAt(tabIndex) == charInfoTab)
				{
					remove(tabIndex);
				}

			}
		}
		if (character.isFeatureEnabled(CControl.DOMAINFEATURE))
		{
			TabTitle tabTitle = domainInfoTab.getTabTitle();
			String title = (String) tabTitle.getValue(TabTitle.TITLE);
			String tooltip = (String) tabTitle.getValue(TabTitle.TOOLTIP);
			Icon icon = (Icon) tabTitle.getValue(TabTitle.ICON);
			insertTab(title, icon, domainInfoTab, tooltip, domainTabLocation);
		}
		else
		{
			remove(domainInfoTab);
		}
	}

	/**
	 * Switch the current tab to be the one named, possibly including a sub tab
	 * and then advise the user of the item to be done. generally the tab will
	 * handle this but a fallback of a dialog will be used if the tab can't do
	 * the advising.
	 *
	 * @param dest An arry of the tab name, the field name and optionally the
	 * sub tab name.
	 */
	private void switchTabsAndAdviseTodo(String[] dest)
	{
		Tab tab = Tab.valueOf(dest[0]);
		String tabName = currentCharacter.getDataSet().getGameMode().getTabName(tab);

		Component selTab = null;
		for (int i = 0; i < getTabCount(); i++)
		{
			if (tabName.equals(getTitleAt(i)))
			{
				setSelectedIndex(i);
				selTab = getComponent(i);
				break;
			}
		}
		if (selTab == null)
		{
			Logging.errorPrint("Failed to find tab " + tabName); //$NON-NLS-1$
			return;
		}

		if (selTab instanceof JTabbedPane tabPane && dest.length > 2)
		{
			for (int i = 0; i < tabPane.getTabCount(); i++)
			{
				if (dest[2].equals(tabPane.getTitleAt(i)))
				{
					tabPane.setSelectedIndex(i);
					break;
				}
			}
		}

		if (selTab instanceof TodoHandler)
		{
			((TodoHandler) selTab).adviseTodo(dest[1]);
		}
		else
		{
			String message = LanguageBundle.getFormattedString("in_todoUseField", dest[1]); //$NON-NLS-1$
			JOptionPane.showMessageDialog(selTab, message, LanguageBundle.getString("in_tipsString"), //$NON-NLS-1$
				JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void handleDisplayAware()
	{
		// The currently displayed tab has changed so if the new one wants to know about it, let it know 
		Component comp = getSelectedComponent();
		if (comp instanceof DisplayAwareTab)
		{
			((DisplayAwareTab) comp).tabSelected();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		handleDisplayAware();
	}

	public void characterRemoved(CharacterFacade character)
	{
		stateMap.removeAll(character);
	}

	/**
	 * This class handles the concurrent processing of storing and restoring tab
	 * models. Conceptually this process consists of two separate processing
	 * queues. One queue is the orderly execution of restoring tab models which
	 * takes place in a semi-concurrent manner. Each tab has its models
	 * restored as a separate task on the EventDispatchThread which allows for
	 * the UI to remain responsive to other events. If the user selects a
	 * different character while tab models are being restored then the model
	 * restoration is canceled and the tabs which completed restoration will be
	 * processed to store their models. This is where the second queue is needed
	 * because it contains the tabs which have completed their model
	 * restoration. So on a character tab change the general process is as
	 * follows:<br>
	 * 1. cancel all restoration tasks that have not yet executed<br>
	 * 2. clear the restoration queue<br>
	 * 3. process the store queue<br>
	 * 4. push all tabs onto the restoration process queue<br>
	 *
	 * The order in which tabs have their models restored is dependent on the
	 * amount of time that it takes a tab to restore their model data. The tabs
	 * that take the least amount of time to restore their models will be
	 * executed first, the second least second, and so on. The calculation of
	 * time taken is based on the amount of time the previous execution of
	 * restoreModels() took.
	 */
	private class TabModelService extends ThreadPoolExecutor implements Comparator<CharacterInfoTab>
	{

		private final Map<CharacterInfoTab, Long> timingMap;
		private final Queue<CharacterInfoTab> storeQueue;
		private final Queue<Future<?>> restoreQueue;

		public TabModelService()
		{
			super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("tab-info-thread"); //$NON-NLS-1$
                return thread;
            });
			this.timingMap = new HashMap<>();
			storeQueue = new LinkedList<>();
			restoreQueue = new LinkedList<>();
		}

		@Override
		public int compare(CharacterInfoTab o1, CharacterInfoTab o2)
		{
			if (timingMap.containsKey(o1) && timingMap.containsKey(o2))
			{
				return Long.compare(timingMap.get(o1), timingMap.get(o2));
			}
			else if (timingMap.containsKey(o1))
			{
				return 1;
			}
			else if (timingMap.containsKey(o2))
			{
				return -1;
			}
			return 0;
		}

		private void restoreTab(CharacterInfoTab infoTab, ModelMap models)
		{
			long starttime = System.nanoTime();
			infoTab.restoreModels(models);
			long time = System.nanoTime() - starttime;
			timingMap.put(infoTab, time);
			storeQueue.add(infoTab);
		}

		public void restoreModels(Map<CharacterInfoTab, ModelMap> states, int selectedIndex)
		{
			CharacterInfoTab firstTab = (CharacterInfoTab) getComponentAt(selectedIndex);
			restoreTab(firstTab, states.get(firstTab));
			int oldSelectedIndex = getSelectedIndex();
			setSelectedIndex(selectedIndex);
			if (oldSelectedIndex == selectedIndex)
			{
				handleDisplayAware();
			}

			PriorityQueue<CharacterInfoTab> queue = new PriorityQueue<>(states.keySet().size(), this);
			queue.addAll(states.keySet());
			queue.remove(firstTab);

			while (!queue.isEmpty())
			{
				CharacterInfoTab infoTab = queue.poll();
				ModelMap models = states.get(infoTab);
				restoreQueue.add(submit(new RestoreModelsTask(infoTab, models)));
			}
		}

		public void storeModels(Map<CharacterInfoTab, ModelMap> states)
		{
			while (!storeQueue.isEmpty())
			{
				CharacterInfoTab infoTab = storeQueue.poll();
				infoTab.storeModels(states.get(infoTab));
			}
		}

		public void cancelRestoreTasks()
		{
			while (!restoreQueue.isEmpty())
			{
				restoreQueue.poll().cancel(true);
			}
		}

		private class RestoreModelsTask implements Runnable
		{

			private final CharacterInfoTab infoTab;
			private final ModelMap models;
			private boolean executed;

			public RestoreModelsTask(CharacterInfoTab infoTab, ModelMap models)
			{
				this.infoTab = infoTab;
				this.models = models;
				this.executed = false;
			}

			@Override
			public void run()
			{
				try
				{
					SwingUtilities.invokeAndWait(() -> {
						if (!executed)
						{
							restoreTab(infoTab, models);
						}
					});
				}
				catch (InterruptedException | InvocationTargetException ex)
				{
					Logging.errorPrint("exception in InfoTabbedPane", ex);
				}
				finally
				{
					executed = true;
				}
			}

		}

	}

	private class TabActionListener implements PropertyChangeListener
	{

		private final Component component;

		public TabActionListener(Component component)
		{
			this.component = component;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			int index = indexOfComponent(component);
			if (index < 0)
			{
				return;
			}
			String propName = evt.getPropertyName();
			if (TabTitle.TITLE.equals(propName))
			{
				InfoTabbedPane.this.setTitleAt(index, (String) evt.getNewValue());
			}
			else if (TabTitle.ICON.equals(propName))
			{
				InfoTabbedPane.this.setIconAt(index, (Icon) evt.getNewValue());
			}
			else if (TabTitle.TOOLTIP.equals(propName))
			{
				InfoTabbedPane.this.setToolTipTextAt(index, (String) evt.getNewValue());
			}
			else if (TodoFacade.SWITCH_TABS.equals(propName))
			{
				String destString = (String) evt.getNewValue();
				String[] dest = destString.split("/"); //$NON-NLS-1$
				switchTabsAndAdviseTodo(dest);
			}
		}

	}

}
