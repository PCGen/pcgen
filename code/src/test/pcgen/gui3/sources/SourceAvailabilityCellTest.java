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
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.IdentityHashMap;
import java.util.Map;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.paint.Color;

import pcgen.core.Campaign;
import pcgen.core.campaign.CampaignAvailability.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/**
 * The available-sources tree marks each row with how it stands against the
 * current selection, so the user learns a source cannot be added without
 * having to click Add and read a dialog. The legacy Swing panel did this with
 * a single "not qualified" colour; this cell distinguishes a source that is
 * genuinely blocked from one that is already loaded through a selected
 * collection, which is not a problem at all.
 *
 * <p>Cells are recycled as the tree scrolls, so the tests below care as much
 * about what a cell forgets as about what it shows: a stale colour or tooltip
 * left on a reused cell would mislabel an unrelated row.
 */
@ExtendWith(ApplicationExtension.class)
class SourceAvailabilityCellTest
{
	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		return campaign;
	}

	private static SourceAvailabilityCell cellShowing(Campaign campaign, Status status)
	{
		Map<Campaign, Status> statuses = new IdentityHashMap<>();
		statuses.put(campaign, status);
		var cell = new SourceAvailabilityCell(() -> statuses, c -> "why " + c.getDisplayName());
		show(cell, new SourceTreeNode.Leaf(campaign), campaign.getDisplayName());
		return cell;
	}

	/** Puts the cell on a row showing {@code node}, as the tree does when it lays out. */
	private static void show(SourceAvailabilityCell cell, SourceTreeNode node, String text)
	{
		var row = new TreeTableRow<SourceTreeNode>();
		row.updateTreeItem(new TreeItem<>(node));
		cell.updateTreeTableRow(row);
		cell.updateItem(text, false);
	}

	@Test
	void blockedSource_isMarkedWithTheNotQualifiedColour()
	{
		var cell = cellShowing(campaign("Pathfinder RPG for Players"), Status.BLOCKED);

		assertEquals(pcgen.gui2.UIPropertyContext.getNotQualifiedColor(), cell.getTextFill(),
				"blocked rows use the colour the rest of PCGen uses for a failed prerequisite");
	}

	@Test
	void addableSource_isMarkedWithTheQualifiedColour()
	{
		var cell = cellShowing(campaign("Core Rulebook"), Status.ADDABLE);

		assertEquals(pcgen.gui2.UIPropertyContext.getQualifiedColor(), cell.getTextFill());
	}

	/**
	 * A book inside a selected collection is loaded, not broken. Giving it the
	 * failure colour is what the warning dialog was just changed to stop doing.
	 */
	@Test
	void alreadyIncludedSource_isNotMarkedAsAFailure()
	{
		var cell = cellShowing(campaign("Advanced Player's Guide"), Status.ALREADY_INCLUDED);

		assertNotEquals(pcgen.gui2.UIPropertyContext.getNotQualifiedColor(), cell.getTextFill());
		assertNotEquals(pcgen.gui2.UIPropertyContext.getQualifiedColor(), cell.getTextFill(),
				"it must still be distinguishable from a source that can be added");
	}

	@Test
	void blockedSource_explainsItselfInATooltip()
	{
		var cell = cellShowing(campaign("Pathfinder RPG for Players"), Status.BLOCKED);

		assertNotNull(cell.getTooltip());
		assertTrue(cell.getTooltip().getText().contains("Pathfinder RPG for Players"),
				cell.getTooltip().getText());
	}

	@Test
	void alreadyIncludedSource_explainsItselfInATooltip()
	{
		var cell = cellShowing(campaign("Advanced Player's Guide"), Status.ALREADY_INCLUDED);

		assertNotNull(cell.getTooltip());
	}

	@Test
	void addableSource_hasNothingToExplain()
	{
		var cell = cellShowing(campaign("Core Rulebook"), Status.ADDABLE);

		assertNull(cell.getTooltip(), "a tooltip on every row would be noise");
	}

	/**
	 * Publisher and setting rows group the books; they are not sources and
	 * cannot be added, so they must not be coloured as though they could.
	 */
	@Test
	void groupingRow_isLeftAlone()
	{
		var cell = new SourceAvailabilityCell(IdentityHashMap::new, c -> "why");
		show(cell, new SourceTreeNode.Publisher("Paizo"), "Paizo");

		assertNull(cell.getTextFill());
		assertNull(cell.getTooltip());
	}

	@Test
	void recycledCell_forgetsTheColourOfTheRowItUsedToShow()
	{
		var blocked = campaign("Pathfinder RPG for Players");
		Map<Campaign, Status> statuses = new IdentityHashMap<>();
		statuses.put(blocked, Status.BLOCKED);
		var addable = campaign("Core Rulebook");
		statuses.put(addable, Status.ADDABLE);
		var cell = new SourceAvailabilityCell(() -> statuses, c -> "why");

		show(cell, new SourceTreeNode.Leaf(blocked), blocked.getDisplayName());
		show(cell, new SourceTreeNode.Leaf(addable), addable.getDisplayName());

		assertEquals(pcgen.gui2.UIPropertyContext.getQualifiedColor(), cell.getTextFill());
	}

	@Test
	void recycledCell_forgetsTheTooltipOfTheRowItUsedToShow()
	{
		var blocked = campaign("Pathfinder RPG for Players");
		Map<Campaign, Status> statuses = new IdentityHashMap<>();
		statuses.put(blocked, Status.BLOCKED);
		var addable = campaign("Core Rulebook");
		statuses.put(addable, Status.ADDABLE);
		var cell = new SourceAvailabilityCell(() -> statuses, c -> "why");

		show(cell, new SourceTreeNode.Leaf(blocked), blocked.getDisplayName());
		show(cell, new SourceTreeNode.Leaf(addable), addable.getDisplayName());

		assertNull(cell.getTooltip());
	}

	@Test
	void emptyCell_showsNothingAtAll()
	{
		var blocked = campaign("Pathfinder RPG for Players");
		Map<Campaign, Status> statuses = new IdentityHashMap<>();
		statuses.put(blocked, Status.BLOCKED);
		var cell = new SourceAvailabilityCell(() -> statuses, c -> "why");

		show(cell, new SourceTreeNode.Leaf(blocked), blocked.getDisplayName());
		cell.updateItem(null, true);

		assertNull(cell.getText());
		assertNull(cell.getTextFill());
		assertNull(cell.getTooltip());
	}

	/**
	 * The status map is rebuilt whenever the selection changes. A cell that
	 * captured the old map would keep showing the previous answer.
	 */
	@Test
	void cellReadsTheStatusMapEveryTime_soASelectionChangeTakesEffect()
	{
		var book = campaign("Core Rulebook");
		Map<Campaign, Status> statuses = new IdentityHashMap<>();
		statuses.put(book, Status.ADDABLE);
		var cell = new SourceAvailabilityCell(() -> statuses, c -> "why");
		show(cell, new SourceTreeNode.Leaf(book), book.getDisplayName());

		statuses.put(book, Status.BLOCKED);
		cell.updateItem(book.getDisplayName(), false);

		assertEquals(pcgen.gui2.UIPropertyContext.getNotQualifiedColor(), cell.getTextFill());
	}

	/** A campaign missing from the map, such as one filtered out, must not throw. */
	@Test
	void sourceWithNoKnownStatus_isLeftAlone()
	{
		var cell = new SourceAvailabilityCell(IdentityHashMap::new, c -> "why");
		show(cell, new SourceTreeNode.Leaf(campaign("Core Rulebook")), "Core Rulebook");

		assertNull(cell.getTextFill());
		assertNull(cell.getTooltip());
	}

	@Test
	void cellAlwaysShowsItsText()
	{
		var cell = cellShowing(campaign("Core Rulebook"), Status.ADDABLE);

		assertEquals("Core Rulebook", cell.getText());
	}

	/** The colours are the user's, configured in Preferences, not hard-coded here. */
	@Test
	void coloursComeFromThePreferences()
	{
		var original = pcgen.gui2.UIPropertyContext.getNotQualifiedColor();
		try
		{
			pcgen.gui2.UIPropertyContext.setNotQualifiedColor(Color.MAGENTA);

			var cell = cellShowing(campaign("Pathfinder RPG for Players"), Status.BLOCKED);

			assertEquals(Color.MAGENTA, cell.getTextFill());
		}
		finally
		{
			pcgen.gui2.UIPropertyContext.setNotQualifiedColor(original);
		}
	}
}
