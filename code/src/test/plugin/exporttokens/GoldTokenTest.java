/*
 * Copyright James Dempsey, 2014
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
package plugin.exporttokens;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.exporttoken.Token;
import pcgen.persistence.SourceFileLoader;
import plugin.exporttokens.deprecated.GoldToken;

import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@code GoldToken} class.
 */

public class GoldTokenTest extends AbstractCharacterTestCase
{

	private final Token goldToken = new GoldToken();
	/**
	 * Test formatted output of {@link GoldToken#getToken(String, PlayerCharacter, pcgen.io.ExportHandler)}.
	 */
	@Test
	void testGetToken()
	{
		SourceFileLoader.createLangBonusObject(Globals.getContext());
		PlayerCharacter pc = super.getCharacter();
		assertEquals("0", goldToken.getToken("", pc, null), "No money");
		
		pc.setGold(new BigDecimal("500"));
		assertEquals("500", goldToken.getToken("", pc, null), "Non decimal money");
		pc.setGold(new BigDecimal("2500"));
		assertEquals("2,500", goldToken.getToken("", pc, null), "Non decimal money");
		pc.setGold(new BigDecimal("1012500"));
		assertEquals("1,012,500", goldToken.getToken("", pc, null), "Non decimal money");
		
		pc.setGold(new BigDecimal("500.76"));
		assertEquals("500.76", goldToken.getToken("", pc, null), "Decimal money");
		pc.setGold(new BigDecimal("500.701234"));
		assertEquals("500.7", goldToken.getToken("", pc, null), "Decimal money");
		pc.setGold(new BigDecimal("0.701234"));
		assertEquals("0.7", goldToken.getToken("", pc, null), "Decimal money");
		pc.setGold(new BigDecimal("0.709934"));
		assertEquals("0.71", goldToken.getToken("", pc, null), "Decimal money");
	}

}
