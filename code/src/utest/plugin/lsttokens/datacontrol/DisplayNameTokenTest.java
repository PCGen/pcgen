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

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.TestURI;

public class DisplayNameTokenTest extends TestCase
{

	static DisplayNameToken token = new DisplayNameToken();
	ContentDefinition cd;

	protected LoadContext context;

	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static void classSetUp()
	{
		testCampaign =
				new CampaignSourceEntry(new Campaign(), TestURI.getURI());
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
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
		assertFalse(token.parseToken(context, cd, "").passed());
	}

	@Test
	public void testValidStringYes()
	{
		assertNull(cd.getDisplayName());
		assertTrue(token.parseToken(context, cd, "YES").passed());
		assertNotNull(cd.getDisplayName());
		assertEquals("YES", cd.getDisplayName());
		String[] unparsed = token.unparse(context, cd);
		assertNotNull(unparsed);
		assertEquals(1, unparsed.length);
		assertEquals("YES", unparsed[0]);
	}

	@Test
	public void testValidStringNo()
	{
		assertNull(cd.getDisplayName());
		String str = "Wow! Some String?!?";
		assertTrue(token.parseToken(context, cd, str).passed());
		assertNotNull(cd.getDisplayName());
		assertEquals(str, cd.getDisplayName());
		String[] unparsed = token.unparse(context, cd);
		assertNotNull(unparsed);
		assertEquals(1, unparsed.length);
		assertEquals(str, unparsed[0]);
	}

}
