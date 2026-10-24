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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javafx.concurrent.Worker;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Window;

import pcgen.system.LanguageBundle;

/**
 * The warning shown when campaigns the user asked to add would not satisfy
 * their prerequisites.
 *
 * <p>Both halves of this message are markup: the patterns in the language
 * bundle are HTML, and the engine's requirements string carries its own
 * {@code <font color>} and {@code <i>} emphasis from
 * {@code PrerequisiteUtilities}. The legacy Swing panel passed that straight to
 * {@code JOptionPane}, whose {@code JLabel} renders HTML. A JavaFX
 * {@code Alert} puts its content in a {@code Label}, which does not, so the
 * same string was displayed as raw tags.
 *
 * <p>The content is therefore a {@link WebView}, which is also how the rest of
 * gui3 renders engine HTML (see {@code SourceInfoRenderer}). Stripping the tags
 * instead was rejected: the requirements string is an arbitrary prerequisite
 * expression, not a list of names, so a plain-text rendering would have to
 * re-implement the engine's formatting to stay correct.
 *
 * <p>Note that {@code LanguageBundle.getFormattedString} runs the pattern
 * through {@code MessageFormat}, which treats a single quote as an escape
 * character and silently drops it. Patterns here use double quotes for HTML
 * attributes for that reason.
 */
final class BadComboAlert
{
	/** Wide enough for the message, narrow enough to stay a dialog. */
	private static final int CONTENT_WIDTH = 440;

	/** Shown until the document reports its real height. */
	private static final int INITIAL_HEIGHT = 150;

	/** Keeps the last line clear of the content edge. */
	private static final int HEIGHT_PADDING = 16;

	/** A dialog taller than this cannot be dismissed on its own screen. */
	private static final double MAX_SCREEN_FRACTION = 0.75;

	/** Rough allowance for the title bar, button bar and dialog padding. */
	private static final int CHROME_ALLOWANCE = 120;

	/** Below this the message area is too cramped to read at all. */
	private static final int MIN_HEIGHT = 100;

	private BadComboAlert()
	{
	}

	/**
	 * A campaign that could not be added, with the engine's description of what
	 * it needs.
	 *
	 * @param campaignName the campaign's display name, as plain text
	 * @param requirementsHtml the engine's requirements markup, to be rendered
	 */
	record Rejection(String campaignName, String requirementsHtml)
	{
	}

	/**
	 * A campaign that is already loaded through a selected collection.
	 *
	 * @param campaignName the campaign's display name, as plain text
	 * @param containerName the selected campaign that loads it, as plain text
	 */
	record Covered(String campaignName, String containerName)
	{
	}

	/**
	 * Builds the message document.
	 *
	 * @param rejections the campaigns that were refused
	 * @return an HTML document, or an empty string if nothing was rejected
	 */
	static String buildHtml(List<Rejection> rejections)
	{
		return buildHtml(rejections, List.of());
	}

	/**
	 * Builds the message document.
	 *
	 * <p>A single rejection and nothing else reuses the pattern the Swing
	 * dialog has always used, so that case stays identical -- and already
	 * translated. Anything more needs the plural pattern and has to name the
	 * campaigns, since the requirements alone would not say which book they
	 * belong to.
	 *
	 * <p>Campaigns already loaded through a selected collection get their own
	 * section, below the failures and without their wording: nothing failed for
	 * them, and asking the user to resolve a requirement that is already
	 * satisfied would send them looking for a problem that does not exist.
	 *
	 * @param rejections the campaigns that were refused
	 * @param covered the campaigns a selected collection already loads
	 * @return an HTML document, or an empty string if there is nothing to say
	 */
	static String buildHtml(List<Rejection> rejections, List<Covered> covered)
	{
		if (rejections.isEmpty() && covered.isEmpty())
		{
			return "";
		}
		if (covered.isEmpty() && rejections.size() == 1)
		{
			return LanguageBundle.getFormattedString(
					"in_src_badComboMsg", rejections.getFirst().requirementsHtml());
		}
		var body = new StringBuilder();
		if (!rejections.isEmpty())
		{
			body.append(LanguageBundle.getFormattedString(
					"in_src_badComboMsgMultiBody", rejectionList(rejections)));
		}
		if (!covered.isEmpty())
		{
			body.append(LanguageBundle.getFormattedString(
					"in_src_alreadyIncludedMsg", coveredList(covered)));
		}
		return LanguageBundle.getFormattedString("in_src_badComboMsgWrapper", body.toString());
	}

	/**
	 * Describes a campaign refused by campaigns the user has already selected.
	 *
	 * <p>This replaces the engine's requirements expression rather than
	 * supplementing it, because the obstacle is not in the refused campaign's
	 * requirements at all: it lives in the {@code !PRECAMPAIGN} of the selected
	 * campaign that excludes it. The expression could therefore be read in full
	 * without ever learning what to do.
	 *
	 * @param blockerNames the selected campaigns in the way, as plain text
	 * @return the markup describing the obstacle
	 */
	static String blockedBySelected(List<String> blockerNames)
	{
		var names = blockerNames.stream()
				.map(BadComboAlert::escape)
				.map(name -> "<b>" + name + "</b>")
				.collect(Collectors.joining(", "));
		return LanguageBundle.getFormattedString(
				blockerNames.size() == 1 ? "in_src_blockedBySelected" : "in_src_blockedBySelectedMulti",
				names);
	}

	private static String rejectionList(List<Rejection> rejections)
	{
		var list = new StringBuilder("<ul>");
		for (Rejection rejection : rejections)
		{
			list.append("<li><b>").append(escape(rejection.campaignName())).append("</b><br>")
					.append(rejection.requirementsHtml()).append("</li>");
		}
		return list.append("</ul>").toString();
	}

	private static String coveredList(List<Covered> covered)
	{
		var list = new StringBuilder("<ul>");
		for (Covered entry : covered)
		{
			list.append(LanguageBundle.getFormattedString("in_src_alreadyIncludedEntry",
					escape(entry.campaignName()), escape(entry.containerName())));
		}
		return list.append("</ul>").toString();
	}

	/**
	 * Builds the alert without showing it.
	 *
	 * @param rejections the campaigns that were refused; must not be empty
	 * @return the configured alert
	 */
	static Alert build(List<Rejection> rejections)
	{
		return build(rejections, List.of(), null);
	}

	/**
	 * Builds the alert without showing it.
	 *
	 * @param rejections the campaigns that were refused
	 * @param covered the campaigns a selected collection already loads
	 * @return the configured alert
	 */
	static Alert build(List<Rejection> rejections, List<Covered> covered)
	{
		return build(rejections, covered, null);
	}

	/**
	 * Builds the alert without showing it.
	 *
	 * @param rejections the campaigns that were refused
	 * @param covered the campaigns a selected collection already loads
	 * @param owner the window to open over, or {@code null} to let the window
	 *              manager place the dialog
	 * @return the configured alert
	 */
	static Alert build(List<Rejection> rejections, List<Covered> covered, Window owner)
	{
		var alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(LanguageBundle.getString("in_src_badComboTitle"));
		alert.setHeaderText(null);
		if (owner != null)
		{
			alert.initOwner(owner);
		}
		// The list is capped, so the user may want to trade screen space for
		// seeing more of it at once.
		alert.setResizable(true);
		alert.getDialogPane().setContent(buildView(buildHtml(rejections, covered), alert));
		return alert;
	}

	/**
	 * Decides how tall the view should be for content of a given height.
	 *
	 * <p>Rejecting a whole collection produces a list taller than any screen,
	 * and a dialog taller than the screen cannot be dismissed -- its button bar
	 * sits below the bottom edge. The height is therefore capped, and the
	 * WebView scrolls to reach the rest. On a screen too small for even the
	 * chrome, a usable minimum wins over the cap: a dialog that is slightly too
	 * tall is still better than one with no room for its message.
	 *
	 * @param contentHeight the rendered height of the document
	 * @param screenHeight the usable height of the screen
	 * @return the height to give the view
	 */
	static double viewHeightFor(double contentHeight, double screenHeight)
	{
		double ceiling = Math.max(MIN_HEIGHT, screenHeight * MAX_SCREEN_FRACTION - CHROME_ALLOWANCE);
		return Math.min(contentHeight + HEIGHT_PADDING, ceiling);
	}

	/**
	 * Shows the alert over the given window and waits for the user to
	 * acknowledge it. Does nothing when there is nothing to report.
	 *
	 * @param rejections the campaigns that were refused
	 * @param covered the campaigns a selected collection already loads
	 * @param owner the window to open over, or {@code null} to let the window
	 *              manager place the dialog
	 */
	static void show(List<Rejection> rejections, List<Covered> covered, Window owner)
	{
		if (rejections.isEmpty() && covered.isEmpty())
		{
			return;
		}
		build(rejections, covered, owner).showAndWait();
	}

	private static WebView buildView(String html, Alert alert)
	{
		var view = new WebView();
		// A web page's default serif font and white backdrop would read as a
		// foreign object dropped into the dialog.
		view.setPageFill(Color.TRANSPARENT);
		view.getEngine().setUserStyleSheetLocation(uiFontStyleSheet());
		// The built-in context menu offers Reload, which is meaningless for a
		// document loaded from a string.
		view.setContextMenuEnabled(false);
		view.setPrefSize(CONTENT_WIDTH, INITIAL_HEIGHT);
		view.getEngine().getLoadWorker().stateProperty().addListener(
				(obs, old, state) -> {
					if (state == Worker.State.SUCCEEDED)
					{
						fitToContent(view, alert);
					}
				});
		view.getEngine().loadContent(html);
		return view;
	}

	/**
	 * Sizes the view to the rendered document. A WebView has no preferred size
	 * of its own, so without this the dialog is whatever the default 800x600
	 * happens to be.
	 */
	private static void fitToContent(WebView view, Alert alert)
	{
		view.setPrefHeight(viewHeightFor(measureContentHeight(view.getEngine()), usableScreenHeight(alert)));
		var scene = alert.getDialogPane().getScene();
		if (scene != null && scene.getWindow() != null)
		{
			scene.getWindow().sizeToScene();
		}
	}

	/**
	 * The usable height of the screen the dialog will appear on. Visual bounds
	 * are used rather than the full bounds so the cap allows for the taskbar or
	 * dock, which the dialog cannot be placed under.
	 */
	private static double usableScreenHeight(Alert alert)
	{
		var owner = alert.getOwner();
		if (owner != null)
		{
			var screens = Screen.getScreensForRectangle(
					owner.getX(), owner.getY(), owner.getWidth(), owner.getHeight());
			if (!screens.isEmpty())
			{
				return screens.get(0).getVisualBounds().getHeight();
			}
		}
		return Screen.getPrimary().getVisualBounds().getHeight();
	}

	/**
	 * Measures the height actually occupied by the document's content.
	 *
	 * <p>The obvious candidates all lie. WebKit stretches both
	 * {@code documentElement} and {@code body} to the viewport, so
	 * {@code scrollHeight} and {@code offsetHeight} report the height the view
	 * already has -- ask them how tall a three-line message is inside a 600px
	 * view and they answer 600, so the dialog can never shrink to its message.
	 * The bottom edge of the lowest top-level element is independent of the
	 * viewport.
	 *
	 * @param engine the engine holding the loaded document
	 * @return the content height in pixels, or 0 if it cannot be measured
	 */
	static double measureContentHeight(WebEngine engine)
	{
		Object measured = engine.executeScript(
				"(function(){var m=0,c=document.body.children;"
				+ "for(var i=0;i<c.length;i++){var b=c[i].getBoundingClientRect().bottom;"
				+ "if(b>m)m=b;}return m;})()");
		return measured instanceof Number number ? number.doubleValue() : 0;
	}

	/** A user stylesheet making the document use the UI's font. */
	private static String uiFontStyleSheet()
	{
		var font = Font.getDefault();
		var css = "body { font-family: \"" + font.getFamily() + "\", sans-serif; font-size: "
				+ Math.round(font.getSize()) + "px; margin: 0; }";
		return "data:text/css;base64,"
				+ Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
	}

	private static String escape(String text)
	{
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
