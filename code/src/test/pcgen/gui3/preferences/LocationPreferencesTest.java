/*
 * Copyright 2026 the PCGen Project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.gui3.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import pcgen.cdom.base.Constants;
import pcgen.system.ConfigurationSettings;
import pcgen.system.ConfigurationSettings.SettingsFilesPath;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.system.PropertyContext;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testfx.framework.junit5.ApplicationExtension;

/** Verifies the real location FXML bindings and directory preference persistence without a Swing display. */
@ExtendWith(ApplicationExtension.class)
class LocationPreferencesTest
{
	@TempDir
	Path directories;

	private FXMLLoader loader;
	private LocationPreferencesPanelController controller;
	private final Map<DirectoryPreference, String> previousPaths = new LinkedHashMap<>();
	private String previousBackupPreference;

	/** Identifies a directory field and its corresponding persisted preference. */
	private record DirectoryPreference(String fieldId, PropertyContext context, String key)
	{
	}

	@BeforeEach
	void setUp() throws Exception
	{
		var app = PCGenSettings.getInstance();
		var config = ConfigurationSettings.getInstance();
		remember("pcgenCharacterDir", app, PCGenSettings.PCG_SAVE_PATH);
		remember("pcgenPortraitsDir", app, PCGenSettings.CHAR_PORTRAITS_PATH);
		remember("pcgenBackupCharacterDir", app, PCGenSettings.BACKUP_PCG_PATH);
		remember("pcgenCustomDir", app, PCGenSettings.CUSTOM_DATA_DIR);
		remember("pcgenVendorDataDir", app, PCGenSettings.VENDOR_DATA_DIR);
		remember("pcgenHomebrewDataDir", app, PCGenSettings.HOMEBREW_DATA_DIR);
		remember("pcgenDataDir", config, ConfigurationSettings.PCC_FILES_DIR);
		remember("pcgenDocsDir", config, ConfigurationSettings.DOCS_DIR);
		remember("pcgenSystemDir", config, ConfigurationSettings.SYSTEMS_DIR);
		remember("pcgenOutputSheetDir", config, ConfigurationSettings.OUTPUT_SHEETS_DIR);
		remember("pcgenPreviewDir", config, ConfigurationSettings.PREVIEW_DIR);
		remember("pcgenFilesDir", config, ConfigurationSettings.SETTINGS_FILES_PATH);
		previousBackupPreference = PCGenSettings.OPTIONS_CONTEXT.getProperty(PCGenSettings.OPTION_CREATE_PCG_BACKUP);
		LanguageBundle.getString("in_Prefs_location");
		asyncFx(() -> {
			loader = new FXMLLoader(LocationPreferencesPanelController.class.getResource("LocationPreferencesPanel.fxml"),
				LanguageBundle.getBundle());
			loader.load();
			controller = loader.getController();
			return null;
		}).get();
	}

	@AfterEach
	void tearDown()
	{
		previousPaths.forEach((preference, value) -> preference.context().setProperty(preference.key(), value));
		PCGenSettings.OPTIONS_CONTEXT.setProperty(PCGenSettings.OPTION_CREATE_PCG_BACKUP, previousBackupPreference);
	}

	/** Preserves the association between each of the twelve directory fields and its stored path. */
	@Test
	void directoryFields_should_loadSaveAndResetTheirOwnPreferences() throws Exception
	{
		asyncFx(() -> {
			for (var preference : previousPaths.keySet())
			{
				preference.context().setProperty(preference.key(), pathFor(preference, "original"));
			}
			PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_CREATE_PCG_BACKUP, true);
			controller.reset();
			var backup = (CheckBox) loader.getNamespace().get("pcgenCreateBackupCharacter");
			assertTrue(backup.isSelected());
			for (var preference : previousPaths.keySet())
			{
				var field = (TextField) loader.getNamespace().get(preference.fieldId());
				assertEquals(pathFor(preference, "original"), field.getText(), preference.fieldId());
				field.setText(pathFor(preference, "edited"));
			}
			backup.setSelected(false);
			controller.apply();
			assertFalse(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_CREATE_PCG_BACKUP));
			for (var preference : previousPaths.keySet())
			{
				assertEquals(pathFor(preference, "edited"), preference.context().getProperty(preference.key()),
					preference.fieldId());
				((TextField) loader.getNamespace().get(preference.fieldId())).clear();
			}
			backup.setSelected(true);
			controller.reset();
			assertFalse(backup.isSelected());
			for (var preference : previousPaths.keySet())
			{
				assertEquals(pathFor(preference, "edited"),
					((TextField) loader.getNamespace().get(preference.fieldId())).getText(), preference.fieldId());
			}
		}).get();
	}

	/** Restores built-in and custom settings locations and enables browsing only for a custom choice. */
	@ParameterizedTest
	@CsvSource({"pcgen, pcgenFilesDirRadio", "user, usersFilesDirRadio", "mac_user, usersFilesDirRadio",
		"FD_USER, usersFilesDirRadio", "custom, selectFilesDirRadio"})
	void settingsChoice_should_matchTheSavedLocation(String savedLocation, String selectedId) throws Exception
	{
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH, savedLocation);
		asyncFx(() -> {
			controller.reset();
			assertTrue(((RadioButton) loader.getNamespace().get(selectedId)).isSelected());
			assertEquals(ConfigurationSettings.getSettingsDir(),
				((TextField) loader.getNamespace().get("pcgenFilesDir")).getText());
			assertEquals(!savedLocation.equals("custom"),
				((Button) loader.getNamespace().get("pcgenFilesDirButton")).isDisabled());
		}).get();
	}

	/** Exercises the FXML radio actions and their binding to the settings directory browse button. */
	@Test
	void settingsChoices_should_updateThePathAndBrowseAvailability() throws Exception
	{
		asyncFx(() -> {
			var settings = (TextField) loader.getNamespace().get("pcgenFilesDir");
			var browse = (Button) loader.getNamespace().get("pcgenFilesDirButton");
			var program = (RadioButton) loader.getNamespace().get("pcgenFilesDirRadio");
			assertEquals(LanguageBundle.getFormattedString("in_Prefs_progDir", Constants.APPLICATION_NAME),
				program.getText());
			program.fire();
			assertEquals(SettingsFilesPath.pcgen.getSettingsDir(), settings.getText());
			assertTrue(browse.isDisabled());
			((RadioButton) loader.getNamespace().get("usersFilesDirRadio")).fire();
			assertEquals(ConfigurationSettings.getUserSettingsDirFromFilePath(), settings.getText());
			assertTrue(browse.isDisabled());
			((RadioButton) loader.getNamespace().get("selectFilesDirRadio")).fire();
			assertEquals("", settings.getText());
			assertFalse(browse.isDisabled());
		}).get();
	}

	/** Saves the exact prior property value, including absence, for restoration after each scenario. */
	private void remember(String fieldId, PropertyContext context, String key)
	{
		previousPaths.put(new DirectoryPreference(fieldId, context, key), context.getProperty(key));
	}

	/** Supplies distinct absolute paths so swapped field mappings cannot pass a round trip. */
	private String pathFor(DirectoryPreference preference, String version)
	{
		return directories.resolve(version).resolve(preference.fieldId()).toString();
	}
}
