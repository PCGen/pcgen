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

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.base.util.FormatManager;
import pcgen.cdom.base.VarContainer;
import pcgen.cdom.base.VarHolder;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.CDOMWriteToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModifyLstTest extends AbstractGlobalTokenTestCase
{
	private static ModifyLst token = new ModifyLst();
	private static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

	@BeforeEach
	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(new plugin.modifier.number.AddModifierFactory());
		TokenRegistration.register(new plugin.modifier.number.MultiplyModifierFactory());
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
	public CDOMToken<VarHolder> getReadToken()
	{
		return token;
	}

	@Override
	public CDOMWriteToken<VarContainer> getWriteToken()
	{
		return token;
	}

	@Test
	void testInvalidInputEmpty()
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOneItem()
	{
		assertFalse(parse("MyVar"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTwoArgs()
	{
		assertFalse(parse("MyVar|ADD"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoublePipe()
	{
		assertFalse(parse("MyVar||ADD|3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNoValue()
	{
		assertFalse(parse("MyVar|ADD|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNoVar()
	{
		assertFalse(parse("ADD|3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNoModifier()
	{
		assertFalse(parse("MyVar||3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputInvalidVarName()
	{
		assertFalse(parse("IllegalVar|ADD|3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputInvalidMod()
	{
		assertFalse(parse("MyVar|TRUFFLE|3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputInvalidNoPriority()
	{
		assertFalse(parse("MyVar|ADD|3|PRIORITY="));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputInvalidNegativePriority()
	{
		assertFalse(parse("MyVar|ADD|3|PRIORITY=-1000"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputInvalidNonNumberPriority()
	{
		assertFalse(parse("MyVar|ADD|3|PRIORITY=String"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputInvalidTooManyArgs()
	{
		assertFalse(parse("MyVar|ADD|3|PRIORITY=3|Yes"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputInvalidIllegalSourceVar()
	{
		assertFalse(parse("MyVar|ADD|IllegalVar"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputInvalidNotPriority1()
	{
		assertFalse(parse("MyVar|ADD|3|OTHER=3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputInvalidNotPriority2()
	{
		assertFalse(parse("MyVar|ADD|3|OTHERSTRING=3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputBadVar()
	{
		assertFalse(parse("4|ADD|3"));
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinAdd() throws PersistenceLayerException
	{
		runRoundRobin("MyVar|ADD|3");
	}

	@Test
	void testRoundRobinMultiply() throws PersistenceLayerException
	{
		runRoundRobin("MyVar|MULTIPLY|OtherVar");
	}

	@Test
	void testRoundRobinPriority() throws PersistenceLayerException
	{
		runRoundRobin("MyVar|ADD|3|PRIORITY=1090");
	}

	//TODO Ignore for now; reactivate later, see CODE-3299
//	@Test
//	public void testInvalidObject()
//	{
//		assertFalse(token.parseToken(primaryContext, new Campaign(),
//				"MyVar|ADD|3").passed());
//	}

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

	@Override
	protected void additionalSetup(LoadContext context)
	{
		super.additionalSetup(context);
		FormatManager<?> formatManager = context.getReferenceContext().getFormatManager("NUMBER");
		PCGenScope scope = context.getActiveScope();
		context.getVariableContext().assertLegalVariableID("MyVar", scope, formatManager);
		context.getVariableContext().assertLegalVariableID("OtherVar", scope, formatManager);
	}

}
