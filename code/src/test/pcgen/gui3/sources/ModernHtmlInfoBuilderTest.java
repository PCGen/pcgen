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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Status;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for {@link ModernHtmlInfoBuilder}'s pure helpers: {@code imageSrc}
 * (the {@code <img src>} path-resolution logic) and {@code escape}.
 */
class ModernHtmlInfoBuilderTest
{
	@Test
	void imageSrc_should_returnEmpty_when_uriIsNull(@TempDir Path dataDir)
	{
		assertEquals("", ModernHtmlInfoBuilder.imageSrc(null, dataDir));
	}

	@Test
	void imageSrc_should_passThroughAbsoluteUrl_when_schemeIsNotFile(@TempDir Path dataDir)
			throws URISyntaxException
	{
		URI http = new URI("https://example.com/cover.png");
		assertEquals("https://example.com/cover.png", ModernHtmlInfoBuilder.imageSrc(http, dataDir));
	}

	@Test
	void imageSrc_should_returnPathRelativeToDataDir_when_imageIsUnderIt(@TempDir Path dataDir)
	{
		URI img = dataDir.resolve("pathfinder").resolve("cover.jpeg").toUri();
		assertEquals("pathfinder/cover.jpeg", ModernHtmlInfoBuilder.imageSrc(img, dataDir));
	}

	@Test
	void imageSrc_should_returnFileNameOnly_when_imageIsDirectlyInDataDir(@TempDir Path dataDir)
	{
		URI img = dataDir.resolve("logo.png").toUri();
		assertEquals("logo.png", ModernHtmlInfoBuilder.imageSrc(img, dataDir));
	}

	@Test
	void imageSrc_should_urlEncodeSpaces_when_pathHasSpaces(@TempDir Path dataDir)
	{
		URI img = dataDir.resolve("paizo logos").resolve("my cover.png").toUri();
		assertEquals("paizo%20logos/my%20cover.png", ModernHtmlInfoBuilder.imageSrc(img, dataDir));
	}

	@Test
	void imageSrc_should_returnAbsoluteUri_when_imageIsOutsideDataDir(@TempDir Path dataDir)
	{
		// A sibling of dataDir is not under it, so the absolute file: URL is kept.
		URI outside = dataDir.getParent().resolve("vendor-cover.png").toUri();
		assertEquals(outside.toString(), ModernHtmlInfoBuilder.imageSrc(outside, dataDir));
	}

	@Test
	void escape_should_returnEmpty_when_null()
	{
		assertEquals("", ModernHtmlInfoBuilder.escape(null));
	}

	@Test
	void escape_should_escapeHtmlMetacharacters()
	{
		assertEquals("Swords &amp; Sorcery &lt;b&gt;bold&lt;/b&gt;",
				ModernHtmlInfoBuilder.escape("Swords & Sorcery <b>bold</b>"));
	}

	@Test
	void escape_should_leaveOrdinaryTextUnchanged()
	{
		assertEquals("Pathfinder for Game Masters",
				ModernHtmlInfoBuilder.escape("Pathfinder for Game Masters"));
	}

	/**
	 * End-to-end model + template render. Builds a minimal {@link Campaign}
	 * in memory (no data files) and asserts the produced HTML carries the
	 * card's structural pieces.
	 */
	@Nested
	class ForCampaign
	{
		private Campaign minimalCampaign()
		{
			Campaign campaign = new Campaign();
			campaign.setName("Test Source");
			campaign.setSourceURI(URI.create("file:/data/test/source.pcc"));
			campaign.put(ObjectKey.STATUS, Status.Beta);
			campaign.put(StringKey.DESCRIPTION, "A brave adventure.");
			campaign.put(IntegerKey.CAMPAIGN_RANK, 42);
			return campaign;
		}

		@Test
		void rendersFullHtmlDocumentWithTitle(@TempDir Path dataDir) throws IOException
		{
			String html = ModernHtmlInfoBuilder.forCampaign(minimalCampaign(), dataDir);
			assertTrue(html.startsWith("<!DOCTYPE html>"), "should be a full document");
			assertTrue(html.contains("<h1>Test Source</h1>"), "title in h1");
		}

		@Test
		void rendersStatusPillWithLabelAndColor(@TempDir Path dataDir) throws IOException
		{
			String html = ModernHtmlInfoBuilder.forCampaign(minimalCampaign(), dataDir);
			assertTrue(html.contains("class=\"pill\""), "status pill present");
			assertTrue(html.contains(">Beta</span>"), "pill shows the status label");
		}

		@Test
		void rendersRankFact(@TempDir Path dataDir) throws IOException
		{
			String html = ModernHtmlInfoBuilder.forCampaign(minimalCampaign(), dataDir);
			assertTrue(html.contains("<dl class=\"facts\">"), "facts grid present");
			assertTrue(html.contains("42"), "rank value rendered");
		}

		@Test
		void rendersDescriptionText(@TempDir Path dataDir) throws IOException
		{
			String html = ModernHtmlInfoBuilder.forCampaign(minimalCampaign(), dataDir);
			assertTrue(html.contains("A brave adventure."), "description rendered");
		}

		@Test
		void escapesHtmlInPlainTextFields(@TempDir Path dataDir) throws IOException
		{
			Campaign campaign = minimalCampaign();
			campaign.put(StringKey.DESCRIPTION, "Danger <script>alert(1)</script>");
			String html = ModernHtmlInfoBuilder.forCampaign(campaign, dataDir);
			assertTrue(html.contains("Danger &lt;script&gt;"), "description is HTML-escaped");
			assertFalse(html.contains("<script>alert(1)</script>"), "no raw script tag leaks through");
		}

		@Test
		void emptyDescriptionOmitsDescriptionBlock(@TempDir Path dataDir) throws IOException
		{
			Campaign campaign = new Campaign();
			campaign.setName("Bare");
			campaign.setSourceURI(URI.create("file:/data/test/bare.pcc"));
			String html = ModernHtmlInfoBuilder.forCampaign(campaign, dataDir);
			assertTrue(html.contains("<h1>Bare</h1>"), "still renders the title");
			assertFalse(html.contains("class=\"description\""), "no description block when unset");
		}
	}
}
