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
package plugin.lsttokens.editcontext.spell;

import pcgen.core.spell.Spell;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractStringIntegrationTestCase;
import plugin.lsttokens.spell.TargetareaToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class TargetAreaIntegrationTest extends
		AbstractStringIntegrationTestCase<Spell>
{

	private static TargetareaToken token = new TargetareaToken();
	private static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<>();

	@Override
	public Class<Spell> getCDOMClass()
	{
		return Spell.class;
	}

	@Override
	public CDOMLoader<Spell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Spell> getToken()
	{
		return token;
	}

	@Override
	public boolean isClearLegal()
	{
		return true;
	}
}
