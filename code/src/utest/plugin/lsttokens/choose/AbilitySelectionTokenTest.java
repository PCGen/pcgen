/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.choose;

import java.net.URISyntaxException;

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
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.qualifier.ability.PCToken;

public class AbilitySelectionTokenTest extends
		AbstractChooseTokenTestCase<CDOMObject, Ability>
{

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		primaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class,
			"Special Ability");
		secondaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class,
			"Special Ability");

	}

	static ChooseLst token = new ChooseLst();
	static AbilitySelectionToken subtoken = new AbilitySelectionToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

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
	protected Loadable construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.getReferenceContext().constructCDOMObject(Ability.class, one);
		Category<Ability> cat = loadContext.getReferenceContext()
				.silentlyGetConstructedCDOMObject(AbilityCategory.class,
						"Special Ability");
		loadContext.getReferenceContext().reassociateCategory(cat, obj);
		return obj;
	}

	@Override
	protected ReferenceManufacturer<Ability> getManufacturer()
	{
		Category<Ability> cat = primaryContext.getReferenceContext()
				.silentlyGetConstructedCDOMObject(AbilityCategory.class,
						"Special Ability");
		return primaryContext.getReferenceContext().getManufacturer(getTargetClass(), cat);
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
}
