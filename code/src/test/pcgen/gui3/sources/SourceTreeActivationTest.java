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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

/**
 * The source dialog's Load button is {@code ButtonData.OK_DONE}, which makes
 * it the scene's default button, so any Enter that reaches the dialog commits
 * the whole selection and closes it. That is not what Enter means with a row
 * selected in a list of sources, where it should act on the row.
 *
 * <p>These tests cover the gesture wiring shared by both Advanced trees: which
 * gestures activate a row, and -- just as importantly -- that Enter is
 * swallowed so it can never reach the default button.
 */
@ExtendWith(ApplicationExtension.class)
class SourceTreeActivationTest
{
	private final AtomicInteger activations = new AtomicInteger();
	private TreeTableView<SourceTreeNode> tree;

	/** Events that got past the tree to its parent, as one would to the dialog. */
	private final AtomicInteger escaped = new AtomicInteger();

	/**
	 * A control may only be built on the FX thread; the test methods run on the
	 * "Test worker" thread, so construction cannot go in a field initialiser.
	 *
	 * <p>The tree is given a parent that counts what reaches it. Asserting on
	 * the fired event's own {@code isConsumed()} would prove nothing: event
	 * dispatch copies the event, so the copy is what gets consumed.
	 */
	@BeforeEach
	void buildTree() throws Exception
	{
		java.util.concurrent.Callable<TreeTableView<SourceTreeNode>> build = () -> {
			var control = new TreeTableView<SourceTreeNode>();
			var parent = new StackPane(control);
			new Scene(parent);
			parent.addEventHandler(KeyEvent.KEY_PRESSED, _ -> escaped.incrementAndGet());
			return control;
		};
		tree = WaitForAsyncUtils.asyncFx(build).get(5, TimeUnit.SECONDS);
	}

	private void install()
	{
		install(activations::incrementAndGet);
	}

	private void install(Runnable action)
	{
		AdvancedSourceSelectionController.activateRowsWith(tree, action);
	}

	/** Events are delivered on the FX thread, as they are in a running dialog. */
	private void fire(Event event) throws Exception
	{
		WaitForAsyncUtils.asyncFx(() -> Event.fireEvent(tree, event)).get(5, TimeUnit.SECONDS);
	}

	private static KeyEvent keyPress(KeyCode code)
	{
		return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false);
	}

	private static MouseEvent click(MouseButton button, int count)
	{
		return new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, button, count,
				false, false, false, false, button == MouseButton.PRIMARY, false, false,
				false, false, true, null);
	}

	@Test
	void enter_activatesTheRow() throws Exception
	{
		install();

		fire(keyPress(KeyCode.ENTER));

		assertEquals(1, activations.get());
	}

	/**
	 * The regression this exists for: an Enter that bubbles out of the tree
	 * reaches the default Load button and dismisses the dialog.
	 */
	@Test
	void enter_isSwallowed_soItCannotReachTheDefaultButton() throws Exception
	{
		install();

		fire(keyPress(KeyCode.ENTER));

		assertEquals(0, escaped.get(), "Enter must not escape the tree to the dialog's Load button");
	}

	/**
	 * Enter is swallowed whether or not it had anything to act on. Consuming
	 * it only when a row is selected would make the dialog close depending on
	 * where the highlight happens to be.
	 */
	@Test
	void enter_isSwallowedEvenWithNothingToActOn() throws Exception
	{
		install(() -> { });

		fire(keyPress(KeyCode.ENTER));

		assertEquals(0, escaped.get());
	}

	@Test
	void otherKeys_areLeftAlone() throws Exception
	{
		install();

		fire(keyPress(KeyCode.A));

		assertEquals(0, activations.get());
		assertEquals(1, escaped.get(), "only Enter is ours to swallow");
	}

	/**
	 * Escape must keep reaching the dialog, or the tree becomes a place the
	 * user cannot cancel from.
	 */
	@Test
	void escape_isLeftAlone() throws Exception
	{
		install();

		fire(keyPress(KeyCode.ESCAPE));

		assertEquals(1, escaped.get(), "Escape must still reach the dialog, or this tree cannot be left");
	}

	@Test
	void doubleClick_activatesTheRow() throws Exception
	{
		install();

		fire(click(MouseButton.PRIMARY, 2));

		assertEquals(1, activations.get());
	}

	@Test
	void singleClick_doesNotActivateTheRow() throws Exception
	{
		install();

		fire(click(MouseButton.PRIMARY, 1));

		assertEquals(0, activations.get());
	}

	@Test
	void rightDoubleClick_doesNotActivateTheRow() throws Exception
	{
		install();

		fire(click(MouseButton.SECONDARY, 2));

		assertEquals(0, activations.get());
	}

	@Test
	void eachGesture_activatesOnce() throws Exception
	{
		install();

		fire(keyPress(KeyCode.ENTER));
		fire(click(MouseButton.PRIMARY, 2));

		assertEquals(2, activations.get());
	}
}
