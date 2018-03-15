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

import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.analysis.DomainApplication;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.pcclass.level.DomainToken;

import org.junit.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;
import util.TestURI;

public class PCClassLevelDomainTest extends AbstractTokenModelTest
{

	private static DomainToken token = new DomainToken();

	@Test
	public void testSimple() throws PersistenceLayerException
	{
		PCClass source = create(PCClass.class, "Source");
		PCClassLevel pcl = source.getOriginalClassLevel(2);
		Domain granted = create(Domain.class, "Granted");
		ParseResult result = token.parseToken(context, pcl, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages(TestURI.getURI());
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, domainFacet.getCount(id));
		classFacet.addClass(id, source);
		PCClass pcc = pc.getClassKeyed(source.getKeyName());
		classFacet.setLevel(id, pcc, 1);
		//TODO get rid of this using facets :)
		DomainApplication.addDomainsUpToLevel(source, 1, pc);
		assertEquals(0, domainFacet.getCount(id));
		classFacet.setLevel(id, pcc, 2);
		DomainApplication.addDomainsUpToLevel(source, 2, pc);
		assertTrue(domainFacet.contains(id, granted));
		assertEquals(1, domainFacet.getCount(id));
		DomainApplication.removeDomainsForLevel(source, 2, pc);
		DomainApplication.removeDomainsForLevel(source, 1, pc);
		pc.validateCharacterDomains();
		classFacet.removeClass(id, source);
		assertEquals(0, domainFacet.getCount(id));
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

}
