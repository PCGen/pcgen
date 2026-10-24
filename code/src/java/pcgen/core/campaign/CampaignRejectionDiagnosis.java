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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import pcgen.core.Campaign;

/**
 * Explains why campaigns are already loaded or cannot be added.
 *
 * <p>Planner refusals and availability tooltips share these reasons:
 *
 * <ul>
 * <li>{@link AlreadySelected}: the campaign is selected directly.</li>
 * <li>{@link AlreadyIncluded}: a selected campaign loads it, possibly through nested campaigns.</li>
 * <li>{@link ConflictsWith}: adding it would introduce failures in selected campaigns.</li>
 * <li>{@link Unmet}: its requirements are not met for another reason.</li>
 * </ul>
 *
 * <p>Selection and containment take precedence over conflicts. Campaigns are
 * compared by their case-insensitive keys.
 */
public final class CampaignRejectionDiagnosis
{
	private CampaignRejectionDiagnosis()
	{
	}

	/** Why a campaign is already loaded or cannot be added. */
	public sealed interface Reason permits AlreadySelected, AlreadyIncluded, ConflictsWith, Unmet
	{
	}

	/** The campaign is already selected directly. */
	public record AlreadySelected() implements Reason
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
	 * A campaign and the reason it is already loaded or cannot be added.
	 *
	 * @param campaign the campaign being diagnosed
	 * @param reason why it is already loaded or cannot be added
	 */
	public record Diagnosis(Campaign campaign, Reason reason)
	{
	}

	/**
	 * Explains why each campaign is already loaded or cannot be added.
	 *
	 * <p>Checks direct selection, recursive containment, and conflicts, in that
	 * order. Conflicts name only selected campaigns that would newly fail;
	 * existing failures are not blamed on the candidate. Remaining cases have
	 * unmet requirements.
	 *
	 * @param rejected the campaigns to diagnose, in display order
	 * @param selected the campaigns already selected
	 * @param contentsOf the campaigns a campaign loads, normally
	 *                   {@code Campaign::getSubCampaigns}
	 * @param findUnqualified returns the members of a set that do not qualify
	 *                        given that set
	 * @return a diagnosis per campaign, in the order given
	 */
	public static List<Diagnosis> diagnose(List<Campaign> rejected, List<Campaign> selected,
			Function<Campaign, List<Campaign>> contentsOf,
			UnaryOperator<List<Campaign>> findUnqualified)
	{
		if (rejected.isEmpty())
		{
			return Collections.emptyList();
		}
		var selection = new CampaignSelection(selected, findUnqualified);
		return rejected.stream()
				.map(campaign -> new Diagnosis(campaign, reasonFor(campaign, selected, selection, contentsOf)))
				.toList();
	}

	private static Reason reasonFor(Campaign campaign, List<Campaign> selected,
			CampaignSelection selection, Function<Campaign, List<Campaign>> contentsOf)
	{
		if (selection.contains(campaign))
		{
			return new AlreadySelected();
		}
		var container = CampaignContainment.containerOf(campaign, selected, contentsOf);
		if (container.isPresent())
		{
			return new AlreadyIncluded(container.get());
		}
		if (selected.isEmpty())
		{
			return new Unmet();
		}
		List<Campaign> blockers = selection.newFailuresWith(List.of(campaign)).stream()
				.filter(selection::contains)
				.toList();
		return blockers.isEmpty() ? new Unmet() : new ConflictsWith(blockers);
	}
}
