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

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import pcgen.core.Campaign;
import pcgen.gui2.tools.DesktopBrowserLauncher;
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

	/**
	 * Held open across renders. Closing a freshly written file is what the cost
	 * of showing a source almost entirely was; see {@link #writeDocument}.
	 */
	private static FileChannel tempChannel;

	/** The document last written, so an identical one need not be written again. */
	private static String lastHtml;

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
			writeDocument(html);
			ourLoad = true;
			engine.load(sharedTempFile().toUri().toString());
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
		SourceInfoLinks.install(engine, SourceInfoRenderer::openInBrowser);
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

	/** Hands a source's link to the system browser, as the legacy info pane does. */
	private static void openInBrowser(URI address)
	{
		try
		{
			DesktopBrowserLauncher.viewInBrowser(address);
		}
		catch (IOException exception)
		{
			LOG.log(Level.WARNING, exception, () -> "Unable to open " + address);
		}
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

	/**
	 * Writes {@code html} as the document the WebView loads.
	 *
	 * <p>This is the expensive half of showing a source -- about 1.7ms against
	 * the published data, where rendering the HTML takes 0.36ms -- and almost
	 * none of it is the writing. Opening a file and closing it again without
	 * writing costs 0.02ms; it is closing a file that <em>has</em> been written
	 * that makes the filesystem commit, and doing that per click is what the
	 * cost was. Truncating and writing through a channel that is already open
	 * costs 0.017ms, so the channel is opened once and kept.
	 *
	 * <p>Truncating first is not optional: without it, a document shorter than
	 * the last leaves that one's tail behind and the WebView renders the join.
	 *
	 * <p>An unchanged document is not written at all. Re-showing the source
	 * already displayed, and the regeneration behind the WebView's Reload, both
	 * produce byte-identical HTML.
	 *
	 * @param html the document to write
	 * @throws IOException if it cannot be written
	 */
	static synchronized void writeDocument(String html) throws IOException
	{
		if (html.equals(lastHtml))
		{
			return;
		}
		var channel = sharedChannel();
		channel.truncate(0);
		channel.position(0);
		var bytes = ByteBuffer.wrap(html.getBytes(StandardCharsets.UTF_8));
		while (bytes.hasRemaining())
		{
			channel.write(bytes);
		}
		// Only once the file holds it: a failed write must not persuade the
		// next call that there is nothing to do.
		lastHtml = html;
	}

	private static synchronized FileChannel sharedChannel() throws IOException
	{
		if (tempChannel == null)
		{
			tempChannel = FileChannel.open(sharedTempFile(), StandardOpenOption.WRITE);
		}
		return tempChannel;
	}

	/**
	 * The file this renderer writes its HTML into, created on first use.
	 *
	 * <p>It is deliberately not left in the system temp directory, which every
	 * account on the machine can write to. The file is cached and reused for
	 * each campaign the user clicks, so anyone able to replace it between a
	 * render and the {@code load()} that follows chooses what the WebView
	 * shows -- and that document is given a real {@code file:} origin, with
	 * scripting enabled, precisely so it can reach local files. Restricting the
	 * file alone would not help: in a writable directory it can be replaced
	 * whatever its own permissions say.
	 *
	 * @return the file to render into
	 * @throws IOException if the file or its directory cannot be created
	 */
	static synchronized Path sharedTempFile() throws IOException
	{
		if (tempFile == null)
		{
			Path directory = createPrivateDirectory();
			// Deletion runs in reverse order of registration, so the directory
			// must be registered first to be removed after the file it holds.
			directory.toFile().deleteOnExit();
			Path file = Files.createTempFile(directory, "source-info", ".html");
			file.toFile().deleteOnExit();
			tempFile = file;
		}
		return tempFile;
	}

	/**
	 * Creates a temp directory only its owner may enter.
	 *
	 * <p>The permissions are applied as the directory is created rather than
	 * set afterwards, which would leave a window in which it is open. The name
	 * is left to {@code createTempDirectory} for the same reason a fixed one
	 * would be wrong: an attacker who can guess it can create the directory
	 * first, on their own terms.
	 *
	 * @return the new directory
	 * @throws IOException if it cannot be created
	 */
	static Path createPrivateDirectory() throws IOException
	{
		try
		{
			return Files.createTempDirectory("pcgen-source-info",
					PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")));
		}
		catch (UnsupportedOperationException notPosix)
		{
			// Windows has no POSIX permissions, and its per-user temp directory
			// is already closed to other accounts by its inherited ACL.
			return Files.createTempDirectory("pcgen-source-info");
		}
	}
}
