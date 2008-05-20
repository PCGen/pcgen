/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.testsupport;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Campaign;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

public abstract class AbstractGlobalTokenTestCase extends TestCase
{
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected CDOMObject primaryProf;
	protected CDOMObject secondaryProf;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static final void classSetUp() throws URISyntaxException
	{
		testCampaign = new CampaignSourceEntry(new Campaign(), new URI(
				"file:/Test%20Case"));
		classSetUpFired = true;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		if (!classSetUpFired)
		{
			classSetUp();
		}
		TokenRegistration.register(getToken());
		primaryContext = new RuntimeLoadContext();
		secondaryContext = new RuntimeLoadContext();
		primaryProf = primaryContext.ref.constructCDOMObject(getCDOMClass(),
				"TestObj");
		secondaryProf = secondaryContext.ref.constructCDOMObject(
				getCDOMClass(), "TestObj");
	}

	public abstract <T extends CDOMObject> Class<T> getCDOMClass();

	public static void addToken(LstToken tok)
	{
		TokenLibrary.addToTokenMap(tok);
	}

	public static void addBonus(String name, Class<? extends BonusObj> clazz)
	{
		try
		{
			Bonus.addBonusClass(clazz, name);
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	public void runRoundRobin(String... str) throws PersistenceLayerException
	{
		// Default is not to write out anything
		assertNull(getToken().unparse(primaryContext, primaryProf));

		// Set value
		for (String s : str)
		{
			assertTrue(parse(s));
		}
		// Get back the appropriate token:
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);

		assertNotNull(str);
		assertNotNull(unparsed);
		assertEquals(str.length, unparsed.length);

		for (int i = 0; i < str.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", str[i],
					unparsed[i]);
		}

		// Do round Robin
		StringBuilder unparsedBuilt = new StringBuilder();
		for (String s : unparsed)
		{
			unparsedBuilt.append(getToken().getTokenName()).append(':').append(
					s).append('\t');
		}
		getLoader().parseLine(secondaryContext, secondaryProf,
				unparsedBuilt.toString(), testCampaign.getURI());

		// Ensure the objects are the same
		assertEquals(primaryProf, secondaryProf);

		// And that it comes back out the same again
		String[] sUnparsed = getToken()
				.unparse(secondaryContext, secondaryProf);
		assertEquals(unparsed.length, sUnparsed.length);

		for (int i = 0; i < unparsed.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", unparsed[i],
					sUnparsed[i]);
		}
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
		assertEquals(0, secondaryContext.getWriteMessageCount());
	}

	public boolean parse(String str) throws PersistenceLayerException
	{
		boolean b = getToken().parse(primaryContext, primaryProf, str);
		if (b)
		{
			primaryContext.commit();
		}
		else
		{
			Logging.rewindParseMessages();
			Logging.replayParsedMessages();
		}
		return b;
	}

	public boolean parseSecondary(String str) throws PersistenceLayerException
	{
		boolean b = getToken().parse(secondaryContext, secondaryProf, str);
		if (b)
		{
			secondaryContext.commit();
		}
		else
		{
			Logging.rewindParseMessages();
			Logging.replayParsedMessages();
		}
		return b;
	}

	protected String getTokenName()
	{
		return getToken().getTokenName();
	}

	public void isCDOMEqual(CDOMObject cdo1, CDOMObject cdo2)
	{
		assertTrue(cdo1.isCDOMEqual(cdo2));
	}

	public void assertNoSideEffects()
	{
		isCDOMEqual(primaryProf, secondaryProf);
		assertFalse(primaryContext.getListContext().hasMasterLists());
	}

	public abstract CDOMPrimaryToken<CDOMObject> getToken();

	public abstract <T extends CDOMObject> CDOMLoader<T> getLoader();
}
