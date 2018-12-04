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
package plugin.lsttokens.race;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.format.OrderedPairManager;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.util.CControl;
import pcgen.core.Campaign;
import pcgen.core.Race;
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
import pcgen.rules.persistence.token.ModifierFactory;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.modifier.orderedpair.SetModifierFactory;
import util.TestURI;

public class FaceTokenTest extends TestCase
{

	static FaceToken token = new FaceToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();
	static ModifierFactory<OrderedPair> m = new SetModifierFactory();
	private FormatManager<OrderedPair> opManager = new OrderedPairManager();

	@Override
	@Before
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
		TokenRegistration.register(m);
	}

	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	public CDOMLoader<Race> getLoader()
	{
		return loader;
	}

	public FaceToken getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputs()
	{
		// no invalid item should set or reset the value
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("TestWP"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("String"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("TYPE=TestType"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("TYPE.TestType"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("ALL"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("ANY"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("FIVE"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("1/2"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("1+3"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("-1"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("-2, 4"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("6, -3"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("x, 4"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("6, y"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("+, 4"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("6, +"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse(" , 4"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("6,  "));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("1,"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse(",1"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("1,2,3"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse("1,2,"));
		assertEquals(0, primaryProf.getModifierArray().length);
		assertFalse(parse(",2,3"));
		assertEquals(0, primaryProf.getModifierArray().length);
	}
	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin("1");
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		runRoundRobin("0");
	}

	@Test
	public void testRoundRobinZeroX() throws PersistenceLayerException
	{
		runRoundRobin("0,5");
	}

	// Note: Can't do this because if Height is zero, then it is not written
	// out.
	// - Tom Parker 2/23/2007
	// @Test
	// public void testRoundRobinZeroY() throws PersistenceLayerException
	// {
	// testRoundRobin("5,0");
	// }

	@Test
	public void testRoundRobinDecimal() throws PersistenceLayerException
	{
		runRoundRobin("5.1,6.3");
	}

	protected String getAlternateLegalValue()
	{
		return "5.1";
	}

	protected String getLegalValue()
	{
		return "4,5";
	}

	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	protected void additionalSetup(LoadContext context)
	{
		URI testURI = testCampaign.getURI();
		context.setSourceURI(testURI);
		context.setExtractURI(testURI);
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
		context.getVariableContext().assertLegalVariableID(
			CControl.FACE.getDefaultValue(), context.getActiveScope(), opManager);
	}

	public void isCDOMEqual(Race cdo1, Race cdo2)
	{
		assertTrue("Not equal " + cdo1 + " and " + cdo2, cdo1.isCDOMEqual(cdo2));
	}

	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected Race primaryProf;
	protected Race secondaryProf;
	protected int expectedPrimaryMessageCount = 0;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static void classSetUp()
	{
		testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
		classSetUpFired = true;
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

	protected Race get(LoadContext context, String name)
	{
		return context.getReferenceContext().constructCDOMObject(getCDOMClass(), name);
	}

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
		assertNull(getToken().unparse(primaryContext, primaryProf));

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
			assertTrue("Failed to parse " + s, parse(s));
		}
	}

	protected String[] validateUnparsed(LoadContext pc, Race pp, String... str)
	{
		String[] unparsed = getToken().unparse(pc, pp);

		assertNotNull(str);
		assertNotNull(unparsed);
		assertEquals(str.length, unparsed.length);

		for (int i = 0; i < str.length; i++)
		{
			assertEquals("Expected " + i + "th uparsed item to be equal",
				str[i], unparsed[i]);
		}
		return unparsed;
	}

	public void assertNoSideEffects()
	{
		isCDOMEqual(primaryProf, secondaryProf);
		assertFalse(primaryContext.getListContext().hasMasterLists());
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
			fail("Token should not throw an exception with null input");
		}
	}

	@Test
	public void testOverwrite()
	{
		assertTrue(parse(getLegalValue()));
		validateUnparsed(primaryContext, primaryProf, getLegalValue());
		assertTrue(parse(getAlternateLegalValue()));
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer(getLegalValue(), getAlternateLegalValue()));
	}

	@Test
	public void testCleanup()
	{
		String s = new String(getLegalValue());
		WeakReference<String> wr = new WeakReference<>(s);
		assertTrue(parse(s));
		s = null;
		System.gc();
		if (wr.get() != null)
		{
			fail("retained");
		}
	}

	protected static void expectSingle(String[] unparsed, String expected)
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
		assertFalse(
			"Expected one of validate or resolve references to be false.",
			primaryContext.getReferenceContext().validate(null)
				&& primaryContext.getReferenceContext().resolveReferences(null));
	}

	protected void assertCleanConstruction()
	{
		assertTrue(primaryContext.getReferenceContext().validate(null));
		assertTrue(primaryContext.getReferenceContext().resolveReferences(null));
	}

	protected <C extends Categorized<C>> C constructCategorized(LoadContext context,
		Category<C> cat, String name)
	{
		C obj = cat.newInstance();
		obj.setName(name);
		context.getReferenceContext().importObject(obj);
		return obj;
	}

	@Test
	public void testAvoidContext()
	{
		RuntimeLoadContext context = new RuntimeLoadContext(
			RuntimeReferenceContext.createRuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		additionalSetup(context);
		WeakReference<LoadContext> wr = new WeakReference<>(context);
		Race item = this.get(context, "TestObj");
		ParseResult pr = getToken().parseToken(context, item, getLegalValue());
		if (!pr.passed())
		{
			fail();
		}
		context.commit();
		assertTrue(pr.passed());
		context = null;
		System.gc();
		if (wr.get() != null)
		{
			fail("retained");
		}
	}
}
