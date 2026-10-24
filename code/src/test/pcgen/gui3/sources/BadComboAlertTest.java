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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

	/**
	 * With a single rejection the user must see exactly what Swing showed, so
	 * the existing translated pattern is used verbatim rather than a new one.
	 */
	@Test
	void buildHtml_should_matchTheLegacyMessage_forOneRejection()
	{
		var html = BadComboAlert.buildHtml(List.of(rejection("Book of Arcane Magic", RED_ITALIC)));

		assertEquals(LanguageBundle.getFormattedString("in_src_badComboMsg", RED_ITALIC), html);
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
				rejection("Trouble <b>Maker</b>", RED_ITALIC),
				rejection("Path of Iron", "<i>Something Else</i>")));

		assertTrue(html.contains("Trouble &lt;b&gt;Maker&lt;/b&gt;"), html);
		assertFalse(html.contains("Trouble <b>Maker</b>"), html);
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

		assertTrue(html.contains("Advanced Player's Guide"), html);
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
		assertTrue(html.contains("Advanced Player's Guide"), html);
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

		assertTrue(html.indexOf("Bonus Bestiary") < html.indexOf("Advanced Player's Guide"), html);
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
	void blockedBySelected_should_usePluralWording_forSeveralBlockers()
	{
		var html = BadComboAlert.blockedBySelected(
				List.of("Pathfinder RPG for Game Masters", "Ultimate Magic"));

		assertFalse(html.contains("which is already selected"),
				"two blockers are not an \"is\": " + html);
		assertFalse(html.contains("Remove it"), "there is more than one to remove: " + html);
	}

	@Test
	void blockedBySelected_should_useSingularWording_forOneBlocker()
	{
		var html = BadComboAlert.blockedBySelected(List.of("Pathfinder RPG for Game Masters"));

		assertTrue(html.contains("which is already selected"), html);
		assertTrue(html.contains("Remove it"), html);
	}

	@Test
	void blockedBySelected_should_nameEveryBlocker()
	{
		var html = BadComboAlert.blockedBySelected(List.of("Ultimate Magic", "Ultimate Combat"));

		assertTrue(html.contains("Ultimate Magic"), html);
		assertTrue(html.contains("Ultimate Combat"), html);
	}

	/** Blocker names come from data files and must not be able to restyle the document. */
	@Test
	void blockedBySelected_should_escapeBlockerNames()
	{
		var html = BadComboAlert.blockedBySelected(List.of("Trouble <b>Maker</b>"));

		assertTrue(html.contains("Trouble &lt;b&gt;Maker&lt;/b&gt;"), html);
		assertFalse(html.contains("Trouble <b>Maker</b>"), html);
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

	/**
	 * Rejecting every book in a large collection produces a list far taller
	 * than any screen, and a dialog taller than the screen cannot be dismissed:
	 * its button bar is off the bottom edge.
	 */
	@Test
	void viewHeightFor_should_capAtThreeQuartersOfTheScreen()
	{
		var height = BadComboAlert.viewHeightFor(4000, 1000);

		assertTrue(height <= 750,
				"a dialog must not exceed three quarters of a 1000px screen, but asked for " + height);
	}

	/** Content that fits must still be shown whole -- the cap is a ceiling, not a size. */
	@Test
	void viewHeightFor_should_leaveContentThatFitsAlone()
	{
		var height = BadComboAlert.viewHeightFor(120, 1000);

		assertTrue(height >= 120, "content of 120px must not be clipped, but got " + height);
		assertTrue(height < 300, "a short message must not be padded out to the cap, but got " + height);
	}

	/** A cramped screen must still leave something readable rather than collapsing. */
	@Test
	void viewHeightFor_should_stayUsable_onASmallScreen()
	{
		var height = BadComboAlert.viewHeightFor(4000, 200);

		assertTrue(height > 0, "the view must keep a usable height, but got " + height);
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
		void build_should_renderTheMessageInAWebView_notALabel() throws Exception
		{
			var alert = buildOnFxThread(rejection("Book of Arcane Magic", RED_ITALIC));

			assertInstanceOf(WebView.class, alert.getDialogPane().getContent(),
					"HTML content needs a WebView; a Label shows the markup verbatim");
		}

		@Test
		void build_should_useTheTranslatedTitle() throws Exception
		{
			var alert = buildOnFxThread(rejection("Book of Arcane Magic", RED_ITALIC));

			assertEquals(LanguageBundle.getString("in_src_badComboTitle"), alert.getTitle());
		}

		/** An alert type's default header ("Information") adds no useful message. */
		@Test
		void build_should_notShowADefaultHeader() throws Exception
		{
			var alert = buildOnFxThread(rejection("Book of Arcane Magic", RED_ITALIC));

			assertNull(alert.getHeaderText());
		}

		@Test
		void build_should_offerOnlyAnAcknowledgement() throws Exception
		{
			var alert = buildOnFxThread(rejection("Book of Arcane Magic", RED_ITALIC));

			assertEquals(1, alert.getButtonTypes().size(), "an informational warning needs one way out");
			assertEquals(Alert.AlertType.INFORMATION, alert.getAlertType());
		}

		/**
		 * WebKit stretches the document to the viewport, so scrollHeight and
		 * offsetHeight would incorrectly return this view's 600px height.
		 */
		@Test
		void measureContentHeight_should_measureTheContent_notTheViewport() throws Exception
		{
			var html = BadComboAlert.buildHtml(List.of(rejection("Book of Arcane Magic", RED_ITALIC)));
			var view = asyncFx(() -> {
				var created = new WebView();
				created.setPrefSize(480, 600);
				stage.setScene(new Scene(new StackPane(created), 520, 600));
				stage.show();
				created.getEngine().loadContent(html);
				return created;
			}).get();

			waitFor(15, TimeUnit.SECONDS,
					() -> asyncFx(
							() -> view.getEngine().getLoadWorker().getState() == Worker.State.SUCCEEDED).get());
			waitForFxEvents();

			var height = asyncFx(() -> BadComboAlert.measureContentHeight(view.getEngine())).get();

			assertTrue(height > 0, "the message must have a measurable height, but was " + height);
			assertTrue(height < 300,
					"a three-line message must measure its content, not the 600px viewport, but was " + height);
		}

		/** The capped WebView must scroll so the user can reach the hidden content. */
		@Test
		void cappedView_should_scrollToReachTheHiddenContent() throws Exception
		{
			var many = new ArrayList<BadComboAlert.Rejection>();
			for (int i = 0; i < 40; i++)
			{
				many.add(rejection("Book " + i, RED_ITALIC));
			}
			var html = BadComboAlert.buildHtml(many);
			var view = asyncFx(() -> {
				var created = new WebView();
				created.setPrefSize(480, 150);
				stage.setScene(new Scene(new StackPane(created), 520, 150));
				stage.show();
				created.getEngine().loadContent(html);
				return created;
			}).get();
			waitFor(15, TimeUnit.SECONDS,
					() -> asyncFx(
							() -> view.getEngine().getLoadWorker().getState() == Worker.State.SUCCEEDED).get());
			waitForFxEvents();

			var offset = asyncFx(() -> {
				view.getEngine().executeScript("window.scrollTo(0, 10000)");
				return ((Number) view.getEngine().executeScript("window.pageYOffset")).doubleValue();
			}).get();

			assertTrue(offset > 0,
					"a list taller than the view must scroll to reveal the rest, but stayed at " + offset);
		}

		@Test
		void build_should_letTheUserResizeTheDialog() throws Exception
		{
			var alert = buildOnFxThread(rejection("Book of Arcane Magic", RED_ITALIC));

			assertTrue(alert.isResizable(), "a dialog with a capped, scrolling list must be resizable");
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
