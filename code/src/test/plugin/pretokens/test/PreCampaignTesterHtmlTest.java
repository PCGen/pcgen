/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
package plugin.pretokens.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests the text {@link PreCampaignTester} shows for a source prerequisite.
 *
 * <p>The key of a {@code PRECAMPAIGN} is the token its author typed into a
 * {@code .pcc} file, and its prefix is how the engine tells four quite
 * different conditions apart. Echoing the key verbatim, as this used to, puts
 * that syntax in front of the user: the dialog explaining why a source could
 * not be added said "INCLUDESBOOKTYPE=Core Rules and none of (INCLUDES=DSP -
 * Psionics Unleashed, ...)".
 */
class PreCampaignTesterHtmlTest
{
	private static final PreCampaignTester TESTER = new PreCampaignTester();

	private static Prerequisite requirementFor(String key)
	{
		var prereq = new Prerequisite();
		prereq.setKind("CAMPAIGN");
		prereq.setKey(key);
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("1");
		return prereq;
	}

	/**
	 * The loose form, and by far the commonest: satisfied by a collection that
	 * loads the book as well as by the book itself. It gets the plain name.
	 */
	@Test
	void includesPrefix_isNotShownToTheUser()
	{
		var html = TESTER.toHtmlString(requirementFor("INCLUDES=Core Rulebook"));

		assertEquals("Core Rulebook", html);
	}

	/**
	 * The strict form, which a collection loading the book does not satisfy.
	 * It still shows only the name: bare keys are the commonest kind and come
	 * in long lists, so spelling the distinction out on each would bury the
	 * names it qualifies. Where the difference matters the source dialog's own
	 * diagnosis says so.
	 */
	@Test
	void bareCampaignName_isShownAsJustTheName()
	{
		var html = TESTER.toHtmlString(requirementFor("Core Rulebook"));

		assertEquals("Core Rulebook", html);
	}

	@ParameterizedTest
	@ValueSource(strings = {"BOOKTYPE=Core Rules", "INCLUDESBOOKTYPE=Core Rules"})
	void bookType_isDescribedAsAKindOfBook(String key)
	{
		var html = TESTER.toHtmlString(requirementFor(key));

		assertEquals(LanguageBundle.getFormattedString("PreCampaign.bookType", "Core Rules"), html);
	}

	@ParameterizedTest
	@CsvSource({"GTEQ, 2", "LT, 1"})
	void countedRequirement_includesOperatorAndCount(PrerequisiteOperator operator, String count)
	{
		var prereq = requirementFor("INCLUDES=Core Rulebook");
		prereq.setOperator(operator);
		prereq.setOperand(count);

		var html = TESTER.toHtmlString(prereq);

		assertEquals(LanguageBundle.getFormattedString("PreCampaign.toHtml",
				operator.toDisplayString(), count, "Core Rulebook"), html);
	}

	/** A key whose name merely begins with a prefix word must not be mangled. */
	@Test
	void campaignNamedLikeAPrefix_isLeftIntact()
	{
		var html = TESTER.toHtmlString(requirementFor("INCLUDESOMETHING Press Guide"));

		assertEquals("INCLUDESOMETHING Press Guide", html);
	}
}
