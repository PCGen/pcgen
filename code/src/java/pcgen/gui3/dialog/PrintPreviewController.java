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

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.system.BatchExporter;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;
import pcgen.util.fop.FopTask;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.render.awt.AWTRenderer;

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
	private AWTRenderer renderer;
	private double zoom = 0.75;
	private static final double ZOOM_MULTIPLIER = Math.pow(2, 0.125);

	@FXML
	void initialize()
	{
		PCGenFrame rootFrame = (PCGenFrame) Globals.getRootFrame();
		this.character = rootFrame.getSelectedCharacterRef().get();

		populatePaperBox();
		populateSheetBox();
		zoomBox.setItems(FXCollections.observableArrayList(0.25, 0.50, 0.75, 1.00));
		zoomBox.getSelectionModel().select(Double.valueOf(0.75));
		zoomBox.getSelectionModel().selectedItemProperty().addListener((obs, old, now) -> {
			if (now != null)
			{
				zoom = now;
				applyZoom();
			}
		});
		pageBox.getSelectionModel().selectedIndexProperty().addListener((obs, old, now) -> {
			if (now.intValue() >= 0)
			{
				showPage(now.intValue());
			}
		});
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

	private void loadPreview(URI template)
	{
		progress.setVisible(true);
		sheetBox.setDisable(true);
		setEditGroupEnabled(false);

		Task<AWTRenderer> task = new Task<>()
		{
			@Override
			protected AWTRenderer call() throws Exception
			{
				URI osPath = new File(ConfigurationSettings.getOutputSheetsDir()).toURI();
				File xsltFile = new File(osPath.resolve(template));
				FOUserAgent userAgent = FopTask.getFactory().newFOUserAgent();
				AWTRenderer awtRenderer = new AWTRenderer(userAgent, null, false, false);
				try (PipedOutputStream out = new PipedOutputStream())
				{
					FopTask fopTask = FopTask.newFopTask(new PipedInputStream(out), xsltFile, awtRenderer);
					Thread thread = new Thread(fopTask, "fop-preview");
					thread.setDaemon(true);
					thread.start();
					BatchExporter.exportCharacter(character, out);
					try
					{
						thread.join();
					}
					catch (final InterruptedException ex)
					{
						thread.interrupt();
					}
				}
				return awtRenderer;
			}
		};
		task.setOnSucceeded(evt -> {
			renderer = task.getValue();
			progress.setVisible(false);
			sheetBox.setDisable(false);
			setEditGroupEnabled(true);
			int pages = renderer.getNumberOfPages();
			String[] labels = new String[pages];
			Arrays.setAll(labels, i -> (i + 1) + " of " + pages);
			pageBox.setItems(FXCollections.observableArrayList(labels));
			if (pages > 0)
			{
				pageBox.getSelectionModel().select(0);
			}
		});
		task.setOnFailed(evt -> {
			progress.setVisible(false);
			sheetBox.setDisable(false);
			Logging.errorPrint("Could not load sheet", task.getException());
		});
		Thread worker = new Thread(task, "fop-preview-task");
		worker.setDaemon(true);
		worker.start();
	}

	private void showPage(int pageIndex)
	{
		if (renderer == null)
		{
			return;
		}
		try
		{
			java.awt.image.BufferedImage bufferedImage = renderer.getPageImage(pageIndex);
			Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
			previewImage.setImage(fxImage);
			applyZoom();
		}
		catch (final org.apache.fop.apps.FOPException ex)
		{
			Logging.errorPrint("Could not render preview page " + pageIndex, ex);
		}
	}

	private void applyZoom()
	{
		Image image = previewImage.getImage();
		if (image != null)
		{
			previewImage.setFitWidth(image.getWidth() * zoom);
		}
	}

	@FXML
	private void onZoomIn(final ActionEvent actionEvent)
	{
		zoom *= ZOOM_MULTIPLIER;
		zoomBox.getSelectionModel().clearSelection();
		zoomBox.setValue(zoom);
		applyZoom();
	}

	@FXML
	private void onZoomOut(final ActionEvent actionEvent)
	{
		zoom /= ZOOM_MULTIPLIER;
		zoomBox.getSelectionModel().clearSelection();
		zoomBox.setValue(zoom);
		applyZoom();
	}

	@FXML
	private void onPrint(final ActionEvent actionEvent)
	{
		if (renderer == null)
		{
			return;
		}
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		printerJob.setPageable(renderer);
		if (printerJob.printDialog())
		{
			try
			{
				printerJob.print();
				cancelButton.getScene().getWindow().hide();
			}
			catch (final PrinterException ex)
			{
				String message = "Could not print " + character.getNameRef().get();
				Logging.errorPrint(message, ex);
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(Constants.APPLICATION_NAME);
				alert.setContentText(message);
				alert.show();
			}
		}
	}

	@FXML
	private void onCancel(final ActionEvent actionEvent)
	{
		cancelButton.getScene().getWindow().hide();
	}
}
