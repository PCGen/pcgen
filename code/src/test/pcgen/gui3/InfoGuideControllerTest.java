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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pcgen.system.LanguageBundle;

/**
 * Unit tests for the {@link InfoGuideController} plain-text string keys and
 * pure helper methods. These avoid the JavaFX toolkit, exercising only the
 * logic that does not touch live controls.
 */
class InfoGuideControllerTest
{
	@Test
	@DisplayName("plain-text string keys resolve to non-HTML values")
	void plainKeys_should_resolveWithoutHtml()
	{
		for (String key : new String[]{"in_si_title_plain", "in_si_nosources_plain",
			"in_si_whatnext_title_plain", "in_si_whatnext_new_plain",
			"in_si_whatnext_open_plain", "in_si_tip_title_plain"})
		{
			String value = LanguageBundle.getString(key);
			assertFalse(value.isEmpty(), key + " must be defined");
			assertFalse(value.contains("<"), key + " must be plain text, found markup: " + value);
		}
	}

	@Test
	@DisplayName("empty source list yields the no-sources hint, present")
	void sourceCardState_should_beEmptyHint_when_noSources()
	{
		InfoGuideController.SourceCardState state =
				InfoGuideController.sourceCardState(List.of());
		assertTrue(state.isEmpty());
		assertEquals(LanguageBundle.getString("in_si_nosources_plain"), state.hintText());
		assertTrue(state.sources().isEmpty());
	}

	@Test
	@DisplayName("non-empty source list is carried through, not empty")
	void sourceCardState_should_carrySources_when_present()
	{
		var names = List.of("Core Rulebook", "Advanced Guide");
		InfoGuideController.SourceCardState state = InfoGuideController.sourceCardState(names);
		assertFalse(state.isEmpty());
		assertEquals(names, state.sources());
	}

	@Test
	@DisplayName("FXML and CSS resources exist next to the controller")
	void resources_should_exist()
	{
		assertTrue(InfoGuideController.class.getResource("InfoGuidePanel.fxml") != null,
				"InfoGuidePanel.fxml must exist");
		assertTrue(InfoGuideController.class.getResource("InfoGuidePanel.css") != null,
				"InfoGuidePanel.css must exist");
	}
}
