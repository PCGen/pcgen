/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.race;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.base.format.OrderedPairManager;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ModifierFactory;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.modifier.orderedpair.SetModifierFactory;

public class FaceTokenTest extends AbstractCDOMTokenTestCase<Race>
{

	static FaceToken token = new FaceToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();
	static ModifierFactory<OrderedPair> m = new SetModifierFactory();
	private FormatManager<OrderedPair> opManager = new OrderedPairManager();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(m);
		primaryContext.getVariableContext().assertLegalVariableID(
			primaryContext.getActiveScope(), opManager, "Face");
		secondaryContext.getVariableContext().assertLegalVariableID(
			secondaryContext.getActiveScope(), opManager, "Face");
	}

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public CDOMLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Race> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputs()
			throws PersistenceLayerException
	{
		// no invalid item should set or reset the value
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("TestWP"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("String"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("TYPE=TestType"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("TYPE.TestType"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("ALL"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("ANY"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("FIVE"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("1/2"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("1+3"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("-1"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("-2, 4"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("6, -3"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("x, 4"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("6, y"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("+, 4"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("6, +"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse(" , 4"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("6,  "));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("1,"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse(",1"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("1,2,3"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse("1,2,"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
		assertFalse(parse(",2,3"));
		assertNull(primaryProf.getListFor(ListKey.MODIFY));
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin("1");
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		runRoundRobin("0");
	}

	@Test
	public void testRoundRobinZeroX() throws PersistenceLayerException
	{
		runRoundRobin("0,5");
	}

	// Note: Can't do this because if Height is zero, then it is not written
	// out.
	// - Tom Parker 2/23/2007
	// @Test
	// public void testRoundRobinZeroY() throws PersistenceLayerException
	// {
	// testRoundRobin("5,0");
	// }

	@Test
	public void testRoundRobinDecimal() throws PersistenceLayerException
	{
		runRoundRobin("5.1,6.3");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "5.1";
	}

	@Override
	protected String getLegalValue()
	{
		return "4,5";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
