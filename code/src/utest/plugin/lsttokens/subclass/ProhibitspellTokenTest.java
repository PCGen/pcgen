/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.subclass;

import org.junit.Test;

import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class ProhibitspellTokenTest extends AbstractTokenTestCase<SubClass>
{

	static ChoiceToken token = new ChoiceToken();
	static CDOMTokenLoader<SubClass> loader = new CDOMTokenLoader<SubClass>(
			SubClass.class);

	@Override
	public Class<SubClass> getCDOMClass()
	{
		return SubClass.class;
	}

	@Override
	public CDOMLoader<SubClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<SubClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyType() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoValue() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOL|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoType() throws PersistenceLayerException
	{
		assertFalse(parse("|Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("|SCHOOL|Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOL|Good|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleDot() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOL||Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotAType() throws PersistenceLayerException
	{
		assertFalse(parse("NOTATYPE|Good"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinDescriptorSimple()
			throws PersistenceLayerException
	{
		runRoundRobin("DESCRIPTOR|Fire");
	}

	@Test
	public void testRoundRobinSchoolSimple() throws PersistenceLayerException
	{
		runRoundRobin("SCHOOL|Evocation");
	}

	@Test
	public void testRoundRobinSubSchoolSimple()
			throws PersistenceLayerException
	{
		runRoundRobin("SUBSCHOOL|Subsch");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "SUBSCHOOL|Subsch";
	}

	@Override
	protected String getLegalValue()
	{
		return "SCHOOL|Evocation";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
