/*
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
 */
package plugin.notes.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;

import gmgen.GMGenSystem;
import pcgen.gui2.util.event.PopupMouseAdapter;
import pcgen.io.PCGFile;
import pcgen.util.Logging;
import plugin.notes.NotesPlugin;

/**
 *  JIcon is a small form that uses an image, a button and some text to
 *  represent a file. You can launch files in supported operating systems from
 *  JIcon into their associated application.
 */
class JIcon extends JPanel
{
	private final File launch;
	private final NotesPlugin plugin;

	private JButton button;
	private JLabel label;
	private JPopupMenu contextMenu;

	/**
	 * Creates new form JIcon
	 *
	 * @param name file object for the JIcon
	 * @param plugin
	 */
	JIcon(File name, NotesPlugin plugin)
	{
		this.plugin = plugin;
		initComponents();

		if (name.getName().length() > 18)
		{
			label.setText(' ' + name.getName().substring(0, 15) + "... ");
		}
		else
		{
			label.setText(' ' + name.getName() + ' ');
		}

		button.setIcon(getIconForType(name));
		button.setToolTipText(name.getName());
		this.launch = name;
	}

	/**
	 *  Gets the icon of the JIcon object
	 *
	 *@param  file  File name that this represents
	 *@return           The icon
	 */
	private Icon getIconForType(File file)
	{
		// TODO: this blows, it's hardcoded.  This needs to be in a properties file.
		// XXX ideally this should be use mime type
		String labelText = ' ' + file.getName() + ' ';

		if (file.getName().length() > 18)
		{
			labelText = ' ' + file.getName().substring(0, 15) + "... ";
		}
		label.setText(labelText);

		Icon ico = FileSystemView.getFileSystemView().getSystemIcon(file);
		button.setIcon(ico);
		button.setToolTipText(file.getName());
		return ico;
	}

	/**  Delete the file from disk that this icon represents */
	private void deleteFile()
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
			catch (RuntimeException e)
			{
				Logging.errorPrint("delete file failed", e);
			}
		}
	}

	/**
	 *  Launches a file into the appropriate program for the OS we are running on
	 */
	private void launchFile()
	{
		if (PCGFile.isPCGenCharacterOrPartyFile(launch))
		{
			plugin.loadRecognizedFileType(launch);
		}
		else
		{
			if (Desktop.isDesktopSupported())
			{
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.OPEN))
				{
					try
					{
						desktop.open(launch);
					}
					catch (IOException e)
					{
						Logging.errorPrint("failed to launch file", e);
					}
				}
			}
		}
	}

	private void buttonFocusGained(FocusEvent evt)
	{

		setBackground(SystemColor.textHighlight);
		button.setBackground(SystemColor.textHighlight);
	}

	private void buttonFocusLost(FocusEvent evt)
	{

		setBackground((Color) UIManager.getDefaults().get("Panel.background"));
		button.setBackground((Color) UIManager.getDefaults().get("Button.background"));
	}

	private void buttonKeyReleased(KeyEvent evt)
	{

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

	private void deleteMIActionPerformed(ActionEvent evt)
	{
		deleteFile();
	}

	/**
	 *  This method is called from within the constructor to initialize the form.
	 *  WARNING: Do NOT modify this code. The content of this method is always
	 *  regenerated by the Form Editor.
	 */
	private void initComponents()
	{

		contextMenu = new JPopupMenu();
		JMenuItem launchMI = new JMenuItem();
		JMenuItem deleteMI = new JMenuItem();
		button = new JButton();
		label = new JLabel();

		launchMI.setText("Launch File (enter)");
		launchMI.addActionListener(this::launchMIActionPerformed);

		contextMenu.add(launchMI);
		deleteMI.setText("Delete File (del)");
		deleteMI.addActionListener(this::deleteMIActionPerformed);

		contextMenu.add(deleteMI);

		setLayout(new BorderLayout());

		setBackground((Color) UIManager.getDefaults().get("Panel.background"));
		setBorder(new LineBorder(new Color(0, 0, 0)));
		button.setBackground((Color) UIManager.getDefaults().get("Button.background"));
		button.setBorder(null);

		button.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent evt)
			{
				buttonFocusGained(evt);
			}

			@Override
			public void focusLost(FocusEvent evt)
			{
				buttonFocusLost(evt);
			}
		});

		button.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent evt)
			{
				buttonKeyReleased(evt);
			}
		});

		button.addMouseListener(new PopupMouseAdapter()
		{
			@Override
			public void showPopup(final MouseEvent evt)
			{
				contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		});

		add(button, BorderLayout.NORTH);

		label.setBackground(new Color(204, 204, 204));
		add(label, BorderLayout.CENTER);
	}

	private void launchMIActionPerformed(ActionEvent evt)
	{

		launchFile();
	}
}
