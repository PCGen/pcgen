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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.KnownSpellFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.Ability;
import pcgen.core.PCTemplate;
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.core.spell.Spell;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.SpellknownLst;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreVariableParser;
import plugin.pretokens.test.PreVariableTester;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalSpellKnownTest extends AbstractContentTokenTest
{

	private static final CDOMToken<CDOMObject> token = new SpellknownLst();
	private KnownSpellFacet knownSpellFacet;
	private ClassSpellList wizardSpellList;
	private Spell fb;

	@BeforeAll
	static void classSetUp()
	{
		PrerequisiteTestFactory.getInstance().register(new PreVariableTester());
	}

	@Override
	@BeforeEach
	public void setUp() throws Exception
	{
		super.setUp();
		knownSpellFacet = FacetLibrary.getFacet(KnownSpellFacet.class);
		wizardSpellList = context.getReferenceContext().constructNowIfNecessary(ClassSpellList.class, "Wizard");
		fb = context.getReferenceContext().constructNowIfNecessary(Spell.class, "Fireball");
		TokenRegistration.register(new PreVariableParser());
	}

	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result = token.parseToken(context, source, "CLASS|Wizard=2|Fireball");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages(TestURI.getURI());
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
		Collection<Integer> levels = knownSpellFacet.getScopes2(id, wizardSpellList);
		int size = levels.size();
		if (size != 1)
		{
			System.err.println("Size Incorrect");
			return false;
		}
		if (!levels.contains(2))
		{
			System.err.println("Level Incorrect");
			return false;
		}
		Collection<Spell> spells = knownSpellFacet.getSet(id, wizardSpellList, 2);
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
		Collection<Spell> spells = knownSpellFacet.getSet(id, wizardSpellList, 2);
		return (spells == null) ? 0 : spells.size();
	}

	@Override
	protected int baseCount()
	{
		return 0;
	}
	
	public void testConditional()
	{
		Ability source = BuildUtilities.buildFeat(context, "Source");
		ParseResult result =
				token.parseToken(context, source, "CLASS|Wizard=2|Fireball|PREVARLTEQ:3,MyCasterLevel");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages(TestURI.getURI());
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(baseCount(), targetFacetCount());
		CNAbilitySelection cas = new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.AUTOMATIC, source));
		directAbilityFacet.add(id, cas, UserSelection.getInstance());
		assertFalse(containsExpected());
		PCTemplate varsource = create(PCTemplate.class, "VarSource");
		varsource.put(VariableKey.getConstant("MyCasterLevel"), FormulaFactory.getFormulaFor(4.0));
		templateInputFacet.directAdd(id, varsource, null);
		pc.calcActiveBonuses();
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		directAbilityFacet.remove(id, cas, UserSelection.getInstance());
		pc.calcActiveBonuses();
		assertEquals(baseCount(), targetFacetCount());
	}
}
