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
package pcgen.gui3.sources;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import pcgen.core.Campaign;
import pcgen.system.ConfigurationSettings;

/**
 * Renders campaign source-info into a JavaFX {@link WebEngine}.
 *
 * <p>The campaign HTML embeds cover/logo images. A document handed to
 * {@code loadContent} has an {@code about:blank} origin, and WebKit refuses to
 * fetch {@code file:} sub-resources from it, so those images render blank. To
 * give the document a real {@code file:} origin (and matching image access),
 * this renderer writes the HTML to a temp file and {@code load()}s it by URL;
 * {@link ModernHtmlInfoBuilder} supplies the HTML5 markup and a {@code <base
 * href>} pointing at the data directory so relative image paths resolve.
 *
 * <p>The WebView's built-in "Reload" (right-click menu) is intercepted: a plain
 * reload would re-fetch the temp file's stale HTML, so this renderer regenerates
 * the temp file from the last-shown campaign first. Combined with
 * {@link ModernHtmlInfoBuilder}'s dev template loading, that lets a developer
 * edit {@code campaign-info.ftl} and see the change via Reload — no restart.
 */
final class SourceInfoRenderer
{
	private static final Logger LOG = Logger.getLogger(SourceInfoRenderer.class.getName());

	private static Path tempFile;

	/** The campaign last rendered into {@link #tempFile}, for reload-regeneration. */
	private static Campaign lastCampaign;
	private static Path lastDataDir;

	/**
	 * True while we are performing our own {@code load()} of the temp file, so
	 * the reload interceptor can tell our render apart from a user-triggered
	 * reload of the same URL.
	 */
	private static boolean ourLoad;

	/** The engine we have installed the reload interceptor on (install once). */
	private static WebEngine wiredEngine;

	private SourceInfoRenderer()
	{
	}

	/**
	 * Renders {@code campaign}'s source info into {@code engine}. A null
	 * campaign clears the pane.
	 *
	 * @param engine the target engine
	 * @param campaign the campaign to display, or {@code null} to clear
	 */
	static void renderCampaign(WebEngine engine, Campaign campaign)
	{
		installReloadInterceptor(engine);
		if (campaign == null)
		{
			lastCampaign = null;
			clear(engine);
			return;
		}
		lastCampaign = campaign;
		lastDataDir = Path.of(ConfigurationSettings.getPccFilesDir());
		load(engine, campaign, lastDataDir);
	}

	/** Renders {@code campaign} into the temp file and loads it into {@code engine}. */
	private static void load(WebEngine engine, Campaign campaign, Path dataDir)
	{
		try
		{
			String html = ModernHtmlInfoBuilder.forCampaign(campaign, dataDir);
			Path file = sharedTempFile();
			Files.writeString(file, html, StandardCharsets.UTF_8);
			ourLoad = true;
			engine.load(file.toUri().toString());
		}
		catch (IOException exception)
		{
			// Template render or temp write failed: clear the pane rather than
			// throw on selection.
			LOG.log(Level.WARNING, exception, () -> "Unable to render source info for " + campaign.getKeyName());
			clear(engine);
		}
	}

	/**
	 * Installs (once per engine) a listener that turns the WebView's built-in
	 * reload of our temp file into a fresh regeneration of the last campaign, so
	 * template edits are picked up. Our own {@link #load} is ignored via the
	 * {@link #ourLoad} guard.
	 */
	private static void installReloadInterceptor(WebEngine engine)
	{
		if (wiredEngine == engine)
		{
			return;
		}
		wiredEngine = engine;
		engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
			if (newState != Worker.State.SCHEDULED)
			{
				return;
			}
			if (ourLoad)
			{
				// This SCHEDULED is our own load(); consume the guard and let it run.
				ourLoad = false;
				return;
			}
			// A reload we did not initiate (built-in menu) of our temp file:
			// regenerate from the last campaign so the reload shows fresh output.
			// Defer off the load-worker callback to avoid a re-entrant load().
			if (lastCampaign != null && isTempFileLocation(engine.getLocation()))
			{
				javafx.application.Platform.runLater(() -> load(engine, lastCampaign, lastDataDir));
			}
		});
	}

	private static boolean isTempFileLocation(String location)
	{
		return location != null && tempFile != null && location.equals(tempFile.toUri().toString());
	}

	/** Clears the pane. An empty document needs no file access. */
	static void clear(WebEngine engine)
	{
		ourLoad = true;
		engine.loadContent("");
	}

	private static synchronized Path sharedTempFile() throws IOException
	{
		if (tempFile == null)
		{
			File file = File.createTempFile("pcgen-source-info", ".html");
			file.deleteOnExit();
			tempFile = file.toPath();
		}
		return tempFile;
	}
}
