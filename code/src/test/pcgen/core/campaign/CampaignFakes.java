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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import pcgen.core.Campaign;

/** Shared engine stand-ins for the campaign selection tests, without touching {@code Globals}. */
final class CampaignFakes
{
	private CampaignFakes()
	{
	}

	static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		return campaign;
	}

	/** Models the {@code PCC:} lines of a collection, without touching {@code Globals}. */
	static final class FakeContents implements Function<Campaign, List<Campaign>>
	{
		private final Map<Campaign, List<Campaign>> contents = new IdentityHashMap<>();

		void contains(Campaign container, Campaign... contained)
		{
			contents.put(container, List.of(contained));
		}

		@Override
		public List<Campaign> apply(Campaign campaign)
		{
			return contents.getOrDefault(campaign, Collections.emptyList());
		}
	}

	/**
	 * A stand-in for the engine's prerequisite check, with the same whole-set
	 * semantics as {@code FacadeFactory.findUnqualified}: a campaign is refused
	 * when a campaign it requires is absent, a campaign it excludes is present,
	 * or it can never qualify.
	 */
	static final class FakePrereqs implements UnaryOperator<List<Campaign>>
	{
		private final Map<String, Set<String>> requirements = new HashMap<>();
		private final Map<String, Set<String>> exclusions = new HashMap<>();
		private final Set<String> alwaysUnqualified = new HashSet<>();
		final List<List<Campaign>> calls = new ArrayList<>();

		/** Models {@code PRECAMPAIGN}: {@code campaign} is refused if any of these is absent. */
		void require(String campaign, String... required)
		{
			requirements.put(campaign, Set.of(required));
		}

		/** Models {@code !PRECAMPAIGN}: {@code campaign} is refused if any of these is present. */
		void exclude(String campaign, String... excluded)
		{
			exclusions.put(campaign, Set.of(excluded));
		}

		/** Models a campaign that cannot qualify whatever else is present. */
		void neverQualifies(String campaign)
		{
			alwaysUnqualified.add(campaign);
		}

		@Override
		public List<Campaign> apply(List<Campaign> candidates)
		{
			calls.add(List.copyOf(candidates));
			var present = candidates.stream().map(Campaign::getDisplayName).collect(Collectors.toSet());
			return candidates.stream()
					.filter(c -> {
						var name = c.getDisplayName();
						var missingRequirement = !present.containsAll(
								requirements.getOrDefault(name, Set.of()));
						var blocked = exclusions.getOrDefault(name, Set.of()).stream()
								.anyMatch(present::contains);
						return alwaysUnqualified.contains(name) || missingRequirement || blocked;
					})
					.toList();
		}
	}
}
