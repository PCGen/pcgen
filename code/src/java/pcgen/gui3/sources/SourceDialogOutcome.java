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

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 * What the caller should do with the button the source dialog closed on.
 *
 * <p>Every button in a {@code DialogPane}'s bar dismisses the dialog, because
 * {@code DialogPane.createButton} gives each one a handler that sets the result
 * and closes. The difference between them is therefore not whether the dialog
 * shuts but what the caller does next, which is decided here rather than in a
 * chain of comparisons at the call site.
 */
public final class SourceDialogOutcome
{
	private SourceDialogOutcome()
	{
	}

	/**
	 * Whether the highlighted selection should now be loaded.
	 *
	 * @param chosen the button the dialog closed on, or {@code null} if it was
	 *               dismissed without one
	 * @return true if the user asked to load
	 */
	public static boolean loadsSelection(ButtonType chosen)
	{
		return chosen != null && chosen.getButtonData() == ButtonData.OK_DONE;
	}

	/**
	 * Whether the data installer should now be opened.
	 *
	 * <p>Compared against the dialog's own button rather than by
	 * {@code ButtonData}: {@code LEFT} describes where a button sits in the bar,
	 * not what it does, so a second left-hand button would otherwise open the
	 * installer as well.
	 *
	 * @param chosen the button the dialog closed on, or {@code null} if it was
	 *               dismissed without one
	 * @param installData the dialog's Install Data button
	 * @return true if the user asked to install data
	 */
	public static boolean installsData(ButtonType chosen, ButtonType installData)
	{
		return chosen != null && chosen == installData;
	}
}
