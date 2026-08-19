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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the writable default and the round-trip persistence of
 * {@link PCGenSettings#LAST_CHARACTER_PATH}.
 */
class PCGenSettingsTest
{
	@Test
	void defaultCharactersDirIsRootedInUserHome()
	{
		String dir = PCGenSettings.defaultCharactersDir();

		assertTrue(dir.startsWith(SystemUtils.USER_HOME),
				"Expected default characters dir to live under USER_HOME but was: " + dir);
		assertTrue(dir.endsWith(File.separator + PCGenSettings.USER_DIR_NAME + File.separator + "characters"),
				"Expected default characters dir to end with PCGen/characters but was: " + dir);
	}

	@Test
	void defaultCharactersDirIsNotInsideInstallDir()
	{
		// USER_DIR is the JVM working directory; under Program Files installs it is the
		// install root, which is not user-writable. The fix decouples the default from it.
		String dir = PCGenSettings.defaultCharactersDir();
		String installRootedDefault = SystemUtils.USER_DIR + File.separator + "characters";

		assertNotEquals(installRootedDefault, dir,
				"Default characters dir must not equal user.dir/characters");
	}

	/**
	 * Locks the durability claim: {@code LAST_CHARACTER_PATH} written in one
	 * "session" is read back in the next via {@code options.ini}.
	 */
	@Test
	void lastCharacterPathRoundTripsViaOptionsIni(@TempDir Path settingsDir)
	{
		String chosenFolder = "/some/folder/the/user/picked";

		// Session 1: write the property and flush options.ini to the temp dir.
		PropertyContextFactory factory1 = new PropertyContextFactory(settingsDir.toString());
		TestableSettings session1 = new TestableSettings();
		factory1.registerAndLoadPropertyContext(session1);
		session1.setProperty(PCGenSettings.LAST_CHARACTER_PATH, chosenFolder);
		factory1.savePropertyContexts();

		// Session 2: a fresh factory + context reads the same file back.
		PropertyContextFactory factory2 = new PropertyContextFactory(settingsDir.toString());
		TestableSettings session2 = new TestableSettings();
		factory2.registerAndLoadPropertyContext(session2);

		assertEquals(chosenFolder, session2.getProperty(PCGenSettings.LAST_CHARACTER_PATH),
				"Persisted folder must survive a save+load cycle through options.ini");
	}

	/** Plain {@link PropertyContext} so we don't touch the {@code PCGenSettings} singleton. */
	private static final class TestableSettings extends PropertyContext
	{
		TestableSettings()
		{
			super("options.ini");
		}
	}

	/**
	 * Character files are user documents, so on freedesktop systems they belong
	 * under the configured documents folder rather than a bare {@code $HOME}
	 * entry. The file is parsed directly because {@code xdg-user-dir} may not be
	 * installed.
	 */
	@Nested
	class DocumentsDirFromUserDirs
	{
		private static final String HOME = "/home/tester";

		private Path write(Path dir, String content) throws IOException
		{
			Path file = dir.resolve("user-dirs.dirs");
			Files.writeString(file, content);
			return file;
		}

		@Test
		void expandsQuotedHomeReference(@TempDir Path dir) throws IOException
		{
			Path file = write(dir, """
					XDG_DESKTOP_DIR="$HOME/Desktop"
					XDG_DOCUMENTS_DIR="$HOME/Documents"
					""");

			assertEquals(Optional.of(HOME + "/Documents"),
					PCGenSettings.documentsDirFromUserDirs(file, HOME));
		}

		@Test
		void expandsBracedHomeReference(@TempDir Path dir) throws IOException
		{
			Path file = write(dir, "XDG_DOCUMENTS_DIR=\"${HOME}/Dokumente\"\n");

			assertEquals(Optional.of(HOME + "/Dokumente"),
					PCGenSettings.documentsDirFromUserDirs(file, HOME));
		}

		@Test
		void acceptsUnquotedAbsolutePath(@TempDir Path dir) throws IOException
		{
			Path file = write(dir, "XDG_DOCUMENTS_DIR=/mnt/shared/docs\n");

			assertEquals(Optional.of("/mnt/shared/docs"),
					PCGenSettings.documentsDirFromUserDirs(file, HOME));
		}

		@Test
		void ignoresCommentedEntries(@TempDir Path dir) throws IOException
		{
			Path file = write(dir, """
					# XDG_DOCUMENTS_DIR="$HOME/Commented"
					XDG_DOCUMENTS_DIR="$HOME/Real"
					""");

			assertEquals(Optional.of(HOME + "/Real"),
					PCGenSettings.documentsDirFromUserDirs(file, HOME));
		}

		/** The file is sourced by a shell, so a later assignment overwrites an earlier one. */
		@Test
		void lastEntryWins(@TempDir Path dir) throws IOException
		{
			Path file = write(dir, """
					XDG_DOCUMENTS_DIR="$HOME/First"
					XDG_DOCUMENTS_DIR="$HOME/Second"
					""");

			assertEquals(Optional.of(HOME + "/Second"),
					PCGenSettings.documentsDirFromUserDirs(file, HOME));
		}

		@Test
		void returnsEmptyWhenKeyAbsent(@TempDir Path dir) throws IOException
		{
			Path file = write(dir, "XDG_MUSIC_DIR=\"$HOME/Music\"\n");

			assertEquals(Optional.empty(), PCGenSettings.documentsDirFromUserDirs(file, HOME));
		}

		@Test
		void returnsEmptyWhenFileMissing(@TempDir Path dir)
		{
			assertEquals(Optional.empty(),
					PCGenSettings.documentsDirFromUserDirs(dir.resolve("absent.dirs"), HOME));
		}

		/** A key that merely shares the prefix must not be mistaken for the documents entry. */
		@Test
		void doesNotMatchSimilarlyNamedKeys(@TempDir Path dir) throws IOException
		{
			Path file = write(dir, "XDG_DOCUMENTS_DIR_EXTRA=\"$HOME/Nope\"\n");

			assertEquals(Optional.empty(), PCGenSettings.documentsDirFromUserDirs(file, HOME));
		}
	}
}
