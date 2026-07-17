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
package pcgen.gui2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.system.PCGenSettings;
import pcgen.system.PropertyContext;

import org.junit.jupiter.api.Test;

/**
 * Verifies the fallback order of {@link PCGenFrame#resolveCharacterChooserDir}:
 * in-session path → persisted LAST_CHARACTER_PATH → configured PCG_SAVE_PATH.
 */
class PCGenFrameResolveCharacterChooserDirTest
{
	private static final String IN_SESSION = "/session/path";
	private static final String LAST_USED = "/persisted/path";
	private static final String DEFAULT_PCG = "/default/path";

	@Test
	void inSessionPathWinsWhenSet()
	{
		PropertyContext ctx = new TestContext();
		ctx.setProperty(PCGenSettings.LAST_CHARACTER_PATH, LAST_USED);
		ctx.setProperty(PCGenSettings.PCG_SAVE_PATH, DEFAULT_PCG);

		assertEquals(IN_SESSION, PCGenFrame.resolveCharacterChooserDir(IN_SESSION, ctx));
	}

	@Test
	void persistedPathWinsWhenInSessionIsNull()
	{
		PropertyContext ctx = new TestContext();
		ctx.setProperty(PCGenSettings.LAST_CHARACTER_PATH, LAST_USED);
		ctx.setProperty(PCGenSettings.PCG_SAVE_PATH, DEFAULT_PCG);

		assertEquals(LAST_USED, PCGenFrame.resolveCharacterChooserDir(null, ctx));
	}

	@Test
	void persistedPathWinsWhenInSessionIsEmpty()
	{
		PropertyContext ctx = new TestContext();
		ctx.setProperty(PCGenSettings.LAST_CHARACTER_PATH, LAST_USED);
		ctx.setProperty(PCGenSettings.PCG_SAVE_PATH, DEFAULT_PCG);

		assertEquals(LAST_USED, PCGenFrame.resolveCharacterChooserDir("", ctx));
	}

	@Test
	void defaultPathIsUsedWhenNothingElseSet()
	{
		PropertyContext ctx = new TestContext();
		ctx.setProperty(PCGenSettings.PCG_SAVE_PATH, DEFAULT_PCG);

		assertEquals(DEFAULT_PCG, PCGenFrame.resolveCharacterChooserDir(null, ctx));
	}

	/** Plain {@link PropertyContext} so the test doesn't touch the singleton. */
	private static final class TestContext extends PropertyContext
	{
		TestContext()
		{
			super("test-options.ini");
		}
	}
}
