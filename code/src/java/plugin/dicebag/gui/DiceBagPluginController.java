/*
 * Copyright 2003 (C) Ross M. Lodge
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
 */
package plugin.dicebag.gui;

import java.awt.Component;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import pcgen.core.SettingsHandler;
import pcgen.gui3.GuiUtility;
import plugin.dicebag.DiceBagPlugin;

import javafx.stage.FileChooser;

/**
 * <p>
 * The controler class for DiceBag plugin. Should handle all interface actions.
 * </p>
 */
public class DiceBagPluginController
{

	/** The model */
	private DiceBagPluginModel theModel = null;

	/** The view */
	private DiceBagPluginView theView = null;

	/**
	 * <p>
	 * Primary constructor for the DiceBagController object.
	 * </p>
	 */
	public DiceBagPluginController()
	{
		theModel = new DiceBagPluginModel();
		theView = new DiceBagPluginView(theModel);
		openInitialBags();
	}

	/**
	 * <p>
	 * Returns the root component of the view.
	 * </p>
	 * @return Component
	 */
	public Component getComponent()
	{
		return theView.getMainComponent();
	}

	/**
	 * <p>
	 * This static method opens a file save chooser and returns the chosen file.
	 * </p>
	 *
	 * @param bag
	 *          Bag to save
	 * @return Returns the file to save to
	 */
	static File chooseSaveFile(DiceBagModel bag)
	{
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
				"GMGen Dice Bag", "*.dbg"

		);
		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setSelectedExtensionFilter(extensionFilter);

		String bagPath = bag.getFilePath();
		if (bagPath != null)
		{
			File bagFile = new File(bagPath);
			fileChooser.setInitialDirectory(bagFile.getParentFile());
			fileChooser.setInitialFileName(bagFile.getName());
		}
		else
		{
			String sFile = SettingsHandler.getGMGenOption(DiceBagPlugin.LOG_NAME + ".LastFile",
					System.getProperty("user.dir"));
			fileChooser.setInitialDirectory(new File(sFile));
		}

		File file = GuiUtility.runOnJavaFXThreadNow(() ->
				fileChooser.showSaveDialog(null));

		if (file != null)
		{
			SettingsHandler.setGMGenOption(DiceBagPlugin.LOG_NAME + ".LastFile", file.getParent());
			return file;
		}

		return null;
	}

	/**
	 * <p>
	 * Requests the model to open a new dice bag.
	 * </p>
	 */
	public void fileNew()
	{
		theModel.addNewDicebag();
	}

	/**
	 * <p>
	 * Displays a file-open dialog box and processes the selected values.
	 * </p>
	 *
	 */
	public void fileOpen()
	{
		String sFile =
				SettingsHandler.getGMGenOption(DiceBagPlugin.LOG_NAME + ".LastFile", System.getProperty("user.dir"));
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
				"GMGen Dice Bag", "*.dbg"

		);
		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setSelectedExtensionFilter(extensionFilter);

		if (sFile != null)
		{
			File defaultFile = new File(sFile);
			fileChooser.setInitialDirectory(defaultFile.getParentFile());
			fileChooser.setInitialFileName(defaultFile.getName());
		}

		File selectedFile = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));

		if (selectedFile != null)
		{
			SettingsHandler.setGMGenOption(DiceBagPlugin.LOG_NAME + ".LastFile", selectedFile.getParent());
			theModel.loadDiceBag(selectedFile);
		}

	}

	/**
	 * <p>
	 * Saves the currently active bag (if it exists), using
	 * {@code chooseSaveFile()}.
	 * </p>
	 */
	public void fileSave()
	{
		if (theModel.getActiveBag() != null)
		{
			final File saveFile = chooseSaveFile(theModel.getActiveBag());

			if (saveFile != null)
			{
				if (saveFile.exists())
				{
					int choice = JOptionPane.showConfirmDialog(getComponent(), "File Exists, Overwrite?", "File Exists",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (choice == JOptionPane.NO_OPTION)
					{
						return;
					}
				}

				theModel.saveDiceBag(theModel.getActiveBag(), saveFile);
			}
		}
	}

	/**
	 * <p>
	 * Instructs the view to close all windows.
	 * </p>
	 */
	public void windowClosed()
	{
		theView.closeAll();
	}

	//opens bags that were open when the plugins last closed.
	protected void openInitialBags()
	{
		String lastFiles = SettingsHandler.getGMGenOption(DiceBagPlugin.LOG_NAME + "closeFiles", "");
		StringTokenizer tok = new StringTokenizer(lastFiles, "|");
		boolean noLoads = true;

		while (tok.hasMoreTokens())
		{
			String fileName = tok.nextToken();
			File file = new File(fileName);

			if (file.exists() && fileName.endsWith(".dbg"))
			{
				try
				{
					theModel.loadDiceBag(file);
					noLoads = false;
				}
				catch (Exception e)
				{
					// TODO: Exception needs to be handled
				}
			}
		}

		if (noLoads)
		{
			fileNew();
		}
	}
}
