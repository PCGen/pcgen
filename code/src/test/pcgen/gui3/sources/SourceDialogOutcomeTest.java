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
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

import org.junit.jupiter.api.Test;

/**
 * "Install Data" leaves the source dialog and opens the data installer, as it
 * does in the legacy Swing dialog. Both halves matter and they are easy to get
 * wrong in opposite directions: the button is a {@link ButtonType}, and
 * {@code DialogPane.createButton} gives every button it builds a handler that
 * sets the dialog's result and closes it, so the dismissal comes for free while
 * the installer does not.
 *
 * <p>These tests cover the decision the caller has to make when the dialog
 * returns, which is where the two are told apart: the chosen button closed the
 * dialog either to load a selection, to install data, or to cancel.
 */
class SourceDialogOutcomeTest
{
	private static final ButtonType INSTALL =
			new ButtonType("Install Data", ButtonData.LEFT);

	@Test
	void installData_asksForTheInstaller()
	{
		assertTrue(SourceDialogOutcome.installsData(INSTALL, INSTALL));
	}

	@Test
	void installData_doesNotLoadTheSelection()
	{
		assertFalse(SourceDialogOutcome.loadsSelection(INSTALL),
				"leaving to install data must not also load whatever was highlighted");
	}

	@Test
	void load_loadsTheSelection()
	{
		assertTrue(SourceDialogOutcome.loadsSelection(ButtonType.OK));
	}

	@Test
	void load_doesNotAskForTheInstaller()
	{
		assertFalse(SourceDialogOutcome.installsData(ButtonType.OK, INSTALL));
	}

	@Test
	void cancel_doesNeither()
	{
		assertFalse(SourceDialogOutcome.loadsSelection(ButtonType.CANCEL));
		assertFalse(SourceDialogOutcome.installsData(ButtonType.CANCEL, INSTALL));
	}

	/**
	 * Matched by identity against the dialog's own button, not by its
	 * {@code ButtonData}: {@code LEFT} says where a button sits in the bar, not
	 * what it does, and a second left-hand button would otherwise open the
	 * installer too.
	 */
	@Test
	void anotherLeftHandButton_doesNotAskForTheInstaller()
	{
		var other = new ButtonType("Something Else", ButtonData.LEFT);

		assertFalse(SourceDialogOutcome.installsData(other, INSTALL));
	}

	/** A dialog closed by the window manager yields no button at all. */
	@Test
	void noButton_doesNeither()
	{
		assertFalse(SourceDialogOutcome.loadsSelection(null));
		assertFalse(SourceDialogOutcome.installsData(null, INSTALL));
	}

	/**
	 * Guards the premise of the tests above: OK is what the Load button is
	 * declared as in the dialog's FXML.
	 */
	@Test
	void sanity_loadIsAnOkDoneButton()
	{
		assertEquals(ButtonData.OK_DONE, ButtonType.OK.getButtonData());
	}
}
