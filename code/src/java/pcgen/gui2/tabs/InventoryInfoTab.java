/**
 * InventoryInfoTab.java
 * Copyright James Dempsey, 2010
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
 * Created on 29/09/2010 7:16:42 PM
 *
 * $Id: InventoryInfoTab.java 14613 2011-02-25 22:43:05Z cpmeister $
 */
package pcgen.gui2.tabs;

import java.util.Hashtable;
import javax.swing.JTabbedPane;
import pcgen.core.facade.CharacterFacade;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>InventoryInfoTab</code> is a placeholder for the yet
 * to be implemented Inventory tab.
 * <br/>
 * Last Editor: $Author: cpmeister $
 * Last Edited: $Date: 2011-02-25 14:43:05 -0800 (Fri, 25 Feb 2011) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 14613 $
 */
public class InventoryInfoTab extends JTabbedPane implements CharacterInfoTab
{

	private final TabTitle tabTitle = new TabTitle(LanguageBundle.getString("in_inventory"));
	private final EquipInfoTab equipTab = new EquipInfoTab();
	private final PurchaseInfoTab purchaseTab = new PurchaseInfoTab();

	public InventoryInfoTab()
	{
		addTab("Purchase", purchaseTab);
		addTab("Equipment", equipTab);
	}

	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> table = new Hashtable<Object, Object>();
		table.put(equipTab, equipTab.createModels(character));
		table.put(purchaseTab, purchaseTab.createModels(character));
		return table;
	}

	public void restoreModels(Hashtable<?, ?> state)
	{
		equipTab.restoreModels((Hashtable<?, ?>) state.get(equipTab));
		purchaseTab.restoreModels((Hashtable<?, ?>) state.get(purchaseTab));
	}

	public void storeModels(Hashtable<Object, Object> state)
	{
		equipTab.storeModels((Hashtable<Object, Object>) state.get(equipTab));
		purchaseTab.storeModels((Hashtable<Object, Object>) state.get(purchaseTab));
	}

	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

}
