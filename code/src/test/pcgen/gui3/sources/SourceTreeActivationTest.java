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
import static org.testfx.util.WaitForAsyncUtils.asyncFx;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import pcgen.core.Campaign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/** Exercises activation and key propagation through real tree rows and headers. */
@ExtendWith(ApplicationExtension.class)
class SourceTreeActivationTest
{
	private Stage stage;
	private TreeTableView<SourceTreeNode> tree;
	private int activations;
	private int escaped;

	@Start
	void start(Stage window)
	{
		stage = window;
	}

	@BeforeEach
	void buildTree() throws Exception
	{
		var campaign = new Campaign();
		campaign.setName("Campaign");
		asyncFx(() -> {
			var root = new TreeItem<SourceTreeNode>();
			var publisher = new TreeItem<SourceTreeNode>(new SourceTreeNode.Publisher("Publisher"));
			var leaf = new TreeItem<SourceTreeNode>(new SourceTreeNode.Leaf(campaign));
			publisher.getChildren().add(leaf);
			publisher.setExpanded(true);
			root.getChildren().add(publisher);
			tree = new TreeTableView<>(root);
			tree.setShowRoot(false);
			var column = new TreeTableColumn<SourceTreeNode, String>("Name");
			column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getValue().displayLabel()));
			tree.getColumns().add(column);
			AdvancedSourceSelectionController.activateRowsWith(tree, () -> {
				if (!tree.getSelectionModel().isEmpty())
				{
					activations++;
				}
			});
			tree.getSelectionModel().select(leaf);
			var parent = new StackPane(tree);
			parent.addEventHandler(KeyEvent.KEY_PRESSED, _ -> escaped++);
			stage.setScene(new Scene(parent, 400, 300));
			stage.show();
			parent.applyCss();
			parent.layout();
		}).get(5, TimeUnit.SECONDS);
	}

	@ParameterizedTest
	@CsvSource({"ENTER, true, 1, 0", "ENTER, false, 0, 0", "A, true, 0, 1", "ESCAPE, true, 0, 1"})
	void keys_should_activateOrPropagate(KeyCode key, boolean selected, int expectedActivations,
			int expectedEscaped) throws Exception
	{
		asyncFx(() -> {
			if (!selected)
			{
				tree.getSelectionModel().clearSelection();
			}
			tree.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key, false, false, false, false));
			assertEquals(expectedActivations, activations);
			assertEquals(expectedEscaped, escaped);
		}).get(5, TimeUnit.SECONDS);
	}

	@ParameterizedTest
	@CsvSource({
		"campaign, PRIMARY, 2, 1", "campaign, PRIMARY, 1, 0", "campaign, SECONDARY, 2, 0",
		"publisher, PRIMARY, 2, 0", "empty, PRIMARY, 2, 0", "header, PRIMARY, 2, 0"
	})
	void clicks_should_activateOnlyCampaignRows(String target, MouseButton button, int count,
			int expected) throws Exception
	{
		asyncFx(() -> {
			Node clicked = switch (target)
			{
				case "campaign" -> rowMatching(row -> row.getItem() instanceof SourceTreeNode.Leaf);
				case "publisher" -> rowMatching(row -> row.getItem() instanceof SourceTreeNode.Publisher);
				case "empty" -> rowMatching(TreeTableRow::isEmpty);
				case "header" -> tree.lookup(".column-header");
				default -> throw new IllegalArgumentException(target);
			};
			clicked.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, button, count,
					false, false, false, false, false, false, false, false, false, false, null));
			assertEquals(expected, activations);
		}).get(5, TimeUnit.SECONDS);
	}

	private TreeTableRow<?> rowMatching(Predicate<TreeTableRow<?>> predicate)
	{
		return tree.lookupAll(".tree-table-row-cell").stream()
				.map(node -> (TreeTableRow<?>) node)
				.filter(predicate)
				.findFirst().orElseThrow();
	}
}
