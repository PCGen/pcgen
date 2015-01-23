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

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCStat;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractItemIntegrationTestCase;
import plugin.lsttokens.spell.StatToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class StatIntegrationTest extends
		AbstractItemIntegrationTestCase<Spell, PCStat>
{

	static StatToken token = new StatToken();
	static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<Spell>();

	@Override
	protected String getFirstConstant()
	{
		return "STR";
	}

	@Override
	protected String getSecondConstant()
	{
		return "INT";
	}

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		PCStat ps = primaryContext.getReferenceContext().constructCDOMObject(PCStat.class, "STR");
		ps.put(StringKey.ABB, "STR");
		primaryContext.getReferenceContext().registerAbbreviation(ps, "STR");
		PCStat ss = secondaryContext.getReferenceContext().constructCDOMObject(PCStat.class,
				"STR");
		ss.put(StringKey.ABB, "STR");
		secondaryContext.getReferenceContext().registerAbbreviation(ss, "STR");
		PCStat pi = primaryContext.getReferenceContext().constructCDOMObject(PCStat.class, "INT");
		pi.put(StringKey.ABB, "INT");
		primaryContext.getReferenceContext().registerAbbreviation(pi, "INT");
		PCStat si = secondaryContext.getReferenceContext().constructCDOMObject(PCStat.class,
				"INT");
		si.put(StringKey.ABB, "INT");
		secondaryContext.getReferenceContext().registerAbbreviation(si, "INT");
	}

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
	public Class<PCStat> getTargetClass()
	{
		return PCStat.class;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		//Ignore request
	}
	
	
}
