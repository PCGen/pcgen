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

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import pcgen.core.SettingsHandler;
import pcgen.core.UnitSet;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * The Class {@code LanguagePanel} is responsible for
 * managing the language and unit set preferences.
 * 
 * 
 */
@SuppressWarnings("serial")
public final class LanguagePanel extends PCGenPrefsPanel
{
	private final String originalLanguage;
	private final String originalUnitSet;


	private final ChoiceBox<String> unitSetType;

	private final ToggleGroup languageChoiceGroup = new ToggleGroup();

	/**
	 * The set of supported languages.
	 * Should be data controlled, or at least moved to a utility class.
	 */
	private enum LanguageChoice
	{
		// not that order matters w.r.t presentation order
		SYSTEM("system", LanguageBundle.getString("in_Prefs_langSystem")),
		ENGLISH("en", LanguageBundle.getString("in_Prefs_langEnglish")),
		FRENCH("fre", LanguageBundle.getString("in_Prefs_langFrench")),
		GERMAN("ger", LanguageBundle.getString("in_Prefs_langGerman")),
		ITALIAN("it", LanguageBundle.getString("in_Prefs_langItalian")),
		SPANISH("es", LanguageBundle.getString("in_Prefs_langSpanish")),
		PORTUGUESE("pt", LanguageBundle.getString("in_Prefs_langPortuguese"))
		;

		private final String shortName;
		private final String longName;

		private LanguageChoice(final String shortName, final String longName)
		{
			this.shortName = shortName;
			this.longName = longName;
		}

		String getShortName()
		{
			return shortName;
		}

		String getLongName()
		{
			return longName;
		}
	}

	/**
	 * Create a new LanguagePanel
	 */
	public LanguagePanel()
	{
		originalLanguage = ConfigurationSettings.getLanguage();

		if ((SettingsHandler.getGame() != null) && (SettingsHandler.getGame().getUnitSet() != null))
		{
			originalUnitSet = SettingsHandler.getGame().getUnitSet().getDisplayName();
		}
		else
		{
			originalUnitSet = "";
		}

		VBox vbox = new VBox();
		final JFXPanel panel = new JFXPanel();


		for (LanguageChoice languageChoice: LanguageChoice.values())
		{
			ToggleButton languageButton = new RadioButton();
			languageButton.setUserData(languageChoice.getShortName());
			languageButton.setText(languageChoice.getLongName());
			languageButton.setToggleGroup(languageChoiceGroup);
			vbox.getChildren().add(languageButton);
		}

		Collection<UnitSet> unitSets = SettingsHandler.getGame().getModeContext().getReferenceContext()
		                                              .getConstructedCDOMObjects(UnitSet.class);
		Collection<String> names = unitSets.stream()
		                                   .filter(Objects::nonNull)
		                                   .map(UnitSet::getDisplayName)
		                                   .collect(Collectors.toUnmodifiableList());
		ObservableList<String> unitSetNames = FXCollections.observableArrayList(names);
		unitSetType = new ChoiceBox<>();
		unitSetType.setItems(unitSetNames);
		Label unitSetLabel = new Label(LanguageBundle.getString("in_Prefs_unitSetType"));
		unitSetLabel.setLabelFor(unitSetType);
		vbox.getChildren().add(unitSetLabel);
		vbox.getChildren().add(unitSetType);

		Node restartInfo = new Text(LanguageBundle.getString("in_Prefs_restartInfo"));
		vbox.getChildren().add(restartInfo);

		Platform.runLater(() -> {
			Scene scene = new Scene(vbox);
			panel.setScene(scene);
		});

		this.add(panel);
	}


	@Override
	public void applyOptionValuesToControls()
	{
		String origLanguage = ConfigurationSettings.getLanguage();
		if ((origLanguage == null) || origLanguage.isEmpty())
		{
			origLanguage = "system";
		}

		for (Toggle button : languageChoiceGroup.getToggles())
		{
			button.setSelected(button.getUserData() == origLanguage);
		}


		String currentUnitSet;
		if ((SettingsHandler.getGame() != null) && (SettingsHandler.getGame().getUnitSet() != null))
		{
			currentUnitSet = SettingsHandler.getGame().getUnitSet().getDisplayName();
		}
		else
		{
			currentUnitSet = "";
		}
		if (!unitSetType.getItems().isEmpty())
		{
			unitSetType.setValue(currentUnitSet);
		}
	}

	@Override
	public String getTitle()
	{
		return LanguageBundle.getString("in_Prefs_language");
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		if (languageChoiceGroup.getSelectedToggle() != null)
		{
			String languageShortString = (String) languageChoiceGroup.getSelectedToggle().getUserData();

			ConfigurationSettings.setLanguage(languageShortString);
			ConfigurationSettings.setCountry(languageShortString.toUpperCase(Locale.ENGLISH));
		}

		SettingsHandler.getGame().selectUnitSet(unitSetType.getValue());
	}



	@Override
	public boolean needsRestart()
	{
		if (languageChoiceGroup.getSelectedToggle() != null && originalLanguage != languageChoiceGroup.getSelectedToggle().getUserData())
		{
			return true;
		}
		if (originalUnitSet != unitSetType.getValue())
		{
			return true;
		}

		return false;
	}

}
