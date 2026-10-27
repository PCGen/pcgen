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
 */
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javafx.concurrent.Worker;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 * The prerequisite warning is built from markup: the message pattern in the
 * language bundle is HTML, and the engine's requirements string carries its own
 * {@code <font>} and {@code <i>} emphasis. Swing's {@code JOptionPane} rendered
 * that because it puts the message in a {@code JLabel}, which understands HTML.
 * JavaFX's {@code Alert} puts its content in a {@code Label}, which does not,
 * so the same string appeared as raw tags on screen.
 *
 * <p>These tests pin the two halves of the fix: the content node is a
 * {@link WebView} rather than a label, and the markup handed to it is the
 * engine's, unescaped.
 */
class BadComboAlertTest
{
	private static final String RED_ITALIC =
			"<font color=\"#ff0000\"><i>DSP - Psionics Unleashed</i></font>";

	private static BadComboAlert.Rejection rejection(String name, String requirements)
	{
		return new BadComboAlert.Rejection(name, requirements);
	}

	/** Single-source wording remains localized inside the styled document. */
	@Test
	void buildHtml_should_renderTheSingleMessage_inAStyledDocument()
	{
		var html = BadComboAlert.buildHtml(List.of(rejection("Book of Arcane Magic", RED_ITALIC)));

		assertAll(
				() -> assertTrue(html.startsWith("<!DOCTYPE html>"), html),
				() -> assertTrue(html.contains("<style>"), html),
				() -> assertTrue(html.contains(
						LanguageBundle.getFormattedString("in_src_badComboMsg", RED_ITALIC)), html));
	}

	@Test
	void buildHtml_should_keepEngineMarkupUnescaped()
	{
		var html = BadComboAlert.buildHtml(List.of(rejection("Book of Arcane Magic", RED_ITALIC)));

		assertTrue(html.contains(RED_ITALIC), "engine emphasis must survive into the document: " + html);
		assertFalse(html.contains("&lt;font"), "engine markup must not be escaped: " + html);
	}

	@Test
	void buildHtml_should_nameEveryRejection_whenThereAreSeveral()
	{
		var html = BadComboAlert.buildHtml(List.of(
				rejection("Book of Arcane Magic", RED_ITALIC),
				rejection("Path of Iron", "<i>Something Else</i>")));

		assertTrue(html.contains("Book of Arcane Magic"), html);
		assertTrue(html.contains("Path of Iron"), html);
		assertTrue(html.contains(RED_ITALIC), html);
		assertTrue(html.contains("<i>Something Else</i>"), html);
	}

	/**
	 * One rejection reads "This source ... it relies"; several must not. The
	 * plural pattern is a separate bundle key.
	 */
	@Test
	void buildHtml_should_useThePluralPattern_whenThereAreSeveral()
	{
		var html = BadComboAlert.buildHtml(List.of(
				rejection("Book of Arcane Magic", RED_ITALIC),
				rejection("Path of Iron", "<i>Something Else</i>")));

		assertFalse(html.contains(LanguageBundle.getFormattedString("in_src_badComboMsg", RED_ITALIC)),
				"the singular pattern must not be reused for several rejections: " + html);
	}

	/**
	 * Campaign names come from data files, so a name containing markup must not
	 * be able to restyle or break the document.
	 */
	@Test
	void buildHtml_should_escapeCampaignNames()
	{
		var html = BadComboAlert.buildHtml(List.of(
				rejection("Trouble <b>Maker</b> & \"friends\"", RED_ITALIC),
				rejection("Path of Iron", "<i>Something Else</i>")));

		assertTrue(html.contains("Trouble &lt;b&gt;Maker&lt;/b&gt; &amp; &quot;friends&quot;"), html);
		assertFalse(html.contains("Trouble <b>Maker</b>"), html);
		assertFalse(html.contains("&amp;lt;"), "plain text must be escaped exactly once: " + html);
	}

	@Test
	void buildHtml_should_returnNothing_whenNothingWasRejected()
	{
		assertEquals("", BadComboAlert.buildHtml(List.of()));
	}

	private static BadComboAlert.Covered covered(String name, String container)
	{
		return new BadComboAlert.Covered(name, container);
	}

	private static BadComboAlert.Rejection conflict(String... blockers)
	{
		return new BadComboAlert.Rejection("Conflicting Book", "", List.of(blockers));
	}

	/**
	 * A book inside a selected MultiSet is already loaded, so presenting it
	 * among the failures tells the user to fix something that is not broken.
	 */
	@Test
	void buildHtml_should_notUseTheFailureWording_whenEverythingWasAlreadyIncluded()
	{
		var html = BadComboAlert.buildHtml(List.of(),
				List.of(covered("Advanced Player's Guide", "Pathfinder RPG for Game Masters")));

		assertFalse(html.isEmpty(), "the user asked for something and deserves an answer");
		assertFalse(html.contains("cannot be selected"),
				"nothing failed, so nothing may be described as a failure: " + html);
	}

	@Test
	void buildHtml_should_nameTheBookAndItsContainer_whenAlreadyIncluded()
	{
		var html = BadComboAlert.buildHtml(List.of(),
				List.of(covered("Advanced Player's Guide", "Pathfinder RPG for Game Masters")));

		assertTrue(html.contains("Advanced Player&#39;s Guide"), html);
		assertTrue(html.contains("Pathfinder RPG for Game Masters"),
				"the container is the whole point of the message: " + html);
	}

	@Test
	void buildHtml_should_reportBothSections_whenSomeFailedAndSomeWereAlreadyIncluded()
	{
		var html = BadComboAlert.buildHtml(
				List.of(rejection("Bonus Bestiary", RED_ITALIC)),
				List.of(covered("Advanced Player's Guide", "Pathfinder RPG for Game Masters")));

		assertTrue(html.contains("Bonus Bestiary"), html);
		assertTrue(html.contains("Advanced Player&#39;s Guide"), html);
		assertTrue(html.contains(RED_ITALIC), html);
	}

	/**
	 * The failures are what the user has to act on, so they come first; the
	 * already-included note is reassurance and belongs underneath.
	 */
	@Test
	void buildHtml_should_putTheFailuresBeforeTheAlreadyIncludedNote()
	{
		var html = BadComboAlert.buildHtml(
				List.of(rejection("Bonus Bestiary", RED_ITALIC)),
				List.of(covered("Advanced Player's Guide", "Pathfinder RPG for Game Masters")));

		assertTrue(html.indexOf("Bonus Bestiary") < html.indexOf("Advanced Player&#39;s Guide"), html);
	}

	@Test
	void buildHtml_should_escapeNames_inTheAlreadyIncludedNote()
	{
		var html = BadComboAlert.buildHtml(List.of(),
				List.of(covered("Trouble <b>Maker</b>", "Bundle <i>X</i>")));

		assertTrue(html.contains("Trouble &lt;b&gt;Maker&lt;/b&gt;"), html);
		assertTrue(html.contains("Bundle &lt;i&gt;X&lt;/i&gt;"), html);
	}

	/**
	 * Five blockers read as "..., which is already selected. Remove it", which
	 * is both wrong and confusing about how many sources are in the way.
	 */
	@Test
	void buildHtml_should_usePluralConflictWording_forSeveralBlockers()
	{
		var html = BadComboAlert.buildHtml(List.of(
				conflict("Pathfinder RPG for Game Masters", "Ultimate Magic")));

		assertFalse(html.contains("which is already selected"),
				"two blockers are not an \"is\": " + html);
		assertFalse(html.contains("Remove it"), "there is more than one to remove: " + html);
	}

	@Test
	void buildHtml_should_useSingularConflictWording_forOneBlocker()
	{
		var html = BadComboAlert.buildHtml(List.of(conflict("Pathfinder RPG for Game Masters")));

		assertTrue(html.contains("which is already selected"), html);
		assertTrue(html.contains("Remove it"), html);
	}

	@Test
	void buildHtml_should_nameEveryBlocker()
	{
		var html = BadComboAlert.buildHtml(List.of(conflict("Ultimate Magic", "Ultimate Combat")));

		assertTrue(html.contains("Ultimate Magic"), html);
		assertTrue(html.contains("Ultimate Combat"), html);
	}

	/** Blocker names come from data files and must not be able to restyle the document. */
	@Test
	void buildHtml_should_escapeBlockerNames()
	{
		var html = BadComboAlert.buildHtml(List.of(conflict("Trouble <b>Maker</b> & \"friends\"")));

		assertTrue(html.contains("Trouble &lt;b&gt;Maker&lt;/b&gt; &amp; &quot;friends&quot;"), html);
		assertFalse(html.contains("Trouble <b>Maker</b>"), html);
		assertFalse(html.contains("&amp;lt;"), html);
	}

	@Test
	void buildHtml_should_returnNothing_whenNeitherSectionHasAnything()
	{
		assertEquals("", BadComboAlert.buildHtml(List.of(), List.of()));
	}

	/**
	 * A single failure alongside an already-included note cannot use the
	 * singular pattern, which ends by telling the user to consult Source Info
	 * for "the" requirements and would read as though it covered both.
	 */
	@Test
	void buildHtml_should_notUseTheSingularPattern_whenThereIsAlsoAnAlreadyIncludedNote()
	{
		var html = BadComboAlert.buildHtml(
				List.of(rejection("Bonus Bestiary", RED_ITALIC)),
				List.of(covered("Advanced Player's Guide", "Pathfinder RPG for Game Masters")));

		assertFalse(html.contains(LanguageBundle.getFormattedString("in_src_badComboMsg", RED_ITALIC)),
				"the singular pattern describes the whole dialog and cannot be reused here: " + html);
	}

	@ParameterizedTest
	@ValueSource(strings = {"fr", "es", "it"})
	void buildHtml_should_preserveLocalizedMessages_withoutNestedDocuments(String language)
	{
		Locale previous = Locale.getDefault();
		try
		{
			Locale.setDefault(Locale.of(language));
			LanguageBundle.reload();
			var html = BadComboAlert.buildHtml(List.of(rejection("Book", RED_ITALIC)));
			assertAll(
					() -> assertTrue(html.contains(
							LanguageBundle.getFormattedString("in_src_badComboMsg", RED_ITALIC)), html),
					() -> assertEquals(html.indexOf("<html>"), html.lastIndexOf("<html>"), html),
					() -> assertEquals(html.indexOf("<body>"), html.lastIndexOf("<body>"), html));
		}
		finally
		{
			Locale.setDefault(previous);
			LanguageBundle.reload();
			LanguageBundle.getString("in_src_badComboTitle");
		}
	}

	/** Only actual dialog and WebView tests need a toolkit and a Stage. */
	@Nested
	@ExtendWith(ApplicationExtension.class)
	class Rendering
	{
		private Stage stage;

		@Start
		void onStart(Stage primaryStage)
		{
			stage = primaryStage;
		}

		/** Dialogs own a Stage, which may only be constructed on the FX thread. */
		private static Alert buildOnFxThread(BadComboAlert.Rejection... rejections)
				throws InterruptedException, ExecutionException
		{
			return asyncFx(() -> BadComboAlert.build(List.of(rejections))).get();
		}

		@Test
		void build_should_configureAnHtmlAcknowledgement() throws Exception
		{
			asyncFx(() -> {
				var alert = BadComboAlert.build(List.of(rejection("Book of Arcane Magic", RED_ITALIC)));
				assertAll(
						() -> assertInstanceOf(WebView.class, alert.getDialogPane().getContent()),
						() -> assertEquals(LanguageBundle.getString("in_src_badComboTitle"), alert.getTitle()),
						() -> assertNull(alert.getHeaderText()),
						() -> assertEquals(1, alert.getButtonTypes().size()),
						() -> assertEquals(Alert.AlertType.INFORMATION, alert.getAlertType()));
			}).get();
		}

		/** Long reports keep the same initial size and scroll to reveal the rest. */
		@Test
		void longMessage_should_scrollWithoutChangingTheInitialSize() throws Exception
		{
			var many = new ArrayList<BadComboAlert.Rejection>();
			for (int i = 0; i < 40; i++)
			{
				many.add(rejection("Book " + i, RED_ITALIC));
			}
			var initialHeight = new AtomicReference<Double>();
			var alert = asyncFx(() -> {
				var created = BadComboAlert.build(many);
				initialHeight.set(((WebView) created.getDialogPane().getContent()).getPrefHeight());
				return created;
			}).get();
			try
			{
				var view = showAndLoad(alert);
				assertEquals(initialHeight.get(), asyncFx(view::getHeight).get(), 1,
						"message length must not change the initial viewport height");
				var offset = asyncFx(() -> {
					view.getEngine().executeScript("window.scrollTo(0, 10000)");
					return ((Number) view.getEngine().executeScript("window.pageYOffset")).doubleValue();
				}).get();
				assertTrue(offset > 0, "long reports must scroll to reveal the hidden content");
			}
			finally
			{
				asyncFx(alert::close).get();
			}
		}

		@Test
		void resizing_should_resizeAndReflowTheMessage() throws Exception
		{
			var alert = buildOnFxThread(
					rejection("Book of Arcane Magic", RED_ITALIC),
					rejection("Another Book", RED_ITALIC));
			try
			{
				var view = showAndLoad(alert);
				assertTrue(alert.isResizable());

				var wide = resize(alert, view, 900, 600);
				double wideText = bodyWidth(view);
				var narrow = resize(alert, view, 650, 350);
				double narrowText = bodyWidth(view);

				assertTrue(wide.getWidth() > narrow.getWidth() + 100, wide + " -> " + narrow);
				assertTrue(wide.getHeight() > narrow.getHeight() + 100, wide + " -> " + narrow);
				assertTrue(wideText > narrowText + 100,
						"message width must follow the resized viewport: " + wideText + " -> " + narrowText);
				assertEquals(narrow.getWidth(), narrowText, 20, "message must fill the available width");
				asyncFx(() -> {
					var engine = view.getEngine();
					double top = ((Number) engine.executeScript(
							"document.body.firstElementChild.getBoundingClientRect().top")).doubleValue();
					double right = ((Number) engine.executeScript(
							"document.documentElement.clientWidth"
									+ " - document.body.firstElementChild.getBoundingClientRect().right")).doubleValue();
					assertTrue(top >= 16 && right >= 16,
							"message needs space at the top and right: top=" + top + ", right=" + right);
				}).get();
			}
			finally
			{
				asyncFx(alert::close).get();
			}
		}

		private static WebView showAndLoad(Alert alert) throws Exception
		{
			var view = asyncFx(() -> {
				alert.show();
				return (WebView) alert.getDialogPane().getContent();
			}).get();
			waitFor(15, TimeUnit.SECONDS,
					() -> asyncFx(
							() -> view.getEngine().getLoadWorker().getState() == Worker.State.SUCCEEDED).get());
			waitForFxEvents();
			return view;
		}

		private static Dimension2D resize(Alert alert, WebView view, double width, double height) throws Exception
		{
			asyncFx(() -> {
				alert.setWidth(width);
				alert.setHeight(height);
			}).get();
			waitForFxEvents();
			return asyncFx(() -> new Dimension2D(view.getWidth(), view.getHeight())).get();
		}

		private static double bodyWidth(WebView view) throws Exception
		{
			return asyncFx(() -> ((Number) view.getEngine()
					.executeScript("document.body.getBoundingClientRect().width")).doubleValue()).get();
		}

		/** An owner centres the dialog over its parent rather than an arbitrary monitor. */
		@Test
		void build_should_centreOnItsOwner() throws Exception
		{
			var alert = asyncFx(() -> {
				stage.setScene(new Scene(new StackPane(), 520, 400));
				stage.show();
				return BadComboAlert.build(List.of(rejection("Book of Arcane Magic", RED_ITALIC)), List.of(), stage);
			}).get();

			assertEquals(stage, alert.getOwner(),
					"the warning must belong to the window that raised it, so it opens over it");
		}
	}
}
