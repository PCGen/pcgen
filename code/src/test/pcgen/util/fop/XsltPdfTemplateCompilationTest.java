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
 * Verifies that the stylesheets fixed on this branch no longer trigger
 * XTSE0710 (unknown attribute set) under Saxon-HE. These files are
 * fragments included by master stylesheets, so XTSE0650 (unknown template)
 * is expected and ignored — only undeclared-attribute-set errors are checked.
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
		List<String> errors = new ArrayList<>();
		XsltCompiler compiler = PROCESSOR.newXsltCompiler();
		compiler.setErrorReporter(error -> {
			if (!error.isWarning() && isAttributeSetError(error))
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
			// Fragment-level compilation failures (XTSE0650, XTSE0165) are
			// expected; attribute-set errors are captured via the error reporter above.
		}

		if (!errors.isEmpty())
		{
			fail(String.join("\n", errors));
		}
	}

	private static boolean isAttributeSetError(XmlProcessingError error)
	{
		if (error.getErrorCode() == null)
		{
			return false;
		}
		String code = error.getErrorCode().getLocalName();
		// XTSE0710 = unknown attribute set; XTSE0720 = circular attribute set
		return code.equals("XTSE0710") || code.equals("XTSE0720");
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
