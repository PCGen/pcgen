/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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
package tokenmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.input.RaceInputFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.TemplateLst;
import plugin.lsttokens.choose.TemplateToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractGrantedListTokenTest;
import util.TestURI;

public class GlobalTemplateTest extends AbstractGrantedListTokenTest<PCTemplate>
{

	private static TemplateLst token = new TemplateLst();
	protected RaceInputFacet raceInputFacet = FacetLibrary
			.getFacet(RaceInputFacet.class);
	private static TemplateToken CHOOSE_TEMPLATE_TOKEN = new TemplateToken();

	@Override
	@BeforeEach
	public void setUp() throws Exception
	{
		super.setUp();
		ChooserFactory.setDelegate(new MockUIDelegate());
	}

	@Test
	public void testChoose()
	{
		Race source = create(Race.class, "Source");
		PCTemplate granted = create(PCTemplate.class, "Granted");
		ParseResult result = token.parseToken(context, source, "CHOOSE:Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages(TestURI.getURI());
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, templateConsolidationFacet.getCount(id));
		raceFacet.directSet(id, source, getAssoc());
		assertTrue(templateConsolidationFacet.contains(id, granted));
		assertEquals(1, templateConsolidationFacet.getCount(id));
		raceFacet.remove(id);
		assertEquals(0, templateConsolidationFacet.getCount(id));
	}

	@Test
	public void testList()
	{
		Race source = create(Race.class, "Source");
		PCTemplate granted = create(PCTemplate.class, "Granted");
		ParseResult result = token.parseToken(context, source, "%LIST");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages(TestURI.getURI());
			fail("Test Setup Failed");
		}
		result = CHOOSE_TEMPLATE_TOKEN.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages(TestURI.getURI());
			fail("Test Setup Failed");
		}
		source.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		finishLoad();
		assertEquals(0, templateConsolidationFacet.getCount(id));
		raceInputFacet.set(id, source);
		assertTrue(templateConsolidationFacet.contains(id, granted));
		assertEquals(1, templateConsolidationFacet.getCount(id));
		raceInputFacet.remove(id);
		assertEquals(0, templateConsolidationFacet.getCount(id));
	}

	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages(TestURI.getURI());
			fail("Test Setup Failed");
		}
		finishLoad();
	}

	@Override
	protected Class<PCTemplate> getGrantClass()
	{
		return PCTemplate.class;
	}

	@Override
	protected TemplateFacet getTargetFacet()
	{
		return templateConsolidationFacet;
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

	@Override
	protected int getCount()
	{
		return getTargetFacet().getCount(id);
	}

	@Override
	protected boolean containsExpected(PCTemplate granted)
	{
		return getTargetFacet().contains(id, granted);
	}

}
