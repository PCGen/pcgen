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
 */
package pcgen.system;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests that {@link Main#ensureSavePathExists(String)} creates the save
 * directory even when its parents are missing.
 */
class MainTest
{
	@Test
	void createsSaveDirWhenParentMissing(@TempDir Path base)
	{
		Path saveDir = base.resolve("PCGen").resolve("characters");

		assertTrue(Main.ensureSavePathExists(saveDir.toString()),
				"ensureSavePathExists must report success when creating a nested dir");
		assertTrue(Files.isDirectory(saveDir),
				"Save dir and its missing parent must be created");
	}

	@Test
	void succeedsWhenDirAlreadyExists(@TempDir Path existing)
	{
		assertTrue(Main.ensureSavePathExists(existing.toString()),
				"An already-existing directory must be treated as success");
	}
}
