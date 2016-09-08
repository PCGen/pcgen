/*
 *  GMGenSystem.java - main class for GMGen
 *  Copyright (C) 2003 Devon Jones, Emily Smirle
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  Created on May 24, 2003
 */
package gmgen;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *  This class is the main {@code JPanel} for the whole application. It is
 *  a {@code JTabbedPane} so that it can hold many tabs for each section of
 *  functionality.<br>
 *  Created on February 20, 2003.<br>
 *  Updated on February 26, 2003.
 *
 *@author     Expires 2003
 */
public class GMGenSystemView extends JPanel implements ChangeListener
{

	private static final long serialVersionUID = -6724721014153894305L;
	/**
	 *  The {@code JPanel} that holds the panes.
	 */
	private static JTabbedPane tabbedPane;

	/**
	 *  Creates an instance of this class. It creates the tabbed pane, sets the
	 *  layout, and registers all the listeners.
	 *
	 */
	public GMGenSystemView()
	{
		/*
		 * TODO This is very strange - multiple instances of GMGenSystemView can
		 * theoretically be created, but then they will share a JTabbedPane
		 * because it is static?? - thpr 10/27/06
		 */
		GMGenSystemView.tabbedPane = new JTabbedPane();
		initComponents();
		GMGenSystemView.tabbedPane.addChangeListener(this);
	}

	/**
	 *  Gets the {@code JPanel} that is the tabbed pane.
	 *
	 *@return    the tabbed pane.
	 */
	public static JTabbedPane getTabPane()
	{
		return tabbedPane;
	}

	/**
	 *  Inserts a pane into the panel in an arbitrary index. The system will call
	 *  it sending it a {@code JPanel} and that will be placed in the view.
	 *
	 *@param  paneName  the name to be on the tab.
	 *@param  pane      the pane to be displayed.
	 *@param  index     index to place the pane at
	 */
	static void insertPane(String paneName, Component pane, int index)
	{
		tabbedPane.insertTab(paneName, null, pane, paneName, index);
	}

	/**
	 *  Places the whole {@code JTabbedPane} on the main frame setting it
	 *  visible.
	 *
	 */
	void showPane()
	{
		add(GMGenSystemView.tabbedPane, BorderLayout.CENTER);
	}

	/**
	 *  Initializes the GUI components and sets up the layout being used.
	 *
	 */
	private void initComponents()
	{
		setLayout(new BorderLayout());
	}

	@Override
	public void stateChanged(final ChangeEvent e)
	{
	}
}
