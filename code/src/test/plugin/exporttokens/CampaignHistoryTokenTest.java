/*
 * CampaignHistoryTokenTest.java
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
 *
 * Created on 04/11/2013
 *
 * $Id$
 */
package plugin.exporttokens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.ChronicleEntry;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.FileAccess;
import pcgen.util.TestHelper;

/**
 * CampaignHistoryTokenTest validates the functions of the 
 * CampaignHistoryToken class.
 * 
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
public class CampaignHistoryTokenTest  extends AbstractCharacterTestCase
{

	private ChronicleEntry visibleEntry;
	private ChronicleEntry hiddenEntry;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
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
		Assert.assertEquals("Field Campaign", visibleEntry.getCampaign(),
			evaluateToken("CAMPAIGNHISTORY.0.CAMPAIGN", character));
		Assert.assertEquals("Field ADVENTURE", visibleEntry.getAdventure(),
			evaluateToken("CAMPAIGNHISTORY.0.ADVENture", character));
		Assert.assertEquals("Field PARTY", visibleEntry.getParty(),
			evaluateToken("CAMPAIGNHISTORY.0.PARTY", character));
		Assert.assertEquals("Field DATE", visibleEntry.getDate(),
			evaluateToken("CAMPAIGNHISTORY.0.DATE", character));
		Assert.assertEquals("Field XP", visibleEntry.getXpField(),
			Integer.parseInt(evaluateToken("CAMPAIGNHISTORY.0.XP", character)));
		Assert.assertEquals("Field GM", visibleEntry.getGmField(),
			evaluateToken("CAMPAIGNHISTORY.0.GM", character));
		Assert.assertEquals("Field Text", visibleEntry.getChronicle(),
			evaluateToken("CAMPAIGNHISTORY.0.TEXT", character));
		Assert.assertEquals("Default field", visibleEntry.getChronicle(),
			evaluateToken("CAMPAIGNHISTORY.0", character));

		Assert.assertEquals("Invalid field", "",
			evaluateToken("CAMPAIGNHISTORY.0.LALALA", character));
	}


	@Test
	public void testVisibility() throws IOException
	{
		FileAccess.setCurrentOutputFilter("xml");
		PlayerCharacter character = getCharacter();
		Assert.assertEquals("Default visibility", visibleEntry.getAdventure(),
			evaluateToken("CAMPAIGNHISTORY.0.ADVENTURE", character));
		Assert.assertEquals("Default visibility", "",
			evaluateToken("CAMPAIGNHISTORY.1.ADVENTURE", character));

		Assert.assertEquals("Hidden visibility", hiddenEntry.getAdventure(),
			evaluateToken("CAMPAIGNHISTORY.HIDDEN.0.ADVENTURE", character));
		Assert.assertEquals("Hidden visibility", "",
			evaluateToken("CAMPAIGNHISTORY.HIDDEN.1.ADVENTURE", character));

		Assert.assertEquals("All visibility", visibleEntry.getAdventure(),
			evaluateToken("CAMPAIGNHISTORY.ALL.0.ADVENTURE", character));
		Assert.assertEquals("All visibility", hiddenEntry.getAdventure(),
			evaluateToken("CAMPAIGNHISTORY.ALL.1.ADVENTURE", character));

		Assert.assertEquals("Visible visibility", visibleEntry.getAdventure(),
			evaluateToken("CAMPAIGNHISTORY.VISIBLE.0.ADVENTURE", character));
		Assert.assertEquals("Visible visibility", "",
			evaluateToken("CAMPAIGNHISTORY.VISIBLE.1.ADVENTURE", character));

		Assert.assertEquals("Invalid visibility", "",
			evaluateToken("CAMPAIGNHISTORY.LALALA.0.ADVENTURE", character));
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
