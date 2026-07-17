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

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.AbstractChooseTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SchoolsTokenTest extends AbstractChooseTokenTestCase
{

	static ChooseLst token = new ChooseLst();
	static SchoolsToken subtoken = new SchoolsToken();
	static plugin.primitive.pobject.AbilityToken<?> abprim =
			new plugin.primitive.pobject.AbilityToken();
	static CDOMTokenLoader<CDOMObject> loader =
			new CDOMTokenLoader<>();

	@BeforeEach
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
	protected String getLegalValue()
	{
		return "SCHOOLS|Abjuration";
	}

	@Test
	void testRoundRobinAll() throws PersistenceLayerException
	{
		construct(primaryContext, "Abjuration");
		construct(secondaryContext, "Abjuration");
		runRoundRobin("SCHOOLS|ALL");
	}

	@Test
	void testRoundRobinFeat() throws PersistenceLayerException
	{
		construct(primaryContext, "Abjuration");
		construct(secondaryContext, "Abjuration");
		BuildUtilities.buildAbility(primaryContext, BuildUtilities.getFeatCat(), "School Stuff");
		BuildUtilities.buildAbility(secondaryContext, BuildUtilities.getFeatCat(), "School Stuff");
		runRoundRobin("SCHOOLS|ABILITY=FEAT[School Stuff]");
	}

	@Test
	void testRoundRobinItems() throws PersistenceLayerException
	{
		construct(primaryContext, "Abjuration");
		construct(primaryContext, "Evocation");
		construct(secondaryContext, "Abjuration");
		construct(secondaryContext, "Evocation");
		runRoundRobin("SCHOOLS|Abjuration|Evocation");
	}

	@Test
	void testRoundRobinSpecificTitle() throws PersistenceLayerException
	{
		construct(primaryContext, "Abjuration");
		construct(primaryContext, "Evocation");
		construct(secondaryContext, "Abjuration");
		construct(secondaryContext, "Evocation");
		runRoundRobin("SCHOOLS|Abjuration|Evocation|TITLE=Pick a Special School");
	}

	@Test
	void testInvalidInputNoBrackets()
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
	@Test
	@Disabled("Must ignore due to 5.16 syntax")
	public void testInvalidInputOnlySubToken()
	{
	}

	@Override
	@Test
	@Disabled("Must ignore due to 5.16 syntax")
	public void testInvalidInputOnlySubTokenPipe()
	{
	}

	@Override
	protected Loadable construct(LoadContext loadContext, String one)
	{
		return loadContext.getReferenceContext().constructNowIfNecessary(SpellSchool.class, one);
	}

	@Override
	@Test
	@Disabled("SpellSchool doesn't have a RM")
	public void testUnparseIllegalAllItem()
	{
	}

	@Override
	@Test
	@Disabled("SpellSchool doesn't have a RM")
	public void testUnparseIllegalAllType()
	{
	}

	@Override
	@Test
	@Disabled("SpellSchool doesn't have a RM")
	public void testUnparseIllegalItemAll()
	{
	}

	@Override
	@Test
	@Disabled("SpellSchool doesn't have a RM")
	public void testUnparseIllegalTypeAll()
	{
	}

	@Override
	@Test
	@Disabled("SpellSchool doesn't have a RM")
	public void testUnparseLegal()
	{
	}

	@Override
	protected boolean usesComma()
	{
		return false;
	}
}
