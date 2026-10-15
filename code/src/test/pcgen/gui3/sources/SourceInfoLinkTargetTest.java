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
		assertTrue(SourceInfoLinks.externalTarget(null).isEmpty());
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

	/**
	 * The renderer only intercepts anchors once the load worker reports
	 * SUCCEEDED, at which point a loaded document is not an assumption the
	 * code is entitled to hedge on. Returning quietly on a null one would turn
	 * every link in the pane dead with nothing said about why; a thrown NPE
	 * reaches PCGen's uncaught-exception handler and the log.
	 *
	 * <p>It does not take the pane with it: JavaFX invokes property listeners
	 * individually and routes a throw to the uncaught-exception handler, so the
	 * renderer's reload interceptor still runs.
	 */
	@Test
	void aMissingDocument_isNotHiddenBehindADeadPane()
	{
		var thrown = org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
				() -> SourceInfoLinks.interceptAnchors(null, _ -> { }));

		assertTrue(thrown.getMessage() != null && thrown.getMessage().contains("document"),
				"the message has to say what was missing: " + thrown.getMessage());
	}
}
