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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import pcgen.core.Campaign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

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
	private final AtomicInteger loadsScheduled = new AtomicInteger();

	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		campaign.setSourceURI(java.net.URI.create("file:/data/test/" + name.replace(' ', '_') + ".pcc"));
		return campaign;
	}

	@BeforeEach
	void buildEngine() throws Exception
	{
		Callable<WebEngine> build = () -> {
			var newEngine = new WebEngine();
			newEngine.getLoadWorker().stateProperty().addListener((_, _, state) -> {
				if (state == Worker.State.SCHEDULED)
				{
					loadsScheduled.incrementAndGet();
				}
			});
			return newEngine;
		};
		engine = WaitForAsyncUtils.asyncFx(build).get(5, TimeUnit.SECONDS);
	}

	private void render(Campaign campaign) throws Exception
	{
		WaitForAsyncUtils.asyncFx(() -> SourceInfoRenderer.renderCampaign(engine, campaign))
				.get(5, TimeUnit.SECONDS);
		settle();
	}

	/** Lets any deferred regeneration run, so a second load would be counted. */
	private static void settle()
	{
		WaitForAsyncUtils.waitForFxEvents();
		WaitForAsyncUtils.sleep(300, TimeUnit.MILLISECONDS);
		WaitForAsyncUtils.waitForFxEvents();
	}

	@Test
	void showingASource_loadsTheDocumentOnce() throws Exception
	{
		render(campaign("Core Rulebook"));

		assertEquals(1, loadsScheduled.get(),
				"a second load means the renderer mistook its own render for a user reload");
	}

	@Test
	void showingTwoSourcesInTurn_loadsTheDocumentTwice() throws Exception
	{
		render(campaign("Core Rulebook"));
		render(campaign("Bestiary"));

		assertEquals(2, loadsScheduled.get());
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

		WaitForAsyncUtils.asyncFx(() -> SourceInfoRenderer.renderCampaign(engine, null))
				.get(5, TimeUnit.SECONDS);
		settle();

		assertEquals(1, loadsScheduled.get(), "clearing must not be taken for a reload either");
	}
}
