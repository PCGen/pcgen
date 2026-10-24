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
import java.util.List;
import java.util.function.UnaryOperator;

import pcgen.core.Campaign;

/** A selection snapshot against which additions share the same prerequisite-failure baseline. */
final class CampaignSelection
{
	private final List<Campaign> selected;
	private final List<Campaign> alreadyUnqualified;
	private final UnaryOperator<List<Campaign>> findUnqualified;

	/**
	 * Captures the selection and the failures that an addition must not be blamed for.
	 *
	 * @param selected the campaigns already selected
	 * @param findUnqualified the campaigns that fail against a whole selection
	 */
	CampaignSelection(List<Campaign> selected, UnaryOperator<List<Campaign>> findUnqualified)
	{
		this.selected = List.copyOf(selected);
		this.findUnqualified = findUnqualified;
		alreadyUnqualified = this.selected.isEmpty()
				? Collections.emptyList() : List.copyOf(findUnqualified.apply(this.selected));
	}

	/** Whether a campaign with this key is already selected. */
	boolean contains(Campaign campaign)
	{
		return selected.contains(campaign);
	}

	/**
	 * Finds failures introduced by adding campaigns to the original selection.
	 *
	 * @param additions the campaigns to add, in encounter order
	 * @return newly failing campaigns in the engine's order, excluding baseline failures by campaign key
	 */
	List<Campaign> newFailuresWith(List<Campaign> additions)
	{
		var combined = new ArrayList<>(selected);
		combined.addAll(additions);
		return findUnqualified.apply(combined).stream()
				.filter(campaign -> !alreadyUnqualified.contains(campaign))
				.toList();
	}
}
