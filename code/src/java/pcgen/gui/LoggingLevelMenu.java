/*
 * LoggingLevelMenu.java
 * Copyright 2007 (C) James Dempsey
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
 * Created on 11/09/2007
 *
 * $Id$
 */
package pcgen.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

/**
 * code>LoggingLevelMenu</code> is a menu which allows the user 
 * to control the level of logging in use.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
final class LoggingLevelMenu extends JMenu
{
	static final long serialVersionUID = -6751569845505079621L;
	private ButtonGroup levelGroup = null;
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private JRadioButtonMenuItem[] levelMenuItems = null;
	private List<Level> levelList = new ArrayList<Level>();

	/**
	 * Create a new LoggingLevelMenu instance based on the supported 
	 * log levels.
	 */
	LoggingLevelMenu()
	{
		try
		{
			buildMenu();
			setText(LanguageBundle.getString("in_mnuLoggingLevel"));
			setMnemonic(LanguageBundle.getMnemonic("in_mn_mnuLoggingLevel"));
			Utility.setDescription(this, LanguageBundle.getString("in_mnuLoggingLevelTip"));
			updateMenu();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Exception while initing the form", e);
		}
	}

	/**
	 * Create the sub components of the menu, one submenu for 
	 * each supported logging level. 
	 */
	private void buildMenu()
	{
		levelList = Logging.getLoggingLevels();
		final int levelCount = levelList.size();

		if (levelList.size() == 0)
		{
			return;
		}

		levelMenuItems = new JRadioButtonMenuItem[levelCount];

		levelGroup = new ButtonGroup();
		for (int i = 0; i < levelCount; ++i)
		{
			JMenu mnuLevel = this;

			Level lvl = levelList.get(i);

			//
			// If there are more tokens, then add a JMenu with this description
			// unless one already exists.
			//
			levelMenuItems[i] =
					new JRadioButtonMenuItem(LanguageBundle
						.getString("in_loglvl" + lvl.getName()), false);
			levelGroup.add(mnuLevel.add(levelMenuItems[i]));
			Utility.setDescription(levelMenuItems[i], LanguageBundle.getString("in_loglvl" + lvl.getName() + "Tip"));
			levelMenuItems[i].addActionListener(checkBoxHandler);
			add(levelMenuItems[i]);
		}

		//
		// Look for &'s in the menu text...translate into mnemonic. NOTE "&&" translates to "&"
		//
		for (int i = 0; i < levelMenuItems.length; ++i)
		{
			Utility.setTextAndMnemonic(levelMenuItems[i], levelMenuItems[i].getText());
		}
	}

	/**
	 * Update the selected menu item to match the currently 
	 * selected logging level. 
	 */
	public void updateMenu()
	{
		if (levelMenuItems != null)
		{
			for (int i = 0; i < levelMenuItems.length; ++i)
			{
				if (Logging.getCurrentLoggingLevel() == levelList.get(i))
				{
					levelMenuItems[i].setSelected(true);
					break;
				}
			}
		}
	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private final class CheckBoxListener implements ActionListener
	{
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();
			if (source == null)
			{
				return;
			}

			// Switch to the selected logging level
			for (int i = 0; i < levelMenuItems.length; i++)
			{
				if (source == levelMenuItems[i])
				{
					Logging.setCurrentLoggingLevel(levelList.get(i));
				}
			}
		}
	}
}
