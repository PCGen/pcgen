/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.gui3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/** Exercises the actual guide FXML, controller updates, and tip presentation. */
@ExtendWith(ApplicationExtension.class)
class InfoGuidePanelTest
{
	private Scene scene;
	private InfoGuideController controller;
	private Label hint;
	private ListView<?> sources;
	private WebView browser;

	@BeforeAll
	static void initializeLanguage()
	{
		LanguageBundle.getString("in_si_sources");
	}

	@Start
	void start(Stage stage) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(InfoGuideController.class.getResource("InfoGuidePanel.fxml"),
				LanguageBundle.getBundle());
		scene = loader.load();
		controller = loader.getController();
		hint = (Label) loader.getNamespace().get("sourcesHint");
		Object sourceNode = loader.getNamespace().get("sourcesList");
		if (!(sourceNode instanceof ListView<?> sourceList))
		{
			throw new IllegalStateException("Missing source list in guide FXML");
		}
		sources = sourceList;
		browser = (WebView) loader.getNamespace().get("tipBrowser");
		stage.setScene(scene);
		stage.setWidth(950);
		stage.setHeight(650);
		stage.show();
	}

	@Test
	void tipCard_should_haveBorderAndPadding() throws Exception
	{
		asyncFx(() -> {
			scene.getRoot().applyCss();
			VBox card = (VBox) browser.getParent();
			assertTrue(card.getStyleClass().containsAll(List.of("guide-card", "tip-card")));
			assertNotNull(card.getBorder());
			assertEquals(12, card.getPadding().getBottom());
		}).get(5, TimeUnit.SECONDS);
	}

	@Test
	void sources_should_switchBetweenHintAndList() throws Exception
	{
		controller.setNoSources();
		assertSources(List.of());
		controller.setSources(List.of("Core Rulebook", "Advanced Guide"));
		assertSources(List.of("Core Rulebook", "Advanced Guide"));
		controller.setNoSources();
		assertSources(List.of());
	}

	/** Checks visibility, layout participation, and displayed sources after queued updates. */
	private void assertSources(List<String> expected) throws Exception
	{
		asyncFx(() -> {
			assertEquals(expected.isEmpty(), hint.isVisible());
			assertEquals(expected.isEmpty(), hint.isManaged());
			assertEquals(!expected.isEmpty(), sources.isVisible());
			assertEquals(!expected.isEmpty(), sources.isManaged());
			assertEquals(expected, sources.getItems());
		}).get(5, TimeUnit.SECONDS);
	}

	@Test
	void tip_should_useSystemFontAndTransparentBackground() throws Exception
	{
		controller.setTip("<b id=\"marker\">Default tip</b>");
		waitForTip("rgb(51, 51, 51)");
		asyncFx(() -> {
			String family = (String) browser.getEngine().executeScript(
					"getComputedStyle(document.body).fontFamily");
			String expectedFamily = "System".equals(hint.getFont().getFamily())
					? "system-ui" : hint.getFont().getFamily();
			assertTrue(family.contains(expectedFamily));
			assertEquals("13px", browser.getEngine().executeScript(
					"getComputedStyle(document.body).fontSize"));
			assertEquals("rgba(0, 0, 0, 0)", browser.getEngine().executeScript(
					"getComputedStyle(document.body).backgroundColor"));
		}).get(5, TimeUnit.SECONDS);
	}

	@Test
	void tip_should_followNativeTypographyAndPreserveMarkup() throws Exception
	{
		asyncFx(() -> {
			hint.setStyle("-fx-font-family: 'Serif'; -fx-font-size: 19px; -fx-text-fill: #abcdef;");
			scene.getRoot().applyCss();
		}).get(5, TimeUnit.SECONDS);
		controller.setTip("<b id=\"marker\">A &amp; B</b>");
		waitForTip("rgb(171, 205, 239)");
		asyncFx(() -> {
			assertEquals("A & B", browser.getEngine().executeScript(
					"document.getElementById('marker').textContent"));
			assertEquals("19px", browser.getEngine().executeScript(
					"getComputedStyle(document.body).fontSize"));
			String family = (String) browser.getEngine().executeScript(
					"getComputedStyle(document.body).fontFamily");
			assertTrue(family.contains(hint.getFont().getFamily()));
			assertEquals("700", browser.getEngine().executeScript(
					"getComputedStyle(document.getElementById('marker')).fontWeight"));
			hint.setStyle("-fx-font-family: 'Serif'; -fx-font-size: 21px; -fx-text-fill: #fedcba;");
			scene.getRoot().applyCss();
		}).get(5, TimeUnit.SECONDS);
		waitForTip("rgb(254, 220, 186)");
		asyncFx(() -> assertEquals("21px", browser.getEngine().executeScript(
				"getComputedStyle(document.body).fontSize"))).get(5, TimeUnit.SECONDS);
	}

	/** Waits for the expected tip document and color, rather than a previous completed load. */
	private void waitForTip(String color) throws Exception
	{
		waitFor(10, TimeUnit.SECONDS, () -> asyncFx(() ->
				browser.getEngine().getLoadWorker().getState() == Worker.State.SUCCEEDED
						&& browser.getEngine().getDocument().getElementById("marker") != null
						&& color.equals(browser.getEngine().executeScript(
						"getComputedStyle(document.body).color"))).get());
	}
}
