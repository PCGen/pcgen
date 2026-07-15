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
package plugin.lsttokens.equipment;

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

class ContainsTokenTest extends AbstractCDOMTokenTestCase<Equipment>
{

	static ContainsToken token = new ContainsToken();
	static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<>();

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public CDOMLoader<Equipment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Equipment> getToken()
	{
		return token;
	}

	@Test
	void testInvalidInputEmpty()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNaN()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "X4").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputReducingFirstNaN()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "X4%60").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputReducingSecondNaN()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "50%X4").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputSplatReducing()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*50%40").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTwoPercent()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "50%40%30").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTrailingSplat()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "4*").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmbeddedSplat()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5*4").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNaNTyped()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "X4|Any=25").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputReducingFirstNaNTyped()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "X4%60|Any=25").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputReducingSecondNaNTyped()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "50%X4|Any=25").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputSplatReducingTyped()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*50%40|Any=25").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTwoPercentTyped()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "50%40%30|Any=25").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTrailingSplatTyped()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "4*|Any=25").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmbeddedSplatTyped()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5*4|Any=25").passed());
		assertNoSideEffects();
	}

	public void testInvalidNoCapacity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "|Cookies").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidCapacityNoTypeQuantity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Any=").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidCapacityZeroQuantity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Cookies=0").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidCapacityNegativeQuantity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Cookies=-10").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidCapacityTypeQuantityNaN()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Any=4X").passed());
		assertNoSideEffects();
	}

	public void testInvalidCapacityUselessPipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidCapacityTypeLeadingDoublePipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5||Any=4").passed());
		assertNoSideEffects();
	}

	@Test
	void testNegativeWeightCapacity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "-5|Any=4").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidCapacityTypeTrailingPipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Any=4|").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidCapacityTypeDoubleEquals()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Any=4=3").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidCapacityTypeMiddlePipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "5|Cookies=4||Crackers=3").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidWeightlessNoTypeQuantity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Any=").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidWeightlessZeroQuantity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Cookies=0").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidWeightlessNegativeQuantity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Cookies=-10").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidWeightlessTypeQuantityNaN()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Any=4X").passed());
		assertNoSideEffects();
	}

	public void testInvalidWeightlessUselessPipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidWeightlessTypeLeadingDoublePipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5||Any=4").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidWeightlessTypeTrailingPipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Any=4|").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidWeightlessTypeDoubleEquals()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Any=4=3").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidWeightlessTypeMiddlePipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "*5|Cookies=4||Crackers=3").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidReducedNoTypeQuantity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Any=").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidReducedTypeQuantityNaN()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Any=4X").passed());
		assertNoSideEffects();
	}

	public void testInvalidReducedUselessPipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidReducedTypeLeadingDoublePipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30||Any=4").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidReducedTypeTrailingPipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Any=4|").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidReducedZeroQuantity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Cookies=0").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidReducedNegativeQuantity()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf,
			"40%30|Cookies=-10").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidReducedTypeDoubleEquals()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "40%30|Any=4=3").passed());
		assertNoSideEffects();
	}

	@Test
	void testInvalidReducedTypeMiddlePipe()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf,
			"40%30|Cookies=4||Crackers=3").passed());
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinSimple() throws PersistenceLayerException
	{
		this.runRoundRobin("500");
	}

	@Test
	void testRoundRobinSimpleWeightless()
		throws PersistenceLayerException
	{
		this.runRoundRobin("*500");
	}

	@Test
	void testRoundRobinSimpleReducing() throws PersistenceLayerException
	{
		this.runRoundRobin("50%40");
	}

	@Test
	void testRoundRobinTypeLimited() throws PersistenceLayerException
	{
		this.runRoundRobin("50|Cookies");
	}

	@Test
	void testRoundRobinTypeLimitMix() throws PersistenceLayerException
	{
		this.runRoundRobin("5|Cookies=4|Crackers");
	}

	@Test
	void testRoundRobinWeightlessTypeLimitMix()
		throws PersistenceLayerException
	{
		this.runRoundRobin("*15|Cookies=4|Crackers");
	}

	@Test
	void testRoundRobinLimitedReducing()
		throws PersistenceLayerException
	{
		this.runRoundRobin("50%30|Any=25");
	}

	@Test
	void testRoundRobinCountLimitedReducing()
		throws PersistenceLayerException
	{
		this.runRoundRobin("25%UNLIM|Any=100");
	}

	@Test
	void testRoundRobinCountLimitedCursedAdding()
		throws PersistenceLayerException
	{
		this.runRoundRobin("-35%UNLIM|Any=100");
	}

	@Test
	void testRoundRobinTypeQuantityLimited()
		throws PersistenceLayerException
	{
		this.runRoundRobin("500|Potions=100");
	}

	@Test
	void testRoundRobinTypeUnlimited() throws PersistenceLayerException
	{
		this.runRoundRobin("UNLIM");
	}

	@Test
	void testRoundRobinTypeCountLimited()
		throws PersistenceLayerException
	{
		this.runRoundRobin("UNLIM|Any=100");
	}

	@Test
	void testRoundRobinSubUnlimited() throws PersistenceLayerException
	{
		this.runRoundRobin("3150|Any");
	}

	@Test
	void testRoundRobinTypeMultipleWeightUnlimited()
		throws PersistenceLayerException
	{
		this.runRoundRobin("UNLIM|Total=10|Paper=10|Scroll=10");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "UNLIM|Any=100";
	}

	@Override
	protected String getLegalValue()
	{
		return "500|Potions=50";
	}
	
	// CONSIDER Optional Input methods
	// this.runRoundRobin("UNLIM|Total=10|Paper=10|Scroll=UNLIM");
	// this.runRoundRobin("UNLIM|Paper=10|Scroll=UNLIM");

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
