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

import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui3.PanelFromResource;
import pcgen.system.BatchExporter;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;
import pcgen.util.fop.FopTask;

import javafx.application.Platform;
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
import javafx.stage.Screen;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.render.awt.AWTRenderer;

/**
 * JavaFX print-preview dialog: renders each character-sheet page to an image and lets the user
 * pick a page size defaulting from preferences/locale.
 */
public class PrintPreviewController
{
	/** Opens the print-preview dialog as a non-modal stage. Shared by the menu action and the toolbar. */
	public static void showDialog()
	{
		PanelFromResource<PrintPreviewController> panel =
				new PanelFromResource<>(PrintPreviewController.class, "PrintPreview.fxml");
		panel.showAsStage(LanguageBundle.getString("in_mnuFilePrintPreview"));
	}

	@FXML
	private ComboBox<URI> sheetBox;
	@FXML
	private ComboBox<String> paperBox;
	@FXML
	private ComboBox<String> pageBox;
	@FXML
	private Button printButton;
	@FXML
	private Button refreshButton;
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
	private URI currentTemplate;
	private static final double BASE_DPI = 72.0;

	@FXML
	void initialize()
	{
		PCGenFrame rootFrame = (PCGenFrame) Globals.getRootFrame();
		this.character = rootFrame.getSelectedCharacterRef().get();

		populatePaperBox();
		populateSheetBox();
		// Fit each rendered page to the visible width of the scroll pane; long pages scroll vertically.
		previewImage.fitWidthProperty().bind(previewScroll.viewportBoundsProperty().map(bounds -> bounds.getWidth()));
		pageBox.getSelectionModel().selectedIndexProperty().subscribe(index -> {
			if (index.intValue() >= 0)
			{
				showPage(index.intValue());
			}
		});
		setEditGroupEnabled(false);
	}

	private void populatePaperBox()
	{
		List<PrintPreviewPaperDefault.PaperOption> options = IntStream.range(0, Globals.getPaperCount())
				.mapToObj(i -> new PrintPreviewPaperDefault.PaperOption(
						Globals.getPaperInfo(i, PaperInfo.NAME),
						PrintPreviewPaperDefault.parseDimensionToPoints(Globals.getPaperInfo(i, PaperInfo.WIDTH)),
						PrintPreviewPaperDefault.parseDimensionToPoints(Globals.getPaperInfo(i, PaperInfo.HEIGHT))))
				.toList();
		List<String> paperNames = options.stream().map(PrintPreviewPaperDefault.PaperOption::name).toList();
		paperBox.setItems(FXCollections.observableArrayList(paperNames));

		paperBox.getSelectionModel().selectedItemProperty().subscribe(paper -> {
			if (paper != null)
			{
				Globals.selectPaper(paper);
			}
		});
		String persisted = PCGenSettings.getInstance().getProperty(PCGenSettings.PAPERSIZE);
		String chosen = PrintPreviewPaperDefault.chooseDefaultForCurrentLocaleAndPrinter(persisted, paperNames, options);
		if (chosen != null)
		{
			paperBox.getSelectionModel().select(chosen);
		}
	}

	private void populateSheetBox()
	{
		Path dir = Path.of(ConfigurationSettings.getOutputSheetsDir());
		URI osPath = outputSheetsUri();
		try (Stream<Path> walk = Files.walk(dir))
		{
			List<URI> templates = walk.filter(Files::isRegularFile)
			                          .filter(PrintPreviewController::isCharacterTemplate)
			                          .map(p -> osPath.relativize(p.toUri()))
			                          .sorted(Comparator.comparing(URI::toString))
			                          .toList();
			sheetBox.setItems(FXCollections.observableArrayList(templates));
		}
		catch (final IOException ex)
		{
			Logging.errorPrint("could not walk output sheets directory " + dir, ex);
		}
		sheetBox.getSelectionModel().selectedItemProperty().subscribe(template -> {
			if (template != null)
			{
				loadPreview(template);
			}
		});
	}

	/** True for a printable character sheet: a non-{@code .fo} file named with the template prefix, directly under a {@code pdf} dir. */
	static boolean isCharacterTemplate(Path path)
	{
		Path parent = path.getParent();
		Path fileName = path.getFileName();
		if (parent == null || fileName == null || parent.getFileName() == null)
		{
			return false;
		}
		String name = fileName.toString();
		return parent.getFileName().toString().equalsIgnoreCase("pdf")
				&& !name.endsWith(".fo")
				&& name.startsWith(Constants.CHARACTER_TEMPLATE_PREFIX);
	}

	private void setEditGroupEnabled(boolean enable)
	{
		pageBox.setDisable(!enable);
		printButton.setDisable(!enable);
		refreshButton.setDisable(!enable);
	}

	private static URI outputSheetsUri()
	{
		return Path.of(ConfigurationSettings.getOutputSheetsDir()).toUri();
	}

	/** Device pixel scale to render at (2.0 on Retina, else 1.0): dialog window, else primary screen. Call on the FX thread. */
	private double currentOutputScale()
	{
		if (previewScroll.getScene() != null && previewScroll.getScene().getWindow() != null)
		{
			return previewScroll.getScene().getWindow().getOutputScaleX();
		}
		return Screen.getPrimary().getOutputScaleX();
	}

	private void loadPreview(URI template)
	{
		currentTemplate = template;
		progress.setVisible(true);
		sheetBox.setDisable(true);
		setEditGroupEnabled(false);

		// Render at the display scale (read on the FX thread) so fitting the page downscales a
		// high-res bitmap rather than upscaling 72 DPI (which looks blurry).
		final double renderScale = currentOutputScale();

		Task<AWTRenderer> task = new Task<>()
		{
			@Override
			protected AWTRenderer call() throws Exception
			{
				URI osPath = outputSheetsUri();
				// FopTask requires a java.io.File, so resolve via NIO and convert only at that boundary.
				File xsltFile = Path.of(osPath.resolve(template)).toFile();
				FOUserAgent userAgent = FopTask.getFactory().newFOUserAgent();
				userAgent.setTargetResolution((float) (BASE_DPI * renderScale));
				AWTRenderer awtRenderer = new AWTRenderer(userAgent, null, false, false);
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				BatchExporter.exportCharacter(character, buffer);
				FopTask.newFopTask(new ByteArrayInputStream(buffer.toByteArray()), xsltFile, awtRenderer).run();
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
			setEditGroupEnabled(renderer != null);
			Logging.errorPrint("Could not load sheet", task.getException());
		});
		Thread worker = new Thread(task, "fop-preview-task");
		worker.setDaemon(true);
		worker.start();
	}

	private void showPage(int pageIndex)
	{
		// A Refresh can leave a stale selected index that is now past the (possibly shorter) page count.
		if (renderer == null || pageIndex < 0 || pageIndex >= renderer.getNumberOfPages())
		{
			return;
		}
		try
		{
			BufferedImage bufferedImage = renderer.getPageImage(pageIndex);
			Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
			previewImage.setImage(fxImage);
		}
		catch (final FOPException ex)
		{
			Logging.errorPrint("Could not render preview page " + pageIndex, ex);
		}
	}

	@FXML
	private void onPrint(final ActionEvent actionEvent)
	{
		if (renderer == null)
		{
			return;
		}
		// printDialog()/print() block (and deadlock the UI on macOS) if run on the FX thread, so run
		// them on a background thread. Disable the button meanwhile to prevent a second print dialog.
		printButton.setDisable(true);
		final AWTRenderer pageable = renderer;
		final String characterName = character.getNameRef().get();
		Thread printThread = new Thread(() -> {
			PrintOutcome outcome = doPrint(pageable, characterName);
			Platform.runLater(() -> finishPrint(outcome));
		}, "print-preview-print");
		printThread.setDaemon(true);
		printThread.start();
	}

	/** The result of a print attempt: whether the job printed, and any user-facing error message. */
	private record PrintOutcome(boolean printed, String errorMessage) { }

	/** Runs the blocking AWT print off the FX thread and reports the outcome (no UI work here). */
	private static PrintOutcome doPrint(AWTRenderer pageable, String characterName)
	{
		try
		{
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			printerJob.setPageable(pageable);
			if (!printerJob.printDialog())
			{
				return new PrintOutcome(false, null); // user cancelled the print dialog
			}
			printerJob.print();
			return new PrintOutcome(true, null);
		}
		catch (final PrinterException | RuntimeException ex)
		{
			String message = LanguageBundle.getFormattedString("in_printPreview_printError", characterName);
			Logging.errorPrint(message, ex);
			return new PrintOutcome(false, message);
		}
	}

	/** On the FX thread: close the dialog on success, else re-enable the button and alert on a real failure. */
	private void finishPrint(PrintOutcome outcome)
	{
		if (outcome.printed())
		{
			cancelButton.getScene().getWindow().hide();
			return;
		}
		printButton.setDisable(false);
		if (outcome.errorMessage() != null)
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(Constants.APPLICATION_NAME);
			alert.setContentText(outcome.errorMessage());
			alert.show();
		}
	}

	@FXML
	private void onRefresh(final ActionEvent actionEvent)
	{
		if (currentTemplate != null)
		{
			loadPreview(currentTemplate);
		}
	}

	@FXML
	private void onCancel(final ActionEvent actionEvent)
	{
		cancelButton.getScene().getWindow().hide();
	}
}
