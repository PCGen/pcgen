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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.EquipIconLstToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code IconTokenTest} tests that the IconToken class is
 * operating correctly.
 */
class IconTokenTest
{
	private URI uri;
	
	@BeforeEach
	void setUp() throws Exception
	{
		uri = new URI("http://www.pcgen.org");
	}

	@Test
	public void testValidSyntax() throws Exception
	{
		GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		assertNull(gameMode
				.getEquipTypeIcon("Eyegear"), "starting condition, eyegear icon should be null");

		EquipIconLstToken token = new IconToken();
		assertTrue(token.parse(gameMode,
				"Eyegear|preview/summary/images/icon_eye.png", uri
		), "Parse should succeed");

		assertEquals(
				"preview/summary/images/icon_eye.png", gameMode
						.getEquipTypeIcon("Eyegear"),
				"Incorrect icon path"
		);
		assertEquals(
				"preview/summary/images/icon_eye.png", gameMode
						.getEquipTypeIcon("EyegeaR"),
				"Case misimatch fails"
		);
		assertNull(gameMode
				.getEquipTypeIcon("Unknown"), "Unknown type should be null");
	}

	@Test
	public void testInValidSyntax() throws Exception
	{
		GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		EquipIconLstToken token = new IconToken();
		assertFalse(token.parse(gameMode,
				"Eyegear:preview/summary/images/icon_eye.png", uri
		), "Parse should fail");
		assertFalse(token.parse(gameMode,
				"Eyegear|preview/summary/images|icon_eye.png", uri
		), "Parse should fail");
	}
}
