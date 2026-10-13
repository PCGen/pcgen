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
import java.util.Optional;

import pcgen.system.LanguageBundle;

/**
 * Whether a name the user typed into Save Source Selection may be used, and
 * what to tell them if not.
 *
 * <p>The legacy Swing dialog asked none of this. It compared the typed name
 * only against <em>custom</em> selections, so naming one after a bundled
 * selection quietly produced a second row with the same label; it accepted an
 * empty name, which the engine stored happily; and it replaced an existing
 * selection without asking.
 *
 * <p>Names are compared exactly, as {@link SourceSelectionBridge} resolves
 * them, so the decision made here and the facade later written to cannot
 * disagree. Surrounding whitespace is trimmed first: it is invisible, and a
 * name that merely looks identical would be a separate selection.
 *
 * @param name the name to save under, trimmed
 * @param canSave whether saving may go ahead at all
 * @param needsConfirmation whether saving would replace an existing selection
 * @param message what to tell the user, if anything
 */
record SourceSaveDecision(String name, boolean canSave, boolean needsConfirmation,
		Optional<String> message)
{
	/**
	 * Decides whether {@code name} may be saved over {@code sources}.
	 *
	 * @param name the name as typed, possibly null or padded
	 * @param sources the selections already on offer
	 * @return the decision
	 */
	static SourceSaveDecision forName(String name, List<SourceBundle> sources)
	{
		String trimmed = name == null ? "" : name.trim();
		if (trimmed.isEmpty())
		{
			// The Save button is disabled for an empty name, so there is
			// nothing to explain: the user has not finished typing.
			return new SourceSaveDecision(trimmed, false, false, Optional.empty());
		}
		return switch (SourceSelectionBridge.classifyName(trimmed, sources))
		{
			case NEW -> new SourceSaveDecision(trimmed, true, false, Optional.empty());
			case OVERWRITE_CUSTOM -> new SourceSaveDecision(trimmed, true, true,
					Optional.of(LanguageBundle.getFormattedString("in_src_saveOverwrite", trimmed)));
			case BLOCK_BUILTIN -> new SourceSaveDecision(trimmed, false, false,
					Optional.of(LanguageBundle.getFormattedString("in_src_saveNameTaken", trimmed)));
		};
	}
}
