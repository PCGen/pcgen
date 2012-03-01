/*
 * AbilitiesInfoTab.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jul 15, 2008, 6:58:51 PM
 */
package pcgen.gui2.tabs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pcgen.core.facade.AbilityCategoryFacade;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.gui2.util.SharedTabPane;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class AbilitiesInfoTab extends SharedTabPane implements CharacterInfoTab, TodoHandler
{

	private final AbilityChooserTab abilityTab;
	private final TabTitle tabTitle;

	public AbilitiesInfoTab()
	{
		this.abilityTab = new AbilityChooserTab();
		this.tabTitle = new TabTitle("in_featsAbilities");
		setSharedComponent(abilityTab);
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put("TabsModel", new AbilityTabsModel(character));
		return state;
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		AbilityTabsModel tabsModel = (AbilityTabsModel) state.get("TabsModel");
		tabsModel.uninstall();
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		AbilityTabsModel tabsModel = (AbilityTabsModel) state.get("TabsModel");
		tabsModel.install();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	private class AbilityTabsModel implements ListListener<AbilityCategoryFacade>, ChangeListener
	{

		private final Map<String, TabInfo> typeMap = new HashMap<String, TabInfo>();
		private final List<TabInfo> tabs = new ArrayList<TabInfo>();
		private final ListFacade<AbilityCategoryFacade> categories;
		private final CharacterFacade character;
		private boolean isInstalled = false;
		private String selectedTitle = null;
		private ListFacade<AbilityCategoryFacade> activeCategories;

		public AbilityTabsModel(CharacterFacade character)
		{
			this.character = character;
			this.activeCategories = character.getActiveAbilityCategories();
			this.categories = character.getDataSet().getAbilityCategories();
			for (AbilityCategoryFacade category : activeCategories)
			{
				String type = category.getType();
				if (!typeMap.containsKey(type))
				{
					tabs.add(new TabInfo(type, character));
					populateFullCategoryList(type, typeMap.get(type));
				}
				typeMap.get(type).categoryList.addElement(category);
			}
			activeCategories.addListListener(this);
			selectedTitle = tabs.get(0).title;
		}

		/**
		 * Populate the fullCategoryList for a TabInfo object with all 
		 * categories of the specified type.
		 * 
		 * @param type The type to be scanned for.
		 * @param tabInfo The TabInfo to be populated.
		 */
		private void populateFullCategoryList(String type, TabInfo tabInfo)
		{
			for (AbilityCategoryFacade category : categories)
			{
				if (type.equals(category.getType()))
				{
					tabInfo.fullCategoryList.addElement(category);
				}
			}
		}

		@Override
		public void elementAdded(ListEvent<AbilityCategoryFacade> e)
		{
			AbilityCategoryFacade element = e.getElement();
			String type = element.getType();
			if (!typeMap.containsKey(type))
			{
				if (e.getIndex() >= tabs.size())
				{
					Logging.log(Logging.WARNING, "Trying to add " + type + " to "
						+ tabs + " at index " + e.getIndex()
						+ ". Putting at end.");
					tabs.add(new TabInfo(type, character));
				}
				else
				{
					tabs.add(e.getIndex(), new TabInfo(type, character));
				}
				populateFullCategoryList(type, typeMap.get(type));
				if (isInstalled)
				{//Add new tab
					addTab(type, e.getIndex());
				}
			}
			typeMap.get(type).categoryList.addElement(element);
		}

		@Override
		public void elementRemoved(ListEvent<AbilityCategoryFacade> e)
		{
			AbilityCategoryFacade element = e.getElement();
			String type = element.getType();
			TabInfo info = typeMap.get(type);
			info.categoryList.removeElement(element);
			if (info.categoryList.isEmpty())
			{
				tabs.remove(typeMap.remove(type));
				if (isInstalled)
				{//Remove Tab
					removeTab(type);
				}
			}
		}

		@Override
		public void elementsChanged(ListEvent<AbilityCategoryFacade> e)
		{
			Map<String, Collection<AbilityCategoryFacade>> tempMap;
			tempMap = new HashMap<String, Collection<AbilityCategoryFacade>>();
			for (AbilityCategoryFacade category : categories)
			{
				String type = category.getType();
				if (!tempMap.containsKey(type))
				{
					tempMap.put(type, new ArrayList<AbilityCategoryFacade>());
				}
				tempMap.get(type).add(category);
			}
			for (String type : tempMap.keySet())
			{
				if (!typeMap.containsKey(type))
				{
					tabs.add(new TabInfo(type, character));
					populateFullCategoryList(type, typeMap.get(type));
					if (isInstalled)
					{
						addTab(type);
					}
				}
				typeMap.get(type).categoryList.setContents(tempMap.get(type));
			}
			Iterator<String> oldTypes = typeMap.keySet().iterator();
			while (oldTypes.hasNext())
			{
				String type = oldTypes.next();
				if (!tempMap.containsKey(type))
				{
					tabs.remove(typeMap.get(type));
					oldTypes.remove();
					if (isInstalled)
					{
						removeTab(type);
					}
				}
			}
		}

		public void install()
		{
			for (TabInfo tabInfo : tabs)
			{
				addTab(tabInfo.title);
			}
			setSelectedIndex(indexOfTab(selectedTitle));
			abilityTab.restoreState(typeMap.get(selectedTitle).tabData);
			addChangeListener(this);
			isInstalled = true;
		}

		public void uninstall()
		{
			abilityTab.storeState(typeMap.get(selectedTitle).tabData);
			removeChangeListener(this);
			removeAll();
			isInstalled = false;
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			TabInfo tabInfo = typeMap.get(selectedTitle);
			if (tabInfo != null)
			{
				abilityTab.storeState(tabInfo.tabData);
			}
			if (getSelectedIndex() != -1)
			{
				selectedTitle = getTitleAt(getSelectedIndex());
				abilityTab.restoreState(typeMap.get(selectedTitle).tabData);
			}
		}

		private class TabInfo
		{

			public final String title;
			public final Hashtable<Object, Object> tabData;
			public final DefaultListFacade<AbilityCategoryFacade> categoryList;
			public final DefaultListFacade<AbilityCategoryFacade> fullCategoryList;

			public TabInfo(String title, CharacterFacade character)
			{
				this.title = title;
				this.categoryList = new DefaultListFacade<AbilityCategoryFacade>();
				this.fullCategoryList = new DefaultListFacade<AbilityCategoryFacade>();
				this.tabData = abilityTab.createState(character, categoryList, fullCategoryList);
				typeMap.put(title, this);
			}

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void adviseTodo(String fieldName)
	{
		abilityTab.adviseTodo(fieldName);
	}

}
