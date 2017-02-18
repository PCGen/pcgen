/**
 * IconTokenTest.java
 * Copyright James Dempsey, 2011
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
 *
 *
 */
package plugin.lsttokens.gamemode;

import java.net.URI;

import junit.framework.TestCase;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;

/**
 * The Class <code>IconTokenTest</code> tests that the IconToken class is 
 * operating correctly.
 *
 * <br/>
 * 
 */
public class IconTokenTest extends TestCase
{
	private URI uri;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		uri = new URI("http://www.pcgen.org");
		super.setUp();
	}

	public void testValidSyntax() throws Exception
	{
		GameMode gameMode = SettingsHandler.getGame();
		assertNull("starting condition, eyegear icon should be null", gameMode
			.getEquipTypeIcon("Eyegear"));

		IconToken token = new IconToken();
		assertTrue("Parse should succeed", token.parse(gameMode,
			"Eyegear|preview/summary/images/icon_eye.png", uri));		

		assertEquals("Incorrect icon path",
			"preview/summary/images/icon_eye.png", gameMode
				.getEquipTypeIcon("Eyegear"));
		assertEquals("Case misimatch fails",
			"preview/summary/images/icon_eye.png", gameMode
				.getEquipTypeIcon("EyegeaR"));
		assertNull("Unknown type should be null", gameMode
			.getEquipTypeIcon("Unknown"));
	}

	public void testInValidSyntax() throws Exception
	{
		GameMode gameMode = SettingsHandler.getGame();
		IconToken token = new IconToken();
		assertFalse("Parse should fail", token.parse(gameMode,
			"Eyegear:preview/summary/images/icon_eye.png", uri));		
		assertFalse("Parse should fail", token.parse(gameMode,
			"Eyegear|preview/summary/images|icon_eye.png", uri));		
	}
}
