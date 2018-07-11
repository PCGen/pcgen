/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package actor.choose;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.choose.AbilitySelectionToken;
import plugin.lsttokens.choose.StringToken;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * Unit test of the class AbilitySelectionToken.
 * 
 * 
 */
public class AbilitySelectionTokenTest
{

	static final AbilitySelectionToken pca = new AbilitySelectionToken();

	protected LoadContext context;

	@Before
	public void setUp()
	{
		Globals.emptyLists();
		SettingsHandler.getGame().clearLoadContext();
		context = Globals.getContext();
		
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
	}

	@Test
	public void testEncodeChoice()
	{
		Ability item = construct("ItemName");
		AbilitySelection as = new AbilitySelection(item, null);
		assertEquals("CATEGORY=FEAT|ItemName", pca.encodeChoice(as));
		Ability paren = construct("ParenName (test)");
		as = new AbilitySelection(paren, null);
		assertEquals("CATEGORY=FEAT|ParenName (test)", pca.encodeChoice(as));
		Ability sel = construct("ChooseName");
		sel.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		StringToken st = new plugin.lsttokens.choose.StringToken();
		ParseResult pr = st.parseToken(Globals.getContext(), sel, "selection|Acrobatics");
		assertTrue(pr.passed());
		Globals.getContext().commit();
		as = new AbilitySelection(sel, "selection");
		assertEquals("CATEGORY=FEAT|ChooseName|selection", pca.encodeChoice(as));
	}

	@Test
	public void testDecodeChoice()
	{
		try
		{
			pca.decodeChoice(context, "Category=Special Ability|ItemName");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		Ability item = construct("ItemName");
		AbilitySelection as = new AbilitySelection(item, null);
		assertEquals(as, pca.decodeChoice(context, "CATEGORY=FEAT|ItemName"));
		Ability paren = construct("ParenName (test)");
		as = new AbilitySelection(paren, null);
		assertEquals(as, pca.decodeChoice(context, "CATEGORY=Feat|ParenName (test)"));
		Ability sel = construct("ChooseName");
		sel.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		StringToken st = new plugin.lsttokens.choose.StringToken();
		ParseResult pr = st.parseToken(Globals.getContext(), sel, "selection|Acrobatics");
		assertTrue(pr.passed());
		Globals.getContext().commit();
		as = new AbilitySelection(sel, "selection");
		assertEquals(as, pca.decodeChoice(context, "CATEGORY=Feat|ChooseName|selection"));
	}

	protected Ability construct(String one)
	{
		Ability a = BuildUtilities.getFeatCat().newInstance();
		a.setName(one);
		context.getReferenceContext().importObject(a);
		return a;
	}
}
