/*
 * Copyright 2026 the PCGen Project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.gui3.install;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;

import pcgen.cdom.enumeration.Destination;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.system.PCGenPropBundle;
import pcgen.system.LanguageBundle;
import plugin.lsttokens.campaign.CampaignToken;
import plugin.lsttokens.campaign.InfotextToken;
import plugin.lsttokens.campaign.installable.DestToken;
import plugin.lsttokens.campaign.installable.MindevverToken;
import plugin.lsttokens.campaign.installable.MinverToken;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testfx.framework.junit5.ApplicationExtension;

/** Exercises the FXML workflow with real manifests and extraction, without opening native prompts. */
@ExtendWith(ApplicationExtension.class)
class DataInstallerDialogTest
{
	@TempDir
	Path directory;
	private Map<Destination, Path> destinations;
	private Path sheets;
	private Path application;
	private DataInstallerDialog dialog;
	private final Queue<Runnable> work = new ConcurrentLinkedQueue<>();
	private final List<Notice> notices = new ArrayList<>();
	private Map<String, LstToken> previousInstallTokens;

	private record Notice(AlertType type, String message)
	{
	}

	@BeforeEach
	void setUp() throws Exception
	{
		var tokens = TokenStore.inst().getTokenMap(InstallLstToken.class);
		previousInstallTokens = new HashMap<>(tokens);
		List.<InstallLstToken>of(new CampaignToken(), new InfotextToken(), new DestToken(),
				new MinverToken(), new MindevverToken())
				.forEach(token -> tokens.put(token.getTokenName(), token));
		destinations = Map.of(
				Destination.DATA, Files.createDirectory(directory.resolve("data")),
				Destination.VENDORDATA, Files.createDirectory(directory.resolve("vendor")),
				Destination.HOMEBREWDATA, Files.createDirectory(directory.resolve("homebrew")));
		sheets = Files.createDirectory(directory.resolve("sheets"));
		application = Files.createDirectory(directory.resolve("application"));
		asyncFx(() -> {
			dialog = new DataInstallerDialog(destinations, sheets, application, work::add);
			dialog.setNotifier((type, message) -> {
				assertTrue(Platform.isFxApplicationThread());
				notices.add(new Notice(type, message));
			});
			dialog.setConfirmation((header, files) -> {
				throw new AssertionError("Unexpected confirmation: " + header);
			});
			dialog.show();
		}).get();
	}

	@AfterEach
	void tearDown() throws Exception
	{
		try
		{
			while (!work.isEmpty())
			{
				runNext();
			}
			if (dialog != null)
			{
				asyncFx(() -> {
					if (dialog.isShowing())
					{
						dialog.getDialogPane().getScene().getWindow().hide();
					}
				}).get();
			}
		}
		finally
		{
			var tokens = TokenStore.inst().getTokenMap(InstallLstToken.class);
			tokens.clear();
			tokens.putAll(previousInstallTokens);
		}
	}

	@ParameterizedTest
	@CsvSource({"DATA,dataLocation", "VENDORDATA,vendorLocation", "HOMEBREWDATA,homebrewLocation"})
	void install_should_requireALocationAndUseTheChosenDirectory(Destination destination, String control)
			throws Exception
	{
		select(writePackage("book.pcz", "CAMPAIGN:Book\nMINVER:1.0.0\n",
				Map.of("data/publisher/book.lst", "book")));
		runNext();
		asyncFx(() -> {
			assertTrue(button("install").isDisabled());
			((RadioButton) dialog.getDialogPane().lookup("#" + control)).fire();
			assertFalse(button("install").isDisabled());
			button("install").fire();
		}).get();
		assertFalse(Files.exists(destinations.get(destination).resolve("publisher/book.lst")));
		runNext();
		runNext();
		assertEquals("book", Files.readString(destinations.get(destination).resolve("publisher/book.lst")));
		assertAll(
				() -> assertEquals(1, notices.size()),
				() -> assertEquals(AlertType.INFORMATION, notices.getFirst().type()),
				() -> assertTrue(notices.getFirst().message().contains("Book")));
		asyncFx(() -> assertTrue(dialog.isShowing(), "Install must not dismiss the dialog")).get();
	}

	@Test
	void choose_should_preserveTheSelectionWhenTheChooserIsCancelled() throws Exception
	{
		select(writePackage("book.zip", "CAMPAIGN:Book\r\nMINVER:1.0.0\r\nDEST:DATA\r\n", Map.of()));
		runNext();
		asyncFx(() -> {
			dialog.setChooser(() -> null);
			button("choose").fire();
			assertFalse(button("install").isDisabled());
			assertTrue(((TextField) dialog.getDialogPane().lookup("#archivePath")).getText().endsWith("book.zip"));
		}).get();
		assertTrue(work.isEmpty());
	}

	@Test
	void choose_should_previewPackageMetadataWithoutInterpretingItsMarkup() throws Exception
	{
		select(writePackage("book.pcz", "CAMPAIGN:Book <edition> & more\nMINVER:1.0.0\n"
				+ "INFOTEXT:Useful <b>text</b> & details\nDEST:DATA\n", Map.of()));
		runNext();
		var engine = asyncFx(() -> ((WebView) dialog.getDialogPane().lookup("#information")).getEngine()).get();
		waitFor(10, TimeUnit.SECONDS, () -> asyncFx(() ->
				engine.getLoadWorker().getState() == Worker.State.SUCCEEDED
						&& ((String) engine.executeScript("document.body.innerText")).contains("Book <edition> & more"))
				.get());
		String text = asyncFx(() -> (String) engine.executeScript("document.body.innerText")).get();
		assertTrue(text.contains("Useful <b>text</b> & details"), text);
	}

	@ParameterizedTest
	@CsvSource({"manifest,WARNING", "version,WARNING", "empty,ERROR"})
	void choose_should_clearAnOldSelectionWhenTheNewPackageIsRejected(String problem, AlertType expected)
			throws Exception
	{
		select(writePackage("good.pcz", "CAMPAIGN:Good\nMINVER:1.0.0\nDEST:DATA\n", Map.of()));
		runNext();
		asyncFx(() -> assertFalse(button("install").isDisabled())).get();
		Path invalid = switch (problem)
		{
			case "manifest" -> writeArchive("invalid.zip", Map.of("readme.txt", "not a package"));
			case "version" -> writePackage("invalid.pcz", "CAMPAIGN:Newer\nMINVER:999.0.0\nDEST:DATA\n", Map.of());
			case "empty" -> writePackage("invalid.pcz", "", Map.of());
			default -> throw new IllegalArgumentException(problem);
		};
		select(invalid);
		runNext();
		asyncFx(() -> {
			assertTrue(button("install").isDisabled());
			assertTrue(((TextField) dialog.getDialogPane().lookup("#archivePath")).getText().isEmpty());
		}).get();
		assertEquals(expected, notices.getFirst().type());
	}

	@Test
	void choose_should_rejectAnUnsupportedDevelopmentVersion() throws Exception
	{
		String[] version = PCGenPropBundle.getVersionNumber().split("\\.");
		String minimum = version[0] + "." + version[1] + ".999999";
		select(writePackage("newer.pcz", "CAMPAIGN:Newer\r\nMINVER:1.0.0\r\nMINDEVVER:" + minimum + "\r\nDEST:DATA\r\n",
				Map.of()));
		runNext();
		asyncFx(() -> assertTrue(button("install").isDisabled())).get();
		assertEquals(AlertType.WARNING, notices.getFirst().type());
		assertTrue(notices.getFirst().message().contains(minimum));
	}

	@Test
	void install_should_skipNonstandardAndExistingFilesWithoutAbortingTheRemainder() throws Exception
	{
		Files.writeString(destinations.get(Destination.DATA).resolve("existing.lst"), "keep");
		select(writePackage("book.pcz", "CAMPAIGN:Book\nMINVER:1.0.0\nDEST:DATA\n", Map.of(
				"data/existing.lst", "replace", "data/new.lst", "new", "notes/readme.txt", "notes")));
		runNext();
		var confirmations = new ArrayList<List<Path>>();
		asyncFx(() -> {
			dialog.setConfirmation((header, files) -> {
				assertTrue(Platform.isFxApplicationThread());
				assertFalse(header.contains("<html>"));
				confirmations.add(files);
				return DataInstallerDialog.Decision.SKIP;
			});
			button("install").fire();
		}).get();
		runNext();
		runNext();
		assertAll(
				() -> assertEquals(2, confirmations.size()),
				() -> assertEquals(application.resolve("notes/readme.txt").toFile().getCanonicalFile().toPath(),
						confirmations.get(0).getFirst()),
				() -> assertEquals("keep", Files.readString(destinations.get(Destination.DATA).resolve("existing.lst"))),
				() -> assertEquals("new", Files.readString(destinations.get(Destination.DATA).resolve("new.lst"))),
				() -> assertFalse(Files.exists(application.resolve("notes"))),
				() -> assertEquals(AlertType.INFORMATION, notices.getFirst().type()));
	}

	@Test
	void install_should_writeNothingWhenConfirmationIsCancelled() throws Exception
	{
		select(writePackage("book.pcz", "CAMPAIGN:Book\nMINVER:1.0.0\nDEST:DATA\n",
				Map.of("data/new.lst", "new", "notes/readme.txt", "notes")));
		runNext();
		asyncFx(() -> {
			dialog.setConfirmation((_, _) -> DataInstallerDialog.Decision.CANCEL);
			button("install").fire();
		}).get();
		runNext();
		assertTrue(work.isEmpty());
		assertFalse(Files.exists(destinations.get(Destination.DATA).resolve("new.lst")));
		assertTrue(notices.isEmpty());
		asyncFx(() -> assertFalse(button("install").isDisabled())).get();
	}

	@Test
	void install_should_notClaimSuccessWhenEveryFileIsSkipped() throws Exception
	{
		Files.writeString(destinations.get(Destination.DATA).resolve("existing.lst"), "keep");
		select(writePackage("book.pcz", "CAMPAIGN:Book\nMINVER:1.0.0\nDEST:DATA\n",
				Map.of("data/existing.lst", "replace")));
		runNext();
		asyncFx(() -> {
			dialog.setConfirmation((_, _) -> DataInstallerDialog.Decision.SKIP);
			button("install").fire();
		}).get();
		runNext();
		assertTrue(work.isEmpty());
		assertEquals("keep", Files.readString(destinations.get(Destination.DATA).resolve("existing.lst")));
		assertEquals(LanguageBundle.getString("in_diNothingToInstall"), notices.getFirst().message());
	}

	@Test
	void install_should_keepTheDialogOpenAndInputsDisabledDuringWork() throws Exception
	{
		select(writePackage("book.pcz", "CAMPAIGN:Book\nMINVER:1.0.0\nDEST:DATA\n",
				Map.of("data/new.lst", "new")));
		assertBusyAndCannotClose();
		runNext();
		asyncFx(() -> button("install").fire()).get();
		assertBusyAndCannotClose();
		runNext();
		assertBusyAndCannotClose();
		runNext();
		asyncFx(() -> {
			assertFalse(button("choose").isDisabled());
			dialog.close();
			assertFalse(dialog.isShowing());
		}).get();
	}

	@Test
	void install_should_reportFilesystemFailuresAndAllowRetry() throws Exception
	{
		select(writePackage("book.pcz", "CAMPAIGN:Book\nMINVER:1.0.0\nDEST:DATA\n",
				Map.of("data/new.lst", "new")));
		runNext();
		asyncFx(() -> button("install").fire()).get();
		runNext();
		Files.writeString(destinations.get(Destination.DATA).resolve("new.lst"), "created later");
		runNext();
		assertEquals(AlertType.ERROR, notices.getFirst().type());
		assertEquals("created later", Files.readString(destinations.get(Destination.DATA).resolve("new.lst")));
		asyncFx(() -> assertFalse(button("install").isDisabled())).get();
	}

	private void assertBusyAndCannotClose() throws Exception
	{
		asyncFx(() -> {
			assertTrue(button("choose").isDisabled());
			assertTrue(button("install").isDisabled());
			dialog.close();
			assertTrue(dialog.isShowing());
		}).get();
	}

	private void select(Path archive) throws Exception
	{
		asyncFx(() -> {
			dialog.setChooser(archive::toFile);
			button("choose").fire();
		}).get();
	}

	private Button button(String id)
	{
		return (Button) dialog.getDialogPane().lookup("#" + id);
	}

	private void runNext() throws Exception
	{
		Runnable task = work.poll();
		assertNotNull(task, "The dialog should have queued background work");
		task.run();
		asyncFx(() -> { }).get();
	}

	private Path writePackage(String name, String manifest, Map<String, String> files) throws IOException
	{
		var entries = new LinkedHashMap<String, String>();
		entries.put("install.lst", manifest);
		entries.putAll(files);
		return writeArchive(name, entries);
	}

	private Path writeArchive(String name, Map<String, String> entries) throws IOException
	{
		Path archive = directory.resolve(name);
		try (var zip = new ZipOutputStream(Files.newOutputStream(archive)))
		{
			for (var entry : entries.entrySet())
			{
				zip.putNextEntry(new ZipEntry(entry.getKey()));
				zip.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
				zip.closeEntry();
			}
		}
		return archive;
	}
}
