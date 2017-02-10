/*
 * GoldTokenTest.java
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
 *
 *
 */
package plugin.exporttokens;

import java.math.BigDecimal;

import org.junit.Test;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.SourceFileLoader;
import plugin.exporttokens.deprecated.GoldToken;

/**
 * Unit test for the <code>GoldToken</code> class.
 *
 * <br/>
 * 
 */

public class GoldTokenTest extends AbstractCharacterTestCase
{

	private GoldToken goldToken = new GoldToken();
	/**
	 * Test formatted output of {@link plugin.exporttokens.deprecated.GoldToken#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)}.
	 */
	@Test
	public void testGetToken()
	{
		SourceFileLoader.createLangBonusObject(Globals.getContext());
		PlayerCharacter pc = super.getCharacter();
		assertEquals("No money", "0", goldToken.getToken("", pc, null));
		
		pc.setGold(new BigDecimal("500"));
		assertEquals("Non decimal money", "500", goldToken.getToken("", pc, null));
		pc.setGold(new BigDecimal("2500"));
		assertEquals("Non decimal money", "2,500", goldToken.getToken("", pc, null));
		pc.setGold(new BigDecimal("1012500"));
		assertEquals("Non decimal money", "1,012,500", goldToken.getToken("", pc, null));
		
		pc.setGold(new BigDecimal("500.76"));
		assertEquals("Decimal money", "500.76", goldToken.getToken("", pc, null));
		pc.setGold(new BigDecimal("500.701234"));
		assertEquals("Decimal money", "500.7", goldToken.getToken("", pc, null));
		pc.setGold(new BigDecimal("0.701234"));
		assertEquals("Decimal money", "0.7", goldToken.getToken("", pc, null));
		pc.setGold(new BigDecimal("0.709934"));
		assertEquals("Decimal money", "0.71", goldToken.getToken("", pc, null));
	}

}
