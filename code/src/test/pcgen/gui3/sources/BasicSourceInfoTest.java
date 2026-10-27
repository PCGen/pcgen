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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/** Checks the Basic preview through its real selection listener and WebView. */
@ExtendWith(ApplicationExtension.class)
class BasicSourceInfoTest
{
	private static final UIPropertyContext CONTEXT = UIPropertyContext.createContext("SourceSelectionDialog");
	private final GameMode mode = new GameMode("BasicSourceInfoTest");
	private final Campaign first = campaign("First", "First description");
	private final Campaign second = campaign("Second", "Second description");
	private BasicSourceSelectionController controller;
	private WebEngine engine;
	private Stage stage;
	private String previousSource;

	@Start
	void start(Stage window) throws Exception
	{
		LanguageBundle.getString("");
		previousSource = CONTEXT.getProperty("selectedSource", null);
		stage = window;
		var loader = new FXMLLoader(getClass().getResource("BasicSourceSelection.fxml"), LanguageBundle.getBundle());
		Tab tab = loader.load();
		controller = loader.getController();
		var tabs = new TabPane(tab);
		stage.setScene(new Scene(tabs, 800, 600));
		stage.show();
		engine = ((WebView) tabs.lookup("#infoPane")).getEngine();
	}

	@AfterEach
	void close() throws Exception
	{
		asyncFx(() -> {
			controller.close();
			stage.hide();
			CONTEXT.setProperty("selectedSource", previousSource);
		}).get(5, TimeUnit.SECONDS);
	}

	@Test
	void selectionPreview_should_includeAndReloadEveryCampaign() throws Exception
	{
		asyncFx(() -> controller.setSources(FXCollections.observableArrayList(
				new SourceBundle("My selection", mode, List.of(first, second), true, null))))
				.get(5, TimeUnit.SECONDS);
		awaitLoaded();
		String text = asyncFx(() -> (String) engine.executeScript("document.body.innerText"))
				.get(5, TimeUnit.SECONDS);
		assertTrue(text.contains("My selection"), text);
		assertTrue(text.contains("First description"), text);
		assertTrue(text.contains("Second description"), text);

		asyncFx(() -> {
			second.put(StringKey.DESCRIPTION, "Updated second description");
			engine.reload();
		}).get(5, TimeUnit.SECONDS);
		waitFor(10, TimeUnit.SECONDS, () -> asyncFx(() ->
				engine.getLoadWorker().getState() == Worker.State.SUCCEEDED
						&& ((String) engine.executeScript("document.body.innerText"))
								.contains("Updated second description")).get());
		assertTrue(asyncFx(() -> (String) engine.executeScript("document.body.innerText"))
				.get(5, TimeUnit.SECONDS).contains("First description"));
	}

	private void awaitLoaded() throws Exception
	{
		waitFor(10, TimeUnit.SECONDS,
				() -> asyncFx(() -> engine.getLoadWorker().getState() == Worker.State.SUCCEEDED).get());
	}

	private static Campaign campaign(String name, String description)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		campaign.setSourceURI(URI.create("file:/test/" + name + ".pcc"));
		campaign.put(StringKey.DESCRIPTION, description);
		return campaign;
	}
}
