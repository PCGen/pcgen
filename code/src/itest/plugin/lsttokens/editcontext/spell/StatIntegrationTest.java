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

import pcgen.core.PCStat;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractItemIntegrationTestCase;
import plugin.lsttokens.spell.StatToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.BeforeEach;

public class StatIntegrationTest extends
		AbstractItemIntegrationTestCase<Spell, PCStat>
{

	private static StatToken token = new StatToken();
	private static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<>();

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
	@BeforeEach
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		PCStat ps = BuildUtilities.createStat("Strength", "STR");
		primaryContext.getReferenceContext().importObject(ps);
		PCStat pi = BuildUtilities.createStat("Intelligence", "INT");
		primaryContext.getReferenceContext().importObject(pi);
		PCStat ss = BuildUtilities.createStat("Strength", "STR");
		secondaryContext.getReferenceContext().importObject(ss);
		PCStat si = BuildUtilities.createStat("Intelligence", "INT");
		secondaryContext.getReferenceContext().importObject(si);
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
}
