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
package pcgen.core.campaign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import pcgen.core.Campaign;

/**
 * Explains why campaigns the user asked to add were refused.
 *
 * <p>{@link CampaignAdditionPlanner} decides <em>what</em> cannot be added; it
 * cannot say <em>why</em>, because the engine answers only "this set does not
 * qualify". Quoting each campaign's own prerequisite expression, as the dialog
 * used to, describes the rule rather than the situation, and so is least
 * helpful exactly when the situation is surprising.
 *
 * <p>Two of the three answers are far more useful than the raw expression:
 *
 * <ul>
 * <li>The campaign is <em>already included</em> in a selected one. PCGen's data
 * ships MultiSet bundles -- "Pathfinder RPG for Game Masters" lists the Core
 * Rulebook, the Bestiaries and the Advanced Player's Guide among its
 * {@code PCC:} files -- and each bundle carries a {@code !PRECAMPAIGN} against
 * the books it repackages, to stop them being loaded twice. Asking for such a
 * book is not a failure at all: it is already loaded, through the bundle.</li>
 * <li>The campaign <em>conflicts with</em> something selected. The planner
 * never drops a campaign the user already chose, so a selected bundle silently
 * vetoes every book it excludes. Naming it turns a dead end into an action.</li>
 * </ul>
 *
 * <p>Anything else is a genuinely unmet requirement, where the engine's own
 * description is the best available answer and is kept.
 *
 * <p>Campaigns are compared by identity, as elsewhere in this package: they are
 * engine-wide singletons, and identity is immune to whatever equality
 * {@code CDOMObject} happens to define.
 */
public final class CampaignRejectionDiagnosis
{
	private CampaignRejectionDiagnosis()
	{
	}

	/** Why a campaign was refused. */
	public sealed interface Reason permits AlreadyIncluded, ConflictsWith, Unmet
	{
	}

	/**
	 * The campaign is already loaded as part of a selected one.
	 *
	 * @param container the selected campaign that includes it
	 */
	public record AlreadyIncluded(Campaign container) implements Reason
	{
	}

	/**
	 * Adding the campaign would stop a selected campaign qualifying.
	 *
	 * @param blockers the selected campaigns it would break, never empty
	 */
	public record ConflictsWith(List<Campaign> blockers) implements Reason
	{
	}

	/** The campaign needs something that is not selected. */
	public record Unmet() implements Reason
	{
	}

	/**
	 * A refused campaign and the reason for it.
	 *
	 * @param campaign the campaign that was refused
	 * @param reason why it was refused
	 */
	public record Diagnosis(Campaign campaign, Reason reason)
	{
	}

	/**
	 * Explains each rejection.
	 *
	 * <p>"Already included" is tested first. A bundle both contains and
	 * excludes its own books, so a book inside a selected bundle is also a
	 * conflict in the engine's eyes -- but telling the user to remove the
	 * bundle to gain a book the bundle already gives them would be absurd.
	 *
	 * <p>This is an error path: nothing here runs unless something was refused.
	 *
	 * @param rejected the campaigns the planner refused, in display order
	 * @param selected the campaigns already selected
	 * @param contentsOf the campaigns a campaign loads, normally
	 *                   {@code Campaign::getSubCampaigns}
	 * @param findUnqualified returns the members of a set that do not qualify
	 *                        given that set
	 * @return a diagnosis per rejection, in the order given
	 */
	public static List<Diagnosis> diagnose(List<Campaign> rejected, List<Campaign> selected,
			Function<Campaign, List<Campaign>> contentsOf,
			Function<List<Campaign>, List<Campaign>> findUnqualified)
	{
		if (rejected.isEmpty())
		{
			return List.of();
		}
		// Whatever was already wrong with the selection is not the candidate's
		// doing, and must not be reported as though it were.
		Set<Campaign> alreadyBroken = selected.isEmpty()
				? identitySet(List.of())
				: identitySet(findUnqualified.apply(List.copyOf(selected)));

		List<Diagnosis> diagnoses = new ArrayList<>(rejected.size());
		for (Campaign campaign : rejected)
		{
			diagnoses.add(new Diagnosis(campaign,
					reasonFor(campaign, selected, alreadyBroken, contentsOf, findUnqualified)));
		}
		return List.copyOf(diagnoses);
	}

	private static Reason reasonFor(Campaign campaign, List<Campaign> selected,
			Set<Campaign> alreadyBroken, Function<Campaign, List<Campaign>> contentsOf,
			Function<List<Campaign>, List<Campaign>> findUnqualified)
	{
		Campaign container = CampaignContainment.containerOf(campaign, selected, contentsOf);
		if (container != null)
		{
			return new AlreadyIncluded(container);
		}
		List<Campaign> blockers = brokenByAdding(campaign, selected, alreadyBroken, findUnqualified);
		return blockers.isEmpty() ? new Unmet() : new ConflictsWith(blockers);
	}

	/**
	 * The selected campaigns that adding {@code campaign} would stop
	 * qualifying.
	 */
	private static List<Campaign> brokenByAdding(Campaign campaign, List<Campaign> selected,
			Set<Campaign> alreadyBroken, Function<List<Campaign>, List<Campaign>> findUnqualified)
	{
		if (selected.isEmpty())
		{
			return List.of();
		}
		List<Campaign> withCandidate = new ArrayList<>(selected);
		withCandidate.add(campaign);
		Set<Campaign> stillSelected = identitySet(selected);
		return findUnqualified.apply(withCandidate).stream()
				.filter(stillSelected::contains)
				.filter(broken -> !alreadyBroken.contains(broken))
				.toList();
	}

	private static Set<Campaign> identitySet(List<Campaign> campaigns)
	{
		Set<Campaign> set = Collections.newSetFromMap(new IdentityHashMap<>());
		set.addAll(campaigns);
		return set;
	}
}
