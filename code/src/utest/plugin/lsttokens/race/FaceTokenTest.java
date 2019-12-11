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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;

import pcgen.base.format.OrderedPairManager;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.cdom.util.CControl;
import pcgen.core.Campaign;
import pcgen.core.Race;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.FormatSupport;
import util.TestURI;

public class FaceTokenTest
{

	private static FaceToken token = new FaceToken();
	private static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();
	private static ModifierFactory<OrderedPair> modifierFactory =
			new SetModifierFactory();
	private static CampaignSourceEntry testCampaign;

	private FormatManager<OrderedPair> opManager = new OrderedPairManager();
	private LoadContext primaryContext;
	private LoadContext secondaryContext;
	private Race primaryProf;
	private Race secondaryProf;
	private int expectedPrimaryMessageCount = 0;

	@BeforeAll
	public static void classSetUp()
	{
		testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
	}

	@BeforeEach
	void setUp() throws PersistenceLayerException, URISyntaxException
	{
		TokenRegistration.clearTokens();
		TokenRegistration.register(getToken());
		resetContext();
		expectedPrimaryMessageCount = 0;
		TokenRegistration.register(modifierFactory);
	}

	@AfterEach
	public void tearDown()
	{
		TokenRegistration.clearTokens();
		primaryContext = null;
		secondaryContext = null;
		primaryProf = null;
		secondaryProf = null;
		opManager = null;
	}
	
	@AfterAll
	public static void classTearDown()
	{
		token = null;
		loader = null;
		modifierFactory = null;
		testCampaign = null;
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
		FormatSupport.addBasicDefaults(context);
		context.getVariableContext().assertLegalVariableID(
			CControl.FACE.getDefaultValue(), context.getActiveScope(), opManager);
	}

	public static void isCDOMEqual(Race cdo1, Race cdo2)
	{
		assertTrue(cdo1.isCDOMEqual(cdo2), "Not equal " + cdo1 + " and " + cdo2);
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

	private void validateUnparse(String... unparsed)
	{
		// And that it comes back out the same again
		String[] sUnparsed = getToken()
				.unparse(secondaryContext, secondaryProf);
		assertArrayEquals(sUnparsed, unparsed);
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
			assertTrue(parse(s), "Failed to parse " + s);
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
			assertEquals(
					str[i], unparsed[i],
					"Expected " + i + "th uparsed item to be equal"
			);
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
					+ e.getLocalizedMessage(), e.getStackTrace());
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
	public void testNoStackTraceOnNull()
	{
		assertDoesNotThrow(() -> getToken().parseToken(primaryContext, primaryProf, null));
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
        System.gc(); // NOPMD
		assertNull(wr.get(), "retained");
	}

	protected static void expectSingle(String[] unparsed, String expected)
	{
		assertArrayEquals(new String[]{expected}, unparsed);
	}

	protected void assertBadUnparse()
	{
		assertNull(getToken().unparse(primaryContext, primaryProf));
		assertTrue(primaryContext.getWriteMessageCount() > 0);
	}

	protected void assertConstructionError()
	{
		assertFalse(
				primaryContext.getReferenceContext().validate(null)
						&& primaryContext.getReferenceContext().resolveReferences(null),
				"Expected one of validate or resolve references to be false."
		);
	}

	protected void assertCleanConstruction()
	{
		assertTrue(primaryContext.getReferenceContext().validate(null));
		assertTrue(primaryContext.getReferenceContext().resolveReferences(null));
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
		assertTrue(pr.passed());
		context.commit();
		assertTrue(pr.passed());
        System.gc(); // NOPMD
		assertNull(wr.get(), "retained");
	}
}
