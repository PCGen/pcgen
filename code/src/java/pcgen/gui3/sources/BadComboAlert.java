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

import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Window;

import pcgen.system.LanguageBundle;
import pcgen.util.Html;

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

	/** Initial message height; longer reports scroll until the user resizes. */
	private static final int INITIAL_HEIGHT = 150;

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
				.map(Html::escape)
				.map(name -> "<b>" + name + "</b>")
				.collect(Collectors.joining(", "));
		return LanguageBundle.getFormattedString(
				blockerNames.size() == 1 ? "in_src_blockedBySelected" : "in_src_blockedBySelectedMulti",
				names);
	}

	private static String rejectionList(List<Rejection> rejections)
	{
		return rejections.stream()
				.map(rejection -> "<li><b>" + Html.escape(rejection.campaignName()) + "</b><br>"
						+ rejection.requirementsHtml() + "</li>")
				.collect(Collectors.joining("", "<ul>", "</ul>"));
	}

	private static String coveredList(List<Covered> covered)
	{
		return covered.stream()
				.map(entry -> LanguageBundle.getFormattedString("in_src_alreadyIncludedEntry",
						Html.escape(entry.campaignName()), Html.escape(entry.containerName())))
				.collect(Collectors.joining("", "<ul>", "</ul>"));
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
		alert.initOwner(owner);
		alert.setResizable(true);
		alert.getDialogPane().setContent(buildView(buildHtml(rejections, covered)));
		return alert;
	}

	private static WebView buildView(String html)
	{
		var view = new WebView();
		// A web page's default serif font and white backdrop would read as a
		// foreign object dropped into the dialog.
		view.setPageFill(Color.TRANSPARENT);
		view.getEngine().setUserStyleSheetLocation(uiFontStyleSheet());
		view.setContextMenuEnabled(false); // disable a meaningless Reload-context menu for an HTML string
		view.setPrefSize(CONTENT_WIDTH, INITIAL_HEIGHT);
		view.getEngine().loadContent(html);
		return view;
	}

	/** Uses the UI font and overrides legacy messages' fixed inline body widths. */
	private static String uiFontStyleSheet()
	{
		var font = Font.getDefault();
		var css = "body { width: auto !important; font-family: \"" + font.getFamily() + "\", sans-serif; font-size: "
				+ Math.round(font.getSize()) + "px; margin: 0; padding: 16px 16px 0 0; }";
		return "data:text/css;base64,"
				+ Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
	}
}
