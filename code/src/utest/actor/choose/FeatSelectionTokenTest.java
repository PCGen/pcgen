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
package actor.choose;

import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.choose.FeatSelectionToken;
import plugin.lsttokens.choose.StringToken;

public class FeatSelectionTokenTest extends TestCase
{

	static FeatSelectionToken pca = new FeatSelectionToken();

	protected LoadContext context;

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		Globals.emptyLists();
		SettingsHandler.getGame().clearLoadContext();
		context = Globals.getContext();
		
		// new RuntimeLoadContext(new RuntimeReferenceContext(),
		// new ConsolidatedListCommitStrategy());
	}

	@Test
	public void testEncodeChoice()
	{
		Ability item = construct("ItemName");
		AbilitySelection as = new AbilitySelection(item, null);
		assertEquals("ItemName", pca.encodeChoice(as));
		Ability paren = construct("ParenName (test)");
		as = new AbilitySelection(paren, null);
		assertEquals("ParenName (test)", pca.encodeChoice(as));
		Ability sel = construct("ChooseName");
		sel.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		StringToken st = new plugin.lsttokens.choose.StringToken();
		ParseResult pr = st.parseToken(Globals.getContext(), sel, "selection|Acrobatics");
		assertTrue(pr.passed());
		Globals.getContext().commit();
		as = new AbilitySelection(sel, "selection");
		assertEquals("ChooseName (selection)", pca.encodeChoice(as));
	}

	@Test
	public void testDecodeChoice()
	{
		try
		{
			pca.decodeChoice(context, "ItemName");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		Ability item = construct("ItemName");
		AbilitySelection as = new AbilitySelection(item, null);
		assertEquals(as, pca.decodeChoice(context, "ItemName"));
		Ability paren = construct("ParenName (test)");
		as = new AbilitySelection(paren, null);
		assertEquals(as, pca.decodeChoice(context, "ParenName (test)"));
		Ability sel = construct("ChooseName");
		sel.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		StringToken st = new plugin.lsttokens.choose.StringToken();
		ParseResult pr = st.parseToken(Globals.getContext(), sel, "selection|Acrobatics");
		assertTrue(pr.passed());
		Globals.getContext().commit();
		as = new AbilitySelection(sel, "selection");
		assertEquals(as, pca.decodeChoice(context, "ChooseName(selection)"));
	}

	protected Ability construct(String one)
	{
		Ability obj = context.getReferenceContext().constructCDOMObject(Ability.class, one);
		context.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}
}
