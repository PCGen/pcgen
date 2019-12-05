/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
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
package pcgen.gui3.component;

import javax.swing.SwingUtilities;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.PCGenUIManager;
import pcgen.gui2.dialog.ExportDialog;
import pcgen.gui2.dialog.PrintPreviewDialog;
import pcgen.gui2.tools.Icons;
import pcgen.gui3.behavior.EnabledOnlyWithCharacter;
import pcgen.gui3.behavior.EnabledOnlyWithSources;
import pcgen.system.LanguageBundle;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;

/**
 * The toolbar that is displayed in PCGen's main window. Provides shortcuts to
 * common PCGen activities.
 *
 * @see pcgen.gui2.PCGenFrame
 */
public final class PCGenToolBar
{
    private final PCGenFrame rootFrame;

    public PCGenToolBar(final PCGenFrame rootFrame)
    {
        this.rootFrame = rootFrame;
    }

    public ToolBar buildMenu()
    {
        ToolBar toolBar = new ToolBar();

        Button newButton = new Button();
        newButton.setOnAction(this::onNew);
        newButton.setGraphic(new ImageView(Icons.New16.asJavaFX()));
        newButton.setText(LanguageBundle.getString("in_mnuFileNew"));
        newButton.setDisable(true);
        ReferenceFacade<DataSetFacade> loadedDataSetRef = rootFrame.getLoadedDataSetRef();
        loadedDataSetRef.addReferenceListener(new EnabledOnlyWithSources(newButton, rootFrame));
        toolBar.getItems().add(newButton);

        Button openButton = new Button();
        openButton.setOnAction(this::onOpen);
        openButton.setGraphic(new ImageView(Icons.Open16.asJavaFX()));
        openButton.setText(LanguageBundle.getString("in_mnuFileOpen"));
        toolBar.getItems().add(openButton);

        Button closeButton = new Button();
        closeButton.setOnAction(this::onClose);
        closeButton.setGraphic(new ImageView(Icons.Close16.asJavaFX()));
        closeButton.setText(LanguageBundle.getString("in_mnuFileClose"));
        toolBar.getItems().add(closeButton);
        ReferenceFacade<CharacterFacade> ref = rootFrame.getSelectedCharacterRef();
        ref.addReferenceListener(new EnabledOnlyWithCharacter(closeButton, rootFrame));

        Button saveButton = new Button();
        saveButton.setOnAction(this::onSave);
        saveButton.setGraphic(new ImageView(Icons.Save16.asJavaFX()));
        saveButton.setText(LanguageBundle.getString("in_mnuFileSave"));
        toolBar.getItems().add(saveButton);
        ref.addReferenceListener(new EnabledOnlyWithCharacter(saveButton, rootFrame));

        Button printButton = new Button();
        printButton.setOnAction(this::onPrint);
        printButton.setGraphic(new ImageView(Icons.Print16.asJavaFX()));
        printButton.setText(LanguageBundle.getString("in_mnuFilePrint"));
        toolBar.getItems().add(printButton);
        ref.addReferenceListener(new EnabledOnlyWithCharacter(printButton, rootFrame));

        Button exportButton = new Button();
        exportButton.setOnAction(this::onExport);
        exportButton.setGraphic(new ImageView(Icons.Export16.asJavaFX()));
        exportButton.setText(LanguageBundle.getString("in_mnuFileExport"));
        toolBar.getItems().add(exportButton);
        ref.addReferenceListener(new EnabledOnlyWithCharacter(exportButton, rootFrame));

        Button preferencesButton = new Button();
        preferencesButton.setOnAction(this::onPreferences);
        preferencesButton.setGraphic(new ImageView(Icons.Preferences16.asJavaFX()));
        preferencesButton.setText(LanguageBundle.getString("in_mnuToolsPreferences"));
        toolBar.getItems().add(preferencesButton);

        return toolBar;
    }

    private void onNew(final ActionEvent actionEvent)
    {
        SwingUtilities.invokeLater(() -> rootFrame.createNewCharacter(null));
    }

    private void onOpen(final ActionEvent actionEvent)
    {
        SwingUtilities.invokeLater(rootFrame::showOpenCharacterChooser);
    }

    private void onClose(final ActionEvent actionEvent)
    {
        SwingUtilities.invokeLater(() -> rootFrame.closeCharacter(rootFrame.getSelectedCharacterRef().get()));
    }

    private void onSave(final ActionEvent actionEvent)
    {
        SwingUtilities.invokeLater(() -> rootFrame.saveCharacter(rootFrame.getSelectedCharacterRef().get()));
    }

    private void onPrint(final ActionEvent actionEvent)
    {
        SwingUtilities.invokeLater(() -> PrintPreviewDialog.showPrintPreviewDialog(rootFrame));
    }

    private void onExport(final ActionEvent actionEvent)
    {
        SwingUtilities.invokeLater(() -> ExportDialog.showExportDialog(rootFrame));
    }

    private void onPreferences(final ActionEvent actionEvent)
    {
        SwingUtilities.invokeLater(PCGenUIManager::displayPreferencesDialog);
    }
}
