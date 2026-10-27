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

import pcgen.gui3.sources.SourceSelectionDialogPane.ActiveTabEnum;

/**
 * Which tab the source dialog should open on, remembered between sessions.
 *
 * <p>The stored key states the opposite of the check box the user sees:
 * {@code UIPropertyContext.SOURCE_USE_BASIC_KEY} is true exactly when "Always
 * use advanced sources" is clear. That inversion is the whole of the logic
 * here and the whole of the risk — getting it backwards neither fails to
 * compile nor throws, it just makes the setting do the opposite of what it
 * says — so it lives in one place with the round trip under test.
 *
 * <p>The key is shared with the legacy dialog and with the Sources page of
 * Preferences, so the three stay in agreement.
 *
 * @param alwaysAdvanced whether the dialog should open on the Advanced tab
 */
public record SourceTabPreference(boolean alwaysAdvanced)
{
	/**
	 * Reads the preference as stored.
	 *
	 * @param useBasic the stored {@code SOURCE_USE_BASIC_KEY}, true by default
	 * @return the preference
	 */
	public static SourceTabPreference fromStored(boolean useBasic)
	{
		return new SourceTabPreference(!useBasic);
	}

	/**
	 * The value to store.
	 *
	 * @return what {@code SOURCE_USE_BASIC_KEY} should be set to
	 */
	public boolean storedUseBasic()
	{
		return !alwaysAdvanced;
	}

	/**
	 * The tab to show when the dialog opens.
	 *
	 * @return the tab
	 */
	public ActiveTabEnum initialTab()
	{
		return alwaysAdvanced ? ActiveTabEnum.ADVANCED : ActiveTabEnum.BASIC;
	}
}
