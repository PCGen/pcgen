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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.List;
import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import pcgen.core.GameMode;
import pcgen.system.LanguageBundle;

/**
 * The two-tab source-selection dialog body. Holds an immutable list of
 * pre-defined {@link SourceBundle}s and the supported {@link GameMode}s; the
 * caller drives both via JavaFX-native {@code ObservableList}s. Use
 * {@link #getSelectedSource()} after the dialog returns to find out what the
 * user picked.
 *
 * <p>Only dialog-global actions belong in the button bar. Tab-specific actions
 * live inside their own tab — Save and Always-Advanced in the Advanced tab's
 * action rows, Delete on the Basic tab's individual rows — for two reasons:
 * adding or hiding a {@link ButtonType} per tab re-flows the bar and makes the
 * buttons jump on every tab switch, and any {@code ButtonType} button dismisses
 * the dialog unless its action event is consumed.
 */
public class SourceSelectionDialogPane extends DialogPane
{
	private final ObservableList<SourceBundle> sources;
	private final ObservableList<GameMode> gameModes;

	@FXML
	private TabPane tabSources;

	@FXML
	private Tab basicTab;

	@FXML
	private BasicSourceSelectionController basicTabController;

	@FXML
	private AdvancedSourceSelectionController advancedTabController;

	@FXML
	private Tab advancedTab;

	@FXML
	private ButtonType btnInstallData;

	/**
	 * Which tab is in front. A plain field, not an observable property: nothing
	 * binds to it or observes it, and the lazy-property idiom it used to follow
	 * left two different answers for one question -- null meant BASIC, while an
	 * instantiated-but-unset property meant INITIAL, so merely calling the
	 * accessor changed what the getter reported.
	 */
	private ActiveTabEnum activeTab = ActiveTabEnum.BASIC;

	private final SourceSelectionModel model = new SourceSelectionModel();

	public SourceSelectionDialogPane(ObservableList<SourceBundle> sources,
									 ObservableList<GameMode> gameModes)
	{
		this.sources = sources;
		this.gameModes = gameModes;

		var loader = new FXMLLoader(SourceSelectionDialogPane.class.getResource("SourceSelectionDialogPane.fxml"),
				LanguageBundle.getBundle());
		loader.setRoot(this);
		loader.setController(this);

		try
		{
			loader.load();
		}
		catch (IOException exception)
		{
			throw new UncheckedIOException(exception);
		}
	}

	@FXML
	protected void initialize()
	{
		basicTabController.setSources(sources);
		// Items before model: setModel resolves the saved default against them.
		advancedTabController.setGameModeSource(gameModes);
		advancedTabController.setModel(model);

		basicTabController.selectedSourceProperty().addListener(
				(obs, old, bundle) -> project(model, bundle));
		basicTabController.getSelectedSource().ifPresent(bundle -> project(model, bundle));

		trackActiveTab(tabSources, basicTab, tab -> activeTab = tab);
	}

	/**
	 * Projects a Basic-tab {@link SourceBundle} onto the shared model: adopts its
	 * game mode, then replaces the campaign set. A null bundle (nothing selected)
	 * is ignored. One-way only — nothing here reads the model back into Basic.
	 */
	static void project(SourceSelectionModel model, SourceBundle bundle)
	{
		if (bundle == null)
		{
			return;
		}
		// setGameMode fires the Advanced tab's per-mode restore; setAll then
		// overrides it, so a Basic projection always wins over remembered sources
		// (restore is for a manual mode-switch on the Advanced tab).
		model.setGameMode(bundle.gameMode());
		model.getSelectedCampaigns().setAll(bundle.campaigns());
	}

	/**
	 * Wires the Load action so double-clicking a Basic row or pressing OK runs
	 * {@code handler}.
	 *
	 * <p>The Advanced tab does not take part: its rows move sources between its
	 * two trees rather than committing the dialog, so a gesture there must not
	 * load anything.
	 */
	public void setOnLoadRequested(Runnable handler)
	{
		basicTabController.setOnLoadRequested(handler);
	}

	/**
	 * Registers the handler for Save Source Selection, invoked with the
	 * Advanced tab's campaign set under the name the user chose.
	 *
	 * <p>The Advanced tab also needs to see the selections already on offer, or
	 * every name looks free and a save would shadow a bundled selection or
	 * duplicate a saved one.
	 *
	 * @param handler stores the selection, typically via
	 *                {@code SourceSelectionBridge.saveSourceSelection}
	 */
	public void setOnSaveRequested(Consumer<SourceBundle> handler)
	{
		advancedTabController.setOnSaveRequested(handler);
		advancedTabController.setExistingSources(() -> List.copyOf(sources));
	}

	/**
	 * Registers the handler for deleting a custom source selection, invoked with
	 * the bundle whose row trash icon was clicked (or that was selected when the
	 * DELETE key was pressed). The controller has already confirmed with the user.
	 */
	public void setOnDeleteRequested(Consumer<SourceBundle> handler)
	{
		basicTabController.setOnDeleteRequested(handler);
	}

	/**
	 * Registers the handler for the Advanced tab's Unload All button — typically
	 * {@code PCGenFrame.unloadSources()}.
	 */
	public void setOnUnloadAllRequested(Runnable handler)
	{
		advancedTabController.setOnUnloadAllRequested(handler);
	}

	/**
	 * The button that leaves this dialog for the data installer.
	 *
	 * <p>Every button in the bar dismisses the dialog, so the caller tells this
	 * one apart by identity once the dialog has closed; see
	 * {@link SourceDialogOutcome}.
	 *
	 * @return the Install Data button
	 */
	public ButtonType getInstallDataButton()
	{
		return btnInstallData;
	}

	/**
	 * Highlights {@code bundle} in the Basic list without changing tab, so a
	 * save made from the Advanced tab is visible without moving the user off
	 * the tab they are working in.
	 *
	 * @param bundle the selection to highlight
	 */
	public void selectSource(SourceBundle bundle)
	{
		basicTabController.selectSource(bundle);
	}

	/** The {@link SourceBundle} chosen on the active tab, or empty if none. */
	public Optional<SourceBundle> getSelectedSource()
	{
		return getActiveTab() == ActiveTabEnum.ADVANCED
				? advancedTabController.getSelectedSource()
				: basicTabController.getSelectedSource();
	}

	/**
	 * Persists the Advanced tab's choice (remembered game mode + per-mode
	 * campaigns). Call only when the dialog is committed with Load/OK.
	 */
	public void commitSelection(SourceBundle loaded)
	{
		// Only the Advanced tab curates per-mode source memory; loading from the Basic tab must not overwrite it.
		if (getActiveTab() == ActiveTabEnum.ADVANCED)
		{
			advancedTabController.commitSelection(loaded);
		}
	}

	/**
	 * Keeps each button at its natural width. The bar mixes a wide action
	 * ("Install Data") with short ones, and uniform sizing would stretch them all
	 * to the widest.
	 */
	@Override
	protected Node createButton(ButtonType buttonType)
	{
		Node button = super.createButton(buttonType);
		ButtonBar.setButtonUniformSize(button, false);
		return button;
	}

	public final ActiveTabEnum getActiveTab()
	{
		return activeTab;
	}

	/**
	 * Reports which tab is in front, now and on every change.
	 *
	 * <p>Taken from the selection model rather than from a selection-changed
	 * event. {@code Event.getTarget()} is typed only as {@code EventTarget} --
	 * {@code Node}, {@code Window}, {@code Scene}, {@code MenuItem} and
	 * {@code TreeItem} all implement it -- so reading the tab out of the event
	 * needed a cast whose correctness rested on an FXML attribute staying
	 * attached to a {@code Tab}, with nothing to check it until it threw. The
	 * event also fired for the tab being left as well as the one being
	 * entered, so the handler ran twice per switch and filtered the other out
	 * by hand.
	 *
	 * <p>The current tab is reported immediately, so no caller has to cope with
	 * a period before the first switch when the answer is unknown.
	 *
	 * @param tabs the tab pane to follow
	 * @param basicTab the tab that counts as {@link ActiveTabEnum#BASIC}
	 * @param sink told which tab is in front
	 */
	static void trackActiveTab(TabPane tabs, Tab basicTab, Consumer<ActiveTabEnum> sink)
	{
		var selection = tabs.getSelectionModel();
		selection.selectedItemProperty().addListener(
				(_, _, tab) -> sink.accept(activeTabFor(tab, basicTab)));
		sink.accept(activeTabFor(selection.getSelectedItem(), basicTab));
	}

	private static ActiveTabEnum activeTabFor(Tab selected, Tab basicTab)
	{
		return selected == basicTab ? ActiveTabEnum.BASIC : ActiveTabEnum.ADVANCED;
	}

	public enum ActiveTabEnum
	{
		BASIC, ADVANCED
	}
}
