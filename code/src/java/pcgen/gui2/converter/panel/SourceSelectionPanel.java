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
package pcgen.gui2.converter.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.gui2.converter.event.ProgressEvent;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui3.GuiUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;
import pcgen.system.PropertyContext;

import javafx.stage.DirectoryChooser;

/**
 * The Class {@code SourceSelectionPanel} gathers the source
 * folder for the conversion process from the user.
 * 
 * 
 */
public class SourceSelectionPanel extends ConvertSubPanel
{

	private File path = null;

	private JRadioButton[] radioButtons;

	private enum SourceFolder
	{
		DATA("Data directory", ConfigurationSettings.getPccFilesDir()), VENDORDATA("Vendor data directory",
				PCGenSettings.getVendorDataDir()), HOMEBREWDATA("Homebrew data directory",
						PCGenSettings.getHomebrewDataDir()), OTHER("Other directory", ".");

		private final String title;

		private File file;

		SourceFolder(String title, String fileName)
		{
			this.title = title;
			this.file = new File(fileName);
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
	public boolean returnAllowed()
	{
		return true;
	}

	@Override
	public void setupDisplay(JPanel panel, final CDOMObject pc)
	{
		panel.setLayout(new GridBagLayout());

		JLabel label = new JLabel("Please select the Source Directory to Convert: ");
		GridBagConstraints gbc = new GridBagConstraints();
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0, GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHWEST);
		gbc.insets = new Insets(50, 25, 10, 25);
		panel.add(label, gbc);

		JButton button = new JButton("Browse...");
		button.setMnemonic('r');
		button.addActionListener(arg0 -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setInitialDirectory(SourceFolder.OTHER.getFile());
			directoryChooser.showDialog(null);
			directoryChooser.setTitle("Please select the Source Directory to Convert");

			File file = GuiUtility.runOnJavaFXThreadNow(
					() -> directoryChooser.showDialog(null));
			if (file != null)
			{
				this.path = file;
				SourceFolder.OTHER.setFile(file);
				pc.put(ObjectKey.DIRECTORY, path);
				PropertyContext context = PCGenSettings.getInstance();
				context.setProperty(PCGenSettings.CONVERT_INPUT_PATH, path.getAbsolutePath());
				AbstractButton button1 = radioButtons[SourceFolder.OTHER.ordinal()];
				button1.setSelected(true);
				button1.setText(buildFolderText(SourceFolder.OTHER, file.getAbsolutePath()));
			}
		});

		radioButtons = new JRadioButton[SourceFolder.values().length];
		String selectedPath;
		File selectedFile = pc.get(ObjectKey.DIRECTORY);
		if (selectedFile != null)
		{
			selectedPath = selectedFile.getAbsolutePath();
		}
		else
		{
			PCGenSettings context = PCGenSettings.getInstance();
			selectedPath = context.getProperty(PCGenSettings.CONVERT_INPUT_PATH, null);
		}
		ButtonGroup group = new ButtonGroup();
		boolean haveSelected = false;
		Font font = panel.getFont();
		font = FontManipulation.plain(font);
		for (SourceFolder folder : SourceFolder.values())
		{
			JRadioButton pathButton = new JRadioButton();
			final SourceFolder buttonFolder = folder;
			pathButton.addActionListener(e -> {
                PCGenSettings context = PCGenSettings.getInstance();
                context.setProperty(PCGenSettings.CONVERT_INPUT_PATH, buttonFolder.getFile().getAbsolutePath());
                pc.put(ObjectKey.DIRECTORY, buttonFolder.getFile());
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
					PCGenSettings context = PCGenSettings.getInstance();
					context.setProperty(PCGenSettings.CONVERT_INPUT_PATH, path);
					selectedFile = folder.getFile();
				}
			}
			pathButton.setText(buildFolderText(folder, path));
			pathButton.setFont(font);
			radioButtons[folder.ordinal()] = pathButton;
			group.add(pathButton);
			if (folder == SourceFolder.OTHER)
			{
				Utility.buildRelativeConstraints(gbc, 1, GridBagConstraints.REMAINDER, 1.0, 0,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
			}
			else
			{
				Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
			}
			gbc.insets = new Insets(10, 25, 10, 25);
			panel.add(pathButton, gbc);

			if (folder == SourceFolder.OTHER)
			{
				Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.NONE,
					GridBagConstraints.NORTHEAST);
				gbc.insets = new Insets(10, 25, 10, 25);
				panel.add(button, gbc);
			}
		}
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1.0, 1.0,
			GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		panel.add(new JLabel(" "), gbc);

		if (!haveSelected)
		{
			if (selectedPath != null)
			{
				JRadioButton btn = radioButtons[SourceFolder.OTHER.ordinal()];
				btn.setSelected(true);
				selectedFile = new File(selectedPath);
				SourceFolder.OTHER.setFile(selectedFile);
				path = selectedFile;
				btn.setText(buildFolderText(SourceFolder.OTHER, selectedFile.getAbsolutePath()));
			}
			else if (radioButtons[SourceFolder.VENDORDATA.ordinal()].isEnabled())
			{
				JRadioButton btn = radioButtons[SourceFolder.VENDORDATA.ordinal()];
				btn.setSelected(true);
				selectedFile = SourceFolder.VENDORDATA.getFile();
			}
			else if (radioButtons[SourceFolder.HOMEBREWDATA.ordinal()].isEnabled())
			{
				JRadioButton btn = radioButtons[SourceFolder.HOMEBREWDATA.ordinal()];
				btn.setSelected(true);
				selectedFile = SourceFolder.HOMEBREWDATA.getFile();
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
	private static String buildFolderText(SourceFolder folder, String path)
	{
		return "<html><b>" + folder.getTitle() + ":</b> " + path + "</html>";
	}
}
