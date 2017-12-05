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

import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.template.AddLevelToken;

import org.junit.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;

public class TemplateAddLevelTest extends AbstractTokenModelTest
{

	private static AddLevelToken token = new AddLevelToken();

	@Test
	public void testSimple() throws PersistenceLayerException
	{
		PCTemplate source = create(PCTemplate.class, "Source");
		create(PCClass.class, "Granted");
		ParseResult result = token.parseToken(context, source, "Granted|3");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, classFacet.getCount(id));
		templateInputFacet.directAdd(id, source, getAssoc());
		assertEquals(1, classFacet.getCount(id));
		assertNotNull(pc.getClassKeyed("Granted"));
		templateInputFacet.remove(id, source);
		assertEquals(0, classFacet.getCount(id));
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

}
