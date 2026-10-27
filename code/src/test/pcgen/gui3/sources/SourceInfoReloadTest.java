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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import pcgen.core.Campaign;
import pcgen.cdom.content.CampaignURL;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/**
 * Showing a source's info writes an HTML file and loads it into a WebView, and
 * the file write dominates the cost -- measured at roughly 1.7ms against the
 * published data, against 0.36ms to render the HTML itself. Doing it twice per
 * click would therefore be the largest avoidable cost in the pane.
 *
 * <p>Doing it twice is a real possibility, because the renderer listens to its
 * own engine. The listener exists so the WebView's built-in Reload regenerates
 * the document instead of re-showing a stale file, and it tells a user reload
 * from a render of its own with a single boolean consumed on the first
 * {@code SCHEDULED} transition. These tests pin that the renderer's own loads
 * are not mistaken for reloads.
 */
@ExtendWith(ApplicationExtension.class)
class SourceInfoReloadTest
{
	private WebEngine engine;
	private SourceInfoRenderer renderer;
	private final AtomicInteger loadsScheduled = new AtomicInteger();
	private final List<URI> openedLinks = new ArrayList<>();

	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		String key = name.replace(' ', '_');
		campaign.setSourceURI(URI.create("file:/data/test/" + key + ".pcc"));
		campaign.addToListFor(ListKey.CAMPAIGN_URL, new CampaignURL(CampaignURL.URLKind.WEBSITE,
				"Website", URI.create("https://example.org/" + key), "Website"));
		return campaign;
	}

	@BeforeEach
	void buildEngine() throws Exception
	{
		Callable<WebEngine> build = () -> {
			var newEngine = new WebEngine();
			renderer = new SourceInfoRenderer(newEngine, openedLinks::add);
			newEngine.getLoadWorker().stateProperty().addListener((_, _, state) -> {
				if (state == Worker.State.SCHEDULED)
				{
					loadsScheduled.incrementAndGet();
				}
			});
			return newEngine;
		};
		engine = asyncFx(build).get(5, TimeUnit.SECONDS);
	}

	@AfterEach
	void closeRenderer() throws Exception
	{
		asyncFx(renderer::close).get(5, TimeUnit.SECONDS);
	}

	private void render(Campaign campaign) throws Exception
	{
		asyncFx(() -> renderer.renderCampaign(campaign))
				.get(5, TimeUnit.SECONDS);
		awaitLoaded(engine);
	}

	/** Includes regenerations queued from the renderer's SCHEDULED listener. */
	private static void awaitLoaded(WebEngine preview) throws Exception
	{
		waitForFxEvents();
		waitFor(15, TimeUnit.SECONDS, () -> asyncFx(
				() -> preview.getLoadWorker().getState() == Worker.State.SUCCEEDED).get(5, TimeUnit.SECONDS));
		waitForFxEvents();
	}

	@Test
	void showingASource_loadsTheDocumentOnce() throws Exception
	{
		render(campaign("Core Rulebook"));

		assertEquals(1, loadsScheduled.get(),
				"a second load means the renderer mistook its own render for a user reload");
	}

	@Test
	void showingTwoSourcesInTurn_reusesTheFileAndLoadsEachDocumentOnce() throws Exception
	{
		render(campaign("Core Rulebook"));
		var document = asyncFx(renderer::documentFile).get(5, TimeUnit.SECONDS);

		render(campaign("Bestiary"));

		assertEquals(2, loadsScheduled.get());
		assertEquals(document, asyncFx(renderer::documentFile).get(5, TimeUnit.SECONDS),
				"changing the preview must reuse its document file");
	}

	@Test
	void showingTheSameSourceAgain_loadsTheDocumentAgainButNotMore() throws Exception
	{
		var core = campaign("Core Rulebook");

		render(core);
		render(core);

		assertEquals(2, loadsScheduled.get());
	}

	@Test
	void clearingThePane_loadsOnce() throws Exception
	{
		render(campaign("Core Rulebook"));
		loadsScheduled.set(0);

		asyncFx(renderer::clear)
				.get(5, TimeUnit.SECONDS);
		awaitLoaded(engine);

		assertEquals(1, loadsScheduled.get(), "clearing must not be taken for a reload either");
	}

	@Test
	void alternatingPreviews_should_notInstallAnotherReloadListener() throws Exception
	{
		var bestiary = campaign("Bestiary");
		var advanced = campaign("Advanced Players Guide");
		advanced.put(StringKey.DESCRIPTION, "Original preview description");
		var otherEngine = asyncFx((Callable<WebEngine>) WebEngine::new).get(5, TimeUnit.SECONDS);
		var other = asyncFx((Callable<SourceInfoRenderer>) () -> new SourceInfoRenderer(otherEngine))
				.get(5, TimeUnit.SECONDS);
		render(campaign("Core Rulebook"));
		try
		{
			asyncFx(() -> other.renderCampaign(bestiary))
					.get(5, TimeUnit.SECONDS);
			awaitLoaded(otherEngine);
			render(advanced);

			assertEquals(2, loadsScheduled.get(), "returning to a preview must not multiply its own loads");
			asyncFx(() -> engine.executeScript("document.querySelector('a').click()"))
					.get(5, TimeUnit.SECONDS);
			assertEquals(List.of(URI.create("https://example.org/Advanced_Players_Guide")), openedLinks,
					"a link must reach the browser once, without navigating the preview");

			advanced.put(StringKey.DESCRIPTION, "Refreshed preview description");
			asyncFx(engine::reload).get(5, TimeUnit.SECONDS);
			awaitLoaded(engine);
			String text = asyncFx(
					() -> engine.executeScript("document.body.textContent").toString()).get(5, TimeUnit.SECONDS);
			assertTrue(text.contains("Refreshed preview description"), text);
			assertFalse(text.contains("Original preview description"), text);
			assertEquals(4, loadsScheduled.get(), "Reload schedules itself and one deferred regeneration");
		}
		finally
		{
			asyncFx(other::close).get(5, TimeUnit.SECONDS);
		}
	}
}
