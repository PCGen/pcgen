/*
 * Copyright 2010(C) James Dempsey
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
package pcgen.gui3.preferences;

import java.io.File;

import pcgen.cdom.base.Constants;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.ResettableController;
import pcgen.system.ConfigurationSettings;
import pcgen.system.ConfigurationSettings.SettingsFilesPath;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.SystemUtils;

/** Loads, edits, and saves the directory preferences displayed by the location FXML form. */
public final class LocationPreferencesPanelController implements ResettableController
{
	@FXML
	private RadioButton pcgenFilesDirRadio;
	@FXML
	private RadioButton selectFilesDirRadio;
	@FXML
	private RadioButton usersFilesDirRadio;
	@FXML
	private CheckBox pcgenCreateBackupCharacter;

	@FXML
	private TextField pcgenCharacterDir;
	@FXML
	private TextField pcgenCustomDir;
	@FXML
	private TextField pcgenVendorDataDir;
	@FXML
	private TextField pcgenHomebrewDataDir;
	@FXML
	private TextField pcgenDataDir;
	@FXML
	private TextField pcgenDocsDir;
	@FXML
	private TextField pcgenFilesDir;
	@FXML
	private TextField pcgenOutputSheetDir;
	@FXML
	private TextField pcgenBackupCharacterDir;
	@FXML
	private TextField pcgenPreviewDir;
	@FXML
	private TextField pcgenPortraitsDir;
	@FXML
	private TextField pcgenSystemDir;

	/** Formats the application-specific label and loads the saved preferences after FXML injection. */
	@FXML
	private void initialize()
	{
		pcgenFilesDirRadio.setText(LanguageBundle.getFormattedString("in_Prefs_progDir", Constants.APPLICATION_NAME));
		reset();
	}

	/** Restores the directory paths, settings location choice, and backup preference. */
	@Override
	public void reset()
	{
		GuiAssertions.assertIsJavaFXThread();
		pcgenCharacterDir.setText(String.valueOf(PCGenSettings.getPcgDir()));
		pcgenPortraitsDir.setText(String.valueOf(PCGenSettings.getPortraitsDir()));
		pcgenBackupCharacterDir.setText(String.valueOf(PCGenSettings.getBackupPcgDir()));
		pcgenDataDir.setText(String.valueOf(ConfigurationSettings.getPccFilesDir()));
		pcgenCustomDir.setText(String.valueOf(PCGenSettings.getCustomDir()));
		pcgenVendorDataDir.setText(String.valueOf(PCGenSettings.getVendorDataDir()));
		pcgenHomebrewDataDir.setText(String.valueOf(PCGenSettings.getHomebrewDataDir()));
		pcgenDocsDir.setText(String.valueOf(ConfigurationSettings.getDocsDir()));
		pcgenSystemDir.setText(String.valueOf(ConfigurationSettings.getSystemsDir()));
		pcgenOutputSheetDir.setText(String.valueOf(ConfigurationSettings.getOutputSheetsDir()));
		pcgenPreviewDir.setText(String.valueOf(ConfigurationSettings.getPreviewDir()));
		String fType = ConfigurationSettings.getSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH);

		if ((fType == null) || (fType.length() < 1))
		{
			// make sure we have a default
			fType = ConfigurationSettings.getDefaultSettingsFilesPath();
			ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH, fType);
		}

		pcgenFilesDir.setText(ConfigurationSettings.getSettingsDir());
		if (fType.equals(SettingsFilesPath.pcgen.name()))
		{
			pcgenFilesDirRadio.setSelected(true);
		}
		else if (fType.equals(SettingsFilesPath.user.name()) || fType.equals(SettingsFilesPath.mac_user.name())
			|| fType.equals(SettingsFilesPath.FD_USER.name()))
		{
			usersFilesDirRadio.setSelected(true);
		}
		else
		{
			selectFilesDirRadio.setSelected(true);
		}

		pcgenCreateBackupCharacter
			.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_CREATE_PCG_BACKUP));
	}

	/** Selects the settings directory inside the application installation. */
	@FXML
	private void selectProgramSettings()
	{
		pcgenFilesDir.setText(SettingsFilesPath.pcgen.getSettingsDir());
	}

	/** Selects the platform-specific settings directory in the user's home. */
	@FXML
	private void selectUserSettings()
	{
		pcgenFilesDir.setText(ConfigurationSettings.getUserSettingsDirFromFilePath());
	}

	/** Clears the settings path so the user can choose a custom directory. */
	@FXML
	private void selectCustomSettings()
	{
		pcgenFilesDir.setText("");
	}

	/** Saves the edited directory paths and backup preference. */
	@Override
	public void apply()
	{
		GuiAssertions.assertIsJavaFXThread();
		var settings = PCGenSettings.getInstance();
		settings.setProperty(PCGenSettings.PCG_SAVE_PATH, pcgenCharacterDir.getText());
		settings.setProperty(PCGenSettings.CHAR_PORTRAITS_PATH, pcgenPortraitsDir.getText());
		settings.setProperty(PCGenSettings.CUSTOM_DATA_DIR, pcgenCustomDir.getText());
		settings.setProperty(PCGenSettings.VENDOR_DATA_DIR, pcgenVendorDataDir.getText());
		settings.setProperty(PCGenSettings.HOMEBREW_DATA_DIR, pcgenHomebrewDataDir.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.PCC_FILES_DIR, pcgenDataDir.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.DOCS_DIR, pcgenDocsDir.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.SYSTEMS_DIR, pcgenSystemDir.getText());
		if (pcgenFilesDirRadio.isSelected())
		{
			ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH,
				SettingsFilesPath.pcgen.name());
		}
		else if (usersFilesDirRadio.isSelected())
		{
			if (SystemUtils.IS_OS_MAC_OSX)
			{
				ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH,
					SettingsFilesPath.mac_user.name());
			}
			else
			{
				ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH,
					SettingsFilesPath.user.name());
			}
		}
		else
		{
			ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH, pcgenFilesDir.getText());
		}
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH, pcgenFilesDir.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.OUTPUT_SHEETS_DIR, pcgenOutputSheetDir.getText());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_CREATE_PCG_BACKUP,
			pcgenCreateBackupCharacter.isSelected());
		settings.setProperty(PCGenSettings.BACKUP_PCG_PATH, pcgenBackupCharacterDir.getText());

		ConfigurationSettings.setSystemProperty(ConfigurationSettings.PREVIEW_DIR, pcgenPreviewDir.getText());
	}

	/** Chooses the directory for character files. */
	@FXML
	private void chooseCharacterDirectory()
	{
		askForPath(PCGenSettings.getPcgDir(), "in_Prefs_pcgenCharacterDirTitle", pcgenCharacterDir);
	}

	/** Chooses the directory for character backups. */
	@FXML
	private void chooseBackupDirectory()
	{
		askForPath(PCGenSettings.getBackupPcgDir(), "in_Prefs_pcgenBackupCharacterDirTitle", pcgenBackupCharacterDir);
	}

	/** Chooses the directory for portraits. */
	@FXML
	private void choosePortraitsDirectory()
	{
		askForPath(PCGenSettings.getPortraitsDir(), "in_Prefs_pcgenPortraitDirTitle", pcgenPortraitsDir);
	}

	/** Chooses the directory for custom data. */
	@FXML
	private void chooseCustomDataDirectory()
	{
		askForPath(PCGenSettings.getCustomDir(), "in_Prefs_pcgenCustomDirTitle", pcgenCustomDir);
	}

	/** Chooses the directory for vendor data. */
	@FXML
	private void chooseVendorDataDirectory()
	{
		askForPath(PCGenSettings.getVendorDataDir(), "in_Prefs_pcgenVendorDataDirTitle", pcgenVendorDataDir);
	}

	/** Chooses the directory for homebrew data. */
	@FXML
	private void chooseHomebrewDataDirectory()
	{
		askForPath(PCGenSettings.getHomebrewDataDir(), "in_Prefs_pcgenHomebrewDataDirTitle", pcgenHomebrewDataDir);
	}

	/** Chooses the directory for campaign data. */
	@FXML
	private void chooseDataDirectory()
	{
		askForPath(ConfigurationSettings.getPccFilesDir(), "in_Prefs_pcgenDataDirTitle", pcgenDataDir);
	}

	/** Chooses the directory for documentation. */
	@FXML
	private void chooseDocsDirectory()
	{
		askForPath(ConfigurationSettings.getDocsDir(), "in_Prefs_pcgenDocsDirTitle", pcgenDocsDir);
	}

	/** Chooses the directory for system files. */
	@FXML
	private void chooseSystemDirectory()
	{
		askForPath(ConfigurationSettings.getSystemsDir(), "in_Prefs_pcgenSystemDirTitle", pcgenSystemDir);
	}

	/** Chooses the directory for settings files. */
	@FXML
	private void chooseSettingsDirectory()
	{
		askForPath(ConfigurationSettings.getSettingsDir(), "in_Prefs_pcgenFilesDirTitle", pcgenFilesDir);
	}

	/** Chooses the directory for output sheets. */
	@FXML
	private void chooseOutputSheetDirectory()
	{
		askForPath(ConfigurationSettings.getOutputSheetsDir(), "in_Prefs_pcgenOutputSheetDirTitle", pcgenOutputSheetDir);
	}

	/** Chooses the directory for preview sheets. */
	@FXML
	private void choosePreviewDirectory()
	{
		askForPath(ConfigurationSettings.getPreviewDir(), "in_Prefs_pcgenPreviewDirTitle", pcgenPreviewDir);
	}

	/**
	 * Opens a directory chooser and updates the field, using the current path if canceled.
	 * @param currentPath the saved directory to browse initially
	 * @param titleKey the language-bundle key for the chooser title
	 * @param textField the field to receive the selected path
	 */
	private static void askForPath(final String currentPath, final String titleKey, final TextField textField)
	{
		GuiAssertions.assertIsJavaFXThread();
		File currentDirectory = new File(currentPath);
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(currentDirectory);
		directoryChooser.setTitle(LanguageBundle.getString(titleKey));
		File returnFile = directoryChooser.showDialog(null);
		if (returnFile == null)
		{
			returnFile = currentDirectory;
		}

		textField.setText(returnFile.toString());
	}
}
