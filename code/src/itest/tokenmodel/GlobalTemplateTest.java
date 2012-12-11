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

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.TemplateLst;
import tokenmodel.testsupport.AbstractGrantedListTokenTest;

public class GlobalTemplateTest extends AbstractGrantedListTokenTest<PCTemplate>
{

	TemplateLst token = new TemplateLst();

	@Test
	public void testChoose() throws PersistenceLayerException
	{
		Race source = create(Race.class, "Source");
		PCTemplate granted = create(PCTemplate.class, "Granted");
		ParseResult result = token.parseToken(context, source, "CHOOSE:Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, templateFacet.getCount(id));
		raceFacet.set(id, source);
		assertTrue(templateFacet.contains(id, granted));
		assertEquals(1, templateFacet.getCount(id));
		raceFacet.remove(id);
		assertEquals(0, templateFacet.getCount(id));
	}

	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
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
	protected AbstractSourcedListFacet<PCTemplate> getTargetFacet()
	{
		return templateFacet;
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

}
