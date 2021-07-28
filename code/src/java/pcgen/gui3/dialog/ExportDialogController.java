/*
 * Copyright 2021 (C) Eitan Adler <lists@eitanadler.com>
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

package pcgen.gui3.dialog;

import static pcgen.io.ExportUtilities.HTML_EXPORT_DIR_PROP;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.PartyFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.io.ExportUtilities;
import pcgen.system.BatchExporter;
import pcgen.system.CharacterManager;
import pcgen.system.PCGenSettings;
import pcgen.system.PropertyContext;
import pcgen.util.Logging;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The dialog provides the list of output sheets for a character or party to
 * be exported to.
 */
public class ExportDialogController
{
	@FXML
	private ComboBox<CharacterFacade> selectCharacterBox;
	@FXML
	private CheckBox entireParty;
	@FXML
	private ComboBox<ExportUtilities.SheetFilter> exportSheetType;
	@FXML
	private ListView<URI> templateSelect;
	@FXML
	private ProgressBar progress;
	@FXML
	private Button doClose;
	@FXML
	private Button doExport;

	private final List<File> allTemplates;

	public ExportDialogController()
	{
		allTemplates = ExportUtilities.getAllTemplates();
	}

	/*
	 A lot of this logic is questionable to keep in the GUI later. Instead we should have some kind of "Export
	 Manager" that knows how to actually perform all of the export duties and the GUI just pushes information down.
	 It is split this way since the old gui (gui2) did this.
	 In the meantime get the logic to be somewhat reasonable so future refactoring can do things properly.
	 The logic is especially confusing because "is PDF" is a boolean which needs to be chained all the way down
	 */

	@FXML
	private void doExport(final ActionEvent actionEvent)
	{
		boolean isPDF = exportSheetType.getSelectionModel().getSelectedItem() == ExportUtilities.SheetFilter.PDF;
		URI selectedTemplate = templateSelect.getSelectionModel().getSelectedItem();
		String sheetFilterPath = exportSheetType.getSelectionModel().getSelectedItem().getPath();
		// this is kind of lie but really requires some refactoring to make this all work.
		// it should be handled by a task manager with a 'start' and a 'finished' or 'failed' callback
		progress.setVisible(true);
		if (entireParty.isSelected())
		{
			doExportEntireParty(sheetFilterPath, selectedTemplate, isPDF);
		} else
		{
			doExportSingleCharacter(sheetFilterPath, selectedTemplate, isPDF);
		}
		progress.setVisible(false);
	}

	private static void doExportEntireParty(String sheetFilterPath, URI template, boolean isPDF)
	{
		File path = new File(PCGenSettings.getPcgDir());
		String name = "Entire Party";
		FileChooser fileChooser = new FileChooser();
		String extension = ExportUtilities.getOutputExtension(template.toString(), isPDF);
		FileChooser.ExtensionFilter fileFilter = ExportUtilities.getExtensionFilter(isPDF, extension);
		fileChooser.getExtensionFilters().add(fileFilter);
		fileChooser.setSelectedExtensionFilter(fileFilter);
		fileChooser.setInitialDirectory(path);
		fileChooser.setTitle("Export " + name);
		fileChooser.setInitialDirectory(ExportUtilities.getExportDialogBaseDir(isPDF));
		File outFile = fileChooser.showSaveDialog(null);
		if (outFile == null)
		{
			return;
		}
		PartyFacade party = CharacterManager.getCharacters();
		File templateAsFile = ExportUtilities.templateToAbsoluteTemplate(
				sheetFilterPath,
				template
		);
		if (isPDF)
		{
			BatchExporter.exportPartyToPDF(party, outFile, templateAsFile);
		}
		else
		{
			PropertyContext context = UIPropertyContext.createContext("ExportDialog");
			context.setProperty(HTML_EXPORT_DIR_PROP, outFile.getParent());
			SettingsHandler.setSelectedPartyHTMLOutputSheet(templateAsFile.getAbsolutePath());
			BatchExporter.exportPartyToNonPDF(party, outFile, templateAsFile);
			Globals.executePostExportCommandStandard(outFile.getAbsolutePath());
		}
		if (promptUserToOpenFile(outFile))
		{
			doOpenFile(outFile);
		}
	}

	private static boolean promptUserToOpenFile(File file)
	{
		JCheckBox checkbox = new JCheckBox();
		checkbox.setText("Always perform this action");
		PropertyContext context = UIPropertyContext.getInstance();
		String value = context.getProperty(UIPropertyContext.ALWAYS_OPEN_EXPORT_FILE);
		boolean alwaysOpenFile = Boolean.parseBoolean(value);
		// todo: allow setting this value via the prompt. maybe use RememberingChoiceDialog or similar
		if (alwaysOpenFile)
		{
			return true;
		}
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setContentText("Do you want to open " + file.getName() + '?');
		Optional<ButtonType> result = alert.showAndWait();
		return result.isPresent() && !result.get().equals(ButtonType.NO);
	}

	private static void doOpenFile(File file)
	{
		if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
		{
			Alert error = new Alert(Alert.AlertType.ERROR);
			error.setTitle("Cannot Open " + file.getName());
			error.setContentText("Operating System does not support this operation");
			error.show();
			return;
		}
		try
		{
			Desktop.getDesktop().open(file);
		}
		catch (IOException ex)
		{
			String message = "Failed to open " + file.getName();
			Alert error = new Alert(Alert.AlertType.ERROR);
			error.setTitle("Cannot Open " + file.getName());
			error.setContentText(message);
			error.show();
			Logging.errorPrint(message, ex);
		}
	}

	private void doExportSingleCharacter(String sheetFilterPath, URI template, boolean isPDF)
	{
		File templateAsFile = ExportUtilities.templateToAbsoluteTemplate(
				sheetFilterPath,
				template
		);
		CharacterFacade character = selectCharacterBox.getSelectionModel().getSelectedItem();

		FileChooser fileChooser = new FileChooser();
		File baseDir = ExportUtilities.getExportDialogBaseDir(isPDF);
		fileChooser.setInitialDirectory(baseDir);

		String extension = ExportUtilities.getOutputExtension(template.toString(), isPDF);
		FileChooser.ExtensionFilter fileFilter = ExportUtilities.getExtensionFilter(isPDF, extension);
		fileChooser.getExtensionFilters().add(fileFilter);
		fileChooser.setSelectedExtensionFilter(fileFilter);
		String name;
		File path = character.getFileRef().get();
		if (path != null)
		{
			path = path.getParentFile();
		}
		else
		{
			path = new File(PCGenSettings.getPcgDir());
		}
		// this should be on an "exportable" and have something like getExportedName
		name = character.getTabNameRef().get();
		if (StringUtils.isEmpty(name))
		{
			name = character.getNameRef().get();
		}
		fileChooser.setInitialDirectory(path);
		fileChooser.setTitle("Export " + name);
		File outFile = fileChooser.showSaveDialog(null);
		if (outFile == null)
		{
			return;
		}

		if (isPDF)
		{
			boolean result;
			result = BatchExporter.exportCharacterToPDF(character, outFile, templateAsFile);
			if (!result)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(Constants.APPLICATION_NAME);
				alert.setContentText("The character export failed. Please see the log for details.");
				alert.show();
				return;
			}
			Globals.executePostExportCommandPDF(outFile.getAbsolutePath());
		}
		else
		{
			boolean result = BatchExporter.exportCharacterToNonPDF(character, outFile, templateAsFile);
			if (!result)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(Constants.APPLICATION_NAME);
				alert.setContentText("The character export failed. Please see the log for details.");
				alert.show();
				return;
			}
			Globals.executePostExportCommandStandard(outFile.getAbsolutePath());
		}
		if (promptUserToOpenFile(outFile))
		{
			doOpenFile(outFile);
		}
	}




	private class PartyCheckboxChangeListener implements ChangeListener<Boolean>
	{
		@Override
		public void changed(final ObservableValue<? extends Boolean> observable,
		                    final Boolean oldValue,
		                    final Boolean newValue)
		{
			templateSelect.getSelectionModel().clearSelection();
			if (newValue == true)
			{
				selectCharacterBox.setDisable(true);
			}
			else
			{
				selectCharacterBox.setDisable(false);
			}
			refreshFiles(exportSheetType.getSelectionModel().getSelectedItem(), entireParty.isSelected());
		}
	}

	private class TemplateSelectionListener implements ChangeListener<URI>
	{
		@Override
		public void changed(final ObservableValue<? extends URI> observable,
		                    final URI oldValue,
		                    final URI newValue)
		{
			if (newValue != null)
			{
				doExport.setDisable(false);
			} else
			{
				doExport.setDisable(true);
			}
		}
	}

	private void refreshFiles(ExportUtilities.SheetFilter sheetFilter, boolean exportParty)
	{
		URI[] validFiles = ExportUtilities.getValidFiles(allTemplates, sheetFilter, exportParty);
		List<URI> validFilesAsList = Arrays.stream(validFiles).collect(Collectors.toList());
		ObservableList<URI> observableTemplates = FXCollections.observableList(validFilesAsList);
		templateSelect.itemsProperty().setValue(observableTemplates);
	}

	private class ExportSheetTypeSelectionListener implements ChangeListener<ExportUtilities.SheetFilter>
	{
		@Override
		public void changed(final ObservableValue<? extends ExportUtilities.SheetFilter> observable,
		                    final ExportUtilities.SheetFilter oldValue,
		                    final ExportUtilities.SheetFilter newValue)
		{
			PropertyContext context = UIPropertyContext.createContext("ExportDialog");
			context.setProperty(UIPropertyContext.DEFAULT_OS_TYPE,
				exportSheetType.getSelectionModel().getSelectedItem().toString());
			refreshFiles(newValue, entireParty.isSelected());
		}
	}

	@FXML
	private void doOnClose(final ActionEvent actionEvent)
	{
		var button = (Button)actionEvent.getSource();
		button.getScene().getWindow().hide();
	}

	@FXML
	void initialize()
	{
		PartyFacade characters = CharacterManager.getCharacters();
		ObservableList<CharacterFacade> observableList =
				FXCollections.observableList(IteratorUtils.toList(characters.iterator()));
		selectCharacterBox.setItems(observableList);
		// todo: maybe select "current" character pcgenFrame.getSelectedCharacterRef() ???
		selectCharacterBox.getSelectionModel().select(0);
		entireParty.selectedProperty().addListener(new PartyCheckboxChangeListener());
		templateSelect.getSelectionModel().selectedItemProperty().addListener(new TemplateSelectionListener());
		List<ExportUtilities.SheetFilter> sheetFilterValues = Arrays.stream(
				ExportUtilities.SheetFilter.values()).collect(Collectors.toList());
		exportSheetType.setItems(FXCollections.observableList(sheetFilterValues));
		exportSheetType.getSelectionModel().selectedItemProperty().addListener(new ExportSheetTypeSelectionListener());
		exportSheetType.getSelectionModel().select(0);
		PropertyContext context = UIPropertyContext.createContext("ExportDialog");
		String defaultOSType = context.getProperty(UIPropertyContext.DEFAULT_OS_TYPE);
		if (defaultOSType != null)
		{
			Arrays.stream(ExportUtilities.SheetFilter.values())
			      .filter(filter -> defaultOSType.equals(filter.toString()))
			      .findFirst()
			      .ifPresent(filter -> exportSheetType.getSelectionModel().select(filter));
		}
	}
}
