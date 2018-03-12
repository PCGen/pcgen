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

import pcgen.base.format.StringManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.factset.FactSetDefinition;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

public class FactSetLstTest extends AbstractGlobalTokenTestCase
{
	private static FactSetLst token = new FactSetLst();
	private static CDOMTokenLoader<Domain> loader = new CDOMTokenLoader<>();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		TokenRegistration.clearTokens();
		super.setUp();
	}

	@Override
	public Class<Domain> getCDOMClass()
	{
		return Domain.class;
	}

	@Override
	public CDOMLoader<Domain> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputOnlyNumber() throws PersistenceLayerException
	{
		assertFalse(parse("Possibility"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotAFact() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("NaN|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoTarget() throws PersistenceLayerException
	{
		assertFalse(parse("Possibility|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoFact() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("Possibility||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStrange() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("|TestWP1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("Possibility|TestWP1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("Possibility||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("Possibility|TestWP1"));
		assertCleanConstruction();
		assertTrue(parse("Possibility|TestWP1|TestWP2"));
		assertCleanConstruction();
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("Possibility|TestWP1");
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, one);
	}

	@Override
	protected String getLegalValue()
	{
		return "Possibility|TestWP1";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Possibility|TestWP2";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return strings -> new String[] { "Possibility|TestWP1|TestWP2" };
	}

	@Override
	protected void additionalSetup(LoadContext context)
	{
		super.additionalSetup(context);
		FactSetDefinition fd = new FactSetDefinition();
		fd.setName("DEITY.Possibility");
		fd.setFactSetName("Possibility");
		fd.setUsableLocation(Domain.class);
		fd.setFormatManager(new StringManager());
		fd.setVisibility(Visibility.HIDDEN);
		context.getReferenceContext().importObject(fd);
		SourceFileLoader.processFactDefinitions(context);
	}
	
	
}
