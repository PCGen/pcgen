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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

/**
 * Writing the rendered document is the expensive half of showing a source:
 * about 1.7ms against the published data, where rendering the HTML takes
 * 0.36ms. Almost all of that is opening a file, truncating it and closing it
 * again — opening and closing without writing costs 0.02ms, and truncating and
 * writing through a channel that is already open costs 0.017ms.
 *
 * <p>So the document is written through one long-lived channel, and not
 * rewritten when the bytes have not changed. Both are easy to get subtly
 * wrong: a reused channel leaves the tail of a longer previous document behind
 * unless it is truncated, and skipping work is only safe if "unchanged" really
 * means unchanged.
 */
class SourceInfoDocumentWriteTest
{
	private static String contents() throws IOException
	{
		return Files.readString(SourceInfoRenderer.sharedTempFile(), StandardCharsets.UTF_8);
	}

	@Test
	void documentIsWrittenToTheFile() throws IOException
	{
		SourceInfoRenderer.writeDocument("<html>Core Rulebook</html>");

		assertEquals("<html>Core Rulebook</html>", contents());
	}

	@Test
	void adifferentDocumentReplacesTheLastOne() throws IOException
	{
		SourceInfoRenderer.writeDocument("<html>Core Rulebook</html>");

		SourceInfoRenderer.writeDocument("<html>Bestiary</html>");

		assertEquals("<html>Bestiary</html>", contents());
	}

	/**
	 * The one a reused channel gets wrong. Writing over a longer document
	 * without truncating first leaves its tail behind, and the WebView would
	 * render the join.
	 */
	@Test
	void ashorterDocumentDoesNotLeaveTheTailOfALongerOne() throws IOException
	{
		SourceInfoRenderer.writeDocument("<html>" + "x".repeat(4000) + "</html>");

		SourceInfoRenderer.writeDocument("<html>short</html>");

		assertEquals("<html>short</html>", contents());
	}

	/**
	 * Probed by changing the file behind the renderer's back, which is the only
	 * way to see from outside that a write did not happen. Nothing does that in
	 * practice -- the file lives in a directory only this process can enter --
	 * so the staleness this demonstrates is not reachable.
	 */
	@Test
	void anUnchangedDocumentIsNotWrittenAgain() throws IOException
	{
		SourceInfoRenderer.writeDocument("<html>Core Rulebook</html>");
		Files.writeString(SourceInfoRenderer.sharedTempFile(), "untouched");

		SourceInfoRenderer.writeDocument("<html>Core Rulebook</html>");

		assertEquals("untouched", contents(), "an identical document must not be written again");
	}

	@Test
	void returningToADocumentAfterAnotherWritesItAgain() throws IOException
	{
		SourceInfoRenderer.writeDocument("<html>Core Rulebook</html>");
		SourceInfoRenderer.writeDocument("<html>Bestiary</html>");

		SourceInfoRenderer.writeDocument("<html>Core Rulebook</html>");

		assertEquals("<html>Core Rulebook</html>", contents(),
				"only the document last written may be skipped");
	}

	/** Campaign names and publishers are not all ASCII. */
	@Test
	void nonAsciiTextSurvivesTheRoundTrip() throws IOException
	{
		SourceInfoRenderer.writeDocument("<html>Pathfinder – Bestiário ©</html>");

		assertEquals("<html>Pathfinder – Bestiário ©</html>", contents());
	}

	@Test
	void anEmptyDocumentClearsTheFile() throws IOException
	{
		SourceInfoRenderer.writeDocument("<html>Core Rulebook</html>");

		SourceInfoRenderer.writeDocument("");

		assertEquals("", contents());
	}
}
