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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import javafx.util.Callback;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.campaign.CampaignAdditionPlanner;
import pcgen.core.campaign.CampaignAvailability;
import pcgen.core.campaign.CampaignRejectionDiagnosis;
import pcgen.core.campaign.CampaignDependencyOrder;
import pcgen.core.GameMode;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;

public class AdvancedSourceSelectionController
{
	private static final UIPropertyContext CONTEXT =
			UIPropertyContext.createContext("advancedSourceSelectionPanel"); //$NON-NLS-1$
	private static final String PROP_SELECTED_GAME = "selectedGame"; //$NON-NLS-1$
	private static final String PROP_SELECTED_SOURCES = "selectedSources."; //$NON-NLS-1$

	private static final Logger LOG = Logger.getLogger(AdvancedSourceSelectionController.class.getName());

	@FXML
	private Button btnFilterClear;

	@FXML
	private Button btnAddSelected;

	@FXML
	private Button btnRemoveSelected;

	@FXML
	private Button btnUnloadAll;

	@FXML
	private Button btnSave;

	@FXML
	private CheckBox chkAlwaysAdvanced;

	@FXML
	private TextField fldSearch;

	@FXML
	private ComboBox<GameMode> cmbGameMode;

	@FXML
	private TreeTableView<SourceTreeNode> treeAvailable;

	@FXML
	private TreeTableColumn<SourceTreeNode, String> colAvailableName;

	@FXML
	private TreeTableColumn<SourceTreeNode, String> colAvailableBookType;

	@FXML
	private TreeTableColumn<SourceTreeNode, String> colAvailableStatus;

	@FXML
	private TreeTableColumn<SourceTreeNode, String> colAvailableLoaded;

	@FXML
	private TreeTableView<SourceTreeNode> treeSelected;

	@FXML
	private TreeTableColumn<SourceTreeNode, String> colSelectedName;

	@FXML
	private WebView infoPane;

	/**
	 * The campaigns chosen on this tab. Backed by {@link #model} once
	 * {@link #setModel} runs; an empty standalone list until then, so the
	 * controller is usable in isolation (e.g. FXML preview).
	 */
	private ObservableList<Campaign> selectedCampaigns = FXCollections.observableArrayList();

	private SourceSelectionModel model;

	private Runnable onUnloadAllRequested = () -> { };
	/** Title shared by the Save prompt and both of its outcomes. */
	private static final String SAVE_TITLE = "in_src_saveTitle";

	private Consumer<SourceBundle> onSaveRequested = _ -> { };

	/** The sources already on offer, for deciding whether a Save name is free. */
	private Supplier<List<SourceBundle>> existingSources = List::of;

	/** Asks the user what to call the selection. Replaceable so tests raise no modal. */
	private Function<String, Optional<String>> namePrompt = AdvancedSourceSelectionController::promptForName;

	/** Asks before replacing a saved selection. Replaceable so tests raise no modal. */
	private Predicate<SourceSaveDecision> saveConfirmation =
			AdvancedSourceSelectionController::confirmOverwrite;

	/**
	 * How each offered campaign stands against the current selection, for the
	 * available tree's row marking. Recomputed whenever the selection or the
	 * offered list changes rather than per cell repaint: the cost is one engine
	 * call per campaign, which is cheap once but not on every scroll tick.
	 */
	private Map<Campaign, CampaignAvailability.Status> availability = Map.of();

	/**
	 * Why each marked campaign is marked, for its tooltip. Computed with the
	 * statuses rather than in the cell: diagnosing a campaign costs two engine
	 * calls, which is affordable once per selection change but not on every
	 * repaint of every visible row.
	 */
	private Map<Campaign, String> availabilityReasons = Map.of();

	@FXML
	protected void initialize()
	{
		LOG.fine("Initialize AdvancedSourceSelectionController");
		btnFilterClear.setGraphic(new ImageView(pcgen.gui2.tools.Icons.CloseX9.asJavaFX()));
		cmbGameMode.setCellFactory(new GameModeCellFactory());

		treeAvailable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		treeSelected.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		treeAvailable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		treeSelected.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		bindAvailableColumns();
		bindSelectedColumns();

		// Either tree drives the bottom info pane: whichever was clicked last
		// shows its leaf's HTML. The legacy mirrors this — the panel listens to
		// both selection models with the same handler.
		treeAvailable.getSelectionModel().selectedItemProperty()
				.addListener((obs, old, item) -> showInfoFor(item));
		treeSelected.getSelectionModel().selectedItemProperty()
				.addListener((obs, old, item) -> showInfoFor(item));

		// Each tree acts on its own rows: the available tree adds them, the
		// selected tree takes them back.
		activateRowsWith(treeAvailable, () -> onAddSelectedAction(null));
		activateRowsWith(treeSelected, () -> onRemoveSelectedAction(null));

		// Enter in the search field is swallowed so it does not bubble to the
		// dialog's default Load button and close the dialog mid-search; filtering
		// is live via the searchQuery binding (see setModel).
		fldSearch.setOnAction(ActionEvent::consume);
	}

	/**
	 * Makes Enter and a double-click act on the rows of {@code control}.
	 *
	 * <p>Enter is swallowed whether or not it had anything to act on. The
	 * dialog's Load button is {@code ButtonData.OK_DONE}, which
	 * {@code DialogPane.createButton} makes the scene's default button, so any
	 * Enter that escapes a control commits the whole selection and closes the
	 * dialog -- which is not what Enter means with a source highlighted.
	 * Consuming it only when a row happened to be selected would leave the
	 * dialog closing or not depending on where the highlight sat.
	 *
	 * <p>Escape and every other key are left alone, so the dialog can still be
	 * cancelled from here.
	 *
	 * @param control the list or tree whose rows are to be actionable
	 * @param action what to do with the selected rows
	 */
	static void activateRowsWith(Control control, Runnable action)
	{
		control.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER)
			{
				event.consume();
				action.run();
			}
		});
		control.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
			{
				action.run();
			}
		});
	}

	private void bindAvailableColumns()
	{
		colAvailableName.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(v.getValue().getValue().displayLabel()));
		colAvailableName.setCellFactory(_ ->
				new SourceAvailabilityCell(() -> availability, availabilityReasons::get));
		colAvailableBookType.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(campaignOf(v.getValue())
						.map(c -> c.getListAsString(ListKey.BOOK_TYPE)).orElse("")));
		colAvailableStatus.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(campaignOf(v.getValue())
						.map(c -> c.getSafe(ObjectKey.STATUS).toString()).orElse("")));
		colAvailableLoaded.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(campaignOf(v.getValue()).isPresent() ? "Loaded" : "Not loaded"));
	}

	private void bindSelectedColumns()
	{
		colSelectedName.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(v.getValue().getValue().displayLabel()));
	}

	@FXML
	protected void onFilterClearAction(ActionEvent actionEvent)
	{
		fldSearch.setText("");
	}

	/**
	 * Copies every focused leaf in the available tree into the selected list.
	 *
	 * <p>The whole resulting set is validated in one go. Validating candidates
	 * one at a time, as this and the legacy Swing panel used to, rejected a
	 * campaign whose prerequisite sorted later in the tree even when the user
	 * had selected both -- see {@link CampaignAdditionPlanner}. Anything still
	 * refused is reported once, together, rather than one modal per campaign.
	 *
	 * <p>Candidates are offered most-depended-upon first so that any conflict
	 * between mutually exclusive books is settled in favour of the one other
	 * books need, rather than the one that happens to come first in the tree.
	 */
	@FXML
	protected void onAddSelectedAction(ActionEvent event)
	{
		var plan = CampaignAdditionPlanner.plan(
				List.copyOf(selectedCampaigns),
				CampaignDependencyOrder.mostDependedUponFirst(selectedLeavesIn(treeAvailable).toList()),
				FacadeFactory::findUnqualified);
		selectedCampaigns.addAll(plan.accepted());
		warnBadCombo(plan.rejected());
	}

	@FXML
	protected void onRemoveSelectedAction(ActionEvent event)
	{
		selectedLeavesIn(treeSelected).toList().forEach(selectedCampaigns::remove);
	}

	@FXML
	protected void onUnloadAllAction(ActionEvent event)
	{
		onUnloadAllRequested.run();
		selectedCampaigns.clear();
	}

	/**
	 * Reports the campaigns that could not be added, saying why.
	 *
	 * <p>The engine only ever answers "this set does not qualify", so the
	 * dialog used to quote each campaign's own prerequisite expression. That
	 * describes the rule rather than the situation, and is least helpful
	 * exactly when the situation is surprising -- a user who selected every
	 * Pathfinder book while a MultiSet collection was already selected was
	 * shown fourteen expressions and no hint that one selected collection was
	 * behind all of them. {@link CampaignRejectionDiagnosis} works out which of
	 * three things actually happened, and each gets the message that fits it.
	 *
	 * <p>Requirements are still described against the selection as it now
	 * stands, so they answer "what is missing from what you have" rather than
	 * from some intermediate set the user never saw.
	 */
	private void warnBadCombo(List<Campaign> rejected)
	{
		if (rejected.isEmpty())
		{
			return;
		}
		var context = List.copyOf(selectedCampaigns);
		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				rejected, context, Campaign::getSubCampaigns, FacadeFactory::findUnqualified);
		var problems = new ArrayList<BadComboAlert.Rejection>();
		var covered = new ArrayList<BadComboAlert.Covered>();
		for (var diagnosis : diagnoses)
		{
			var campaign = diagnosis.campaign();
			if (diagnosis.reason() instanceof CampaignRejectionDiagnosis.AlreadyIncluded(Campaign container))
			{
				covered.add(new BadComboAlert.Covered(
						campaign.getDisplayName(), container.getDisplayName()));
			}
			else
			{
				problems.add(new BadComboAlert.Rejection(
						campaign.getDisplayName(), explain(diagnosis.reason(), campaign, context)));
			}
		}
		BadComboAlert.show(List.copyOf(problems), List.copyOf(covered), ownerWindow());
	}

	/**
	 * The markup describing why a campaign was refused.
	 *
	 * <p>A conflict with something already selected is named outright, because
	 * the campaign's own requirements never mention it: the obstacle lives in
	 * the other campaign's {@code !PRECAMPAIGN}. Anything else falls back to
	 * the engine's description, which for a genuinely unmet requirement is the
	 * best answer available.
	 */
	private static String explain(CampaignRejectionDiagnosis.Reason reason, Campaign campaign,
			List<Campaign> context)
	{
		if (reason instanceof CampaignRejectionDiagnosis.ConflictsWith(List<Campaign> blockers))
		{
			return BadComboAlert.blockedBySelected(
					blockers.stream().map(Campaign::getDisplayName).toList());
		}
		return FacadeFactory.getCampaignInfoFactory().getRequirementsHTMLString(campaign, context);
	}

	/**
	 * The window this panel lives in, so dialogs raised from it open over it
	 * rather than wherever the window manager decides.
	 */
	private Window ownerWindow()
	{
		var scene = treeAvailable == null ? null : treeAvailable.getScene();
		return scene == null ? null : scene.getWindow();
	}

	private void showInfoFor(TreeItem<SourceTreeNode> item)
	{
		if (item == null)
		{
			SourceInfoRenderer.clear(infoPane.getEngine());
			return;
		}
		campaignOf(item).ifPresentOrElse(
				c -> SourceInfoRenderer.renderCampaign(infoPane.getEngine(), c),
				() -> SourceInfoRenderer.clear(infoPane.getEngine()));
	}

	/**
	 * Registers the action to invoke for Unload All — typically
	 * {@code PCGenFrame.unloadSources()}. The selected list is cleared
	 * unconditionally afterwards on this side.
	 */
	public void setOnUnloadAllRequested(Runnable handler)
	{
		this.onUnloadAllRequested = handler == null ? () -> { } : handler;
	}

	/**
	 * Registers the action to invoke for Save Source Selection — persisting this
	 * tab's campaign set as a named custom selection.
	 */
	public void setOnSaveRequested(Consumer<SourceBundle> handler)
	{
		this.onSaveRequested = handler == null ? _ -> { } : handler;
	}

	/**
	 * Supplies the selections already on offer, so a Save name can be checked
	 * against them. Without this every name looks free and the dialog would
	 * shadow a bundled selection or silently duplicate a saved one.
	 */
	public void setExistingSources(Supplier<List<SourceBundle>> sources)
	{
		this.existingSources = sources == null ? List::of : sources;
	}

	/** Overrides the name prompt, so tests need not raise a modal. */
	void setNamePrompt(Function<String, Optional<String>> prompt)
	{
		this.namePrompt = prompt;
	}

	/** Overrides the overwrite confirmation, so tests need not raise a modal. */
	void setSaveConfirmation(Predicate<SourceSaveDecision> confirmation)
	{
		this.saveConfirmation = confirmation;
	}

	/**
	 * Saves this tab's campaign set as a named selection.
	 *
	 * <p>The name is offered pre-filled with the current selection's, which is
	 * how the user overwrites one -- the Swing dialog listed the saved
	 * selections and copied whichever was clicked into its name field, to the
	 * same end.
	 */
	@FXML
	protected void onSaveAction(ActionEvent event)
	{
		var existing = existingSources.get();
		namePrompt.apply(suggestedSaveName(List.copyOf(selectedCampaigns), existing))
				.flatMap(typed -> plannedSave(typed, existing, model.getGameMode(),
						List.copyOf(selectedCampaigns), saveConfirmation, this::reportSaveRefused))
				.ifPresent(onSaveRequested);
	}

	/**
	 * What saving under {@code typed} should store, if anything.
	 *
	 * <p>A name that cannot be used is reported and nothing is stored; one that
	 * would replace an existing selection is stored only if confirmed.
	 *
	 * @param typed the name the user gave
	 * @param existing the selections already on offer
	 * @param mode the game mode the selection belongs to
	 * @param campaigns the campaigns to store
	 * @param confirmation asked before replacing an existing selection
	 * @param report tells the user why a name was refused
	 * @return the selection to store, or empty if the save should not happen
	 */
	static Optional<SourceBundle> plannedSave(String typed, List<SourceBundle> existing, GameMode mode,
			List<Campaign> campaigns, Predicate<SourceSaveDecision> confirmation, Consumer<String> report)
	{
		var decision = SourceSaveDecision.forName(typed, existing);
		if (!decision.canSave())
		{
			decision.message().ifPresent(report);
			return Optional.empty();
		}
		if (decision.needsConfirmation() && !confirmation.test(decision))
		{
			return Optional.empty();
		}
		return Optional.of(new SourceBundle(decision.name(), mode, campaigns, true, null));
	}

	/**
	 * The name to offer in the Save prompt: the one already saved under this
	 * exact campaign set, so re-saving a selection the user opened overwrites
	 * it rather than quietly creating a second copy under a name they have to
	 * retype.
	 *
	 * <p>Bundled selections are skipped: they cannot be saved over, so offering
	 * one's name would walk the user into a refusal. An empty selection matches
	 * nothing, or it would offer the name of any saved selection that happens
	 * to be empty.
	 *
	 * @param selected the campaigns about to be saved
	 * @param existing the selections already on offer
	 * @return the name to pre-fill, or an empty string
	 */
	static String suggestedSaveName(List<Campaign> selected, List<SourceBundle> existing)
	{
		if (selected.isEmpty())
		{
			return "";
		}
		return existing.stream()
				.filter(bundle -> bundle.modifiable() && bundle.name() != null)
				.filter(bundle -> bundle.campaigns().equals(selected))
				.map(SourceBundle::name)
				.findFirst()
				.orElse("");
	}

	private void reportSaveRefused(String message)
	{
		var alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(LanguageBundle.getString(SAVE_TITLE));
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.initOwner(ownerWindow());
		alert.showAndWait();
	}

	/** Default prompt: a single text field, pre-filled and pre-selected. */
	private static Optional<String> promptForName(String suggested)
	{
		var dialog = new TextInputDialog(suggested);
		dialog.setTitle(LanguageBundle.getString(SAVE_TITLE));
		dialog.setHeaderText(null);
		dialog.setContentText(LanguageBundle.getString("in_src_savePrompt"));
		return dialog.showAndWait();
	}

	/** Default confirmation: overwriting discards whatever was stored before. */
	private static boolean confirmOverwrite(SourceSaveDecision decision)
	{
		var alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(LanguageBundle.getString(SAVE_TITLE));
		alert.setHeaderText(decision.message().orElse(null));
		var save = new ButtonType(LanguageBundle.getString("in_save"), ButtonData.OK_DONE);
		alert.getButtonTypes().setAll(save, ButtonType.CANCEL);
		return alert.showAndWait()
				.filter(button -> button.getButtonData() == ButtonData.OK_DONE)
				.isPresent();
	}

	/** Whether the user asked for the Advanced tab to be the default on reopen. */
	public boolean isAlwaysAdvanced()
	{
		return chkAlwaysAdvanced.isSelected();
	}

	/**
	 * Shows the remembered state of "always use advanced sources".
	 *
	 * @param alwaysAdvanced whether the box should be ticked
	 */
	public void setAlwaysAdvanced(boolean alwaysAdvanced)
	{
		chkAlwaysAdvanced.setSelected(alwaysAdvanced);
	}

	/**
	 * Builds the {@link SourceBundle} the user has chosen on this tab.
	 * Prefers the explicit Selected list when non-empty; falls back to the
	 * single focused leaf in the available tree so single-click + Load still
	 * works without an Add round-trip.
	 */
	public Optional<SourceBundle> getSelectedSource()
	{
		var gameMode = cmbGameMode.getValue();
		if (gameMode == null)
		{
			return Optional.empty();
		}
		if (!selectedCampaigns.isEmpty())
		{
			var snapshot = List.copyOf(selectedCampaigns);
			return Optional.of(new SourceBundle(null, gameMode, snapshot, false, null));
		}
		return selectedLeavesIn(treeAvailable).findFirst().map(c ->
				new SourceBundle(c.getDisplayName(), gameMode, List.of(c), false, null));
	}

	/**
	 * Persists the committed choice as the remembered defaults: the game mode and,
	 * keyed by that mode, the campaign names actually loaded. Call only when the
	 * user commits the dialog with Load/OK on the Advanced tab, passing the bundle
	 * being loaded (not {@code null}) — browsing then cancelling, or loading from
	 * the Basic tab, must not overwrite the Advanced memory.
	 *
	 * <p>Keys/values use {@link GameMode#getName()} and {@link Campaign#toString()}
	 * for interoperability with the legacy Swing dialog.
	 */
	public void commitSelection(SourceBundle loaded)
	{
		GameMode mode = loaded.gameMode();
		if (mode == null)
		{
			return;
		}
		CONTEXT.setProperty(PROP_SELECTED_GAME, mode.getName());
		String names = loaded.campaigns().stream()
				.map(Campaign::toString)
				.collect(Collectors.joining("|")); //$NON-NLS-1$
		CONTEXT.setProperty(PROP_SELECTED_SOURCES + mode.getName(), names);
	}

	private static Stream<Campaign> selectedLeavesIn(TreeTableView<SourceTreeNode> tree)
	{
		return tree.getSelectionModel().getSelectedItems().stream()
				.filter(Objects::nonNull)
				.flatMap(item -> campaignOf(item).stream());
	}

	private static Optional<Campaign> campaignOf(TreeItem<SourceTreeNode> item)
	{
		return item.getValue() instanceof SourceTreeNode.Leaf(Campaign campaign) ? Optional.of(campaign) : Optional.empty();
	}

	/**
	 * Binds this tab to its shared {@link SourceSelectionModel}: the Selected
	 * tree now reflects the model's campaign list, and the available tree
	 * follows the model's game mode. The Basic tab projects onto the same model,
	 * so a Basic selection lands here through this binding.
	 */
	public void setModel(SourceSelectionModel sourceModel)
	{
		this.model = Objects.requireNonNull(sourceModel);
		this.selectedCampaigns = sourceModel.getSelectedCampaigns();

		// Selected list changes drive the right-side tree rebuild.
		selectedCampaigns.addListener((ListChangeListener<Campaign>) _ -> {
			treeSelected.setRoot(buildTree(selectedCampaigns));
			refreshAvailability();
		});
		treeSelected.setRoot(buildTree(selectedCampaigns));

		// The available tree mirrors the model's maintained availableCampaigns
		// list (recomputed by the model on game-mode or search-query change).
		var available = model.getAvailableCampaigns();
		available.addListener((ListChangeListener<Campaign>) _ -> {
			refreshAvailability();
			treeAvailable.setRoot(buildTree(available));
		});

		// Seed the model's game mode (unless already set, e.g. by a Basic
		// projection) before binding, so there is a single value to react to.
		if (model.getGameMode() == null)
		{
			model.setGameMode(resolveDefaultGameMode());
		}

		// The combo's value and the model's game mode are one store; likewise the
		// search field and the model's query. Write only through the model.
		cmbGameMode.valueProperty().bindBidirectional(model.gameModeProperty());
		fldSearch.textProperty().bindBidirectional(model.searchQueryProperty());

		// Swap the selected campaigns to the new mode's remembered set when the
		// user switches mode (persistence of the choice happens only on Load/OK).
		model.gameModeProperty().addListener((_, _, mode) -> onGameModeChanged(mode));

		// Reflect the initial mode into the tree once.
		refreshAvailability();
		treeAvailable.setRoot(buildTree(model.getAvailableCampaigns()));
	}

	/**
	 * Recomputes how each offered campaign stands against the selection, and
	 * repaints the available tree so the marking follows.
	 *
	 * <p>A refresh is needed because the cells are only told that their row's
	 * data changed when the row itself changes, and here nothing about the row
	 * has: what changed is the selection it is judged against.
	 */
	private void refreshAvailability()
	{
		var selected = List.copyOf(selectedCampaigns);
		availability = CampaignAvailability.classify(
				List.copyOf(model.getAvailableCampaigns()),
				selected,
				Campaign::getSubCampaigns,
				FacadeFactory::passesPrereqs);
		var marked = availability.entrySet().stream()
				.filter(entry -> entry.getValue() != CampaignAvailability.Status.ADDABLE)
				.map(Map.Entry::getKey)
				.toList();
		availabilityReasons = describeAll(marked, selected);
		treeAvailable.refresh();
	}

	/**
	 * Explains every marked campaign in one pass.
	 *
	 * <p>These are the same diagnoses the warning dialog gives, so the tree and
	 * the dialog cannot contradict each other -- the tree simply says it before
	 * the user clicks Add rather than after.
	 */
	private static Map<Campaign, String> describeAll(List<Campaign> marked, List<Campaign> selected)
	{
		Map<Campaign, String> reasons = new IdentityHashMap<>();
		CampaignRejectionDiagnosis.diagnose(marked, selected,
						Campaign::getSubCampaigns, FacadeFactory::findUnqualified)
				.forEach(diagnosis -> reasons.put(diagnosis.campaign(), tipFor(diagnosis.reason())));
		return Collections.unmodifiableMap(reasons);
	}

	/** The tooltip text for a diagnosis. Plain text: a Tooltip renders no markup. */
	private static String tipFor(CampaignRejectionDiagnosis.Reason reason)
	{
		return switch (reason)
		{
			case CampaignRejectionDiagnosis.AlreadyIncluded(Campaign container) ->
					LanguageBundle.getFormattedString(
							"in_src_tipAlreadyIncluded", container.getDisplayName());
			case CampaignRejectionDiagnosis.ConflictsWith(List<Campaign> blockers) ->
					LanguageBundle.getFormattedString("in_src_tipBlockedBySelected",
							blockers.stream().map(Campaign::getDisplayName)
									.collect(Collectors.joining(", ")));
			case CampaignRejectionDiagnosis.Unmet _ ->
					LanguageBundle.getString("in_src_tipUnmetRequirements");
		};
	}

	/**
	 * Reacts to a user mode switch: replace the selected campaigns (which belong
	 * to the old mode) with the new mode's remembered set. The Basic projection
	 * sets the mode then replaces campaigns, so its {@code setAll} runs after this
	 * and wins (see {@link SourceSelectionDialogPane#project}).
	 */
	private void onGameModeChanged(GameMode mode)
	{
		selectedCampaigns.setAll(rememberedCampaignsFor(mode));
	}

	/**
	 * The campaigns to pre-select for {@code mode}: the set remembered under
	 * {@link #PROP_SELECTED_SOURCES} for this mode, or the mode's default data set
	 * when nothing is remembered. Names that no longer resolve to a supported
	 * campaign are skipped with a warning — a stale config must never be fatal.
	 */
	private List<Campaign> rememberedCampaignsFor(GameMode mode)
	{
		if (mode == null)
		{
			return List.of();
		}
		String remembered = CONTEXT.getProperty(PROP_SELECTED_SOURCES + mode.getName(), null);
		List<String> names = (remembered == null || remembered.isBlank())
				? mode.getDefaultDataSetList()
				: List.of(remembered.split("\\|")); //$NON-NLS-1$
		List<Campaign> available = StreamSupport
				.stream(FacadeFactory.getSupportedCampaigns(mode).spliterator(), false)
				.toList();
		return resolveCampaigns(names, available);
	}

	/**
	 * Resolves campaign {@code names} against the {@code available} campaigns,
	 * matching on {@link Campaign#toString()} (the key the legacy Swing dialog
	 * reads and writes). A name that no longer resolves is skipped with a warning
	 * rather than failing — a stale config must never be fatal. Package-private
	 * for unit testing.
	 */
	static List<Campaign> resolveCampaigns(List<String> names, List<Campaign> available)
	{
		Map<String, Campaign> byName = new HashMap<>();
		available.forEach(c -> byName.put(c.toString(), c));

		List<Campaign> resolved = new ArrayList<>(names.size());
		for (String name : names)
		{
			Campaign campaign = byName.get(name);
			if (campaign == null)
			{
				LOG.warning(() -> "Remembered source '" + name
						+ "' is no longer available; skipping.");
			}
			else
			{
				resolved.add(campaign);
			}
		}
		return resolved;
	}

	/**
	 * Supplies the combo's game-mode choices. The combo's value is bound to the
	 * model in {@link #setModel}; selection changes flow through that binding, so
	 * there is no listener to install here.
	 */
	public void setGameModeSource(ObservableList<GameMode> gameModes)
	{
		cmbGameMode.setItems(gameModes);
	}

	/**
	 * Resolves the last-used game mode saved under {@link #PROP_SELECTED_GAME}
	 * against the combo's current items, falling back to the first item. Requires
	 * {@link #setGameModeSource} to have populated the items first.
	 */
	private GameMode resolveDefaultGameMode()
	{
		Optional<String> savedName = Optional.ofNullable(CONTEXT.getProperty(PROP_SELECTED_GAME, null));
		return savedName
				.flatMap(name -> cmbGameMode.getItems().stream()
						.filter(g -> name.equals(g.getName()))
						.findFirst())
				.map(saved -> {
					LOG.fine(() -> "Restored saved GameMode: " + saved.getDisplayName());
					return saved;
				})
				.orElseGet(() -> cmbGameMode.getItems().isEmpty() ? null : cmbGameMode.getItems().getFirst());
	}

	/**
	 * Builds an expanded Publisher → Setting → Campaign tree from the supplied
	 * campaigns. Campaigns without a CAMPAIGN_SETTING attach as direct
	 * publisher children; those with a setting are grouped under a setting node.
	 * Package-private for unit testing.
	 */
	static TreeItem<SourceTreeNode> buildTree(List<Campaign> campaigns)
	{
		var fallbackPublisher = LanguageBundle.getString("in_other");

		Map<String, List<Campaign>> byPublisher = campaigns.stream()
				.collect(Collectors.groupingBy(c -> Optional.ofNullable(c.get(StringKey.DATA_PRODUCER))
						.orElse(fallbackPublisher), Collectors.toList()));

		var root = new TreeItem<SourceTreeNode>();
		byPublisher.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
				.forEach(pubEntry -> root.getChildren().add(buildPublisherNode(pubEntry.getKey(), pubEntry.getValue())));
		return root;
	}

	private static TreeItem<SourceTreeNode> buildPublisherNode(String publisher, List<Campaign> children)
	{
		var node = new TreeItem<SourceTreeNode>(new SourceTreeNode.Publisher(publisher));
		node.setExpanded(true);

		// Campaigns within a publisher group either have a setting (folder) or
		// not (direct child). Settings are alphabetised; direct campaigns sort
		// after their setting siblings, both groups by display name.
		Map<Optional<String>, List<Campaign>> bySetting = children.stream()
				.collect(Collectors.groupingBy(c -> Optional.ofNullable(c.get(StringKey.CAMPAIGN_SETTING)),
						Collectors.toList()));

		bySetting.entrySet().stream()
				.filter(e -> e.getKey().isPresent())
				.sorted(Comparator.comparing(e -> e.getKey().get(), String.CASE_INSENSITIVE_ORDER))
				.forEach(e -> node.getChildren().add(buildSettingNode(publisher, e.getKey().get(), e.getValue())));

		bySetting.getOrDefault(Optional.empty(), List.of()).stream()
				.sorted(Comparator.comparing(Campaign::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.forEach(c -> node.getChildren().add(new TreeItem<>(new SourceTreeNode.Leaf(c))));

		return node;
	}

	private static TreeItem<SourceTreeNode> buildSettingNode(String publisher, String setting, List<Campaign> children)
	{
		var node = new TreeItem<SourceTreeNode>(new SourceTreeNode.Setting(publisher, setting));
		node.setExpanded(true);
		children.stream()
				.sorted(Comparator.comparing(Campaign::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.forEach(c -> node.getChildren().add(new TreeItem<>(new SourceTreeNode.Leaf(c))));
		return node;
	}

	private static class GameModeCellFactory implements Callback<ListView<GameMode>, ListCell<GameMode>>
	{
		/**
		 * Creates a custom ListCell for displaying a GameMode object.
		 * Overrides the updateItem method to handle displaying the game mode's display name.
		 *
		 * @param param The ListView object that this ListCell is being used in.
		 * @return ListCell<GameMode> The custom ListCell object.
		 */
		@Override
		public ListCell<GameMode> call(ListView<GameMode> param)
		{
			return new ListCell<>()
			{
				@Override
				public void updateItem(GameMode gameMode, boolean empty)
				{
					super.updateItem(gameMode, empty);
					if (empty || gameMode == null)
					{
						setText(null);
						setGraphic(null);
					}
					else
					{
						setText(gameMode.getDisplayName());
					}
				}
			};
		}
	}
}
