/*
 * BioTokenTest.java
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
 *
 * Created on Dec 8, 2004
 *
 * $Id$
 *
 */
package plugin.exporttokens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.FileAccess;
import plugin.exporttokens.BioToken;

/**
 * <code>BioTokenTest</code> is ...
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class BioTokenTest extends AbstractCharacterTestCase
{

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(BioTokenTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();

		character.setBio("Test bio entry\n2nd line\nThird line\nlast one");
	}

	/**
	 * Class under test for String getToken(String, PlayerCharacter)
	 * @throws Exception
	 */
	public void testGetTokenStringPlayerCharacter() throws Exception
	{
		assertEquals("Default Bio",
			"Test bio entry<br>2nd line<br>Third line<br>last one", new BioToken()
				.getToken("BIO", getCharacter(), null));

		assertEquals("Old Style Bio",
			"Test bio entry|2nd line|Third line|last one", new BioToken()
				.getToken("BIO,|", getCharacter(), null));

		assertEquals("Old Style Bio with comma",
			"Test bio entry,2nd line,Third line,last one", new BioToken()
				.getToken("BIO,,", getCharacter(), null));

		assertEquals(
			"New Style Bio start and end",
			"<b>Test bio entry</b>| <b>2nd line</b>| <b>Third line</b>| <b>last one</b>| ",
			new BioToken().getToken("BIO.<b>.</b>| ", getCharacter(), null));

		assertEquals("New Style Bio start only",
			"**Test bio entry<br>**2nd line<br>**Third line<br>**last one<br>", new BioToken()
				.getToken("BIO.**", getCharacter(), null));

		assertEquals("New Style Bio start only",
			"Test bio entry,2nd line,Third line,last one,", new BioToken()
				.getToken("BIO..,", getCharacter(), null));
	}
	
	/**
	 * Test the bio export
	 * @throws Exception
	 */
	public void testBioExport() throws Exception
	{
		FileAccess.setCurrentOutputFilter("foo.htm");
		PlayerCharacter character = getCharacter();
		character.setBio("Test bio <br/>entry\n2nd line\nThird line\nlast one");
		
		assertEquals("Bio and exportHandler should be the same",
			evaluateToken("BIO.<b>.</b>", character), new BioToken()
				.getToken("BIO.<b>.</b>", character, null));

		assertEquals("New Style Bio start only",
			"Test bio &lt;br/&gt;entry,2nd line,Third line,last one,", new BioToken()
				.getToken("BIO..,", getCharacter(), null));
	}


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
