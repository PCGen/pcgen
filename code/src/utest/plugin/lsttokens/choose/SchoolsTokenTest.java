/*
 * 
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.AbstractChooseTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

public class SchoolsTokenTest extends AbstractChooseTokenTestCase
{

	static ChooseLst token = new ChooseLst();
	static SchoolsToken subtoken = new SchoolsToken();
	static plugin.primitive.pobject.AbilityToken<?> abprim =
			new plugin.primitive.pobject.AbilityToken();
	static CDOMTokenLoader<CDOMObject> loader =
			new CDOMTokenLoader<>();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(subtoken);
		TokenRegistration.register(abprim);
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "SCHOOLS|ALL";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Override
	protected String getLegalValue()
	{
		return "SCHOOLS|Abjuration";
	}

	@Test
	public void testRoundRobinAll() throws PersistenceLayerException
	{
		construct(primaryContext, "Abjuration");
		construct(secondaryContext, "Abjuration");
		runRoundRobin("SCHOOLS|ALL");
	}

	@Test
	public void testRoundRobinFeat() throws PersistenceLayerException
	{
		construct(primaryContext, "Abjuration");
		construct(secondaryContext, "Abjuration");
		Ability ss =
				primaryContext.getReferenceContext().constructCDOMObject(Ability.class,
					"School Stuff");
		primaryContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, ss);
		ss =
				secondaryContext.getReferenceContext().constructCDOMObject(Ability.class,
					"School Stuff");
		secondaryContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, ss);
		runRoundRobin("SCHOOLS|ABILITY=FEAT[School Stuff]");
	}

	@Test
	public void testRoundRobinItems() throws PersistenceLayerException
	{
		construct(primaryContext, "Abjuration");
		construct(primaryContext, "Evocation");
		construct(secondaryContext, "Abjuration");
		construct(secondaryContext, "Evocation");
		runRoundRobin("SCHOOLS|Abjuration|Evocation");
	}

	@Test
	public void testRoundRobinSpecificTitle() throws PersistenceLayerException
	{
		construct(primaryContext, "Abjuration");
		construct(primaryContext, "Evocation");
		construct(secondaryContext, "Abjuration");
		construct(secondaryContext, "Evocation");
		runRoundRobin("SCHOOLS|Abjuration|Evocation|TITLE=Pick a Special School");
	}

	@Test
	public void testInvalidInputNoBrackets() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOLS|Sorry No [Brackets]"));
		assertNoSideEffects();
	}

	@Override
	protected boolean allowsQualifier()
	{
		return false;
	}

	@Override
	protected String getChoiceTitle()
	{
		return "Title For Choice";
	}

	@Override
	protected QualifierToken<CDOMObject> getPCQualifier()
	{
		return null;
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<SpellSchool> getTargetClass()
	{
		return SpellSchool.class;
	}

	@Override
	protected boolean isAllLegal()
	{
		return true;
	}

	@Override
	protected boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public void testInvalidInputOnlySubToken() throws PersistenceLayerException
	{
		// Must ignore due to 5.16 syntax
	}

	@Override
	public void testInvalidInputOnlySubTokenPipe()
		throws PersistenceLayerException
	{
		// Must ignore due to 5.16 syntax
	}

	@Override
	protected Loadable construct(LoadContext loadContext, String one)
	{
		return loadContext.getReferenceContext().constructNowIfNecessary(SpellSchool.class, one);
	}

	@Override
	public void testUnparseIllegalAllItem() throws PersistenceLayerException
	{
		//Ignore since SpellSchool doesn't have a RM
	}

	@Override
	public void testUnparseIllegalAllType() throws PersistenceLayerException
	{
		//Ignore since SpellSchool doesn't have a RM
	}

	@Override
	public void testUnparseIllegalItemAll() throws PersistenceLayerException
	{
		//Ignore since SpellSchool doesn't have a RM
	}

	@Override
	public void testUnparseIllegalTypeAll() throws PersistenceLayerException
	{
		//Ignore since SpellSchool doesn't have a RM
	}

	@Override
	public void testUnparseLegal() throws PersistenceLayerException
	{
		//Ignore since SpellSchool doesn't have a RM
	}

	@Override
	protected boolean usesComma()
	{
		return false;
	}
}
