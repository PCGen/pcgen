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
package pcgen.cdom.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AllowUtilitiesTest
{

	@Test
	void escapesAmpersandToEntity()
	{
		assertEquals("Tom &amp; Jerry", AllowUtilities.htmlEscape("Tom & Jerry"));
	}

	@Test
	void escapesLessThanToEntity()
	{
		assertEquals("a &lt; b", AllowUtilities.htmlEscape("a < b"));
	}

	@Test
	void doesNotEscapeGreaterThan()
	{
		assertEquals("a > b", AllowUtilities.htmlEscape("a > b"));
	}

	@Test
	void doesNotEscapeDoubleQuote()
	{
		assertEquals("say \"hi\"", AllowUtilities.htmlEscape("say \"hi\""));
	}

	@Test
	void doesNotEscapeApostrophe()
	{
		assertEquals("Terendelev's scales", AllowUtilities.htmlEscape("Terendelev's scales"));
	}

	@Test
	void escapesAllAmpersandsIncludingExistingEntities()
	{
		assertEquals("&amp;amp; &amp;nl;", AllowUtilities.htmlEscape("&amp; &nl;"));
	}

	@Test
	void emptyStringIsReturnedUnchanged()
	{
		assertEquals("", AllowUtilities.htmlEscape(""));
	}

	@Test
	void plainTextIsReturnedUnchanged()
	{
		String plain = "Bear's endurance grants +4 Constitution.";
		assertEquals(plain, AllowUtilities.htmlEscape(plain));
	}

	@Test
	void mixedMetacharactersOnlyAmpersandAndLessThanAreEscaped()
	{
		assertEquals(
				"&amp;&lt;>\"'",
				AllowUtilities.htmlEscape("&<>\"'"));
	}

	@Test
	void unicodeIsPreserved()
	{
		String unicode = "café — naïve — 日本語";
		assertEquals(unicode, AllowUtilities.htmlEscape(unicode));
	}
}
