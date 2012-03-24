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

import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.choose.FeatSelectionToken;

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
		CategorizedAbilitySelection as =
				new CategorizedAbilitySelection(AbilityCategory.FEAT, item,
					Nature.NORMAL);
		assertEquals("ItemName", pca.encodeChoice(as));
		Ability paren = construct("ParenName (test)");
		as = new CategorizedAbilitySelection(AbilityCategory.FEAT, paren, Nature.NORMAL);
		assertEquals("ParenName (test)", pca.encodeChoice(as));
		Ability sel = construct("ChooseName");
		sel.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		as = new CategorizedAbilitySelection(AbilityCategory.FEAT, sel, Nature.NORMAL, "selection");
		assertEquals("ChooseName(selection)", pca.encodeChoice(as));
	}

	@Test
	public void testDecodeChoice()
	{
		try
		{
			pca.decodeChoice("ItemName");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		Ability item = construct("ItemName");
		CategorizedAbilitySelection as =
				new CategorizedAbilitySelection(AbilityCategory.FEAT, item,
					Nature.NORMAL);
		assertEquals(as, pca.decodeChoice("ItemName"));
		Ability paren = construct("ParenName (test)");
		as = new CategorizedAbilitySelection(AbilityCategory.FEAT, paren, Nature.NORMAL);
		assertEquals(as, pca.decodeChoice("ParenName (test)"));
		Ability sel = construct("ChooseName");
		sel.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		as = new CategorizedAbilitySelection(AbilityCategory.FEAT, sel, Nature.NORMAL, "selection");
		assertEquals(as, pca.decodeChoice("ChooseName(selection)"));
	}

	protected Ability construct(String one)
	{
		Ability obj = context.ref.constructCDOMObject(Ability.class, one);
		context.ref.reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}
}
