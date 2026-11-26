/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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

import pcgen.base.util.FormatManager;
import pcgen.cdom.base.ConcretePrereqObject;
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

import org.junit.jupiter.api.Test;

/**
 * Tests the INFOVARS token. INFOVARS references variables already declared in the formula
 * system; the variables it can resolve are global (written bare, no scope prefix), so these
 * tests exercise bare variable names in the active scope. See INFO &amp; INFOVARS spec.
 */
class InfoVarsLstTest extends AbstractGlobalTokenTestCase
{
	static InfoVarsLst token = new InfoVarsLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

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
	public CDOMToken<? extends ConcretePrereqObject> getReadToken()
	{
		return token;
	}

	@Override
	public CDOMWriteToken<? extends ConcretePrereqObject> getWriteToken()
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
	void testInvalidInputOnlyName()
	{
		assertFalse(parse("InfoName"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmptyEntry()
	{
		assertFalse(parse("InfoName|MyVar||OtherVar"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputIllegalVarName()
	{
		assertFalse(parse("InfoName|NotAVariable"));
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinSingle() throws PersistenceLayerException
	{
		runRoundRobin("InfoName|MyVar");
	}

	@Test
	void testRoundRobinMultiple() throws PersistenceLayerException
	{
		runRoundRobin("InfoName|MyVar|OtherVar");
	}

	@Override
	protected String getLegalValue()
	{
		return "InfoName|MyVar";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "InfoName|MyVar|OtherVar";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
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
