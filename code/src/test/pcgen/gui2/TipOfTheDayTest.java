/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */
package pcgen.gui2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.TipOfTheDayHandler;
import pcgen.gui3.dialog.TipOfTheDayController;
import pcgen.gui3.InfoGuideController;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PropertyContext;

import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.framework.junit5.ApplicationExtension;

/** Exercises automatic tip requests and the real dialog's reload and disposal behavior. */
@ExtendWith(ApplicationExtension.class)
class TipOfTheDayTest
{

	@TempDir
	Path directory;
	private Path tipsFile;
	private GameMode oldGame;
	private String oldSystems;
	private String oldLastTip;
	private String oldShowTips;
	private Field instanceField;
	private Object oldHandler;
	private List<Window> existingWindows;
	private Stage stage;
	private WebEngine engine;
	private InfoGuideController guideController;
	private PropertyContext context;
	private final StartupTipOfTheDay startupTip = new StartupTipOfTheDay();

	@BeforeEach
	void prepareTips() throws Exception
	{
		// Initialize the domain outside timed FX callbacks and isolate all singleton state.
		oldGame = SettingsHandler.getGameAsProperty().get();
		oldSystems = ConfigurationSettings.getSystemProperty(ConfigurationSettings.SYSTEMS_DIR);
		context = UIPropertyContext.createContext("TipOfTheDay");
		oldLastTip = context.getProperty("lastTip");
		oldShowTips = context.getProperty("showTipOfTheDay");
		instanceField = TipOfTheDayHandler.class.getDeclaredField("INSTANCE");
		instanceField.setAccessible(true);
		oldHandler = instanceField.get(null);
		instanceField.set(null, null);
		context.setInt("lastTip", -1);
		context.setBoolean("showTipOfTheDay", true);
		SettingsHandler.getGameAsProperty().set(new GameMode("tip-test"));
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.SYSTEMS_DIR, directory.toString());
		Path modeDir = Files.createDirectories(directory.resolve("gameModes/tip-test"));
		tipsFile = modeDir.resolve(LanguageBundle.getString("in_tipsFileName"));
		Files.writeString(tipsFile, "First <em>tip</em>\nSecond tip\nThird tip\n");
		TipOfTheDayHandler.getInstance().loadTips();
		existingWindows = asyncFx(() -> List.copyOf(Window.getWindows())).get(5, TimeUnit.SECONDS);
	}

	@AfterEach
	void restoreState() throws Exception
	{
		asyncFx(() -> {
			if (guideController != null)
			{
				guideController.close();
			}
			newWindows().forEach(Window::hide);
		}).get(5, TimeUnit.SECONDS);
		waitForFxEvents();
		instanceField.set(null, oldHandler);
		SettingsHandler.getGameAsProperty().set(oldGame);
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.SYSTEMS_DIR, oldSystems);
		context.setProperty("lastTip", oldLastTip);
		context.setProperty("showTipOfTheDay", oldShowTips);
	}

	private List<Window> newWindows()
	{
		return Window.getWindows().stream().filter(window -> !existingWindows.contains(window)).toList();
	}

	private void openDialog() throws Exception
	{
		asyncFx(() -> {
			var loader = new FXMLLoader(TipOfTheDayController.class.getResource("TipOfTheDay.fxml"),
					LanguageBundle.getBundle());
			Scene scene = loader.load();
			stage = new Stage();
			stage.setScene(scene);
			stage.show();
			engine = ((WebView) scene.lookup("#browser")).getEngine();
			return null;
		}).get(10, TimeUnit.SECONDS);
		awaitLoaded();
	}

	/** Opens the actual guide scene and displays its next tip using the shared handler. */
	private void openGuide() throws Exception
	{
		asyncFx(() -> {
			var loader = new FXMLLoader(InfoGuideController.class.getResource("InfoGuidePanel.fxml"),
					LanguageBundle.getBundle());
			Scene scene = loader.load();
			guideController = loader.getController();
			stage = new Stage();
			stage.setScene(scene);
			stage.show();
			engine = ((WebView) scene.lookup("#tipBrowser")).getEngine();
			guideController.showNextTip();
			return null;
		}).get(10, TimeUnit.SECONDS);
		awaitLoaded();
	}

	@Test
	void guideReload_should_rereadItsOwnDisplayedSlotAndCleanUp() throws Exception
	{
		openGuide();
		assertTrue(tipText().contains("First tip"));
		Path document = asyncFx(() -> Path.of(URI.create(engine.getLocation()))).get(5, TimeUnit.SECONDS);
		TipOfTheDayHandler.getInstance().getNextTip();
		Files.writeString(tipsFile, "Edited <em>first</em>\nEdited second\n");
		asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
		waitFor(10, TimeUnit.SECONDS, () -> tipText().contains("Edited first"));
		assertEquals("first", asyncFx(() -> engine.executeScript("document.querySelector('em').textContent"))
				.get(5, TimeUnit.SECONDS));
		asyncFx(guideController::close).get(5, TimeUnit.SECONDS);
		assertFalse(Files.exists(document));
		assertFalse(Files.exists(document.getParent()));
		guideController.showNextTip();
		waitForFxEvents();
		assertFalse(Files.exists(document), "Late updates must not reopen a disposed guide");
	}

	@Test
	void guideReload_should_recoverAfterTipsBecomeEmpty() throws Exception
	{
		openGuide();
		Files.writeString(tipsFile, "");
		asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
		waitFor(10, TimeUnit.SECONDS, () -> tipText().isBlank());
		Files.writeString(tipsFile, "Recovered tip\n");
		asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
		waitFor(10, TimeUnit.SECONDS, () -> tipText().contains("Recovered tip"));
	}

	@Test
	void guideReload_should_clampSlotWhenTipsAreRemoved() throws Exception
	{
		openGuide();
		guideController.showNextTip();
		waitFor(10, TimeUnit.SECONDS, () -> tipText().contains("Second tip"));
		Files.writeString(tipsFile, "Only remaining tip\n");
		asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
		waitFor(10, TimeUnit.SECONDS, () -> tipText().contains("Only remaining tip"));
		Files.writeString(tipsFile, "Edited remaining tip\nAnother tip\n");
		asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
		waitFor(10, TimeUnit.SECONDS, () -> tipText().contains("Edited remaining tip"));
	}

	private void awaitLoaded() throws Exception
	{
		waitForFxEvents();
		waitFor(10, TimeUnit.SECONDS, () -> asyncFx(
				() -> engine.getLoadWorker().getState() == Worker.State.SUCCEEDED).get(5, TimeUnit.SECONDS));
		waitForFxEvents();
	}

	private String tipText() throws Exception
	{
		return asyncFx(() -> engine.executeScript("document.body.textContent").toString())
				.get(5, TimeUnit.SECONDS);
	}

	private void click(String key) throws Exception
	{
		asyncFx(() -> stage.getScene().getRoot().lookupAll(".button").stream()
				.map(Button.class::cast).filter(button -> button.getText().equals(LanguageBundle.getString(key)))
				.findFirst().orElseThrow().fire()).get(5, TimeUnit.SECONDS);
	}

	@Test
	void automaticRequests_showOnlyOneDialog() throws Exception
	{
		startupTip.showIfEnabled();
		startupTip.showIfEnabled();
		waitForFxEvents();
		assertEquals(1, asyncFx(() -> newWindows().size()).get(5, TimeUnit.SECONDS));
		asyncFx(() -> newWindows().forEach(Window::hide)).get(5, TimeUnit.SECONDS);
		startupTip.showIfEnabled();
		waitForFxEvents();
		assertEquals(0, asyncFx(() -> newWindows().size()).get(5, TimeUnit.SECONDS));
	}

	@Test
	void disabledAtStartup_doesNotShowOnLaterRequests() throws Exception
	{
		context.setBoolean("showTipOfTheDay", false);
		startupTip.showIfEnabled();
		waitForFxEvents();
		context.setBoolean("showTipOfTheDay", true);
		startupTip.showIfEnabled();
		waitForFxEvents();
		assertEquals(0, asyncFx(() -> newWindows().size()).get(5, TimeUnit.SECONDS));
	}

	@Test
	void disablingTipsBeforeQueuedShow_preventsTheDialog() throws Exception
	{
		asyncFx(() -> {
			startupTip.showIfEnabled();
			context.setBoolean("showTipOfTheDay", false);
		}).get(5, TimeUnit.SECONDS);
		waitForFxEvents();
		assertEquals(0, asyncFx(() -> newWindows().size()).get(5, TimeUnit.SECONDS));
	}

	@Test
	void manualDialog_canReopenAfterAutomaticTipsAreDisabled() throws Exception
	{
		context.setBoolean("showTipOfTheDay", false);
		startupTip.showIfEnabled();
		waitForFxEvents();
		openDialog();
		assertTrue(tipText().contains("First tip"));
		click("in_close");
		Files.writeString(tipsFile, "Reopened with fresh tips\n");
		openDialog();
		assertTrue(tipText().contains("Reopened with fresh tips"));
	}

	@Test
	void reload_rereadsDisplayedSlotAndKeepsNavigationWorking() throws Exception
	{
		openDialog();
		click("in_tod_nextTip");
		awaitLoaded();
		assertTrue(tipText().contains("Second tip"));
		// The info guide also consumes the singleton: reload must use this window's slot.
		TipOfTheDayHandler.getInstance().getNextTip();
		Files.writeString(tipsFile, "Edited first\nEdited <em>second</em>\nEdited third\n");
		asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
		awaitLoaded();
		assertTrue(tipText().contains("Edited second"), tipText());
		assertEquals("second", asyncFx(() -> engine.executeScript("document.querySelector('em').textContent"))
				.get(5, TimeUnit.SECONDS));
		click("in_tod_prevTip");
		awaitLoaded();
		assertTrue(tipText().contains("Edited first"));
		click("in_tod_nextTip");
		awaitLoaded();
		assertTrue(tipText().contains("Edited second"));
	}

	@Test
	void translatedTips_preserveUtf8WhenLoadedFromAFile() throws Exception
	{
		Files.writeString(tipsFile, "Café – 日本語 <em>Zażółć</em>\n");
		openDialog();
		assertTrue(tipText().contains("Café – 日本語 Zażółć"), tipText());
	}

	@ParameterizedTest
	@ValueSource(strings = {"A tip", ""})
	void tipDocument_usesStandardsMode(String tip) throws Exception
	{
		Files.writeString(tipsFile, tip);
		openDialog();
		assertEquals("CSS1Compat", asyncFx(() -> engine.executeScript("document.compatMode"))
				.get(5, TimeUnit.SECONDS));
	}

	@Test
	void reload_handlesRemovedTipsAndAnEmptyFile() throws Exception
	{
		openDialog();
		click("in_tod_nextTip");
		click("in_tod_nextTip");
		Files.writeString(tipsFile, "Only remaining tip\n");
		asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
		awaitLoaded();
		assertTrue(tipText().contains("Only remaining tip"), tipText());
		Files.writeString(tipsFile, "");
		asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
		awaitLoaded();
		assertEquals("", tipText().trim());
		Files.writeString(tipsFile, "New tip after empty file\n");
		asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
		awaitLoaded();
		assertTrue(tipText().contains("New tip after empty file"));
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void closingBeforeNavigation_deletesDocumentAndCancelsQueuedReload(boolean closeButton) throws Exception
	{
		openDialog();
		String location = asyncFx(engine::getLocation).get(5, TimeUnit.SECONDS);
		assertTrue(location.startsWith("file:"), "Reload needs a file origin: " + location);
		Path document = Path.of(URI.create(location));
		if (Files.getFileStore(document).supportsFileAttributeView("posix"))
		{
			assertEquals(PosixFilePermissions.fromString("rwx------"),
					Files.getPosixFilePermissions(document.getParent()));
		}
		asyncFx(() -> {
			engine.reload();
			if (closeButton)
			{
				stage.getScene().getRoot().lookupAll(".button").stream().map(Button.class::cast)
						.filter(Button::isCancelButton).findFirst().orElseThrow().fire();
			}
			else
			{
				stage.close();
			}
		}).get(5, TimeUnit.SECONDS);
		waitForFxEvents();
		assertFalse(Files.exists(document));
		assertFalse(Files.exists(document.getParent()));
	}
}
