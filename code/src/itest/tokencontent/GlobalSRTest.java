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
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.CharacterSpellResistanceFacet;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.SrLst;

import tokencontent.testsupport.AbstractContentTokenTest;

public class GlobalSRTest extends AbstractContentTokenTest
{

	private static SrLst token = new SrLst();
	private CharacterSpellResistanceFacet srFacet;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		srFacet = FacetLibrary.getFacet(CharacterSpellResistanceFacet.class);
	}

	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result = token.parseToken(context, source, "25+INT");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
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
		//Cannot use contains because facet is using instance identity
		return srFacet.getSet(id).iterator().next().equals(FormulaFactory.getFormulaFor("25+INT"));
	}

	@Override
	protected int targetFacetCount()
	{
		return srFacet.getCount(id);
	}

	@Override
	protected int baseCount()
	{
		return 0;
	}
}
