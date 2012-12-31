/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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
package tokencontent;

import java.util.Collection;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.KnownSpellFacet;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.spell.Spell;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.SpellknownLst;
import tokencontent.testsupport.AbstractContentTokenTest;

public class GlobalSpellKnownTest extends AbstractContentTokenTest
{

	private static SpellknownLst token = new SpellknownLst();
	private KnownSpellFacet knownSpellFacet;
	private ClassSpellList wizardSpellList;
	private Spell fb;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		knownSpellFacet = FacetLibrary.getFacet(KnownSpellFacet.class);
		wizardSpellList = context.ref.constructNowIfNecessary(ClassSpellList.class, "Wizard");
		fb = context.ref.constructNowIfNecessary(Spell.class, "Fireball");
	}

	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result = token.parseToken(context, source, "CLASS|Wizard=2|Fireball");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

	@Override
	protected boolean containsExpected()
	{
		//Cannot use contains because facet is using instance identity
		Map<Integer, Collection<Spell>> levelMap = knownSpellFacet.getKnownSpells(id, wizardSpellList);
		int size = levelMap.size();
		if (size != 1)
		{
			System.err.println("Size Incorrect");
			return false;
		}
		Collection<Spell> spells = levelMap.get(2);
		if (spells.size() != 1)
		{
			System.err.println("Spell Size Incorrect");
			return false;
		}
		return spells.contains(fb);
	}

	@Override
	protected int targetFacetCount()
	{
		Map<Integer, Collection<Spell>> levelMap = knownSpellFacet.getKnownSpells(id, wizardSpellList);
		if (levelMap.isEmpty())
		{
			return 0;
		}
		Collection<Spell> spells = levelMap.get(2);
		return (spells == null) ? 0 : spells.size();
	}

	@Override
	protected int baseCount()
	{
		return 0;
	}
}
