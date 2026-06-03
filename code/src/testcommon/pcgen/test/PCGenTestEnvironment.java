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
 * any test class runs. Replaces the lazy {@code TestHelper.loadPlugins()}
 * pattern: tests no longer need to remember to call it.
 *
 * <p>Auto-registered via
 * {@code META-INF/services/org.junit.jupiter.api.extension.Extension}
 * combined with {@code junit.jupiter.extensions.autodetection.enabled=true}.
 *
 * <p>This extension does NOT call {@link Main#loadProperties} or run
 * GameModeFileLoader / CampaignFileLoader — those need a settings file that
 * most tests don't provide. Tests that load real game data (DataTest,
 * DataLoadTest) still drive that sequence themselves; everything else
 * just needs plugins registered with the various factories.
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
