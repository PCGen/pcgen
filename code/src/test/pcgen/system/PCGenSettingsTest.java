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

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the default character/portraits/backup directory is rooted in
 * the user's home rather than {@code user.dir}.
 */
class PCGenSettingsTest
{
	@Test
	void defaultCharactersDirIsRootedInUserHome()
	{
		String dir = PCGenSettings.defaultCharactersDir();

		assertTrue(dir.startsWith(SystemUtils.USER_HOME),
				"Expected default characters dir to live under USER_HOME but was: " + dir);
		assertTrue(dir.endsWith(File.separator + "PCGen" + File.separator + "characters"),
				"Expected default characters dir to end with PCGen/characters but was: " + dir);
	}

	@Test
	void defaultCharactersDirIsNotInsideInstallDir()
	{
		// USER_DIR is the JVM working directory; under Program Files installs it is the
		// install root, which is not user-writable. The fix decouples the default from it.
		String dir = PCGenSettings.defaultCharactersDir();
		String installRootedDefault = SystemUtils.USER_DIR + File.separator + "characters";

		assertTrue(!dir.equals(installRootedDefault),
				"Default characters dir must not equal user.dir/characters but was: " + dir);
	}
}
