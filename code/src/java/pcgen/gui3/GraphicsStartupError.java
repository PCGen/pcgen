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
 */
package pcgen.gui3;

import java.awt.GraphicsEnvironment;
import java.util.Locale;

import javax.swing.JOptionPane;

import pcgen.util.GracefulExit;
import pcgen.util.Logging;

/**
 * Turns a JavaFX toolkit-initialisation failure into an honest, actionable
 * message instead of the misleading raw stack trace. See the design rationale
 * in the Troubleshooting section of the README (issue: JavaFX links against
 * {@code libgthread-2.0.so.0}, which recent glib no longer ships as a stub).
 */
public final class GraphicsStartupError
{
	private GraphicsStartupError()
	{
	}

	/**
	 * The kind of toolkit-init failure, used to select the message.
	 */
	public enum Kind
	{
		/** A native library could not be loaded (e.g. an unsatisfiable transitive dependency). */
		NATIVE_LIBRARY,
		/** The libraries loaded, but no display could be opened (headless / no X or Wayland). */
		NO_DISPLAY,
		/** Any other toolkit-init failure. */
		GENERIC
	}

	/**
	 * Classifies {@code failure} by walking its cause chain.
	 *
	 * @param failure the throwable raised while starting the JavaFX toolkit
	 * @return the {@link Kind} of failure
	 */
	public static Kind classify(Throwable failure)
	{
		for (Throwable t = failure; t != null; t = t.getCause())
		{
			if (t instanceof LinkageError)
			{
				return Kind.NATIVE_LIBRARY;
			}
			String message = t.getMessage();
			if (message != null && message.contains("Unable to open DISPLAY"))
			{
				return Kind.NO_DISPLAY;
			}
		}
		return Kind.GENERIC;
	}

	/**
	 * Builds the user-facing message for a classified failure. The message is
	 * universal (no distro-specific commands) and echoes the real underlying
	 * cause so a knowledgeable user gets the concrete clue.
	 *
	 * @param kind    the classification from {@link #classify(Throwable)}
	 * @param failure the original throwable
	 * @return the message to log and (when not headless) show in a dialog
	 */
	public static String buildMessage(Kind kind, Throwable failure)
	{
		String cause = "Underlying error: " + rootMessage(failure);
		String seeReadme = "See the Troubleshooting section of the PCGen README.";
		return switch (kind)
		{
			case NATIVE_LIBRARY -> String.join("\n",
					"PCGen's graphical toolkit (JavaFX) failed to start.",
					"It could not load a native graphics library. This is usually a missing system package.",
					cause,
					"See the Troubleshooting section of the PCGen README for platform-specific fixes.");
			case NO_DISPLAY -> String.join("\n",
					"PCGen's graphical toolkit (JavaFX) could not open a display.",
					"Run PCGen from a graphical desktop session, not a remote/SSH shell.",
					cause,
					seeReadme);
			case GENERIC -> String.join("\n",
					"PCGen's graphical toolkit (JavaFX) failed to start.",
					cause,
					seeReadme);
		};
	}

	/**
	 * Reports the toolkit-init failure to the user and exits. Logs the message
	 * (with the throwable) always; additionally shows a Swing dialog when a
	 * display is available. Does not return.
	 *
	 * @param failure the throwable raised while starting the JavaFX toolkit
	 */
	public static void reportAndExit(Throwable failure)
	{
		Kind kind = classify(failure);
		String message = buildMessage(kind, failure);
		Logging.errorPrint(message, failure);
		if (!GraphicsEnvironment.isHeadless())
		{
			JOptionPane.showMessageDialog(null, message, "PCGen", JOptionPane.ERROR_MESSAGE);
		}
		GracefulExit.exit(1);
	}

	private static String rootMessage(Throwable failure)
	{
		Throwable root = failure;
		while (root.getCause() != null)
		{
			root = root.getCause();
		}
		String message = root.getMessage();
		return (message != null) ? message : root.getClass().getName();
	}
}
