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

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.cdom.content.fact.FactDefinition;
import pcgen.core.Campaign;
import pcgen.core.Domain;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import plugin.lsttokens.testsupport.TokenRegistration;

public class FactDefTokenTest extends TestCase
{

	static FactDefToken token = new FactDefToken();
	FactDefinition fd;

	protected LoadContext context;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static final void classSetUp() throws URISyntaxException
	{
		testCampaign =
				new CampaignSourceEntry(new Campaign(), new URI(
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
		TokenRegistration.clearTokens();
		TokenRegistration.register(token);
		resetContext();
	}

	protected void resetContext()
	{
		URI testURI = testCampaign.getURI();
		context =
				new RuntimeLoadContext(new RuntimeReferenceContext(),
					new ConsolidatedListCommitStrategy());
		context.setSourceURI(testURI);
		context.setExtractURI(testURI);
		fd = new FactDefinition();
	}

	@Test
	public void testInvalidInputNullString() throws PersistenceLayerException
	{
		Assert.assertFalse(token.parseToken(context, fd, null).passed());
	}

	@Test
	public void testInvalidInputEmptyString() throws PersistenceLayerException
	{
		Assert.assertFalse(token.parseToken(context, fd, "").passed());
	}

	@Test
	public void testInvalidInputNoPipe() throws PersistenceLayerException
	{
		Assert.assertFalse(token.parseToken(context, fd, "SKILL").passed());
	}

	@Test
	public void testInvalidInputTrailingPipe() throws PersistenceLayerException
	{
		Assert.assertFalse(token.parseToken(context, fd, "SKILL|").passed());
	}

	@Test
	public void testInvalidInputLeadingPipe() throws PersistenceLayerException
	{
		Assert.assertFalse(token.parseToken(context, fd, "|Possibility").passed());
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		Assert.assertFalse(token.parseToken(context, fd, "SKILL||Possibility").passed());
	}

	@Test
	public void testInvalidInputDoublePipe2() throws PersistenceLayerException
	{
		Assert.assertFalse(token.parseToken(context, fd, "SKILL|Possibility|Exception").passed());
	}

	@Test
	public void testValidStringString() throws PersistenceLayerException
	{
		Assert.assertNull(fd.getFactName());
		Assert.assertNull(fd.getUsableLocation());
		Assert.assertTrue(token.parseToken(context, fd, "SKILL|Possibility").passed());
		Assert.assertNotNull(fd.getFactName());
		Assert.assertNotNull(fd.getUsableLocation());
		Assert.assertEquals("Possibility", fd.getFactName());
		Assert.assertEquals(Skill.class, fd.getUsableLocation());
		String[] unparsed = token.unparse(context, fd);
		Assert.assertNotNull(unparsed);
		Assert.assertEquals(1, unparsed.length);
		Assert.assertEquals("SKILL|Possibility", unparsed[0]);
	}

	@Test
	public void testValidStringNo() throws PersistenceLayerException
	{
		Assert.assertNull(fd.getFactName());
		Assert.assertNull(fd.getUsableLocation());
		Assert.assertTrue(token.parseToken(context, fd, "DOMAIN|Caster").passed());
		Assert.assertNotNull(fd.getFactName());
		Assert.assertNotNull(fd.getUsableLocation());
		Assert.assertEquals("Caster", fd.getFactName());
		Assert.assertEquals(Domain.class, fd.getUsableLocation());
		String[] unparsed = token.unparse(context, fd);
		Assert.assertNotNull(unparsed);
		Assert.assertEquals(1, unparsed.length);
		Assert.assertEquals("DOMAIN|Caster", unparsed[0]);
	}

}
