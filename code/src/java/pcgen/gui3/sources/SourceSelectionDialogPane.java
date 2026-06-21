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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
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
	private ButtonType btnSave;

	@FXML
	private ButtonType btnAlways;

	@FXML
	private ButtonType btnDelete;

	private ObjectProperty<ActiveTabEnum> activeTab;

	public SourceSelectionDialogPane(ObservableList<SourceBundle> sources,
									 ObservableList<GameMode> gameModes)
	{
		this.sources = sources;
		this.gameModes = gameModes;

		var loader = new FXMLLoader(SourceSelectionDialogPane.class.getResource("SourceSelectionDialogPane.fxml"),
				LanguageBundle.getBundle());
		loader.setRoot(this);
		loader.setController(this);

		activeTabProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == ActiveTabEnum.BASIC)
			{
				this.getButtonTypes().remove(btnSave);
				this.getButtonTypes().remove(btnAlways);
				this.getButtonTypes().add(btnDelete);
			}
			else
			{
				this.getButtonTypes().add(btnSave);
				this.getButtonTypes().add(btnAlways);
				this.getButtonTypes().remove(btnDelete);
			}
		});

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
		advancedTabController.setGameModeSource(gameModes);
	}

	/**
	 * Wires the Load action so that double-clicking either list/tree, or
	 * pressing the dialog's OK button, runs the same handler. The handler is
	 * given the {@link SourceBundle} the user picked (if any).
	 */
	public void setOnLoadRequested(Runnable handler)
	{
		basicTabController.setOnLoadRequested(handler);
		advancedTabController.setOnLoadRequested(handler);
	}

	/**
	 * Registers the handler invoked when the user presses Unload All on the
	 * Advanced tab — typically {@code PCGenFrame.unloadSources()}.
	 */
	public void setOnUnloadAllRequested(Runnable handler)
	{
		advancedTabController.setOnUnloadAllRequested(handler);
	}

	/**
	 * The {@link SourceBundle} the user has chosen on the active tab, or
	 * empty if nothing is selected.
	 */
	public Optional<SourceBundle> getSelectedSource()
	{
		return getActiveTab() == ActiveTabEnum.ADVANCED
				? advancedTabController.getSelectedSource()
				: basicTabController.getSelectedSource();
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

	@Override
	protected Node createButton(ButtonType buttonType)
	{
		Node button;
		if (buttonType.getButtonData() == ButtonBar.ButtonData.SMALL_GAP)
		{
			var checkBox = new CheckBox();
			checkBox.setText(buttonType.getText());
			button = checkBox;
		}
		else
		{
			button = super.createButton(buttonType);
		}
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
