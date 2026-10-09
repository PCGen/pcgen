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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import pcgen.core.GameMode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

/**
 * Delete is a property of a single row, not of the tab: only a user-saved
 * (modifiable) bundle can be removed, so only those rows carry a trash icon.
 * Built-in bundles show no affordance at all rather than a permanently disabled
 * one.
 *
 * <p>{@link javafx.scene.control.ListCell}s are recycled as the list scrolls, so
 * every assertion here is really about {@code updateItem} fully resetting the
 * row — a cell that once held a custom bundle must not keep its trash icon when
 * it is reused for a built-in one.
 */
@ExtendWith(ApplicationExtension.class)
class SourceBundleListCellTest
{
	private static final GameMode MODE = new GameMode("test");
	private static final SourceBundle BUNDLED = new SourceBundle("Bundled", MODE, List.of(), false, null);
	private static final SourceBundle CUSTOM = new SourceBundle("Custom", MODE, List.of(), true, null);

	/** Records the bundle the cell asked to delete, or null if it never asked. */
	private SourceBundle deleted;

	private SourceBundleListCell newCell()
	{
		return new SourceBundleListCell(bundle -> deleted = bundle);
	}

	private static Optional<Button> trashOf(SourceBundleListCell cell)
	{
		Node graphic = cell.getGraphic();
		if (!(graphic instanceof Parent parent))
		{
			return Optional.empty();
		}
		return parent.getChildrenUnmodifiable().stream()
				.filter(Button.class::isInstance)
				.map(Button.class::cast)
				.findFirst();
	}

	@Test
	void cell_should_showTrashIcon_when_bundleIsModifiable()
	{
		var cell = WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
			var created = newCell();
			created.updateItem(CUSTOM, false);
			return created;
		});

		assertTrue(trashOf(cell).isPresent(), "a custom bundle row should offer Delete");
	}

	@Test
	void cell_should_hideTrashIcon_when_bundleIsNotModifiable()
	{
		var cell = WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
			var created = newCell();
			created.updateItem(BUNDLED, false);
			return created;
		});

		assertFalse(trashOf(cell).isPresent(), "a built-in bundle cannot be deleted, so it shows no trash icon");
	}

	@Test
	void cell_should_dropTrashIcon_when_recycledOntoABuiltInBundle()
	{
		var cell = WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
			var created = newCell();
			created.updateItem(CUSTOM, false);
			created.updateItem(BUNDLED, false);
			return created;
		});

		assertFalse(trashOf(cell).isPresent(), "recycling a row must not leave the previous row's trash icon behind");
	}

	@Test
	void cell_should_clearGraphic_when_becomingEmpty()
	{
		var cell = WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
			var created = newCell();
			created.updateItem(CUSTOM, false);
			created.updateItem(null, true);
			return created;
		});

		assertNull(cell.getGraphic(), "an empty row must render nothing");
	}

	@Test
	void cell_should_requestDeleteOfItsOwnBundle_when_trashIsClicked()
	{
		WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
			var cell = newCell();
			cell.updateItem(CUSTOM, false);
			trashOf(cell).orElseThrow().fire();
		});

		assertSame(CUSTOM, deleted, "the trash icon deletes the bundle on its own row");
	}

	@Test
	void trashIcon_should_notBeFocusTraversable()
	{
		var cell = WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
			var created = newCell();
			created.updateItem(CUSTOM, false);
			return created;
		});

		assertFalse(trashOf(cell).orElseThrow().isFocusTraversable(),
				"a button inside a list row would otherwise trap keyboard traversal in the list");
	}
}
