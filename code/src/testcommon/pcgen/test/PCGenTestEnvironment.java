/*
 * Copyright 2026 the PCGen Project. Author: Vest <Vest@users.noreply.github.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package pcgen.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import pcgen.system.Main;

/**
 * JUnit 5 extension that loads PCGen plugins exactly once per JVM, before
 * any test class that opts in runs. Replaces the lazy
 * {@code TestHelper.loadPlugins()} pattern: opted-in test classes no
 * longer need to remember to call it from their setup hooks.
 *
 * <p>This is opt-in (NOT auto-discovered). Plugin loading populates the
 * global {@code PluginFunctionLibrary}, which any newly constructed
 * {@code VariableContext} reads from at construction time — so tests
 * that build a {@code VariableContext} expecting an empty function
 * library (e.g. {@code SetSolverManagerTest}) must NOT have plugins
 * loaded. Per-class opt-in keeps that boundary explicit.
 *
 * <p>Usage: annotate the test class with
 * {@code @ExtendWith(PCGenTestEnvironment.class)}. Most test classes
 * inherit this from {@code AbstractCharacterTestCase} or
 * {@code AbstractJunit5CharacterTestCase} and don't need to add it
 * themselves.
 *
 * <p>This extension does NOT call {@link Main#loadProperties} or run
 * GameModeFileLoader / CampaignFileLoader — those need a settings file
 * that most tests don't provide. Tests that load real game data
 * (DataTest, DataLoadTest) drive that sequence themselves; everything
 * else just needs plugins registered with the various factories.
 */
public final class PCGenTestEnvironment implements BeforeAllCallback
{
	private static final ExtensionContext.Namespace NAMESPACE =
			ExtensionContext.Namespace.create(PCGenTestEnvironment.class);
	private static final String LOADED_KEY = "plugins-loaded";

	@Override
	public void beforeAll(ExtensionContext context)
	{
		// getRoot() ensures one entry across the whole JVM, not one per test class.
		context.getRoot().getStore(NAMESPACE).getOrComputeIfAbsent(LOADED_KEY, k -> {
			Main.createLoadPluginTask().run();
			return Boolean.TRUE;
		});
	}
}
