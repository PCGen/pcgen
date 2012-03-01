/**
 * SpellsInfoTab.java
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
 * $Id: SpellsInfoTab.java 13208 2010-09-29 12:59:43Z jdempsey $
 */
package pcgen.gui2.tabs;

import java.util.Hashtable;
import javax.swing.JTabbedPane;
import pcgen.core.facade.CharacterFacade;
import pcgen.gui2.tabs.spells.SpellBooksTab;
import pcgen.gui2.tabs.spells.SpellsKnownTab;
import pcgen.gui2.tabs.spells.SpellsPreparedTab;

/**
 * The Class <code>SpellsInfoTab</code> is a placeholder for the yet
 * to be implemented Seplls tab.
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2010-09-29 05:59:43 -0700 (Wed, 29 Sep 2010) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 13208 $
 */
public class SpellsInfoTab extends JTabbedPane implements CharacterInfoTab, TodoHandler
{

	private final TabTitle tabTitle = new TabTitle("Spells");
	private final SpellsKnownTab knownTab = new SpellsKnownTab();
	private final SpellsPreparedTab preparedTab = new SpellsPreparedTab();
	private final SpellBooksTab booksTab = new SpellBooksTab();

	public SpellsInfoTab()
	{
		addTab("Known Spells", knownTab);
		addTab("Prepared Spells", preparedTab);
		addTab("Spell Books", booksTab);
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> table = new Hashtable<Object, Object>();
		table.put(knownTab, knownTab.createModels(character));
		table.put(preparedTab, preparedTab.createModels(character));
		table.put(booksTab, booksTab.createModels(character));
		return table;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		knownTab.restoreModels((Hashtable<?, ?>) state.get(knownTab));
		preparedTab.restoreModels((Hashtable<?, ?>) state.get(preparedTab));
		booksTab.restoreModels((Hashtable<?, ?>) state.get(booksTab));
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		knownTab.storeModels((Hashtable<Object, Object>) state.get(knownTab));
		preparedTab.storeModels((Hashtable<Object, Object>) state.get(preparedTab));
		booksTab.storeModels((Hashtable<Object, Object>) state.get(booksTab));
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void adviseTodo(String fieldName)
	{
		if ("Known".equals(fieldName))
		{
			setSelectedIndex(0);
		}
		
	}

}
