/*
 * InfoTabbedPane.java
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
 * Created on Aug 29, 2009, 1:00:39 PM
 */
package pcgen.gui2.tabs;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pcgen.base.util.DoubleKeyMap;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.TodoFacade;
import pcgen.core.facade.TodoFacade.CharacterTab;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.CharacterSelectionListener;
import pcgen.gui2.util.DisplayAwareTab;
import pcgen.util.Logging;

/**
 * This class is the tabbed pane that contains all of the CharacterInfoTabs and
 * manages the models for those tabs.
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class InfoTabbedPane extends JTabbedPane
		implements CharacterSelectionListener, ChangeListener
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
	private final DoubleKeyMap<CharacterFacade, CharacterInfoTab, Hashtable<Object, Object>> stateMap;
	private final Map<CharacterFacade, Integer> tabSelectionMap;
	private final TabModelService modelService;
	private CharacterFacade currentCharacter = null;

	public InfoTabbedPane()
	{
		this.stateMap = new DoubleKeyMap<CharacterFacade, CharacterInfoTab, Hashtable<Object, Object>>(WeakHashMap.class, HashMap.class);
		this.tabSelectionMap = new WeakHashMap<CharacterFacade, Integer>();
		this.modelService = new TabModelService();
		initComponent();
	}

	public void clearStateMap()
	{
		//Make sure that models get a chance to detach themselves from the UI before disgarding them
		if (currentCharacter != null)
		{
			Map<CharacterInfoTab, Hashtable<Object, Object>> states = stateMap.getMapFor(currentCharacter);
			for (CharacterInfoTab tab : states.keySet())
			{
				tab.storeModels(states.get(tab));
			}
		}
		stateMap.clear();
		tabSelectionMap.clear();
		currentCharacter = null;
	}

	private void initComponent()
	{
		setTabPlacement(JTabbedPane.TOP);

		SummaryInfoTab tab = new SummaryInfoTab();
		addTab(tab);
		tab.addPropertyChangeListener(new TabActionListener(tab));
		addTab(new RaceInfoTab());
		addTab(new TemplateInfoTab());
		addTab(new ClassInfoTab());
		addTab(new SkillInfoTab());
		addTab(new AbilitiesInfoTab());
		addTab(new DomainInfoTab());
		addTab(new SpellsInfoTab());
		addTab(new InventoryInfoTab());
		addTab(new DescriptionInfoTab());
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
				Hashtable<Object, Object> state = tab.createModels(character);
				stateMap.put(character, tab, state);
			}
			String key = UIPropertyContext.C_PROP_INITIAL_TAB;
			key = UIPropertyContext.createCharacterPropertyKey(character, key);
			//defaults to the summary tab if prop doesn't exist
			int startingTab = UIPropertyContext.getInstance().getInt(key, SUMMARY_TAB);
			tabSelectionMap.put(character, startingTab);
		}
		if (currentCharacter != null)
		{
			Map<CharacterInfoTab, Hashtable<Object, Object>> states = stateMap.getMapFor(currentCharacter);
			modelService.storeModels(states);
			//Save tabSelection for this character
			tabSelectionMap.put(currentCharacter, getSelectedIndex());
		}
		currentCharacter = character;

		Map<CharacterInfoTab, Hashtable<Object, Object>> states = stateMap.getMapFor(character);
		int selectedIndex = tabSelectionMap.get(character);
		modelService.restoreModels(states, selectedIndex);
	}

	/**
	 * Switch the current tab to be the one named, possibly including a sub tab and 
	 * then advise the user of the item to be done. generally the tab will handle 
	 * this but a fallback of a dialog will be used if the tab can't do the advising.    
	 * @param dest An arry of the tab name, the field name and optionally the sub tab name.
	 */
	private void switchTabsAndAdviseTodo(String[] dest)
	{
		String tabName = CharacterTab.valueOf(dest[0]).getTabTile();
		
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
			Logging.errorPrint("Failed to find tab " + tabName);
			return;
		}

		if (selTab instanceof JTabbedPane && dest.length > 2)
		{
			JTabbedPane tab = (JTabbedPane) selTab;
			for (int i = 0; i < tab.getTabCount(); i++)
			{
				if (dest[2].equals(tab.getTitleAt(i)))
				{
					tab.setSelectedIndex(i);
					//selTab = tab.getComponent(i);
					break;
				}
			}
		}

		if (selTab instanceof TodoHandler)
		{
			((TodoHandler)selTab).adviseTodo(dest[1]);
		}
		else
		{
			String message = "Please use the " + dest[1] + " field.";
			JOptionPane.showMessageDialog(selTab, message,
										  "Things To Be Done", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public void stateChanged(ChangeEvent e)
	{
		// The currently displayed tab has changed so if the new one wants to know about it, let it know 
		Component comp = getSelectedComponent();
		if (comp instanceof DisplayAwareTab)
		{
			((DisplayAwareTab) comp).tabSelected();
		}
	}
	 
	/**
	 * This class handles the concurrent processing of storing and restoring tab models.
	 * Conceptually this process consists of two separate processing queues.
	 * One queue is the orderly execution of restoring tab models which takes place in a
	 * a semi-concurrent manner. Each tab has its models restored as a separate task on
	 * the EventDispatchThread which allows for the UI to remain responsive to other events.
	 * If the user selects a different character while tab models are being restored then
	 * the model restoration is canceled and the tabs which completed restoration will be
	 * processed to store their models. This is where the second queue is needed because
	 * it contains the tabs which have completed their model restoration.
	 * So on a character tab change the general process is as follows:<br>
	 * 1. cancel all restoration tasks that have not yet executed<br>
	 * 2. clear the restoration queue<br>
	 * 3. process the store queue<br>
	 * 4. push all tabs onto the restoration process queue<br>
	 * 
	 * The order in which tabs have their models restored is dependent on the amount of time
	 * that it takes a tab to restore their model data. The tabs that take the least amount of
	 * time to restore their models will be executed first, the second least second, and so on.
	 * The calculation of time taken is based on the amount of time the previous execution of
	 * restoreModels() took.
	 */
	private class TabModelService extends ThreadPoolExecutor implements Comparator<CharacterInfoTab>
	{

		private final Map<CharacterInfoTab, Long> timingMap;
		private final Queue<CharacterInfoTab> storeQueue;
		private final Queue<Future<?>> restoreQueue;

		public TabModelService()
		{
			super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory()
			{

				@Override
				public Thread newThread(Runnable r)
				{
					Thread thread = new Thread(r);
					thread.setDaemon(true);
					thread.setPriority(Thread.NORM_PRIORITY);
					thread.setName("tab-info-thread");
					return thread;
				}

			});
			this.timingMap = new HashMap<CharacterInfoTab, Long>();
			storeQueue = new LinkedList<CharacterInfoTab>();
			restoreQueue = new LinkedList<Future<?>>();
		}

		@Override
		public int compare(CharacterInfoTab o1, CharacterInfoTab o2)
		{
			if (timingMap.containsKey(o1) && timingMap.containsKey(o2))
			{
				long dif = timingMap.get(o1) - timingMap.get(o2);
				if (dif < 0)
				{
					return -1;
				}
				if (dif > 0)
				{
					return 1;
				}
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

		private void restoreTab(CharacterInfoTab infoTab, Hashtable<Object, Object> models)
		{
			long starttime = System.nanoTime();
			infoTab.restoreModels(models);
			long time = System.nanoTime() - starttime;
			timingMap.put(infoTab, time);
			storeQueue.add(infoTab);
		}

		public void restoreModels(Map<CharacterInfoTab, Hashtable<Object, Object>> states, int selectedIndex)
		{
			CharacterInfoTab firstTab = (CharacterInfoTab) getComponentAt(selectedIndex);
			restoreTab(firstTab, states.get(firstTab));
			setSelectedIndex(selectedIndex);

			PriorityQueue<CharacterInfoTab> queue = new PriorityQueue<CharacterInfoTab>(states.keySet().size(), this);
			queue.addAll(states.keySet());
			queue.remove(firstTab);

			while (!queue.isEmpty())
			{
				CharacterInfoTab infoTab = queue.poll();
				Hashtable<Object, Object> models = states.get(infoTab);
				restoreQueue.add(submit(new RestoreModelsTask(infoTab, models)));
			}
		}

		public void storeModels(Map<CharacterInfoTab, Hashtable<Object, Object>> states)
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
			private final Hashtable<Object, Object> models;
			private boolean executed;

			public RestoreModelsTask(CharacterInfoTab infoTab, Hashtable<Object, Object> models)
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
					SwingUtilities.invokeAndWait(new Runnable()
					{

						@Override
						public void run()
						{
							if (!executed)
							{
								restoreTab(infoTab, models);
							}
						}

					});
				}
				catch (InterruptedException ex)
				{
				}
				catch (InvocationTargetException ex)
				{
					Logging.errorPrint(null, ex.getCause());
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

		private Component component;

		public TabActionListener(Component component)
		{
			this.component = component;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			int index = indexOfComponent(component);
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
			else if (TabTitle.ENABLED.equals(propName))
			{
				InfoTabbedPane.this.setEnabledAt(index, (Boolean) evt.getNewValue());
			}
			else if (TodoFacade.SWITCH_TABS.equals(propName))
			{
				String destString = (String) evt.getNewValue();
				String[] dest = destString.split("/");
				switchTabsAndAdviseTodo(dest);
			}
		}

	}

}
