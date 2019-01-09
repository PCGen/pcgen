/*
 * Copyright (c) 2007-12 Tom Parker <thpr@users.sourceforge.net>
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


import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.Campaign;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.CDOMWriteToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import util.FormatSupport;
import util.TestURI;

public abstract class AbstractGlobalTokenTestCase extends TestCase
{
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected CDOMObject primaryProf;
	protected CDOMObject secondaryProf;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static void classSetUp()
	{
		Locale.setDefault(Locale.US);
		testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
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
		TokenRegistration.register(getReadToken());
		TokenRegistration.register(getWriteToken());
		primaryContext = new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		secondaryContext = new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		primaryProf = primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"TestObj");
		secondaryProf = secondaryContext.getReferenceContext().constructCDOMObject(
				getCDOMClass(), "TestObj");
		additionalSetup(primaryContext);
		additionalSetup(secondaryContext);
	}

	public abstract <T extends CDOMObject> Class<T> getCDOMClass();

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
		Assert.assertNull(getWriteToken().unparse(primaryContext, primaryProf));

		// Set value
		for (String s : str)
		{
			Assert.assertTrue("Should be able to parse " + s, parse(s));
		}
		// Get back the appropriate token:
		String[] unparsed = getWriteToken().unparse(primaryContext, primaryProf);

		Assert.assertNotNull(str);
		Assert.assertNotNull(unparsed);
		Assert.assertEquals(str.length, unparsed.length);

		for (int i = 0; i < str.length; i++)
		{
			Assert.assertEquals("Expected " + i + "th unparsed item to be equal",
				str[i], unparsed[i]);
		}

		// Do round Robin
		String unparsedBuilt = Arrays.stream(unparsed)
		                             .map(s -> getReadToken().getTokenName() + ':' + s + '\t')
		                             .collect(Collectors.joining());
		getLoader().parseLine(secondaryContext, secondaryProf,
				unparsedBuilt, testCampaign.getURI());

		// Ensure the objects are the same
		Assert.assertEquals(primaryProf, secondaryProf);

		// And that it comes back out the same again
		validateUnparsed(secondaryContext, secondaryProf, unparsed);
		assertCleanConstruction();
		Assert.assertTrue(secondaryContext.getReferenceContext().validate(null));
		Assert.assertTrue(secondaryContext.getReferenceContext().resolveReferences(null));
		Assert.assertEquals(0, primaryContext.getWriteMessageCount());
		Assert.assertEquals(0, secondaryContext.getWriteMessageCount());
	}

	
	/**
	 * Run a test for conversion of a deprecated format to a supported format.
	 * @param deprecated The old token format.
	 * @param target The expected new token format.
	 * @throws PersistenceLayerException If the parsing 
	 */
	protected void runMigrationRoundRobin(String deprecated, String target)
			throws PersistenceLayerException
	{
		// Default is not to write out anything
		Assert.assertNull(getWriteToken().unparse(primaryContext, primaryProf));

		parse(deprecated);
		primaryProf.setSourceURI(testCampaign.getURI());
		String[] unparsed = validateUnparsed(primaryContext, primaryProf, target);

		// Do round Robin
		String unparsedBuilt = Arrays.stream(unparsed)
		                             .map(s -> getReadToken().getTokenName() + ':' + s + '\t')
		                             .collect(Collectors.joining());
		getLoader().parseLine(secondaryContext, secondaryProf,
				unparsedBuilt, testCampaign.getURI());
		// Ensure the objects are the same
		isCDOMEqual(primaryProf, secondaryProf);
		validateUnparsed(secondaryContext, secondaryProf, unparsed);
	}
	
	private String[] validateUnparsed(LoadContext sc, CDOMObject sp,
			String... unparsed)
	{
		String[] sUnparsed = getWriteToken().unparse(sc, sp);
		if (unparsed == null)
		{
			Assert.assertNull(sUnparsed);
		}
		else
		{
			for (int i = 0; (i < unparsed.length) && (i < sUnparsed.length); i++)
			{
				Assert.assertEquals("Expected " + i + "th unparsed item to be equal",
					unparsed[i], sUnparsed[i]);
			}
			Assert.assertEquals("Mismatched number of unparsed values",
				unparsed.length, sUnparsed.length);
		}

		return sUnparsed;
	}

	public boolean parse(String str)
	{
		ParseResult pr;
		try
		{
			pr = getReadToken().parseToken(primaryContext, primaryProf, str);
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
		boolean b = getReadToken().parseToken(secondaryContext, secondaryProf, str).passed();
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
		return getReadToken().getTokenName();
	}

	private static void isCDOMEqual(CDOMObject cdo1, CDOMObject cdo2)
	{
		Assert.assertTrue(cdo1.isCDOMEqual(cdo2));
	}

	public void assertNoSideEffects()
	{
		isCDOMEqual(primaryProf, secondaryProf);
		Assert.assertFalse(primaryContext.getListContext().hasMasterLists());
	}

	public abstract <T extends ConcretePrereqObject> CDOMToken<T> getReadToken();

	public abstract <T extends ConcretePrereqObject> CDOMWriteToken<T> getWriteToken();

	public abstract <T extends CDOMObject> CDOMLoader<T> getLoader();

	public void testOverwrite()
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

	protected static void expectSingle(String[] unparsed, String expected)
	{
		Assert.assertNotNull(unparsed);
		Assert.assertEquals(1, unparsed.length);
		Assert.assertEquals("Expected item to be equal", expected, unparsed[0]);
	}

	protected void assertBadUnparse()
	{
		Assert.assertNull(getWriteToken().unparse(primaryContext, primaryProf));
		Assert.assertTrue(primaryContext.getWriteMessageCount() > 0);
	}

	protected void assertConstructionError()
	{
		boolean validate = primaryContext.getReferenceContext().validate(null);
		boolean resolve = primaryContext.getReferenceContext().resolveReferences(null);
		Assert.assertFalse(validate && resolve);
	}

	protected void assertCleanConstruction()
	{
		Assert.assertTrue(primaryContext.getReferenceContext().validate(null));
		Assert.assertTrue(primaryContext.getReferenceContext().resolveReferences(null));
	}

	public void testCleanup()
	{
		String s = getLegalValue();
		Assert.assertTrue(parse(s));
	}

	public void testAvoidContext()
	{
		RuntimeLoadContext context = new RuntimeLoadContext(
			RuntimeReferenceContext.createRuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		additionalSetup(context);
		CDOMObject item = context.getReferenceContext()
				.constructCDOMObject(getCDOMClass(), "TestObj");
		ParseResult pr = getReadToken().parseToken(context, item, getLegalValue());
		if (!pr.passed())
		{
			Assert.fail();
		}
		context.commit();
		Assert.assertTrue(pr.passed());
	}

	protected void additionalSetup(LoadContext context)
	{
		FormatSupport.addBasicDefaults(context);
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
	}

}
