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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import pcgen.gui3.GraphicsStartupError.Kind;

class GraphicsStartupErrorTest
{
	@Test
	void classify_should_be_native_library_when_UnsatisfiedLinkError_is_wrapped()
	{
		Throwable failure = new RuntimeException(new UnsatisfiedLinkError("no glassgtk3 in java.library.path"));
		assertEquals(Kind.NATIVE_LIBRARY, GraphicsStartupError.classify(failure));
	}

	@Test
	void classify_should_be_native_library_when_UnsatisfiedLinkError_is_nested_deep()
	{
		Throwable root = new UnsatisfiedLinkError("libgthread-2.0.so.0: cannot open shared object file");
		Throwable failure = new RuntimeException("startup failed", new IllegalStateException("glass", root));
		assertEquals(Kind.NATIVE_LIBRARY, GraphicsStartupError.classify(failure));
	}

	@Test
	void classify_should_be_no_display_when_message_reports_unable_to_open_display()
	{
		Throwable failure = new UnsupportedOperationException("Unable to open DISPLAY");
		assertEquals(Kind.NO_DISPLAY, GraphicsStartupError.classify(failure));
	}

	@Test
	void classify_should_be_generic_for_unrelated_runtime_error()
	{
		Throwable failure = new RuntimeException("something else entirely");
		assertEquals(Kind.GENERIC, GraphicsStartupError.classify(failure));
	}

	@Test
	void buildMessage_for_native_library_should_echo_underlying_cause_and_point_to_troubleshooting()
	{
		Throwable failure = new RuntimeException(
				new UnsatisfiedLinkError("libgthread-2.0.so.0: cannot open shared object file"));

		String message = GraphicsStartupError.buildMessage(Kind.NATIVE_LIBRARY, failure);

		assertAll(
				() -> assertTrue(message.contains("libgthread-2.0.so.0"),
						"message should echo the real underlying cause: " + message),
				() -> assertTrue(message.contains("Troubleshooting"),
						"message should point at the README Troubleshooting section: " + message));
	}

	@Test
	void buildMessage_for_native_library_should_not_recommend_installing_gtk()
	{
		Throwable failure = new RuntimeException(new UnsatisfiedLinkError("no glassgtk3"));

		String message = GraphicsStartupError.buildMessage(Kind.NATIVE_LIBRARY, failure)
				.toLowerCase(Locale.ROOT);

		assertFalse(message.contains("install gtk"),
				"must not reintroduce the misleading 'install GTK' advice: " + message);
	}

	@Test
	void buildMessage_should_separate_label_from_cause_with_a_space()
	{
		Throwable failure = new RuntimeException(new UnsatisfiedLinkError("no glassgtk3"));

		String message = GraphicsStartupError.buildMessage(Kind.NATIVE_LIBRARY, failure);

		assertTrue(message.contains("Underlying error: no glassgtk3"),
				"label and cause must be separated by a space: " + message);
	}

	@Test
	void buildMessage_for_no_display_should_mention_desktop_session()
	{
		Throwable failure = new UnsupportedOperationException("Unable to open DISPLAY");

		String message = GraphicsStartupError.buildMessage(Kind.NO_DISPLAY, failure)
				.toLowerCase(Locale.ROOT);

		assertTrue(message.contains("desktop"),
				"no-display message should advise running from a desktop session: " + message);
	}
}
