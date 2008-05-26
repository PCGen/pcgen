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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Campaign;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
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
	protected CDOMTokenLoader<PCClassLevel> loader = new CDOMTokenLoader<PCClassLevel>(
			PCClassLevel.class);

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
		primaryContext = new RuntimeLoadContext();
		secondaryContext = new RuntimeLoadContext();
		primaryProf = primaryContext.ref.constructCDOMObject(PCClass.class,
				"TestObj");
		secondaryProf = secondaryContext.ref.constructCDOMObject(PCClass.class,
				"TestObj");
		primaryProf1 = primaryProf.getClassLevel(1);
		primaryProf2 = primaryProf.getClassLevel(2);
		primaryProf3 = primaryProf.getClassLevel(3);
		secondaryProf1 = secondaryProf.getClassLevel(1);
		secondaryProf2 = secondaryProf.getClassLevel(2);
		secondaryProf3 = secondaryProf.getClassLevel(3);
	}

	public Class<? extends PCClassLevel> getCDOMClass()
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
		String[] sUnparsed = getToken().unparse(secondaryContext,
				secondaryProf2);
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

	public abstract CDOMPrimaryToken<PCClassLevel> getToken();

	public void isCDOMEqual(CDOMObject cdo1, CDOMObject cdo2)
	{
		assertTrue(cdo1.isCDOMEqual(cdo2));
	}

	public void assertNoSideEffects()
	{
		isCDOMEqual(primaryProf, secondaryProf);
		assertFalse(primaryContext.getListContext().hasMasterLists());
	}

	public boolean parse(String str, int level)
			throws PersistenceLayerException
	{
		boolean b = getToken().parse(primaryContext,
				primaryProf.getClassLevel(level), str);
		if (b)
		{
			primaryContext.commit();
		}
		return b;
	}

	public boolean parseSecondary(String str, int level)
			throws PersistenceLayerException
	{
		boolean b = getToken().parse(secondaryContext,
				secondaryProf.getClassLevel(level), str);
		if (b)
		{
			secondaryContext.commit();
		}
		return b;
	}
}
