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

import java.io.File;

import pcgen.cdom.base.Constants;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.ConfigurationSettings.SettingsFilesPath;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
        VBox vBox = new VBox();
        // todo: make a custom component that combines a label, textfield, and browse button

        Text in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenCharacterDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenCharacterDir = new TextField(String.valueOf(PCGenSettings.getPcgDir()));

        vBox.getChildren().add(pcgenCharacterDir);
        pcgenCharacterDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenCharacterDirButton);
        pcgenCharacterDirButton.setOnAction(this::prefsButtonHandler);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenPortraitsDir"));
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenPortraitsDir = new TextField(String.valueOf(PCGenSettings.getPortraitsDir()));

        vBox.getChildren().add(pcgenPortraitsDir);
        pcgenPortraitsDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenPortraitsDirButton);
        pcgenPortraitsDirButton.setOnAction(this::prefsButtonHandler);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenDataDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenDataDir = new TextField(String.valueOf(ConfigurationSettings.getPccFilesDir()));

        vBox.getChildren().add(pcgenDataDir);
        pcgenDataDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenDataDirButton);
        pcgenDataDirButton.setOnAction(this::prefsButtonHandler);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenCustomDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenCustomDir = new TextField(String.valueOf(PCGenSettings.getCustomDir()));

        vBox.getChildren().add(pcgenCustomDir);
        pcgenCustomDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenCustomDirButton);
        pcgenCustomDirButton.setOnAction(this::prefsButtonHandler);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenVendorDataDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenVendorDataDir = new TextField(String.valueOf(PCGenSettings.getVendorDataDir()));

        vBox.getChildren().add(pcgenVendorDataDir);
        pcgenVendorDataDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenVendorDataDirButton);
        pcgenVendorDataDirButton.setOnAction(this::prefsButtonHandler);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenHomebrewDataDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenHomebrewDataDir = new TextField(String.valueOf(PCGenSettings.getHomebrewDataDir()));

        vBox.getChildren().add(pcgenHomebrewDataDir);
        pcgenHomebrewDataDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenHomebrewDataDirButton);
        pcgenHomebrewDataDirButton.setOnAction(this::prefsButtonHandler);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenDocsDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenDocsDir = new TextField(String.valueOf(ConfigurationSettings.getDocsDir()));

        vBox.getChildren().add(pcgenDocsDir);
        pcgenDocsDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenDocsDirButton);
        pcgenDocsDirButton.setOnAction(this::prefsButtonHandler);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenSystemDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenSystemDir = new TextField(String.valueOf(ConfigurationSettings.getSystemsDir()));

        vBox.getChildren().add(pcgenSystemDir);
        pcgenSystemDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenSystemDirButton);
        pcgenSystemDirButton.setOnAction(this::prefsButtonHandler);

        // Output Sheet directory
        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenOutputSheetDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenOutputSheetDir = new TextField(String.valueOf(ConfigurationSettings.getOutputSheetsDir()));
        vBox.getChildren().add(pcgenOutputSheetDir);
        pcgenOutputSheetDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenOutputSheetDirButton);
        pcgenOutputSheetDirButton.setOnAction(this::prefsButtonHandler);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenPreviewDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenPreviewDir = new TextField(String.valueOf(ConfigurationSettings.getPreviewDir()));
        vBox.getChildren().add(pcgenPreviewDir);
        pcgenPreviewDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenPreviewDirButton);
        pcgenPreviewDirButton.setOnAction(this::prefsButtonHandler);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenCreateBackupCharacter") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        vBox.getChildren().add(pcgenCreateBackupCharacter);

        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenBackupCharacterDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);
        pcgenBackupCharacterDir = new TextField(String.valueOf(PCGenSettings.getBackupPcgDir()));
        vBox.getChildren().add(pcgenBackupCharacterDir);
        pcgenBackupCharacterDirButton = new Button(IN_CHOOSE);
        vBox.getChildren().add(pcgenBackupCharacterDirButton);
        pcgenBackupCharacterDirButton.setOnAction(this::prefsButtonHandler);

        // Where to store options.ini file
        in_prefs_pcgenCharacterDir = new Text(LanguageBundle.getString("in_Prefs_pcgenFilesDir") + ": ");
        vBox.getChildren().add(in_prefs_pcgenCharacterDir);

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
        } else if (fType.equals(SettingsFilesPath.user.name()) || fType.equals(SettingsFilesPath.mac_user.name())
                || fType.equals(SettingsFilesPath.FD_USER.name()))
        {
            usersFilesDirRadio.setSelected(true);
        } else
        {
            selectFilesDirRadio.setSelected(true);
        }

        vBox.getChildren().add(pcgenFilesDirRadio);
        vBox.getChildren().add(usersFilesDirRadio);

        groupFilesDir = new ToggleGroup();
        pcgenFilesDirRadio.setToggleGroup(groupFilesDir);
        usersFilesDirRadio.setToggleGroup(groupFilesDir);
        selectFilesDirRadio.setToggleGroup(groupFilesDir);

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

        vBox.getChildren().add(selectFilesDirRadio);
        vBox.getChildren().add(pcgenFilesDir);
        pcgenFilesDirButton = new Button(IN_CHOOSE);
        pcgenFilesDirButton.setDisable(!selectFilesDirRadio.isSelected());
        vBox.getChildren().add(pcgenFilesDirButton);
        pcgenFilesDirButton.setOnAction(this::prefsButtonHandler);

        this.add(GuiUtility.wrapParentAsJFXPanel(vBox));
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
        } else if (usersFilesDirRadio.isSelected())
        {
            if (SystemUtils.IS_OS_MAC_OSX)
            {
                ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH,
                        SettingsFilesPath.mac_user.name());
            } else
            {
                ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH,
                        SettingsFilesPath.user.name());
            }
        } else
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
        } else if (source == pcgenBackupCharacterDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenBackupCharacterDirTitle");
            final String currentPath = PCGenSettings.getBackupPcgDir();
            askForPath(currentPath, dialogTitle, pcgenBackupCharacterDir);
        } else if (source == pcgenPortraitsDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenPortraitDirTitle");
            final String currentPath = PCGenSettings.getPortraitsDir();
            askForPath(currentPath, dialogTitle, pcgenPortraitsDir);
        } else if (source == pcgenCustomDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenCustomDirTitle");
            final String currentPath = PCGenSettings.getCustomDir();
            askForPath(currentPath, dialogTitle, pcgenCustomDir);
        } else if (source == pcgenVendorDataDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenVendorDataDirTitle");
            final String currentPath = PCGenSettings.getVendorDataDir();
            askForPath(currentPath, dialogTitle, pcgenVendorDataDir);
        } else if (source == pcgenHomebrewDataDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenHomebrewDataDirTitle");
            final String currentPath = PCGenSettings.getHomebrewDataDir();
            askForPath(currentPath, dialogTitle, pcgenHomebrewDataDir);
        } else if (source == pcgenDataDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenDataDirTitle");
            final String currentPath = ConfigurationSettings.getPccFilesDir();
            askForPath(currentPath, dialogTitle, pcgenDataDir);
        } else if (source == pcgenDocsDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenDocsDirTitle");
            final String currentPath = ConfigurationSettings.getDocsDir();
            askForPath(currentPath, dialogTitle, pcgenDocsDir);
        } else if (source == pcgenSystemDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenSystemDirTitle");
            final String currentPath = ConfigurationSettings.getSystemsDir();
            askForPath(currentPath, dialogTitle, pcgenSystemDir);
        } else if (source == pcgenFilesDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenFilesDirTitle");
            final String currentPath = ConfigurationSettings.getSettingsDir();
            askForPath(currentPath, dialogTitle, pcgenFilesDir);
        } else if (source == pcgenOutputSheetDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenOutputSheetDirTitle");
            final String currentPath = ConfigurationSettings.getOutputSheetsDir();
            askForPath(currentPath, dialogTitle, pcgenOutputSheetDir);
        } else if (source == pcgenPreviewDirButton)
        {
            final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenPreviewDirTitle");
            final String currentPath = ConfigurationSettings.getPreviewDir();
            askForPath(currentPath, dialogTitle, pcgenPreviewDir);
        } else
        {
            throw new IllegalStateException("illegal source in location panel" + source);
        }

    }

    /**
     * Ask for a path, and return it (possibly return the currentPath.)
     *
     * @param currentPath when entering the method
     * @param dialogTitle to show
     * @param textField   to update with the path information
     */
    private static void askForPath(final String currentPath, final String dialogTitle, final TextField textField)
    {
        GuiAssertions.assertIsJavaFXThread();
        askForPath(new File(currentPath), dialogTitle, textField);
    }

    /**
     * Ask for a path, and return it (possibly return the currentPath.)
     *
     * @param currentPath when entering the method
     * @param dialogTitle to show
     * @param textField   to update with the path information
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
