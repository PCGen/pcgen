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
import org.junit.Test;

import pcgen.core.Campaign;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public abstract class AbstractSubTokenTestCase<T> extends TestCase
{
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected T primaryProf;
	protected T secondaryProf;
	protected int expectedPrimaryMessageCount = 0;

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
		// Yea, this causes warnings...
		TokenRegistration.register(getToken());
		primaryContext = new RuntimeLoadContext(new RuntimeReferenceContext(),
				new ConsolidatedListCommitStrategy());
		secondaryContext = new RuntimeLoadContext(new RuntimeReferenceContext(),
				new ConsolidatedListCommitStrategy());
		URI testURI = testCampaign.getURI();
		primaryContext.getObjectContext().setSourceURI(testURI);
		primaryContext.getObjectContext().setExtractURI(testURI);
		secondaryContext.getObjectContext().setSourceURI(testURI);
		secondaryContext.getObjectContext().setExtractURI(testURI);
		primaryProf = getSubInstance();
		secondaryProf = getSubInstance();
		expectedPrimaryMessageCount = 0;
	}

	protected T getSubInstance()
	{
		try
		{
			return getCDOMClass().newInstance();
		}
		catch (InstantiationException e)
		{
			throw new InternalError(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			throw new InternalError(e.getMessage());
		}
	}

	public abstract Class<? extends T> getCDOMClass();

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
		// And that it comes back out the same again
		String[] sUnparsed = getToken()
				.unparse(secondaryContext, secondaryProf);
		assertEquals(unparsed.length, sUnparsed.length);

		for (int i = 0; i < unparsed.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", unparsed[i],
					sUnparsed[i]);
		}
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(secondaryContext.ref.validate(null));
		assertEquals(expectedPrimaryMessageCount, primaryContext
				.getWriteMessageCount());
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
			primaryContext.rollback();
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
			secondaryContext.rollback();
			Logging.rewindParseMessages();
			Logging.replayParsedMessages();
		}
		return b;
	}

	public abstract CDOMSubLineLoader<T> getLoader();

	public abstract CDOMSecondaryToken<T> getToken();

	@Test
	public void testNoStackTrace()
	{
		try
		{
			getToken().parse(primaryContext, primaryProf, null);
		}
		catch (Exception e)
		{
			fail("Token should not throw an exception with null input");
		}
	}
	
	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
	}


}
