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

import org.junit.Assert;
import org.junit.Test;

import pcgen.base.lang.ObjectUtil;
import pcgen.cdom.content.DatasetVariable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class LocalTokenTest extends AbstractTokenTestCase<DatasetVariable>
{

	private static LocalToken token = new LocalToken();
	private static CDOMTokenLoader<DatasetVariable> loader =
			new CDOMTokenLoader<DatasetVariable>();

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
		Assert.assertFalse(pr.passed());
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadName() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Bad-Name"));
		assertNoSideEffects();
	}

	@Test
	public void testValidBasic() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("EQUIPMENT|IsANumberVar"));
	}

	@Test
	public void testValidFormatted() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("EQUIPMENT|NUMBER=IsANumberVar"));
	}

	@Test
	public void testInvalidDoubleEqual() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("EQUIPMENT|STRING==Pipe"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("EQUIPMENT||STRING=Pipe"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidThreeArgs() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("EQUIPMENT|STRING|Pipe"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidThreeEqualArgs() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("EQUIPMENT|STRING=Pipe=Too"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		Assert.assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyEqual() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("EQUIPMENT|="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyFormat() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("EQUIPMENT|=Value"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyVarName() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("EQUIPMENT|NUMBER="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadFormat() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("EQUIPMENT|BADFORMAT=Illegal"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidName() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("EQUIPMENT|NUMBER=Illegal Name!"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyScope() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("|STRING=Value"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadScope() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("BADSCOPE|STRING=Illegal"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinDefault() throws PersistenceLayerException
	{
		runRoundRobin("EQUIPMENT|IsANumberVar");
	}

	@Test
	public void testRoundRobinFormatted() throws PersistenceLayerException
	{
		runRoundRobin("EQUIPMENT|STRING=StringVar");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "EQUIPMENT|Alt";
	}

	@Override
	protected String getLegalValue()
	{
		return "EQUIPMENT|Var";
	}

	@Test
	public void testInvalidDupeVarName() throws PersistenceLayerException
	{
		DatasetVariable dv = new DatasetVariable();
		ParseResult pr =
				token.parseToken(primaryContext, dv, "EQUIPMENT|MyVar");
		Assert.assertTrue(pr.passed());
		Assert.assertFalse(parse("EQUIPMENT|STRING=MyVar"));
		assertNoSideEffects();
	}

	@Override
	public void isCDOMEqual(DatasetVariable dv1, DatasetVariable dv2)
	{
		Assert.assertTrue(
			"Display Name not equal " + dv1 + " and " + dv2,
			ObjectUtil.compareWithNull(dv1.getDisplayName(),
				dv2.getDisplayName()));
		Assert.assertTrue("Format not equal " + dv1 + " and " + dv2,
			ObjectUtil.compareWithNull(dv1.getFormat(), dv2.getFormat()));
		Assert.assertTrue("Scope Name not equal " + dv1 + " and " + dv2,
			ObjectUtil.compareWithNull(dv1.getScopeName(), dv2.getScopeName()));
		Assert.assertTrue("Source URI not equal " + dv1 + " and " + dv2,
			ObjectUtil.compareWithNull(dv1.getSourceURI(), dv2.getSourceURI()));
		Assert.assertTrue(
			"Explanation not equal " + dv1 + " and " + dv2,
			ObjectUtil.compareWithNull(dv1.getExplanation(),
				dv2.getExplanation()));
	}

	@Override
	protected DatasetVariable getPrimary(String name)
	{
		return new DatasetVariable();
	}

	@Override
	protected DatasetVariable getSecondary(String name)
	{
		return new DatasetVariable();
	}

	@Override
	@Test
	public void testOverwrite() throws PersistenceLayerException
	{
		Assert.assertTrue(parse(getLegalValue()));
		validateUnparsed(primaryContext, primaryProf, getLegalValue());
		Assert.assertFalse(parse(getAlternateLegalValue()));
	}

}
