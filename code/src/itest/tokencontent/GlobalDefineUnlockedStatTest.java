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
package tokencontent;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.UnlockedStatFacet;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.DefineStatLst;

import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalDefineUnlockedStatTest extends AbstractContentTokenTest
{

	private static DefineStatLst token = new DefineStatLst();
	private UnlockedStatFacet unlockedStatFacet;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		unlockedStatFacet = FacetLibrary.getFacet(UnlockedStatFacet.class);
	}

	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result = token.parseToken(context, source, "UNLOCK|INT");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages(TestURI.getURI());
			fail("Test Setup Failed");
		}
		finishLoad();
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

	@Override
	protected boolean containsExpected()
	{
		return unlockedStatFacet.contains(id, intel);
	}

	@Override
	protected int targetFacetCount()
	{
		return unlockedStatFacet.getCount(id);
	}

	@Override
	protected int baseCount()
	{
		return 0;
	}

}
