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

import pcgen.cdom.base.Loadable;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
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
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public abstract class AbstractKitTokenTestCase<T extends Loadable> extends TestCase
{
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected T primaryProf;
	protected T secondaryProf;
	protected int expectedPrimaryMessageCount = 0;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static void classSetUp() throws URISyntaxException
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
		primaryContext.setSourceURI(testURI);
		primaryContext.setExtractURI(testURI);
		secondaryContext.setSourceURI(testURI);
		secondaryContext.setExtractURI(testURI);
		primaryContext.getReferenceContext().importObject(AbilityCategory.FEAT);
		secondaryContext.getReferenceContext().importObject(AbilityCategory.FEAT);
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
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new InternalError(e.getMessage());
		}
	}

	public abstract Class<? extends T> getCDOMClass();

	public static void addToken(LstToken tok)
	{
		TokenLibrary.addToTokenMap(tok);
	}

	public static void addBonus(Class<? extends BonusObj> clazz)
	{
		try
		{
			TokenLibrary.addBonusClass(clazz);
		}
		catch (InstantiationException | IllegalAccessException e)
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
				unparsedBuilt.toString());
		// And that it comes back out the same again
		String[] sUnparsed = getToken()
				.unparse(secondaryContext, secondaryProf);
		assertEquals(unparsed.length, sUnparsed.length);

		for (int i = 0; i < unparsed.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", unparsed[i],
					sUnparsed[i]);
		}
		assertCleanConstruction();
		assertTrue(secondaryContext.getReferenceContext().validate(null));
		assertTrue(secondaryContext.getReferenceContext().resolveReferences(null));
		assertEquals(expectedPrimaryMessageCount, primaryContext
				.getWriteMessageCount());
		assertEquals(0, secondaryContext.getWriteMessageCount());
	}

	public boolean parse(String str)
	{
		ParseResult pr = getToken()
				.parseToken(primaryContext, primaryProf, str);
		if (pr.passed())
		{
			primaryContext.commit();
		}
		else
		{
			pr.addMessagesToLog();
			primaryContext.rollback();
			Logging.rewindParseMessages();
			Logging.replayParsedMessages();
		}
		return pr.passed();
	}

	public boolean parseSecondary(String str)
	{
		boolean b = getToken().parseToken(secondaryContext, secondaryProf, str).passed();
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

	public abstract CDOMPrimaryToken<T> getToken();

	@Test
	public void testNoStackTrace()
	{
		try
		{
			getToken().parseToken(primaryContext, primaryProf, null);
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

	protected void assertConstructionError()
	{
		assertFalse(primaryContext.getReferenceContext().validate(null)
				&& primaryContext.getReferenceContext().resolveReferences(null));
	}

	protected void assertCleanConstruction()
	{
		assertTrue(primaryContext.getReferenceContext().validate(null));
		assertTrue(primaryContext.getReferenceContext().resolveReferences(null));
	}
}
