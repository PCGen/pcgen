/*
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
 */
package pcgen.gui2.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pcgen.core.AbilityCategory;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.util.SharedTabPane;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

/**
 * This component is the tabbed pane which contains all of the
 * AbilityChooserTabs. This component doesn't actually display any character
 * information, that is the job of the AbilityChooserTab. All this class does is
 * manage the states of all the AbilityChooserTab.
 *
 * @see AbilityChooserTab
 */
@SuppressWarnings("serial")
public class AbilitiesInfoTab extends SharedTabPane implements CharacterInfoTab, TodoHandler
{

    private final AbilityChooserTab abilityTab;
    private final TabTitle tabTitle;

    public AbilitiesInfoTab()
    {
        this.abilityTab = new AbilityChooserTab();
        this.tabTitle = new TabTitle(Tab.ABILITIES);
        setSharedComponent(abilityTab);
    }

    @Override
    public ModelMap createModels(CharacterFacade character)
    {
        ModelMap models = new ModelMap();
        models.put(AbilityTabsModel.class, new AbilityTabsModel(character));
        return models;
    }

    @Override
    public void storeModels(ModelMap models)
    {
        models.get(AbilityTabsModel.class).uninstall();
    }

    @Override
    public void restoreModels(ModelMap models)
    {
        models.get(AbilityTabsModel.class).install();
    }

    @Override
    public TabTitle getTabTitle()
    {
        return tabTitle;
    }

    private class AbilityTabsModel implements ListListener<AbilityCategory>, ChangeListener
    {

        private final Map<String, TabInfo> typeMap = new HashMap<>();
        private final List<TabInfo> tabs = new ArrayList<>();
        private final MapFacade<AbilityCategory, ListFacade<AbilityFacade>> categoryMap;
        private final CharacterFacade character;
        private boolean isInstalled = false;
        private String selectedTitle = null;
        private final ListFacade<AbilityCategory> activeCategories;

        public AbilityTabsModel(CharacterFacade character)
        {
            this.character = character;
            this.activeCategories = character.getActiveAbilityCategories();
            this.categoryMap = character.getDataSet().getAbilities();
            for (AbilityCategory category : activeCategories)
            {
                String type = category.getType();
                if (!typeMap.containsKey(type))
                {
                    tabs.add(new TabInfo(type, character));
                    populateFullCategoryList(type, typeMap.get(type));
                }
                typeMap.get(type).categoryList.addElement(category);
            }
            selectedTitle = tabs.get(0).title;
        }

        /**
         * Populate the fullCategoryList for a TabInfo object with all
         * categories of the specified type.
         *
         * @param type    The type to be scanned for.
         * @param tabInfo The TabInfo to be populated.
         */
        private void populateFullCategoryList(String type, TabInfo tabInfo)
        {
            for (AbilityCategory category : categoryMap.getKeys())
            {
                if (type.equals(category.getType()))
                {
                    tabInfo.fullCategoryList.addElement(category);
                }
            }
        }

        @Override
        public void elementAdded(ListEvent<AbilityCategory> e)
        {
            AbilityCategory element = e.getElement();
            String type = element.getType();
            if (!typeMap.containsKey(type))
            {
                int index = e.getIndex();
                if (index > tabs.size())
                {
                    Logging.log(Logging.WARNING, "Trying to add " + type + " to " //$NON-NLS-2$
                            + tabs + " at index " + index //$NON-NLS-1$
                            + ". Putting at end."); //$NON-NLS-1$
                    index = tabs.size();
                    tabs.add(new TabInfo(type, character));
                } else
                {
                    tabs.add(index, new TabInfo(type, character));
                }
                populateFullCategoryList(type, typeMap.get(type));
                if (isInstalled)
                {//Add new tab
                    addTab(type, index);
                }
            }
            typeMap.get(type).categoryList.addElement(element);
        }

        @Override
        public void elementRemoved(ListEvent<AbilityCategory> e)
        {
            AbilityCategory element = e.getElement();
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
        public void elementsChanged(ListEvent<AbilityCategory> e)
        {
            Map<String, List<AbilityCategory>> tempMap;
            tempMap = new HashMap<>();
            for (AbilityCategory category : categoryMap.getKeys())
            {
                String type = category.getType();
                if (!tempMap.containsKey(type))
                {
                    tempMap.put(type, new ArrayList<>());
                }
                tempMap.get(type).add(category);
            }
            for (final Map.Entry<String, List<AbilityCategory>> entry : tempMap.entrySet())
            {
                String type = entry.getKey();
                if (!typeMap.containsKey(type))
                {
                    tabs.add(new TabInfo(type, character));
                    populateFullCategoryList(type, typeMap.get(type));
                    if (isInstalled)
                    {
                        addTab(type);
                    }
                }
                typeMap.get(type).categoryList.updateContents(entry.getValue());
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

        @Override
        public void elementModified(ListEvent<AbilityCategory> e)
        {
            //TODO: do something
        }

        public void install()
        {
            activeCategories.addListListener(this);
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
            activeCategories.removeListListener(this);
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
                if (typeMap.get(selectedTitle) == null)
                {
                    Logging.errorPrint("Selected tab " + selectedTitle + " at index " + getSelectedIndex()
                            + " but there is no typeMap entry for it.");
                    Logging.reportAllThreads();
                }
                abilityTab.restoreState(typeMap.get(selectedTitle).tabData);
            }
        }

        @SuppressWarnings({"UseOfObsoleteCollectionType", "PMD.ReplaceHashtableWithMap", "serial"})
        private final class TabInfo
        {
            public final String title;
            private final Hashtable<Object, Object> tabData;
            private final DefaultListFacade<AbilityCategory> categoryList;
            private final DefaultListFacade<AbilityCategory> fullCategoryList;

            private TabInfo(String title, CharacterFacade character)
            {
                this.title = title;
                this.categoryList = new DefaultListFacade<>();
                this.fullCategoryList = new DefaultListFacade<>();
                this.tabData = abilityTab.createState(character, categoryList, fullCategoryList, title);
                typeMap.put(title, this);
            }

            @SuppressWarnings("nls")
            @Override
            public String toString()
            {
                return "TabInfo [title=" + title + ", categoryList=" + categoryList + "]";
            }

        }

        @SuppressWarnings("nls")
        @Override
        public String toString()
        {
            return "AbilityTabsModel [tabs=" + tabs + ", isInstalled=" + isInstalled + ", selectedTitle="
                    + selectedTitle + "]";
        }

    }

    @Override
    public void adviseTodo(String fieldName)
    {
        abilityTab.adviseTodo(fieldName);
    }

}
