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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/** Checks cover and metadata layout in a shown WebView at different preview widths. */
@ExtendWith(ApplicationExtension.class)
class SourceInfoLayoutTest
{
	private Stage stage;
	private WebView view;

	@Start
	void start(Stage window)
	{
		stage = window;
		view = new WebView();
		stage.setScene(new Scene(new StackPane(view), 800, 600));
		stage.show();
	}

	@AfterEach
	void close() throws Exception
	{
		asyncFx(() -> stage.hide()).get(5, TimeUnit.SECONDS);
	}

	@ParameterizedTest
	@ValueSource(ints = {280, 360, 500, 800})
	void metadata_should_remainReadableBesideCover(int width, @TempDir Path directory) throws Exception
	{
		LanguageBundle.getString("");
		Path cover = directory.resolve("cover.png");
		ImageIO.write(new BufferedImage(600, 800, BufferedImage.TYPE_INT_RGB), "png", cover.toFile());
		var campaign = new Campaign();
		campaign.setName("Pathfinder Core Rulebook");
		campaign.setSourceURI(directory.resolve("source.pcc").toUri());
		campaign.put(StringKey.SOURCE_LONG, "Pathfinder Roleplaying Game Core Rulebook");
		campaign.put(StringKey.PUB_NAME_LONG, "Paizo Publishing");
		campaign.addToListFor(ListKey.GAME_MODE, "Pathfinder");
		campaign.addToListFor(ListKey.FILE_COVER, new CampaignSourceEntry(campaign, cover.toUri()));
		Path html = directory.resolve("preview.html");
		Files.writeString(html, ModernHtmlInfoBuilder.forCampaign(campaign, directory));
		asyncFx(() -> {
			view.setMinWidth(width);
			view.setMaxWidth(width);
			view.getEngine().load(html.toUri().toString());
		}).get(5, TimeUnit.SECONDS);
		waitFor(10, TimeUnit.SECONDS, () -> asyncFx(() ->
				view.getEngine().getLoadWorker().getState() == Worker.State.SUCCEEDED
						&& Boolean.TRUE.equals(view.getEngine().executeScript(
						"innerWidth == " + width + " && document.querySelector('.cover').naturalWidth > 0"))).get());

		assertTrue(evaluate("""
				const cover = document.querySelector('.cover').getBoundingClientRect();
				const facts = document.querySelector('.facts').getBoundingClientRect();
				facts.top < cover.bottom && facts.left >= cover.right
				"""), "Metadata should start beside the cover at width " + width);
		assertTrue(evaluate("""
				Array.from(document.querySelectorAll('.facts dd')).every(element => {
				    const bounds = element.getBoundingClientRect();
				    return bounds.width >= 90 && bounds.right <= innerWidth;
				})
				"""), "Values need readable columns within the preview at width " + width);
	}

	/** Evaluates a layout assertion on the JavaFX thread after the document has loaded. */
	private boolean evaluate(String script) throws Exception
	{
		return asyncFx(() -> (Boolean) view.getEngine().executeScript(script)).get(5, TimeUnit.SECONDS);
	}
}
