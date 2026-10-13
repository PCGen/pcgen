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
package pcgen.gui3.sources;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.paint.Color;

import pcgen.core.Campaign;
import pcgen.core.campaign.CampaignAvailability.Status;
import pcgen.gui2.UIPropertyContext;

/**
 * A name cell in the available-sources tree, marked with how its source stands
 * against the current selection.
 *
 * <p>The legacy Swing panel coloured a row with the user's "not qualified"
 * colour whenever adding it would fail, which is where the red rows came from.
 * That answer was right but incomplete: a book inside a selected collection
 * also fails, because collections exclude the books they repackage, and
 * marking it red says "broken" about something that is loaded. Those rows are
 * greyed instead, which reads as "nothing to do here" rather than "fix this".
 *
 * <p>Swing recomputed the verdict inside the renderer, once per cell repaint.
 * A JavaFX cell is repainted far more often and recycled between rows, so the
 * statuses are computed once per selection change and supplied here as a map.
 * The map is read through a {@link Supplier} rather than captured, so a cell
 * built before a selection change still gives the current answer.
 *
 * <p>Cells are recycled, so every path through {@link #updateItem} must set
 * the colour and the tooltip, including the paths that clear them. A cell that
 * only ever added them would carry a blocked row's red text onto whatever row
 * reused it.
 *
 * <p>Clearing the colour means restoring the unmarked one, never setting
 * {@code null}. {@code Labeled.textFill} is bound to the fill of the
 * {@code Text} node the skin draws, and a {@code Text} with a null fill paints
 * nothing -- so a null fill does not restore the default, it makes the row's
 * label vanish while its disclosure arrow stays behind.
 */
final class SourceAvailabilityCell extends TreeTableCell<SourceTreeNode, String>
{
	/** Reads as "nothing to do here", where red would read as "fix this". */
	private static final Color ALREADY_INCLUDED_COLOR = Color.GRAY;

	private final Supplier<Map<Campaign, Status>> statuses;
	private final Function<Campaign, String> explain;

	/**
	 * @param statuses the current status of each offered campaign, re-read on
	 *                 every update so a selection change takes effect
	 * @param explain why a campaign cannot be added, for the tooltip
	 */
	SourceAvailabilityCell(Supplier<Map<Campaign, Status>> statuses, Function<Campaign, String> explain)
	{
		this.statuses = statuses;
		this.explain = explain;
	}

	@Override
	protected void updateItem(String item, boolean empty)
	{
		super.updateItem(item, empty);
		if (empty || item == null)
		{
			setText(null);
			setTextFill(null);
			setTooltip(null);
			return;
		}
		setText(item);
		apply(campaignOfRow());
	}

	/**
	 * The campaign this row shows, or {@code null} for the publisher and
	 * setting rows that group them.
	 */
	private Campaign campaignOfRow()
	{
		var row = getTreeTableRow();
		var treeItem = row == null ? null : row.getTreeItem();
		var node = treeItem == null ? null : treeItem.getValue();
		return node instanceof SourceTreeNode.Leaf leaf ? leaf.campaign() : null;
	}

	private void apply(Campaign campaign)
	{
		Status status = campaign == null ? null : statuses.get().get(campaign);
		if (status == null)
		{
			// A grouping row, or a campaign no longer offered. Neither has a
			// standing against the selection worth describing, but both still
			// need a fill: a null one paints nothing at all.
			setTextFill(UIPropertyContext.getQualifiedColor());
			setTooltip(null);
			return;
		}
		switch (status)
		{
			case ADDABLE -> {
				setTextFill(UIPropertyContext.getQualifiedColor());
				setTooltip(null);
			}
			case ALREADY_INCLUDED -> {
				setTextFill(ALREADY_INCLUDED_COLOR);
				setTooltip(new Tooltip(explain.apply(campaign)));
			}
			case BLOCKED -> {
				setTextFill(UIPropertyContext.getNotQualifiedColor());
				setTooltip(new Tooltip(explain.apply(campaign)));
			}
		}
	}
}
