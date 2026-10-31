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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
	private Button pcgenCharacterDirButton;
	@FXML
	private Button pcgenCustomDirButton;
	@FXML
	private Button pcgenVendorDataDirButton;
	@FXML
	private Button pcgenHomebrewDataDirButton;
	@FXML
	private Button pcgenDataDirButton;
	@FXML
	private Button pcgenDocsDirButton;
	@FXML
	private Button pcgenFilesDirButton;
	@FXML
	private Button pcgenOutputSheetDirButton;
	@FXML
	private Button pcgenPreviewDirButton;
	@FXML
	private Button pcgenPortraitsDirButton;
	@FXML
	private Button pcgenSystemDirButton;
	@FXML
	private Button pcgenBackupCharacterDirButton;

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
		PCGenSettings.getInstance().setProperty(PCGenSettings.PCG_SAVE_PATH, pcgenCharacterDir.getText());
		PCGenSettings.getInstance().setProperty(PCGenSettings.CHAR_PORTRAITS_PATH, pcgenPortraitsDir.getText());
		PCGenSettings.getInstance().setProperty(PCGenSettings.CUSTOM_DATA_DIR, pcgenCustomDir.getText());
		PCGenSettings.getInstance().setProperty(PCGenSettings.VENDOR_DATA_DIR, pcgenVendorDataDir.getText());
		PCGenSettings.getInstance().setProperty(PCGenSettings.HOMEBREW_DATA_DIR, pcgenHomebrewDataDir.getText());
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
		PCGenSettings.getInstance().setProperty(PCGenSettings.BACKUP_PCG_PATH, pcgenBackupCharacterDir.getText());

		ConfigurationSettings.setSystemProperty(ConfigurationSettings.PREVIEW_DIR, pcgenPreviewDir.getText());
	}


	/** Opens the directory chooser associated with the activated browse button. */
	@FXML
	private void prefsButtonHandler(final ActionEvent actionEvent)
	{
		GuiAssertions.assertIsJavaFXThread();
		Button source = (Button) actionEvent.getSource();

		if (source == pcgenCharacterDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenCharacterDirTitle");
			final String currentPath = PCGenSettings.getPcgDir();
			askForPath(currentPath, dialogTitle, pcgenCharacterDir);
		}
		else if (source == pcgenBackupCharacterDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenBackupCharacterDirTitle");
			final String currentPath = PCGenSettings.getBackupPcgDir();
			askForPath(currentPath, dialogTitle, pcgenBackupCharacterDir);
		}
		else if (source == pcgenPortraitsDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenPortraitDirTitle");
			final String currentPath = PCGenSettings.getPortraitsDir();
			askForPath(currentPath, dialogTitle, pcgenPortraitsDir);
		}
		else if (source == pcgenCustomDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenCustomDirTitle");
			final String currentPath = PCGenSettings.getCustomDir();
			askForPath(currentPath, dialogTitle, pcgenCustomDir);
		}
		else if (source == pcgenVendorDataDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenVendorDataDirTitle");
			final String currentPath = PCGenSettings.getVendorDataDir();
			askForPath(currentPath, dialogTitle, pcgenVendorDataDir);
		}
		else if (source == pcgenHomebrewDataDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenHomebrewDataDirTitle");
			final String currentPath = PCGenSettings.getHomebrewDataDir();
			askForPath(currentPath, dialogTitle, pcgenHomebrewDataDir);
		}
		else if (source == pcgenDataDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenDataDirTitle");
			final String currentPath = ConfigurationSettings.getPccFilesDir();
			askForPath(currentPath, dialogTitle, pcgenDataDir);
		}
		else if (source == pcgenDocsDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenDocsDirTitle");
			final String currentPath = ConfigurationSettings.getDocsDir();
			askForPath(currentPath, dialogTitle, pcgenDocsDir);
		}
		else if (source == pcgenSystemDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenSystemDirTitle");
			final String currentPath = ConfigurationSettings.getSystemsDir();
			askForPath(currentPath, dialogTitle, pcgenSystemDir);
		}
		else if (source == pcgenFilesDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenFilesDirTitle");
			final String currentPath = ConfigurationSettings.getSettingsDir();
			askForPath(currentPath, dialogTitle, pcgenFilesDir);
		}
		else if (source == pcgenOutputSheetDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenOutputSheetDirTitle");
			final String currentPath = ConfigurationSettings.getOutputSheetsDir();
			askForPath(currentPath, dialogTitle, pcgenOutputSheetDir);
		}
		else if (source == pcgenPreviewDirButton)
		{
			final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenPreviewDirTitle");
			final String currentPath = ConfigurationSettings.getPreviewDir();
			askForPath(currentPath, dialogTitle, pcgenPreviewDir);
		}
		else
		{
			throw new IllegalStateException("illegal source in location panel" + source);
		}

	}

	/**
	 * Ask for a path, and return it (possibly return the currentPath.)
	 * @param currentPath when entering the method
	 * @param dialogTitle to show
	 * @param textField to update with the path information
	 */
	private static void askForPath(final String currentPath, final String dialogTitle, final TextField textField)
	{
		GuiAssertions.assertIsJavaFXThread();
		askForPath(new File(currentPath), dialogTitle, textField);
	}

	/**
	 * Ask for a path, and return it (possibly return the currentPath.)
	 * @param currentPath when entering the method
	 * @param dialogTitle to show
	 * @param textField to update with the path information
	 */
	private static void askForPath(final File currentPath, final String dialogTitle, final TextField textField)
	{
		GuiAssertions.assertIsJavaFXThread();
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(currentPath);
		directoryChooser.setTitle(dialogTitle);
		File returnFile = directoryChooser.showDialog(null);
		if (returnFile == null)
		{
			returnFile = currentPath;
		}

		textField.setText(returnFile.toString());
	}
}
