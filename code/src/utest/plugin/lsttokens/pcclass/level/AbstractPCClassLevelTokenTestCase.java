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
package plugin.lsttokens.pcclass.level;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Campaign;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

public abstract class AbstractPCClassLevelTokenTestCase extends TestCase
{
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected PCClass primaryProf;
	protected PCClass secondaryProf;
	protected PCClassLevel primaryProf1;
	protected PCClassLevel secondaryProf1;
	protected PCClassLevel primaryProf2;
	protected PCClassLevel secondaryProf2;
	protected PCClassLevel primaryProf3;
	protected PCClassLevel secondaryProf3;
	protected CDOMTokenLoader<PCClassLevel> loader = new CDOMTokenLoader<>();

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
		primaryContext = new RuntimeLoadContext(new RuntimeReferenceContext(), new ConsolidatedListCommitStrategy());
		secondaryContext = new RuntimeLoadContext(new RuntimeReferenceContext(), new ConsolidatedListCommitStrategy());
		primaryProf = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class,
				"TestObj");
		secondaryProf = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class,
				"TestObj");
		primaryProf1 = primaryProf.getOriginalClassLevel(1);
		primaryProf2 = primaryProf.getOriginalClassLevel(2);
		primaryProf3 = primaryProf.getOriginalClassLevel(3);
		secondaryProf1 = secondaryProf.getOriginalClassLevel(1);
		secondaryProf2 = secondaryProf.getOriginalClassLevel(2);
		secondaryProf3 = secondaryProf.getOriginalClassLevel(3);
	}

	public static Class<? extends PCClassLevel> getCDOMClass()
	{
		return PCClassLevel.class;
	}

	public static void addToken(LstToken tok)
	{
		TokenLibrary.addToTokenMap(tok);
	}

	public void runRoundRobin(String... str) throws PersistenceLayerException
	{
		// Default is not to write out anything
		assertNull(getToken().unparse(primaryContext, primaryProf1));
		assertNull(getToken().unparse(primaryContext, primaryProf2));
		assertNull(getToken().unparse(primaryContext, primaryProf3));

		// Set value
		for (String s : str)
		{
			assertTrue(parse(s, 2));
		}
		// Doesn't pollute other levels
		assertNull(getToken().unparse(primaryContext, primaryProf1));
		assertNull(getToken().unparse(primaryContext, primaryProf3));
		// Get back the appropriate token:
		String[] unparsed = getToken().unparse(primaryContext, primaryProf2);

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
		loader.parseLine(secondaryContext, secondaryProf2, unparsedBuilt
				.toString(), testCampaign.getURI());

		// Ensure the objects are the same
		assertEquals(primaryProf, secondaryProf);

		// And that it comes back out the same again
		// Doesn't pollute other levels
		assertNull(getToken().unparse(secondaryContext, secondaryProf1));
		assertNull(getToken().unparse(secondaryContext, secondaryProf3));
		validateUnparsed(secondaryContext, secondaryProf2, unparsed);
		assertCleanConstruction();
		assertTrue(secondaryContext.getReferenceContext().validate(null));
		assertTrue(secondaryContext.getReferenceContext().resolveReferences(null));
		assertEquals(0, primaryContext.getWriteMessageCount());
		assertEquals(0, secondaryContext.getWriteMessageCount());
	}

	private void validateUnparsed(LoadContext sc, PCClassLevel sp,
			String... unparsed)
	{
		String[] sUnparsed = getToken().unparse(sc,
				sp);
		assertEquals(unparsed.length, sUnparsed.length);

		for (int i = 0; i < unparsed.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", unparsed[i],
					sUnparsed[i]);
		}
	}

	public abstract CDOMPrimaryToken<PCClassLevel> getToken();

	public static void isCDOMEqual(CDOMObject cdo1, CDOMObject cdo2)
	{
		assertTrue(cdo1.isCDOMEqual(cdo2));
	}

	public void assertNoSideEffects()
	{
		isCDOMEqual(primaryProf, secondaryProf);
		assertFalse(primaryContext.getListContext().hasMasterLists());
	}

	public boolean parse(String str, int level)
	{
		boolean b = getToken().parseToken(primaryContext,
				primaryProf.getOriginalClassLevel(level), str).passed();
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

	public boolean parseSecondary(String str, int level)
	{
		boolean b = getToken().parseToken(secondaryContext,
				secondaryProf.getOriginalClassLevel(level), str).passed();
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

	@Test
	public void testOverwrite() throws PersistenceLayerException
	{
		parse(getLegalValue(), 1);
		validateUnparsed(primaryContext, primaryProf.getOriginalClassLevel(1),
				getLegalValue());
		parse(getAlternateLegalValue(), 1);
		validateUnparsed(primaryContext, primaryProf.getOriginalClassLevel(1),
				getConsolidationRule().getAnswer(getLegalValue(),
						getAlternateLegalValue()));
	}

	protected abstract String getLegalValue();

	protected abstract String getAlternateLegalValue();

	protected abstract ConsolidationRule getConsolidationRule();

	protected static void expectSingle(String[] unparsed, String expected)
	{
		assertNotNull(unparsed);
		assertEquals(1, unparsed.length);
		assertEquals("Expected item to be equal", expected, unparsed[0]);
	}

	protected void assertCleanConstruction()
	{
		assertTrue(primaryContext.getReferenceContext().validate(null));
		assertTrue(primaryContext.getReferenceContext().resolveReferences(null));
	}
}
