/**
 * Copyright James Dempsey, 2010
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.tabs;

import javax.swing.JTabbedPane;

import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.tabs.spells.SpellBooksTab;
import pcgen.gui2.tabs.spells.SpellsKnownTab;
import pcgen.gui2.tabs.spells.SpellsPreparedTab;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * The Class {@code SpellsInfoTab} is a placeholder for the yet
 * to be implemented Seplls tab.
 */
@SuppressWarnings("serial")
public class SpellsInfoTab extends JTabbedPane implements CharacterInfoTab, TodoHandler
{

    private final TabTitle tabTitle = new TabTitle(Tab.SPELLS);
    private final SpellsKnownTab knownTab = new SpellsKnownTab();
    private final SpellsPreparedTab preparedTab = new SpellsPreparedTab();
    private final SpellBooksTab booksTab = new SpellBooksTab();

    public SpellsInfoTab()
    {
        addTab(LanguageBundle.getString("in_InfoKnown"), knownTab); //$NON-NLS-1$
        addTab(LanguageBundle.getString("in_InfoPrepared"), preparedTab); //$NON-NLS-1$
        addTab(LanguageBundle.getString("in_InfoSpellbooks"), booksTab); //$NON-NLS-1$
    }

    @Override
    public ModelMap createModels(CharacterFacade character)
    {
        ModelMap models = new ModelMap();
        models.put(ModelHandler.class, new ModelHandler(character));
        return models;
    }

    @Override
    public void restoreModels(ModelMap models)
    {
        models.get(ModelHandler.class).restoreModels();
    }

    @Override
    public void storeModels(ModelMap models)
    {
        models.get(ModelHandler.class).storeModels();
    }

    private class ModelHandler
    {
        private final ModelMap knownTabMap;
        private final ModelMap preparedTabMap;
        private final ModelMap booksTabMap;

        public ModelHandler(CharacterFacade character)
        {
            this.knownTabMap = knownTab.createModels(character);
            this.preparedTabMap = preparedTab.createModels(character);
            this.booksTabMap = booksTab.createModels(character);
        }

        public void restoreModels()
        {
            knownTab.restoreModels(knownTabMap);
            preparedTab.restoreModels(preparedTabMap);
            booksTab.restoreModels(booksTabMap);
        }

        public void storeModels()
        {
            knownTab.storeModels(knownTabMap);
            preparedTab.storeModels(preparedTabMap);
            booksTab.storeModels(booksTabMap);
        }
    }

    @Override
    public TabTitle getTabTitle()
    {
        return tabTitle;
    }

    @Override
    public void adviseTodo(String fieldName)
    {
        if ("Known".equals(fieldName)) //$NON-NLS-1$
        {
            setSelectedIndex(0);
        }

    }

}
