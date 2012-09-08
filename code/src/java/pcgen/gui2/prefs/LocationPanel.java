/*
 * LocationPanel.java
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
 *
 * Created on 18/11/2010 19:50:00
 *
 * $Id$
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
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.SystemUtils;

import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.tools.Utility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.system.ConfigurationSettings.SettingsFilesPath;

/**
 * The Class <code>LocationPanel</code> is responsible for 
 * displaying file location related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class LocationPanel extends PCGenPrefsPanel
{
	private static String in_location =
		LanguageBundle.getString("in_Prefs_location");

	private static String in_browserPath =
		LanguageBundle.getString("in_Prefs_browserPath");
	private static String in_clearBrowserPath =
		LanguageBundle.getString("in_Prefs_clearBrowserPath");
	private static String in_choose = "...";

	private ButtonGroup groupFilesDir;
	private JRadioButton pcgenFilesDirRadio;
	private JRadioButton selectFilesDirRadio;
	private JRadioButton usersFilesDirRadio;
	private JCheckBox pcgenCreateBackupCharacter = new JCheckBox();

	private JButton browserPathButton;
	private JButton clearBrowserPathButton;
	private JButton pcgenCharacterDirButton;
	private JButton pcgenCustomDirButton;
	private JButton pcgenVendorDataDirButton;
	private JButton pcgenDataDirButton;
	private JButton pcgenDocsDirButton;
	private JButton pcgenFilesDirButton;
	private JButton pcgenOutputSheetDirButton;
	private JButton pcgenPreviewDirButton;
	private JButton pcgenPortraitsDirButton;
	private JButton pcgenSystemDirButton;
	private JButton pcgenBackupCharacterDirButton;

	private JTextField browserPath;
	private JTextField pcgenCharacterDir;
	private JTextField pcgenCustomDir;
	private JTextField pcgenVendorDataDir;
	private JTextField pcgenDataDir;
	private JTextField pcgenDocsDir;
	private JTextField pcgenFilesDir;
	private JTextField pcgenOutputSheetDir;
	private JTextField pcgenBackupCharacterDir;
	private JTextField pcgenPreviewDir;
	private JTextField pcgenPortraitsDir;
	private JTextField pcgenSystemDir;

	private PrefsButtonListener prefsButtonHandler = new PrefsButtonListener();
	private final TextFocusLostListener textFieldListener =
		new TextFocusLostListener();

	/**
	 * Instantiates a new location panel.
	 */
	public LocationPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_location);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		label = new JLabel(in_browserPath + ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 0, 1, 1, 1, 0);
		browserPath =
				new JTextField(String.valueOf(PCGenSettings.getBrowserPath()));

		// sage_sam 9 April 2003
		browserPath.addFocusListener(textFieldListener);
		gridbag.setConstraints(browserPath, c);
		this.add(browserPath);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		browserPathButton = new JButton(in_choose);
		gridbag.setConstraints(browserPathButton, c);
		this.add(browserPathButton);
		browserPathButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 1, 1, 1, 1, 0, 0);
		clearBrowserPathButton = new JButton(in_clearBrowserPath);
		gridbag.setConstraints(clearBrowserPathButton, c);
		this.add(clearBrowserPathButton);
		clearBrowserPathButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenCharacterDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 2, 1, 1, 0, 0);
		pcgenCharacterDir =
				new JTextField(String.valueOf(PCGenSettings.getPcgDir()));

		// sage_sam 9 April 2003
		pcgenCharacterDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenCharacterDir, c);
		this.add(pcgenCharacterDir);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		pcgenCharacterDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenCharacterDirButton, c);
		this.add(pcgenCharacterDirButton);
		pcgenCharacterDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 3, 1, 1, 0, 0);

		//TODO i18n
		label = new JLabel("PCGen Portraits Directory" + ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 3, 1, 1, 0, 0);
		pcgenPortraitsDir =
				new JTextField(String.valueOf(PCGenSettings
					.getPortraitsDir()));

		// sage_sam 9 April 2003
		pcgenPortraitsDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenPortraitsDir, c);
		this.add(pcgenPortraitsDir);
		Utility.buildConstraints(c, 2, 3, 1, 1, 0, 0);
		pcgenPortraitsDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenPortraitsDirButton, c);
		this.add(pcgenPortraitsDirButton);
		pcgenPortraitsDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 4, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenDataDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 4, 1, 1, 0, 0);
		pcgenDataDir =
				new JTextField(String.valueOf(ConfigurationSettings
					.getPccFilesDir()));

		// sage_sam 9 April 2003
		pcgenDataDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenDataDir, c);
		this.add(pcgenDataDir);
		Utility.buildConstraints(c, 2, 4, 1, 1, 0, 0);
		pcgenDataDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenDataDirButton, c);
		this.add(pcgenDataDirButton);
		pcgenDataDirButton.addActionListener(prefsButtonHandler);

		//////////////////////
		Utility.buildConstraints(c, 0, 5, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenCustomDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 5, 1, 1, 0, 0);
		pcgenCustomDir =
				new JTextField(String.valueOf(ConfigurationSettings
					.getCustomDir()));

		// sage_sam 9 April 2003
		pcgenCustomDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenCustomDir, c);
		this.add(pcgenCustomDir);
		Utility.buildConstraints(c, 2, 5, 1, 1, 0, 0);
		pcgenCustomDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenCustomDirButton, c);
		this.add(pcgenCustomDirButton);
		pcgenCustomDirButton.addActionListener(prefsButtonHandler);

		////////////////////

		Utility.buildConstraints(c, 0, 6, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenVendorDataDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 6, 1, 1, 0, 0);
		pcgenVendorDataDir =
				new JTextField(String.valueOf(ConfigurationSettings
					.getVendorDataDir()));

		// sage_sam 9 April 2003
		pcgenVendorDataDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenVendorDataDir, c);
		this.add(pcgenVendorDataDir);
		Utility.buildConstraints(c, 2, 6, 1, 1, 0, 0);
		pcgenVendorDataDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenVendorDataDirButton, c);
		this.add(pcgenVendorDataDirButton);
		pcgenVendorDataDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 7, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenDocsDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 7, 1, 1, 0, 0);
		pcgenDocsDir =
				new JTextField(String
					.valueOf(ConfigurationSettings.getDocsDir()));

		// sage_sam 9 April 2003
		pcgenDocsDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenDocsDir, c);
		this.add(pcgenDocsDir);
		Utility.buildConstraints(c, 2, 7, 1, 1, 0, 0);
		pcgenDocsDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenDocsDirButton, c);
		this.add(pcgenDocsDirButton);
		pcgenDocsDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 8, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenSystemDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 8, 1, 1, 0, 0);
		pcgenSystemDir =
				new JTextField(String.valueOf(ConfigurationSettings
					.getSystemsDir()));

		// sage_sam 9 April 2003
		pcgenSystemDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenSystemDir, c);
		this.add(pcgenSystemDir);
		Utility.buildConstraints(c, 2, 8, 1, 1, 0, 0);
		pcgenSystemDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenSystemDirButton, c);
		this.add(pcgenSystemDirButton);
		pcgenSystemDirButton.addActionListener(prefsButtonHandler);

		// Output Sheet directory
		Utility.buildConstraints(c, 0, 9, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenOutputSheetDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 9, 1, 1, 0, 0);
		pcgenOutputSheetDir =
				new JTextField(String.valueOf(ConfigurationSettings
					.getOutputSheetsDir()));
		pcgenOutputSheetDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenOutputSheetDir, c);
		this.add(pcgenOutputSheetDir);
		Utility.buildConstraints(c, 2, 9, 1, 1, 0, 0);
		pcgenOutputSheetDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenOutputSheetDirButton, c);
		this.add(pcgenOutputSheetDirButton);
		pcgenOutputSheetDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 10, 1, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_pcgenPreviewDir") + ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 10, 1, 1, 0, 0);
		pcgenPreviewDir = new JTextField(String.valueOf(ConfigurationSettings.getPreviewDir()));
		pcgenPreviewDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenPreviewDir, c);
		this.add(pcgenPreviewDir);
		Utility.buildConstraints(c, 2, 10, 1, 1, 0, 0);
		pcgenPreviewDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenPreviewDirButton, c);
		this.add(pcgenPreviewDirButton);
		pcgenPreviewDirButton.addActionListener(prefsButtonHandler);
		
		// Character File Backup directory
		Utility.buildConstraints(c, 0, 11, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenCreateBackupCharacter")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 11, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenCreateBackupCharacter, c);
		this.add(pcgenCreateBackupCharacter);

		Utility.buildConstraints(c, 0, 12, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenBackupCharacterDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 12, 1, 1, 0, 0);
		pcgenBackupCharacterDir =
				new JTextField(String.valueOf(PCGenSettings
					.getBackupPcgDir()));
		pcgenBackupCharacterDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenBackupCharacterDir, c);
		this.add(pcgenBackupCharacterDir);
		Utility.buildConstraints(c, 2, 12, 1, 1, 0, 0);
		pcgenBackupCharacterDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenBackupCharacterDirButton, c);
		this.add(pcgenBackupCharacterDirButton);
		pcgenBackupCharacterDirButton.addActionListener(prefsButtonHandler);

		// Where to store options.ini file
		Utility.buildConstraints(c, 0, 13, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenFilesDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);

		pcgenFilesDirRadio = new JRadioButton("PCGen Dir");
		usersFilesDirRadio = new JRadioButton("Home Dir");
		selectFilesDirRadio = new JRadioButton("Select a directory");
		pcgenFilesDir =
				new JTextField(String.valueOf(ConfigurationSettings
					.getSettingsDir()));
		pcgenFilesDir.addFocusListener(textFieldListener);

		String fType =
				ConfigurationSettings
					.getSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH);

		if ((fType == null) || (fType.length() < 1))
		{
			// make sure we have a default
			fType = ConfigurationSettings.getDefaultSettingsFilesPath();
			ConfigurationSettings.setSystemProperty(
				ConfigurationSettings.SETTINGS_FILES_PATH, fType);
		}

		pcgenFilesDir.setText(ConfigurationSettings.getSettingsDir());
		if (fType.equals(SettingsFilesPath.pcgen.name()))
		{
			pcgenFilesDirRadio.setSelected(true);
		}
		else if (fType.equals(SettingsFilesPath.user.name())
			|| fType.equals(SettingsFilesPath.mac_user.name())
			|| fType.equals(SettingsFilesPath.FD_USER.name()))
		{
			usersFilesDirRadio.setSelected(true);
		}
		else
		{
			selectFilesDirRadio.setSelected(true);
		}

		Utility.buildConstraints(c, 0, 14, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenFilesDirRadio, c);
		this.add(pcgenFilesDirRadio);
		Utility.buildConstraints(c, 1, 14, 1, 1, 0, 0);
		gridbag.setConstraints(usersFilesDirRadio, c);
		this.add(usersFilesDirRadio);

		groupFilesDir = new ButtonGroup();
		groupFilesDir.add(pcgenFilesDirRadio);
		groupFilesDir.add(usersFilesDirRadio);
		groupFilesDir.add(selectFilesDirRadio);

		pcgenFilesDirRadio.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				pcgenFilesDir.setText(SettingsFilesPath.pcgen.getSettingsDir());
				pcgenFilesDirButton.setEnabled(false);
			}
		});
		usersFilesDirRadio.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				pcgenFilesDir.setText(ConfigurationSettings.getUserSettingsDirFromFilePath());
				pcgenFilesDirButton.setEnabled(false);
			}
		});
		selectFilesDirRadio.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				pcgenFilesDir.setText("");
				pcgenFilesDirButton.setEnabled(true);
			}
		});

		Utility.buildConstraints(c, 0, 15, 1, 1, 0, 0);
		gridbag.setConstraints(selectFilesDirRadio, c);
		this.add(selectFilesDirRadio);
		Utility.buildConstraints(c, 1, 15, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenFilesDir, c);
		this.add(pcgenFilesDir);
		Utility.buildConstraints(c, 2, 15, 1, 1, 0, 0);
		pcgenFilesDirButton = new JButton(in_choose);
		pcgenFilesDirButton.setEnabled(selectFilesDirRadio.isSelected());
		gridbag.setConstraints(pcgenFilesDirButton, c);
		this.add(pcgenFilesDirButton);
		pcgenFilesDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 20, 3, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");

		gridbag.setConstraints(label, c);
		this.add(label);
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_location;
	}
	
	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PreferencesPanel#applyPreferences()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		// Location -- added 10 April 2000 by sage_sam
		PCGenSettings.getInstance().setProperty(
			PCGenSettings.BROWSER_PATH, browserPath.getText());
		PCGenSettings.getInstance().setProperty(
			PCGenSettings.PCG_SAVE_PATH, pcgenCharacterDir.getText());
		PCGenSettings.getInstance().setProperty(
			PCGenSettings.CHAR_PORTRAITS_PATH, pcgenPortraitsDir
				.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.CUSTOM_DATA_DIR, pcgenCustomDir
			.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.VENDOR_DATA_DIR, pcgenVendorDataDir
			.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.PCC_FILES_DIR, pcgenDataDir
			.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.DOCS_DIR,
			pcgenDocsDir.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.SYSTEMS_DIR,
			pcgenSystemDir.getText());
		if (pcgenFilesDirRadio.isSelected())
		{
			ConfigurationSettings.setSystemProperty(
				ConfigurationSettings.SETTINGS_FILES_PATH, SettingsFilesPath.pcgen.name());
		}
		else if (usersFilesDirRadio.isSelected())
		{
			if (SystemUtils.IS_OS_MAC_OSX)
			{
				ConfigurationSettings.setSystemProperty(
					ConfigurationSettings.SETTINGS_FILES_PATH, SettingsFilesPath.mac_user.name());
			}
			else
			{
				ConfigurationSettings.setSystemProperty(
					ConfigurationSettings.SETTINGS_FILES_PATH, SettingsFilesPath.user.name());
			}
		}
		else
		{
			ConfigurationSettings.setSystemProperty(
				ConfigurationSettings.SETTINGS_FILES_PATH, pcgenFilesDir
					.getText());
		}
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH,
			pcgenFilesDir.getText());
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.OUTPUT_SHEETS_DIR,
			pcgenOutputSheetDir.getText());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_CREATE_PCG_BACKUP, pcgenCreateBackupCharacter
				.isSelected());
		PCGenSettings.getInstance().setProperty(PCGenSettings.BACKUP_PCG_PATH,
			pcgenBackupCharacterDir.getText());

		ConfigurationSettings.setSystemProperty(ConfigurationSettings.PREVIEW_DIR,
			pcgenPreviewDir.getText());
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PreferencesPanel#initPreferences()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		pcgenCreateBackupCharacter.setSelected(PCGenSettings.OPTIONS_CONTEXT
			.getBoolean(PCGenSettings.OPTION_CREATE_PCG_BACKUP));
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
			else if (source == browserPathButton)
			{
				Utility.selectDefaultBrowser(getParent());
				browserPath.setText(String.valueOf(PCGenSettings
					.getBrowserPath()));
			}
			else if (source == clearBrowserPathButton)
			{
				// If none is set, there is nothing to clear
				if (PCGenSettings.getBrowserPath() == null)
				{
					return;
				}

				final int choice =
						JOptionPane.showConfirmDialog(null, LanguageBundle
							.getString("in_Prefs_clearBrowserWarn"),
							LanguageBundle
								.getString("in_Prefs_clearBrowserTitle"),
							JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.YES_OPTION)
				{
					PCGenSettings.getInstance().setProperty(
						PCGenSettings.BROWSER_PATH, "");
				}

				browserPath.setText(String.valueOf(PCGenSettings
					.getBrowserPath()));
			}
			else if (source == pcgenCharacterDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenCharacterDirTitle");
				final String currentPath = PCGenSettings.getPcgDir();
				askForPath(currentPath, dialogTitle, pcgenCharacterDir);
			}
			else if (source == pcgenBackupCharacterDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenBackupCharacterDirTitle");
				final String currentPath = PCGenSettings.getBackupPcgDir();
				askForPath(currentPath, dialogTitle, pcgenBackupCharacterDir);
			}
			else if (source == pcgenPortraitsDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenPortraitDirTitle");
				final String currentPath = PCGenSettings.getPortraitsDir();
				askForPath(currentPath, dialogTitle, pcgenPortraitsDir);
			}
			else if (source == pcgenCustomDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenCustomDirTitle");
				final String currentPath = ConfigurationSettings.getCustomDir();
				askForPath(currentPath, dialogTitle, pcgenCustomDir);
			}
			else if (source == pcgenVendorDataDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenVendorDataDirTitle");
				final String currentPath = ConfigurationSettings.getVendorDataDir();
				askForPath(currentPath, dialogTitle, pcgenVendorDataDir);
			}
			else if (source == pcgenDataDirButton)
			{
				final String dialogTitle =
						LanguageBundle.getString("in_Prefs_pcgenDataDirTitle");
				final String currentPath = ConfigurationSettings.getPccFilesDir();
				askForPath(currentPath, dialogTitle, pcgenDataDir);
			}
			else if (source == pcgenDocsDirButton)
			{
				final String dialogTitle =
						LanguageBundle.getString("in_Prefs_pcgenDocsDirTitle");
				final String currentPath = ConfigurationSettings.getDocsDir();
				askForPath(currentPath, dialogTitle, pcgenDocsDir);
			}
			else if (source == pcgenSystemDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenSystemDirTitle");
				final String currentPath = ConfigurationSettings.getSystemsDir();
				askForPath(currentPath, dialogTitle, pcgenSystemDir);
			}
			else if (source == pcgenFilesDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenFilesDirTitle");
				final String currentPath = ConfigurationSettings.getSettingsDir();
				askForPath(currentPath, dialogTitle, pcgenFilesDir);
			}
			else if (source == pcgenOutputSheetDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenOutputSheetDirTitle");
				final String currentPath =
						ConfigurationSettings.getOutputSheetsDir();
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
		private File askForPath(final String currentPath,
			final String dialogTitle, final JTextField textField)
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
		private File askForPath(final File currentPath,
			final String dialogTitle, final JTextField textField)
		{
			File returnFile = currentPath;
			JFileChooser fc = null;

			if (currentPath == null)
			{
				fc = new JFileChooser();
			}
			else
			{
				fc = new JFileChooser(currentPath);
			}

			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle(dialogTitle);

			if (System.getProperty("os.name").startsWith("Mac OS"))
			{
				// On MacOS X, do not traverse file bundles
				fc.putClientProperty("JFileChooser.appBundleIsTraversable",
					"never");
			}

			final int returnVal = fc.showOpenDialog(getParent());

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
	private final class TextFocusLostListener implements FocusListener
	{
		private String initialValue = null;
		private boolean dialogOpened = false;

		/**
		 * @see java.awt.event.FocusListener#focusGained(FocusEvent)
		 */
		@Override
		public void focusGained(FocusEvent e)
		{
			// reset variables
			dialogOpened = false;

			final Object source = e.getSource();

			if (source instanceof JTextField)
			{
				// get the field value
				initialValue = ((JTextField) source).getText();
			}
		}

		/**
		 * @see java.awt.event.FocusListener#focusLost(FocusEvent)
		 */
		@Override
		public void focusLost(FocusEvent e)
		{
			// Check the source to see if it was a text field
			final Object source = e.getSource();

			if (source instanceof JTextField)
			{
				// get the field value and validate it exists
				final String fieldValue = ((JTextField) source).getText();
				final File fieldFile = new File(fieldValue);

				if ((!fieldFile.exists())
					&& (!fieldValue.equalsIgnoreCase("null"))
					&& (fieldValue.trim().length() > 0) && (!dialogOpened))
				{
					// display error dialog and restore previous value
					dialogOpened = true;
					ShowMessageDelegate.showMessageDialog(
						"File does not exist; preferences were not set.",
						"Invalid Path", MessageType.ERROR);
					((JTextField) source).setText(initialValue);
				}
			}
		}
	}
}
