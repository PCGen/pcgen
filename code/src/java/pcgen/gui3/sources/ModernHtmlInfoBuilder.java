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
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import pcgen.cdom.content.CampaignURL;
import pcgen.cdom.content.CampaignURL.URLKind;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.Status;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.AllowUtilities;
import pcgen.core.Campaign;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.system.LanguageBundle;
import pcgen.base.lang.StringUtil;
import pcgen.gui3.utilty.ColorUtilty;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

/**
 * Builds the campaign source-info page shown in the JavaFX source-selection
 * {@code WebView}. The HTML5 markup, CSS, and page skeleton live in
 * {@code campaign-info.ftl} (loaded from the classpath); this class extracts a
 * {@link Campaign}'s fields into the template's data model.
 *
 * <p>This is the modern counterpart to the Swing-era {@code HtmlInfoBuilder}/
 * {@code Gui2CampaignInfoFactory}, which emit legacy HTML 3.2 for
 * {@code JEditorPane}. The field set mirrors {@code Gui2CampaignInfoFactory},
 * but exposed as structured model entries (status pill, facts grid, prose
 * description, sections) that the template lays out as a card; fragments from
 * shared prereq/allow/URL helpers are embedded as trusted HTML.
 *
 * <p>Cover/logo images are emitted with paths relative to the data directory
 * (see {@link #imageSrc}); the template's {@code <base href>} plus loading the
 * document from a {@code file:} URL lets WebKit resolve them, and a CSS cap keeps
 * oversized art from blowing out the layout.
 */
final class ModernHtmlInfoBuilder
{
	/** Maximum rendered size (px) of a cover/logo image, so large art cannot blow out the layout. */
	private static final int MAX_IMAGE_PX = 128;

	private static final String TEMPLATE_NAME = "campaign-info.ftl";

	/**
	 * Source location of {@link #TEMPLATE_NAME} in a working copy. When this
	 * directory exists (developer run from the repo), the template is loaded from
	 * it with caching off, so edits to the {@code .ftl} show on the next render
	 * without a rebuild. In a packaged app the path is absent and rendering falls
	 * back to the classpath copy.
	 */
	private static final Path DEV_TEMPLATE_DIR =
			Path.of("code", "src", "resources", "pcgen", "gui3", "sources");

	private static final Configuration CONFIG = createConfiguration();

	private ModernHtmlInfoBuilder()
	{
	}

	private static Configuration createConfiguration()
	{
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
		cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());
		try
		{
			if (Files.isReadable(DEV_TEMPLATE_DIR.resolve(TEMPLATE_NAME)))
			{
				// Dev run from the repo: load from source and disable caching so
				// template edits are picked up on the next render (see reload).
				cfg.setDirectoryForTemplateLoading(DEV_TEMPLATE_DIR.toFile());
				cfg.setTemplateUpdateDelayMilliseconds(0);
				return cfg;
			}
		}
		catch (IOException | RuntimeException ignored)
		{
			// Fall through to the classpath loader below.
		}
		cfg.setClassForTemplateLoading(ModernHtmlInfoBuilder.class, "");
		return cfg;
	}

	/**
	 * Renders {@code campaign} as a complete HTML5 document, resolving image
	 * paths relative to {@code dataDir}.
	 *
	 * @param campaign the campaign to describe
	 * @param dataDir the absolute PCGen data directory (the {@code <base href>})
	 * @return a full {@code <!DOCTYPE html>} document
	 * @throws IOException if the template cannot be loaded or rendered
	 */
	static String forCampaign(Campaign campaign, Path dataDir) throws IOException
	{
		Map<String, Object> model = buildModel(campaign, dataDir);
		try
		{
			Template template = CONFIG.getTemplate(TEMPLATE_NAME);
			StringWriter out = new StringWriter(2048);
			template.process(model, out);
			return out.toString();
		}
		catch (TemplateException exception)
		{
			throw new IOException("Failed to render " + TEMPLATE_NAME, exception);
		}
	}

	private static Map<String, Object> buildModel(Campaign aCamp, Path dataDir)
	{
		Map<String, Object> model = new LinkedHashMap<>();
		model.put("baseHref", dataDir.toUri().toString());
		model.put("maxImagePx", MAX_IMAGE_PX);
		model.put("title", aCamp.getDisplayName());
		model.put("images", buildImages(aCamp, dataDir));
		model.put("status", buildStatus(aCamp));
		model.put("facts", buildFacts(aCamp));
		model.put("description", aCamp.get(StringKey.DESCRIPTION));
		model.put("requirements", buildRequirements(aCamp));
		model.put("allow", buildAllow(aCamp));
		model.put("sections", buildSections(aCamp));
		model.put("pccLabel", LanguageBundle.getString("in_infPccPath"));
		model.put("pccPath", aCamp.getSourceURI().getPath());
		return model;
	}

	private static List<Map<String, Object>> buildImages(Campaign aCamp, Path dataDir)
	{
		List<Map<String, Object>> images = new ArrayList<>(2);
		addImage(images, aCamp, ListKey.FILE_COVER, dataDir, aCamp.getDisplayName() + " cover");
		addImage(images, aCamp, ListKey.FILE_LOGO, dataDir, aCamp.getDisplayName() + " logo");
		return images;
	}

	private static void addImage(List<Map<String, Object>> images, Campaign aCamp,
			ListKey<CampaignSourceEntry> key, Path dataDir, String alt)
	{
		if (aCamp.getSizeOfListFor(key) > 0)
		{
			Map<String, Object> img = new LinkedHashMap<>();
			img.put("src", imageSrc(aCamp.getSafeListFor(key).getFirst().getURI(), dataDir));
			img.put("alt", alt);
			images.add(img);
		}
	}

	/** The status pill: {@code label} plus a {@code #RRGGBB} colour. */
	private static Map<String, Object> buildStatus(Campaign aCamp)
	{
		Status status = aCamp.getSafe(ObjectKey.STATUS);
		Map<String, Object> pill = new LinkedHashMap<>();
		pill.put("label", status.toString());
		pill.put("color", ColorUtilty.colorToRGBString(status.getColor()));
		return pill;
	}

	/**
	 * The short metadata fields, in display order, each {@code {label, valueHtml}}.
	 * {@code label} is the plain i18n label (no colon); {@code valueHtml} is
	 * already-safe HTML (escaped text or trusted markup such as links).
	 */
	private static List<Map<String, Object>> buildFacts(Campaign aCamp)
	{
		List<Map<String, Object>> facts = new ArrayList<>();

		String source = SourceFormat.getFormattedString(aCamp, SourceFormat.MEDIUM, true);
		if (StringUtils.isEmpty(source))
		{
			source = SourceFormat.getFormattedString(aCamp, SourceFormat.LONG, true);
		}
		// in_infByPub is an HTML pattern ("<b> by</b> {0}") appended after the SOURCE value.
		facts.add(fact(LanguageBundle.getString("in_sumSource"),
				escape(source) + LanguageBundle.getFormattedString("in_infByPub", escape(aCamp.getSafe(StringKey.PUB_NAME_LONG)))));

		if (!aCamp.getType().isEmpty())
		{
			facts.add(fact(LanguageBundle.getString("in_infType"), escape(aCamp.getType())));
		}
		facts.add(fact(LanguageBundle.getString("in_infRank"),
				escape(String.valueOf(aCamp.getSafe(IntegerKey.CAMPAIGN_RANK)))));
		String gameModes = StringUtil.join(aCamp.getSafeListFor(ListKey.GAME_MODE), ", ");
		if (!gameModes.isEmpty())
		{
			facts.add(fact(LanguageBundle.getString("in_infGame"), escape(gameModes)));
		}

		urlFact(aCamp, URLKind.WEBSITE, "in_infWebsite").ifPresent(facts::add);
		urlFact(aCamp, URLKind.PURCHASE, "in_infPurchase").ifPresent(facts::add);
		urlFact(aCamp, URLKind.SURVEY, "in_infSurvey").ifPresent(facts::add);

		return facts;
	}

	/** The prerequisite requirements markup, or {@code null} when there are none. */
	private static String buildRequirements(Campaign aCamp)
	{
		String preString =
				PrerequisiteUtilities.preReqHTMLStringsForList(null, null, aCamp.getPrerequisiteList(), false);
		if (preString.isEmpty())
		{
			return null;
		}
		// in_InfoRequirements is an HTML pattern ("<br><b>Requirements:</b>&nbsp;{0}"); preString is markup.
		return LanguageBundle.getFormattedString("in_InfoRequirements", preString);
	}

	/** The allow-info markup ({@code {label, valueHtml}}), or {@code null} when empty. */
	private static Map<String, Object> buildAllow(Campaign aCamp)
	{
		String allow = AllowUtilities.getAllowInfo(null, aCamp);
		if (allow.isEmpty())
		{
			return null;
		}
		return fact(LanguageBundle.getString("in_requirements"), allow);
	}

	/** The titled text blocks (INFORMATION, COPYRIGHT, INCLUDED SOURCES), in order. */
	private static List<Map<String, Object>> buildSections(Campaign aCamp)
	{
		List<Map<String, Object>> sections = new ArrayList<>();
		addTextSection(sections, LanguageBundle.getString("in_infInf"), aCamp.getListFor(ListKey.INFO_TEXT), false);
		// Copyright is boilerplate legalese: render it muted and small so it does not compete.
		addTextSection(sections, LanguageBundle.getString("in_infCopyright"), aCamp.getListFor(ListKey.SECTION_15), true);
		addSubCampaignSection(sections, aCamp);
		return sections;
	}

	private static void addTextSection(List<Map<String, Object>> sections, String heading, List<String> lines,
			boolean muted)
	{
		if (lines == null)
		{
			return;
		}
		sections.add(section(heading, lines.stream().map(ModernHtmlInfoBuilder::escape).toList(), muted, false));
	}

	private static void addSubCampaignSection(List<Map<String, Object>> sections, Campaign aCamp)
	{
		List<Campaign> subCampaigns = aCamp.getSubCampaigns();
		List<CampaignSourceEntry> notFound = aCamp.getNotFoundSubCampaigns();
		if (subCampaigns == null || notFound == null || (subCampaigns.isEmpty() && notFound.isEmpty()))
		{
			return;
		}
		List<String> lines = new ArrayList<>();
		subCampaigns.forEach(sub -> lines.add(escape(sub.getDisplayName())));
		notFound.forEach(sub ->
				lines.add(escape(LanguageBundle.getFormattedString("in_infMissingCampaign", sub.getURI()))));
		sections.add(section(LanguageBundle.getString("in_infIncludedCampaigns"), lines, false, true));
	}

	private static Optional<Map<String, Object>> urlFact(Campaign aCamp, URLKind kind, String i18nKey)
	{
		List<CampaignURL> urls = aCamp.getSafeListFor(ListKey.CAMPAIGN_URL).stream()
				.filter(u -> u.getUrlKind() == kind)
				.toList();
		if (urls.isEmpty())
		{
			return Optional.empty();
		}
		String anchors = urls.stream()
				.map(u -> "<a href=\"" + escapeAttr(u.getUri().toString()) + "\">" + escape(u.getUrlDesc()) + "</a>")
				.collect(Collectors.joining(" | "));
		return Optional.of(fact(LanguageBundle.getString(i18nKey), anchors));
	}

	/** A {@code {label, valueHtml}} entry; {@code valueHtml} must be already-safe HTML. */
	private static Map<String, Object> fact(String label, String valueHtml)
	{
		Map<String, Object> f = new LinkedHashMap<>();
		f.put("label", label);
		f.put("valueHtml", valueHtml);
		return f;
	}

	private static Map<String, Object> section(String heading, List<String> lines, boolean muted, boolean columns)
	{
		Map<String, Object> s = new LinkedHashMap<>();
		s.put("heading", heading);
		s.put("lines", lines);
		s.put("muted", muted);
		// columns: render lines as a compact multi-column list rather than one per row.
		s.put("columns", columns);
		return s;
	}

	/**
	 * Produces the {@code <img src>} value for an image URI: relative to
	 * {@code dataDir} when the image lives under it, otherwise the absolute URL
	 * (vendor/homebrew/external images, which resolve directly under a
	 * {@code file:} document origin). Non-{@code file} schemes pass through.
	 */
	static String imageSrc(URI fileUri, Path dataDir)
	{
		if (fileUri == null)
		{
			return "";
		}
		if (!"file".equalsIgnoreCase(fileUri.getScheme()))
		{
			return fileUri.toString();
		}
		try
		{
			Path img = Path.of(fileUri).toAbsolutePath().normalize();
			Path base = dataDir.toAbsolutePath().normalize();
			if (!img.startsWith(base))
			{
				return fileUri.toString();
			}
			Path rel = base.relativize(img);
			StringBuilder sb = new StringBuilder(rel.getNameCount() * 16);
			for (int i = 0; i < rel.getNameCount(); i++)
			{
				if (i > 0)
				{
					sb.append('/');
				}
				sb.append(URLEncoder.encode(rel.getName(i).toString(), StandardCharsets.UTF_8).replace("+", "%20"));
			}
			return sb.toString();
		}
		catch (RuntimeException exception)
		{
			return fileUri.toString();
		}
	}

	static String escape(String text)
	{
		if (text == null)
		{
			return "";
		}
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private static String escapeAttr(String text)
	{
		return escape(text).replace("\"", "&quot;");
	}
}
