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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import pcgen.util.ExitFunction;
import pcgen.util.GracefulExit;

/**
 * Tests for {@link Main} startup and shutdown behaviour: that
 * {@link Main#ensureSavePathExists(String)} creates the save directory even when
 * its parents are missing, and that {@link Main#shutdown(boolean)} always exits
 * even when individual cleanup steps fail.
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

	/**
	 * A failing cleanup step used to propagate out of shutdown into the AWT
	 * event dispatch thread, where it was logged and ignored, leaving a
	 * windowless JVM alive forever. Shutdown must still exit exactly once.
	 */
	@ParameterizedTest(name = "success={0} exits with status {1}")
	@ValueSource(booleans = {true, false})
	void shutdownAlwaysExitsEvenWhenCleanupStepsFail(boolean success)
	{
		int expectedStatus = success ? 0 : 1;
		AtomicInteger observedStatus = new AtomicInteger(-1);
		AtomicInteger exitCalls = new AtomicInteger();
		ExitFunction original = GracefulExit.getExitFunction();
		GracefulExit.registerExitFunction(status -> {
			exitCalls.incrementAndGet();
			observedStatus.set(status);
		});

		try
		{
			Main.shutdown(success);
		}
		finally
		{
			GracefulExit.registerExitFunction(original);
		}

		assertEquals(1, exitCalls.get(), "Shutdown must exit exactly once despite failing cleanup steps");
		assertEquals(expectedStatus, observedStatus.get(), "Exit status must reflect the success flag");
	}
}
