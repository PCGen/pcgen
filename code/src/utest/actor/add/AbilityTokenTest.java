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
package actor.add;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;
import pcgen.testsupport.AbstractCharacterUsingTestCase;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.add.AbilityToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbilityTokenTest extends AbstractCharacterUsingTestCase
{

	private static final LstToken ADD_TOKEN = new AddLst();
	private static final LstToken ADD_ABILITY_TOKEN = new AbilityToken();

	private static final AbilityToken PCA = new AbilityToken();

	protected LoadContext context;

	@Override
	@BeforeEach
	public void setUp() throws Exception
	{
		super.setUp();
		SettingsHandler.getGame().clearLoadContext();
		context = Globals.getContext();
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
	}

	@AfterEach
	@Override
	public void tearDown() throws Exception
	{
		context = null;
		super.tearDown();
	}

	@Test
	public void testEncodeChoice()
	{
		Ability item = construct("ItemName");
		CNAbilitySelection as = new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, item));
		assertEquals("CATEGORY=FEAT|NATURE=NORMAL|ItemName", PCA
			.encodeChoice(as));
	}

	@Test
	public void testDecodeChoice()
	{
		try
		{
			PCA.decodeChoice(context, "CATEGORY=FEAT|NATURE=NORMAL|ItemName");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		Ability item = construct("ItemName");
		CNAbilitySelection as = new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, item));
		assertEquals(as, PCA
			.decodeChoice(context, "CATEGORY=FEAT|NATURE=NORMAL|ItemName"));
	}


	@Test
	public void testWithChoose()
	{
		setUpPC();
		//Need to make sure we use the character related context
		context = Globals.getContext();
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
		TokenRegistration.register(ADD_TOKEN);
		TokenRegistration.register(ADD_ABILITY_TOKEN);
		Ability item = construct("ChooseAbility");
		Ability parent = construct("Parent");
		context.getReferenceContext().constructCDOMObject(Language.class, "Foo");
		context.getReferenceContext().constructCDOMObject(Language.class, "Bar");
		context.getReferenceContext().constructCDOMObject(Language.class, "Goo");
		context.getReferenceContext().constructCDOMObject(Language.class, "Wow");
		context.getReferenceContext().constructCDOMObject(Language.class, "Rev");
		AbilityCategory ff = context.getReferenceContext()
			.constructCDOMObject(AbilityCategory.class, "Fighter Feat");
		ff.setAbilityCategory(CDOMDirectSingleRef.getRef(BuildUtilities.getFeatCat()));
		AbilityCategory oc = context.getReferenceContext()
			.constructCDOMObject(AbilityCategory.class, "Some Other Category");
		Ability badCA = oc.newInstance();
		badCA.setName("ChooseAbility");
		context.getReferenceContext().importObject(badCA);
		assertTrue(context.processToken(item, "CHOOSE", "LANG|Foo|Bar|Goo|Wow|Rev"));
		assertTrue(context.processToken(item, "MULT", "Yes"));
		assertTrue(context.processToken(badCA, "CHOOSE", "LANG|Foo|Bar|Goo|Wow|Rev"));
		assertTrue(context.processToken(badCA, "MULT", "Yes"));
		assertTrue(context.processToken(parent, "ADD", "ABILITY|FEAT|NORMAL|ChooseAbility"));
		finishLoad(context);
		PlayerCharacter pc = new PlayerCharacter();
		Object source = UserSelection.getInstance();
		
		CNAbilitySelection badCACAS = new CNAbilitySelection(
			CNAbilityFactory.getCNAbility(oc, Nature.AUTOMATIC, badCA), "Foo");
		CNAbilitySelection fooCAS = new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.AUTOMATIC, item), "Foo");
		CNAbilitySelection barCAS = new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.VIRTUAL, item), "Bar");
		CNAbilitySelection gooCAS = new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, item), "Goo");
		CNAbilitySelection wowCAS = new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, item), "Wow");
		CNAbilitySelection wowFFCAS = new CNAbilitySelection(
			CNAbilityFactory.getCNAbility(ff, Nature.NORMAL, item), "Wow");
		CNAbilitySelection revCAS = new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, item), "Rev");
		CNAbilitySelection revFFCAS = new CNAbilitySelection(
			CNAbilityFactory.getCNAbility(ff, Nature.NORMAL, item), "Rev");
		
		assertTrue(PCA.allow(fooCAS, pc, false));
		assertTrue(PCA.allow(barCAS, pc, false));
		assertTrue(PCA.allow(gooCAS, pc, false));
		assertTrue(PCA.allow(wowCAS, pc, false));
		assertTrue(PCA.allow(revFFCAS, pc, false));
		pc.applyAbility(badCACAS, source);
		//Should have had no effect
		assertTrue(PCA.allow(fooCAS, pc, false));
		assertTrue(PCA.allow(barCAS, pc, false));
		assertTrue(PCA.allow(gooCAS, pc, false));
		assertTrue(PCA.allow(wowCAS, pc, false));
		assertTrue(PCA.allow(revFFCAS, pc, false));
		pc.applyAbility(fooCAS, source);
		assertFalse(PCA.allow(fooCAS, pc, false));
		assertTrue(PCA.allow(barCAS, pc, false));
		assertTrue(PCA.allow(gooCAS, pc, false));
		assertTrue(PCA.allow(wowCAS, pc, false));
		assertTrue(PCA.allow(revFFCAS, pc, false));
		pc.applyAbility(barCAS, source);
		assertFalse(PCA.allow(fooCAS, pc, false));
		assertFalse(PCA.allow(barCAS, pc, false));
		assertTrue(PCA.allow(gooCAS, pc, false));
		assertTrue(PCA.allow(wowCAS, pc, false));
		assertTrue(PCA.allow(revFFCAS, pc, false));
		pc.applyAbility(gooCAS, source);
		assertFalse(PCA.allow(fooCAS, pc, false));
		assertFalse(PCA.allow(barCAS, pc, false));
		assertFalse(PCA.allow(gooCAS, pc, false));
		assertTrue(PCA.allow(wowCAS, pc, false));
		assertTrue(PCA.allow(revFFCAS, pc, false));
		pc.applyAbility(wowFFCAS, source);
		assertFalse(PCA.allow(fooCAS, pc, false));
		assertFalse(PCA.allow(barCAS, pc, false));
		assertFalse(PCA.allow(gooCAS, pc, false));
		assertFalse(PCA.allow(wowCAS, pc, false));
		assertTrue(PCA.allow(revFFCAS, pc, false));
		pc.applyAbility(revCAS, source);
		assertFalse(PCA.allow(fooCAS, pc, false));
		assertFalse(PCA.allow(barCAS, pc, false));
		assertFalse(PCA.allow(gooCAS, pc, false));
		assertFalse(PCA.allow(wowCAS, pc, false));
		assertFalse(PCA.allow(revFFCAS, pc, false));
	}

	protected Ability construct(String one)
	{
		Ability a = BuildUtilities.getFeatCat().newInstance();
		a.setName(one);
		context.getReferenceContext().importObject(a);
		return a;
	}
}
