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
package plugin.lsttokens;

import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class VisionLstTest extends AbstractGlobalTokenTestCase
{

	PreClassParser preclass = new PreClassParser();
	PreClassWriter preclasswriter = new PreClassWriter();
	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	static CDOMPrimaryToken<CDOMObject> token = new VisionLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<PCTemplate>();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
	}

	@Override
	public CDOMLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNoOpenParen() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision 25')"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoCloseParen() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision (25'"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoParen() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision 25'"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidExtraStuff() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision (25')Normal"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidExtraStuffAfterFoot()
		throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision (25'm)"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDecimalFoot() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision (25.5')"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDistanceNaN() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision (zzzb32')"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidNoOpenParen() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Normal|Darkvision 25')"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidNoCloseParen() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Normal|Darkvision (25'"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidNoParen() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Normal|Darkvision 25'"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidExtraStuff() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Normal|Darkvision (25')Normal"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidExtraStuffAfterFoot()
		throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Normal|Darkvision (25'm)"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidDecimalFoot() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Normal|Darkvision (25.5')"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidDistanceNaN() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Normal|Darkvision (zzzb32')"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoComma() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Normal,Darkvision"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyPre() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("PRERACE:1,Dwarf"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTrailingPipe() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidLeadingPipe() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("|Darkvision"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision||PRERACE:1,Dwarf"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMiddlePre() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("Darkvision|PRERACE:1,Dwarf|Normal (100')"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidClearDotPre() throws PersistenceLayerException
	{
		Assert.assertFalse(parse(".CLEAR.Darkvision|PRERACE:1,Dwarf"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidClearPre() throws PersistenceLayerException
	{
		Assert.assertFalse(parse(".CLEAR|PRERACE:1,Dwarf"));
		assertNoSideEffects();
	}

	@Test
	public void testValidDistanceFormula() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("Darkvision (zzzb32)"));
	}

	@Test
	public void testValidDistanceNoSpaceNumber()
		throws PersistenceLayerException
	{
		Assert.assertTrue(parse("Darkvision(20')"));
	}

	@Test
	public void testValidDistanceNoSpaceShortNumber()
		throws PersistenceLayerException
	{
		Assert.assertTrue(parse("Darkvision(5')"));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision");
	}

	@Test
	public void testRoundRobinSimplePre() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision|PRERACE:1,Dwarf");
	}

	@Test
	public void testRoundRobinNumber() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (30')");
	}

	@Test
	public void testRoundRobinShortNumber() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (5')");
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (Formula*5)");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision|Normal");
	}

	@Test
	public void testRoundRobinMultipleNumber() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (10')|Normal");
	}

	@Test
	public void testRoundRobinMultipleNumberToo()
		throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (10')|Normal (20')");
	}

	@Test
	public void testRoundRobinMultipleNumberSame()
		throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (20')|Normal (20')");
	}

	@Test
	public void testRoundRobinMultipleFormula()
		throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (CL*10)|Normal (Form)");
	}

	@Test
	public void testRoundRobinTwoPre() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (20')|TestWP1|PRECLASS:1,Fighter=3|PRERACE:1,Dwarf");
	}

	@Override
	protected String getLegalValue()
	{
		return "Darkvision (20')|TestWP1|PRECLASS:1,Fighter=3|PRERACE:1,Dwarf";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Darkvision (CL*10)|Normal (Form)";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}
