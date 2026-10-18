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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForAsyncFx;

import java.util.List;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import pcgen.core.GameMode;
import pcgen.facade.core.LoadableFacade.LoadingState;
import pcgen.gui2.UIPropertyContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.framework.junit5.ApplicationExtension;

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
		var cell = waitForAsyncFx(5000, () -> {
			var created = newCell();
			created.updateItem(CUSTOM, false);
			return created;
		});

		assertTrue(trashOf(cell).isPresent(), "a custom bundle row should offer Delete");
	}

	@Test
	void cell_should_hideTrashIcon_when_bundleIsNotModifiable()
	{
		var cell = waitForAsyncFx(5000, () -> {
			var created = newCell();
			created.updateItem(BUNDLED, false);
			return created;
		});

		assertFalse(trashOf(cell).isPresent(), "a built-in bundle cannot be deleted, so it shows no trash icon");
	}

	@Test
	void cell_should_dropTrashIcon_when_recycledOntoABuiltInBundle()
	{
		var cell = waitForAsyncFx(5000, () -> {
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
		var cell = waitForAsyncFx(5000, () -> {
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
		waitForAsyncFx(5000, () -> {
			var cell = newCell();
			cell.updateItem(CUSTOM, false);
			trashOf(cell).orElseThrow().fire();
		});

		assertSame(CUSTOM, deleted, "the trash icon deletes the bundle on its own row");
	}

	@Test
	void trashIcon_should_notBeFocusTraversable()
	{
		var cell = waitForAsyncFx(5000, () -> {
			var created = newCell();
			created.updateItem(CUSTOM, false);
			return created;
		});

		assertFalse(trashOf(cell).orElseThrow().isFocusTraversable(),
				"a button inside a list row would otherwise trap keyboard traversal in the list");
	}

	private static final SourceBundle BROKEN = new SourceBundle("Broken", MODE, List.of(), true,
			LoadingState.LOADED_WITH_ERRORS, "Some campaigns are missing");

	private static Label labelOf(SourceBundleListCell cell)
	{
		return (Label) ((Parent) cell.getGraphic()).getChildrenUnmodifiable().stream()
				.filter(Label.class::isInstance)
				.findFirst()
				.orElseThrow();
	}

	/**
	 * A saved selection whose campaigns are no longer installed loads short of
	 * what it names. Shown as though it were intact, the user picks it, and
	 * sources vanish with no explanation -- so it is greyed, as the legacy list
	 * greys it.
	 */
	@Test
	void cell_should_greyABundleThatCannotLoadCompletely()
	{
		var original = UIPropertyContext.getQualifiedColor();
		try
		{
			UIPropertyContext.setQualifiedColor(Color.MAGENTA);
			waitForAsyncFx(5000, () -> {
				var cell = cellShowing(BROKEN);
				assertNotEquals(Color.MAGENTA, labelOf(cell).getTextFill(),
						"a selection that cannot load must not look like one that can");
			});
		}
		finally
		{
			UIPropertyContext.setQualifiedColor(original);
		}
	}

	@Test
	void cell_should_explainWhyInATooltip()
	{
		waitForAsyncFx(5000, () -> {
			var cell = cellShowing(BROKEN);
			var tooltip = labelOf(cell).getTooltip();
			assertNotNull(tooltip, "greying it without saying why leaves the user guessing");
			assertTrue(tooltip.getText().contains("missing"), tooltip.getText());
		});
	}

	@Test
	void cell_should_leaveAHealthyBundleUnmarked()
	{
		waitForAsyncFx(5000, () ->
				assertNull(labelOf(cellShowing(CUSTOM)).getTooltip()));
	}

	/**
	 * A broken selection is one the user is most likely to want rid of, so the
	 * marking must not cost them the way to delete it.
	 */
	@Test
	void cell_should_stillOfferTheTrashIcon_whenTheBundleIsBroken()
	{
		waitForAsyncFx(5000, () -> assertTrue(trashOf(cellShowing(BROKEN)).isPresent()));
	}

	/** Cells are recycled, so a healthy row must not inherit a broken one's marking. */
	@Test
	void recycledCell_should_forgetTheMarkingOfABrokenBundle()
	{
		var original = UIPropertyContext.getQualifiedColor();
		try
		{
			UIPropertyContext.setQualifiedColor(Color.MAGENTA);
			waitForAsyncFx(5000, () -> {
				var cell = cellShowing(BROKEN);
				cell.updateItem(CUSTOM, false);

				assertNull(labelOf(cell).getTooltip());
				assertEquals(Color.MAGENTA, labelOf(cell).getTextFill());
			});
		}
		finally
		{
			UIPropertyContext.setQualifiedColor(original);
		}
	}

	private SourceBundleListCell cellShowing(SourceBundle bundle)
	{
		var cell = newCell();
		cell.updateItem(bundle, false);
		return cell;
	}

	/**
	 * A {@code Labeled}'s text fill is bound to the fill of the {@code Text}
	 * its skin draws, and a {@code Text} with a null fill paints nothing — so
	 * clearing the marking by setting null would not restore the default, it
	 * would make the row's name vanish. That is how the source tree's headings
	 * once went blank, and nothing in this file would have caught it.
	 */
	@Test
	void everyRowWithAName_hasAFillToPaintItWith()
	{
		waitForAsyncFx(5000, () -> {
			for (SourceBundle bundle : List.of(BUNDLED, CUSTOM, BROKEN))
			{
				var cell = cellShowing(bundle);

				assertNotNull(labelOf(cell).getTextFill(),
						() -> "a null fill makes " + bundle.displayName() + " invisible");
			}
		});
	}

	/** The unmarked colour is the user's, configured in Preferences, not a hardcoded black. */
	@ParameterizedTest
	@ValueSource(strings = {"black", "white", "gray", "magenta"})
	void healthyRow_usesTheConfiguredTextColour(String colourName)
	{
		var original = UIPropertyContext.getQualifiedColor();
		try
		{
			var colour = Color.valueOf(colourName);
			UIPropertyContext.setQualifiedColor(colour);
			waitForAsyncFx(5000, () ->
					assertEquals(colour, labelOf(cellShowing(CUSTOM)).getTextFill()));
		}
		finally
		{
			UIPropertyContext.setQualifiedColor(original);
		}
	}
}
