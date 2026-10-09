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

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import pcgen.facade.core.SourceSelectionFacade;

/**
 * Engine boundary between the gui3 {@link SourceBundle} record and the legacy
 * {@link SourceSelectionFacade} world that {@code FacadeFactory} owns.
 * <p>
 * The dialog deliberately never sees facade types, so every translation back to
 * the engine lands here. Each method takes its engine collaborators as
 * parameters rather than reaching for {@code FacadeFactory} directly, which is
 * what lets the rules below be tested without the engine's global state.
 */
public final class SourceSelectionBridge
{
	private SourceSelectionBridge()
	{
	}

	/** Classification of a proposed Save name against the current source list. */
	enum NameClass
	{
		NEW, OVERWRITE_CUSTOM, BLOCK_BUILTIN
	}

	/**
	 * Finds the custom selection facade whose name ({@code toString()}) matches
	 * {@code name}, among {@code candidates} (typically
	 * {@code FacadeFactory.getCustomSourceSelections()}).
	 */
	static Optional<SourceSelectionFacade> resolveCustomFacade(
			String name, List<SourceSelectionFacade> candidates)
	{
		return candidates.stream()
				.filter(facade -> name.equals(facade.toString()))
				.findFirst();
	}

	/**
	 * Resolves {@code bundle} to its custom facade and hands it to
	 * {@code deleter}, reporting whether the delete actually happened. Returns
	 * false without calling the deleter when the bundle is absent, unnamed, not
	 * user-modifiable, or has no matching facade.
	 * <p>
	 * Callers must only drop the corresponding row on a true return.
	 * {@code FacadeFactory.deleteCustomSourceSelection} throws
	 * {@code IllegalArgumentException} for anything that is not a
	 * {@code CustomSourceSelectionFacade}, hence the modifiable guard.
	 */
	public static boolean deleteSourceSelection(
			SourceBundle bundle, List<SourceSelectionFacade> candidates,
			Consumer<SourceSelectionFacade> deleter)
	{
		if (bundle == null || bundle.name() == null || !bundle.modifiable())
		{
			return false;
		}
		// Matched on name(), never displayName(): an unnamed bundle falls back to
		// the game-mode name, which would delete whichever selection shares it.
		return resolveCustomFacade(bundle.name(), candidates)
				.map(facade -> {
					deleter.accept(facade);
					return true;
				})
				.orElse(false);
	}

	/**
	 * Classifies {@code name} against {@code sources}: a case-sensitive exact
	 * match on a modifiable (custom) bundle is OVERWRITE_CUSTOM; a match on a
	 * non-modifiable (bundled) one is BLOCK_BUILTIN; otherwise NEW.
	 */
	static NameClass classifyName(String name, List<SourceBundle> sources)
	{
		for (SourceBundle bundle : sources)
		{
			if (bundle.name() != null && bundle.name().equals(name))
			{
				return bundle.modifiable() ? NameClass.OVERWRITE_CUSTOM : NameClass.BLOCK_BUILTIN;
			}
		}
		return NameClass.NEW;
	}
}
