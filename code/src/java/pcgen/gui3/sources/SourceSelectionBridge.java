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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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
	 * false without calling the deleter when no matching facade remains.
	 * <p>
	 * Callers must only drop the corresponding row on a true return.
	 * The controller supplies a named custom selection after confirming deletion.
	 */
	public static boolean deleteSourceSelection(
			SourceBundle bundle, List<SourceSelectionFacade> candidates,
			Consumer<SourceSelectionFacade> deleter)
	{
		return resolveCustomFacade(bundle.name(), candidates)
				.map(facade -> {
					deleter.accept(facade);
					return true;
				})
				.orElse(false);
	}

	/**
	 * Stores {@code bundle} as a custom source selection, reusing the facade
	 * already kept under its name or asking {@code creator} for a new one.
	 *
	 * <p>Reusing matters: {@code FacadeFactory.createCustomSourceSelection}
	 * appends unconditionally, so creating one under a name already taken
	 * leaves the user with two entries they cannot tell apart. Names are
	 * matched exactly, as {@link #classifyName} does, so the dialog's decision
	 * to overwrite and the facade it then overwrites cannot disagree.
	 *
	 * <p>The controller supplies a named selection accepted by
	 * {@link AdvancedSourceSelectionController#plannedSave}.
	 *
	 * @param bundle the selection to store
	 * @param candidates the existing custom selections, typically
	 *                   {@code FacadeFactory.getCustomSourceSelections()}
	 * @param creator makes a new custom selection of a given name, typically
	 *                {@code FacadeFactory::createCustomSourceSelection}
	 * @return the facade written to
	 */
	public static SourceSelectionFacade saveSourceSelection(
			SourceBundle bundle, List<SourceSelectionFacade> candidates,
			Function<String, SourceSelectionFacade> creator)
	{
		String name = bundle.name();
		SourceSelectionFacade facade =
				resolveCustomFacade(name, candidates).orElseGet(() -> creator.apply(name));
		facade.setCampaigns(bundle.campaigns());
		facade.setGameMode(bundle.gameMode());
		return facade;
	}

	/**
	 * Classifies {@code name} against {@code sources}: a case-sensitive exact
	 * match on a modifiable (custom) bundle is OVERWRITE_CUSTOM; a match on a
	 * non-modifiable (bundled) one is BLOCK_BUILTIN; otherwise NEW.
	 */
	static NameClass classifyName(String name, List<SourceBundle> sources)
	{
		return sources.stream()
				.filter(bundle -> Objects.equals(bundle.name(), name))
				.findFirst()
				.map(bundle -> bundle.modifiable() ? NameClass.OVERWRITE_CUSTOM : NameClass.BLOCK_BUILTIN)
				.orElse(NameClass.NEW);
	}
}
