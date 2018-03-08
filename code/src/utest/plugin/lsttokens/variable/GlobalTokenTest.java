/*
 * Copyright (c) 2016 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.variable;

import java.util.Objects;

import org.junit.Test;

import pcgen.cdom.content.DatasetVariable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class GlobalTokenTest extends AbstractTokenTestCase<DatasetVariable>
{

	private static GlobalToken token = new GlobalToken();
	private static CDOMTokenLoader<DatasetVariable> loader =
			new CDOMTokenLoader<>();

	@Override
	public CDOMPrimaryToken<DatasetVariable> getToken()
	{
		return token;
	}

	@Override
	public Class<DatasetVariable> getCDOMClass()
	{
		return DatasetVariable.class;
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Override
	public CDOMLoader<DatasetVariable> getLoader()
	{
		return loader;
	}

	@Test
	public void testDisplayNameProhibited() throws PersistenceLayerException
	{
		DatasetVariable dv = new DatasetVariable();
		dv.setName("FirstName");
		ParseResult pr = token.parseToken(primaryContext, dv, "VarName");
		assertFalse(pr.passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadName() throws PersistenceLayerException
	{
		assertFalse(parse("Bad-Name"));
		assertNoSideEffects();
	}

	@Test
	public void testValidBasic() throws PersistenceLayerException
	{
		assertTrue(parse("IsANumberVar"));
	}

	@Test
	public void testValidFormatted() throws PersistenceLayerException
	{
		assertTrue(parse("NUMBER=IsANumberVar"));
	}

	@Test
	public void testInvalidDoubleEqual() throws PersistenceLayerException
	{
		assertFalse(parse("STRING==Pipe"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyEqual() throws PersistenceLayerException
	{
		assertFalse(parse("="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyFormat() throws PersistenceLayerException
	{
		assertFalse(parse("=Value"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyVarName() throws PersistenceLayerException
	{
		assertFalse(parse("NUMBER="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDupeVarName() throws PersistenceLayerException
	{
		DatasetVariable dv = new DatasetVariable();
		ParseResult pr = token.parseToken(primaryContext, dv, "MyVar");
		assertTrue(pr.passed());
		assertFalse(parse("STRING=MyVar"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadFormat() throws PersistenceLayerException
	{
		assertFalse(parse("BADFORMAT=Illegal"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidName() throws PersistenceLayerException
	{
		assertFalse(parse("NUMBER=Illegal Name!"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinDefault() throws PersistenceLayerException
	{
		runRoundRobin("IsANumberVar");
	}

	@Test
	public void testRoundRobinFormatted() throws PersistenceLayerException
	{
		runRoundRobin("STRING=StringVar");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Alternate Explanation!";
	}

	@Override
	protected String getLegalValue()
	{
		return "Explanation";
	}

	@Override
	public void isCDOMEqual(DatasetVariable dv1, DatasetVariable dv2)
	{
		assertTrue(
			"Display Name not equal " + dv1 + " and " + dv2,
			Objects.equals(dv1.getDisplayName(), dv2.getDisplayName()));
		assertTrue("Format not equal " + dv1 + " and " + dv2,
			Objects.equals(dv1.getFormat(), dv2.getFormat()));
		assertTrue("Scope Name not equal " + dv1 + " and " + dv2,
			Objects.equals(dv1.getScope(), dv2.getScope()));
		assertTrue("Source URI not equal " + dv1 + " and " + dv2,
			Objects.equals(dv1.getSourceURI(), dv2.getSourceURI()));
		assertTrue(
			"Explanation not equal " + dv1 + " and " + dv2,
			Objects.equals(dv1.getExplanation(), dv2.getExplanation()));
	}

	@Override
	protected DatasetVariable get(LoadContext context, String name)
	{
		return new DatasetVariable();
	}

	@Test
	@Override
	public void testOverwrite() throws PersistenceLayerException
	{
		assertTrue(parse(getLegalValue()));
		validateUnparsed(primaryContext, primaryProf, getLegalValue());
		assertFalse(parse(getAlternateLegalValue()));
	}

}
