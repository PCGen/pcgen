/*
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.NotePCAttribute;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.FileAccess;

/**
 * {@code BioTokenTest} is ...
 */
public class BioTokenTest extends AbstractCharacterTestCase
{
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();

		character.setPCAttribute(NotePCAttribute.BIO, "Test bio entry\n2nd line\nThird line\nlast one");
	}

	/**
	 * Test the bio export
	 * @throws Exception  Signals that an I/O exception has occurred.
	 */
	public void testBioExport() throws Exception
	{
		FileAccess.setCurrentOutputFilter("xml");
		PlayerCharacter character = getCharacter();
		assertEquals(
			"Default Bio",
			"<para>Test bio entry</para><para>2nd line</para><para>Third line</para><para>last one</para>",
			evaluateToken("BIO", character));

		assertEquals(
			"New Style Bio start and end",
			"<para>[b]Test bio entry[/b]</para><para>[b]2nd line[/b]</para><para>[b]Third line[/b]"
			+ "</para><para>[b]last one[/b]</para>",
			evaluateToken("BIO.[b].[/b]", character));

		assertEquals(
			"New Style Bio start only",
			"<para>**Test bio entry</para><para>**2nd line</para><para>**Third line</para><para>**last one</para>",
			evaluateToken("BIO.**", character));

		assertEquals(
			"New Style Bio start only",
			"<para>Test bio entry,</para><para>2nd line,</para><para>Third line,</para><para>last one,</para>",
			evaluateToken("BIO..,", character));

		FileAccess.setCurrentOutputFilter("foo.htm");
		character.setPCAttribute(NotePCAttribute.BIO, "Test bio <br/>entry\n2nd line\nThird line\nlast one");

		String expected =
				"<p>[b]Test bio &lt;br/&gt;entry[/b]</p>\n<p>[b]2nd line[/b]</p>"
						+ "\n<p>[b]Third line[/b]</p>\n<p>[b]last one[/b]</p>";
		String actual = evaluateToken("BIO.[b].[/b]", character);
		assertEquals(expected, actual);

		actual = evaluateToken("BIO..,", character);
		expected =
				"<p>Test bio &lt;br/&gt;entry,</p>\n<p>2nd line,</p>\n<p>Third line,</p>\n<p>last one,</p>";
		assertEquals("New Style Bio start only", expected, actual);
	}

	/**
	 * Evaluate token.
	 *
	 * @param token the token
	 * @param pc the pc
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String evaluateToken(String token, PlayerCharacter pc)
		throws IOException
	{
		StringWriter retWriter = new StringWriter();
		BufferedWriter bufWriter = new BufferedWriter(retWriter);
		ExportHandler export = new ExportHandler(new File(""));
		export.replaceTokenSkipMath(pc, token, bufWriter);
		retWriter.flush();

		bufWriter.flush();

		return retWriter.toString();
	}

}
