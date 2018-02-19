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
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
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
import plugin.qualifier.ability.PCToken;

public class AbilityTokenTest extends
		AbstractChooseTokenTestCase<CDOMObject, Ability>
{

	static ChooseLst token = new ChooseLst();
	static AbilityToken subtoken = new AbilityToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		primaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class,
				"Special Ability");
		secondaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class,
				"Special Ability");
		//We build dummy objects so that AbilityCategory.FEAT has been loaded properly
		construct(primaryContext, "Dummy");
		construct(secondaryContext, "Dummy");
	}

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
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
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<Ability> getTargetClass()
	{
		return Ability.class;
	}

	@Override
	protected boolean allowsQualifier()
	{
		return true;
	}

	@Override
	protected String getChoiceTitle()
	{
		return subtoken.getDefaultTitle();
	}

	@Override
	protected QualifierToken<Ability> getPCQualifier()
	{
		return new PCToken();
	}

	@Override
	protected Loadable construct(LoadContext loadContext, String name)
	{
		AbilityCategory cat = loadContext.getReferenceContext()
				.silentlyGetConstructedCDOMObject(AbilityCategory.class,
						"Special Ability");
		return BuildUtilities.buildAbility(loadContext, cat, name);
	}

	@Override
	protected ReferenceManufacturer<Ability> getManufacturer()
	{
		Category<Ability> cat = primaryContext.getReferenceContext()
				.silentlyGetConstructedCDOMObject(AbilityCategory.class,
						"Special Ability");
		return primaryContext.getReferenceContext().getManufacturerId(cat);
	}

	@Override
	protected boolean isTypeLegal()
	{
		return true;
	}

	@Override
	protected boolean isAllLegal()
	{
		return true;
	}

	@Override
	public String getSubTokenName()
	{
		return super.getSubTokenName() + "|Special Ability";
	}

	@Override
	public void testUnparseLegal() throws PersistenceLayerException
	{
		//Hard to get correct - doesn't assume Category :(
	}

	@Test
	public void testInvalidBadCategory() throws PersistenceLayerException
	{
		assertFalse(parse("ABILITY|BadCat|TYPE=Foo"));
		assertNoSideEffects();
	}

	@Override
	protected Ability getSecondary(String name)
	{
		Ability a = AbilityCategory.FEAT.newInstance();
		a.setName(name);
		secondaryContext.getReferenceContext().importObject(a);
		return a;
	}

	@Override
	protected Ability getPrimary(String name)
	{
		Ability a = AbilityCategory.FEAT.newInstance();
		a.setName(name);
		primaryContext.getReferenceContext().importObject(a);
		return a;
	}
}

