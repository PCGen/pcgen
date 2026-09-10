/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
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
package pcgen.gui3.dialog;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrintPreviewPaperDefaultTest
{
	// ── parseDimensionToPoints ──────────────────────────────────────────────

	@Test
	void parseInchesLetter11()
	{
		assertEquals(792.0, PrintPreviewPaperDefault.parseDimensionToPoints("11in"), 0.001);
	}

	@Test
	void parseInchesLetter8_5()
	{
		assertEquals(612.0, PrintPreviewPaperDefault.parseDimensionToPoints("8.5in"), 0.001);
	}

	@Test
	void parseMmA4Height297()
	{
		assertEquals(841.89, PrintPreviewPaperDefault.parseDimensionToPoints("297mm"), 0.5);
	}

	@Test
	void parseMmA4Width210()
	{
		assertEquals(595.28, PrintPreviewPaperDefault.parseDimensionToPoints("210mm"), 0.5);
	}

	@Test
	void parseBadStringReturnsNaN()
	{
		assertTrue(Double.isNaN(PrintPreviewPaperDefault.parseDimensionToPoints("abc")));
	}

	@Test
	void parseEmptyStringReturnsNaN()
	{
		assertTrue(Double.isNaN(PrintPreviewPaperDefault.parseDimensionToPoints("")));
	}

	@Test
	void parseNullReturnsNaN()
	{
		assertTrue(Double.isNaN(PrintPreviewPaperDefault.parseDimensionToPoints(null)));
	}

	@Test
	void parseBareNumberReturnsNaN()
	{
		assertTrue(Double.isNaN(PrintPreviewPaperDefault.parseDimensionToPoints("11")));
	}

	// ── matchByDimensions ──────────────────────────────────────────────────

	private static final List<PrintPreviewPaperDefault.PaperOption> PAPER_OPTIONS = List.of(
			new PrintPreviewPaperDefault.PaperOption("A4", 595.28, 841.89),
			new PrintPreviewPaperDefault.PaperOption("Letter", 612.0, 792.0),
			new PrintPreviewPaperDefault.PaperOption("Legal", 612.0, 1008.0)
	);

	@Test
	void matchLetterDimensionsReturnsLetter()
	{
		assertEquals("Letter", PrintPreviewPaperDefault.matchByDimensions(612.0, 792.0, PAPER_OPTIONS));
	}

	@Test
	void matchA4DimensionsReturnsA4()
	{
		assertEquals("A4", PrintPreviewPaperDefault.matchByDimensions(595.28, 841.89, PAPER_OPTIONS));
	}

	@Test
	void matchOrientationSwappedLetterStillReturnsLetter()
	{
		// Portrait vs landscape — orientation-agnostic match
		assertEquals("Letter", PrintPreviewPaperDefault.matchByDimensions(792.0, 612.0, PAPER_OPTIONS));
	}

	@Test
	void matchFarFromEverythingReturnsNull()
	{
		assertNull(PrintPreviewPaperDefault.matchByDimensions(100.0, 100.0, PAPER_OPTIONS));
	}

	@Test
	void matchEmptyOptionsReturnsNull()
	{
		assertNull(PrintPreviewPaperDefault.matchByDimensions(612.0, 792.0, List.of()));
	}

	@Test
	void matchNearLetterWithinToleranceReturnsLetter()
	{
		// 1 pt off — well within 5pt tolerance
		assertEquals("Letter", PrintPreviewPaperDefault.matchByDimensions(611.0, 791.0, PAPER_OPTIONS));
	}

	// ── 6-arg chooseDefault — printer dimension step ────────────────────────

	@Test
	void printerDimensionsWinOverLocaleWhenNoPersistedValue()
	{
		// Printer reports Letter size; locale is FR (would pick A4 without printer)
		assertEquals("Letter",
				PrintPreviewPaperDefault.chooseDefault(null, "FR", 612.0, 792.0, List.of("A4", "Letter"), PAPER_OPTIONS));
	}

	@Test
	void persistedStillWinsOverPrinterDimensions()
	{
		// Persisted="Legal" must win even if printer says Letter
		assertEquals("Legal",
				PrintPreviewPaperDefault.chooseDefault("Legal", "US", 612.0, 792.0,
						List.of("A4", "Letter", "Legal"), PAPER_OPTIONS));
	}

	@Test
	void canadaLocaleFallsBackToLetter()
	{
		// No persisted value, no printer dims → CA is a Letter locale like US
		assertEquals("Letter",
				PrintPreviewPaperDefault.chooseDefault(null, "CA", 0.0, 0.0, List.of("A4", "Letter"), PAPER_OPTIONS));
	}

	@Test
	void nullCountryFallsBackToA4()
	{
		// Unknown country → non-Letter locale → A4
		assertEquals("A4",
				PrintPreviewPaperDefault.chooseDefault(null, null, 0.0, 0.0, List.of("A4", "Letter"), PAPER_OPTIONS));
	}

	@Test
	void localeWithoutMatchingSizeFallsBackToFirstAvailable()
	{
		// US locale but no Letter-sized option (by name or dimension) → first available as last resort
		List<PrintPreviewPaperDefault.PaperOption> a4Only =
				List.of(new PrintPreviewPaperDefault.PaperOption("A4", 595.28, 841.89));
		assertEquals("A4",
				PrintPreviewPaperDefault.chooseDefault(null, "US", 0.0, 0.0, List.of("A4"), a4Only));
	}

	@Test
	void zeroPrinterDimensionsFallsBackToLocale()
	{
		// w=0, h=0 → dimension match skipped → locale (US) → Letter
		assertEquals("Letter",
				PrintPreviewPaperDefault.chooseDefault(null, "US", 0.0, 0.0, List.of("A4", "Letter"), PAPER_OPTIONS));
	}

	@Test
	void printerDimsNotMatchingAnyOptionFallsBackToLocale()
	{
		// Printer is 100×100 — no match → falls back to locale FR → A4
		assertEquals("A4",
				PrintPreviewPaperDefault.chooseDefault(null, "FR", 100.0, 100.0, List.of("A4", "Letter"), PAPER_OPTIONS));
	}

	// ── locale fallback resolves BY DIMENSION, not just by name ─────────────
	// Papers can carry localized names (e.g. "in_PaperLetter"); the locale default must still find
	// the right size by its dimensions. Without dimension matching these would pick the first option.

	private static final List<PrintPreviewPaperDefault.PaperOption> LOCALIZED_OPTIONS = List.of(
			new PrintPreviewPaperDefault.PaperOption("Papier A4", 595.28, 841.89),
			new PrintPreviewPaperDefault.PaperOption("US Brief", 612.0, 792.0)
	);
	private static final List<String> LOCALIZED_NAMES = List.of("Papier A4", "US Brief");

	@Test
	void frLocaleMatchesA4ByDimensionWhenNameLacksA4()
	{
		// Name "Papier A4" contains "A4" incidentally, but "US Brief" does not contain "Letter";
		// FR must resolve A4 by dimension, not accidentally return the first option.
		assertEquals("Papier A4",
				PrintPreviewPaperDefault.chooseDefault(null, "FR", 0.0, 0.0, LOCALIZED_NAMES, LOCALIZED_OPTIONS));
	}

	@Test
	void usLocaleMatchesLetterByDimensionWhenNameLacksLetter()
	{
		// "US Brief" is Letter-sized but its name has no "Letter"; the name-only fallback would return
		// the first option ("Papier A4"). Dimension matching must pick "US Brief".
		assertEquals("US Brief",
				PrintPreviewPaperDefault.chooseDefault(null, "US", 0.0, 0.0, LOCALIZED_NAMES, LOCALIZED_OPTIONS));
	}
}
