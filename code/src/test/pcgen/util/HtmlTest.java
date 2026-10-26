/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Verifies the distinct escaping rules for HTML text and attributes. */
class HtmlTest
{
	@ParameterizedTest
	@MethodSource("textCases")
	void escape_should_escapeElementText(String input, String expected)
	{
		assertEquals(expected, Html.escape(input));
	}

	static Stream<Arguments> textCases()
	{
		return Stream.of(
				Arguments.of("&<>\"'", "&amp;&lt;>\"'"),
				Arguments.of("Tom & Jerry", "Tom &amp; Jerry"),
				Arguments.of("&amp; &nl;", "&amp;amp; &amp;nl;"),
				Arguments.of("", ""),
				Arguments.of(null, ""),
				Arguments.of("café <日本語> & naïve", "café &lt;日本語> &amp; naïve"));
	}

	@ParameterizedTest
	@MethodSource("attributeCases")
	void escapeAttr_should_escapeDoubleQuotedAttributes(String input, String expected)
	{
		assertEquals(expected, Html.escapeAttr(input));
	}

	static Stream<Arguments> attributeCases()
	{
		return Stream.of(
				Arguments.of("&<>\"'", "&amp;&lt;&gt;&quot;'"),
				Arguments.of("&quot;", "&amp;quot;"),
				Arguments.of(null, ""));
	}
}
