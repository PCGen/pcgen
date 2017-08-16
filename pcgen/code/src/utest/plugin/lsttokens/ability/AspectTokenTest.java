/*
 * Copyright 2008 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.ability;

import org.junit.Test;

import pcgen.core.Ability;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

/**
 * The Class {@code AspectTokenTest} is responsible for verifying that
 * the Ability AspectToken is working properly 
 * 
 * 
 */
public class AspectTokenTest extends AbstractCDOMTokenTestCase<Ability>
{

	/** The token being tested. */
	static AspectToken token = new AspectToken();
	
	/** The token loader. */
	static CDOMTokenLoader<Ability> loader = new CDOMTokenLoader<>();

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
	}

	@Override
	public CDOMLoader<Ability> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Ability> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNoPipe() throws PersistenceLayerException
	{
		assertFalse(parse("NoPipe"));
		assertNoSideEffects();
	}

	@Test
	public void testValidTwoPipe() throws PersistenceLayerException
	{
		assertTrue(parse("One|Two|Three"));
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("Two||Pipe"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyPipe() throws PersistenceLayerException
	{
		assertFalse(parse("|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyKey() throws PersistenceLayerException
	{
		assertFalse(parse("|Value"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyValue() throws PersistenceLayerException
	{
		assertFalse(parse("Key|"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("QualityName|QualityValue");
	}

	@Test
	public void testRoundRobinSpaces() throws PersistenceLayerException
	{
		runRoundRobin("Quality Name|Quality Value");
	}

	@Test
	public void testRoundRobinInternational() throws PersistenceLayerException
	{
		runRoundRobin("Niederösterreich Quality|Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon Quality|Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinEncoding() throws PersistenceLayerException
	{
		runRoundRobin("DESC|&nl; Trained&colon; When you gain this aspect, choose persuasion...");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "First Quality|Niederösterreich";
	}

	@Override
	protected String getLegalValue()
	{
		return "First Quality|Languedoc-Roussillon";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}
