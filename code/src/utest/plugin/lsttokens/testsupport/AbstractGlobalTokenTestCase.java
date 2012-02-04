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
import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
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
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
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
		Locale.setDefault(Locale.US);
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
		primaryContext = new RuntimeLoadContext(new RuntimeReferenceContext(), new ConsolidatedListCommitStrategy());
		secondaryContext = new RuntimeLoadContext(new RuntimeReferenceContext(), new ConsolidatedListCommitStrategy());
		primaryProf = primaryContext.ref.constructCDOMObject(getCDOMClass(),
				"TestObj");
		secondaryProf = secondaryContext.ref.constructCDOMObject(
				getCDOMClass(), "TestObj");
		primaryContext.ref.importObject(AbilityCategory.FEAT);
		secondaryContext.ref.importObject(AbilityCategory.FEAT);
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
			TokenLibrary.addBonusClass(clazz, name);
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
		validateUnparsed(secondaryContext, secondaryProf, unparsed);
		assertCleanConstruction();
		assertTrue(secondaryContext.ref.validate(null));
		assertTrue(secondaryContext.ref.resolveReferences(null));
		assertEquals(0, primaryContext.getWriteMessageCount());
		assertEquals(0, secondaryContext.getWriteMessageCount());
	}

	private String[] validateUnparsed(LoadContext sc, CDOMObject sp,
			String... unparsed)
	{
		String[] sUnparsed = getToken().unparse(sc, sp);
		if (unparsed == null)
		{
			assertNull(sUnparsed);
		}
		else
		{
			assertEquals(unparsed.length, sUnparsed.length);
			for (int i = 0; i < unparsed.length; i++)
			{
				assertEquals("Expected " + i + " item to be equal", unparsed[i],
						sUnparsed[i]);
			}
		}

		return sUnparsed;
	}

	public boolean parse(String str) throws PersistenceLayerException
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

	public boolean parseSecondary(String str) throws PersistenceLayerException
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

	public abstract <T extends ConcretePrereqObject> CDOMPrimaryToken<T> getToken();

	public abstract <T extends CDOMObject> CDOMLoader<T> getLoader();

	@Test
	public void testOverwrite() throws PersistenceLayerException
	{
		parse(getLegalValue());
		validateUnparsed(primaryContext, primaryProf, getLegalValue());
		parse(getAlternateLegalValue());
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer(getLegalValue(), getAlternateLegalValue()));
	}

	protected abstract String getLegalValue();

	protected abstract String getAlternateLegalValue();

	protected abstract ConsolidationRule getConsolidationRule();

	protected void expectSingle(String[] unparsed, String expected)
	{
		assertNotNull(unparsed);
		assertEquals(1, unparsed.length);
		assertEquals("Expected item to be equal", expected, unparsed[0]);
	}

	protected void assertBadUnparse()
	{
		assertNull(getToken().unparse(primaryContext, primaryProf));
		assertTrue(primaryContext.getWriteMessageCount() > 0);
	}

	protected void assertConstructionError()
	{
		boolean validate = primaryContext.ref.validate(null);
		boolean resolve = primaryContext.ref.resolveReferences(null);
		assertFalse(validate && resolve);
	}

	protected void assertCleanConstruction()
	{
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(primaryContext.ref.resolveReferences(null));
	}
}
