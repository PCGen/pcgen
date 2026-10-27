/*
 * Copyright 2026 the PCGen Project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.gui3.install;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import pcgen.cdom.enumeration.Destination;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.InstallableCampaign;
import pcgen.core.utils.CoreUtility;
import pcgen.gui3.sources.SourceInfoRenderer;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.install.DataPackageInstaller;
import pcgen.persistence.install.DataPackageInstaller.DataPackage;
import pcgen.persistence.install.DataPackageInstaller.InstallEntry;
import pcgen.persistence.install.DataPackageInstaller.Plan;
import pcgen.persistence.lst.InstallLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

/**
 * Installs a selected PCZ or ZIP package using background tasks and FX-owned prompts.
 * The dialog stays open after installation and cannot close while a task is active.
 */
public final class DataInstallerDialog extends Dialog<Void>
{
	/** The user's choice for an entire group of nonstandard or existing files. */
	enum Decision
	{
		INSTALL, SKIP, CANCEL
	}

	private record LoadedPackage(DataPackage data, InstallableCampaign campaign)
	{
	}

	private final Map<Destination, Path> destinations;
	private final Path sheetDirectory;
	private final Path applicationDirectory;
	private final Executor executor;
	private final BooleanProperty busy = new SimpleBooleanProperty();
	private final ObjectProperty<LoadedPackage> selection = new SimpleObjectProperty<>();
	private Map<Destination, RadioButton> locationButtons;
	private SourceInfoRenderer preview;
	private File currentFolder;
	private Supplier<File> chooser = this::chooseArchive;
	private BiFunction<String, List<Path>, Decision> confirmation = this::confirm;
	private BiConsumer<AlertType, String> notifier = this::showMessage;

	@FXML private TextField archivePath;
	@FXML private Button choose;
	@FXML private Button install;
	@FXML private ToggleGroup locations;
	@FXML private RadioButton dataLocation;
	@FXML private RadioButton vendorLocation;
	@FXML private RadioButton homebrewLocation;
	@FXML private WebView information;
	@FXML private ProgressBar progress;
	@FXML private Label status;

	/** Creates an installer using the application's configured destination directories. */
	public DataInstallerDialog()
	{
		this(Map.of(
				Destination.DATA, Path.of(ConfigurationSettings.getPccFilesDir()),
				Destination.VENDORDATA, Path.of(PCGenSettings.getVendorDataDir()),
				Destination.HOMEBREWDATA, Path.of(PCGenSettings.getHomebrewDataDir())),
				Path.of(ConfigurationSettings.getOutputSheetsDir()),
				Path.of(ConfigurationSettings.getInstallRoot()),
				task -> Thread.ofVirtual().name("data-installer").start(task));
	}

	/** Supplies isolated destinations and an executor for the installation workflow. */
	DataInstallerDialog(Map<Destination, Path> destinations, Path sheetDirectory,
			Path applicationDirectory, Executor executor)
	{
		this.destinations = destinations;
		this.sheetDirectory = sheetDirectory;
		this.applicationDirectory = applicationDirectory;
		this.executor = executor;
		setTitle(LanguageBundle.getString("in_dataInstaller"));
		var loader = new FXMLLoader(getClass().getResource("DataInstallerDialog.fxml"), LanguageBundle.getBundle());
		loader.setRoot(getDialogPane());
		loader.setController(this);
		try
		{
			loader.load();
		}
		catch (IOException exception)
		{
			throw new UncheckedIOException(exception);
		}
		setResizable(true);
		getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		getDialogPane().lookupButton(ButtonType.CLOSE).disableProperty().bind(busy);
		setOnCloseRequest(event -> {
			if (busy.get())
			{
				event.consume();
			}
		});
		setOnHidden(_ -> preview.close());
	}

	/** Binds installation eligibility to the package, location, and task state. */
	@FXML
	private void initialize()
	{
		locationButtons = Map.of(Destination.DATA, dataLocation, Destination.VENDORDATA, vendorLocation,
				Destination.HOMEBREWDATA, homebrewLocation);
		preview = new SourceInfoRenderer(information.getEngine());
		choose.disableProperty().bind(busy);
		locationButtons.values().forEach(button -> button.disableProperty().bind(busy));
		install.disableProperty().bind(busy.or(selection.isNull()).or(locations.selectedToggleProperty().isNull()));
		progress.visibleProperty().bind(busy);
		progress.managedProperty().bind(busy);
	}

	/** Leaves a canceled chooser alone; a new choice invalidates the previous package immediately. */
	@FXML
	private void onChoose()
	{
		File archive = chooser.get();
		if (archive == null)
		{
			return;
		}
		currentFolder = archive.getParentFile();
		selection.set(null);
		archivePath.clear();
		locations.selectToggle(null);
		preview.clear();
		var task = new Task<LoadedPackage>()
		{
			@Override
			protected LoadedPackage call() throws IOException, PersistenceLayerException
			{
				var data = DataPackageInstaller.read(archive.toPath());
				var loader = new InstallLoader();
				loader.loadLstString(null, archive.toURI(), data.installInfo());
				var campaign = loader.getCampaign();
				if (campaign.getDisplayName().isBlank())
				{
					throw new IOException("Missing CAMPAIGN in " + archive);
				}
				return new LoadedPackage(data, campaign);
			}
		};
		runTask(task, "in_diReading", this::showPackage, failure -> {
			Logging.errorPrint("Failed to read data package " + archive, failure);
			if (failure instanceof DataPackageInstaller.MissingInstallFileException)
			{
				notifier.accept(AlertType.WARNING,
						LanguageBundle.getFormattedString("in_diNoInstallFile", archive.getName()));
			}
			else
			{
				notifier.accept(AlertType.ERROR, LanguageBundle.getFormattedString("in_diBadDataSet", archive));
			}
		});
	}

	/** Applies a compatible package and its metadata together, never mixing successive choices. */
	private void showPackage(LoadedPackage loaded)
	{
		var problem = versionProblem(loaded.campaign());
		if (problem.isPresent())
		{
			Logging.errorPrint(problem.get());
			notifier.accept(AlertType.WARNING, problem.get());
			return;
		}
		selection.set(loaded);
		archivePath.setText(loaded.data().archive().toString());
		Destination destination = loaded.campaign().get(ObjectKey.DESTINATION);
		locations.selectToggle(destination == null ? null : locationButtons.get(destination));
		preview.renderCampaign(loaded.campaign());
	}

	/** Preserves the manifest's production and current-development-series version rules. */
	private static Optional<String> versionProblem(InstallableCampaign campaign)
	{
		String development = campaign.getSafe(StringKey.MINDEVVER);
		String production = campaign.getSafe(StringKey.MINVER);
		if (!development.isEmpty() && !CoreUtility.isPriorToCurrent(development)
				&& CoreUtility.isCurrMinorVer(development))
		{
			return Optional.of(LanguageBundle.getFormattedString("in_diVersionTooOldDev", development, production));
		}
		if (!production.isEmpty() && !CoreUtility.isPriorToCurrent(production))
		{
			return Optional.of(LanguageBundle.getFormattedString("in_diVersionTooOld", production));
		}
		return Optional.empty();
	}

	/** Plans paths on the worker before asking about nonstandard entries and overwrites. */
	@FXML
	private void onInstall()
	{
		var loaded = selection.get();
		Destination destination = locationButtons.entrySet().stream()
				.filter(entry -> entry.getValue().isSelected()).map(Map.Entry::getKey).findFirst().orElseThrow();
		var task = new Task<Plan>()
		{
			@Override
			protected Plan call() throws IOException
			{
				return DataPackageInstaller.plan(loaded.data(), destinations.get(destination),
						sheetDirectory, applicationDirectory);
			}
		};
		runTask(task, "in_diPreparing", plan -> confirmAndInstall(plan, loaded.campaign().getDisplayName()),
				this::installationFailed);
	}

	/** Skipping a group keeps the remaining entries; canceling never starts extraction. */
	private void confirmAndInstall(Plan plan, String name)
	{
		busy.set(true);
		var confirmed = confirmEntries(plan, plan.nonstandard(), "in_diNonStandardFilesPlain")
				.flatMap(remaining -> confirmEntries(remaining, remaining.overwrites(), "in_diOverwriteFilesPlain"));
		if (confirmed.isEmpty())
		{
			busy.set(false);
			return;
		}
		Plan approved = confirmed.get();
		if (approved.entries().stream().noneMatch(entry -> !entry.directory()))
		{
			busy.set(false);
			notifier.accept(AlertType.INFORMATION, LanguageBundle.getString("in_diNothingToInstall"));
			return;
		}
		var task = new Task<Void>()
		{
			@Override
			protected Void call() throws IOException
			{
				DataPackageInstaller.install(approved, count -> updateProgress(count, approved.entries().size()));
				return null;
			}
		};
		runTask(task, "in_diInstalling",
				_ -> notifier.accept(AlertType.INFORMATION, LanguageBundle.getFormattedString("in_diInstalled", name)),
				this::installationFailed);
	}

	/** Returns the remaining plan, or empty when the user cancels this group. */
	private Optional<Plan> confirmEntries(Plan plan, List<InstallEntry> entries, String messageKey)
	{
		if (entries.isEmpty())
		{
			return Optional.of(plan);
		}
		var decision = confirmation.apply(LanguageBundle.getString(messageKey),
				entries.stream().map(InstallEntry::target).toList());
		return switch (decision)
		{
			case INSTALL -> Optional.of(plan);
			case SKIP -> Optional.of(plan.without(entries));
			case CANCEL -> Optional.empty();
		};
	}

	/** Runs I/O on the executor; releases busy state before a callback can start the next task. */
	private <T> void runTask(Task<T> task, String messageKey, Consumer<T> success, Consumer<Throwable> failure)
	{
		busy.set(true);
		status.setText(LanguageBundle.getString(messageKey));
		progress.progressProperty().bind(task.progressProperty());
		task.setOnSucceeded(_ -> {
			finishTask();
			success.accept(task.getValue());
		});
		task.setOnFailed(_ -> {
			finishTask();
			failure.accept(task.getException());
		});
		executor.execute(task);
	}

	/** Releases bindings before callbacks, including callbacks that open nested prompts. */
	private void finishTask()
	{
		progress.progressProperty().unbind();
		status.setText("");
		busy.set(false);
	}

	/** Reports the actual failure, distinguishing a missing configured data folder. */
	private void installationFailed(Throwable failure)
	{
		Logging.errorPrint("Data package installation failed", failure);
		String message = failure instanceof NotDirectoryException exception
				? LanguageBundle.getFormattedString("in_diDataFolderNotExist", exception.getFile())
				: LanguageBundle.getFormattedString("in_diInstallFailed", failure.getMessage());
		notifier.accept(AlertType.ERROR, message);
	}

	/** Opens the native chooser on FX with the installer as its owner. */
	private File chooseArchive()
	{
		var fileChooser = new FileChooser();
		fileChooser.setTitle(LanguageBundle.getString("in_diChooserTitle"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
				LanguageBundle.getString("in_diPackageFilter"), "*.pcz", "*.zip"));
		if (currentFolder != null && currentFolder.isDirectory())
		{
			fileChooser.setInitialDirectory(currentFolder);
		}
		return fileChooser.showOpenDialog(getDialogPane().getScene().getWindow());
	}

	/** Shows a scrollable path list with explicit install, skip, and cancel choices. */
	private Decision confirm(String header, List<Path> files)
	{
		var alert = new Alert(AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.initOwner(getDialogPane().getScene().getWindow());
		alert.setTitle(getTitle());
		alert.setHeaderText(header);
		var list = new ListView<>(FXCollections.observableArrayList(files.stream().map(Path::toString).toList()));
		list.setPrefSize(600, 180);
		alert.getDialogPane().setContent(list);
		alert.setResizable(true);
		var result = alert.showAndWait().orElse(ButtonType.CANCEL);
		if (result == ButtonType.YES)
		{
			return Decision.INSTALL;
		}
		return result == ButtonType.NO ? Decision.SKIP : Decision.CANCEL;
	}

	/** Shows a localized result or error over the installer. */
	private void showMessage(AlertType type, String message)
	{
		var alert = new Alert(type, message, ButtonType.OK);
		alert.initOwner(getDialogPane().getScene().getWindow());
		alert.setTitle(getTitle());
		alert.setHeaderText(null);
		alert.showAndWait();
	}

	/** Replaces the native chooser for an embedded workflow or a headless test. */
	void setChooser(Supplier<File> chooser)
	{
		this.chooser = chooser;
	}

	/** Supplies the decision for each confirmation group. */
	void setConfirmation(BiFunction<String, List<Path>, Decision> confirmation)
	{
		this.confirmation = confirmation;
	}

	/** Supplies the result/error notification handler. */
	void setNotifier(BiConsumer<AlertType, String> notifier)
	{
		this.notifier = notifier;
	}
}
