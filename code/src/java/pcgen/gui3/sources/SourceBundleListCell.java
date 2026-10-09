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
		row.getChildren().setAll(bundle.modifiable()
				? List.of(name, spacer, delete)
				: List.of(name, spacer));
		setText(null);
		setGraphic(row);
	}
}
