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

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import pcgen.core.Campaign;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;

/**
 * Orders campaigns so that the ones other campaigns depend on come first.
 *
 * <p>Where two campaigns exclude one another only one of them can be added, and
 * {@link CampaignAdditionPlanner} resolves such a conflict in favour of
 * whichever it is offered first. Offering them in tree order makes that an
 * arbitrary choice, and an arbitrary choice here is expensive. The Pathfinder
 * data shows why: the three {@code BOOKTYPE:MultiSet} bundles repackage the
 * individual books and carry a {@code !PRECAMPAIGN} against every Core Rules
 * book. A bundle sorts early in the tree, so in tree order it won its conflicts
 * and the books it displaced -- Bestiary 2, Bestiary 3, Monster Codex, Mythic
 * Adventures and others -- all became unsatisfiable. Selecting every Pathfinder
 * book refused twenty of them; ordering by demand brings that down to the nine
 * that genuinely conflict.
 *
 * <p>Demand is a better tie-breaker than position, and the data states it: a
 * campaign's standing here is how many of the other campaigns on offer name it
 * in a prerequisite. Bundles and alternate editions are named by nobody, so
 * they give way to the books they stand in for.
 *
 * @see "SourceSelectionDataTest, which measures this against the real data"
 */
public final class CampaignDependencyOrder
{
	/** Prefix marking a prerequisite on a named campaign rather than a book type. */
	private static final String INCLUDES = "INCLUDES=";

	private CampaignDependencyOrder()
	{
	}

	/**
	 * Orders candidates by how many of them depend on each, most first.
	 *
	 * <p>The sort is stable, so campaigns that nothing distinguishes keep the
	 * order the user sees them in.
	 *
	 * @param candidates the campaigns on offer, in display order
	 * @return the same campaigns, most depended upon first
	 */
	public static List<Campaign> mostDependedUponFirst(List<Campaign> candidates)
	{
		Map<String, Integer> demand = new HashMap<>();
		for (Campaign candidate : candidates)
		{
			for (String required : requiredCampaignNames(candidate))
			{
				if (!required.equalsIgnoreCase(candidate.getKeyName()))
				{
					demand.merge(key(required), 1, Integer::sum);
				}
			}
		}
		return candidates.stream()
				.sorted(Comparator.comparingInt(
						(Campaign campaign) -> demand.getOrDefault(key(campaign.getKeyName()), 0))
						.reversed())
				.toList();
	}

	/**
	 * The campaigns this one requires by name, counted once each however many
	 * times they are mentioned.
	 */
	private static Set<String> requiredCampaignNames(Campaign campaign)
	{
		Set<String> names = new LinkedHashSet<>();
		campaign.getPrerequisiteList().forEach(prereq -> collectRequired(prereq, names));
		return names;
	}

	private static void collectRequired(Prerequisite prereq, Set<String> names)
	{
		if (isNegated(prereq.getOperator()))
		{
			// An exclusion names a campaign in order to refuse it. Crediting
			// that as demand would give an alternate edition exactly the
			// standing of the book it replaces -- the tie being broken here.
			return;
		}
		var name = requiredName(prereq);
		if (name != null)
		{
			names.add(name);
		}
		prereq.getPrerequisites().forEach(child -> collectRequired(child, names));
	}

	/**
	 * The campaign named by a prerequisite, or {@code null} if it names none.
	 * {@code BOOKTYPE=} and {@code INCLUDESBOOKTYPE=} ask for any book of a
	 * type, so they credit no individual campaign.
	 */
	private static String requiredName(Prerequisite prereq)
	{
		if (!"campaign".equalsIgnoreCase(prereq.getKind()))
		{
			return null;
		}
		var key = prereq.getKey();
		if (key.startsWith(INCLUDES))
		{
			return key.substring(INCLUDES.length());
		}
		return key.contains("=") ? null : key;
	}

	private static boolean isNegated(PrerequisiteOperator operator)
	{
		return operator == PrerequisiteOperator.LT
				|| operator == PrerequisiteOperator.LTEQ
				|| operator == PrerequisiteOperator.NEQ;
	}

	private static String key(String campaignName)
	{
		return campaignName.toLowerCase(Locale.ROOT);
	}
}
