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

import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;
import pcgen.core.Campaign;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import util.FormatSupport;
import util.TestURI;

@SuppressWarnings("nls")
public abstract class AbstractTokenTestCase<T extends Loadable>
{
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected T primaryProf;
	protected T secondaryProf;
	protected int expectedPrimaryMessageCount = 0;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeAll
	public static void classSetUp()
	{
		testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
		classSetUpFired = true;
	}

	@BeforeEach
	@BeforeEach
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		if (!classSetUpFired)
		{
			classSetUp();
		}
		TokenRegistration.clearTokens();
		TokenRegistration.register(getToken());
		resetContext();
		expectedPrimaryMessageCount = 0;
	}

	@AfterEach
	@AfterEach
	public void tearDown() throws Exception
	{
		primaryContext = null;
		secondaryContext = null;
		primaryProf = null;
		secondaryProf = null;
	}

	protected void resetContext()
	{
		primaryContext = getPrimaryContext();
		secondaryContext =
				new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
					new ConsolidatedListCommitStrategy());
		additionalSetup(primaryContext);
		additionalSetup(secondaryContext);
		primaryProf = get(primaryContext, "TestObj");
		primaryProf.setSourceURI(testCampaign.getURI());
		secondaryProf = get(secondaryContext, "TestObj");
		secondaryProf.setSourceURI(testCampaign.getURI());
	}

	protected LoadContext getPrimaryContext()
	{
		return new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
				new ConsolidatedListCommitStrategy());
	}

	protected T get(LoadContext context, String name)
	{
		return context.getReferenceContext().constructCDOMObject(getCDOMClass(), name);
	}

	public abstract Class<? extends T> getCDOMClass();

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
		Assertions.assertNull(getToken().unparse(primaryContext, primaryProf));

		parse(str);
		primaryProf.setSourceURI(testCampaign.getURI());
		String[] unparsed = validateUnparsed(primaryContext, primaryProf, str);
		parseSecondary(unparsed);
		// Ensure the objects are the same
		isCDOMEqual(primaryProf, secondaryProf);
		validateUnparse(unparsed);
	}
	
	/**
	 * Run a test for conversion of a deprecated format to a supported format.
	 * @param deprecated The old token format.
	 * @param target The expected new token format.
	 * @throws PersistenceLayerException If the parsing 
	 */
	public void runMigrationRoundRobin(String deprecated, String target) 
			throws PersistenceLayerException
	{
		// Default is not to write out anything
		Assertions.assertNull(getToken().unparse(primaryContext, primaryProf));

		parse(deprecated);
		primaryProf.setSourceURI(testCampaign.getURI());
		String[] unparsed = validateUnparsed(primaryContext, primaryProf, target);
		parseSecondary(unparsed);
		// Ensure the objects are the same
		isCDOMEqual(primaryProf, secondaryProf);
		validateUnparse(unparsed);
		
	}

	protected void validateUnparse(String... unparsed)
	{
		// And that it comes back out the same again
		String[] sUnparsed = getToken()
				.unparse(secondaryContext, secondaryProf);
		Assertions.assertEquals(unparsed.length, sUnparsed.length);

		for (int i = 0; i < unparsed.length; i++)
		{
			Assertions.assertEquals(
					unparsed[i],
					sUnparsed[i],
					"Expected " + i + " item to be equal"
			);
		}
		assertCleanConstruction();
		Assertions.assertTrue(secondaryContext.getReferenceContext().validate(null));
		Assertions.assertTrue(secondaryContext.getReferenceContext().resolveReferences(null));
		Assertions.assertEquals(expectedPrimaryMessageCount, primaryContext
				.getWriteMessageCount());
		Assertions.assertEquals(0, secondaryContext.getWriteMessageCount());
	}

	protected void parseSecondary(String[] unparsed)
			throws PersistenceLayerException
	{
		// Do round Robin
		secondaryProf.setSourceURI(testCampaign.getURI());
		StringBuilder unparsedBuilt = new StringBuilder();
		for (String s : unparsed)
		{
			unparsedBuilt.append(getToken().getTokenName()).append(':').append(
					s).append('\t');
		}
		getLoader().parseLine(secondaryContext, secondaryProf,
				unparsedBuilt.toString(), testCampaign.getURI());
	}

	private void parse(String... str)
	{
		// Set value
		for (String s : str)
		{
			Assertions.assertTrue(parse(s), "Failed to parse " + s);
		}
	}

	protected String[] validateUnparsed(LoadContext pc, T pp, String... str)
	{
		String[] unparsed = getToken().unparse(pc, pp);

		Assertions.assertNotNull(str);
		Assertions.assertNotNull(unparsed);
		Assertions.assertEquals(str.length, unparsed.length);

		for (int i = 0; i < str.length; i++)
		{
			Assertions.assertEquals(
					str[i], unparsed[i],
					"Expected " + i + "th uparsed item to be equal"
			);
		}
		return unparsed;
	}

	public abstract void isCDOMEqual(T cdo1, T cdo2);

	public void assertNoSideEffects()
	{
		isCDOMEqual(primaryProf, secondaryProf);
		Assertions.assertFalse(primaryContext.getListContext().hasMasterLists());
	}

	public boolean parse(String str)
	{
		ParseResult pr;
		try
		{
			pr = getToken().parseToken(primaryContext, primaryProf, str);
		}
		catch (IllegalArgumentException e)
		{
			Logging.addParseMessage(
				Logging.LST_ERROR,
				"Token generated an IllegalArgumentException: "
					+ e.getLocalizedMessage());
			pr = new ParseResult.Fail("Token processing failed");
		}

		if (pr.passed())
		{
			primaryContext.commit();
		}
		else
		{
			pr.addMessagesToLog(TestURI.getURI());
			primaryContext.rollback();
			Logging.rewindParseMessages();
			Logging.replayParsedMessages();
		}
		return pr.passed();
	}

	public boolean parseSecondary(String str)
	{
		ParseResult pr = getToken()
				.parseToken(secondaryContext, secondaryProf, str);
		if (pr.passed())
		{
			secondaryContext.commit();
		}
		else
		{
			pr.addMessagesToLog(TestURI.getURI());
			secondaryContext.rollback();
			Logging.rewindParseMessages();
			Logging.replayParsedMessages();
		}
		return pr.passed();
	}

	public abstract CDOMLoader<T> getLoader();

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
			e.printStackTrace();
			Assertions.fail("Token should not throw an exception with null input");
		}
	}

	@Test
	public void testOverwrite()
	{
		Assertions.assertTrue(parse(getLegalValue()));
		validateUnparsed(primaryContext, primaryProf, getLegalValue());
		Assertions.assertTrue(parse(getAlternateLegalValue()));
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer(getLegalValue(), getAlternateLegalValue()));
	}

	protected abstract String getLegalValue();

	protected abstract String getAlternateLegalValue();

	protected abstract ConsolidationRule getConsolidationRule();

	protected static void expectSingle(String[] unparsed, String expected)
	{
		Assertions.assertNotNull(unparsed);
		Assertions.assertEquals(1, unparsed.length);
		Assertions.assertEquals(expected, unparsed[0]);
	}

	protected void assertBadUnparse()
	{
		Assertions.assertNull(getToken().unparse(primaryContext, primaryProf));
		Assertions.assertTrue(primaryContext.getWriteMessageCount() > 0);
	}

	protected void assertConstructionError()
	{
		Assertions.assertFalse(
				primaryContext.getReferenceContext().validate(null)
						&& primaryContext.getReferenceContext().resolveReferences(null),
				"Expected one of validate or resolve references to be false."
		);
	}

	protected void assertCleanConstruction()
	{
		Assertions.assertTrue(primaryContext.getReferenceContext().validate(null));
		Assertions.assertTrue(primaryContext.getReferenceContext().resolveReferences(null));
	}

	protected <C extends Categorized<C>> C constructCategorized(LoadContext context,
		Category<C> cat, String name)
	{
		C obj = cat.newInstance();
		obj.setName(name);
		context.getReferenceContext().importObject(obj);
		return obj;
	}

	protected void additionalSetup(LoadContext context)
	{
		URI testURI = testCampaign.getURI();
		context.setSourceURI(testURI);
		context.setExtractURI(testURI);
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
		FormatSupport.addBasicDefaults(context);
		SourceFileLoader.defineBuiltinVariables(context);
	}
}
