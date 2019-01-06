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
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

public class VisibleTokenTest
{

	static VisibleToken token = new VisibleToken();
	ContentDefinition cd;

	protected LoadContext context;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeAll
	public static void classSetUp()
	{
		testCampaign =
				new CampaignSourceEntry(new Campaign(), TestURI.getURI());
		classSetUpFired = true;
	}

	@BeforeEach
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
		Assertions.assertNull(cd.getVisibility());
		Assertions.assertTrue(token.parseToken(context, cd, "YES").passed());
		Assertions.assertNotNull(cd.getVisibility());
		Assertions.assertEquals(Visibility.DEFAULT, cd.getVisibility());
		String[] unparsed = token.unparse(context, cd);
		Assertions.assertNotNull(unparsed);
		Assertions.assertEquals(1, unparsed.length);
		Assertions.assertEquals("YES", unparsed[0]);
	}

	@Test
	public void testValidStringNo()
	{
		Assertions.assertNull(cd.getVisibility());
		Assertions.assertTrue(token.parseToken(context, cd, "NO").passed());
		Assertions.assertNotNull(cd.getVisibility());
		Assertions.assertEquals(Visibility.HIDDEN, cd.getVisibility());
		String[] unparsed = token.unparse(context, cd);
		Assertions.assertNotNull(unparsed);
		Assertions.assertEquals(1, unparsed.length);
		Assertions.assertEquals("NO", unparsed[0]);
	}

	@Test
	public void testValidStringDisplay()
	{
		Assertions.assertNull(cd.getVisibility());
		Assertions.assertTrue(token.parseToken(context, cd, "DISPLAY").passed());
		Assertions.assertNotNull(cd.getVisibility());
		Assertions.assertEquals(Visibility.DISPLAY_ONLY, cd.getVisibility());
		String[] unparsed = token.unparse(context, cd);
		Assertions.assertNotNull(unparsed);
		Assertions.assertEquals(1, unparsed.length);
		Assertions.assertEquals("DISPLAY", unparsed[0]);
	}

	@Test
	public void testValidStringExport()
	{
		Assertions.assertNull(cd.getVisibility());
		Assertions.assertTrue(token.parseToken(context, cd, "EXPORT").passed());
		Assertions.assertNotNull(cd.getVisibility());
		Assertions.assertEquals(Visibility.OUTPUT_ONLY, cd.getVisibility());
		String[] unparsed = token.unparse(context, cd);
		Assertions.assertNotNull(unparsed);
		Assertions.assertEquals(1, unparsed.length);
		Assertions.assertEquals("EXPORT", unparsed[0]);
	}

}
