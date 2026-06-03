/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
import static pcgen.util.TestHelper.evaluateToken;

import java.io.IOException;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.ChronicleEntry;
import pcgen.core.PlayerCharacter;
import pcgen.io.FileAccess;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CampaignHistoryTokenTest validates the functions of the
 * CampaignHistoryToken class.
 *
 *
 */
public class CampaignHistoryTokenTest  extends AbstractCharacterTestCase
{

	private ChronicleEntry visibleEntry;
	private ChronicleEntry hiddenEntry;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();

		visibleEntry = TestHelper.buildChronicleEntry(true, "Kingmaker", "17Dec2012", "Vic",
			"Ruling council", "Finns folly", 150,
			"A ruin is conquered in the forest and a new town is founded.");

		hiddenEntry = TestHelper.buildChronicleEntry(false, "Campaign", "Date", "GM", "Party",
			"Adventure", 1390, "Chronicle");

		character.addChronicleEntry(visibleEntry);
		character.addChronicleEntry(hiddenEntry);
	}

	@Test
	public void testFieldChoice() throws IOException
	{
		FileAccess.setCurrentOutputFilter("xml");
		PlayerCharacter character = getCharacter();
		assertEquals(visibleEntry.getCampaign(), evaluateToken("CAMPAIGNHISTORY.0.CAMPAIGN", character), "Field Campaign");
		assertEquals(visibleEntry.getAdventure(), evaluateToken("CAMPAIGNHISTORY.0.ADVENture", character), "Field ADVENTURE");
		assertEquals(visibleEntry.getParty(), evaluateToken("CAMPAIGNHISTORY.0.PARTY", character), "Field PARTY");
		assertEquals(visibleEntry.getDate(), evaluateToken("CAMPAIGNHISTORY.0.DATE", character), "Field DATE");
		assertEquals(visibleEntry.getXpField(), Integer.parseInt(evaluateToken("CAMPAIGNHISTORY.0.XP", character)), "Field XP");
		assertEquals(visibleEntry.getGmField(), evaluateToken("CAMPAIGNHISTORY.0.GM", character), "Field GM");
		assertEquals(visibleEntry.getChronicle(), evaluateToken("CAMPAIGNHISTORY.0.TEXT", character), "Field Text");
		assertEquals(visibleEntry.getChronicle(), evaluateToken("CAMPAIGNHISTORY.0", character), "Default field");

		assertEquals("", evaluateToken("CAMPAIGNHISTORY.0.LALALA", character), "Invalid field");
	}


	@Test
	public void testVisibility() throws IOException
	{
		FileAccess.setCurrentOutputFilter("xml");
		PlayerCharacter character = getCharacter();
		assertEquals(visibleEntry.getAdventure(), evaluateToken("CAMPAIGNHISTORY.0.ADVENTURE", character), "Default visibility");
		assertEquals("", evaluateToken("CAMPAIGNHISTORY.1.ADVENTURE", character), "Default visibility");

		assertEquals(hiddenEntry.getAdventure(), evaluateToken("CAMPAIGNHISTORY.HIDDEN.0.ADVENTURE", character), "Hidden visibility");
		assertEquals("", evaluateToken("CAMPAIGNHISTORY.HIDDEN.1.ADVENTURE", character), "Hidden visibility");

		assertEquals(visibleEntry.getAdventure(), evaluateToken("CAMPAIGNHISTORY.ALL.0.ADVENTURE", character), "All visibility");
		assertEquals(hiddenEntry.getAdventure(), evaluateToken("CAMPAIGNHISTORY.ALL.1.ADVENTURE", character), "All visibility");

		assertEquals(visibleEntry.getAdventure(), evaluateToken("CAMPAIGNHISTORY.VISIBLE.0.ADVENTURE", character), "Visible visibility");
		assertEquals("", evaluateToken("CAMPAIGNHISTORY.VISIBLE.1.ADVENTURE", character), "Visible visibility");

		assertEquals("", evaluateToken("CAMPAIGNHISTORY.LALALA.0.ADVENTURE", character), "Invalid visibility");
	}
}
