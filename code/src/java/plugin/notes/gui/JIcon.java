/*
 *  JIcon.java - 'icon' used for launching files from the notes plugin
 *  Copyright (C) 2003 Devon Jones
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
package plugin.notes.gui;

import gmgen.GMGenSystem;
import pcgen.util.Logging;
import plugin.notes.NotesPlugin;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.Container;
import java.awt.SystemColor;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 *  JIcon is a snall form that uses an image, a button and some text to
 *  represent a file. You can launch files in supported operating systems from
 *  JIcon into their associated application.
 *
 *@author     soulcatcher
 *@since    August 1, 2003, 4:48 PM
 */
public class JIcon extends JPanel
{
	/**  Boolean true if this is a Macintosh systems */
	public static final boolean MAC_OS_X = (System.getProperty("os.name").equals("Mac OS X"));
	File launch;
	NotesPlugin plugin;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JButton button;
	private JLabel label;
	private JMenuItem deleteMI;
	private JMenuItem launchMI;
	private JPopupMenu contextMenu;

	/**
	 *  Creates new form JIcon
	 *
	 *@param  name  Name of the file to load (full path)
	 * @param plugin
	 */
	public JIcon(File name, NotesPlugin plugin)
	{
		this.plugin = plugin;
		initComponents();

		if (name.getName().length() > 18)
		{
			label.setText(" " + name.getName().substring(0, 15) + "... ");
		}
		else
		{
			label.setText(" " + name.getName() + " ");
		}

		button.setIcon(getIconForType(name.getName()));
		button.setToolTipText(name.getName());
		this.launch = name;
	}

	/**
	 *  Gets the icon of the JIcon object
	 *
	 *@param  filename  File name that this represents
	 *@return           The icon
	 */
	public ImageIcon getIconForType(String filename)
	{
		// TODO: this blows, it's hardcoded.  This nees to be in a properties file.
		String ext = filename.replaceFirst(".*\\.", "");

		if (ext.equalsIgnoreCase("html") || ext.equalsIgnoreCase("htm"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/gnome-text-html.png"));
		}
		else if (ext.equalsIgnoreCase("doc"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/win-word.png"));
		}
		else if (ext.equalsIgnoreCase("pdf"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/win-acrobat.png"));
		}
		else if (ext.equalsIgnoreCase("rtf"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/gnome-application-rtf.png"));
		}
		else if (ext.equalsIgnoreCase("xls"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/win-excel.png"));
		}
		else if (ext.equalsIgnoreCase("ppt"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/gnome-application-vnd.ms-powerpoint.png"));
		}
		else if (ext.equalsIgnoreCase("txt"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/gnome-text-plain.png"));
		}
		else if (ext.equalsIgnoreCase("fcw"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/win-cc2.png"));
		}
		else if (ext.equalsIgnoreCase("zip"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/win-zip.png"));
		}
		else if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("png"))
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/gnome-image-generic.png"));
		}
		else
		{
			return new ImageIcon(getClass().getResource("/pcgen/gui/resource/gnome-generic.png"));
		}
	}

	/**  Delete the file from disk that this icon represents */
	protected void deleteFile()
	{
		int choice = JOptionPane.showConfirmDialog(GMGenSystem.inst, "Delete file " + launch.getPath(), "Delete File?",
			    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (choice == JOptionPane.YES_OPTION)
		{
			try
			{
				launch.delete();

				Container cnt = getParent();
				cnt.remove(this);

				if (cnt instanceof JComponent)
				{
					JComponent comp = (JComponent) cnt;
					comp.updateUI();
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	//GEN-LAST:event_buttonActionPerformed

	/**
	 *  Launches a file into the appropriate program for the OS we are running on
	 */
	protected void launchFile()
	{
		if (plugin.isRecognizedFileType(launch))
		{
			plugin.loadRecognizedFileType(launch);
		}
		else
		{
			//Mac OS X
			if (MAC_OS_X)
			{
				//
				// From the command line, the open command acts as if the argument was double clicked from the finder
				// (see: man open)
				//
				String openCmd = ("/usr/bin/open");
				String filePath = (launch.getAbsolutePath());
				String[] args = { openCmd, filePath };
				System.err.println("Runtime.getRuntime().exec: [" + args[0] + "] [" + args[1] + "]");

				try
				{
					Runtime.getRuntime().exec(args);
				}
				catch (IOException e)
				{
					Logging.errorPrint(e.getMessage(), e);
				}
			}

			//Windows
			else
			{
				try
				{
					String start = (" rundll32 url.dll,FileProtocolHandler file://" + launch.getAbsoluteFile());
					Runtime.getRuntime().exec(start);
				}
				catch (Exception e)
				{
					Logging.errorPrint(e.getMessage(), e);
				}
			}
		}
	}

	//GEN-LAST:event_buttonMouseClicked
	private void buttonActionPerformed(ActionEvent evt)
	{
		//GEN-FIRST:event_buttonActionPerformed
	}

	//GEN-LAST:event_buttonFocusLost
	private void buttonFocusGained(FocusEvent evt)
	{
		//GEN-FIRST:event_buttonFocusGained
		setBackground(SystemColor.textHighlight);
		button.setBackground(SystemColor.textHighlight);
	}

	//GEN-LAST:event_buttonKeyReleased
	private void buttonFocusLost(FocusEvent evt)
	{
		//GEN-FIRST:event_buttonFocusLost
		setBackground((Color) UIManager.getDefaults().get("Panel.background"));
		button.setBackground((Color) UIManager.getDefaults().get("Button.background"));
	}

	//GEN-LAST:event_launchMIActionPerformed
	private void buttonKeyReleased(KeyEvent evt)
	{
		//GEN-FIRST:event_buttonKeyReleased
		int key = evt.getKeyCode();

		if (key == KeyEvent.VK_DELETE)
		{
			deleteFile();
		}
		else if (key == KeyEvent.VK_ENTER)
		{
			launchFile();
		}
	}

	//GEN-LAST:event_buttonFocusGained
	private void buttonMouseClicked(MouseEvent evt)
	{
		//GEN-FIRST:event_buttonMouseClicked
		if (evt.getClickCount() >= 2)
		{
			launchFile();
		}
	}

	//GEN-END:initComponents
	private void buttonMouseReleased(MouseEvent evt)
	{
		//GEN-FIRST:event_buttonMouseReleased
		if (evt.isPopupTrigger())
		{
			contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	//GEN-LAST:event_buttonMouseReleased
	private void deleteMIActionPerformed(ActionEvent evt)
	{
		//GEN-FIRST:event_deleteMIActionPerformed
		deleteFile();
	}

	/**
	 *  This method is called from within the constructor to initialize the form.
	 *  WARNING: Do NOT modify this code. The content of this method is always
	 *  regenerated by the Form Editor.
	 */
	private void initComponents()
	{
		//GEN-BEGIN:initComponents
		contextMenu = new JPopupMenu();
		launchMI = new JMenuItem();
		deleteMI = new JMenuItem();
		button = new JButton();
		label = new JLabel();

		launchMI.setText("Launch File (enter)");
		launchMI.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					launchMIActionPerformed(evt);
				}
			});

		contextMenu.add(launchMI);
		deleteMI.setText("Delete File (del)");
		deleteMI.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					deleteMIActionPerformed(evt);
				}
			});

		contextMenu.add(deleteMI);

		setLayout(new BorderLayout());

		setBackground((Color) UIManager.getDefaults().get("Panel.background"));
		setBorder(new LineBorder(new Color(0, 0, 0)));
		button.setBackground((Color) UIManager.getDefaults().get("Button.background"));
		button.setBorder(null);
		button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					buttonActionPerformed(evt);
				}
			});

		button.addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent evt)
				{
					buttonFocusGained(evt);
				}

				public void focusLost(FocusEvent evt)
				{
					buttonFocusLost(evt);
				}
			});

		button.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent evt)
				{
					buttonKeyReleased(evt);
				}
			});

		button.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					buttonMouseClicked(evt);
				}

				public void mouseReleased(MouseEvent evt)
				{
					buttonMouseReleased(evt);
				}
			});

		add(button, BorderLayout.NORTH);

		label.setBackground(new Color(204, 204, 204));
		add(label, BorderLayout.CENTER);
	}

	//GEN-LAST:event_deleteMIActionPerformed
	private void launchMIActionPerformed(ActionEvent evt)
	{
		//GEN-FIRST:event_launchMIActionPerformed
		launchFile();
	}

	// End of variables declaration//GEN-END:variables
}
