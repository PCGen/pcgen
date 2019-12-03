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

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URI;
import java.net.URISyntaxException;

import pcgen.cdom.content.UserFunction;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.persistence.token.CDOMToken;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

public class FunctionTokenTest
{

	private static CDOMToken<UserFunction> token = new FunctionToken();
	private static CampaignSourceEntry testCampaign;

	private UserFunction function;
	private LoadContext context;


	@BeforeAll
	public static void classSetUp()
	{
		testCampaign =
				new CampaignSourceEntry(new Campaign(), TestURI.getURI());
	}

	@BeforeEach
	public void setUp() {
		TokenRegistration.clearTokens();
		TokenRegistration.register(token);
		resetContext();
	}

	@AfterEach
	public void tearDown()
	{
		TokenRegistration.clearTokens();
		context = null;
		function = null;
	}

	@AfterAll
	public static void classTearDown()
	{
		token = null;
		testCampaign = null;
	}

	protected void resetContext()
	{
		URI testURI = testCampaign.getURI();
		context =
				new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
					new ConsolidatedListCommitStrategy());
		context.setSourceURI(testURI);
		context.setExtractURI(testURI);
		function = new UserFunction();
	}

	@Test
	public void testInvalidInputNullString()
	{
		assertFalse(token.parseToken(context, function, null).passed());
	}

	@Test
	public void testInvalidInputEmptyString()
	{
		try
		{
			assertFalse(token.parseToken(context, function, "").passed());
		}
		catch (IllegalArgumentException e)
		{
			//This is ok too
		}
	}
	
	/*
	 * TODO This is not a round robin token :(
	 */
//
//	@Test
//	public void testValidStringString() throws PersistenceLayerException
//	{
//		assertTrue(token.parseToken(context, function, "STRING").passed());
//		String[] unparsed = token.unparse(context, function);
//		assertNotNull(unparsed);
//		assertEquals(1, unparsed.length);
//		assertEquals("STRING", unparsed[0]);
//	}
//
//	@Test
//	public void testValidStringNo() throws PersistenceLayerException
//	{
//		assertTrue(token.parseToken(context, function, "ORDEREDPAIR").passed());
//		String[] unparsed = token.unparse(context, function);
//		assertNotNull(unparsed);
//		assertEquals(1, unparsed.length);
//		assertEquals("ORDEREDPAIR", unparsed[0]);
//	}
}
