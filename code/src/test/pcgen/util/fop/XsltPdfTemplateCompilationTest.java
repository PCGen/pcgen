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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.util.fop;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XmlProcessingError;
import net.sf.saxon.s9api.XsltCompiler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.transform.stream.StreamSource;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Pins down XSLT fixes made against Saxon-HE (replacing Xalan).
 *
 * Two parameterized checks:
 * <ul>
 *   <li>{@link #noUndeclaredAttributeSets} — fragment-level files where only
 *       XTSE0710 / XTSE0720 (unknown / circular attribute set) are
 *       interesting; missing-template / missing-import errors are expected
 *       because the fragment is meant to be included by a master sheet.</li>
 *   <li>{@link #entryPointCompilesCleanly} — full csheet entry points whose
 *       import tree is closed; any non-warning static error fails the test.
 *       Covers the issues fixed alongside this test: starfinder XPST0003,
 *       pathfinder_2 stale block_hp_defense import, sagaborn missing
 *       inc_pagedimensions, 4e missing fantasy_master_simple.</li>
 * </ul>
 */
class XsltPdfTemplateCompilationTest
{
	private static final Processor PROCESSOR = new Processor(false);

	// TODO: coverage is limited to files fixed on this branch — many other stylesheets have
	// pre-existing Saxon incompatibilities. Extend this test as those are addressed in future PRs.
	static Stream<Path> fixedStylesheets()
	{
		return Stream.of(
			Path.of("outputsheets/d20/fantasy/pdf/fantasy_common.xsl"),
			Path.of("outputsheets/d20/5e/pdf/fantasy_common.xsl"),
			Path.of("outputsheets/d20/starfinder/pdf/fantasy_common.xsl"),
			Path.of("outputsheets/d20/pathfinder_2/pdf/fantasy_common.xsl"),
			Path.of("outputsheets/d20/4e/pdf/fantasy_common.xsl"),
			Path.of("outputsheets/d20/sagaborn/pdf/fantasy_common.xsl"),
			Path.of("outputsheets/killshot/pdf/killshot_common.xsl")
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("fixedStylesheets")
	void noUndeclaredAttributeSets(Path xslPath)
	{
		List<String> errors = compileCollecting(xslPath,
			code -> code.equals("XTSE0710") || code.equals("XTSE0720"));

		if (!errors.isEmpty())
		{
			fail(String.join("\n", errors));
		}
	}

	/**
	 * Complete sheet trees that should compile with zero static errors.
	 * Each entry point exercises one of the issues fixed alongside this test.
	 */
	static Stream<Path> cleanEntryPoints()
	{
		return Stream.of(
			// starfinder block_hp_defense.xslt — XPST0003 syntax fix (12) → 0.08 * (0.71 * $pagePrintableWidth - 69))
			Path.of("outputsheets/d20/starfinder/pdf/csheet_fantasy_std_blackandwhite.xslt"),
			// pathfinder_2 — three masters re-pointed from block_hp_defense.xslt to block_hp_defense_pf2.xslt
			Path.of("outputsheets/d20/pathfinder_2/pdf/csheet_fantasy_no_header.xslt"),
			Path.of("outputsheets/d20/pathfinder_2/pdf/csheet_fantasy_spell_list_only.xslt"),
			Path.of("outputsheets/d20/pathfinder_2/pdf/csheet_fantasy_std_companion_box_first_page.xslt"),
			// sagaborn — inc_pagedimensions.xslt newly added
			Path.of("outputsheets/d20/sagaborn/pdf/csheet_fantasy_simple_blackandwhite.xslt"),
			Path.of("outputsheets/d20/sagaborn/pdf/csheet_fantasy_no_header.xslt"),
			// 4e — fantasy_master_simple.xslt newly added
			Path.of("outputsheets/d20/4e/pdf/csheet_fantasy_simple_blackandwhite.xslt")
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("cleanEntryPoints")
	void entryPointCompilesCleanly(Path xslPath)
	{
		// Match every non-warning static error code.
		List<String> errors = compileCollecting(xslPath, code -> true);

		if (!errors.isEmpty())
		{
			fail(String.join("\n", errors));
		}
	}

	private static List<String> compileCollecting(Path xslPath, java.util.function.Predicate<String> codeFilter)
	{
		List<String> errors = new ArrayList<>();
		XsltCompiler compiler = PROCESSOR.newXsltCompiler();
		compiler.setErrorReporter(error -> {
			if (!error.isWarning() && error.getErrorCode() != null
				&& codeFilter.test(error.getErrorCode().getLocalName()))
			{
				errors.add(formatError(error));
			}
		});

		try
		{
			compiler.compile(new StreamSource(xslPath.toFile()));
		}
		catch (SaxonApiException _)
		{
			// Static-error details are captured via the error reporter above.
		}
		return errors;
	}

	private static String formatError(XmlProcessingError error)
	{
		var loc = error.getLocation();
		String location = (loc != null && loc.getSystemId() != null)
			? "%s:%d ".formatted(loc.getSystemId(), loc.getLineNumber())
			: "";
		return "%s[%s] %s".formatted(location, error.getErrorCode().getLocalName(), error.getMessage());
	}
}
