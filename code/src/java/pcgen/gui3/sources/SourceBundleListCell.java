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

import java.util.List;
import java.util.function.Consumer;

import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import pcgen.facade.core.LoadableFacade.LoadingState;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.Icons;
import pcgen.system.LanguageBundle;

/**
 * A source row: the bundle's name, and — only when the bundle is the user's own
 * ({@link SourceBundle#modifiable()}) — a trailing trash icon that deletes it.
 *
 * <p>Deleting is a property of one row, so the affordance lives on that row
 * rather than in a tab-wide button the user must pair with a selection. Built-in
 * bundles simply have no icon, which reads better than a permanently disabled
 * button.
 *
 * <p>Cells are recycled as the list scrolls, so {@link #updateItem} must rebuild
 * the row every time; leaving the icon in place would offer Delete on a built-in
 * bundle that merely reused the cell.
 */
final class SourceBundleListCell extends ListCell<SourceBundle>
{
	/**
	 * Reads as "this one is not all there". The only fill this cell chooses --
	 * everything else defers to the user's configured text colour.
	 */
	private static final Color INCOMPLETE_COLOR = Color.GRAY;

	private final Label name = new Label();
	private final Region spacer = new Region();
	private final Button delete = new Button();
	private final HBox row;

	SourceBundleListCell(Consumer<SourceBundle> onDelete)
	{
		delete.setGraphic(new ImageView(Icons.Delete16.asJavaFX()));
		delete.getStyleClass().add("row-delete");
		// The row, not the button, is the keyboard target; see the DELETE accelerator
		// on the list itself. A traversable button here would trap Tab inside the list.
		delete.setFocusTraversable(false);
		delete.setTooltip(new Tooltip(LanguageBundle.getString("in_delete")));
		delete.setAccessibleText(LanguageBundle.getString("in_delete"));
		delete.setOnAction(event -> onDelete.accept(getItem()));
		// Without this the row's double-click-to-load would also fire.
		delete.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);

		HBox.setHgrow(spacer, Priority.ALWAYS);
		row = new HBox(name, spacer, delete);
		row.setAlignment(Pos.CENTER_LEFT);
		// The cell lays its graphic out across the full content width, but only up
		// to the graphic's maximum, so the icon reaches the trailing edge without
		// the row having to guess the viewport width. Binding to the list's own
		// width instead would overflow the cell insets and raise a scroll bar.
		row.setMaxWidth(Double.MAX_VALUE);
	}

	/**
	 * Marks a selection that cannot load everything it names.
	 *
	 * <p>A saved selection whose campaigns are no longer installed loads short
	 * of what it says, and shown as though it were intact the user picks it and
	 * sources quietly go missing. The legacy list greyed it and explained
	 * itself in a tooltip; so does this.
	 *
	 * <p>Both the colour and the tooltip are set on every path, including the
	 * one that clears them: cells are recycled, and a healthy row must not
	 * inherit the previous occupant's marking.
	 *
	 * <p>Clearing the colour means restoring the user's configured text
	 * colour, never setting {@code null}. A {@code Labeled}'s fill is bound to
	 * the fill of the {@code Text} its skin draws, and a {@code Text} with a
	 * null fill paints nothing -- the name would vanish rather than revert.
	 * Hardcoding black would be no better: it is readable only under a light
	 * theme, and the colour is the user's to choose.
	 */
	private void markLoadingState(SourceBundle bundle)
	{
		boolean incomplete = bundle.loadingState() == LoadingState.LOADED_WITH_ERRORS;
		name.setTextFill(incomplete ? INCOMPLETE_COLOR : UIPropertyContext.getQualifiedColor());
		name.setTooltip(incomplete ? new Tooltip(loadingMessageOf(bundle)) : null);
	}

	/** The engine's explanation, or a general one when it offered none. */
	private static String loadingMessageOf(SourceBundle bundle)
	{
		return bundle.loadingErrorMessage() == null || bundle.loadingErrorMessage().isBlank()
				? LanguageBundle.getString("in_src_incompleteSelection")
				: bundle.loadingErrorMessage();
	}

	@Override
	public void updateItem(SourceBundle bundle, boolean empty)
	{
		super.updateItem(bundle, empty);
		if (empty || bundle == null)
		{
			setText(null);
			setGraphic(null);
			return;
		}
		name.setText(bundle.displayName());
		markLoadingState(bundle);
		row.getChildren().setAll(bundle.modifiable()
				? List.of(name, spacer, delete)
				: List.of(name, spacer));
		setText(null);
		setGraphic(row);
	}
}
