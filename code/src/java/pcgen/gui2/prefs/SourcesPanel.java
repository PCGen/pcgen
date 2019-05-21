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

import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * The Class {@code SourcesPanel} is responsible for
 * displaying source related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 */
public final class SourcesPanel extends PCGenPrefsPanel
{
	private static final String IN_SOURCES = LanguageBundle.getString("in_Prefs_sources");

	private final CheckBox campLoad = new CheckBox();
	private final CheckBox charCampLoad = new CheckBox();
	private final CheckBox allowOptsInSource = new CheckBox();
	private final CheckBox saveCustom = new CheckBox();
	private final CheckBox showOGL = new CheckBox();
	private final CheckBox showMature = new CheckBox();
	private final ComboBox<String> sourceOptions = new ComboBox<>();
	private final CheckBox loadURL = new CheckBox();
	private final CheckBox allowOverride = new CheckBox();
	private final CheckBox skipSourceSelect = new CheckBox();
	private final CheckBox useAdvancedSourceSelect = new CheckBox();
	private final CheckBox allowMultiLineObjectsSelect = new CheckBox();

	/**
	 * Instantiates a new monster panel.
	 */
	public SourcesPanel()
	{

		TitledPane outerPane = new TitledPane();
		outerPane.setText(IN_SOURCES);
		VBox vbox = new VBox();
		outerPane.setContent(vbox);

		campLoad.setText(LanguageBundle.getString("in_Prefs_autoLoadAtStart"));
		vbox.getChildren().add(campLoad);

		charCampLoad.setText(LanguageBundle.getString("in_Prefs_autoLoadWithPC"));
		vbox.getChildren().add(charCampLoad);

		allowOptsInSource.setText(LanguageBundle.getString("in_Prefs_allowOptionInSource"));
		vbox.getChildren().add(allowOptsInSource);

		saveCustom.setText(LanguageBundle.getString("in_Prefs_saveCustom"));
		vbox.getChildren().add(saveCustom);

		showOGL.setText(LanguageBundle.getString("in_Prefs_displayOGL"));
		vbox.getChildren().add(showOGL);

		showMature.setText(LanguageBundle.getString("in_Prefs_displayMature"));
		vbox.getChildren().add(showMature);

		Node label = new Text(LanguageBundle.getString("in_Prefs_sourceDisplay"));
		vbox.getChildren().add(label);
		var choices = FXCollections.observableList(List.of(LanguageBundle.getString("in_Prefs_sdLong"),
				LanguageBundle.getString("in_Prefs_sdMedium"),
				LanguageBundle.getString("in_Prefs_sdShort"),
				LanguageBundle.getString("in_Prefs_sdPage"),
				LanguageBundle.getString("in_Prefs_sdWeb")));
		sourceOptions.setItems(choices);
		vbox.getChildren().add(sourceOptions);

		loadURL.setText(LanguageBundle.getString("in_Prefs_loadURLs"));
		vbox.getChildren().add(loadURL);
		loadURL.setOnAction(evt -> {
			if (loadURL.isSelected())
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_Prefs_urlBlocked"),
					Constants.APPLICATION_NAME, MessageType.WARNING);
			}
		});

		allowOverride.setText(LanguageBundle.getString("in_Prefs_allowOverride"));
		vbox.getChildren().add(allowOverride);

		skipSourceSelect.setText(LanguageBundle.getString("in_Prefs_skipSourceSelect"));
		vbox.getChildren().add(skipSourceSelect);

		useAdvancedSourceSelect.setText(LanguageBundle.getString("in_Prefs_useAdvancedSourceSelect"));
		vbox.getChildren().add(useAdvancedSourceSelect);

		allowMultiLineObjectsSelect.setText(
			LanguageBundle.getString("in_Prefs_allowMultiLineObjectsSelect"));
		vbox.getChildren().add(allowMultiLineObjectsSelect);
	}

	@Override
	public String getTitle()
	{
		return IN_SOURCES;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_AT_START, campLoad.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_WITH_PC,
			charCampLoad.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_ALLOWED_IN_SOURCES,
			allowOptsInSource.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT, saveCustom.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SHOW_LICENSE, showOGL.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD, showMature.isSelected());
		SettingsHandler.setLoadURLs(loadURL.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_ALLOW_OVERRIDE_DUPLICATES,
			allowOverride.isSelected());

		UIPropertyContext.getInstance().setBoolean(UIPropertyContext.SKIP_SOURCE_SELECTION,
			skipSourceSelect.isSelected());
		UIPropertyContext.getInstance().setBoolean(UIPropertyContext.SOURCE_USE_BASIC_KEY,
			!useAdvancedSourceSelect.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SOURCES_ALLOW_MULTI_LINE,
			allowMultiLineObjectsSelect.isSelected());

		switch (sourceOptions.getSelectionModel().getSelectedIndex())
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
				Logging.errorPrint("In PreferencesDialog.setOptionsBasedOnControls " + "(sourceOptions) the index "
					+ sourceOptions.getSelectionModel().getSelectedIndex() + " is unsupported.");

				break;
		}
	}

	@Override
	public void applyOptionValuesToControls()
	{
		campLoad.setSelected(
			PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_AT_START, false));
		charCampLoad.setSelected(
			PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_WITH_PC, true));
		allowOptsInSource
			.setSelected(PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_ALLOWED_IN_SOURCES, true));

		saveCustom.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT));
		showOGL.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_LICENSE));
		showMature.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD));
		loadURL.setSelected(SettingsHandler.isLoadURLs());
		allowOverride.setSelected(
			PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_ALLOW_OVERRIDE_DUPLICATES, true));
		skipSourceSelect
			.setSelected(UIPropertyContext.getInstance().getBoolean(UIPropertyContext.SKIP_SOURCE_SELECTION));
		useAdvancedSourceSelect
			.setSelected(!UIPropertyContext.getInstance().getBoolean(UIPropertyContext.SOURCE_USE_BASIC_KEY));
		allowMultiLineObjectsSelect
			.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SOURCES_ALLOW_MULTI_LINE));

		switch (Globals.getSourceDisplay())
		{
			case LONG:
				sourceOptions.getSelectionModel().select(0);

				break;

			case MEDIUM:
				sourceOptions.getSelectionModel().select(1);

				break;

			case SHORT:
				sourceOptions.getSelectionModel().select(2);

				break;

			case PAGE:
				sourceOptions.getSelectionModel().select(3);

				break;

			case WEB:
				sourceOptions.getSelectionModel().select(4);

				break;

			default:
				Logging.errorPrint("In PreferencesDialog.applyOptionValuesToControls " + "(source display) the option "
					+ Globals.getSourceDisplay() + " is unsupported.");

				break;
		}
	}

}
