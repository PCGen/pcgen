/*
 * Copyright 2008 (C) James Dempsey
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

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.JComboBoxEx;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

/**
 * The Class {@code SourcesPanel} is responsible for
 * displaying source related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 */
@SuppressWarnings("serial")
public class SourcesPanel extends PCGenPrefsPanel
{
	private static final String in_sources =
		LanguageBundle.getString("in_Prefs_sources"); //$NON-NLS-1$
	
	private final JCheckBox campLoad = new JCheckBox();
	private final JCheckBox charCampLoad = new JCheckBox();
	private final JCheckBox allowOptsInSource = new JCheckBox();
	private final JCheckBox saveCustom = new JCheckBox();
	private final JCheckBox showOGL = new JCheckBox();
	private final JCheckBox showMature = new JCheckBox();
	private final JCheckBox showSponsors = new JCheckBox();
	private JComboBoxEx sourceOptions = new JComboBoxEx();
	private final JCheckBox loadURL = new JCheckBox();
	private final JCheckBox allowOverride = new JCheckBox();
	private final JCheckBox skipSourceSelect = new JCheckBox();
	private final JCheckBox useAdvancedSourceSelect = new JCheckBox();
	private final JCheckBox allowMultiLineObjectsSelect = new JCheckBox();

	/**
	 * Instantiates a new monster panel.
	 */
	public SourcesPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_sources);

		title1.setTitleJustification(TitledBorder.LEADING);
		this.setBorder(title1);
		this.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, GridBagConstraints.REMAINDER, 1, 0, 0);
		campLoad.setText(LanguageBundle.getString("in_Prefs_autoLoadAtStart")); //$NON-NLS-1$
		gridbag.setConstraints(campLoad, c);
		this.add(campLoad);

		Utility.buildConstraints(c, 0, 1, GridBagConstraints.REMAINDER, 1, 0, 0);
		charCampLoad.setText(LanguageBundle.getString("in_Prefs_autoLoadWithPC")); //$NON-NLS-1$
		gridbag.setConstraints(charCampLoad, c);
		this.add(charCampLoad);

		Utility.buildConstraints(c, 0, 2, GridBagConstraints.REMAINDER, 1, 0, 0);
		allowOptsInSource.setText(LanguageBundle
					.getString("in_Prefs_allowOptionInSource")); //$NON-NLS-1$
		gridbag.setConstraints(allowOptsInSource, c);
		this.add(allowOptsInSource);

		Utility.buildConstraints(c, 0, 3, GridBagConstraints.REMAINDER, 1, 0, 0);
		saveCustom.setText(LanguageBundle.getString("in_Prefs_saveCustom")); //$NON-NLS-1$
		gridbag.setConstraints(saveCustom, c);
		this.add(saveCustom);

		Utility.buildConstraints(c, 0, 4, GridBagConstraints.REMAINDER, 1, 0, 0);
		showOGL.setText(LanguageBundle.getString("in_Prefs_displayOGL")); //$NON-NLS-1$
		gridbag.setConstraints(showOGL, c);
		this.add(showOGL);

		Utility.buildConstraints(c, 0, 6, GridBagConstraints.REMAINDER, 1, 0, 0);
		showSponsors.setText(LanguageBundle.getString("in_Prefs_displaySponsors")); //$NON-NLS-1$
		gridbag.setConstraints(showSponsors, c);
		this.add(showSponsors);

		Utility.buildConstraints(c, 0, 7, GridBagConstraints.REMAINDER, 1, 0, 0);
		showMature.setText(LanguageBundle.getString("in_Prefs_displayMature")); //$NON-NLS-1$
		gridbag.setConstraints(showMature, c);
		this.add(showMature);

		Utility.buildConstraints(c, 0, 8, 1, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_sourceDisplay")); //$NON-NLS-1$
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 3, 8, 1, 1, 0, 0);
		sourceOptions =
				new JComboBoxEx(new String[]{LanguageBundle.getString("in_Prefs_sdLong"), LanguageBundle.getString("in_Prefs_sdMedium"),
					LanguageBundle.getString("in_Prefs_sdShort"), LanguageBundle.getString("in_Prefs_sdPage"), LanguageBundle.getString("in_Prefs_sdWeb")});
		gridbag.setConstraints(sourceOptions, c);
		this.add(sourceOptions);

		Utility.buildConstraints(c, 0, 9, GridBagConstraints.REMAINDER, 1, 0, 0);
		loadURL.setText(LanguageBundle.getString("in_Prefs_loadURLs")); //$NON-NLS-1$
		gridbag.setConstraints(loadURL, c);
		this.add(loadURL);
		loadURL.addActionListener(evt -> {
            if (((JCheckBox) evt.getSource()).isSelected())
            {
                ShowMessageDelegate.showMessageDialog(LanguageBundle
                    .getString("in_Prefs_urlBlocked"), Constants.APPLICATION_NAME, //$NON-NLS-1$
                    MessageType.WARNING);
            }
        });

		Utility.buildConstraints(c, 0, 10, GridBagConstraints.REMAINDER, 1, 0, 0);
		allowOverride.setText(LanguageBundle.getString("in_Prefs_allowOverride")); //$NON-NLS-1$
		gridbag.setConstraints(allowOverride, c);
		this.add(allowOverride);

		Utility.buildConstraints(c, 0, 11, GridBagConstraints.REMAINDER, 1, 0, 0);
		skipSourceSelect.setText(LanguageBundle.getString("in_Prefs_skipSourceSelect")); //$NON-NLS-1$
		gridbag.setConstraints(skipSourceSelect, c);
		this.add(skipSourceSelect);

		Utility.buildConstraints(c, 0, 12, GridBagConstraints.REMAINDER, 1, 0, 0);
		useAdvancedSourceSelect.setText(LanguageBundle.getString("in_Prefs_useAdvancedSourceSelect")); //$NON-NLS-1$
		gridbag.setConstraints(useAdvancedSourceSelect, c);
		this.add(useAdvancedSourceSelect);

		Utility.buildConstraints(c, 0, 13, GridBagConstraints.REMAINDER, 1, 0, 0);
		allowMultiLineObjectsSelect.setText(LanguageBundle.getString("in_Prefs_allowMultiLineObjectsSelect")); //$NON-NLS-1$
		gridbag.setConstraints(allowMultiLineObjectsSelect, c);
		this.add(allowMultiLineObjectsSelect);

		Utility.buildConstraints(c, 5, 20, GridBagConstraints.REMAINDER, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel();
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_sources;
	}
	
	/**
	 * @see pcgen.gui2.prefs.PreferencesPanel#applyPreferences()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_AUTOLOAD_SOURCES_AT_START,
			campLoad.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_AUTOLOAD_SOURCES_WITH_PC, charCampLoad.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_ALLOWED_IN_SOURCES, allowOptsInSource.isSelected());
		PCGenSettings.OPTIONS_CONTEXT
			.setBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT,
				saveCustom.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_SHOW_LICENSE, showOGL.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD, showMature.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_SHOW_SPONSORS_ON_LOAD, showSponsors.isSelected());
		SettingsHandler.setLoadURLs(loadURL.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_ALLOW_OVERRIDE_DUPLICATES,
			allowOverride.isSelected());
		
		UIPropertyContext.getInstance().setBoolean(
			UIPropertyContext.SKIP_SOURCE_SELECTION,
			skipSourceSelect.isSelected());
		UIPropertyContext.getInstance().setBoolean(
			UIPropertyContext.SOURCE_USE_BASIC_KEY,
			!useAdvancedSourceSelect.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_SOURCES_ALLOW_MULTI_LINE,
			allowMultiLineObjectsSelect.isSelected());

		switch (sourceOptions.getSelectedIndex())
		{
			case 0:
				Globals.setSourceDisplay(SourceFormat.LONG);
				break;

			case 1:
				Globals.setSourceDisplay(SourceFormat.MEDIUM);
				break;

			case 2:
				Globals.setSourceDisplay(SourceFormat.SHORT);
				break;

			case 3:
				Globals.setSourceDisplay(SourceFormat.PAGE);
				break;

			case 4:
				Globals.setSourceDisplay(SourceFormat.WEB);
				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.setOptionsBasedOnControls (sourceOptions) the index "
						+ sourceOptions.getSelectedIndex() + " is unsupported.");

				break;
		}
	}

	/**
	 * @see pcgen.gui2.prefs.PreferencesPanel#initPreferences()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		campLoad.setSelected(PCGenSettings.OPTIONS_CONTEXT.initBoolean(
			PCGenSettings.OPTION_AUTOLOAD_SOURCES_AT_START, false));
		charCampLoad.setSelected(PCGenSettings.OPTIONS_CONTEXT.initBoolean(
			PCGenSettings.OPTION_AUTOLOAD_SOURCES_WITH_PC, true));
		allowOptsInSource.setSelected(PCGenSettings.OPTIONS_CONTEXT.initBoolean(
			PCGenSettings.OPTION_ALLOWED_IN_SOURCES, true));
		
		saveCustom.setSelected(PCGenSettings.OPTIONS_CONTEXT
			.getBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT));
		showOGL.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(
			PCGenSettings.OPTION_SHOW_LICENSE));
		showMature.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(
			PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD));
		showSponsors.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(
			PCGenSettings.OPTION_SHOW_SPONSORS_ON_LOAD));
		loadURL.setSelected(SettingsHandler.isLoadURLs());
		allowOverride.setSelected(PCGenSettings.OPTIONS_CONTEXT.initBoolean(
			PCGenSettings.OPTION_ALLOW_OVERRIDE_DUPLICATES, true));
		skipSourceSelect.setSelected(UIPropertyContext.getInstance()
				.getBoolean(UIPropertyContext.SKIP_SOURCE_SELECTION));
		useAdvancedSourceSelect.setSelected(!UIPropertyContext.getInstance()
				.getBoolean(UIPropertyContext.SOURCE_USE_BASIC_KEY));
		allowMultiLineObjectsSelect.setSelected(PCGenSettings.OPTIONS_CONTEXT
			.getBoolean(PCGenSettings.OPTION_SOURCES_ALLOW_MULTI_LINE));
		
		switch (Globals.getSourceDisplay())
		{
			case LONG:
				sourceOptions.setSelectedIndex(0);

				break;

			case MEDIUM:
				sourceOptions.setSelectedIndex(1);

				break;

			case SHORT:
				sourceOptions.setSelectedIndex(2);

				break;

			case PAGE:
				sourceOptions.setSelectedIndex(3);

				break;

			case WEB:
				sourceOptions.setSelectedIndex(4);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.applyOptionValuesToControls (source display) the option "
						+ Globals.getSourceDisplay() + " is unsupported.");

				break;
		}
	}

}
