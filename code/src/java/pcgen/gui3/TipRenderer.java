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
package pcgen.gui3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.function.Supplier;

import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

/**
 * Owns one tip view's private HTML document. A file origin makes the native Reload action
 * available; its interceptor regenerates the document from the current tips file. Used only on FX.
 */
public final class TipRenderer implements AutoCloseable
{
	private final WebEngine engine;
	private Path directory;
	private Path document;
	private boolean ourLoad;
	private boolean closed;

	/**
	 * Installs the reload interceptor for this view's engine.
	 *
	 * @param engine the view's WebView engine
	 * @param reload rereads tips and returns the current tip's updated HTML
	 */
	public TipRenderer(WebEngine engine, Supplier<String> reload)
	{
		GuiAssertions.assertIsJavaFXThread();
		this.engine = engine;
		engine.getLoadWorker().stateProperty().addListener((_, _, state) -> {
			if (state == Worker.State.SCHEDULED && !ourLoad && !closed && document != null
					&& engine.getLocation().equals(document.toUri().toString()))
			{
				// Regeneration must run outside the engine's load callback.
				Platform.runLater(() -> {
					if (!closed)
					{
						render(reload.get());
					}
				});
			}
		});
	}

	/**
	 * Writes and displays the tip; calls after the renderer is closed are ignored.
	 *
	 * @param html the complete tip document
	 */
	public void render(String html)
	{
		GuiAssertions.assertIsJavaFXThread();
		if (closed)
		{
			return;
		}
		try
		{
			if (directory == null)
			{
				directory = createPrivateDirectory();
			}
			if (document == null)
			{
				document = Files.createTempFile(directory, "tip-", ".html");
			}
			Files.writeString(document, html);
			ourLoad = true;
			engine.load(document.toUri().toString());
		}
		catch (IOException exception)
		{
			Logging.errorPrint("Unable to render Tip of the Day", exception);
		}
		finally
		{
			ourLoad = false;
		}
	}

	/** Creates the document's parent with owner-only access where POSIX permissions are supported. */
	private static Path createPrivateDirectory() throws IOException
	{
		try
		{
			return Files.createTempDirectory("pcgen-tips-",
					PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")));
		}
		catch (UnsupportedOperationException notPosix)
		{
			// Windows inherits the per-user temp directory's ACL.
			return Files.createTempDirectory("pcgen-tips-");
		}
	}

	/** Cancels pending loads and deletes the private document and directory. Idempotent. */
	@Override
	public void close()
	{
		GuiAssertions.assertIsJavaFXThread();
		closed = true;
		engine.getLoadWorker().cancel();
		try
		{
			if (document != null)
			{
				Files.deleteIfExists(document);
			}
			if (directory != null)
			{
				Files.deleteIfExists(directory);
			}
		}
		catch (IOException exception)
		{
			Logging.errorPrint("Unable to delete Tip of the Day document", exception);
		}
	}
}
