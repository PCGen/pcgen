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
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import pcgen.core.Campaign;

/**
 * Says how each offered source stands against the current selection, so a
 * source list can show it before the user asks to add anything.
 *
 * <p>The legacy Swing panel coloured a row with the "not qualified" colour
 * whenever adding it would fail. That conflates two situations the user has to
 * treat quite differently: a book inside a selected collection also fails --
 * collections carry a {@code !PRECAMPAIGN} against the books they repackage --
 * but it is loaded, and marking it as a problem sends the user hunting for one
 * that does not exist. Selecting the "Pathfinder RPG for Game Masters"
 * collection alone turns ten books that colour.
 *
 * <p>The question asked of the engine is Swing's, and deliberately so: "could
 * this one be added to what is selected now?" It is not the question
 * {@link CampaignAdditionPlanner} answers, which is about a whole batch, so a
 * book marked addable here may still be refused as part of a larger addition
 * that contains something excluding it.
 *
 * <p>One engine call per offered campaign is needed, because the prerequisite
 * check is a property of a whole set rather than of a campaign. Measured
 * against the published Pathfinder data that is 217 calls in about 9ms, which
 * is cheap enough to redo whenever the selection changes -- and far cheaper
 * than Swing's habit of asking once per cell repaint.
 */
public final class CampaignAvailability
{
	private CampaignAvailability()
	{
	}

	/** How an offered source stands against the current selection. */
	public enum Status
	{
		/** It can be added. */
		ADDABLE,

		/** It is already loaded, either selected outright or through a selected collection. */
		ALREADY_INCLUDED,

		/** Adding it would not qualify. */
		BLOCKED
	}

	/**
	 * Classifies every offered campaign.
	 *
	 * <p>"Already included" is tested first, because a collection both contains
	 * and excludes its own books and so would otherwise look blocked.
	 *
	 * <p>Whatever is already wrong with the selection is not an offered
	 * campaign's doing: the set is judged against how it stood before, so a
	 * selection the engine already refuses does not mark every remaining book
	 * as blocked.
	 *
	 * @param offered the campaigns the list shows
	 * @param selected the campaigns already selected
	 * @param contentsOf the campaigns a campaign loads, normally
	 *                   {@code Campaign::getSubCampaigns}
	 * @param passesPrereqs whether a set of campaigns qualifies
	 * @return the status of each offered campaign, keyed by identity
	 */
	public static Map<Campaign, Status> classify(List<Campaign> offered, List<Campaign> selected,
			Function<Campaign, List<Campaign>> contentsOf, Predicate<List<Campaign>> passesPrereqs)
	{
		Map<Campaign, Status> statuses = new IdentityHashMap<>();
		if (offered.isEmpty())
		{
			return Collections.unmodifiableMap(statuses);
		}
		boolean selectionAlreadyFails = !selected.isEmpty() && !passesPrereqs.test(List.copyOf(selected));
		for (Campaign campaign : offered)
		{
			statuses.put(campaign,
					statusOf(campaign, selected, selectionAlreadyFails, contentsOf, passesPrereqs));
		}
		return Collections.unmodifiableMap(statuses);
	}

	private static Status statusOf(Campaign campaign, List<Campaign> selected,
			boolean selectionAlreadyFails, Function<Campaign, List<Campaign>> contentsOf,
			Predicate<List<Campaign>> passesPrereqs)
	{
		if (isSelected(campaign, selected)
				|| CampaignContainment.containerOf(campaign, selected, contentsOf) != null)
		{
			return Status.ALREADY_INCLUDED;
		}
		List<Campaign> withCandidate = new ArrayList<>(selected);
		withCandidate.add(campaign);
		if (passesPrereqs.test(withCandidate))
		{
			return Status.ADDABLE;
		}
		// A selection the engine already refuses would otherwise mark every
		// book blocked, blaming each for a fault none of them caused.
		return selectionAlreadyFails ? Status.ADDABLE : Status.BLOCKED;
	}

	private static boolean isSelected(Campaign campaign, List<Campaign> selected)
	{
		return selected.stream().anyMatch(chosen -> chosen == campaign);
	}
}
