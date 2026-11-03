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

import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

import pcgen.gui2.tools.Icons;
import pcgen.system.LanguageBundle;

/**
 * Controller for the native JavaFX main-screen guide panel (intro, game mode,
 * loaded sources, "what next", and the "Did you know?" tip). Rendered by
 * {@code InfoGuidePanel.fxml} and embedded in Swing through
 * {@link JFXPanelFromResource}. All content methods are safe to call from any
 * thread; each hops to the FX thread before touching a control.
 */
public final class InfoGuideController
{
	@FXML
	private Label gameModeBadge;

	@FXML
	private Label sourcesHint;

	@FXML
	private ListView<String> sourcesList;

	@FXML
	private WebView tipBrowser;

	@FXML
	private ImageView newIcon;

	@FXML
	private ImageView openIcon;

	/** Fills in the toolbar icons for the "what next" rows. */
	@FXML
	private void initialize()
	{
		newIcon.setImage(Icons.New16.asJavaFX());
		openIcon.setImage(Icons.Open16.asJavaFX());
		// Let the card's background and border show through the WebView.
		tipBrowser.setPageFill(Color.TRANSPARENT);
	}

	/**
	 * The resolved state of the source-materials card: either an empty-state
	 * hint or a non-empty list of source names. Pure data, no JavaFX.
	 *
	 * @param sources the source names (empty when nothing is loaded)
	 * @param hintText the hint to show when empty
	 */
	public record SourceCardState(List<String> sources, String hintText)
	{
		boolean isEmpty()
		{
			return sources.isEmpty();
		}
	}

	/**
	 * Computes the source-card state for a set of loaded source names.
	 *
	 * @param sources loaded source names; empty means none selected
	 * @return the card state, with the localized hint when empty
	 */
	static SourceCardState sourceCardState(List<String> sources)
	{
		return new SourceCardState(List.copyOf(sources),
				LanguageBundle.getString("in_si_nosources_plain"));
	}

	/**
	 * Sets the game-mode badge text.
	 *
	 * @param gameMode the display name, or {@code null}/blank for none
	 */
	public void setGameMode(String gameMode)
	{
		String text = (gameMode == null || gameMode.isBlank())
				? LanguageBundle.getString("in_si_none_selected_plain") : gameMode;
		Platform.runLater(() -> gameModeBadge.setText(text));
	}

	/** Shows the "no sources loaded" empty state. */
	public void setNoSources()
	{
		setSources(List.of());
	}

	/**
	 * Shows the loaded source names, or the empty-state hint when the list is
	 * empty.
	 *
	 * @param sources loaded source names
	 */
	public void setSources(List<String> sources)
	{
		SourceCardState state = sourceCardState(sources);
		Platform.runLater(() -> {
			boolean empty = state.isEmpty();
			sourcesHint.setText(state.hintText());
			sourcesHint.setVisible(empty);
			sourcesHint.setManaged(empty);
			sourcesList.setVisible(!empty);
			sourcesList.setManaged(!empty);
			sourcesList.setItems(FXCollections.observableArrayList(state.sources()));
		});
	}

	/**
	 * Renders the current tip in the tip card's WebView. The raw tip may carry
	 * simple author HTML ({@code <b>}, {@code <br>}, {@code <em>}, lists); it is
	 * wrapped in a transparent document that inherits the application font so it
	 * blends into the card. The "Did you know?" heading is drawn natively by the
	 * card header, so it must not appear here.
	 *
	 * @param tip the raw tip markup from {@code TipOfTheDayHandler}
	 */
	public void setTip(String tip)
	{
		String document = """
				<!DOCTYPE html><html><head><meta charset="utf-8"><style>\
				html,body{margin:0;padding:0;background:transparent;\
				font-family:sans-serif;font-size:13px;color:#222}\
				</style></head><body>%s</body></html>""".formatted(tip);
		Platform.runLater(() -> tipBrowser.getEngine().loadContent(document));
	}
}
