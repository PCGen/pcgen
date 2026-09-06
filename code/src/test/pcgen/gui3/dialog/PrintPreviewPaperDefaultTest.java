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
import java.util.Locale;

import pcgen.LocaleDependentTestCase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PrintPreviewPaperDefaultTest
{
	private static final List<String> PAPERS = List.of("A4", "A5", "Letter", "Legal");

	@Test
	void persistedValueWinsWhenAvailable()
	{
		assertEquals("Legal", PrintPreviewPaperDefault.chooseDefault("Legal", "US", PAPERS));
	}

	@Test
	void persistedValueMatchedCaseInsensitively()
	{
		assertEquals("Letter", PrintPreviewPaperDefault.chooseDefault("letter", "DE", PAPERS));
	}

	@Test
	void blankPersistedFallsToLocale()
	{
		assertEquals("Letter", PrintPreviewPaperDefault.chooseDefault("  ", "US", PAPERS));
	}

	@Test
	void usLocaleDefaultsToLetter()
	{
		assertEquals("Letter", PrintPreviewPaperDefault.chooseDefault(null, "US", PAPERS));
	}

	@Test
	void canadaLocaleDefaultsToLetter()
	{
		assertEquals("Letter", PrintPreviewPaperDefault.chooseDefault(null, "CA", PAPERS));
	}

	@Test
	void otherLocaleDefaultsToA4()
	{
		assertEquals("A4", PrintPreviewPaperDefault.chooseDefault(null, "FR", PAPERS));
	}

	@Test
	void persistedValueNotInListIsIgnored()
	{
		assertEquals("A4", PrintPreviewPaperDefault.chooseDefault("B5", "GB", PAPERS));
	}

	@Test
	void emptyListReturnsNull()
	{
		assertNull(PrintPreviewPaperDefault.chooseDefault("A4", "US", List.of()));
	}

	@Test
	void usWithoutLetterFallsBackToFirst()
	{
		assertEquals("A4", PrintPreviewPaperDefault.chooseDefault(null, "US", List.of("A4", "Legal")));
	}

	// The following two tests actually flip the JVM default locale (via the shared
	// LocaleDependentTestCase helper) to verify chooseDefaultForCurrentLocale reads
	// the OS country correctly — this is the CODE-2537 requirement to test 2 locales.

	@Test
	void currentLocaleUsDefaultsToLetter()
	{
		LocaleDependentTestCase.before(Locale.US);
		try
		{
			assertEquals("Letter", PrintPreviewPaperDefault.chooseDefaultForCurrentLocale(null, PAPERS));
		}
		finally
		{
			LocaleDependentTestCase.after();
		}
	}

	@Test
	void currentLocaleFranceDefaultsToA4()
	{
		LocaleDependentTestCase.before(Locale.FRANCE);
		try
		{
			assertEquals("A4", PrintPreviewPaperDefault.chooseDefaultForCurrentLocale(null, PAPERS));
		}
		finally
		{
			LocaleDependentTestCase.after();
		}
	}
}
