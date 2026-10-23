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
package pcgen.core.campaign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreCampaignParser;
import plugin.pretokens.parser.PreMultParser;

/**
 * Unit tests for {@link CampaignDependencyOrder}.
 *
 * <p>When two campaigns exclude one another only one can be added, so the
 * choice has to be made on something better than the order they happen to
 * appear in the tree. Choosing badly is expensive: a MultiSet bundle that
 * nothing depends on can displace a book that a dozen others require, taking
 * all of them down with it.
 *
 * <p>The prerequisites here are parsed from real {@code PRECAMPAIGN} strings
 * taken from the Pathfinder data, so the tests exercise the structures the
 * parser actually produces rather than a guess at them. They use two editions
 * of one book as the clearest illustration of a tie that needs breaking; the
 * effect of getting it wrong on the published data is measured by
 * {@code SourceSelectionDataTest}.
 */
class CampaignDependencyOrderTest
{
	@BeforeEach
	void registerPrereqParsers() throws PersistenceLayerException
	{
		TokenRegistration.clearTokens();
		TokenRegistration.register(new PreCampaignParser());
		TokenRegistration.register(new PreMultParser());
	}

	private static Campaign campaign(String name, String... prerequisites)
			throws PersistenceLayerException
	{
		var campaign = new Campaign();
		campaign.setName(name);
		var factory = PreParserFactory.getInstance();
		for (String prerequisite : prerequisites)
		{
			campaign.addPrerequisite(factory.parse(prerequisite));
		}
		return campaign;
	}

	private static List<String> names(List<Campaign> campaigns)
	{
		return campaigns.stream().map(Campaign::getDisplayName).toList();
	}

	/** The book a dozen others name must be offered before an edition nobody names. */
	@Test
	void order_should_putTheBookOthersDependOn_beforeOneNobodyNeeds() throws Exception
	{
		var pfs = campaign("Core Rulebook (PFS)", "!PRECAMPAIGN:1,BOOKTYPE=Core Rules,[PFS ~ Core Rulebook]");
		var core = campaign("Core Rulebook", "!PRECAMPAIGN:1,BOOKTYPE=Core Rules,[Core Rulebook]");
		var apg = campaign("Advanced Player's Guide", "PRECAMPAIGN:1,INCLUDES=Core Rulebook");
		var bestiary = campaign("Bestiary", "PRECAMPAIGN:1,INCLUDES=Core Rulebook");

		var ordered = CampaignDependencyOrder.mostDependedUponFirst(List.of(pfs, core, apg, bestiary));

		assertEquals("Core Rulebook", names(ordered).get(0),
				"the book others require must be tried first, whatever the tree order");
	}

	/**
	 * An exclusion names a campaign in order to refuse it. Counting that as a
	 * dependency would give every alternate edition exactly the weight of the
	 * book it replaces, which is precisely the tie that needs breaking.
	 */
	@Test
	void order_should_notTreatAnExclusionAsADependency() throws Exception
	{
		var full = campaign("Bestiary", "PRECAMPAIGN:1,INCLUDES=Core Rulebook");
		var reduced = campaign("Bestiary ~ Player Options Only", "!PRECAMPAIGN:1,INCLUDES=Bestiary");
		var core = campaign("Core Rulebook");

		var ordered = CampaignDependencyOrder.mostDependedUponFirst(List.of(reduced, full, core));

		assertEquals(List.of("Core Rulebook", "Bestiary ~ Player Options Only", "Bestiary"), names(ordered),
				"only Core Rulebook is genuinely required; the exclusion must not promote Bestiary");
	}

	/** Books of equal standing must keep the order the user sees them in. */
	@Test
	void order_should_keepDisplayOrder_forBooksOfEqualStanding() throws Exception
	{
		var alpha = campaign("Alpha");
		var beta = campaign("Beta");
		var gamma = campaign("Gamma");

		var ordered = CampaignDependencyOrder.mostDependedUponFirst(List.of(gamma, alpha, beta));

		assertEquals(List.of("Gamma", "Alpha", "Beta"), names(ordered));
	}

	/**
	 * {@code BOOKTYPE=} asks for any book of a type rather than a named one, so
	 * it cannot be credited to a particular campaign.
	 */
	@Test
	void order_should_ignoreRequirementsOnABookTypeRatherThanABook() throws Exception
	{
		var core = campaign("Core Rulebook");
		var psionics = campaign("Ultimate Psionics", "PRECAMPAIGN:1,INCLUDESBOOKTYPE=Core Rules");

		var ordered = CampaignDependencyOrder.mostDependedUponFirst(List.of(core, psionics));

		assertEquals(List.of("Core Rulebook", "Ultimate Psionics"), names(ordered),
				"a book-type requirement credits no individual book, so display order stands");
	}

	/** A campaign naming itself must not inflate its own standing. */
	@Test
	void order_should_ignoreACampaignsReferenceToItself() throws Exception
	{
		var selfish = campaign("Selfish", "PRECAMPAIGN:1,INCLUDES=Selfish");
		var needed = campaign("Needed");
		var dependent = campaign("Dependent", "PRECAMPAIGN:1,INCLUDES=Needed");

		var ordered = CampaignDependencyOrder.mostDependedUponFirst(List.of(selfish, needed, dependent));

		assertEquals("Needed", names(ordered).get(0),
				"a self-reference is not demand from another book");
	}

	@Test
	void order_should_matchDependenciesByKey_when_displayNameDiffers() throws Exception
	{
		var unrelated = campaign("Unrelated");
		var reduced = campaign("Bestiary 3 (Player Options Only)");
		reduced.setKeyName("Bestiary 3 ~ Player Options Only");
		var dependent = campaign("Monster Codex",
				"PRECAMPAIGN:1,INCLUDES=Bestiary 3 ~ Player Options Only");

		var ordered = CampaignDependencyOrder.mostDependedUponFirst(List.of(unrelated, reduced, dependent));

		assertEquals(List.of("Bestiary 3 (Player Options Only)", "Unrelated", "Monster Codex"), names(ordered));
	}

	@Test
	void order_should_ignoreSelfReferencesByKey_when_displayNameDiffers() throws Exception
	{
		var selfish = campaign("Selfish Label", "PRECAMPAIGN:1,INCLUDES=Selfish Key");
		selfish.setKeyName("Selfish Key");
		var needed = campaign("Needed");
		var dependent = campaign("Dependent", "PRECAMPAIGN:1,INCLUDES=Needed");

		var ordered = CampaignDependencyOrder.mostDependedUponFirst(List.of(selfish, needed, dependent));

		assertEquals("Needed", names(ordered).getFirst());
	}

	@Test
	void order_should_notCreditAReference_whoseCaseDoesNotMatchTheCampaignKey() throws Exception
	{
		var unrelated = campaign("Unrelated");
		var core = campaign("Core Rulebook");
		var dependent = campaign("Supplement", "PRECAMPAIGN:1,INCLUDES=core rulebook");

		var ordered = CampaignDependencyOrder.mostDependedUponFirst(List.of(unrelated, core, dependent));

		assertEquals(List.of("Unrelated", "Core Rulebook", "Supplement"), names(ordered),
				"only an exact campaign-key reference contributes dependency demand");
	}

	@Test
	void caseMismatches_should_reportNestedReferences_usingTheDeclaredKeyAndSource() throws Exception
	{
		var source = campaign("Supplement",
				"PREMULT:1,[PRECAMPAIGN:1,INCLUDES=core rulebook],[PRECAMPAIGN:1,BOOKTYPE=Core Rules]");
		source.setSourceURI(URI.create("file:/homebrew/supplement.pcc"));
		var core = campaign("Core Display Name");
		core.setKeyName("Core Rulebook");

		var mismatches = CampaignDependencyOrder.caseMismatches(List.of(source, core));

		assertEquals(List.of(new CampaignDependencyOrder.CaseMismatch(
				URI.create("file:/homebrew/supplement.pcc"), "core rulebook", "Core Rulebook")), mismatches);
	}

	@Test
	void caseMismatches_should_reportARepeatedReferenceOnlyOncePerSource() throws Exception
	{
		var source = campaign("Supplement",
				"PRECAMPAIGN:1,INCLUDES=core rulebook", "PRECAMPAIGN:1,core rulebook");
		source.setSourceURI(URI.create("file:/homebrew/supplement.pcc"));

		var mismatches = CampaignDependencyOrder.caseMismatches(List.of(source, campaign("Core Rulebook")));

		assertEquals(1, mismatches.size());
	}

	@Test
	void caseMismatches_should_notWarnWhenAnExactKeyExistsAlongsideACaseVariant() throws Exception
	{
		var source = campaign("Supplement", "PRECAMPAIGN:1,INCLUDES=core rulebook");
		source.setSourceURI(URI.create("file:/homebrew/supplement.pcc"));

		var mismatches = CampaignDependencyOrder.caseMismatches(
				List.of(source, campaign("Core Rulebook"), campaign("core rulebook")));

		assertTrue(mismatches.isEmpty(), "a reference that already receives strict dependency credit is not a mismatch");
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"PRECAMPAIGN:1,INCLUDES=Core Rulebook",
			"PRECAMPAIGN:1,Core Rulebook",
			"PRECAMPAIGN:1,INCLUDES=Not Installed",
			"PRECAMPAIGN:1,BOOKTYPE=core rulebook",
			"PRECAMPAIGN:1,INCLUDESBOOKTYPE=core rulebook",
			"!PRECAMPAIGN:1,INCLUDES=core rulebook"
	})
	void caseMismatches_should_onlyReportCaseDifferencesAffectingNamedDependencies(String prerequisite)
			throws Exception
	{
		var source = campaign("Supplement", prerequisite);
		source.setSourceURI(URI.create("file:/homebrew/supplement.pcc"));

		assertTrue(CampaignDependencyOrder.caseMismatches(List.of(source, campaign("Core Rulebook"))).isEmpty());
	}
}
