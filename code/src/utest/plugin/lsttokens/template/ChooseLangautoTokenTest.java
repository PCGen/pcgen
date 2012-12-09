/*
 * 
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.template;

import java.util.Collections;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.AbstractSelectionTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class ChooseLangautoTokenTest extends
		AbstractSelectionTokenTestCase<CDOMObject, Language>
{

	static ChooseLst token = new ChooseLst();
	static ChooseLangautoToken subtoken = new ChooseLangautoToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>(
			CDOMObject.class);

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
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
	public Class<Language> getTargetClass()
	{
		return Language.class;
	}

	@Override
	public boolean isAllLegal()
	{
		return true;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public boolean allowsParenAsSub()
	{
		return false;
	}

	@Override
	public boolean allowsFormula()
	{
		return false;
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	public void testUnparseSingle() throws PersistenceLayerException
	{
		Language wp1 = construct(primaryContext, "TestWP1");
		PersistentTransitionChoice<Language> tc = buildChoice(CDOMDirectSingleRef
				.getRef(wp1));
		tc.setChoiceActor(subtoken);
		primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, tc);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "LANGAUTO|TestWP1");
	}

	@Test
	public void testUnparseBadCount() throws PersistenceLayerException
	{
		Language wp1 = construct(primaryContext, "TestWP1");
		ReferenceChoiceSet<Language> rcs = new ReferenceChoiceSet<Language>(
				Collections.singletonList(CDOMDirectSingleRef.getRef(wp1)));
		ChoiceSet<Language> cs = new ChoiceSet<Language>(getSubTokenName(), rcs);
		cs.setTitle("Pick a Language");
		PersistentTransitionChoice<Language> tc1 = new ConcretePersistentTransitionChoice<Language>(
				cs, null);
		tc1.setChoiceActor(subtoken);
		primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, tc1);
		assertBadUnparse();
	}

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
	// primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, tc);
	// assertBadUnparse();
	// }

	@Test
	public void testUnparseMultiple() throws PersistenceLayerException
	{
		Language wp1 = construct(primaryContext, "TestWP1");
		Language wp2 = construct(primaryContext, "TestWP2");
		PersistentTransitionChoice<Language> tc = buildChoice(
				CDOMDirectSingleRef.getRef(wp1), CDOMDirectSingleRef
						.getRef(wp2));
		tc.setChoiceActor(subtoken);
		primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, tc);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "LANGAUTO|TestWP1" + getJoinCharacter()
				+ "TestWP2");
	}

	@Test
	public void testUnparseNullInList() throws PersistenceLayerException
	{
		Language wp1 = construct(primaryContext, "TestWP1");
		ReferenceChoiceSet<Language> rcs = buildRCS(CDOMDirectSingleRef
				.getRef(wp1), null);
		PersistentTransitionChoice<Language> tc = buildTC(rcs);
		tc.setChoiceActor(subtoken);
		primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, tc);
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
		ObjectKey objectKey = ObjectKey.CHOOSE_LANGAUTO;
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

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
