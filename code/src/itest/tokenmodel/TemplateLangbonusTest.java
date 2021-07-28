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

import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.StartingLanguageFacet;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;

import plugin.lsttokens.template.LangbonusToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;
import util.TestURI;

public class TemplateLangbonusTest extends AbstractTokenModelTest
{

	private static LangbonusToken token = new LangbonusToken();

	protected StartingLanguageFacet startingLanguageFacet;
	
	@Test
	public void testSimple()
	{
		PCTemplate source = create(PCTemplate.class, "Source");
		Language granted = create(Language.class, "Granted");
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages(TestURI.getURI());
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, startingLanguageFacet.getCount(id));
		templateInputFacet.directAdd(id, source, getAssoc());
		assertTrue(startingLanguageFacet.contains(id, granted));
		assertEquals(1, startingLanguageFacet.getCount(id));
		templateInputFacet.remove(id, source);
		assertEquals(0, startingLanguageFacet.getCount(id));
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

	@Override
	@BeforeEach
	protected void setUp() throws Exception
	{
		super.setUp();
		startingLanguageFacet = FacetLibrary.getFacet(StartingLanguageFacet.class);
	}

}
