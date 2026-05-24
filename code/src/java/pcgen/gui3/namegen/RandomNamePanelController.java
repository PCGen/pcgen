/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.gui3.namegen;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import pcgen.core.SettingsHandler;
import pcgen.core.namegen.GeneratedName;
import pcgen.core.namegen.NameGenerator;
import pcgen.core.namegen.Rule;
import pcgen.core.namegen.RuleSet;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 * JavaFX controller behind the random-name dialog. Drives two cascading
 * combo boxes (Category → Title) and a gender radio group with sticky
 * fallback, then asks the headless {@link NameGenerator} for names.
 */
public final class RandomNamePanelController
{
	@FXML
	private ComboBox<String> categoryCombo;
	@FXML
	private ComboBox<String> titleCombo;
	@FXML
	private ToggleGroup genderGroup;
	@FXML
	private RadioButton genderFemale;
	@FXML
	private RadioButton genderMale;
	@FXML
	private RadioButton genderOther;
	@FXML
	private TextField generatedNameLabel;
	@FXML
	private Label meaningLabel;
	@FXML
	private Label pronunciationLabel;
	@FXML
	private CheckBox randomStructureCheck;
	@FXML
	private ComboBox<Rule> structureCombo;

	private NameGenerator nameGenerator;
	private String chosenName = "";
	private String chosenGender = "";
	private boolean cancelled = true;
	private String preferredGender;
	private Runnable closeAction;

	@FXML
	void initialize()
	{
		try
		{
			nameGenerator = new NameGenerator(new File(getDataDir()));
		}
		catch (IOException e)
		{
			Logging.errorPrint("failed to load random-name data", e);
			generatedNameLabel.setText(LanguageBundle.getString("in_rndNmDefault"));
			return;
		}

		categoryCombo.setItems(FXCollections.observableArrayList(nameGenerator.getCategories()));
		categoryCombo.valueProperty().addListener((obs, old, val) -> onCategoryChanged(val));
		titleCombo.valueProperty().addListener((obs, old, val) -> onTitleChanged(val));
		genderGroup.selectedToggleProperty().addListener((obs, old, val) -> onGenderChanged(val));
		randomStructureCheck.selectedProperty().addListener((obs, old, val) -> onRandomStructureChanged(val));
		structureCombo.setDisable(true);
		setOptionalLabel(meaningLabel, "in_rndNameMeaning", "", "");
		setOptionalLabel(pronunciationLabel, "in_rndNmPronounciation", "", "");

		if (!categoryCombo.getItems().isEmpty())
		{
			categoryCombo.getSelectionModel().selectFirst();
		}
	}

	/**
	 * Called by {@link RandomNameDialog} before showing, to pre-select a
	 * gender if the character already has one. Must be invoked on the FX
	 * thread, after the FXML has loaded.
	 */
	public void setInitialGender(String gender)
	{
		preferredGender = gender;
		if (gender != null && !gender.isEmpty() && titleCombo.getValue() != null)
		{
			selectGender(gender);
		}
	}

	/**
	 * Hook supplied by {@link RandomNameDialog} so OK/Cancel can dispose
	 * the hosting Swing dialog without the controller knowing about it.
	 */
	public void setCloseAction(Runnable closeAction)
	{
		this.closeAction = closeAction;
	}

	private void onCategoryChanged(String category)
	{
		if (category == null)
		{
			titleCombo.setItems(FXCollections.emptyObservableList());
			return;
		}
		List<String> titles = nameGenerator.getTitlesFor(category);
		titleCombo.setItems(FXCollections.observableArrayList(titles));
		if (!titles.isEmpty())
		{
			titleCombo.getSelectionModel().selectFirst();
		}
	}

	private void onTitleChanged(String title)
	{
		String category = categoryCombo.getValue();
		if (category == null || title == null)
		{
			disableAllGenders();
			return;
		}
		List<String> available = nameGenerator.getGendersFor(category, title);
		genderFemale.setDisable(!available.contains("Female"));
		genderMale.setDisable(!available.contains("Male"));
		genderOther.setDisable(!available.contains("Other"));

		String previous = currentGender();
		String target = chooseStickyGender(available, previous);
		selectGender(target);
	}

	private void onGenderChanged(Toggle selected)
	{
		// Cleared selection happens transiently while we swap toggles —
		// don't react until a button is actually selected.
		if (selected == null)
		{
			return;
		}
		refreshStructureCombo();
		clearOutput();
	}

	private void onRandomStructureChanged(boolean random)
	{
		structureCombo.setDisable(random);
	}

	private void refreshStructureCombo()
	{
		RuleSet catalog = currentCatalog();
		if (catalog == null)
		{
			structureCombo.setItems(FXCollections.emptyObservableList());
			return;
		}
		structureCombo.setItems(FXCollections.observableArrayList(nameGenerator.getRulesFor(catalog)));
		if (!structureCombo.getItems().isEmpty())
		{
			structureCombo.getSelectionModel().selectFirst();
		}
	}

	@FXML
	void onGenerate(ActionEvent event)
	{
		RuleSet catalog = currentCatalog();
		if (catalog == null)
		{
			return;
		}
		try
		{
			GeneratedName result = useForcedRule()
					? nameGenerator.generateWithRule(structureCombo.getValue())
					: nameGenerator.generate(catalog);
			generatedNameLabel.setText(result.name());
			setOptionalLabel(meaningLabel, "in_rndNameMeaning", result.meaning(), result.name());
			setOptionalLabel(pronunciationLabel, "in_rndNmPronounciation", result.pronunciation(), result.name());
		}
		catch (Exception e)
		{
			Logging.errorPrint("failed to generate random name", e);
			generatedNameLabel.setText(LanguageBundle.getString("in_rndNmDefault"));
			setOptionalLabel(meaningLabel, "in_rndNameMeaning", "", "");
			setOptionalLabel(pronunciationLabel, "in_rndNmPronounciation", "", "");
		}
	}

	private boolean useForcedRule()
	{
		return !randomStructureCheck.isSelected() && structureCombo.getValue() != null;
	}

	@FXML
	void onOk(ActionEvent event)
	{
		String text = generatedNameLabel.getText();
		String defaultLabel = LanguageBundle.getString("in_rndNmDefault");
		// If the user clicks OK without ever generating, treat as cancel.
		if (text == null || text.isEmpty() || text.equals(defaultLabel))
		{
			cancelled = true;
		}
		else
		{
			cancelled = false;
			chosenName = text;
			chosenGender = currentGender();
		}
		fireClose();
	}

	@FXML
	void onCancel(ActionEvent event)
	{
		cancelled = true;
		fireClose();
	}

	public String getChosenName()
	{
		return cancelled ? "" : chosenName;
	}

	public String getGender()
	{
		return cancelled ? "" : chosenGender;
	}

	private RuleSet currentCatalog()
	{
		String category = categoryCombo.getValue();
		String title = titleCombo.getValue();
		String gender = currentGender();
		if (category == null || title == null || gender.isEmpty())
		{
			return null;
		}
		return nameGenerator.getCatalog(category, title, gender);
	}

	private String currentGender()
	{
		Toggle selected = genderGroup.getSelectedToggle();
		if (selected instanceof RadioButton rb)
		{
			return rb.getText();
		}
		return "";
	}

	private String chooseStickyGender(List<String> available, String previous)
	{
		return GenderSelection.chooseSticky(available, previous, preferredGender);
	}

	private void selectGender(String gender)
	{
		RadioButton target = switch (gender)
		{
			case "Female" -> genderFemale;
			case "Male" -> genderMale;
			case "Other" -> genderOther;
			default -> null;
		};
		if (target != null && !target.isDisable())
		{
			target.setSelected(true);
		}
		else
		{
			genderGroup.selectToggle(null);
		}
	}

	private void disableAllGenders()
	{
		genderFemale.setDisable(true);
		genderMale.setDisable(true);
		genderOther.setDisable(true);
		genderGroup.selectToggle(null);
	}

	private void clearOutput()
	{
		generatedNameLabel.setText(LanguageBundle.getString("in_rndNmDefault"));
		setOptionalLabel(meaningLabel, "in_rndNameMeaning", "", "");
		setOptionalLabel(pronunciationLabel, "in_rndNmPronounciation", "", "");
	}

	private static void setOptionalLabel(Label label, String prefixKey, String value, String generatedName)
	{
		boolean hasValue = value != null && !value.isEmpty() && !value.equals(generatedName);
		String shown = hasValue ? value : "-";
		label.setText(LanguageBundle.getString(prefixKey) + " " + shown);
	}

	private void fireClose()
	{
		if (closeAction != null)
		{
			closeAction.run();
		}
	}

	private static String getDataDir()
	{
		return Objects.requireNonNull(SettingsHandler.getGmgenPluginDir()).toString()
				+ File.separator + "Random Names";
	}
}
