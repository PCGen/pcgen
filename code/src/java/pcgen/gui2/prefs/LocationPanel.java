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
package pcgen.gui2.prefs;

import java.awt.BorderLayout;
import java.io.File;

import pcgen.cdom.base.Constants;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.ConfigurationSettings.SettingsFilesPath;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.SystemUtils;

/**
 * The Class {@code LocationPanel} is responsible for
 * displaying file location related preferences and allowing the 
 * preferences to be edited by the user.
 */
public final class LocationPanel extends PCGenPrefsPanel
{
	private static final String IN_LOCATION = LanguageBundle.getString("in_Prefs_location");

	private static final String IN_CHOOSE = "...";

	private final ToggleGroup groupFilesDir;
	private final RadioButton pcgenFilesDirRadio;
	private final RadioButton selectFilesDirRadio;
	private final RadioButton usersFilesDirRadio;
	private final CheckBox pcgenCreateBackupCharacter = new CheckBox();

	private final Button pcgenCharacterDirButton;
	private final Button pcgenCustomDirButton;
	private final Button pcgenVendorDataDirButton;
	private final Button pcgenHomebrewDataDirButton;
	private final Button pcgenDataDirButton;
	private final Button pcgenDocsDirButton;
	private Button pcgenFilesDirButton;
	private final Button pcgenOutputSheetDirButton;
	private final Button pcgenPreviewDirButton;
	private final Button pcgenPortraitsDirButton;
	private final Button pcgenSystemDirButton;
	private final Button pcgenBackupCharacterDirButton;

	private final TextField pcgenCharacterDir;
	private final TextField pcgenCustomDir;
	private final TextField pcgenVendorDataDir;
	private final TextField pcgenHomebrewDataDir;
	private final TextField pcgenDataDir;
	private final TextField pcgenDocsDir;
	private final TextField pcgenFilesDir;
	private final TextField pcgenOutputSheetDir;
	private final TextField pcgenBackupCharacterDir;
	private final TextField pcgenPreviewDir;
	private final TextField pcgenPortraitsDir;
	private final TextField pcgenSystemDir;

	/**
	 * Instantiates a new location panel.
	 */
	public LocationPanel()
	{
		GuiAssertions.assertIsNotJavaFXThread();
		setLayout(new BorderLayout());
		GridPane form = new GridPane();
		form.setPadding(new Insets(16));
		form.setHgap(12);
		form.setVgap(8);
		ColumnConstraints labels = new ColumnConstraints();
		labels.setMaxWidth(220);
		ColumnConstraints paths = new ColumnConstraints();
		paths.setHgrow(Priority.ALWAYS);
		form.getColumnConstraints().addAll(labels, paths, new ColumnConstraints());

		int row = 0;
		addSection(form, row++, "in_Prefs_locationCharacterFiles");
		pcgenCharacterDir = new TextField(String.valueOf(PCGenSettings.getPcgDir()));
		pcgenCharacterDirButton = addDirectoryRow(form, row++, "in_Prefs_pcgenCharacterDir", pcgenCharacterDir);
		pcgenPortraitsDir = new TextField(String.valueOf(PCGenSettings.getPortraitsDir()));
		pcgenPortraitsDirButton = addDirectoryRow(form, row++, "in_Prefs_pcgenPortraitsDir", pcgenPortraitsDir);

		pcgenCreateBackupCharacter.setText(LanguageBundle.getString("in_Prefs_pcgenCreateBackupCharacter"));
		form.add(pcgenCreateBackupCharacter, 1, row++, 2, 1);
		pcgenBackupCharacterDir = new TextField(String.valueOf(PCGenSettings.getBackupPcgDir()));
		pcgenBackupCharacterDirButton =
				addDirectoryRow(form, row++, "in_Prefs_pcgenBackupCharacterDir", pcgenBackupCharacterDir);

		addSection(form, row++, "in_Prefs_locationDataSources");
		pcgenDataDir = new TextField(String.valueOf(ConfigurationSettings.getPccFilesDir()));
		pcgenDataDirButton = addDirectoryRow(form, row++, "in_Prefs_pcgenDataDir", pcgenDataDir);
		pcgenCustomDir = new TextField(String.valueOf(PCGenSettings.getCustomDir()));
		pcgenCustomDirButton = addDirectoryRow(form, row++, "in_Prefs_pcgenCustomDir", pcgenCustomDir);
		pcgenVendorDataDir = new TextField(String.valueOf(PCGenSettings.getVendorDataDir()));
		pcgenVendorDataDirButton = addDirectoryRow(form, row++, "in_Prefs_pcgenVendorDataDir", pcgenVendorDataDir);
		pcgenHomebrewDataDir = new TextField(String.valueOf(PCGenSettings.getHomebrewDataDir()));
		pcgenHomebrewDataDirButton =
				addDirectoryRow(form, row++, "in_Prefs_pcgenHomebrewDataDir", pcgenHomebrewDataDir);

		addSection(form, row++, "in_Prefs_locationApplicationFiles");
		pcgenDocsDir = new TextField(String.valueOf(ConfigurationSettings.getDocsDir()));
		pcgenDocsDirButton = addDirectoryRow(form, row++, "in_Prefs_pcgenDocsDir", pcgenDocsDir);
		pcgenSystemDir = new TextField(String.valueOf(ConfigurationSettings.getSystemsDir()));
		pcgenSystemDirButton = addDirectoryRow(form, row++, "in_Prefs_pcgenSystemDir", pcgenSystemDir);
		pcgenOutputSheetDir = new TextField(String.valueOf(ConfigurationSettings.getOutputSheetsDir()));
		pcgenOutputSheetDirButton = addDirectoryRow(form, row++, "in_Prefs_pcgenOutputSheetDir", pcgenOutputSheetDir);
		pcgenPreviewDir = new TextField(String.valueOf(ConfigurationSettings.getPreviewDir()));
		pcgenPreviewDirButton = addDirectoryRow(form, row++, "in_Prefs_pcgenPreviewDir", pcgenPreviewDir);

		addSection(form, row++, "in_Prefs_locationSettings");
		pcgenFilesDirRadio = new RadioButton(LanguageBundle.getFormattedString("in_Prefs_progDir", Constants.APPLICATION_NAME));
		usersFilesDirRadio = new RadioButton(LanguageBundle.getString("in_Prefs_homeDir"));
		selectFilesDirRadio = new RadioButton(LanguageBundle.getString("in_Prefs_selectDir"));
		pcgenFilesDir = new TextField(String.valueOf(ConfigurationSettings.getSettingsDir()));

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

		groupFilesDir = new ToggleGroup();
		pcgenFilesDirRadio.setToggleGroup(groupFilesDir);
		usersFilesDirRadio.setToggleGroup(groupFilesDir);
		selectFilesDirRadio.setToggleGroup(groupFilesDir);

		form.add(new HBox(16, pcgenFilesDirRadio, usersFilesDirRadio, selectFilesDirRadio), 0, row++, 3, 1);
		pcgenFilesDirButton = addDirectoryRow(form, row, "in_Prefs_pcgenFilesDir", pcgenFilesDir);
		pcgenFilesDirButton.setDisable(!selectFilesDirRadio.isSelected());
		pcgenFilesDirRadio.setOnAction(evt -> {
			pcgenFilesDir.setText(SettingsFilesPath.pcgen.getSettingsDir());
			pcgenFilesDirButton.setDisable(true);
		});
		usersFilesDirRadio.setOnAction(evt -> {
			pcgenFilesDir.setText(ConfigurationSettings.getUserSettingsDirFromFilePath());
			pcgenFilesDirButton.setDisable(true);
		});
		selectFilesDirRadio.setOnAction(evt -> {
			pcgenFilesDir.setText("");
			pcgenFilesDirButton.setDisable(false);
		});

		this.add(GuiUtility.wrapParentAsJFXPanel(form));
	}

	/** Adds a section heading without introducing another nested layout. */
	private static void addSection(GridPane form, int row, String titleKey)
	{
		Label title = new Label(LanguageBundle.getString(titleKey));
		title.setStyle("-fx-font-weight: bold;");
		GridPane.setMargin(title, new Insets(row == 0 ? 0 : 12, 0, 4, 0));
		form.add(title, 0, row, 3, 1);
	}

	/** Adds an accessible directory row and returns its browse button for the existing handler. */
	private Button addDirectoryRow(GridPane form, int row, String labelKey, TextField field)
	{
		Label label = new Label(LanguageBundle.getString(labelKey));
		label.setLabelFor(field);
		label.setWrapText(true);
		Button browse = new Button(IN_CHOOSE);
		browse.setOnAction(this::prefsButtonHandler);
		form.addRow(row, label, field, browse);
		return browse;
	}

	@Override
	public String getTitle()
	{
		return IN_LOCATION;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
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

	@Override
	public void applyOptionValuesToControls()
	{
		pcgenCreateBackupCharacter
			.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_CREATE_PCG_BACKUP));
	}

	// todo: split this into different functions instead of a giant if-then list
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
