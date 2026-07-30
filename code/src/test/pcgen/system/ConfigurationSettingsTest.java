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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link ConfigurationSettings#findInstallRoot(String)} — how PCGen
 * locates its bundled data from java.home across the jpackage bundle layouts.
 * See issue #7678.
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
}
