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
package pcgen.gui2.util;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SingleSelectionModel;

public class SharedTabPane extends JTabbedPane
{

	private Component sharedComponent = null;

	protected SharedTabPane()
	{
		final SingleSelectionModel selectionModel = getModel();
		selectionModel.addChangeListener(e -> {
            int index = selectionModel.getSelectedIndex();
            if (index != -1)
            {
                setSharedComponentParent(index);
            }
        });

	}

	public void addTab(String title)
	{
		addTab(title, new JPanel(new BorderLayout()));
	}

	/**
	 * Add the tab to the pane in the specified order.
	 * @param title The name of the tab. 
	 * @param index The location at which to insert the tab.
	 */
	public void addTab(String title, int index)
	{
		insertTab(title, null, new JPanel(new BorderLayout()), null, index);
	}

	protected void addTab(Component tabRenderer)
	{
		int index = getTabCount();
		addTab(null, new JPanel(new BorderLayout()));
		setTabComponentAt(index, tabRenderer);
	}

	public void removeTab(String title)
	{
		int index = indexOfTab(title);
		if (index != -1)
		{
			removeTabAt(index);
		}
	}

	private void setSharedComponentParent(int index)
	{
		JPanel comp = (JPanel) getComponentAt(index);
		comp.add(sharedComponent, BorderLayout.CENTER);
		revalidate();
	}

	@Override
	public void removeTabAt(int index)
	{
		super.removeTabAt(index);
		if (index == getSelectedIndex())
		{
			setSharedComponentParent(index);
			//Let listeners know that they should handle the shared component
			fireStateChanged();
		}
	}

	protected void setSharedComponent(Component comp)
	{
		this.sharedComponent = comp;
	}

}
