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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.management.MemoryUsage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DebugDialogController#formatMemoryCell}. These exercise
 * the memory-table number handling with synthetic {@link MemoryUsage} values,
 * so no JavaFX toolkit or running application is needed.
 */
class DebugDialogControllerTest
{
	private static final long MB = 1024 * 1024;

	@Test
	@DisplayName("column 0 renders the row label verbatim")
	void formatMemoryCell_should_returnLabel_when_column0()
	{
		MemoryUsage usage = new MemoryUsage(0, 0, 0, 0);
		assertEquals("Heap", DebugDialogController.formatMemoryCell(0, "Heap", usage));
		assertEquals("Non-Heap", DebugDialogController.formatMemoryCell(0, "Non-Heap", usage));
	}

	@Test
	@DisplayName("columns 1-3 render init/used/committed in megabytes")
	void formatMemoryCell_should_renderMegabytes_when_sizeColumns()
	{
		MemoryUsage usage = new MemoryUsage(768 * MB, 189 * MB, 240 * MB, 2048 * MB);
		assertEquals("768", DebugDialogController.formatMemoryCell(1, "Heap", usage));
		assertEquals("189", DebugDialogController.formatMemoryCell(2, "Heap", usage));
		assertEquals("240", DebugDialogController.formatMemoryCell(3, "Heap", usage));
	}

	@Test
	@DisplayName("column 4 renders max in megabytes when a maximum is defined")
	void formatMemoryCell_should_renderMaxMegabytes_when_maxDefined()
	{
		MemoryUsage usage = new MemoryUsage(768 * MB, 189 * MB, 240 * MB, 2048 * MB);
		assertEquals("2,048", DebugDialogController.formatMemoryCell(4, "Heap", usage));
	}

	@Test
	@DisplayName("% used is rounded correctly (guards against the old integer-division-to-zero bug)")
	void formatMemoryCell_should_roundPercent_when_maxDefined()
	{
		// 189 / 2048 = 9.2% -> 9%. The old "100 * (used / max)" integer math gave 0.
		MemoryUsage usage = new MemoryUsage(768 * MB, 189 * MB, 240 * MB, 2048 * MB);
		assertEquals("9%", DebugDialogController.formatMemoryCell(5, "Heap", usage));
	}

	@Test
	@DisplayName("% used renders 0% and 100% at the edges")
	void formatMemoryCell_should_handleEdges_when_percent()
	{
		assertEquals("0%",
				DebugDialogController.formatMemoryCell(5, "Heap", new MemoryUsage(0, 0, 0, 1000)));
		assertEquals("100%",
				DebugDialogController.formatMemoryCell(5, "Heap", new MemoryUsage(0, 1000, 1000, 1000)));
	}

	@Test
	@DisplayName("max of -1 (no defined maximum) renders 'n/a' for both Max and % Used")
	void formatMemoryCell_should_returnNa_when_maxUndefined()
	{
		// Non-heap pools report getMax() == -1; the old code showed 0 for Max and
		// a large negative number for % Used.
		MemoryUsage usage = new MemoryUsage(7 * MB, 91 * MB, 97 * MB, -1);
		assertEquals("n/a", DebugDialogController.formatMemoryCell(4, "Non-Heap", usage));
		assertEquals("n/a", DebugDialogController.formatMemoryCell(5, "Non-Heap", usage));
	}

	@Test
	@DisplayName("an unknown column index throws")
	void formatMemoryCell_should_throw_when_unknownColumn()
	{
		MemoryUsage usage = new MemoryUsage(0, 0, 0, 0);
		assertThrows(IllegalStateException.class,
				() -> DebugDialogController.formatMemoryCell(6, "Heap", usage));
	}
}
