/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui.converter.panel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SettingsHandler;
import pcgen.gui.converter.event.ProgressEvent;

public class SourceSelectionPanel extends ConvertSubPanel
{

	private File path = null;

	private SpringLayout layout = new SpringLayout();

	private JRadioButton radioButtons[];
	
	private enum SourceFolder {
		DATA ("Data directory", SettingsHandler.getPccFilesLocation()),
		VENDORDATA ("Vendor data directory", SettingsHandler.getPcgenVendorDataDir()),
		OTHER ("Other directory", new File("."));
		
		private final String title;

		private File file;
		
		SourceFolder(String title, File file)
		{
			this.title = title;
			this.file = file;
		}
		
		/**
		 * @return the file
		 */
		public File getFile()
		{
			return file;
		}

		/**
		 * @param file the file to set
		 */
		public void setFile(File file)
		{
			this.file = file;
		}

		/**
		 * @return the title
		 */
		public String getTitle()
		{
			return title;
		}
	}

	public SourceSelectionPanel()
	{
	}

	public String getPath()
	{
		return path.getAbsolutePath();
	}

	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		fireProgressEvent(ProgressEvent.ALLOWED);
		return true;
	}

	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	@Override
	public void setupDisplay(JPanel panel, final CDOMObject pc)
	{
		panel.setLayout(layout);
		JLabel label = new JLabel(
				"Please select the Source Directory to Convert: ");

		JButton button = new JButton("Browse...");
		button.setMnemonic('r');
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser chooser = new JFileChooser(SourceFolder.OTHER.getFile());
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setSelectedFile(path);
				while (true)
				{
					int open = chooser.showOpenDialog(null);
					if (open == JFileChooser.APPROVE_OPTION)
					{
						File fileToOpen = chooser.getSelectedFile();
						if (fileToOpen.isDirectory())
						{
							path = fileToOpen;
							SourceFolder.OTHER.setFile(fileToOpen);
							pc.put(ObjectKey.DIRECTORY, path);
							JRadioButton button = radioButtons[SourceFolder.OTHER.ordinal()];
							button.setSelected(true);
							button.setText(buildFolderText(SourceFolder.OTHER, fileToOpen.getAbsolutePath()));
							break;
						}
						JOptionPane.showMessageDialog(null,
								"Selection must be a valid Directory");
						chooser.setSelectedFile(path);
					}
					else if (open == JFileChooser.CANCEL_OPTION)
					{
						break;
					}
				}
			}
		});
		panel.add(label);
		layout.putConstraint(SpringLayout.NORTH, label, 50, SpringLayout.NORTH,
				panel);
		layout.putConstraint(SpringLayout.WEST, label, 25, SpringLayout.WEST,
				panel);
		
		radioButtons = new JRadioButton[SourceFolder.values().length];
		String selectedPath = null;
		File selectedFile = pc.get(ObjectKey.DIRECTORY);
		if (selectedFile != null)
		{
			selectedPath = selectedFile.getAbsolutePath();
		}
		JComponent prevComp = label; 
		ButtonGroup group = new ButtonGroup();
		boolean haveSelected = false;
		Font font = panel.getFont();
		font = font.deriveFont(Font.PLAIN);
		for (SourceFolder folder : SourceFolder.values())
		{
			JRadioButton pathButton = new JRadioButton();
			final SourceFolder buttonFolder = folder;
			pathButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e)
				{
					pc.put(ObjectKey.DIRECTORY, buttonFolder.getFile());
				}
			});
			
			String path;
			if (folder.getFile() == null)
			{
				path = "Undefined";
				pathButton.setEnabled(false);
			}
			else
			{
				path = folder.getFile().getAbsolutePath();
				if (path.equals(selectedPath))
				{
					pathButton.setSelected(true);
					haveSelected = true;
				}
			}
			pathButton.setText(buildFolderText(folder, path));
			pathButton.setFont(font);
			radioButtons[folder.ordinal()] = pathButton;
			group.add(pathButton);
			panel.add(pathButton);
			layout.putConstraint(SpringLayout.NORTH, pathButton, 25,
				SpringLayout.SOUTH, prevComp);
			layout.putConstraint(SpringLayout.WEST, pathButton, 25,
				SpringLayout.WEST, panel);

			if (folder == SourceFolder.OTHER)
			{
				panel.add(button);
				layout.putConstraint(SpringLayout.NORTH, button, 0,
					SpringLayout.NORTH, pathButton);
				layout.putConstraint(SpringLayout.EAST, button, -50, SpringLayout.EAST,
					panel);
				layout.putConstraint(SpringLayout.EAST, pathButton, -20,
					SpringLayout.WEST, button);
			}
			prevComp = pathButton;
		}
		
		if (!haveSelected)
		{
			if (radioButtons[SourceFolder.VENDORDATA.ordinal()].isEnabled())
			{
				JRadioButton btn = radioButtons[SourceFolder.VENDORDATA.ordinal()];
				btn.setSelected(true);
				selectedFile = SourceFolder.VENDORDATA.getFile();
			}
			else
			{
				JRadioButton btn = radioButtons[SourceFolder.DATA.ordinal()];
				btn.setSelected(true);
				selectedFile = SourceFolder.DATA.getFile();
			}
		}
		
		pc.put(ObjectKey.DIRECTORY, selectedFile);
				
	}

	/**
	 * Create the label text label for a folder and path. Normally used on the 
	 * radio buttons.
	 * @param folder The folder to be shown
	 * @param path The path to be shown.
	 * @return The new html label text
	 */
	private String buildFolderText(SourceFolder folder, String path)
	{
		return "<html><b>" + folder.getTitle() + ":</b> " + path + "</html>";
	}
}
