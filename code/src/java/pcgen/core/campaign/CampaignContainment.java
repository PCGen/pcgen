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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import pcgen.core.Campaign;

/**
 * Answers whether a campaign is already loaded through one the user selected.
 *
 * <p>A campaign's {@code PCC:} files name other campaigns it loads, and PCGen's
 * data uses that to ship collections: "Pathfinder RPG for Game Masters" loads
 * the Core Rulebook, the Bestiaries and the Advanced Player's Guide. The
 * relationships nest -- each full Bestiary loads its own reduced "Player
 * Options Only" edition -- so a book two levels down is just as loaded as one
 * named directly, and the walk has to go all the way down.
 *
 * <p>Campaigns are compared by their case-insensitive keys, as elsewhere in this package.
 */
public final class CampaignContainment
{
	private CampaignContainment()
	{
	}

	/**
	 * The selected campaign that loads {@code campaign}, at any depth.
	 *
	 * <p>The outermost campaign is returned rather than the immediate parent,
	 * because that is the one the user chose and the only one they can act on.
	 *
	 * @param campaign the campaign to look for
	 * @param selected the campaigns the user has selected
	 * @param contentsOf the campaigns a campaign loads, normally
	 *                   {@code Campaign::getSubCampaigns}
	 * @return the first selected campaign loading it, or empty if none does
	 */
	public static Optional<Campaign> containerOf(Campaign campaign, List<Campaign> selected,
			Function<Campaign, List<Campaign>> contentsOf)
	{
		return selected.stream()
				.filter(candidate -> loads(candidate, campaign, contentsOf))
				.findFirst();
	}

	/** Whether {@code container} loads {@code sought}, at any depth. */
	private static boolean loads(Campaign container, Campaign sought,
			Function<Campaign, List<Campaign>> contentsOf)
	{
		// Data is authored by hand and a collection may name itself, or a
		// sibling that names it back, so the walk has to tolerate cycles.
		List<Campaign> visited = new ArrayList<>(List.of(container));
		var pending = new ArrayDeque<>(contentsOf.apply(container));
		while (!pending.isEmpty())
		{
			Campaign next = pending.pop();
			if (next.equals(sought))
			{
				return true;
			}
			if (!visited.contains(next))
			{
				visited.add(next);
				pending.addAll(contentsOf.apply(next));
			}
		}
		return false;
	}
}
