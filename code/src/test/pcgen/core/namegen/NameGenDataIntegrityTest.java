/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.core.namegen;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Per-ruleset smoke test against the bundled {@code plugins/Random Names}
 * dataset. For every {@code usage="final"} ruleset declared in the data,
 * we call {@link NameGenerator#generate(RuleSet)} a handful of times and
 * assert the produced name is non-empty.
 *
 * <p>This catches damage that {@link NameGenDataLoaderTest} does not:
 * a file whose XML parses and whose cross-references resolve, but whose
 * rules ultimately produce empty strings (all-zero weights, empty
 * referenced lists, malformed rule structure).
 */
class NameGenDataIntegrityTest
{
	private static final File DATA_DIR =
			new File(System.getProperty("user.dir"), "plugins/Random Names");

	private static final int GENERATIONS_PER_RULESET = 5;

	@TestFactory
	Stream<DynamicTest> everyFinalRuleSetProducesNonEmptyName() throws Exception
	{
		// Force a full eager load so the @TestFactory body has access to
		// every parsed ruleset without paying the lazy parse cost per test.
		NameGenerator generator = new NameGenerator(DATA_DIR);
		NameGenData data = generator.getData();
		List<RuleSet> finals = data.rulesets().values().stream()
				.filter(rs -> "final".equals(rs.usage()))
				.toList();
		return finals.stream().map(rs -> DynamicTest.dynamicTest(
				rs.id() + " (" + rs.title() + ")",
				() -> assertGeneratesNonEmpty(generator, rs)));
	}

	private static void assertGeneratesNonEmpty(NameGenerator generator, RuleSet rs)
	{
		assertFalse(rs.rules().isEmpty(),
				"ruleset " + rs.id() + " has no rules");
		for (int i = 0; i < GENERATIONS_PER_RULESET; i++)
		{
			GeneratedName generated = generator.generate(rs);
			assertNotNull(generated, "ruleset " + rs.id() + " produced null result");
			assertFalse(generated.name().isEmpty(),
					"ruleset " + rs.id() + " produced empty name on attempt " + i);
		}
	}
}
