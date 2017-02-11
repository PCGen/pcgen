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

import org.junit.Test;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.core.Campaign;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

public class ModifyLstTest extends AbstractGlobalTokenTestCase
{
	static CDOMPrimaryToken<CDOMObject> token = new ModifyLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(new plugin.modifier.number.AddModifierFactory());
		TokenRegistration.register(new plugin.modifier.number.MultiplyModifierFactory());
		FormatManager<?> formatManager = primaryContext.getReferenceContext().getFormatManager("NUMBER");
		LegalScope pscope = primaryContext.getActiveScope().getLegalScope();
		LegalScope sscope = primaryContext.getActiveScope().getLegalScope();
		primaryContext.getVariableContext().assertLegalVariableID(pscope, formatManager, "MyVar");
		secondaryContext.getVariableContext().assertLegalVariableID(sscope, formatManager, "MyVar");
		primaryContext.getVariableContext().assertLegalVariableID(pscope, formatManager, "OtherVar");
		secondaryContext.getVariableContext().assertLegalVariableID(sscope, formatManager, "OtherVar");
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
	public void testInvalidObject() throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, new Campaign(),
				"MyVar|ADD|3").passed());
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOneItem() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoArgs() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|ADD"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar||ADD|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoValue() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|ADD|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoVar() throws PersistenceLayerException
	{
		assertFalse(parse("ADD|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoModifier() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar||3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputInvalidVarName() throws PersistenceLayerException
	{
		assertFalse(parse("IllegalVar|ADD|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputInvalidMod() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|TRUFFLE|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputInvalidNoPriority() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|ADD|3|PRIORITY="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputInvalidNegativePriority() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|ADD|3|PRIORITY=-1000"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputInvalidNonNumberPriority() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|ADD|3|PRIORITY=String"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputInvalidTooManyArgs() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|ADD|3|PRIORITY=3|Yes"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputInvalidIllegalSourceVar() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|ADD|IllegalVar"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputInvalidNotPriority1() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|ADD|3|OTHER=3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputInvalidNotPriority2() throws PersistenceLayerException
	{
		assertFalse(parse("MyVar|ADD|3|OTHERSTRING=3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadVar() throws PersistenceLayerException
	{
		assertFalse(parse("4|ADD|3"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinAdd() throws PersistenceLayerException
	{
		runRoundRobin("MyVar|ADD|3");
	}

	@Test
	public void testRoundRobinMultiply() throws PersistenceLayerException
	{
		runRoundRobin("MyVar|MULTIPLY|OtherVar");
	}

	@Test
	public void testRoundRobinPriority() throws PersistenceLayerException
	{
		runRoundRobin("MyVar|ADD|3|PRIORITY=1090");
	}

	@Override
	protected String getLegalValue()
	{
		return "MyVar|ADD|3";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "OtherVar|MULTIPLY|3|PRIORITY=1000";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}
