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

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

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
	private static final Configuration TIP_CONFIG = createTipConfiguration();

	private String currentTip;

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
		sourcesHint.fontProperty().addListener((_, _, _) -> renderTip());
		sourcesHint.textFillProperty().addListener((_, _, _) -> renderTip());
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
	 * rendered through {@code info-guide-tip.ftl} with the native body label's
	 * resolved font and text color. Later CSS changes update that presentation.
	 * The "Did you know?" heading is drawn natively by the card header.
	 *
	 * @param tip the raw tip markup from {@code TipOfTheDayHandler}
	 */
	public void setTip(String tip)
	{
		Platform.runLater(() -> {
			sourcesHint.getScene().getRoot().applyCss();
			currentTip = tip;
			renderTip();
		});
	}

	/** Renders the current author markup using resolved native presentation values. */
	private void renderTip()
	{
		if (currentTip == null)
		{
			return;
		}
		if (!(sourcesHint.getTextFill() instanceof Color color))
		{
			throw new IllegalStateException("Guide tip text requires a solid color");
		}
		var font = sourcesHint.getFont();
		var familyCss = new StringBuilder();
		// CSS hexadecimal escapes prevent font names from breaking out of the style element.
		font.getFamily().codePoints().forEach(value ->
				familyCss.append('\\').append(Integer.toHexString(value)).append(' '));
		Map<String, Object> model = Map.of(
				"tipHtml", currentTip,
				"systemFont", "System".equals(font.getFamily()),
				"fontFamilyCss", familyCss.toString(),
				"fontSize", font.getSize(),
				"red", Math.round(color.getRed() * 255),
				"green", Math.round(color.getGreen() * 255),
				"blue", Math.round(color.getBlue() * 255),
				"opacity", color.getOpacity());
		try
		{
			var writer = new StringWriter();
			TIP_CONFIG.getTemplate("info-guide-tip.ftl").process(model, writer);
			tipBrowser.getEngine().loadContent(writer.toString());
		}
		catch (IOException | TemplateException exception)
		{
			throw new IllegalStateException("Unable to render the guide tip", exception);
		}
	}

	/** Loads the tip's classpath template with a reusable UTF-8 configuration. */
	private static Configuration createTipConfiguration()
	{
		var config = new Configuration(Configuration.VERSION_2_3_35);
		config.setDefaultEncoding(StandardCharsets.UTF_8.name());
		config.setClassForTemplateLoading(InfoGuideController.class, "");
		return config;
	}
}
