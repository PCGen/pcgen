/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.editcontext;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.DescLst;
import plugin.lsttokens.editcontext.testsupport.AbstractTypeSafeListIntegrationTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class DescIntegrationTest extends
		AbstractTypeSafeListIntegrationTestCase<CDOMObject>
{

	static DescLst token = new DescLst();
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
	public boolean isClearLegal()
	{
		return true;
	}

	@Override
	public Object getConstant(String string)
	{
		return string;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public boolean isClearDotLegal()
	{
		return true;
	}

	@Override
	protected boolean requiresPreconstruction()
	{
		return false;
	}

	@Override
	protected Ability construct(LoadContext context, String name)
	{
		Ability a = AbilityCategory.FEAT.newInstance();
		a.setName(name);
		context.getReferenceContext().importObject(a);
		return a;
	}
}
