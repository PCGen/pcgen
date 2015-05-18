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

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

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
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.testsupport.AbstractCharacterUsingTestCase;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.add.FeatToken;
import plugin.lsttokens.testsupport.TokenRegistration;

public class FeatTokenTest extends AbstractCharacterUsingTestCase
{

	private static final AddLst ADD_TOKEN = new plugin.lsttokens.AddLst();
	private static final FeatToken ADD_FEAT_TOKEN = new plugin.lsttokens.add.FeatToken();

	static FeatToken pca = new FeatToken();

	protected LoadContext context;

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		SettingsHandler.getGame().clearLoadContext();
		context = Globals.getContext();
		context.getReferenceContext().importObject(AbilityCategory.FEAT);
		// new RuntimeLoadContext(new RuntimeReferenceContext(),
		// new ConsolidatedListCommitStrategy());
	}

	@Test
	public void testEncodeChoice()
	{
		Ability item = construct("ItemName");
		CNAbilitySelection as =
				new CNAbilitySelection(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.NORMAL, item));
		assertEquals("CATEGORY=FEAT|NATURE=NORMAL|ItemName", pca
			.encodeChoice(as));
	}

	@Test
	public void testDecodeChoice()
	{
		try
		{
			pca.decodeChoice(context, "CATEGORY=FEAT|NATURE=NORMAL|ItemName");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		Ability item = construct("ItemName");
		CNAbilitySelection as =
				new CNAbilitySelection(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.NORMAL, item));
		assertEquals(as, pca
			.decodeChoice(context, "CATEGORY=FEAT|NATURE=NORMAL|ItemName"));
	}


	@Test
	public void testWithChoose()
	{
		try {
			setUpPC();
			//Need to make sure we use the character related context
			context = Globals.getContext();
			TokenRegistration.register(ADD_TOKEN);
			TokenRegistration.register(ADD_FEAT_TOKEN);
		} catch (PersistenceLayerException e1) {
			fail("Cannot set up PC");
		}
		Ability item = construct("ChooseAbility");
		Ability parent = construct("Parent");
		context.getReferenceContext().constructCDOMObject(Language.class, "Foo");
		context.getReferenceContext().constructCDOMObject(Language.class, "Bar");
		context.getReferenceContext().constructCDOMObject(Language.class, "Goo");
		context.getReferenceContext().constructCDOMObject(Language.class, "Wow");
		context.getReferenceContext().constructCDOMObject(Language.class, "Rev");
		AbilityCategory ff = context.getReferenceContext().constructCDOMObject(AbilityCategory.class, "Fighter Feat");
		ff.setAbilityCategory(CDOMDirectSingleRef.getRef(AbilityCategory.FEAT));
		AbilityCategory oc = context.getReferenceContext().constructCDOMObject(AbilityCategory.class, "Some Other Category");
		Ability badCA = context.getReferenceContext().constructCDOMObject(Ability.class, "ChooseAbility");
		context.getReferenceContext().reassociateCategory(oc, badCA);
		try {
			assertTrue(context.processToken(item, "CHOOSE", "LANG|Foo|Bar|Goo|Wow|Rev"));
			assertTrue(context.processToken(item, "MULT", "Yes"));
			assertTrue(context.processToken(badCA, "CHOOSE", "LANG|Foo|Bar|Goo|Wow|Rev"));
			assertTrue(context.processToken(badCA, "MULT", "Yes"));
			assertTrue(context.processToken(parent, "ADD", "FEAT|ChooseAbility"));
		} catch (PersistenceLayerException e) {
			e.printStackTrace();
			fail();
		}
		PlayerCharacter pc = new PlayerCharacter();
		Object source = UserSelection.getInstance();
		finishLoad(context);
		
		CNAbilitySelection badCACAS = new CNAbilitySelection(CNAbilityFactory.getCNAbility(oc, Nature.AUTOMATIC, badCA), "Foo");
		CNAbilitySelection fooCAS = new CNAbilitySelection(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.AUTOMATIC, item), "Foo");
		CNAbilitySelection barCAS = new CNAbilitySelection(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.VIRTUAL, item), "Bar");
		CNAbilitySelection gooCAS = new CNAbilitySelection(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.NORMAL, item), "Goo");
		CNAbilitySelection wowCAS =
				new CNAbilitySelection(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.NORMAL, item), "Wow");
		CNAbilitySelection wowFFCAS = new CNAbilitySelection(CNAbilityFactory.getCNAbility(ff, Nature.NORMAL, item), "Wow");
		CNAbilitySelection revCAS =
				new CNAbilitySelection(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.NORMAL, item), "Rev");
		CNAbilitySelection revFFCAS = new CNAbilitySelection(CNAbilityFactory.getCNAbility(ff, Nature.NORMAL, item), "Rev");
		
		assertTrue(pca.allow(fooCAS, pc, false));
		assertTrue(pca.allow(barCAS, pc, false));
		assertTrue(pca.allow(gooCAS, pc, false));
		assertTrue(pca.allow(wowCAS, pc, false));
		assertTrue(pca.allow(revFFCAS, pc, false));
		pc.applyAbility(badCACAS, source);
		//Should have had no effect
		assertTrue(pca.allow(fooCAS, pc, false));
		assertTrue(pca.allow(barCAS, pc, false));
		assertTrue(pca.allow(gooCAS, pc, false));
		assertTrue(pca.allow(wowCAS, pc, false));
		assertTrue(pca.allow(revFFCAS, pc, false));
		pc.applyAbility(fooCAS, source);
		assertFalse(pca.allow(fooCAS, pc, false));
		assertTrue(pca.allow(barCAS, pc, false));
		assertTrue(pca.allow(gooCAS, pc, false));
		assertTrue(pca.allow(wowCAS, pc, false));
		assertTrue(pca.allow(revFFCAS, pc, false));
		pc.applyAbility(barCAS, source);
		assertFalse(pca.allow(fooCAS, pc, false));
		assertFalse(pca.allow(barCAS, pc, false));
		assertTrue(pca.allow(gooCAS, pc, false));
		assertTrue(pca.allow(wowCAS, pc, false));
		assertTrue(pca.allow(revFFCAS, pc, false));
		pc.applyAbility(gooCAS, source);
		assertFalse(pca.allow(fooCAS, pc, false));
		assertFalse(pca.allow(barCAS, pc, false));
		assertFalse(pca.allow(gooCAS, pc, false));
		assertTrue(pca.allow(wowCAS, pc, false));
		assertTrue(pca.allow(revFFCAS, pc, false));
		pc.applyAbility(wowFFCAS, source);
		assertFalse(pca.allow(fooCAS, pc, false));
		assertFalse(pca.allow(barCAS, pc, false));
		assertFalse(pca.allow(gooCAS, pc, false));
		assertFalse(pca.allow(wowCAS, pc, false));
		assertTrue(pca.allow(revFFCAS, pc, false));
		pc.applyAbility(revCAS, source);
		assertFalse(pca.allow(fooCAS, pc, false));
		assertFalse(pca.allow(barCAS, pc, false));
		assertFalse(pca.allow(gooCAS, pc, false));
		assertFalse(pca.allow(wowCAS, pc, false));
		assertFalse(pca.allow(revFFCAS, pc, false));
	}

	protected Ability construct(String one)
	{
		Ability obj = context.getReferenceContext().constructCDOMObject(Ability.class, one);
		context.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}
}
