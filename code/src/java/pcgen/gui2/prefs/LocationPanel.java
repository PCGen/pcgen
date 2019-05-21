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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.tools.Utility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.ConfigurationSettings.SettingsFilesPath;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;

import org.apache.commons.lang3.SystemUtils;

/**
 * The Class {@code LocationPanel} is responsible for
 * displaying file location related preferences and allowing the 
 * preferences to be edited by the user.
 */
@SuppressWarnings("serial")
public class LocationPanel extends PCGenPrefsPanel
{
	private static final String IN_LOCATION = LanguageBundle.getString("in_Prefs_location");

	private static final String IN_CHOOSE = "...";

	private final ButtonGroup groupFilesDir;
	private final JRadioButton pcgenFilesDirRadio;
	private final JRadioButton selectFilesDirRadio;
	private final JRadioButton usersFilesDirRadio;
	private final JCheckBox pcgenCreateBackupCharacter = new JCheckBox();

	private final JButton pcgenCharacterDirButton;
	private final JButton pcgenCustomDirButton;
	private final JButton pcgenVendorDataDirButton;
	private final JButton pcgenHomebrewDataDirButton;
	private final JButton pcgenDataDirButton;
	private final JButton pcgenDocsDirButton;
	private JButton pcgenFilesDirButton;
	private final JButton pcgenOutputSheetDirButton;
	private final JButton pcgenPreviewDirButton;
	private final JButton pcgenPortraitsDirButton;
	private final JButton pcgenSystemDirButton;
	private final JButton pcgenBackupCharacterDirButton;

	private final JTextField pcgenCharacterDir;
	private final JTextField pcgenCustomDir;
	private final JTextField pcgenVendorDataDir;
	private final JTextField pcgenHomebrewDataDir;
	private final JTextField pcgenDataDir;
	private final JTextField pcgenDocsDir;
	private final JTextField pcgenFilesDir;
	private final JTextField pcgenOutputSheetDir;
	private final JTextField pcgenBackupCharacterDir;
	private final JTextField pcgenPreviewDir;
	private final JTextField pcgenPortraitsDir;
	private final JTextField pcgenSystemDir;

	private final PrefsButtonListener prefsButtonHandler = new PrefsButtonListener();
	private final TextFocusLostListener textFieldListener = new TextFocusLostListener();

	/**
	 * Instantiates a new location panel.
	 */
	public LocationPanel()
	{
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, IN_LOCATION);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(constraints, 0, 2, 1, 1, 0, 0);
		JLabel in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenCharacterDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 2, 1, 1, 0, 0);
		pcgenCharacterDir = new JTextField(String.valueOf(PCGenSettings.getPcgDir()));

		// sage_sam 9 April 2003
		pcgenCharacterDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenCharacterDir, constraints);
		this.add(pcgenCharacterDir);
		Utility.buildConstraints(constraints, 2, 2, 1, 1, 0, 0);
		pcgenCharacterDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenCharacterDirButton, constraints);
		this.add(pcgenCharacterDirButton);
		pcgenCharacterDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(constraints, 0, 3, 1, 1, 0, 0);

		//TODO i18n
		in_prefs_pcgenCharacterDir = new JLabel("PCGen Portraits Directory" + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 3, 1, 1, 0, 0);
		pcgenPortraitsDir = new JTextField(String.valueOf(PCGenSettings.getPortraitsDir()));

		// sage_sam 9 April 2003
		pcgenPortraitsDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenPortraitsDir, constraints);
		this.add(pcgenPortraitsDir);
		Utility.buildConstraints(constraints, 2, 3, 1, 1, 0, 0);
		pcgenPortraitsDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenPortraitsDirButton, constraints);
		this.add(pcgenPortraitsDirButton);
		pcgenPortraitsDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(constraints, 0, 4, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenDataDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 4, 1, 1, 0, 0);
		pcgenDataDir = new JTextField(String.valueOf(ConfigurationSettings.getPccFilesDir()));

		// sage_sam 9 April 2003
		pcgenDataDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenDataDir, constraints);
		this.add(pcgenDataDir);
		Utility.buildConstraints(constraints, 2, 4, 1, 1, 0, 0);
		pcgenDataDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenDataDirButton, constraints);
		this.add(pcgenDataDirButton);
		pcgenDataDirButton.addActionListener(prefsButtonHandler);

		//////////////////////
		Utility.buildConstraints(constraints, 0, 5, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenCustomDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 5, 1, 1, 0, 0);
		pcgenCustomDir = new JTextField(String.valueOf(PCGenSettings.getCustomDir()));

		// sage_sam 9 April 2003
		pcgenCustomDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenCustomDir, constraints);
		this.add(pcgenCustomDir);
		Utility.buildConstraints(constraints, 2, 5, 1, 1, 0, 0);
		pcgenCustomDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenCustomDirButton, constraints);
		this.add(pcgenCustomDirButton);
		pcgenCustomDirButton.addActionListener(prefsButtonHandler);

		////////////////////

		Utility.buildConstraints(constraints, 0, 6, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenVendorDataDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 6, 1, 1, 0, 0);
		pcgenVendorDataDir = new JTextField(String.valueOf(PCGenSettings.getVendorDataDir()));

		// sage_sam 9 April 2003
		pcgenVendorDataDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenVendorDataDir, constraints);
		this.add(pcgenVendorDataDir);
		Utility.buildConstraints(constraints, 2, 6, 1, 1, 0, 0);
		pcgenVendorDataDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenVendorDataDirButton, constraints);
		this.add(pcgenVendorDataDirButton);
		pcgenVendorDataDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(constraints, 0, 7, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenHomebrewDataDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 7, 1, 1, 0, 0);
		pcgenHomebrewDataDir = new JTextField(String.valueOf(PCGenSettings.getHomebrewDataDir()));

		pcgenHomebrewDataDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenHomebrewDataDir, constraints);
		this.add(pcgenHomebrewDataDir);
		Utility.buildConstraints(constraints, 2, 7, 1, 1, 0, 0);
		pcgenHomebrewDataDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenHomebrewDataDirButton, constraints);
		this.add(pcgenHomebrewDataDirButton);
		pcgenHomebrewDataDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(constraints, 0, 8, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenDocsDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 8, 1, 1, 0, 0);
		pcgenDocsDir = new JTextField(String.valueOf(ConfigurationSettings.getDocsDir()));

		// sage_sam 9 April 2003
		pcgenDocsDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenDocsDir, constraints);
		this.add(pcgenDocsDir);
		Utility.buildConstraints(constraints, 2, 8, 1, 1, 0, 0);
		pcgenDocsDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenDocsDirButton, constraints);
		this.add(pcgenDocsDirButton);
		pcgenDocsDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(constraints, 0, 9, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenSystemDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 9, 1, 1, 0, 0);
		pcgenSystemDir = new JTextField(String.valueOf(ConfigurationSettings.getSystemsDir()));

		// sage_sam 9 April 2003
		pcgenSystemDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenSystemDir, constraints);
		this.add(pcgenSystemDir);
		Utility.buildConstraints(constraints, 2, 9, 1, 1, 0, 0);
		pcgenSystemDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenSystemDirButton, constraints);
		this.add(pcgenSystemDirButton);
		pcgenSystemDirButton.addActionListener(prefsButtonHandler);

		// Output Sheet directory
		Utility.buildConstraints(constraints, 0, 10, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenOutputSheetDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 10, 1, 1, 0, 0);
		pcgenOutputSheetDir = new JTextField(String.valueOf(ConfigurationSettings.getOutputSheetsDir()));
		pcgenOutputSheetDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenOutputSheetDir, constraints);
		this.add(pcgenOutputSheetDir);
		Utility.buildConstraints(constraints, 2, 10, 1, 1, 0, 0);
		pcgenOutputSheetDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenOutputSheetDirButton, constraints);
		this.add(pcgenOutputSheetDirButton);
		pcgenOutputSheetDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(constraints, 0, 11, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenPreviewDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 11, 1, 1, 0, 0);
		pcgenPreviewDir = new JTextField(String.valueOf(ConfigurationSettings.getPreviewDir()));
		pcgenPreviewDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenPreviewDir, constraints);
		this.add(pcgenPreviewDir);
		Utility.buildConstraints(constraints, 2, 11, 1, 1, 0, 0);
		pcgenPreviewDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenPreviewDirButton, constraints);
		this.add(pcgenPreviewDirButton);
		pcgenPreviewDirButton.addActionListener(prefsButtonHandler);

		// Character File Backup directory
		Utility.buildConstraints(constraints, 0, 12, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenCreateBackupCharacter") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 12, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenCreateBackupCharacter, constraints);
		this.add(pcgenCreateBackupCharacter);

		Utility.buildConstraints(constraints, 0, 13, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenBackupCharacterDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);
		Utility.buildConstraints(constraints, 1, 13, 1, 1, 0, 0);
		pcgenBackupCharacterDir = new JTextField(String.valueOf(PCGenSettings.getBackupPcgDir()));
		pcgenBackupCharacterDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenBackupCharacterDir, constraints);
		this.add(pcgenBackupCharacterDir);
		Utility.buildConstraints(constraints, 2, 13, 1, 1, 0, 0);
		pcgenBackupCharacterDirButton = new JButton(IN_CHOOSE);
		gridbag.setConstraints(pcgenBackupCharacterDirButton, constraints);
		this.add(pcgenBackupCharacterDirButton);
		pcgenBackupCharacterDirButton.addActionListener(prefsButtonHandler);

		// Where to store options.ini file
		Utility.buildConstraints(constraints, 0, 14, 1, 1, 0, 0);
		in_prefs_pcgenCharacterDir = new JLabel(LanguageBundle.getString("in_Prefs_pcgenFilesDir") + ": ");
		gridbag.setConstraints(in_prefs_pcgenCharacterDir, constraints);
		this.add(in_prefs_pcgenCharacterDir);

		pcgenFilesDirRadio = new JRadioButton("PCGen Dir");
		usersFilesDirRadio = new JRadioButton("Home Dir");
		selectFilesDirRadio = new JRadioButton("Select a directory");
		pcgenFilesDir = new JTextField(String.valueOf(ConfigurationSettings.getSettingsDir()));
		pcgenFilesDir.addFocusListener(textFieldListener);

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

		Utility.buildConstraints(constraints, 0, 15, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenFilesDirRadio, constraints);
		this.add(pcgenFilesDirRadio);
		Utility.buildConstraints(constraints, 1, 15, 1, 1, 0, 0);
		gridbag.setConstraints(usersFilesDirRadio, constraints);
		this.add(usersFilesDirRadio);

		groupFilesDir = new ButtonGroup();
		groupFilesDir.add(pcgenFilesDirRadio);
		groupFilesDir.add(usersFilesDirRadio);
		groupFilesDir.add(selectFilesDirRadio);

		pcgenFilesDirRadio.addActionListener(evt -> {
			pcgenFilesDir.setText(SettingsFilesPath.pcgen.getSettingsDir());
			pcgenFilesDirButton.setEnabled(false);
		});
		usersFilesDirRadio.addActionListener(evt -> {
			pcgenFilesDir.setText(ConfigurationSettings.getUserSettingsDirFromFilePath());
			pcgenFilesDirButton.setEnabled(false);
		});
		selectFilesDirRadio.addActionListener(evt -> {
			pcgenFilesDir.setText("");
			pcgenFilesDirButton.setEnabled(true);
		});

		Utility.buildConstraints(constraints, 0, 16, 1, 1, 0, 0);
		gridbag.setConstraints(selectFilesDirRadio, constraints);
		this.add(selectFilesDirRadio);
		Utility.buildConstraints(constraints, 1, 16, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenFilesDir, constraints);
		this.add(pcgenFilesDir);
		Utility.buildConstraints(constraints, 2, 16, 1, 1, 0, 0);
		pcgenFilesDirButton = new JButton(IN_CHOOSE);
		pcgenFilesDirButton.setEnabled(selectFilesDirRadio.isSelected());
		gridbag.setConstraints(pcgenFilesDirButton, constraints);
		this.add(pcgenFilesDirButton);
		pcgenFilesDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(constraints, 0, 20, 3, 1, 1, 1);
		constraints.fill = GridBagConstraints.BOTH;
		JLabel emptyLabel = new JLabel(" ");

		gridbag.setConstraints(emptyLabel, constraints);
		this.add(emptyLabel);
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

	private final class PrefsButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			JButton source = (JButton) actionEvent.getSource();

			if (source == null)
			{
				// Do nothing
			}
			else if (source == pcgenCharacterDirButton)
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
		}

		/**
		 * Ask for a path, and return it (possibly return the currentPath.)
		 * @param currentPath when entering the method
		 * @param dialogTitle to show
		 * @param textField to update with the path information
		 * @return A path to the directory.
		 */
		private File askForPath(final String currentPath, final String dialogTitle, final JTextField textField)
		{
			return askForPath(new File(currentPath), dialogTitle, textField);
		}

		/**
		 * Ask for a path, and return it (possibly return the currentPath.)
		 * @param currentPath when entering the method
		 * @param dialogTitle to show
		 * @param textField to update with the path information
		 * @return A path to the directory.
		 */
		private File askForPath(final File currentPath, final String dialogTitle, final JTextField textField)
		{

			JFileChooser fc = new JFileChooser(currentPath);

			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle(dialogTitle);

			if (SystemUtils.IS_OS_MAC)
			{
				// On MacOS X, do not traverse file bundles
				fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
			}

			final int returnVal = fc.showOpenDialog(getParent());

			File returnFile = currentPath;
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				returnFile = fc.getSelectedFile();
			}

			textField.setText(String.valueOf(returnFile));

			return returnFile;
		}
	}

	// This is the focus listener so that text field values may be manually entered.
	// sage_sam April 2003 for FREQ 707022
	private static class TextFocusLostListener implements FocusListener
	{
		private String initialValue;
		private boolean dialogOpened = false;

		@Override
		public void focusGained(FocusEvent e)
		{
			// reset variables
			dialogOpened = false;

			final Object source = e.getSource();

			if (source instanceof JTextField)
			{
				// get the field value
				initialValue = ((JTextComponent) source).getText();
			}
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			// Check the source to see if it was a text field
			final Object source = e.getSource();

			if (source instanceof JTextField)
			{
				// get the field value and validate it exists
				final String fieldValue = ((JTextComponent) source).getText();
				final File fieldFile = new File(fieldValue);

				if ((!fieldFile.exists()) && (!fieldValue.equalsIgnoreCase("null")) && (!fieldValue.trim().isEmpty())
					&& (!dialogOpened))
				{
					// display error dialog and restore previous value
					dialogOpened = true;
					ShowMessageDelegate.showMessageDialog("File does not exist; preferences were not set.",
						"Invalid Path", MessageType.ERROR);
					((JTextComponent) source).setText(initialValue);
				}
			}
		}
	}
}
