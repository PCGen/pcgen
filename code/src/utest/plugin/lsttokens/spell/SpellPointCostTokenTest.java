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
package plugin.lsttokens.spell;

import org.junit.Test;

import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class SpellPointCostTokenTest extends AbstractTokenTestCase<Spell>
{

	static SpellPointCostToken token = new SpellPointCostToken();
	static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<Spell>();

	@Override
	public Class<Spell> getCDOMClass()
	{
		return Spell.class;
	}

	@Override
	public CDOMLoader<Spell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Spell> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNaN() throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "X4").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingSplat()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "4*").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNaNTyped() throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "X4|Any=25").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCapacityNoTypeQuantity()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Any=").passed());
		assertNoSideEffects();
	}

//	@Test
//	public void testInvalidCapacityZeroQuantity()
//			throws PersistenceLayerException
//	{
//		assertFalse(token.parse(primaryContext, primaryProf, "5|Cookies=0"));
//		assertNoSideEffects();
//	}
//
//	@Test
//	public void testInvalidCapacityNegativeQuantity()
//			throws PersistenceLayerException
//	{
//		assertFalse(token.parse(primaryContext, primaryProf, "5|Cookies=-10"));
//		assertNoSideEffects();
//	}

	@Test
	public void testInvalidCapacityTypeQuantityNaN()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Any=4X").passed());
		assertNoSideEffects();
	}

	public void testInvalidCapacityUselessPipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCapacityTypeLeadingDoublePipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5||Any=4").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCapacityTypeTrailingPipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Any=4|").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCapacityTypeDoubleEquals()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Any=4=3").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCapacityTypeMiddlePipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Cookies=4||Crackers=3").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidWeightlessNoTypeQuantity()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Any=").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidWeightlessZeroQuantity()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Cookies=0").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidWeightlessNegativeQuantity()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Cookies=-10").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidWeightlessTypeQuantityNaN()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Any=4X").passed());
		assertNoSideEffects();
	}

	public void testInvalidWeightlessUselessPipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidWeightlessTypeLeadingDoublePipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5||Any=4").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidWeightlessTypeTrailingPipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Any=4|").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidWeightlessTypeDoubleEquals()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Any=4=3").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidWeightlessTypeMiddlePipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Cookies=4||Crackers=3").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReducedNoTypeQuantity()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Any=").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReducedTypeQuantityNaN()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Any=4X").passed());
		assertNoSideEffects();
	}

	public void testInvalidReducedUselessPipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReducedTypeLeadingDoublePipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30||Any=4").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReducedTypeTrailingPipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Any=4|").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReducedZeroQuantity()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Cookies=0").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReducedNegativeQuantity()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf,
				"40%30|Cookies=-10").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReducedTypeDoubleEquals()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Any=4=3").passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReducedTypeMiddlePipe()
			throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, primaryProf,
				"40%30|Cookies=4||Crackers=3").passed());
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		this.runRoundRobin("500");
	}

	@Test
	public void testRoundRobinTypeQuantityLimited()
			throws PersistenceLayerException
	{
		this.runRoundRobin("500|Potions=100");
	}

	@Test
	public void testRoundRobinTwoQuantityLimited()
			throws PersistenceLayerException
	{
		this.runRoundRobin("Potions=100|Scrolls=500");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Potions=100|Scrolls=500";
	}

	@Override
	protected String getLegalValue()
	{
		return "500|Potions=10";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return new ConsolidationRule.AppendingConsolidation('|');
	}
}
