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
package pcgen.gui2;

import pcgen.gui2.tools.TipOfTheDayHandler;

import javafx.application.Platform;

/** Owns the single automatic tip opportunity for one main window's startup. */
final class StartupTipOfTheDay
{
	/** Read and consumed only on the JavaFX thread, including requests from load workers. */
	private boolean pending = true;

	/**
	 * Consumes the startup opportunity, showing tips on FX if the preference still permits it.
	 * Later source-dialog outcomes cannot show another automatic tip, even if the preference changes.
	 * May be called from any thread.
	 */
	void showIfEnabled()
	{
		Platform.runLater(() -> {
			if (pending)
			{
				pending = false;
				if (TipOfTheDayHandler.shouldShowTipOfTheDay())
				{
					PCGenFrame.showTipsOfTheDay();
				}
			}
		});
	}
}
