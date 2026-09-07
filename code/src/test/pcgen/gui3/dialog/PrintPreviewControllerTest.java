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

import java.nio.file.Path;

import pcgen.cdom.base.Constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the character-sheet template filter used to populate the print-preview
 * template list (the one piece of {@link PrintPreviewController} with real,
 * silently-breakable branching logic).
 */
class PrintPreviewControllerTest
{
	private static final String PREFIX = Constants.CHARACTER_TEMPLATE_PREFIX;

	@Test
	void acceptsCharacterTemplateInPdfDir()
	{
		assertTrue(PrintPreviewController.isCharacterTemplate(Path.of("outputsheets", "pdf", PREFIX + "_std.xslt")));
	}

	@Test
	void pdfParentMatchIsCaseInsensitive()
	{
		assertTrue(PrintPreviewController.isCharacterTemplate(Path.of("outputsheets", "PDF", PREFIX + "_std.xslt")));
	}

	@Test
	void rejectsWhenParentIsNotPdf()
	{
		assertFalse(PrintPreviewController.isCharacterTemplate(Path.of("outputsheets", "html", PREFIX + "_std.htm")));
	}

	@Test
	void rejectsIntermediateFoFile()
	{
		assertFalse(PrintPreviewController.isCharacterTemplate(Path.of("outputsheets", "pdf", PREFIX + "_std.fo")));
	}

	@Test
	void rejectsWhenNameLacksTemplatePrefix()
	{
		assertFalse(PrintPreviewController.isCharacterTemplate(Path.of("outputsheets", "pdf", "psheet_party.xslt")));
	}

	@Test
	void rejectsBarePathWithoutParent()
	{
		assertFalse(PrintPreviewController.isCharacterTemplate(Path.of(PREFIX + "_std.xslt")));
	}
}
