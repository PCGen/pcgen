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
package pcgen.persistence.lst;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tips files must be named tips.txt; tips.lst is not read by TipOfTheDayHandler. */
class TipsFilenameConventionTest
{
	@Test
	void gameModeTips_mustNotUseLstExtension() throws IOException
	{
		Path gameModes = Path.of("system", "gameModes");
		List<Path> strays;
		long tipsTxtCount;
		try (var files = Files.walk(gameModes))
		{
			strays = files.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().equals("tips.lst"))
					.sorted().toList();
		}
		try (var files = Files.walk(gameModes))
		{
			tipsTxtCount = files.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().equals("tips.txt"))
					.count();
		}
		assertAll(
				() -> assertTrue(strays.isEmpty(),
						() -> "Tips files must be tips.txt, not tips.lst: " + strays),
				() -> assertFalse(tipsTxtCount == 0,
						() -> "Expected at least one tips.txt under " + gameModes
								+ "; the walk may have reached the wrong directory"));
	}
}
