/**
 * InventoryInfoTab.java Copyright James Dempsey, 2010
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.tabs;

import javax.swing.JTabbedPane;

import pcgen.facade.core.CharacterFacade;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * The Class {@code InventoryInfoTab} implements the "Inventory" tab
 * including both purchasing and equiping items.
 */
@SuppressWarnings("serial")
public class InventoryInfoTab extends JTabbedPane implements CharacterInfoTab, TodoHandler
{

    private final TabTitle tabTitle = new TabTitle(Tab.INVENTORY);
    private final EquipInfoTab equipTab = new EquipInfoTab();
    private final PurchaseInfoTab purchaseTab = new PurchaseInfoTab();

    public InventoryInfoTab()
    {
        addTab(LanguageBundle.getString("in_purchase"), purchaseTab); //$NON-NLS-1$
        addTab(LanguageBundle.getString("in_equipment"), equipTab); //$NON-NLS-1$
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

        private final ModelMap equipModelMap;
        private final ModelMap purchaseModelMap;

        public ModelHandler(CharacterFacade character)
        {
            equipModelMap = equipTab.createModels(character);
            purchaseModelMap = purchaseTab.createModels(character);
        }

        public void restoreModels()
        {
            equipTab.restoreModels(equipModelMap);
            purchaseTab.restoreModels(purchaseModelMap);
        }

        public void storeModels()
        {
            equipTab.storeModels(equipModelMap);
            purchaseTab.storeModels(purchaseModelMap);
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
        // We don't provide further advice at this time.
    }

}
