/*
 * LogPanel.java - The tabbed Logging window for GMGen
 * Copyright (C) 2003 Tod Milam
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
 * Created on May 24, 2003
 */
package gmgen.gui;

import gmgen.util.LogReceiver;
import gmgen.util.LogUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import pcgen.cdom.base.Constants;

/**
 * LogPanel holds the interface elements for the logging window.
 * It contains tabs for each owner that logs a message, and provides elements
 * to manipulate the contents (such as clearing the log).
 *
 * @author Tod Milam
 */
public class LogPanel extends JPanel implements LogReceiver
{
	private static final String defaultOwner = "General";
	private JTabbedPane pane;

	/**
	 * Default constructor.
	 * Initializes the window elements.
	 */
	public LogPanel()
	{
		pane = new JTabbedPane();
		pane.addTab(defaultOwner, new TabContents());

		setLayout(new GridLayout(1, 1));
		add(pane);

		// register to receive log messages
		LogUtilities.inst().addReceiver(this);
	}
	 // end constructor

	/**
	 * Clear the text from the current tab.
	 */
	public void clearCurrentTab()
	{
		TabContents tab = (TabContents) pane.getSelectedComponent();
		tab.clearContents();
	}
	 // end clearCurrentTab

	// log a message associated with a specific owner - from LogReceiver
	public void logMessage(String owner, String message)
	{
		int index = pane.indexOfTab(owner);
		TabContents tab;

		if (index == -1)
		{
			pane.addTab(owner, new TabContents());
			index = pane.indexOfTab(owner);
			pane.setSelectedIndex(index);
		}
		 // end if we need to add a new tab

		tab = (TabContents) pane.getComponentAt(index);
		tab.addString(message);
	}
	 // end logMessage - 2 params

	// log a message without an owner - from LogReceiver
	public void logMessage(String message)
	{
		logMessage(defaultOwner, message);
	}
	 // end logMessage - 1 param

	/**
	 * TabContents defines the layout of each debug tab.
	 *
	 * Most of this layout is copied from pcgen.gui.MainDebug which is
	 * Copyright 2001 (C) Bryan McRoberts
	 *
	 * The rest of it is
	 * Copyright 2003 (C) Tod Milam
	 */
	private static class TabContents extends JPanel
	{
		private JScrollPane debugCenter = new JScrollPane();
		private JTextArea txtAreaDebug = new JTextArea();

		/**
		 * Constructor
		 */
		public TabContents()
		{
			this.setLayout(new BorderLayout());
			debugCenter.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			debugCenter.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			debugCenter.setDoubleBuffered(true);
			debugCenter.setPreferredSize(new Dimension(446, 37));
			this.add(debugCenter, BorderLayout.CENTER);
			txtAreaDebug.setLineWrap(true);
			txtAreaDebug.setWrapStyleWord(true);
			txtAreaDebug.setDoubleBuffered(true);
			txtAreaDebug.setMinimumSize(new Dimension(426, 17));
			txtAreaDebug.setEditable(false);
			debugCenter.getViewport().add(txtAreaDebug, null);
		}
		 // end constructor

		/**
		 * Add the message
		 * @param msg
		 */
		public void addString(String msg)
		{
			txtAreaDebug.append(msg + Constants.LINE_SEPARATOR);
		}
		 // end addString

		/**
		 * Clear the contents
		 */
		public void clearContents()
		{
			txtAreaDebug.setText("");
		}
		 // end clearContents
	}
	 // end class TabContents
}
 // end class LogPanel
