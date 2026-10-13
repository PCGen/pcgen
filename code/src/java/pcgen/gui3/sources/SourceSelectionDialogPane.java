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
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
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

	private ObjectProperty<ActiveTabEnum> activeTab;

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

	@FXML
	protected void onSelectionTabChanged(Event event)
	{
		var target = (Tab) event.getTarget();
		if (target.isSelected())
		{
			activeTabProperty().set(target == basicTab ? ActiveTabEnum.BASIC : ActiveTabEnum.ADVANCED);
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
		return activeTab == null ? ActiveTabEnum.BASIC : activeTab.get();
	}

	public final ObjectProperty<ActiveTabEnum> activeTabProperty()
	{
		if (activeTab == null)
		{
			activeTab = new SimpleObjectProperty<>(this, "activeTab", ActiveTabEnum.INITIAL)
			{
				@Override
				public void set(ActiveTabEnum newValue)
				{
					var model = tabSources.getSelectionModel();
					var tab = switch (newValue)
					{
						case INITIAL, BASIC -> basicTab;
						case ADVANCED -> advancedTab;
					};
					super.set(newValue);
					model.select(tab);
				}
			};
		}
		return activeTab;
	}

	enum ActiveTabEnum
	{
		INITIAL, BASIC, ADVANCED
	}
}
