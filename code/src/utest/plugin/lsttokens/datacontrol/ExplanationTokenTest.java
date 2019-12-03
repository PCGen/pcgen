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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import pcgen.cdom.content.ContentDefinition;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

public class ExplanationTokenTest
{

	private static ExplanationToken token = new ExplanationToken();
	private static CampaignSourceEntry testCampaign;

	private ContentDefinition cd;
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
		cd = null;
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
		cd = new FactDefinition();
	}

	@Test
	public void testInvalidInputNullString()
	{
		Assertions.assertFalse(token.parseToken(context, cd, null).passed());
	}

	@Test
	public void testInvalidInputEmptyString()
	{
		Assertions.assertFalse(token.parseToken(context, cd, "").passed());
	}

	@Test
	public void testValidStringYes()
	{
		assertNull(cd.getExplanation());
		assertTrue(token.parseToken(context, cd, "YES").passed());
		assertNotNull(cd.getExplanation());
		assertEquals("YES", cd.getExplanation());
		String[] unparsed = token.unparse(context, cd);
		assertNotNull(unparsed);
		assertEquals(1, unparsed.length);
		assertEquals("YES", unparsed[0]);
	}

	@Test
	public void testValidStringNo()
	{
		assertNull(cd.getExplanation());
		String str = "Wow! Some String?!?";
		assertTrue(token.parseToken(context, cd, str).passed());
		assertNotNull(cd.getExplanation());
		assertEquals(str, cd.getExplanation());
		String[] unparsed = token.unparse(context, cd);
		assertNotNull(unparsed);
		assertEquals(1, unparsed.length);
		assertEquals(str, unparsed[0]);
	}

}
