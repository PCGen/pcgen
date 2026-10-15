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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

/**
 * Sends the links in the Source Info pane to the system browser.
 *
 * <p>A campaign's Website, Purchase and Survey entries are rendered as real
 * anchors. Followed inside the pane they would replace the document with a web
 * page and leave the pane with no way back -- the renderer only ever reloads
 * its own file. The legacy Swing pane hands them to the browser, and so does
 * this.
 *
 * <p>Only {@code http} and {@code https} are handed over. The URLs come from
 * {@code .pcc} files, which are hand-authored data PCGen does not control, and
 * the browser launcher passes what it is given to the operating system's
 * handler for whatever scheme it names.
 */
final class SourceInfoLinks
{
	private static final Logger LOG = Logger.getLogger(SourceInfoLinks.class.getName());

	/** The schemes a source link is meant to use. */
	private static final Set<String> BROWSABLE = Set.of("http", "https");

	private SourceInfoLinks()
	{
	}

	/**
	 * The browser-openable address {@code href} names, if it names one.
	 *
	 * <p>Three quite different things end up here and only two are faults. An
	 * anchor with no target, a link within the document, and a path relative to
	 * the data directory are ordinary HTML, and are passed over in silence; a
	 * malformed address and one naming a scheme PCGen will not open are
	 * problems in hand-authored data, and are reported. Since the click is
	 * cancelled either way, a refused link simply does nothing when pressed,
	 * and without a line in the log the author of the data has no way to find
	 * out why.
	 *
	 * @param href the anchor's href, as written in the data
	 * @return the address to open, or empty if there is nothing to open
	 */
	static Optional<URI> externalTarget(String href)
	{
		String address = href == null ? "" : href.trim();
		if (address.isEmpty())
		{
			return Optional.empty();
		}
		URI uri;
		try
		{
			uri = new URI(address);
		}
		catch (URISyntaxException _)
		{
			LOG.warning(() -> "Ignoring a malformed source link: " + address);
			return Optional.empty();
		}
		String scheme = uri.getScheme();
		if (scheme == null)
		{
			// Relative to the document's base href, or a position within it.
			return Optional.empty();
		}
		if (!BROWSABLE.contains(scheme.toLowerCase(Locale.ROOT)))
		{
			LOG.warning(() -> "Refusing to open a source link using the " + scheme + " scheme: " + address);
			return Optional.empty();
		}
		return Optional.of(uri);
	}

	/**
	 * Makes the links in {@code engine}'s documents open in a browser instead
	 * of in the pane.
	 *
	 * <p>Listeners are attached to the anchors once each document has loaded.
	 * Watching the engine's location instead would be simpler but too late:
	 * by the time it changes the pane has already begun leaving the document
	 * behind.
	 *
	 * @param engine the engine showing the source info
	 * @param browser opens an address, normally the desktop browser launcher
	 */
	static void install(WebEngine engine, Consumer<URI> browser)
	{
		engine.getLoadWorker().stateProperty().addListener((_, _, state) -> {
			if (state == Worker.State.SUCCEEDED)
			{
				interceptAnchors(engine.getDocument(), browser);
			}
		});
	}

	/**
	 * Attaches the click handlers to {@code document}'s anchors.
	 *
	 * <p>Called only once the load worker reports {@code SUCCEEDED}, where a
	 * loaded document is not something to hedge on. Returning quietly on a null
	 * one would leave every link in the pane dead and say nothing about why,
	 * whereas the thrown {@code NullPointerException} reaches PCGen's
	 * uncaught-exception handler and the log. It costs the pane nothing:
	 * JavaFX invokes property listeners individually and routes a throw to that
	 * handler, so the renderer's reload interceptor still runs.
	 *
	 * @param document the loaded document
	 * @param browser opens an address
	 */
	static void interceptAnchors(Document document, Consumer<URI> browser)
	{
		Objects.requireNonNull(document, "document was not available after a successful load");
		var anchors = document.getElementsByTagName("a");
		for (int i = 0; i < anchors.getLength(); i++)
		{
			if (anchors.item(i) instanceof EventTarget anchor)
			{
				anchor.addEventListener("click", event -> {
					// Stop the pane following the link whatever the address
					// turns out to be: an address not worth opening is still
					// not worth navigating to.
					event.preventDefault();
					if (event.getCurrentTarget() instanceof Element element)
					{
						externalTarget(element.getAttribute("href")).ifPresent(browser);
					}
				}, false);
			}
		}
	}
}
