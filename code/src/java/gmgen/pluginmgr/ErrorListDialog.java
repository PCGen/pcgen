/*
 *  ErrorListDialog.java - Used to list I/O and plugin load errors
 *  :noTabs=false:
 *
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit by Slava Pestov Copyright (C) 2001
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gmgen.pluginmgr;

import gmgen.util.MiscUtilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 *  Description of the Class
 *  Don't use this class, it's a special case for the plugin code
 *
 *@author     Soulcatcher
 *@since        GMGen 3.3
 */
public class ErrorListDialog extends EnhancedDialog
{
	private JButton ok;
	private JButton pluginMgr;

	/**
	 *  Constructor for the ErrorListDialog object
	 *
	 *@param  frame                Description of the Parameter
	 *@param  title                Description of the Parameter
	 *@param  caption              Description of the Parameter
	 *@param  messages             Description of the Parameter
	 *@param  showPluginMgrButton  Description of the Parameter
	 *@since        GMGen 3.3
	 */
	public ErrorListDialog(Frame frame, String title, String caption, Vector messages, boolean showPluginMgrButton)
	{
		super(frame, title, true);

		JPanel content = new JPanel(new BorderLayout(12, 12));
		content.setBorder(new EmptyBorder(12, 12, 12, 12));
		setContentPane(content);

		Box iconBox = new Box(BoxLayout.Y_AXIS);
		iconBox.add(new JLabel(UIManager.getIcon("OptionPane.errorIcon")));
		iconBox.add(Box.createGlue());
		content.add(BorderLayout.WEST, iconBox);

		JPanel centerPanel = new JPanel(new BorderLayout());

		JLabel label = new JLabel(caption);
		label.setBorder(new EmptyBorder(0, 0, 6, 0));
		centerPanel.add(BorderLayout.NORTH, label);

		JList errors = new JList(messages);
		errors.setCellRenderer(new ErrorListCellRenderer());
		errors.setVisibleRowCount(Math.min(messages.size(), 4));

		// need this bullshit scroll bar policy for the preferred size
		// hack to work
		JScrollPane scrollPane = new JScrollPane(errors, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
		      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		Dimension size = scrollPane.getPreferredSize();
		size.width = Math.min(size.width, 400);
		scrollPane.setPreferredSize(size);

		centerPanel.add(BorderLayout.CENTER, scrollPane);

		content.add(BorderLayout.CENTER, centerPanel);

		Box buttons = new Box(BoxLayout.X_AXIS);
		buttons.add(Box.createGlue());

		ok = new JButton(MiscUtilities.getLocalization("common.ok", null));
		ok.addActionListener(new ActionHandler());

		if (showPluginMgrButton)
		{
			pluginMgr = new JButton(MiscUtilities.getLocalization("error-list.plugin-manager", null));
			pluginMgr.addActionListener(new ActionHandler());
			buttons.add(pluginMgr);
			buttons.add(Box.createHorizontalStrut(6));
		}

		buttons.add(ok);

		buttons.add(Box.createGlue());
		content.add(BorderLayout.SOUTH, buttons);

		getRootPane().setDefaultButton(ok);

		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
	}

	/**
	 *  Description of the Method
	 *@since        GMGen 3.3
	 */
	public void cancel()
	{
		dispose();
	}

	/**
	 *  Description of the Method
	 *@since        GMGen 3.3
	 */
	public void ok()
	{
		dispose();
	}

	/**
	 *  Description of the Class
	 *
	 *@author     Soulcatcher
	 *@since        GMGen 3.3
	 */
	public static class ErrorEntry
	{
		String path;
		String[] messages;

		/**
		 *  Constructor for the ErrorEntry object
		 *
		 *@param  path         Description of the Parameter
		 *@param  messageProp  Description of the Parameter
		 *@param  args         Description of the Parameter
		 *@since        GMGen 3.3
		 */
		public ErrorEntry(String path, String messageProp, Object[] args)
		{
			this.path = path;

			String message = MiscUtilities.getLocalization(messageProp, args);

			if (message == null)
			{
				message = "Undefined property: " + messageProp;
			}

			Vector tokenizedMessage = new Vector();
			int lastIndex = -1;

			for (int i = 0; i < message.length(); i++)
			{
				if (message.charAt(i) == '\n')
				{
					tokenizedMessage.addElement(message.substring(lastIndex + 1, i));
					lastIndex = i;
				}
			}

			if (lastIndex != message.length())
			{
				tokenizedMessage.addElement(message.substring(lastIndex + 1));
			}

			messages = new String[tokenizedMessage.size()];
			tokenizedMessage.copyInto(messages);
		}
	}

	/**
	 * ActionHandler
	 */
	public class ActionHandler implements ActionListener
	{
		/**
		 *  Description of the Method
		 *
		 *@param  evt  Description of the Parameter
		 *@since        GMGen 3.3
		 */
		public void actionPerformed(ActionEvent evt)
		{
			if (evt.getSource() == ok)
			{
				dispose();
			}
			else if (evt.getSource() == pluginMgr)
			{
				//new org.gjt.sp.jedit.pluginmgr.PluginManager(JOptionPane.getFrameForComponent(ErrorListDialog.this));
			}
		}
	}
}
