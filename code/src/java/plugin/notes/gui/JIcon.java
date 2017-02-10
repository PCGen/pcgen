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
 */
package plugin.notes.gui;

import gmgen.GMGenSystem;
import pcgen.util.Logging;
import plugin.notes.NotesPlugin;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.apache.commons.lang3.SystemUtils;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.SystemColor;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import pcgen.gui2.tools.Icons;

/**
 *  JIcon is a small form that uses an image, a button and some text to
 *  represent a file. You can launch files in supported operating systems from
 *  JIcon into their associated application.
 *
 */
public class JIcon extends JPanel
{
	private File launch;
	NotesPlugin plugin;

	// Variables declaration - do not modify                     
	private JButton button;
	private JLabel label;
	private JMenuItem deleteMI;
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
		// TODO: this blows, it's hardcoded.  This needs to be in a properties file.
		// XXX ideally this should be use mime type
		String ext = filename.replaceFirst(".*\\.", "");

		if (ext.equalsIgnoreCase("html") || ext.equalsIgnoreCase("htm"))
		{
			return Icons.createImageIcon("gnome-text-html.png");
		}
		else if (ext.equalsIgnoreCase("doc"))
		{
			return Icons.createImageIcon("win-word.png");
		}
		else if (ext.equalsIgnoreCase("pdf"))
		{
			return Icons.createImageIcon("win-acrobat.png");
		}
		else if (ext.equalsIgnoreCase("rtf"))
		{
			return Icons.createImageIcon("gnome-application-rtf.png");
		}
		else if (ext.equalsIgnoreCase("xls"))
		{
			return Icons.createImageIcon("win-excel.png");
		}
		else if (ext.equalsIgnoreCase("ppt"))
		{
			return Icons.createImageIcon("gnome-application-vnd.ms-powerpoint.png");
		}
		else if (ext.equalsIgnoreCase("txt"))
		{
			return Icons.createImageIcon("gnome-text-plain.png");
		}
		else if (ext.equalsIgnoreCase("fcw"))
		{
			return Icons.createImageIcon("win-cc2.png");
		}
		else if (ext.equalsIgnoreCase("zip"))
		{
			return Icons.createImageIcon("win-zip.png");
		}
		else if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("gif")
			|| ext.equalsIgnoreCase("png"))
		{
			return Icons.createImageIcon("gnome-image-generic.png");
		}
		else
		{
			return Icons.createImageIcon("gnome-generic.png");
		}
	}

	/**  Delete the file from disk that this icon represents */
	protected void deleteFile()
	{
		int choice =
				JOptionPane.showConfirmDialog(GMGenSystem.inst, "Delete file "
					+ launch.getPath(), "Delete File?",
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
			boolean opened = false;
			
			// Use desktop if available
			if (Desktop.isDesktopSupported())
			{
				Desktop d = Desktop.getDesktop();
				if (d.isSupported(Desktop.Action.OPEN))
				{
					try
					{
						d.open(launch);
						opened = true;
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (!opened)
			{
				// Unix
				if (SystemUtils.IS_OS_UNIX )
				{
					String openCmd;
					if (SystemUtils.IS_OS_MAC_OSX)
					{
						// From the command line, the open command acts as if the argument was double clicked from the finder
						// (see: man open)
						openCmd = "/usr/bin/open";
					}
					else
					{
						// Tries freedesktop.org xdg-open. Quite often installed on Linux/BSD
						openCmd = "xdg-open";
					}
					String filePath = launch.getAbsolutePath();
					String[] args = {openCmd, filePath};
					Logging.debugPrintLocalised("Runtime.getRuntime().exec: [{0}] [{1}]", args[0], args[1]);
	
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
				if(SystemUtils.IS_OS_WINDOWS)
				{
					try
					{
						String start =
								(" rundll32 url.dll,FileProtocolHandler file://" + launch
									.getAbsoluteFile());
						Runtime.getRuntime().exec(start);
					}
					catch (Exception e)
					{
						Logging.errorPrint(e.getMessage(), e);
					}
				}
			}
		}
	}

	                                   
	private void buttonActionPerformed(ActionEvent evt)
	{
		                                       
	}

	                                
	private void buttonFocusGained(FocusEvent evt)
	{
		                                   
		setBackground(SystemColor.textHighlight);
		button.setBackground(SystemColor.textHighlight);
	}

	                                  
	private void buttonFocusLost(FocusEvent evt)
	{
		                                 
		setBackground((Color) UIManager.getDefaults().get("Panel.background"));
		button.setBackground((Color) UIManager.getDefaults().get(
			"Button.background"));
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

	                                  
	private void buttonMouseClicked(MouseEvent evt)
	{
		                                    
		if (evt.getClickCount() >= 2)
		{
			launchFile();
		}
	}

	                        
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
		deleteMI = new JMenuItem();
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
		button.setBackground((Color) UIManager.getDefaults().get(
			"Button.background"));
		button.setBorder(null);
		button.addActionListener(this::buttonActionPerformed);

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

		button.addMouseListener(new MouseAdapter()
		{
            @Override
			public void mouseClicked(MouseEvent evt)
			{
				buttonMouseClicked(evt);
			}

            @Override
			public void mouseReleased(MouseEvent evt)
			{
				buttonMouseReleased(evt);
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

	// End of variables declaration                   
}
