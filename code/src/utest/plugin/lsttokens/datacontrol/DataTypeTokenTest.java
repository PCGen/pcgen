/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.datacontrol;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.base.format.OrderedPairManager;
import pcgen.base.format.StringManager;
import pcgen.cdom.content.ContentDefinition;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import plugin.lsttokens.testsupport.TokenRegistration;
import util.TestURI;

public class DataTypeTokenTest extends TestCase
{

	static DataFormatToken token = new DataFormatToken();
	ContentDefinition cd;

	protected LoadContext context;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static void classSetUp()
	{
		testCampaign =
				new CampaignSourceEntry(new Campaign(), TestURI.getURI());
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
		TokenRegistration.clearTokens();
		TokenRegistration.register(token);
		resetContext();
	}

	protected void resetContext()
	{
		URI testURI = testCampaign.getURI();
		context =
				new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
					new ConsolidatedListCommitStrategy());
		context.setSourceURI(testURI);
		context.setExtractURI(testURI);
		cd = new FactDefinition();
	}

	@Test
	public void testInvalidInputNullString()
	{
		assertFalse(token.parseToken(context, cd, null).passed());
	}

	@Test
	public void testInvalidInputEmptyString()
	{
		try
		{
			assertFalse(token.parseToken(context, cd, "").passed());
		}
		catch (IllegalArgumentException e)
		{
			//This is ok too
		}
	}

	@Test
	public void testInvalidInputNotAType()
	{
		try
		{
			assertFalse(token.parseToken(context, cd, "NotAType").passed());
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//This is ok too
		}
	}

	@Test
	public void testValidStringString()
	{
		assertNull(cd.getFormatManager());
		assertTrue(token.parseToken(context, cd, "STRING").passed());
		assertNotNull(cd.getFormatManager());
		assertSame(StringManager.class, cd.getFormatManager().getClass());
		String[] unparsed = token.unparse(context, cd);
		assertNotNull(unparsed);
		assertEquals(1, unparsed.length);
		assertEquals("STRING", unparsed[0]);
	}

	@Test
	public void testValidStringNo()
	{
		assertNull(cd.getFormatManager());
		assertTrue(token.parseToken(context, cd, "ORDEREDPAIR").passed());
		assertNotNull(cd.getFormatManager());
		assertSame(OrderedPairManager.class, cd.getFormatManager().getClass());
		String[] unparsed = token.unparse(context, cd);
		assertNotNull(unparsed);
		assertEquals(1, unparsed.length);
		assertEquals("ORDEREDPAIR", unparsed[0]);
	}

}
