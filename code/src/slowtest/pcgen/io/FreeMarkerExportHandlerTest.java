/*
 * Copyright 2026 the PCGen Project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package pcgen.io;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import pcgen.AbstractCharacterTestCase;

import org.junit.jupiter.api.Test;

/**
 * Exercises the FreeMarker handler with a published character sheet.
 */
class FreeMarkerExportHandlerTest extends AbstractCharacterTestCase
{
	private static final File TEMPLATE =
		new File("outputsheets/d20/fantasy/htmlxml/csheet_test-html.ftl");

	@Test
	void exportsPublishedSheet() throws Exception
	{
		getCharacter().setName("FreeMarker Hero");
		StringWriter output = new StringWriter();

		try (BufferedWriter writer = new BufferedWriter(output))
		{
			ExportHandler.createExportHandler(TEMPLATE).write(getCharacter(), writer);

			String html = output.toString();
			assertAll(
				() -> assertTrue(html.contains("<p> Character Name: FreeMarker Hero </p>")),
				() -> assertTrue(html.contains("<p> Fighter: 0 </p>")),
				() -> assertTrue(html.contains("<p> Rogue: 0 </p>")),
				() -> assertTrue(html.stripTrailing().endsWith("</html>")));
		}
	}

	@Test
	void propagatesFinalFlushFailure() throws Exception
	{
		IOException failure = new IOException("Output flush failed");
		try (BufferedWriter writer = new BufferedWriter(new StringWriter())
		{
			private boolean flushed;

			@Override
			public void flush() throws IOException
			{
				// Template.process auto-flushes first; fail the handler's final flush.
				if (flushed)
				{
					throw failure;
				}
				super.flush();
				flushed = true;
			}
		})
		{
			ExportException exception = assertThrows(ExportException.class,
				() -> ExportHandler.createExportHandler(TEMPLATE).write(getCharacter(), writer));

			assertSame(failure, exception.getCause());
		}
	}
}
