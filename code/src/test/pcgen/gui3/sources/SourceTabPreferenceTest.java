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

import pcgen.gui3.sources.SourceSelectionDialogPane.ActiveTabEnum;

import org.junit.jupiter.api.Test;

/**
 * Which tab the source dialog opens on is remembered between sessions, under a
 * key that states the opposite of the check box the user sees:
 * {@code SourceSelectionDialog.useBasic} is true when "Always use advanced
 * sources" is clear. The legacy dialog writes {@code !isSelected()} on close
 * and inverts it again on open.
 *
 * <p>The inversion is the whole of the logic and the whole of the risk, so it
 * is kept in one place with the round trip pinned. Getting it backwards would
 * not fail to compile and would not throw: the dialog would simply open on the
 * wrong tab, and the setting would appear to do the opposite of what it says.
 */
class SourceTabPreferenceTest
{
	@Test
	void storedUseBasic_opensOnTheBasicTab()
	{
		var preference = SourceTabPreference.fromStored(true);

		assertEquals(ActiveTabEnum.BASIC, preference.initialTab());
		assertFalse(preference.alwaysAdvanced(), "the check box is clear when Basic is the remembered tab");
	}

	@Test
	void storedAlwaysAdvanced_opensOnTheAdvancedTab()
	{
		var preference = SourceTabPreference.fromStored(false);

		assertEquals(ActiveTabEnum.ADVANCED, preference.initialTab());
		assertTrue(preference.alwaysAdvanced());
	}

	@Test
	void tickingTheBox_isStoredAsNotUsingBasic()
	{
		assertFalse(new SourceTabPreference(true).storedUseBasic());
	}

	@Test
	void clearingTheBox_isStoredAsUsingBasic()
	{
		assertTrue(new SourceTabPreference(false).storedUseBasic());
	}

	@Test
	void whatIsStoredIsWhatComesBack()
	{
		for (boolean alwaysAdvanced : new boolean[]{true, false})
		{
			var saved = new SourceTabPreference(alwaysAdvanced);

			var restored = SourceTabPreference.fromStored(saved.storedUseBasic());

			assertEquals(saved, restored, "a session must reopen on the tab the last one left set");
		}
	}

}
