/**
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
 */
package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code IconTokenTest} tests that the IconToken class is
 * operating correctly.
 */
public class IconTokenTest
{
	private URI uri;
	
	@BeforeEach
	public void setUp() throws Exception
	{
		uri = new URI("http://www.pcgen.org");
	}

	@Test
	public void testValidSyntax() throws Exception
	{
		GameMode gameMode = SettingsHandler.getGame();
		Assert.assertNull("starting condition, eyegear icon should be null", gameMode
				.getEquipTypeIcon("Eyegear"));

		IconToken token = new IconToken();
		Assert.assertTrue("Parse should succeed", token.parse(gameMode,
				"Eyegear|preview/summary/images/icon_eye.png", uri
		));

		Assert.assertEquals("Incorrect icon path",
				"preview/summary/images/icon_eye.png", gameMode
						.getEquipTypeIcon("Eyegear")
		);
		Assert.assertEquals("Case misimatch fails",
				"preview/summary/images/icon_eye.png", gameMode
						.getEquipTypeIcon("EyegeaR")
		);
		Assert.assertNull("Unknown type should be null", gameMode
				.getEquipTypeIcon("Unknown"));
	}

	@Test
	public void testInValidSyntax() throws Exception
	{
		GameMode gameMode = SettingsHandler.getGame();
		IconToken token = new IconToken();
		Assert.assertFalse("Parse should fail", token.parse(gameMode,
				"Eyegear:preview/summary/images/icon_eye.png", uri
		));
		Assert.assertFalse("Parse should fail", token.parse(gameMode,
				"Eyegear|preview/summary/images|icon_eye.png", uri
		));
	}
}
