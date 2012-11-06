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

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Component;

/**
 *  This class is the main <code>JPanel</code> for the whole application. It is
 *  a <code>JTabbedPane</code> so that it can hold many tabs for each section of
 *  functionality.<br>
 *  Created on February 20, 2003.<br>
 *  Updated on February 26, 2003.
 *
 *@author     Expires 2003
 *@since    May 30, 2003
 *@version    3.3
 *@since      GMGen 3.3
 */
public class GMGenSystemView extends JPanel implements ChangeListener
{
	/**
	 * A value that signifies the current pane that is showing. Is not directly
	 * used in this class, but is needed to support interface
	 * 
	 * TODO How can this support anything when Eclipse shows this is assigned
	 * (below) but never used? An interface is not requiring this, and can't
	 * because it's static... - thpr 10/27/06
	 */
	public static int currentPane;

	/**
	 *  The <code>JPanel</code> that holds the panes.
	 */
	private static JTabbedPane tabbedPane;

	/**
	 *  Creates an instance of this class. It creates the tabbed pane, sets the
	 *  layout, and registers all the listeners.
	 *
	 *@since    GMGen 3.3
	 */
	public GMGenSystemView()
	{
		/*
		 * TODO This is very strange - multiple instances of GMGenSystemView can
		 * theoretically be created, but then they will share a JTabbedPane
		 * because it is static?? - thpr 10/27/06
		 */
		tabbedPane = new JTabbedPane();
		initComponents();
		tabbedPane.addChangeListener(this);
	}

	/**
	 *  Gets the <code>JPanel</code> that is the tabbed pane.
	 *
	 *@return    the tabbed pane.
	 *@since     GMGen 3.3
	 */
	public static JTabbedPane getTabPane()
	{
		return tabbedPane;
	}

	/**
	 *  Inserts a pane into the panel in an arbitrary index. The system will call
	 *  it sending it a <code>JPanel</code> and that will be placed in the view.
	 *
	 *@param  paneName  the name to be on the tab.
	 *@param  pane      the pane to be displayed.
	 *@param  index     index to place the pane at
	 *@since            GMGen 3.3
	 */
	public void insertPane(String paneName, Component pane, int index)
	{
		tabbedPane.insertTab(paneName, null, pane, paneName, index);
	}

	/**
	 *  Places the whole <code>JTabbedPane</code> on the main frame setting it
	 *  visible.
	 *
	 *@since    GMGen 3.3
	 */
	public void showPane()
	{
		add(tabbedPane, java.awt.BorderLayout.CENTER);
	}

	/**
	 *  Updates the current pane value for further use.
	 *
	 *@param  e  an event that made the change change.
	 *@since     GMGen 3.3
	 */
    @Override
	public void stateChanged(ChangeEvent e)
	{
		currentPane = getTabPane().getSelectedIndex();
	}

	/**
	 *  Initializes the GUI components and sets up the layout being used.
	 *
	 *@since    GMGen 3.3
	 */
	private void initComponents()
	{
		setLayout(new java.awt.BorderLayout());
	}
}
