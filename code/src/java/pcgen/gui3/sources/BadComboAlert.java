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
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Window;

import pcgen.system.LanguageBundle;

/**
 * Renders a localized source-selection report through {@code bad-combo.ftl}.
 *
 * <p>The template escapes plain names and preserves the engine's requirements
 * markup. HTML and CSS belong to the template, not to the Java data model.
 */
final class BadComboAlert
{
	/** Wide enough for the message, narrow enough to stay a dialog. */
	private static final int CONTENT_WIDTH = 440;

	/** Initial message height; longer reports scroll until the user resizes. */
	private static final int INITIAL_HEIGHT = 150;

	private static final Configuration CONFIG = createConfiguration();

	private BadComboAlert()
	{
	}

	/**
	 * A refused campaign, described by engine markup or selected blockers.
	 *
	 * @param campaignName the campaign's display name, as plain text
	 * @param requirementsHtml the engine's requirements markup, to be rendered
	 * @param blockerNames the selected campaigns blocking it, or empty for engine requirements
	 */
	record Rejection(String campaignName, String requirementsHtml, List<String> blockerNames)
	{
		/** Describes an unmet requirement using the engine's existing markup. */
		Rejection(String campaignName, String requirementsHtml)
		{
			this(campaignName, requirementsHtml, List.of());
		}
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
	 * <p>Names are passed as plain text; only engine requirements are trusted HTML.
	 * The template selects the localized single/plural wording and separate
	 * already-included section.
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
		String title = LanguageBundle.getString("in_src_badComboTitle");
		var font = Font.getDefault();
		Map<String, Object> model = Map.of(
				"title", title,
				"messages", LanguageBundle.getBundle(),
				"fontFamily", font.getFamily(),
				"fontSize", Math.round(font.getSize()),
				"rejections", rejections.stream().map(rejection -> Map.of(
						"campaignName", rejection.campaignName(),
						"requirementsHtml", rejection.requirementsHtml(),
						"blockerNames", rejection.blockerNames())).toList(),
				"covered", covered.stream().map(entry -> Map.of(
						"campaignName", entry.campaignName(),
						"containerName", entry.containerName())).toList());
		try
		{
			var writer = new StringWriter();
			CONFIG.getTemplate("bad-combo.ftl").process(model, writer);
			return writer.toString();
		}
		catch (IOException | TemplateException exception)
		{
			throw new IllegalStateException("Unable to render the source-selection report", exception);
		}
	}

	/** Loads the alert's classpath template with one reusable configuration. */
	private static Configuration createConfiguration()
	{
		var config = new Configuration(Configuration.VERSION_2_3_35);
		config.setDefaultEncoding(StandardCharsets.UTF_8.name());
		config.setClassForTemplateLoading(BadComboAlert.class, "");
		return config;
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
		view.setContextMenuEnabled(false); // disable a meaningless Reload-context menu for an HTML string
		view.setPrefSize(CONTENT_WIDTH, INITIAL_HEIGHT);
		view.getEngine().loadContent(html);
		return view;
	}

}
