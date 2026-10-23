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

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
	 * A reference whose spelling differs only in case from a declared campaign key.
	 *
	 * @param source the PCC file containing the prerequisite
	 * @param reference the referenced key as written in the prerequisite
	 * @param declaredKey the spelling declared by the referenced campaign
	 */
	public record CaseMismatch(URI source, String reference, String declaredKey)
	{
	}

	/**
	 * Orders candidates by how many of them depend on each, most first.
	 *
	 * <p>The sort is stable, so campaigns that nothing distinguishes keep the
	 * order the user sees them in. References must match declared campaign keys
	 * exactly, including case, to contribute demand.
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
				if (!required.equals(candidate.getKeyName()))
				{
					demand.merge(required, 1, Integer::sum);
				}
			}
		}
		return candidates.stream()
				.sorted(Comparator.comparingInt(
						(Campaign campaign) -> demand.getOrDefault(campaign.getKeyName(), 0))
						.reversed())
				.toList();
	}

	/**
	 * Finds positive named dependencies that strict scoring ignores because their case differs.
	 *
	 * <p>Call after all campaign metadata is loaded so forward references can
	 * resolve. Repeated references in one source are reported once. Unresolved
	 * names, exclusions and book-type conditions are not case mismatches in
	 * dependency scoring and are left to their existing validation paths.
	 *
	 * @param campaigns all loaded campaigns, used as both sources and reference targets
	 * @return the mismatches and their source locations, without changing data or logging
	 */
	public static List<CaseMismatch> caseMismatches(List<Campaign> campaigns)
	{
		List<String> declaredKeys = campaigns.stream().map(Campaign::getKeyName).toList();
		List<CaseMismatch> mismatches = new ArrayList<>();
		for (Campaign source : campaigns)
		{
			for (String reference : requiredCampaignNames(source))
			{
				if (!declaredKeys.contains(reference))
				{
					declaredKeys.stream()
							.filter(reference::equalsIgnoreCase)
							.findFirst()
							.ifPresent(declaredKey -> mismatches.add(
									new CaseMismatch(source.getSourceURI(), reference, declaredKey)));
				}
			}
		}
		return List.copyOf(mismatches);
	}

	/**
	 * Collects named campaign references used to score this campaign's dependency demand.
	 *
	 * @param campaign the campaign whose prerequisites are inspected
	 * @return distinct referenced names in encounter order, without resolving them to campaign objects
	 */
	private static Set<String> requiredCampaignNames(Campaign campaign)
	{
		Set<String> names = new LinkedHashSet<>();
		campaign.getPrerequisiteList().forEach(prereq -> collectRequired(prereq, names));
		return names;
	}

	/**
	 * Adds named campaign references from a prerequisite and its descendants to the supplied set.
	 *
	 * <p>Bare campaign names and {@code INCLUDES=} references contribute names;
	 * book-type conditions do not identify an individual campaign. Negated
	 * subtrees are skipped so exclusions do not count as dependency demand.
	 * This only inspects the prerequisite structure; it does not evaluate qualification.
	 *
	 * @param prereq the root of the prerequisite subtree to inspect
	 * @param names the destination for collected campaign names, modified in place
	 */
	private static void collectRequired(Prerequisite prereq, Set<String> names)
	{
		if (isNegated(prereq.getOperator()))
		{
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
	 *
	 * @param prereq the prerequisite whose kind and key are inspected
	 * @return the bare campaign name or the value after {@code INCLUDES=}, or null for other conditions
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

	/**
	 * Identifies comparison operators representing exclusions rather than positive dependency demand.
	 *
	 * @param operator the parsed prerequisite comparison operator
	 * @return true for {@code LT}, {@code LTEQ} or {@code NEQ}
	 */
	private static boolean isNegated(PrerequisiteOperator operator)
	{
		return operator == PrerequisiteOperator.LT
				|| operator == PrerequisiteOperator.LTEQ
				|| operator == PrerequisiteOperator.NEQ;
	}
}
