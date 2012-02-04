/**
 * UnimplementedTab.java
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
 * Created on 29/09/2010 7:06:55 PM
 *
 * $Id: UnimplementedTab.java 13208 2010-09-29 12:59:43Z jdempsey $
 */
package pcgen.gui2.tabs;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pcgen.core.facade.CharacterFacade;

/**
 * The Class <code>UnimplementedTab</code> is a general implementation of 
 8 the display of a tab that has not yet been implemented.
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2010-09-29 05:59:43 -0700 (Wed, 29 Sep 2010) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 13208 $
 */
public abstract class UnimplementedTab extends JPanel implements CharacterInfoTab
{

	public UnimplementedTab()
	{
		add(new JLabel("This tab has not yet been implemented."));
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.tabs.CharacterInfoTab#getTabTitle()
	 */
	public TabTitle getTabTitle()
	{
		TabTitle title = new TabTitle(getTabName());
		title.putValue(TabTitle.TOOLTIP, "This tab has not yet been implemented.");
		return title;
	}
	
	/**
	 * @return The name of the tab that has not been implemented.
	 */
	abstract String getTabName();

	/* (non-Javadoc)
	 * @see pcgen.gui2.tabs.CharacterInfoTab#createModels(pcgen.core.facade.CharacterFacade)
	 */
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		return new Hashtable<Object, Object>();
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.tabs.CharacterInfoTab#restoreModels(java.util.Hashtable)
	 */
	public void restoreModels(Hashtable<?, ?> state)
	{
		//No action
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.tabs.CharacterInfoTab#storeModels(java.util.Hashtable)
	 */
	public void storeModels(Hashtable<Object, Object> state)
	{
		//No action
	}

}
