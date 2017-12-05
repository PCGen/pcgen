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

import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.BaseMovementFacet;
import pcgen.core.Movement;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.race.MoveToken;

import org.junit.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;

public class RaceMoveTest extends AbstractTokenModelTest
{

	private static MoveToken token = new MoveToken();
	private BaseMovementFacet baseMoveFacet;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		baseMoveFacet = FacetLibrary.getFacet(BaseMovementFacet.class);
	}

	@Test
	public void testFromRace() throws PersistenceLayerException
	{
		Race source = create(Race.class, "Source");
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		raceFacet.directSet(id, source, getAssoc());
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		raceFacet.remove(id);
		assertEquals(baseCount(), targetFacetCount());
	}

	public void processToken(Race source)
	{
		ParseResult result = token.parseToken(context, source, "Fly,30");
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

	protected boolean containsExpected()
	{
		//Cannot use contains because facet is using instance identity
		Movement movement = baseMoveFacet.getSet(id).iterator().next();
		return (movement.getMoveRatesFlag() == 0)
			&& (movement.getDoubleMovement() == 30.0)
			&& (movement.getMovementMult(0) == 0.0)
			&& (movement.getMovementMultOp(0).length() == 0)
			&& movement.getMovementType(0).equals("Fly")
			&& (movement.getNumberOfMovements() == 1);
	}

	protected int targetFacetCount()
	{
		return baseMoveFacet.getCount(id);
	}

	protected int baseCount()
	{
		return 0;
	}
}
