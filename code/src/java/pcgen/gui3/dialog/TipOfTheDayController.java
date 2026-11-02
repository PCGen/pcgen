/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3.dialog;

import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.TipOfTheDayHandler;
import pcgen.gui3.GuiAssertions;
import pcgen.system.LanguageBundle;
import pcgen.system.PropertyContext;
import pcgen.util.Html;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;

/**
 * Displays tips from the active game mode, with navigation and native Reload support.
 * Each window owns its rendered document and displayed position in {@link TipOfTheDayHandler}.
 */
public class TipOfTheDayController
{

	private static final PropertyContext PROPERTY_CONTEXT = UIPropertyContext.createContext("TipOfTheDay");

	@FXML
	private CheckBox showTips;

	private static final String HTML_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			</head>
			<body style="margin-left: 5px; margin-right: 5px; margin-top: 5px">%s</body>
			</html>
			""";
	private final TipOfTheDayHandler tipHandler = TipOfTheDayHandler.getInstance();
	@FXML
	private WebView browser;
	@FXML
	private Scene tipScene;
	private TipRenderer renderer;
	private int currentTipNumber;

	/** Initializes preferences, loads tips, and arranges cleanup when the scene receives its window. */
	@FXML
	void initialize()
	{
		// ideally, this should be become a bindable "BeanProperty" rather than
		// a changeListener based construct.
		showTips.setSelected(
			PROPERTY_CONTEXT.initBoolean("showTipOfTheDay", true)
		);
		showTips.selectedProperty().addListener((ov, old_val, new_val) -> PROPERTY_CONTEXT.setBoolean("showTipOfTheDay", showTips.isSelected()));
		renderer = new TipRenderer(browser.getEngine(), this::reloadTip);
		tipScene.windowProperty().addListener((_, _, window) -> {
			if (window != null)
			{
				window.addEventHandler(WindowEvent.WINDOW_HIDDEN, _ -> renderer.close());
			}
		});
		tipHandler.loadTips();
		showNextTip();
	}

	/** Hides this dialog; the window-hidden handler releases its renderer. */
	@FXML
	private void onClose(final ActionEvent actionEvent)
	{
		var button = (Button)actionEvent.getSource();
		button.getScene().getWindow().hide();
	}

	/** Displays the next tip, wrapping at the end of the list. */
	@FXML
	private void onNextTip(final ActionEvent actionEvent)
	{
		showNextTip();
	}

	/** Displays the previous tip, wrapping at the start of the list. */
	@FXML
	private void onPrevTip(final ActionEvent actionEvent)
	{
		showPrevTip();
	}

	private void showNextTip()
	{
		showTip(tipHandler.getNextTip());
	}

	private void showPrevTip()
	{
		showTip(tipHandler.getPrevTip());
	}

	private void showTip(final String tip)
	{
		GuiAssertions.assertIsJavaFXThread();
		currentTipNumber = tipHandler.getLastNumber();
		renderer.render(buildTipText(tip));
	}

	/** Rereads the file and retrieves this window's tip again, even if another view advanced the singleton. */
	private String reloadTip()
	{
		tipHandler.loadTips();
		String tip = tipHandler.getTip(currentTipNumber);
		currentTipNumber = tipHandler.getLastNumber();
		return buildTipText(tip);
	}

	/** Formats authored tip HTML, leaving an empty document reloadable when the file has no tips. */
	private String buildTipText(String tip)
	{
		String content = "";
		if (!tip.isEmpty())
		{
			// The tip number is plain data and is escaped via pcgen.util.Html.
			// The tip body is authored HTML from the tips data files (e.g. <em>...</em>)
			// and is deliberately passed through unescaped.
			String tipNumber = Html.escape(Integer.toString(currentTipNumber + 1));
			content = LanguageBundle.getFormattedString("in_tod_tipDisplay", tipNumber, tip);
		}
		return HTML_TEMPLATE.formatted(content);
	}
}
