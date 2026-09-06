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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui3.dialog;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;

/**
 * JavaFX print-preview dialog (CODE-2537): renders each character-sheet page to
 * an image and lets the user pick a page size defaulting from preferences/locale.
 */
public class PrintPreviewController
{
	@FXML
	private ComboBox<URI> sheetBox;
	@FXML
	private ComboBox<String> paperBox;
	@FXML
	private ComboBox<String> pageBox;
	@FXML
	private ComboBox<Double> zoomBox;
	@FXML
	private Button zoomInButton;
	@FXML
	private Button zoomOutButton;
	@FXML
	private Button printButton;
	@FXML
	private Button cancelButton;
	@FXML
	private ScrollPane previewScroll;
	@FXML
	private ImageView previewImage;
	@FXML
	private ProgressBar progress;

	private CharacterFacade character;

	@FXML
	void initialize()
	{
		PCGenFrame rootFrame = (PCGenFrame) Globals.getRootFrame();
		this.character = rootFrame.getSelectedCharacterRef().get();

		populatePaperBox();
		populateSheetBox();
		setEditGroupEnabled(false);
	}

	private void populatePaperBox()
	{
		List<String> paperNames = IntStream.range(0, Globals.getPaperCount())
		                                   .mapToObj(i -> Globals.getPaperInfo(i, PaperInfo.NAME))
		                                   .toList();
		paperBox.setItems(FXCollections.observableArrayList(paperNames));

		paperBox.getSelectionModel().selectedItemProperty().addListener((obs, old, now) -> {
			if (now != null)
			{
				Globals.selectPaper(now);
			}
		});
		String persisted = PCGenSettings.getInstance().getProperty(PCGenSettings.PAPERSIZE);
		String chosen = PrintPreviewPaperDefault.chooseDefaultForCurrentLocale(persisted, paperNames);
		if (chosen != null)
		{
			paperBox.getSelectionModel().select(chosen);
		}
	}

	private void populateSheetBox()
	{
		Path dir = Path.of(ConfigurationSettings.getOutputSheetsDir());
		URI osPath = dir.toUri();
		Predicate<Path> filter = p -> p.getParent().getFileName().toString().equalsIgnoreCase("pdf")
				&& !p.getFileName().toString().endsWith(".fo")
				&& p.getFileName().toString().startsWith(Constants.CHARACTER_TEMPLATE_PREFIX);
		try (Stream<Path> walk = Files.walk(dir))
		{
			List<URI> templates = walk.filter(Files::isRegularFile)
			                          .filter(filter)
			                          .map(p -> osPath.relativize(p.toUri()))
			                          .toList();
			sheetBox.setItems(FXCollections.observableArrayList(templates));
		}
		catch (final IOException ex)
		{
			Logging.errorPrint("could not walk output sheets directory " + dir, ex);
		}
		sheetBox.getSelectionModel().selectedItemProperty().addListener((obs, old, now) -> {
			if (now != null)
			{
				loadPreview(now);
			}
		});
	}

	private void setEditGroupEnabled(boolean enable)
	{
		pageBox.setDisable(!enable);
		zoomBox.setDisable(!enable);
		zoomInButton.setDisable(!enable);
		zoomOutButton.setDisable(!enable);
		printButton.setDisable(!enable);
	}

	// Rendering, zoom and print are added in Task 4.
	private void loadPreview(URI template)
	{
		// Implemented in Task 4.
	}

	@FXML
	private void onZoomIn(final ActionEvent actionEvent)
	{
		// Implemented in Task 4.
	}

	@FXML
	private void onZoomOut(final ActionEvent actionEvent)
	{
		// Implemented in Task 4.
	}

	@FXML
	private void onPrint(final ActionEvent actionEvent)
	{
		// Implemented in Task 4.
	}

	@FXML
	private void onCancel(final ActionEvent actionEvent)
	{
		cancelButton.getScene().getWindow().hide();
	}
}
