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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

/**
 * A campaign's Website, Purchase and Survey entries are rendered as real
 * anchors in the Source Info pane. Followed inside the pane they would replace
 * the document with a web page, leaving the pane showing something it has no
 * way back from; the legacy Swing pane hands them to the system browser
 * instead.
 *
 * <p>What may be handed over needs deciding rather than assuming. The URLs come
 * from {@code .pcc} files, which are hand-authored data PCGen does not control,
 * and passing an arbitrary string to the desktop browser launcher is passing it
 * to the operating system's handler for whatever scheme it names.
 */
class SourceInfoLinkTargetTest
{
	@Test
	void httpLink_isOpenedInTheBrowser()
	{
		assertEquals(URI.create("http://paizo.com"),
				SourceInfoLinks.externalTarget("http://paizo.com").orElseThrow());
	}

	@Test
	void httpsLink_isOpenedInTheBrowser()
	{
		assertEquals(URI.create("https://paizo.com/store"),
				SourceInfoLinks.externalTarget("https://paizo.com/store").orElseThrow());
	}

	/**
	 * The data is hand-authored and the launcher hands whatever it is given to
	 * the operating system, so only the two schemes a source link is meant to
	 * use are passed on.
	 */
	@Test
	void javascriptUrl_isNotOpened()
	{
		assertTrue(SourceInfoLinks.externalTarget("javascript:alert(1)").isEmpty());
	}

	@Test
	void fileUrl_isNotOpened()
	{
		assertTrue(SourceInfoLinks.externalTarget("file:///etc/passwd").isEmpty(),
				"a document link must not be able to launch a local file");
	}

	@Test
	void unknownScheme_isNotOpened()
	{
		assertTrue(SourceInfoLinks.externalTarget("ftp://example.com/x").isEmpty());
	}

	/** Relative hrefs resolve against the data directory and are not web links. */
	@Test
	void relativeLink_isNotOpened()
	{
		assertTrue(SourceInfoLinks.externalTarget("pathfinder/cover.png").isEmpty());
	}

	@Test
	void anchorWithinTheDocument_isNotOpened()
	{
		assertTrue(SourceInfoLinks.externalTarget("#requirements").isEmpty());
	}

	@Test
	void missingHref_isNotOpened()
	{
		assertTrue(SourceInfoLinks.externalTarget("").isEmpty());
		assertTrue(SourceInfoLinks.externalTarget("   ").isEmpty());
	}

	/** A malformed href is data, not a programming error, and must not throw. */
	@Test
	void malformedHref_isNotOpened()
	{
		assertTrue(SourceInfoLinks.externalTarget("http://exa mple.com/ ]").isEmpty());
	}

	@Test
	void schemeIsMatchedIgnoringCase()
	{
		assertTrue(SourceInfoLinks.externalTarget("HTTPS://paizo.com").isPresent());
	}

	@Test
	void surroundingWhitespace_isIgnored()
	{
		assertEquals(URI.create("https://paizo.com"),
				SourceInfoLinks.externalTarget("  https://paizo.com  ").orElseThrow());
	}

	/** Captures what the class reports, so "ignored" can be told from "ignored silently". */
	private static final class Captured implements AutoCloseable
	{
		private final Logger logger = Logger.getLogger(SourceInfoLinks.class.getName());
		private final List<String> messages = new ArrayList<>();
		private final Handler handler = new Handler()
		{
			@Override public void publish(LogRecord record)
			{
				messages.add(record.getMessage());
			}

			@Override public void flush() { }

			@Override public void close() { }
		};

		Captured()
		{
			logger.addHandler(handler);
		}

		@Override public void close()
		{
			logger.removeHandler(handler);
		}
	}

	private static List<String> reportsFor(String href)
	{
		try (var captured = new Captured())
		{
			SourceInfoLinks.externalTarget(href);
			return List.copyOf(captured.messages);
		}
	}

	/**
	 * A link PCGen refuses to open does nothing when clicked, since the click
	 * is cancelled either way. Without a word in the log, a publisher with a
	 * bad URL in their data has no way to discover it.
	 */
	@Test
	void malformedLink_isReported()
	{
		var reports = reportsFor("http://exa mple.com/ ]");

		assertEquals(1, reports.size(), reports.toString());
		assertTrue(reports.getFirst().contains("exa mple.com"), reports.getFirst());
	}

	@Test
	void refusedScheme_isReported_sayingWhichScheme()
	{
		var reports = reportsFor("javascript:alert(1)");

		assertEquals(1, reports.size(), reports.toString());
		assertTrue(reports.getFirst().contains("javascript"), reports.getFirst());
	}

	/**
	 * An anchor with no target, a fragment, or a path relative to the data
	 * directory are ordinary HTML, not faults. Reporting them would bury the
	 * two cases above in noise.
	 */
	@Test
	void ordinaryNonWebLinks_areNotReported()
	{
		assertEquals(List.of(), reportsFor(""), "an anchor with no target");
		assertEquals(List.of(), reportsFor("#requirements"), "a link within the document");
		assertEquals(List.of(), reportsFor("pathfinder/cover.png"), "a path under the data dir");
	}

	@Test
	void aLinkThatOpens_isNotReported()
	{
		assertEquals(List.of(), reportsFor("https://paizo.com"));
	}
}
