/*
 * LogWindow.java - Window to hold the Log Panel
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

import gmgen.util.LogUtilities;
import pcgen.core.SettingsHandler;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * LogWindow is the top-level frame for the logging window.
 * It contains a panel that implements all of the interface elements.
 *
 * @author Tod Milam
 */
public class LogWindow extends JFrame implements ActionListener
{
	private JMenuBar menu;
	private JMenuItem clear;
	private List dbgList = new ArrayList();
	private LogPanel panel;

	/**
	 * Handles the creation of its children and initial display duties.
	 *
	 * @deprecated Unused
	 */
	public LogWindow()
	{
		getContentPane().setLayout(new BorderLayout());
		panel = new LogPanel();
		getContentPane().add(panel, BorderLayout.CENTER);

		// set up menu items
		menu = new JMenuBar();
		setJMenuBar(menu);

		//JMenu file = new JMenu("File");
		//file.setMnemonic('F');
		//menu.add(file);
		//close = new JMenuItem("Close", 'l');
		//file.add(close);
		JMenu edit = new JMenu("Edit");
		edit.setMnemonic('E');
		menu.add(edit);
		clear = new JMenuItem("Clear", 'C');
		edit.add(clear);
		clear.addActionListener(this);

		JMenu debugLevel = new JMenu("Debug Level");
		debugLevel.setMnemonic('D');
		edit.add(debugLevel);

		int currDebugLvl = SettingsHandler.getGMGenOption("Logging.DebugLevel", 2);
		boolean isSelected;
		ButtonGroup bgroup = new ButtonGroup();
		String[] debugStrs =
		{
			"Minimum Debug Messages", "Error Messages", "Exceptions", "Plugin Communication", "Data Structures",
			"Standard Messages", "Major Function Entry/Exit", "All Function Entry/Exit", "Everything"
		};

		for (int i = 1; i < 10; i++)
		{
			isSelected = currDebugLvl == i;

			JRadioButtonMenuItem dbg = new JRadioButtonMenuItem(i + " " + debugStrs[i - 1], isSelected);
			bgroup.add(dbg);
			dbg.addActionListener(this);
			dbgList.add(dbg);
			debugLevel.add(dbg);
		}

		int iWinX = SettingsHandler.getGMGenOption("Logging.WindowX", 0);
		int iWinY = SettingsHandler.getGMGenOption("Logging.WindowY", 0);
		int iWinWidth = SettingsHandler.getGMGenOption("Logging.WindowWidth", 440);
		int iWinHeight = SettingsHandler.getGMGenOption("Logging.WindowHeight", 230);

		panel.setPreferredSize(new Dimension(iWinWidth - 5, iWinHeight - 5));
		pack();

		setTitle("GMGen Log Console");
		setLocation(iWinX, iWinY);
		setSize(iWinWidth, iWinHeight);

		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage(getClass().getResource("/pcgen/gui/resource/gmgen_icon.png"));
		setIconImage(img);

		setVisible(true);
	}
	 // end constructor

	/**
	 * Handle menu items.
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == null)
		{
			setVisible(false);
		}
		else if (e.getSource() == clear)
		{
			panel.clearCurrentTab();
			LogUtilities.inst().logMessage("LogWindow", "Clearing current tab");
		}

//TODO: Commented out as it has no effect whatsoever. Will remove next time I come across it.JK 2003-12-28
//		else if(dbgList.contains(e.getSource())) {
//			JMenuItem mnu = (JMenuItem)e.getSource();
//			try {
//				int level = Integer.parseInt(mnu.getText().substring(0,1));
//			}
//			catch(NumberFormatException ne) {
//			}
//		}
	}
	 // end actionPerformed
}
 // end class LogWindow
