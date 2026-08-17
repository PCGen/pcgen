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
package pcgen.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link ConfigurationSettings#findInstallRoot(String)} — how PCGen
 * locates its bundled data from java.home across the jpackage bundle layouts.
 */
class ConfigurationSettingsTest
{
	/** Create dir/{data,system} so it passes the install-root marker check. */
	private static Path makeInstallRoot(Path dir) throws IOException
	{
		Files.createDirectories(dir.resolve("data"));
		Files.createDirectories(dir.resolve("system"));
		return dir;
	}

	@Test
	void findInstallRoot_should_findAppDir_when_macLayout(@TempDir Path bundle) throws IOException
	{
		// PcGen.app/Contents/{runtime/Contents/Home, app/{data,system}}
		Path contents = Files.createDirectories(bundle.resolve("Contents"));
		Path javaHome = Files.createDirectories(contents.resolve("runtime/Contents/Home"));
		Path app = makeInstallRoot(contents.resolve("app"));

		Optional<Path> root = ConfigurationSettings.findInstallRoot(javaHome.toString());

		assertEquals(app.toRealPath(), root.orElseThrow().toRealPath());
	}

	@Test
	void findInstallRoot_should_findAppDir_when_linuxLayout(@TempDir Path image) throws IOException
	{
		// PcGen/lib/{runtime, app/{data,system}}
		Path lib = Files.createDirectories(image.resolve("lib"));
		Path javaHome = Files.createDirectories(lib.resolve("runtime"));
		Path app = makeInstallRoot(lib.resolve("app"));

		Optional<Path> root = ConfigurationSettings.findInstallRoot(javaHome.toString());

		assertEquals(app.toRealPath(), root.orElseThrow().toRealPath());
	}

	@Test
	void findInstallRoot_should_findAppDir_when_windowsLayout(@TempDir Path image) throws IOException
	{
		// PcGen\{runtime, app\{data,system}}
		Path javaHome = Files.createDirectories(image.resolve("runtime"));
		Path app = makeInstallRoot(image.resolve("app"));

		Optional<Path> root = ConfigurationSettings.findInstallRoot(javaHome.toString());

		assertEquals(app.toRealPath(), root.orElseThrow().toRealPath());
	}

	@Test
	void findInstallRoot_should_findAncestor_when_dataIsAboveJavaHome(@TempDir Path root) throws IOException
	{
		// data/system sit at the root, java.home is nested below (a dev-style
		// checkout run against a JDK inside the tree)
		makeInstallRoot(root);
		Path javaHome = Files.createDirectories(root.resolve("nested/jdk/home"));

		Optional<Path> found = ConfigurationSettings.findInstallRoot(javaHome.toString());

		assertEquals(root.toRealPath(), found.orElseThrow().toRealPath());
	}

	@Test
	void findInstallRoot_should_beEmpty_when_noDataFound(@TempDir Path javaHome)
	{
		// java.home with no data/system anywhere up the tree — caller falls back
		// to user.dir.
		Optional<Path> found = ConfigurationSettings.findInstallRoot(javaHome.toString());

		assertTrue(found.isEmpty());
	}

	@Test
	void findInstallRoot_should_beEmpty_when_javaHomeNull()
	{
		assertTrue(ConfigurationSettings.findInstallRoot(null).isEmpty());
	}

	@Test
	void findInstallRoot_should_requireBothMarkers_when_onlyDataPresent(@TempDir Path app) throws IOException
	{
		// A lone "data" dir must not match — the marker is data AND system.
		Files.createDirectories(app.resolve("data"));
		Path javaHome = Files.createDirectories(app.resolve("runtime"));

		Optional<Path> found = ConfigurationSettings.findInstallRoot(javaHome.toString());

		assertTrue(found.isEmpty());
	}

	/**
	 * Regression guard: the search used to inspect every child of every ancestor
	 * all the way to the filesystem root, so any unrelated directory holding
	 * data+system was adopted as the install root. A stray PCGen checkout sitting
	 * beside the temp directory was enough to break unrelated runs.
	 */
	@Test
	void findInstallRoot_should_ignoreUnrelatedSibling_when_notNamedApp(@TempDir Path parent) throws IOException
	{
		makeInstallRoot(parent.resolve("some-other-checkout"));
		Path javaHome = Files.createDirectories(parent.resolve("runtime"));

		Optional<Path> found = ConfigurationSettings.findInstallRoot(javaHome.toString());

		assertTrue(found.isEmpty(),
				"An arbitrary sibling directory must not be mistaken for the install root, but got: " + found);
	}

	/** The bundled layout is specifically an "app" directory beside "runtime". */
	@Test
	void findInstallRoot_should_findAppSibling_when_bothSiblingsExist(@TempDir Path parent) throws IOException
	{
		makeInstallRoot(parent.resolve("decoy"));
		Path app = makeInstallRoot(parent.resolve("app"));
		Path javaHome = Files.createDirectories(parent.resolve("runtime"));

		Optional<Path> found = ConfigurationSettings.findInstallRoot(javaHome.toString());

		assertEquals(app.toRealPath(), found.orElseThrow().toRealPath());
	}

	/** A symlinked java.home must resolve against the real tree, since getParent is lexical. */
	@Test
	void findInstallRoot_should_followSymlinkedJavaHome(@TempDir Path base) throws IOException
	{
		Path image = Files.createDirectories(base.resolve("image"));
		Path app = makeInstallRoot(image.resolve("app"));
		Path realRuntime = Files.createDirectories(image.resolve("runtime"));
		Path link = base.resolve("link-to-runtime");
		assumeTrue(createSymlink(link, realRuntime), "filesystem does not support symlinks");

		Optional<Path> found = ConfigurationSettings.findInstallRoot(link.toString());

		assertEquals(app.toRealPath(), found.orElseThrow().toRealPath());
	}

	/** The climb must stop rather than run to the filesystem root. */
	@Test
	void findInstallRoot_should_beEmpty_when_markersAreTooFarAbove(@TempDir Path root) throws IOException
	{
		makeInstallRoot(root);
		Path javaHome = Files.createDirectories(root.resolve("a/b/c/d/e/f/g/h"));

		Optional<Path> found = ConfigurationSettings.findInstallRoot(javaHome.toString());

		assertTrue(found.isEmpty(), "The search must be bounded, but reached: " + found);
	}

	private static boolean createSymlink(Path link, Path target)
	{
		try
		{
			Files.createSymbolicLink(link, target);
			return true;
		}
		catch (IOException | UnsupportedOperationException _)
		{
			return false;
		}
	}

	@Test
	void isWritableDir_should_beTrue_when_dirIsWritable(@TempDir Path dir)
	{
		// A portable/dev install root: an ordinary writable directory.
		assertTrue(ConfigurationSettings.isWritableDir(dir));
	}

	@Test
	void isWritableDir_should_beFalse_when_dirIsReadOnly(@TempDir Path parent) throws IOException
	{
		// An installed app's root (read-only DMG / sealed bundle).
		Path readOnly = Files.createDirectory(parent.resolve("install"));
		File asFile = readOnly.toFile();
		assumeTrue(asFile.setWritable(false) && !Files.isWritable(readOnly),
				"filesystem/user ignores the read-only bit (e.g. running as root)");

		assertFalse(ConfigurationSettings.isWritableDir(readOnly));
	}

	@Test
	void isWritableDir_should_beFalse_when_dirDoesNotExist(@TempDir Path parent)
	{
		// The install root must be an existing directory, not just a path.
		assertFalse(ConfigurationSettings.isWritableDir(parent.resolve("missing")));
	}

	@Test
	void nearestExistingDir_should_returnItself_when_dirExists(@TempDir Path dir)
	{
		assertEquals(Optional.of(dir), ConfigurationSettings.nearestExistingDir(dir.toString()));
	}

	@Test
	void nearestExistingDir_should_walkUp_when_leafMissing(@TempDir Path parent)
	{
		// e.g. <install>/settings where "settings" has not been created yet.
		Path missingLeaf = parent.resolve("settings");
		assertEquals(Optional.of(parent), ConfigurationSettings.nearestExistingDir(missingLeaf.toString()));
	}

	@Test
	void nearestExistingDir_should_walkUpSeveralLevels_when_deepPathMissing(@TempDir Path parent)
	{
		Path deepMissing = parent.resolve("a/b/c");
		assertEquals(Optional.of(parent), ConfigurationSettings.nearestExistingDir(deepMissing.toString()));
	}

	@Test
	void nearestExistingDir_should_beEmpty_when_pathIsNull()
	{
		assertTrue(ConfigurationSettings.nearestExistingDir(null).isEmpty());
	}
}
