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
import java.util.Arrays;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SkillFilter;
import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.BooleanUtils;

/**
 * The Class {@code OutputPanel} is responsible for displaying character output related preferences and allowing the
 * preferences to be edited by the user.
 */
public final class OutputPanel extends PCGenPrefsPanel
{
	private static final String IN_OUTPUT = LanguageBundle.getString("in_Prefs_output");

	private static final String IN_ALWAYS_OVERWRITE = LanguageBundle.getString("in_Prefs_alwaysOverwrite");
	private static final String IN_OUTPUT_SHEET_EQ_SET = LanguageBundle.getString("in_Prefs_templateEqSet");
	private static final String IN_PAPER_TYPE = LanguageBundle.getString("in_Prefs_paperType");
	private static final String IN_POST_EXPORT_COMAND_STANDARD =
			LanguageBundle.getString("in_Prefs_postExportCommandStandard");
	private static final String IN_POST_EXPORT_COMMAND_PDF = LanguageBundle.getString("in_Prefs_postExportCommandPDF");
	private static final String IN_REMOVE_TEMP = LanguageBundle.getString("in_Prefs_removeTemp");
	private static final String IN_SAVE_OUTPUT_SHEET_WITH_PC =
			LanguageBundle.getString("in_Prefs_saveOutputSheetWithPC");
	private static final String IN_WEAPON_PROF_PRINTOUT = LanguageBundle.getString("in_Prefs_weaponProfPrintout");
	private static final String IN_SKILL_FILTER = LanguageBundle.getString("in_Prefs_skillFilterLabel");
	private static final String IN_CHOOSE = LanguageBundle.getString("...");
	private static final String IN_GENERATE_TEMP_FILE_WITH_PDF =
			LanguageBundle.getString("in_Prefs_generateTempFileWithPdf");

	private final CheckBox printSpellsWithPC = new CheckBox();
	private final CheckBox removeTempFiles = new CheckBox(IN_REMOVE_TEMP);
	private final CheckBox saveOutputSheetWithPC = new CheckBox();
	private final CheckBox generateTempFileWithPdf = new CheckBox(IN_GENERATE_TEMP_FILE_WITH_PDF);

	private final CheckBox weaponProfPrintout;
	private final Button outputSheetEqSetButton;
	private final Button outputSheetHTMLDefaultButton;
	private final Button outputSheetPDFDefaultButton;
	private final Button outputSheetSpellsDefaultButton;

	private final TextField outputSheetEqSet;
	private final TextField outputSheetHTMLDefault;
	private final TextField outputSheetPDFDefault;
	private final TextField outputSheetSpellsDefault;

	private final ComboBox<String> paperType;
	private final ComboBox<SkillFilter> skillFilter = new ComboBox<>();
	private final ComboBox<ExportChoices> exportChoice =
			new ComboBox<>(FXCollections.observableArrayList(ExportChoices.values()));

	private final TextField postExportCommandStandard;
	private final TextField postExportCommandPDF;
	private final CheckBox alwaysOverwrite;

	/**
	 * Instantiates a new output panel.
	 */
	public OutputPanel()
	{
		GridPane outerPanel = new GridPane();

		// TODO: make the label+text+button into a "Control"
		int row = 0;
		Label label = new Label(LanguageBundle.getString("in_Prefs_outputSheetHTMLDefault"));
		outerPanel.add(label, 0, row);
		outputSheetHTMLDefault =
				new TextField(String.valueOf(SettingsHandler.getSelectedCharacterHTMLOutputSheet(null)));
		outerPanel.add(outputSheetHTMLDefault, 1, row);
		outputSheetHTMLDefaultButton = new Button(IN_CHOOSE);
		outerPanel.add(outputSheetHTMLDefaultButton, 2, row);
		outputSheetHTMLDefaultButton.setOnAction(this::onOutputSheetHTMLDefaultButton);

		++row;
		label = new Label(LanguageBundle.getString("in_Prefs_outputSheetPDFDefault"));
		outerPanel.add(label, 0, row);
		outputSheetPDFDefault =
				new TextField(String.valueOf(SettingsHandler.getSelectedCharacterPDFOutputSheet(null)));
		outerPanel.add(outputSheetPDFDefault, 1, row);
		outputSheetPDFDefaultButton = new Button(IN_CHOOSE);
		outerPanel.add(outputSheetPDFDefaultButton, 2, row);
		outputSheetPDFDefaultButton.setOnAction(this::onOutputSheetPDFDefaultButton);

		++row;
		label = new Label(IN_OUTPUT_SHEET_EQ_SET);
		outerPanel.add(label, 0, row);
		outputSheetEqSet = new TextField(String.valueOf(SettingsHandler.getSelectedEqSetTemplate()));
		outerPanel.add(outputSheetEqSet, 1, row);
		outputSheetEqSetButton = new Button(IN_CHOOSE);
		outerPanel.add(outputSheetEqSetButton, 2, row);
		outputSheetEqSetButton.setOnAction(this::onOutputSheetEqSetButton);

		++row;
		saveOutputSheetWithPC.setText(IN_SAVE_OUTPUT_SHEET_WITH_PC);
		outerPanel.add(saveOutputSheetWithPC, 0, row);

		++row;
		label = new Label(LanguageBundle.getString("in_Prefs_outputSpellSheetDefault"));
		outerPanel.add(label, 0, row);
		outputSheetSpellsDefault = new TextField(String.valueOf(SettingsHandler.getSelectedSpellSheet()));
		outerPanel.add(outputSheetSpellsDefault, 1, row);
		outputSheetSpellsDefaultButton = new Button(IN_CHOOSE);
		outerPanel.add(outputSheetSpellsDefaultButton, 2, row);
		outputSheetSpellsDefaultButton.setOnAction(this::onOutputSheetSpellsDefaultButton);

		++row;
		printSpellsWithPC.setText(LanguageBundle.getString("in_Prefs_printSpellsWithPC"));
		outerPanel.add(printSpellsWithPC, 0, row);

		++row;
		label = new Label(IN_PAPER_TYPE);
		outerPanel.add(label, 0, row);
		final int paperCount = Globals.getPaperCount();
		String[] paperNames = new String[paperCount];
		Arrays.setAll(paperNames, i ->  Globals.getPaperInfo(i, PaperInfo.NAME));
		paperType = new ComboBox<>(FXCollections.observableArrayList(paperNames));
		outerPanel.add(paperType, 1, row);

		++row;
		outerPanel.add(removeTempFiles, 0, row);

		++row;
		weaponProfPrintout = new CheckBox(IN_WEAPON_PROF_PRINTOUT);
		weaponProfPrintout.setSelected(SettingsHandler.getWeaponProfPrintout());
		outerPanel.add(weaponProfPrintout, 0, row);

		++row;
		label = new Label(IN_POST_EXPORT_COMAND_STANDARD);
		outerPanel.add(label, 0, row );
		postExportCommandStandard = new TextField(String.valueOf(SettingsHandler.getPostExportCommandStandard()));
		outerPanel.add(postExportCommandStandard, 1, row);

		++row;
		label = new Label(IN_POST_EXPORT_COMMAND_PDF);
		outerPanel.add(label, 0, row);
		postExportCommandPDF = new TextField(String.valueOf(SettingsHandler.getPostExportCommandPDF()));
		outerPanel.add(postExportCommandPDF, 1, row);

		++row;
		label = new Label(IN_SKILL_FILTER);
		outerPanel.add(label, 0, row);
		SkillFilter[] skillFilterItemArray = {SkillFilter.Ranks, SkillFilter.NonDefault, SkillFilter.Usable,
				SkillFilter.All};
		ObservableList<SkillFilter> skillFilterItems = FXCollections.observableArrayList(skillFilterItemArray);
		skillFilter.setItems(skillFilterItems);
		skillFilter.getSelectionModel().select(SkillFilter.getByValue(
				PCGenSettings.OPTIONS_CONTEXT.initInt(
						PCGenSettings.OPTION_SKILL_FILTER,
						SkillFilter.Usable.getValue()
				)));
		outerPanel.add(skillFilter, 1, row);

		++row;
		alwaysOverwrite = new CheckBox(IN_ALWAYS_OVERWRITE);
		alwaysOverwrite.setSelected(SettingsHandler.getAlwaysOverwrite());
		outerPanel.add(alwaysOverwrite, 0, row);

		++row;
		label = new Label(LanguageBundle.getString("in_Prefs_exportChoice")); // $NON-NSL-1$
		outerPanel.add(label, 0, row);
		outerPanel.add(exportChoice, 1, row);

		++row;
		outerPanel.add(generateTempFileWithPdf, 0, row);
		this.add(GuiUtility.wrapParentAsJFXPanel(outerPanel));
	}

	@Override
	public String getTitle()
	{
		return IN_OUTPUT;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		UIPropertyContext context = UIPropertyContext.getInstance();
		Globals.selectPaper(paperType.getSelectionModel().getSelectedItem());

		context.setBoolean(UIPropertyContext.CLEANUP_TEMP_FILES, removeTempFiles.isSelected());

		if (SettingsHandler.getWeaponProfPrintout() != weaponProfPrintout.isSelected())
		{
			SettingsHandler.setWeaponProfPrintout(weaponProfPrintout.isSelected());
		}

		if (SettingsHandler.getAlwaysOverwrite() || alwaysOverwrite.isSelected())
		{
			SettingsHandler.setAlwaysOverwrite(alwaysOverwrite.isSelected());
		}

		context.setProperty(UIPropertyContext.DEFAULT_HTML_OUTPUT_SHEET, outputSheetHTMLDefault.getText());
		context.setProperty(UIPropertyContext.DEFAULT_PDF_OUTPUT_SHEET, outputSheetPDFDefault.getText());
		SettingsHandler.setSelectedEqSetTemplate(outputSheetEqSet.getText());
		context.setBoolean(UIPropertyContext.SAVE_OUTPUT_SHEET_WITH_PC, saveOutputSheetWithPC.isSelected());
		SettingsHandler.setSelectedSpellSheet(outputSheetSpellsDefault.getText());
		SettingsHandler.setPrintSpellsWithPC(printSpellsWithPC.isSelected());
		SettingsHandler.setPostExportCommandStandard(postExportCommandStandard.getText());
		SettingsHandler.setPostExportCommandPDF(postExportCommandPDF.getText());
		PCGenSettings.OPTIONS_CONTEXT.setInt(
				PCGenSettings.OPTION_SKILL_FILTER,
				skillFilter.getSelectionModel().getSelectedItem().getValue()
		);

		ExportChoices choice = exportChoice.getSelectionModel().getSelectedItem();
		context.setProperty(UIPropertyContext.ALWAYS_OPEN_EXPORT_FILE, choice.getValue());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
				PCGenSettings.OPTION_GENERATE_TEMP_FILE_WITH_PDF,
				generateTempFileWithPdf.isSelected()
		);
	}

	@Override
	public void applyOptionValuesToControls()
	{
		UIPropertyContext context = UIPropertyContext.getInstance();

		GuiAssertions.assertIsNotJavaFXThread();
		Platform.runLater(() -> {
			paperType.getSelectionModel().select(Globals.getSelectedPaper());
			weaponProfPrintout.setSelected(SettingsHandler.getWeaponProfPrintout());

			outputSheetHTMLDefault.setText(context.getProperty(UIPropertyContext.DEFAULT_HTML_OUTPUT_SHEET));
			outputSheetPDFDefault.setText(context.getProperty(UIPropertyContext.DEFAULT_PDF_OUTPUT_SHEET));
			saveOutputSheetWithPC.setSelected(context.getBoolean(UIPropertyContext.SAVE_OUTPUT_SHEET_WITH_PC));
			removeTempFiles.setSelected(context.initBoolean(UIPropertyContext.CLEANUP_TEMP_FILES, true));

			printSpellsWithPC.setSelected(SettingsHandler.getPrintSpellsWithPC());
			skillFilter.getSelectionModel().select(SkillFilter.getByValue(
					PCGenSettings.OPTIONS_CONTEXT.initInt(
							PCGenSettings.OPTION_SKILL_FILTER,
							SkillFilter.Usable.getValue()
					)));

			String value = context.getProperty(UIPropertyContext.ALWAYS_OPEN_EXPORT_FILE);
			exportChoice.getSelectionModel().select(ExportChoices.getChoice(value));

			generateTempFileWithPdf.setSelected(
					PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_GENERATE_TEMP_FILE_WITH_PDF, false));
		});
	}

	private void onOutputSheetHTMLDefaultButton(final ActionEvent actionEvent)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(LanguageBundle.getString("in_Prefs_outputSheetHTMLDefaultTitle"));
		fileChooser.setInitialDirectory(new File(SettingsHandler.getHTMLOutputSheetPath()));
		fileChooser.setInitialFileName(SettingsHandler.getSelectedCharacterHTMLOutputSheet(null));
		File newTemplate = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));

		if (newTemplate != null)
		{
			if ((!newTemplate.getName().startsWith("csheet") && !newTemplate.getName().startsWith("psheet")))
			{
				ShowMessageDelegate.showMessageDialog(
						LanguageBundle.getString("in_Prefs_outputSheetDefaultError"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, MessageType.ERROR
				);
			}
			else
			{
				if (newTemplate.getName().startsWith("csheet"))
				{
					SettingsHandler.setSelectedCharacterHTMLOutputSheet(newTemplate.getAbsolutePath(), null);
				}
				else
				{
					//it must be a psheet
					SettingsHandler.setSelectedPartyHTMLOutputSheet(newTemplate.getAbsolutePath());
				}
			}
		}

		outputSheetHTMLDefault
				.setText(String.valueOf(SettingsHandler.getSelectedCharacterHTMLOutputSheet(null)));
	}

	private void onOutputSheetPDFDefaultButton(final ActionEvent actionEvent)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(LanguageBundle.getString("in_Prefs_outputSheetPDFDefaultTitle"));
		fileChooser.setInitialDirectory(new File(SettingsHandler.getPDFOutputSheetPath()));
		fileChooser.setInitialFileName(SettingsHandler.getSelectedCharacterPDFOutputSheet(null));
		File newTemplate = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));

		if (newTemplate != null)
		{
			if (!newTemplate.getName().startsWith("csheet") && !newTemplate.getName().startsWith("psheet"))
			{
				ShowMessageDelegate.showMessageDialog(
						LanguageBundle.getString("in_Prefs_outputSheetDefaultError"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, MessageType.ERROR
				);
			}
			else
			{
				if (newTemplate.getName().startsWith("csheet"))
				{
					SettingsHandler.setSelectedCharacterPDFOutputSheet(newTemplate.getAbsolutePath(), null);
				}
				else
				{
					//it must be a psheet
					SettingsHandler.setSelectedPartyPDFOutputSheet(newTemplate.getAbsolutePath());
				}
			}
		}

		outputSheetPDFDefault.setText(String.valueOf(SettingsHandler.getSelectedCharacterPDFOutputSheet(null)));
	}

	private void onOutputSheetEqSetButton(final ActionEvent actionEvent)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(LanguageBundle.getString("in_Prefs_templateEqSetTitle"));
		fileChooser.setInitialDirectory(new File(ConfigurationSettings.getOutputSheetsDir()));
		fileChooser.setInitialFileName(SettingsHandler.getSelectedEqSetTemplate());
		File newTemplate = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));

		if (newTemplate != null)
		{
			if (newTemplate.getName().startsWith("eqsheet"))
			{
				SettingsHandler.setSelectedEqSetTemplate(newTemplate.getAbsolutePath());
			}
			else
			{
				ShowMessageDelegate.showMessageDialog(
						LanguageBundle.getString("in_Prefs_templateEqSetError"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, MessageType.ERROR
				);
			}
		}

		outputSheetEqSet.setText(String.valueOf(SettingsHandler.getSelectedEqSetTemplate()));
	}

	private void onOutputSheetSpellsDefaultButton(final ActionEvent actionEvent)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(LanguageBundle.getString("in_Prefs_outputSpellSheetDefault"));
		fileChooser.setInitialDirectory(new File(ConfigurationSettings.getOutputSheetsDir()));
		fileChooser.setInitialFileName(PCGenSettings.getSelectedSpellSheet());
		File newTemplate = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));

		if (newTemplate != null)
		{
			if (newTemplate.getName().startsWith("csheet"))
			{
				PCGenSettings.getInstance().setProperty(
						PCGenSettings.SELECTED_SPELL_SHEET_PATH,
						newTemplate.getAbsolutePath()
				);
			}
			else
			{
				ShowMessageDelegate.showMessageDialog(
						LanguageBundle.getString("in_Prefs_outputSheetDefaultError"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, MessageType.ERROR
				);
			}
		}

		outputSheetSpellsDefault.setText(PCGenSettings.getSelectedSpellSheet());
	}

	private enum ExportChoices
	{

		ASK
				{
					@Override
					public String toString()
					{
						return LanguageBundle.getString("in_Prefs_ask"); //$NON-NLS-1$
					}
				},
		ALWAYS_OPEN
				{
					@Override
					public String toString()
					{
						return LanguageBundle.getString("in_Prefs_alwaysOpen"); //$NON-NLS-1$
					}
				},
		NEVER_OPEN
				{
					@Override
					public String toString()
					{
						return LanguageBundle.getString("in_Prefs_neverOpen"); //$NON-NLS-1$
					}
				};

		public String getValue()
		{

			switch (this)
			{
				case ASK:
					return "";
				case ALWAYS_OPEN:
					return "true";
				case NEVER_OPEN:
					return "false";
				default:
					throw new InternalError();
			}
		}

		public static ExportChoices getChoice(String value)
		{
			Boolean choice = BooleanUtils.toBooleanObject(value);
			if (choice == null)
			{
				return ExportChoices.ASK;
			}
			else if (choice)
			{
				return ExportChoices.ALWAYS_OPEN;
			}
			else
			{
				return ExportChoices.NEVER_OPEN;
			}
		}

	}

}
