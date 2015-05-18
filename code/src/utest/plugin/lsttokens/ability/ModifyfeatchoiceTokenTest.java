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
package plugin.lsttokens.ability;

import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcreteTransitionChoice;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ModifyChoiceDecorator;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.TabInfo;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.Tab;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class ModifyfeatchoiceTokenTest extends
		AbstractListTokenTestCase<Ability, Ability>
{
	static ModifyfeatchoiceToken token = new ModifyfeatchoiceToken();

	static CDOMTokenLoader<Ability> loader = new CDOMTokenLoader<Ability>();

	PreClassParser preclass = new PreClassParser();

	PreClassWriter preclasswriter = new PreClassWriter();

	PreRaceParser prerace = new PreRaceParser();

	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
		TabInfo ti = primaryContext.getReferenceContext().constructCDOMObject(TabInfo.class, "Feats");
		ti.setName("Feats");
		ti = secondaryContext.getReferenceContext().constructCDOMObject(TabInfo.class, "Feats");
		ti.setName("Feats");
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<Ability> getTargetClass()
	{
		return Ability.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
	}

	@Override
	public CDOMLoader<Ability> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Ability> getToken()
	{
		return token;
	}

	@Override
	protected Ability construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.getReferenceContext().constructCDOMObject(Ability.class, one);
		loadContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Override
	public boolean allowDups()
	{
		return false;
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.MODIFY_CHOICE, null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	protected TransitionChoice<CNAbility> buildChoice(
			CDOMReference<Ability>... refs)
	{
		ReferenceChoiceSet<Ability> rcs = buildRCS(refs);
		assertTrue(rcs.getGroupingState().isValid());
		return buildTC(rcs);
	}

	protected TransitionChoice<CNAbility> buildTC(ReferenceChoiceSet<Ability> rcs)
	{
		ModifyChoiceDecorator gfd = new ModifyChoiceDecorator(rcs);
		ChoiceSet<CNAbility> cs = new ChoiceSet<CNAbility>(getToken()
				.getTokenName(), gfd);
		TabInfo ti = primaryContext.getReferenceContext().silentlyGetConstructedCDOMObject(
				TabInfo.class, Tab.ABILITIES.toString());
		String singularName = ti.getResolvedName();
		if (singularName.endsWith("s"))
		{
			singularName = singularName.substring(0, singularName.length() - 1);
		}
		cs.setTitle("Select a " + singularName + " to modify");
		TransitionChoice<CNAbility> tc = new ConcreteTransitionChoice<CNAbility>(cs,
				FormulaFactory.ONE);
		tc.setRequired(false);
		// tc.setChoiceActor(getToken());
		return tc;
	}

	protected ReferenceChoiceSet<Ability> buildRCS(
			CDOMReference<Ability>... refs)
	{
		ReferenceChoiceSet<Ability> rcs = new ReferenceChoiceSet<Ability>(
				Arrays.asList(refs));
		return rcs;
	}

	@Test
	public void testUnparseSingle() throws PersistenceLayerException
	{
		Ability wp1 = construct(primaryContext, "TestWP1");
		TransitionChoice<CNAbility> tc = buildChoice(CDOMDirectSingleRef
				.getRef(wp1));
		primaryProf.put(ObjectKey.MODIFY_CHOICE, tc);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "TestWP1");
	}

	/*
	 * TODO Need to check this - count needs to be 1
	 */
	// @Test
	// public void testUnparseBadCount() throws PersistenceLayerException
	// {
	// Ability wp1 = construct(primaryContext, "TestWP1");
	// ReferenceChoiceSet<Ability> rcs = new ReferenceChoiceSet<Ability>(
	// Collections.singletonList(CDOMDirectSingleRef.getRef(wp1)));
	// ModifyChoiceDecorator gfd = new ModifyChoiceDecorator(rcs);
	// ChoiceSet<Ability> cs = new ChoiceSet<Ability>(getToken()
	// .getTokenName(), gfd);
	// cs.setTitle("Select a "
	// + SettingsHandler.getGame().getSingularTabName(Tab.ABILITIES)
	// + " to modify");
	// TransitionChoice<Ability> tc1 = new TransitionChoice<Ability>(cs, null);
	// primaryProf.put(ObjectKey.MODIFY_CHOICE, tc1);
	// assertBadUnparse();
	// }
	/*
	 * TODO Need to figure out who's responsibility this is!
	 */
	// @Test
	// public void testUnparseBadList() throws PersistenceLayerException
	// {
	// Language wp1 = construct(primaryContext, "TestWP1");
	// ReferenceChoiceSet<Language> rcs = buildRCS(CDOMDirectSingleRef
	// .getRef(wp1), primaryContext.ref
	// .getCDOMAllReference(getTargetClass()));
	// assertFalse(rcs.getGroupingState().isValid());
	// PersistentTransitionChoice<Language> tc = buildTC(rcs);
	// tc.setChoiceActor(subtoken);
	// primaryProf.put(ObjectKey.MODIFY_CHOICE, tc);
	// assertBadUnparse();
	// }
	@Test
	public void testUnparseMultiple() throws PersistenceLayerException
	{
		Ability wp1 = construct(primaryContext, "TestWP1");
		Ability wp2 = construct(primaryContext, "TestWP2");
		TransitionChoice<CNAbility> tc = buildChoice(CDOMDirectSingleRef
				.getRef(wp1), CDOMDirectSingleRef.getRef(wp2));
		primaryProf.put(ObjectKey.MODIFY_CHOICE, tc);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "TestWP1|TestWP2");
	}

	@Test
	public void testUnparseNullInList() throws PersistenceLayerException
	{
		Ability wp1 = construct(primaryContext, "TestWP1");
		ReferenceChoiceSet<Ability> rcs = buildRCS(CDOMDirectSingleRef
				.getRef(wp1), null);
		TransitionChoice<CNAbility> tc = buildTC(rcs);
		primaryProf.put(ObjectKey.MODIFY_CHOICE, tc);
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFail() throws PersistenceLayerException
	{
		ObjectKey objectKey = ObjectKey.MODIFY_CHOICE;
		primaryProf.put(objectKey, new Object());
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (ClassCastException e)
		{
			// Yep!
		}
	}

}
