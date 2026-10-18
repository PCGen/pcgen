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
import java.util.function.UnaryOperator;

import pcgen.core.Campaign;

/**
 * Decides which of the campaigns a user asked to add can actually be added.
 *
 * <p>Campaign prerequisites are a property of the <em>whole</em> loaded set --
 * {@code FacadeFactory.passesPrereqs} sets the chosen source files to the
 * entire list before asking each campaign whether it qualifies. Validating
 * candidates one at a time against a growing set therefore asks the wrong
 * question: a campaign whose prerequisite happens to sort later in the tree is
 * judged against a set that does not contain it yet, and is rejected even
 * though the user selected both. Selecting every book made this unmissable,
 * because the first book alphabetically is the one most likely to depend on
 * something further down.
 *
 * <p>This planner asks the question once, about the set that would result from
 * the addition, so the outcome no longer depends on tree order. It then drops
 * what does not qualify and takes back everything the remainder can carry,
 * because the data's many mutual exclusions would otherwise make a single
 * whole-set verdict refuse both sides of every conflict.
 *
 * <p>Campaigns are compared by identity rather than {@code equals}. They are
 * engine-wide singletons supplied by {@code FacadeFactory}, and identity is
 * both what the caller means and immune to whatever equality
 * {@code CDOMObject} happens to define.
 */
public final class CampaignAdditionPlanner
{
	private CampaignAdditionPlanner()
	{
	}

	/**
	 * The outcome of planning an addition.
	 *
	 * @param accepted candidates that may be added, in the order requested
	 * @param rejected candidates that would not qualify, in the order requested
	 */
	public record Plan(List<Campaign> accepted, List<Campaign> rejected)
	{
	}

	/**
	 * Plans the addition of {@code candidates} to {@code alreadySelected}.
	 *
	 * <p>Candidates already selected, and duplicates within {@code candidates},
	 * are ignored. Campaigns in {@code alreadySelected} are never reported as
	 * rejected: the user did not ask to add them, so neither dropping them nor
	 * blaming them in a warning would be honest.
	 *
	 * <p>Rejected candidates are dropped from the set before the rest are
	 * judged, so a campaign that only qualifies once some other candidate is
	 * gone is still added. This matters because PCGen's data is full of
	 * deliberate exclusions: a MultiSet such as "Pathfinder RPG Core
	 * Supplements" repackages a dozen individual books and carries a
	 * {@code !PRECAMPAIGN} against each of them, and reduced editions such as
	 * "Bestiary ~ Player Options Only" exclude the full book. Judging the
	 * complete set once would refuse the bundle <em>and</em> every book it
	 * covers.
	 *
	 * <p>Where two candidates genuinely exclude one another, the one the user
	 * sees first wins. That is predictable, but it is not guaranteed to produce
	 * the largest possible selection -- finding that is a set-cover problem.
	 *
	 * @param alreadySelected campaigns already in the selected list
	 * @param candidates campaigns the user asked to add, in display order
	 * @param findUnqualified returns the members of a set that do not qualify
	 *                        given that set; not called when nothing is to be
	 *                        added
	 * @return the accepted and rejected candidates
	 */
	public static Plan plan(List<Campaign> alreadySelected, List<Campaign> candidates,
			UnaryOperator<List<Campaign>> findUnqualified)
	{
		Set<Campaign> selected = identitySet(alreadySelected);
		Set<Campaign> seen = identitySet(List.of());
		// Identity-based deduplication is stateful; keep this stream sequential.
		List<Campaign> toAdd = candidates.stream()
				.filter(candidate -> !selected.contains(candidate))
				.filter(seen::add)
				.toList();
		if (toAdd.isEmpty())
		{
			return new Plan(List.of(), List.of());
		}

		// Whatever is already wrong with the existing selection is not this
		// addition's doing, and must not be allowed to veto every candidate.
		Set<Campaign> tolerated = identitySet(alreadySelected.isEmpty()
				? List.of()
				: findUnqualified.apply(List.copyOf(alreadySelected)));

		Set<Campaign> kept = identitySet(toAdd);
		shrinkToSomethingValid(alreadySelected, toAdd, kept, tolerated, findUnqualified);
		takeBackWhatFits(alreadySelected, toAdd, kept, tolerated, findUnqualified);

		List<Campaign> accepted = new ArrayList<>();
		List<Campaign> rejected = new ArrayList<>();
		for (Campaign candidate : toAdd)
		{
			if (kept.contains(candidate))
			{
				accepted.add(candidate);
			}
			else
			{
				rejected.add(candidate);
			}
		}
		return new Plan(List.copyOf(accepted), List.copyOf(rejected));
	}

	/**
	 * Drops candidates until the set is valid.
	 *
	 * <p>This starts from the complete set rather than building up, so a
	 * candidate whose prerequisite is another candidate is judged with that
	 * other candidate present. It over-shoots: mutually exclusive candidates are
	 * all unqualified at once and all get dropped, which is what
	 * {@link #takeBackWhatFits} then repairs.
	 */
	private static void shrinkToSomethingValid(List<Campaign> alreadySelected, List<Campaign> toAdd,
			Set<Campaign> kept, Set<Campaign> tolerated,
			UnaryOperator<List<Campaign>> findUnqualified)
	{
		while (!kept.isEmpty())
		{
			List<Campaign> unqualified =
					findUnqualified.apply(wholeSet(alreadySelected, toAdd, kept));
			List<Campaign> offenders = unqualified.stream().filter(kept::contains).toList();
			if (!offenders.isEmpty())
			{
				offenders.forEach(kept::remove);
			}
			else if (unqualified.stream().allMatch(tolerated::contains))
			{
				return;
			}
			else
			{
				// The addition broke something the user already had, and the
				// engine will not say which candidate did it. Give up the last
				// one and ask again.
				kept.remove(lastKept(toAdd, kept));
			}
		}
	}

	/**
	 * Takes back every dropped candidate that the remaining set can carry.
	 *
	 * <p>Conflicts are resolved in favour of the candidate the user sees first,
	 * which is predictable but does not promise the largest possible set: where
	 * two candidates exclude each other, the earlier one wins even if the later
	 * one would have allowed more books in. Passes are repeated because taking a
	 * candidate back can satisfy the requirements of one already tried.
	 */
	private static void takeBackWhatFits(List<Campaign> alreadySelected, List<Campaign> toAdd,
			Set<Campaign> kept, Set<Campaign> tolerated,
			UnaryOperator<List<Campaign>> findUnqualified)
	{
		boolean grew = true;
		while (grew)
		{
			grew = false;
			for (Campaign candidate : toAdd)
			{
				if (kept.contains(candidate))
				{
					continue;
				}
				kept.add(candidate);
				List<Campaign> unqualified =
						findUnqualified.apply(wholeSet(alreadySelected, toAdd, kept));
				if (unqualified.stream().allMatch(tolerated::contains))
				{
					grew = true;
				}
				else
				{
					kept.remove(candidate);
				}
			}
		}
	}

	/** The set to validate: the existing selection plus the surviving candidates, in display order. */
	private static List<Campaign> wholeSet(List<Campaign> alreadySelected, List<Campaign> toAdd,
			Set<Campaign> kept)
	{
		List<Campaign> whole = new ArrayList<>(alreadySelected);
		toAdd.stream().filter(kept::contains).forEach(whole::add);
		return whole;
	}

	private static Campaign lastKept(List<Campaign> toAdd, Set<Campaign> kept)
	{
		Campaign last = null;
		for (Campaign candidate : toAdd)
		{
			if (kept.contains(candidate))
			{
				last = candidate;
			}
		}
		return last;
	}

	private static Set<Campaign> identitySet(List<Campaign> campaigns)
	{
		Set<Campaign> set = Collections.newSetFromMap(new IdentityHashMap<>());
		set.addAll(campaigns);
		return set;
	}
}
