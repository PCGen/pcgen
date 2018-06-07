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
import pcgen.cdom.facet.analysis.MovementFacet;
import pcgen.core.Movement;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.MovecloneLst;

import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalMoveCloneTest extends AbstractContentTokenTest
{

	private static MovecloneLst token = new MovecloneLst();
	private MovementFacet moveFacet;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		moveFacet = FacetLibrary.getFacet(MovementFacet.class);
	}

	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result = token.parseToken(context, source, "Walk,Fly,*2");
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
		//Cannot use contains because facet is using instance identity
		Movement movement = moveFacet.getSet(id).iterator().next();
		return (movement.getMoveRatesFlag() == 2)
			&& (movement.getDoubleMovement() == 0.0)
			&& (movement.getMovementMult(0) == 0.0)
			&& (movement.getMovementMultOp(0).equals(""))
			&& movement.getMovementType(0).equals("Walk")
			&& (movement.getMovementMult(1) == 2.0)
			&& (movement.getMovementMultOp(1).equals("*"))
			&& movement.getMovementType(1).equals("Fly")
			&& (movement.getNumberOfMovements() == 2);
	}

	@Override
	protected int targetFacetCount()
	{
		return moveFacet.getCount(id);
	}

	@Override
	protected int baseCount()
	{
		return 0;
	}
}
